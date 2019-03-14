package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.apache.commons.lang3.tuple.Pair;

public class FoodItemSetting {
	private final int hunger;
	private final float saturationModifier;
	private final boolean wolfFood;
	private final boolean alwaysEdible;
	private final boolean eatenFast;
	private final List<Pair<StatusEffectInstance, Float>> statusEffectChances;

	private FoodItemSetting(int i, float f, boolean bl, boolean bl2, boolean bl3, List<Pair<StatusEffectInstance, Float>> list) {
		this.hunger = i;
		this.saturationModifier = f;
		this.wolfFood = bl;
		this.alwaysEdible = bl2;
		this.eatenFast = bl3;
		this.statusEffectChances = list;
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

	public boolean isEatenFast() {
		return this.eatenFast;
	}

	public List<Pair<StatusEffectInstance, Float>> getStatusEffectChances() {
		return this.statusEffectChances;
	}

	public static class Builder {
		private int hunger;
		private float saturationModifier;
		private boolean wolfFood;
		private boolean alwaysEdible;
		private boolean eatenFast;
		private final List<Pair<StatusEffectInstance, Float>> statusEffectChances = Lists.<Pair<StatusEffectInstance, Float>>newArrayList();

		public FoodItemSetting.Builder hunger(int i) {
			this.hunger = i;
			return this;
		}

		public FoodItemSetting.Builder saturationModifier(float f) {
			this.saturationModifier = f;
			return this;
		}

		public FoodItemSetting.Builder wolfFood() {
			this.wolfFood = true;
			return this;
		}

		public FoodItemSetting.Builder alwaysEdible() {
			this.alwaysEdible = true;
			return this;
		}

		public FoodItemSetting.Builder eatenFast() {
			this.eatenFast = true;
			return this;
		}

		public FoodItemSetting.Builder statusEffect(StatusEffectInstance statusEffectInstance, float f) {
			this.statusEffectChances.add(Pair.of(statusEffectInstance, f));
			return this;
		}

		public FoodItemSetting build() {
			return new FoodItemSetting(this.hunger, this.saturationModifier, this.wolfFood, this.alwaysEdible, this.eatenFast, this.statusEffectChances);
		}
	}
}
