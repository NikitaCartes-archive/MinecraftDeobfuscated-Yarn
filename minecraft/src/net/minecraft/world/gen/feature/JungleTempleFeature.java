package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_6834;
import net.minecraft.structure.JungleTempleGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.world.Heightmap;

public class JungleTempleFeature extends StructureFeature<DefaultFeatureConfig> {
	public JungleTempleFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec, class_6834.simple(JungleTempleFeature::method_39818, JungleTempleFeature::addPieces));
	}

	private static <C extends FeatureConfig> boolean method_39818(class_6834.class_6835<C> arg) {
		return !arg.method_39848(Heightmap.Type.WORLD_SURFACE_WG) ? false : arg.method_39846(12, 15) >= arg.chunkGenerator().getSeaLevel();
	}

	private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<DefaultFeatureConfig> context) {
		collector.addPiece(new JungleTempleGenerator(context.random(), context.chunkPos().getStartX(), context.chunkPos().getStartZ()));
	}
}
