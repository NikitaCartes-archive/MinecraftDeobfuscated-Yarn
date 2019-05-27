package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.apache.commons.lang3.tuple.Pair;

public class FoodComponent {
	private final int hunger;
	private final float saturationModifier;
	private final boolean wolfFood;
	private final boolean alwaysEdible;
	private final boolean snack;
	private final List<Pair<StatusEffectInstance, Float>> statusEffects;

	private FoodComponent(int i, float f, boolean bl, boolean bl2, boolean bl3, List<Pair<StatusEffectInstance, Float>> list) {
		this.hunger = i;
		this.saturationModifier = f;
		this.wolfFood = bl;
		this.alwaysEdible = bl2;
		this.snack = bl3;
		this.statusEffects = list;
	}

	public int getHunger() {
		return this.hunger;
	}

	public float getSaturationModifier() {
		return this.saturationModifier;
	}

	public boolean isWolfFood() {
		return this.wolfFood;
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
		private boolean wolfFood;
		private boolean alwaysEdible;
		private boolean snack;
		private final List<Pair<StatusEffectInstance, Float>> statusEffects = Lists.<Pair<StatusEffectInstance, Float>>newArrayList();

		public FoodComponent.Builder hunger(int i) {
			this.hunger = i;
			return this;
		}

		public FoodComponent.Builder saturationModifier(float f) {
			this.saturationModifier = f;
			return this;
		}

		public FoodComponent.Builder wolfFood() {
			this.wolfFood = true;
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

		public FoodComponent.Builder statusEffect(StatusEffectInstance statusEffectInstance, float f) {
			this.statusEffects.add(Pair.of(statusEffectInstance, f));
			return this;
		}

		public FoodComponent build() {
			return new FoodComponent(this.hunger, this.saturationModifier, this.wolfFood, this.alwaysEdible, this.snack, this.statusEffects);
		}
	}
}
