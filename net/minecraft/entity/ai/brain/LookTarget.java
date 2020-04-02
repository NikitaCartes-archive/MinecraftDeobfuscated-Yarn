/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface LookTarget {
    public Vec3d getPos();

    public BlockPos getBlockPos();

    public boolean isSeenBy(LivingEntity var1);
}

