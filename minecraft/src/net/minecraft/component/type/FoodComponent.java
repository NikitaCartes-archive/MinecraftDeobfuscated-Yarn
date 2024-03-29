package net.minecraft.component.type;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;

/**
 * Represents the components that make up the properties of a food item.
 */
public record FoodComponent(int nutrition, float saturationModifier, boolean canAlwaysEat, float eatSeconds, List<FoodComponent.StatusEffectEntry> effects) {
	private static final float DEFAULT_EAT_SECONDS = 1.6F;
	public static final Codec<FoodComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.NONNEGATIVE_INT.fieldOf("nutrition").forGetter(FoodComponent::nutrition),
					Codec.FLOAT.fieldOf("saturation_modifier").forGetter(FoodComponent::saturationModifier),
					Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "can_always_eat", false).forGetter(FoodComponent::canAlwaysEat),
					Codecs.createStrictOptionalFieldCodec(Codecs.POSITIVE_FLOAT, "eat_seconds", 1.6F).forGetter(FoodComponent::eatSeconds),
					Codecs.createStrictOptionalFieldCodec(FoodComponent.StatusEffectEntry.CODEC.listOf(), "effects", List.of()).forGetter(FoodComponent::effects)
				)
				.apply(instance, FoodComponent::new)
	);
	public static final PacketCodec<RegistryByteBuf, FoodComponent> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT,
		FoodComponent::nutrition,
		PacketCodecs.FLOAT,
		FoodComponent::saturationModifier,
		PacketCodecs.BOOL,
		FoodComponent::canAlwaysEat,
		PacketCodecs.FLOAT,
		FoodComponent::eatSeconds,
		FoodComponent.StatusEffectEntry.PACKET_CODEC.collect(PacketCodecs.toList()),
		FoodComponent::effects,
		FoodComponent::new
	);

	public int getEatTicks() {
		return (int)(this.eatSeconds * 20.0F);
	}

	public static class Builder {
		private int hunger;
		private float saturationModifier;
		private boolean alwaysEdible;
		private float eatSeconds = 1.6F;
		private final ImmutableList.Builder<FoodComponent.StatusEffectEntry> statusEffects = ImmutableList.builder();

		/**
		 * Specifies the amount of hunger a food item will fill.
		 * 
		 * <p>One hunger is equivalent to half of a hunger bar icon.
		 * 
		 * @param hunger the amount of hunger
		 */
		public FoodComponent.Builder hunger(int hunger) {
			this.hunger = hunger;
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
			this.alwaysEdible = true;
			return this;
		}

		/**
		 * Specifies that a food item is snack-like and is eaten quickly.
		 */
		public FoodComponent.Builder snack() {
			this.eatSeconds = 0.8F;
			return this;
		}

		/**
		 * Specifies a status effect to apply to an entity when a food item is consumed.
		 * This method may be called multiple times to apply several status effects when food is consumed.
		 * 
		 * @param chance the chance the status effect is applied, on a scale of {@code 0.0F} to {@code 1.0F}
		 * @param effect the effect instance to apply
		 */
		public FoodComponent.Builder statusEffect(StatusEffectInstance effect, float chance) {
			this.statusEffects.add(new FoodComponent.StatusEffectEntry(effect, chance));
			return this;
		}

		public FoodComponent build() {
			return new FoodComponent(this.hunger, this.saturationModifier, this.alwaysEdible, this.eatSeconds, this.statusEffects.build());
		}
	}

	public static record StatusEffectEntry(StatusEffectInstance effect, float probability) {
		public static final Codec<FoodComponent.StatusEffectEntry> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						StatusEffectInstance.CODEC.fieldOf("effect").forGetter(FoodComponent.StatusEffectEntry::effect),
						Codecs.createStrictOptionalFieldCodec(Codec.floatRange(0.0F, 1.0F), "probability", 1.0F).forGetter(FoodComponent.StatusEffectEntry::probability)
					)
					.apply(instance, FoodComponent.StatusEffectEntry::new)
		);
		public static final PacketCodec<RegistryByteBuf, FoodComponent.StatusEffectEntry> PACKET_CODEC = PacketCodec.tuple(
			StatusEffectInstance.PACKET_CODEC,
			FoodComponent.StatusEffectEntry::effect,
			PacketCodecs.FLOAT,
			FoodComponent.StatusEffectEntry::probability,
			FoodComponent.StatusEffectEntry::new
		);

		public StatusEffectInstance effect() {
			return new StatusEffectInstance(this.effect);
		}
	}
}
