/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Oxidizable {
    default public int getOxidationTime(Random random) {
        return 1200000 + random.nextInt(768000);
    }

    public BlockState getOxidationResult(BlockState var1);

    default public void scheduleOxidation(World world, Block block, BlockPos pos) {
        world.getBlockTickScheduler().schedule(pos, block, this.getOxidationTime(world.getRandom()));
    }

    default public void oxidize(World world, BlockState state, BlockPos pos) {
        world.setBlockState(pos, this.getOxidationResult(state));
    }
}

