package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public class EndMidlandsBiome extends Biome {
	public EndMidlandsBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(ConfiguredSurfaceBuilders.END)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.THEEND)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(0.5F)
				.downfall(0.5F)
				.effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(10518688).moodSound(BiomeMoodSound.CAVE).build())
				.parent(null)
		);
		this.addStructureFeature(ConfiguredStructureFeatures.END_CITY);
		DefaultBiomeFeatures.addEndMobs(this);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getSkyColor() {
		return 0;
	}
}
