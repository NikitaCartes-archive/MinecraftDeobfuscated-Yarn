/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class CocoaBeansTreeDecorator
extends TreeDecorator {
    public static final Codec<CocoaBeansTreeDecorator> CODEC = ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("probability")).xmap(CocoaBeansTreeDecorator::new, decorator -> Float.valueOf(decorator.probability)).codec();
    private final float probability;

    public CocoaBeansTreeDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.COCOA;
    }

    @Override
    public void generate(TreeDecorator.Generator generator) {
        AbstractRandom abstractRandom = generator.getRandom();
        if (abstractRandom.nextFloat() >= this.probability) {
            return;
        }
        ObjectArrayList<BlockPos> list = generator.getLogPositions();
        int i = ((BlockPos)list.get(0)).getY();
        list.stream().filter(pos -> pos.getY() - i <= 2).forEach(pos -> {
            for (Direction direction : Direction.Type.HORIZONTAL) {
                Direction direction2;
                BlockPos blockPos;
                if (!(abstractRandom.nextFloat() <= 0.25f) || !generator.isAir(blockPos = pos.add((direction2 = direction.getOpposite()).getOffsetX(), 0, direction2.getOffsetZ()))) continue;
                generator.replace(blockPos, (BlockState)((BlockState)Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, abstractRandom.nextInt(3))).with(CocoaBlock.FACING, direction));
            }
        });
    }
}

