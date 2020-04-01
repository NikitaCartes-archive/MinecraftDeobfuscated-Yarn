package net.minecraft;

import net.minecraft.block.Blocks;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class class_5081 extends OverworldDimension {
	public class_5081(World world, DimensionType dimensionType) {
		super(world, dimensionType);
	}

	@Override
	public ChunkGenerator<? extends ChunkGeneratorConfig> createChunkGenerator() {
		FixedBiomeSourceConfig fixedBiomeSourceConfig = BiomeSourceType.FIXED.getConfig(0L).setBiome(Biomes.BUSY);
		return ChunkGeneratorType.SURFACE.create(this.world, new FixedBiomeSource(fixedBiomeSourceConfig), ChunkGeneratorType.SURFACE.createConfig());
	}

	public static class class_5082 extends Biome {
		public class_5082() {
			super(
				new Biome.Settings()
					.configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_CONFIG)
					.precipitation(Biome.Precipitation.SNOW)
					.category(Biome.Category.TAIGA)
					.depth(0.3F)
					.scale(0.4F)
					.temperature(-0.5F)
					.downfall(0.4F)
					.effects(new BiomeEffects.Builder().waterColor(4020182).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build())
					.parent("snowy_taiga")
			);
			this.addStructureFeature(Feature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL)));
			this.addStructureFeature(Feature.STRONGHOLD.configure(FeatureConfig.DEFAULT));
			DefaultBiomeFeatures.addLandCarvers(this);
			DefaultBiomeFeatures.addDefaultStructures(this);
			DefaultBiomeFeatures.addDefaultLakes(this);
			DefaultBiomeFeatures.addDungeons(this);
			DefaultBiomeFeatures.addLargeFerns(this);
			DefaultBiomeFeatures.addMineables(this);
			DefaultBiomeFeatures.addDefaultDisks(this);
			DefaultBiomeFeatures.addTaigaTrees(this);
			DefaultBiomeFeatures.addDefaultFlowers(this);
			DefaultBiomeFeatures.addTaigaGrass(this);
			DefaultBiomeFeatures.addDefaultMushrooms(this);
			DefaultBiomeFeatures.addDefaultVegetation(this);
			DefaultBiomeFeatures.addSprings(this);
			DefaultBiomeFeatures.addSweetBerryBushesSnowy(this);
			DefaultBiomeFeatures.addFrozenTopLayer(this);
			ConfiguredDecorator<RangeDecoratorConfig> configuredDecorator = Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(20, 0, 0, 128));
			this.addFeature(
				GenerationStep.Feature.UNDERGROUND_ORES,
				Feature.ORE
					.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.COAL_BLOCK.getDefaultState(), 17))
					.createDecoratedFeature(configuredDecorator)
			);
			this.addFeature(
				GenerationStep.Feature.UNDERGROUND_ORES,
				Feature.ORE
					.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.IRON_BLOCK.getDefaultState(), 9))
					.createDecoratedFeature(configuredDecorator)
			);
			this.addFeature(
				GenerationStep.Feature.UNDERGROUND_ORES,
				Feature.ORE
					.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.GOLD_BLOCK.getDefaultState(), 9))
					.createDecoratedFeature(configuredDecorator)
			);
			this.addFeature(
				GenerationStep.Feature.UNDERGROUND_ORES,
				Feature.ORE
					.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.REDSTONE_BLOCK.getDefaultState(), 8))
					.createDecoratedFeature(configuredDecorator)
			);
			this.addFeature(
				GenerationStep.Feature.UNDERGROUND_ORES,
				Feature.ORE
					.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.DIAMOND_BLOCK.getDefaultState(), 8))
					.createDecoratedFeature(configuredDecorator)
			);
			this.addFeature(
				GenerationStep.Feature.UNDERGROUND_ORES,
				Feature.ORE
					.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.LAPIS_BLOCK.getDefaultState(), 7))
					.createDecoratedFeature(configuredDecorator)
			);
			this.addFeature(
				GenerationStep.Feature.UNDERGROUND_ORES,
				Feature.ORE
					.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.PISTON.getDefaultState(), 17))
					.createDecoratedFeature(configuredDecorator)
			);
			this.addFeature(
				GenerationStep.Feature.UNDERGROUND_ORES,
				Feature.ORE
					.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.STICKY_PISTON.getDefaultState(), 9))
					.createDecoratedFeature(configuredDecorator)
			);
			this.addFeature(
				GenerationStep.Feature.UNDERGROUND_ORES,
				Feature.ORE
					.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.DISPENSER.getDefaultState(), 9))
					.createDecoratedFeature(configuredDecorator)
			);
			this.addFeature(
				GenerationStep.Feature.UNDERGROUND_ORES,
				Feature.ORE
					.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.DROPPER.getDefaultState(), 8))
					.createDecoratedFeature(configuredDecorator)
			);
			this.addFeature(
				GenerationStep.Feature.UNDERGROUND_ORES,
				Feature.ORE
					.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.OBSERVER.getDefaultState(), 8))
					.createDecoratedFeature(configuredDecorator)
			);
			this.addFeature(
				GenerationStep.Feature.UNDERGROUND_ORES,
				Feature.ORE
					.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.HOPPER.getDefaultState(), 7))
					.createDecoratedFeature(configuredDecorator)
			);
		}
	}
}
