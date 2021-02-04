/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.SlabBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class OxidizableSlabBlock
extends SlabBlock
implements Oxidizable {
    private final Oxidizable.OxidizationLevel oxidizationLevel;
    private final Block waxed;

    public OxidizableSlabBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.oxidizationLevel = Oxidizable.OxidizationLevel.values()[Oxidizable.OxidizationLevel.values().length - 1];
        this.waxed = this;
    }

    public OxidizableSlabBlock(AbstractBlock.Settings settings, Oxidizable.OxidizationLevel oxidizationLevel, Block waxed) {
        super(settings);
        this.oxidizationLevel = oxidizationLevel;
        this.waxed = waxed;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return this.waxed != this;
    }

    @Override
    public Oxidizable.OxidizationLevel getDegradationLevel() {
        return this.oxidizationLevel;
    }

    @Override
    public BlockState getDegradationResult(BlockState state) {
        return (BlockState)((BlockState)this.waxed.getDefaultState().with(TYPE, state.get(TYPE))).with(WATERLOGGED, state.get(WATERLOGGED));
    }

    @Override
    public /* synthetic */ Enum getDegradationLevel() {
        return this.getDegradationLevel();
    }
}

