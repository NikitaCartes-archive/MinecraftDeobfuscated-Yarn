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
				.method_8737(SurfaceBuilder.NETHER, SurfaceBuilder.field_15690)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.NETHER)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(2.0F)
				.downfall(0.0F)
				.waterColor(4159204)
				.waterFogColor(329011)
				.parent(null)
		);
		this.method_8710(Feature.NETHER_BRIDGE, FeatureConfig.field_13603);
		this.addCarver(GenerationStep.Carver.field_13169, method_8714(Carver.NETHER_CAVE, new ProbabilityConfig(0.2F)));
		this.addFeature(
			GenerationStep.Feature.field_13178,
			method_8699(Feature.field_13513, new SpringFeatureConfig(Fluids.LAVA.getDefaultState()), Decorator.field_14266, new RangeDecoratorConfig(20, 8, 16, 256))
		);
		DefaultBiomeFeatures.addDefaultMushrooms(this);
		this.addFeature(
			GenerationStep.Feature.field_13177, method_8699(Feature.NETHER_BRIDGE, FeatureConfig.field_13603, Decorator.NOPE, DecoratorConfig.field_13436)
		);
		this.addFeature(
			GenerationStep.Feature.field_13177,
			method_8699(Feature.field_13563, new NetherSpringFeatureConfig(false), Decorator.field_14241, new RangeDecoratorConfig(8, 4, 8, 128))
		);
		this.addFeature(
			GenerationStep.Feature.field_13177, method_8699(Feature.field_13523, FeatureConfig.field_13603, Decorator.field_14235, new CountDecoratorConfig(10))
		);
		this.addFeature(
			GenerationStep.Feature.field_13177, method_8699(Feature.field_13568, FeatureConfig.field_13603, Decorator.field_14256, new CountDecoratorConfig(10))
		);
		this.addFeature(
			GenerationStep.Feature.field_13177,
			method_8699(Feature.field_13568, FeatureConfig.field_13603, Decorator.field_14241, new RangeDecoratorConfig(10, 0, 0, 128))
		);
		this.addFeature(
			GenerationStep.Feature.field_13177,
			method_8699(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10251.getDefaultState()), Decorator.field_14248, new ChanceRangeDecoratorConfig(0.5F, 0, 0, 128)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13177,
			method_8699(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10559.getDefaultState()), Decorator.field_14248, new ChanceRangeDecoratorConfig(0.5F, 0, 0, 128)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13177,
			method_8699(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NETHERRACK, Blocks.field_10213.getDefaultState(), 14),
				Decorator.field_14241,
				new RangeDecoratorConfig(16, 10, 20, 128)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13177,
			method_8699(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NETHERRACK, Blocks.field_10092.getDefaultState(), 33),
				Decorator.field_14244,
				new CountDecoratorConfig(4)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13177,
			method_8699(Feature.field_13563, new NetherSpringFeatureConfig(true), Decorator.field_14241, new RangeDecoratorConfig(16, 10, 20, 128))
		);
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.GHAST, 50, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.ZOMBIE_PIGMAN, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 2, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4));
	}
}
