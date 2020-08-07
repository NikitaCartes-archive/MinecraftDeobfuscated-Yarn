package net.minecraft.item;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.entity.effect.StatusEffectInstance;

public class FoodComponent {
	private final int hunger;
	private final float saturationModifier;
	private final boolean meat;
	private final boolean alwaysEdible;
	private final boolean snack;
	private final List<Pair<StatusEffectInstance, Float>> statusEffects;

	private FoodComponent(
		int hunger, float saturationModifier, boolean meat, boolean alwaysEdible, boolean snack, List<Pair<StatusEffectInstance, Float>> statusEffects
	) {
		this.hunger = hunger;
		this.saturationModifier = saturationModifier;
		this.meat = meat;
		this.alwaysEdible = alwaysEdible;
		this.snack = snack;
		this.statusEffects = statusEffects;
	}

	public int getHunger() {
		return this.hunger;
	}

	public float getSaturationModifier() {
		return this.saturationModifier;
	}

	public boolean isMeat() {
		return this.meat;
	}

	public boolean isAlwaysEdible() {
		return this.alwaysEdible;
	}

	public boolean isSnack() {
		return this.snack;
	}

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

		public FoodComponent.Builder hunger(int hunger) {
			this.hunger = hunger;
			return this;
		}

		public FoodComponent.Builder saturationModifier(float saturationModifier) {
			this.saturationModifier = saturationModifier;
			return this;
		}

		public FoodComponent.Builder meat() {
			this.meat = true;
			return this;
		}

		public FoodComponent.Builder alwaysEdible() {
			this.alwaysEdible = true;
			return this;
		}

		public FoodComponent.Builder snack() {
			this.snack = true;
			return this;
		}

		public FoodComponent.Builder statusEffect(StatusEffectInstance effect, float chance) {
			this.statusEffects.add(Pair.of(effect, chance));
			return this;
		}

		public FoodComponent build() {
			return new FoodComponent(this.hunger, this.saturationModifier, this.meat, this.alwaysEdible, this.snack, this.statusEffects);
		}
	}
}
