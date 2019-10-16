package net.minecraft.world.biome;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.decorator.ChanceRangeDecoratorConfig;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class NetherBiome extends Biome {
	protected NetherBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.NETHER, SurfaceBuilder.NETHER_CONFIG)
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
		this.addStructureFeature(Feature.NETHER_BRIDGE.configure(FeatureConfig.DEFAULT));
		this.addCarver(GenerationStep.Carver.AIR, configureCarver(Carver.HELL_CAVE, new ProbabilityConfig(0.2F)));
		this.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.SPRING_FEATURE
				.configure(DefaultBiomeFeatures.field_21112)
				.createDecoratedFeature(Decorator.COUNT_VERY_BIASED_RANGE.configure(new RangeDecoratorConfig(20, 8, 16, 256)))
		);
		DefaultBiomeFeatures.addDefaultMushrooms(this);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.NETHER_BRIDGE.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.SPRING_FEATURE
				.configure(DefaultBiomeFeatures.field_21113)
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(8, 4, 8, 128)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.RANDOM_PATCH.configure(DefaultBiomeFeatures.field_21094).createDecoratedFeature(Decorator.HELL_FIRE.configure(new CountDecoratorConfig(10)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.GLOWSTONE_BLOB.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.LIGHT_GEM_CHANCE.configure(new CountDecoratorConfig(10)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.GLOWSTONE_BLOB.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(10, 0, 0, 128)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.RANDOM_PATCH
				.configure(DefaultBiomeFeatures.field_21097)
				.createDecoratedFeature(Decorator.CHANCE_RANGE.configure(new ChanceRangeDecoratorConfig(0.5F, 0, 0, 128)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.RANDOM_PATCH
				.configure(DefaultBiomeFeatures.field_21096)
				.createDecoratedFeature(Decorator.CHANCE_RANGE.configure(new ChanceRangeDecoratorConfig(0.5F, 0, 0, 128)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NETHERRACK, Blocks.NETHER_QUARTZ_ORE.getDefaultState(), 14))
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(16, 10, 20, 128)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.ORE
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.NETHERRACK, Blocks.MAGMA_BLOCK.getDefaultState(), 33))
				.createDecoratedFeature(Decorator.MAGMA.configure(new CountDecoratorConfig(4)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.SPRING_FEATURE
				.configure(DefaultBiomeFeatures.field_21141)
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(16, 10, 20, 128)))
		);
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.GHAST, 50, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE_PIGMAN, 100, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 2, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4));
	}
}
