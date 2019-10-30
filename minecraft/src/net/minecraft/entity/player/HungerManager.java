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
		this.foodSaturationLevel = Math.min(this.foodSaturationLevel + (float)food * f * 2.0F, (float)this.foodLevel);
	}

	public void eat(Item item, ItemStack itemStack) {
		if (item.isFood()) {
			FoodComponent foodComponent = item.getFoodComponent();
			this.add(foodComponent.getHunger(), foodComponent.getSaturationModifier());
		}
	}

	public void update(PlayerEntity playerEntity) {
		Difficulty difficulty = playerEntity.world.getDifficulty();
		this.prevFoodLevel = this.foodLevel;
		if (this.exhaustion > 4.0F) {
			this.exhaustion -= 4.0F;
			if (this.foodSaturationLevel > 0.0F) {
				this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
			} else if (difficulty != Difficulty.PEACEFUL) {
				this.foodLevel = Math.max(this.foodLevel - 1, 0);
			}
		}

		boolean bl = playerEntity.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
		if (bl && this.foodSaturationLevel > 0.0F && playerEntity.canFoodHeal() && this.foodLevel >= 20) {
			this.foodStarvationTimer++;
			if (this.foodStarvationTimer >= 10) {
				float f = Math.min(this.foodSaturationLevel, 6.0F);
				playerEntity.heal(f / 6.0F);
				this.addExhaustion(f);
				this.foodStarvationTimer = 0;
			}
		} else if (bl && this.foodLevel >= 18 && playerEntity.canFoodHeal()) {
			this.foodStarvationTimer++;
			if (this.foodStarvationTimer >= 80) {
				playerEntity.heal(1.0F);
				this.addExhaustion(6.0F);
				this.foodStarvationTimer = 0;
			}
		} else if (this.foodLevel <= 0) {
			this.foodStarvationTimer++;
			if (this.foodStarvationTimer >= 80) {
				if (playerEntity.getHealth() > 10.0F || difficulty == Difficulty.HARD || playerEntity.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
					playerEntity.damage(DamageSource.STARVE, 1.0F);
				}

				this.foodStarvationTimer = 0;
			}
		} else {
			this.foodStarvationTimer = 0;
		}
	}

	public void deserialize(CompoundTag compoundTag) {
		if (compoundTag.contains("foodLevel", 99)) {
			this.foodLevel = compoundTag.getInt("foodLevel");
			this.foodStarvationTimer = compoundTag.getInt("foodTickTimer");
			this.foodSaturationLevel = compoundTag.getFloat("foodSaturationLevel");
			this.exhaustion = compoundTag.getFloat("foodExhaustionLevel");
		}
	}

	public void serialize(CompoundTag compoundTag) {
		compoundTag.putInt("foodLevel", this.foodLevel);
		compoundTag.putInt("foodTickTimer", this.foodStarvationTimer);
		compoundTag.putFloat("foodSaturationLevel", this.foodSaturationLevel);
		compoundTag.putFloat("foodExhaustionLevel", this.exhaustion);
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
}
