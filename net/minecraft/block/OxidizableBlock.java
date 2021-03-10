/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class OxidizableBlock
extends Block
implements Oxidizable {
    private final Oxidizable.OxidizationLevel oxidizationLevel;
    private final Block degraded;

    public OxidizableBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.oxidizationLevel = Oxidizable.OxidizationLevel.values()[Oxidizable.OxidizationLevel.values().length - 1];
        this.degraded = this;
    }

    public OxidizableBlock(AbstractBlock.Settings settings, Oxidizable.OxidizationLevel oxidizationLevel, Block degraded) {
        super(settings);
        this.oxidizationLevel = oxidizationLevel;
        this.degraded = degraded;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return this.degraded != this;
    }

    @Override
    public Oxidizable.OxidizationLevel getDegradationLevel() {
        return this.oxidizationLevel;
    }

    @Override
    public BlockState getDegradationResult(BlockState state) {
        return this.degraded.getDefaultState();
    }

    @Override
    public /* synthetic */ Enum getDegradationLevel() {
        return this.getDegradationLevel();
    }
}

