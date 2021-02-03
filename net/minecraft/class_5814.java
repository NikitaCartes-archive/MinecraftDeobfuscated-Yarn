/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.class_5810;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class class_5814
extends StairsBlock
implements class_5810 {
    private final class_5810.class_5811 field_28713;
    private final Block field_28714;

    public class_5814(BlockState blockState, AbstractBlock.Settings settings) {
        super(blockState, settings);
        this.field_28713 = class_5810.class_5811.values()[class_5810.class_5811.values().length - 1];
        this.field_28714 = this;
    }

    public class_5814(BlockState blockState, AbstractBlock.Settings settings, class_5810.class_5811 arg, Block block) {
        super(blockState, settings);
        this.field_28713 = arg;
        this.field_28714 = block;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.method_33621(state, world, pos, random);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return this.field_28714 != this;
    }

    @Override
    public class_5810.class_5811 method_33622() {
        return this.field_28713;
    }

    @Override
    public BlockState getOxidationResult(BlockState state) {
        return (BlockState)((BlockState)((BlockState)((BlockState)this.field_28714.getDefaultState().with(FACING, state.get(FACING))).with(HALF, state.get(HALF))).with(SHAPE, state.get(SHAPE))).with(WATERLOGGED, state.get(WATERLOGGED));
    }

    @Override
    public /* synthetic */ Enum method_33622() {
        return this.method_33622();
    }
}

