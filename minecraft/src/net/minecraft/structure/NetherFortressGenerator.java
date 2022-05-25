package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class NetherFortressGenerator {
	private static final int field_31557 = 30;
	private static final int field_31558 = 10;
	public static final int field_34730 = 64;
	static final NetherFortressGenerator.PieceData[] ALL_BRIDGE_PIECES = new NetherFortressGenerator.PieceData[]{
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.Bridge.class, 30, 0, true),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.BridgeCrossing.class, 10, 4),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.BridgeSmallCrossing.class, 10, 4),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.BridgeStairs.class, 10, 3),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.BridgePlatform.class, 5, 2),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorExit.class, 5, 1)
	};
	static final NetherFortressGenerator.PieceData[] ALL_CORRIDOR_PIECES = new NetherFortressGenerator.PieceData[]{
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.SmallCorridor.class, 25, 0, true),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorCrossing.class, 15, 5),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorRightTurn.class, 5, 10),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorLeftTurn.class, 5, 10),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorStairs.class, 10, 3, true),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorBalcony.class, 7, 2),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorNetherWartsRoom.class, 5, 2)
	};

	static NetherFortressGenerator.Piece createPiece(
		NetherFortressGenerator.PieceData pieceData, StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength
	) {
		Class<? extends NetherFortressGenerator.Piece> class_ = pieceData.pieceType;
		NetherFortressGenerator.Piece piece = null;
		if (class_ == NetherFortressGenerator.Bridge.class) {
			piece = NetherFortressGenerator.Bridge.create(holder, random, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.BridgeCrossing.class) {
			piece = NetherFortressGenerator.BridgeCrossing.create(holder, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.BridgeSmallCrossing.class) {
			piece = NetherFortressGenerator.BridgeSmallCrossing.create(holder, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.BridgeStairs.class) {
			piece = NetherFortressGenerator.BridgeStairs.create(holder, x, y, z, chainLength, orientation);
		} else if (class_ == NetherFortressGenerator.BridgePlatform.class) {
			piece = NetherFortressGenerator.BridgePlatform.create(holder, x, y, z, chainLength, orientation);
		} else if (class_ == NetherFortressGenerator.CorridorExit.class) {
			piece = NetherFortressGenerator.CorridorExit.create(holder, random, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.SmallCorridor.class) {
			piece = NetherFortressGenerator.SmallCorridor.create(holder, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.CorridorRightTurn.class) {
			piece = NetherFortressGenerator.CorridorRightTurn.create(holder, random, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.CorridorLeftTurn.class) {
			piece = NetherFortressGenerator.CorridorLeftTurn.create(holder, random, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.CorridorStairs.class) {
			piece = NetherFortressGenerator.CorridorStairs.create(holder, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.CorridorBalcony.class) {
			piece = NetherFortressGenerator.CorridorBalcony.create(holder, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.CorridorCrossing.class) {
			piece = NetherFortressGenerator.CorridorCrossing.create(holder, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.CorridorNetherWartsRoom.class) {
			piece = NetherFortressGenerator.CorridorNetherWartsRoom.create(holder, x, y, z, orientation, chainLength);
		}

		return piece;
	}

	public static class Bridge extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 5;
		private static final int SIZE_Y = 10;
		private static final int SIZE_Z = 19;

		public Bridge(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public Bridge(NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			this.fillForwardOpening((NetherFortressGenerator.Start)start, holder, random, 1, 3, false);
		}

		public static NetherFortressGenerator.Bridge create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -3, 0, 5, 10, 19, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.Bridge(chainLength, random, blockBox, orientation)
				: null;
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
			this.fillWithOutline(world, chunkBox, 0, 3, 0, 4, 4, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 5, 0, 3, 7, 18, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 5, 0, 0, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 4, 5, 0, 4, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 0, 4, 2, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 13, 4, 2, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 0, 15, 4, 1, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 2; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, chunkBox);
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, 18 - j, chunkBox);
				}
			}

			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			BlockState blockState2 = blockState.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState3 = blockState.with(FenceBlock.WEST, Boolean.valueOf(true));
			this.fillWithOutline(world, chunkBox, 0, 1, 1, 0, 4, 1, blockState2, blockState2, false);
			this.fillWithOutline(world, chunkBox, 0, 3, 4, 0, 4, 4, blockState2, blockState2, false);
			this.fillWithOutline(world, chunkBox, 0, 3, 14, 0, 4, 14, blockState2, blockState2, false);
			this.fillWithOutline(world, chunkBox, 0, 1, 17, 0, 4, 17, blockState2, blockState2, false);
			this.fillWithOutline(world, chunkBox, 4, 1, 1, 4, 4, 1, blockState3, blockState3, false);
			this.fillWithOutline(world, chunkBox, 4, 3, 4, 4, 4, 4, blockState3, blockState3, false);
			this.fillWithOutline(world, chunkBox, 4, 3, 14, 4, 4, 14, blockState3, blockState3, false);
			this.fillWithOutline(world, chunkBox, 4, 1, 17, 4, 4, 17, blockState3, blockState3, false);
		}
	}

	public static class BridgeCrossing extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 19;
		private static final int SIZE_Y = 10;
		private static final int SIZE_Z = 19;

		public BridgeCrossing(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		protected BridgeCrossing(int x, int z, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, 0, StructurePiece.createBox(x, 64, z, orientation, 19, 10, 19));
			this.setOrientation(orientation);
		}

		protected BridgeCrossing(StructurePieceType structurePieceType, NbtCompound nbtCompound) {
			super(structurePieceType, nbtCompound);
		}

		public BridgeCrossing(NbtCompound nbt) {
			this(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			this.fillForwardOpening((NetherFortressGenerator.Start)start, holder, random, 8, 3, false);
			this.fillNWOpening((NetherFortressGenerator.Start)start, holder, random, 3, 8, false);
			this.fillSEOpening((NetherFortressGenerator.Start)start, holder, random, 3, 8, false);
		}

		public static NetherFortressGenerator.BridgeCrossing create(StructurePiecesHolder holder, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -8, -3, 0, 19, 10, 19, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.BridgeCrossing(chainLength, blockBox, orientation)
				: null;
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
			this.fillWithOutline(world, chunkBox, 7, 3, 0, 11, 4, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 3, 7, 18, 4, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 8, 5, 0, 10, 7, 18, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 5, 8, 18, 7, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 7, 5, 0, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 7, 5, 11, 7, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 11, 5, 0, 11, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 11, 5, 11, 11, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 5, 7, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 11, 5, 7, 18, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 5, 11, 7, 5, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 11, 5, 11, 18, 5, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 7, 2, 0, 11, 2, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 7, 2, 13, 11, 2, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 7, 0, 0, 11, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 7, 0, 15, 11, 1, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 7; i <= 11; i++) {
				for (int j = 0; j <= 2; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, chunkBox);
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, 18 - j, chunkBox);
				}
			}

			this.fillWithOutline(world, chunkBox, 0, 2, 7, 5, 2, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 13, 2, 7, 18, 2, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 0, 7, 3, 1, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 15, 0, 7, 18, 1, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 2; i++) {
				for (int j = 7; j <= 11; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, chunkBox);
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), 18 - i, -1, j, chunkBox);
				}
			}
		}
	}

	public static class BridgeEnd extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 5;
		private static final int SIZE_Y = 10;
		private static final int SIZE_Z = 8;
		private final int seed;

		public BridgeEnd(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END, chainLength, boundingBox);
			this.setOrientation(orientation);
			this.seed = random.nextInt();
		}

		public BridgeEnd(NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END, nbt);
			this.seed = nbt.getInt("Seed");
		}

		public static NetherFortressGenerator.BridgeEnd create(
			StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -3, 0, 5, 10, 8, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.BridgeEnd(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			super.writeNbt(context, nbt);
			nbt.putInt("Seed", this.seed);
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
			Random random2 = Random.create((long)this.seed);

			for (int i = 0; i <= 4; i++) {
				for (int j = 3; j <= 4; j++) {
					int k = random2.nextInt(8);
					this.fillWithOutline(world, chunkBox, i, j, 0, i, j, k, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				}
			}

			int i = random2.nextInt(8);
			this.fillWithOutline(world, chunkBox, 0, 5, 0, 0, 5, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			i = random2.nextInt(8);
			this.fillWithOutline(world, chunkBox, 4, 5, 0, 4, 5, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int ix = 0; ix <= 4; ix++) {
				int j = random2.nextInt(5);
				this.fillWithOutline(world, chunkBox, ix, 2, 0, ix, 2, j, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			}

			for (int ix = 0; ix <= 4; ix++) {
				for (int j = 0; j <= 1; j++) {
					int k = random2.nextInt(3);
					this.fillWithOutline(world, chunkBox, ix, j, 0, ix, j, k, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				}
			}
		}
	}

	public static class BridgePlatform extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 7;
		private static final int SIZE_Y = 8;
		private static final int SIZE_Z = 9;
		private boolean hasBlazeSpawner;

		public BridgePlatform(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_PLATFORM, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public BridgePlatform(NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_PLATFORM, nbt);
			this.hasBlazeSpawner = nbt.getBoolean("Mob");
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			super.writeNbt(context, nbt);
			nbt.putBoolean("Mob", this.hasBlazeSpawner);
		}

		public static NetherFortressGenerator.BridgePlatform create(StructurePiecesHolder holder, int x, int y, int z, int chainLength, Direction orientation) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -2, 0, 0, 7, 8, 9, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.BridgePlatform(chainLength, blockBox, orientation)
				: null;
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
			this.fillWithOutline(world, chunkBox, 0, 2, 0, 6, 7, 7, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 0, 0, 5, 1, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 2, 1, 5, 2, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 3, 2, 5, 3, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 4, 3, 5, 4, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 2, 0, 1, 4, 2, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 5, 2, 0, 5, 4, 2, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 5, 2, 1, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 5, 5, 2, 5, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 5, 3, 0, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 6, 5, 3, 6, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 5, 8, 5, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)), 1, 6, 3, chunkBox);
			this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)), 5, 6, 3, chunkBox);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)).with(FenceBlock.NORTH, Boolean.valueOf(true)),
				0,
				6,
				3,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.NORTH, Boolean.valueOf(true)),
				6,
				6,
				3,
				chunkBox
			);
			this.fillWithOutline(world, chunkBox, 0, 6, 4, 0, 6, 7, blockState2, blockState2, false);
			this.fillWithOutline(world, chunkBox, 6, 6, 4, 6, 6, 7, blockState2, blockState2, false);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true)),
				0,
				6,
				8,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true)),
				6,
				6,
				8,
				chunkBox
			);
			this.fillWithOutline(world, chunkBox, 1, 6, 8, 5, 6, 8, blockState, blockState, false);
			this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)), 1, 7, 8, chunkBox);
			this.fillWithOutline(world, chunkBox, 2, 7, 8, 4, 7, 8, blockState, blockState, false);
			this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)), 5, 7, 8, chunkBox);
			this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)), 2, 8, 8, chunkBox);
			this.addBlock(world, blockState, 3, 8, 8, chunkBox);
			this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)), 4, 8, 8, chunkBox);
			if (!this.hasBlazeSpawner) {
				BlockPos blockPos = this.offsetPos(3, 5, 5);
				if (chunkBox.contains(blockPos)) {
					this.hasBlazeSpawner = true;
					world.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), Block.NOTIFY_LISTENERS);
					BlockEntity blockEntity = world.getBlockEntity(blockPos);
					if (blockEntity instanceof MobSpawnerBlockEntity) {
						((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(EntityType.BLAZE);
					}
				}
			}

			for (int i = 0; i <= 6; i++) {
				for (int j = 0; j <= 6; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, chunkBox);
				}
			}
		}
	}

	public static class BridgeSmallCrossing extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 7;
		private static final int SIZE_Y = 9;
		private static final int SIZE_Z = 7;

		public BridgeSmallCrossing(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public BridgeSmallCrossing(NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			this.fillForwardOpening((NetherFortressGenerator.Start)start, holder, random, 2, 0, false);
			this.fillNWOpening((NetherFortressGenerator.Start)start, holder, random, 0, 2, false);
			this.fillSEOpening((NetherFortressGenerator.Start)start, holder, random, 0, 2, false);
		}

		public static NetherFortressGenerator.BridgeSmallCrossing create(StructurePiecesHolder holder, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -2, 0, 0, 7, 9, 7, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.BridgeSmallCrossing(chainLength, blockBox, orientation)
				: null;
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
			this.fillWithOutline(world, chunkBox, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 0, 6, 7, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 0, 1, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 6, 1, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 5, 2, 0, 6, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 5, 2, 6, 6, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 0, 0, 6, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 5, 0, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 6, 2, 0, 6, 6, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 6, 2, 5, 6, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(world, chunkBox, 2, 6, 0, 4, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 2, 5, 0, 4, 5, 0, blockState, blockState, false);
			this.fillWithOutline(world, chunkBox, 2, 6, 6, 4, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 2, 5, 6, 4, 5, 6, blockState, blockState, false);
			this.fillWithOutline(world, chunkBox, 0, 6, 2, 0, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 5, 2, 0, 5, 4, blockState2, blockState2, false);
			this.fillWithOutline(world, chunkBox, 6, 6, 2, 6, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 6, 5, 2, 6, 5, 4, blockState2, blockState2, false);

			for (int i = 0; i <= 6; i++) {
				for (int j = 0; j <= 6; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, chunkBox);
				}
			}
		}
	}

	public static class BridgeStairs extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 7;
		private static final int SIZE_Y = 11;
		private static final int SIZE_Z = 7;

		public BridgeStairs(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STAIRS, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public BridgeStairs(NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STAIRS, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			this.fillSEOpening((NetherFortressGenerator.Start)start, holder, random, 6, 2, false);
		}

		public static NetherFortressGenerator.BridgeStairs create(StructurePiecesHolder holder, int x, int y, int z, int chainlength, Direction orientation) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -2, 0, 0, 7, 11, 7, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.BridgeStairs(chainlength, blockBox, orientation)
				: null;
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
			this.fillWithOutline(world, chunkBox, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 0, 6, 10, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 0, 1, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 5, 2, 0, 6, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 1, 0, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 6, 2, 1, 6, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 2, 6, 5, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(world, chunkBox, 0, 3, 2, 0, 5, 4, blockState2, blockState2, false);
			this.fillWithOutline(world, chunkBox, 6, 3, 2, 6, 5, 2, blockState2, blockState2, false);
			this.fillWithOutline(world, chunkBox, 6, 3, 4, 6, 5, 4, blockState2, blockState2, false);
			this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 5, 2, 5, chunkBox);
			this.fillWithOutline(world, chunkBox, 4, 2, 5, 4, 3, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 3, 2, 5, 3, 4, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 2, 2, 5, 2, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 2, 5, 1, 6, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 7, 1, 5, 7, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 6, 8, 2, 6, 8, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 2, 6, 0, 4, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 2, 5, 0, 4, 5, 0, blockState, blockState, false);

			for (int i = 0; i <= 6; i++) {
				for (int j = 0; j <= 6; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, chunkBox);
				}
			}
		}
	}

	public static class CorridorBalcony extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 9;
		private static final int SIZE_Y = 7;
		private static final int SIZE_Z = 9;

		public CorridorBalcony(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_BALCONY, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public CorridorBalcony(NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_BALCONY, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			int i = 1;
			Direction direction = this.getFacing();
			if (direction == Direction.WEST || direction == Direction.NORTH) {
				i = 5;
			}

			this.fillNWOpening((NetherFortressGenerator.Start)start, holder, random, 0, i, random.nextInt(8) > 0);
			this.fillSEOpening((NetherFortressGenerator.Start)start, holder, random, 0, i, random.nextInt(8) > 0);
		}

		public static NetherFortressGenerator.CorridorBalcony create(StructurePiecesHolder holder, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -3, 0, 0, 9, 7, 9, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.CorridorBalcony(chainLength, blockBox, orientation)
				: null;
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
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			this.fillWithOutline(world, chunkBox, 0, 0, 0, 8, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 0, 8, 5, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 6, 0, 8, 6, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 0, 2, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 6, 2, 0, 8, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 3, 0, 1, 4, 0, blockState2, blockState2, false);
			this.fillWithOutline(world, chunkBox, 7, 3, 0, 7, 4, 0, blockState2, blockState2, false);
			this.fillWithOutline(world, chunkBox, 0, 2, 4, 8, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 1, 4, 2, 2, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 6, 1, 4, 7, 2, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 3, 8, 7, 3, 8, blockState2, blockState2, false);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true)),
				0,
				3,
				8,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true)),
				8,
				3,
				8,
				chunkBox
			);
			this.fillWithOutline(world, chunkBox, 0, 3, 6, 0, 3, 7, blockState, blockState, false);
			this.fillWithOutline(world, chunkBox, 8, 3, 6, 8, 3, 7, blockState, blockState, false);
			this.fillWithOutline(world, chunkBox, 0, 3, 4, 0, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 8, 3, 4, 8, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 3, 5, 2, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 6, 3, 5, 7, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 4, 5, 1, 5, 5, blockState2, blockState2, false);
			this.fillWithOutline(world, chunkBox, 7, 4, 5, 7, 5, 5, blockState2, blockState2, false);

			for (int i = 0; i <= 5; i++) {
				for (int j = 0; j <= 8; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), j, -1, i, chunkBox);
				}
			}
		}
	}

	public static class CorridorCrossing extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 5;
		private static final int SIZE_Y = 7;
		private static final int SIZE_Z = 5;

		public CorridorCrossing(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_CROSSING, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public CorridorCrossing(NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_CROSSING, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			this.fillForwardOpening((NetherFortressGenerator.Start)start, holder, random, 1, 0, true);
			this.fillNWOpening((NetherFortressGenerator.Start)start, holder, random, 0, 1, true);
			this.fillSEOpening((NetherFortressGenerator.Start)start, holder, random, 0, 1, true);
		}

		public static NetherFortressGenerator.CorridorCrossing create(StructurePiecesHolder holder, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, 0, 0, 5, 7, 5, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.CorridorCrossing(chainLength, blockBox, orientation)
				: null;
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
			this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 4, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 4, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, chunkBox);
				}
			}
		}
	}

	public static class CorridorExit extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 13;
		private static final int SIZE_Y = 14;
		private static final int SIZE_Z = 13;

		public CorridorExit(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_EXIT, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public CorridorExit(NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_EXIT, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			this.fillForwardOpening((NetherFortressGenerator.Start)start, holder, random, 5, 3, true);
		}

		public static NetherFortressGenerator.CorridorExit create(
			StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -5, -3, 0, 13, 14, 13, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.CorridorExit(chainLength, random, blockBox, orientation)
				: null;
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
			this.fillWithOutline(world, chunkBox, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 5, 0, 12, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 5, 8, 0, 7, 8, 0, Blocks.NETHER_BRICK_FENCE.getDefaultState(), Blocks.NETHER_BRICK_FENCE.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));

			for (int i = 1; i <= 11; i += 2) {
				this.fillWithOutline(world, chunkBox, i, 10, 0, i, 11, 0, blockState, blockState, false);
				this.fillWithOutline(world, chunkBox, i, 10, 12, i, 11, 12, blockState, blockState, false);
				this.fillWithOutline(world, chunkBox, 0, 10, i, 0, 11, i, blockState2, blockState2, false);
				this.fillWithOutline(world, chunkBox, 12, 10, i, 12, 11, i, blockState2, blockState2, false);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 0, chunkBox);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 12, chunkBox);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 0, 13, i, chunkBox);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 12, 13, i, chunkBox);
				if (i != 11) {
					this.addBlock(world, blockState, i + 1, 13, 0, chunkBox);
					this.addBlock(world, blockState, i + 1, 13, 12, chunkBox);
					this.addBlock(world, blockState2, 0, 13, i + 1, chunkBox);
					this.addBlock(world, blockState2, 12, 13, i + 1, chunkBox);
				}
			}

			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
				0,
				13,
				0,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
				0,
				13,
				12,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
				12,
				13,
				12,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
				12,
				13,
				0,
				chunkBox
			);

			for (int ix = 3; ix <= 9; ix += 2) {
				this.fillWithOutline(
					world,
					chunkBox,
					1,
					7,
					ix,
					1,
					8,
					ix,
					blockState2.with(FenceBlock.WEST, Boolean.valueOf(true)),
					blockState2.with(FenceBlock.WEST, Boolean.valueOf(true)),
					false
				);
				this.fillWithOutline(
					world,
					chunkBox,
					11,
					7,
					ix,
					11,
					8,
					ix,
					blockState2.with(FenceBlock.EAST, Boolean.valueOf(true)),
					blockState2.with(FenceBlock.EAST, Boolean.valueOf(true)),
					false
				);
			}

			this.fillWithOutline(world, chunkBox, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int ix = 4; ix <= 8; ix++) {
				for (int j = 0; j <= 2; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), ix, -1, j, chunkBox);
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), ix, -1, 12 - j, chunkBox);
				}
			}

			for (int ix = 0; ix <= 2; ix++) {
				for (int j = 4; j <= 8; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), ix, -1, j, chunkBox);
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), 12 - ix, -1, j, chunkBox);
				}
			}

			this.fillWithOutline(world, chunkBox, 5, 5, 5, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 6, 1, 6, 6, 4, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 6, 0, 6, chunkBox);
			this.addBlock(world, Blocks.LAVA.getDefaultState(), 6, 5, 6, chunkBox);
			BlockPos blockPos = this.offsetPos(6, 5, 6);
			if (chunkBox.contains(blockPos)) {
				world.createAndScheduleFluidTick(blockPos, Fluids.LAVA, 0);
			}
		}
	}

	public static class CorridorLeftTurn extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 5;
		private static final int SIZE_Y = 7;
		private static final int SIZE_Z = 5;
		private boolean containsChest;

		public CorridorLeftTurn(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_LEFT_TURN, chainLength, boundingBox);
			this.setOrientation(orientation);
			this.containsChest = random.nextInt(3) == 0;
		}

		public CorridorLeftTurn(NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_LEFT_TURN, nbt);
			this.containsChest = nbt.getBoolean("Chest");
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			super.writeNbt(context, nbt);
			nbt.putBoolean("Chest", this.containsChest);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			this.fillNWOpening((NetherFortressGenerator.Start)start, holder, random, 0, 1, true);
		}

		public static NetherFortressGenerator.CorridorLeftTurn create(
			StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, 0, 0, 5, 7, 5, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.CorridorLeftTurn(chainLength, random, blockBox, orientation)
				: null;
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
			this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(world, chunkBox, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 4, 3, 1, 4, 4, 1, blockState2, blockState2, false);
			this.fillWithOutline(world, chunkBox, 4, 3, 3, 4, 4, 3, blockState2, blockState2, false);
			this.fillWithOutline(world, chunkBox, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 4, 3, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 3, 4, 1, 4, 4, blockState, blockState, false);
			this.fillWithOutline(world, chunkBox, 3, 3, 4, 3, 4, 4, blockState, blockState, false);
			if (this.containsChest && chunkBox.contains(this.offsetPos(3, 2, 3))) {
				this.containsChest = false;
				this.addChest(world, chunkBox, random, 3, 2, 3, LootTables.NETHER_BRIDGE_CHEST);
			}

			this.fillWithOutline(world, chunkBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, chunkBox);
				}
			}
		}
	}

	public static class CorridorNetherWartsRoom extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 13;
		private static final int SIZE_Y = 14;
		private static final int SIZE_Z = 13;

		public CorridorNetherWartsRoom(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public CorridorNetherWartsRoom(NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			this.fillForwardOpening((NetherFortressGenerator.Start)start, holder, random, 5, 3, true);
			this.fillForwardOpening((NetherFortressGenerator.Start)start, holder, random, 5, 11, true);
		}

		public static NetherFortressGenerator.CorridorNetherWartsRoom create(
			StructurePiecesHolder holder, int x, int y, int z, Direction orientation, int chainlength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -5, -3, 0, 13, 14, 13, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.CorridorNetherWartsRoom(chainlength, blockBox, orientation)
				: null;
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
			this.fillWithOutline(world, chunkBox, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 5, 0, 12, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			BlockState blockState3 = blockState2.with(FenceBlock.WEST, Boolean.valueOf(true));
			BlockState blockState4 = blockState2.with(FenceBlock.EAST, Boolean.valueOf(true));

			for (int i = 1; i <= 11; i += 2) {
				this.fillWithOutline(world, chunkBox, i, 10, 0, i, 11, 0, blockState, blockState, false);
				this.fillWithOutline(world, chunkBox, i, 10, 12, i, 11, 12, blockState, blockState, false);
				this.fillWithOutline(world, chunkBox, 0, 10, i, 0, 11, i, blockState2, blockState2, false);
				this.fillWithOutline(world, chunkBox, 12, 10, i, 12, 11, i, blockState2, blockState2, false);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 0, chunkBox);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 12, chunkBox);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 0, 13, i, chunkBox);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 12, 13, i, chunkBox);
				if (i != 11) {
					this.addBlock(world, blockState, i + 1, 13, 0, chunkBox);
					this.addBlock(world, blockState, i + 1, 13, 12, chunkBox);
					this.addBlock(world, blockState2, 0, 13, i + 1, chunkBox);
					this.addBlock(world, blockState2, 12, 13, i + 1, chunkBox);
				}
			}

			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
				0,
				13,
				0,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
				0,
				13,
				12,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
				12,
				13,
				12,
				chunkBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
				12,
				13,
				0,
				chunkBox
			);

			for (int ix = 3; ix <= 9; ix += 2) {
				this.fillWithOutline(world, chunkBox, 1, 7, ix, 1, 8, ix, blockState3, blockState3, false);
				this.fillWithOutline(world, chunkBox, 11, 7, ix, 11, 8, ix, blockState4, blockState4, false);
			}

			BlockState blockState5 = Blocks.NETHER_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);

			for (int j = 0; j <= 6; j++) {
				int k = j + 4;

				for (int l = 5; l <= 7; l++) {
					this.addBlock(world, blockState5, l, 5 + j, k, chunkBox);
				}

				if (k >= 5 && k <= 8) {
					this.fillWithOutline(world, chunkBox, 5, 5, k, 7, j + 4, k, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				} else if (k >= 9 && k <= 10) {
					this.fillWithOutline(world, chunkBox, 5, 8, k, 7, j + 4, k, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				}

				if (j >= 1) {
					this.fillWithOutline(world, chunkBox, 5, 6 + j, k, 7, 9 + j, k, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				}
			}

			for (int j = 5; j <= 7; j++) {
				this.addBlock(world, blockState5, j, 12, 11, chunkBox);
			}

			this.fillWithOutline(world, chunkBox, 5, 6, 7, 5, 7, 7, blockState4, blockState4, false);
			this.fillWithOutline(world, chunkBox, 7, 6, 7, 7, 7, 7, blockState3, blockState3, false);
			this.fillWithOutline(world, chunkBox, 5, 13, 12, 7, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 2, 5, 2, 3, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 2, 5, 9, 3, 5, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 2, 5, 4, 2, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 9, 5, 2, 10, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 9, 5, 9, 10, 5, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 10, 5, 4, 10, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState6 = blockState5.with(StairsBlock.FACING, Direction.EAST);
			BlockState blockState7 = blockState5.with(StairsBlock.FACING, Direction.WEST);
			this.addBlock(world, blockState7, 4, 5, 2, chunkBox);
			this.addBlock(world, blockState7, 4, 5, 3, chunkBox);
			this.addBlock(world, blockState7, 4, 5, 9, chunkBox);
			this.addBlock(world, blockState7, 4, 5, 10, chunkBox);
			this.addBlock(world, blockState6, 8, 5, 2, chunkBox);
			this.addBlock(world, blockState6, 8, 5, 3, chunkBox);
			this.addBlock(world, blockState6, 8, 5, 9, chunkBox);
			this.addBlock(world, blockState6, 8, 5, 10, chunkBox);
			this.fillWithOutline(world, chunkBox, 3, 4, 4, 4, 4, 8, Blocks.SOUL_SAND.getDefaultState(), Blocks.SOUL_SAND.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 8, 4, 4, 9, 4, 8, Blocks.SOUL_SAND.getDefaultState(), Blocks.SOUL_SAND.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 3, 5, 4, 4, 5, 8, Blocks.NETHER_WART.getDefaultState(), Blocks.NETHER_WART.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 8, 5, 4, 9, 5, 8, Blocks.NETHER_WART.getDefaultState(), Blocks.NETHER_WART.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int l = 4; l <= 8; l++) {
				for (int m = 0; m <= 2; m++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), l, -1, m, chunkBox);
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), l, -1, 12 - m, chunkBox);
				}
			}

			for (int l = 0; l <= 2; l++) {
				for (int m = 4; m <= 8; m++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), l, -1, m, chunkBox);
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), 12 - l, -1, m, chunkBox);
				}
			}
		}
	}

	public static class CorridorRightTurn extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 5;
		private static final int SIZE_Y = 7;
		private static final int SIZE_Z = 5;
		private boolean containsChest;

		public CorridorRightTurn(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_RIGHT_TURN, chainLength, boundingBox);
			this.setOrientation(orientation);
			this.containsChest = random.nextInt(3) == 0;
		}

		public CorridorRightTurn(NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_RIGHT_TURN, nbt);
			this.containsChest = nbt.getBoolean("Chest");
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			super.writeNbt(context, nbt);
			nbt.putBoolean("Chest", this.containsChest);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			this.fillSEOpening((NetherFortressGenerator.Start)start, holder, random, 0, 1, true);
		}

		public static NetherFortressGenerator.CorridorRightTurn create(
			StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, 0, 0, 5, 7, 5, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.CorridorRightTurn(chainLength, random, blockBox, orientation)
				: null;
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
			this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(world, chunkBox, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 3, 1, 0, 4, 1, blockState2, blockState2, false);
			this.fillWithOutline(world, chunkBox, 0, 3, 3, 0, 4, 3, blockState2, blockState2, false);
			this.fillWithOutline(world, chunkBox, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 3, 4, 1, 4, 4, blockState, blockState, false);
			this.fillWithOutline(world, chunkBox, 3, 3, 4, 3, 4, 4, blockState, blockState, false);
			if (this.containsChest && chunkBox.contains(this.offsetPos(1, 2, 3))) {
				this.containsChest = false;
				this.addChest(world, chunkBox, random, 1, 2, 3, LootTables.NETHER_BRIDGE_CHEST);
			}

			this.fillWithOutline(world, chunkBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, chunkBox);
				}
			}
		}
	}

	public static class CorridorStairs extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 5;
		private static final int SIZE_Y = 14;
		private static final int SIZE_Z = 10;

		public CorridorStairs(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_STAIRS, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public CorridorStairs(NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_STAIRS, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			this.fillForwardOpening((NetherFortressGenerator.Start)start, holder, random, 1, 0, true);
		}

		public static NetherFortressGenerator.CorridorStairs create(StructurePiecesHolder holder, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -7, 0, 5, 14, 10, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.CorridorStairs(chainLength, blockBox, orientation)
				: null;
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
			BlockState blockState = Blocks.NETHER_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));

			for (int i = 0; i <= 9; i++) {
				int j = Math.max(1, 7 - i);
				int k = Math.min(Math.max(j + 5, 14 - i), 13);
				int l = i;
				this.fillWithOutline(world, chunkBox, 0, 0, i, 4, j, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				this.fillWithOutline(world, chunkBox, 1, j + 1, i, 3, k - 1, i, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				if (i <= 6) {
					this.addBlock(world, blockState, 1, j + 1, i, chunkBox);
					this.addBlock(world, blockState, 2, j + 1, i, chunkBox);
					this.addBlock(world, blockState, 3, j + 1, i, chunkBox);
				}

				this.fillWithOutline(world, chunkBox, 0, k, i, 4, k, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				this.fillWithOutline(world, chunkBox, 0, j + 1, i, 0, k - 1, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				this.fillWithOutline(world, chunkBox, 4, j + 1, i, 4, k - 1, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				if ((i & 1) == 0) {
					this.fillWithOutline(world, chunkBox, 0, j + 2, i, 0, j + 3, i, blockState2, blockState2, false);
					this.fillWithOutline(world, chunkBox, 4, j + 2, i, 4, j + 3, i, blockState2, blockState2, false);
				}

				for (int m = 0; m <= 4; m++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), m, -1, l, chunkBox);
				}
			}
		}
	}

	abstract static class Piece extends StructurePiece {
		protected Piece(StructurePieceType structurePieceType, int i, BlockBox blockBox) {
			super(structurePieceType, i, blockBox);
		}

		public Piece(StructurePieceType structurePieceType, NbtCompound nbtCompound) {
			super(structurePieceType, nbtCompound);
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
		}

		private int checkRemainingPieces(List<NetherFortressGenerator.PieceData> possiblePieces) {
			boolean bl = false;
			int i = 0;

			for (NetherFortressGenerator.PieceData pieceData : possiblePieces) {
				if (pieceData.limit > 0 && pieceData.generatedCount < pieceData.limit) {
					bl = true;
				}

				i += pieceData.weight;
			}

			return bl ? i : -1;
		}

		private NetherFortressGenerator.Piece pickPiece(
			NetherFortressGenerator.Start start,
			List<NetherFortressGenerator.PieceData> possiblePieces,
			StructurePiecesHolder holder,
			Random random,
			int x,
			int y,
			int z,
			Direction orientation,
			int chainLength
		) {
			int i = this.checkRemainingPieces(possiblePieces);
			boolean bl = i > 0 && chainLength <= 30;
			int j = 0;

			while (j < 5 && bl) {
				j++;
				int k = random.nextInt(i);

				for (NetherFortressGenerator.PieceData pieceData : possiblePieces) {
					k -= pieceData.weight;
					if (k < 0) {
						if (!pieceData.canGenerate(chainLength) || pieceData == start.lastPiece && !pieceData.repeatable) {
							break;
						}

						NetherFortressGenerator.Piece piece = NetherFortressGenerator.createPiece(pieceData, holder, random, x, y, z, orientation, chainLength);
						if (piece != null) {
							pieceData.generatedCount++;
							start.lastPiece = pieceData;
							if (!pieceData.canGenerate()) {
								possiblePieces.remove(pieceData);
							}

							return piece;
						}
					}
				}
			}

			return NetherFortressGenerator.BridgeEnd.create(holder, random, x, y, z, orientation, chainLength);
		}

		private StructurePiece pieceGenerator(
			NetherFortressGenerator.Start start,
			StructurePiecesHolder holder,
			Random random,
			int x,
			int y,
			int z,
			@Nullable Direction orientation,
			int chainLength,
			boolean inside
		) {
			if (Math.abs(x - start.getBoundingBox().getMinX()) <= 112 && Math.abs(z - start.getBoundingBox().getMinZ()) <= 112) {
				List<NetherFortressGenerator.PieceData> list = start.bridgePieces;
				if (inside) {
					list = start.corridorPieces;
				}

				StructurePiece structurePiece = this.pickPiece(start, list, holder, random, x, y, z, orientation, chainLength + 1);
				if (structurePiece != null) {
					holder.addPiece(structurePiece);
					start.pieces.add(structurePiece);
				}

				return structurePiece;
			} else {
				return NetherFortressGenerator.BridgeEnd.create(holder, random, x, y, z, orientation, chainLength);
			}
		}

		@Nullable
		protected StructurePiece fillForwardOpening(
			NetherFortressGenerator.Start start, StructurePiecesHolder holder, Random random, int leftRightOffset, int heightOffset, boolean inside
		) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return this.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMinX() + leftRightOffset,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() - 1,
							direction,
							this.getChainLength(),
							inside
						);
					case SOUTH:
						return this.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMinX() + leftRightOffset,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMaxZ() + 1,
							direction,
							this.getChainLength(),
							inside
						);
					case WEST:
						return this.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMinX() - 1,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() + leftRightOffset,
							direction,
							this.getChainLength(),
							inside
						);
					case EAST:
						return this.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMaxX() + 1,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() + leftRightOffset,
							direction,
							this.getChainLength(),
							inside
						);
				}
			}

			return null;
		}

		@Nullable
		protected StructurePiece fillNWOpening(
			NetherFortressGenerator.Start start, StructurePiecesHolder holder, Random random, int heightOffset, int leftRightOffset, boolean inside
		) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return this.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMinX() - 1,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() + leftRightOffset,
							Direction.WEST,
							this.getChainLength(),
							inside
						);
					case SOUTH:
						return this.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMinX() - 1,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() + leftRightOffset,
							Direction.WEST,
							this.getChainLength(),
							inside
						);
					case WEST:
						return this.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMinX() + leftRightOffset,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() - 1,
							Direction.NORTH,
							this.getChainLength(),
							inside
						);
					case EAST:
						return this.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMinX() + leftRightOffset,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() - 1,
							Direction.NORTH,
							this.getChainLength(),
							inside
						);
				}
			}

			return null;
		}

		@Nullable
		protected StructurePiece fillSEOpening(
			NetherFortressGenerator.Start start, StructurePiecesHolder holder, Random random, int heightOffset, int leftRightOffset, boolean inside
		) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return this.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMaxX() + 1,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() + leftRightOffset,
							Direction.EAST,
							this.getChainLength(),
							inside
						);
					case SOUTH:
						return this.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMaxX() + 1,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() + leftRightOffset,
							Direction.EAST,
							this.getChainLength(),
							inside
						);
					case WEST:
						return this.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMinX() + leftRightOffset,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMaxZ() + 1,
							Direction.SOUTH,
							this.getChainLength(),
							inside
						);
					case EAST:
						return this.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMinX() + leftRightOffset,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMaxZ() + 1,
							Direction.SOUTH,
							this.getChainLength(),
							inside
						);
				}
			}

			return null;
		}

		protected static boolean isInBounds(BlockBox boundingBox) {
			return boundingBox != null && boundingBox.getMinY() > 10;
		}
	}

	static class PieceData {
		public final Class<? extends NetherFortressGenerator.Piece> pieceType;
		public final int weight;
		public int generatedCount;
		public final int limit;
		public final boolean repeatable;

		public PieceData(Class<? extends NetherFortressGenerator.Piece> pieceType, int weight, int limit, boolean repeatable) {
			this.pieceType = pieceType;
			this.weight = weight;
			this.limit = limit;
			this.repeatable = repeatable;
		}

		public PieceData(Class<? extends NetherFortressGenerator.Piece> pieceType, int weight, int limit) {
			this(pieceType, weight, limit, false);
		}

		public boolean canGenerate(int chainLength) {
			return this.limit == 0 || this.generatedCount < this.limit;
		}

		public boolean canGenerate() {
			return this.limit == 0 || this.generatedCount < this.limit;
		}
	}

	public static class SmallCorridor extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 5;
		private static final int SIZE_Y = 7;
		private static final int SIZE_Z = 5;

		public SmallCorridor(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_SMALL_CORRIDOR, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public SmallCorridor(NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_SMALL_CORRIDOR, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			this.fillForwardOpening((NetherFortressGenerator.Start)start, holder, random, 1, 0, true);
		}

		public static NetherFortressGenerator.SmallCorridor create(StructurePiecesHolder holder, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, 0, 0, 5, 7, 5, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.SmallCorridor(chainLength, blockBox, orientation)
				: null;
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
			this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(world, chunkBox, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 0, 3, 1, 0, 4, 1, blockState, blockState, false);
			this.fillWithOutline(world, chunkBox, 0, 3, 3, 0, 4, 3, blockState, blockState, false);
			this.fillWithOutline(world, chunkBox, 4, 3, 1, 4, 4, 1, blockState, blockState, false);
			this.fillWithOutline(world, chunkBox, 4, 3, 3, 4, 4, 3, blockState, blockState, false);
			this.fillWithOutline(world, chunkBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, chunkBox);
				}
			}
		}
	}

	public static class Start extends NetherFortressGenerator.BridgeCrossing {
		public NetherFortressGenerator.PieceData lastPiece;
		public List<NetherFortressGenerator.PieceData> bridgePieces;
		public List<NetherFortressGenerator.PieceData> corridorPieces;
		public final List<StructurePiece> pieces = Lists.<StructurePiece>newArrayList();

		public Start(Random random, int x, int z) {
			super(x, z, getRandomHorizontalDirection(random));
			this.bridgePieces = Lists.<NetherFortressGenerator.PieceData>newArrayList();

			for (NetherFortressGenerator.PieceData pieceData : NetherFortressGenerator.ALL_BRIDGE_PIECES) {
				pieceData.generatedCount = 0;
				this.bridgePieces.add(pieceData);
			}

			this.corridorPieces = Lists.<NetherFortressGenerator.PieceData>newArrayList();

			for (NetherFortressGenerator.PieceData pieceData : NetherFortressGenerator.ALL_CORRIDOR_PIECES) {
				pieceData.generatedCount = 0;
				this.corridorPieces.add(pieceData);
			}
		}

		public Start(NbtCompound nbtCompound) {
			super(StructurePieceType.NETHER_FORTRESS_START, nbtCompound);
		}
	}
}
