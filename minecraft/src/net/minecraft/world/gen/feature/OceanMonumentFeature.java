package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class OceanMonumentFeature extends StructureFeature<DefaultFeatureConfig> {
	private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.<Biome.SpawnEntry>newArrayList(new Biome.SpawnEntry(EntityType.GUARDIAN, 1, 2, 4));

	public OceanMonumentFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected int getSpacing(DimensionType dimensionType, ChunkGeneratorConfig chunkGeneratorConfig) {
		return chunkGeneratorConfig.getOceanMonumentSpacing();
	}

	@Override
	protected int getSeparation(DimensionType dimensionType, ChunkGeneratorConfig chunkGenerationConfig) {
		return chunkGenerationConfig.getOceanMonumentSeparation();
	}

	@Override
	protected int getSeedModifier(ChunkGeneratorConfig chunkGeneratorConfig) {
		return 10387313;
	}

	@Override
	protected boolean method_27219() {
		return false;
	}

	@Override
	protected boolean shouldStartAt(
		BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, ChunkRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos
	) {
		for (Biome biome2 : chunkGenerator.getBiomeSource().getBiomesInArea(chunkX * 16 + 9, chunkGenerator.getSeaLevel(), chunkZ * 16 + 9, 16)) {
			if (!chunkGenerator.hasStructure(biome2, this)) {
				return false;
			}
		}

		for (Biome biome3 : chunkGenerator.getBiomeSource().getBiomesInArea(chunkX * 16 + 9, chunkGenerator.getSeaLevel(), chunkZ * 16 + 9, 29)) {
			if (biome3.getCategory() != Biome.Category.OCEAN && biome3.getCategory() != Biome.Category.RIVER) {
				return false;
			}
		}

		return true;
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return OceanMonumentFeature.Start::new;
	}

	@Override
	public String getName() {
		return "Monument";
	}

	@Override
	public int getRadius() {
		return 8;
	}

	@Override
	public List<Biome.SpawnEntry> getMonsterSpawns() {
		return MONSTER_SPAWNS;
	}

	public static class Start extends StructureStart {
		private boolean field_13717;

		public Start(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		@Override
		public void init(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			this.method_16588(x, z);
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
			IWorld world, StructureAccessor structureAccessor, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos
		) {
			if (!this.field_13717) {
				this.children.clear();
				this.method_16588(this.getChunkX(), this.getChunkZ());
			}

			super.generateStructure(world, structureAccessor, chunkGenerator, random, blockBox, chunkPos);
		}
	}
}
