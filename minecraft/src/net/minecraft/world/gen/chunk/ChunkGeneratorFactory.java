package net.minecraft.world.gen.chunk;

import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;

interface ChunkGeneratorFactory<C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> {
	T create(IWorld world, BiomeSource biomeSource, C config);
}
