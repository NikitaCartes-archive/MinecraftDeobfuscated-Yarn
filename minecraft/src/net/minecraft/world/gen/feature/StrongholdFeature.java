package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.class_6622;
import net.minecraft.class_6626;
import net.minecraft.structure.MarginedStructureStart;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.random.ChunkRandom;

public class StrongholdFeature extends MarginedStructureStart<DefaultFeatureConfig> {
	public StrongholdFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec, StrongholdFeature::method_38686);
	}

	protected boolean shouldStartAt(
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		long l,
		ChunkRandom chunkRandom,
		ChunkPos chunkPos,
		ChunkPos chunkPos2,
		DefaultFeatureConfig defaultFeatureConfig,
		HeightLimitView heightLimitView
	) {
		return chunkGenerator.isStrongholdStartingChunk(chunkPos);
	}

	private static void method_38686(class_6626 arg, DefaultFeatureConfig defaultFeatureConfig, class_6622.class_6623 arg2) {
		int i = 0;

		StrongholdGenerator.Start start;
		do {
			arg.method_38719();
			arg2.random().setCarverSeed(arg2.seed() + (long)(i++), arg2.chunkPos().x, arg2.chunkPos().z);
			StrongholdGenerator.init();
			start = new StrongholdGenerator.Start(arg2.random(), arg2.chunkPos().getOffsetX(2), arg2.chunkPos().getOffsetZ(2));
			arg.addPiece(start);
			start.fillOpenings(start, arg, arg2.random());
			List<StructurePiece> list = start.pieces;

			while (!list.isEmpty()) {
				int j = arg2.random().nextInt(list.size());
				StructurePiece structurePiece = (StructurePiece)list.remove(j);
				structurePiece.fillOpenings(start, arg, arg2.random());
			}

			arg.method_38716(arg2.chunkGenerator().getSeaLevel(), arg2.chunkGenerator().getMinimumY(), arg2.random(), 10);
		} while (arg.method_38720() || start.portalRoom == null);
	}
}
