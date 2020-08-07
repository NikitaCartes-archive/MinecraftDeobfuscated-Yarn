package net.minecraft.entity.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

public class HungerManager {
	private int foodLevel = 20;
	private float foodSaturationLevel;
	private float exhaustion;
	private int foodStarvationTimer;
	private int prevFoodLevel = 20;

	public HungerManager() {
		this.foodSaturationLevel = 5.0F;
	}

	public void add(int food, float f) {
		this.foodLevel = Math.min(food + this.foodLevel, 20);
		this.foodSaturationLevel = Math.max(this.foodSaturationLevel, (float)food * f * 2.0F);
	}

	public void eat(Item item, ItemStack stack) {
		if (item.isFood()) {
			FoodComponent foodComponent = item.getFoodComponent();
			this.add(foodComponent.getHunger(), foodComponent.getSaturationModifier());
		}
	}

	public void update(PlayerEntity player) {
		Difficulty difficulty = player.world.getDifficulty();
		this.prevFoodLevel = this.foodLevel;
		if (this.exhaustion > 4.0F) {
			this.exhaustion -= 4.0F;
			if (this.foodSaturationLevel > 0.0F) {
				this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
			} else if (difficulty != Difficulty.field_5801) {
				this.foodLevel = Math.max(this.foodLevel - 1, 0);
			}
		}

		boolean bl = player.world.getGameRules().getBoolean(GameRules.field_19395);
		if (bl && this.foodLevel > 6 && player.canFoodHeal()) {
			this.foodStarvationTimer++;
			if (this.foodStarvationTimer >= 40) {
				player.heal(1.0F);
				if (player.world.random.nextBoolean()) {
					this.foodLevel = Math.max(this.foodLevel - 1, 0);
				}

				this.foodStarvationTimer = 0;
			}
		} else if (this.foodLevel <= 0) {
			this.foodStarvationTimer++;
			if (this.foodStarvationTimer >= 40) {
				if (player.getHealth() > 10.0F || difficulty == Difficulty.field_5807 || player.getHealth() > 1.0F && difficulty == Difficulty.field_5802) {
					player.damage(DamageSource.STARVE, 1.0F);
				}

				this.foodStarvationTimer = 0;
			}
		} else {
			this.foodStarvationTimer = 0;
		}
	}

	public void fromTag(CompoundTag tag) {
		if (tag.contains("foodLevel", 99)) {
			this.foodLevel = tag.getInt("foodLevel");
			this.foodStarvationTimer = tag.getInt("foodTickTimer");
			this.foodSaturationLevel = tag.getFloat("foodSaturationLevel");
			this.exhaustion = tag.getFloat("foodExhaustionLevel");
		}
	}

	public void toTag(CompoundTag tag) {
		tag.putInt("foodLevel", this.foodLevel);
		tag.putInt("foodTickTimer", this.foodStarvationTimer);
		tag.putFloat("foodSaturationLevel", this.foodSaturationLevel);
		tag.putFloat("foodExhaustionLevel", this.exhaustion);
	}

	public int getFoodLevel() {
		return this.foodLevel;
	}

	public boolean isNotFull() {
		return this.foodLevel < 20;
	}

	public void addExhaustion(float exhaustion) {
		this.exhaustion = Math.min(this.exhaustion + exhaustion, 40.0F);
	}

	public float getSaturationLevel() {
		return this.foodSaturationLevel;
	}

	public void setFoodLevel(int foodLevel) {
		this.foodLevel = foodLevel;
	}

	@Environment(EnvType.CLIENT)
	public void setSaturationLevelClient(float saturationLevel) {
		this.foodSaturationLevel = saturationLevel;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_31209() {
		return this.foodLevel > 6;
	}
}
