package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.FeaturePlacementContext;

@Deprecated
public class CountMultilayerPlacementModifier extends PlacementModifier {
	public static final Codec<CountMultilayerPlacementModifier> MODIFIER_CODEC = IntProvider.createValidatingCodec(0, 256)
		.fieldOf("count")
		.<CountMultilayerPlacementModifier>xmap(CountMultilayerPlacementModifier::new, countMultilayerPlacementModifier -> countMultilayerPlacementModifier.count)
		.codec();
	private final IntProvider count;

	private CountMultilayerPlacementModifier(IntProvider count) {
		this.count = count;
	}

	public static CountMultilayerPlacementModifier of(IntProvider count) {
		return new CountMultilayerPlacementModifier(count);
	}

	public static CountMultilayerPlacementModifier of(int count) {
		return of(ConstantIntProvider.create(count));
	}

	@Override
	public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
		Builder<BlockPos> builder = Stream.builder();
		int i = 0;

		boolean bl;
		do {
			bl = false;

			for (int j = 0; j < this.count.get(random); j++) {
				int k = random.nextInt(16) + pos.getX();
				int l = random.nextInt(16) + pos.getZ();
				int m = context.getTopY(Heightmap.Type.MOTION_BLOCKING, k, l);
				int n = findPos(context, k, m, l, i);
				if (n != Integer.MAX_VALUE) {
					builder.add(new BlockPos(k, n, l));
					bl = true;
				}
			}

			i++;
		} while (bl);

		return builder.build();
	}

	@Override
	public PlacementModifierType<?> getType() {
		return PlacementModifierType.COUNT_ON_EVERY_LAYER;
	}

	private static int findPos(FeaturePlacementContext context, int x, int y, int z, int targetY) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);
		int i = 0;
		BlockState blockState = context.getBlockState(mutable);

		for (int j = y; j >= context.getBottomY() + 1; j--) {
			mutable.setY(j - 1);
			BlockState blockState2 = context.getBlockState(mutable);
			if (!blocksSpawn(blockState2) && blocksSpawn(blockState) && !blockState2.isOf(Blocks.BEDROCK)) {
				if (i == targetY) {
					return mutable.getY() + 1;
				}

				i++;
			}

			blockState = blockState2;
		}

		return Integer.MAX_VALUE;
	}

	private static boolean blocksSpawn(BlockState state) {
		return state.isAir() || state.isOf(Blocks.WATER) || state.isOf(Blocks.LAVA);
	}
}
