package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_6834;
import net.minecraft.structure.IglooGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public class IglooFeature extends StructureFeature<DefaultFeatureConfig> {
	public IglooFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec, class_6834.simple(class_6834.checkForBiomeOnTop(Heightmap.Type.WORLD_SURFACE_WG), IglooFeature::addPieces));
	}

	private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<DefaultFeatureConfig> context) {
		BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 90, context.chunkPos().getStartZ());
		BlockRotation blockRotation = BlockRotation.random(context.random());
		IglooGenerator.addPieces(context.structureManager(), blockPos, blockRotation, collector, context.random());
	}
}
