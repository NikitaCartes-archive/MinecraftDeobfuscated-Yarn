package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.fabricmc.yarn.constants.SetBlockStateFlags;
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
		if ((simpleBlockFeatureConfig.placeOn.isEmpty() || simpleBlockFeatureConfig.placeOn.contains(structureWorldAccess.getBlockState(blockPos.down())))
			&& (simpleBlockFeatureConfig.placeIn.isEmpty() || simpleBlockFeatureConfig.placeIn.contains(structureWorldAccess.getBlockState(blockPos)))
			&& (simpleBlockFeatureConfig.placeUnder.isEmpty() || simpleBlockFeatureConfig.placeUnder.contains(structureWorldAccess.getBlockState(blockPos.up())))) {
			BlockState blockState = simpleBlockFeatureConfig.toPlace.getBlockState(context.getRandom(), blockPos);
			if (blockState.canPlaceAt(structureWorldAccess, blockPos)) {
				if (blockState.getBlock() instanceof TallPlantBlock) {
					if (!structureWorldAccess.isAir(blockPos.up())) {
						return false;
					}

					TallPlantBlock tallPlantBlock = (TallPlantBlock)blockState.getBlock();
					tallPlantBlock.placeAt(structureWorldAccess, blockState, blockPos, 2);
				} else {
					structureWorldAccess.setBlockState(blockPos, blockState, SetBlockStateFlags.NOTIFY_LISTENERS);
				}

				return true;
			}
		}

		return false;
	}
}
