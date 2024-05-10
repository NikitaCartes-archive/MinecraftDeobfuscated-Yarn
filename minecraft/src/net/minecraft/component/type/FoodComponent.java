package net.minecraft.component.type;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.HungerConstants;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;

/**
 * Represents the components that make up the properties of a food item.
 */
public record FoodComponent(
	int nutrition, float saturation, boolean canAlwaysEat, float eatSeconds, Optional<ItemStack> usingConvertsTo, List<FoodComponent.StatusEffectEntry> effects
) {
	private static final float DEFAULT_EAT_SECONDS = 1.6F;
	public static final Codec<FoodComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.NONNEGATIVE_INT.fieldOf("nutrition").forGetter(FoodComponent::nutrition),
					Codec.FLOAT.fieldOf("saturation").forGetter(FoodComponent::saturation),
					Codec.BOOL.optionalFieldOf("can_always_eat", Boolean.valueOf(false)).forGetter(FoodComponent::canAlwaysEat),
					Codecs.POSITIVE_FLOAT.optionalFieldOf("eat_seconds", 1.6F).forGetter(FoodComponent::eatSeconds),
					ItemStack.UNCOUNTED_CODEC.optionalFieldOf("using_converts_to").forGetter(FoodComponent::usingConvertsTo),
					FoodComponent.StatusEffectEntry.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(FoodComponent::effects)
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
		PacketCodecs.FLOAT,
		FoodComponent::eatSeconds,
		ItemStack.PACKET_CODEC.collect(PacketCodecs::optional),
		FoodComponent::usingConvertsTo,
		FoodComponent.StatusEffectEntry.PACKET_CODEC.collect(PacketCodecs.toList()),
		FoodComponent::effects,
		FoodComponent::new
	);

	public int getEatTicks() {
		return (int)(this.eatSeconds * 20.0F);
	}

	public static class Builder {
		private int nutrition;
		private float saturationModifier;
		private boolean canAlwaysEat;
		private float eatSeconds = 1.6F;
		private Optional<ItemStack> usingConvertsTo = Optional.empty();
		private final ImmutableList.Builder<FoodComponent.StatusEffectEntry> effects = ImmutableList.builder();

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
			this.effects.add(new FoodComponent.StatusEffectEntry(effect, chance));
			return this;
		}

		public FoodComponent.Builder usingConvertsTo(ItemConvertible item) {
			this.usingConvertsTo = Optional.of(new ItemStack(item));
			return this;
		}

		public FoodComponent build() {
			float f = HungerConstants.calculateSaturation(this.nutrition, this.saturationModifier);
			return new FoodComponent(this.nutrition, f, this.canAlwaysEat, this.eatSeconds, this.usingConvertsTo, this.effects.build());
		}
	}

	public static record StatusEffectEntry(StatusEffectInstance effect, float probability) {
		public static final Codec<FoodComponent.StatusEffectEntry> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						StatusEffectInstance.CODEC.fieldOf("effect").forGetter(FoodComponent.StatusEffectEntry::effect),
						Codec.floatRange(0.0F, 1.0F).optionalFieldOf("probability", 1.0F).forGetter(FoodComponent.StatusEffectEntry::probability)
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
