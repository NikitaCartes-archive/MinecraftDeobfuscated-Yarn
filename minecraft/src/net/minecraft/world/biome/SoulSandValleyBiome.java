package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleTypes;
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

public class SoulSandValleyBiome extends Biome {
	protected SoulSandValleyBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.SOUL_SAND_VALLEY, SurfaceBuilder.SOUL_SAND_CONFIG)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.NETHER)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(2.0F)
				.downfall(0.0F)
				.effects(
					new BiomeEffects.Builder()
						.waterColor(4159204)
						.waterFogColor(329011)
						.fogColor(1787717)
						.particleConfig(new BiomeParticleConfig(ParticleTypes.ASH, 0.00625F, random -> 0.0, random -> 0.0, random -> 0.0))
						.build()
				)
				.parent(null)
				.noises(ImmutableList.of(new Biome.MixedNoisePoint(0.0F, 0.0F, 0.0F, 0.5F, 1.0F)))
		);
		this.addStructureFeature(Feature.NETHER_BRIDGE.configure(FeatureConfig.DEFAULT));
		this.addStructureFeature(Feature.NETHER_FOSSIL.configure(FeatureConfig.DEFAULT));
		this.addCarver(GenerationStep.Carver.AIR, configureCarver(Carver.NETHER_CAVE, new ProbabilityConfig(0.2F)));
		this.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.SPRING_FEATURE
				.configure(DefaultBiomeFeatures.LAVA_SPRING_CONFIG)
				.createDecoratedFeature(Decorator.COUNT_VERY_BIASED_RANGE.configure(new RangeDecoratorConfig(20, 8, 16, 256)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.NETHER_BRIDGE.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.NETHER_FOSSIL.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.BASALT_PILLAR.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(10, 0, 0, 128)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.SPRING_FEATURE
				.configure(DefaultBiomeFeatures.NETHER_SPRING_CONFIG)
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(8, 4, 8, 128)))
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
				.configure(DefaultBiomeFeatures.SOUL_SAND_CRIMSON_ROOTS_CONFIG)
				.createDecoratedFeature(Decorator.CHANCE_RANGE.configure(new ChanceRangeDecoratorConfig(1.0F, 0, 0, 128)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.RANDOM_PATCH.configure(DefaultBiomeFeatures.NETHER_FIRE_CONFIG).createDecoratedFeature(Decorator.FIRE.configure(new CountDecoratorConfig(10)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.RANDOM_PATCH.configure(DefaultBiomeFeatures.SOUL_FIRE_CONFIG).createDecoratedFeature(Decorator.FIRE.configure(new CountDecoratorConfig(10)))
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
				.configure(DefaultBiomeFeatures.ENCLOSED_NETHER_SPRING_CONFIG)
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(16, 10, 20, 128)))
		);
		DefaultBiomeFeatures.addNetherOres(this);
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SKELETON, 2, 5, 5));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.GHAST, 50, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4));
	}
}
