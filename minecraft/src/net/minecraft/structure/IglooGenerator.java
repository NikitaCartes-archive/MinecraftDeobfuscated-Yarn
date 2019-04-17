package net.minecraft.structure;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.loot.LootTables;

public class IglooGenerator {
	private static final Identifier TOP_TEMPLATE = new Identifier("igloo/top");
	private static final Identifier MIDDLE_TEMPLATE = new Identifier("igloo/middle");
	private static final Identifier BOTTOM_TEMPLATE = new Identifier("igloo/bottom");
	private static final Map<Identifier, BlockPos> field_14408 = ImmutableMap.of(
		TOP_TEMPLATE, new BlockPos(3, 5, 5), MIDDLE_TEMPLATE, new BlockPos(1, 3, 1), BOTTOM_TEMPLATE, new BlockPos(3, 6, 7)
	);
	private static final Map<Identifier, BlockPos> field_14406 = ImmutableMap.of(
		TOP_TEMPLATE, BlockPos.ORIGIN, MIDDLE_TEMPLATE, new BlockPos(2, -3, 4), BOTTOM_TEMPLATE, new BlockPos(0, -3, -2)
	);

	public static void method_14705(
		StructureManager structureManager, BlockPos blockPos, Rotation rotation, List<StructurePiece> list, Random random, DefaultFeatureConfig defaultFeatureConfig
	) {
		if (random.nextDouble() < 0.5) {
			int i = random.nextInt(8) + 4;
			list.add(new IglooGenerator.Piece(structureManager, BOTTOM_TEMPLATE, blockPos, rotation, i * 3));

			for (int j = 0; j < i - 1; j++) {
				list.add(new IglooGenerator.Piece(structureManager, MIDDLE_TEMPLATE, blockPos, rotation, j * 3));
			}
		}

		list.add(new IglooGenerator.Piece(structureManager, TOP_TEMPLATE, blockPos, rotation, 0));
	}

	public static class Piece extends SimpleStructurePiece {
		private final Identifier template;
		private final Rotation rotation;

		public Piece(StructureManager structureManager, Identifier identifier, BlockPos blockPos, Rotation rotation, int i) {
			super(StructurePieceType.IGLOO, 0);
			this.template = identifier;
			BlockPos blockPos2 = (BlockPos)IglooGenerator.field_14406.get(identifier);
			this.pos = blockPos.add(blockPos2.getX(), blockPos2.getY() - i, blockPos2.getZ());
			this.rotation = rotation;
			this.method_14708(structureManager);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.IGLOO, compoundTag);
			this.template = new Identifier(compoundTag.getString("Template"));
			this.rotation = Rotation.valueOf(compoundTag.getString("Rot"));
			this.method_14708(structureManager);
		}

		private void method_14708(StructureManager structureManager) {
			Structure structure = structureManager.getStructureOrBlank(this.template);
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.setRotation(this.rotation)
				.setMirrored(Mirror.NONE)
				.setPosition((BlockPos)IglooGenerator.field_14408.get(this.template))
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
			this.setStructureData(structure, this.pos, structurePlacementData);
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putString("Template", this.template.toString());
			compoundTag.putString("Rot", this.rotation.name());
		}

		@Override
		protected void handleMetadata(String string, BlockPos blockPos, IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox) {
			if ("chest".equals(string)) {
				iWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
				BlockEntity blockEntity = iWorld.getBlockEntity(blockPos.down());
				if (blockEntity instanceof ChestBlockEntity) {
					((ChestBlockEntity)blockEntity).setLootTable(LootTables.CHEST_IGLOO, random.nextLong());
				}
			}
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.setRotation(this.rotation)
				.setMirrored(Mirror.NONE)
				.setPosition((BlockPos)IglooGenerator.field_14408.get(this.template))
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
			BlockPos blockPos = (BlockPos)IglooGenerator.field_14406.get(this.template);
			BlockPos blockPos2 = this.pos.add(Structure.method_15171(structurePlacementData, new BlockPos(3 - blockPos.getX(), 0, 0 - blockPos.getZ())));
			int i = iWorld.getTop(Heightmap.Type.field_13194, blockPos2.getX(), blockPos2.getZ());
			BlockPos blockPos3 = this.pos;
			this.pos = this.pos.add(0, i - 90 - 1, 0);
			boolean bl = super.generate(iWorld, random, mutableIntBoundingBox, chunkPos);
			if (this.template.equals(IglooGenerator.TOP_TEMPLATE)) {
				BlockPos blockPos4 = this.pos.add(Structure.method_15171(structurePlacementData, new BlockPos(3, 0, 5)));
				BlockState blockState = iWorld.getBlockState(blockPos4.down());
				if (!blockState.isAir() && blockState.getBlock() != Blocks.field_9983) {
					iWorld.setBlockState(blockPos4, Blocks.field_10491.getDefaultState(), 3);
				}
			}

			this.pos = blockPos3;
			return bl;
		}
	}
}
