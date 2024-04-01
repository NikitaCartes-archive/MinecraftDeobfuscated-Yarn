package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_9586;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class PotatoBudsFeature extends Feature<TwistingVinesFeatureConfig> {
	public PotatoBudsFeature(Codec<TwistingVinesFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<TwistingVinesFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		if (isNotAirOrCorruptedPeelgrass(structureWorldAccess, blockPos)) {
			return false;
		} else {
			Random random = context.getRandom();
			TwistingVinesFeatureConfig twistingVinesFeatureConfig = context.getConfig();
			int i = twistingVinesFeatureConfig.spreadWidth();
			int j = twistingVinesFeatureConfig.spreadHeight();
			int k = twistingVinesFeatureConfig.maxHeight();
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int l = 0; l < i * i; l++) {
				mutable.set(blockPos).move(MathHelper.nextInt(random, -i, i), MathHelper.nextInt(random, -j, j), MathHelper.nextInt(random, -i, i));
				if (tryFindSurfacePos(structureWorldAccess, mutable) && !isNotAirOrCorruptedPeelgrass(structureWorldAccess, mutable)) {
					int m = MathHelper.nextInt(random, 1, k);
					if (random.nextInt(6) == 0) {
						m *= 2;
					}

					if (random.nextInt(5) == 0) {
						m = 1;
					}

					method_59258(structureWorldAccess, random, mutable, m);
				}
			}

			return true;
		}
	}

	private static boolean tryFindSurfacePos(WorldAccess world, BlockPos.Mutable pos) {
		do {
			pos.move(0, -1, 0);
			if (world.isOutOfHeightLimit(pos)) {
				return false;
			}
		} while (world.getBlockState(pos).isAir());

		pos.move(0, 1, 0);
		return true;
	}

	public static void method_59258(WorldAccess world, Random random, BlockPos.Mutable pos, int i) {
		world.setBlockState(pos, Blocks.POTATO_BUD.getDefaultState(), Block.NOTIFY_ALL);
		class_9586.method_59229(Blocks.POTATO_BUD, world, pos, Direction.UP, i, false);
	}

	private static boolean isNotAirOrCorruptedPeelgrass(WorldAccess world, BlockPos pos) {
		if (!world.isAir(pos)) {
			return true;
		} else {
			BlockState blockState = world.getBlockState(pos.down());
			return !blockState.isOf(Blocks.CORRUPTED_PEELGRASS_BLOCK);
		}
	}
}
