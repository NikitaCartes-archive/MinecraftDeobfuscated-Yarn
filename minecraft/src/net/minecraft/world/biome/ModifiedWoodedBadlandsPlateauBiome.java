package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class ModifiedWoodedBadlandsPlateauBiome extends Biome {
	public ModifiedWoodedBadlandsPlateauBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.WOODED_BADLANDS, SurfaceBuilder.BADLANDS_CONFIG)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.MESA)
				.depth(0.45F)
				.scale(0.3F)
				.temperature(2.0F)
				.downfall(0.0F)
				.effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build())
				.parent("wooded_badlands_plateau")
		);
		DefaultBiomeFeatures.addBadlandsUndergroundStructures(this);
		this.addStructureFeature(DefaultBiomeFeatures.MOUNTAIN_RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(this);
		DefaultBiomeFeatures.addDefaultLakes(this);
		DefaultBiomeFeatures.addDungeons(this);
		DefaultBiomeFeatures.addMineables(this);
		DefaultBiomeFeatures.addDefaultOres(this);
		DefaultBiomeFeatures.addExtraGoldOre(this);
		DefaultBiomeFeatures.addDefaultDisks(this);
		DefaultBiomeFeatures.addBadlandsPlateauTrees(this);
		DefaultBiomeFeatures.addBadlandsGrass(this);
		DefaultBiomeFeatures.addDefaultMushrooms(this);
		DefaultBiomeFeatures.addBadlandsVegetation(this);
		DefaultBiomeFeatures.addSprings(this);
		DefaultBiomeFeatures.addFrozenTopLayer(this);
		this.addSpawn(SpawnGroup.AMBIENT, new Biome.SpawnEntry(EntityType.BAT, 10, 8, 8));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE, 95, 4, 4));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SKELETON, 100, 4, 4));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SLIME, 100, 4, 4));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.WITCH, 5, 1, 1));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getFoliageColor() {
		return 10387789;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getGrassColorAt(double x, double z) {
		return 9470285;
	}
}
