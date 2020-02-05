/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WeepingVinesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class WeepingVinesFeature
extends Feature<DefaultFeatureConfig> {
    private static final Direction[] DIRECTIONS = Direction.values();

    public WeepingVinesFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        if (!iWorld.isAir(blockPos)) {
            return false;
        }
        Block block = iWorld.getBlockState(blockPos.up()).getBlock();
        if (block != Blocks.NETHERRACK && block != Blocks.NETHER_WART_BLOCK) {
            return false;
        }
        this.tryGenerateNearVines(iWorld, random, blockPos);
        this.tryGenerateFarVines(iWorld, random, blockPos);
        return true;
    }

    private void tryGenerateNearVines(IWorld world, Random random, BlockPos pos) {
        world.setBlockState(pos, Blocks.NETHER_WART_BLOCK.getDefaultState(), 2);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos.Mutable mutable2 = new BlockPos.Mutable();
        for (int i = 0; i < 200; ++i) {
            mutable.set(pos).setOffset(random.nextInt(6) - random.nextInt(6), random.nextInt(2) - random.nextInt(5), random.nextInt(6) - random.nextInt(6));
            if (!world.isAir(mutable)) continue;
            int j = 0;
            for (Direction direction : DIRECTIONS) {
                Block block = world.getBlockState(mutable2.set(mutable).setOffset(direction)).getBlock();
                if (block == Blocks.NETHERRACK || block == Blocks.NETHER_WART_BLOCK) {
                    ++j;
                }
                if (j > 1) break;
            }
            if (j != true) continue;
            world.setBlockState(mutable, Blocks.NETHER_WART_BLOCK.getDefaultState(), 2);
        }
    }

    private void tryGenerateFarVines(IWorld world, Random random, BlockPos pos) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = 0; i < 100; ++i) {
            Block block;
            mutable.set(pos).setOffset(random.nextInt(8) - random.nextInt(8), random.nextInt(2) - random.nextInt(7), random.nextInt(8) - random.nextInt(8));
            if (!world.isAir(mutable) || (block = world.getBlockState(mutable.up()).getBlock()) != Blocks.NETHERRACK && block != Blocks.NETHER_WART_BLOCK) continue;
            int j = MathHelper.nextInt(random, 1, 8);
            if (random.nextInt(6) == 0) {
                j *= 2;
            }
            if (random.nextInt(5) == 0) {
                j = 1;
            }
            int k = 17;
            int l = 25;
            WeepingVinesFeature.generateVines(world, random, mutable, j, 17, 25);
        }
    }

    public static void generateVines(IWorld world, Random random, BlockPos.Mutable pos, int length, int minAge, int maxAge) {
        for (int i = 0; i <= length; ++i) {
            if (world.isAir(pos)) {
                if (i == length) {
                    world.setBlockState(pos, (BlockState)Blocks.WEEPING_VINES.getDefaultState().with(WeepingVinesBlock.AGE, MathHelper.nextInt(random, minAge, maxAge)), 2);
                } else {
                    world.setBlockState(pos, Blocks.WEEPING_VINES_PLANT.getDefaultState(), 2);
                }
            } else if (world.getBlockState(pos.up()).getBlock() == Blocks.WEEPING_VINES_PLANT) {
                world.setBlockState(pos.up(), (BlockState)Blocks.WEEPING_VINES.getDefaultState().with(WeepingVinesBlock.AGE, MathHelper.nextInt(random, minAge, maxAge)), 2);
                break;
            }
            pos.setOffset(Direction.DOWN);
        }
    }
}

