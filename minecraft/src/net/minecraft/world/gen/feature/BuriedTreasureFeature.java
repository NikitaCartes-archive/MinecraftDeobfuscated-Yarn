package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.BuriedTreasureGenerator;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class BuriedTreasureFeature extends StructureFeature<ProbabilityConfig> {
	private static final int SALT = 10387320;

	public BuriedTreasureFeature(Codec<ProbabilityConfig> configCodec) {
		super(configCodec, StructureGeneratorFactory.simple(BuriedTreasureFeature::canGenerate, BuriedTreasureFeature::addPieces));
	}

	private static boolean canGenerate(StructureGeneratorFactory.Context<ProbabilityConfig> context) {
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setRegionSeed(context.seed(), context.chunkPos().x, context.chunkPos().z, 10387320);
		return chunkRandom.nextFloat() < context.config().probability && context.isBiomeValid(Heightmap.Type.OCEAN_FLOOR_WG);
	}

	private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<ProbabilityConfig> context) {
		BlockPos blockPos = new BlockPos(context.chunkPos().getOffsetX(9), 90, context.chunkPos().getOffsetZ(9));
		collector.addPiece(new BuriedTreasureGenerator.Piece(blockPos));
	}
}
