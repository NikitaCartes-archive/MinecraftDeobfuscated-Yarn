package net.minecraft.structure;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class VillageGenerator {
	public static void init() {
		PlainsVillageData.init();
		SnowyVillageData.init();
		SavannaVillageData.init();
		DesertVillageData.init();
		TaigaVillageData.init();
	}

	public static void addPieces(
		ChunkGenerator<?> chunkGenerator,
		StructureManager structureManager,
		BlockPos pos,
		List<StructurePiece> pieces,
		ChunkRandom random,
		StructurePoolFeatureConfig config
	) {
		init();
		StructurePoolBasedGenerator.addPieces(
			config.startPool, config.size, VillageGenerator.Piece::new, chunkGenerator, structureManager, pos, pieces, random, true, true
		);
	}

	public static class Piece extends PoolStructurePiece {
		public Piece(
			StructureManager structureManager, StructurePoolElement structurePoolElement, BlockPos blockPos, int i, BlockRotation blockRotation, BlockBox blockBox
		) {
			super(StructurePieceType.VILLAGE, structureManager, structurePoolElement, blockPos, i, blockRotation, blockBox);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(structureManager, compoundTag, StructurePieceType.VILLAGE);
		}
	}
}
