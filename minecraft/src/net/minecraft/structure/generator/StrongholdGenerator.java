package net.minecraft.structure.generator;

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
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.loot.LootTables;

public class StrongholdGenerator {
	private static final StrongholdGenerator.class_3427[] field_15265 = new StrongholdGenerator.class_3427[]{
		new StrongholdGenerator.class_3427(StrongholdGenerator.class_3435.class, 40, 0),
		new StrongholdGenerator.class_3427(StrongholdGenerator.class_3429.class, 5, 5),
		new StrongholdGenerator.class_3427(StrongholdGenerator.class_3425.class, 20, 0),
		new StrongholdGenerator.class_3427(StrongholdGenerator.class_3430.class, 20, 0),
		new StrongholdGenerator.class_3427(StrongholdGenerator.class_3431.class, 10, 6),
		new StrongholdGenerator.class_3427(StrongholdGenerator.class_3436.class, 5, 5),
		new StrongholdGenerator.class_3427(StrongholdGenerator.class_3433.class, 5, 5),
		new StrongholdGenerator.class_3427(StrongholdGenerator.FiveWayCrossing.class, 5, 4),
		new StrongholdGenerator.class_3427(StrongholdGenerator.Door.class, 5, 4),
		new StrongholdGenerator.class_3427(StrongholdGenerator.class_3426.class, 10, 2) {
			@Override
			public boolean method_14862(int i) {
				return super.method_14862(i) && i > 4;
			}
		},
		new StrongholdGenerator.class_3427(StrongholdGenerator.class_3428.class, 20, 1) {
			@Override
			public boolean method_14862(int i) {
				return super.method_14862(i) && i > 5;
			}
		}
	};
	private static List<StrongholdGenerator.class_3427> field_15267;
	private static Class<? extends StrongholdGenerator.class_3437> field_15266;
	private static int field_15264;
	private static final StrongholdGenerator.class_3432 field_15263 = new StrongholdGenerator.class_3432();

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

