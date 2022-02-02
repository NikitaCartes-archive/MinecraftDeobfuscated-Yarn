/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;

public class LookAtCustomerGoal
extends LookAtEntityGoal {
    private final MerchantEntity merchant;

    public LookAtCustomerGoal(MerchantEntity merchant) {
        super(merchant, PlayerEntity.class, 8.0f);
        this.merchant = merchant;
    }

    @Override
    public boolean canStart() {
        if (this.merchant.hasCustomer()) {
            this.target = this.merchant.getCustomer();
            return true;
        }
        return false;
    }
}

