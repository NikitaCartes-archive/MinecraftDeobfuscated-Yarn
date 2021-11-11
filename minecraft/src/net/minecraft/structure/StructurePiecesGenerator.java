package net.minecraft.structure;

import java.lang.runtime.ObjectMethods;
import java.util.function.Predicate;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
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
	void generatePieces(StructurePiecesCollector collector, C config, StructurePiecesGenerator.Context context);

	public static final class Context extends Record {
		private final DynamicRegistryManager registryManager;
		private final ChunkGenerator chunkGenerator;
		private final StructureManager structureManager;
		private final ChunkPos chunkPos;
		private final Predicate<Biome> biomeLimit;
		private final HeightLimitView world;
		private final ChunkRandom random;
		private final long seed;

		public Context(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			ChunkPos chunkPos,
			Predicate<Biome> predicate,
			HeightLimitView heightLimitView,
			ChunkRandom chunkRandom,
			long l
		) {
			this.registryManager = dynamicRegistryManager;
			this.chunkGenerator = chunkGenerator;
			this.structureManager = structureManager;
			this.chunkPos = chunkPos;
			this.biomeLimit = predicate;
			this.world = heightLimitView;
			this.random = chunkRandom;
			this.seed = l;
		}

		public boolean isBiomeValid(Heightmap.Type type) {
			int i = this.chunkPos.getCenterX();
			int j = this.chunkPos.getCenterZ();
			int k = this.chunkGenerator.getHeightInGround(i, j, type, this.world);
			Biome biome = this.chunkGenerator.getBiomeForNoiseGen(BiomeCoords.fromBlock(i), BiomeCoords.fromBlock(k), BiomeCoords.fromBlock(j));
			return this.biomeLimit.test(biome);
		}

		public int getMinInGroundHeight(int deltaX, int deltaZ) {
			int i = this.chunkPos.getStartX();
			int j = this.chunkPos.getStartZ();
			int[] is = this.getHeightsInGround(i, deltaX, j, deltaZ);
			return Math.min(Math.min(is[0], is[1]), Math.min(is[2], is[3]));
		}

		public int[] getHeightsInGround(int x, int deltaX, int z, int deltaZ) {
			return new int[]{
				this.chunkGenerator.getHeightInGround(x, z, Heightmap.Type.WORLD_SURFACE_WG, this.world),
				this.chunkGenerator.getHeightInGround(x, z + deltaZ, Heightmap.Type.WORLD_SURFACE_WG, this.world),
				this.chunkGenerator.getHeightInGround(x + deltaX, z, Heightmap.Type.WORLD_SURFACE_WG, this.world),
				this.chunkGenerator.getHeightInGround(x + deltaX, z + deltaZ, Heightmap.Type.WORLD_SURFACE_WG, this.world)
			};
		}

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",StructurePiecesGenerator.Context,"registryAccess;chunkGenerator;structureManager;chunkPos;validBiome;heightAccessor;random;seed",StructurePiecesGenerator.Context::registryManager,StructurePiecesGenerator.Context::chunkGenerator,StructurePiecesGenerator.Context::structureManager,StructurePiecesGenerator.Context::chunkPos,StructurePiecesGenerator.Context::biomeLimit,StructurePiecesGenerator.Context::world,StructurePiecesGenerator.Context::random,StructurePiecesGenerator.Context::seed>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",StructurePiecesGenerator.Context,"registryAccess;chunkGenerator;structureManager;chunkPos;validBiome;heightAccessor;random;seed",StructurePiecesGenerator.Context::registryManager,StructurePiecesGenerator.Context::chunkGenerator,StructurePiecesGenerator.Context::structureManager,StructurePiecesGenerator.Context::chunkPos,StructurePiecesGenerator.Context::biomeLimit,StructurePiecesGenerator.Context::world,StructurePiecesGenerator.Context::random,StructurePiecesGenerator.Context::seed>(
				this
			);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",StructurePiecesGenerator.Context,"registryAccess;chunkGenerator;structureManager;chunkPos;validBiome;heightAccessor;random;seed",StructurePiecesGenerator.Context::registryManager,StructurePiecesGenerator.Context::chunkGenerator,StructurePiecesGenerator.Context::structureManager,StructurePiecesGenerator.Context::chunkPos,StructurePiecesGenerator.Context::biomeLimit,StructurePiecesGenerator.Context::world,StructurePiecesGenerator.Context::random,StructurePiecesGenerator.Context::seed>(
				this, object
			);
		}

		public DynamicRegistryManager registryManager() {
			return this.registryManager;
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

		public Predicate<Biome> biomeLimit() {
			return this.biomeLimit;
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
