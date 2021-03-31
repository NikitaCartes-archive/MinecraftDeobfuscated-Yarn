/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;

public class CountMultilayerDecorator
extends Decorator<CountConfig> {
    public CountMultilayerDecorator(Codec<CountConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, CountConfig countConfig, BlockPos blockPos) {
        boolean bl;
        ArrayList<BlockPos> list = Lists.newArrayList();
        int i = 0;
        do {
            bl = false;
            for (int j = 0; j < countConfig.getCount().get(random); ++j) {
                int l;
                int m;
                int k = random.nextInt(16) + blockPos.getX();
                int n = CountMultilayerDecorator.findPos(decoratorContext, k, m = decoratorContext.getTopY(Heightmap.Type.MOTION_BLOCKING, k, l = random.nextInt(16) + blockPos.getZ()), l, i);
                if (n == Integer.MAX_VALUE) continue;
                list.add(new BlockPos(k, n, l));
                bl = true;
            }
            ++i;
        } while (bl);
        return list.stream();
    }

    private static int findPos(DecoratorContext context, int x, int y, int z, int targetY) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);
        int i = 0;
        BlockState blockState = context.getBlockState(mutable);
        for (int j = y; j >= context.getBottomY() + 1; --j) {
            mutable.setY(j - 1);
            BlockState blockState2 = context.getBlockState(mutable);
            if (!CountMultilayerDecorator.blocksSpawn(blockState2) && CountMultilayerDecorator.blocksSpawn(blockState) && !blockState2.isOf(Blocks.BEDROCK)) {
                if (i == targetY) {
                    return mutable.getY() + 1;
                }
                ++i;
            }
            blockState = blockState2;
        }
        return Integer.MAX_VALUE;
    }

    private static boolean blocksSpawn(BlockState state) {
        return state.isAir() || state.isOf(Blocks.WATER) || state.isOf(Blocks.LAVA);
    }
}

