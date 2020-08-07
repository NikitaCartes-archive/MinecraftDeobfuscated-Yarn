package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class UnderwaterDiskFeature extends DiskFeature {
	public UnderwaterDiskFeature(Codec<DiskFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean method_13005(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DiskFeatureConfig diskFeatureConfig
	) {
		return !structureWorldAccess.getFluidState(blockPos).isIn(FluidTags.field_15517)
			? false
			: super.method_13005(structureWorldAccess, chunkGenerator, random, blockPos, diskFeatureConfig);
	}
}
