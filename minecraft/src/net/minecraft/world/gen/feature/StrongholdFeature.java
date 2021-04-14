package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.structure.MarginedStructureStart;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
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
		ChunkPos chunkPos,
		Biome biome,
		ChunkPos chunkPos2,
		DefaultFeatureConfig defaultFeatureConfig,
		HeightLimitView heightLimitView
	) {
		return chunkGenerator.isStrongholdStartingChunk(chunkPos);
	}

	public static class Start extends MarginedStructureStart<DefaultFeatureConfig> {
		private final long seed;

		public Start(StructureFeature<DefaultFeatureConfig> structureFeature, ChunkPos chunkPos, int i, long l) {
			super(structureFeature, chunkPos, i, l);
			this.seed = l;
		}

		public void init(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			ChunkPos chunkPos,
			Biome biome,
			DefaultFeatureConfig defaultFeatureConfig,
			HeightLimitView heightLimitView
		) {
			int i = 0;

			StrongholdGenerator.Start start;
			do {
				this.clearChildren();
				this.random.setCarverSeed(this.seed + (long)(i++), chunkPos.x, chunkPos.z);
				StrongholdGenerator.init();
				start = new StrongholdGenerator.Start(this.random, chunkPos.getOffsetX(2), chunkPos.getOffsetZ(2));
				this.addPiece(start);
				start.fillOpenings(start, this, this.random);
				List<StructurePiece> list = start.pieces;

				while (!list.isEmpty()) {
					int j = this.random.nextInt(list.size());
					StructurePiece structurePiece = (StructurePiece)list.remove(j);
					structurePiece.fillOpenings(start, this, this.random);
				}

				this.randomUpwardTranslation(chunkGenerator.getSeaLevel(), chunkGenerator.getMinimumY(), this.random, 10);
			} while (this.hasNoChildren() || start.portalRoom == null);
		}
	}
}
