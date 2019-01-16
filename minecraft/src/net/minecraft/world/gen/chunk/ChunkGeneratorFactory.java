package net.minecraft.world.gen.chunk;

import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;

interface ChunkGeneratorFactory<C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> {
	T create(World world, BiomeSource biomeSource, C chunkGeneratorConfig);
}
