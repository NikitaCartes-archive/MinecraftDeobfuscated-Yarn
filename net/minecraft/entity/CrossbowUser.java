/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface CrossbowUser {
    public void setCharging(boolean var1);

    public void shoot(LivingEntity var1, ItemStack var2, Projectile var3, float var4);

    @Nullable
    public LivingEntity getTarget();
}

