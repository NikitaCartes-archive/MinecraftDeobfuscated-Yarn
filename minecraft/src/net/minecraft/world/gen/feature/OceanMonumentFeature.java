package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class OceanMonumentFeature extends StructureFeature<DefaultFeatureConfig> {
	private static final List<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = ImmutableList.of(new SpawnSettings.SpawnEntry(EntityType.GUARDIAN, 1, 2, 4));

	public OceanMonumentFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	protected boolean isUniformDistribution() {
		return false;
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
		for (Biome biome2 : biomeSource.getBiomesInArea(i * 16 + 9, chunkGenerator.getSeaLevel(), j * 16 + 9, 16)) {
			if (!biome2.getGenerationSettings().hasStructureFeature(this)) {
				return false;
			}
		}

		for (Biome biome3 : biomeSource.getBiomesInArea(i * 16 + 9, chunkGenerator.getSeaLevel(), j * 16 + 9, 29)) {
			if (biome3.getCategory() != Biome.Category.OCEAN && biome3.getCategory() != Biome.Category.RIVER) {
				return false;
			}
		}

		return true;
	}

	@Override
	public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
		return OceanMonumentFeature.Start::new;
	}

	@Override
	public List<SpawnSettings.SpawnEntry> getMonsterSpawns() {
		return MONSTER_SPAWNS;
	}

	public static class Start extends StructureStart<DefaultFeatureConfig> {
		private boolean field_13717;

		public Start(StructureFeature<DefaultFeatureConfig> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
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
			this.method_16588(i, j);
		}

		private void method_16588(int chunkX, int chunkZ) {
			int i = chunkX * 16 - 29;
			int j = chunkZ * 16 - 29;
			Direction direction = Direction.Type.HORIZONTAL.random(this.random);
			this.children.add(new OceanMonumentGenerator.Base(this.random, i, j, direction));
			this.setBoundingBoxFromChildren();
			this.field_13717 = true;
		}

		@Override
		public void generateStructure(
			StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox box, ChunkPos chunkPos
		) {
			if (!this.field_13717) {
				this.children.clear();
				this.method_16588(this.getChunkX(), this.getChunkZ());
			}

			super.generateStructure(world, structureAccessor, chunkGenerator, random, box, chunkPos);
		}
	}
}
