package net.minecraft.world.biome;

import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public final class DesertHillsBiome extends Biome {
	public DesertHillsBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(ConfiguredSurfaceBuilders.DESERT)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.DESERT)
				.depth(0.45F)
				.scale(0.3F)
				.temperature(2.0F)
				.downfall(0.0F)
				.effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build())
				.parent(null)
		);
		this.addStructureFeature(ConfiguredStructureFeatures.DESERT_PYRAMID);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(this);
		this.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_DESERT);
		DefaultBiomeFeatures.addLandCarvers(this);
		DefaultBiomeFeatures.addDesertLakes(this);
		DefaultBiomeFeatures.addDungeons(this);
		DefaultBiomeFeatures.addMineables(this);
		DefaultBiomeFeatures.addDefaultOres(this);
		DefaultBiomeFeatures.addDefaultDisks(this);
		DefaultBiomeFeatures.addDefaultFlowers(this);
		DefaultBiomeFeatures.addDefaultGrass(this);
		DefaultBiomeFeatures.addDesertDeadBushes(this);
		DefaultBiomeFeatures.addDefaultMushrooms(this);
		DefaultBiomeFeatures.addDesertVegetation(this);
		DefaultBiomeFeatures.addSprings(this);
		DefaultBiomeFeatures.addDesertFeatures(this);
		DefaultBiomeFeatures.addFrozenTopLayer(this);
		DefaultBiomeFeatures.addDesertMobs(this);
	}
}
