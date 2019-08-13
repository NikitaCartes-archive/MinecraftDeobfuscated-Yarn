package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class SwampHillsBiome extends Biome {
	protected SwampHillsBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.field_15681, SurfaceBuilder.GRASS_CONFIG)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.field_9364)
				.depth(-0.1F)
				.scale(0.3F)
				.temperature(0.8F)
				.downfall(0.9F)
				.waterColor(6388580)
				.waterFogColor(2302743)
				.parent("swamp")
		);
		this.addStructureFeature(Feature.MINESHAFT, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.field_13692));
		DefaultBiomeFeatures.addLandCarvers(this);
		DefaultBiomeFeatures.addDefaultStructures(this);
		DefaultBiomeFeatures.addDefaultLakes(this);
		DefaultBiomeFeatures.addDungeons(this);
		DefaultBiomeFeatures.addMineables(this);
		DefaultBiomeFeatures.addDefaultOres(this);
		DefaultBiomeFeatures.addClay(this);
		DefaultBiomeFeatures.addSwampFeatures(this);
		DefaultBiomeFeatures.addDefaultMushrooms(this);
		DefaultBiomeFeatures.addSwampVegetation(this);
		DefaultBiomeFeatures.addSprings(this);
		DefaultBiomeFeatures.addFossils(this);
		DefaultBiomeFeatures.addFrozenTopLayer(this);
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.field_6115, 12, 4, 4));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.field_6093, 10, 4, 4));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.field_6132, 10, 4, 4));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.field_6085, 8, 4, 4));
		this.addSpawn(EntityCategory.field_6303, new Biome.SpawnEntry(EntityType.field_6108, 10, 8, 8));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6079, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6051, 95, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6054, 5, 1, 1));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6137, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6046, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6069, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6091, 10, 1, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6145, 5, 1, 1));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6069, 1, 1, 1));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getGrassColorAt(BlockPos blockPos) {
		double d = FOLIAGE_NOISE.sample((double)blockPos.getX() * 0.0225, (double)blockPos.getZ() * 0.0225);
		return d < -0.1 ? 5011004 : 6975545;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getFoliageColorAt(BlockPos blockPos) {
		return 6975545;
	}
}
