package net.minecraft.world.biome;

import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public class BasaltDeltasBiome extends Biome {
	public BasaltDeltasBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(ConfiguredSurfaceBuilders.BASALT_DELTAS)
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
						.particleConfig(new BiomeParticleConfig(ParticleTypes.WHITE_ASH, 0.118093334F))
						.loopSound(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP)
						.moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_BASALT_DELTAS_MOOD, 6000, 8, 2.0))
						.additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_BASALT_DELTAS_ADDITIONS, 0.0111))
						.music(MusicType.method_27283(SoundEvents.MUSIC_NETHER_BASALT_DELTAS))
						.build()
				)
				.parent(null)
		);
		this.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER);
		this.addCarver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE);
		this.addStructureFeature(ConfiguredStructureFeatures.FORTRESS);
		this.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.DELTA);
		this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA_DOUBLE);
		this.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.SMALL_BASALT_COLUMNS);
		this.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.LARGE_BASALT_COLUMNS);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.BASALT_BLOBS);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.BLACKSTONE_BLOBS);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_DELTA);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_SOUL_FIRE);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.BROWN_MUSHROOM_NETHER);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.RED_MUSHROOM_NETHER);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED_DOUBLE);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_GOLD_DELTAS);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_QUARTZ_DELTAS);
		DefaultBiomeFeatures.addAncientDebris(this);
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.GHAST, 40, 1, 1));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 100, 2, 5));
		this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.STRIDER, 60, 1, 2));
	}
}
