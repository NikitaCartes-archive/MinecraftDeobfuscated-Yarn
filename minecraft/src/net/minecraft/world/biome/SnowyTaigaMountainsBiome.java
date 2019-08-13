package net.minecraft.world.biome;

import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class SnowyTaigaMountainsBiome extends Biome {
	public SnowyTaigaMountainsBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.field_15701, SurfaceBuilder.GRASS_CONFIG)
				.precipitation(Biome.Precipitation.SNOW)
				.category(Biome.Category.field_9361)
				.depth(0.3F)
				.scale(0.4F)
				.temperature(-0.5F)
				.downfall(0.4F)
				.waterColor(4020182)
				.waterFogColor(329011)
				.parent("snowy_taiga")
		);
		this.addStructureFeature(Feature.MINESHAFT, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.field_13692));
		this.addStructureFeature(Feature.STRONGHOLD, FeatureConfig.DEFAULT);
		DefaultBiomeFeatures.addLandCarvers(this);
		DefaultBiomeFeatures.addDefaultStructures(this);
		DefaultBiomeFeatures.addDefaultLakes(this);
		DefaultBiomeFeatures.addDungeons(this);
		DefaultBiomeFeatures.addLargeFerns(this);
		DefaultBiomeFeatures.addMineables(this);
		DefaultBiomeFeatures.addDefaultOres(this);
		DefaultBiomeFeatures.addDefaultDisks(this);
		DefaultBiomeFeatures.addTaigaTrees(this);
		DefaultBiomeFeatures.addDefaultFlowers(this);
		DefaultBiomeFeatures.addTaigaGrass(this);
		DefaultBiomeFeatures.addDefaultMushrooms(this);
		DefaultBiomeFeatures.addDefaultVegetation(this);
		DefaultBiomeFeatures.addSprings(this);
		DefaultBiomeFeatures.addSweetBerryBushesSnowy(this);
		DefaultBiomeFeatures.addFrozenTopLayer(this);
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.field_6115, 12, 4, 4));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.field_6093, 10, 4, 4));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.field_6132, 10, 4, 4));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.field_6085, 8, 4, 4));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.field_6055, 8, 4, 4));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.field_6140, 4, 2, 3));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.field_17943, 8, 2, 4));
		this.addSpawn(EntityCategory.field_6303, new Biome.SpawnEntry(EntityType.field_6108, 10, 8, 8));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6079, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6051, 95, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6054, 5, 1, 1));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6137, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6046, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6069, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6091, 10, 1, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6145, 5, 1, 1));
	}
}
