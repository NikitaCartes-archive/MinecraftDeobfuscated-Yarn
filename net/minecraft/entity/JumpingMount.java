/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface JumpingMount {
    @Environment(value=EnvType.CLIENT)
    public void setJumpStrength(int var1);

    public boolean canJump();

    public void startJumping(int var1);

    public void stopJumping();
}

