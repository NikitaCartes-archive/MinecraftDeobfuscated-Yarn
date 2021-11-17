package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_6834;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class BastionRemnantFeature extends JigsawFeature {
	private static final int STRUCTURE_START_Y = 33;

	public BastionRemnantFeature(Codec<StructurePoolFeatureConfig> configCodec) {
		super(configCodec, 33, false, false, BastionRemnantFeature::method_28617);
	}

	private static boolean method_28617(class_6834.class_6835<StructurePoolFeatureConfig> arg) {
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setCarverSeed(arg.seed(), arg.chunkPos().x, arg.chunkPos().z);
		return chunkRandom.nextInt(5) >= 2;
	}
}
