package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class FreezeTopLayerFeature extends Feature<DefaultFeatureConfig> {
	public FreezeTopLayerFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		DefaultFeatureConfig defaultFeatureConfig
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		BlockPos.Mutable mutable2 = new BlockPos.Mutable();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int k = blockPos.getX() + i;
				int l = blockPos.getZ() + j;
				int m = serverWorldAccess.getTopY(Heightmap.Type.MOTION_BLOCKING, k, l);
				mutable.set(k, m, l);
				mutable2.set(mutable).move(Direction.DOWN, 1);
				Biome biome = serverWorldAccess.getBiome(mutable);
				if (biome.canSetIce(serverWorldAccess, mutable2, false)) {
					serverWorldAccess.setBlockState(mutable2, Blocks.ICE.getDefaultState(), 2);
				}

				if (biome.canSetSnow(serverWorldAccess, mutable)) {
					serverWorldAccess.setBlockState(mutable, Blocks.SNOW.getDefaultState(), 2);
					BlockState blockState = serverWorldAccess.getBlockState(mutable2);
					if (blockState.contains(SnowyBlock.SNOWY)) {
						serverWorldAccess.setBlockState(mutable2, blockState.with(SnowyBlock.SNOWY, Boolean.valueOf(true)), 2);
					}
				}
			}
		}

		return true;
	}
}
