/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CauldronBlock
extends AbstractCauldronBlock {
    public CauldronBlock(AbstractBlock.Settings settings) {
        super(settings, CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR);
    }

    protected static boolean canFillWithRain(World world, BlockPos pos) {
        if (world.random.nextInt(20) != 1) {
            return false;
        }
        return world.getBiome(pos).getTemperature(pos) >= 0.15f;
    }

    @Override
    public void rainTick(BlockState state, World world, BlockPos pos) {
        if (!CauldronBlock.canFillWithRain(world, pos)) {
            return;
        }
        world.setBlockState(pos, Blocks.WATER_CAULDRON.getDefaultState());
    }
}

