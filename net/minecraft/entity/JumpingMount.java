/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import net.minecraft.entity.Mount;
import net.minecraft.entity.player.PlayerEntity;

public interface JumpingMount
extends Mount {
    public void setJumpStrength(int var1);

    public boolean canJump(PlayerEntity var1);

    public void startJumping(int var1);

    public void stopJumping();

    default public int getJumpCooldown() {
        return 0;
    }
}

