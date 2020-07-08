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

public class CrimsonForestBiome extends Biome {
	public CrimsonForestBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(ConfiguredSurfaceBuilders.CRIMSON_FOREST)
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
						.particleConfig(new BiomeParticleConfig(ParticleTypes.CRIMSON_SPORE, 0.025F))
						.loopSound(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
						.moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD, 6000, 8, 2.0))
						.additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS, 0.0111))
						.music(MusicType.method_27283(SoundEvents.MUSIC_NETHER_CRIMSON_FOREST))
						.build()
				)
				.parent(null)
		);
		this.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER);
		this.addCarver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE);
		this.addStructureFeature(ConfiguredStructureFeatures.FORTRESS);
		this.addStructureFeature(ConfiguredStructureFeatures.BASTION_REMNANT);
		this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA);
		DefaultBiomeFeatures.addDefaultMushrooms(this);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_OPEN);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED);
		this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.WEEPING_VINES);
		this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.CRIMSON_FUNGI);
		this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.CRIMSON_FOREST_VEGETATION);
		DefaultBiomeFeatures.addNetherMineables(this);
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 1, 2, 4));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.HOGLIN, 9, 3, 4));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.PIGLIN, 5, 3, 4));
		this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.STRIDER, 60, 1, 2));
	}
}
