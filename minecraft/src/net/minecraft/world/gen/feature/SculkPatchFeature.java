package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.class_7124;
import net.minecraft.class_7128;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SculkPatchFeature extends Feature<SculkPatchFeatureConfig> {
	public SculkPatchFeature(Codec<SculkPatchFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<SculkPatchFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		if (!this.method_41571(structureWorldAccess, blockPos)) {
			return false;
		} else {
			SculkPatchFeatureConfig sculkPatchFeatureConfig = context.getConfig();
			Random random = context.getRandom();
			class_7128 lv = class_7128.method_41485();
			int i = sculkPatchFeatureConfig.spreadRounds() + sculkPatchFeatureConfig.growthRounds();

			for (int j = 0; j < i; j++) {
				for (int k = 0; k < sculkPatchFeatureConfig.chargeCount(); k++) {
					lv.method_41482(blockPos, sculkPatchFeatureConfig.amountPerCharge());
				}

				boolean bl = j < sculkPatchFeatureConfig.spreadRounds();

				for (int l = 0; l < sculkPatchFeatureConfig.spreadAttempts(); l++) {
					lv.method_41479(structureWorldAccess, blockPos, random, bl);
				}

				lv.method_41494();
			}

			BlockPos blockPos2 = blockPos.down();
			if (random.nextFloat() <= sculkPatchFeatureConfig.catalystChance()
				&& structureWorldAccess.getBlockState(blockPos2).isFullCube(structureWorldAccess, blockPos2)) {
				structureWorldAccess.setBlockState(blockPos, Blocks.SCULK_CATALYST.getDefaultState(), Block.NOTIFY_ALL);
			}

			return true;
		}
	}

	private boolean method_41571(WorldAccess worldAccess, BlockPos blockPos) {
		BlockState blockState = worldAccess.getBlockState(blockPos);
		if (blockState.getBlock() instanceof class_7124) {
			return true;
		} else {
			return !blockState.isAir() && (!blockState.isOf(Blocks.WATER) || !blockState.getFluidState().isStill())
				? false
				: Direction.stream().map(blockPos::offset).anyMatch(blockPosx -> worldAccess.getBlockState(blockPosx).isFullCube(worldAccess, blockPosx));
		}
	}
}
