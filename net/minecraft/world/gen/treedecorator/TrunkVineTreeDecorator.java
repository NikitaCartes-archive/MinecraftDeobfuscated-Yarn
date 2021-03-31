/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class TrunkVineTreeDecorator
extends TreeDecorator {
    public static final Codec<TrunkVineTreeDecorator> CODEC = Codec.unit(() -> INSTANCE);
    public static final TrunkVineTreeDecorator INSTANCE = new TrunkVineTreeDecorator();

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.TRUNK_VINE;
    }

    @Override
    public void generate(TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, List<BlockPos> leavesPositions, List<BlockPos> list) {
        leavesPositions.forEach(blockPos -> {
            BlockPos blockPos2;
            if (random.nextInt(3) > 0 && Feature.isAir(testableWorld, blockPos2 = blockPos.west())) {
                TrunkVineTreeDecorator.placeVine(biConsumer, blockPos2, VineBlock.EAST);
            }
            if (random.nextInt(3) > 0 && Feature.isAir(testableWorld, blockPos2 = blockPos.east())) {
                TrunkVineTreeDecorator.placeVine(biConsumer, blockPos2, VineBlock.WEST);
            }
            if (random.nextInt(3) > 0 && Feature.isAir(testableWorld, blockPos2 = blockPos.north())) {
                TrunkVineTreeDecorator.placeVine(biConsumer, blockPos2, VineBlock.SOUTH);
            }
            if (random.nextInt(3) > 0 && Feature.isAir(testableWorld, blockPos2 = blockPos.south())) {
                TrunkVineTreeDecorator.placeVine(biConsumer, blockPos2, VineBlock.NORTH);
            }
        });
    }
}

