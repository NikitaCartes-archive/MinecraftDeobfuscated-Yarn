package net.minecraft.world.biome;

import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class DeepLukewarmOceanBiome extends Biome {
	public DeepLukewarmOceanBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_SAND_UNDERWATER_CONFIG)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.OCEAN)
				.depth(-1.8F)
				.scale(0.1F)
				.temperature(0.5F)
				.downfall(0.5F)
				.effects(new BiomeEffects.Builder().waterColor(4566514).waterFogColor(267827).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build())
				.parent(null)
		);
		this.addStructureFeature(Feature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.WARM, 0.3F, 0.9F)));
		this.addStructureFeature(Feature.OCEAN_MONUMENT.configure(FeatureConfig.DEFAULT));
		this.addStructureFeature(Feature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL)));
		this.addStructureFeature(Feature.SHIPWRECK.configure(new ShipwreckFeatureConfig(false)));
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
		DefaultBiomeFeatures.addMoreSeagrass(this);
		DefaultBiomeFeatures.addSeagrassOnStone(this);
		DefaultBiomeFeatures.addLessKelp(this);
		DefaultBiomeFeatures.addFrozenTopLayer(this);
		this.addSpawn(EntityCategory.WATER_CREATURE, new Biome.SpawnEntry(EntityType.SQUID, 8, 1, 4));
		this.addSpawn(EntityCategory.WATER_CREATURE, new Biome.SpawnEntry(EntityType.COD, 8, 3, 6));
		this.addSpawn(EntityCategory.WATER_CREATURE, new Biome.SpawnEntry(EntityType.PUFFERFISH, 5, 1, 3));
		this.addSpawn(EntityCategory.WATER_CREATURE, new Biome.SpawnEntry(EntityType.TROPICAL_FISH, 25, 8, 8));
		this.addSpawn(EntityCategory.WATER_CREATURE, new Biome.SpawnEntry(EntityType.DOLPHIN, 2, 1, 2));
		this.addSpawn(EntityCategory.AMBIENT, new Biome.SpawnEntry(EntityType.BAT, 10, 8, 8));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE, 95, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SKELETON, 100, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SLIME, 100, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.WITCH, 5, 1, 1));
	}
}
