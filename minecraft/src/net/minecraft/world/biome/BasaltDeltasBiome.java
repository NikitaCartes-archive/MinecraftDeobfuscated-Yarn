package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
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
import net.minecraft.world.gen.decorator.ChanceRangeDecoratorConfig;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.RuinedPortalFeature;
import net.minecraft.world.gen.feature.RuinedPortalFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class BasaltDeltasBiome extends Biome {
	protected BasaltDeltasBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.BASALT_DELTAS, SurfaceBuilder.BASALT_DELTA_CONFIG)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.NETHER)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(2.0F)
				.downfall(0.0F)
				.effects(
					new BiomeEffects.Builder()
						.waterColor(4159204)
						.waterFogColor(4341314)
						.fogColor(6840176)
						.particleConfig(
							new BiomeParticleConfig(
								ParticleTypes.WHITE_ASH,
								0.118093334F,
								random -> (double)random.nextFloat() * -1.9 * (double)random.nextFloat() * 0.1,
								random -> (double)random.nextFloat() * -0.5 * (double)random.nextFloat() * 0.1 * 5.0,
								random -> (double)random.nextFloat() * -1.9 * (double)random.nextFloat() * 0.1
							)
						)
						.loopSound(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP)
						.moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_BASALT_DELTAS_MOOD, 6000, 8, 2.0))
						.additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_BASALT_DELTAS_ADDITIONS, 0.0111))
						.build()
				)
				.parent(null)
				.noises(ImmutableList.of(new Biome.MixedNoisePoint(0.5F, 0.0F, 0.0F, 0.0F, 1.0F)))
		);
		this.addStructureFeature(Feature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.NETHER)));
		this.addCarver(GenerationStep.Carver.AIR, configureCarver(Carver.NETHER_CAVE, new ProbabilityConfig(0.2F)));
		this.addStructureFeature(Feature.NETHER_BRIDGE.configure(FeatureConfig.DEFAULT));
		DefaultBiomeFeatures.addDefaultStructures(this);
		this.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Feature.DELTA_FEATURE.configure(DefaultBiomeFeatures.field_23858).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP.configure(new CountDecoratorConfig(40)))
		);
		this.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.SPRING_FEATURE
				.configure(DefaultBiomeFeatures.LAVA_SPRING_CONFIG)
				.createDecoratedFeature(Decorator.COUNT_VERY_BIASED_RANGE.configure(new RangeDecoratorConfig(40, 8, 16, 256)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.BASALT_COLUMNS.configure(DefaultBiomeFeatures.field_23854).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP.configure(new CountDecoratorConfig(4)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.BASALT_COLUMNS.configure(DefaultBiomeFeatures.field_23855).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP.configure(new CountDecoratorConfig(2)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.NETHERRACK_REPLACE_BLOBS
				.configure(DefaultBiomeFeatures.field_23856)
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(75, 0, 0, 128)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.NETHERRACK_REPLACE_BLOBS
				.configure(DefaultBiomeFeatures.field_23857)
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(25, 0, 0, 128)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.NETHER_BRIDGE.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.RUINED_PORTAL
				.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.NETHER))
				.createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.SPRING_FEATURE
				.configure(DefaultBiomeFeatures.field_23851)
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(16, 4, 8, 128)))
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
			Feature.GLOWSTONE_BLOB.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.LIGHT_GEM_CHANCE.configure(new CountDecoratorConfig(10)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.GLOWSTONE_BLOB.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(10, 0, 0, 128)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.RANDOM_PATCH
				.configure(DefaultBiomeFeatures.BROWN_MUSHROOM_CONFIG)
				.createDecoratedFeature(Decorator.CHANCE_RANGE.configure(new ChanceRangeDecoratorConfig(0.5F, 0, 0, 128)))
		);
		this.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Feature.RANDOM_PATCH
				.configure(DefaultBiomeFeatures.RED_MUSHROOM_CONFIG)
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
				.configure(DefaultBiomeFeatures.ENCLOSED_NETHER_SPRING_CONFIG)
				.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(32, 10, 20, 128)))
		);
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.GHAST, 40, 1, 1));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 100, 2, 5));
		this.addSpawn(EntityCategory.CREATURE, new Biome.SpawnEntry(EntityType.STRIDER, 60, 2, 4));
	}
}
