package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class NetherFortressGenerator {
	private static final NetherFortressGenerator.PieceData[] field_14494 = new NetherFortressGenerator.PieceData[]{
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.Bridge.class, 30, 0, true),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.BridgeCrossing.class, 10, 4),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.BridgeSmallCrossing.class, 10, 4),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.BridgeStairs.class, 10, 3),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.BridgePlatform.class, 5, 2),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorExit.class, 5, 1)
	};
	private static final NetherFortressGenerator.PieceData[] field_14493 = new NetherFortressGenerator.PieceData[]{
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.SmallCorridor.class, 25, 0, true),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorCrossing.class, 15, 5),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorRightTurn.class, 5, 10),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorLeftTurn.class, 5, 10),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorStairs.class, 10, 3, true),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorBalcony.class, 7, 2),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorNetherWartsRoom.class, 5, 2)
	};

	private static NetherFortressGenerator.Piece generatePiece(
		NetherFortressGenerator.PieceData pieceData, List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l
	) {
		Class<? extends NetherFortressGenerator.Piece> class_ = pieceData.pieceType;
		NetherFortressGenerator.Piece piece = null;
		if (class_ == NetherFortressGenerator.Bridge.class) {
			piece = NetherFortressGenerator.Bridge.method_14798(list, random, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.BridgeCrossing.class) {
			piece = NetherFortressGenerator.BridgeCrossing.method_14796(list, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.BridgeSmallCrossing.class) {
			piece = NetherFortressGenerator.BridgeSmallCrossing.method_14817(list, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.BridgeStairs.class) {
			piece = NetherFortressGenerator.BridgeStairs.method_14818(list, i, j, k, l, direction);
		} else if (class_ == NetherFortressGenerator.BridgePlatform.class) {
			piece = NetherFortressGenerator.BridgePlatform.method_14807(list, i, j, k, l, direction);
		} else if (class_ == NetherFortressGenerator.CorridorExit.class) {
			piece = NetherFortressGenerator.CorridorExit.method_14801(list, random, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.SmallCorridor.class) {
			piece = NetherFortressGenerator.SmallCorridor.method_14804(list, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.CorridorRightTurn.class) {
			piece = NetherFortressGenerator.CorridorRightTurn.method_14805(list, random, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.CorridorLeftTurn.class) {
			piece = NetherFortressGenerator.CorridorLeftTurn.method_14803(list, random, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.CorridorStairs.class) {
			piece = NetherFortressGenerator.CorridorStairs.method_14799(list, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.CorridorBalcony.class) {
			piece = NetherFortressGenerator.CorridorBalcony.method_14800(list, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.CorridorCrossing.class) {
			piece = NetherFortressGenerator.CorridorCrossing.method_14802(list, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.CorridorNetherWartsRoom.class) {
			piece = NetherFortressGenerator.CorridorNetherWartsRoom.method_14806(list, i, j, k, direction, l);
		}

		return piece;
	}

	public static class Bridge extends NetherFortressGenerator.Piece {
		public Bridge(int i, Random random, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE, i);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
		}

		public Bridge(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE, compoundTag);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14814((NetherFortressGenerator.Start)structurePiece, list, random, 1, 3, false);
		}

		public static NetherFortressGenerator.Bridge method_14798(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -3, 0, 5, 10, 19, direction);
			return method_14809(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new NetherFortressGenerator.Bridge(l, random, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			IWorld world,
			StructureAccessor structureAccessor,
			ChunkGenerator<?> chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(world, boundingBox, 0, 3, 0, 4, 4, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 5, 0, 3, 7, 18, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 0, 0, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 5, 0, 4, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 4, 2, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 13, 4, 2, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 0, 15, 4, 1, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 2; j++) {
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, 18 - j, boundingBox);
				}
			}

			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			BlockState blockState2 = blockState.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState3 = blockState.with(FenceBlock.WEST, Boolean.valueOf(true));
			this.fillWithOutline(world, boundingBox, 0, 1, 1, 0, 4, 1, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 0, 3, 4, 0, 4, 4, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 0, 3, 14, 0, 4, 14, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 0, 1, 17, 0, 4, 17, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 4, 1, 1, 4, 4, 1, blockState3, blockState3, false);
			this.fillWithOutline(world, boundingBox, 4, 3, 4, 4, 4, 4, blockState3, blockState3, false);
			this.fillWithOutline(world, boundingBox, 4, 3, 14, 4, 4, 14, blockState3, blockState3, false);
			this.fillWithOutline(world, boundingBox, 4, 1, 17, 4, 4, 17, blockState3, blockState3, false);
			return true;
		}
	}

	public static class BridgeCrossing extends NetherFortressGenerator.Piece {
		public BridgeCrossing(int i, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, i);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
		}

		protected BridgeCrossing(Random random, int x, int z) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, 0);
			this.setOrientation(Direction.Type.HORIZONTAL.random(random));
			if (this.getFacing().getAxis() == Direction.Axis.Z) {
				this.boundingBox = new BlockBox(x, 64, z, x + 19 - 1, 73, z + 19 - 1);
			} else {
				this.boundingBox = new BlockBox(x, 64, z, x + 19 - 1, 73, z + 19 - 1);
			}
		}

		protected BridgeCrossing(StructurePieceType structurePieceType, CompoundTag compoundTag) {
			super(structurePieceType, compoundTag);
		}

		public BridgeCrossing(StructureManager structureManager, CompoundTag compoundTag) {
			this(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, compoundTag);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14814((NetherFortressGenerator.Start)structurePiece, list, random, 8, 3, false);
			this.method_14812((NetherFortressGenerator.Start)structurePiece, list, random, 3, 8, false);
			this.method_14808((NetherFortressGenerator.Start)structurePiece, list, random, 3, 8, false);
		}

		public static NetherFortressGenerator.BridgeCrossing method_14796(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -8, -3, 0, 19, 10, 19, direction);
			return method_14809(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new NetherFortressGenerator.BridgeCrossing(l, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			IWorld world,
			StructureAccessor structureAccessor,
			ChunkGenerator<?> chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(world, boundingBox, 7, 3, 0, 11, 4, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 3, 7, 18, 4, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 5, 0, 10, 7, 18, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 8, 18, 7, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 7, 5, 0, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 7, 5, 11, 7, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 11, 5, 0, 11, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 11, 5, 11, 11, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 7, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 11, 5, 7, 18, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 11, 7, 5, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 11, 5, 11, 18, 5, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 7, 2, 0, 11, 2, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 7, 2, 13, 11, 2, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 7, 0, 0, 11, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 7, 0, 15, 11, 1, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 7; i <= 11; i++) {
				for (int j = 0; j <= 2; j++) {
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, 18 - j, boundingBox);
				}
			}

			this.fillWithOutline(world, boundingBox, 0, 2, 7, 5, 2, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 13, 2, 7, 18, 2, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 0, 7, 3, 1, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 15, 0, 7, 18, 1, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 2; i++) {
				for (int j = 7; j <= 11; j++) {
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), 18 - i, -1, j, boundingBox);
				}
			}

			return true;
		}
	}

	public static class BridgeEnd extends NetherFortressGenerator.Piece {
		private final int seed;

		public BridgeEnd(int i, Random random, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END, i);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
			this.seed = random.nextInt();
		}

		public BridgeEnd(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END, compoundTag);
			this.seed = compoundTag.getInt("Seed");
		}

		public static NetherFortressGenerator.BridgeEnd method_14797(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -3, 0, 5, 10, 8, direction);
			return method_14809(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new NetherFortressGenerator.BridgeEnd(l, random, blockBox, direction)
				: null;
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putInt("Seed", this.seed);
		}

		@Override
		public boolean generate(
			IWorld world,
			StructureAccessor structureAccessor,
			ChunkGenerator<?> chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			Random random2 = new Random((long)this.seed);

			for (int i = 0; i <= 4; i++) {
				for (int j = 3; j <= 4; j++) {
					int k = random2.nextInt(8);
					this.fillWithOutline(world, boundingBox, i, j, 0, i, j, k, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				}
			}

			int i = random2.nextInt(8);
			this.fillWithOutline(world, boundingBox, 0, 5, 0, 0, 5, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			i = random2.nextInt(8);
			this.fillWithOutline(world, boundingBox, 4, 5, 0, 4, 5, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int ix = 0; ix <= 4; ix++) {
				int j = random2.nextInt(5);
				this.fillWithOutline(world, boundingBox, ix, 2, 0, ix, 2, j, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			}

			for (int ix = 0; ix <= 4; ix++) {
				for (int j = 0; j <= 1; j++) {
					int k = random2.nextInt(3);
					this.fillWithOutline(world, boundingBox, ix, j, 0, ix, j, k, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				}
			}

			return true;
		}
	}

	public static class BridgePlatform extends NetherFortressGenerator.Piece {
		private boolean hasBlazeSpawner;

		public BridgePlatform(int i, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_PLATFORM, i);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
		}

		public BridgePlatform(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_PLATFORM, compoundTag);
			this.hasBlazeSpawner = compoundTag.getBoolean("Mob");
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putBoolean("Mob", this.hasBlazeSpawner);
		}

		public static NetherFortressGenerator.BridgePlatform method_14807(List<StructurePiece> list, int i, int j, int k, int l, Direction direction) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -2, 0, 0, 7, 8, 9, direction);
			return method_14809(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new NetherFortressGenerator.BridgePlatform(l, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			IWorld world,
			StructureAccessor structureAccessor,
			ChunkGenerator<?> chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 6, 7, 7, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 0, 0, 5, 1, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 2, 1, 5, 2, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 3, 2, 5, 3, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 4, 3, 5, 4, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 2, 0, 1, 4, 2, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 2, 0, 5, 4, 2, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 5, 2, 1, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 5, 2, 5, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 3, 0, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 5, 3, 6, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 5, 8, 5, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)), 1, 6, 3, boundingBox);
			this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)), 5, 6, 3, boundingBox);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)).with(FenceBlock.NORTH, Boolean.valueOf(true)),
				0,
				6,
				3,
				boundingBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.NORTH, Boolean.valueOf(true)),
				6,
				6,
				3,
				boundingBox
			);
			this.fillWithOutline(world, boundingBox, 0, 6, 4, 0, 6, 7, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 6, 6, 4, 6, 6, 7, blockState2, blockState2, false);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true)),
				0,
				6,
				8,
				boundingBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true)),
				6,
				6,
				8,
				boundingBox
			);
			this.fillWithOutline(world, boundingBox, 1, 6, 8, 5, 6, 8, blockState, blockState, false);
			this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)), 1, 7, 8, boundingBox);
			this.fillWithOutline(world, boundingBox, 2, 7, 8, 4, 7, 8, blockState, blockState, false);
			this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)), 5, 7, 8, boundingBox);
			this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)), 2, 8, 8, boundingBox);
			this.addBlock(world, blockState, 3, 8, 8, boundingBox);
			this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)), 4, 8, 8, boundingBox);
			if (!this.hasBlazeSpawner) {
				BlockPos blockPos2 = new BlockPos(this.applyXTransform(3, 5), this.applyYTransform(5), this.applyZTransform(3, 5));
				if (boundingBox.contains(blockPos2)) {
					this.hasBlazeSpawner = true;
					world.setBlockState(blockPos2, Blocks.SPAWNER.getDefaultState(), 2);
					BlockEntity blockEntity = world.getBlockEntity(blockPos2);
					if (blockEntity instanceof MobSpawnerBlockEntity) {
						((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(EntityType.BLAZE);
					}
				}
			}

			for (int i = 0; i <= 6; i++) {
				for (int j = 0; j <= 6; j++) {
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
				}
			}

			return true;
		}
	}

	public static class BridgeSmallCrossing extends NetherFortressGenerator.Piece {
		public BridgeSmallCrossing(int i, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING, i);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
		}

		public BridgeSmallCrossing(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING, compoundTag);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14814((NetherFortressGenerator.Start)structurePiece, list, random, 2, 0, false);
			this.method_14812((NetherFortressGenerator.Start)structurePiece, list, random, 0, 2, false);
			this.method_14808((NetherFortressGenerator.Start)structurePiece, list, random, 0, 2, false);
		}

		public static NetherFortressGenerator.BridgeSmallCrossing method_14817(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -2, 0, 0, 7, 9, 7, direction);
			return method_14809(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new NetherFortressGenerator.BridgeSmallCrossing(l, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			IWorld world,
			StructureAccessor structureAccessor,
			ChunkGenerator<?> chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 6, 7, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 1, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 6, 1, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 2, 0, 6, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 2, 6, 6, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 6, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 5, 0, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 2, 0, 6, 6, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 2, 5, 6, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(world, boundingBox, 2, 6, 0, 4, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 0, 4, 5, 0, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 2, 6, 6, 4, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 6, 4, 5, 6, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 0, 6, 2, 0, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 2, 0, 5, 4, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 6, 6, 2, 6, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 5, 2, 6, 5, 4, blockState2, blockState2, false);

			for (int i = 0; i <= 6; i++) {
				for (int j = 0; j <= 6; j++) {
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
				}
			}

			return true;
		}
	}

	public static class BridgeStairs extends NetherFortressGenerator.Piece {
		public BridgeStairs(int i, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STAIRS, i);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
		}

		public BridgeStairs(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STAIRS, compoundTag);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14808((NetherFortressGenerator.Start)structurePiece, list, random, 6, 2, false);
		}

		public static NetherFortressGenerator.BridgeStairs method_14818(List<StructurePiece> list, int i, int j, int k, int l, Direction direction) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -2, 0, 0, 7, 11, 7, direction);
			return method_14809(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new NetherFortressGenerator.BridgeStairs(l, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			IWorld world,
			StructureAccessor structureAccessor,
			ChunkGenerator<?> chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 6, 10, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 1, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 2, 0, 6, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 1, 0, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 2, 1, 6, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 2, 6, 5, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(world, boundingBox, 0, 3, 2, 0, 5, 4, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 6, 3, 2, 6, 5, 2, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 6, 3, 4, 6, 5, 4, blockState2, blockState2, false);
			this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 5, 2, 5, boundingBox);
			this.fillWithOutline(world, boundingBox, 4, 2, 5, 4, 3, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 3, 2, 5, 3, 4, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 2, 5, 2, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 2, 5, 1, 6, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 7, 1, 5, 7, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 8, 2, 6, 8, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 6, 0, 4, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 0, 4, 5, 0, blockState, blockState, false);

			for (int i = 0; i <= 6; i++) {
				for (int j = 0; j <= 6; j++) {
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
				}
			}

			return true;
		}
	}

	public static class CorridorBalcony extends NetherFortressGenerator.Piece {
		public CorridorBalcony(int i, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_BALCONY, i);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
		}

		public CorridorBalcony(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_BALCONY, compoundTag);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			int i = 1;
			Direction direction = this.getFacing();
			if (direction == Direction.WEST || direction == Direction.NORTH) {
				i = 5;
			}

			this.method_14812((NetherFortressGenerator.Start)structurePiece, list, random, 0, i, random.nextInt(8) > 0);
			this.method_14808((NetherFortressGenerator.Start)structurePiece, list, random, 0, i, random.nextInt(8) > 0);
		}

		public static NetherFortressGenerator.CorridorBalcony method_14800(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -3, 0, 0, 9, 7, 9, direction);
			return method_14809(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new NetherFortressGenerator.CorridorBalcony(l, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			IWorld world,
			StructureAccessor structureAccessor,
			ChunkGenerator<?> chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 8, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 8, 5, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 6, 0, 8, 6, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 2, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 2, 0, 8, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 3, 0, 1, 4, 0, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 7, 3, 0, 7, 4, 0, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 0, 2, 4, 8, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 1, 4, 2, 2, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 1, 4, 7, 2, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 3, 8, 7, 3, 8, blockState2, blockState2, false);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true)),
				0,
				3,
				8,
				boundingBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true)),
				8,
				3,
				8,
				boundingBox
			);
			this.fillWithOutline(world, boundingBox, 0, 3, 6, 0, 3, 7, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 8, 3, 6, 8, 3, 7, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 0, 3, 4, 0, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 3, 4, 8, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 3, 5, 2, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 3, 5, 7, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 4, 5, 1, 5, 5, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 7, 4, 5, 7, 5, 5, blockState2, blockState2, false);

			for (int i = 0; i <= 5; i++) {
				for (int j = 0; j <= 8; j++) {
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), j, -1, i, boundingBox);
				}
			}

			return true;
		}
	}

	public static class CorridorCrossing extends NetherFortressGenerator.Piece {
		public CorridorCrossing(int i, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_CROSSING, i);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
		}

		public CorridorCrossing(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_CROSSING, compoundTag);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14814((NetherFortressGenerator.Start)structurePiece, list, random, 1, 0, true);
			this.method_14812((NetherFortressGenerator.Start)structurePiece, list, random, 0, 1, true);
			this.method_14808((NetherFortressGenerator.Start)structurePiece, list, random, 0, 1, true);
		}

		public static NetherFortressGenerator.CorridorCrossing method_14802(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -1, 0, 0, 5, 7, 5, direction);
			return method_14809(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new NetherFortressGenerator.CorridorCrossing(l, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			IWorld world,
			StructureAccessor structureAccessor,
			ChunkGenerator<?> chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 4, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
				}
			}

			return true;
		}
	}

	public static class CorridorExit extends NetherFortressGenerator.Piece {
		public CorridorExit(int i, Random random, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_EXIT, i);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
		}

		public CorridorExit(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_EXIT, compoundTag);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14814((NetherFortressGenerator.Start)structurePiece, list, random, 5, 3, true);
		}

		public static NetherFortressGenerator.CorridorExit method_14801(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -5, -3, 0, 13, 14, 13, direction);
			return method_14809(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new NetherFortressGenerator.CorridorExit(l, random, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			IWorld world,
			StructureAccessor structureAccessor,
			ChunkGenerator<?> chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(world, boundingBox, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 0, 12, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 8, 0, 7, 8, 0, Blocks.NETHER_BRICK_FENCE.getDefaultState(), Blocks.NETHER_BRICK_FENCE.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));

			for (int i = 1; i <= 11; i += 2) {
				this.fillWithOutline(world, boundingBox, i, 10, 0, i, 11, 0, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, i, 10, 12, i, 11, 12, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 0, 10, i, 0, 11, i, blockState2, blockState2, false);
				this.fillWithOutline(world, boundingBox, 12, 10, i, 12, 11, i, blockState2, blockState2, false);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 0, boundingBox);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 12, boundingBox);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 0, 13, i, boundingBox);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 12, 13, i, boundingBox);
				if (i != 11) {
					this.addBlock(world, blockState, i + 1, 13, 0, boundingBox);
					this.addBlock(world, blockState, i + 1, 13, 12, boundingBox);
					this.addBlock(world, blockState2, 0, 13, i + 1, boundingBox);
					this.addBlock(world, blockState2, 12, 13, i + 1, boundingBox);
				}
			}

			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
				0,
				13,
				0,
				boundingBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
				0,
				13,
				12,
				boundingBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
				12,
				13,
				12,
				boundingBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
				12,
				13,
				0,
				boundingBox
			);

			for (int ix = 3; ix <= 9; ix += 2) {
				this.fillWithOutline(
					world,
					boundingBox,
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
					boundingBox,
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

			this.fillWithOutline(world, boundingBox, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int ix = 4; ix <= 8; ix++) {
				for (int j = 0; j <= 2; j++) {
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), ix, -1, j, boundingBox);
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), ix, -1, 12 - j, boundingBox);
				}
			}

			for (int ix = 0; ix <= 2; ix++) {
				for (int j = 4; j <= 8; j++) {
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), ix, -1, j, boundingBox);
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), 12 - ix, -1, j, boundingBox);
				}
			}

			this.fillWithOutline(world, boundingBox, 5, 5, 5, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 1, 6, 6, 4, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 6, 0, 6, boundingBox);
			this.addBlock(world, Blocks.LAVA.getDefaultState(), 6, 5, 6, boundingBox);
			BlockPos blockPos2 = new BlockPos(this.applyXTransform(6, 6), this.applyYTransform(5), this.applyZTransform(6, 6));
			if (boundingBox.contains(blockPos2)) {
				world.getFluidTickScheduler().schedule(blockPos2, Fluids.LAVA, 0);
			}

			return true;
		}
	}

	public static class CorridorLeftTurn extends NetherFortressGenerator.Piece {
		private boolean containsChest;

		public CorridorLeftTurn(int i, Random random, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_LEFT_TURN, i);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
			this.containsChest = random.nextInt(3) == 0;
		}

		public CorridorLeftTurn(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_LEFT_TURN, compoundTag);
			this.containsChest = compoundTag.getBoolean("Chest");
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putBoolean("Chest", this.containsChest);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14812((NetherFortressGenerator.Start)structurePiece, list, random, 0, 1, true);
		}

		public static NetherFortressGenerator.CorridorLeftTurn method_14803(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -1, 0, 0, 5, 7, 5, direction);
			return method_14809(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new NetherFortressGenerator.CorridorLeftTurn(l, random, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			IWorld world,
			StructureAccessor structureAccessor,
			ChunkGenerator<?> chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(world, boundingBox, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 3, 1, 4, 4, 1, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 4, 3, 3, 4, 4, 3, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 4, 3, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 3, 4, 1, 4, 4, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 3, 3, 4, 3, 4, 4, blockState, blockState, false);
			if (this.containsChest && boundingBox.contains(new BlockPos(this.applyXTransform(3, 3), this.applyYTransform(2), this.applyZTransform(3, 3)))) {
				this.containsChest = false;
				this.addChest(world, boundingBox, random, 3, 2, 3, LootTables.NETHER_BRIDGE_CHEST);
			}

			this.fillWithOutline(world, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
				}
			}

			return true;
		}
	}

	public static class CorridorNetherWartsRoom extends NetherFortressGenerator.Piece {
		public CorridorNetherWartsRoom(int i, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM, i);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
		}

		public CorridorNetherWartsRoom(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM, compoundTag);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14814((NetherFortressGenerator.Start)structurePiece, list, random, 5, 3, true);
			this.method_14814((NetherFortressGenerator.Start)structurePiece, list, random, 5, 11, true);
		}

		public static NetherFortressGenerator.CorridorNetherWartsRoom method_14806(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -5, -3, 0, 13, 14, 13, direction);
			return method_14809(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new NetherFortressGenerator.CorridorNetherWartsRoom(l, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			IWorld world,
			StructureAccessor structureAccessor,
			ChunkGenerator<?> chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(world, boundingBox, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 0, 12, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
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
				this.fillWithOutline(world, boundingBox, i, 10, 0, i, 11, 0, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, i, 10, 12, i, 11, 12, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 0, 10, i, 0, 11, i, blockState2, blockState2, false);
				this.fillWithOutline(world, boundingBox, 12, 10, i, 12, 11, i, blockState2, blockState2, false);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 0, boundingBox);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 12, boundingBox);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 0, 13, i, boundingBox);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 12, 13, i, boundingBox);
				if (i != 11) {
					this.addBlock(world, blockState, i + 1, 13, 0, boundingBox);
					this.addBlock(world, blockState, i + 1, 13, 12, boundingBox);
					this.addBlock(world, blockState2, 0, 13, i + 1, boundingBox);
					this.addBlock(world, blockState2, 12, 13, i + 1, boundingBox);
				}
			}

			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
				0,
				13,
				0,
				boundingBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
				0,
				13,
				12,
				boundingBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
				12,
				13,
				12,
				boundingBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
				12,
				13,
				0,
				boundingBox
			);

			for (int ix = 3; ix <= 9; ix += 2) {
				this.fillWithOutline(world, boundingBox, 1, 7, ix, 1, 8, ix, blockState3, blockState3, false);
				this.fillWithOutline(world, boundingBox, 11, 7, ix, 11, 8, ix, blockState4, blockState4, false);
			}

			BlockState blockState5 = Blocks.NETHER_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);

			for (int j = 0; j <= 6; j++) {
				int k = j + 4;

				for (int l = 5; l <= 7; l++) {
					this.addBlock(world, blockState5, l, 5 + j, k, boundingBox);
				}

				if (k >= 5 && k <= 8) {
					this.fillWithOutline(world, boundingBox, 5, 5, k, 7, j + 4, k, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				} else if (k >= 9 && k <= 10) {
					this.fillWithOutline(world, boundingBox, 5, 8, k, 7, j + 4, k, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				}

				if (j >= 1) {
					this.fillWithOutline(world, boundingBox, 5, 6 + j, k, 7, 9 + j, k, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				}
			}

			for (int j = 5; j <= 7; j++) {
				this.addBlock(world, blockState5, j, 12, 11, boundingBox);
			}

			this.fillWithOutline(world, boundingBox, 5, 6, 7, 5, 7, 7, blockState4, blockState4, false);
			this.fillWithOutline(world, boundingBox, 7, 6, 7, 7, 7, 7, blockState3, blockState3, false);
			this.fillWithOutline(world, boundingBox, 5, 13, 12, 7, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 2, 3, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 9, 3, 5, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 4, 2, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 9, 5, 2, 10, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 9, 5, 9, 10, 5, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 10, 5, 4, 10, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState6 = blockState5.with(StairsBlock.FACING, Direction.EAST);
			BlockState blockState7 = blockState5.with(StairsBlock.FACING, Direction.WEST);
			this.addBlock(world, blockState7, 4, 5, 2, boundingBox);
			this.addBlock(world, blockState7, 4, 5, 3, boundingBox);
			this.addBlock(world, blockState7, 4, 5, 9, boundingBox);
			this.addBlock(world, blockState7, 4, 5, 10, boundingBox);
			this.addBlock(world, blockState6, 8, 5, 2, boundingBox);
			this.addBlock(world, blockState6, 8, 5, 3, boundingBox);
			this.addBlock(world, blockState6, 8, 5, 9, boundingBox);
			this.addBlock(world, blockState6, 8, 5, 10, boundingBox);
			this.fillWithOutline(world, boundingBox, 3, 4, 4, 4, 4, 8, Blocks.SOUL_SAND.getDefaultState(), Blocks.SOUL_SAND.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 4, 4, 9, 4, 8, Blocks.SOUL_SAND.getDefaultState(), Blocks.SOUL_SAND.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 3, 5, 4, 4, 5, 8, Blocks.NETHER_WART.getDefaultState(), Blocks.NETHER_WART.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 5, 4, 9, 5, 8, Blocks.NETHER_WART.getDefaultState(), Blocks.NETHER_WART.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int l = 4; l <= 8; l++) {
				for (int m = 0; m <= 2; m++) {
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), l, -1, m, boundingBox);
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), l, -1, 12 - m, boundingBox);
				}
			}

			for (int l = 0; l <= 2; l++) {
				for (int m = 4; m <= 8; m++) {
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), l, -1, m, boundingBox);
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), 12 - l, -1, m, boundingBox);
				}
			}

			return true;
		}
	}

	public static class CorridorRightTurn extends NetherFortressGenerator.Piece {
		private boolean containsChest;

		public CorridorRightTurn(int i, Random random, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_RIGHT_TURN, i);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
			this.containsChest = random.nextInt(3) == 0;
		}

		public CorridorRightTurn(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_RIGHT_TURN, compoundTag);
			this.containsChest = compoundTag.getBoolean("Chest");
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putBoolean("Chest", this.containsChest);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14808((NetherFortressGenerator.Start)structurePiece, list, random, 0, 1, true);
		}

		public static NetherFortressGenerator.CorridorRightTurn method_14805(
			List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l
		) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -1, 0, 0, 5, 7, 5, direction);
			return method_14809(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new NetherFortressGenerator.CorridorRightTurn(l, random, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			IWorld world,
			StructureAccessor structureAccessor,
			ChunkGenerator<?> chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 3, 1, 0, 4, 1, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 0, 3, 3, 0, 4, 3, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 3, 4, 1, 4, 4, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 3, 3, 4, 3, 4, 4, blockState, blockState, false);
			if (this.containsChest && boundingBox.contains(new BlockPos(this.applyXTransform(1, 3), this.applyYTransform(2), this.applyZTransform(1, 3)))) {
				this.containsChest = false;
				this.addChest(world, boundingBox, random, 1, 2, 3, LootTables.NETHER_BRIDGE_CHEST);
			}

			this.fillWithOutline(world, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
				}
			}

			return true;
		}
	}

	public static class CorridorStairs extends NetherFortressGenerator.Piece {
		public CorridorStairs(int i, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_STAIRS, i);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
		}

		public CorridorStairs(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_STAIRS, compoundTag);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14814((NetherFortressGenerator.Start)structurePiece, list, random, 1, 0, true);
		}

		public static NetherFortressGenerator.CorridorStairs method_14799(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -7, 0, 5, 14, 10, direction);
			return method_14809(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new NetherFortressGenerator.CorridorStairs(l, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			IWorld world,
			StructureAccessor structureAccessor,
			ChunkGenerator<?> chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
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
				this.fillWithOutline(world, boundingBox, 0, 0, i, 4, j, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 1, j + 1, i, 3, k - 1, i, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				if (i <= 6) {
					this.addBlock(world, blockState, 1, j + 1, i, boundingBox);
					this.addBlock(world, blockState, 2, j + 1, i, boundingBox);
					this.addBlock(world, blockState, 3, j + 1, i, boundingBox);
				}

				this.fillWithOutline(world, boundingBox, 0, k, i, 4, k, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 0, j + 1, i, 0, k - 1, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 4, j + 1, i, 4, k - 1, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				if ((i & 1) == 0) {
					this.fillWithOutline(world, boundingBox, 0, j + 2, i, 0, j + 3, i, blockState2, blockState2, false);
					this.fillWithOutline(world, boundingBox, 4, j + 2, i, 4, j + 3, i, blockState2, blockState2, false);
				}

				for (int m = 0; m <= 4; m++) {
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), m, -1, l, boundingBox);
				}
			}

			return true;
		}
	}

	abstract static class Piece extends StructurePiece {
		protected Piece(StructurePieceType structurePieceType, int i) {
			super(structurePieceType, i);
		}

		public Piece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
			super(structurePieceType, compoundTag);
		}

		@Override
		protected void toNbt(CompoundTag tag) {
		}

		private int method_14810(List<NetherFortressGenerator.PieceData> list) {
			boolean bl = false;
			int i = 0;

			for (NetherFortressGenerator.PieceData pieceData : list) {
				if (pieceData.field_14499 > 0 && pieceData.field_14502 < pieceData.field_14499) {
					bl = true;
				}

				i += pieceData.field_14503;
			}

			return bl ? i : -1;
		}

		private NetherFortressGenerator.Piece method_14811(
			NetherFortressGenerator.Start start,
			List<NetherFortressGenerator.PieceData> list,
			List<StructurePiece> list2,
			Random random,
			int i,
			int j,
			int k,
			Direction direction,
			int l
		) {
			int m = this.method_14810(list);
			boolean bl = m > 0 && l <= 30;
			int n = 0;

			while (n < 5 && bl) {
				n++;
				int o = random.nextInt(m);

				for (NetherFortressGenerator.PieceData pieceData : list) {
					o -= pieceData.field_14503;
					if (o < 0) {
						if (!pieceData.method_14816(l) || pieceData == start.field_14506 && !pieceData.field_14500) {
							break;
						}

						NetherFortressGenerator.Piece piece = NetherFortressGenerator.generatePiece(pieceData, list2, random, i, j, k, direction, l);
						if (piece != null) {
							pieceData.field_14502++;
							start.field_14506 = pieceData;
							if (!pieceData.method_14815()) {
								list.remove(pieceData);
							}

							return piece;
						}
					}
				}
			}

			return NetherFortressGenerator.BridgeEnd.method_14797(list2, random, i, j, k, direction, l);
		}

		private StructurePiece method_14813(
			NetherFortressGenerator.Start start, List<StructurePiece> list, Random random, int i, int j, int k, @Nullable Direction direction, int l, boolean bl
		) {
			if (Math.abs(i - start.getBoundingBox().minX) <= 112 && Math.abs(k - start.getBoundingBox().minZ) <= 112) {
				List<NetherFortressGenerator.PieceData> list2 = start.bridgePieces;
				if (bl) {
					list2 = start.corridorPieces;
				}

				StructurePiece structurePiece = this.method_14811(start, list2, list, random, i, j, k, direction, l + 1);
				if (structurePiece != null) {
					list.add(structurePiece);
					start.field_14505.add(structurePiece);
				}

				return structurePiece;
			} else {
				return NetherFortressGenerator.BridgeEnd.method_14797(list, random, i, j, k, direction, l);
			}
		}

		@Nullable
		protected StructurePiece method_14814(NetherFortressGenerator.Start start, List<StructurePiece> list, Random random, int i, int j, boolean bl) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return this.method_14813(
							start, list, random, this.boundingBox.minX + i, this.boundingBox.minY + j, this.boundingBox.minZ - 1, direction, this.getLength(), bl
						);
					case SOUTH:
						return this.method_14813(
							start, list, random, this.boundingBox.minX + i, this.boundingBox.minY + j, this.boundingBox.maxZ + 1, direction, this.getLength(), bl
						);
					case WEST:
						return this.method_14813(
							start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + j, this.boundingBox.minZ + i, direction, this.getLength(), bl
						);
					case EAST:
						return this.method_14813(
							start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + j, this.boundingBox.minZ + i, direction, this.getLength(), bl
						);
				}
			}

			return null;
		}

		@Nullable
		protected StructurePiece method_14812(NetherFortressGenerator.Start start, List<StructurePiece> list, Random random, int i, int j, boolean bl) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return this.method_14813(
							start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.WEST, this.getLength(), bl
						);
					case SOUTH:
						return this.method_14813(
							start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.WEST, this.getLength(), bl
						);
					case WEST:
						return this.method_14813(
							start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.minZ - 1, Direction.NORTH, this.getLength(), bl
						);
					case EAST:
						return this.method_14813(
							start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.minZ - 1, Direction.NORTH, this.getLength(), bl
						);
				}
			}

			return null;
		}

		@Nullable
		protected StructurePiece method_14808(NetherFortressGenerator.Start start, List<StructurePiece> list, Random random, int i, int j, boolean bl) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return this.method_14813(
							start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.EAST, this.getLength(), bl
						);
					case SOUTH:
						return this.method_14813(
							start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.EAST, this.getLength(), bl
						);
					case WEST:
						return this.method_14813(
							start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.maxZ + 1, Direction.SOUTH, this.getLength(), bl
						);
					case EAST:
						return this.method_14813(
							start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.maxZ + 1, Direction.SOUTH, this.getLength(), bl
						);
				}
			}

			return null;
		}

		protected static boolean method_14809(BlockBox blockBox) {
			return blockBox != null && blockBox.minY > 10;
		}
	}

	static class PieceData {
		public final Class<? extends NetherFortressGenerator.Piece> pieceType;
		public final int field_14503;
		public int field_14502;
		public final int field_14499;
		public final boolean field_14500;

		public PieceData(Class<? extends NetherFortressGenerator.Piece> class_, int i, int j, boolean bl) {
			this.pieceType = class_;
			this.field_14503 = i;
			this.field_14499 = j;
			this.field_14500 = bl;
		}

		public PieceData(Class<? extends NetherFortressGenerator.Piece> class_, int i, int j) {
			this(class_, i, j, false);
		}

		public boolean method_14816(int i) {
			return this.field_14499 == 0 || this.field_14502 < this.field_14499;
		}

		public boolean method_14815() {
			return this.field_14499 == 0 || this.field_14502 < this.field_14499;
		}
	}

	public static class SmallCorridor extends NetherFortressGenerator.Piece {
		public SmallCorridor(int i, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_SMALL_CORRIDOR, i);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
		}

		public SmallCorridor(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_SMALL_CORRIDOR, compoundTag);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14814((NetherFortressGenerator.Start)structurePiece, list, random, 1, 0, true);
		}

		public static NetherFortressGenerator.SmallCorridor method_14804(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -1, 0, 0, 5, 7, 5, direction);
			return method_14809(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new NetherFortressGenerator.SmallCorridor(l, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			IWorld world,
			StructureAccessor structureAccessor,
			ChunkGenerator<?> chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 3, 1, 0, 4, 1, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 0, 3, 3, 0, 4, 3, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 4, 3, 1, 4, 4, 1, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 4, 3, 3, 4, 4, 3, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.method_14936(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
				}
			}

			return true;
		}
	}

	public static class Start extends NetherFortressGenerator.BridgeCrossing {
		public NetherFortressGenerator.PieceData field_14506;
		public List<NetherFortressGenerator.PieceData> bridgePieces;
		public List<NetherFortressGenerator.PieceData> corridorPieces;
		public final List<StructurePiece> field_14505 = Lists.<StructurePiece>newArrayList();

		public Start(Random random, int i, int j) {
			super(random, i, j);
			this.bridgePieces = Lists.<NetherFortressGenerator.PieceData>newArrayList();

			for (NetherFortressGenerator.PieceData pieceData : NetherFortressGenerator.field_14494) {
				pieceData.field_14502 = 0;
				this.bridgePieces.add(pieceData);
			}

			this.corridorPieces = Lists.<NetherFortressGenerator.PieceData>newArrayList();

			for (NetherFortressGenerator.PieceData pieceData : NetherFortressGenerator.field_14493) {
				pieceData.field_14502 = 0;
				this.corridorPieces.add(pieceData);
			}
		}

		public Start(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_START, compoundTag);
		}
	}
}
