package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_6834;
import net.minecraft.structure.BuriedTreasureGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class BuriedTreasureFeature extends StructureFeature<ProbabilityConfig> {
	private static final int SALT = 10387320;

	public BuriedTreasureFeature(Codec<ProbabilityConfig> configCodec) {
		super(configCodec, class_6834.simple(BuriedTreasureFeature::method_28619, BuriedTreasureFeature::addPieces));
	}

	private static boolean method_28619(class_6834.class_6835<ProbabilityConfig> arg) {
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setRegionSeed(arg.seed(), arg.chunkPos().x, arg.chunkPos().z, 10387320);
		return chunkRandom.nextFloat() < ((ProbabilityConfig)arg.config()).probability && arg.method_39848(Heightmap.Type.OCEAN_FLOOR_WG);
	}

	private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<ProbabilityConfig> context) {
		BlockPos blockPos = new BlockPos(context.chunkPos().getOffsetX(9), 90, context.chunkPos().getOffsetZ(9));
		collector.addPiece(new BuriedTreasureGenerator.Piece(blockPos));
	}

	@Override
	public BlockPos getLocatedPos(ChunkPos chunkPos) {
		return new BlockPos(chunkPos.getOffsetX(9), 0, chunkPos.getOffsetZ(9));
	}
}
