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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.loot.LootTables;

public class StrongholdGenerator {
	private static final StrongholdGenerator.class_3427[] field_15265 = new StrongholdGenerator.class_3427[]{
		new StrongholdGenerator.class_3427(StrongholdGenerator.Corridor.class, 40, 0),
		new StrongholdGenerator.class_3427(StrongholdGenerator.PrisonHall.class, 5, 5),
		new StrongholdGenerator.class_3427(StrongholdGenerator.LeftTurn.class, 20, 0),
		new StrongholdGenerator.class_3427(StrongholdGenerator.RightTurn.class, 20, 0),
		new StrongholdGenerator.class_3427(StrongholdGenerator.SquareRoom.class, 10, 6),
		new StrongholdGenerator.class_3427(StrongholdGenerator.Stairs.class, 5, 5),
		new StrongholdGenerator.class_3427(StrongholdGenerator.SpiralStaircase.class, 5, 5),
		new StrongholdGenerator.class_3427(StrongholdGenerator.FiveWayCrossing.class, 5, 4),
		new StrongholdGenerator.class_3427(StrongholdGenerator.ChestCorridor.class, 5, 4),
		new StrongholdGenerator.class_3427(StrongholdGenerator.Library.class, 10, 2) {
			@Override
			public boolean method_14862(int i) {
				return super.method_14862(i) && i > 4;
			}
		},
		new StrongholdGenerator.class_3427(StrongholdGenerator.PortalRoom.class, 20, 1) {
			@Override
			public boolean method_14862(int i) {
				return super.method_14862(i) && i > 5;
			}
		}
	};
	private static List<StrongholdGenerator.class_3427> field_15267;
	private static Class<? extends StrongholdGenerator.Piece> field_15266;
	private static int field_15264;
	private static final StrongholdGenerator.StoneBrickRandomizer field_15263 = new StrongholdGenerator.StoneBrickRandomizer();

	public static void method_14855() {
		field_15267 = Lists.<StrongholdGenerator.class_3427>newArrayList();

		for (StrongholdGenerator.class_3427 lv : field_15265) {
			lv.field_15277 = 0;
			field_15267.add(lv);
		}

		field_15266 = null;
	}

	private static boolean method_14852() {
		boolean bl = false;
		field_15264 = 0;

		for (StrongholdGenerator.class_3427 lv : field_15267) {
			if (lv.field_15275 > 0 && lv.field_15277 < lv.field_15275) {
				bl = true;
			}

			field_15264 = field_15264 + lv.field_15278;
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
			if (field_15266 != null) {
				StrongholdGenerator.Piece piece = method_14847(field_15266, list, random, i, j, k, direction, l);
				field_15266 = null;
				if (piece != null) {
					return piece;
				}
			}

			int m = 0;

			while (m < 5) {
				m++;
				int n = random.nextInt(field_15264);

				for (StrongholdGenerator.class_3427 lv : field_15267) {
					n -= lv.field_15278;
					if (n < 0) {
						if (!lv.method_14862(l) || lv == start.field_15284) {
							break;
						}

						StrongholdGenerator.Piece piece2 = method_14847(lv.field_15276, list, random, i, j, k, direction, l);
						if (piece2 != null) {
							lv.field_15277++;
							start.field_15284 = lv;
							if (!lv.method_14861()) {
								field_15267.remove(lv);
							}

							return piece2;
						}
					}
				}
			}

			MutableIntBoundingBox mutableIntBoundingBox = StrongholdGenerator.SmallCorridor.method_14857(list, random, i, j, k, direction);
			return mutableIntBoundingBox != null && mutableIntBoundingBox.minY > 1 ? new StrongholdGenerator.SmallCorridor(l, mutableIntBoundingBox, direction) : null;
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

		public ChestCorridor(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, i);
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = mutableIntBoundingBox;
		}

		public ChestCorridor(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, compoundTag);
			this.chestGenerated = compoundTag.getBoolean("Chest");
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putBoolean("Chest", this.chestGenerated);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
		}

