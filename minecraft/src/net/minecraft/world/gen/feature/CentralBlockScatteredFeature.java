package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class CentralBlockScatteredFeature extends Feature<CentralBlockScatteredFeatureConfig> {
	public CentralBlockScatteredFeature(Codec<CentralBlockScatteredFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<CentralBlockScatteredFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		CentralBlockScatteredFeatureConfig centralBlockScatteredFeatureConfig = context.getConfig();
		Random random = context.getRandom();
		BlockPos blockPos = context.getOrigin();
		Predicate<BlockState> predicate = getTagPredicate(centralBlockScatteredFeatureConfig.canPlaceCentralBlockOn);
		BlockState blockState = structureWorldAccess.getBlockState(blockPos.down());
		if (predicate.test(blockState)) {
			structureWorldAccess.setBlockState(blockPos, centralBlockScatteredFeatureConfig.centralState.getBlockState(random, blockPos), Block.NOTIFY_LISTENERS);
			((ConfiguredFeature)centralBlockScatteredFeatureConfig.centralFeature.get()).generate(structureWorldAccess, context.getGenerator(), random, blockPos);
			float f = (float)centralBlockScatteredFeatureConfig.maxFeatureDistance / 3.0F;
			int i = centralBlockScatteredFeatureConfig.maxFeatureDistance * centralBlockScatteredFeatureConfig.maxFeatureDistance;
			ConfiguredFeature<?, ?> configuredFeature = (ConfiguredFeature<?, ?>)centralBlockScatteredFeatureConfig.scatteredFeature.get();
			int j = MathHelper.nextBetween(random, centralBlockScatteredFeatureConfig.featureCountMin, centralBlockScatteredFeatureConfig.featureCountMax);

			for (int k = 0; k < j; k++) {
				double d = random.nextGaussian();
				double e = random.nextGaussian();
				BlockPos blockPos2 = blockPos.add(d * (double)f, 0.0, e * (double)f);
				if (blockPos2.getSquaredDistance(blockPos, true) <= (double)i) {
					configuredFeature.generate(structureWorldAccess, context.getGenerator(), random, blockPos2);
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private static Predicate<BlockState> getTagPredicate(Identifier tagId) {
		Tag<Block> tag = BlockTags.getTagGroup().getTag(tagId);
		return tag == null ? state -> true : state -> state.isIn(tag);
	}
}
