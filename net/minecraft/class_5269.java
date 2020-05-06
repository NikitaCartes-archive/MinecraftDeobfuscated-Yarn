/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.class_5217;
import net.minecraft.util.math.BlockPos;

public interface class_5269
extends class_5217 {
    public void setSpawnX(int var1);

    public void setSpawnY(int var1);

    public void setSpawnZ(int var1);

    default public void setSpawnPos(BlockPos pos) {
        this.setSpawnX(pos.getX());
        this.setSpawnY(pos.getY());
        this.setSpawnZ(pos.getZ());
    }

    public void setTime(long var1);

    public void setTimeOfDay(long var1);
}

