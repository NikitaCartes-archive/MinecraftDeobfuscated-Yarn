package net.minecraft.world.biome;

import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public final class SnowyBeachBiome extends Biome {
	public SnowyBeachBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(ConfiguredSurfaceBuilders.DESERT)
				.precipitation(Biome.Precipitation.SNOW)
				.category(Biome.Category.BEACH)
				.depth(0.0F)
				.scale(0.025F)
				.temperature(0.05F)
				.downfall(0.3F)
				.effects(new BiomeEffects.Builder().waterColor(4020182).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build())
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
		DefaultBiomeFeatures.addBatsAndMonsters(this);
	}
}
