package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_6622;
import net.minecraft.class_6626;
import net.minecraft.structure.JungleTempleGenerator;
import net.minecraft.world.Heightmap;

public class JungleTempleFeature extends StructureFeature<DefaultFeatureConfig> {
	public JungleTempleFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec, JungleTempleFeature::method_38677);
	}

	private static void method_38677(class_6626 arg, DefaultFeatureConfig defaultFeatureConfig, class_6622.class_6623 arg2) {
		if (arg2.method_38707(Heightmap.Type.WORLD_SURFACE_WG)) {
			if (arg2.method_38705(12, 15) >= arg2.chunkGenerator().getSeaLevel()) {
				arg.addPiece(new JungleTempleGenerator(arg2.random(), arg2.chunkPos().getStartX(), arg2.chunkPos().getStartZ()));
			}
		}
	}
}
