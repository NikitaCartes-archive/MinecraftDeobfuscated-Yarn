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
		Identifier.method_60656("nether_fossils/fossil_1"),
		Identifier.method_60656("nether_fossils/fossil_2"),
		Identifier.method_60656("nether_fossils/fossil_3"),
		Identifier.method_60656("nether_fossils/fossil_4"),
		Identifier.method_60656("nether_fossils/fossil_5"),
		Identifier.method_60656("nether_fossils/fossil_6"),
		Identifier.method_60656("nether_fossils/fossil_7"),
		Identifier.method_60656("nether_fossils/fossil_8"),
		Identifier.method_60656("nether_fossils/fossil_9"),
		Identifier.method_60656("nether_fossils/fossil_10"),
		Identifier.method_60656("nether_fossils/fossil_11"),
		Identifier.method_60656("nether_fossils/fossil_12"),
		Identifier.method_60656("nether_fossils/fossil_13"),
		Identifier.method_60656("nether_fossils/fossil_14")
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
