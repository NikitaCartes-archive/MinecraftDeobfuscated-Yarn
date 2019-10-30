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
import net.minecraft.world.gen.feature.VillageFeatureConfig;

public class VillageGenerator {
	public static void addPieces(
		ChunkGenerator<?> chunkGenerator,
		StructureManager structureManager,
		BlockPos pos,
		List<StructurePiece> pieces,
		ChunkRandom random,
		VillageFeatureConfig config
	) {
		PlainsVillageData.initialize();
		SnowyVillageData.initialize();
		SavannaVillageData.initialize();
		DesertVillageData.initialize();
		TaigaVillageData.initialize();
		StructurePoolBasedGenerator.addPieces(config.startPool, config.size, VillageGenerator.Piece::new, chunkGenerator, structureManager, pos, pieces, random);
	}

	public static class Piece extends PoolStructurePiece {
		public Piece(
			StructureManager structureManager, StructurePoolElement structurePoolElement, BlockPos blockPos, int i, BlockRotation blockRotation, BlockBox blockBox
		) {
			super(StructurePieceType.NVI, structureManager, structurePoolElement, blockPos, i, blockRotation, blockBox);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(structureManager, compoundTag, StructurePieceType.NVI);
		}
	}
}
