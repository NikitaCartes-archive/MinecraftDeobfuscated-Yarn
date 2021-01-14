/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
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
    public void generate(StructureWorldAccess world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> placedStates, BlockBox box) {
        logPositions.forEach(pos -> {
            BlockPos blockPos;
            if (random.nextInt(3) > 0 && Feature.isAir(world, blockPos = pos.west())) {
                this.placeVine(world, blockPos, VineBlock.EAST, placedStates, box);
            }
            if (random.nextInt(3) > 0 && Feature.isAir(world, blockPos = pos.east())) {
                this.placeVine(world, blockPos, VineBlock.WEST, placedStates, box);
            }
            if (random.nextInt(3) > 0 && Feature.isAir(world, blockPos = pos.north())) {
                this.placeVine(world, blockPos, VineBlock.SOUTH, placedStates, box);
            }
            if (random.nextInt(3) > 0 && Feature.isAir(world, blockPos = pos.south())) {
                this.placeVine(world, blockPos, VineBlock.NORTH, placedStates, box);
            }
        });
    }
}

