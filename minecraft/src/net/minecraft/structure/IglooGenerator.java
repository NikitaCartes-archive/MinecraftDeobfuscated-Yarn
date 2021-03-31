package net.minecraft.structure;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.class_6130;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class IglooGenerator {
	public static final int field_31550 = 90;
	private static final Identifier TOP_TEMPLATE = new Identifier("igloo/top");
	private static final Identifier MIDDLE_TEMPLATE = new Identifier("igloo/middle");
	private static final Identifier BOTTOM_TEMPLATE = new Identifier("igloo/bottom");
	private static final Map<Identifier, BlockPos> field_14408 = ImmutableMap.of(
		TOP_TEMPLATE, new BlockPos(3, 5, 5), MIDDLE_TEMPLATE, new BlockPos(1, 3, 1), BOTTOM_TEMPLATE, new BlockPos(3, 6, 7)
	);
	private static final Map<Identifier, BlockPos> field_14406 = ImmutableMap.of(
		TOP_TEMPLATE, BlockPos.ORIGIN, MIDDLE_TEMPLATE, new BlockPos(2, -3, 4), BOTTOM_TEMPLATE, new BlockPos(0, -3, -2)
	);

	public static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, class_6130 arg, Random random) {
		if (random.nextDouble() < 0.5) {
			int i = random.nextInt(8) + 4;
			arg.method_35462(new IglooGenerator.Piece(manager, BOTTOM_TEMPLATE, pos, rotation, i * 3));

			for (int j = 0; j < i - 1; j++) {
				arg.method_35462(new IglooGenerator.Piece(manager, MIDDLE_TEMPLATE, pos, rotation, j * 3));
			}
		}

		arg.method_35462(new IglooGenerator.Piece(manager, TOP_TEMPLATE, pos, rotation, 0));
	}

	public static class Piece extends SimpleStructurePiece {
		public Piece(StructureManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation, int yOffset) {
			super(StructurePieceType.IGLOO, 0, manager, identifier, identifier.toString(), method_35428(rotation, identifier), method_35430(identifier, pos, yOffset));
		}

		public Piece(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.IGLOO, nbt, world, identifier -> method_35428(BlockRotation.valueOf(nbt.getString("Rot")), identifier));
		}

		private static StructurePlacementData method_35428(BlockRotation blockRotation, Identifier identifier) {
			return new StructurePlacementData()
				.setRotation(blockRotation)
				.setMirror(BlockMirror.NONE)
				.setPosition((BlockPos)IglooGenerator.field_14408.get(identifier))
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
		}

		private static BlockPos method_35430(Identifier identifier, BlockPos blockPos, int i) {
			return blockPos.add((Vec3i)IglooGenerator.field_14406.get(identifier)).down(i);
		}

		@Override
		protected void writeNbt(ServerWorld world, NbtCompound nbt) {
			super.writeNbt(world, nbt);
			nbt.putString("Rot", this.placementData.getRotation().name());
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
			if ("chest".equals(metadata)) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
				BlockEntity blockEntity = world.getBlockEntity(pos.down());
				if (blockEntity instanceof ChestBlockEntity) {
					((ChestBlockEntity)blockEntity).setLootTable(LootTables.IGLOO_CHEST_CHEST, random.nextLong());
				}
			}
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
			Identifier identifier = new Identifier(this.field_31664);
			StructurePlacementData structurePlacementData = method_35428(this.placementData.getRotation(), identifier);
			BlockPos blockPos = (BlockPos)IglooGenerator.field_14406.get(identifier);
			BlockPos blockPos2 = this.pos.add(Structure.transform(structurePlacementData, new BlockPos(3 - blockPos.getX(), 0, -blockPos.getZ())));
			int i = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, blockPos2.getX(), blockPos2.getZ());
			BlockPos blockPos3 = this.pos;
			this.pos = this.pos.add(0, i - 90 - 1, 0);
			boolean bl = super.generate(world, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, pos);
			if (identifier.equals(IglooGenerator.TOP_TEMPLATE)) {
				BlockPos blockPos4 = this.pos.add(Structure.transform(structurePlacementData, new BlockPos(3, 0, 5)));
				BlockState blockState = world.getBlockState(blockPos4.down());
				if (!blockState.isAir() && !blockState.isOf(Blocks.LADDER)) {
					world.setBlockState(blockPos4, Blocks.SNOW_BLOCK.getDefaultState(), Block.NOTIFY_ALL);
				}
			}

			this.pos = blockPos3;
			return bl;
		}
	}
}
