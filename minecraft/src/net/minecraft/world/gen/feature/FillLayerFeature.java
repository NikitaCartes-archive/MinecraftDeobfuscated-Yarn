package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class FillLayerFeature extends Feature<FillLayerFeatureConfig> {
	public FillLayerFeature(Codec<FillLayerFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, FillLayerFeatureConfig fillLayerFeatureConfig
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int k = blockPos.getX() + i;
				int l = blockPos.getZ() + j;
				int m = fillLayerFeatureConfig.height;
				mutable.set(k, m, l);
				if (structureWorldAccess.getBlockState(mutable).isAir()) {
					structureWorldAccess.setBlockState(mutable, fillLayerFeatureConfig.state, 2);
				}
			}
		}

		return true;
	}
}
