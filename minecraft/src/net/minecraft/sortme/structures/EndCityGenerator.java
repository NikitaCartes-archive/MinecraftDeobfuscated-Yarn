package net.minecraft.sortme.structures;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.class_3443;
import net.minecraft.class_3470;
import net.minecraft.class_3485;
import net.minecraft.class_3492;
import net.minecraft.class_3499;
import net.minecraft.class_3545;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.StructurePiece;
import net.minecraft.sortme.structures.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.loot.LootTables;

public class EndCityGenerator {
	private static final class_3492 field_14383 = new class_3492().method_15133(true).method_16184(BlockIgnoreStructureProcessor.field_16718);
	private static final class_3492 field_14389 = new class_3492().method_15133(true).method_16184(BlockIgnoreStructureProcessor.field_16721);
	private static final EndCityGenerator.class_3344 field_14390 = new EndCityGenerator.class_3344() {
		@Override
		public void method_14688() {
		}

		@Override
		public boolean method_14687(class_3485 arg, int i, EndCityGenerator.class_3343 arg2, BlockPos blockPos, List<class_3443> list, Random random) {
			if (i > 8) {
				return false;
			} else {
				Rotation rotation = arg2.field_15434.method_15113();
				EndCityGenerator.class_3343 lv = EndCityGenerator.method_14681(list, EndCityGenerator.method_14684(arg, arg2, blockPos, "base_floor", rotation, true));
				int j = random.nextInt(3);
				if (j == 0) {
					lv = EndCityGenerator.method_14681(list, EndCityGenerator.method_14684(arg, lv, new BlockPos(-1, 4, -1), "base_roof", rotation, true));
				} else if (j == 1) {
					lv = EndCityGenerator.method_14681(list, EndCityGenerator.method_14684(arg, lv, new BlockPos(-1, 0, -1), "second_floor_2", rotation, false));
					lv = EndCityGenerator.method_14681(list, EndCityGenerator.method_14684(arg, lv, new BlockPos(-1, 8, -1), "second_roof", rotation, false));
					EndCityGenerator.method_14673(arg, EndCityGenerator.field_14386, i + 1, lv, null, list, random);
				} else if (j == 2) {
					lv = EndCityGenerator.method_14681(list, EndCityGenerator.method_14684(arg, lv, new BlockPos(-1, 0, -1), "second_floor_2", rotation, false));
					lv = EndCityGenerator.method_14681(list, EndCityGenerator.method_14684(arg, lv, new BlockPos(-1, 4, -1), "third_floor_2", rotation, false));
					lv = EndCityGenerator.method_14681(list, EndCityGenerator.method_14684(arg, lv, new BlockPos(-1, 8, -1), "third_roof", rotation, true));
					EndCityGenerator.method_14673(arg, EndCityGenerator.field_14386, i + 1, lv, null, list, random);
				}

				return true;
			}
		}
	};
	private static final List<class_3545<Rotation, BlockPos>> field_14385 = Lists.<class_3545<Rotation, BlockPos>>newArrayList(
		new class_3545<>(Rotation.ROT_0, new BlockPos(1, -1, 0)),
		new class_3545<>(Rotation.ROT_90, new BlockPos(6, -1, 1)),
		new class_3545<>(Rotation.ROT_270, new BlockPos(0, -1, 5)),
		new class_3545<>(Rotation.ROT_180, new BlockPos(5, -1, 6))
	);
	private static final EndCityGenerator.class_3344 field_14386 = new EndCityGenerator.class_3344() {
		@Override
		public void method_14688() {
		}

		@Override
		public boolean method_14687(class_3485 arg, int i, EndCityGenerator.class_3343 arg2, BlockPos blockPos, List<class_3443> list, Random random) {
			Rotation rotation = arg2.field_15434.method_15113();
			EndCityGenerator.class_3343 lv = EndCityGenerator.method_14681(
				list, EndCityGenerator.method_14684(arg, arg2, new BlockPos(3 + random.nextInt(2), -3, 3 + random.nextInt(2)), "tower_base", rotation, true)
			);
			lv = EndCityGenerator.method_14681(list, EndCityGenerator.method_14684(arg, lv, new BlockPos(0, 7, 0), "tower_piece", rotation, true));
			EndCityGenerator.class_3343 lv2 = random.nextInt(3) == 0 ? lv : null;
			int j = 1 + random.nextInt(3);

			for (int k = 0; k < j; k++) {
				lv = EndCityGenerator.method_14681(list, EndCityGenerator.method_14684(arg, lv, new BlockPos(0, 4, 0), "tower_piece", rotation, true));
				if (k < j - 1 && random.nextBoolean()) {
					lv2 = lv;
				}
			}

			if (lv2 != null) {
				for (class_3545<Rotation, BlockPos> lv3 : EndCityGenerator.field_14385) {
					if (random.nextBoolean()) {
						EndCityGenerator.class_3343 lv4 = EndCityGenerator.method_14681(
							list, EndCityGenerator.method_14684(arg, lv2, lv3.method_15441(), "bridge_end", rotation.method_10501(lv3.method_15442()), true)
						);
						EndCityGenerator.method_14673(arg, EndCityGenerator.field_14387, i + 1, lv4, null, list, random);
					}
				}

				lv = EndCityGenerator.method_14681(list, EndCityGenerator.method_14684(arg, lv, new BlockPos(-1, 4, -1), "tower_top", rotation, true));
			} else {
				if (i != 7) {
					return EndCityGenerator.method_14673(arg, EndCityGenerator.field_14384, i + 1, lv, null, list, random);
				}

				lv = EndCityGenerator.method_14681(list, EndCityGenerator.method_14684(arg, lv, new BlockPos(-1, 4, -1), "tower_top", rotation, true));
			}

			return true;
		}
	};
	private static final EndCityGenerator.class_3344 field_14387 = new EndCityGenerator.class_3344() {
		public boolean field_14394;

		@Override
		public void method_14688() {
			this.field_14394 = false;
		}

		@Override
		public boolean method_14687(class_3485 arg, int i, EndCityGenerator.class_3343 arg2, BlockPos blockPos, List<class_3443> list, Random random) {
			Rotation rotation = arg2.field_15434.method_15113();
			int j = random.nextInt(4) + 1;
			EndCityGenerator.class_3343 lv = EndCityGenerator.method_14681(
				list, EndCityGenerator.method_14684(arg, arg2, new BlockPos(0, 0, -4), "bridge_piece", rotation, true)
			);
			lv.field_15316 = -1;
			int k = 0;

			for (int l = 0; l < j; l++) {
				if (random.nextBoolean()) {
					lv = EndCityGenerator.method_14681(list, EndCityGenerator.method_14684(arg, lv, new BlockPos(0, k, -4), "bridge_piece", rotation, true));
					k = 0;
				} else {
					if (random.nextBoolean()) {
						lv = EndCityGenerator.method_14681(list, EndCityGenerator.method_14684(arg, lv, new BlockPos(0, k, -4), "bridge_steep_stairs", rotation, true));
					} else {
						lv = EndCityGenerator.method_14681(list, EndCityGenerator.method_14684(arg, lv, new BlockPos(0, k, -8), "bridge_gentle_stairs", rotation, true));
					}

					k = 4;
				}
			}

			if (!this.field_14394 && random.nextInt(10 - i) == 0) {
				EndCityGenerator.method_14681(
					list, EndCityGenerator.method_14684(arg, lv, new BlockPos(-8 + random.nextInt(8), k, -70 + random.nextInt(10)), "ship", rotation, true)
				);
				this.field_14394 = true;
			} else if (!EndCityGenerator.method_14673(arg, EndCityGenerator.field_14390, i + 1, lv, new BlockPos(-3, k + 1, -11), list, random)) {
				return false;
			}

			lv = EndCityGenerator.method_14681(
				list, EndCityGenerator.method_14684(arg, lv, new BlockPos(4, k, 0), "bridge_end", rotation.method_10501(Rotation.ROT_180), true)
			);
			lv.field_15316 = -1;
			return true;
		}
	};
	private static final List<class_3545<Rotation, BlockPos>> field_14388 = Lists.<class_3545<Rotation, BlockPos>>newArrayList(
		new class_3545<>(Rotation.ROT_0, new BlockPos(4, -1, 0)),
		new class_3545<>(Rotation.ROT_90, new BlockPos(12, -1, 4)),
		new class_3545<>(Rotation.ROT_270, new BlockPos(0, -1, 8)),
		new class_3545<>(Rotation.ROT_180, new BlockPos(8, -1, 12))
	);
	private static final EndCityGenerator.class_3344 field_14384 = new EndCityGenerator.class_3344() {
		@Override
		public void method_14688() {
		}

		@Override
		public boolean method_14687(class_3485 arg, int i, EndCityGenerator.class_3343 arg2, BlockPos blockPos, List<class_3443> list, Random random) {
			Rotation rotation = arg2.field_15434.method_15113();
			EndCityGenerator.class_3343 lv = EndCityGenerator.method_14681(
				list, EndCityGenerator.method_14684(arg, arg2, new BlockPos(-3, 4, -3), "fat_tower_base", rotation, true)
			);
			lv = EndCityGenerator.method_14681(list, EndCityGenerator.method_14684(arg, lv, new BlockPos(0, 4, 0), "fat_tower_middle", rotation, true));

			for (int j = 0; j < 2 && random.nextInt(3) != 0; j++) {
				lv = EndCityGenerator.method_14681(list, EndCityGenerator.method_14684(arg, lv, new BlockPos(0, 8, 0), "fat_tower_middle", rotation, true));

				for (class_3545<Rotation, BlockPos> lv2 : EndCityGenerator.field_14388) {
					if (random.nextBoolean()) {
						EndCityGenerator.class_3343 lv3 = EndCityGenerator.method_14681(
							list, EndCityGenerator.method_14684(arg, lv, lv2.method_15441(), "bridge_end", rotation.method_10501(lv2.method_15442()), true)
						);
						EndCityGenerator.method_14673(arg, EndCityGenerator.field_14387, i + 1, lv3, null, list, random);
					}
				}
			}

			lv = EndCityGenerator.method_14681(list, EndCityGenerator.method_14684(arg, lv, new BlockPos(-2, 8, -2), "fat_tower_top", rotation, true));
			return true;
		}
	};

