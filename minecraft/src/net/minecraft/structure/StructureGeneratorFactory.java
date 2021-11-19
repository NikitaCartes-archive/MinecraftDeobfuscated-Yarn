package net.minecraft.structure;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;

@FunctionalInterface
public interface StructureGeneratorFactory<C extends FeatureConfig> {
	Optional<StructurePiecesGenerator<C>> createGenerator(StructureGeneratorFactory.Context<C> context);

	static <C extends FeatureConfig> StructureGeneratorFactory<C> simple(
		Predicate<StructureGeneratorFactory.Context<C>> predicate, StructurePiecesGenerator<C> generator
	) {
		Optional<StructurePiecesGenerator<C>> optional = Optional.of(generator);
		return context -> predicate.test(context) ? optional : Optional.empty();
	}

	static <C extends FeatureConfig> Predicate<StructureGeneratorFactory.Context<C>> checkForBiomeOnTop(Heightmap.Type heightmapType) {
		return context -> context.isBiomeValid(heightmapType);
	}

	public static record Context() {
		private final ChunkGenerator chunkGenerator;
		private final BiomeSource biomeSource;
		private final long seed;
		private final ChunkPos chunkPos;
		private final C config;
		private final HeightLimitView world;
		private final Predicate<Biome> validBiome;
		private final StructureManager structureManager;
		private final DynamicRegistryManager registryManager;

		public Context(
			ChunkGenerator chunkGenerator,
			BiomeSource biomeSource,
			long l,
			ChunkPos chunkPos,
			C featureConfig,
			HeightLimitView heightLimitView,
			Predicate<Biome> predicate,
			StructureManager structureManager,
			DynamicRegistryManager dynamicRegistryManager
		) {
			this.chunkGenerator = chunkGenerator;
			this.biomeSource = biomeSource;
			this.seed = l;
			this.chunkPos = chunkPos;
			this.config = featureConfig;
			this.world = heightLimitView;
			this.validBiome = predicate;
			this.structureManager = structureManager;
			this.registryManager = dynamicRegistryManager;
		}

		public boolean isBiomeValid(Heightmap.Type heightmapType) {
			int i = this.chunkPos.getCenterX();
			int j = this.chunkPos.getCenterZ();
			int k = this.chunkGenerator.getHeightInGround(i, j, heightmapType, this.world);
			Biome biome = this.chunkGenerator.getBiomeForNoiseGen(BiomeCoords.fromBlock(i), BiomeCoords.fromBlock(k), BiomeCoords.fromBlock(j));
			return this.validBiome.test(biome);
		}

		public int[] getCornerHeights(int x, int width, int z, int height) {
			return new int[]{
				this.chunkGenerator.getHeightInGround(x, z, Heightmap.Type.WORLD_SURFACE_WG, this.world),
				this.chunkGenerator.getHeightInGround(x, z + height, Heightmap.Type.WORLD_SURFACE_WG, this.world),
				this.chunkGenerator.getHeightInGround(x + width, z, Heightmap.Type.WORLD_SURFACE_WG, this.world),
				this.chunkGenerator.getHeightInGround(x + width, z + height, Heightmap.Type.WORLD_SURFACE_WG, this.world)
			};
		}

		public int getMinCornerHeight(int width, int height) {
			int i = this.chunkPos.getStartX();
			int j = this.chunkPos.getStartZ();
			int[] is = this.getCornerHeights(i, width, j, height);
			return Math.min(Math.min(is[0], is[1]), Math.min(is[2], is[3]));
		}
	}
}
