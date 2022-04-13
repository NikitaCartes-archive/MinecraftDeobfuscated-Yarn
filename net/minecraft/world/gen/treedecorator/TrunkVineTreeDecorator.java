/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
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
    public void generate(TreeDecorator.Generator generator) {
        AbstractRandom abstractRandom = generator.getRandom();
        generator.getLogPositions().forEach(pos -> {
            BlockPos blockPos;
            if (abstractRandom.nextInt(3) > 0 && generator.isAir(blockPos = pos.west())) {
                generator.replaceWithVine(blockPos, VineBlock.EAST);
            }
            if (abstractRandom.nextInt(3) > 0 && generator.isAir(blockPos = pos.east())) {
                generator.replaceWithVine(blockPos, VineBlock.WEST);
            }
            if (abstractRandom.nextInt(3) > 0 && generator.isAir(blockPos = pos.north())) {
                generator.replaceWithVine(blockPos, VineBlock.SOUTH);
            }
            if (abstractRandom.nextInt(3) > 0 && generator.isAir(blockPos = pos.south())) {
                generator.replaceWithVine(blockPos, VineBlock.NORTH);
            }
        });
    }
}

