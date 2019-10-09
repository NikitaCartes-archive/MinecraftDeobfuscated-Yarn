package net.minecraft.world.biome;

import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class MushroomFieldShoreBiome extends Biome {
	public MushroomFieldShoreBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.MYCELIUM_CONFIG)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.MUSHROOM)
				.depth(0.0F)
				.scale(0.025F)
				.temperature(0.9F)
				.downfall(1.0F)
				.waterColor(4159204)
				.waterFogColor(329011)
				.parent(null)
		);
		this.addStructureFeature(Feature.MINESHAFT.method_23397(new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL)));
		this.addStructureFeature(Feature.STRONGHOLD.method_23397(FeatureConfig.DEFAULT));
		DefaultBiomeFeatures.addLandCarvers(this);
		DefaultBiomeFeatures.addDefaultStructures(this);
		DefaultBiomeFeatures.addDefaultLakes(this);
		DefaultBiomeFeatures.addDungeons(this);
		DefaultBiomeFeatures.addMineables(this);
		DefaultBiomeFeatures.addDefaultOres(this);
		DefaultBiomeFeatures.addDefaultDisks(this);
		DefaultBiomeFeatures.addMushroomFieldsFeatures(this);
		DefaultBiomeFeatures.addDefaultMushrooms(this);
		DefaultBiomeFeatures.addDefaultVegetation(this);
		DefaultBiomeFeatures.addSprings(this);
		DefaultBiomeFeatures.addFrozenTopLayer(this);
		this.addSpawn(EntityCategory.CREATURE, new Biome.SpawnEntry(EntityType.MOOSHROOM, 8, 4, 8));
		this.addSpawn(EntityCategory.AMBIENT, new Biome.SpawnEntry(EntityType.BAT, 10, 8, 8));
	}
}
