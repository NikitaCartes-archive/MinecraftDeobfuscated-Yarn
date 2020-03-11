/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.shape.VoxelShape;

public class VineLogic {
    public static final VoxelShape STEM_OUTLINE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public static boolean isValidForWeepingStem(BlockState state) {
        return state.isAir();
    }
}

