package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.BuriedTreasureGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
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
		ChunkPos chunkPos,
		Biome biome,
		ChunkPos chunkPos2,
		ProbabilityConfig probabilityConfig,
		HeightLimitView heightLimitView
	) {
		chunkRandom.setRegionSeed(l, chunkPos.x, chunkPos.z, 10387320);
		return chunkRandom.nextFloat() < probabilityConfig.probability;
	}

	@Override
	public StructureFeature.StructureStartFactory<ProbabilityConfig> getStructureStartFactory() {
		return BuriedTreasureFeature.Start::new;
	}

	public static class Start extends StructureStart<ProbabilityConfig> {
		public Start(StructureFeature<ProbabilityConfig> structureFeature, ChunkPos chunkPos, BlockBox blockBox, int i, long l) {
			super(structureFeature, chunkPos, blockBox, i, l);
		}

		public void init(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			ChunkPos chunkPos,
			Biome biome,
			ProbabilityConfig probabilityConfig,
			HeightLimitView heightLimitView
		) {
			BlockPos blockPos = new BlockPos(chunkPos.getOffsetX(9), 90, chunkPos.getOffsetZ(9));
			this.children.add(new BuriedTreasureGenerator.Piece(blockPos));
			this.setBoundingBoxFromChildren();
		}

		@Override
		public BlockPos getPos() {
			ChunkPos chunkPos = this.method_34000();
			return new BlockPos(chunkPos.getOffsetX(9), 0, chunkPos.getOffsetZ(9));
		}
	}
}
