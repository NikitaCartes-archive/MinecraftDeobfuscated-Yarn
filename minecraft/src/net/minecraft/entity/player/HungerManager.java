package net.minecraft.entity.player;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

public class HungerManager {
	private int foodLevel = 20;
	private float saturationLevel = 5.0F;
	private float exhaustion;
	private int foodTickTimer;

	private void addInternal(int nutrition, float saturation) {
		this.foodLevel = MathHelper.clamp(nutrition + this.foodLevel, 0, 20);
		this.saturationLevel = MathHelper.clamp(saturation + this.saturationLevel, 0.0F, (float)this.foodLevel);
	}

	public void add(int food, float saturationModifier) {
		this.addInternal(food, HungerConstants.calculateSaturation(food, saturationModifier));
	}

	public void eat(FoodComponent foodComponent) {
		this.addInternal(foodComponent.nutrition(), foodComponent.saturation());
	}

	public void update(ServerPlayerEntity player) {
		ServerWorld serverWorld = player.getServerWorld();
		Difficulty difficulty = serverWorld.getDifficulty();
		if (this.exhaustion > 4.0F) {
			this.exhaustion -= 4.0F;
			if (this.saturationLevel > 0.0F) {
				this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
			} else if (difficulty != Difficulty.PEACEFUL) {
				this.foodLevel = Math.max(this.foodLevel - 1, 0);
			}
		}

		boolean bl = serverWorld.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
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
					player.damage(serverWorld, player.getDamageSources().starve(), 1.0F);
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

	public boolean isNotFull() {
		return this.foodLevel < 20;
	}

	public void addExhaustion(float exhaustion) {
		this.exhaustion = Math.min(this.exhaustion + exhaustion, 40.0F);
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
}
