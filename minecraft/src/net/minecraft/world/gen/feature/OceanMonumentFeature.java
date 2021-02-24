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
import net.minecraft.world.HeightLimitView;
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
		ChunkPos chunkPos,
		Biome biome,
		ChunkPos chunkPos2,
		DefaultFeatureConfig defaultFeatureConfig,
		HeightLimitView heightLimitView
	) {
		int i = chunkPos.method_33939(9);
		int j = chunkPos.method_33941(9);

		for (Biome biome2 : biomeSource.getBiomesInArea(i, chunkGenerator.getSeaLevel(), j, 16)) {
			if (!biome2.getGenerationSettings().hasStructureFeature(this)) {
				return false;
			}
		}

		for (Biome biome3 : biomeSource.getBiomesInArea(i, chunkGenerator.getSeaLevel(), j, 29)) {
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

		public Start(StructureFeature<DefaultFeatureConfig> structureFeature, ChunkPos chunkPos, BlockBox blockBox, int i, long l) {
			super(structureFeature, chunkPos, blockBox, i, l);
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
			this.method_16588(chunkPos);
		}

		private void method_16588(ChunkPos chunkPos) {
			int i = chunkPos.getStartX() - 29;
			int j = chunkPos.getStartZ() - 29;
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
				this.method_16588(this.method_34000());
			}

			super.generateStructure(world, structureAccessor, chunkGenerator, random, box, chunkPos);
		}
	}
}
