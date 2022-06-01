package net.minecraft.structure;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
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

	public static void addPieces(StructureTemplateManager manager, StructurePiecesHolder holder, Random random, BlockPos pos) {
		BlockRotation blockRotation = BlockRotation.random(random);
		holder.addPiece(new NetherFossilGenerator.Piece(manager, Util.getRandom(FOSSILS, random), pos, blockRotation));
	}

	public static class Piece extends SimpleStructurePiece {
		public Piece(StructureTemplateManager manager, Identifier template, BlockPos pos, BlockRotation rotation) {
			super(StructurePieceType.NETHER_FOSSIL, 0, manager, template, template.toString(), createPlacementData(rotation), pos);
		}

		public Piece(StructureTemplateManager manager, NbtCompound nbt) {
			super(StructurePieceType.NETHER_FOSSIL, nbt, manager, id -> createPlacementData(BlockRotation.valueOf(nbt.getString("Rot"))));
		}

		private static StructurePlacementData createPlacementData(BlockRotation rotation) {
			return new StructurePlacementData()
				.setRotation(rotation)
				.setMirror(BlockMirror.NONE)
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			super.writeNbt(context, nbt);
			nbt.putString("Rot", this.placementData.getRotation().name());
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
		}

		@Override
		public void generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox chunkBox,
			ChunkPos chunkPos,
			BlockPos pivot
		) {
			chunkBox.encompass(this.template.calculateBoundingBox(this.placementData, this.pos));
			super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
		}
	}
}
