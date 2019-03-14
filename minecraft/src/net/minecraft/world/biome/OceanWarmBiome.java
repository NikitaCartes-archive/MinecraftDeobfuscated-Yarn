package net.minecraft.world.biome;

import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.TopSolidHeightmapNoiseBiasedDecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.SeaPickleFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.SimpleRandomFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class OceanWarmBiome extends Biome {
	public OceanWarmBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.SAND_SAND_UNDERWATER_CONFIG)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.OCEAN)
				.depth(-1.0F)
				.scale(0.1F)
				.temperature(0.5F)
				.downfall(0.5F)
				.waterColor(4445678)
				.waterFogColor(270131)
				.parent(null)
		);
		this.addStructureFeature(Feature.OCEAN_RUIN, new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.WARM, 0.3F, 0.9F));
		this.addStructureFeature(Feature.MINESHAFT, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL));
		this.addStructureFeature(Feature.SHIPWRECK, new ShipwreckFeatureConfig(false));
		DefaultBiomeFeatures.addOceanCarvers(this);
		DefaultBiomeFeatures.addDefaultStructures(this);
		DefaultBiomeFeatures.addDefaultLakes(this);
		DefaultBiomeFeatures.addDungeons(this);
		DefaultBiomeFeatures.addMineables(this);
		DefaultBiomeFeatures.addDefaultOres(this);
		DefaultBiomeFeatures.addDefaultDisks(this);
		DefaultBiomeFeatures.addWaterBiomeOakTrees(this);
		DefaultBiomeFeatures.addDefaultFlowers(this);
		DefaultBiomeFeatures.addDefaultGrass(this);
		DefaultBiomeFeatures.addDefaultMushrooms(this);
		DefaultBiomeFeatures.addDefaultVegetation(this);
		DefaultBiomeFeatures.addSprings(this);
		this.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			configureFeature(
				Feature.field_13555,
				new SimpleRandomFeatureConfig(
					new Feature[]{Feature.field_13525, Feature.field_13546, Feature.field_13585},
					new FeatureConfig[]{FeatureConfig.DEFAULT, FeatureConfig.DEFAULT, FeatureConfig.DEFAULT}
				),
				Decorator.field_14247,
				new TopSolidHeightmapNoiseBiasedDecoratorConfig(20, 400.0, 0.0, Heightmap.Type.OCEAN_FLOOR_WG)
			)
		);
		DefaultBiomeFeatures.addSeagrass(this);
		this.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			configureFeature(Feature.field_13575, new SeaPickleFeatureConfig(20), Decorator.field_14258, new ChanceDecoratorConfig(16))
		);
		DefaultBiomeFeatures.addFrozenTopLayer(this);
		this.addSpawn(EntityCategory.field_6300, new Biome.SpawnEntry(EntityType.SQUID, 10, 4, 4));
		this.addSpawn(EntityCategory.field_6300, new Biome.SpawnEntry(EntityType.PUFFERFISH, 15, 1, 3));
		this.addSpawn(EntityCategory.field_6300, new Biome.SpawnEntry(EntityType.TROPICAL_FISH, 25, 8, 8));
		this.addSpawn(EntityCategory.field_6300, new Biome.SpawnEntry(EntityType.DOLPHIN, 2, 1, 2));
		this.addSpawn(EntityCategory.field_6303, new Biome.SpawnEntry(EntityType.BAT, 10, 8, 8));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.ZOMBIE, 95, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.SKELETON, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.SLIME, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.WITCH, 5, 1, 1));
	}
}
