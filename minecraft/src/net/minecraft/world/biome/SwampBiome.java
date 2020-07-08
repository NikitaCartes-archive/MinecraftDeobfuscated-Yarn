package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public final class SwampBiome extends Biome {
	public SwampBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(ConfiguredSurfaceBuilders.SWAMP)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.SWAMP)
				.depth(-0.2F)
				.scale(0.1F)
				.temperature(0.8F)
				.downfall(0.9F)
				.effects(new BiomeEffects.Builder().waterColor(6388580).waterFogColor(2302743).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build())
				.parent(null)
		);
		this.addStructureFeature(ConfiguredStructureFeatures.SWAMP_HUT);
		this.addStructureFeature(ConfiguredStructureFeatures.MINESHAFT);
		this.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_SWAMP);
		DefaultBiomeFeatures.addLandCarvers(this);
		DefaultBiomeFeatures.addFossils(this);
		DefaultBiomeFeatures.addDefaultLakes(this);
		DefaultBiomeFeatures.addDungeons(this);
		DefaultBiomeFeatures.addMineables(this);
		DefaultBiomeFeatures.addDefaultOres(this);
		DefaultBiomeFeatures.addClay(this);
		DefaultBiomeFeatures.addSwampFeatures(this);
		DefaultBiomeFeatures.addDefaultMushrooms(this);
		DefaultBiomeFeatures.addSwampVegetation(this);
		DefaultBiomeFeatures.addSprings(this);
		this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEAGRASS_SWAMP);
		DefaultBiomeFeatures.addFrozenTopLayer(this);
		DefaultBiomeFeatures.addFarmAnimals(this);
		DefaultBiomeFeatures.addBatsAndMonsters(this);
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SLIME, 1, 1, 1));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getGrassColorAt(double x, double z) {
		double d = FOLIAGE_NOISE.sample(x * 0.0225, z * 0.0225, false);
		return d < -0.1 ? 5011004 : 6975545;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getFoliageColor() {
		return 6975545;
	}
}
