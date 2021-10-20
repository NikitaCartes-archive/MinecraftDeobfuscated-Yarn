package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.DripstoneHelper;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SmallDripstoneFeature extends Feature<SmallDripstoneFeatureConfig> {
	public SmallDripstoneFeature(Codec<SmallDripstoneFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<SmallDripstoneFeatureConfig> context) {
		WorldAccess worldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		Random random = context.getRandom();
		SmallDripstoneFeatureConfig smallDripstoneFeatureConfig = context.getConfig();
		Optional<Direction> optional = method_39175(worldAccess, blockPos, random);
		if (optional.isEmpty()) {
			return false;
		} else {
			BlockPos blockPos2 = blockPos.offset(((Direction)optional.get()).getOpposite());
			generateDripstoneBlocks(worldAccess, random, blockPos2, smallDripstoneFeatureConfig);
			int i = random.nextFloat() < smallDripstoneFeatureConfig.chanceOfTallerDripstone
					&& DripstoneHelper.canGenerate(worldAccess.getBlockState(blockPos.offset((Direction)optional.get())))
				? 2
				: 1;
			DripstoneHelper.generatePointedDripstone(worldAccess, blockPos, (Direction)optional.get(), i, false);
			return true;
		}
	}

	private static Optional<Direction> method_39175(WorldAccess worldAccess, BlockPos blockPos, Random random) {
		boolean bl = DripstoneHelper.canReplace(worldAccess.getBlockState(blockPos.up()));
		boolean bl2 = DripstoneHelper.canReplace(worldAccess.getBlockState(blockPos.down()));
		if (bl && bl2) {
			return Optional.of(random.nextBoolean() ? Direction.DOWN : Direction.UP);
		} else if (bl) {
			return Optional.of(Direction.DOWN);
		} else {
			return bl2 ? Optional.of(Direction.UP) : Optional.empty();
		}
	}

	private static void generateDripstoneBlocks(WorldAccess worldAccess, Random random, BlockPos pos, SmallDripstoneFeatureConfig smallDripstoneFeatureConfig) {
		DripstoneHelper.generateDripstoneBlock(worldAccess, pos);

		for (Direction direction : Direction.Type.HORIZONTAL) {
			if (!(random.nextFloat() > smallDripstoneFeatureConfig.field_35416)) {
				BlockPos blockPos = pos.offset(direction);
				DripstoneHelper.generateDripstoneBlock(worldAccess, blockPos);
				if (!(random.nextFloat() > smallDripstoneFeatureConfig.field_35417)) {
					BlockPos blockPos2 = blockPos.offset(Direction.random(random));
					DripstoneHelper.generateDripstoneBlock(worldAccess, blockPos2);
					if (!(random.nextFloat() > smallDripstoneFeatureConfig.field_35418)) {
						BlockPos blockPos3 = blockPos2.offset(Direction.random(random));
						DripstoneHelper.generateDripstoneBlock(worldAccess, blockPos3);
					}
				}
			}
		}
	}
}
