/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.placer;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.placer.BlockPlacerType;

public abstract class BlockPlacer
implements DynamicSerializable {
    protected final BlockPlacerType<?> type;

    protected BlockPlacer(BlockPlacerType<?> type) {
        this.type = type;
    }

    public abstract void method_23403(IWorld var1, BlockPos var2, BlockState var3, Random var4);
}

