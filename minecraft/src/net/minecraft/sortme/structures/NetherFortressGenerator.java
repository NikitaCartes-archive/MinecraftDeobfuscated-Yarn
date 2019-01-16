package net.minecraft.sortme.structures;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3443;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.StructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.loot.LootTables;

public class NetherFortressGenerator {
	private static final NetherFortressGenerator.class_3404[] field_14494 = new NetherFortressGenerator.class_3404[]{
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.class_3393.class, 30, 0, true),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.class_3391.class, 10, 4),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.class_3405.class, 10, 4),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.class_3406.class, 10, 3),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.class_3402.class, 5, 2),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.class_3396.class, 5, 1)
	};
	private static final NetherFortressGenerator.class_3404[] field_14493 = new NetherFortressGenerator.class_3404[]{
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.class_3399.class, 25, 0, true),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.class_3397.class, 15, 5),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.class_3400.class, 5, 10),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.class_3398.class, 5, 10),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.class_3394.class, 10, 3, true),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.class_3395.class, 7, 2),
		new NetherFortressGenerator.class_3404(NetherFortressGenerator.class_3401.class, 5, 2)
	};

	private static NetherFortressGenerator.class_3403 method_14795(
		NetherFortressGenerator.class_3404 arg, List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l
	) {
		Class<? extends NetherFortressGenerator.class_3403> class_ = arg.field_14501;
		NetherFortressGenerator.class_3403 lv = null;
		if (class_ == NetherFortressGenerator.class_3393.class) {
			lv = NetherFortressGenerator.class_3393.method_14798(list, random, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.class_3391.class) {
			lv = NetherFortressGenerator.class_3391.method_14796(list, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.class_3405.class) {
			lv = NetherFortressGenerator.class_3405.method_14817(list, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.class_3406.class) {
			lv = NetherFortressGenerator.class_3406.method_14818(list, i, j, k, l, direction);
		} else if (class_ == NetherFortressGenerator.class_3402.class) {
			lv = NetherFortressGenerator.class_3402.method_14807(list, i, j, k, l, direction);
		} else if (class_ == NetherFortressGenerator.class_3396.class) {
			lv = NetherFortressGenerator.class_3396.method_14801(list, random, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.class_3399.class) {
			lv = NetherFortressGenerator.class_3399.method_14804(list, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.class_3400.class) {
			lv = NetherFortressGenerator.class_3400.method_14805(list, random, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.class_3398.class) {
			lv = NetherFortressGenerator.class_3398.method_14803(list, random, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.class_3394.class) {
			lv = NetherFortressGenerator.class_3394.method_14799(list, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.class_3395.class) {
			lv = NetherFortressGenerator.class_3395.method_14800(list, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.class_3397.class) {
			lv = NetherFortressGenerator.class_3397.method_14802(list, i, j, k, direction, l);
		} else if (class_ == NetherFortressGenerator.class_3401.class) {
			lv = NetherFortressGenerator.class_3401.method_14806(list, i, j, k, direction, l);
		}

		return lv;
	}

	public static class class_3391 extends NetherFortressGenerator.class_3403 {
		public class_3391(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16926, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
		}

		protected class_3391(Random random, int i, int j) {
			super(StructurePiece.field_16926, 0);
			this.method_14926(Direction.class_2353.HORIZONTAL.random(random));
			if (this.getFacing().getAxis() == Direction.Axis.Z) {
				this.structureBounds = new MutableIntBoundingBox(i, 64, j, i + 19 - 1, 73, j + 19 - 1);
			} else {
				this.structureBounds = new MutableIntBoundingBox(i, 64, j, i + 19 - 1, 73, j + 19 - 1);
			}
		}

		protected class_3391(StructurePiece structurePiece, CompoundTag compoundTag) {
			super(structurePiece, compoundTag);
		}

		public class_3391(StructureManager structureManager, CompoundTag compoundTag) {
			this(StructurePiece.field_16926, compoundTag);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14814((NetherFortressGenerator.class_3407)arg, list, random, 8, 3, false);
			this.method_14812((NetherFortressGenerator.class_3407)arg, list, random, 3, 8, false);
			this.method_14808((NetherFortressGenerator.class_3407)arg, list, random, 3, 8, false);
		}

		public static NetherFortressGenerator.class_3391 method_14796(List<class_3443> list, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -8, -3, 0, 19, 10, 19, direction);
			return method_14809(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.class_3391(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
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

	public static class class_3392 extends NetherFortressGenerator.class_3403 {
		private final int field_14495;

		public class_3392(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16903, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
			this.field_14495 = random.nextInt();
		}

		public class_3392(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16903, compoundTag);
			this.field_14495 = compoundTag.getInt("Seed");
		}

		public static NetherFortressGenerator.class_3392 method_14797(List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -3, 0, 5, 10, 8, direction);
			return method_14809(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.class_3392(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putInt("Seed", this.field_14495);
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			Random random2 = new Random((long)this.field_14495);

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

	public static class class_3393 extends NetherFortressGenerator.class_3403 {
		public class_3393(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16917, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
		}

		public class_3393(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16917, compoundTag);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14814((NetherFortressGenerator.class_3407)arg, list, random, 1, 3, false);
		}

		public static NetherFortressGenerator.class_3393 method_14798(List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -3, 0, 5, 10, 19, direction);
			return method_14809(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.class_3393(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
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

	public static class class_3394 extends NetherFortressGenerator.class_3403 {
		public class_3394(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16930, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
		}

		public class_3394(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16930, compoundTag);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14814((NetherFortressGenerator.class_3407)arg, list, random, 1, 0, true);
		}

		public static NetherFortressGenerator.class_3394 method_14799(List<class_3443> list, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -7, 0, 5, 14, 10, direction);
			return method_14809(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.class_3394(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			BlockState blockState = Blocks.field_10159.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
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

	public static class class_3395 extends NetherFortressGenerator.class_3403 {
		public class_3395(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16943, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
		}

		public class_3395(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16943, compoundTag);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			int i = 1;
			Direction direction = this.getFacing();
			if (direction == Direction.WEST || direction == Direction.NORTH) {
				i = 5;
			}

			this.method_14812((NetherFortressGenerator.class_3407)arg, list, random, 0, i, random.nextInt(8) > 0);
			this.method_14808((NetherFortressGenerator.class_3407)arg, list, random, 0, i, random.nextInt(8) > 0);
		}

		public static NetherFortressGenerator.class_3395 method_14800(List<class_3443> list, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -3, 0, 0, 9, 7, 9, direction);
			return method_14809(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.class_3395(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
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

	public static class class_3396 extends NetherFortressGenerator.class_3403 {
		public class_3396(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16952, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
		}

		public class_3396(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16952, compoundTag);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14814((NetherFortressGenerator.class_3407)arg, list, random, 5, 3, true);
		}

		public static NetherFortressGenerator.class_3396 method_14801(List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -5, -3, 0, 13, 14, 13, direction);
			return method_14809(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.class_3396(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
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

	public static class class_3397 extends NetherFortressGenerator.class_3403 {
		public class_3397(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16929, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
		}

		public class_3397(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16929, compoundTag);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14814((NetherFortressGenerator.class_3407)arg, list, random, 1, 0, true);
			this.method_14812((NetherFortressGenerator.class_3407)arg, list, random, 0, 1, true);
			this.method_14808((NetherFortressGenerator.class_3407)arg, list, random, 0, 1, true);
		}

		public static NetherFortressGenerator.class_3397 method_14802(List<class_3443> list, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, 0, 0, 5, 7, 5, direction);
			return method_14809(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.class_3397(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
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

	public static class class_3398 extends NetherFortressGenerator.class_3403 {
		private boolean field_14496;

		public class_3398(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16962, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
			this.field_14496 = random.nextInt(3) == 0;
		}

		public class_3398(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16962, compoundTag);
			this.field_14496 = compoundTag.getBoolean("Chest");
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putBoolean("Chest", this.field_14496);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14812((NetherFortressGenerator.class_3407)arg, list, random, 0, 1, true);
		}

		public static NetherFortressGenerator.class_3398 method_14803(List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, 0, 0, 5, 7, 5, direction);
			return method_14809(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.class_3398(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
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
			if (this.field_14496 && mutableIntBoundingBox.contains(new BlockPos(this.applyXTransform(3, 3), this.applyYTransform(2), this.applyZTransform(3, 3)))) {
				this.field_14496 = false;
				this.method_14915(iWorld, mutableIntBoundingBox, random, 3, 2, 3, LootTables.CHEST_NETHER_BRIDGE);
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

	public static class class_3399 extends NetherFortressGenerator.class_3403 {
		public class_3399(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16921, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
		}

		public class_3399(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16921, compoundTag);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14814((NetherFortressGenerator.class_3407)arg, list, random, 1, 0, true);
		}

		public static NetherFortressGenerator.class_3399 method_14804(List<class_3443> list, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, 0, 0, 5, 7, 5, direction);
			return method_14809(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.class_3399(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
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

	public static class class_3400 extends NetherFortressGenerator.class_3403 {
		private boolean field_14497;

		public class_3400(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16945, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
			this.field_14497 = random.nextInt(3) == 0;
		}

		public class_3400(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16945, compoundTag);
			this.field_14497 = compoundTag.getBoolean("Chest");
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putBoolean("Chest", this.field_14497);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14808((NetherFortressGenerator.class_3407)arg, list, random, 0, 1, true);
		}

		public static NetherFortressGenerator.class_3400 method_14805(List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, 0, 0, 5, 7, 5, direction);
			return method_14809(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.class_3400(l, random, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
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
			if (this.field_14497 && mutableIntBoundingBox.contains(new BlockPos(this.applyXTransform(1, 3), this.applyYTransform(2), this.applyZTransform(1, 3)))) {
				this.field_14497 = false;
				this.method_14915(iWorld, mutableIntBoundingBox, random, 1, 2, 3, LootTables.CHEST_NETHER_BRIDGE);
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

	public static class class_3401 extends NetherFortressGenerator.class_3403 {
		public class_3401(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16961, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
		}

		public class_3401(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16961, compoundTag);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14814((NetherFortressGenerator.class_3407)arg, list, random, 5, 3, true);
			this.method_14814((NetherFortressGenerator.class_3407)arg, list, random, 5, 11, true);
		}

		public static NetherFortressGenerator.class_3401 method_14806(List<class_3443> list, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -5, -3, 0, 13, 14, 13, direction);
			return method_14809(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.class_3401(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
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

			BlockState blockState5 = Blocks.field_10159.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);

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
			BlockState blockState6 = blockState5.with(StairsBlock.FACING, Direction.EAST);
			BlockState blockState7 = blockState5.with(StairsBlock.FACING, Direction.WEST);
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

	public static class class_3402 extends NetherFortressGenerator.class_3403 {
		private boolean field_14498;

		public class_3402(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16931, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
		}

		public class_3402(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16931, compoundTag);
			this.field_14498 = compoundTag.getBoolean("Mob");
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putBoolean("Mob", this.field_14498);
		}

		public static NetherFortressGenerator.class_3402 method_14807(List<class_3443> list, int i, int j, int k, int l, Direction direction) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -2, 0, 0, 7, 8, 9, direction);
			return method_14809(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.class_3402(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
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
			if (!this.field_14498) {
				BlockPos blockPos = new BlockPos(this.applyXTransform(3, 5), this.applyYTransform(5), this.applyZTransform(3, 5));
				if (mutableIntBoundingBox.contains(blockPos)) {
					this.field_14498 = true;
					iWorld.setBlockState(blockPos, Blocks.field_10260.getDefaultState(), 2);
					BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
					if (blockEntity instanceof MobSpawnerBlockEntity) {
						((MobSpawnerBlockEntity)blockEntity).getLogic().method_8274(EntityType.BLAZE);
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

	abstract static class class_3403 extends class_3443 {
		protected class_3403(StructurePiece structurePiece, int i) {
			super(structurePiece, i);
		}

		public class_3403(StructurePiece structurePiece, CompoundTag compoundTag) {
			super(structurePiece, compoundTag);
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

		private NetherFortressGenerator.class_3403 method_14811(
			NetherFortressGenerator.class_3407 arg,
			List<NetherFortressGenerator.class_3404> list,
			List<class_3443> list2,
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
						if (!lv.method_14816(l) || lv == arg.field_14506 && !lv.field_14500) {
							break;
						}

						NetherFortressGenerator.class_3403 lv2 = NetherFortressGenerator.method_14795(lv, list2, random, i, j, k, direction, l);
						if (lv2 != null) {
							lv.field_14502++;
							arg.field_14506 = lv;
							if (!lv.method_14815()) {
								list.remove(lv);
							}

							return lv2;
						}
					}
				}
			}

			return NetherFortressGenerator.class_3392.method_14797(list2, random, i, j, k, direction, l);
		}

		private class_3443 method_14813(
			NetherFortressGenerator.class_3407 arg, List<class_3443> list, Random random, int i, int j, int k, @Nullable Direction direction, int l, boolean bl
		) {
			if (Math.abs(i - arg.method_14935().minX) <= 112 && Math.abs(k - arg.method_14935().minZ) <= 112) {
				List<NetherFortressGenerator.class_3404> list2 = arg.field_14507;
				if (bl) {
					list2 = arg.field_14504;
				}

				class_3443 lv = this.method_14811(arg, list2, list, random, i, j, k, direction, l + 1);
				if (lv != null) {
					list.add(lv);
					arg.field_14505.add(lv);
				}

				return lv;
			} else {
				return NetherFortressGenerator.class_3392.method_14797(list, random, i, j, k, direction, l);
			}
		}

		@Nullable
		protected class_3443 method_14814(NetherFortressGenerator.class_3407 arg, List<class_3443> list, Random random, int i, int j, boolean bl) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return this.method_14813(
							arg, list, random, this.structureBounds.minX + i, this.structureBounds.minY + j, this.structureBounds.minZ - 1, direction, this.method_14923(), bl
						);
					case SOUTH:
						return this.method_14813(
							arg, list, random, this.structureBounds.minX + i, this.structureBounds.minY + j, this.structureBounds.maxZ + 1, direction, this.method_14923(), bl
						);
					case WEST:
						return this.method_14813(
							arg, list, random, this.structureBounds.minX - 1, this.structureBounds.minY + j, this.structureBounds.minZ + i, direction, this.method_14923(), bl
						);
					case EAST:
						return this.method_14813(
							arg, list, random, this.structureBounds.maxX + 1, this.structureBounds.minY + j, this.structureBounds.minZ + i, direction, this.method_14923(), bl
						);
				}
			}

			return null;
		}

		@Nullable
		protected class_3443 method_14812(NetherFortressGenerator.class_3407 arg, List<class_3443> list, Random random, int i, int j, boolean bl) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return this.method_14813(
							arg, list, random, this.structureBounds.minX - 1, this.structureBounds.minY + i, this.structureBounds.minZ + j, Direction.WEST, this.method_14923(), bl
						);
					case SOUTH:
						return this.method_14813(
							arg, list, random, this.structureBounds.minX - 1, this.structureBounds.minY + i, this.structureBounds.minZ + j, Direction.WEST, this.method_14923(), bl
						);
					case WEST:
						return this.method_14813(
							arg, list, random, this.structureBounds.minX + j, this.structureBounds.minY + i, this.structureBounds.minZ - 1, Direction.NORTH, this.method_14923(), bl
						);
					case EAST:
						return this.method_14813(
							arg, list, random, this.structureBounds.minX + j, this.structureBounds.minY + i, this.structureBounds.minZ - 1, Direction.NORTH, this.method_14923(), bl
						);
				}
			}

			return null;
		}

		@Nullable
		protected class_3443 method_14808(NetherFortressGenerator.class_3407 arg, List<class_3443> list, Random random, int i, int j, boolean bl) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return this.method_14813(
							arg, list, random, this.structureBounds.maxX + 1, this.structureBounds.minY + i, this.structureBounds.minZ + j, Direction.EAST, this.method_14923(), bl
						);
					case SOUTH:
						return this.method_14813(
							arg, list, random, this.structureBounds.maxX + 1, this.structureBounds.minY + i, this.structureBounds.minZ + j, Direction.EAST, this.method_14923(), bl
						);
					case WEST:
						return this.method_14813(
							arg, list, random, this.structureBounds.minX + j, this.structureBounds.minY + i, this.structureBounds.maxZ + 1, Direction.SOUTH, this.method_14923(), bl
						);
					case EAST:
						return this.method_14813(
							arg, list, random, this.structureBounds.minX + j, this.structureBounds.minY + i, this.structureBounds.maxZ + 1, Direction.SOUTH, this.method_14923(), bl
						);
				}
			}

			return null;
		}

		protected static boolean method_14809(MutableIntBoundingBox mutableIntBoundingBox) {
			return mutableIntBoundingBox != null && mutableIntBoundingBox.minY > 10;
		}
	}

	static class class_3404 {
		public final Class<? extends NetherFortressGenerator.class_3403> field_14501;
		public final int field_14503;
		public int field_14502;
		public final int field_14499;
		public final boolean field_14500;

		public class_3404(Class<? extends NetherFortressGenerator.class_3403> class_, int i, int j, boolean bl) {
			this.field_14501 = class_;
			this.field_14503 = i;
			this.field_14499 = j;
			this.field_14500 = bl;
		}

		public class_3404(Class<? extends NetherFortressGenerator.class_3403> class_, int i, int j) {
			this(class_, i, j, false);
		}

		public boolean method_14816(int i) {
			return this.field_14499 == 0 || this.field_14502 < this.field_14499;
		}

		public boolean method_14815() {
			return this.field_14499 == 0 || this.field_14502 < this.field_14499;
		}
	}

	public static class class_3405 extends NetherFortressGenerator.class_3403 {
		public class_3405(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16908, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
		}

		public class_3405(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16908, compoundTag);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14814((NetherFortressGenerator.class_3407)arg, list, random, 2, 0, false);
			this.method_14812((NetherFortressGenerator.class_3407)arg, list, random, 0, 2, false);
			this.method_14808((NetherFortressGenerator.class_3407)arg, list, random, 0, 2, false);
		}

		public static NetherFortressGenerator.class_3405 method_14817(List<class_3443> list, int i, int j, int k, Direction direction, int l) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -2, 0, 0, 7, 9, 7, direction);
			return method_14809(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.class_3405(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
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

	public static class class_3406 extends NetherFortressGenerator.class_3403 {
		public class_3406(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
			super(StructurePiece.field_16967, i);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
		}

		public class_3406(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16967, compoundTag);
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			this.method_14808((NetherFortressGenerator.class_3407)arg, list, random, 6, 2, false);
		}

		public static NetherFortressGenerator.class_3406 method_14818(List<class_3443> list, int i, int j, int k, int l, Direction direction) {
			MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -2, 0, 0, 7, 11, 7, direction);
			return method_14809(mutableIntBoundingBox) && class_3443.method_14932(list, mutableIntBoundingBox) == null
				? new NetherFortressGenerator.class_3406(l, mutableIntBoundingBox, direction)
				: null;
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
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

	public static class class_3407 extends NetherFortressGenerator.class_3391 {
		public NetherFortressGenerator.class_3404 field_14506;
		public List<NetherFortressGenerator.class_3404> field_14507;
		public List<NetherFortressGenerator.class_3404> field_14504;
		public final List<class_3443> field_14505 = Lists.<class_3443>newArrayList();

		public class_3407(Random random, int i, int j) {
			super(random, i, j);
			this.field_14507 = Lists.<NetherFortressGenerator.class_3404>newArrayList();

			for (NetherFortressGenerator.class_3404 lv : NetherFortressGenerator.field_14494) {
				lv.field_14502 = 0;
				this.field_14507.add(lv);
			}

			this.field_14504 = Lists.<NetherFortressGenerator.class_3404>newArrayList();

			for (NetherFortressGenerator.class_3404 lv : NetherFortressGenerator.field_14493) {
				lv.field_14502 = 0;
				this.field_14504.add(lv);
			}
		}

		public class_3407(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16924, compoundTag);
		}
	}
}
