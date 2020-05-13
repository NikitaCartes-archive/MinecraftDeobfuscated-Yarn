/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class DesertWellFeature
extends Feature<DefaultFeatureConfig> {
    private static final BlockStatePredicate CAN_GENERATE = BlockStatePredicate.forBlock(Blocks.SAND);
    private final BlockState slab = Blocks.SANDSTONE_SLAB.getDefaultState();
    private final BlockState wall = Blocks.SANDSTONE.getDefaultState();
    private final BlockState fluidInside = Blocks.WATER.getDefaultState();

    public DesertWellFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        int i;
        int j;
        int i2;
        blockPos = blockPos.up();
        while (serverWorldAccess.isAir(blockPos) && blockPos.getY() > 2) {
            blockPos = blockPos.down();
        }
        if (!CAN_GENERATE.test(serverWorldAccess.getBlockState(blockPos))) {
            return false;
        }
        for (i2 = -2; i2 <= 2; ++i2) {
            for (j = -2; j <= 2; ++j) {
                if (!serverWorldAccess.isAir(blockPos.add(i2, -1, j)) || !serverWorldAccess.isAir(blockPos.add(i2, -2, j))) continue;
                return false;
            }
        }
        for (i2 = -1; i2 <= 0; ++i2) {
            for (j = -2; j <= 2; ++j) {
                for (int k = -2; k <= 2; ++k) {
                    serverWorldAccess.setBlockState(blockPos.add(j, i2, k), this.wall, 2);
                }
            }
        }
        serverWorldAccess.setBlockState(blockPos, this.fluidInside, 2);
        for (Direction direction : Direction.Type.HORIZONTAL) {
            serverWorldAccess.setBlockState(blockPos.offset(direction), this.fluidInside, 2);
        }
        for (i = -2; i <= 2; ++i) {
            for (int j2 = -2; j2 <= 2; ++j2) {
                if (i != -2 && i != 2 && j2 != -2 && j2 != 2) continue;
                serverWorldAccess.setBlockState(blockPos.add(i, 1, j2), this.wall, 2);
            }
        }
        serverWorldAccess.setBlockState(blockPos.add(2, 1, 0), this.slab, 2);
        serverWorldAccess.setBlockState(blockPos.add(-2, 1, 0), this.slab, 2);
        serverWorldAccess.setBlockState(blockPos.add(0, 1, 2), this.slab, 2);
        serverWorldAccess.setBlockState(blockPos.add(0, 1, -2), this.slab, 2);
        for (i = -1; i <= 1; ++i) {
            for (int j3 = -1; j3 <= 1; ++j3) {
                if (i == 0 && j3 == 0) {
                    serverWorldAccess.setBlockState(blockPos.add(i, 4, j3), this.wall, 2);
                    continue;
                }
                serverWorldAccess.setBlockState(blockPos.add(i, 4, j3), this.slab, 2);
            }
        }
        for (i = 1; i <= 3; ++i) {
            serverWorldAccess.setBlockState(blockPos.add(-1, i, -1), this.wall, 2);
            serverWorldAccess.setBlockState(blockPos.add(-1, i, 1), this.wall, 2);
            serverWorldAccess.setBlockState(blockPos.add(1, i, -1), this.wall, 2);
            serverWorldAccess.setBlockState(blockPos.add(1, i, 1), this.wall, 2);
        }
        return true;
    }
}

