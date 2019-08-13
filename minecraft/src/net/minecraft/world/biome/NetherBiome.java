package net.minecraft.world.biome;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.decorator.ChanceRangeDecoratorConfig;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.BushFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.NetherSpringFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.SpringFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class NetherBiome extends Biome {
	protected NetherBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.field_15693, SurfaceBuilder.NETHER_CONFIG)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.field_9366)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(2.0F)
				.downfall(0.0F)
				.waterColor(4159204)
				.waterFogColor(329011)
				.parent(null)
		);
		this.addStructureFeature(Feature.NETHER_BRIDGE, FeatureConfig.DEFAULT);
		this.addCarver(GenerationStep.Carver.field_13169, configureCarver(Carver.field_13297, new ProbabilityConfig(0.2F)));
		this.addFeature(
			GenerationStep.Feature.field_13178,
			configureFeature(
				Feature.field_13513, new SpringFeatureConfig(Fluids.LAVA.getDefaultState()), Decorator.field_14266, new RangeDecoratorConfig(20, 8, 16, 256)
			)
		);
		DefaultBiomeFeatures.addDefaultMushrooms(this);
		this.addFeature(
			GenerationStep.Feature.field_13177, configureFeature(Feature.NETHER_BRIDGE, FeatureConfig.DEFAULT, Decorator.field_14250, DecoratorConfig.DEFAULT)
		);
		this.addFeature(
			GenerationStep.Feature.field_13177,
			configureFeature(Feature.field_13563, new NetherSpringFeatureConfig(false), Decorator.field_14241, new RangeDecoratorConfig(8, 4, 8, 128))
		);
		this.addFeature(
			GenerationStep.Feature.field_13177, configureFeature(Feature.field_13523, FeatureConfig.DEFAULT, Decorator.field_14235, new CountDecoratorConfig(10))
		);
		this.addFeature(
			GenerationStep.Feature.field_13177, configureFeature(Feature.field_13568, FeatureConfig.DEFAULT, Decorator.field_14256, new CountDecoratorConfig(10))
		);
		this.addFeature(
			GenerationStep.Feature.field_13177,
			configureFeature(Feature.field_13568, FeatureConfig.DEFAULT, Decorator.field_14241, new RangeDecoratorConfig(10, 0, 0, 128))
		);
		this.addFeature(
			GenerationStep.Feature.field_13177,
			configureFeature(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10251.getDefaultState()), Decorator.field_14248, new ChanceRangeDecoratorConfig(0.5F, 0, 0, 128)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13177,
			configureFeature(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10559.getDefaultState()), Decorator.field_14248, new ChanceRangeDecoratorConfig(0.5F, 0, 0, 128)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13177,
			configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.field_13727, Blocks.field_10213.getDefaultState(), 14),
				Decorator.field_14241,
				new RangeDecoratorConfig(16, 10, 20, 128)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13177,
			configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.field_13727, Blocks.field_10092.getDefaultState(), 33),
				Decorator.field_14244,
				new CountDecoratorConfig(4)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13177,
			configureFeature(Feature.field_13563, new NetherSpringFeatureConfig(true), Decorator.field_14241, new RangeDecoratorConfig(16, 10, 20, 128))
		);
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6107, 50, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6050, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6102, 2, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6091, 1, 4, 4));
	}
}