	private static StrongholdGenerator.class_3437 method_14847(
		Class<? extends StrongholdGenerator.class_3437> class_, List<StructurePiece> list, Random random, int i, int j, int k, @Nullable Direction direction, int l
	) {
		StrongholdGenerator.class_3437 lv = null;
		if (class_ == StrongholdGenerator.class_3435.class) {
			lv = StrongholdGenerator.class_3435.method_14867(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.class_3429.class) {
			lv = StrongholdGenerator.class_3429.method_14864(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.class_3425.class) {
			lv = StrongholdGenerator.class_3425.method_14859(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.class_3430.class) {
			lv = StrongholdGenerator.class_3430.method_16652(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.class_3431.class) {
			lv = StrongholdGenerator.class_3431.method_14865(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.class_3436.class) {
			lv = StrongholdGenerator.class_3436.method_14868(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.class_3433.class) {
			lv = StrongholdGenerator.class_3433.method_14866(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.FiveWayCrossing.class) {
			lv = StrongholdGenerator.FiveWayCrossing.method_14858(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.Door.class) {
			lv = StrongholdGenerator.Door.method_14856(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.class_3426.class) {
			lv = StrongholdGenerator.class_3426.method_14860(list, random, i, j, k, direction, l);
		} else if (class_ == StrongholdGenerator.class_3428.class) {
			lv = StrongholdGenerator.class_3428.method_14863(list, i, j, k, direction, l);
		}

		return lv;
	}

	private static StrongholdGenerator.class_3437 method_14851(
		StrongholdGenerator.Start start, List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l
	) {
		if (!method_14852()) {
			return null;
		} else {
			if (field_15266 != null) {
				StrongholdGenerator.class_3437 lv = method_14847(field_15266, list, random, i, j, k, direction, l);
				field_15266 = null;
				if (lv != null) {
					return lv;
				}
			}

			int m = 0;

			while (m < 5) {
				m++;
				int n = random.nextInt(field_15264);

				for (StrongholdGenerator.class_3427 lv2 : field_15267) {
					n -= lv2.field_15278;
					if (n < 0) {
						if (!lv2.method_14862(l) || lv2 == start.field_15284) {
							break;
						}

						StrongholdGenerator.class_3437 lv3 = method_14847(lv2.field_15276, list, random, i, j, k, direction, l);
						if (lv3 != null) {
							lv2.field_15277++;
							start.field_15284 = lv2;
							if (!lv2.method_14861()) {
								field_15267.remove(lv2);
							}

							return lv3;
						}
					}
				}
			}

			MutableIntBoundingBox mutableIntBoundingBox = StrongholdGenerator.class_3423.method_14857(list, random, i, j, k, direction);
			return mutableIntBoundingBox != null && mutableIntBoundingBox.minY > 1 ? new StrongholdGenerator.class_3423(l, mutableIntBoundingBox, direction) : null;
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

	public static class Door extends StrongholdGenerator.class_3437 {
		private boolean field_15268;

		public Door(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.field_16955, i);
			this.method_14926(direction);
			this.entryDoor = this.method_14869(random);
			this.boundingBox = mutableIntBoundingBox;
		}

		public Door(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.field_16955, compoundTag);
			this.field_15268 = compoundTag.getBoolean("Chest");
		}

		@Override
		protected void method_14943(CompoundTag compoundTag) {
			super.method_14943(compoundTag);
			compoundTag.putBoolean("Chest", this.field_15268);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
		}

		public static StrongholdGenerator.Door method_14856(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.method_14667(i, j, k, -1, -1, 0, 5, 5, 7, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.Door(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 4, 6, true, random, StrongholdGenerator.field_15263);
			this.method_14872(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 1, 0);
			this.method_14872(iWorld, random, mutableIntBoundingBox, StrongholdGenerator.class_3437.class_3438.field_15288, 1, 1, 6);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 1, 2, 3, 1, 4, Blocks.field_10056.method_9564(), Blocks.field_10056.method_9564(), false);
			this.addBlock(iWorld, Blocks.field_10131.method_9564(), 3, 1, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10131.method_9564(), 3, 1, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10131.method_9564(), 3, 2, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10131.method_9564(), 3, 2, 4, mutableIntBoundingBox);

			for (int i = 2; i <= 4; i++) {
				this.addBlock(iWorld, Blocks.field_10131.method_9564(), 2, 1, i, mutableIntBoundingBox);
			}

			if (!this.field_15268 && mutableIntBoundingBox.method_14662(new BlockPos(this.applyXTransform(3, 3), this.applyYTransform(2), this.applyZTransform(3, 3)))) {
				this.field_15268 = true;
				this.method_14915(iWorld, mutableIntBoundingBox, random, 3, 2, 3, LootTables.field_842);
			}

			return true;
		}
	}

	public static class FiveWayCrossing extends StrongholdGenerator.class_3437 {
		private final boolean leftLow;
		private final boolean leftHigh;
		private final boolean rightLow;
		private final boolean rightHigh;

		public FiveWayCrossing(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.STRONGHOLD_FIVE_WAY_CROSSING, i);
			this.method_14926(direction);
			this.entryDoor = this.method_14869(random);
			this.boundingBox = mutableIntBoundingBox;
			this.leftLow = random.nextBoolean();
			this.leftHigh = random.nextBoolean();
			this.rightLow = random.nextBoolean();
			this.rightHigh = random.nextInt(3) > 0;
		}

		public FiveWayCrossing(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_FIVE_WAY_CROSSING, compoundTag);
			this.leftLow = compoundTag.getBoolean("leftLow");
			this.leftHigh = compoundTag.getBoolean("leftHigh");
			this.rightLow = compoundTag.getBoolean("rightLow");
			this.rightHigh = compoundTag.getBoolean("rightHigh");
		}

		@Override
		protected void method_14943(CompoundTag compoundTag) {
			super.method_14943(compoundTag);
			compoundTag.putBoolean("leftLow", this.leftLow);
			compoundTag.putBoolean("leftHigh", this.leftHigh);
			compoundTag.putBoolean("rightLow", this.rightLow);
			compoundTag.putBoolean("rightHigh", this.rightHigh);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			int i = 3;
			int j = 5;
			Direction direction = this.method_14934();
			if (direction == Direction.WEST || direction == Direction.NORTH) {
				i = 8 - i;
				j = 8 - j;
			}

			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 5, 1);
			if (this.leftLow) {
				this.method_14870((StrongholdGenerator.Start)structurePiece, list, random, i, 1);
			}

			if (this.leftHigh) {
				this.method_14870((StrongholdGenerator.Start)structurePiece, list, random, j, 7);
			}

			if (this.rightLow) {
				this.method_14873((StrongholdGenerator.Start)structurePiece, list, random, i, 1);
			}

			if (this.rightHigh) {
				this.method_14873((StrongholdGenerator.Start)structurePiece, list, random, j, 7);
			}
		}

		public static StrongholdGenerator.FiveWayCrossing method_14858(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.method_14667(i, j, k, -4, -3, 0, 10, 9, 11, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.FiveWayCrossing(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 9, 8, 10, true, random, StrongholdGenerator.field_15263);
			this.method_14872(iWorld, random, mutableIntBoundingBox, this.entryDoor, 4, 3, 0);
			if (this.leftLow) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, 1, 0, 5, 3, AIR, AIR, false);
			}

			if (this.rightLow) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 3, 1, 9, 5, 3, AIR, AIR, false);
			}

			if (this.leftHigh) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 7, 0, 7, 9, AIR, AIR, false);
			}

			if (this.rightHigh) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 5, 7, 9, 7, 9, AIR, AIR, false);
			}

			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 1, 10, 7, 3, 10, AIR, AIR, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 2, 1, 8, 2, 6, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 5, 4, 4, 9, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 1, 5, 8, 4, 9, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 4, 7, 3, 4, 9, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 3, 5, 3, 3, 6, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 3, 4, 3, 3, 4, Blocks.field_10136.method_9564(), Blocks.field_10136.method_9564(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 4, 6, 3, 4, 6, Blocks.field_10136.method_9564(), Blocks.field_10136.method_9564(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 1, 7, 7, 1, 8, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 1, 9, 7, 1, 9, Blocks.field_10136.method_9564(), Blocks.field_10136.method_9564(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 2, 7, 7, 2, 7, Blocks.field_10136.method_9564(), Blocks.field_10136.method_9564(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 5, 7, 4, 5, 9, Blocks.field_10136.method_9564(), Blocks.field_10136.method_9564(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 5, 7, 8, 5, 9, Blocks.field_10136.method_9564(), Blocks.field_10136.method_9564(), false);
			this.fillWithOutline(
				iWorld,
				mutableIntBoundingBox,
				5,
				5,
				7,
				7,
				5,
				9,
				Blocks.field_10136.method_9564().method_11657(SlabBlock.field_11501, SlabType.field_12682),
				Blocks.field_10136.method_9564().method_11657(SlabBlock.field_11501, SlabType.field_12682),
				false
			);
			this.addBlock(iWorld, Blocks.field_10099.method_9564().method_11657(WallTorchBlock.field_11731, Direction.SOUTH), 6, 5, 6, mutableIntBoundingBox);
			return true;
		}
	}

	public static class Start extends StrongholdGenerator.class_3433 {
		public StrongholdGenerator.class_3427 field_15284;
		@Nullable
		public StrongholdGenerator.class_3428 field_15283;
		public final List<StructurePiece> field_15282 = Lists.<StructurePiece>newArrayList();

		public Start(Random random, int i, int j) {
			super(StructurePieceType.STRONGHOLD_START, 0, random, i, j);
		}

		public Start(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.STRONGHOLD_START, compoundTag);
		}
	}

	public static class class_3423 extends StrongholdGenerator.class_3437 {
		private final int field_15269;

		public class_3423(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.field_16965, i);
			this.method_14926(direction);
			this.boundingBox = mutableIntBoundingBox;
			this.field_15269 = direction != Direction.NORTH && direction != Direction.SOUTH
				? mutableIntBoundingBox.getBlockCountX()
				: mutableIntBoundingBox.getBlockCountZ();
		}

		public class_3423(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.field_16965, compoundTag);
			this.field_15269 = compoundTag.getInt("Steps");
		}

		@Override
		protected void method_14943(CompoundTag compoundTag) {
			super.method_14943(compoundTag);
			compoundTag.putInt("Steps", this.field_15269);
		}

		public static MutableIntBoundingBox method_14857(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction) {
			int l = 3;
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.method_14667(i, j, k, -1, -1, 0, 5, 5, 4, direction);
			StructurePiece structurePiece = StructurePiece.method_14932(list, mutableIntBoundingBox);
			if (structurePiece == null) {
				return null;
			} else {
				if (structurePiece.getBoundingBox().minY == mutableIntBoundingBox.minY) {
					for (int m = 3; m >= 1; m--) {
						mutableIntBoundingBox = MutableIntBoundingBox.method_14667(i, j, k, -1, -1, 0, 5, 5, m - 1, direction);
						if (!structurePiece.getBoundingBox().intersects(mutableIntBoundingBox)) {
							return MutableIntBoundingBox.method_14667(i, j, k, -1, -1, 0, 5, 5, m, direction);
						}
					}
				}

				return null;
			}
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			for (int i = 0; i < this.field_15269; i++) {
				this.addBlock(iWorld, Blocks.field_10056.method_9564(), 0, 0, i, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10056.method_9564(), 1, 0, i, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10056.method_9564(), 2, 0, i, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10056.method_9564(), 3, 0, i, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10056.method_9564(), 4, 0, i, mutableIntBoundingBox);

				for (int j = 1; j <= 3; j++) {
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), 0, j, i, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10543.method_9564(), 1, j, i, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10543.method_9564(), 2, j, i, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10543.method_9564(), 3, j, i, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), 4, j, i, mutableIntBoundingBox);
				}

