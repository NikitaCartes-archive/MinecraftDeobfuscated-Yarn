package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class EndHighlandsBiome extends Biome {
	public EndHighlandsBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.END_CONFIG)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.THEEND)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(0.5F)
				.downfall(0.5F)
				.waterColor(4159204)
				.waterFogColor(329011)
				.parent(null)
		);
		this.addStructureFeature(Feature.END_CITY, FeatureConfig.DEFAULT);
		this.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			configureFeature(
				Feature.END_GATEWAY, EndGatewayFeatureConfig.createConfig(TheEndDimension.SPAWN_POINT, true), Decorator.END_GATEWAY, DecoratorConfig.DEFAULT
			)
		);
		DefaultBiomeFeatures.method_20826(this);
		this.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION, configureFeature(Feature.CHORUS_PLANT, FeatureConfig.DEFAULT, Decorator.CHORUS_PLANT, DecoratorConfig.DEFAULT)
		);
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 4, 4));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getSkyColor(float temperature) {
		return 0;
	}
}
