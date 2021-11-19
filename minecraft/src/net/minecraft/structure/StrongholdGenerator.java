package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightType;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class StrongholdGenerator {
	private static final int field_31624 = 3;
	private static final int field_31625 = 3;
	private static final int field_31626 = 50;
	private static final int field_31627 = 10;
	private static final boolean field_31628 = true;
	private static final StrongholdGenerator.PieceData[] ALL_PIECES = new StrongholdGenerator.PieceData[]{
		new StrongholdGenerator.PieceData(StrongholdGenerator.Corridor.class, 40, 0),
		new StrongholdGenerator.PieceData(StrongholdGenerator.PrisonHall.class, 5, 5),
		new StrongholdGenerator.PieceData(StrongholdGenerator.LeftTurn.class, 20, 0),
		new StrongholdGenerator.PieceData(StrongholdGenerator.RightTurn.class, 20, 0),
		new StrongholdGenerator.PieceData(StrongholdGenerator.SquareRoom.class, 10, 6),
		new StrongholdGenerator.PieceData(StrongholdGenerator.Stairs.class, 5, 5),
		new StrongholdGenerator.PieceData(StrongholdGenerator.SpiralStaircase.class, 5, 5),
		new StrongholdGenerator.PieceData(StrongholdGenerator.FiveWayCrossing.class, 5, 4),
		new StrongholdGenerator.PieceData(StrongholdGenerator.ChestCorridor.class, 5, 4),
		new StrongholdGenerator.PieceData(StrongholdGenerator.Library.class, 10, 2) {
			@Override
			public boolean canGenerate(int chainLength) {
				return super.canGenerate(chainLength) && chainLength > 4;
			}
		},
		new StrongholdGenerator.PieceData(StrongholdGenerator.PortalRoom.class, 20, 1) {
			@Override
			public boolean canGenerate(int chainLength) {
				return super.canGenerate(chainLength) && chainLength > 5;
			}
		}
	};
	private static List<StrongholdGenerator.PieceData> possiblePieces;
	static Class<? extends StrongholdGenerator.Piece> activePieceType;
	private static int totalWeight;
	static final StrongholdGenerator.StoneBrickRandomizer STONE_BRICK_RANDOMIZER = new StrongholdGenerator.StoneBrickRandomizer();

	public static void init() {
		possiblePieces = Lists.<StrongholdGenerator.PieceData>newArrayList();

		for (StrongholdGenerator.PieceData pieceData : ALL_PIECES) {
			pieceData.generatedCount = 0;
			possiblePieces.add(pieceData);
		}

		activePieceType = null;
	}

	private static boolean checkRemainingPieces() {
		boolean bl = false;
		totalWeight = 0;

		for (StrongholdGenerator.PieceData pieceData : possiblePieces) {
			if (pieceData.limit > 0 && pieceData.generatedCount < pieceData.limit) {
				bl = true;
			}

			totalWeight = totalWeight + pieceData.weight;
		}

		return bl;
	}

	private static StrongholdGenerator.Piece createPiece(
		Class<? extends StrongholdGenerator.Piece> pieceType,
		StructurePiecesHolder holder,
		Random random,
		int x,
		int y,
		int z,
		@Nullable Direction orientation,
		int chainLength
	) {
		StrongholdGenerator.Piece piece = null;
		if (pieceType == StrongholdGenerator.Corridor.class) {
			piece = StrongholdGenerator.Corridor.create(holder, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdGenerator.PrisonHall.class) {
			piece = StrongholdGenerator.PrisonHall.create(holder, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdGenerator.LeftTurn.class) {
			piece = StrongholdGenerator.LeftTurn.create(holder, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdGenerator.RightTurn.class) {
			piece = StrongholdGenerator.RightTurn.create(holder, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdGenerator.SquareRoom.class) {
			piece = StrongholdGenerator.SquareRoom.create(holder, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdGenerator.Stairs.class) {
			piece = StrongholdGenerator.Stairs.create(holder, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdGenerator.SpiralStaircase.class) {
			piece = StrongholdGenerator.SpiralStaircase.create(holder, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdGenerator.FiveWayCrossing.class) {
			piece = StrongholdGenerator.FiveWayCrossing.create(holder, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdGenerator.ChestCorridor.class) {
			piece = StrongholdGenerator.ChestCorridor.create(holder, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdGenerator.Library.class) {
			piece = StrongholdGenerator.Library.create(holder, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdGenerator.PortalRoom.class) {
			piece = StrongholdGenerator.PortalRoom.create(holder, x, y, z, orientation, chainLength);
		}

		return piece;
	}

	private static StrongholdGenerator.Piece pickPiece(
		StrongholdGenerator.Start start, StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength
	) {
		if (!checkRemainingPieces()) {
			return null;
		} else {
			if (activePieceType != null) {
				StrongholdGenerator.Piece piece = createPiece(activePieceType, holder, random, x, y, z, orientation, chainLength);
				activePieceType = null;
				if (piece != null) {
					return piece;
				}
			}

			int i = 0;

			while (i < 5) {
				i++;
				int j = random.nextInt(totalWeight);

				for (StrongholdGenerator.PieceData pieceData : possiblePieces) {
					j -= pieceData.weight;
					if (j < 0) {
						if (!pieceData.canGenerate(chainLength) || pieceData == start.lastPiece) {
							break;
						}

						StrongholdGenerator.Piece piece2 = createPiece(pieceData.pieceType, holder, random, x, y, z, orientation, chainLength);
						if (piece2 != null) {
							pieceData.generatedCount++;
							start.lastPiece = pieceData;
							if (!pieceData.canGenerate()) {
								possiblePieces.remove(pieceData);
							}

							return piece2;
						}
					}
				}
			}

			BlockBox blockBox = StrongholdGenerator.SmallCorridor.create(holder, random, x, y, z, orientation);
			return blockBox != null && blockBox.getMinY() > 1 ? new StrongholdGenerator.SmallCorridor(chainLength, blockBox, orientation) : null;
		}
	}

	static StructurePiece pieceGenerator(
		StrongholdGenerator.Start start, StructurePiecesHolder holder, Random random, int x, int y, int z, @Nullable Direction orientation, int chainLength
	) {
		if (chainLength > 50) {
			return null;
		} else if (Math.abs(x - start.getBoundingBox().getMinX()) <= 112 && Math.abs(z - start.getBoundingBox().getMinZ()) <= 112) {
			StructurePiece structurePiece = pickPiece(start, holder, random, x, y, z, orientation, chainLength + 1);
			if (structurePiece != null) {
				holder.addPiece(structurePiece);
				start.pieces.add(structurePiece);
			}

			return structurePiece;
		} else {
			return null;
		}
	}

	public static class ChestCorridor extends StrongholdGenerator.Piece {
		private static final int SIZE_X = 5;
		private static final int SIZE_Y = 5;
		private static final int SIZE_Z = 7;
		private boolean chestGenerated;

		public ChestCorridor(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, chainLength, boundingBox);
			this.setOrientation(orientation);
			this.entryDoor = this.getRandomEntrance(random);
		}

		public ChestCorridor(NbtCompound nbt) {
			super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, nbt);
			this.chestGenerated = nbt.getBoolean("Chest");
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			super.writeNbt(context, nbt);
			nbt.putBoolean("Chest", this.chestGenerated);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			this.fillForwardOpening((StrongholdGenerator.Start)start, holder, random, 1, 1);
		}

		public static StrongholdGenerator.ChestCorridor create(
			StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainlength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, 7, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new StrongholdGenerator.ChestCorridor(chainlength, random, blockBox, orientation)
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
			BlockPos pos
		) {
			this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 4, 6, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(world, random, chunkBox, this.entryDoor, 1, 1, 0);
			this.generateEntrance(world, random, chunkBox, StrongholdGenerator.Piece.EntranceType.OPENING, 1, 1, 6);
			this.fillWithOutline(world, chunkBox, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.getDefaultState(), Blocks.STONE_BRICKS.getDefaultState(), false);
			this.addBlock(world, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 1, 1, chunkBox);
			this.addBlock(world, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 1, 5, chunkBox);
			this.addBlock(world, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 2, 2, chunkBox);
			this.addBlock(world, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 2, 4, chunkBox);

			for (int i = 2; i <= 4; i++) {
				this.addBlock(world, Blocks.STONE_BRICK_SLAB.getDefaultState(), 2, 1, i, chunkBox);
			}

			if (!this.chestGenerated && chunkBox.contains(this.offsetPos(3, 2, 3))) {
				this.chestGenerated = true;
				this.addChest(world, chunkBox, random, 3, 2, 3, LootTables.STRONGHOLD_CORRIDOR_CHEST);
			}
		}
	}

	public static class Corridor extends StrongholdGenerator.Piece {
		private static final int SIZE_X = 5;
		private static final int SIZE_Y = 5;
		private static final int SIZE_Z = 7;
		private final boolean leftExitExists;
		private final boolean rightExitExists;

		public Corridor(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.STRONGHOLD_CORRIDOR, chainLength, boundingBox);
			this.setOrientation(orientation);
			this.entryDoor = this.getRandomEntrance(random);
			this.leftExitExists = random.nextInt(2) == 0;
			this.rightExitExists = random.nextInt(2) == 0;
		}

		public Corridor(NbtCompound nbt) {
			super(StructurePieceType.STRONGHOLD_CORRIDOR, nbt);
			this.leftExitExists = nbt.getBoolean("Left");
			this.rightExitExists = nbt.getBoolean("Right");
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			super.writeNbt(context, nbt);
			nbt.putBoolean("Left", this.leftExitExists);
			nbt.putBoolean("Right", this.rightExitExists);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			this.fillForwardOpening((StrongholdGenerator.Start)start, holder, random, 1, 1);
			if (this.leftExitExists) {
				this.fillNWOpening((StrongholdGenerator.Start)start, holder, random, 1, 2);
			}

			if (this.rightExitExists) {
				this.fillSEOpening((StrongholdGenerator.Start)start, holder, random, 1, 2);
			}
		}

		public static StrongholdGenerator.Corridor create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, 7, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new StrongholdGenerator.Corridor(chainLength, random, blockBox, orientation)
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
			BlockPos pos
		) {
			this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 4, 6, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(world, random, chunkBox, this.entryDoor, 1, 1, 0);
			this.generateEntrance(world, random, chunkBox, StrongholdGenerator.Piece.EntranceType.OPENING, 1, 1, 6);
			BlockState blockState = Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST);
			BlockState blockState2 = Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST);
			this.addBlockWithRandomThreshold(world, chunkBox, random, 0.1F, 1, 2, 1, blockState);
			this.addBlockWithRandomThreshold(world, chunkBox, random, 0.1F, 3, 2, 1, blockState2);
			this.addBlockWithRandomThreshold(world, chunkBox, random, 0.1F, 1, 2, 5, blockState);
			this.addBlockWithRandomThreshold(world, chunkBox, random, 0.1F, 3, 2, 5, blockState2);
			if (this.leftExitExists) {
				this.fillWithOutline(world, chunkBox, 0, 1, 2, 0, 3, 4, AIR, AIR, false);
			}

			if (this.rightExitExists) {
				this.fillWithOutline(world, chunkBox, 4, 1, 2, 4, 3, 4, AIR, AIR, false);
			}
		}
	}

	public static class FiveWayCrossing extends StrongholdGenerator.Piece {
		protected static final int SIZE_X = 10;
		protected static final int SIZE_Y = 9;
		protected static final int SIZE_Z = 11;
		private final boolean lowerLeftExists;
		private final boolean upperLeftExists;
		private final boolean lowerRightExists;
		private final boolean upperRightExists;

		public FiveWayCrossing(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.STRONGHOLD_FIVE_WAY_CROSSING, chainLength, boundingBox);
			this.setOrientation(orientation);
			this.entryDoor = this.getRandomEntrance(random);
			this.lowerLeftExists = random.nextBoolean();
			this.upperLeftExists = random.nextBoolean();
			this.lowerRightExists = random.nextBoolean();
			this.upperRightExists = random.nextInt(3) > 0;
		}

		public FiveWayCrossing(NbtCompound nbt) {
			super(StructurePieceType.STRONGHOLD_FIVE_WAY_CROSSING, nbt);
			this.lowerLeftExists = nbt.getBoolean("leftLow");
			this.upperLeftExists = nbt.getBoolean("leftHigh");
			this.lowerRightExists = nbt.getBoolean("rightLow");
			this.upperRightExists = nbt.getBoolean("rightHigh");
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			super.writeNbt(context, nbt);
			nbt.putBoolean("leftLow", this.lowerLeftExists);
			nbt.putBoolean("leftHigh", this.upperLeftExists);
			nbt.putBoolean("rightLow", this.lowerRightExists);
			nbt.putBoolean("rightHigh", this.upperRightExists);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			int i = 3;
			int j = 5;
			Direction direction = this.getFacing();
			if (direction == Direction.WEST || direction == Direction.NORTH) {
				i = 8 - i;
				j = 8 - j;
			}

			this.fillForwardOpening((StrongholdGenerator.Start)start, holder, random, 5, 1);
			if (this.lowerLeftExists) {
				this.fillNWOpening((StrongholdGenerator.Start)start, holder, random, i, 1);
			}

			if (this.upperLeftExists) {
				this.fillNWOpening((StrongholdGenerator.Start)start, holder, random, j, 7);
			}

			if (this.lowerRightExists) {
				this.fillSEOpening((StrongholdGenerator.Start)start, holder, random, i, 1);
			}

			if (this.upperRightExists) {
				this.fillSEOpening((StrongholdGenerator.Start)start, holder, random, j, 7);
			}
		}

		public static StrongholdGenerator.FiveWayCrossing create(
			StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -4, -3, 0, 10, 9, 11, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new StrongholdGenerator.FiveWayCrossing(chainLength, random, blockBox, orientation)
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
			BlockPos pos
		) {
			this.fillWithOutline(world, chunkBox, 0, 0, 0, 9, 8, 10, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(world, random, chunkBox, this.entryDoor, 4, 3, 0);
			if (this.lowerLeftExists) {
				this.fillWithOutline(world, chunkBox, 0, 3, 1, 0, 5, 3, AIR, AIR, false);
			}

			if (this.lowerRightExists) {
				this.fillWithOutline(world, chunkBox, 9, 3, 1, 9, 5, 3, AIR, AIR, false);
			}

			if (this.upperLeftExists) {
				this.fillWithOutline(world, chunkBox, 0, 5, 7, 0, 7, 9, AIR, AIR, false);
			}

			if (this.upperRightExists) {
				this.fillWithOutline(world, chunkBox, 9, 5, 7, 9, 7, 9, AIR, AIR, false);
			}

			this.fillWithOutline(world, chunkBox, 5, 1, 10, 7, 3, 10, AIR, AIR, false);
			this.fillWithOutline(world, chunkBox, 1, 2, 1, 8, 2, 6, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 4, 1, 5, 4, 4, 9, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 8, 1, 5, 8, 4, 9, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 1, 4, 7, 3, 4, 9, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 1, 3, 5, 3, 3, 6, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 1, 3, 4, 3, 3, 4, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 1, 4, 6, 3, 4, 6, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 5, 1, 7, 7, 1, 8, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 5, 1, 9, 7, 1, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 5, 2, 7, 7, 2, 7, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 4, 5, 7, 4, 5, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 8, 5, 7, 8, 5, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
			this.fillWithOutline(
				world,
				chunkBox,
				5,
				5,
				7,
				7,
				5,
				9,
				Blocks.SMOOTH_STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.DOUBLE),
				Blocks.SMOOTH_STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.DOUBLE),
				false
			);
			this.addBlock(world, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH), 6, 5, 6, chunkBox);
		}
	}

	public static class LeftTurn extends StrongholdGenerator.Turn {
		public LeftTurn(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.STRONGHOLD_LEFT_TURN, chainLength, boundingBox);
			this.setOrientation(orientation);
			this.entryDoor = this.getRandomEntrance(random);
		}

		public LeftTurn(NbtCompound nbt) {
			super(StructurePieceType.STRONGHOLD_LEFT_TURN, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			Direction direction = this.getFacing();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.fillSEOpening((StrongholdGenerator.Start)start, holder, random, 1, 1);
			} else {
				this.fillNWOpening((StrongholdGenerator.Start)start, holder, random, 1, 1);
			}
		}

		public static StrongholdGenerator.LeftTurn create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, 5, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new StrongholdGenerator.LeftTurn(chainLength, random, blockBox, orientation)
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
			BlockPos pos
		) {
			this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 4, 4, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(world, random, chunkBox, this.entryDoor, 1, 1, 0);
			Direction direction = this.getFacing();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.fillWithOutline(world, chunkBox, 4, 1, 1, 4, 3, 3, AIR, AIR, false);
			} else {
				this.fillWithOutline(world, chunkBox, 0, 1, 1, 0, 3, 3, AIR, AIR, false);
			}
		}
	}

	public static class Library extends StrongholdGenerator.Piece {
		protected static final int SIZE_X = 14;
		protected static final int field_31636 = 6;
		protected static final int SIZE_Y = 11;
		protected static final int SIZE_Z = 15;
		private final boolean tall;

		public Library(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.STRONGHOLD_LIBRARY, chainLength, boundingBox);
			this.setOrientation(orientation);
			this.entryDoor = this.getRandomEntrance(random);
			this.tall = boundingBox.getBlockCountY() > 6;
		}

		public Library(NbtCompound nbt) {
			super(StructurePieceType.STRONGHOLD_LIBRARY, nbt);
			this.tall = nbt.getBoolean("Tall");
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			super.writeNbt(context, nbt);
			nbt.putBoolean("Tall", this.tall);
		}

		public static StrongholdGenerator.Library create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -4, -1, 0, 14, 11, 15, orientation);
			if (!isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
				blockBox = BlockBox.rotated(x, y, z, -4, -1, 0, 14, 6, 15, orientation);
				if (!isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
					return null;
				}
			}

			return new StrongholdGenerator.Library(chainLength, random, blockBox, orientation);
		}

		@Override
		public void generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox chunkBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			int i = 11;
			if (!this.tall) {
				i = 6;
			}

			this.fillWithOutline(world, chunkBox, 0, 0, 0, 13, i - 1, 14, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(world, random, chunkBox, this.entryDoor, 4, 1, 0);
			this.fillWithOutlineUnderSeaLevel(
				world, chunkBox, random, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.COBWEB.getDefaultState(), Blocks.COBWEB.getDefaultState(), false, false
			);
			int j = 1;
			int k = 12;

			for (int l = 1; l <= 13; l++) {
				if ((l - 1) % 4 == 0) {
					this.fillWithOutline(world, chunkBox, 1, 1, l, 1, 4, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
					this.fillWithOutline(world, chunkBox, 12, 1, l, 12, 4, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
					this.addBlock(world, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST), 2, 3, l, chunkBox);
					this.addBlock(world, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST), 11, 3, l, chunkBox);
					if (this.tall) {
						this.fillWithOutline(world, chunkBox, 1, 6, l, 1, 9, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
						this.fillWithOutline(world, chunkBox, 12, 6, l, 12, 9, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
					}
				} else {
					this.fillWithOutline(world, chunkBox, 1, 1, l, 1, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
					this.fillWithOutline(world, chunkBox, 12, 1, l, 12, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
					if (this.tall) {
						this.fillWithOutline(world, chunkBox, 1, 6, l, 1, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
						this.fillWithOutline(world, chunkBox, 12, 6, l, 12, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
					}
				}
			}

			for (int lx = 3; lx < 12; lx += 2) {
				this.fillWithOutline(world, chunkBox, 3, 1, lx, 4, 3, lx, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
				this.fillWithOutline(world, chunkBox, 6, 1, lx, 7, 3, lx, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
				this.fillWithOutline(world, chunkBox, 9, 1, lx, 10, 3, lx, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
			}

			if (this.tall) {
				this.fillWithOutline(world, chunkBox, 1, 5, 1, 3, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
				this.fillWithOutline(world, chunkBox, 10, 5, 1, 12, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
				this.fillWithOutline(world, chunkBox, 4, 5, 1, 9, 5, 2, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
				this.fillWithOutline(world, chunkBox, 4, 5, 12, 9, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
				this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 9, 5, 11, chunkBox);
				this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 8, 5, 11, chunkBox);
				this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 9, 5, 10, chunkBox);
				BlockState blockState = Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true));
				BlockState blockState2 = Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true));
				this.fillWithOutline(world, chunkBox, 3, 6, 3, 3, 6, 11, blockState2, blockState2, false);
				this.fillWithOutline(world, chunkBox, 10, 6, 3, 10, 6, 9, blockState2, blockState2, false);
				this.fillWithOutline(world, chunkBox, 4, 6, 2, 9, 6, 2, blockState, blockState, false);
				this.fillWithOutline(world, chunkBox, 4, 6, 12, 7, 6, 12, blockState, blockState, false);
				this.addBlock(
					world, Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)), 3, 6, 2, chunkBox
				);
				this.addBlock(
					world, Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)), 3, 6, 12, chunkBox
				);
				this.addBlock(
					world, Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)), 10, 6, 2, chunkBox
				);

				for (int m = 0; m <= 2; m++) {
					this.addBlock(
						world,
						Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
						8 + m,
						6,
						12 - m,
						chunkBox
					);
					if (m != 2) {
						this.addBlock(
							world,
							Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
							8 + m,
							6,
							11 - m,
							chunkBox
						);
					}
				}

				BlockState blockState3 = Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, Direction.SOUTH);
				this.addBlock(world, blockState3, 10, 1, 13, chunkBox);
				this.addBlock(world, blockState3, 10, 2, 13, chunkBox);
				this.addBlock(world, blockState3, 10, 3, 13, chunkBox);
				this.addBlock(world, blockState3, 10, 4, 13, chunkBox);
				this.addBlock(world, blockState3, 10, 5, 13, chunkBox);
				this.addBlock(world, blockState3, 10, 6, 13, chunkBox);
				this.addBlock(world, blockState3, 10, 7, 13, chunkBox);
				int n = 7;
				int o = 7;
				BlockState blockState4 = Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true));
				this.addBlock(world, blockState4, 6, 9, 7, chunkBox);
				BlockState blockState5 = Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true));
				this.addBlock(world, blockState5, 7, 9, 7, chunkBox);
				this.addBlock(world, blockState4, 6, 8, 7, chunkBox);
				this.addBlock(world, blockState5, 7, 8, 7, chunkBox);
				BlockState blockState6 = blockState2.with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true));
				this.addBlock(world, blockState6, 6, 7, 7, chunkBox);
				this.addBlock(world, blockState6, 7, 7, 7, chunkBox);
				this.addBlock(world, blockState4, 5, 7, 7, chunkBox);
				this.addBlock(world, blockState5, 8, 7, 7, chunkBox);
				this.addBlock(world, blockState4.with(FenceBlock.NORTH, Boolean.valueOf(true)), 6, 7, 6, chunkBox);
				this.addBlock(world, blockState4.with(FenceBlock.SOUTH, Boolean.valueOf(true)), 6, 7, 8, chunkBox);
				this.addBlock(world, blockState5.with(FenceBlock.NORTH, Boolean.valueOf(true)), 7, 7, 6, chunkBox);
				this.addBlock(world, blockState5.with(FenceBlock.SOUTH, Boolean.valueOf(true)), 7, 7, 8, chunkBox);
				BlockState blockState7 = Blocks.TORCH.getDefaultState();
				this.addBlock(world, blockState7, 5, 8, 7, chunkBox);
				this.addBlock(world, blockState7, 8, 8, 7, chunkBox);
				this.addBlock(world, blockState7, 6, 8, 6, chunkBox);
				this.addBlock(world, blockState7, 6, 8, 8, chunkBox);
				this.addBlock(world, blockState7, 7, 8, 6, chunkBox);
				this.addBlock(world, blockState7, 7, 8, 8, chunkBox);
			}

			this.addChest(world, chunkBox, random, 3, 3, 5, LootTables.STRONGHOLD_LIBRARY_CHEST);
			if (this.tall) {
				this.addBlock(world, AIR, 12, 9, 1, chunkBox);
				this.addChest(world, chunkBox, random, 12, 8, 1, LootTables.STRONGHOLD_LIBRARY_CHEST);
			}
		}
	}

	abstract static class Piece extends StructurePiece {
		protected StrongholdGenerator.Piece.EntranceType entryDoor = StrongholdGenerator.Piece.EntranceType.OPENING;

		protected Piece(StructurePieceType structurePieceType, int i, BlockBox blockBox) {
			super(structurePieceType, i, blockBox);
		}

		public Piece(StructurePieceType structurePieceType, NbtCompound nbtCompound) {
			super(structurePieceType, nbtCompound);
			this.entryDoor = StrongholdGenerator.Piece.EntranceType.valueOf(nbtCompound.getString("EntryDoor"));
		}

		@Override
		public StructureWeightType getWeightType() {
			return StructureWeightType.BURY;
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			nbt.putString("EntryDoor", this.entryDoor.name());
		}

		protected void generateEntrance(
			StructureWorldAccess world, Random random, BlockBox boundingBox, StrongholdGenerator.Piece.EntranceType type, int x, int y, int z
		) {
			switch (type) {
				case OPENING:
					this.fillWithOutline(world, boundingBox, x, y, z, x + 3 - 1, y + 3 - 1, z, AIR, AIR, false);
					break;
				case WOOD_DOOR:
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x, y, z, boundingBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x, y + 1, z, boundingBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x, y + 2, z, boundingBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 1, y + 2, z, boundingBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y + 2, z, boundingBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y + 1, z, boundingBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y, z, boundingBox);
					this.addBlock(world, Blocks.OAK_DOOR.getDefaultState(), x + 1, y, z, boundingBox);
					this.addBlock(world, Blocks.OAK_DOOR.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.UPPER), x + 1, y + 1, z, boundingBox);
					break;
				case GRATES:
					this.addBlock(world, Blocks.CAVE_AIR.getDefaultState(), x + 1, y, z, boundingBox);
					this.addBlock(world, Blocks.CAVE_AIR.getDefaultState(), x + 1, y + 1, z, boundingBox);
					this.addBlock(world, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)), x, y, z, boundingBox);
					this.addBlock(world, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)), x, y + 1, z, boundingBox);
					this.addBlock(
						world,
						Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
						x,
						y + 2,
						z,
						boundingBox
					);
					this.addBlock(
						world,
						Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
						x + 1,
						y + 2,
						z,
						boundingBox
					);
					this.addBlock(
						world,
						Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
						x + 2,
						y + 2,
						z,
						boundingBox
					);
					this.addBlock(world, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)), x + 2, y + 1, z, boundingBox);
					this.addBlock(world, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)), x + 2, y, z, boundingBox);
					break;
				case IRON_DOOR:
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x, y, z, boundingBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x, y + 1, z, boundingBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x, y + 2, z, boundingBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 1, y + 2, z, boundingBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y + 2, z, boundingBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y + 1, z, boundingBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y, z, boundingBox);
					this.addBlock(world, Blocks.IRON_DOOR.getDefaultState(), x + 1, y, z, boundingBox);
					this.addBlock(world, Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.UPPER), x + 1, y + 1, z, boundingBox);
					this.addBlock(world, Blocks.STONE_BUTTON.getDefaultState().with(AbstractButtonBlock.FACING, Direction.NORTH), x + 2, y + 1, z + 1, boundingBox);
					this.addBlock(world, Blocks.STONE_BUTTON.getDefaultState().with(AbstractButtonBlock.FACING, Direction.SOUTH), x + 2, y + 1, z - 1, boundingBox);
			}
		}

		protected StrongholdGenerator.Piece.EntranceType getRandomEntrance(Random random) {
			int i = random.nextInt(5);
			switch (i) {
				case 0:
				case 1:
				default:
					return StrongholdGenerator.Piece.EntranceType.OPENING;
				case 2:
					return StrongholdGenerator.Piece.EntranceType.WOOD_DOOR;
				case 3:
					return StrongholdGenerator.Piece.EntranceType.GRATES;
				case 4:
					return StrongholdGenerator.Piece.EntranceType.IRON_DOOR;
			}
		}

		@Nullable
		protected StructurePiece fillForwardOpening(
			StrongholdGenerator.Start start, StructurePiecesHolder holder, Random random, int leftRightOffset, int heightOffset
		) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return StrongholdGenerator.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMinX() + leftRightOffset,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() - 1,
							direction,
							this.getChainLength()
						);
					case SOUTH:
						return StrongholdGenerator.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMinX() + leftRightOffset,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMaxZ() + 1,
							direction,
							this.getChainLength()
						);
					case WEST:
						return StrongholdGenerator.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMinX() - 1,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() + leftRightOffset,
							direction,
							this.getChainLength()
						);
					case EAST:
						return StrongholdGenerator.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMaxX() + 1,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() + leftRightOffset,
							direction,
							this.getChainLength()
						);
				}
			}

			return null;
		}

		@Nullable
		protected StructurePiece fillNWOpening(StrongholdGenerator.Start start, StructurePiecesHolder holder, Random random, int heightOffset, int leftRightOffset) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return StrongholdGenerator.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMinX() - 1,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() + leftRightOffset,
							Direction.WEST,
							this.getChainLength()
						);
					case SOUTH:
						return StrongholdGenerator.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMinX() - 1,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() + leftRightOffset,
							Direction.WEST,
							this.getChainLength()
						);
					case WEST:
						return StrongholdGenerator.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMinX() + leftRightOffset,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() - 1,
							Direction.NORTH,
							this.getChainLength()
						);
					case EAST:
						return StrongholdGenerator.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMinX() + leftRightOffset,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() - 1,
							Direction.NORTH,
							this.getChainLength()
						);
				}
			}

			return null;
		}

		@Nullable
		protected StructurePiece fillSEOpening(StrongholdGenerator.Start start, StructurePiecesHolder holder, Random random, int heightOffset, int leftRightOffset) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return StrongholdGenerator.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMaxX() + 1,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() + leftRightOffset,
							Direction.EAST,
							this.getChainLength()
						);
					case SOUTH:
						return StrongholdGenerator.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMaxX() + 1,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() + leftRightOffset,
							Direction.EAST,
							this.getChainLength()
						);
					case WEST:
						return StrongholdGenerator.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMinX() + leftRightOffset,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMaxZ() + 1,
							Direction.SOUTH,
							this.getChainLength()
						);
					case EAST:
						return StrongholdGenerator.pieceGenerator(
							start,
							holder,
							random,
							this.boundingBox.getMinX() + leftRightOffset,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMaxZ() + 1,
							Direction.SOUTH,
							this.getChainLength()
						);
				}
			}

			return null;
		}

		protected static boolean isInBounds(BlockBox boundingBox) {
			return boundingBox != null && boundingBox.getMinY() > 10;
		}

		protected static enum EntranceType {
			OPENING,
			WOOD_DOOR,
			GRATES,
			IRON_DOOR;
		}
	}

	static class PieceData {
		public final Class<? extends StrongholdGenerator.Piece> pieceType;
		public final int weight;
		public int generatedCount;
		public final int limit;

		public PieceData(Class<? extends StrongholdGenerator.Piece> pieceType, int weight, int limit) {
			this.pieceType = pieceType;
			this.weight = weight;
			this.limit = limit;
		}

		public boolean canGenerate(int chainLength) {
			return this.limit == 0 || this.generatedCount < this.limit;
		}

		public boolean canGenerate() {
			return this.limit == 0 || this.generatedCount < this.limit;
		}
	}

	public static class PortalRoom extends StrongholdGenerator.Piece {
		protected static final int SIZE_X = 11;
		protected static final int SIZE_Y = 8;
		protected static final int SIZE_Z = 16;
		private boolean spawnerPlaced;

		public PortalRoom(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public PortalRoom(NbtCompound nbt) {
			super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, nbt);
			this.spawnerPlaced = nbt.getBoolean("Mob");
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			super.writeNbt(context, nbt);
			nbt.putBoolean("Mob", this.spawnerPlaced);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			if (start != null) {
				((StrongholdGenerator.Start)start).portalRoom = this;
			}
		}

		public static StrongholdGenerator.PortalRoom create(StructurePiecesHolder holder, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -4, -1, 0, 11, 8, 16, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null ? new StrongholdGenerator.PortalRoom(chainLength, blockBox, orientation) : null;
		}

		@Override
		public void generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox chunkBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			this.fillWithOutline(world, chunkBox, 0, 0, 0, 10, 7, 15, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(world, random, chunkBox, StrongholdGenerator.Piece.EntranceType.GRATES, 4, 1, 0);
			int i = 6;
			this.fillWithOutline(world, chunkBox, 1, i, 1, 1, i, 14, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 9, i, 1, 9, i, 14, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 2, i, 1, 8, i, 2, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 2, i, 14, 8, i, 14, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 1, 1, 1, 2, 1, 4, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 8, 1, 1, 9, 1, 4, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 1, 1, 1, 1, 1, 3, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 9, 1, 1, 9, 1, 3, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
			this.fillWithOutline(world, chunkBox, 3, 1, 8, 7, 1, 12, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 4, 1, 9, 6, 1, 11, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
			BlockState blockState = Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)).with(PaneBlock.EAST, Boolean.valueOf(true));

			for (int j = 3; j < 14; j += 2) {
				this.fillWithOutline(world, chunkBox, 0, 3, j, 0, 4, j, blockState, blockState, false);
				this.fillWithOutline(world, chunkBox, 10, 3, j, 10, 4, j, blockState, blockState, false);
			}

			for (int j = 2; j < 9; j += 2) {
				this.fillWithOutline(world, chunkBox, j, 3, 15, j, 4, 15, blockState2, blockState2, false);
			}

			BlockState blockState3 = Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
			this.fillWithOutline(world, chunkBox, 4, 1, 5, 6, 1, 7, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 4, 2, 6, 6, 2, 7, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 4, 3, 7, 6, 3, 7, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);

			for (int k = 4; k <= 6; k++) {
				this.addBlock(world, blockState3, k, 1, 4, chunkBox);
				this.addBlock(world, blockState3, k, 2, 5, chunkBox);
				this.addBlock(world, blockState3, k, 3, 6, chunkBox);
			}

			BlockState blockState4 = Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.NORTH);
			BlockState blockState5 = Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.SOUTH);
			BlockState blockState6 = Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.EAST);
			BlockState blockState7 = Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.WEST);
			boolean bl = true;
			boolean[] bls = new boolean[12];

			for (int l = 0; l < bls.length; l++) {
				bls[l] = random.nextFloat() > 0.9F;
				bl &= bls[l];
			}

			this.addBlock(world, blockState4.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[0])), 4, 3, 8, chunkBox);
			this.addBlock(world, blockState4.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[1])), 5, 3, 8, chunkBox);
			this.addBlock(world, blockState4.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[2])), 6, 3, 8, chunkBox);
			this.addBlock(world, blockState5.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[3])), 4, 3, 12, chunkBox);
			this.addBlock(world, blockState5.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[4])), 5, 3, 12, chunkBox);
			this.addBlock(world, blockState5.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[5])), 6, 3, 12, chunkBox);
			this.addBlock(world, blockState6.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[6])), 3, 3, 9, chunkBox);
			this.addBlock(world, blockState6.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[7])), 3, 3, 10, chunkBox);
			this.addBlock(world, blockState6.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[8])), 3, 3, 11, chunkBox);
			this.addBlock(world, blockState7.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[9])), 7, 3, 9, chunkBox);
			this.addBlock(world, blockState7.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[10])), 7, 3, 10, chunkBox);
			this.addBlock(world, blockState7.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[11])), 7, 3, 11, chunkBox);
			if (bl) {
				BlockState blockState8 = Blocks.END_PORTAL.getDefaultState();
				this.addBlock(world, blockState8, 4, 3, 9, chunkBox);
				this.addBlock(world, blockState8, 5, 3, 9, chunkBox);
				this.addBlock(world, blockState8, 6, 3, 9, chunkBox);
				this.addBlock(world, blockState8, 4, 3, 10, chunkBox);
				this.addBlock(world, blockState8, 5, 3, 10, chunkBox);
				this.addBlock(world, blockState8, 6, 3, 10, chunkBox);
				this.addBlock(world, blockState8, 4, 3, 11, chunkBox);
				this.addBlock(world, blockState8, 5, 3, 11, chunkBox);
				this.addBlock(world, blockState8, 6, 3, 11, chunkBox);
			}

			if (!this.spawnerPlaced) {
				BlockPos blockPos = this.offsetPos(5, 3, 6);
				if (chunkBox.contains(blockPos)) {
					this.spawnerPlaced = true;
					world.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), Block.NOTIFY_LISTENERS);
					BlockEntity blockEntity = world.getBlockEntity(blockPos);
					if (blockEntity instanceof MobSpawnerBlockEntity) {
						((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(EntityType.SILVERFISH);
					}
				}
			}
		}
	}

	public static class PrisonHall extends StrongholdGenerator.Piece {
		protected static final int SIZE_X = 9;
		protected static final int SIZE_Y = 5;
		protected static final int SIZE_Z = 11;

		public PrisonHall(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.STRONGHOLD_PRISON_HALL, chainLength, boundingBox);
			this.setOrientation(orientation);
			this.entryDoor = this.getRandomEntrance(random);
		}

		public PrisonHall(NbtCompound nbt) {
			super(StructurePieceType.STRONGHOLD_PRISON_HALL, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			this.fillForwardOpening((StrongholdGenerator.Start)start, holder, random, 1, 1);
		}

		public static StrongholdGenerator.PrisonHall create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 9, 5, 11, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new StrongholdGenerator.PrisonHall(chainLength, random, blockBox, orientation)
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
			BlockPos pos
		) {
			this.fillWithOutline(world, chunkBox, 0, 0, 0, 8, 4, 10, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(world, random, chunkBox, this.entryDoor, 1, 1, 0);
			this.fillWithOutline(world, chunkBox, 1, 1, 10, 3, 3, 10, AIR, AIR, false);
			this.fillWithOutline(world, chunkBox, 4, 1, 1, 4, 3, 1, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 4, 1, 3, 4, 3, 3, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 4, 1, 7, 4, 3, 7, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(world, chunkBox, 4, 1, 9, 4, 3, 9, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);

			for (int i = 1; i <= 3; i++) {
				this.addBlock(
					world, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true)), 4, i, 4, chunkBox
				);
				this.addBlock(
					world,
					Blocks.IRON_BARS
						.getDefaultState()
						.with(PaneBlock.NORTH, Boolean.valueOf(true))
						.with(PaneBlock.SOUTH, Boolean.valueOf(true))
						.with(PaneBlock.EAST, Boolean.valueOf(true)),
					4,
					i,
					5,
					chunkBox
				);
				this.addBlock(
					world, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true)), 4, i, 6, chunkBox
				);
				this.addBlock(
					world, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)).with(PaneBlock.EAST, Boolean.valueOf(true)), 5, i, 5, chunkBox
				);
				this.addBlock(
					world, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)).with(PaneBlock.EAST, Boolean.valueOf(true)), 6, i, 5, chunkBox
				);
				this.addBlock(
					world, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)).with(PaneBlock.EAST, Boolean.valueOf(true)), 7, i, 5, chunkBox
				);
			}

			this.addBlock(
				world, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true)), 4, 3, 2, chunkBox
			);
			this.addBlock(
				world, Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true)), 4, 3, 8, chunkBox
			);
			BlockState blockState = Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.WEST);
			BlockState blockState2 = Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.WEST).with(DoorBlock.HALF, DoubleBlockHalf.UPPER);
			this.addBlock(world, blockState, 4, 1, 2, chunkBox);
			this.addBlock(world, blockState2, 4, 2, 2, chunkBox);
			this.addBlock(world, blockState, 4, 1, 8, chunkBox);
			this.addBlock(world, blockState2, 4, 2, 8, chunkBox);
		}
	}

	public static class RightTurn extends StrongholdGenerator.Turn {
		public RightTurn(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.STRONGHOLD_RIGHT_TURN, chainLength, boundingBox);
			this.setOrientation(orientation);
			this.entryDoor = this.getRandomEntrance(random);
		}

		public RightTurn(NbtCompound nbt) {
			super(StructurePieceType.STRONGHOLD_RIGHT_TURN, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			Direction direction = this.getFacing();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.fillNWOpening((StrongholdGenerator.Start)start, holder, random, 1, 1);
			} else {
				this.fillSEOpening((StrongholdGenerator.Start)start, holder, random, 1, 1);
			}
		}

		public static StrongholdGenerator.RightTurn create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, 5, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new StrongholdGenerator.RightTurn(chainLength, random, blockBox, orientation)
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
			BlockPos pos
		) {
			this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 4, 4, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(world, random, chunkBox, this.entryDoor, 1, 1, 0);
			Direction direction = this.getFacing();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.fillWithOutline(world, chunkBox, 0, 1, 1, 0, 3, 3, AIR, AIR, false);
			} else {
				this.fillWithOutline(world, chunkBox, 4, 1, 1, 4, 3, 3, AIR, AIR, false);
			}
		}
	}

	public static class SmallCorridor extends StrongholdGenerator.Piece {
		private final int length;

		public SmallCorridor(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.STRONGHOLD_SMALL_CORRIDOR, chainLength, boundingBox);
			this.setOrientation(orientation);
			this.length = orientation != Direction.NORTH && orientation != Direction.SOUTH ? boundingBox.getBlockCountX() : boundingBox.getBlockCountZ();
		}

		public SmallCorridor(NbtCompound nbt) {
			super(StructurePieceType.STRONGHOLD_SMALL_CORRIDOR, nbt);
			this.length = nbt.getInt("Steps");
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			super.writeNbt(context, nbt);
			nbt.putInt("Steps", this.length);
		}

		public static BlockBox create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation) {
			int i = 3;
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, 4, orientation);
			StructurePiece structurePiece = holder.getIntersecting(blockBox);
			if (structurePiece == null) {
				return null;
			} else {
				if (structurePiece.getBoundingBox().getMinY() == blockBox.getMinY()) {
					for (int j = 2; j >= 1; j--) {
						blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, j, orientation);
						if (!structurePiece.getBoundingBox().intersects(blockBox)) {
							return BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, j + 1, orientation);
						}
					}
				}

				return null;
			}
		}

		@Override
		public void generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox chunkBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			for (int i = 0; i < this.length; i++) {
				this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 0, 0, i, chunkBox);
				this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 0, i, chunkBox);
				this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 2, 0, i, chunkBox);
				this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 0, i, chunkBox);
				this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 4, 0, i, chunkBox);

				for (int j = 1; j <= 3; j++) {
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 0, j, i, chunkBox);
					this.addBlock(world, Blocks.CAVE_AIR.getDefaultState(), 1, j, i, chunkBox);
					this.addBlock(world, Blocks.CAVE_AIR.getDefaultState(), 2, j, i, chunkBox);
					this.addBlock(world, Blocks.CAVE_AIR.getDefaultState(), 3, j, i, chunkBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 4, j, i, chunkBox);
				}

				this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 0, 4, i, chunkBox);
				this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 4, i, chunkBox);
				this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 2, 4, i, chunkBox);
				this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 4, i, chunkBox);
				this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 4, 4, i, chunkBox);
			}
		}
	}

	public static class SpiralStaircase extends StrongholdGenerator.Piece {
		private static final int SIZE_X = 5;
		private static final int SIZE_Y = 11;
		private static final int SIZE_Z = 5;
		private final boolean isStructureStart;

		public SpiralStaircase(StructurePieceType structurePieceType, int chainLength, int x, int z, Direction orientation) {
			super(structurePieceType, chainLength, createBox(x, 64, z, orientation, 5, 11, 5));
			this.isStructureStart = true;
			this.setOrientation(orientation);
			this.entryDoor = StrongholdGenerator.Piece.EntranceType.OPENING;
		}

		public SpiralStaircase(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.STRONGHOLD_SPIRAL_STAIRCASE, chainLength, boundingBox);
			this.isStructureStart = false;
			this.setOrientation(orientation);
			this.entryDoor = this.getRandomEntrance(random);
		}

		public SpiralStaircase(StructurePieceType structurePieceType, NbtCompound nbtCompound) {
			super(structurePieceType, nbtCompound);
			this.isStructureStart = nbtCompound.getBoolean("Source");
		}

		public SpiralStaircase(NbtCompound nbt) {
			this(StructurePieceType.STRONGHOLD_SPIRAL_STAIRCASE, nbt);
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			super.writeNbt(context, nbt);
			nbt.putBoolean("Source", this.isStructureStart);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			if (this.isStructureStart) {
				StrongholdGenerator.activePieceType = StrongholdGenerator.FiveWayCrossing.class;
			}

			this.fillForwardOpening((StrongholdGenerator.Start)start, holder, random, 1, 1);
		}

		public static StrongholdGenerator.SpiralStaircase create(
			StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -7, 0, 5, 11, 5, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new StrongholdGenerator.SpiralStaircase(chainLength, random, blockBox, orientation)
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
			BlockPos pos
		) {
			this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 10, 4, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(world, random, chunkBox, this.entryDoor, 1, 7, 0);
			this.generateEntrance(world, random, chunkBox, StrongholdGenerator.Piece.EntranceType.OPENING, 1, 1, 4);
			this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 2, 6, 1, chunkBox);
			this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 5, 1, chunkBox);
			this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 6, 1, chunkBox);
			this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 5, 2, chunkBox);
			this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 4, 3, chunkBox);
			this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 5, 3, chunkBox);
			this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 2, 4, 3, chunkBox);
			this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 3, 3, chunkBox);
			this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 3, 4, 3, chunkBox);
			this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 3, 2, chunkBox);
			this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 2, 1, chunkBox);
			this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 3, 3, 1, chunkBox);
			this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 2, 2, 1, chunkBox);
			this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 1, 1, chunkBox);
			this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 2, 1, chunkBox);
			this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 1, 2, chunkBox);
			this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 1, 3, chunkBox);
		}
	}

	public static class SquareRoom extends StrongholdGenerator.Piece {
		protected static final int SIZE_X = 11;
		protected static final int SIZE_Y = 7;
		protected static final int SIZE_Z = 11;
		protected final int roomType;

		public SquareRoom(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.STRONGHOLD_SQUARE_ROOM, chainLength, boundingBox);
			this.setOrientation(orientation);
			this.entryDoor = this.getRandomEntrance(random);
			this.roomType = random.nextInt(5);
		}

		public SquareRoom(NbtCompound nbt) {
			super(StructurePieceType.STRONGHOLD_SQUARE_ROOM, nbt);
			this.roomType = nbt.getInt("Type");
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			super.writeNbt(context, nbt);
			nbt.putInt("Type", this.roomType);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			this.fillForwardOpening((StrongholdGenerator.Start)start, holder, random, 4, 1);
			this.fillNWOpening((StrongholdGenerator.Start)start, holder, random, 1, 4);
			this.fillSEOpening((StrongholdGenerator.Start)start, holder, random, 1, 4);
		}

		public static StrongholdGenerator.SquareRoom create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -4, -1, 0, 11, 7, 11, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null
				? new StrongholdGenerator.SquareRoom(chainLength, random, blockBox, orientation)
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
			BlockPos pos
		) {
			this.fillWithOutline(world, chunkBox, 0, 0, 0, 10, 6, 10, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(world, random, chunkBox, this.entryDoor, 4, 1, 0);
			this.fillWithOutline(world, chunkBox, 4, 1, 10, 6, 3, 10, AIR, AIR, false);
			this.fillWithOutline(world, chunkBox, 0, 1, 4, 0, 3, 6, AIR, AIR, false);
			this.fillWithOutline(world, chunkBox, 10, 1, 4, 10, 3, 6, AIR, AIR, false);
			switch (this.roomType) {
				case 0:
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 5, 1, 5, chunkBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 5, 2, 5, chunkBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 5, 3, 5, chunkBox);
					this.addBlock(world, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST), 4, 3, 5, chunkBox);
					this.addBlock(world, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST), 6, 3, 5, chunkBox);
					this.addBlock(world, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH), 5, 3, 4, chunkBox);
					this.addBlock(world, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.NORTH), 5, 3, 6, chunkBox);
					this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 4, chunkBox);
					this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 5, chunkBox);
					this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 6, chunkBox);
					this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 4, chunkBox);
					this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 5, chunkBox);
					this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 6, chunkBox);
					this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 5, 1, 4, chunkBox);
					this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 5, 1, 6, chunkBox);
					break;
				case 1:
					for (int i = 0; i < 5; i++) {
						this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 1, 3 + i, chunkBox);
						this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 7, 1, 3 + i, chunkBox);
						this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 3 + i, 1, 3, chunkBox);
						this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 3 + i, 1, 7, chunkBox);
					}

					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 5, 1, 5, chunkBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 5, 2, 5, chunkBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 5, 3, 5, chunkBox);
					this.addBlock(world, Blocks.WATER.getDefaultState(), 5, 4, 5, chunkBox);
					break;
				case 2:
					for (int i = 1; i <= 9; i++) {
						this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 1, 3, i, chunkBox);
						this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 9, 3, i, chunkBox);
					}

					for (int i = 1; i <= 9; i++) {
						this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), i, 3, 1, chunkBox);
						this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), i, 3, 9, chunkBox);
					}

					this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 4, chunkBox);
					this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 6, chunkBox);
					this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 4, chunkBox);
					this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 6, chunkBox);
					this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 4, 1, 5, chunkBox);
					this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 6, 1, 5, chunkBox);
					this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 4, 3, 5, chunkBox);
					this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 6, 3, 5, chunkBox);

					for (int i = 1; i <= 3; i++) {
						this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 4, i, 4, chunkBox);
						this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 6, i, 4, chunkBox);
						this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 4, i, 6, chunkBox);
						this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 6, i, 6, chunkBox);
					}

					this.addBlock(world, Blocks.TORCH.getDefaultState(), 5, 3, 5, chunkBox);

					for (int i = 2; i <= 8; i++) {
						this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 2, 3, i, chunkBox);
						this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 3, 3, i, chunkBox);
						if (i <= 3 || i >= 7) {
							this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 4, 3, i, chunkBox);
							this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 5, 3, i, chunkBox);
							this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 6, 3, i, chunkBox);
						}

						this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 7, 3, i, chunkBox);
						this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 8, 3, i, chunkBox);
					}

					BlockState blockState = Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, Direction.WEST);
					this.addBlock(world, blockState, 9, 1, 3, chunkBox);
					this.addBlock(world, blockState, 9, 2, 3, chunkBox);
					this.addBlock(world, blockState, 9, 3, 3, chunkBox);
					this.addChest(world, chunkBox, random, 3, 4, 8, LootTables.STRONGHOLD_CROSSING_CHEST);
			}
		}
	}

	public static class Stairs extends StrongholdGenerator.Piece {
		private static final int SIZE_X = 5;
		private static final int SIZE_Y = 11;
		private static final int SIZE_Z = 8;

		public Stairs(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.STRONGHOLD_STAIRS, chainLength, boundingBox);
			this.setOrientation(orientation);
			this.entryDoor = this.getRandomEntrance(random);
		}

		public Stairs(NbtCompound nbt) {
			super(StructurePieceType.STRONGHOLD_STAIRS, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
			this.fillForwardOpening((StrongholdGenerator.Start)start, holder, random, 1, 1);
		}

		public static StrongholdGenerator.Stairs create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -7, 0, 5, 11, 8, orientation);
			return isInBounds(blockBox) && holder.getIntersecting(blockBox) == null ? new StrongholdGenerator.Stairs(chainLength, random, blockBox, orientation) : null;
		}

		@Override
		public void generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox chunkBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 10, 7, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(world, random, chunkBox, this.entryDoor, 1, 7, 0);
			this.generateEntrance(world, random, chunkBox, StrongholdGenerator.Piece.EntranceType.OPENING, 1, 1, 7);
			BlockState blockState = Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);

			for (int i = 0; i < 6; i++) {
				this.addBlock(world, blockState, 1, 6 - i, 1 + i, chunkBox);
				this.addBlock(world, blockState, 2, 6 - i, 1 + i, chunkBox);
				this.addBlock(world, blockState, 3, 6 - i, 1 + i, chunkBox);
				if (i < 5) {
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 5 - i, 1 + i, chunkBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 2, 5 - i, 1 + i, chunkBox);
					this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 5 - i, 1 + i, chunkBox);
				}
			}
		}
	}

	public static class Start extends StrongholdGenerator.SpiralStaircase {
		public StrongholdGenerator.PieceData lastPiece;
		@Nullable
		public StrongholdGenerator.PortalRoom portalRoom;
		public final List<StructurePiece> pieces = Lists.<StructurePiece>newArrayList();

		public Start(Random random, int i, int j) {
			super(StructurePieceType.STRONGHOLD_START, 0, i, j, getRandomHorizontalDirection(random));
		}

		public Start(NbtCompound nbtCompound) {
			super(StructurePieceType.STRONGHOLD_START, nbtCompound);
		}

		@Override
		public BlockPos getCenter() {
			return this.portalRoom != null ? this.portalRoom.getCenter() : super.getCenter();
		}
	}

	static class StoneBrickRandomizer extends StructurePiece.BlockRandomizer {
		@Override
		public void setBlock(Random random, int x, int y, int z, boolean placeBlock) {
			if (placeBlock) {
				float f = random.nextFloat();
				if (f < 0.2F) {
					this.block = Blocks.CRACKED_STONE_BRICKS.getDefaultState();
				} else if (f < 0.5F) {
					this.block = Blocks.MOSSY_STONE_BRICKS.getDefaultState();
				} else if (f < 0.55F) {
					this.block = Blocks.INFESTED_STONE_BRICKS.getDefaultState();
				} else {
					this.block = Blocks.STONE_BRICKS.getDefaultState();
				}
			} else {
				this.block = Blocks.CAVE_AIR.getDefaultState();
			}
		}
	}

	public abstract static class Turn extends StrongholdGenerator.Piece {
		protected static final int SIZE_X = 5;
		protected static final int SIZE_Y = 5;
		protected static final int SIZE_Z = 5;

		protected Turn(StructurePieceType structurePieceType, int i, BlockBox blockBox) {
			super(structurePieceType, i, blockBox);
		}

		public Turn(StructurePieceType structurePieceType, NbtCompound nbtCompound) {
			super(structurePieceType, nbtCompound);
		}
	}
}
