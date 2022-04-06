/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
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
    public void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, AbstractRandom random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, List<BlockPos> rootPositions) {
        logPositions.forEach(pos -> {
            BlockPos blockPos;
            if (random.nextInt(3) > 0 && Feature.isAir(world, blockPos = pos.west())) {
                TrunkVineTreeDecorator.placeVine(replacer, blockPos, VineBlock.EAST);
            }
            if (random.nextInt(3) > 0 && Feature.isAir(world, blockPos = pos.east())) {
                TrunkVineTreeDecorator.placeVine(replacer, blockPos, VineBlock.WEST);
            }
            if (random.nextInt(3) > 0 && Feature.isAir(world, blockPos = pos.north())) {
                TrunkVineTreeDecorator.placeVine(replacer, blockPos, VineBlock.SOUTH);
            }
            if (random.nextInt(3) > 0 && Feature.isAir(world, blockPos = pos.south())) {
                TrunkVineTreeDecorator.placeVine(replacer, blockPos, VineBlock.NORTH);
            }
        });
    }
}

