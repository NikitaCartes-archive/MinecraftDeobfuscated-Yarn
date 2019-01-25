package net.minecraft.structure.generator;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Mirror;
import net.minecraft.util.Pair;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.loot.LootTables;

public class WoodlandMansionGenerator {
	public static void method_15029(
		StructureManager structureManager, BlockPos blockPos, Rotation rotation, List<WoodlandMansionGenerator.Piece> list, Random random
	) {
		WoodlandMansionGenerator.class_3474 lv = new WoodlandMansionGenerator.class_3474(random);
		WoodlandMansionGenerator.class_3475 lv2 = new WoodlandMansionGenerator.class_3475(structureManager, random);
		lv2.method_15050(blockPos, rotation, list, lv);
	}

	public static class Piece extends SimpleStructurePiece {
		private final String template;
		private final Rotation rotation;
		private final Mirror mirror;

		public Piece(StructureManager structureManager, String string, BlockPos blockPos, Rotation rotation) {
			this(structureManager, string, blockPos, rotation, Mirror.NONE);
		}

		public Piece(StructureManager structureManager, String string, BlockPos blockPos, Rotation rotation, Mirror mirror) {
			super(StructurePieceType.WOODLAND_MANSION, 0);
			this.template = string;
			this.pos = blockPos;
			this.rotation = rotation;
			this.mirror = mirror;
			this.method_15068(structureManager);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.WOODLAND_MANSION, compoundTag);
			this.template = compoundTag.getString("Template");
			this.rotation = Rotation.valueOf(compoundTag.getString("Rot"));
			this.mirror = Mirror.valueOf(compoundTag.getString("Mi"));
			this.method_15068(structureManager);
		}

		private void method_15068(StructureManager structureManager) {
			Structure structure = structureManager.getStructureOrBlank(new Identifier("woodland_mansion/" + this.template));
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.setIgnoreEntities(true)
				.setRotation(this.rotation)
				.setMirrored(this.mirror)
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
			this.setStructureData(structure, this.pos, structurePlacementData);
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putString("Template", this.template);
			compoundTag.putString("Rot", this.placementData.getRotation().name());
			compoundTag.putString("Mi", this.placementData.getMirror().name());
		}