				this.addBlock(iWorld, Blocks.field_10056.method_9564(), 0, 4, i, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10056.method_9564(), 1, 4, i, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10056.method_9564(), 2, 4, i, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10056.method_9564(), 3, 4, i, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10056.method_9564(), 4, 4, i, mutableIntBoundingBox);
			}

			return true;
		}
	}

	public static class class_3425 extends StrongholdGenerator.class_3466 {
		public class_3425(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.field_16906, i);
			this.method_14926(direction);
			this.entryDoor = this.method_14869(random);
			this.boundingBox = mutableIntBoundingBox;
		}

		public class_3425(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.field_16906, compoundTag);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			Direction direction = this.method_14934();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.method_14873((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
			} else {
				this.method_14870((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
			}
		}

		public static StrongholdGenerator.class_3425 method_14859(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.method_14667(i, j, k, -1, -1, 0, 5, 5, 5, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.class_3425(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 4, 4, true, random, StrongholdGenerator.field_15263);
			this.method_14872(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 1, 0);
			Direction direction = this.method_14934();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 1, 4, 3, 3, AIR, AIR, false);
			} else {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 1, 0, 3, 3, AIR, AIR, false);
			}

			return true;
		}
	}

	public static class class_3426 extends StrongholdGenerator.class_3437 {
		private final boolean field_15274;

		public class_3426(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.field_16959, i);
			this.method_14926(direction);
			this.entryDoor = this.method_14869(random);
			this.boundingBox = mutableIntBoundingBox;
			this.field_15274 = mutableIntBoundingBox.getBlockCountY() > 6;
		}

		public class_3426(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.field_16959, compoundTag);
			this.field_15274 = compoundTag.getBoolean("Tall");
		}

		@Override
		protected void method_14943(CompoundTag compoundTag) {
			super.method_14943(compoundTag);
			compoundTag.putBoolean("Tall", this.field_15274);
		}

		public static StrongholdGenerator.class_3426 method_14860(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.method_14667(i, j, k, -4, -1, 0, 14, 11, 15, direction);
			if (!method_14871(mutableIntBoundingBox) || StructurePiece.method_14932(list, mutableIntBoundingBox) != null) {
				mutableIntBoundingBox = MutableIntBoundingBox.method_14667(i, j, k, -4, -1, 0, 14, 6, 15, direction);
				if (!method_14871(mutableIntBoundingBox) || StructurePiece.method_14932(list, mutableIntBoundingBox) != null) {
					return null;
				}
			}

			return new StrongholdGenerator.class_3426(l, random, mutableIntBoundingBox, direction);
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			int i = 11;
			if (!this.field_15274) {
				i = 6;
			}

			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 13, i - 1, 14, true, random, StrongholdGenerator.field_15263);
			this.method_14872(iWorld, random, mutableIntBoundingBox, this.entryDoor, 4, 1, 0);
			this.fillWithOutlineUnderSealevel(
				iWorld, mutableIntBoundingBox, random, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.field_10343.method_9564(), Blocks.field_10343.method_9564(), false, false
			);
			int j = 1;
			int k = 12;

			for (int l = 1; l <= 13; l++) {
				if ((l - 1) % 4 == 0) {
					this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, l, 1, 4, l, Blocks.field_10161.method_9564(), Blocks.field_10161.method_9564(), false);
					this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 1, l, 12, 4, l, Blocks.field_10161.method_9564(), Blocks.field_10161.method_9564(), false);
					this.addBlock(iWorld, Blocks.field_10099.method_9564().method_11657(WallTorchBlock.field_11731, Direction.EAST), 2, 3, l, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10099.method_9564().method_11657(WallTorchBlock.field_11731, Direction.WEST), 11, 3, l, mutableIntBoundingBox);
					if (this.field_15274) {
						this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 6, l, 1, 9, l, Blocks.field_10161.method_9564(), Blocks.field_10161.method_9564(), false);
						this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 6, l, 12, 9, l, Blocks.field_10161.method_9564(), Blocks.field_10161.method_9564(), false);
					}
				} else {
					this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, l, 1, 4, l, Blocks.field_10504.method_9564(), Blocks.field_10504.method_9564(), false);
					this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 1, l, 12, 4, l, Blocks.field_10504.method_9564(), Blocks.field_10504.method_9564(), false);
					if (this.field_15274) {
						this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 6, l, 1, 9, l, Blocks.field_10504.method_9564(), Blocks.field_10504.method_9564(), false);
						this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 6, l, 12, 9, l, Blocks.field_10504.method_9564(), Blocks.field_10504.method_9564(), false);
					}
				}
			}

			for (int lx = 3; lx < 12; lx += 2) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 1, lx, 4, 3, lx, Blocks.field_10504.method_9564(), Blocks.field_10504.method_9564(), false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 1, lx, 7, 3, lx, Blocks.field_10504.method_9564(), Blocks.field_10504.method_9564(), false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 1, lx, 10, 3, lx, Blocks.field_10504.method_9564(), Blocks.field_10504.method_9564(), false);
			}

			if (this.field_15274) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 5, 1, 3, 5, 13, Blocks.field_10161.method_9564(), Blocks.field_10161.method_9564(), false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 10, 5, 1, 12, 5, 13, Blocks.field_10161.method_9564(), Blocks.field_10161.method_9564(), false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 5, 1, 9, 5, 2, Blocks.field_10161.method_9564(), Blocks.field_10161.method_9564(), false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 5, 12, 9, 5, 13, Blocks.field_10161.method_9564(), Blocks.field_10161.method_9564(), false);
				this.addBlock(iWorld, Blocks.field_10161.method_9564(), 9, 5, 11, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10161.method_9564(), 8, 5, 11, mutableIntBoundingBox);
				this.addBlock(iWorld, Blocks.field_10161.method_9564(), 9, 5, 10, mutableIntBoundingBox);
				BlockState blockState = Blocks.field_10620
					.method_9564()
					.method_11657(FenceBlock.field_10903, Boolean.valueOf(true))
					.method_11657(FenceBlock.field_10907, Boolean.valueOf(true));
				BlockState blockState2 = Blocks.field_10620
					.method_9564()
					.method_11657(FenceBlock.field_10905, Boolean.valueOf(true))
					.method_11657(FenceBlock.field_10904, Boolean.valueOf(true));
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 6, 3, 3, 6, 11, blockState2, blockState2, false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 10, 6, 3, 10, 6, 9, blockState2, blockState2, false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 6, 2, 9, 6, 2, blockState, blockState, false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 6, 12, 7, 6, 12, blockState, blockState, false);
				this.addBlock(
					iWorld,
					Blocks.field_10620.method_9564().method_11657(FenceBlock.field_10905, Boolean.valueOf(true)).method_11657(FenceBlock.field_10907, Boolean.valueOf(true)),
					3,
					6,
					2,
					mutableIntBoundingBox
				);
				this.addBlock(
					iWorld,
					Blocks.field_10620.method_9564().method_11657(FenceBlock.field_10904, Boolean.valueOf(true)).method_11657(FenceBlock.field_10907, Boolean.valueOf(true)),
					3,
					6,
					12,
					mutableIntBoundingBox
				);
				this.addBlock(
					iWorld,
					Blocks.field_10620.method_9564().method_11657(FenceBlock.field_10905, Boolean.valueOf(true)).method_11657(FenceBlock.field_10903, Boolean.valueOf(true)),
					10,
					6,
					2,
					mutableIntBoundingBox
				);

				for (int m = 0; m <= 2; m++) {
					this.addBlock(
						iWorld,
						Blocks.field_10620.method_9564().method_11657(FenceBlock.field_10904, Boolean.valueOf(true)).method_11657(FenceBlock.field_10903, Boolean.valueOf(true)),
						8 + m,
						6,
						12 - m,
						mutableIntBoundingBox
					);
					if (m != 2) {
						this.addBlock(
							iWorld,
							Blocks.field_10620.method_9564().method_11657(FenceBlock.field_10905, Boolean.valueOf(true)).method_11657(FenceBlock.field_10907, Boolean.valueOf(true)),
							8 + m,
							6,
							11 - m,
							mutableIntBoundingBox
						);
					}
				}

				BlockState blockState3 = Blocks.field_9983.method_9564().method_11657(LadderBlock.field_11253, Direction.SOUTH);
				this.addBlock(iWorld, blockState3, 10, 1, 13, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState3, 10, 2, 13, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState3, 10, 3, 13, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState3, 10, 4, 13, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState3, 10, 5, 13, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState3, 10, 6, 13, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState3, 10, 7, 13, mutableIntBoundingBox);
				int n = 7;
				int o = 7;
				BlockState blockState4 = Blocks.field_10620.method_9564().method_11657(FenceBlock.field_10907, Boolean.valueOf(true));
				this.addBlock(iWorld, blockState4, 6, 9, 7, mutableIntBoundingBox);
				BlockState blockState5 = Blocks.field_10620.method_9564().method_11657(FenceBlock.field_10903, Boolean.valueOf(true));
				this.addBlock(iWorld, blockState5, 7, 9, 7, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState4, 6, 8, 7, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState5, 7, 8, 7, mutableIntBoundingBox);
				BlockState blockState6 = blockState2.method_11657(FenceBlock.field_10903, Boolean.valueOf(true))
					.method_11657(FenceBlock.field_10907, Boolean.valueOf(true));
				this.addBlock(iWorld, blockState6, 6, 7, 7, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState6, 7, 7, 7, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState4, 5, 7, 7, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState5, 8, 7, 7, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState4.method_11657(FenceBlock.field_10905, Boolean.valueOf(true)), 6, 7, 6, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState4.method_11657(FenceBlock.field_10904, Boolean.valueOf(true)), 6, 7, 8, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState5.method_11657(FenceBlock.field_10905, Boolean.valueOf(true)), 7, 7, 6, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState5.method_11657(FenceBlock.field_10904, Boolean.valueOf(true)), 7, 7, 8, mutableIntBoundingBox);
				BlockState blockState7 = Blocks.field_10336.method_9564();
				this.addBlock(iWorld, blockState7, 5, 8, 7, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState7, 8, 8, 7, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState7, 6, 8, 6, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState7, 6, 8, 8, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState7, 7, 8, 6, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState7, 7, 8, 8, mutableIntBoundingBox);
			}

			this.method_14915(iWorld, mutableIntBoundingBox, random, 3, 3, 5, LootTables.field_683);
			if (this.field_15274) {
				this.addBlock(iWorld, AIR, 12, 9, 1, mutableIntBoundingBox);
				this.method_14915(iWorld, mutableIntBoundingBox, random, 12, 8, 1, LootTables.field_683);
			}

			return true;
		}
	}

	static class class_3427 {
		public final Class<? extends StrongholdGenerator.class_3437> field_15276;
		public final int field_15278;
		public int field_15277;
		public final int field_15275;

		public class_3427(Class<? extends StrongholdGenerator.class_3437> class_, int i, int j) {
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

	public static class class_3428 extends StrongholdGenerator.class_3437 {
		private boolean field_15279;

		public class_3428(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.field_16939, i);
			this.method_14926(direction);
			this.boundingBox = mutableIntBoundingBox;
		}

		public class_3428(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.field_16939, compoundTag);
			this.field_15279 = compoundTag.getBoolean("Mob");
		}

		@Override
		protected void method_14943(CompoundTag compoundTag) {
			super.method_14943(compoundTag);
			compoundTag.putBoolean("Mob", this.field_15279);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			if (structurePiece != null) {
				((StrongholdGenerator.Start)structurePiece).field_15283 = this;
			}
		}

		public static StrongholdGenerator.class_3428 method_14863(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.method_14667(i, j, k, -4, -1, 0, 11, 8, 16, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.class_3428(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 10, 7, 15, false, random, StrongholdGenerator.field_15263);
			this.method_14872(iWorld, random, mutableIntBoundingBox, StrongholdGenerator.class_3437.class_3438.field_15289, 4, 1, 0);
			int i = 6;
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, i, 1, 1, i, 14, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, i, 1, 9, i, 14, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, i, 1, 8, i, 2, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, i, 14, 8, i, 14, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 1, 2, 1, 4, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 1, 1, 9, 1, 4, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 1, 1, 1, 3, Blocks.field_10164.method_9564(), Blocks.field_10164.method_9564(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 1, 1, 9, 1, 3, Blocks.field_10164.method_9564(), Blocks.field_10164.method_9564(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 1, 8, 7, 1, 12, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 9, 6, 1, 11, Blocks.field_10164.method_9564(), Blocks.field_10164.method_9564(), false);
			BlockState blockState = Blocks.field_10576
				.method_9564()
				.method_11657(PaneBlock.field_10905, Boolean.valueOf(true))
				.method_11657(PaneBlock.field_10904, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.field_10576
				.method_9564()
				.method_11657(PaneBlock.field_10903, Boolean.valueOf(true))
				.method_11657(PaneBlock.field_10907, Boolean.valueOf(true));

			for (int j = 3; j < 14; j += 2) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, j, 0, 4, j, blockState, blockState, false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 10, 3, j, 10, 4, j, blockState, blockState, false);
			}

			for (int j = 2; j < 9; j += 2) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, j, 3, 15, j, 4, 15, blockState2, blockState2, false);
			}

			BlockState blockState3 = Blocks.field_10392.method_9564().method_11657(StairsBlock.field_11571, Direction.NORTH);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 5, 6, 1, 7, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 2, 6, 6, 2, 7, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 3, 7, 6, 3, 7, false, random, StrongholdGenerator.field_15263);

			for (int k = 4; k <= 6; k++) {
				this.addBlock(iWorld, blockState3, k, 1, 4, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState3, k, 2, 5, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState3, k, 3, 6, mutableIntBoundingBox);
			}

			BlockState blockState4 = Blocks.field_10398.method_9564().method_11657(EndPortalFrameBlock.field_10954, Direction.NORTH);
			BlockState blockState5 = Blocks.field_10398.method_9564().method_11657(EndPortalFrameBlock.field_10954, Direction.SOUTH);
			BlockState blockState6 = Blocks.field_10398.method_9564().method_11657(EndPortalFrameBlock.field_10954, Direction.EAST);
			BlockState blockState7 = Blocks.field_10398.method_9564().method_11657(EndPortalFrameBlock.field_10954, Direction.WEST);
			boolean bl = true;
			boolean[] bls = new boolean[12];

			for (int l = 0; l < bls.length; l++) {
				bls[l] = random.nextFloat() > 0.9F;
				bl &= bls[l];
			}

			this.addBlock(iWorld, blockState4.method_11657(EndPortalFrameBlock.field_10958, Boolean.valueOf(bls[0])), 4, 3, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4.method_11657(EndPortalFrameBlock.field_10958, Boolean.valueOf(bls[1])), 5, 3, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4.method_11657(EndPortalFrameBlock.field_10958, Boolean.valueOf(bls[2])), 6, 3, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState5.method_11657(EndPortalFrameBlock.field_10958, Boolean.valueOf(bls[3])), 4, 3, 12, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState5.method_11657(EndPortalFrameBlock.field_10958, Boolean.valueOf(bls[4])), 5, 3, 12, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState5.method_11657(EndPortalFrameBlock.field_10958, Boolean.valueOf(bls[5])), 6, 3, 12, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6.method_11657(EndPortalFrameBlock.field_10958, Boolean.valueOf(bls[6])), 3, 3, 9, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6.method_11657(EndPortalFrameBlock.field_10958, Boolean.valueOf(bls[7])), 3, 3, 10, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState6.method_11657(EndPortalFrameBlock.field_10958, Boolean.valueOf(bls[8])), 3, 3, 11, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState7.method_11657(EndPortalFrameBlock.field_10958, Boolean.valueOf(bls[9])), 7, 3, 9, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState7.method_11657(EndPortalFrameBlock.field_10958, Boolean.valueOf(bls[10])), 7, 3, 10, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState7.method_11657(EndPortalFrameBlock.field_10958, Boolean.valueOf(bls[11])), 7, 3, 11, mutableIntBoundingBox);
			if (bl) {
				BlockState blockState8 = Blocks.field_10027.method_9564();
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

			if (!this.field_15279) {
				i = this.applyYTransform(3);
				BlockPos blockPos = new BlockPos(this.applyXTransform(5, 6), i, this.applyZTransform(5, 6));
				if (mutableIntBoundingBox.method_14662(blockPos)) {
					this.field_15279 = true;
					iWorld.method_8652(blockPos, Blocks.field_10260.method_9564(), 2);
					BlockEntity blockEntity = iWorld.method_8321(blockPos);
					if (blockEntity instanceof MobSpawnerBlockEntity) {
						((MobSpawnerBlockEntity)blockEntity).getLogic().method_8274(EntityType.SILVERFISH);
					}
				}
			}

			return true;
		}
	}

	public static class class_3429 extends StrongholdGenerator.class_3437 {
		public class_3429(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.field_16948, i);
			this.method_14926(direction);
			this.entryDoor = this.method_14869(random);
			this.boundingBox = mutableIntBoundingBox;
		}

		public class_3429(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.field_16948, compoundTag);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
		}

		public static StrongholdGenerator.class_3429 method_14864(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.method_14667(i, j, k, -1, -1, 0, 9, 5, 11, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.class_3429(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 8, 4, 10, true, random, StrongholdGenerator.field_15263);
			this.method_14872(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 1, 0);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 10, 3, 3, 10, AIR, AIR, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 1, 4, 3, 1, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 3, 4, 3, 3, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 7, 4, 3, 7, false, random, StrongholdGenerator.field_15263);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 9, 4, 3, 9, false, random, StrongholdGenerator.field_15263);

			for (int i = 1; i <= 3; i++) {
				this.addBlock(
					iWorld,
					Blocks.field_10576.method_9564().method_11657(PaneBlock.field_10905, Boolean.valueOf(true)).method_11657(PaneBlock.field_10904, Boolean.valueOf(true)),
					4,
					i,
					4,
					mutableIntBoundingBox
				);
				this.addBlock(
					iWorld,
					Blocks.field_10576
						.method_9564()
						.method_11657(PaneBlock.field_10905, Boolean.valueOf(true))
						.method_11657(PaneBlock.field_10904, Boolean.valueOf(true))
						.method_11657(PaneBlock.field_10907, Boolean.valueOf(true)),
					4,
					i,
					5,
					mutableIntBoundingBox
				);
				this.addBlock(
					iWorld,
					Blocks.field_10576.method_9564().method_11657(PaneBlock.field_10905, Boolean.valueOf(true)).method_11657(PaneBlock.field_10904, Boolean.valueOf(true)),
					4,
					i,
					6,
					mutableIntBoundingBox
				);
				this.addBlock(
					iWorld,
					Blocks.field_10576.method_9564().method_11657(PaneBlock.field_10903, Boolean.valueOf(true)).method_11657(PaneBlock.field_10907, Boolean.valueOf(true)),
					5,
					i,
					5,
					mutableIntBoundingBox
				);
				this.addBlock(
					iWorld,
					Blocks.field_10576.method_9564().method_11657(PaneBlock.field_10903, Boolean.valueOf(true)).method_11657(PaneBlock.field_10907, Boolean.valueOf(true)),
					6,
					i,
					5,
					mutableIntBoundingBox
				);
				this.addBlock(
					iWorld,
					Blocks.field_10576.method_9564().method_11657(PaneBlock.field_10903, Boolean.valueOf(true)).method_11657(PaneBlock.field_10907, Boolean.valueOf(true)),
					7,
					i,
					5,
					mutableIntBoundingBox
				);
			}

			this.addBlock(
				iWorld,
				Blocks.field_10576.method_9564().method_11657(PaneBlock.field_10905, Boolean.valueOf(true)).method_11657(PaneBlock.field_10904, Boolean.valueOf(true)),
				4,
				3,
				2,
				mutableIntBoundingBox
			);
			this.addBlock(
				iWorld,
				Blocks.field_10576.method_9564().method_11657(PaneBlock.field_10905, Boolean.valueOf(true)).method_11657(PaneBlock.field_10904, Boolean.valueOf(true)),
				4,
				3,
				8,
				mutableIntBoundingBox
			);
			BlockState blockState = Blocks.field_9973.method_9564().method_11657(DoorBlock.field_10938, Direction.WEST);
			BlockState blockState2 = Blocks.field_9973
				.method_9564()
				.method_11657(DoorBlock.field_10938, Direction.WEST)
				.method_11657(DoorBlock.field_10946, DoubleBlockHalf.field_12609);
			this.addBlock(iWorld, blockState, 4, 1, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 4, 2, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState, 4, 1, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState2, 4, 2, 8, mutableIntBoundingBox);
			return true;
		}
	}

	public static class class_3430 extends StrongholdGenerator.class_3466 {
		public class_3430(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.field_16958, i);
			this.method_14926(direction);
			this.entryDoor = this.method_14869(random);
			this.boundingBox = mutableIntBoundingBox;
		}

		public class_3430(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.field_16958, compoundTag);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			Direction direction = this.method_14934();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.method_14870((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
			} else {
				this.method_14873((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
			}
		}

		public static StrongholdGenerator.class_3430 method_16652(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.method_14667(i, j, k, -1, -1, 0, 5, 5, 5, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.class_3430(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 4, 4, true, random, StrongholdGenerator.field_15263);
			this.method_14872(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 1, 0);
			Direction direction = this.method_14934();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 1, 0, 3, 3, AIR, AIR, false);
			} else {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 1, 4, 3, 3, AIR, AIR, false);
			}

			return true;
		}
	}

	public static class class_3431 extends StrongholdGenerator.class_3437 {
		protected final int field_15280;

		public class_3431(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.field_16941, i);
			this.method_14926(direction);
			this.entryDoor = this.method_14869(random);
			this.boundingBox = mutableIntBoundingBox;
			this.field_15280 = random.nextInt(5);
		}

		public class_3431(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.field_16941, compoundTag);
			this.field_15280 = compoundTag.getInt("Type");
		}

		@Override
		protected void method_14943(CompoundTag compoundTag) {
			super.method_14943(compoundTag);
			compoundTag.putInt("Type", this.field_15280);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 4, 1);
			this.method_14870((StrongholdGenerator.Start)structurePiece, list, random, 1, 4);
			this.method_14873((StrongholdGenerator.Start)structurePiece, list, random, 1, 4);
		}

		public static StrongholdGenerator.class_3431 method_14865(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.method_14667(i, j, k, -4, -1, 0, 11, 7, 11, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.class_3431(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 10, 6, 10, true, random, StrongholdGenerator.field_15263);
			this.method_14872(iWorld, random, mutableIntBoundingBox, this.entryDoor, 4, 1, 0);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 10, 6, 3, 10, AIR, AIR, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 4, 0, 3, 6, AIR, AIR, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 10, 1, 4, 10, 3, 6, AIR, AIR, false);
			switch (this.field_15280) {
				case 0:
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), 5, 1, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), 5, 2, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), 5, 3, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10099.method_9564().method_11657(WallTorchBlock.field_11731, Direction.WEST), 4, 3, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10099.method_9564().method_11657(WallTorchBlock.field_11731, Direction.EAST), 6, 3, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10099.method_9564().method_11657(WallTorchBlock.field_11731, Direction.SOUTH), 5, 3, 4, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10099.method_9564().method_11657(WallTorchBlock.field_11731, Direction.NORTH), 5, 3, 6, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10136.method_9564(), 4, 1, 4, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10136.method_9564(), 4, 1, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10136.method_9564(), 4, 1, 6, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10136.method_9564(), 6, 1, 4, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10136.method_9564(), 6, 1, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10136.method_9564(), 6, 1, 6, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10136.method_9564(), 5, 1, 4, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10136.method_9564(), 5, 1, 6, mutableIntBoundingBox);
					break;
				case 1:
					for (int i = 0; i < 5; i++) {
						this.addBlock(iWorld, Blocks.field_10056.method_9564(), 3, 1, 3 + i, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10056.method_9564(), 7, 1, 3 + i, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10056.method_9564(), 3 + i, 1, 3, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10056.method_9564(), 3 + i, 1, 7, mutableIntBoundingBox);
					}

					this.addBlock(iWorld, Blocks.field_10056.method_9564(), 5, 1, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), 5, 2, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), 5, 3, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10382.method_9564(), 5, 4, 5, mutableIntBoundingBox);
					break;
				case 2:
					for (int i = 1; i <= 9; i++) {
						this.addBlock(iWorld, Blocks.field_10445.method_9564(), 1, 3, i, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10445.method_9564(), 9, 3, i, mutableIntBoundingBox);
					}

					for (int i = 1; i <= 9; i++) {
						this.addBlock(iWorld, Blocks.field_10445.method_9564(), i, 3, 1, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10445.method_9564(), i, 3, 9, mutableIntBoundingBox);
					}

					this.addBlock(iWorld, Blocks.field_10445.method_9564(), 5, 1, 4, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10445.method_9564(), 5, 1, 6, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10445.method_9564(), 5, 3, 4, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10445.method_9564(), 5, 3, 6, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10445.method_9564(), 4, 1, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10445.method_9564(), 6, 1, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10445.method_9564(), 4, 3, 5, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10445.method_9564(), 6, 3, 5, mutableIntBoundingBox);

					for (int i = 1; i <= 3; i++) {
						this.addBlock(iWorld, Blocks.field_10445.method_9564(), 4, i, 4, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10445.method_9564(), 6, i, 4, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10445.method_9564(), 4, i, 6, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10445.method_9564(), 6, i, 6, mutableIntBoundingBox);
					}

					this.addBlock(iWorld, Blocks.field_10336.method_9564(), 5, 3, 5, mutableIntBoundingBox);

					for (int i = 2; i <= 8; i++) {
						this.addBlock(iWorld, Blocks.field_10161.method_9564(), 2, 3, i, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10161.method_9564(), 3, 3, i, mutableIntBoundingBox);
						if (i <= 3 || i >= 7) {
							this.addBlock(iWorld, Blocks.field_10161.method_9564(), 4, 3, i, mutableIntBoundingBox);
							this.addBlock(iWorld, Blocks.field_10161.method_9564(), 5, 3, i, mutableIntBoundingBox);
							this.addBlock(iWorld, Blocks.field_10161.method_9564(), 6, 3, i, mutableIntBoundingBox);
						}

						this.addBlock(iWorld, Blocks.field_10161.method_9564(), 7, 3, i, mutableIntBoundingBox);
						this.addBlock(iWorld, Blocks.field_10161.method_9564(), 8, 3, i, mutableIntBoundingBox);
					}

					BlockState blockState = Blocks.field_9983.method_9564().method_11657(LadderBlock.field_11253, Direction.WEST);
					this.addBlock(iWorld, blockState, 9, 1, 3, mutableIntBoundingBox);
					this.addBlock(iWorld, blockState, 9, 2, 3, mutableIntBoundingBox);
					this.addBlock(iWorld, blockState, 9, 3, 3, mutableIntBoundingBox);
					this.method_14915(iWorld, mutableIntBoundingBox, random, 3, 4, 8, LootTables.field_800);
			}

			return true;
		}
	}

	static class class_3432 extends StructurePiece.class_3444 {
		private class_3432() {
		}

		@Override
		public void method_14948(Random random, int i, int j, int k, boolean bl) {
			if (bl) {
				float f = random.nextFloat();
				if (f < 0.2F) {
					this.block = Blocks.field_10416.method_9564();
				} else if (f < 0.5F) {
					this.block = Blocks.field_10065.method_9564();
				} else if (f < 0.55F) {
					this.block = Blocks.field_10387.method_9564();
				} else {
					this.block = Blocks.field_10056.method_9564();
				}
			} else {
				this.block = Blocks.field_10543.method_9564();
			}
		}
	}

	public static class class_3433 extends StrongholdGenerator.class_3437 {
		private final boolean field_15281;

		public class_3433(StructurePieceType structurePieceType, int i, Random random, int j, int k) {
			super(structurePieceType, i);
			this.field_15281 = true;
			this.method_14926(Direction.Type.HORIZONTAL.random(random));
			this.entryDoor = StrongholdGenerator.class_3437.class_3438.field_15288;
			if (this.method_14934().getAxis() == Direction.Axis.Z) {
				this.boundingBox = new MutableIntBoundingBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
			} else {
				this.boundingBox = new MutableIntBoundingBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
			}
		}

		public class_3433(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.field_16904, i);
			this.field_15281 = false;
			this.method_14926(direction);
			this.entryDoor = this.method_14869(random);
			this.boundingBox = mutableIntBoundingBox;
		}

		public class_3433(StructurePieceType structurePieceType, CompoundTag compoundTag) {
			super(structurePieceType, compoundTag);
			this.field_15281 = compoundTag.getBoolean("Source");
		}

		public class_3433(StructureManager structureManager, CompoundTag compoundTag) {
			this(StructurePieceType.field_16904, compoundTag);
		}

		@Override
		protected void method_14943(CompoundTag compoundTag) {
			super.method_14943(compoundTag);
			compoundTag.putBoolean("Source", this.field_15281);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			if (this.field_15281) {
				StrongholdGenerator.field_15266 = StrongholdGenerator.FiveWayCrossing.class;
			}

			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
		}

		public static StrongholdGenerator.class_3433 method_14866(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.method_14667(i, j, k, -1, -7, 0, 5, 11, 5, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.class_3433(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 10, 4, true, random, StrongholdGenerator.field_15263);
			this.method_14872(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 7, 0);
			this.method_14872(iWorld, random, mutableIntBoundingBox, StrongholdGenerator.class_3437.class_3438.field_15288, 1, 1, 4);
			this.addBlock(iWorld, Blocks.field_10056.method_9564(), 2, 6, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.method_9564(), 1, 5, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10136.method_9564(), 1, 6, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.method_9564(), 1, 5, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.method_9564(), 1, 4, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10136.method_9564(), 1, 5, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.method_9564(), 2, 4, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.method_9564(), 3, 3, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10136.method_9564(), 3, 4, 3, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.method_9564(), 3, 3, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.method_9564(), 3, 2, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10136.method_9564(), 3, 3, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.method_9564(), 2, 2, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.method_9564(), 1, 1, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10136.method_9564(), 1, 2, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10056.method_9564(), 1, 1, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10136.method_9564(), 1, 1, 3, mutableIntBoundingBox);
			return true;
		}
	}

	public static class class_3435 extends StrongholdGenerator.class_3437 {
		private final boolean field_15286;
		private final boolean field_15285;

		public class_3435(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.field_16934, i);
			this.method_14926(direction);
			this.entryDoor = this.method_14869(random);
			this.boundingBox = mutableIntBoundingBox;
			this.field_15286 = random.nextInt(2) == 0;
			this.field_15285 = random.nextInt(2) == 0;
		}

		public class_3435(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.field_16934, compoundTag);
			this.field_15286 = compoundTag.getBoolean("Left");
			this.field_15285 = compoundTag.getBoolean("Right");
		}

		@Override
		protected void method_14943(CompoundTag compoundTag) {
			super.method_14943(compoundTag);
			compoundTag.putBoolean("Left", this.field_15286);
			compoundTag.putBoolean("Right", this.field_15285);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
			if (this.field_15286) {
				this.method_14870((StrongholdGenerator.Start)structurePiece, list, random, 1, 2);
			}

			if (this.field_15285) {
				this.method_14873((StrongholdGenerator.Start)structurePiece, list, random, 1, 2);
			}
		}

		public static StrongholdGenerator.class_3435 method_14867(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.method_14667(i, j, k, -1, -1, 0, 5, 5, 7, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.class_3435(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 4, 6, true, random, StrongholdGenerator.field_15263);
			this.method_14872(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 1, 0);
			this.method_14872(iWorld, random, mutableIntBoundingBox, StrongholdGenerator.class_3437.class_3438.field_15288, 1, 1, 6);
			BlockState blockState = Blocks.field_10099.method_9564().method_11657(WallTorchBlock.field_11731, Direction.EAST);
			BlockState blockState2 = Blocks.field_10099.method_9564().method_11657(WallTorchBlock.field_11731, Direction.WEST);
			this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, 0.1F, 1, 2, 1, blockState);
			this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, 0.1F, 3, 2, 1, blockState2);
			this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, 0.1F, 1, 2, 5, blockState);
			this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, 0.1F, 3, 2, 5, blockState2);
			if (this.field_15286) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 2, 0, 3, 4, AIR, AIR, false);
			}

			if (this.field_15285) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 2, 4, 3, 4, AIR, AIR, false);
			}

			return true;
		}
	}

	public static class class_3436 extends StrongholdGenerator.class_3437 {
		public class_3436(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePieceType.field_16949, i);
			this.method_14926(direction);
			this.entryDoor = this.method_14869(random);
			this.boundingBox = mutableIntBoundingBox;
		}

		public class_3436(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.field_16949, compoundTag);
		}

		@Override
		public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			this.method_14874((StrongholdGenerator.Start)structurePiece, list, random, 1, 1);
		}

		public static StrongholdGenerator.class_3436 method_14868(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.method_14667(i, j, k, -1, -7, 0, 5, 11, 8, direction);
			return method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null
				? new StrongholdGenerator.class_3436(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 10, 7, true, random, StrongholdGenerator.field_15263);
			this.method_14872(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 7, 0);
			this.method_14872(iWorld, random, mutableIntBoundingBox, StrongholdGenerator.class_3437.class_3438.field_15288, 1, 1, 7);
			BlockState blockState = Blocks.field_10596.method_9564().method_11657(StairsBlock.field_11571, Direction.SOUTH);

			for (int i = 0; i < 6; i++) {
				this.addBlock(iWorld, blockState, 1, 6 - i, 1 + i, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState, 2, 6 - i, 1 + i, mutableIntBoundingBox);
				this.addBlock(iWorld, blockState, 3, 6 - i, 1 + i, mutableIntBoundingBox);
				if (i < 5) {
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), 1, 5 - i, 1 + i, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), 2, 5 - i, 1 + i, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), 3, 5 - i, 1 + i, mutableIntBoundingBox);
				}
			}

			return true;
		}
	}

	abstract static class class_3437 extends StructurePiece {
		protected StrongholdGenerator.class_3437.class_3438 entryDoor = StrongholdGenerator.class_3437.class_3438.field_15288;

		protected class_3437(StructurePieceType structurePieceType, int i) {
			super(structurePieceType, i);
		}

		public class_3437(StructurePieceType structurePieceType, CompoundTag compoundTag) {
			super(structurePieceType, compoundTag);
			this.entryDoor = StrongholdGenerator.class_3437.class_3438.valueOf(compoundTag.getString("EntryDoor"));
		}

		@Override
		protected void method_14943(CompoundTag compoundTag) {
			compoundTag.putString("EntryDoor", this.entryDoor.name());
		}

		protected void method_14872(
			IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, StrongholdGenerator.class_3437.class_3438 arg, int i, int j, int k
		) {
			switch (arg) {
				case field_15288:
					this.fillWithOutline(iWorld, mutableIntBoundingBox, i, j, k, i + 3 - 1, j + 3 - 1, k, AIR, AIR, false);
					break;
				case field_15290:
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), i, j, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), i, j + 1, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), i, j + 2, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), i + 1, j + 2, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), i + 2, j + 2, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), i + 2, j + 1, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), i + 2, j, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10149.method_9564(), i + 1, j, k, mutableIntBoundingBox);
					this.addBlock(
						iWorld, Blocks.field_10149.method_9564().method_11657(DoorBlock.field_10946, DoubleBlockHalf.field_12609), i + 1, j + 1, k, mutableIntBoundingBox
					);
					break;
				case field_15289:
					this.addBlock(iWorld, Blocks.field_10543.method_9564(), i + 1, j, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10543.method_9564(), i + 1, j + 1, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10576.method_9564().method_11657(PaneBlock.field_10903, Boolean.valueOf(true)), i, j, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10576.method_9564().method_11657(PaneBlock.field_10903, Boolean.valueOf(true)), i, j + 1, k, mutableIntBoundingBox);
					this.addBlock(
						iWorld,
						Blocks.field_10576.method_9564().method_11657(PaneBlock.field_10907, Boolean.valueOf(true)).method_11657(PaneBlock.field_10903, Boolean.valueOf(true)),
						i,
						j + 2,
						k,
						mutableIntBoundingBox
					);
					this.addBlock(
						iWorld,
						Blocks.field_10576.method_9564().method_11657(PaneBlock.field_10907, Boolean.valueOf(true)).method_11657(PaneBlock.field_10903, Boolean.valueOf(true)),
						i + 1,
						j + 2,
						k,
						mutableIntBoundingBox
					);
					this.addBlock(
						iWorld,
						Blocks.field_10576.method_9564().method_11657(PaneBlock.field_10907, Boolean.valueOf(true)).method_11657(PaneBlock.field_10903, Boolean.valueOf(true)),
						i + 2,
						j + 2,
						k,
						mutableIntBoundingBox
					);
					this.addBlock(iWorld, Blocks.field_10576.method_9564().method_11657(PaneBlock.field_10907, Boolean.valueOf(true)), i + 2, j + 1, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10576.method_9564().method_11657(PaneBlock.field_10907, Boolean.valueOf(true)), i + 2, j, k, mutableIntBoundingBox);
					break;
				case field_15291:
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), i, j, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), i, j + 1, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), i, j + 2, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), i + 1, j + 2, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), i + 2, j + 2, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), i + 2, j + 1, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_10056.method_9564(), i + 2, j, k, mutableIntBoundingBox);
					this.addBlock(iWorld, Blocks.field_9973.method_9564(), i + 1, j, k, mutableIntBoundingBox);
					this.addBlock(
						iWorld, Blocks.field_9973.method_9564().method_11657(DoorBlock.field_10946, DoubleBlockHalf.field_12609), i + 1, j + 1, k, mutableIntBoundingBox
					);
					this.addBlock(
						iWorld, Blocks.field_10494.method_9564().method_11657(AbstractButtonBlock.field_11177, Direction.NORTH), i + 2, j + 1, k + 1, mutableIntBoundingBox
					);
					this.addBlock(
						iWorld, Blocks.field_10494.method_9564().method_11657(AbstractButtonBlock.field_11177, Direction.SOUTH), i + 2, j + 1, k - 1, mutableIntBoundingBox
					);
			}
		}

		protected StrongholdGenerator.class_3437.class_3438 method_14869(Random random) {
			int i = random.nextInt(5);
			switch (i) {
				case 0:
				case 1:
				default:
					return StrongholdGenerator.class_3437.class_3438.field_15288;
				case 2:
					return StrongholdGenerator.class_3437.class_3438.field_15290;
				case 3:
					return StrongholdGenerator.class_3437.class_3438.field_15289;
				case 4:
					return StrongholdGenerator.class_3437.class_3438.field_15291;
			}
		}

		@Nullable
		protected StructurePiece method_14874(StrongholdGenerator.Start start, List<StructurePiece> list, Random random, int i, int j) {
			Direction direction = this.method_14934();
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
			Direction direction = this.method_14934();
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
			Direction direction = this.method_14934();
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

		public static enum class_3438 {
			field_15288,
			field_15290,
			field_15289,
			field_15291;
		}
	}

	public abstract static class class_3466 extends StrongholdGenerator.class_3437 {
		protected class_3466(StructurePieceType structurePieceType, int i) {
			super(structurePieceType, i);
		}

		public class_3466(StructurePieceType structurePieceType, CompoundTag compoundTag) {
			super(structurePieceType, compoundTag);
		}
	}
}
