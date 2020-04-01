package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.util.ColoredBlockArrays;
import net.minecraft.util.Util;
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
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.dimension.RandomDimension;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.decorator.ChanceRangeDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureEntry;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class class_5031 extends RandomDimension {
	public class_5031(World world, DimensionType dimensionType) {
		super(world, dimensionType, 1.0F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5031.class_5032(this.field_23566, method_26572(Biomes.SHAPES), class_5099.field_23565);
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return OverworldDimension.method_26524(timeOfDay, 3000.0);
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

	public static class class_5032 extends ChunkGenerator<class_5099> {
		public class_5032(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
			super(iWorld, biomeSource, arg);
		}

		@Override
		public void buildSurface(ChunkRegion region, Chunk chunk) {
		}

		@Override
		public int getSpawnHeight() {
			return 30;
		}

		@Override
		public void populateNoise(IWorld world, Chunk chunk) {
		}

		@Override
		public void carve(BiomeAccess biomeAccess, Chunk chunk, GenerationStep.Carver carver) {
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
			return ChunkGeneratorType.field_23463;
		}
	}

	public static class class_5033 extends Biome {
		public class_5033() {
			super(
				new Biome.Settings()
					.configureSurfaceBuilder(SurfaceBuilder.NOPE, SurfaceBuilder.STONE_CONFIG)
					.precipitation(Biome.Precipitation.NONE)
					.category(Biome.Category.NONE)
					.depth(0.1F)
					.scale(0.2F)
					.temperature(0.5F)
					.downfall(0.5F)
					.effects(new BiomeEffects.Builder().waterColor(52713007).waterFogColor(1876255554).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build())
					.parent(null)
			);
			Random random = new Random(12461L);
			List<ConfiguredFeature<?, ?>> list = Lists.<ConfiguredFeature<?, ?>>newArrayList();
			Stream.of(ColoredBlockArrays.ALL)
				.flatMap(Stream::of)
				.forEach(
					block -> {
						float fx = 1.0F + random.nextFloat() * 5.0F;
						float gx = Math.min(fx + random.nextFloat() * 10.0F, 15.0F);
						list.add(
							Feature.SHAPE
								.configure(new class_5105(new SimpleBlockStateProvider(block.getDefaultState()), Util.method_26721(random, class_5105.class_5106.values()), fx, gx))
						);
					}
				);

			for (Block[] blocks : ColoredBlockArrays.ALL) {
				ImmutableList<BlockState> immutableList = (ImmutableList<BlockState>)Stream.of(blocks).map(Block::getDefaultState).collect(ImmutableList.toImmutableList());

				for (class_5105.class_5106 lv : class_5105.class_5106.values()) {
					float f = 1.0F + random.nextFloat() * 5.0F;
					float g = Math.min(f + random.nextFloat() * 10.0F, 15.0F);
					list.add(Feature.SHAPE.configure(new class_5105(new class_5107(immutableList), lv, f, g)));
				}
			}

			float h = 1.0F / (float)list.size();
			this.addFeature(
				GenerationStep.Feature.SURFACE_STRUCTURES,
				Feature.RANDOM_SELECTOR
					.configure(
						new RandomFeatureConfig(
							(List<RandomFeatureEntry<?>>)list.stream().map(configuredFeature -> new RandomFeatureEntry(configuredFeature, h)).collect(Collectors.toList()),
							Util.method_26719(random, list)
						)
					)
					.createDecoratedFeature(Decorator.CHANCE_RANGE.configure(new ChanceRangeDecoratorConfig(1.0F, 16, 16, 128)))
			);
		}
	}
}
