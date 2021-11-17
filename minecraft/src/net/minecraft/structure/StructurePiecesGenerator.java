package net.minecraft.structure;

import java.lang.runtime.ObjectMethods;
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

	public static final class Context extends Record {
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

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",StructurePiecesGenerator.Context,"config;chunkGenerator;structureManager;chunkPos;heightAccessor;random;seed",StructurePiecesGenerator.Context::config,StructurePiecesGenerator.Context::chunkGenerator,StructurePiecesGenerator.Context::structureManager,StructurePiecesGenerator.Context::chunkPos,StructurePiecesGenerator.Context::world,StructurePiecesGenerator.Context::random,StructurePiecesGenerator.Context::seed>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",StructurePiecesGenerator.Context,"config;chunkGenerator;structureManager;chunkPos;heightAccessor;random;seed",StructurePiecesGenerator.Context::config,StructurePiecesGenerator.Context::chunkGenerator,StructurePiecesGenerator.Context::structureManager,StructurePiecesGenerator.Context::chunkPos,StructurePiecesGenerator.Context::world,StructurePiecesGenerator.Context::random,StructurePiecesGenerator.Context::seed>(
				this
			);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",StructurePiecesGenerator.Context,"config;chunkGenerator;structureManager;chunkPos;heightAccessor;random;seed",StructurePiecesGenerator.Context::config,StructurePiecesGenerator.Context::chunkGenerator,StructurePiecesGenerator.Context::structureManager,StructurePiecesGenerator.Context::chunkPos,StructurePiecesGenerator.Context::world,StructurePiecesGenerator.Context::random,StructurePiecesGenerator.Context::seed>(
				this, object
			);
		}

		public C config() {
			return this.config;
		}

		public ChunkGenerator chunkGenerator() {
			return this.chunkGenerator;
		}

		public StructureManager structureManager() {
			return this.structureManager;
		}

		public ChunkPos chunkPos() {
			return this.chunkPos;
		}

		public HeightLimitView world() {
			return this.world;
		}

		public ChunkRandom random() {
			return this.random;
		}

		public long seed() {
			return this.seed;
		}
	}
}
