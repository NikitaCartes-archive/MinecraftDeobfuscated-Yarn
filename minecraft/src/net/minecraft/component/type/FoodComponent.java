package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/**
 * Represents the components that make up the properties of a food item.
 */
public record FoodComponent(int nutrition, float saturation, boolean canAlwaysEat) implements Consumable {
	public static final Codec<FoodComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.NON_NEGATIVE_INT.fieldOf("nutrition").forGetter(FoodComponent::nutrition),
					Codec.FLOAT.fieldOf("saturation").forGetter(FoodComponent::saturation),
					Codec.BOOL.optionalFieldOf("can_always_eat", Boolean.valueOf(false)).forGetter(FoodComponent::canAlwaysEat)
				)
				.apply(instance, FoodComponent::new)
	);
	public static final PacketCodec<RegistryByteBuf, FoodComponent> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT,
		FoodComponent::nutrition,
		PacketCodecs.FLOAT,
		FoodComponent::saturation,
		PacketCodecs.BOOL,
		FoodComponent::canAlwaysEat,
		FoodComponent::new
	);

	@Override
	public void onConsume(World world, LivingEntity user, ItemStack stack, ConsumableComponent consumable) {
		Random random = user.getRandom();
		world.playSound(null, user.getX(), user.getY(), user.getZ(), consumable.sound().value(), SoundCategory.NEUTRAL, 1.0F, random.nextTriangular(1.0F, 0.4F));
		if (user instanceof PlayerEntity playerEntity) {
			playerEntity.getHungerManager().eat(this);
			world.playSound(
				null,
				playerEntity.getX(),
				playerEntity.getY(),
				playerEntity.getZ(),
				SoundEvents.ENTITY_PLAYER_BURP,
				SoundCategory.PLAYERS,
				0.5F,
				MathHelper.nextBetween(random, 0.9F, 1.0F)
			);
		}
	}

	public static class Builder {
		private int nutrition;
		private float saturationModifier;
		private boolean canAlwaysEat;

		/**
		 * Specifies the amount of hunger a food item will fill.
		 * 
		 * <p>One hunger is equivalent to half of a hunger bar icon.
		 * 
		 * @param nutrition the amount of hunger
		 */
		public FoodComponent.Builder nutrition(int nutrition) {
			this.nutrition = nutrition;
			return this;
		}

		/**
		 * Specifies the saturation modifier of a food item.
		 * 
		 * <p>This value is typically used to determine how long a player can sustain the current hunger value before the hunger is used.
		 * 
		 * @param saturationModifier the saturation modifier
		 */
		public FoodComponent.Builder saturationModifier(float saturationModifier) {
			this.saturationModifier = saturationModifier;
			return this;
		}

		/**
		 * Specifies that a food item can be eaten when the current hunger bar is full.
		 */
		public FoodComponent.Builder alwaysEdible() {
			this.canAlwaysEat = true;
			return this;
		}

		public FoodComponent build() {
			float f = HungerConstants.calculateSaturation(this.nutrition, this.saturationModifier);
			return new FoodComponent(this.nutrition, f, this.canAlwaysEat);
		}
	}
}
