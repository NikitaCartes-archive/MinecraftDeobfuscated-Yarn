package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class EmeraldOreFeature extends Feature<EmeraldOreFeatureConfig> {
	public EmeraldOreFeature(Codec<EmeraldOreFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, EmeraldOreFeatureConfig emeraldOreFeatureConfig
	) {
		if (structureWorldAccess.getBlockState(blockPos).isOf(emeraldOreFeatureConfig.target.getBlock())) {
			structureWorldAccess.setBlockState(blockPos, emeraldOreFeatureConfig.state, 2);
		}

		return true;
	}
}
