/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.loot.OneTwentyLootTables;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.apache.commons.lang3.mutable.MutableInt;

public class DesertWellFeature
extends Feature<DefaultFeatureConfig> {
    private static final BlockStatePredicate CAN_GENERATE = BlockStatePredicate.forBlock(Blocks.SAND);
    private final BlockState slab = Blocks.SANDSTONE_SLAB.getDefaultState();
    private final BlockState wall = Blocks.SANDSTONE.getDefaultState();
    private final BlockState fluidInside = Blocks.WATER.getDefaultState();

    public DesertWellFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        int i;
        int j;
        int i2;
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        blockPos = blockPos.up();
        while (structureWorldAccess.isAir(blockPos) && blockPos.getY() > structureWorldAccess.getBottomY() + 2) {
            blockPos = blockPos.down();
        }
        if (!CAN_GENERATE.test(structureWorldAccess.getBlockState(blockPos))) {
            return false;
        }
        for (i2 = -2; i2 <= 2; ++i2) {
            for (j = -2; j <= 2; ++j) {
                if (!structureWorldAccess.isAir(blockPos.add(i2, -1, j)) || !structureWorldAccess.isAir(blockPos.add(i2, -2, j))) continue;
                return false;
            }
        }
        for (i2 = -1; i2 <= 0; ++i2) {
            for (j = -2; j <= 2; ++j) {
                for (int k = -2; k <= 2; ++k) {
                    structureWorldAccess.setBlockState(blockPos.add(j, i2, k), this.wall, Block.NOTIFY_LISTENERS);
                }
            }
        }
        if (structureWorldAccess.getEnabledFeatures().contains(FeatureFlags.UPDATE_1_20)) {
            DesertWellFeature.generateSuspiciousSand(structureWorldAccess, blockPos, context.getRandom());
        }
        structureWorldAccess.setBlockState(blockPos, this.fluidInside, Block.NOTIFY_LISTENERS);
        for (Direction direction : Direction.Type.HORIZONTAL) {
            structureWorldAccess.setBlockState(blockPos.offset(direction), this.fluidInside, Block.NOTIFY_LISTENERS);
        }
        for (i = -2; i <= 2; ++i) {
            for (int j2 = -2; j2 <= 2; ++j2) {
                if (i != -2 && i != 2 && j2 != -2 && j2 != 2) continue;
                structureWorldAccess.setBlockState(blockPos.add(i, 1, j2), this.wall, Block.NOTIFY_LISTENERS);
            }
        }
        structureWorldAccess.setBlockState(blockPos.add(2, 1, 0), this.slab, Block.NOTIFY_LISTENERS);
        structureWorldAccess.setBlockState(blockPos.add(-2, 1, 0), this.slab, Block.NOTIFY_LISTENERS);
        structureWorldAccess.setBlockState(blockPos.add(0, 1, 2), this.slab, Block.NOTIFY_LISTENERS);
        structureWorldAccess.setBlockState(blockPos.add(0, 1, -2), this.slab, Block.NOTIFY_LISTENERS);
        for (i = -1; i <= 1; ++i) {
            for (int j3 = -1; j3 <= 1; ++j3) {
                if (i == 0 && j3 == 0) {
                    structureWorldAccess.setBlockState(blockPos.add(i, 4, j3), this.wall, Block.NOTIFY_LISTENERS);
                    continue;
                }
                structureWorldAccess.setBlockState(blockPos.add(i, 4, j3), this.slab, Block.NOTIFY_LISTENERS);
            }
        }
        for (i = 1; i <= 3; ++i) {
            structureWorldAccess.setBlockState(blockPos.add(-1, i, -1), this.wall, Block.NOTIFY_LISTENERS);
            structureWorldAccess.setBlockState(blockPos.add(-1, i, 1), this.wall, Block.NOTIFY_LISTENERS);
            structureWorldAccess.setBlockState(blockPos.add(1, i, -1), this.wall, Block.NOTIFY_LISTENERS);
            structureWorldAccess.setBlockState(blockPos.add(1, i, 1), this.wall, Block.NOTIFY_LISTENERS);
        }
        return true;
    }

    private static void generateSuspiciousSand(StructureWorldAccess world, BlockPos pos2, Random random) {
        BlockPos blockPos = pos2.add(0, -1, 0);
        ObjectArrayList objectArrayList = Util.make(new ObjectArrayList(), positions -> {
            positions.add(blockPos.east());
            positions.add(blockPos.south());
            positions.add(blockPos.west());
            positions.add(blockPos.north());
        });
        Util.shuffle(objectArrayList, random);
        MutableInt mutableInt = new MutableInt(random.nextBetweenExclusive(2, 4));
        Stream.concat(Stream.of(blockPos), objectArrayList.stream()).forEach(pos -> {
            if (mutableInt.getAndDecrement() > 0) {
                world.setBlockState((BlockPos)pos, Blocks.SUSPICIOUS_SAND.getDefaultState(), Block.NOTIFY_ALL);
                world.getBlockEntity((BlockPos)pos, BlockEntityType.SUSPICIOUS_SAND).ifPresent(blockEntity -> blockEntity.setLootTable(OneTwentyLootTables.DESERT_WELL_ARCHAEOLOGY, pos.asLong()));
            } else {
                world.setBlockState((BlockPos)pos, Blocks.SAND.getDefaultState(), Block.NOTIFY_ALL);
            }
        });
    }
}

