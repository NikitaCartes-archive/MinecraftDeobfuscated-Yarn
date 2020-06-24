/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.Feature;

public class TrunkVineTreeDecorator
extends TreeDecorator {
    public static final Codec<TrunkVineTreeDecorator> field_24964 = Codec.unit(() -> field_24965);
    public static final TrunkVineTreeDecorator field_24965 = new TrunkVineTreeDecorator();

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.TRUNK_VINE;
    }

    @Override
    public void generate(WorldAccess world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box) {
        logPositions.forEach(blockPos -> {
            BlockPos blockPos2;
            if (random.nextInt(3) > 0 && Feature.isAir(world, blockPos2 = blockPos.west())) {
                this.placeVine(world, blockPos2, VineBlock.EAST, set, box);
            }
            if (random.nextInt(3) > 0 && Feature.isAir(world, blockPos2 = blockPos.east())) {
                this.placeVine(world, blockPos2, VineBlock.WEST, set, box);
            }
            if (random.nextInt(3) > 0 && Feature.isAir(world, blockPos2 = blockPos.north())) {
                this.placeVine(world, blockPos2, VineBlock.SOUTH, set, box);
            }
            if (random.nextInt(3) > 0 && Feature.isAir(world, blockPos2 = blockPos.south())) {
                this.placeVine(world, blockPos2, VineBlock.NORTH, set, box);
            }
        });
    }
}

