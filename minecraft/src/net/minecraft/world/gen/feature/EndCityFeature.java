package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class EndCityFeature extends StructureFeature<DefaultFeatureConfig> {
	private static final int Z_SEED_MULTIPLIER = 10387313;

	public EndCityFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec, EndCityFeature::addPieces);
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

	private static Optional<StructurePiecesGenerator<DefaultFeatureConfig>> addPieces(StructureGeneratorFactory.Context<DefaultFeatureConfig> context) {
		int i = getGenerationHeight(context.chunkPos(), context.chunkGenerator(), context.world());
		if (i < 60) {
			return Optional.empty();
		} else {
			BlockPos blockPos = context.chunkPos().getCenterAtY(i);
			return !context.validBiome()
					.test(
						context.chunkGenerator()
							.getBiomeForNoiseGen(BiomeCoords.fromBlock(blockPos.getX()), BiomeCoords.fromBlock(blockPos.getY()), BiomeCoords.fromBlock(blockPos.getZ()))
					)
				? Optional.empty()
				: Optional.of((StructurePiecesGenerator<>)(structurePiecesCollector, contextx) -> {
					BlockRotation blockRotation = BlockRotation.random(contextx.random());
					List<StructurePiece> list = Lists.<StructurePiece>newArrayList();
					EndCityGenerator.addPieces(contextx.structureManager(), blockPos, blockRotation, list, contextx.random());
					list.forEach(structurePiecesCollector::addPiece);
				});
		}
	}
}
