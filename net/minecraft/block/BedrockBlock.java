/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class BedrockBlock
extends Block {
    public BedrockBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public boolean allowsSpawning(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
        return false;
    }
}

