package net.minecraft.structure;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.random.ChunkRandom;

/**
 * A structure pieces generator adds structure pieces for a structure,
 * but does not yet realize those pieces into the world. It executes in the
 * structure starts chunk status.
 */
@FunctionalInterface
public interface StructurePiecesGenerator<C extends FeatureConfig> {
	void generatePieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<C> context);

	public static record Context() {
		private final C config;
		private final ChunkGenerator chunkGenerator;
		private final StructureManager structureManager;
		private final ChunkPos chunkPos;
		private final HeightLimitView world;
		private final ChunkRandom random;
		private final long seed;

		public Context(
			C featureConfig,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			ChunkPos chunkPos,
			HeightLimitView heightLimitView,
			ChunkRandom chunkRandom,
			long l
		) {
			this.config = featureConfig;
			this.chunkGenerator = chunkGenerator;
			this.structureManager = structureManager;
			this.chunkPos = chunkPos;
			this.world = heightLimitView;
			this.random = chunkRandom;
			this.seed = l;
		}
	}
}
