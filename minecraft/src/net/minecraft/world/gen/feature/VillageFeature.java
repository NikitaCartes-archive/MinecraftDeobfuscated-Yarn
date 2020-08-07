package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;

public class VillageFeature extends JigsawFeature {
	public VillageFeature(Codec<StructurePoolFeatureConfig> codec) {
		super(codec, 0, true, true);
	}
}
