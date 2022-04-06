package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class IcePatchFeature extends DiskFeature {
	public IcePatchFeature(Codec<DiskFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DiskFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		ChunkGenerator chunkGenerator = context.getGenerator();
		AbstractRandom abstractRandom = context.getRandom();
		DiskFeatureConfig diskFeatureConfig = context.getConfig();
		BlockPos blockPos = context.getOrigin();

		while (structureWorldAccess.isAir(blockPos) && blockPos.getY() > structureWorldAccess.getBottomY() + 2) {
			blockPos = blockPos.down();
		}

		return !context.getWorld().getBlockState(blockPos).isIn(diskFeatureConfig.canOriginReplace())
			? false
			: super.generate(
				new FeatureContext<>(context.getFeature(), structureWorldAccess, context.getGenerator(), context.getRandom(), blockPos, context.getConfig())
			);
	}
}
