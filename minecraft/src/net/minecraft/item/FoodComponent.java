package net.minecraft.item;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.entity.effect.StatusEffectInstance;

/**
 * Represents the components that make up the properties of a food item.
 */
public class FoodComponent {
	private final int hunger;
	private final float saturationModifier;
	private final boolean meat;
	private final boolean alwaysEdible;
	private final boolean snack;
	private final List<Pair<StatusEffectInstance, Float>> statusEffects;

	FoodComponent(int hunger, float saturationModifier, boolean meat, boolean alwaysEdible, boolean snack, List<Pair<StatusEffectInstance, Float>> statusEffects) {
		this.hunger = hunger;
		this.saturationModifier = saturationModifier;
		this.meat = meat;
		this.alwaysEdible = alwaysEdible;
		this.snack = snack;
		this.statusEffects = statusEffects;
	}

	/**
	 * Gets the amount of hunger a food item will fill.
	 * 
	 * <p>One hunger is equivalent to half of a hunger bar icon.
	 */
	public int getHunger() {
		return this.hunger;
	}

	/**
	 * Gets the saturation modifier of a food item.
	 * 
	 * <p>This value is typically used to determine how long a player can sustain the current hunger value before the hunger is used.
	 */
	public float getSaturationModifier() {
		return this.saturationModifier;
	}

	/**
	 * Checks if a food item can be fed to dogs.
	 */
	public boolean isMeat() {
		return this.meat;
	}

	/**
	 * Checks if a food item can be eaten when the current hunger bar is full.
	 */
	public boolean isAlwaysEdible() {
		return this.alwaysEdible;
	}

	/**
	 * Checks if a food item is snack-like and is eaten quickly.
	 */
	public boolean isSnack() {
		return this.snack;
	}

	/**
	 * Gets a list of all status effect instances that may be applied when a food item is consumed.
	 * 
	 * <p>The first value in the pair is the status effect instance to be applied.
	 * <p>The second value is the chance the status effect gets applied, on a scale between {@code 0.0F} and {@code 1.0F}.
	 */
	public List<Pair<StatusEffectInstance, Float>> getStatusEffects() {
		return this.statusEffects;
	}

	public static class Builder {
		private int hunger;
		private float saturationModifier;
		private boolean meat;
		private boolean alwaysEdible;
		private boolean snack;
		private final List<Pair<StatusEffectInstance, Float>> statusEffects = Lists.<Pair<StatusEffectInstance, Float>>newArrayList();

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
		 * Specifies that a food item can be fed to dogs.
		 */
		public FoodComponent.Builder meat() {
			this.meat = true;
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
			this.snack = true;
			return this;
		}

		/**
		 * Specifies a status effect to apply to an entity when a food item is consumed.
		 * This method may be called multiple times to apply several status effects when food is consumed.
		 * 
		 * @param effect the effect instance to apply
		 * @param chance the chance the status effect is applied, on a scale of {@code 0.0F} to {@code 1.0F}
		 */
		public FoodComponent.Builder statusEffect(StatusEffectInstance effect, float chance) {
			this.statusEffects.add(Pair.of(effect, chance));
			return this;
		}

		public FoodComponent build() {
			return new FoodComponent(this.hunger, this.saturationModifier, this.meat, this.alwaysEdible, this.snack, this.statusEffects);
		}
	}
}
