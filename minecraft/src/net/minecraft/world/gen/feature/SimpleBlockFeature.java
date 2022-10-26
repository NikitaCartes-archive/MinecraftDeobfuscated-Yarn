package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SimpleBlockFeature extends Feature<SimpleBlockFeatureConfig> {
	public SimpleBlockFeature(Codec<SimpleBlockFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<SimpleBlockFeatureConfig> context) {
		SimpleBlockFeatureConfig simpleBlockFeatureConfig = context.getConfig();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		BlockState blockState = simpleBlockFeatureConfig.toPlace().get(context.getRandom(), blockPos);
		if (blockState.canPlaceAt(structureWorldAccess, blockPos)) {
			if (blockState.getBlock() instanceof TallPlantBlock) {
				if (!structureWorldAccess.isAir(blockPos.up())) {
					return false;
				}

				TallPlantBlock.placeAt(structureWorldAccess, blockState, blockPos, 2);
			} else {
				structureWorldAccess.setBlockState(blockPos, blockState, Block.NOTIFY_LISTENERS);
			}

			return true;
		} else {
			return false;
		}
	}
}
