package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.item.consume.PlaySoundConsumeEffect;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public record ConsumableComponent(
	float consumeSeconds, UseAction useAction, RegistryEntry<SoundEvent> sound, boolean hasConsumeParticles, List<ConsumeEffect> onConsumeEffects
) {
	public static final float DEFAULT_CONSUME_SECONDS = 1.6F;
	private static final int PARTICLES_AND_SOUND_TICK_INTERVAL = 4;
	private static final float PARTICLES_AND_SOUND_TICK_THRESHOLD = 0.21875F;
	public static final Codec<ConsumableComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.NON_NEGATIVE_FLOAT.optionalFieldOf("consume_seconds", 1.6F).forGetter(ConsumableComponent::consumeSeconds),
					UseAction.CODEC.optionalFieldOf("animation", UseAction.EAT).forGetter(ConsumableComponent::useAction),
					SoundEvent.ENTRY_CODEC.optionalFieldOf("sound", SoundEvents.ENTITY_GENERIC_EAT).forGetter(ConsumableComponent::sound),
					Codec.BOOL.optionalFieldOf("has_consume_particles", Boolean.valueOf(true)).forGetter(ConsumableComponent::hasConsumeParticles),
					ConsumeEffect.CODEC.listOf().optionalFieldOf("on_consume_effects", List.of()).forGetter(ConsumableComponent::onConsumeEffects)
				)
				.apply(instance, ConsumableComponent::new)
	);
	public static final PacketCodec<RegistryByteBuf, ConsumableComponent> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.FLOAT,
		ConsumableComponent::consumeSeconds,
		UseAction.PACKET_CODEC,
		ConsumableComponent::useAction,
		SoundEvent.ENTRY_PACKET_CODEC,
		ConsumableComponent::sound,
		PacketCodecs.BOOL,
		ConsumableComponent::hasConsumeParticles,
		ConsumeEffect.PACKET_CODEC.collect(PacketCodecs.toList()),
		ConsumableComponent::onConsumeEffects,
		ConsumableComponent::new
	);

	public ActionResult consume(LivingEntity user, ItemStack stack, Hand hand) {
		if (!this.canConsume(user, stack)) {
			return ActionResult.FAIL;
		} else {
			boolean bl = this.getConsumeTicks() > 0;
			if (bl) {
				user.setCurrentHand(hand);
				return ActionResult.CONSUME;
			} else {
				ItemStack itemStack = this.finishConsumption(user.getWorld(), user, stack);
				return ActionResult.CONSUME.withNewHandStack(itemStack);
			}
		}
	}

	public ItemStack finishConsumption(World world, LivingEntity user, ItemStack stack) {
		Random random = user.getRandom();
		this.spawnParticlesAndPlaySound(random, user, stack, 16);
		if (user instanceof ServerPlayerEntity serverPlayerEntity) {
			serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
			Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
		}

		stack.streamAll(Consumable.class).forEach(consumable -> consumable.onConsume(world, user, stack, this));
		if (!world.isClient) {
			this.onConsumeEffects.forEach(effect -> effect.onConsume(world, stack, user));
		}

		user.emitGameEvent(this.useAction == UseAction.DRINK ? GameEvent.DRINK : GameEvent.EAT);
		stack.decrementUnlessCreative(1, user);
		return stack;
	}

	public boolean canConsume(LivingEntity user, ItemStack stack) {
		FoodComponent foodComponent = stack.get(DataComponentTypes.FOOD);
		return foodComponent != null && user instanceof PlayerEntity playerEntity ? playerEntity.canConsume(foodComponent.canAlwaysEat()) : true;
	}

	public int getConsumeTicks() {
		return (int)(this.consumeSeconds * 20.0F);
	}

	public void spawnParticlesAndPlaySound(Random random, LivingEntity user, ItemStack stack, int particleCount) {
		float f = random.nextBoolean() ? 0.5F : 1.0F;
		float g = random.nextTriangular(1.0F, 0.2F);
		float h = 0.5F;
		float i = MathHelper.nextBetween(random, 0.9F, 1.0F);
		float j = this.useAction == UseAction.DRINK ? 0.5F : f;
		float k = this.useAction == UseAction.DRINK ? i : g;
		if (this.hasConsumeParticles) {
			user.spawnItemParticles(stack, particleCount);
		}

		SoundEvent soundEvent = user instanceof ConsumableComponent.ConsumableSoundProvider consumableSoundProvider
			? consumableSoundProvider.getConsumeSound(stack)
			: this.sound.value();
		user.playSound(soundEvent, j, k);
	}

	public boolean shouldSpawnParticlesAndPlaySounds(int remainingUseTicks) {
		int i = this.getConsumeTicks() - remainingUseTicks;
		int j = (int)((float)this.getConsumeTicks() * 0.21875F);
		boolean bl = i > j;
		return bl && remainingUseTicks % 4 == 0;
	}

	public static ConsumableComponent.Builder builder() {
		return new ConsumableComponent.Builder();
	}

	public static class Builder {
		private float consumeSeconds = 1.6F;
		private UseAction useAction = UseAction.EAT;
		private RegistryEntry<SoundEvent> sound = SoundEvents.ENTITY_GENERIC_EAT;
		private boolean consumeParticles = true;
		private final List<ConsumeEffect> consumeEffects = new ArrayList();

		Builder() {
		}

		public ConsumableComponent.Builder consumeSeconds(float consumeSeconds) {
			this.consumeSeconds = consumeSeconds;
			return this;
		}

		public ConsumableComponent.Builder useAction(UseAction useAction) {
			this.useAction = useAction;
			return this;
		}

		public ConsumableComponent.Builder sound(RegistryEntry<SoundEvent> sound) {
			this.sound = sound;
			return this;
		}

		public ConsumableComponent.Builder finishSound(RegistryEntry<SoundEvent> finishSound) {
			return this.consumeEffect(new PlaySoundConsumeEffect(finishSound));
		}

		public ConsumableComponent.Builder consumeParticles(boolean consumeParticles) {
			this.consumeParticles = consumeParticles;
			return this;
		}

		public ConsumableComponent.Builder consumeEffect(ConsumeEffect consumeEffect) {
			this.consumeEffects.add(consumeEffect);
			return this;
		}

		public ConsumableComponent build() {
			return new ConsumableComponent(this.consumeSeconds, this.useAction, this.sound, this.consumeParticles, this.consumeEffects);
		}
	}

	public interface ConsumableSoundProvider {
		SoundEvent getConsumeSound(ItemStack stack);
	}
}
