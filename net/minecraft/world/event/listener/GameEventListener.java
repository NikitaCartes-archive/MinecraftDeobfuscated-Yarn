/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.event.listener;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import org.jetbrains.annotations.Nullable;

public interface GameEventListener {
    public PositionSource getPositionSource();

    public int getRange();

    public boolean listen(World var1, GameEvent var2, @Nullable Entity var3, BlockPos var4);
}

