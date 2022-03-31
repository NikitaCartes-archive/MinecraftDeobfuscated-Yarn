package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.DesertTempleGenerator;
import net.minecraft.structure.StructureType;

public class DesertPyramidFeature extends BasicTempleStructureFeature {
	public static final Codec<DesertPyramidFeature> CODEC = createCodec(DesertPyramidFeature::new);

	public DesertPyramidFeature(StructureFeature.Config config) {
		super(DesertTempleGenerator::new, 21, 21, config);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.DESERT_PYRAMID;
	}
}
