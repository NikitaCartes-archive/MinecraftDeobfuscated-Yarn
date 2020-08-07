package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BastionRemnantFeature extends JigsawFeature {
	public BastionRemnantFeature(Codec<StructurePoolFeatureConfig> codec) {
		super(codec, 33, false, false);
	}

	protected boolean shouldStartAt(
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		long l,
		ChunkRandom chunkRandom,
		int i,
		int j,
		Biome biome,
		ChunkPos chunkPos,
		StructurePoolFeatureConfig structurePoolFeatureConfig
	) {
		return chunkRandom.nextInt(5) >= 2;
	}
}
