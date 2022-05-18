package net.minecraft.world.gen.structure;

import com.mojang.serialization.Codec;
import net.minecraft.structure.DesertTempleGenerator;

public class DesertPyramidStructure extends BasicTempleStructure {
	public static final Codec<DesertPyramidStructure> CODEC = createCodec(DesertPyramidStructure::new);

	public DesertPyramidStructure(StructureType.Config config) {
		super(DesertTempleGenerator::new, 21, 21, config);
	}

	@Override
	public net.minecraft.structure.StructureType<?> getType() {
		return net.minecraft.structure.StructureType.DESERT_PYRAMID;
	}
}