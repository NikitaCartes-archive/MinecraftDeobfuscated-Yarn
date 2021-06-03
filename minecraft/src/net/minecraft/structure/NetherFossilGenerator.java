package net.minecraft.structure;

import java.util.Random;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class NetherFossilGenerator {
	private static final Identifier[] FOSSILS = new Identifier[]{
		new Identifier("nether_fossils/fossil_1"),
		new Identifier("nether_fossils/fossil_2"),
		new Identifier("nether_fossils/fossil_3"),
		new Identifier("nether_fossils/fossil_4"),
		new Identifier("nether_fossils/fossil_5"),
		new Identifier("nether_fossils/fossil_6"),
		new Identifier("nether_fossils/fossil_7"),
		new Identifier("nether_fossils/fossil_8"),
		new Identifier("nether_fossils/fossil_9"),
		new Identifier("nether_fossils/fossil_10"),
		new Identifier("nether_fossils/fossil_11"),
		new Identifier("nether_fossils/fossil_12"),
		new Identifier("nether_fossils/fossil_13"),
		new Identifier("nether_fossils/fossil_14")
	};

	public static void addPieces(StructureManager manager, StructurePiecesHolder structurePiecesHolder, Random random, BlockPos pos) {
		BlockRotation blockRotation = BlockRotation.random(random);
		structurePiecesHolder.addPiece(new NetherFossilGenerator.Piece(manager, Util.getRandom(FOSSILS, random), pos, blockRotation));
	}

	public static class Piece extends SimpleStructurePiece {
		public Piece(StructureManager manager, Identifier template, BlockPos pos, BlockRotation rotation) {
			super(StructurePieceType.NETHER_FOSSIL, 0, manager, template, template.toString(), createPlacementData(rotation), pos);
		}

		public Piece(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.NETHER_FOSSIL, nbt, world, identifier -> createPlacementData(BlockRotation.valueOf(nbt.getString("Rot"))));
		}

		private static StructurePlacementData createPlacementData(BlockRotation rotation) {
			return new StructurePlacementData()
				.setRotation(rotation)
				.setMirror(BlockMirror.NONE)
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
		}

		@Override
		protected void writeNbt(ServerWorld world, NbtCompound nbt) {
			super.writeNbt(world, nbt);
			nbt.putString("Rot", this.placementData.getRotation().name());
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			boundingBox.encompass(this.structure.calculateBoundingBox(this.placementData, this.pos));
			return super.generate(world, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, pos);
		}
	}
}
