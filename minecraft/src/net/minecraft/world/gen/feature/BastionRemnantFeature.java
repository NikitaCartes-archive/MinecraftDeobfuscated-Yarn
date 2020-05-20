package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.VillageStructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BastionRemnantFeature extends StructureFeature<BastionRemnantFeatureConfig> {
	public BastionRemnantFeature(Codec<BastionRemnantFeatureConfig> codec) {
		super(codec);
	}

	protected boolean shouldStartAt(
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		long l,
		ChunkRandom chunkRandom,
		int i,
		int j,
		Biome biome,
		ChunkPos chunkPos,
		BastionRemnantFeatureConfig bastionRemnantFeatureConfig
	) {
		return chunkRandom.nextInt(6) >= 2;
	}

	@Override
	public StructureFeature.StructureStartFactory<BastionRemnantFeatureConfig> getStructureStartFactory() {
		return BastionRemnantFeature.Start::new;
	}

	public static class Start extends VillageStructureStart<BastionRemnantFeatureConfig> {
		public Start(StructureFeature<BastionRemnantFeatureConfig> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		public void init(
			ChunkGenerator chunkGenerator, StructureManager structureManager, int i, int j, Biome biome, BastionRemnantFeatureConfig bastionRemnantFeatureConfig
		) {
			BlockPos blockPos = new BlockPos(i * 16, 33, j * 16);
			BastionRemnantGenerator.addPieces(chunkGenerator, structureManager, blockPos, this.children, this.random, bastionRemnantFeatureConfig);
			this.setBoundingBoxFromChildren();
		}
	}
}
