package net.minecraft.world.biome;

import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.SeagrassFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class DeepOceanBiome extends Biome {
	public DeepOceanBiome() {
		super(
			new Biome.Settings()
				.method_8737(SurfaceBuilder.field_15701, SurfaceBuilder.field_15677)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.field_9367)
				.depth(-1.8F)
				.scale(0.1F)
				.temperature(0.5F)
				.downfall(0.5F)
				.waterColor(4159204)
				.waterFogColor(329011)
				.parent(null)
		);
		this.method_8710(Feature.field_13588, FeatureConfig.field_13603);
		this.method_8710(Feature.field_13547, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.field_13692));
		this.method_8710(Feature.field_13589, new ShipwreckFeatureConfig(false));
		this.method_8710(Feature.field_13536, new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.field_14528, 0.3F, 0.9F));
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
		this.method_8719(
			GenerationStep.Feature.field_13178, method_8699(Feature.field_13567, new SeagrassFeatureConfig(48, 0.8), Decorator.field_14231, DecoratorConfig.field_13436)
		);
		DefaultBiomeFeatures.addSeagrassOnStone(this);
		DefaultBiomeFeatures.addKelp(this);
		DefaultBiomeFeatures.addFrozenTopLayer(this);
		this.addSpawn(EntityCategory.field_6300, new Biome.SpawnEntry(EntityType.field_6114, 1, 1, 4));
		this.addSpawn(EntityCategory.field_6300, new Biome.SpawnEntry(EntityType.field_6070, 10, 3, 6));
		this.addSpawn(EntityCategory.field_6300, new Biome.SpawnEntry(EntityType.field_6087, 1, 1, 2));
		this.addSpawn(EntityCategory.field_6303, new Biome.SpawnEntry(EntityType.field_6108, 10, 8, 8));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6079, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6051, 95, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6123, 5, 1, 1));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6054, 5, 1, 1));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6137, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6046, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6069, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6091, 10, 1, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6145, 5, 1, 1));
	}
}
