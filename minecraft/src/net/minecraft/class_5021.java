package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.RandomDimension;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class class_5021 extends RandomDimension {
	public class_5021(World world, DimensionType dimensionType) {
		super(world, dimensionType, 1.5F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5021.class_5023(this.field_23566, method_26572(Biomes.BETWEEN), class_5099.field_23565);
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return 0.0F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d modifyFogColor(Vec3d vec3d, float tickDelta) {
		return vec3d;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isFogThick(int x, int z) {
		return false;
	}

	public static class class_5022 extends Biome {
		public class_5022() {
			super(
				new Biome.Settings()
					.configureSurfaceBuilder(SurfaceBuilder.NOPE, SurfaceBuilder.STONE_CONFIG)
					.precipitation(Biome.Precipitation.NONE)
					.category(Biome.Category.NONE)
					.depth(0.1F)
					.scale(0.2F)
					.temperature(0.5F)
					.downfall(0.5F)
					.effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build())
					.parent(null)
			);
			this.addStructureFeature(Feature.field_23572.configure(FeatureConfig.DEFAULT));
			this.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Feature.field_23572.configure(FeatureConfig.DEFAULT));
		}
	}

	public static class class_5023 extends ChunkGenerator<class_5099> {
		public class_5023(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
			super(iWorld, biomeSource, arg);
		}

		@Override
		public void buildSurface(ChunkRegion region, Chunk chunk) {
		}

		@Override
		public void carve(BiomeAccess biomeAccess, Chunk chunk, GenerationStep.Carver carver) {
		}

		@Override
		public int getSpawnHeight() {
			return 0;
		}

		@Override
		public void populateNoise(IWorld world, Chunk chunk) {
		}

		@Override
		public int getHeight(int x, int z, Heightmap.Type heightmapType) {
			return 0;
		}

		@Override
		public BlockView getColumnSample(int x, int z) {
			return EmptyBlockView.INSTANCE;
		}

		@Override
		public ChunkGeneratorType<?, ?> method_26490() {
			return ChunkGeneratorType.field_23460;
		}
	}

	public static class class_5024 extends StructureFeature<DefaultFeatureConfig> {
		public class_5024(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, Function<Random, ? extends DefaultFeatureConfig> function2) {
			super(function, function2);
		}

		@Override
		public boolean shouldStartAt(BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, Random random, int chunkX, int chunkZ, Biome biome) {
			return (chunkX & 1) == 0 && (chunkZ & 1) == 0;
		}

		@Override
		public StructureFeature.StructureStartFactory getStructureStartFactory() {
			return class_5021.class_5026::new;
		}

		@Override
		public String getName() {
			return "Ship";
		}

		@Override
		public int getRadius() {
			return 0;
		}
	}

	public static class class_5025 extends SimpleStructurePiece {
		private static final Identifier field_23516 = new Identifier("end_city/ship");

		public class_5025(StructureManager structureManager, BlockPos blockPos) {
			super(StructurePieceType.FP, 0);
			this.pos = blockPos;
			this.method_26532(structureManager);
		}

		public class_5025(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.FP, compoundTag);
			this.method_26532(structureManager);
		}

		private void method_26532(StructureManager structureManager) {
			Structure structure = structureManager.getStructureOrBlank(field_23516);
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS)
				.setIgnoreEntities(true);
			this.setStructureData(structure, this.pos, structurePlacementData);
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, IWorld world, Random random, BlockBox boundingBox) {
			if (metadata.startsWith("Chest")) {
				BlockPos blockPos = pos.down();
				if (boundingBox.contains(blockPos)) {
					LootableContainerBlockEntity.setLootTable(world, random, blockPos, LootTables.FLEET_ORDERS_CHEST);
				}
			}
		}
	}

	static class class_5026 extends StructureStart {
		public class_5026(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			this.children.add(new class_5021.class_5025(structureManager, new BlockPos(x * 16 + 9, 50, z * 16 + 9)));
			this.children.add(new class_5021.class_5025(structureManager, new BlockPos(x * 16 + 9, 100, z * 16 + 9)));
			this.children.add(new class_5021.class_5025(structureManager, new BlockPos(x * 16 + 9, 150, z * 16 + 9)));
			this.setBoundingBoxFromChildren();
		}
	}
}
