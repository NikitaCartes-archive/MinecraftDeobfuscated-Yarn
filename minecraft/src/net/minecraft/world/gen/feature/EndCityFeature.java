package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class EndCityFeature extends StructureFeature<DefaultFeatureConfig> {
	private static final int Z_SEED_MULTIPLIER = 10387313;

	public EndCityFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec, EndCityFeature::addPieces);
	}

	@Override
	protected boolean isUniformDistribution() {
		return false;
	}

	protected boolean shouldStartAt(
		ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkPos chunkPos, DefaultFeatureConfig defaultFeatureConfig, HeightLimitView heightLimitView
	) {
		return getGenerationHeight(chunkPos, chunkGenerator, heightLimitView) >= 60;
	}

	private static int getGenerationHeight(ChunkPos pos, ChunkGenerator chunkGenerator, HeightLimitView world) {
		Random random = new Random((long)(pos.x + pos.z * 10387313));
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

		int k = pos.getOffsetX(7);
		int l = pos.getOffsetZ(7);
		int m = chunkGenerator.getHeightInGround(k, l, Heightmap.Type.WORLD_SURFACE_WG, world);
		int n = chunkGenerator.getHeightInGround(k, l + j, Heightmap.Type.WORLD_SURFACE_WG, world);
		int o = chunkGenerator.getHeightInGround(k + i, l, Heightmap.Type.WORLD_SURFACE_WG, world);
		int p = chunkGenerator.getHeightInGround(k + i, l + j, Heightmap.Type.WORLD_SURFACE_WG, world);
		return Math.min(Math.min(m, n), Math.min(o, p));
	}

	private static void addPieces(StructurePiecesCollector collector, DefaultFeatureConfig config, StructurePiecesGenerator.Context context) {
		BlockRotation blockRotation = BlockRotation.random(context.random());
		int i = getGenerationHeight(context.chunkPos(), context.chunkGenerator(), context.world());
		if (i >= 60) {
			BlockPos blockPos = context.chunkPos().getCenterAtY(i);
			if (context.biomeLimit()
				.test(
					context.chunkGenerator()
						.getBiomeForNoiseGen(BiomeCoords.fromBlock(blockPos.getX()), BiomeCoords.fromBlock(blockPos.getY()), BiomeCoords.fromBlock(blockPos.getZ()))
				)) {
				List<StructurePiece> list = Lists.<StructurePiece>newArrayList();
				EndCityGenerator.addPieces(context.structureManager(), blockPos, blockRotation, list, context.random());
				list.forEach(collector::addPiece);
			}
		}
	}
}
