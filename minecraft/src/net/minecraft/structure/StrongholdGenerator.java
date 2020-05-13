package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractButtonBlock;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class StrongholdGenerator {
	private static final StrongholdGenerator.PieceSetting[] ALL_PIECE_SETTINGS = new StrongholdGenerator.PieceSetting[]{
		new StrongholdGenerator.PieceSetting(StrongholdGenerator.Corridor.class, 40, 0),
		new StrongholdGenerator.PieceSetting(StrongholdGenerator.PrisonHall.class, 5, 5),
		new StrongholdGenerator.PieceSetting(StrongholdGenerator.LeftTurn.class, 20, 0),
		new StrongholdGenerator.PieceSetting(StrongholdGenerator.RightTurn.class, 20, 0),
		new StrongholdGenerator.PieceSetting(StrongholdGenerator.SquareRoom.class, 10, 6),
		new StrongholdGenerator.PieceSetting(StrongholdGenerator.Stairs.class, 5, 5),
		new StrongholdGenerator.PieceSetting(StrongholdGenerator.SpiralStaircase.class, 5, 5),
		new StrongholdGenerator.PieceSetting(StrongholdGenerator.FiveWayCrossing.class, 5, 4),
		new StrongholdGenerator.PieceSetting(StrongholdGenerator.ChestCorridor.class, 5, 4),
		new StrongholdGenerator.PieceSetting(StrongholdGenerator.Library.class, 10, 2) {
			@Override
			public boolean canGenerate(int depth) {
				return super.canGenerate(depth) && depth > 4;
			}
		},
		new StrongholdGenerator.PieceSetting(StrongholdGenerator.PortalRoom.class, 20, 1) {
			@Override
			public boolean canGenerate(int depth) {
				return super.canGenerate(depth) && depth > 5;
			}
		}
	};
	private static List<StrongholdGenerator.PieceSetting> possiblePieceSettings;
	private static Class<? extends StrongholdGenerator.Piece> activePieceType;
	private static int field_15264;
	private static final StrongholdGenerator.StoneBrickRandomizer STONE_BRICK_RANDOMIZER = new StrongholdGenerator.StoneBrickRandomizer();

	public static void init() {
		possiblePieceSettings = Lists.<StrongholdGenerator.PieceSetting>newArrayList();

		for (StrongholdGenerator.PieceSetting pieceSetting : ALL_PIECE_SETTINGS) {
			pieceSetting.generatedCount = 0;
			possiblePieceSettings.add(pieceSetting);
		}

		activePieceType = null;
	}

	private static boolean method_14852() {
		boolean bl = false;
		field_15264 = 0;

		for (StrongholdGenerator.PieceSetting pieceSetting : possiblePieceSettings) {
			if (pieceSetting.limit > 0 && pieceSetting.generatedCount < pieceSetting.limit) {
				bl = true;
			}

			field_15264 = field_15264 + pieceSetting.field_15278;
		}

		return bl;
	}

	private static StrongholdGenerator.Piece method_14847(
		Class<? extends StrongholdGenerator.Piece> class_, List<StructurePiece> list, Random random, int i, int j, int k, @Nullable Direction direction, int l
	) {
		StrongholdGenerator.Piece piece = null;
		if (class_ == StrongholdGenerator.Corridor.class) {
			piece = StrongholdGenerator.Corridor.method_14867(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.PrisonHall.class) {
			piece = StrongholdGenerator.PrisonHall.method_14864(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.LeftTurn.class) {
			piece = StrongholdGenerator.LeftTurn.method_14859(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.RightTurn.class) {
			piece = StrongholdGenerator.RightTurn.method_16652(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.SquareRoom.class) {
			piece = StrongholdGenerator.SquareRoom.method_14865(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.Stairs.class) {
			piece = StrongholdGenerator.Stairs.method_14868(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.SpiralStaircase.class) {
			piece = StrongholdGenerator.SpiralStaircase.method_14866(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.FiveWayCrossing.class) {
			piece = StrongholdGenerator.FiveWayCrossing.method_14858(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.ChestCorridor.class) {
			piece = StrongholdGenerator.ChestCorridor.method_14856(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.Library.class) {
			piece = StrongholdGenerator.Library.method_14860(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.PortalRoom.class) {
			piece = StrongholdGenerator.PortalRoom.method_14863(list, i, j, k, direction, l);
		}

		return piece;
	}

	private static StrongholdGenerator.Piece method_14851(
		StrongholdGenerator.Start start, List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l
	) {
		if (!method_14852()) {
			return null;
		} else {
			if (activePieceType != null) {
				StrongholdGenerator.Piece piece = method_14847(activePieceType, list, random, i, j, k, direction, l);
				activePieceType = null;
				if (piece != null) {
					return piece;
				}
			}

			int m = 0;

			while (m < 5) {
				m++;
				int n = random.nextInt(field_15264);

				for (StrongholdGenerator.PieceSetting pieceSetting : possiblePieceSettings) {
					n -= pieceSetting.field_15278;
					if (n < 0) {
						if (!pieceSetting.canGenerate(l) || pieceSetting == start.field_15284) {
							break;
						}

						StrongholdGenerator.Piece piece2 = method_14847(pieceSetting.pieceType, list, random, i, j, k, direction, l);
						if (piece2 != null) {
							pieceSetting.generatedCount++;
							start.field_15284 = pieceSetting;
							if (!pieceSetting.canGenerate()) {
								possiblePieceSettings.remove(pieceSetting);
							}

							return piece2;
						}
					}
				}
			}

			BlockBox blockBox = StrongholdGenerator.SmallCorridor.method_14857(list, random, i, j, k, direction);
			return blockBox != null && blockBox.minY > 1 ? new StrongholdGenerator.SmallCorridor(l, blockBox, direction) : null;
		}
	}

	private static StructurePiece method_14854(
		StrongholdGenerator.Start start, List<StructurePiece> list, Random random, int i, int j, int k, @Nullable Direction direction, int l
	) {
		if (l > 50) {
			return null;
		} else if (Math.abs(i - start.getBoundingBox().minX) <= 112 && Math.abs(k - start.getBoundingBox().minZ) <= 112) {
			StructurePiece structurePiece = method_14851(start, list, random, i, j, k, direction, l + 1);
			if (structurePiece != null) {
				list.add(structurePiece);
				start.field_15282.add(structurePiece);
			}

			return structurePiece;
		} else {
			return null;
		}
	}

	public static class ChestCorridor extends StrongholdGenerator.Piece {
		private boolean chestGenerated;

		public ChestCorridor(int i, Random random, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, i);
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = blockBox;
		}

		public ChestCorridor(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, compoundTag);
			this.chestGenerated = compoundTag.getBoolean("Chest");
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putBoolean("Chest", this.chestGenerated);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
		}

		public static StrongholdGenerator.ChestCorridor method_14856(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, 7, direction);
			return method_14871(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new StrongholdGenerator.ChestCorridor(l, random, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			ServerWorldAccess serverWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(serverWorldAccess, boundingBox, 0, 0, 0, 4, 4, 6, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 1, 1, 0);
			this.generateEntrance(serverWorldAccess, random, boundingBox, StrongholdGenerator.Piece.EntranceType.OPENING, 1, 1, 6);
			this.fillWithOutline(serverWorldAccess, boundingBox, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.getDefaultState(), Blocks.STONE_BRICKS.getDefaultState(), false);
			this.addBlock(serverWorldAccess, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 1, 1, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 1, 5, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 2, 2, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 2, 4, boundingBox);

			for (int i = 2; i <= 4; i++) {
				this.addBlock(serverWorldAccess, Blocks.STONE_BRICK_SLAB.getDefaultState(), 2, 1, i, boundingBox);
			}

			if (!this.chestGenerated && boundingBox.contains(new BlockPos(this.applyXTransform(3, 3), this.applyYTransform(2), this.applyZTransform(3, 3)))) {
				this.chestGenerated = true;
				this.addChest(serverWorldAccess, boundingBox, random, 3, 2, 3, LootTables.STRONGHOLD_CORRIDOR_CHEST);
			}

			return true;
		}
	}

	public static class Corridor extends StrongholdGenerator.Piece {
		private final boolean leftExitExists;
		private final boolean rightExitExixts;

		public Corridor(int i, Random random, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_CORRIDOR, i);
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = blockBox;
			this.leftExitExists = random.nextInt(2) == 0;
			this.rightExitExixts = random.nextInt(2) == 0;
		}

		public Corridor(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_CORRIDOR, compoundTag);
			this.leftExitExists = compoundTag.getBoolean("Left");
			this.rightExitExixts = compoundTag.getBoolean("Right");
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putBoolean("Left", this.leftExitExists);
			tag.putBoolean("Right", this.rightExitExixts);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
			if (this.leftExitExists) {
				this.method_14870((StrongholdGenerator.Start)structurePiece, list, random, 1, 2);
			}

			if (this.rightExitExixts) {
				this.method_14873((StrongholdGenerator.Start)structurePiece, list, random, 1, 2);
			}
		}

		public static StrongholdGenerator.Corridor method_14867(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, 7, direction);
			return method_14871(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new StrongholdGenerator.Corridor(l, random, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			ServerWorldAccess serverWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(serverWorldAccess, boundingBox, 0, 0, 0, 4, 4, 6, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 1, 1, 0);
			this.generateEntrance(serverWorldAccess, random, boundingBox, StrongholdGenerator.Piece.EntranceType.OPENING, 1, 1, 6);
			BlockState blockState = Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST);
			BlockState blockState2 = Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST);
			this.addBlockWithRandomThreshold(serverWorldAccess, boundingBox, random, 0.1F, 1, 2, 1, blockState);
			this.addBlockWithRandomThreshold(serverWorldAccess, boundingBox, random, 0.1F, 3, 2, 1, blockState2);
			this.addBlockWithRandomThreshold(serverWorldAccess, boundingBox, random, 0.1F, 1, 2, 5, blockState);
			this.addBlockWithRandomThreshold(serverWorldAccess, boundingBox, random, 0.1F, 3, 2, 5, blockState2);
			if (this.leftExitExists) {
				this.fillWithOutline(serverWorldAccess, boundingBox, 0, 1, 2, 0, 3, 4, AIR, AIR, false);
			}

			if (this.rightExitExixts) {
				this.fillWithOutline(serverWorldAccess, boundingBox, 4, 1, 2, 4, 3, 4, AIR, AIR, false);
			}

			return true;
		}
	}

	public static class FiveWayCrossing extends StrongholdGenerator.Piece {
		private final boolean lowerLeftExists;
		private final boolean upperLeftExists;
		private final boolean lowerRightExists;
		private final boolean upperRightExists;

		public FiveWayCrossing(int i, Random random, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_FIVE_WAY_CROSSING, i);
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = blockBox;
			this.lowerLeftExists = random.nextBoolean();
			this.upperLeftExists = random.nextBoolean();
			this.lowerRightExists = random.nextBoolean();
			this.upperRightExists = random.nextInt(3) > 0;
		}

		public FiveWayCrossing(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_FIVE_WAY_CROSSING, compoundTag);
			this.lowerLeftExists = compoundTag.getBoolean("leftLow");
			this.upperLeftExists = compoundTag.getBoolean("leftHigh");
			this.lowerRightExists = compoundTag.getBoolean("rightLow");
			this.upperRightExists = compoundTag.getBoolean("rightHigh");
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putBoolean("leftLow", this.lowerLeftExists);
			tag.putBoolean("leftHigh", this.upperLeftExists);
			tag.putBoolean("rightLow", this.lowerRightExists);
			tag.putBoolean("rightHigh", this.upperRightExists);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			int i = 3;
			int j = 5;
			Direction direction = this.getFacing();
			if (direction == Direction.WEST || direction == Direction.NORTH) {
				i = 8 - i;
				j = 8 - j;
			}

			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 5, 1);
			if (this.lowerLeftExists) {
				this.method_14870((StrongholdGenerator.Start)structurePiece, list, random, i, 1);
			}

			if (this.upperLeftExists) {
				this.method_14870((StrongholdGenerator.Start)structurePiece, list, random, j, 7);
			}

			if (this.lowerRightExists) {
				this.method_14873((StrongholdGenerator.Start)structurePiece, list, random, i, 1);
			}

			if (this.upperRightExists) {
				this.method_14873((StrongholdGenerator.Start)structurePiece, list, random, j, 7);
			}
		}

		public static StrongholdGenerator.FiveWayCrossing method_14858(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -4, -3, 0, 10, 9, 11, direction);
			return method_14871(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new StrongholdGenerator.FiveWayCrossing(l, random, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			ServerWorldAccess serverWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(serverWorldAccess, boundingBox, 0, 0, 0, 9, 8, 10, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 4, 3, 0);
			if (this.lowerLeftExists) {
				this.fillWithOutline(serverWorldAccess, boundingBox, 0, 3, 1, 0, 5, 3, AIR, AIR, false);
			}

			if (this.lowerRightExists) {
				this.fillWithOutline(serverWorldAccess, boundingBox, 9, 3, 1, 9, 5, 3, AIR, AIR, false);
			}

			if (this.upperLeftExists) {
				this.fillWithOutline(serverWorldAccess, boundingBox, 0, 5, 7, 0, 7, 9, AIR, AIR, false);
			}

			if (this.upperRightExists) {
				this.fillWithOutline(serverWorldAccess, boundingBox, 9, 5, 7, 9, 7, 9, AIR, AIR, false);
			}

			this.fillWithOutline(serverWorldAccess, boundingBox, 5, 1, 10, 7, 3, 10, AIR, AIR, false);
			this.fillWithOutline(serverWorldAccess, boundingBox, 1, 2, 1, 8, 2, 6, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(serverWorldAccess, boundingBox, 4, 1, 5, 4, 4, 9, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(serverWorldAccess, boundingBox, 8, 1, 5, 8, 4, 9, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(serverWorldAccess, boundingBox, 1, 4, 7, 3, 4, 9, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(serverWorldAccess, boundingBox, 1, 3, 5, 3, 3, 6, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(
				serverWorldAccess, boundingBox, 1, 3, 4, 3, 3, 4, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false
			);
			this.fillWithOutline(
				serverWorldAccess, boundingBox, 1, 4, 6, 3, 4, 6, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false
			);
			this.fillWithOutline(serverWorldAccess, boundingBox, 5, 1, 7, 7, 1, 8, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(
				serverWorldAccess, boundingBox, 5, 1, 9, 7, 1, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false
			);
			this.fillWithOutline(
				serverWorldAccess, boundingBox, 5, 2, 7, 7, 2, 7, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false
			);
			this.fillWithOutline(
				serverWorldAccess, boundingBox, 4, 5, 7, 4, 5, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false
			);
			this.fillWithOutline(
				serverWorldAccess, boundingBox, 8, 5, 7, 8, 5, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false
			);
			this.fillWithOutline(
				serverWorldAccess,
				boundingBox,
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
			this.addBlock(serverWorldAccess, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH), 6, 5, 6, boundingBox);
			return true;
		}
	}

	public static class LeftTurn extends StrongholdGenerator.Turn {
		public LeftTurn(int i, Random random, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_LEFT_TURN, i);
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = blockBox;
		}

		public LeftTurn(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_LEFT_TURN, compoundTag);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			Direction direction = this.getFacing();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.method_14873((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
			} else {
				this.method_14870((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
			}
		}

		public static StrongholdGenerator.LeftTurn method_14859(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, 5, direction);
			return method_14871(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new StrongholdGenerator.LeftTurn(l, random, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			ServerWorldAccess serverWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(serverWorldAccess, boundingBox, 0, 0, 0, 4, 4, 4, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 1, 1, 0);
			Direction direction = this.getFacing();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.fillWithOutline(serverWorldAccess, boundingBox, 4, 1, 1, 4, 3, 3, AIR, AIR, false);
			} else {
				this.fillWithOutline(serverWorldAccess, boundingBox, 0, 1, 1, 0, 3, 3, AIR, AIR, false);
			}

			return true;
		}
	}

	public static class Library extends StrongholdGenerator.Piece {
		private final boolean tall;

		public Library(int i, Random random, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_LIBRARY, i);
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = blockBox;
			this.tall = blockBox.getBlockCountY() > 6;
		}

		public Library(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_LIBRARY, compoundTag);
			this.tall = compoundTag.getBoolean("Tall");
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putBoolean("Tall", this.tall);
		}

		public static StrongholdGenerator.Library method_14860(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -4, -1, 0, 14, 11, 15, direction);
			if (!method_14871(blockBox) || StructurePiece.getOverlappingPiece(list, blockBox) != null) {
				blockBox = BlockBox.rotated(i, j, k, -4, -1, 0, 14, 6, 15, direction);
				if (!method_14871(blockBox) || StructurePiece.getOverlappingPiece(list, blockBox) != null) {
					return null;
				}
			}

			return new StrongholdGenerator.Library(l, random, blockBox, direction);
		}

		@Override
		public boolean generate(
			ServerWorldAccess serverWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			int i = 11;
			if (!this.tall) {
				i = 6;
			}

			this.fillWithOutline(serverWorldAccess, boundingBox, 0, 0, 0, 13, i - 1, 14, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 4, 1, 0);
			this.fillWithOutlineUnderSealevel(
				serverWorldAccess, boundingBox, random, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.COBWEB.getDefaultState(), Blocks.COBWEB.getDefaultState(), false, false
			);
			int j = 1;
			int k = 12;

			for (int l = 1; l <= 13; l++) {
				if ((l - 1) % 4 == 0) {
					this.fillWithOutline(serverWorldAccess, boundingBox, 1, 1, l, 1, 4, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
					this.fillWithOutline(serverWorldAccess, boundingBox, 12, 1, l, 12, 4, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
					this.addBlock(serverWorldAccess, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST), 2, 3, l, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST), 11, 3, l, boundingBox);
					if (this.tall) {
						this.fillWithOutline(serverWorldAccess, boundingBox, 1, 6, l, 1, 9, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
						this.fillWithOutline(serverWorldAccess, boundingBox, 12, 6, l, 12, 9, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
					}
				} else {
					this.fillWithOutline(serverWorldAccess, boundingBox, 1, 1, l, 1, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
					this.fillWithOutline(serverWorldAccess, boundingBox, 12, 1, l, 12, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
					if (this.tall) {
						this.fillWithOutline(serverWorldAccess, boundingBox, 1, 6, l, 1, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
						this.fillWithOutline(serverWorldAccess, boundingBox, 12, 6, l, 12, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
					}
				}
			}

			for (int lx = 3; lx < 12; lx += 2) {
				this.fillWithOutline(serverWorldAccess, boundingBox, 3, 1, lx, 4, 3, lx, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
				this.fillWithOutline(serverWorldAccess, boundingBox, 6, 1, lx, 7, 3, lx, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
				this.fillWithOutline(serverWorldAccess, boundingBox, 9, 1, lx, 10, 3, lx, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
			}

			if (this.tall) {
				this.fillWithOutline(serverWorldAccess, boundingBox, 1, 5, 1, 3, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
				this.fillWithOutline(serverWorldAccess, boundingBox, 10, 5, 1, 12, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
				this.fillWithOutline(serverWorldAccess, boundingBox, 4, 5, 1, 9, 5, 2, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
				this.fillWithOutline(serverWorldAccess, boundingBox, 4, 5, 12, 9, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
				this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 9, 5, 11, boundingBox);
				this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 8, 5, 11, boundingBox);
				this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 9, 5, 10, boundingBox);
				BlockState blockState = Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true));
				BlockState blockState2 = Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true));
				this.fillWithOutline(serverWorldAccess, boundingBox, 3, 6, 3, 3, 6, 11, blockState2, blockState2, false);
				this.fillWithOutline(serverWorldAccess, boundingBox, 10, 6, 3, 10, 6, 9, blockState2, blockState2, false);
				this.fillWithOutline(serverWorldAccess, boundingBox, 4, 6, 2, 9, 6, 2, blockState, blockState, false);
				this.fillWithOutline(serverWorldAccess, boundingBox, 4, 6, 12, 7, 6, 12, blockState, blockState, false);
				this.addBlock(
					serverWorldAccess,
					Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
					3,
					6,
					2,
					boundingBox
				);
				this.addBlock(
					serverWorldAccess,
					Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
					3,
					6,
					12,
					boundingBox
				);
				this.addBlock(
					serverWorldAccess,
					Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
					10,
					6,
					2,
					boundingBox
				);

				for (int m = 0; m <= 2; m++) {
					this.addBlock(
						serverWorldAccess,
						Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
						8 + m,
						6,
						12 - m,
						boundingBox
					);
					if (m != 2) {
						this.addBlock(
							serverWorldAccess,
							Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
							8 + m,
							6,
							11 - m,
							boundingBox
						);
					}
				}

				BlockState blockState3 = Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, Direction.SOUTH);
				this.addBlock(serverWorldAccess, blockState3, 10, 1, 13, boundingBox);
				this.addBlock(serverWorldAccess, blockState3, 10, 2, 13, boundingBox);
				this.addBlock(serverWorldAccess, blockState3, 10, 3, 13, boundingBox);
				this.addBlock(serverWorldAccess, blockState3, 10, 4, 13, boundingBox);
				this.addBlock(serverWorldAccess, blockState3, 10, 5, 13, boundingBox);
				this.addBlock(serverWorldAccess, blockState3, 10, 6, 13, boundingBox);
				this.addBlock(serverWorldAccess, blockState3, 10, 7, 13, boundingBox);
				int n = 7;
				int o = 7;
				BlockState blockState4 = Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true));
				this.addBlock(serverWorldAccess, blockState4, 6, 9, 7, boundingBox);
				BlockState blockState5 = Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true));
				this.addBlock(serverWorldAccess, blockState5, 7, 9, 7, boundingBox);
				this.addBlock(serverWorldAccess, blockState4, 6, 8, 7, boundingBox);
				this.addBlock(serverWorldAccess, blockState5, 7, 8, 7, boundingBox);
				BlockState blockState6 = blockState2.with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true));
				this.addBlock(serverWorldAccess, blockState6, 6, 7, 7, boundingBox);
				this.addBlock(serverWorldAccess, blockState6, 7, 7, 7, boundingBox);
				this.addBlock(serverWorldAccess, blockState4, 5, 7, 7, boundingBox);
				this.addBlock(serverWorldAccess, blockState5, 8, 7, 7, boundingBox);
				this.addBlock(serverWorldAccess, blockState4.with(FenceBlock.NORTH, Boolean.valueOf(true)), 6, 7, 6, boundingBox);
				this.addBlock(serverWorldAccess, blockState4.with(FenceBlock.SOUTH, Boolean.valueOf(true)), 6, 7, 8, boundingBox);
				this.addBlock(serverWorldAccess, blockState5.with(FenceBlock.NORTH, Boolean.valueOf(true)), 7, 7, 6, boundingBox);
				this.addBlock(serverWorldAccess, blockState5.with(FenceBlock.SOUTH, Boolean.valueOf(true)), 7, 7, 8, boundingBox);
				BlockState blockState7 = Blocks.TORCH.getDefaultState();
				this.addBlock(serverWorldAccess, blockState7, 5, 8, 7, boundingBox);
				this.addBlock(serverWorldAccess, blockState7, 8, 8, 7, boundingBox);
				this.addBlock(serverWorldAccess, blockState7, 6, 8, 6, boundingBox);
				this.addBlock(serverWorldAccess, blockState7, 6, 8, 8, boundingBox);
				this.addBlock(serverWorldAccess, blockState7, 7, 8, 6, boundingBox);
				this.addBlock(serverWorldAccess, blockState7, 7, 8, 8, boundingBox);
			}

			this.addChest(serverWorldAccess, boundingBox, random, 3, 3, 5, LootTables.STRONGHOLD_LIBRARY_CHEST);
			if (this.tall) {
				this.addBlock(serverWorldAccess, AIR, 12, 9, 1, boundingBox);
				this.addChest(serverWorldAccess, boundingBox, random, 12, 8, 1, LootTables.STRONGHOLD_LIBRARY_CHEST);
			}

			return true;
		}
	}

	abstract static class Piece extends StructurePiece {
		protected StrongholdGenerator.Piece.EntranceType entryDoor = StrongholdGenerator.Piece.EntranceType.OPENING;

		protected Piece(StructurePieceType structurePieceType, int i) {
			super(structurePieceType, i);
		}

		public Piece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
			super(structurePieceType, compoundTag);
			this.entryDoor = StrongholdGenerator.Piece.EntranceType.valueOf(compoundTag.getString("EntryDoor"));
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			tag.putString("EntryDoor", this.entryDoor.name());
		}

		protected void generateEntrance(WorldAccess world, Random random, BlockBox boundingBox, StrongholdGenerator.Piece.EntranceType type, int x, int y, int z) {
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
		protected StructurePiece method_14874(StrongholdGenerator.Start start, List<StructurePiece> list, Random random, int i, int j) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.minX + i, this.boundingBox.minY + j, this.boundingBox.minZ - 1, direction, this.getLength()
						);
					case SOUTH:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.minX + i, this.boundingBox.minY + j, this.boundingBox.maxZ + 1, direction, this.getLength()
						);
					case WEST:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + j, this.boundingBox.minZ + i, direction, this.getLength()
						);
					case EAST:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + j, this.boundingBox.minZ + i, direction, this.getLength()
						);
				}
			}

			return null;
		}

		@Nullable
		protected StructurePiece method_14870(StrongholdGenerator.Start start, List<StructurePiece> list, Random random, int i, int j) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.WEST, this.getLength()
						);
					case SOUTH:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.WEST, this.getLength()
						);
					case WEST:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.minZ - 1, Direction.NORTH, this.getLength()
						);
					case EAST:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.minZ - 1, Direction.NORTH, this.getLength()
						);
				}
			}

			return null;
		}

		@Nullable
		protected StructurePiece method_14873(StrongholdGenerator.Start start, List<StructurePiece> list, Random random, int i, int j) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.EAST, this.getLength()
						);
					case SOUTH:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.EAST, this.getLength()
						);
					case WEST:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.maxZ + 1, Direction.SOUTH, this.getLength()
						);
					case EAST:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.maxZ + 1, Direction.SOUTH, this.getLength()
						);
				}
			}

			return null;
		}

		protected static boolean method_14871(BlockBox blockBox) {
			return blockBox != null && blockBox.minY > 10;
		}

		public static enum EntranceType {
			OPENING,
			WOOD_DOOR,
			GRATES,
			IRON_DOOR;
		}
	}

	static class PieceSetting {
		public final Class<? extends StrongholdGenerator.Piece> pieceType;
		public final int field_15278;
		public int generatedCount;
		public final int limit;

		public PieceSetting(Class<? extends StrongholdGenerator.Piece> class_, int i, int j) {
			this.pieceType = class_;
			this.field_15278 = i;
			this.limit = j;
		}

		public boolean canGenerate(int depth) {
			return this.limit == 0 || this.generatedCount < this.limit;
		}

		public boolean canGenerate() {
			return this.limit == 0 || this.generatedCount < this.limit;
		}
	}

	public static class PortalRoom extends StrongholdGenerator.Piece {
		private boolean spawnerPlaced;

		public PortalRoom(int i, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, i);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
		}

		public PortalRoom(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, compoundTag);
			this.spawnerPlaced = compoundTag.getBoolean("Mob");
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putBoolean("Mob", this.spawnerPlaced);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			if (structurePiece != null) {
				((StrongholdGenerator.Start)structurePiece).field_15283 = this;
			}
		}

		public static StrongholdGenerator.PortalRoom method_14863(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -4, -1, 0, 11, 8, 16, direction);
			return method_14871(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new StrongholdGenerator.PortalRoom(l, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			ServerWorldAccess serverWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(serverWorldAccess, boundingBox, 0, 0, 0, 10, 7, 15, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(serverWorldAccess, random, boundingBox, StrongholdGenerator.Piece.EntranceType.GRATES, 4, 1, 0);
			int i = 6;
			this.fillWithOutline(serverWorldAccess, boundingBox, 1, i, 1, 1, i, 14, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(serverWorldAccess, boundingBox, 9, i, 1, 9, i, 14, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(serverWorldAccess, boundingBox, 2, i, 1, 8, i, 2, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(serverWorldAccess, boundingBox, 2, i, 14, 8, i, 14, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(serverWorldAccess, boundingBox, 1, 1, 1, 2, 1, 4, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(serverWorldAccess, boundingBox, 8, 1, 1, 9, 1, 4, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(serverWorldAccess, boundingBox, 1, 1, 1, 1, 1, 3, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
			this.fillWithOutline(serverWorldAccess, boundingBox, 9, 1, 1, 9, 1, 3, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
			this.fillWithOutline(serverWorldAccess, boundingBox, 3, 1, 8, 7, 1, 12, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(serverWorldAccess, boundingBox, 4, 1, 9, 6, 1, 11, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
			BlockState blockState = Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)).with(PaneBlock.EAST, Boolean.valueOf(true));

			for (int j = 3; j < 14; j += 2) {
				this.fillWithOutline(serverWorldAccess, boundingBox, 0, 3, j, 0, 4, j, blockState, blockState, false);
				this.fillWithOutline(serverWorldAccess, boundingBox, 10, 3, j, 10, 4, j, blockState, blockState, false);
			}

			for (int j = 2; j < 9; j += 2) {
				this.fillWithOutline(serverWorldAccess, boundingBox, j, 3, 15, j, 4, 15, blockState2, blockState2, false);
			}

			BlockState blockState3 = Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
			this.fillWithOutline(serverWorldAccess, boundingBox, 4, 1, 5, 6, 1, 7, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(serverWorldAccess, boundingBox, 4, 2, 6, 6, 2, 7, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(serverWorldAccess, boundingBox, 4, 3, 7, 6, 3, 7, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);

			for (int k = 4; k <= 6; k++) {
				this.addBlock(serverWorldAccess, blockState3, k, 1, 4, boundingBox);
				this.addBlock(serverWorldAccess, blockState3, k, 2, 5, boundingBox);
				this.addBlock(serverWorldAccess, blockState3, k, 3, 6, boundingBox);
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

			this.addBlock(serverWorldAccess, blockState4.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[0])), 4, 3, 8, boundingBox);
			this.addBlock(serverWorldAccess, blockState4.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[1])), 5, 3, 8, boundingBox);
			this.addBlock(serverWorldAccess, blockState4.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[2])), 6, 3, 8, boundingBox);
			this.addBlock(serverWorldAccess, blockState5.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[3])), 4, 3, 12, boundingBox);
			this.addBlock(serverWorldAccess, blockState5.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[4])), 5, 3, 12, boundingBox);
			this.addBlock(serverWorldAccess, blockState5.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[5])), 6, 3, 12, boundingBox);
			this.addBlock(serverWorldAccess, blockState6.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[6])), 3, 3, 9, boundingBox);
			this.addBlock(serverWorldAccess, blockState6.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[7])), 3, 3, 10, boundingBox);
			this.addBlock(serverWorldAccess, blockState6.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[8])), 3, 3, 11, boundingBox);
			this.addBlock(serverWorldAccess, blockState7.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[9])), 7, 3, 9, boundingBox);
			this.addBlock(serverWorldAccess, blockState7.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[10])), 7, 3, 10, boundingBox);
			this.addBlock(serverWorldAccess, blockState7.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[11])), 7, 3, 11, boundingBox);
			if (bl) {
				BlockState blockState8 = Blocks.END_PORTAL.getDefaultState();
				this.addBlock(serverWorldAccess, blockState8, 4, 3, 9, boundingBox);
				this.addBlock(serverWorldAccess, blockState8, 5, 3, 9, boundingBox);
				this.addBlock(serverWorldAccess, blockState8, 6, 3, 9, boundingBox);
				this.addBlock(serverWorldAccess, blockState8, 4, 3, 10, boundingBox);
				this.addBlock(serverWorldAccess, blockState8, 5, 3, 10, boundingBox);
				this.addBlock(serverWorldAccess, blockState8, 6, 3, 10, boundingBox);
				this.addBlock(serverWorldAccess, blockState8, 4, 3, 11, boundingBox);
				this.addBlock(serverWorldAccess, blockState8, 5, 3, 11, boundingBox);
				this.addBlock(serverWorldAccess, blockState8, 6, 3, 11, boundingBox);
			}

			if (!this.spawnerPlaced) {
				i = this.applyYTransform(3);
				BlockPos blockPos2 = new BlockPos(this.applyXTransform(5, 6), i, this.applyZTransform(5, 6));
				if (boundingBox.contains(blockPos2)) {
					this.spawnerPlaced = true;
					serverWorldAccess.setBlockState(blockPos2, Blocks.SPAWNER.getDefaultState(), 2);
					BlockEntity blockEntity = serverWorldAccess.getBlockEntity(blockPos2);
					if (blockEntity instanceof MobSpawnerBlockEntity) {
						((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(EntityType.SILVERFISH);
					}
				}
			}

			return true;
		}
	}

	public static class PrisonHall extends StrongholdGenerator.Piece {
		public PrisonHall(int i, Random random, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_PRISON_HALL, i);
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = blockBox;
		}

		public PrisonHall(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_PRISON_HALL, compoundTag);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
		}

		public static StrongholdGenerator.PrisonHall method_14864(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -1, 0, 9, 5, 11, direction);
			return method_14871(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new StrongholdGenerator.PrisonHall(l, random, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			ServerWorldAccess serverWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(serverWorldAccess, boundingBox, 0, 0, 0, 8, 4, 10, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 1, 1, 0);
			this.fillWithOutline(serverWorldAccess, boundingBox, 1, 1, 10, 3, 3, 10, AIR, AIR, false);
			this.fillWithOutline(serverWorldAccess, boundingBox, 4, 1, 1, 4, 3, 1, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(serverWorldAccess, boundingBox, 4, 1, 3, 4, 3, 3, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(serverWorldAccess, boundingBox, 4, 1, 7, 4, 3, 7, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.fillWithOutline(serverWorldAccess, boundingBox, 4, 1, 9, 4, 3, 9, false, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);

			for (int i = 1; i <= 3; i++) {
				this.addBlock(
					serverWorldAccess,
					Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true)),
					4,
					i,
					4,
					boundingBox
				);
				this.addBlock(
					serverWorldAccess,
					Blocks.IRON_BARS
						.getDefaultState()
						.with(PaneBlock.NORTH, Boolean.valueOf(true))
						.with(PaneBlock.SOUTH, Boolean.valueOf(true))
						.with(PaneBlock.EAST, Boolean.valueOf(true)),
					4,
					i,
					5,
					boundingBox
				);
				this.addBlock(
					serverWorldAccess,
					Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true)),
					4,
					i,
					6,
					boundingBox
				);
				this.addBlock(
					serverWorldAccess,
					Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)).with(PaneBlock.EAST, Boolean.valueOf(true)),
					5,
					i,
					5,
					boundingBox
				);
				this.addBlock(
					serverWorldAccess,
					Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)).with(PaneBlock.EAST, Boolean.valueOf(true)),
					6,
					i,
					5,
					boundingBox
				);
				this.addBlock(
					serverWorldAccess,
					Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)).with(PaneBlock.EAST, Boolean.valueOf(true)),
					7,
					i,
					5,
					boundingBox
				);
			}

			this.addBlock(
				serverWorldAccess,
				Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true)),
				4,
				3,
				2,
				boundingBox
			);
			this.addBlock(
				serverWorldAccess,
				Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true)),
				4,
				3,
				8,
				boundingBox
			);
			BlockState blockState = Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.WEST);
			BlockState blockState2 = Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.WEST).with(DoorBlock.HALF, DoubleBlockHalf.UPPER);
			this.addBlock(serverWorldAccess, blockState, 4, 1, 2, boundingBox);
			this.addBlock(serverWorldAccess, blockState2, 4, 2, 2, boundingBox);
			this.addBlock(serverWorldAccess, blockState, 4, 1, 8, boundingBox);
			this.addBlock(serverWorldAccess, blockState2, 4, 2, 8, boundingBox);
			return true;
		}
	}

	public static class RightTurn extends StrongholdGenerator.Turn {
		public RightTurn(int i, Random random, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_RIGHT_TURN, i);
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = blockBox;
		}

		public RightTurn(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_RIGHT_TURN, compoundTag);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			Direction direction = this.getFacing();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.method_14870((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
			} else {
				this.method_14873((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
			}
		}

		public static StrongholdGenerator.RightTurn method_16652(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, 5, direction);
			return method_14871(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new StrongholdGenerator.RightTurn(l, random, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			ServerWorldAccess serverWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(serverWorldAccess, boundingBox, 0, 0, 0, 4, 4, 4, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 1, 1, 0);
			Direction direction = this.getFacing();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.fillWithOutline(serverWorldAccess, boundingBox, 0, 1, 1, 0, 3, 3, AIR, AIR, false);
			} else {
				this.fillWithOutline(serverWorldAccess, boundingBox, 4, 1, 1, 4, 3, 3, AIR, AIR, false);
			}

			return true;
		}
	}

	public static class SmallCorridor extends StrongholdGenerator.Piece {
		private final int length;

		public SmallCorridor(int i, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_SMALL_CORRIDOR, i);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
			this.length = direction != Direction.NORTH && direction != Direction.SOUTH ? blockBox.getBlockCountX() : blockBox.getBlockCountZ();
		}

		public SmallCorridor(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_SMALL_CORRIDOR, compoundTag);
			this.length = compoundTag.getInt("Steps");
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putInt("Steps", this.length);
		}

		public static BlockBox method_14857(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction) {
			int l = 3;
			BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, 4, direction);
			StructurePiece structurePiece = StructurePiece.getOverlappingPiece(list, blockBox);
			if (structurePiece == null) {
				return null;
			} else {
				if (structurePiece.getBoundingBox().minY == blockBox.minY) {
					for (int m = 3; m >= 1; m--) {
						blockBox = BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, m - 1, direction);
						if (!structurePiece.getBoundingBox().intersects(blockBox)) {
							return BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, m, direction);
						}
					}
				}

				return null;
			}
		}

		@Override
		public boolean generate(
			ServerWorldAccess serverWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			for (int i = 0; i < this.length; i++) {
				this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 0, 0, i, boundingBox);
				this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 1, 0, i, boundingBox);
				this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 2, 0, i, boundingBox);
				this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 3, 0, i, boundingBox);
				this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 4, 0, i, boundingBox);

				for (int j = 1; j <= 3; j++) {
					this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 0, j, i, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.CAVE_AIR.getDefaultState(), 1, j, i, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.CAVE_AIR.getDefaultState(), 2, j, i, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.CAVE_AIR.getDefaultState(), 3, j, i, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 4, j, i, boundingBox);
				}

				this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 0, 4, i, boundingBox);
				this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 1, 4, i, boundingBox);
				this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 2, 4, i, boundingBox);
				this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 3, 4, i, boundingBox);
				this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 4, 4, i, boundingBox);
			}

			return true;
		}
	}

	public static class SpiralStaircase extends StrongholdGenerator.Piece {
		private final boolean isStructureStart;

		public SpiralStaircase(StructurePieceType structurePieceType, int i, Random random, int j, int k) {
			super(structurePieceType, i);
			this.isStructureStart = true;
			this.setOrientation(Direction.Type.HORIZONTAL.random(random));
			this.entryDoor = StrongholdGenerator.Piece.EntranceType.OPENING;
			if (this.getFacing().getAxis() == Direction.Axis.Z) {
				this.boundingBox = new BlockBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
			} else {
				this.boundingBox = new BlockBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
			}
		}

		public SpiralStaircase(int i, Random random, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_SPIRAL_STAIRCASE, i);
			this.isStructureStart = false;
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = blockBox;
		}

		public SpiralStaircase(StructurePieceType structurePieceType, CompoundTag compoundTag) {
			super(structurePieceType, compoundTag);
			this.isStructureStart = compoundTag.getBoolean("Source");
		}

		public SpiralStaircase(StructureManager structureManager, CompoundTag compoundTag) {
			this(StructurePieceType.STRONGHOLD_SPIRAL_STAIRCASE, compoundTag);
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putBoolean("Source", this.isStructureStart);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			if (this.isStructureStart) {
				StrongholdGenerator.activePieceType = StrongholdGenerator.FiveWayCrossing.class;
			}

			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
		}

		public static StrongholdGenerator.SpiralStaircase method_14866(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -7, 0, 5, 11, 5, direction);
			return method_14871(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new StrongholdGenerator.SpiralStaircase(l, random, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			ServerWorldAccess serverWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(serverWorldAccess, boundingBox, 0, 0, 0, 4, 10, 4, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 1, 7, 0);
			this.generateEntrance(serverWorldAccess, random, boundingBox, StrongholdGenerator.Piece.EntranceType.OPENING, 1, 1, 4);
			this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 2, 6, 1, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 1, 5, 1, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 6, 1, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 1, 5, 2, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 1, 4, 3, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 5, 3, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 2, 4, 3, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 3, 3, 3, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 3, 4, 3, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 3, 3, 2, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 3, 2, 1, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 3, 3, 1, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 2, 2, 1, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 1, 1, 1, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 2, 1, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 1, 1, 2, boundingBox);
			this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 1, 3, boundingBox);
			return true;
		}
	}

	public static class SquareRoom extends StrongholdGenerator.Piece {
		protected final int roomType;

		public SquareRoom(int i, Random random, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_SQUARE_ROOM, i);
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = blockBox;
			this.roomType = random.nextInt(5);
		}

		public SquareRoom(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_SQUARE_ROOM, compoundTag);
			this.roomType = compoundTag.getInt("Type");
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putInt("Type", this.roomType);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 4, 1);
			this.method_14870((StrongholdGenerator.Start)structurePiece, list, random, 1, 4);
			this.method_14873((StrongholdGenerator.Start)structurePiece, list, random, 1, 4);
		}

		public static StrongholdGenerator.SquareRoom method_14865(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -4, -1, 0, 11, 7, 11, direction);
			return method_14871(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new StrongholdGenerator.SquareRoom(l, random, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			ServerWorldAccess serverWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(serverWorldAccess, boundingBox, 0, 0, 0, 10, 6, 10, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 4, 1, 0);
			this.fillWithOutline(serverWorldAccess, boundingBox, 4, 1, 10, 6, 3, 10, AIR, AIR, false);
			this.fillWithOutline(serverWorldAccess, boundingBox, 0, 1, 4, 0, 3, 6, AIR, AIR, false);
			this.fillWithOutline(serverWorldAccess, boundingBox, 10, 1, 4, 10, 3, 6, AIR, AIR, false);
			switch (this.roomType) {
				case 0:
					this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 5, 1, 5, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 5, 2, 5, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 5, 3, 5, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST), 4, 3, 5, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST), 6, 3, 5, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH), 5, 3, 4, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.NORTH), 5, 3, 6, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 4, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 5, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 6, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 4, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 5, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 6, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 5, 1, 4, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 5, 1, 6, boundingBox);
					break;
				case 1:
					for (int i = 0; i < 5; i++) {
						this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 3, 1, 3 + i, boundingBox);
						this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 7, 1, 3 + i, boundingBox);
						this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 3 + i, 1, 3, boundingBox);
						this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 3 + i, 1, 7, boundingBox);
					}

					this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 5, 1, 5, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 5, 2, 5, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 5, 3, 5, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.WATER.getDefaultState(), 5, 4, 5, boundingBox);
					break;
				case 2:
					for (int i = 1; i <= 9; i++) {
						this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 1, 3, i, boundingBox);
						this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 9, 3, i, boundingBox);
					}

					for (int i = 1; i <= 9; i++) {
						this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), i, 3, 1, boundingBox);
						this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), i, 3, 9, boundingBox);
					}

					this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 4, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 6, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 4, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 6, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 4, 1, 5, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 6, 1, 5, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 4, 3, 5, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 6, 3, 5, boundingBox);

					for (int i = 1; i <= 3; i++) {
						this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 4, i, 4, boundingBox);
						this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 6, i, 4, boundingBox);
						this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 4, i, 6, boundingBox);
						this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 6, i, 6, boundingBox);
					}

					this.addBlock(serverWorldAccess, Blocks.TORCH.getDefaultState(), 5, 3, 5, boundingBox);

					for (int i = 2; i <= 8; i++) {
						this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 2, 3, i, boundingBox);
						this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 3, 3, i, boundingBox);
						if (i <= 3 || i >= 7) {
							this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 4, 3, i, boundingBox);
							this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 5, 3, i, boundingBox);
							this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 6, 3, i, boundingBox);
						}

						this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 7, 3, i, boundingBox);
						this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 8, 3, i, boundingBox);
					}

					BlockState blockState = Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, Direction.WEST);
					this.addBlock(serverWorldAccess, blockState, 9, 1, 3, boundingBox);
					this.addBlock(serverWorldAccess, blockState, 9, 2, 3, boundingBox);
					this.addBlock(serverWorldAccess, blockState, 9, 3, 3, boundingBox);
					this.addChest(serverWorldAccess, boundingBox, random, 3, 4, 8, LootTables.STRONGHOLD_CROSSING_CHEST);
			}

			return true;
		}
	}

	public static class Stairs extends StrongholdGenerator.Piece {
		public Stairs(int i, Random random, BlockBox blockBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_STAIRS, i);
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = blockBox;
		}

		public Stairs(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_STAIRS, compoundTag);
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
		}

		public static StrongholdGenerator.Stairs method_14868(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -7, 0, 5, 11, 8, direction);
			return method_14871(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null
				? new StrongholdGenerator.Stairs(l, random, blockBox, direction)
				: null;
		}

		@Override
		public boolean generate(
			ServerWorldAccess serverWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			this.fillWithOutline(serverWorldAccess, boundingBox, 0, 0, 0, 4, 10, 7, true, random, StrongholdGenerator.STONE_BRICK_RANDOMIZER);
			this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 1, 7, 0);
			this.generateEntrance(serverWorldAccess, random, boundingBox, StrongholdGenerator.Piece.EntranceType.OPENING, 1, 1, 7);
			BlockState blockState = Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);

			for (int i = 0; i < 6; i++) {
				this.addBlock(serverWorldAccess, blockState, 1, 6 - i, 1 + i, boundingBox);
				this.addBlock(serverWorldAccess, blockState, 2, 6 - i, 1 + i, boundingBox);
				this.addBlock(serverWorldAccess, blockState, 3, 6 - i, 1 + i, boundingBox);
				if (i < 5) {
					this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 1, 5 - i, 1 + i, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 2, 5 - i, 1 + i, boundingBox);
					this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 3, 5 - i, 1 + i, boundingBox);
				}
			}

			return true;
		}
	}

	public static class Start extends StrongholdGenerator.SpiralStaircase {
		public StrongholdGenerator.PieceSetting field_15284;
		@Nullable
		public StrongholdGenerator.PortalRoom field_15283;
		public final List<StructurePiece> field_15282 = Lists.<StructurePiece>newArrayList();

		public Start(Random random, int i, int j) {
			super(StructurePieceType.STRONGHOLD_START, 0, random, i, j);
		}

		public Start(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_START, compoundTag);
		}
	}

	static class StoneBrickRandomizer extends StructurePiece.BlockRandomizer {
		private StoneBrickRandomizer() {
		}

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
		protected Turn(StructurePieceType structurePieceType, int i) {
			super(structurePieceType, i);
		}

		public Turn(StructurePieceType structurePieceType, CompoundTag compoundTag) {
			super(structurePieceType, compoundTag);
		}
	}
}
