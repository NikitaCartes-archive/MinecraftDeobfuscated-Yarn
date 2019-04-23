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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.loot.LootTables;

public class NetherFortressGenerator {
	private static final NetherFortressGenerator.class_3404[] field_14494 = new NetherFortressGenerator.class_3404[]{
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.Bridge.class, 30, 0, true),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.BridgeCrossing.class, 10, 4),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.BridgeSmallCrossing.class, 10, 4),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.BridgeStairs.class, 10, 3),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.BridgePlatform.class, 5, 2),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.CorridorExit.class, 5, 1)
	};
	private static final NetherFortressGenerator.class_3404[] field_14493 = new NetherFortressGenerator.class_3404[]{
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.SmallCorridor.class, 25, 0, true),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.CorridorCrossing.class, 15, 5),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.CorridorRightTurn.class, 5, 10),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.CorridorLeftTurn.class, 5, 10),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.CorridorStairs.class, 10, 3, true),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.CorridorBalcony.class, 7, 2),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.CorridorNetherWartsRoom.class, 5, 2)
	};

	private static NetherFortressGenerator.Piece generatePiece(
		NetherFortressGenerator.class_3404 arg, List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l
	) {
		Class<? extends NetherFortressGenerator.Piece> class_ = arg.field_14501;
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
		public Bridge(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE, i);
			this.setOrientation(direction);
			this.boundingBox = mutableIntBoundingBox;
		}

		public Bridge(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE, compoundTag);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14814((NetherFortressGenerator.Start)structurePiece, list, random, 1, 3, false);
		}

		public static NetherFortressGenerator.Bridge method_14798(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -3, 0, 5, 10, 19, direction);
			return method_14809(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.Bridge(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, 0, 4, 4, 18, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 5, 0, 3, 7, 18, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 0, 0, 5, 18, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 5, 0, 4, 5, 18, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 0, 4, 2, 5, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 13, 4, 2, 18, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 1, 3, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 15, 4, 1, 18, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 2; j++) {
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), i, -1, j, mutableIntBoundingBox);
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), i, -1, 18 - j, mutableIntBoundingBox);
				}
			}

			BlockState blockState = Blocks.field_10364.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true));
			BlockState blockState2 = blockState.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState3 = blockState.with(FenceBlock.WEST, Boolean.valueOf(true));
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 1, 0, 4, 1, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, 4, 0, 4, 4, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, 14, 0, 4, 14, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 17, 0, 4, 17, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 1, 4, 4, 1, blockState3, blockState3, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 3, 4, 4, 4, 4, blockState3, blockState3, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 3, 14, 4, 4, 14, blockState3, blockState3, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 17, 4, 4, 17, blockState3, blockState3, false);
			return true;
		}
	}

	public static class BridgeCrossing extends NetherFortressGenerator.Piece {
		public BridgeCrossing(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, i);
			this.setOrientation(direction);
			this.boundingBox = mutableIntBoundingBox;
		}

		protected BridgeCrossing(Random random, int i, int j) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, 0);
			this.setOrientation(Direction.Type.field_11062.random(random));
			if (this.getFacing().getAxis() == Direction.Axis.Z) {
				this.boundingBox = new MutableIntBoundingBox(i, 64, j, i + 19 - 1, 73, j + 19 - 1);
			} else {
				this.boundingBox = new MutableIntBoundingBox(i, 64, j, i + 19 - 1, 73, j + 19 - 1);
			}
		}

		protected BridgeCrossing(StructurePieceType structurePieceType, CompoundTag compoundTag) {
			super(structurePieceType, compoundTag);
		}

		public BridgeCrossing(StructureManager structureManager, CompoundTag compoundTag) {
			this(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, compoundTag);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14814((NetherFortressGenerator.Start)structurePiece, list, random, 8, 3, false);
			this.method_14812((NetherFortressGenerator.Start)structurePiece, list, random, 3, 8, false);
			this.method_14808((NetherFortressGenerator.Start)structurePiece, list, random, 3, 8, false);
		}

		public static NetherFortressGenerator.BridgeCrossing method_14796(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -8, -3, 0, 19, 10, 19, direction);
			return method_14809(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.BridgeCrossing(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 3, 0, 11, 4, 18, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, 7, 18, 4, 11, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 5, 0, 10, 7, 18, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 8, 18, 7, 10, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 5, 0, 7, 5, 7, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 5, 11, 7, 5, 18, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 11, 5, 0, 11, 5, 7, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 11, 5, 11, 11, 5, 18, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 7, 7, 5, 7, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 11, 5, 7, 18, 5, 7, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 11, 7, 5, 11, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 11, 5, 11, 18, 5, 11, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 2, 0, 11, 2, 5, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 2, 13, 11, 2, 18, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 0, 0, 11, 1, 3, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 0, 15, 11, 1, 18, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);

			for (int i = 7; i <= 11; i++) {
				for (int j = 0; j <= 2; j++) {
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), i, -1, j, mutableIntBoundingBox);
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), i, -1, 18 - j, mutableIntBoundingBox);
				}
			}

			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 7, 5, 2, 11, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 13, 2, 7, 18, 2, 11, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 7, 3, 1, 11, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 15, 0, 7, 18, 1, 11, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);

			for (int i = 0; i <= 2; i++) {
				for (int j = 7; j <= 11; j++) {
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), i, -1, j, mutableIntBoundingBox);
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), 18 - i, -1, j, mutableIntBoundingBox);
				}
			}

			return true;
		}
	}

	public static class BridgeEnd extends NetherFortressGenerator.Piece {
		private final int seed;

		public BridgeEnd(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END, i);
			this.setOrientation(direction);
			this.boundingBox = mutableIntBoundingBox;
			this.seed = random.nextInt();
		}

		public BridgeEnd(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END, compoundTag);
			this.seed = compoundTag.getInt("Seed");
		}

		public static NetherFortressGenerator.BridgeEnd method_14797(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -3, 0, 5, 10, 8, direction);
			return method_14809(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.BridgeEnd(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putInt("Seed", this.seed);
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			Random random2 = new Random((long)this.seed);

			for (int i = 0; i <= 4; i++) {
				for (int j = 3; j <= 4; j++) {
					int k = random2.nextInt(8);
					this.fillWithOutline(iWorld, mutableIntBoundingBox, i, j, 0, i, j, k, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
				}
			}

			int i = random2.nextInt(8);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 0, 0, 5, i, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			i = random2.nextInt(8);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 5, 0, 4, 5, i, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);

			for (int ix = 0; ix <= 4; ix++) {
				int j = random2.nextInt(5);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, ix, 2, 0, ix, 2, j, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			}

			for (int ix = 0; ix <= 4; ix++) {
				for (int j = 0; j <= 1; j++) {
					int k = random2.nextInt(3);
					this.fillWithOutline(iWorld, mutableIntBoundingBox, ix, j, 0, ix, j, k, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
				}
			}

			return true;
		}
	}

	public static class BridgePlatform extends NetherFortressGenerator.Piece {
		private boolean hasBlazeSpawner;

		public BridgePlatform(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_PLATFORM, i);
			this.setOrientation(direction);
			this.boundingBox = mutableIntBoundingBox;
		}

		public BridgePlatform(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_PLATFORM, compoundTag);
			this.hasBlazeSpawner = compoundTag.getBoolean("Mob");
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putBoolean("Mob", this.hasBlazeSpawner);
		}

		public static NetherFortressGenerator.BridgePlatform method_14807(List<StructurePiece> list, int i, int j, int k, int l, Direction direction) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -2, 0, 0, 7, 8, 9, direction);
			return method_14809(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.BridgePlatform(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 0, 6, 7, 7, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 0, 5, 1, 7, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 2, 1, 5, 2, 7, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 3, 2, 5, 3, 7, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 4, 3, 5, 4, 7, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 2, 0, 1, 4, 2, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 2, 0, 5, 4, 2, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 5, 2, 1, 5, 3, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 5, 2, 5, 5, 3, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 3, 0, 5, 8, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 5, 3, 6, 5, 8, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 5, 8, 5, 5, 8, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			BlockState blockState = Blocks.field_10364.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.field_10364.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.addBlock(iWorld, Blocks.field_10364.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)), 1, 6, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10364.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)), 5, 6, 3, mutableIntBoundingBox);
			this.addBlock(
				iWorld,
				Blocks.field_10364.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)).with(FenceBlock.NORTH, Boolean.valueOf(true)),
				0,
				6,
				3,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10364.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.NORTH, Boolean.valueOf(true)),
				6,
				6,
				3,
				mutableIntBoundingBox
			);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 6, 4, 0, 6, 7, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 6, 4, 6, 6, 7, blockState2, blockState2, false);
			this.addBlock(
				iWorld,
				Blocks.field_10364.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true)),
				0,
				6,
				8,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10364.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true)),
				6,
				6,
				8,
				mutableIntBoundingBox
			);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 6, 8, 5, 6, 8, blockState, blockState, false);
			this.addBlock(iWorld, Blocks.field_10364.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)), 1, 7, 8, mutableIntBoundingBox);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 7, 8, 4, 7, 8, blockState, blockState, false);
			this.addBlock(iWorld, Blocks.field_10364.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)), 5, 7, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10364.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)), 2, 8, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 3, 8, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10364.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)), 4, 8, 8, mutableIntBoundingBox);
			if (!this.hasBlazeSpawner) {
				BlockPos blockPos = new BlockPos(this.applyXTransform(3, 5), this.applyYTransform(5), this.applyZTransform(3, 5));
				if (mutableIntBoundingBox.contains(blockPos)) {
					this.hasBlazeSpawner = true;
					iWorld.setBlockState(blockPos, Blocks.field_10260.getDefaultState(), 2);
					BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
					if (blockEntity instanceof MobSpawnerBlockEntity) {
						((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(EntityType.field_6099);
					}
				}
			}

			for (int i = 0; i <= 6; i++) {
				for (int j = 0; j <= 6; j++) {
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), i, -1, j, mutableIntBoundingBox);
				}
			}

			return true;
		}
	}

	public static class BridgeSmallCrossing extends NetherFortressGenerator.Piece {
		public BridgeSmallCrossing(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING, i);
			this.setOrientation(direction);
			this.boundingBox = mutableIntBoundingBox;
		}

		public BridgeSmallCrossing(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING, compoundTag);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14814((NetherFortressGenerator.Start)structurePiece, list, random, 2, 0, false);
			this.method_14812((NetherFortressGenerator.Start)structurePiece, list, random, 0, 2, false);
			this.method_14808((NetherFortressGenerator.Start)structurePiece, list, random, 0, 2, false);
		}

		public static NetherFortressGenerator.BridgeSmallCrossing method_14817(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -2, 0, 0, 7, 9, 7, direction);
			return method_14809(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.BridgeSmallCrossing(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 6, 1, 6, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 0, 6, 7, 6, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 0, 1, 6, 0, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 6, 1, 6, 6, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 2, 0, 6, 6, 0, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 2, 6, 6, 6, 6, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 0, 0, 6, 1, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 5, 0, 6, 6, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 2, 0, 6, 6, 1, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 2, 5, 6, 6, 6, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			BlockState blockState = Blocks.field_10364.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.field_10364.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 6, 0, 4, 6, 0, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 5, 0, 4, 5, 0, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 6, 6, 4, 6, 6, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 5, 6, 4, 5, 6, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 6, 2, 0, 6, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 2, 0, 5, 4, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 6, 2, 6, 6, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 5, 2, 6, 5, 4, blockState2, blockState2, false);

			for (int i = 0; i <= 6; i++) {
				for (int j = 0; j <= 6; j++) {
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), i, -1, j, mutableIntBoundingBox);
				}
			}

			return true;
		}
	}

	public static class BridgeStairs extends NetherFortressGenerator.Piece {
		public BridgeStairs(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STAIRS, i);
			this.setOrientation(direction);
			this.boundingBox = mutableIntBoundingBox;
		}

		public BridgeStairs(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STAIRS, compoundTag);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14808((NetherFortressGenerator.Start)structurePiece, list, random, 6, 2, false);
		}

		public static NetherFortressGenerator.BridgeStairs method_14818(List<StructurePiece> list, int i, int j, int k, int l, Direction direction) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -2, 0, 0, 7, 11, 7, direction);
			return method_14809(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.BridgeStairs(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 6, 1, 6, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 0, 6, 10, 6, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 0, 1, 8, 0, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 2, 0, 6, 8, 0, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 1, 0, 8, 6, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 2, 1, 6, 8, 6, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 2, 6, 5, 8, 6, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			BlockState blockState = Blocks.field_10364.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.field_10364.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, 2, 0, 5, 4, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 3, 2, 6, 5, 2, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 3, 4, 6, 5, 4, blockState2, blockState2, false);
			this.addBlock(iWorld, Blocks.field_10266.getDefaultState(), 5, 2, 5, mutableIntBoundingBox);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 2, 5, 4, 3, 5, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 2, 5, 3, 4, 5, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 2, 5, 2, 5, 5, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 2, 5, 1, 6, 5, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 7, 1, 5, 7, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 8, 2, 6, 8, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 6, 0, 4, 8, 0, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 5, 0, 4, 5, 0, blockState, blockState, false);

			for (int i = 0; i <= 6; i++) {
				for (int j = 0; j <= 6; j++) {
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), i, -1, j, mutableIntBoundingBox);
				}
			}

			return true;
		}
	}

	public static class CorridorBalcony extends NetherFortressGenerator.Piece {
		public CorridorBalcony(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_BALCONY, i);
			this.setOrientation(direction);
			this.boundingBox = mutableIntBoundingBox;
		}

		public CorridorBalcony(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_BALCONY, compoundTag);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			int i = 1;
			Direction direction = this.getFacing();
			if (direction == Direction.field_11039 || direction == Direction.field_11043) {
				i = 5;
			}

			this.method_14812((NetherFortressGenerator.Start)structurePiece, list, random, 0, i, random.nextInt(8) > 0);
			this.method_14808((NetherFortressGenerator.Start)structurePiece, list, random, 0, i, random.nextInt(8) > 0);
		}

		public static NetherFortressGenerator.CorridorBalcony method_14800(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -3, 0, 0, 9, 7, 9, direction);
			return method_14809(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.CorridorBalcony(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			BlockState blockState = Blocks.field_10364.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.field_10364.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true));
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 8, 1, 8, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 0, 8, 5, 8, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 6, 0, 8, 6, 5, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 0, 2, 5, 0, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 2, 0, 8, 5, 0, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 3, 0, 1, 4, 0, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 3, 0, 7, 4, 0, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 4, 8, 2, 8, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 4, 2, 2, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 1, 4, 7, 2, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 3, 8, 7, 3, 8, blockState2, blockState2, false);
			this.addBlock(
				iWorld,
				Blocks.field_10364.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true)),
				0,
				3,
				8,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10364.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true)),
				8,
				3,
				8,
				mutableIntBoundingBox
			);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, 6, 0, 3, 7, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 3, 6, 8, 3, 7, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, 4, 0, 5, 5, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 3, 4, 8, 5, 5, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 3, 5, 2, 5, 5, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 3, 5, 7, 5, 5, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 4, 5, 1, 5, 5, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 4, 5, 7, 5, 5, blockState2, blockState2, false);

			for (int i = 0; i <= 5; i++) {
				for (int j = 0; j <= 8; j++) {
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), j, -1, i, mutableIntBoundingBox);
				}
			}

			return true;
		}
	}

	public static class CorridorCrossing extends NetherFortressGenerator.Piece {
		public CorridorCrossing(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_CROSSING, i);
			this.setOrientation(direction);
			this.boundingBox = mutableIntBoundingBox;
		}

		public CorridorCrossing(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_CROSSING, compoundTag);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14814((NetherFortressGenerator.Start)structurePiece, list, random, 1, 0, true);
			this.method_14812((NetherFortressGenerator.Start)structurePiece, list, random, 0, 1, true);
			this.method_14808((NetherFortressGenerator.Start)structurePiece, list, random, 0, 1, true);
		}

		public static NetherFortressGenerator.CorridorCrossing method_14802(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, 0, 0, 5, 7, 5, direction);
			return method_14809(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.CorridorCrossing(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 1, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 0, 4, 5, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 0, 0, 5, 0, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 2, 0, 4, 5, 0, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 4, 0, 5, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 2, 4, 4, 5, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 6, 0, 4, 6, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), i, -1, j, mutableIntBoundingBox);
				}
			}

			return true;
		}
	}

	public static class CorridorExit extends NetherFortressGenerator.Piece {
		public CorridorExit(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_EXIT, i);
			this.setOrientation(direction);
			this.boundingBox = mutableIntBoundingBox;
		}

		public CorridorExit(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_EXIT, compoundTag);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14814((NetherFortressGenerator.Start)structurePiece, list, random, 5, 3, true);
		}

		public static NetherFortressGenerator.CorridorExit method_14801(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -5, -3, 0, 13, 14, 13, direction);
			return method_14809(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.CorridorExit(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, 0, 12, 4, 12, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 0, 12, 13, 12, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 0, 1, 12, 12, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 11, 5, 0, 12, 12, 12, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 5, 11, 4, 12, 12, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 5, 11, 10, 12, 12, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 9, 11, 7, 12, 12, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 5, 0, 4, 12, 1, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 5, 0, 10, 12, 1, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 9, 0, 7, 12, 1, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 11, 2, 10, 12, 10, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 8, 0, 7, 8, 0, Blocks.field_10364.getDefaultState(), Blocks.field_10364.getDefaultState(), false);
			BlockState blockState = Blocks.field_10364.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.field_10364.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true));

			for (int i = 1; i <= 11; i += 2) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, i, 10, 0, i, 11, 0, blockState, blockState, false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, i, 10, 12, i, 11, 12, blockState, blockState, false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 10, i, 0, 11, i, blockState2, blockState2, false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 10, i, 12, 11, i, blockState2, blockState2, false);
				this.addBlock(iWorld, Blocks.field_10266.getDefaultState(), i, 13, 0, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10266.getDefaultState(), i, 13, 12, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10266.getDefaultState(), 0, 13, i, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10266.getDefaultState(), 12, 13, i, mutableIntBoundingBox);
				if (i != 11) {
					this.addBlock(iWorld, blockState, i + 1, 13, 0, mutableIntBoundingBox);
					this.addBlock(iWorld, blockState, i + 1, 13, 12, mutableIntBoundingBox);
					this.addBlock(iWorld, blockState2, 0, 13, i + 1, mutableIntBoundingBox);
					this.addBlock(iWorld, blockState2, 12, 13, i + 1, mutableIntBoundingBox);
				}
			}

			this.addBlock(
				iWorld,
				Blocks.field_10364.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
				0,
				13,
				0,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10364.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
				0,
				13,
				12,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10364.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
				12,
				13,
				12,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10364.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
				12,
				13,
				0,
				mutableIntBoundingBox
			);

			for (int ix = 3; ix <= 9; ix += 2) {
				this.fillWithOutline(
					iWorld,
					mutableIntBoundingBox,
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
					iWorld,
					mutableIntBoundingBox,
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

			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 2, 0, 8, 2, 12, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 4, 12, 2, 8, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 0, 0, 8, 1, 3, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 0, 9, 8, 1, 12, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 4, 3, 1, 8, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 0, 4, 12, 1, 8, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);

			for (int ix = 4; ix <= 8; ix++) {
				for (int j = 0; j <= 2; j++) {
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), ix, -1, j, mutableIntBoundingBox);
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), ix, -1, 12 - j, mutableIntBoundingBox);
				}
			}

			for (int ix = 0; ix <= 2; ix++) {
				for (int j = 4; j <= 8; j++) {
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), ix, -1, j, mutableIntBoundingBox);
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), 12 - ix, -1, j, mutableIntBoundingBox);
				}
			}

			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 5, 5, 7, 5, 7, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 1, 6, 6, 4, 6, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.addBlock(iWorld, Blocks.field_10266.getDefaultState(), 6, 0, 6, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10164.getDefaultState(), 6, 5, 6, mutableIntBoundingBox);
			BlockPos blockPos = new BlockPos(this.applyXTransform(6, 6), this.applyYTransform(5), this.applyZTransform(6, 6));
			if (mutableIntBoundingBox.contains(blockPos)) {
				iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.LAVA, 0);
			}

			return true;
		}
	}

	public static class CorridorLeftTurn extends NetherFortressGenerator.Piece {
		private boolean containsChest;

		public CorridorLeftTurn(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_LEFT_TURN, i);
			this.setOrientation(direction);
			this.boundingBox = mutableIntBoundingBox;
			this.containsChest = random.nextInt(3) == 0;
		}

		public CorridorLeftTurn(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_LEFT_TURN, compoundTag);
			this.containsChest = compoundTag.getBoolean("Chest");
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putBoolean("Chest", this.containsChest);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14812((NetherFortressGenerator.Start)structurePiece, list, random, 0, 1, true);
		}

		public static NetherFortressGenerator.CorridorLeftTurn method_14803(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, 0, 0, 5, 7, 5, direction);
			return method_14809(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.CorridorLeftTurn(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 1, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 0, 4, 5, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			BlockState blockState = Blocks.field_10364.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.field_10364.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 2, 0, 4, 5, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 3, 1, 4, 4, 1, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 3, 3, 4, 4, 3, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 0, 0, 5, 0, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 4, 3, 5, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 3, 4, 1, 4, 4, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 3, 4, 3, 4, 4, blockState, blockState, false);
			if (this.containsChest && mutableIntBoundingBox.contains(new BlockPos(this.applyXTransform(3, 3), this.applyYTransform(2), this.applyZTransform(3, 3)))) {
				this.containsChest = false;
				this.addChest(iWorld, mutableIntBoundingBox, random, 3, 2, 3, LootTables.field_615);
			}

			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 6, 0, 4, 6, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), i, -1, j, mutableIntBoundingBox);
				}
			}

			return true;
		}
	}

	public static class CorridorNetherWartsRoom extends NetherFortressGenerator.Piece {
		public CorridorNetherWartsRoom(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM, i);
			this.setOrientation(direction);
			this.boundingBox = mutableIntBoundingBox;
		}

		public CorridorNetherWartsRoom(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM, compoundTag);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14814((NetherFortressGenerator.Start)structurePiece, list, random, 5, 3, true);
			this.method_14814((NetherFortressGenerator.Start)structurePiece, list, random, 5, 11, true);
		}

		public static NetherFortressGenerator.CorridorNetherWartsRoom method_14806(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -5, -3, 0, 13, 14, 13, direction);
			return method_14809(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.CorridorNetherWartsRoom(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, 0, 12, 4, 12, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 0, 12, 13, 12, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 0, 1, 12, 12, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 11, 5, 0, 12, 12, 12, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 5, 11, 4, 12, 12, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 5, 11, 10, 12, 12, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 9, 11, 7, 12, 12, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 5, 0, 4, 12, 1, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 5, 0, 10, 12, 1, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 9, 0, 7, 12, 1, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 11, 2, 10, 12, 10, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			BlockState blockState = Blocks.field_10364.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.field_10364.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true));
			BlockState blockState3 = blockState2.with(FenceBlock.WEST, Boolean.valueOf(true));
			BlockState blockState4 = blockState2.with(FenceBlock.EAST, Boolean.valueOf(true));

			for (int i = 1; i <= 11; i += 2) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, i, 10, 0, i, 11, 0, blockState, blockState, false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, i, 10, 12, i, 11, 12, blockState, blockState, false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 10, i, 0, 11, i, blockState2, blockState2, false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 10, i, 12, 11, i, blockState2, blockState2, false);
				this.addBlock(iWorld, Blocks.field_10266.getDefaultState(), i, 13, 0, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10266.getDefaultState(), i, 13, 12, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10266.getDefaultState(), 0, 13, i, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10266.getDefaultState(), 12, 13, i, mutableIntBoundingBox);
				if (i != 11) {
					this.addBlock(iWorld, blockState, i + 1, 13, 0, mutableIntBoundingBox);
					this.addBlock(iWorld, blockState, i + 1, 13, 12, mutableIntBoundingBox);
					this.addBlock(iWorld, blockState2, 0, 13, i + 1, mutableIntBoundingBox);
					this.addBlock(iWorld, blockState2, 12, 13, i + 1, mutableIntBoundingBox);
				}
			}

			this.addBlock(
				iWorld,
				Blocks.field_10364.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
				0,
				13,
				0,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10364.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
				0,
				13,
				12,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10364.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
				12,
				13,
				12,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10364.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
				12,
				13,
				0,
				mutableIntBoundingBox
			);

			for (int ix = 3; ix <= 9; ix += 2) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 7, ix, 1, 8, ix, blockState3, blockState3, false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 11, 7, ix, 11, 8, ix, blockState4, blockState4, false);
			}

			BlockState blockState5 = Blocks.field_10159.getDefaultState().with(StairsBlock.FACING, Direction.field_11043);

			for (int j = 0; j <= 6; j++) {
				int k = j + 4;

				for (int l = 5; l <= 7; l++) {
					this.addBlock(iWorld, blockState5, l, 5 + j, k, mutableIntBoundingBox);
				}

				if (k >= 5 && k <= 8) {
					this.fillWithOutline(
						iWorld, mutableIntBoundingBox, 5, 5, k, 7, j + 4, k, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false
					);
				} else if (k >= 9 && k <= 10) {
					this.fillWithOutline(
						iWorld, mutableIntBoundingBox, 5, 8, k, 7, j + 4, k, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false
					);
				}

				if (j >= 1) {
					this.fillWithOutline(
						iWorld, mutableIntBoundingBox, 5, 6 + j, k, 7, 9 + j, k, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false
					);
				}
			}

			for (int j = 5; j <= 7; j++) {
				this.addBlock(iWorld, blockState5, j, 12, 11, mutableIntBoundingBox);
			}

			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 6, 7, 5, 7, 7, blockState4, blockState4, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 7, 6, 7, 7, 7, 7, blockState3, blockState3, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 13, 12, 7, 13, 12, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 5, 2, 3, 5, 3, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 5, 9, 3, 5, 10, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 5, 4, 2, 5, 8, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 5, 2, 10, 5, 3, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 5, 9, 10, 5, 10, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 10, 5, 4, 10, 5, 8, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			BlockState blockState6 = blockState5.with(StairsBlock.FACING, Direction.field_11034);
			BlockState blockState7 = blockState5.with(StairsBlock.FACING, Direction.field_11039);
			this.addBlock(iWorld, blockState7, 4, 5, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState7, 4, 5, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState7, 4, 5, 9, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState7, 4, 5, 10, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6, 8, 5, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6, 8, 5, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6, 8, 5, 9, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6, 8, 5, 10, mutableIntBoundingBox);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 4, 4, 4, 4, 8, Blocks.field_10114.getDefaultState(), Blocks.field_10114.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 4, 4, 9, 4, 8, Blocks.field_10114.getDefaultState(), Blocks.field_10114.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 5, 4, 4, 5, 8, Blocks.field_9974.getDefaultState(), Blocks.field_9974.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 5, 4, 9, 5, 8, Blocks.field_9974.getDefaultState(), Blocks.field_9974.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 2, 0, 8, 2, 12, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 4, 12, 2, 8, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 0, 0, 8, 1, 3, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 0, 9, 8, 1, 12, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 4, 3, 1, 8, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 0, 4, 12, 1, 8, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);

			for (int l = 4; l <= 8; l++) {
				for (int m = 0; m <= 2; m++) {
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), l, -1, m, mutableIntBoundingBox);
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), l, -1, 12 - m, mutableIntBoundingBox);
				}
			}

			for (int l = 0; l <= 2; l++) {
				for (int m = 4; m <= 8; m++) {
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), l, -1, m, mutableIntBoundingBox);
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), 12 - l, -1, m, mutableIntBoundingBox);
				}
			}

			return true;
		}
	}

	public static class CorridorRightTurn extends NetherFortressGenerator.Piece {
		private boolean containsChest;

		public CorridorRightTurn(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_RIGHT_TURN, i);
			this.setOrientation(direction);
			this.boundingBox = mutableIntBoundingBox;
			this.containsChest = random.nextInt(3) == 0;
		}

		public CorridorRightTurn(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_RIGHT_TURN, compoundTag);
			this.containsChest = compoundTag.getBoolean("Chest");
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putBoolean("Chest", this.containsChest);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14808((NetherFortressGenerator.Start)structurePiece, list, random, 0, 1, true);
		}

		public static NetherFortressGenerator.CorridorRightTurn method_14805(
			List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l
		) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, 0, 0, 5, 7, 5, direction);
			return method_14809(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.CorridorRightTurn(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 1, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 0, 4, 5, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			BlockState blockState = Blocks.field_10364.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.field_10364.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 0, 0, 5, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, 1, 0, 4, 1, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, 3, 0, 4, 3, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 2, 0, 4, 5, 0, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 2, 4, 4, 5, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 3, 4, 1, 4, 4, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 3, 4, 3, 4, 4, blockState, blockState, false);
			if (this.containsChest && mutableIntBoundingBox.contains(new BlockPos(this.applyXTransform(1, 3), this.applyYTransform(2), this.applyZTransform(1, 3)))) {
				this.containsChest = false;
				this.addChest(iWorld, mutableIntBoundingBox, random, 1, 2, 3, LootTables.field_615);
			}

			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 6, 0, 4, 6, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), i, -1, j, mutableIntBoundingBox);
				}
			}

			return true;
		}
	}

	public static class CorridorStairs extends NetherFortressGenerator.Piece {
		public CorridorStairs(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_STAIRS, i);
			this.setOrientation(direction);
			this.boundingBox = mutableIntBoundingBox;
		}

		public CorridorStairs(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_STAIRS, compoundTag);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14814((NetherFortressGenerator.Start)structurePiece, list, random, 1, 0, true);
		}

		public static NetherFortressGenerator.CorridorStairs method_14799(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -7, 0, 5, 14, 10, direction);
			return method_14809(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.CorridorStairs(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			BlockState blockState = Blocks.field_10159.getDefaultState().with(StairsBlock.FACING, Direction.field_11035);
			BlockState blockState2 = Blocks.field_10364.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true));

			for (int i = 0; i <= 9; i++) {
				int j = Math.max(1, 7 - i);
				int k = Math.min(Math.max(j + 5, 14 - i), 13);
				int l = i;
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, i, 4, j, i, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
				this.fillWithOutline(
					iWorld, mutableIntBoundingBox, 1, j + 1, i, 3, k - 1, i, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false
				);
				if (i <= 6) {
					this.addBlock(iWorld, blockState, 1, j + 1, i, mutableIntBoundingBox);
					this.addBlock(iWorld, blockState, 2, j + 1, i, mutableIntBoundingBox);
					this.addBlock(iWorld, blockState, 3, j + 1, i, mutableIntBoundingBox);
				}

				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, k, i, 4, k, i, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
				this.fillWithOutline(
					iWorld, mutableIntBoundingBox, 0, j + 1, i, 0, k - 1, i, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false
				);
				this.fillWithOutline(
					iWorld, mutableIntBoundingBox, 4, j + 1, i, 4, k - 1, i, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false
				);
				if ((i & 1) == 0) {
					this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, j + 2, i, 0, j + 3, i, blockState2, blockState2, false);
					this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, j + 2, i, 4, j + 3, i, blockState2, blockState2, false);
				}

				for (int m = 0; m <= 4; m++) {
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), m, -1, l, mutableIntBoundingBox);
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
		protected void toNbt(CompoundTag compoundTag) {
		}

		private int method_14810(List<NetherFortressGenerator.class_3404> list) {
			boolean bl = false;
			int i = 0;

			for (NetherFortressGenerator.class_3404 lv : list) {
				if (lv.field_14499 > 0 && lv.field_14502 < lv.field_14499) {
					bl = true;
				}

				i += lv.field_14503;
			}

			return bl ? i : -1;
		}

		private NetherFortressGenerator.Piece method_14811(
			NetherFortressGenerator.Start start,
			List<NetherFortressGenerator.class_3404> list,
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

				for (NetherFortressGenerator.class_3404 lv : list) {
					o -= lv.field_14503;
					if (o < 0) {
						if (!lv.method_14816(l) || lv == start.field_14506 && !lv.field_14500) {
							break;
						}

						NetherFortressGenerator.Piece piece = NetherFortressGenerator.generatePiece(lv, list2, random, i, j, k, direction, l);
						if (piece != null) {
							lv.field_14502++;
							start.field_14506 = lv;
							if (!lv.method_14815()) {
								list.remove(lv);
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
				List<NetherFortressGenerator.class_3404> list2 = start.bridgePieces;
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
					case field_11043:
						return this.method_14813(
							start, list, random, this.boundingBox.minX + i, this.boundingBox.minY + j, this.boundingBox.minZ - 1, direction, this.method_14923(), bl
						);
					case field_11035:
						return this.method_14813(
							start, list, random, this.boundingBox.minX + i, this.boundingBox.minY + j, this.boundingBox.maxZ + 1, direction, this.method_14923(), bl
						);
					case field_11039:
						return this.method_14813(
							start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + j, this.boundingBox.minZ + i, direction, this.method_14923(), bl
						);
					case field_11034:
						return this.method_14813(
							start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + j, this.boundingBox.minZ + i, direction, this.method_14923(), bl
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
					case field_11043:
						return this.method_14813(
							start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.field_11039, this.method_14923(), bl
						);
					case field_11035:
						return this.method_14813(
							start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.field_11039, this.method_14923(), bl
						);
					case field_11039:
						return this.method_14813(
							start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.minZ - 1, Direction.field_11043, this.method_14923(), bl
						);
					case field_11034:
						return this.method_14813(
							start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.minZ - 1, Direction.field_11043, this.method_14923(), bl
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
					case field_11043:
						return this.method_14813(
							start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.field_11034, this.method_14923(), bl
						);
					case field_11035:
						return this.method_14813(
							start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.field_11034, this.method_14923(), bl
						);
					case field_11039:
						return this.method_14813(
							start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.maxZ + 1, Direction.field_11035, this.method_14923(), bl
						);
					case field_11034:
						return this.method_14813(
							start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.maxZ + 1, Direction.field_11035, this.method_14923(), bl
						);
				}
			}

			return null;
		}

		protected static boolean method_14809(MutableIntBoundingBox mutableIntBoundingBox) {
			return mutableIntBoundingBox != null && mutableIntBoundingBox.minY > 10;
		}
	}

	public static class SmallCorridor extends NetherFortressGenerator.Piece {
		public SmallCorridor(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.NETHER_FORTRESS_SMALL_CORRIDOR, i);
			this.setOrientation(direction);
			this.boundingBox = mutableIntBoundingBox;
		}

		public SmallCorridor(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_SMALL_CORRIDOR, compoundTag);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14814((NetherFortressGenerator.Start)structurePiece, list, random, 1, 0, true);
		}

		public static NetherFortressGenerator.SmallCorridor method_14804(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, 0, 0, 5, 7, 5, direction);
			return method_14809(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.SmallCorridor(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 1, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 0, 4, 5, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
			BlockState blockState = Blocks.field_10364.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 2, 0, 0, 5, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 2, 0, 4, 5, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, 1, 0, 4, 1, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, 3, 0, 4, 3, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 3, 1, 4, 4, 1, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 3, 3, 4, 4, 3, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 6, 0, 4, 6, 4, Blocks.field_10266.getDefaultState(), Blocks.field_10266.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.method_14936(iWorld, Blocks.field_10266.getDefaultState(), i, -1, j, mutableIntBoundingBox);
				}
			}

			return true;
		}
	}

	public static class Start extends NetherFortressGenerator.BridgeCrossing {
		public NetherFortressGenerator.class_3404 field_14506;
		public List<NetherFortressGenerator.class_3404> bridgePieces;
		public List<NetherFortressGenerator.class_3404> corridorPieces;
		public final List<StructurePiece> field_14505 = Lists.<StructurePiece>newArrayList();

		public Start(Random random, int i, int j) {
			super(random, i, j);
			this.bridgePieces = Lists.<NetherFortressGenerator.class_3404>newArrayList();

			for (NetherFortressGenerator.class_3404 lv : NetherFortressGenerator.field_14494) {
				lv.field_14502 = 0;
				this.bridgePieces.add(lv);
			}

			this.corridorPieces = Lists.<NetherFortressGenerator.class_3404>newArrayList();

			for (NetherFortressGenerator.class_3404 lv : NetherFortressGenerator.field_14493) {
				lv.field_14502 = 0;
				this.corridorPieces.add(lv);
			}
		}

		public Start(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.NETHER_FORTRESS_START, compoundTag);
		}
	}

	static class class_3404 {
		public final Class<? extends NetherFortressGenerator.Piece> field_14501;
		public final int field_14503;
		public int field_14502;
		public final int field_14499;
		public final boolean field_14500;

		public class_3404(Class<? extends NetherFortressGenerator.Piece> class_, int i, int j, boolean bl) {
			this.field_14501 = class_;
			this.field_14503 = i;
			this.field_14499 = j;
			this.field_14500 = bl;
		}

		public class_3404(Class<? extends NetherFortressGenerator.Piece> class_, int i, int j) {
			this(class_, i, j, false);
		}

		public boolean method_14816(int i) {
			return this.field_14499 == 0 || this.field_14502 < this.field_14499;
		}

		public boolean method_14815() {
			return this.field_14499 == 0 || this.field_14502 < this.field_14499;
		}
	}
}
