package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class BastionRemnantFeature extends JigsawFeature {
	private static final int STRUCTURE_START_Y = 33;

	public BastionRemnantFeature(Codec<StructurePoolFeatureConfig> configCodec) {
		super(configCodec, 33, false, false, BastionRemnantFeature::canGenerate);
	}

	private static boolean canGenerate(StructureGeneratorFactory.Context<StructurePoolFeatureConfig> context) {
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setCarverSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
		return chunkRandom.nextInt(5) >= 2;
	}
}
