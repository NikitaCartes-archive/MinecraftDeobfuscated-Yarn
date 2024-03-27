package net.minecraft.world.biome.source;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;

public class BiomeSources {
	public static MapCodec<? extends BiomeSource> registerAndGetDefault(Registry<MapCodec<? extends BiomeSource>> registry) {
		Registry.register(registry, "fixed", FixedBiomeSource.CODEC);
		Registry.register(registry, "multi_noise", MultiNoiseBiomeSource.CODEC);
		Registry.register(registry, "checkerboard", CheckerboardBiomeSource.CODEC);
		return Registry.register(registry, "the_end", TheEndBiomeSource.CODEC);
	}
}
