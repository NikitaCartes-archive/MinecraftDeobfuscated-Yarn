/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public class TransparentBlock
extends Block {
    protected TransparentBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isSideInvisible(BlockState blockState, BlockState blockState2, Direction direction) {
        if (blockState2.getBlock() == this) {
            return true;
        }
        return super.isSideInvisible(blockState, blockState2, direction);
    }
}

