package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class BastionRemnantFeature extends JigsawFeature {
	private static final int STRUCTURE_START_Y = 33;

	public BastionRemnantFeature(Codec<StructurePoolFeatureConfig> configCodec) {
		super(configCodec, 33, false, false);
	}

	protected boolean shouldStartAt(
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		long l,
		ChunkPos chunkPos,
		StructurePoolFeatureConfig structurePoolFeatureConfig,
		HeightLimitView heightLimitView
	) {
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setCarverSeed(l, chunkPos.x, chunkPos.z);
		return chunkRandom.nextInt(5) >= 2;
	}
}
