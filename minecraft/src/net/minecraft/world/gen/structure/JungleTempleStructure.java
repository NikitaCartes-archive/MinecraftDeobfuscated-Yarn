package net.minecraft.world.gen.structure;

import com.mojang.serialization.Codec;
import net.minecraft.structure.JungleTempleGenerator;

public class JungleTempleStructure extends BasicTempleStructure {
	public static final Codec<JungleTempleStructure> CODEC = createCodec(JungleTempleStructure::new);

	public JungleTempleStructure(StructureType.Config config) {
		super(JungleTempleGenerator::new, 12, 15, config);
	}

	@Override
	public net.minecraft.structure.StructureType<?> getType() {
		return net.minecraft.structure.StructureType.JUNGLE_TEMPLE;
	}
}
