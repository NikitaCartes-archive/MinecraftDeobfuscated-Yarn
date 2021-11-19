package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.JungleTempleGenerator;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.world.Heightmap;

public class JungleTempleFeature extends StructureFeature<DefaultFeatureConfig> {
	public JungleTempleFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec, StructureGeneratorFactory.simple(JungleTempleFeature::method_39818, JungleTempleFeature::addPieces));
	}

	private static <C extends FeatureConfig> boolean method_39818(StructureGeneratorFactory.Context<C> context) {
		return !context.isBiomeValid(Heightmap.Type.WORLD_SURFACE_WG) ? false : context.getMinCornerHeight(12, 15) >= context.chunkGenerator().getSeaLevel();
	}

	private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<DefaultFeatureConfig> context) {
		collector.addPiece(new JungleTempleGenerator(context.random(), context.chunkPos().getStartX(), context.chunkPos().getStartZ()));
	}
}
