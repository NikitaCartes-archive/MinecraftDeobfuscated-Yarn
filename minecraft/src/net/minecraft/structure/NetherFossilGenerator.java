package net.minecraft.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
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

	public static void addPieces(StructureManager manager, List<StructurePiece> pieces, Random random, BlockPos pos) {
		BlockRotation blockRotation = BlockRotation.random(random);
		pieces.add(new NetherFossilGenerator.Piece(manager, Util.getRandom(FOSSILS, random), pos, blockRotation));
	}

	public static class Piece extends SimpleStructurePiece {
		private final Identifier template;
		private final BlockRotation structureRotation;

		public Piece(StructureManager manager, Identifier template, BlockPos pos, BlockRotation rotation) {
			super(StructurePieceType.NETHER_FOSSIL, 0);
			this.template = template;
			this.pos = pos;
			this.structureRotation = rotation;
			this.initializeStructureData(manager);
		}

		public Piece(StructureManager manager, CompoundTag tag) {
			super(StructurePieceType.NETHER_FOSSIL, tag);
			this.template = new Identifier(tag.getString("Template"));
			this.structureRotation = BlockRotation.valueOf(tag.getString("Rot"));
			this.initializeStructureData(manager);
		}

		private void initializeStructureData(StructureManager manager) {
			Structure structure = manager.getStructureOrBlank(this.template);
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.setRotation(this.structureRotation)
				.setMirror(BlockMirror.NONE)
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
			this.setStructureData(structure, this.pos, structurePlacementData);
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putString("Template", this.template.toString());
			tag.putString("Rot", this.structureRotation.name());
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess serverWorldAccess, Random random, BlockBox boundingBox) {
		}

		@Override
		public boolean generate(
			StructureWorldAccess structureWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			boundingBox.encompass(this.structure.calculateBoundingBox(this.placementData, this.pos));
			return super.generate(structureWorldAccess, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, blockPos);
		}
	}
}
