package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_6622;
import net.minecraft.class_6626;
import net.minecraft.structure.DesertTempleGenerator;
import net.minecraft.world.Heightmap;

public class DesertPyramidFeature extends StructureFeature<DefaultFeatureConfig> {
	public DesertPyramidFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec, DesertPyramidFeature::method_38673);
	}

	private static void method_38673(class_6626 arg, DefaultFeatureConfig defaultFeatureConfig, class_6622.class_6623 arg2) {
		if (arg2.method_38707(Heightmap.Type.WORLD_SURFACE_WG)) {
			if (arg2.method_38705(21, 21) >= arg2.chunkGenerator().getSeaLevel()) {
				arg.addPiece(new DesertTempleGenerator(arg2.random(), arg2.chunkPos().getStartX(), arg2.chunkPos().getStartZ()));
			}
		}
	}
}
