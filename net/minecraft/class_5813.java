/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.class_5810;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class class_5813
extends SlabBlock
implements class_5810 {
    private final class_5810.class_5811 field_28711;
    private final Block field_28712;

    public class_5813(AbstractBlock.Settings settings) {
        super(settings);
        this.field_28711 = class_5810.class_5811.values()[class_5810.class_5811.values().length - 1];
        this.field_28712 = this;
    }

    public class_5813(AbstractBlock.Settings settings, class_5810.class_5811 arg, Block block) {
        super(settings);
        this.field_28711 = arg;
        this.field_28712 = block;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.method_33621(state, world, pos, random);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return this.field_28712 != this;
    }

    @Override
    public class_5810.class_5811 method_33622() {
        return this.field_28711;
    }

    @Override
    public BlockState getOxidationResult(BlockState state) {
        return (BlockState)((BlockState)this.field_28712.getDefaultState().with(TYPE, state.get(TYPE))).with(WATERLOGGED, state.get(WATERLOGGED));
    }

    @Override
    public /* synthetic */ Enum method_33622() {
        return this.method_33622();
    }
}

