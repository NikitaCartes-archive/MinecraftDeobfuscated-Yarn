package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class FreezeTopLayerFeature extends Feature<DefaultFeatureConfig> {
	public FreezeTopLayerFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	public boolean method_13978(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		BlockPos.Mutable mutable2 = new BlockPos.Mutable();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int k = blockPos.getX() + i;
				int l = blockPos.getZ() + j;
				int m = structureWorldAccess.getTopY(Heightmap.Type.field_13197, k, l);
				mutable.set(k, m, l);
				mutable2.set(mutable).move(Direction.field_11033, 1);
				Biome biome = structureWorldAccess.getBiome(mutable);
				if (biome.canSetIce(structureWorldAccess, mutable2, false)) {
					structureWorldAccess.setBlockState(mutable2, Blocks.field_10295.getDefaultState(), 2);
				}

				if (biome.canSetSnow(structureWorldAccess, mutable)) {
					structureWorldAccess.setBlockState(mutable, Blocks.field_10477.getDefaultState(), 2);
					BlockState blockState = structureWorldAccess.getBlockState(mutable2);
					if (blockState.contains(SnowyBlock.SNOWY)) {
						structureWorldAccess.setBlockState(mutable2, blockState.with(SnowyBlock.SNOWY, Boolean.valueOf(true)), 2);
					}
				}
			}
		}

		return true;
	}
}
