/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.PlacementModifier;
import net.minecraft.world.gen.decorator.PlacementModifierType;

@Deprecated
public class CountMultilayerPlacementModifier
extends PlacementModifier {
    public static final Codec<CountMultilayerPlacementModifier> MODIFIER_CODEC = ((MapCodec)IntProvider.createValidatingCodec(0, 256).fieldOf("count")).xmap(CountMultilayerPlacementModifier::new, countMultilayerPlacementModifier -> countMultilayerPlacementModifier.count).codec();
    private final IntProvider count;

    private CountMultilayerPlacementModifier(IntProvider count) {
        this.count = count;
    }

    public static CountMultilayerPlacementModifier of(IntProvider count) {
        return new CountMultilayerPlacementModifier(count);
    }

    public static CountMultilayerPlacementModifier of(int count) {
        return CountMultilayerPlacementModifier.of(ConstantIntProvider.create(count));
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext context, Random random, BlockPos pos) {
        boolean bl;
        Stream.Builder<BlockPos> builder = Stream.builder();
        int i = 0;
        do {
            bl = false;
            for (int j = 0; j < this.count.get(random); ++j) {
                int l;
                int m;
                int k = random.nextInt(16) + pos.getX();
                int n = CountMultilayerPlacementModifier.findPos(context, k, m = context.getTopY(Heightmap.Type.MOTION_BLOCKING, k, l = random.nextInt(16) + pos.getZ()), l, i);
                if (n == Integer.MAX_VALUE) continue;
                builder.add(new BlockPos(k, n, l));
                bl = true;
            }
            ++i;
        } while (bl);
        return builder.build();
    }

    @Override
    public PlacementModifierType<?> getType() {
        return PlacementModifierType.COUNT_ON_EVERY_LAYER;
    }

    private static int findPos(DecoratorContext context, int x, int y, int z, int targetY) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);
        int i = 0;
        BlockState blockState = context.getBlockState(mutable);
        for (int j = y; j >= context.getBottomY() + 1; --j) {
            mutable.setY(j - 1);
            BlockState blockState2 = context.getBlockState(mutable);
            if (!CountMultilayerPlacementModifier.blocksSpawn(blockState2) && CountMultilayerPlacementModifier.blocksSpawn(blockState) && !blockState2.isOf(Blocks.BEDROCK)) {
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

