package net.minecraft.world.biome;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public class BambooJungleBiome extends Biome {
	public BambooJungleBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(ConfiguredSurfaceBuilders.GRASS)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.JUNGLE)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(0.95F)
				.downfall(0.9F)
				.effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build())
				.parent(null)
		);
		this.addStructureFeature(ConfiguredStructureFeatures.JUNGLE_PYRAMID);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(this);
		this.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_JUNGLE);
		DefaultBiomeFeatures.addLandCarvers(this);
		DefaultBiomeFeatures.addDefaultLakes(this);
		DefaultBiomeFeatures.addDungeons(this);
		DefaultBiomeFeatures.addMineables(this);
		DefaultBiomeFeatures.addDefaultOres(this);
		DefaultBiomeFeatures.addDefaultDisks(this);
		DefaultBiomeFeatures.addBambooJungleTrees(this);
		DefaultBiomeFeatures.addExtraDefaultFlowers(this);
		DefaultBiomeFeatures.addJungleGrass(this);
		DefaultBiomeFeatures.addDefaultMushrooms(this);
		DefaultBiomeFeatures.addDefaultVegetation(this);
		DefaultBiomeFeatures.addSprings(this);
		DefaultBiomeFeatures.addJungleVegetation(this);
		DefaultBiomeFeatures.addFrozenTopLayer(this);
		DefaultBiomeFeatures.addJungleMobs(this);
		this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.PARROT, 40, 1, 2));
		this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.PANDA, 80, 1, 2));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.OCELOT, 2, 1, 1));
	}
}
