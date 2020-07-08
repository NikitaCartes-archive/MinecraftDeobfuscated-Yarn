package net.minecraft.world.biome;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public class DeepColdOceanBiome extends Biome {
	public DeepColdOceanBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(ConfiguredSurfaceBuilders.GRASS)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.OCEAN)
				.depth(-1.8F)
				.scale(0.1F)
				.temperature(0.5F)
				.downfall(0.5F)
				.effects(new BiomeEffects.Builder().waterColor(4020182).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build())
				.parent(null)
		);
		this.addStructureFeature(ConfiguredStructureFeatures.OCEAN_RUIN_COLD);
		this.addStructureFeature(ConfiguredStructureFeatures.MONUMENT);
		DefaultBiomeFeatures.addOceanStructures(this);
		this.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_OCEAN);
		DefaultBiomeFeatures.addOceanCarvers(this);
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
		this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEAGRASS_DEEP_COLD);
		DefaultBiomeFeatures.addSeagrassOnStone(this);
		DefaultBiomeFeatures.addKelp(this);
		DefaultBiomeFeatures.addFrozenTopLayer(this);
		this.addSpawn(SpawnGroup.WATER_CREATURE, new Biome.SpawnEntry(EntityType.SQUID, 3, 1, 4));
		this.addSpawn(SpawnGroup.WATER_AMBIENT, new Biome.SpawnEntry(EntityType.COD, 15, 3, 6));
		this.addSpawn(SpawnGroup.WATER_AMBIENT, new Biome.SpawnEntry(EntityType.SALMON, 15, 1, 5));
		DefaultBiomeFeatures.addBatsAndMonsters(this);
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
	}
}
