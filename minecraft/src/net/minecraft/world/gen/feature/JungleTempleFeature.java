package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.JungleTempleGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.world.Heightmap;

public class JungleTempleFeature extends StructureFeature<DefaultFeatureConfig> {
	public JungleTempleFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec, JungleTempleFeature::addPieces);
	}

	private static void addPieces(StructurePiecesCollector collector, DefaultFeatureConfig config, StructurePiecesGenerator.Context context) {
		if (context.isBiomeValid(Heightmap.Type.WORLD_SURFACE_WG)) {
			if (context.getMinInGroundHeight(12, 15) >= context.chunkGenerator().getSeaLevel()) {
				collector.addPiece(new JungleTempleGenerator(context.random(), context.chunkPos().getStartX(), context.chunkPos().getStartZ()));
			}
		}
	}
}
