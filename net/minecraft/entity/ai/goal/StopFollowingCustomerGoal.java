/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.player.PlayerEntity;

public class StopFollowingCustomerGoal
extends Goal {
    private final AbstractTraderEntity trader;

    public StopFollowingCustomerGoal(AbstractTraderEntity trader) {
        this.trader = trader;
        this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (!this.trader.isAlive()) {
            return false;
        }
        if (this.trader.isTouchingWater()) {
            return false;
        }
        if (!this.trader.onGround) {
            return false;
        }
        if (this.trader.velocityModified) {
            return false;
        }
        PlayerEntity playerEntity = this.trader.getCurrentCustomer();
        if (playerEntity == null) {
            return false;
        }
        if (this.trader.squaredDistanceTo(playerEntity) > 16.0) {
            return false;
        }
        return playerEntity.container != null;
    }

    @Override
    public void start() {
        this.trader.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.trader.setCurrentCustomer(null);
    }
}

