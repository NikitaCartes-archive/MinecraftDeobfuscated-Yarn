package net.minecraft.world.biome;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public final class TaigaHillsBiome extends Biome {
	public TaigaHillsBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(ConfiguredSurfaceBuilders.GRASS)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.TAIGA)
				.depth(0.45F)
				.scale(0.3F)
				.temperature(0.25F)
				.downfall(0.8F)
				.effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build())
				.parent(null)
		);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(this);
		this.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(this);
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
		DefaultBiomeFeatures.addSweetBerryBushes(this);
		DefaultBiomeFeatures.addFrozenTopLayer(this);
		DefaultBiomeFeatures.addFarmAnimals(this);
		this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.WOLF, 8, 4, 4));
		this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
		this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.FOX, 8, 2, 4));
		DefaultBiomeFeatures.addBatsAndMonsters(this);
	}
}
