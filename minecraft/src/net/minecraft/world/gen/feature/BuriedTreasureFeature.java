package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.BuriedTreasureGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class BuriedTreasureFeature extends StructureFeature<ProbabilityConfig> {
	private static final int SALT = 10387320;

	public BuriedTreasureFeature(Codec<ProbabilityConfig> configCodec) {
		super(configCodec, BuriedTreasureFeature::addPieces);
	}

	protected boolean shouldStartAt(
		ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkPos chunkPos, ProbabilityConfig probabilityConfig, HeightLimitView heightLimitView
	) {
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setRegionSeed(l, chunkPos.x, chunkPos.z, 10387320);
		return chunkRandom.nextFloat() < probabilityConfig.probability;
	}

	private static void addPieces(StructurePiecesCollector collector, ProbabilityConfig config, StructurePiecesGenerator.Context context) {
		if (context.isBiomeValid(Heightmap.Type.OCEAN_FLOOR_WG)) {
			BlockPos blockPos = new BlockPos(context.chunkPos().getOffsetX(9), 90, context.chunkPos().getOffsetZ(9));
			collector.addPiece(new BuriedTreasureGenerator.Piece(blockPos));
		}
	}

	@Override
	public BlockPos getLocatedPos(ChunkPos chunkPos) {
		return new BlockPos(chunkPos.getOffsetX(9), 0, chunkPos.getOffsetZ(9));
	}
}
