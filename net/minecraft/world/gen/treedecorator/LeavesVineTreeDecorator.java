/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
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
    public void generate(TreeDecorator.Generator generator) {
        Random random = generator.getRandom();
        generator.getLeavesPositions().forEach(pos -> {
            BlockPos blockPos;
            if (random.nextFloat() < this.probability && generator.isAir(blockPos = pos.west())) {
                LeavesVineTreeDecorator.placeVines(blockPos, VineBlock.EAST, generator);
            }
            if (random.nextFloat() < this.probability && generator.isAir(blockPos = pos.east())) {
                LeavesVineTreeDecorator.placeVines(blockPos, VineBlock.WEST, generator);
            }
            if (random.nextFloat() < this.probability && generator.isAir(blockPos = pos.north())) {
                LeavesVineTreeDecorator.placeVines(blockPos, VineBlock.SOUTH, generator);
            }
            if (random.nextFloat() < this.probability && generator.isAir(blockPos = pos.south())) {
                LeavesVineTreeDecorator.placeVines(blockPos, VineBlock.NORTH, generator);
            }
        });
    }

    /**
     * Places a vine at a given position and then up to 4 more vines going downwards.
     */
    private static void placeVines(BlockPos pos, BooleanProperty faceProperty, TreeDecorator.Generator generator) {
        generator.replaceWithVine(pos, faceProperty);
        pos = pos.down();
        for (int i = 4; generator.isAir(pos) && i > 0; --i) {
            generator.replaceWithVine(pos, faceProperty);
            pos = pos.down();
        }
    }
}

