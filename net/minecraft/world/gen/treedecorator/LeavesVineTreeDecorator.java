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
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class LeavesVineTreeDecorator
extends TreeDecorator {
    public static final Codec<LeavesVineTreeDecorator> CODEC = Codec.unit(() -> INSTANCE);
    public static final LeavesVineTreeDecorator INSTANCE = new LeavesVineTreeDecorator();

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.LEAVE_VINE;
    }

    @Override
    public void generate(TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, List<BlockPos> leavesPositions, List<BlockPos> list) {
        list.forEach(blockPos -> {
            BlockPos blockPos2;
            if (random.nextInt(4) == 0 && Feature.isAir(testableWorld, blockPos2 = blockPos.west())) {
                LeavesVineTreeDecorator.placeVines(testableWorld, blockPos2, VineBlock.EAST, biConsumer);
            }
            if (random.nextInt(4) == 0 && Feature.isAir(testableWorld, blockPos2 = blockPos.east())) {
                LeavesVineTreeDecorator.placeVines(testableWorld, blockPos2, VineBlock.WEST, biConsumer);
            }
            if (random.nextInt(4) == 0 && Feature.isAir(testableWorld, blockPos2 = blockPos.north())) {
                LeavesVineTreeDecorator.placeVines(testableWorld, blockPos2, VineBlock.SOUTH, biConsumer);
            }
            if (random.nextInt(4) == 0 && Feature.isAir(testableWorld, blockPos2 = blockPos.south())) {
                LeavesVineTreeDecorator.placeVines(testableWorld, blockPos2, VineBlock.NORTH, biConsumer);
            }
        });
    }

    /**
     * Places a vine at a given position and then up to 4 more vines going downwards.
     */
    private static void placeVines(TestableWorld testableWorld, BlockPos blockPos, BooleanProperty booleanProperty, BiConsumer<BlockPos, BlockState> biConsumer) {
        LeavesVineTreeDecorator.placeVine(biConsumer, blockPos, booleanProperty);
        blockPos = blockPos.down();
        for (int i = 4; Feature.isAir(testableWorld, blockPos) && i > 0; --i) {
            LeavesVineTreeDecorator.placeVine(biConsumer, blockPos, booleanProperty);
            blockPos = blockPos.down();
        }
    }
}

