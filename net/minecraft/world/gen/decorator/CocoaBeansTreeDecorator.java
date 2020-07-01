/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.Feature;

public class CocoaBeansTreeDecorator
extends TreeDecorator {
    public static final Codec<CocoaBeansTreeDecorator> CODEC = ((MapCodec)Codec.FLOAT.fieldOf("probability")).xmap(CocoaBeansTreeDecorator::new, cocoaBeansTreeDecorator -> Float.valueOf(cocoaBeansTreeDecorator.field_21318)).codec();
    private final float field_21318;

    public CocoaBeansTreeDecorator(float f) {
        this.field_21318 = f;
    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.COCOA;
    }

    @Override
    public void generate(ServerWorldAccess serverWorldAccess, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box) {
        if (random.nextFloat() >= this.field_21318) {
            return;
        }
        int i = logPositions.get(0).getY();
        logPositions.stream().filter(blockPos -> blockPos.getY() - i <= 2).forEach(blockPos -> {
            for (Direction direction : Direction.Type.HORIZONTAL) {
                Direction direction2;
                BlockPos blockPos2;
                if (!(random.nextFloat() <= 0.25f) || !Feature.isAir(serverWorldAccess, blockPos2 = blockPos.add((direction2 = direction.getOpposite()).getOffsetX(), 0, direction2.getOffsetZ()))) continue;
                BlockState blockState = (BlockState)((BlockState)Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, random.nextInt(3))).with(CocoaBlock.FACING, direction);
                this.setBlockStateAndEncompassPosition(serverWorldAccess, blockPos2, blockState, set, box);
            }
        });
    }
}

