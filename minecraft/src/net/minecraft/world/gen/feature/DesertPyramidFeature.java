package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.DesertTempleGenerator;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.world.Heightmap;

public class DesertPyramidFeature extends StructureFeature<DefaultFeatureConfig> {
	public DesertPyramidFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec, StructureGeneratorFactory.simple(DesertPyramidFeature::method_39816, DesertPyramidFeature::addPieces));
	}

	private static <C extends FeatureConfig> boolean method_39816(StructureGeneratorFactory.Context<C> context) {
		return !context.isBiomeValid(Heightmap.Type.WORLD_SURFACE_WG) ? false : context.getMinCornerHeight(21, 21) >= context.chunkGenerator().getSeaLevel();
	}

	private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<DefaultFeatureConfig> context) {
		collector.addPiece(new DesertTempleGenerator(context.random(), context.chunkPos().getStartX(), context.chunkPos().getStartZ()));
	}
}
