/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpreadableBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MyceliumBlock
extends SpreadableBlock {
    public MyceliumBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        super.randomDisplayTick(blockState, world, blockPos, random);
        if (random.nextInt(10) == 0) {
            world.addParticle(ParticleTypes.MYCELIUM, (float)blockPos.getX() + random.nextFloat(), (float)blockPos.getY() + 1.1f, (float)blockPos.getZ() + random.nextFloat(), 0.0, 0.0, 0.0);
        }
    }
}

