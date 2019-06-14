package net.minecraft.world.biome;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.DoublePlantFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class SunflowerPlainsBiome extends Biome {
	protected SunflowerPlainsBiome() {
		super(
			new Biome.Settings()
				.method_8737(SurfaceBuilder.field_15701, SurfaceBuilder.field_15677)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.field_9355)
				.depth(0.125F)
				.scale(0.05F)
				.temperature(0.8F)
				.downfall(0.4F)
				.waterColor(4159204)
				.waterFogColor(329011)
				.parent("plains")
		);
		this.method_8710(Feature.field_13547, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.field_13692));
		this.method_8710(Feature.field_13565, FeatureConfig.field_13603);
		DefaultBiomeFeatures.addLandCarvers(this);
		DefaultBiomeFeatures.addDefaultStructures(this);
		DefaultBiomeFeatures.addDefaultLakes(this);
		DefaultBiomeFeatures.addDungeons(this);
		DefaultBiomeFeatures.addPlainsTallGrass(this);
		this.method_8719(
			GenerationStep.Feature.field_13178,
			method_8699(Feature.field_13576, new DoublePlantFeatureConfig(Blocks.field_10583.method_9564()), Decorator.field_14253, new CountDecoratorConfig(10))
		);
		DefaultBiomeFeatures.addMineables(this);
		DefaultBiomeFeatures.addDefaultOres(this);
		DefaultBiomeFeatures.addDefaultDisks(this);
		DefaultBiomeFeatures.addPlainsFeatures(this);
		this.method_8719(
			GenerationStep.Feature.field_13178, method_8699(Feature.field_13583, FeatureConfig.field_13603, Decorator.field_14240, new CountDecoratorConfig(10))
		);
		DefaultBiomeFeatures.addDefaultMushrooms(this);
		this.method_8719(
			GenerationStep.Feature.field_13178, method_8699(Feature.field_13524, FeatureConfig.field_13603, Decorator.field_14263, new ChanceDecoratorConfig(32))
		);
		DefaultBiomeFeatures.addSprings(this);
		DefaultBiomeFeatures.addFrozenTopLayer(this);
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.field_6115, 12, 4, 4));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.field_6093, 10, 4, 4));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.field_6132, 10, 4, 4));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.field_6085, 8, 4, 4));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.field_6139, 5, 2, 6));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.field_6067, 1, 1, 3));
		this.addSpawn(EntityCategory.field_6303, new Biome.SpawnEntry(EntityType.field_6108, 10, 8, 8));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6079, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6051, 95, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6054, 5, 1, 1));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6137, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6046, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6069, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6091, 10, 1, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6145, 5, 1, 1));
	}
}
