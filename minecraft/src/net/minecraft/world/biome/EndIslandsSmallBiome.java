package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class EndIslandsSmallBiome extends Biome {
	public EndIslandsSmallBiome() {
		super(
			new Biome.Settings()
				.method_8737(SurfaceBuilder.DEFAULT, SurfaceBuilder.field_15671)
				.precipitation(Biome.Precipitation.NONE)
				.method_8738(Biome.Category.THE_END)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(0.5F)
				.downfall(0.5F)
				.waterColor(4159204)
				.waterFogColor(329011)
				.parent(null)
		);
		this.method_8719(
			GenerationStep.Feature.RAW_GENERATION, method_8699(Feature.field_13574, FeatureConfig.field_13603, Decorator.field_14251, DecoratorConfig.field_13436)
		);
		this.method_8719(
			GenerationStep.Feature.SURFACE_STRUCTURES, method_8699(Feature.field_13553, FeatureConfig.field_13603, Decorator.NOPE, DecoratorConfig.field_13436)
		);
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 4, 4));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getSkyColor(float f) {
		return 0;
	}
}
