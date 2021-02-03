package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.BuriedTreasureGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BuriedTreasureFeature extends StructureFeature<ProbabilityConfig> {
	public BuriedTreasureFeature(Codec<ProbabilityConfig> codec) {
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
		ProbabilityConfig probabilityConfig,
		HeightLimitView heightLimitView
	) {
		chunkRandom.setRegionSeed(l, i, j, 10387320);
		return chunkRandom.nextFloat() < probabilityConfig.probability;
	}

	@Override
	public StructureFeature.StructureStartFactory<ProbabilityConfig> getStructureStartFactory() {
		return BuriedTreasureFeature.Start::new;
	}

	public static class Start extends StructureStart<ProbabilityConfig> {
		public Start(StructureFeature<ProbabilityConfig> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		public void init(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			int i,
			int j,
			Biome biome,
			ProbabilityConfig probabilityConfig,
			HeightLimitView heightLimitView
		) {
			BlockPos blockPos = new BlockPos(ChunkSectionPos.getOffsetPos(i, 9), 90, ChunkSectionPos.getOffsetPos(j, 9));
			this.children.add(new BuriedTreasureGenerator.Piece(blockPos));
			this.setBoundingBoxFromChildren();
		}

		@Override
		public BlockPos getPos() {
			return new BlockPos(ChunkSectionPos.getOffsetPos(this.getChunkX(), 9), 0, ChunkSectionPos.getOffsetPos(this.getChunkZ(), 9));
		}
	}
}
