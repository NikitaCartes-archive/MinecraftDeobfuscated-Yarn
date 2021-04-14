/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.player;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

public class HungerManager {
    private int foodLevel = 20;
    private float foodSaturationLevel = 5.0f;
    private float exhaustion;
    private int foodTickTimer;
    private int prevFoodLevel = 20;

    public void add(int food, float saturationModifier) {
        this.foodLevel = Math.min(food + this.foodLevel, 20);
        this.foodSaturationLevel = Math.min(this.foodSaturationLevel + (float)food * saturationModifier * 2.0f, (float)this.foodLevel);
    }

    public void eat(Item item, ItemStack stack) {
        if (item.isFood()) {
            FoodComponent foodComponent = item.getFoodComponent();
            this.add(foodComponent.getHunger(), foodComponent.getSaturationModifier());
        }
    }

    public void update(PlayerEntity player) {
        boolean bl;
        Difficulty difficulty = player.world.getDifficulty();
        this.prevFoodLevel = this.foodLevel;
        if (this.exhaustion > 4.0f) {
            this.exhaustion -= 4.0f;
            if (this.foodSaturationLevel > 0.0f) {
                this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0f, 0.0f);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }
        if ((bl = player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) && this.foodSaturationLevel > 0.0f && player.canFoodHeal() && this.foodLevel >= 20) {
            ++this.foodTickTimer;
            if (this.foodTickTimer >= 10) {
                float f = Math.min(this.foodSaturationLevel, 6.0f);
                player.heal(f / 6.0f);
                this.addExhaustion(f);
                this.foodTickTimer = 0;
            }
        } else if (bl && this.foodLevel >= 18 && player.canFoodHeal()) {
            ++this.foodTickTimer;
            if (this.foodTickTimer >= 80) {
                player.heal(1.0f);
                this.addExhaustion(6.0f);
                this.foodTickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.foodTickTimer;
            if (this.foodTickTimer >= 80) {
                if (player.getHealth() > 10.0f || difficulty == Difficulty.HARD || player.getHealth() > 1.0f && difficulty == Difficulty.NORMAL) {
                    player.damage(DamageSource.STARVE, 1.0f);
                }
                this.foodTickTimer = 0;
            }
        } else {
            this.foodTickTimer = 0;
        }
    }

    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("foodLevel", 99)) {
            this.foodLevel = nbt.getInt("foodLevel");
            this.foodTickTimer = nbt.getInt("foodTickTimer");
            this.foodSaturationLevel = nbt.getFloat("foodSaturationLevel");
            this.exhaustion = nbt.getFloat("foodExhaustionLevel");
        }
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("foodLevel", this.foodLevel);
        nbt.putInt("foodTickTimer", this.foodTickTimer);
        nbt.putFloat("foodSaturationLevel", this.foodSaturationLevel);
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
        this.exhaustion = Math.min(this.exhaustion + exhaustion, 40.0f);
    }

    public float getExhaustion() {
        return this.exhaustion;
    }

    public float getSaturationLevel() {
        return this.foodSaturationLevel;
    }

    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    public void setSaturationLevel(float saturationLevel) {
        this.foodSaturationLevel = saturationLevel;
    }

    public void setExhaustion(float exhaustion) {
        this.exhaustion = exhaustion;
    }
}

