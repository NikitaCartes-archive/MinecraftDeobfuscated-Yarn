package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_6622;
import net.minecraft.class_6626;
import net.minecraft.structure.BuriedTreasureGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.random.ChunkRandom;

public class BuriedTreasureFeature extends StructureFeature<ProbabilityConfig> {
	private static final int SALT = 10387320;

	public BuriedTreasureFeature(Codec<ProbabilityConfig> codec) {
		super(codec, BuriedTreasureFeature::method_38672);
	}

	protected boolean shouldStartAt(
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		long l,
		ChunkRandom chunkRandom,
		ChunkPos chunkPos,
		ChunkPos chunkPos2,
		ProbabilityConfig probabilityConfig,
		HeightLimitView heightLimitView
	) {
		chunkRandom.setRegionSeed(l, chunkPos.x, chunkPos.z, 10387320);
		return chunkRandom.nextFloat() < probabilityConfig.probability;
	}

	private static void method_38672(class_6626 arg, ProbabilityConfig probabilityConfig, class_6622.class_6623 arg2) {
		if (arg2.method_38707(Heightmap.Type.OCEAN_FLOOR_WG)) {
			BlockPos blockPos = new BlockPos(arg2.chunkPos().getOffsetX(9), 90, arg2.chunkPos().getOffsetZ(9));
			arg.addPiece(new BuriedTreasureGenerator.Piece(blockPos));
		}
	}

	@Override
	public BlockPos method_38671(ChunkPos chunkPos) {
		return new BlockPos(chunkPos.getOffsetX(9), 0, chunkPos.getOffsetZ(9));
	}
}
