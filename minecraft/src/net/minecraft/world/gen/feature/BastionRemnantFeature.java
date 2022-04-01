package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;

public class BastionRemnantFeature extends JigsawFeature {
	private static final int STRUCTURE_START_Y = 33;

	public BastionRemnantFeature(Codec<StructurePoolFeatureConfig> configCodec) {
		super(configCodec, 33, false, false, context -> true);
	}
}
