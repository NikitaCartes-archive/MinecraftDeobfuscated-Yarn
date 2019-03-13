package net.minecraft.structure.generator;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.loot.LootTables;

public class IglooGenerator {
	private static final Identifier field_14409 = new Identifier("igloo/top");
	private static final Identifier field_14407 = new Identifier("igloo/middle");
	private static final Identifier field_14410 = new Identifier("igloo/bottom");
	private static final Map<Identifier, BlockPos> field_14408 = ImmutableMap.of(
		field_14409, new BlockPos(3, 5, 5), field_14407, new BlockPos(1, 3, 1), field_14410, new BlockPos(3, 6, 7)
	);
	private static final Map<Identifier, BlockPos> field_14406 = ImmutableMap.of(
		field_14409, BlockPos.ORIGIN, field_14407, new BlockPos(2, -3, 4), field_14410, new BlockPos(0, -3, -2)
	);

	public static void method_14705(
		StructureManager structureManager, BlockPos blockPos, Rotation rotation, List<StructurePiece> list, Random random, DefaultFeatureConfig defaultFeatureConfig
	) {
		if (random.nextDouble() < 0.5) {
			int i = random.nextInt(8) + 4;
			list.add(new IglooGenerator.Piece(structureManager, field_14410, blockPos, rotation, i * 3));

			for (int j = 0; j < i - 1; j++) {
				list.add(new IglooGenerator.Piece(structureManager, field_14407, blockPos, rotation, j * 3));
			}
		}

		list.add(new IglooGenerator.Piece(structureManager, field_14409, blockPos, rotation, 0));
	}

	public static class Piece extends SimpleStructurePiece {
		private final Identifier field_14411;
		private final Rotation rotation;

		public Piece(StructureManager structureManager, Identifier identifier, BlockPos blockPos, Rotation rotation, int i) {
			super(StructurePieceType.IGLOO, 0);
			this.field_14411 = identifier;
			BlockPos blockPos2 = (BlockPos)IglooGenerator.field_14406.get(identifier);
			this.field_15432 = blockPos.add(blockPos2.getX(), blockPos2.getY() - i, blockPos2.getZ());
			this.rotation = rotation;
			this.method_14708(structureManager);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.IGLOO, compoundTag);
			this.field_14411 = new Identifier(compoundTag.getString("Template"));
			this.rotation = Rotation.valueOf(compoundTag.getString("Rot"));
			this.method_14708(structureManager);
		}

		private void method_14708(StructureManager structureManager) {
			Structure structure = structureManager.method_15091(this.field_14411);
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.setRotation(this.rotation)
				.setMirrored(Mirror.NONE)
				.method_15119((BlockPos)IglooGenerator.field_14408.get(this.field_14411))
				.method_16184(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
			this.method_15027(structure, this.field_15432, structurePlacementData);
		}

		@Override
		protected void method_14943(CompoundTag compoundTag) {
			super.method_14943(compoundTag);
			compoundTag.putString("Template", this.field_14411.toString());
			compoundTag.putString("Rot", this.rotation.name());
		}

		@Override
		protected void method_15026(String string, BlockPos blockPos, IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox) {
			if ("chest".equals(string)) {
				iWorld.method_8652(blockPos, Blocks.field_10124.method_9564(), 3);
				BlockEntity blockEntity = iWorld.method_8321(blockPos.down());
				if (blockEntity instanceof ChestBlockEntity) {
					((ChestBlockEntity)blockEntity).method_11285(LootTables.field_662, random.nextLong());
				}
			}
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.setRotation(this.rotation)
				.setMirrored(Mirror.NONE)
				.method_15119((BlockPos)IglooGenerator.field_14408.get(this.field_14411))
				.method_16184(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
			BlockPos blockPos = (BlockPos)IglooGenerator.field_14406.get(this.field_14411);
			BlockPos blockPos2 = this.field_15432
				.method_10081(Structure.method_15171(structurePlacementData, new BlockPos(3 - blockPos.getX(), 0, 0 - blockPos.getZ())));
			int i = iWorld.method_8589(Heightmap.Type.WORLD_SURFACE_WG, blockPos2.getX(), blockPos2.getZ());
			BlockPos blockPos3 = this.field_15432;
			this.field_15432 = this.field_15432.add(0, i - 90 - 1, 0);
			boolean bl = super.generate(iWorld, random, mutableIntBoundingBox, chunkPos);
			if (this.field_14411.equals(IglooGenerator.field_14409)) {
				BlockPos blockPos4 = this.field_15432.method_10081(Structure.method_15171(structurePlacementData, new BlockPos(3, 0, 5)));
				BlockState blockState = iWorld.method_8320(blockPos4.down());
				if (!blockState.isAir() && blockState.getBlock() != Blocks.field_9983) {
					iWorld.method_8652(blockPos4, Blocks.field_10491.method_9564(), 3);
				}
			}

			this.field_15432 = blockPos3;
			return bl;
		}
	}
}
