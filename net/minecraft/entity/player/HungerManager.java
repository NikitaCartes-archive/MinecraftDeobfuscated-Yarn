/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;

public class HungerManager {
    private int foodLevel = 20;
    private float foodSaturationLevel = 5.0f;
    private float exhaustion;
    private int foodStarvationTimer;
    private int prevFoodLevel = 20;

    public void add(int i, float f) {
        this.foodLevel = Math.min(i + this.foodLevel, 20);
        this.foodSaturationLevel = Math.min(this.foodSaturationLevel + (float)i * f * 2.0f, (float)this.foodLevel);
    }

    public void eat(Item item, ItemStack itemStack) {
        if (item.isFood()) {
            FoodComponent foodComponent = item.getFoodComponent();
            this.add(foodComponent.getHunger(), foodComponent.getSaturationModifier());
        }
    }

    public void update(PlayerEntity playerEntity) {
        boolean bl;
        Difficulty difficulty = playerEntity.world.getDifficulty();
        this.prevFoodLevel = this.foodLevel;
        if (this.exhaustion > 4.0f) {
            this.exhaustion -= 4.0f;
            if (this.foodSaturationLevel > 0.0f) {
                this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0f, 0.0f);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }
        if ((bl = playerEntity.world.getGameRules().getBoolean("naturalRegeneration")) && this.foodSaturationLevel > 0.0f && playerEntity.canFoodHeal() && this.foodLevel >= 20) {
            ++this.foodStarvationTimer;
            if (this.foodStarvationTimer >= 10) {
                float f = Math.min(this.foodSaturationLevel, 6.0f);
                playerEntity.heal(f / 6.0f);
                this.addExhaustion(f);
                this.foodStarvationTimer = 0;
            }
        } else if (bl && this.foodLevel >= 18 && playerEntity.canFoodHeal()) {
            ++this.foodStarvationTimer;
            if (this.foodStarvationTimer >= 80) {
                playerEntity.heal(1.0f);
                this.addExhaustion(6.0f);
                this.foodStarvationTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.foodStarvationTimer;
            if (this.foodStarvationTimer >= 80) {
                if (playerEntity.getHealth() > 10.0f || difficulty == Difficulty.HARD || playerEntity.getHealth() > 1.0f && difficulty == Difficulty.NORMAL) {
                    playerEntity.damage(DamageSource.STARVE, 1.0f);
                }
                this.foodStarvationTimer = 0;
            }
        } else {
            this.foodStarvationTimer = 0;
        }
    }

    public void deserialize(CompoundTag compoundTag) {
        if (compoundTag.containsKey("foodLevel", 99)) {
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

    public void addExhaustion(float f) {
        this.exhaustion = Math.min(this.exhaustion + f, 40.0f);
    }

    public float getSaturationLevel() {
        return this.foodSaturationLevel;
    }

    public void setFoodLevel(int i) {
        this.foodLevel = i;
    }

    @Environment(value=EnvType.CLIENT)
    public void setSaturationLevelClient(float f) {
        this.foodSaturationLevel = f;
    }
}

