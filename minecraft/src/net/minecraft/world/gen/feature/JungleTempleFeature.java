package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.JungleTempleGenerator;
import net.minecraft.structure.StructureType;

public class JungleTempleFeature extends BasicTempleStructureFeature {
	public static final Codec<JungleTempleFeature> CODEC = createCodec(JungleTempleFeature::new);

	public JungleTempleFeature(StructureFeature.Config config) {
		super(JungleTempleGenerator::new, 12, 15, config);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.JUNGLE_TEMPLE;
	}
}
