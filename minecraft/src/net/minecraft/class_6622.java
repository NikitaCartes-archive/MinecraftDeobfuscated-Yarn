package net.minecraft;

import java.lang.runtime.ObjectMethods;
import java.util.function.Predicate;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.random.ChunkRandom;

@FunctionalInterface
public interface class_6622<C extends FeatureConfig> {
	void generatePieces(class_6626 arg, C featureConfig, class_6622.class_6623 arg2);

	public static final class class_6623 extends Record {
		private final DynamicRegistryManager registryAccess;
		private final ChunkGenerator chunkGenerator;
		private final StructureManager structureManager;
		private final ChunkPos chunkPos;
		private final Predicate<Biome> validBiome;
		private final HeightLimitView heightAccessor;
		private final ChunkRandom random;
		private final long seed;

		public class_6623(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			ChunkPos chunkPos,
			Predicate<Biome> predicate,
			HeightLimitView heightLimitView,
			ChunkRandom chunkRandom,
			long l
		) {
			this.registryAccess = dynamicRegistryManager;
			this.chunkGenerator = chunkGenerator;
			this.structureManager = structureManager;
			this.chunkPos = chunkPos;
			this.validBiome = predicate;
			this.heightAccessor = heightLimitView;
			this.random = chunkRandom;
			this.seed = l;
		}

		public boolean method_38707(Heightmap.Type type) {
			int i = this.chunkPos.getCenterX();
			int j = this.chunkPos.getCenterZ();
			int k = this.chunkGenerator.getHeightInGround(i, j, type, this.heightAccessor);
			Biome biome = this.chunkGenerator.getBiomeForNoiseGen(BiomeCoords.fromBlock(i), BiomeCoords.fromBlock(k), BiomeCoords.fromBlock(j));
			return this.validBiome.test(biome);
		}

		public int method_38705(int i, int j) {
			int k = this.chunkPos.getStartX();
			int l = this.chunkPos.getStartZ();
			int[] is = this.method_38706(k, i, l, j);
			return Math.min(Math.min(is[0], is[1]), Math.min(is[2], is[3]));
		}

		public int[] method_38706(int i, int j, int k, int l) {
			return new int[]{
				this.chunkGenerator.getHeightInGround(i, k, Heightmap.Type.WORLD_SURFACE_WG, this.heightAccessor),
				this.chunkGenerator.getHeightInGround(i, k + l, Heightmap.Type.WORLD_SURFACE_WG, this.heightAccessor),
				this.chunkGenerator.getHeightInGround(i + j, k, Heightmap.Type.WORLD_SURFACE_WG, this.heightAccessor),
				this.chunkGenerator.getHeightInGround(i + j, k + l, Heightmap.Type.WORLD_SURFACE_WG, this.heightAccessor)
			};
		}

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",class_6622.class_6623,"registryAccess;chunkGenerator;structureManager;chunkPos;validBiome;heightAccessor;random;seed",class_6622.class_6623::registryAccess,class_6622.class_6623::chunkGenerator,class_6622.class_6623::structureManager,class_6622.class_6623::chunkPos,class_6622.class_6623::validBiome,class_6622.class_6623::heightAccessor,class_6622.class_6623::random,class_6622.class_6623::seed>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",class_6622.class_6623,"registryAccess;chunkGenerator;structureManager;chunkPos;validBiome;heightAccessor;random;seed",class_6622.class_6623::registryAccess,class_6622.class_6623::chunkGenerator,class_6622.class_6623::structureManager,class_6622.class_6623::chunkPos,class_6622.class_6623::validBiome,class_6622.class_6623::heightAccessor,class_6622.class_6623::random,class_6622.class_6623::seed>(
				this
			);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",class_6622.class_6623,"registryAccess;chunkGenerator;structureManager;chunkPos;validBiome;heightAccessor;random;seed",class_6622.class_6623::registryAccess,class_6622.class_6623::chunkGenerator,class_6622.class_6623::structureManager,class_6622.class_6623::chunkPos,class_6622.class_6623::validBiome,class_6622.class_6623::heightAccessor,class_6622.class_6623::random,class_6622.class_6623::seed>(
				this, object
			);
		}

		public DynamicRegistryManager registryAccess() {
			return this.registryAccess;
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

		public Predicate<Biome> validBiome() {
			return this.validBiome;
		}

		public HeightLimitView heightAccessor() {
			return this.heightAccessor;
		}

		public ChunkRandom random() {
			return this.random;
		}

		public long seed() {
			return this.seed;
		}
	}
}
