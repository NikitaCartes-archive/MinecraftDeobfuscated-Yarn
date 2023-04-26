package net.minecraft.entity.player;

import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

public class HungerManager {
	private int foodLevel = 20;
	private float saturationLevel;
	private float exhaustion;
	private int foodTickTimer;
	private int prevFoodLevel = 20;

	public HungerManager() {
		this.saturationLevel = 5.0F;
	}

	public void add(int food, float saturationModifier) {
		this.foodLevel = Math.min(food + this.foodLevel, 20);
		this.saturationLevel = Math.min(this.saturationLevel + (float)food * saturationModifier * 2.0F, (float)this.foodLevel);
	}

	public void eat(Item item, ItemStack stack) {
		if (item.isFood()) {
			FoodComponent foodComponent = item.getFoodComponent();
			this.add(foodComponent.getHunger(), foodComponent.getSaturationModifier());
		}
	}

	public void update(PlayerEntity player) {
		Difficulty difficulty = player.getWorld().getDifficulty();
		this.prevFoodLevel = this.foodLevel;
		if (this.exhaustion > 4.0F) {
			this.exhaustion -= 4.0F;
			if (this.saturationLevel > 0.0F) {
				this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
			} else if (difficulty != Difficulty.PEACEFUL) {
				this.foodLevel = Math.max(this.foodLevel - 1, 0);
			}
		}

		boolean bl = player.getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
		if (bl && this.saturationLevel > 0.0F && player.canFoodHeal() && this.foodLevel >= 20) {
			this.foodTickTimer++;
			if (this.foodTickTimer >= 10) {
				float f = Math.min(this.saturationLevel, 6.0F);
				player.heal(f / 6.0F);
				this.addExhaustion(f);
				this.foodTickTimer = 0;
			}
		} else if (bl && this.foodLevel >= 18 && player.canFoodHeal()) {
			this.foodTickTimer++;
			if (this.foodTickTimer >= 80) {
				player.heal(1.0F);
				this.addExhaustion(6.0F);
				this.foodTickTimer = 0;
			}
		} else if (this.foodLevel <= 0) {
			this.foodTickTimer++;
			if (this.foodTickTimer >= 80) {
				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
					player.damage(player.getDamageSources().starve(), 1.0F);
				}

				this.foodTickTimer = 0;
			}
		} else {
			this.foodTickTimer = 0;
		}
	}

	public void readNbt(NbtCompound nbt) {
		if (nbt.contains("foodLevel", NbtElement.NUMBER_TYPE)) {
			this.foodLevel = nbt.getInt("foodLevel");
			this.foodTickTimer = nbt.getInt("foodTickTimer");
			this.saturationLevel = nbt.getFloat("foodSaturationLevel");
			this.exhaustion = nbt.getFloat("foodExhaustionLevel");
		}
	}

	public void writeNbt(NbtCompound nbt) {
		nbt.putInt("foodLevel", this.foodLevel);
		nbt.putInt("foodTickTimer", this.foodTickTimer);
		nbt.putFloat("foodSaturationLevel", this.saturationLevel);
		nbt.putFloat("foodExhaustionLevel", this.exhaustion);
	}

	public int getFoodLevel() {
		return this.foodLevel;
	}

	public int getPrevFoodLevel() {
		return this.prevFoodLevel;
	}

	public boolean isNotFull() {
		return this.foodLevel < 20;
	}

	public void addExhaustion(float exhaustion) {
		this.exhaustion = Math.min(this.exhaustion + exhaustion, 40.0F);
	}

	public float getExhaustion() {
		return this.exhaustion;
	}

	public float getSaturationLevel() {
		return this.saturationLevel;
	}

	public void setFoodLevel(int foodLevel) {
		this.foodLevel = foodLevel;
	}

	public void setSaturationLevel(float saturationLevel) {
		this.saturationLevel = saturationLevel;
	}

	public void setExhaustion(float exhaustion) {
		this.exhaustion = exhaustion;
	}
}
