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
				.effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(10518688).build())
				.parent(null)
		);
		this.addStructureFeature(Feature.END_CITY.configure(FeatureConfig.DEFAULT));
		this.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Feature.END_GATEWAY
				.configure(EndGatewayFeatureConfig.createConfig(TheEndDimension.SPAWN_POINT, true))
				.createDecoratedFeature(Decorator.END_GATEWAY.configure(DecoratorConfig.DEFAULT))
		);
		DefaultBiomeFeatures.addEndCities(this);
		this.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.CHORUS_PLANT.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.CHORUS_PLANT.configure(DecoratorConfig.DEFAULT))
		);
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 4, 4));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getSkyColor() {
		return 0;
	}
}
