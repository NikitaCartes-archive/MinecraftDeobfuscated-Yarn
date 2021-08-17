package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class EmeraldOreFeature extends Feature<EmeraldOreFeatureConfig> {
	public EmeraldOreFeature(Codec<EmeraldOreFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<EmeraldOreFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		EmeraldOreFeatureConfig emeraldOreFeatureConfig = context.getConfig();

		for (OreFeatureConfig.Target target : emeraldOreFeatureConfig.targets) {
			if (target.target.test(structureWorldAccess.getBlockState(blockPos), context.getRandom())) {
				structureWorldAccess.setBlockState(blockPos, target.state, Block.NOTIFY_LISTENERS);
				break;
			}
		}

		return true;
	}
}
