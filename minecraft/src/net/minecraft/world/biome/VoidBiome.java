package net.minecraft.world.biome;

import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class VoidBiome extends Biome {
	public VoidBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.field_15683, SurfaceBuilder.STONE_CONFIG)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.field_9371)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(0.5F)
				.downfall(0.5F)
				.waterColor(4159204)
				.waterFogColor(329011)
				.parent(null)
		);
		this.addFeature(
			GenerationStep.Feature.field_13179, configureFeature(Feature.field_13591, FeatureConfig.DEFAULT, Decorator.field_14250, DecoratorConfig.DEFAULT)
		);
	}
}
