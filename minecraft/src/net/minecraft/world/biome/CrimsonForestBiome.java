package net.minecraft.world.biome;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class CrimsonForestBiome extends Biome {
	protected CrimsonForestBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.NETHER_FOREST, SurfaceBuilder.CRIMSON_NYLIUM_CONFIG)
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
						.fogColor(3343107)
						.particleConfig(new BiomeParticleConfig(ParticleTypes.CRIMSON_SPORE, 0.025F, 1.0E-6F, 1.0E-4F, 1.0E-6F))
						.loopSound(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
						.moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD, 6000, 8, 2.0))
						.additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS, 0.0111))
						.build()
				)
				.parent(null)
		);
		this.addCarver(GenerationStep.Carver.AIR, configureCarver(Carver.NETHER_CAVE, new ProbabilityConfig(0.2F)));
		this.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.SPRING_FEATURE
				.configure(DefaultBiomeFeatures.LAVA_SPRING_CONFIG)
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
				.configure(DefaultBiomeFeatures.NETHER_SPRING_CONFIG)
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(8, 4, 8, 128)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.RANDOM_PATCH.configure(DefaultBiomeFeatures.NETHER_FIRE_CONFIG).createDecoratedFeature(Decorator.HELL_FIRE.configure(new CountDecoratorConfig(10)))
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
		this.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.WEEPING_VINES.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(10, 0, 0, 128)))
		);
		DefaultBiomeFeatures.addCrimsonForestVegetation(this);
		DefaultBiomeFeatures.addNetherOres(this);
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 1, 2, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.HOGLIN, 9, 3, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.PIGLIN, 5, 3, 4));
		this.addSpawn(EntityCategory.CREATURE, new Biome.SpawnEntry(EntityType.STRIDER, 60, 2, 4));
	}
}
