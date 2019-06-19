package net.minecraft.structure;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.VillageFeatureConfig;

public class VillageGenerator {
	public static void addPieces(
		ChunkGenerator<?> chunkGenerator,
		StructureManager structureManager,
		BlockPos blockPos,
		List<StructurePiece> list,
		ChunkRandom chunkRandom,
		VillageFeatureConfig villageFeatureConfig
	) {
		PlainsVillageData.initialize();
		SnowyVillageData.initialize();
		SavannaVillageData.initialize();
		DesertVillageData.initialize();
		TaigaVillageData.initialize();
		StructurePoolBasedGenerator.addPieces(
			villageFeatureConfig.startPool, villageFeatureConfig.size, VillageGenerator.Piece::new, chunkGenerator, structureManager, blockPos, list, chunkRandom
		);
	}

	public static class Piece extends PoolStructurePiece {
		public Piece(
			StructureManager structureManager,
			StructurePoolElement structurePoolElement,
			BlockPos blockPos,
			int i,
			BlockRotation blockRotation,
			MutableIntBoundingBox mutableIntBoundingBox
		) {
			super(StructurePieceType.VILLAGE, structureManager, structurePoolElement, blockPos, i, blockRotation, mutableIntBoundingBox);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(structureManager, compoundTag, StructurePieceType.VILLAGE);
		}
	}
}
