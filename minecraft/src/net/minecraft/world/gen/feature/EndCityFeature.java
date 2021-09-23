package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import net.minecraft.class_6622;
import net.minecraft.class_6626;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.random.ChunkRandom;

public class EndCityFeature extends StructureFeature<DefaultFeatureConfig> {
	private static final int field_31502 = 10387313;

	public EndCityFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec, EndCityFeature::method_38674);
	}

	@Override
	protected boolean isUniformDistribution() {
		return false;
	}

	protected boolean shouldStartAt(
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		long l,
		ChunkRandom chunkRandom,
		ChunkPos chunkPos,
		ChunkPos chunkPos2,
		DefaultFeatureConfig defaultFeatureConfig,
		HeightLimitView heightLimitView
	) {
		return getGenerationHeight(chunkPos, chunkGenerator, heightLimitView) >= 60;
	}

	private static int getGenerationHeight(ChunkPos chunkPos, ChunkGenerator chunkGenerator, HeightLimitView heightLimitView) {
		Random random = new Random((long)(chunkPos.x + chunkPos.z * 10387313));
		BlockRotation blockRotation = BlockRotation.random(random);
		int i = 5;
		int j = 5;
		if (blockRotation == BlockRotation.CLOCKWISE_90) {
			i = -5;
		} else if (blockRotation == BlockRotation.CLOCKWISE_180) {
			i = -5;
			j = -5;
		} else if (blockRotation == BlockRotation.COUNTERCLOCKWISE_90) {
			j = -5;
		}

		int k = chunkPos.getOffsetX(7);
		int l = chunkPos.getOffsetZ(7);
		int m = chunkGenerator.getHeightInGround(k, l, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
		int n = chunkGenerator.getHeightInGround(k, l + j, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
		int o = chunkGenerator.getHeightInGround(k + i, l, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
		int p = chunkGenerator.getHeightInGround(k + i, l + j, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
		return Math.min(Math.min(m, n), Math.min(o, p));
	}

	private static void method_38674(class_6626 arg, DefaultFeatureConfig defaultFeatureConfig, class_6622.class_6623 arg2) {
		BlockRotation blockRotation = BlockRotation.random(arg2.random());
		int i = getGenerationHeight(arg2.chunkPos(), arg2.chunkGenerator(), arg2.heightAccessor());
		if (i >= 60) {
			BlockPos blockPos = arg2.chunkPos().getCenterAtY(i);
			if (arg2.validBiome()
				.test(
					arg2.chunkGenerator()
						.getBiomeForNoiseGen(BiomeCoords.fromBlock(blockPos.getX()), BiomeCoords.fromBlock(blockPos.getY()), BiomeCoords.fromBlock(blockPos.getZ()))
				)) {
				List<StructurePiece> list = Lists.<StructurePiece>newArrayList();
				EndCityGenerator.addPieces(arg2.structureManager(), blockPos, blockRotation, list, arg2.random());
				list.forEach(arg::addPiece);
			}
		}
	}
}
