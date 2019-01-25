package net.minecraft.structure.generator;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.ListPoolElement;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class PillagerOutpostGenerator {
	public static void addPieces(
		ChunkGenerator<?> chunkGenerator, StructureManager structureManager, BlockPos blockPos, List<StructurePiece> list, ChunkRandom chunkRandom
	) {
		StructurePoolBasedGenerator.addPieces(
			new Identifier("pillager_outpost/base_plates"), 7, PillagerOutpostGenerator.Piece::new, chunkGenerator, structureManager, blockPos, list, chunkRandom
		);
	}

	static {
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("pillager_outpost/base_plates"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("pillager_outpost/base_plate"), 1)),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("pillager_outpost/towers"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(
							new ListPoolElement(
								ImmutableList.of(
									new SinglePoolElement("pillager_outpost/watchtower"),
									new SinglePoolElement("pillager_outpost/watchtower_overgrown", ImmutableList.of(new BlockRotStructureProcessor(0.05F)))
								)
							),
							1
						)
					),
					StructurePool.Projection.RIGID
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("pillager_outpost/feature_plates"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new SinglePoolElement("pillager_outpost/feature_plate"), 1)),
					StructurePool.Projection.TERRAIN_MATCHING
				)
			);
		StructurePoolBasedGenerator.REGISTRY
			.add(
				new StructurePool(
					new Identifier("pillager_outpost/features"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new SinglePoolElement("pillager_outpost/feature_cage1"), 1),
						Pair.of(new SinglePoolElement("pillager_outpost/feature_cage2"), 1),
						Pair.of(new SinglePoolElement("pillager_outpost/feature_logs"), 1),
						Pair.of(new SinglePoolElement("pillager_outpost/feature_tent1"), 1),
						Pair.of(new SinglePoolElement("pillager_outpost/feature_tent2"), 1),
						Pair.of(new SinglePoolElement("pillager_outpost/feature_targets"), 1),
						Pair.of(EmptyPoolElement.INSTANCE, 6)
					),
					StructurePool.Projection.RIGID
				)
			);
	}

	public static class Piece extends PoolStructurePiece {
		public Piece(StructureManager structureManager, StructurePoolElement structurePoolElement, BlockPos blockPos, int i, Rotation rotation) {
			super(StructurePieceType.PILLAGER_OUTPOST, structureManager, structurePoolElement, blockPos, i, rotation);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(structureManager, compoundTag, StructurePieceType.PILLAGER_OUTPOST);
		}
	}
}
