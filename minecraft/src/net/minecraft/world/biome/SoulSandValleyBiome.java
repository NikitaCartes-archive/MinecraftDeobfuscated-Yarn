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

public class SoulSandValleyBiome extends Biome {
	public SoulSandValleyBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(ConfiguredSurfaceBuilders.SOUL_SAND_VALLEY)
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
						.particleConfig(new BiomeParticleConfig(ParticleTypes.ASH, 0.00625F))
						.loopSound(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP)
						.moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD, 6000, 8, 2.0))
						.additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS, 0.0111))
						.music(MusicType.method_27283(SoundEvents.MUSIC_NETHER_SOUL_SAND_VALLEY))
						.build()
				)
				.parent(null)
		);
		this.addStructureFeature(ConfiguredStructureFeatures.FORTRESS);
		this.addStructureFeature(ConfiguredStructureFeatures.NETHER_FOSSIL);
		this.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER);
		this.addStructureFeature(ConfiguredStructureFeatures.BASTION_REMNANT);
		this.addCarver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE);
		this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA);
		this.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ConfiguredFeatures.BASALT_PILLAR);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_OPEN);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_CRIMSON_ROOTS);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_SOUL_FIRE);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED);
		this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_SOUL_SAND);
		DefaultBiomeFeatures.addNetherMineables(this);
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SKELETON, 20, 5, 5));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.GHAST, 50, 4, 4));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4));
		this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.STRIDER, 60, 1, 2));
		double d = 0.7;
		double e = 0.15;
		this.addSpawnDensity(EntityType.SKELETON, 0.7, 0.15);
		this.addSpawnDensity(EntityType.GHAST, 0.7, 0.15);
		this.addSpawnDensity(EntityType.ENDERMAN, 0.7, 0.15);
		this.addSpawnDensity(EntityType.STRIDER, 0.7, 0.15);
	}
}
