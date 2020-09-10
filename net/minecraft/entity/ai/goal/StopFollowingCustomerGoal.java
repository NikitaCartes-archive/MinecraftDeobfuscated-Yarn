/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;

public class StopFollowingCustomerGoal
extends Goal {
    private final MerchantEntity merchant;

    public StopFollowingCustomerGoal(MerchantEntity merchant) {
        this.merchant = merchant;
        this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (!this.merchant.isAlive()) {
            return false;
        }
        if (this.merchant.isTouchingWater()) {
            return false;
        }
        if (!this.merchant.isOnGround()) {
            return false;
        }
        if (this.merchant.velocityModified) {
            return false;
        }
        PlayerEntity playerEntity = this.merchant.getCurrentCustomer();
        if (playerEntity == null) {
            return false;
        }
        if (this.merchant.squaredDistanceTo(playerEntity) > 16.0) {
            return false;
        }
        return playerEntity.currentScreenHandler != null;
    }

    @Override
    public void start() {
        this.merchant.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.merchant.setCurrentCustomer(null);
    }
}

