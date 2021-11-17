package net.minecraft;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiecesGenerator;
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
public interface class_6834<C extends FeatureConfig> {
	Optional<StructurePiecesGenerator<C>> createGenerator(class_6834.class_6835<C> arg);

	static <C extends FeatureConfig> class_6834<C> simple(Predicate<class_6834.class_6835<C>> predicate, StructurePiecesGenerator<C> structurePiecesGenerator) {
		Optional<StructurePiecesGenerator<C>> optional = Optional.of(structurePiecesGenerator);
		return arg -> predicate.test(arg) ? optional : Optional.empty();
	}

	static <C extends FeatureConfig> Predicate<class_6834.class_6835<C>> checkForBiomeOnTop(Heightmap.Type type) {
		return arg -> arg.method_39848(type);
	}

	public static record class_6835() {
		private final ChunkGenerator chunkGenerator;
		private final BiomeSource biomeSource;
		private final long seed;
		private final ChunkPos chunkPos;
		private final C config;
		private final HeightLimitView heightAccessor;
		private final Predicate<Biome> validBiome;
		private final StructureManager structureManager;
		private final DynamicRegistryManager registryAccess;

		public class_6835(
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
			this.heightAccessor = heightLimitView;
			this.validBiome = predicate;
			this.structureManager = structureManager;
			this.registryAccess = dynamicRegistryManager;
		}

		public boolean method_39848(Heightmap.Type type) {
			int i = this.chunkPos.getCenterX();
			int j = this.chunkPos.getCenterZ();
			int k = this.chunkGenerator.getHeightInGround(i, j, type, this.heightAccessor);
			Biome biome = this.chunkGenerator.getBiomeForNoiseGen(BiomeCoords.fromBlock(i), BiomeCoords.fromBlock(k), BiomeCoords.fromBlock(j));
			return this.validBiome.test(biome);
		}

		public int[] method_39847(int i, int j, int k, int l) {
			return new int[]{
				this.chunkGenerator.getHeightInGround(i, k, Heightmap.Type.WORLD_SURFACE_WG, this.heightAccessor),
				this.chunkGenerator.getHeightInGround(i, k + l, Heightmap.Type.WORLD_SURFACE_WG, this.heightAccessor),
				this.chunkGenerator.getHeightInGround(i + j, k, Heightmap.Type.WORLD_SURFACE_WG, this.heightAccessor),
				this.chunkGenerator.getHeightInGround(i + j, k + l, Heightmap.Type.WORLD_SURFACE_WG, this.heightAccessor)
			};
		}

		public int method_39846(int i, int j) {
			int k = this.chunkPos.getStartX();
			int l = this.chunkPos.getStartZ();
			int[] is = this.method_39847(k, i, l, j);
			return Math.min(Math.min(is[0], is[1]), Math.min(is[2], is[3]));
		}
	}
}
