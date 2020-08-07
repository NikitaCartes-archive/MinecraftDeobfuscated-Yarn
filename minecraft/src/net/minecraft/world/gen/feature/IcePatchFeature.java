package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class IcePatchFeature extends DiskFeature {
	public IcePatchFeature(Codec<DiskFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean method_13005(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DiskFeatureConfig diskFeatureConfig
	) {
		while (structureWorldAccess.isAir(blockPos) && blockPos.getY() > 2) {
			blockPos = blockPos.method_10074();
		}

		return !structureWorldAccess.getBlockState(blockPos).isOf(Blocks.field_10491)
			? false
			: super.method_13005(structureWorldAccess, chunkGenerator, random, blockPos, diskFeatureConfig);
	}
}
