package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.SeagrassFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class SwampBiome extends Biome {
	protected SwampBiome() {
		super(
			new Biome.Settings()
				.method_8737(SurfaceBuilder.SWAMP, SurfaceBuilder.field_15677)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.SWAMP)
				.depth(-0.2F)
				.scale(0.1F)
				.temperature(0.8F)
				.downfall(0.9F)
				.waterColor(6388580)
				.waterFogColor(2302743)
				.parent(null)
		);
		this.method_8710(Feature.SWAMP_HUT, FeatureConfig.field_13603);
		this.method_8710(Feature.MINESHAFT, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL));
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
		this.addFeature(
			GenerationStep.Feature.field_13178, method_8699(Feature.field_13567, new SeagrassFeatureConfig(64, 0.6), Decorator.field_14231, DecoratorConfig.field_13436)
		);
		DefaultBiomeFeatures.addFossils(this);
		DefaultBiomeFeatures.addFrozenTopLayer(this);
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.SHEEP, 12, 4, 4));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.PIG, 10, 4, 4));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.CHICKEN, 10, 4, 4));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.COW, 8, 4, 4));
		this.addSpawn(EntityCategory.field_6303, new Biome.SpawnEntry(EntityType.BAT, 10, 8, 8));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.ZOMBIE, 95, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.SKELETON, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.SLIME, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.WITCH, 5, 1, 1));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.SLIME, 1, 1, 1));
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
