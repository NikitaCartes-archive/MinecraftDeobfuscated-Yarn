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
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.VillageFeatureConfig;

public class VillageGenerator {
	public static void method_16753(
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
		StructurePoolBasedGenerator.method_16605(
			villageFeatureConfig.field_16861, villageFeatureConfig.size, VillageGenerator.Piece::new, chunkGenerator, structureManager, blockPos, list, chunkRandom
		);
	}

	public static class Piece extends PoolStructurePiece {
		public Piece(
			StructureManager structureManager,
			StructurePoolElement structurePoolElement,
			BlockPos blockPos,
			int i,
			Rotation rotation,
			MutableIntBoundingBox mutableIntBoundingBox
		) {
			super(StructurePieceType.VILLAGE, structureManager, structurePoolElement, blockPos, i, rotation, mutableIntBoundingBox);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(structureManager, compoundTag, StructurePieceType.VILLAGE);
		}
	}
}
