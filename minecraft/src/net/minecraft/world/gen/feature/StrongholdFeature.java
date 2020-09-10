package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class StrongholdFeature extends StructureFeature<DefaultFeatureConfig> {
	public StrongholdFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
		return StrongholdFeature.Start::new;
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
		DefaultFeatureConfig defaultFeatureConfig
	) {
		return chunkGenerator.isStrongholdStartingChunk(new ChunkPos(i, j));
	}

	public static class Start extends StructureStart<DefaultFeatureConfig> {
		private final long seed;

		public Start(StructureFeature<DefaultFeatureConfig> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
			this.seed = l;
		}

		public void init(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			int i,
			int j,
			Biome biome,
			DefaultFeatureConfig defaultFeatureConfig
		) {
			int k = 0;

			StrongholdGenerator.Start start;
			do {
				this.children.clear();
				this.boundingBox = BlockBox.empty();
				this.random.setCarverSeed(this.seed + (long)(k++), i, j);
				StrongholdGenerator.init();
				start = new StrongholdGenerator.Start(this.random, (i << 4) + 2, (j << 4) + 2);
				this.children.add(start);
				start.fillOpenings(start, this.children, this.random);
				List<StructurePiece> list = start.pieces;

				while (!list.isEmpty()) {
					int l = this.random.nextInt(list.size());
					StructurePiece structurePiece = (StructurePiece)list.remove(l);
					structurePiece.fillOpenings(start, this.children, this.random);
				}

				this.setBoundingBoxFromChildren();
				this.randomUpwardTranslation(chunkGenerator.getSeaLevel(), this.random, 10);
			} while (this.children.isEmpty() || start.portalRoom == null);
		}
	}
}
