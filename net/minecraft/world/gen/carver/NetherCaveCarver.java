/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.CaveCarver;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NetherCaveCarver
extends CaveCarver {
    public NetherCaveCarver(Codec<CarverConfig> codec) {
        super(codec);
        this.alwaysCarvableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK, Blocks.BASALT, Blocks.BLACKSTONE});
        this.carvableFluids = ImmutableSet.of(Fluids.LAVA, Fluids.WATER);
    }

    @Override
    protected int getMaxCaveCount() {
        return 10;
    }

    @Override
    protected float getTunnelSystemWidth(Random random) {
        return (random.nextFloat() * 2.0f + random.nextFloat()) * 2.0f;
    }

    @Override
    protected double getTunnelSystemHeightWidthRatio() {
        return 5.0;
    }

    @Override
    protected int getCaveY(CarverContext context, Random random) {
        return random.nextInt(context.getMaxY());
    }

    @Override
    protected boolean carveAtPoint(CarverContext context, CarverConfig config, Chunk chunk, Function<BlockPos, Biome> posToBiome, BitSet carvingMask, Random random, BlockPos.Mutable pos, BlockPos.Mutable downPos, int mainChunkX, MutableBoolean foundSurface) {
        if (this.canAlwaysCarveBlock(chunk.getBlockState(pos))) {
            BlockState blockState = pos.getY() <= context.getMinY() + 31 ? LAVA.getBlockState() : CAVE_AIR;
            chunk.setBlockState(pos, blockState, false);
            return true;
        }
        return false;
    }
}

