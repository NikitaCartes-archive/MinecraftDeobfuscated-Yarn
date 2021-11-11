package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.DesertTempleGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.world.Heightmap;

public class DesertPyramidFeature extends StructureFeature<DefaultFeatureConfig> {
	public DesertPyramidFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec, DesertPyramidFeature::addPieces);
	}

	private static void addPieces(StructurePiecesCollector collector, DefaultFeatureConfig config, StructurePiecesGenerator.Context context) {
		if (context.isBiomeValid(Heightmap.Type.WORLD_SURFACE_WG)) {
			if (context.getMinInGroundHeight(21, 21) >= context.chunkGenerator().getSeaLevel()) {
				collector.addPiece(new DesertTempleGenerator(context.random(), context.chunkPos().getStartX(), context.chunkPos().getStartZ()));
			}
		}
	}
}
