package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.StructureGeneratorFactory;

public class VillageFeature extends JigsawFeature {
	public VillageFeature(Codec<StructurePoolFeatureConfig> configCodec) {
		super(configCodec, 0, true, true, context -> true);
	}
}