		public static StrongholdGenerator.ChestCorridor method_14856(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -1, 0, 5, 5, 7, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.ChestCorridor(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 4, 6, true, random, StrongholdGenerator.field_15263);
			this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 1, 0);
			this.generateEntrance(iWorld, random, mutableIntBoundingBox, StrongholdGenerator.Piece.EntranceType.field_15288, 1, 1, 6);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 1, 2, 3, 1, 4, Blocks.field_10056.getDefaultState(), Blocks.field_10056.getDefaultState(), false);
			this.addBlock(iWorld, Blocks.field_10131.getDefaultState(), 3, 1, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10131.getDefaultState(), 3, 1, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10131.getDefaultState(), 3, 2, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10131.getDefaultState(), 3, 2, 4, mutableIntBoundingBox);

			for (int i = 2; i <= 4; i++) {
				this.addBlock(iWorld, Blocks.field_10131.getDefaultState(), 2, 1, i, mutableIntBoundingBox);
			}

			if (!this.chestGenerated && mutableIntBoundingBox.contains(new BlockPos(this.applyXTransform(3, 3), this.applyYTransform(2), this.applyZTransform(3, 3)))) {
				this.chestGenerated = true;
				this.addChest(iWorld, mutableIntBoundingBox, random, 3, 2, 3, LootTables.CHEST_STRONGHOLD_CORRIDOR);
			}

			return true;
		}
	}

	public static class Corridor extends StrongholdGenerator.Piece {
		private final boolean leftExitExists;
		private final boolean rightExitExixts;

		public Corridor(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_CORRIDOR, i);
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = mutableIntBoundingBox;
			this.leftExitExists = random.nextInt(2) == 0;
			this.rightExitExixts = random.nextInt(2) == 0;
		}

		public Corridor(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_CORRIDOR, compoundTag);
			this.leftExitExists = compoundTag.getBoolean("Left");
			this.rightExitExixts = compoundTag.getBoolean("Right");
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putBoolean("Left", this.leftExitExists);
			compoundTag.putBoolean("Right", this.rightExitExixts);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
			if (this.leftExitExists) {
				this.method_14870((StrongholdGenerator.Start)structurePiece, list, random, 1, 2);
			}

			if (this.rightExitExixts) {
				this.method_14873((StrongholdGenerator.Start)structurePiece, list, random, 1, 2);
			}
		}

		public static StrongholdGenerator.Corridor method_14867(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -1, 0, 5, 5, 7, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.Corridor(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 4, 6, true, random, StrongholdGenerator.field_15263);
			this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 1, 0);
			this.generateEntrance(iWorld, random, mutableIntBoundingBox, StrongholdGenerator.Piece.EntranceType.field_15288, 1, 1, 6);
			BlockState blockState = Blocks.field_10099.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST);
			BlockState blockState2 = Blocks.field_10099.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST);
			this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, 0.1F, 1, 2, 1, blockState);
			this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, 0.1F, 3, 2, 1, blockState2);
			this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, 0.1F, 1, 2, 5, blockState);
			this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, 0.1F, 3, 2, 5, blockState2);
			if (this.leftExitExists) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 2, 0, 3, 4, AIR, AIR, false);
			}

			if (this.rightExitExixts) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 2, 4, 3, 4, AIR, AIR, false);
			}

			return true;
		}
	}

	public static class FiveWayCrossing extends StrongholdGenerator.Piece {
		private final boolean lowerLeftExists;
		private final boolean upperLeftExists;
		private final boolean lowerRightExists;
		private final boolean upperRightExists;

		public FiveWayCrossing(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_FIVE_WAY_CROSSING, i);
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = mutableIntBoundingBox;
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
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putBoolean("leftLow", this.lowerLeftExists);
			compoundTag.putBoolean("leftHigh", this.upperLeftExists);
			compoundTag.putBoolean("rightLow", this.lowerRightExists);
			compoundTag.putBoolean("rightHigh", this.upperRightExists);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
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
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -4, -3, 0, 10, 9, 11, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.FiveWayCrossing(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 9, 8, 10, true, random, StrongholdGenerator.field_15263);
			this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 4, 3, 0);
			if (this.lowerLeftExists) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, 1, 0, 5, 3, AIR, AIR, false);
			}

			if (this.lowerRightExists) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 3, 1, 9, 5, 3, AIR, AIR, false);
			}

			if (this.upperLeftExists) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 7, 0, 7, 9, AIR, AIR, false);
			}

			if (this.upperRightExists) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 5, 7, 9, 7, 9, AIR, AIR, false);
			}

			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 1, 10, 7, 3, 10, AIR, AIR, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 2, 1, 8, 2, 6, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 5, 4, 4, 9, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 1, 5, 8, 4, 9, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 4, 7, 3, 4, 9, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 3, 5, 3, 3, 6, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 3, 4, 3, 3, 4, Blocks.field_10136.getDefaultState(), Blocks.field_10136.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 4, 6, 3, 4, 6, Blocks.field_10136.getDefaultState(), Blocks.field_10136.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 1, 7, 7, 1, 8, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 1, 9, 7, 1, 9, Blocks.field_10136.getDefaultState(), Blocks.field_10136.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 2, 7, 7, 2, 7, Blocks.field_10136.getDefaultState(), Blocks.field_10136.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 5, 7, 4, 5, 9, Blocks.field_10136.getDefaultState(), Blocks.field_10136.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 5, 7, 8, 5, 9, Blocks.field_10136.getDefaultState(), Blocks.field_10136.getDefaultState(), false);
			this.fillWithOutline(
				iWorld,
				mutableIntBoundingBox,
				5,
				5,
				7,
				7,
				5,
				9,
				Blocks.field_10136.getDefaultState().with(SlabBlock.TYPE, SlabType.field_12682),
				Blocks.field_10136.getDefaultState().with(SlabBlock.TYPE, SlabType.field_12682),
				false
			);
			this.addBlock(iWorld, Blocks.field_10099.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH), 6, 5, 6, mutableIntBoundingBox);
			return true;
		}
	}

	public static class LeftTurn extends StrongholdGenerator.class_3466 {
		public LeftTurn(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_LEFT_TURN, i);
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = mutableIntBoundingBox;
		}

		public LeftTurn(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_LEFT_TURN, compoundTag);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			Direction direction = this.getFacing();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.method_14873((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
			} else {
				this.method_14870((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
			}
		}

		public static StrongholdGenerator.LeftTurn method_14859(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -1, 0, 5, 5, 5, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.LeftTurn(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 4, 4, true, random, StrongholdGenerator.field_15263);
			this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 1, 0);
			Direction direction = this.getFacing();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 1, 4, 3, 3, AIR, AIR, false);
			} else {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 1, 0, 3, 3, AIR, AIR, false);
			}

			return true;
		}
	}

	public static class Library extends StrongholdGenerator.Piece {
		private final boolean tall;

		public Library(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.STRONGJOLD_LIBRARY, i);
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = mutableIntBoundingBox;
			this.tall = mutableIntBoundingBox.getBlockCountY() > 6;
		}

		public Library(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGJOLD_LIBRARY, compoundTag);
			this.tall = compoundTag.getBoolean("Tall");
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putBoolean("Tall", this.tall);
		}

		public static StrongholdGenerator.Library method_14860(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -4, -1, 0, 14, 11, 15, direction);
			if (!method_14871(mutableIntBoundingBox) || StructurePiece.method_14932(list, mutableIntBoundingBox) != null) {
				mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -4, -1, 0, 14, 6, 15, direction);
				if (!method_14871(mutableIntBoundingBox) || StructurePiece.method_14932(list, mutableIntBoundingBox) != null) {
					return null;
				}
			}

			return new StrongholdGenerator.Library(l, random, mutableIntBoundingBox, direction);
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			int i = 11;
			if (!this.tall) {
				i = 6;
			}

			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 13, i - 1, 14, true, random, StrongholdGenerator.field_15263);
			this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 4, 1, 0);
			this.fillWithOutlineUnderSealevel(
				iWorld, mutableIntBoundingBox, random, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.field_10343.getDefaultState(), Blocks.field_10343.getDefaultState(), false, false
			);
			int j = 1;
			int k = 12;

			for (int l = 1; l <= 13; l++) {
				if ((l - 1) % 4 == 0) {
					this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, l, 1, 4, l, Blocks.field_10161.getDefaultState(), Blocks.field_10161.getDefaultState(), false);
					this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 1, l, 12, 4, l, Blocks.field_10161.getDefaultState(), Blocks.field_10161.getDefaultState(), false);
					this.addBlock(iWorld, Blocks.field_10099.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST), 2, 3, l, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10099.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST), 11, 3, l, mutableIntBoundingBox);
					if (this.tall) {
						this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 6, l, 1, 9, l, Blocks.field_10161.getDefaultState(), Blocks.field_10161.getDefaultState(), false);
						this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 6, l, 12, 9, l, Blocks.field_10161.getDefaultState(), Blocks.field_10161.getDefaultState(), false);
					}
				} else {
					this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, l, 1, 4, l, Blocks.field_10504.getDefaultState(), Blocks.field_10504.getDefaultState(), false);
					this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 1, l, 12, 4, l, Blocks.field_10504.getDefaultState(), Blocks.field_10504.getDefaultState(), false);
					if (this.tall) {
						this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 6, l, 1, 9, l, Blocks.field_10504.getDefaultState(), Blocks.field_10504.getDefaultState(), false);
						this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 6, l, 12, 9, l, Blocks.field_10504.getDefaultState(), Blocks.field_10504.getDefaultState(), false);
					}
				}
			}

			for (int lx = 3; lx < 12; lx += 2) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 1, lx, 4, 3, lx, Blocks.field_10504.getDefaultState(), Blocks.field_10504.getDefaultState(), false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 1, lx, 7, 3, lx, Blocks.field_10504.getDefaultState(), Blocks.field_10504.getDefaultState(), false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 1, lx, 10, 3, lx, Blocks.field_10504.getDefaultState(), Blocks.field_10504.getDefaultState(), false);
			}

			if (this.tall) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 5, 1, 3, 5, 13, Blocks.field_10161.getDefaultState(), Blocks.field_10161.getDefaultState(), false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 10, 5, 1, 12, 5, 13, Blocks.field_10161.getDefaultState(), Blocks.field_10161.getDefaultState(), false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 5, 1, 9, 5, 2, Blocks.field_10161.getDefaultState(), Blocks.field_10161.getDefaultState(), false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 5, 12, 9, 5, 13, Blocks.field_10161.getDefaultState(), Blocks.field_10161.getDefaultState(), false);
				this.addBlock(iWorld, Blocks.field_10161.getDefaultState(), 9, 5, 11, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10161.getDefaultState(), 8, 5, 11, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10161.getDefaultState(), 9, 5, 10, mutableIntBoundingBox);
				BlockState blockState = Blocks.field_10620.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true));
				BlockState blockState2 = Blocks.field_10620.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true));
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 6, 3, 3, 6, 11, blockState2, blockState2, false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 10, 6, 3, 10, 6, 9, blockState2, blockState2, false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 6, 2, 9, 6, 2, blockState, blockState, false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 6, 12, 7, 6, 12, blockState, blockState, false);
				this.addBlock(
					iWorld,
					Blocks.field_10620.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
					3,
					6,
					2,
					mutableIntBoundingBox
				);
				this.addBlock(
					iWorld,
					Blocks.field_10620.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
					3,
					6,
					12,
					mutableIntBoundingBox
				);
				this.addBlock(
					iWorld,
					Blocks.field_10620.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
					10,
					6,
					2,
					mutableIntBoundingBox
				);

				for (int m = 0; m <= 2; m++) {
					this.addBlock(
						iWorld,
						Blocks.field_10620.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
						8 + m,
						6,
						12 - m,
						mutableIntBoundingBox
					);
					if (m != 2) {
						this.addBlock(
							iWorld,
							Blocks.field_10620.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
							8 + m,
							6,
							11 - m,
							mutableIntBoundingBox
						);
					}
				}

				BlockState blockState3 = Blocks.field_9983.getDefaultState().with(LadderBlock.FACING, Direction.SOUTH);
				this.addBlock(iWorld, blockState3, 10, 1, 13, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState3, 10, 2, 13, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState3, 10, 3, 13, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState3, 10, 4, 13, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState3, 10, 5, 13, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState3, 10, 6, 13, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState3, 10, 7, 13, mutableIntBoundingBox);
				int n = 7;
				int o = 7;
				BlockState blockState4 = Blocks.field_10620.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true));
				this.addBlock(iWorld, blockState4, 6, 9, 7, mutableIntBoundingBox);
				BlockState blockState5 = Blocks.field_10620.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true));
				this.addBlock(iWorld, blockState5, 7, 9, 7, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState4, 6, 8, 7, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState5, 7, 8, 7, mutableIntBoundingBox);
				BlockState blockState6 = blockState2.with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true));
				this.addBlock(iWorld, blockState6, 6, 7, 7, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState6, 7, 7, 7, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState4, 5, 7, 7, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState5, 8, 7, 7, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState4.with(FenceBlock.NORTH, Boolean.valueOf(true)), 6, 7, 6, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState4.with(FenceBlock.SOUTH, Boolean.valueOf(true)), 6, 7, 8, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState5.with(FenceBlock.NORTH, Boolean.valueOf(true)), 7, 7, 6, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState5.with(FenceBlock.SOUTH, Boolean.valueOf(true)), 7, 7, 8, mutableIntBoundingBox);
				BlockState blockState7 = Blocks.field_10336.getDefaultState();
				this.addBlock(iWorld, blockState7, 5, 8, 7, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState7, 8, 8, 7, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState7, 6, 8, 6, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState7, 6, 8, 8, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState7, 7, 8, 6, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState7, 7, 8, 8, mutableIntBoundingBox);
			}

			this.addChest(iWorld, mutableIntBoundingBox, random, 3, 3, 5, LootTables.CHEST_STRONGHOLD_LIBRARY);
			if (this.tall) {
				this.addBlock(iWorld, AIR, 12, 9, 1, mutableIntBoundingBox);
				this.addChest(iWorld, mutableIntBoundingBox, random, 12, 8, 1, LootTables.CHEST_STRONGHOLD_LIBRARY);
			}

			return true;
		}
	}

	abstract static class Piece extends StructurePiece {
		protected StrongholdGenerator.Piece.EntranceType entryDoor = StrongholdGenerator.Piece.EntranceType.field_15288;

		protected Piece(StructurePieceType structurePieceType, int i) {
			super(structurePieceType, i);
		}

		public Piece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
			super(structurePieceType, compoundTag);
			this.entryDoor = StrongholdGenerator.Piece.EntranceType.valueOf(compoundTag.getString("EntryDoor"));
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			compoundTag.putString("EntryDoor", this.entryDoor.name());
		}

		protected void generateEntrance(
			IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, StrongholdGenerator.Piece.EntranceType entranceType, int i, int j, int k
		) {
			switch (entranceType) {
				case field_15288:
					this.fillWithOutline(iWorld, mutableIntBoundingBox, i, j, k, i + 3 - 1, j + 3 - 1, k, AIR, AIR, false);
					break;
				case field_15290:
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), i, j, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), i, j + 1, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), i, j + 2, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), i + 1, j + 2, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), i + 2, j + 2, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), i + 2, j + 1, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), i + 2, j, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10149.getDefaultState(), i + 1, j, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10149.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.field_12609), i + 1, j + 1, k, mutableIntBoundingBox);
					break;
				case field_15289:
					this.addBlock(iWorld, Blocks.field_10543.getDefaultState(), i + 1, j, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10543.getDefaultState(), i + 1, j + 1, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10576.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)), i, j, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10576.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)), i, j + 1, k, mutableIntBoundingBox);
					this.addBlock(
						iWorld,
						Blocks.field_10576.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
						i,
						j + 2,
						k,
						mutableIntBoundingBox
					);
					this.addBlock(
						iWorld,
						Blocks.field_10576.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
						i + 1,
						j + 2,
						k,
						mutableIntBoundingBox
					);
					this.addBlock(
						iWorld,
						Blocks.field_10576.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)).with(PaneBlock.WEST, Boolean.valueOf(true)),
						i + 2,
						j + 2,
						k,
						mutableIntBoundingBox
					);
					this.addBlock(iWorld, Blocks.field_10576.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)), i + 2, j + 1, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10576.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)), i + 2, j, k, mutableIntBoundingBox);
					break;
				case field_15291:
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), i, j, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), i, j + 1, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), i, j + 2, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), i + 1, j + 2, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), i + 2, j + 2, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), i + 2, j + 1, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), i + 2, j, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_9973.getDefaultState(), i + 1, j, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_9973.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.field_12609), i + 1, j + 1, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10494.getDefaultState().with(AbstractButtonBlock.FACING, Direction.NORTH), i + 2, j + 1, k + 1, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10494.getDefaultState().with(AbstractButtonBlock.FACING, Direction.SOUTH), i + 2, j + 1, k - 1, mutableIntBoundingBox);
			}
		}

		protected StrongholdGenerator.Piece.EntranceType getRandomEntrance(Random random) {
			int i = random.nextInt(5);
			switch (i) {
				case 0:
				case 1:
				default:
					return StrongholdGenerator.Piece.EntranceType.field_15288;
				case 2:
					return StrongholdGenerator.Piece.EntranceType.field_15290;
				case 3:
					return StrongholdGenerator.Piece.EntranceType.field_15289;
				case 4:
					return StrongholdGenerator.Piece.EntranceType.field_15291;
			}
		}

		@Nullable
		protected StructurePiece method_14874(StrongholdGenerator.Start start, List<StructurePiece> list, Random random, int i, int j) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.minX + i, this.boundingBox.minY + j, this.boundingBox.minZ - 1, direction, this.method_14923()
						);
					case SOUTH:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.minX + i, this.boundingBox.minY + j, this.boundingBox.maxZ + 1, direction, this.method_14923()
						);
					case WEST:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + j, this.boundingBox.minZ + i, direction, this.method_14923()
						);
					case EAST:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + j, this.boundingBox.minZ + i, direction, this.method_14923()
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
							start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.WEST, this.method_14923()
						);
					case SOUTH:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.WEST, this.method_14923()
						);
					case WEST:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.minZ - 1, Direction.NORTH, this.method_14923()
						);
					case EAST:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.minZ - 1, Direction.NORTH, this.method_14923()
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
							start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.EAST, this.method_14923()
						);
					case SOUTH:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.EAST, this.method_14923()
						);
					case WEST:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.maxZ + 1, Direction.SOUTH, this.method_14923()
						);
					case EAST:
						return StrongholdGenerator.method_14854(
							start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.maxZ + 1, Direction.SOUTH, this.method_14923()
						);
				}
			}

			return null;
		}

		protected static boolean method_14871(MutableIntBoundingBox mutableIntBoundingBox) {
			return mutableIntBoundingBox != null && mutableIntBoundingBox.minY > 10;
		}

		public static enum EntranceType {
			field_15288,
			field_15290,
			field_15289,
			field_15291;
		}
	}

	public static class PortalRoom extends StrongholdGenerator.Piece {
		private boolean spawnerPlaced;

		public PortalRoom(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, i);
			this.setOrientation(direction);
			this.boundingBox = mutableIntBoundingBox;
		}

		public PortalRoom(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, compoundTag);
			this.spawnerPlaced = compoundTag.getBoolean("Mob");
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putBoolean("Mob", this.spawnerPlaced);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			if (structurePiece != null) {
				((StrongholdGenerator.Start)structurePiece).field_15283 = this;
			}
		}

		public static StrongholdGenerator.PortalRoom method_14863(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -4, -1, 0, 11, 8, 16, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.PortalRoom(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 10, 7, 15, false, random, StrongholdGenerator.field_15263);
			this.generateEntrance(iWorld, random, mutableIntBoundingBox, StrongholdGenerator.Piece.EntranceType.field_15289, 4, 1, 0);
			int i = 6;
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, i, 1, 1, i, 14, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, i, 1, 9, i, 14, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, i, 1, 8, i, 2, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, i, 14, 8, i, 14, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 1, 2, 1, 4, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 1, 1, 9, 1, 4, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 1, 1, 1, 3, Blocks.field_10164.getDefaultState(), Blocks.field_10164.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 1, 1, 9, 1, 3, Blocks.field_10164.getDefaultState(), Blocks.field_10164.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 1, 8, 7, 1, 12, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 9, 6, 1, 11, Blocks.field_10164.getDefaultState(), Blocks.field_10164.getDefaultState(), false);
			BlockState blockState = Blocks.field_10576.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.field_10576.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)).with(PaneBlock.EAST, Boolean.valueOf(true));

			for (int j = 3; j < 14; j += 2) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, j, 0, 4, j, blockState, blockState, false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 10, 3, j, 10, 4, j, blockState, blockState, false);
			}

			for (int j = 2; j < 9; j += 2) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, j, 3, 15, j, 4, 15, blockState2, blockState2, false);
			}

			BlockState blockState3 = Blocks.field_10392.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 5, 6, 1, 7, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 2, 6, 6, 2, 7, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 3, 7, 6, 3, 7, false, random, StrongholdGenerator.field_15263);

			for (int k = 4; k <= 6; k++) {
				this.addBlock(iWorld, blockState3, k, 1, 4, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState3, k, 2, 5, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState3, k, 3, 6, mutableIntBoundingBox);
			}

			BlockState blockState4 = Blocks.field_10398.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.NORTH);
			BlockState blockState5 = Blocks.field_10398.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.SOUTH);
			BlockState blockState6 = Blocks.field_10398.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.EAST);
			BlockState blockState7 = Blocks.field_10398.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.WEST);
			boolean bl = true;
			boolean[] bls = new boolean[12];

			for (int l = 0; l < bls.length; l++) {
				bls[l] = random.nextFloat() > 0.9F;
				bl &= bls[l];
			}

			this.addBlock(iWorld, blockState4.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[0])), 4, 3, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[1])), 5, 3, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[2])), 6, 3, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState5.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[3])), 4, 3, 12, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState5.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[4])), 5, 3, 12, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState5.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[5])), 6, 3, 12, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[6])), 3, 3, 9, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[7])), 3, 3, 10, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[8])), 3, 3, 11, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState7.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[9])), 7, 3, 9, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState7.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[10])), 7, 3, 10, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState7.with(EndPortalFrameBlock.EYE, Boolean.valueOf(bls[11])), 7, 3, 11, mutableIntBoundingBox);
			if (bl) {
				BlockState blockState8 = Blocks.field_10027.getDefaultState();
				this.addBlock(iWorld, blockState8, 4, 3, 9, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState8, 5, 3, 9, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState8, 6, 3, 9, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState8, 4, 3, 10, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState8, 5, 3, 10, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState8, 6, 3, 10, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState8, 4, 3, 11, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState8, 5, 3, 11, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState8, 6, 3, 11, mutableIntBoundingBox);
			}

			if (!this.spawnerPlaced) {
				i = this.applyYTransform(3);
				BlockPos blockPos = new BlockPos(this.applyXTransform(5, 6), i, this.applyZTransform(5, 6));
				if (mutableIntBoundingBox.contains(blockPos)) {
					this.spawnerPlaced = true;
					iWorld.setBlockState(blockPos, Blocks.field_10260.getDefaultState(), 2);
					BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
					if (blockEntity instanceof MobSpawnerBlockEntity) {
						((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(EntityType.SILVERFISH);
					}
				}
			}

			return true;
		}
	}

	public static class PrisonHall extends StrongholdGenerator.Piece {
		public PrisonHall(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_PRISON_HALL, i);
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = mutableIntBoundingBox;
		}

		public PrisonHall(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_PRISON_HALL, compoundTag);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
		}

		public static StrongholdGenerator.PrisonHall method_14864(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -1, 0, 9, 5, 11, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.PrisonHall(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 8, 4, 10, true, random, StrongholdGenerator.field_15263);
			this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 1, 0);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 10, 3, 3, 10, AIR, AIR, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 1, 4, 3, 1, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 3, 4, 3, 3, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 7, 4, 3, 7, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 9, 4, 3, 9, false, random, StrongholdGenerator.field_15263);

			for (int i = 1; i <= 3; i++) {
				this.addBlock(
					iWorld,
					Blocks.field_10576.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true)),
					4,
					i,
					4,
					mutableIntBoundingBox
				);
				this.addBlock(
					iWorld,
					Blocks.field_10576
						.getDefaultState()
						.with(PaneBlock.NORTH, Boolean.valueOf(true))
						.with(PaneBlock.SOUTH, Boolean.valueOf(true))
						.with(PaneBlock.EAST, Boolean.valueOf(true)),
					4,
					i,
					5,
					mutableIntBoundingBox
				);
				this.addBlock(
					iWorld,
					Blocks.field_10576.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true)),
					4,
					i,
					6,
					mutableIntBoundingBox
				);
				this.addBlock(
					iWorld,
					Blocks.field_10576.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)).with(PaneBlock.EAST, Boolean.valueOf(true)),
					5,
					i,
					5,
					mutableIntBoundingBox
				);
				this.addBlock(
					iWorld,
					Blocks.field_10576.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)).with(PaneBlock.EAST, Boolean.valueOf(true)),
					6,
					i,
					5,
					mutableIntBoundingBox
				);
				this.addBlock(
					iWorld,
					Blocks.field_10576.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)).with(PaneBlock.EAST, Boolean.valueOf(true)),
					7,
					i,
					5,
					mutableIntBoundingBox
				);
			}

			this.addBlock(
				iWorld,
				Blocks.field_10576.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true)),
				4,
				3,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10576.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(true)).with(PaneBlock.SOUTH, Boolean.valueOf(true)),
				4,
				3,
				8,
				mutableIntBoundingBox
			);
			BlockState blockState = Blocks.field_9973.getDefaultState().with(DoorBlock.FACING, Direction.WEST);
			BlockState blockState2 = Blocks.field_9973.getDefaultState().with(DoorBlock.FACING, Direction.WEST).with(DoorBlock.HALF, DoubleBlockHalf.field_12609);
			this.addBlock(iWorld, blockState, 4, 1, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 4, 2, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 4, 1, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 4, 2, 8, mutableIntBoundingBox);
			return true;
		}
	}

	public static class RightTurn extends StrongholdGenerator.class_3466 {
		public RightTurn(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_RIGHT_TURN, i);
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = mutableIntBoundingBox;
		}

		public RightTurn(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_RIGHT_TURN, compoundTag);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			Direction direction = this.getFacing();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.method_14870((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
			} else {
				this.method_14873((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
			}
		}

		public static StrongholdGenerator.RightTurn method_16652(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -1, 0, 5, 5, 5, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.RightTurn(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 4, 4, true, random, StrongholdGenerator.field_15263);
			this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 1, 0);
			Direction direction = this.getFacing();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 1, 0, 3, 3, AIR, AIR, false);
			} else {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 1, 4, 3, 3, AIR, AIR, false);
			}

			return true;
		}
	}

	public static class SmallCorridor extends StrongholdGenerator.Piece {
		private final int length;

		public SmallCorridor(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_SMALL_CORRIDOR, i);
			this.setOrientation(direction);
			this.boundingBox = mutableIntBoundingBox;
			this.length = direction != Direction.NORTH && direction != Direction.SOUTH ? mutableIntBoundingBox.getBlockCountX() : mutableIntBoundingBox.getBlockCountZ();
		}

		public SmallCorridor(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_SMALL_CORRIDOR, compoundTag);
			this.length = compoundTag.getInt("Steps");
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putInt("Steps", this.length);
		}

		public static MutableIntBoundingBox method_14857(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction) {
			int l = 3;
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -1, 0, 5, 5, 4, direction);
			StructurePiece structurePiece = StructurePiece.method_14932(list, mutableIntBoundingBox);
			if (structurePiece == null) {
				return null;
			} else {
				if (structurePiece.getBoundingBox().minY == mutableIntBoundingBox.minY) {
					for (int m = 3; m >= 1; m--) {
						mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -1, 0, 5, 5, m - 1, direction);
						if (!structurePiece.getBoundingBox().intersects(mutableIntBoundingBox)) {
							return MutableIntBoundingBox.createRotated(i, j, k, -1, -1, 0, 5, 5, m, direction);
						}
					}
				}

				return null;
			}
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			for (int i = 0; i < this.length; i++) {
				this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 0, 0, i, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 1, 0, i, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 2, 0, i, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 3, 0, i, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 4, 0, i, mutableIntBoundingBox);

				for (int j = 1; j <= 3; j++) {
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 0, j, i, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10543.getDefaultState(), 1, j, i, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10543.getDefaultState(), 2, j, i, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10543.getDefaultState(), 3, j, i, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 4, j, i, mutableIntBoundingBox);
				}

				this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 0, 4, i, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 1, 4, i, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 2, 4, i, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 3, 4, i, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 4, 4, i, mutableIntBoundingBox);
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
			this.entryDoor = StrongholdGenerator.Piece.EntranceType.field_15288;
			if (this.getFacing().getAxis() == Direction.Axis.Z) {
				this.boundingBox = new MutableIntBoundingBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
			} else {
				this.boundingBox = new MutableIntBoundingBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
			}
		}

		public SpiralStaircase(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_SPIRAL_STAIRCASE, i);
			this.isStructureStart = false;
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = mutableIntBoundingBox;
		}

		public SpiralStaircase(StructurePieceType structurePieceType, CompoundTag compoundTag) {
			super(structurePieceType, compoundTag);
			this.isStructureStart = compoundTag.getBoolean("Source");
		}

		public SpiralStaircase(StructureManager structureManager, CompoundTag compoundTag) {
			this(StructurePieceType.STRONGHOLD_SPIRAL_STAIRCASE, compoundTag);
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putBoolean("Source", this.isStructureStart);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			if (this.isStructureStart) {
				StrongholdGenerator.field_15266 = StrongholdGenerator.FiveWayCrossing.class;
			}

			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
		}

		public static StrongholdGenerator.SpiralStaircase method_14866(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -7, 0, 5, 11, 5, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.SpiralStaircase(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 10, 4, true, random, StrongholdGenerator.field_15263);
			this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 7, 0);
			this.generateEntrance(iWorld, random, mutableIntBoundingBox, StrongholdGenerator.Piece.EntranceType.field_15288, 1, 1, 4);
			this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 2, 6, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 1, 5, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10136.getDefaultState(), 1, 6, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 1, 5, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 1, 4, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10136.getDefaultState(), 1, 5, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 2, 4, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 3, 3, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10136.getDefaultState(), 3, 4, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 3, 3, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 3, 2, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10136.getDefaultState(), 3, 3, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 2, 2, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 1, 1, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10136.getDefaultState(), 1, 2, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 1, 1, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10136.getDefaultState(), 1, 1, 3, mutableIntBoundingBox);
			return true;
		}
	}

	public static class SquareRoom extends StrongholdGenerator.Piece {
		protected final int roomType;

		public SquareRoom(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_SQUARE_ROOM, i);
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = mutableIntBoundingBox;
			this.roomType = random.nextInt(5);
		}

		public SquareRoom(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_SQUARE_ROOM, compoundTag);
			this.roomType = compoundTag.getInt("Type");
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putInt("Type", this.roomType);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 4, 1);
			this.method_14870((StrongholdGenerator.Start)structurePiece, list, random, 1, 4);
			this.method_14873((StrongholdGenerator.Start)structurePiece, list, random, 1, 4);
		}

		public static StrongholdGenerator.SquareRoom method_14865(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -4, -1, 0, 11, 7, 11, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.SquareRoom(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 10, 6, 10, true, random, StrongholdGenerator.field_15263);
			this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 4, 1, 0);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 10, 6, 3, 10, AIR, AIR, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 4, 0, 3, 6, AIR, AIR, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 10, 1, 4, 10, 3, 6, AIR, AIR, false);
			switch (this.roomType) {
				case 0:
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 5, 1, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 5, 2, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 5, 3, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10099.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST), 4, 3, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10099.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST), 6, 3, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10099.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH), 5, 3, 4, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10099.getDefaultState().with(WallTorchBlock.FACING, Direction.NORTH), 5, 3, 6, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10136.getDefaultState(), 4, 1, 4, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10136.getDefaultState(), 4, 1, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10136.getDefaultState(), 4, 1, 6, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10136.getDefaultState(), 6, 1, 4, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10136.getDefaultState(), 6, 1, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10136.getDefaultState(), 6, 1, 6, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10136.getDefaultState(), 5, 1, 4, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10136.getDefaultState(), 5, 1, 6, mutableIntBoundingBox);
					break;
				case 1:
					for (int i = 0; i < 5; i++) {
						this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 3, 1, 3 + i, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 7, 1, 3 + i, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 3 + i, 1, 3, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 3 + i, 1, 7, mutableIntBoundingBox);
					}

					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 5, 1, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 5, 2, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 5, 3, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10382.getDefaultState(), 5, 4, 5, mutableIntBoundingBox);
					break;
				case 2:
					for (int i = 1; i <= 9; i++) {
						this.addBlock(iWorld, Blocks.field_10445.getDefaultState(), 1, 3, i, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10445.getDefaultState(), 9, 3, i, mutableIntBoundingBox);
					}

					for (int i = 1; i <= 9; i++) {
						this.addBlock(iWorld, Blocks.field_10445.getDefaultState(), i, 3, 1, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10445.getDefaultState(), i, 3, 9, mutableIntBoundingBox);
					}

					this.addBlock(iWorld, Blocks.field_10445.getDefaultState(), 5, 1, 4, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10445.getDefaultState(), 5, 1, 6, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10445.getDefaultState(), 5, 3, 4, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10445.getDefaultState(), 5, 3, 6, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10445.getDefaultState(), 4, 1, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10445.getDefaultState(), 6, 1, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10445.getDefaultState(), 4, 3, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10445.getDefaultState(), 6, 3, 5, mutableIntBoundingBox);

					for (int i = 1; i <= 3; i++) {
						this.addBlock(iWorld, Blocks.field_10445.getDefaultState(), 4, i, 4, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10445.getDefaultState(), 6, i, 4, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10445.getDefaultState(), 4, i, 6, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10445.getDefaultState(), 6, i, 6, mutableIntBoundingBox);
					}

					this.addBlock(iWorld, Blocks.field_10336.getDefaultState(), 5, 3, 5, mutableIntBoundingBox);

					for (int i = 2; i <= 8; i++) {
						this.addBlock(iWorld, Blocks.field_10161.getDefaultState(), 2, 3, i, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10161.getDefaultState(), 3, 3, i, mutableIntBoundingBox);
						if (i <= 3 || i >= 7) {
							this.addBlock(iWorld, Blocks.field_10161.getDefaultState(), 4, 3, i, mutableIntBoundingBox);
							this.addBlock(iWorld, Blocks.field_10161.getDefaultState(), 5, 3, i, mutableIntBoundingBox);
							this.addBlock(iWorld, Blocks.field_10161.getDefaultState(), 6, 3, i, mutableIntBoundingBox);
						}

						this.addBlock(iWorld, Blocks.field_10161.getDefaultState(), 7, 3, i, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10161.getDefaultState(), 8, 3, i, mutableIntBoundingBox);
					}

					BlockState blockState = Blocks.field_9983.getDefaultState().with(LadderBlock.FACING, Direction.WEST);
					this.addBlock(iWorld, blockState, 9, 1, 3, mutableIntBoundingBox);
					this.addBlock(iWorld, blockState, 9, 2, 3, mutableIntBoundingBox);
					this.addBlock(iWorld, blockState, 9, 3, 3, mutableIntBoundingBox);
					this.addChest(iWorld, mutableIntBoundingBox, random, 3, 4, 8, LootTables.CHEST_STRONGHOLD_CROSSING);
			}

			return true;
		}
	}

	public static class Stairs extends StrongholdGenerator.Piece {
		public Stairs(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_STAIRS, i);
			this.setOrientation(direction);
			this.entryDoor = this.getRandomEntrance(random);
			this.boundingBox = mutableIntBoundingBox;
		}

		public Stairs(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_STAIRS, compoundTag);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
		}

		public static StrongholdGenerator.Stairs method_14868(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -7, 0, 5, 11, 8, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.Stairs(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 10, 7, true, random, StrongholdGenerator.field_15263);
			this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 7, 0);
			this.generateEntrance(iWorld, random, mutableIntBoundingBox, StrongholdGenerator.Piece.EntranceType.field_15288, 1, 1, 7);
			BlockState blockState = Blocks.field_10596.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);

			for (int i = 0; i < 6; i++) {
				this.addBlock(iWorld, blockState, 1, 6 - i, 1 + i, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState, 2, 6 - i, 1 + i, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState, 3, 6 - i, 1 + i, mutableIntBoundingBox);
				if (i < 5) {
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 1, 5 - i, 1 + i, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 2, 5 - i, 1 + i, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.getDefaultState(), 3, 5 - i, 1 + i, mutableIntBoundingBox);
				}
			}

			return true;
		}
	}

	public static class Start extends StrongholdGenerator.SpiralStaircase {
		public StrongholdGenerator.class_3427 field_15284;
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
		public void setBlock(Random random, int i, int j, int k, boolean bl) {
			if (bl) {
				float f = random.nextFloat();
				if (f < 0.2F) {
					this.block = Blocks.field_10416.getDefaultState();
				} else if (f < 0.5F) {
					this.block = Blocks.field_10065.getDefaultState();
				} else if (f < 0.55F) {
					this.block = Blocks.field_10387.getDefaultState();
				} else {
					this.block = Blocks.field_10056.getDefaultState();
				}
			} else {
				this.block = Blocks.field_10543.getDefaultState();
			}
		}
	}

	static class class_3427 {
		public final Class<? extends StrongholdGenerator.Piece> field_15276;
		public final int field_15278;
		public int field_15277;
		public final int field_15275;

		public class_3427(Class<? extends StrongholdGenerator.Piece> class_, int i, int j) {
			this.field_15276 = class_;
			this.field_15278 = i;
			this.field_15275 = j;
		}

		public boolean method_14862(int i) {
			return this.field_15275 == 0 || this.field_15277 < this.field_15275;
		}

		public boolean method_14861() {
			return this.field_15275 == 0 || this.field_15277 < this.field_15275;
		}
	}

	public abstract static class class_3466 extends StrongholdGenerator.Piece {
		protected class_3466(StructurePieceType structurePieceType, int i) {
			super(structurePieceType, i);
		}

		public class_3466(StructurePieceType structurePieceType, CompoundTag compoundTag) {
			super(structurePieceType, compoundTag);
		}
	}
}
