/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.class_5217;
import net.minecraft.util.math.BlockPos;

public interface class_5269
extends class_5217 {
    public void method_27416(int var1);

    public void method_27417(int var1);

    public void method_27419(int var1);

    default public void setSpawnPos(BlockPos blockPos) {
        this.method_27416(blockPos.getX());
        this.method_27417(blockPos.getY());
        this.method_27419(blockPos.getZ());
    }

    public void setTime(long var1);

    public void setTimeOfDay(long var1);
}

