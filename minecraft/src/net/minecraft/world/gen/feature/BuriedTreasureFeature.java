package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.BuriedTreasureGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BuriedTreasureFeature extends StructureFeature<BuriedTreasureFeatureConfig> {
	public BuriedTreasureFeature(Codec<BuriedTreasureFeatureConfig> codec) {
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
		BuriedTreasureFeatureConfig buriedTreasureFeatureConfig
	) {
		chunkRandom.setRegionSeed(l, i, j, 10387320);
		return chunkRandom.nextFloat() < buriedTreasureFeatureConfig.probability;
	}

	@Override
	public StructureFeature.StructureStartFactory<BuriedTreasureFeatureConfig> getStructureStartFactory() {
		return BuriedTreasureFeature.Start::new;
	}

	public static class Start extends StructureStart<BuriedTreasureFeatureConfig> {
		public Start(StructureFeature<BuriedTreasureFeatureConfig> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		public void init(
			ChunkGenerator chunkGenerator, StructureManager structureManager, int i, int j, Biome biome, BuriedTreasureFeatureConfig buriedTreasureFeatureConfig
		) {
			int k = i * 16;
			int l = j * 16;
			BlockPos blockPos = new BlockPos(k + 9, 90, l + 9);
			this.children.add(new BuriedTreasureGenerator.Piece(blockPos));
			this.setBoundingBoxFromChildren();
		}

		@Override
		public BlockPos getPos() {
			return new BlockPos((this.getChunkX() << 4) + 9, 0, (this.getChunkZ() << 4) + 9);
		}
	}
}
