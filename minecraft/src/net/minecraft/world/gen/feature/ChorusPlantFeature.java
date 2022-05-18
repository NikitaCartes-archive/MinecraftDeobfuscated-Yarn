package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChorusFlowerBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ChorusPlantFeature extends Feature<DefaultFeatureConfig> {
	public ChorusPlantFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		Random random = context.getRandom();
		if (structureWorldAccess.isAir(blockPos) && structureWorldAccess.getBlockState(blockPos.down()).isOf(Blocks.END_STONE)) {
			ChorusFlowerBlock.generate(structureWorldAccess, blockPos, random, 8);
			return true;
		} else {
			return false;
		}
	}
}
