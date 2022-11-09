package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registry;

public class ChunkGenerators {
	public static Codec<? extends ChunkGenerator> registerAndGetDefault(Registry<Codec<? extends ChunkGenerator>> registry) {
		Registry.register(registry, "noise", NoiseChunkGenerator.CODEC);
		Registry.register(registry, "flat", FlatChunkGenerator.CODEC);
		return Registry.register(registry, "debug", DebugChunkGenerator.CODEC);
	}
}
