package net.minecraft.structure.generator.village;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.NewVillageFeatureConfig;

public class VillageGenerator {
	public static void addPieces(
		ChunkGenerator<?> chunkGenerator,
		StructureManager structureManager,
		BlockPos blockPos,
		List<StructurePiece> list,
		ChunkRandom chunkRandom,
		NewVillageFeatureConfig newVillageFeatureConfig
	) {
		PlainsVillageData.initialize();
		SnowyVillageData.initialize();
		SavannaVillageData.initialize();
		DesertVillageData.initialize();
		TaigaVillageData.initialize();
		StructurePoolBasedGenerator.addPieces(
			newVillageFeatureConfig.startPool, newVillageFeatureConfig.size, VillageGenerator.Piece::new, chunkGenerator, structureManager, blockPos, list, chunkRandom
		);
	}

	public static class Piece extends PoolStructurePiece {
		public Piece(StructureManager structureManager, StructurePoolElement structurePoolElement, BlockPos blockPos, int i, Rotation rotation) {
			super(StructurePieceType.VILLAGE, structureManager, structurePoolElement, blockPos, i, rotation);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(structureManager, compoundTag, StructurePieceType.VILLAGE);
		}
	}
}
