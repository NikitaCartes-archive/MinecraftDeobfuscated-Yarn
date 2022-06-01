package net.minecraft.world.gen.structure;

import com.mojang.serialization.Codec;
import net.minecraft.structure.JungleTempleGenerator;

public class JungleTempleStructure extends BasicTempleStructure {
	public static final Codec<JungleTempleStructure> CODEC = createCodec(JungleTempleStructure::new);

	public JungleTempleStructure(Structure.Config config) {
		super(JungleTempleGenerator::new, 12, 15, config);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.JUNGLE_TEMPLE;
	}
}
