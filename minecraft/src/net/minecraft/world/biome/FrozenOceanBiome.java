package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class FrozenOceanBiome extends Biome {
	protected static final OctaveSimplexNoiseSampler field_9487 = new OctaveSimplexNoiseSampler(new ChunkRandom(3456L), ImmutableList.of(-2, -1, 0));

	public FrozenOceanBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.FROZEN_OCEAN, SurfaceBuilder.GRASS_CONFIG)
				.precipitation(Biome.Precipitation.SNOW)
				.category(Biome.Category.OCEAN)
				.depth(-1.0F)
				.scale(0.1F)
				.temperature(0.0F)
				.downfall(0.5F)
				.effects(new BiomeEffects.Builder().waterColor(3750089).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build())
				.parent(null)
		);
		this.addStructureFeature(DefaultBiomeFeatures.COLD_OCEAN_RUIN);
		DefaultBiomeFeatures.addOceanStructures(this);
		this.addStructureFeature(DefaultBiomeFeatures.OCEAN_RUINED_PORTAL);
		DefaultBiomeFeatures.addOceanCarvers(this);
		DefaultBiomeFeatures.addDefaultLakes(this);
		DefaultBiomeFeatures.addIcebergs(this);
		DefaultBiomeFeatures.addDungeons(this);
		DefaultBiomeFeatures.addBlueIce(this);
		DefaultBiomeFeatures.addMineables(this);
		DefaultBiomeFeatures.addDefaultOres(this);
		DefaultBiomeFeatures.addDefaultDisks(this);
		DefaultBiomeFeatures.addWaterBiomeOakTrees(this);
		DefaultBiomeFeatures.addDefaultFlowers(this);
		DefaultBiomeFeatures.addDefaultGrass(this);
		DefaultBiomeFeatures.addDefaultMushrooms(this);
		DefaultBiomeFeatures.addDefaultVegetation(this);
		DefaultBiomeFeatures.addSprings(this);
		DefaultBiomeFeatures.addFrozenTopLayer(this);
		this.addSpawn(SpawnGroup.WATER_CREATURE, new Biome.SpawnEntry(EntityType.SQUID, 1, 1, 4));
		this.addSpawn(SpawnGroup.WATER_AMBIENT, new Biome.SpawnEntry(EntityType.SALMON, 15, 1, 5));
		this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.POLAR_BEAR, 1, 1, 2));
		this.addSpawn(SpawnGroup.AMBIENT, new Biome.SpawnEntry(EntityType.BAT, 10, 8, 8));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE, 95, 4, 4));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SKELETON, 100, 4, 4));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SLIME, 100, 4, 4));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.WITCH, 5, 1, 1));
	}

	@Override
	protected float computeTemperature(BlockPos blockPos) {
		float f = this.getTemperature();
		double d = field_9487.sample((double)blockPos.getX() * 0.05, (double)blockPos.getZ() * 0.05, false) * 7.0;
		double e = FOLIAGE_NOISE.sample((double)blockPos.getX() * 0.2, (double)blockPos.getZ() * 0.2, false);
		double g = d + e;
		if (g < 0.3) {
			double h = FOLIAGE_NOISE.sample((double)blockPos.getX() * 0.09, (double)blockPos.getZ() * 0.09, false);
			if (h < 0.8) {
				f = 0.2F;
			}
		}

		if (blockPos.getY() > 64) {
			float i = (float)(TEMPERATURE_NOISE.sample((double)((float)blockPos.getX() / 8.0F), (double)((float)blockPos.getZ() / 8.0F), false) * 4.0);
			return f - (i + (float)blockPos.getY() - 64.0F) * 0.05F / 30.0F;
		} else {
			return f;
		}
	}
}
