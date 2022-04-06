/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class LeavesVineTreeDecorator
extends TreeDecorator {
    public static final Codec<LeavesVineTreeDecorator> CODEC = ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("probability")).xmap(LeavesVineTreeDecorator::new, treeDecorator -> Float.valueOf(treeDecorator.probability)).codec();
    private final float probability;

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.LEAVE_VINE;
    }

    public LeavesVineTreeDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    public void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, AbstractRandom random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, List<BlockPos> rootPositions) {
        leavesPositions.forEach(pos -> {
            BlockPos blockPos;
            if (random.nextFloat() < this.probability && Feature.isAir(world, blockPos = pos.west())) {
                LeavesVineTreeDecorator.placeVines(world, blockPos, VineBlock.EAST, replacer);
            }
            if (random.nextFloat() < this.probability && Feature.isAir(world, blockPos = pos.east())) {
                LeavesVineTreeDecorator.placeVines(world, blockPos, VineBlock.WEST, replacer);
            }
            if (random.nextFloat() < this.probability && Feature.isAir(world, blockPos = pos.north())) {
                LeavesVineTreeDecorator.placeVines(world, blockPos, VineBlock.SOUTH, replacer);
            }
            if (random.nextFloat() < this.probability && Feature.isAir(world, blockPos = pos.south())) {
                LeavesVineTreeDecorator.placeVines(world, blockPos, VineBlock.NORTH, replacer);
            }
        });
    }

    /**
     * Places a vine at a given position and then up to 4 more vines going downwards.
     */
    private static void placeVines(TestableWorld world, BlockPos pos, BooleanProperty facing, BiConsumer<BlockPos, BlockState> replacer) {
        LeavesVineTreeDecorator.placeVine(replacer, pos, facing);
        pos = pos.down();
        for (int i = 4; Feature.isAir(world, pos) && i > 0; --i) {
            LeavesVineTreeDecorator.placeVine(replacer, pos, facing);
            pos = pos.down();
        }
    }
}

