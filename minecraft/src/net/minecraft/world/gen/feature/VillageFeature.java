package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_6834;

public class VillageFeature extends JigsawFeature {
	public VillageFeature(Codec<StructurePoolFeatureConfig> configCodec) {
		super(configCodec, 0, true, true, arg -> true);
	}
}