		@Override
		protected void handleMetadata(String string, BlockPos blockPos, IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox) {
			if (string.startsWith("Chest")) {
				Rotation rotation = this.placementData.getRotation();
				BlockState blockState = Blocks.field_10034.getDefaultState();
				if ("ChestWest".equals(string)) {
					blockState = blockState.with(ChestBlock.FACING, rotation.rotate(Direction.WEST));
				} else if ("ChestEast".equals(string)) {
					blockState = blockState.with(ChestBlock.FACING, rotation.rotate(Direction.EAST));
				} else if ("ChestSouth".equals(string)) {
					blockState = blockState.with(ChestBlock.FACING, rotation.rotate(Direction.SOUTH));
				} else if ("ChestNorth".equals(string)) {
					blockState = blockState.with(ChestBlock.FACING, rotation.rotate(Direction.NORTH));
				}

				this.addChest(iWorld, mutableIntBoundingBox, random, blockPos, LootTables.CHEST_WOODLAND_MANSION, blockState);
			} else if ("Mage".equals(string)) {
				EvokerEntity evokerEntity = new EvokerEntity(iWorld.getWorld());
				evokerEntity.setPersistent();
				evokerEntity.setPositionAndAngles(blockPos, 0.0F, 0.0F);
				iWorld.spawnEntity(evokerEntity);
				iWorld.setBlockState(blockPos, Blocks.field_10124.getDefaultState(), 2);
			} else if ("Warrior".equals(string)) {
				VindicatorEntity vindicatorEntity = new VindicatorEntity(iWorld.getWorld());
				vindicatorEntity.setPersistent();
				vindicatorEntity.setPositionAndAngles(blockPos, 0.0F, 0.0F);
				vindicatorEntity.prepareEntityData(iWorld, iWorld.getLocalDifficulty(new BlockPos(vindicatorEntity)), SpawnType.field_16474, null, null);
				iWorld.spawnEntity(vindicatorEntity);
				iWorld.setBlockState(blockPos, Blocks.field_10124.getDefaultState(), 2);
			}
		}
	}

	static class class_3472 extends WoodlandMansionGenerator.class_3473 {
		private class_3472() {
		}

		@Override
		public String method_15037(Random random) {
			return "1x1_a" + (random.nextInt(5) + 1);
		}

		@Override
		public String method_15032(Random random) {
			return "1x1_as" + (random.nextInt(4) + 1);
		}

		@Override
		public String method_15033(Random random, boolean bl) {
			return "1x2_a" + (random.nextInt(9) + 1);
		}

		@Override
		public String method_15031(Random random, boolean bl) {
			return "1x2_b" + (random.nextInt(5) + 1);
		}

		@Override
		public String method_15035(Random random) {
			return "1x2_s" + (random.nextInt(2) + 1);
		}

		@Override
		public String method_15034(Random random) {
			return "2x2_a" + (random.nextInt(4) + 1);
		}

		@Override
		public String method_15036(Random random) {
			return "2x2_s1";
		}
	}

	abstract static class class_3473 {
		private class_3473() {
		}

		public abstract String method_15037(Random random);

		public abstract String method_15032(Random random);

		public abstract String method_15033(Random random, boolean bl);

		public abstract String method_15031(Random random, boolean bl);

		public abstract String method_15035(Random random);

		public abstract String method_15034(Random random);

		public abstract String method_15036(Random random);
	}

	static class class_3474 {
		private final Random field_15438;
		private final WoodlandMansionGenerator.class_3478 field_15440;
		private final WoodlandMansionGenerator.class_3478 field_15439;
		private final WoodlandMansionGenerator.class_3478[] field_15443;
		private final int field_15442;
		private final int field_15441;

		public class_3474(Random random) {
			this.field_15438 = random;
			int i = 11;
			this.field_15442 = 7;
			this.field_15441 = 4;
			this.field_15440 = new WoodlandMansionGenerator.class_3478(11, 11, 5);
			this.field_15440.method_15062(this.field_15442, this.field_15441, this.field_15442 + 1, this.field_15441 + 1, 3);
			this.field_15440.method_15062(this.field_15442 - 1, this.field_15441, this.field_15442 - 1, this.field_15441 + 1, 2);
			this.field_15440.method_15062(this.field_15442 + 2, this.field_15441 - 2, this.field_15442 + 3, this.field_15441 + 3, 5);
			this.field_15440.method_15062(this.field_15442 + 1, this.field_15441 - 2, this.field_15442 + 1, this.field_15441 - 1, 1);
			this.field_15440.method_15062(this.field_15442 + 1, this.field_15441 + 2, this.field_15442 + 1, this.field_15441 + 3, 1);
			this.field_15440.method_15065(this.field_15442 - 1, this.field_15441 - 1, 1);
			this.field_15440.method_15065(this.field_15442 - 1, this.field_15441 + 2, 1);
			this.field_15440.method_15062(0, 0, 11, 1, 5);
			this.field_15440.method_15062(0, 9, 11, 11, 5);
			this.method_15045(this.field_15440, this.field_15442, this.field_15441 - 2, Direction.WEST, 6);
			this.method_15045(this.field_15440, this.field_15442, this.field_15441 + 3, Direction.WEST, 6);
			this.method_15045(this.field_15440, this.field_15442 - 2, this.field_15441 - 1, Direction.WEST, 3);
			this.method_15045(this.field_15440, this.field_15442 - 2, this.field_15441 + 2, Direction.WEST, 3);

			while (this.method_15046(this.field_15440)) {
			}

			this.field_15443 = new WoodlandMansionGenerator.class_3478[3];
			this.field_15443[0] = new WoodlandMansionGenerator.class_3478(11, 11, 5);
			this.field_15443[1] = new WoodlandMansionGenerator.class_3478(11, 11, 5);
			this.field_15443[2] = new WoodlandMansionGenerator.class_3478(11, 11, 5);
			this.method_15042(this.field_15440, this.field_15443[0]);
			this.method_15042(this.field_15440, this.field_15443[1]);
			this.field_15443[0].method_15062(this.field_15442 + 1, this.field_15441, this.field_15442 + 1, this.field_15441 + 1, 8388608);
			this.field_15443[1].method_15062(this.field_15442 + 1, this.field_15441, this.field_15442 + 1, this.field_15441 + 1, 8388608);
			this.field_15439 = new WoodlandMansionGenerator.class_3478(this.field_15440.field_15454, this.field_15440.field_15453, 5);
			this.method_15048();
			this.method_15042(this.field_15439, this.field_15443[2]);
		}

		public static boolean method_15047(WoodlandMansionGenerator.class_3478 arg, int i, int j) {
			int k = arg.method_15066(i, j);
			return k == 1 || k == 2 || k == 3 || k == 4;
		}

		public boolean method_15039(WoodlandMansionGenerator.class_3478 arg, int i, int j, int k, int l) {
			return (this.field_15443[k].method_15066(i, j) & 65535) == l;
		}

		@Nullable
		public Direction method_15040(WoodlandMansionGenerator.class_3478 arg, int i, int j, int k, int l) {
			for (Direction direction : Direction.Type.HORIZONTAL) {
				if (this.method_15039(arg, i + direction.getOffsetX(), j + direction.getOffsetZ(), k, l)) {
					return direction;
				}
			}

			return null;
		}

		private void method_15045(WoodlandMansionGenerator.class_3478 arg, int i, int j, Direction direction, int k) {
			if (k > 0) {
				arg.method_15065(i, j, 1);
				arg.method_15061(i + direction.getOffsetX(), j + direction.getOffsetZ(), 0, 1);

				for (int l = 0; l < 8; l++) {
					Direction direction2 = Direction.fromHorizontal(this.field_15438.nextInt(4));
					if (direction2 != direction.getOpposite() && (direction2 != Direction.EAST || !this.field_15438.nextBoolean())) {
						int m = i + direction.getOffsetX();
						int n = j + direction.getOffsetZ();
						if (arg.method_15066(m + direction2.getOffsetX(), n + direction2.getOffsetZ()) == 0
							&& arg.method_15066(m + direction2.getOffsetX() * 2, n + direction2.getOffsetZ() * 2) == 0) {
							this.method_15045(arg, i + direction.getOffsetX() + direction2.getOffsetX(), j + direction.getOffsetZ() + direction2.getOffsetZ(), direction2, k - 1);
							break;
						}
					}
				}

				Direction direction3 = direction.rotateYClockwise();
				Direction direction2 = direction.rotateYCounterclockwise();
				arg.method_15061(i + direction3.getOffsetX(), j + direction3.getOffsetZ(), 0, 2);
				arg.method_15061(i + direction2.getOffsetX(), j + direction2.getOffsetZ(), 0, 2);
				arg.method_15061(i + direction.getOffsetX() + direction3.getOffsetX(), j + direction.getOffsetZ() + direction3.getOffsetZ(), 0, 2);
				arg.method_15061(i + direction.getOffsetX() + direction2.getOffsetX(), j + direction.getOffsetZ() + direction2.getOffsetZ(), 0, 2);
				arg.method_15061(i + direction.getOffsetX() * 2, j + direction.getOffsetZ() * 2, 0, 2);
				arg.method_15061(i + direction3.getOffsetX() * 2, j + direction3.getOffsetZ() * 2, 0, 2);
				arg.method_15061(i + direction2.getOffsetX() * 2, j + direction2.getOffsetZ() * 2, 0, 2);
			}
		}

		private boolean method_15046(WoodlandMansionGenerator.class_3478 arg) {
			boolean bl = false;

			for (int i = 0; i < arg.field_15453; i++) {
				for (int j = 0; j < arg.field_15454; j++) {
					if (arg.method_15066(j, i) == 0) {
						int k = 0;
						k += method_15047(arg, j + 1, i) ? 1 : 0;
						k += method_15047(arg, j - 1, i) ? 1 : 0;
						k += method_15047(arg, j, i + 1) ? 1 : 0;
						k += method_15047(arg, j, i - 1) ? 1 : 0;
						if (k >= 3) {
							arg.method_15065(j, i, 2);
							bl = true;
						} else if (k == 2) {
							int l = 0;
							l += method_15047(arg, j + 1, i + 1) ? 1 : 0;
							l += method_15047(arg, j - 1, i + 1) ? 1 : 0;
							l += method_15047(arg, j + 1, i - 1) ? 1 : 0;
							l += method_15047(arg, j - 1, i - 1) ? 1 : 0;
							if (l <= 1) {
								arg.method_15065(j, i, 2);
								bl = true;
							}
						}
					}
				}
			}

			return bl;
		}

		private void method_15048() {
			List<Pair<Integer, Integer>> list = Lists.<Pair<Integer, Integer>>newArrayList();
			WoodlandMansionGenerator.class_3478 lv = this.field_15443[1];

			for (int i = 0; i < this.field_15439.field_15453; i++) {
				for (int j = 0; j < this.field_15439.field_15454; j++) {
					int k = lv.method_15066(j, i);
					int l = k & 983040;
					if (l == 131072 && (k & 2097152) == 2097152) {
						list.add(new Pair<>(j, i));
					}
				}
			}

			if (list.isEmpty()) {
				this.field_15439.method_15062(0, 0, this.field_15439.field_15454, this.field_15439.field_15453, 5);
			} else {
				Pair<Integer, Integer> pair = (Pair<Integer, Integer>)list.get(this.field_15438.nextInt(list.size()));
				int jx = lv.method_15066(pair.getLeft(), pair.getRight());
				lv.method_15065(pair.getLeft(), pair.getRight(), jx | 4194304);
				Direction direction = this.method_15040(this.field_15440, pair.getLeft(), pair.getRight(), 1, jx & 65535);
				int l = pair.getLeft() + direction.getOffsetX();
				int m = pair.getRight() + direction.getOffsetZ();

				for (int n = 0; n < this.field_15439.field_15453; n++) {
					for (int o = 0; o < this.field_15439.field_15454; o++) {
						if (!method_15047(this.field_15440, o, n)) {
							this.field_15439.method_15065(o, n, 5);
						} else if (o == pair.getLeft() && n == pair.getRight()) {
							this.field_15439.method_15065(o, n, 3);
						} else if (o == l && n == m) {
							this.field_15439.method_15065(o, n, 3);
							this.field_15443[2].method_15065(o, n, 8388608);
						}
					}
				}

				List<Direction> list2 = Lists.<Direction>newArrayList();

				for (Direction direction2 : Direction.Type.HORIZONTAL) {
					if (this.field_15439.method_15066(l + direction2.getOffsetX(), m + direction2.getOffsetZ()) == 0) {
						list2.add(direction2);
					}
				}

				if (list2.isEmpty()) {
					this.field_15439.method_15062(0, 0, this.field_15439.field_15454, this.field_15439.field_15453, 5);
					lv.method_15065(pair.getLeft(), pair.getRight(), jx);
				} else {
					Direction direction3 = (Direction)list2.get(this.field_15438.nextInt(list2.size()));
					this.method_15045(this.field_15439, l + direction3.getOffsetX(), m + direction3.getOffsetZ(), direction3, 4);

					while (this.method_15046(this.field_15439)) {
					}
				}
			}
		}

		private void method_15042(WoodlandMansionGenerator.class_3478 arg, WoodlandMansionGenerator.class_3478 arg2) {
			List<Pair<Integer, Integer>> list = Lists.<Pair<Integer, Integer>>newArrayList();

			for (int i = 0; i < arg.field_15453; i++) {
				for (int j = 0; j < arg.field_15454; j++) {
					if (arg.method_15066(j, i) == 2) {
						list.add(new Pair<>(j, i));
					}
				}
			}

			Collections.shuffle(list, this.field_15438);
			int i = 10;

			for (Pair<Integer, Integer> pair : list) {
				int k = pair.getLeft();
				int l = pair.getRight();
				if (arg2.method_15066(k, l) == 0) {
					int m = k;
					int n = k;
					int o = l;
					int p = l;
					int q = 65536;
					if (arg2.method_15066(k + 1, l) == 0
						&& arg2.method_15066(k, l + 1) == 0
						&& arg2.method_15066(k + 1, l + 1) == 0
						&& arg.method_15066(k + 1, l) == 2
						&& arg.method_15066(k, l + 1) == 2
						&& arg.method_15066(k + 1, l + 1) == 2) {
						n = k + 1;
						p = l + 1;
						q = 262144;
					} else if (arg2.method_15066(k - 1, l) == 0
						&& arg2.method_15066(k, l + 1) == 0
						&& arg2.method_15066(k - 1, l + 1) == 0
						&& arg.method_15066(k - 1, l) == 2
						&& arg.method_15066(k, l + 1) == 2
						&& arg.method_15066(k - 1, l + 1) == 2) {
						m = k - 1;
						p = l + 1;
						q = 262144;
					} else if (arg2.method_15066(k - 1, l) == 0
						&& arg2.method_15066(k, l - 1) == 0
						&& arg2.method_15066(k - 1, l - 1) == 0
						&& arg.method_15066(k - 1, l) == 2
						&& arg.method_15066(k, l - 1) == 2
						&& arg.method_15066(k - 1, l - 1) == 2) {
						m = k - 1;
						o = l - 1;
						q = 262144;
					} else if (arg2.method_15066(k + 1, l) == 0 && arg.method_15066(k + 1, l) == 2) {
						n = k + 1;
						q = 131072;
					} else if (arg2.method_15066(k, l + 1) == 0 && arg.method_15066(k, l + 1) == 2) {
						p = l + 1;
						q = 131072;
					} else if (arg2.method_15066(k - 1, l) == 0 && arg.method_15066(k - 1, l) == 2) {
						m = k - 1;
						q = 131072;
					} else if (arg2.method_15066(k, l - 1) == 0 && arg.method_15066(k, l - 1) == 2) {
						o = l - 1;
						q = 131072;
					}

					int r = this.field_15438.nextBoolean() ? m : n;
					int s = this.field_15438.nextBoolean() ? o : p;
					int t = 2097152;
					if (!arg.method_15067(r, s, 1)) {
						r = r == m ? n : m;
						s = s == o ? p : o;
						if (!arg.method_15067(r, s, 1)) {
							s = s == o ? p : o;
							if (!arg.method_15067(r, s, 1)) {
								r = r == m ? n : m;
								s = s == o ? p : o;
								if (!arg.method_15067(r, s, 1)) {
									t = 0;
									r = m;
									s = o;
								}
							}
						}
					}

					for (int u = o; u <= p; u++) {
						for (int v = m; v <= n; v++) {
							if (v == r && u == s) {
								arg2.method_15065(v, u, 1048576 | t | q | i);
							} else {
								arg2.method_15065(v, u, q | i);
							}
						}
					}

					i++;
				}
			}
		}
	}

	static class class_3475 {
		private final StructureManager field_15444;
		private final Random field_15447;
		private int field_15446;
		private int field_15445;

		public class_3475(StructureManager structureManager, Random random) {
			this.field_15444 = structureManager;
			this.field_15447 = random;
		}

		public void method_15050(BlockPos blockPos, Rotation rotation, List<WoodlandMansionGenerator.Piece> list, WoodlandMansionGenerator.class_3474 arg) {
			WoodlandMansionGenerator.class_3476 lv = new WoodlandMansionGenerator.class_3476();
			lv.field_15449 = blockPos;
			lv.field_15450 = rotation;
			lv.field_15448 = "wall_flat";
			WoodlandMansionGenerator.class_3476 lv2 = new WoodlandMansionGenerator.class_3476();
			this.method_15054(list, lv);
			lv2.field_15449 = lv.field_15449.up(8);
			lv2.field_15450 = lv.field_15450;
			lv2.field_15448 = "wall_window";
			if (!list.isEmpty()) {
			}

			WoodlandMansionGenerator.class_3478 lv3 = arg.field_15440;
			WoodlandMansionGenerator.class_3478 lv4 = arg.field_15439;
			this.field_15446 = arg.field_15442 + 1;
			this.field_15445 = arg.field_15441 + 1;
			int i = arg.field_15442 + 1;
			int j = arg.field_15441;
			this.method_15051(list, lv, lv3, Direction.SOUTH, this.field_15446, this.field_15445, i, j);
			this.method_15051(list, lv2, lv3, Direction.SOUTH, this.field_15446, this.field_15445, i, j);
			WoodlandMansionGenerator.class_3476 lv5 = new WoodlandMansionGenerator.class_3476();
			lv5.field_15449 = lv.field_15449.up(19);
			lv5.field_15450 = lv.field_15450;
			lv5.field_15448 = "wall_window";
			boolean bl = false;

			for (int k = 0; k < lv4.field_15453 && !bl; k++) {
				for (int l = lv4.field_15454 - 1; l >= 0 && !bl; l--) {
					if (WoodlandMansionGenerator.class_3474.method_15047(lv4, l, k)) {
						lv5.field_15449 = lv5.field_15449.offset(rotation.rotate(Direction.SOUTH), 8 + (k - this.field_15445) * 8);
						lv5.field_15449 = lv5.field_15449.offset(rotation.rotate(Direction.EAST), (l - this.field_15446) * 8);
						this.method_15052(list, lv5);
						this.method_15051(list, lv5, lv4, Direction.SOUTH, l, k, l, k);
						bl = true;
					}
				}
			}

			this.method_15055(list, blockPos.up(16), rotation, lv3, lv4);
			this.method_15055(list, blockPos.up(27), rotation, lv4, null);
			if (!list.isEmpty()) {
			}

			WoodlandMansionGenerator.class_3473[] lvs = new WoodlandMansionGenerator.class_3473[]{
				new WoodlandMansionGenerator.class_3472(), new WoodlandMansionGenerator.class_3477(), new WoodlandMansionGenerator.class_3479()
			};

			for (int lx = 0; lx < 3; lx++) {
				BlockPos blockPos2 = blockPos.up(8 * lx + (lx == 2 ? 3 : 0));
				WoodlandMansionGenerator.class_3478 lv6 = arg.field_15443[lx];
				WoodlandMansionGenerator.class_3478 lv7 = lx == 2 ? lv4 : lv3;
				String string = lx == 0 ? "carpet_south_1" : "carpet_south_2";
				String string2 = lx == 0 ? "carpet_west_1" : "carpet_west_2";

				for (int m = 0; m < lv7.field_15453; m++) {
					for (int n = 0; n < lv7.field_15454; n++) {
						if (lv7.method_15066(n, m) == 1) {
							BlockPos blockPos3 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 8 + (m - this.field_15445) * 8);
							blockPos3 = blockPos3.offset(rotation.rotate(Direction.EAST), (n - this.field_15446) * 8);
							list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "corridor_floor", blockPos3, rotation));
							if (lv7.method_15066(n, m - 1) == 1 || (lv6.method_15066(n, m - 1) & 8388608) == 8388608) {
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "carpet_north", blockPos3.offset(rotation.rotate(Direction.EAST), 1).up(), rotation));
							}

							if (lv7.method_15066(n + 1, m) == 1 || (lv6.method_15066(n + 1, m) & 8388608) == 8388608) {
								list.add(
									new WoodlandMansionGenerator.Piece(
										this.field_15444, "carpet_east", blockPos3.offset(rotation.rotate(Direction.SOUTH), 1).offset(rotation.rotate(Direction.EAST), 5).up(), rotation
									)
								);
							}

							if (lv7.method_15066(n, m + 1) == 1 || (lv6.method_15066(n, m + 1) & 8388608) == 8388608) {
								list.add(
									new WoodlandMansionGenerator.Piece(
										this.field_15444, string, blockPos3.offset(rotation.rotate(Direction.SOUTH), 5).offset(rotation.rotate(Direction.WEST), 1), rotation
									)
								);
							}

							if (lv7.method_15066(n - 1, m) == 1 || (lv6.method_15066(n - 1, m) & 8388608) == 8388608) {
								list.add(
									new WoodlandMansionGenerator.Piece(
										this.field_15444, string2, blockPos3.offset(rotation.rotate(Direction.WEST), 1).offset(rotation.rotate(Direction.NORTH), 1), rotation
									)
								);
							}
						}
					}
				}

				String string3 = lx == 0 ? "indoors_wall_1" : "indoors_wall_2";
				String string4 = lx == 0 ? "indoors_door_1" : "indoors_door_2";
				List<Direction> list2 = Lists.<Direction>newArrayList();

				for (int o = 0; o < lv7.field_15453; o++) {
					for (int p = 0; p < lv7.field_15454; p++) {
						boolean bl2 = lx == 2 && lv7.method_15066(p, o) == 3;
						if (lv7.method_15066(p, o) == 2 || bl2) {
							int q = lv6.method_15066(p, o);
							int r = q & 983040;
							int s = q & 65535;
							bl2 = bl2 && (q & 8388608) == 8388608;
							list2.clear();
							if ((q & 2097152) == 2097152) {
								for (Direction direction : Direction.Type.HORIZONTAL) {
									if (lv7.method_15066(p + direction.getOffsetX(), o + direction.getOffsetZ()) == 1) {
										list2.add(direction);
									}
								}
							}

							Direction direction2 = null;
							if (!list2.isEmpty()) {
								direction2 = (Direction)list2.get(this.field_15447.nextInt(list2.size()));
							} else if ((q & 1048576) == 1048576) {
								direction2 = Direction.UP;
							}

							BlockPos blockPos4 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 8 + (o - this.field_15445) * 8);
							blockPos4 = blockPos4.offset(rotation.rotate(Direction.EAST), -1 + (p - this.field_15446) * 8);
							if (WoodlandMansionGenerator.class_3474.method_15047(lv7, p - 1, o) && !arg.method_15039(lv7, p - 1, o, lx, s)) {
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, direction2 == Direction.WEST ? string4 : string3, blockPos4, rotation));
							}

							if (lv7.method_15066(p + 1, o) == 1 && !bl2) {
								BlockPos blockPos5 = blockPos4.offset(rotation.rotate(Direction.EAST), 8);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, direction2 == Direction.EAST ? string4 : string3, blockPos5, rotation));
							}

							if (WoodlandMansionGenerator.class_3474.method_15047(lv7, p, o + 1) && !arg.method_15039(lv7, p, o + 1, lx, s)) {
								BlockPos blockPos5 = blockPos4.offset(rotation.rotate(Direction.SOUTH), 7);
								blockPos5 = blockPos5.offset(rotation.rotate(Direction.EAST), 7);
								list.add(
									new WoodlandMansionGenerator.Piece(
										this.field_15444, direction2 == Direction.SOUTH ? string4 : string3, blockPos5, rotation.method_10501(Rotation.ROT_90)
									)
								);
							}

							if (lv7.method_15066(p, o - 1) == 1 && !bl2) {
								BlockPos blockPos5 = blockPos4.offset(rotation.rotate(Direction.NORTH), 1);
								blockPos5 = blockPos5.offset(rotation.rotate(Direction.EAST), 7);
								list.add(
									new WoodlandMansionGenerator.Piece(
										this.field_15444, direction2 == Direction.NORTH ? string4 : string3, blockPos5, rotation.method_10501(Rotation.ROT_90)
									)
								);
							}

							if (r == 65536) {
								this.method_15057(list, blockPos4, rotation, direction2, lvs[lx]);
							} else if (r == 131072 && direction2 != null) {
								Direction direction3 = arg.method_15040(lv7, p, o, lx, s);
								boolean bl3 = (q & 4194304) == 4194304;
								this.method_15059(list, blockPos4, rotation, direction3, direction2, lvs[lx], bl3);
							} else if (r == 262144 && direction2 != null && direction2 != Direction.UP) {
								Direction direction3 = direction2.rotateYClockwise();
								if (!arg.method_15039(lv7, p + direction3.getOffsetX(), o + direction3.getOffsetZ(), lx, s)) {
									direction3 = direction3.getOpposite();
								}

								this.method_15056(list, blockPos4, rotation, direction3, direction2, lvs[lx]);
							} else if (r == 262144 && direction2 == Direction.UP) {
								this.method_15053(list, blockPos4, rotation, lvs[lx]);
							}
						}
					}
				}
			}
		}

		private void method_15051(
			List<WoodlandMansionGenerator.Piece> list,
			WoodlandMansionGenerator.class_3476 arg,
			WoodlandMansionGenerator.class_3478 arg2,
			Direction direction,
			int i,
			int j,
			int k,
			int l
		) {
			int m = i;
			int n = j;
			Direction direction2 = direction;

			do {
				if (!WoodlandMansionGenerator.class_3474.method_15047(arg2, m + direction.getOffsetX(), n + direction.getOffsetZ())) {
					this.method_15058(list, arg);
					direction = direction.rotateYClockwise();
					if (m != k || n != l || direction2 != direction) {
						this.method_15052(list, arg);
					}
				} else if (WoodlandMansionGenerator.class_3474.method_15047(arg2, m + direction.getOffsetX(), n + direction.getOffsetZ())
					&& WoodlandMansionGenerator.class_3474.method_15047(
						arg2,
						m + direction.getOffsetX() + direction.rotateYCounterclockwise().getOffsetX(),
						n + direction.getOffsetZ() + direction.rotateYCounterclockwise().getOffsetZ()
					)) {
					this.method_15060(list, arg);
					m += direction.getOffsetX();
					n += direction.getOffsetZ();
					direction = direction.rotateYCounterclockwise();
				} else {
					m += direction.getOffsetX();
					n += direction.getOffsetZ();
					if (m != k || n != l || direction2 != direction) {
						this.method_15052(list, arg);
					}
				}
			} while (m != k || n != l || direction2 != direction);
		}

		private void method_15055(
			List<WoodlandMansionGenerator.Piece> list,
			BlockPos blockPos,
			Rotation rotation,
			WoodlandMansionGenerator.class_3478 arg,
			@Nullable WoodlandMansionGenerator.class_3478 arg2
		) {
			for (int i = 0; i < arg.field_15453; i++) {
				for (int j = 0; j < arg.field_15454; j++) {
					BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (i - this.field_15445) * 8);
					blockPos2 = blockPos2.offset(rotation.rotate(Direction.EAST), (j - this.field_15446) * 8);
					boolean bl = arg2 != null && WoodlandMansionGenerator.class_3474.method_15047(arg2, j, i);
					if (WoodlandMansionGenerator.class_3474.method_15047(arg, j, i) && !bl) {
						list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof", blockPos2.up(3), rotation));
						if (!WoodlandMansionGenerator.class_3474.method_15047(arg, j + 1, i)) {
							BlockPos blockPos3 = blockPos2.offset(rotation.rotate(Direction.EAST), 6);
							list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_front", blockPos3, rotation));
						}

						if (!WoodlandMansionGenerator.class_3474.method_15047(arg, j - 1, i)) {
							BlockPos blockPos3 = blockPos2.offset(rotation.rotate(Direction.EAST), 0);
							blockPos3 = blockPos3.offset(rotation.rotate(Direction.SOUTH), 7);
							list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_front", blockPos3, rotation.method_10501(Rotation.ROT_180)));
						}

						if (!WoodlandMansionGenerator.class_3474.method_15047(arg, j, i - 1)) {
							BlockPos blockPos3 = blockPos2.offset(rotation.rotate(Direction.WEST), 1);
							list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_front", blockPos3, rotation.method_10501(Rotation.ROT_270)));
						}

						if (!WoodlandMansionGenerator.class_3474.method_15047(arg, j, i + 1)) {
							BlockPos blockPos3 = blockPos2.offset(rotation.rotate(Direction.EAST), 6);
							blockPos3 = blockPos3.offset(rotation.rotate(Direction.SOUTH), 6);
							list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_front", blockPos3, rotation.method_10501(Rotation.ROT_90)));
						}
					}
				}
			}

			if (arg2 != null) {
				for (int i = 0; i < arg.field_15453; i++) {
					for (int jx = 0; jx < arg.field_15454; jx++) {
						BlockPos var17 = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (i - this.field_15445) * 8);
						var17 = var17.offset(rotation.rotate(Direction.EAST), (jx - this.field_15446) * 8);
						boolean bl = WoodlandMansionGenerator.class_3474.method_15047(arg2, jx, i);
						if (WoodlandMansionGenerator.class_3474.method_15047(arg, jx, i) && bl) {
							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx + 1, i)) {
								BlockPos blockPos3 = var17.offset(rotation.rotate(Direction.EAST), 7);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "small_wall", blockPos3, rotation));
							}

							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx - 1, i)) {
								BlockPos blockPos3 = var17.offset(rotation.rotate(Direction.WEST), 1);
								blockPos3 = blockPos3.offset(rotation.rotate(Direction.SOUTH), 6);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "small_wall", blockPos3, rotation.method_10501(Rotation.ROT_180)));
							}

							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx, i - 1)) {
								BlockPos blockPos3 = var17.offset(rotation.rotate(Direction.WEST), 0);
								blockPos3 = blockPos3.offset(rotation.rotate(Direction.NORTH), 1);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "small_wall", blockPos3, rotation.method_10501(Rotation.ROT_270)));
							}

							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx, i + 1)) {
								BlockPos blockPos3 = var17.offset(rotation.rotate(Direction.EAST), 6);
								blockPos3 = blockPos3.offset(rotation.rotate(Direction.SOUTH), 7);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "small_wall", blockPos3, rotation.method_10501(Rotation.ROT_90)));
							}

							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx + 1, i)) {
								if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx, i - 1)) {
									BlockPos blockPos3 = var17.offset(rotation.rotate(Direction.EAST), 7);
									blockPos3 = blockPos3.offset(rotation.rotate(Direction.NORTH), 2);
									list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "small_wall_corner", blockPos3, rotation));
								}

								if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx, i + 1)) {
									BlockPos blockPos3 = var17.offset(rotation.rotate(Direction.EAST), 8);
									blockPos3 = blockPos3.offset(rotation.rotate(Direction.SOUTH), 7);
									list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "small_wall_corner", blockPos3, rotation.method_10501(Rotation.ROT_90)));
								}
							}

							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx - 1, i)) {
								if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx, i - 1)) {
									BlockPos blockPos3 = var17.offset(rotation.rotate(Direction.WEST), 2);
									blockPos3 = blockPos3.offset(rotation.rotate(Direction.NORTH), 1);
									list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "small_wall_corner", blockPos3, rotation.method_10501(Rotation.ROT_270)));
								}

								if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx, i + 1)) {
									BlockPos blockPos3 = var17.offset(rotation.rotate(Direction.WEST), 1);
									blockPos3 = blockPos3.offset(rotation.rotate(Direction.SOUTH), 8);
									list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "small_wall_corner", blockPos3, rotation.method_10501(Rotation.ROT_180)));
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < arg.field_15453; i++) {
				for (int jxx = 0; jxx < arg.field_15454; jxx++) {
					BlockPos var19 = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (i - this.field_15445) * 8);
					var19 = var19.offset(rotation.rotate(Direction.EAST), (jxx - this.field_15446) * 8);
					boolean bl = arg2 != null && WoodlandMansionGenerator.class_3474.method_15047(arg2, jxx, i);
					if (WoodlandMansionGenerator.class_3474.method_15047(arg, jxx, i) && !bl) {
						if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jxx + 1, i)) {
							BlockPos blockPos3 = var19.offset(rotation.rotate(Direction.EAST), 6);
							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jxx, i + 1)) {
								BlockPos blockPos4 = blockPos3.offset(rotation.rotate(Direction.SOUTH), 6);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_corner", blockPos4, rotation));
							} else if (WoodlandMansionGenerator.class_3474.method_15047(arg, jxx + 1, i + 1)) {
								BlockPos blockPos4 = blockPos3.offset(rotation.rotate(Direction.SOUTH), 5);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_inner_corner", blockPos4, rotation));
							}

							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jxx, i - 1)) {
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_corner", blockPos3, rotation.method_10501(Rotation.ROT_270)));
							} else if (WoodlandMansionGenerator.class_3474.method_15047(arg, jxx + 1, i - 1)) {
								BlockPos blockPos4 = var19.offset(rotation.rotate(Direction.EAST), 9);
								blockPos4 = blockPos4.offset(rotation.rotate(Direction.NORTH), 2);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_inner_corner", blockPos4, rotation.method_10501(Rotation.ROT_90)));
							}
						}

						if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jxx - 1, i)) {
							BlockPos blockPos3x = var19.offset(rotation.rotate(Direction.EAST), 0);
							blockPos3x = blockPos3x.offset(rotation.rotate(Direction.SOUTH), 0);
							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jxx, i + 1)) {
								BlockPos blockPos4 = blockPos3x.offset(rotation.rotate(Direction.SOUTH), 6);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_corner", blockPos4, rotation.method_10501(Rotation.ROT_90)));
							} else if (WoodlandMansionGenerator.class_3474.method_15047(arg, jxx - 1, i + 1)) {
								BlockPos blockPos4 = blockPos3x.offset(rotation.rotate(Direction.SOUTH), 8);
								blockPos4 = blockPos4.offset(rotation.rotate(Direction.WEST), 3);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_inner_corner", blockPos4, rotation.method_10501(Rotation.ROT_270)));
							}

							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jxx, i - 1)) {
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_corner", blockPos3x, rotation.method_10501(Rotation.ROT_180)));
							} else if (WoodlandMansionGenerator.class_3474.method_15047(arg, jxx - 1, i - 1)) {
								BlockPos blockPos4 = blockPos3x.offset(rotation.rotate(Direction.SOUTH), 1);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_inner_corner", blockPos4, rotation.method_10501(Rotation.ROT_180)));
							}
						}
					}
				}
			}
		}

		private void method_15054(List<WoodlandMansionGenerator.Piece> list, WoodlandMansionGenerator.class_3476 arg) {
			Direction direction = arg.field_15450.rotate(Direction.WEST);
			list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "entrance", arg.field_15449.offset(direction, 9), arg.field_15450));
			arg.field_15449 = arg.field_15449.offset(arg.field_15450.rotate(Direction.SOUTH), 16);
		}

		private void method_15052(List<WoodlandMansionGenerator.Piece> list, WoodlandMansionGenerator.class_3476 arg) {
			list.add(
				new WoodlandMansionGenerator.Piece(this.field_15444, arg.field_15448, arg.field_15449.offset(arg.field_15450.rotate(Direction.EAST), 7), arg.field_15450)
			);
			arg.field_15449 = arg.field_15449.offset(arg.field_15450.rotate(Direction.SOUTH), 8);
		}

		private void method_15058(List<WoodlandMansionGenerator.Piece> list, WoodlandMansionGenerator.class_3476 arg) {
			arg.field_15449 = arg.field_15449.offset(arg.field_15450.rotate(Direction.SOUTH), -1);
			list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "wall_corner", arg.field_15449, arg.field_15450));
			arg.field_15449 = arg.field_15449.offset(arg.field_15450.rotate(Direction.SOUTH), -7);
			arg.field_15449 = arg.field_15449.offset(arg.field_15450.rotate(Direction.WEST), -6);
			arg.field_15450 = arg.field_15450.method_10501(Rotation.ROT_90);
		}

		private void method_15060(List<WoodlandMansionGenerator.Piece> list, WoodlandMansionGenerator.class_3476 arg) {
			arg.field_15449 = arg.field_15449.offset(arg.field_15450.rotate(Direction.SOUTH), 6);
			arg.field_15449 = arg.field_15449.offset(arg.field_15450.rotate(Direction.EAST), 8);
			arg.field_15450 = arg.field_15450.method_10501(Rotation.ROT_270);
		}

		private void method_15057(
			List<WoodlandMansionGenerator.Piece> list, BlockPos blockPos, Rotation rotation, Direction direction, WoodlandMansionGenerator.class_3473 arg
		) {
			Rotation rotation2 = Rotation.ROT_0;
			String string = arg.method_15037(this.field_15447);
			if (direction != Direction.EAST) {
				if (direction == Direction.NORTH) {
					rotation2 = rotation2.method_10501(Rotation.ROT_270);
				} else if (direction == Direction.WEST) {
					rotation2 = rotation2.method_10501(Rotation.ROT_180);
				} else if (direction == Direction.SOUTH) {
					rotation2 = rotation2.method_10501(Rotation.ROT_90);
				} else {
					string = arg.method_15032(this.field_15447);
				}
			}

			BlockPos blockPos2 = Structure.method_15162(new BlockPos(1, 0, 0), Mirror.NONE, rotation2, 7, 7);
			rotation2 = rotation2.method_10501(rotation);
			blockPos2 = blockPos2.method_10070(rotation);
			BlockPos blockPos3 = blockPos.add(blockPos2.getX(), 0, blockPos2.getZ());
			list.add(new WoodlandMansionGenerator.Piece(this.field_15444, string, blockPos3, rotation2));
		}

		private void method_15059(
			List<WoodlandMansionGenerator.Piece> list,
			BlockPos blockPos,
			Rotation rotation,
			Direction direction,
			Direction direction2,
			WoodlandMansionGenerator.class_3473 arg,
			boolean bl
		) {
			if (direction2 == Direction.EAST && direction == Direction.SOUTH) {
				BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 1);
				list.add(new WoodlandMansionGenerator.Piece(this.field_15444, arg.method_15033(this.field_15447, bl), blockPos2, rotation));
			} else if (direction2 == Direction.EAST && direction == Direction.NORTH) {
				BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 1);
				blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
				list.add(new WoodlandMansionGenerator.Piece(this.field_15444, arg.method_15033(this.field_15447, bl), blockPos2, rotation, Mirror.LEFT_RIGHT));
			} else if (direction2 == Direction.WEST && direction == Direction.NORTH) {
				BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 7);
				blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
				list.add(new WoodlandMansionGenerator.Piece(this.field_15444, arg.method_15033(this.field_15447, bl), blockPos2, rotation.method_10501(Rotation.ROT_180)));
			} else if (direction2 == Direction.WEST && direction == Direction.SOUTH) {
				BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 7);
				list.add(new WoodlandMansionGenerator.Piece(this.field_15444, arg.method_15033(this.field_15447, bl), blockPos2, rotation, Mirror.FRONT_BACK));
			} else if (direction2 == Direction.SOUTH && direction == Direction.EAST) {
				BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 1);
				list.add(
					new WoodlandMansionGenerator.Piece(
						this.field_15444, arg.method_15033(this.field_15447, bl), blockPos2, rotation.method_10501(Rotation.ROT_90), Mirror.LEFT_RIGHT
					)
				);
			} else if (direction2 == Direction.SOUTH && direction == Direction.WEST) {
				BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 7);
				list.add(new WoodlandMansionGenerator.Piece(this.field_15444, arg.method_15033(this.field_15447, bl), blockPos2, rotation.method_10501(Rotation.ROT_90)));
			} else if (direction2 == Direction.NORTH && direction == Direction.WEST) {
				BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 7);
				blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
				list.add(
					new WoodlandMansionGenerator.Piece(
						this.field_15444, arg.method_15033(this.field_15447, bl), blockPos2, rotation.method_10501(Rotation.ROT_90), Mirror.FRONT_BACK
					)
				);
			} else if (direction2 == Direction.NORTH && direction == Direction.EAST) {
				BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 1);
				blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
				list.add(new WoodlandMansionGenerator.Piece(this.field_15444, arg.method_15033(this.field_15447, bl), blockPos2, rotation.method_10501(Rotation.ROT_270)));
			} else if (direction2 == Direction.SOUTH && direction == Direction.NORTH) {
				BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 1);
				blockPos2 = blockPos2.offset(rotation.rotate(Direction.NORTH), 8);
				list.add(new WoodlandMansionGenerator.Piece(this.field_15444, arg.method_15031(this.field_15447, bl), blockPos2, rotation));
			} else if (direction2 == Direction.NORTH && direction == Direction.SOUTH) {
				BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 7);
				blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 14);
				list.add(new WoodlandMansionGenerator.Piece(this.field_15444, arg.method_15031(this.field_15447, bl), blockPos2, rotation.method_10501(Rotation.ROT_180)));
			} else if (direction2 == Direction.WEST && direction == Direction.EAST) {
				BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 15);
				list.add(new WoodlandMansionGenerator.Piece(this.field_15444, arg.method_15031(this.field_15447, bl), blockPos2, rotation.method_10501(Rotation.ROT_90)));
			} else if (direction2 == Direction.EAST && direction == Direction.WEST) {
				BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.WEST), 7);
				blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
				list.add(new WoodlandMansionGenerator.Piece(this.field_15444, arg.method_15031(this.field_15447, bl), blockPos2, rotation.method_10501(Rotation.ROT_270)));
			} else if (direction2 == Direction.UP && direction == Direction.EAST) {
				BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 15);
				list.add(new WoodlandMansionGenerator.Piece(this.field_15444, arg.method_15035(this.field_15447), blockPos2, rotation.method_10501(Rotation.ROT_90)));
			} else if (direction2 == Direction.UP && direction == Direction.SOUTH) {
				BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 1);
				blockPos2 = blockPos2.offset(rotation.rotate(Direction.NORTH), 0);
				list.add(new WoodlandMansionGenerator.Piece(this.field_15444, arg.method_15035(this.field_15447), blockPos2, rotation));
			}
		}

		private void method_15056(
			List<WoodlandMansionGenerator.Piece> list,
			BlockPos blockPos,
			Rotation rotation,
			Direction direction,
			Direction direction2,
			WoodlandMansionGenerator.class_3473 arg
		) {
			int i = 0;
			int j = 0;
			Rotation rotation2 = rotation;
			Mirror mirror = Mirror.NONE;
			if (direction2 == Direction.EAST && direction == Direction.SOUTH) {
				i = -7;
			} else if (direction2 == Direction.EAST && direction == Direction.NORTH) {
				i = -7;
				j = 6;
				mirror = Mirror.LEFT_RIGHT;
			} else if (direction2 == Direction.NORTH && direction == Direction.EAST) {
				i = 1;
				j = 14;
				rotation2 = rotation.method_10501(Rotation.ROT_270);
			} else if (direction2 == Direction.NORTH && direction == Direction.WEST) {
				i = 7;
				j = 14;
				rotation2 = rotation.method_10501(Rotation.ROT_270);
				mirror = Mirror.LEFT_RIGHT;
			} else if (direction2 == Direction.SOUTH && direction == Direction.WEST) {
				i = 7;
				j = -8;
				rotation2 = rotation.method_10501(Rotation.ROT_90);
			} else if (direction2 == Direction.SOUTH && direction == Direction.EAST) {
				i = 1;
				j = -8;
				rotation2 = rotation.method_10501(Rotation.ROT_90);
				mirror = Mirror.LEFT_RIGHT;
			} else if (direction2 == Direction.WEST && direction == Direction.NORTH) {
				i = 15;
				j = 6;
				rotation2 = rotation.method_10501(Rotation.ROT_180);
			} else if (direction2 == Direction.WEST && direction == Direction.SOUTH) {
				i = 15;
				mirror = Mirror.FRONT_BACK;
			}

			BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), i);
			blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), j);
			list.add(new WoodlandMansionGenerator.Piece(this.field_15444, arg.method_15034(this.field_15447), blockPos2, rotation2, mirror));
		}

		private void method_15053(List<WoodlandMansionGenerator.Piece> list, BlockPos blockPos, Rotation rotation, WoodlandMansionGenerator.class_3473 arg) {
			BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 1);
			list.add(new WoodlandMansionGenerator.Piece(this.field_15444, arg.method_15036(this.field_15447), blockPos2, rotation, Mirror.NONE));
		}
	}

	static class class_3476 {
		public Rotation field_15450;
		public BlockPos field_15449;
		public String field_15448;

		private class_3476() {
		}
	}

	static class class_3477 extends WoodlandMansionGenerator.class_3473 {
		private class_3477() {
		}

		@Override
		public String method_15037(Random random) {
			return "1x1_b" + (random.nextInt(4) + 1);
		}

		@Override
		public String method_15032(Random random) {
			return "1x1_as" + (random.nextInt(4) + 1);
		}

		@Override
		public String method_15033(Random random, boolean bl) {
			return bl ? "1x2_c_stairs" : "1x2_c" + (random.nextInt(4) + 1);
		}

		@Override
		public String method_15031(Random random, boolean bl) {
			return bl ? "1x2_d_stairs" : "1x2_d" + (random.nextInt(5) + 1);
		}

		@Override
		public String method_15035(Random random) {
			return "1x2_se" + (random.nextInt(1) + 1);
		}

		@Override
		public String method_15034(Random random) {
			return "2x2_b" + (random.nextInt(5) + 1);
		}

		@Override
		public String method_15036(Random random) {
			return "2x2_s1";
		}
	}

	static class class_3478 {
		private final int[][] field_15451;
		private final int field_15454;
		private final int field_15453;
		private final int field_15452;

		public class_3478(int i, int j, int k) {
			this.field_15454 = i;
			this.field_15453 = j;
			this.field_15452 = k;
			this.field_15451 = new int[i][j];
		}

		public void method_15065(int i, int j, int k) {
			if (i >= 0 && i < this.field_15454 && j >= 0 && j < this.field_15453) {
				this.field_15451[i][j] = k;
			}
		}

		public void method_15062(int i, int j, int k, int l, int m) {
			for (int n = j; n <= l; n++) {
				for (int o = i; o <= k; o++) {
					this.method_15065(o, n, m);
				}
			}
		}

		public int method_15066(int i, int j) {
			return i >= 0 && i < this.field_15454 && j >= 0 && j < this.field_15453 ? this.field_15451[i][j] : this.field_15452;
		}

		public void method_15061(int i, int j, int k, int l) {
			if (this.method_15066(i, j) == k) {
				this.method_15065(i, j, l);
			}
		}

		public boolean method_15067(int i, int j, int k) {
			return this.method_15066(i - 1, j) == k || this.method_15066(i + 1, j) == k || this.method_15066(i, j + 1) == k || this.method_15066(i, j - 1) == k;
		}
	}

	static class class_3479 extends WoodlandMansionGenerator.class_3477 {
		private class_3479() {
		}
	}
}
