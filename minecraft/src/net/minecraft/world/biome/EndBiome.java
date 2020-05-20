package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class EndBiome extends Biome {
	public EndBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.END_CONFIG)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.THEEND)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(0.5F)
				.downfall(0.5F)
				.effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(10518688).moodSound(BiomeMoodSound.CAVE).build())
				.parent(null)
		);
		this.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Feature.END_SPIKE.configure(new EndSpikeFeatureConfig(false, ImmutableList.of(), null)));
		this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 4, 4));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getSkyColor() {
		return 0;
	}
}