	private static EndCityGenerator.class_3343 method_14684(
		class_3485 arg, EndCityGenerator.class_3343 arg2, BlockPos blockPos, String string, Rotation rotation, boolean bl
	) {
		EndCityGenerator.class_3343 lv = new EndCityGenerator.class_3343(arg, string, arg2.field_15432, rotation, bl);
		BlockPos blockPos2 = arg2.field_15433.method_15180(arg2.field_15434, blockPos, lv.field_15434, BlockPos.ORIGIN);
		lv.translate(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
		return lv;
	}

	public static void method_14679(class_3485 arg, BlockPos blockPos, Rotation rotation, List<class_3443> list, Random random) {
		field_14384.method_14688();
		field_14390.method_14688();
		field_14387.method_14688();
		field_14386.method_14688();
		EndCityGenerator.class_3343 lv = method_14681(list, new EndCityGenerator.class_3343(arg, "base_floor", blockPos, rotation, true));
		lv = method_14681(list, method_14684(arg, lv, new BlockPos(-1, 0, -1), "second_floor_1", rotation, false));
		lv = method_14681(list, method_14684(arg, lv, new BlockPos(-1, 4, -1), "third_floor_1", rotation, false));
		lv = method_14681(list, method_14684(arg, lv, new BlockPos(-1, 8, -1), "third_roof", rotation, true));
		method_14673(arg, field_14386, 1, lv, null, list, random);
	}

	private static EndCityGenerator.class_3343 method_14681(List<class_3443> list, EndCityGenerator.class_3343 arg) {
		list.add(arg);
		return arg;
	}

	private static boolean method_14673(
		class_3485 arg, EndCityGenerator.class_3344 arg2, int i, EndCityGenerator.class_3343 arg3, BlockPos blockPos, List<class_3443> list, Random random
	) {
		if (i > 8) {
			return false;
		} else {
			List<class_3443> list2 = Lists.<class_3443>newArrayList();
			if (arg2.method_14687(arg, i, arg3, blockPos, list2, random)) {
				boolean bl = false;
				int j = random.nextInt();

				for (class_3443 lv : list2) {
					lv.field_15316 = j;
					class_3443 lv2 = class_3443.method_14932(list, lv.method_14935());
					if (lv2 != null && lv2.field_15316 != arg3.field_15316) {
						bl = true;
						break;
					}
				}

				if (!bl) {
					list.addAll(list2);
					return true;
				}
			}

			return false;
		}
	}

	public static class class_3343 extends class_3470 {
		private final String field_14391;
		private final Rotation field_14393;
		private final boolean field_14392;

		public class_3343(class_3485 arg, String string, BlockPos blockPos, Rotation rotation, boolean bl) {
			super(StructurePiece.field_16936, 0);
			this.field_14391 = string;
			this.field_15432 = blockPos;
			this.field_14393 = rotation;
			this.field_14392 = bl;
			this.method_14686(arg);
		}

		public class_3343(class_3485 arg, CompoundTag compoundTag) {
			super(StructurePiece.field_16936, compoundTag);
			this.field_14391 = compoundTag.getString("Template");
			this.field_14393 = Rotation.valueOf(compoundTag.getString("Rot"));
			this.field_14392 = compoundTag.getBoolean("OW");
			this.method_14686(arg);
		}

		private void method_14686(class_3485 arg) {
			class_3499 lv = arg.method_15091(new Identifier("end_city/" + this.field_14391));
			class_3492 lv2 = (this.field_14392 ? EndCityGenerator.field_14383 : EndCityGenerator.field_14389).method_15128().method_15123(this.field_14393);
			this.method_15027(lv, this.field_15432, lv2);
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putString("Template", this.field_14391);
			compoundTag.putString("Rot", this.field_14393.name());
			compoundTag.putBoolean("OW", this.field_14392);
		}

		@Override
		protected void method_15026(String string, BlockPos blockPos, IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox) {
			if (string.startsWith("Chest")) {
				BlockPos blockPos2 = blockPos.down();
				if (mutableIntBoundingBox.contains(blockPos2)) {
					LootableContainerBlockEntity.method_11287(iWorld, random, blockPos2, LootTables.CHEST_END_CITY_TREASURE);
				}
			} else if (string.startsWith("Sentry")) {
				ShulkerEntity shulkerEntity = new ShulkerEntity(iWorld.method_8410());
				shulkerEntity.setPosition((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
				shulkerEntity.setAttachedBlock(blockPos);
				iWorld.spawnEntity(shulkerEntity);
			} else if (string.startsWith("Elytra")) {
				ItemFrameEntity itemFrameEntity = new ItemFrameEntity(iWorld.method_8410(), blockPos, this.field_14393.method_10503(Direction.SOUTH));
				itemFrameEntity.setHeldItemStack(new ItemStack(Items.field_8833), false);
				iWorld.spawnEntity(itemFrameEntity);
			}
		}
	}

	interface class_3344 {
		void method_14688();

		boolean method_14687(class_3485 arg, int i, EndCityGenerator.class_3343 arg2, BlockPos blockPos, List<class_3443> list, Random random);
	}
}
