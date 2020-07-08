package net.minecraft.world.biome;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public final class BeachBiome extends Biome {
	public BeachBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(ConfiguredSurfaceBuilders.DESERT)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.BEACH)
				.depth(0.0F)
				.scale(0.025F)
				.temperature(0.8F)
				.downfall(0.4F)
				.effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build())
				.parent(null)
		);
		this.addStructureFeature(ConfiguredStructureFeatures.MINESHAFT);
		this.addStructureFeature(ConfiguredStructureFeatures.BURIED_TREASURE);
		this.addStructureFeature(ConfiguredStructureFeatures.SHIPWRECK_BEACHED);
		this.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(this);
		DefaultBiomeFeatures.addDefaultLakes(this);
		DefaultBiomeFeatures.addDungeons(this);
		DefaultBiomeFeatures.addMineables(this);
		DefaultBiomeFeatures.addDefaultOres(this);
		DefaultBiomeFeatures.addDefaultDisks(this);
		DefaultBiomeFeatures.addDefaultFlowers(this);
		DefaultBiomeFeatures.addDefaultGrass(this);
		DefaultBiomeFeatures.addDefaultMushrooms(this);
		DefaultBiomeFeatures.addDefaultVegetation(this);
		DefaultBiomeFeatures.addSprings(this);
		DefaultBiomeFeatures.addFrozenTopLayer(this);
		this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.TURTLE, 5, 2, 5));
		DefaultBiomeFeatures.addBatsAndMonsters(this);
	}
}
