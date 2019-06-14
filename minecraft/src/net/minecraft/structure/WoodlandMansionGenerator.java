package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.loot.LootTables;

public class WoodlandMansionGenerator {
	public static void method_15029(
		StructureManager structureManager, BlockPos blockPos, BlockRotation blockRotation, List<WoodlandMansionGenerator.Piece> list, Random random
	) {
		WoodlandMansionGenerator.class_3474 lv = new WoodlandMansionGenerator.class_3474(random);
		WoodlandMansionGenerator.LayoutGenerator layoutGenerator = new WoodlandMansionGenerator.LayoutGenerator(structureManager, random);
		layoutGenerator.generate(blockPos, blockRotation, list, lv);
	}

	static class FirstFloorRoomPool extends WoodlandMansionGenerator.RoomPool {
		private FirstFloorRoomPool() {
		}

		@Override
		public String getSmallRoom(Random random) {
			return "1x1_a" + (random.nextInt(5) + 1);
		}

		@Override
		public String getSmallSecretRoom(Random random) {
			return "1x1_as" + (random.nextInt(4) + 1);
		}

		@Override
		public String getMediumFunctionalRoom(Random random, boolean bl) {
			return "1x2_a" + (random.nextInt(9) + 1);
		}

		@Override
		public String getMediumGenericRoom(Random random, boolean bl) {
			return "1x2_b" + (random.nextInt(5) + 1);
		}

		@Override
		public String getMediumSecretRoom(Random random) {
			return "1x2_s" + (random.nextInt(2) + 1);
		}

		@Override
		public String getBigRoom(Random random) {
			return "2x2_a" + (random.nextInt(4) + 1);
		}

		@Override
		public String getBigSecretRoom(Random random) {
			return "2x2_s1";
		}
	}

	static class GenerationPiece {
		public BlockRotation rotation;
		public BlockPos position;
		public String template;

		private GenerationPiece() {
		}
	}

	static class LayoutGenerator {
		private final StructureManager field_15444;
		private final Random random;
		private int field_15446;
		private int field_15445;

		public LayoutGenerator(StructureManager structureManager, Random random) {
			this.field_15444 = structureManager;
			this.random = random;
		}

		public void generate(BlockPos blockPos, BlockRotation blockRotation, List<WoodlandMansionGenerator.Piece> list, WoodlandMansionGenerator.class_3474 arg) {
			WoodlandMansionGenerator.GenerationPiece generationPiece = new WoodlandMansionGenerator.GenerationPiece();
			generationPiece.position = blockPos;
			generationPiece.rotation = blockRotation;
			generationPiece.template = "wall_flat";
			WoodlandMansionGenerator.GenerationPiece generationPiece2 = new WoodlandMansionGenerator.GenerationPiece();
			this.addEntrance(list, generationPiece);
			generationPiece2.position = generationPiece.position.up(8);
			generationPiece2.rotation = generationPiece.rotation;
			generationPiece2.template = "wall_window";
			if (!list.isEmpty()) {
			}

			WoodlandMansionGenerator.class_3478 lv = arg.field_15440;
			WoodlandMansionGenerator.class_3478 lv2 = arg.field_15439;
			this.field_15446 = arg.field_15442 + 1;
			this.field_15445 = arg.field_15441 + 1;
			int i = arg.field_15442 + 1;
			int j = arg.field_15441;
			this.addRoof(list, generationPiece, lv, Direction.field_11035, this.field_15446, this.field_15445, i, j);
			this.addRoof(list, generationPiece2, lv, Direction.field_11035, this.field_15446, this.field_15445, i, j);
			WoodlandMansionGenerator.GenerationPiece generationPiece3 = new WoodlandMansionGenerator.GenerationPiece();
			generationPiece3.position = generationPiece.position.up(19);
			generationPiece3.rotation = generationPiece.rotation;
			generationPiece3.template = "wall_window";
			boolean bl = false;

			for (int k = 0; k < lv2.field_15453 && !bl; k++) {
				for (int l = lv2.field_15454 - 1; l >= 0 && !bl; l--) {
					if (WoodlandMansionGenerator.class_3474.method_15047(lv2, l, k)) {
						generationPiece3.position = generationPiece3.position.offset(blockRotation.rotate(Direction.field_11035), 8 + (k - this.field_15445) * 8);
						generationPiece3.position = generationPiece3.position.offset(blockRotation.rotate(Direction.field_11034), (l - this.field_15446) * 8);
						this.method_15052(list, generationPiece3);
						this.addRoof(list, generationPiece3, lv2, Direction.field_11035, l, k, l, k);
						bl = true;
					}
				}
			}

			this.method_15055(list, blockPos.up(16), blockRotation, lv, lv2);
			this.method_15055(list, blockPos.up(27), blockRotation, lv2, null);
			if (!list.isEmpty()) {
			}

			WoodlandMansionGenerator.RoomPool[] roomPools = new WoodlandMansionGenerator.RoomPool[]{
				new WoodlandMansionGenerator.FirstFloorRoomPool(), new WoodlandMansionGenerator.SecondFloorRoomPool(), new WoodlandMansionGenerator.ThirdFloorRoomPool()
			};

			for (int lx = 0; lx < 3; lx++) {
				BlockPos blockPos2 = blockPos.up(8 * lx + (lx == 2 ? 3 : 0));
				WoodlandMansionGenerator.class_3478 lv3 = arg.field_15443[lx];
				WoodlandMansionGenerator.class_3478 lv4 = lx == 2 ? lv2 : lv;
				String string = lx == 0 ? "carpet_south_1" : "carpet_south_2";
				String string2 = lx == 0 ? "carpet_west_1" : "carpet_west_2";

				for (int m = 0; m < lv4.field_15453; m++) {
					for (int n = 0; n < lv4.field_15454; n++) {
						if (lv4.method_15066(n, m) == 1) {
							BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.field_11035), 8 + (m - this.field_15445) * 8);
							blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.field_11034), (n - this.field_15446) * 8);
							list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "corridor_floor", blockPos3, blockRotation));
							if (lv4.method_15066(n, m - 1) == 1 || (lv3.method_15066(n, m - 1) & 8388608) == 8388608) {
								list.add(
									new WoodlandMansionGenerator.Piece(
										this.field_15444, "carpet_north", blockPos3.offset(blockRotation.rotate(Direction.field_11034), 1).up(), blockRotation
									)
								);
							}

							if (lv4.method_15066(n + 1, m) == 1 || (lv3.method_15066(n + 1, m) & 8388608) == 8388608) {
								list.add(
									new WoodlandMansionGenerator.Piece(
										this.field_15444,
										"carpet_east",
										blockPos3.offset(blockRotation.rotate(Direction.field_11035), 1).offset(blockRotation.rotate(Direction.field_11034), 5).up(),
										blockRotation
									)
								);
							}

							if (lv4.method_15066(n, m + 1) == 1 || (lv3.method_15066(n, m + 1) & 8388608) == 8388608) {
								list.add(
									new WoodlandMansionGenerator.Piece(
										this.field_15444,
										string,
										blockPos3.offset(blockRotation.rotate(Direction.field_11035), 5).offset(blockRotation.rotate(Direction.field_11039), 1),
										blockRotation
									)
								);
							}

							if (lv4.method_15066(n - 1, m) == 1 || (lv3.method_15066(n - 1, m) & 8388608) == 8388608) {
								list.add(
									new WoodlandMansionGenerator.Piece(
										this.field_15444,
										string2,
										blockPos3.offset(blockRotation.rotate(Direction.field_11039), 1).offset(blockRotation.rotate(Direction.field_11043), 1),
										blockRotation
									)
								);
							}
						}
					}
				}

				String string3 = lx == 0 ? "indoors_wall_1" : "indoors_wall_2";
				String string4 = lx == 0 ? "indoors_door_1" : "indoors_door_2";
				List<Direction> list2 = Lists.<Direction>newArrayList();

				for (int o = 0; o < lv4.field_15453; o++) {
					for (int p = 0; p < lv4.field_15454; p++) {
						boolean bl2 = lx == 2 && lv4.method_15066(p, o) == 3;
						if (lv4.method_15066(p, o) == 2 || bl2) {
							int q = lv3.method_15066(p, o);
							int r = q & 983040;
							int s = q & 65535;
							bl2 = bl2 && (q & 8388608) == 8388608;
							list2.clear();
							if ((q & 2097152) == 2097152) {
								for (Direction direction : Direction.Type.field_11062) {
									if (lv4.method_15066(p + direction.getOffsetX(), o + direction.getOffsetZ()) == 1) {
										list2.add(direction);
									}
								}
							}

							Direction direction2 = null;
							if (!list2.isEmpty()) {
								direction2 = (Direction)list2.get(this.random.nextInt(list2.size()));
							} else if ((q & 1048576) == 1048576) {
								direction2 = Direction.field_11036;
							}

							BlockPos blockPos4 = blockPos2.offset(blockRotation.rotate(Direction.field_11035), 8 + (o - this.field_15445) * 8);
							blockPos4 = blockPos4.offset(blockRotation.rotate(Direction.field_11034), -1 + (p - this.field_15446) * 8);
							if (WoodlandMansionGenerator.class_3474.method_15047(lv4, p - 1, o) && !arg.method_15039(lv4, p - 1, o, lx, s)) {
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, direction2 == Direction.field_11039 ? string4 : string3, blockPos4, blockRotation));
							}

							if (lv4.method_15066(p + 1, o) == 1 && !bl2) {
								BlockPos blockPos5 = blockPos4.offset(blockRotation.rotate(Direction.field_11034), 8);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, direction2 == Direction.field_11034 ? string4 : string3, blockPos5, blockRotation));
							}

							if (WoodlandMansionGenerator.class_3474.method_15047(lv4, p, o + 1) && !arg.method_15039(lv4, p, o + 1, lx, s)) {
								BlockPos blockPos5 = blockPos4.offset(blockRotation.rotate(Direction.field_11035), 7);
								blockPos5 = blockPos5.offset(blockRotation.rotate(Direction.field_11034), 7);
								list.add(
									new WoodlandMansionGenerator.Piece(
										this.field_15444, direction2 == Direction.field_11035 ? string4 : string3, blockPos5, blockRotation.rotate(BlockRotation.field_11463)
									)
								);
							}

							if (lv4.method_15066(p, o - 1) == 1 && !bl2) {
								BlockPos blockPos5 = blockPos4.offset(blockRotation.rotate(Direction.field_11043), 1);
								blockPos5 = blockPos5.offset(blockRotation.rotate(Direction.field_11034), 7);
								list.add(
									new WoodlandMansionGenerator.Piece(
										this.field_15444, direction2 == Direction.field_11043 ? string4 : string3, blockPos5, blockRotation.rotate(BlockRotation.field_11463)
									)
								);
							}

							if (r == 65536) {
								this.addSmallRoom(list, blockPos4, blockRotation, direction2, roomPools[lx]);
							} else if (r == 131072 && direction2 != null) {
								Direction direction3 = arg.method_15040(lv4, p, o, lx, s);
								boolean bl3 = (q & 4194304) == 4194304;
								this.addMediumRoom(list, blockPos4, blockRotation, direction3, direction2, roomPools[lx], bl3);
							} else if (r == 262144 && direction2 != null && direction2 != Direction.field_11036) {
								Direction direction3 = direction2.rotateYClockwise();
								if (!arg.method_15039(lv4, p + direction3.getOffsetX(), o + direction3.getOffsetZ(), lx, s)) {
									direction3 = direction3.getOpposite();
								}

								this.addBigRoom(list, blockPos4, blockRotation, direction3, direction2, roomPools[lx]);
							} else if (r == 262144 && direction2 == Direction.field_11036) {
								this.addBigSecretRoom(list, blockPos4, blockRotation, roomPools[lx]);
							}
						}
					}
				}
			}
		}

		private void addRoof(
			List<WoodlandMansionGenerator.Piece> list,
			WoodlandMansionGenerator.GenerationPiece generationPiece,
			WoodlandMansionGenerator.class_3478 arg,
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
				if (!WoodlandMansionGenerator.class_3474.method_15047(arg, m + direction.getOffsetX(), n + direction.getOffsetZ())) {
					this.method_15058(list, generationPiece);
					direction = direction.rotateYClockwise();
					if (m != k || n != l || direction2 != direction) {
						this.method_15052(list, generationPiece);
					}
				} else if (WoodlandMansionGenerator.class_3474.method_15047(arg, m + direction.getOffsetX(), n + direction.getOffsetZ())
					&& WoodlandMansionGenerator.class_3474.method_15047(
						arg,
						m + direction.getOffsetX() + direction.rotateYCounterclockwise().getOffsetX(),
						n + direction.getOffsetZ() + direction.rotateYCounterclockwise().getOffsetZ()
					)) {
					this.method_15060(list, generationPiece);
					m += direction.getOffsetX();
					n += direction.getOffsetZ();
					direction = direction.rotateYCounterclockwise();
				} else {
					m += direction.getOffsetX();
					n += direction.getOffsetZ();
					if (m != k || n != l || direction2 != direction) {
						this.method_15052(list, generationPiece);
					}
				}
			} while (m != k || n != l || direction2 != direction);
		}

		private void method_15055(
			List<WoodlandMansionGenerator.Piece> list,
			BlockPos blockPos,
			BlockRotation blockRotation,
			WoodlandMansionGenerator.class_3478 arg,
			@Nullable WoodlandMansionGenerator.class_3478 arg2
		) {
			for (int i = 0; i < arg.field_15453; i++) {
				for (int j = 0; j < arg.field_15454; j++) {
					BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11035), 8 + (i - this.field_15445) * 8);
					blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.field_11034), (j - this.field_15446) * 8);
					boolean bl = arg2 != null && WoodlandMansionGenerator.class_3474.method_15047(arg2, j, i);
					if (WoodlandMansionGenerator.class_3474.method_15047(arg, j, i) && !bl) {
						list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof", blockPos2.up(3), blockRotation));
						if (!WoodlandMansionGenerator.class_3474.method_15047(arg, j + 1, i)) {
							BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.field_11034), 6);
							list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_front", blockPos3, blockRotation));
						}

						if (!WoodlandMansionGenerator.class_3474.method_15047(arg, j - 1, i)) {
							BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.field_11034), 0);
							blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.field_11035), 7);
							list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_front", blockPos3, blockRotation.rotate(BlockRotation.field_11464)));
						}

						if (!WoodlandMansionGenerator.class_3474.method_15047(arg, j, i - 1)) {
							BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.field_11039), 1);
							list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_front", blockPos3, blockRotation.rotate(BlockRotation.field_11465)));
						}

						if (!WoodlandMansionGenerator.class_3474.method_15047(arg, j, i + 1)) {
							BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.field_11034), 6);
							blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.field_11035), 6);
							list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_front", blockPos3, blockRotation.rotate(BlockRotation.field_11463)));
						}
					}
				}
			}

			if (arg2 != null) {
				for (int i = 0; i < arg.field_15453; i++) {
					for (int jx = 0; jx < arg.field_15454; jx++) {
						BlockPos var17 = blockPos.offset(blockRotation.rotate(Direction.field_11035), 8 + (i - this.field_15445) * 8);
						var17 = var17.offset(blockRotation.rotate(Direction.field_11034), (jx - this.field_15446) * 8);
						boolean bl = WoodlandMansionGenerator.class_3474.method_15047(arg2, jx, i);
						if (WoodlandMansionGenerator.class_3474.method_15047(arg, jx, i) && bl) {
							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx + 1, i)) {
								BlockPos blockPos3 = var17.offset(blockRotation.rotate(Direction.field_11034), 7);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "small_wall", blockPos3, blockRotation));
							}

							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx - 1, i)) {
								BlockPos blockPos3 = var17.offset(blockRotation.rotate(Direction.field_11039), 1);
								blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.field_11035), 6);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "small_wall", blockPos3, blockRotation.rotate(BlockRotation.field_11464)));
							}

							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx, i - 1)) {
								BlockPos blockPos3 = var17.offset(blockRotation.rotate(Direction.field_11039), 0);
								blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.field_11043), 1);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "small_wall", blockPos3, blockRotation.rotate(BlockRotation.field_11465)));
							}

							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx, i + 1)) {
								BlockPos blockPos3 = var17.offset(blockRotation.rotate(Direction.field_11034), 6);
								blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.field_11035), 7);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "small_wall", blockPos3, blockRotation.rotate(BlockRotation.field_11463)));
							}

							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx + 1, i)) {
								if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx, i - 1)) {
									BlockPos blockPos3 = var17.offset(blockRotation.rotate(Direction.field_11034), 7);
									blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.field_11043), 2);
									list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "small_wall_corner", blockPos3, blockRotation));
								}

								if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx, i + 1)) {
									BlockPos blockPos3 = var17.offset(blockRotation.rotate(Direction.field_11034), 8);
									blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.field_11035), 7);
									list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "small_wall_corner", blockPos3, blockRotation.rotate(BlockRotation.field_11463)));
								}
							}

							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx - 1, i)) {
								if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx, i - 1)) {
									BlockPos blockPos3 = var17.offset(blockRotation.rotate(Direction.field_11039), 2);
									blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.field_11043), 1);
									list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "small_wall_corner", blockPos3, blockRotation.rotate(BlockRotation.field_11465)));
								}

								if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jx, i + 1)) {
									BlockPos blockPos3 = var17.offset(blockRotation.rotate(Direction.field_11039), 1);
									blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.field_11035), 8);
									list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "small_wall_corner", blockPos3, blockRotation.rotate(BlockRotation.field_11464)));
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < arg.field_15453; i++) {
				for (int jxx = 0; jxx < arg.field_15454; jxx++) {
					BlockPos var19 = blockPos.offset(blockRotation.rotate(Direction.field_11035), 8 + (i - this.field_15445) * 8);
					var19 = var19.offset(blockRotation.rotate(Direction.field_11034), (jxx - this.field_15446) * 8);
					boolean bl = arg2 != null && WoodlandMansionGenerator.class_3474.method_15047(arg2, jxx, i);
					if (WoodlandMansionGenerator.class_3474.method_15047(arg, jxx, i) && !bl) {
						if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jxx + 1, i)) {
							BlockPos blockPos3 = var19.offset(blockRotation.rotate(Direction.field_11034), 6);
							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jxx, i + 1)) {
								BlockPos blockPos4 = blockPos3.offset(blockRotation.rotate(Direction.field_11035), 6);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_corner", blockPos4, blockRotation));
							} else if (WoodlandMansionGenerator.class_3474.method_15047(arg, jxx + 1, i + 1)) {
								BlockPos blockPos4 = blockPos3.offset(blockRotation.rotate(Direction.field_11035), 5);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_inner_corner", blockPos4, blockRotation));
							}

							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jxx, i - 1)) {
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_corner", blockPos3, blockRotation.rotate(BlockRotation.field_11465)));
							} else if (WoodlandMansionGenerator.class_3474.method_15047(arg, jxx + 1, i - 1)) {
								BlockPos blockPos4 = var19.offset(blockRotation.rotate(Direction.field_11034), 9);
								blockPos4 = blockPos4.offset(blockRotation.rotate(Direction.field_11043), 2);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_inner_corner", blockPos4, blockRotation.rotate(BlockRotation.field_11463)));
							}
						}

						if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jxx - 1, i)) {
							BlockPos blockPos3x = var19.offset(blockRotation.rotate(Direction.field_11034), 0);
							blockPos3x = blockPos3x.offset(blockRotation.rotate(Direction.field_11035), 0);
							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jxx, i + 1)) {
								BlockPos blockPos4 = blockPos3x.offset(blockRotation.rotate(Direction.field_11035), 6);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_corner", blockPos4, blockRotation.rotate(BlockRotation.field_11463)));
							} else if (WoodlandMansionGenerator.class_3474.method_15047(arg, jxx - 1, i + 1)) {
								BlockPos blockPos4 = blockPos3x.offset(blockRotation.rotate(Direction.field_11035), 8);
								blockPos4 = blockPos4.offset(blockRotation.rotate(Direction.field_11039), 3);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_inner_corner", blockPos4, blockRotation.rotate(BlockRotation.field_11465)));
							}

							if (!WoodlandMansionGenerator.class_3474.method_15047(arg, jxx, i - 1)) {
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_corner", blockPos3x, blockRotation.rotate(BlockRotation.field_11464)));
							} else if (WoodlandMansionGenerator.class_3474.method_15047(arg, jxx - 1, i - 1)) {
								BlockPos blockPos4 = blockPos3x.offset(blockRotation.rotate(Direction.field_11035), 1);
								list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "roof_inner_corner", blockPos4, blockRotation.rotate(BlockRotation.field_11464)));
							}
						}
					}
				}
			}
		}

		private void addEntrance(List<WoodlandMansionGenerator.Piece> list, WoodlandMansionGenerator.GenerationPiece generationPiece) {
			Direction direction = generationPiece.rotation.rotate(Direction.field_11039);
			list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "entrance", generationPiece.position.offset(direction, 9), generationPiece.rotation));
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.field_11035), 16);
		}

		private void method_15052(List<WoodlandMansionGenerator.Piece> list, WoodlandMansionGenerator.GenerationPiece generationPiece) {
			list.add(
				new WoodlandMansionGenerator.Piece(
					this.field_15444,
					generationPiece.template,
					generationPiece.position.offset(generationPiece.rotation.rotate(Direction.field_11034), 7),
					generationPiece.rotation
				)
			);
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.field_11035), 8);
		}

		private void method_15058(List<WoodlandMansionGenerator.Piece> list, WoodlandMansionGenerator.GenerationPiece generationPiece) {
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.field_11035), -1);
			list.add(new WoodlandMansionGenerator.Piece(this.field_15444, "wall_corner", generationPiece.position, generationPiece.rotation));
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.field_11035), -7);
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.field_11039), -6);
			generationPiece.rotation = generationPiece.rotation.rotate(BlockRotation.field_11463);
		}

		private void method_15060(List<WoodlandMansionGenerator.Piece> list, WoodlandMansionGenerator.GenerationPiece generationPiece) {
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.field_11035), 6);
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.field_11034), 8);
			generationPiece.rotation = generationPiece.rotation.rotate(BlockRotation.field_11465);
		}

		private void addSmallRoom(
			List<WoodlandMansionGenerator.Piece> list, BlockPos blockPos, BlockRotation blockRotation, Direction direction, WoodlandMansionGenerator.RoomPool roomPool
		) {
			BlockRotation blockRotation2 = BlockRotation.field_11467;
			String string = roomPool.getSmallRoom(this.random);
			if (direction != Direction.field_11034) {
				if (direction == Direction.field_11043) {
					blockRotation2 = blockRotation2.rotate(BlockRotation.field_11465);
				} else if (direction == Direction.field_11039) {
					blockRotation2 = blockRotation2.rotate(BlockRotation.field_11464);
				} else if (direction == Direction.field_11035) {
					blockRotation2 = blockRotation2.rotate(BlockRotation.field_11463);
				} else {
					string = roomPool.getSmallSecretRoom(this.random);
				}
			}

			BlockPos blockPos2 = Structure.method_15162(new BlockPos(1, 0, 0), BlockMirror.field_11302, blockRotation2, 7, 7);
			blockRotation2 = blockRotation2.rotate(blockRotation);
			blockPos2 = blockPos2.rotate(blockRotation);
			BlockPos blockPos3 = blockPos.add(blockPos2.getX(), 0, blockPos2.getZ());
			list.add(new WoodlandMansionGenerator.Piece(this.field_15444, string, blockPos3, blockRotation2));
		}

		private void addMediumRoom(
			List<WoodlandMansionGenerator.Piece> list,
			BlockPos blockPos,
			BlockRotation blockRotation,
			Direction direction,
			Direction direction2,
			WoodlandMansionGenerator.RoomPool roomPool,
			boolean bl
		) {
			if (direction2 == Direction.field_11034 && direction == Direction.field_11035) {
				BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 1);
				list.add(new WoodlandMansionGenerator.Piece(this.field_15444, roomPool.getMediumFunctionalRoom(this.random, bl), blockPos2, blockRotation));
			} else if (direction2 == Direction.field_11034 && direction == Direction.field_11043) {
				BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 1);
				blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.field_11035), 6);
				list.add(
					new WoodlandMansionGenerator.Piece(this.field_15444, roomPool.getMediumFunctionalRoom(this.random, bl), blockPos2, blockRotation, BlockMirror.field_11300)
				);
			} else if (direction2 == Direction.field_11039 && direction == Direction.field_11043) {
				BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 7);
				blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.field_11035), 6);
				list.add(
					new WoodlandMansionGenerator.Piece(
						this.field_15444, roomPool.getMediumFunctionalRoom(this.random, bl), blockPos2, blockRotation.rotate(BlockRotation.field_11464)
					)
				);
			} else if (direction2 == Direction.field_11039 && direction == Direction.field_11035) {
				BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 7);
				list.add(
					new WoodlandMansionGenerator.Piece(this.field_15444, roomPool.getMediumFunctionalRoom(this.random, bl), blockPos2, blockRotation, BlockMirror.field_11301)
				);
			} else if (direction2 == Direction.field_11035 && direction == Direction.field_11034) {
				BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 1);
				list.add(
					new WoodlandMansionGenerator.Piece(
						this.field_15444, roomPool.getMediumFunctionalRoom(this.random, bl), blockPos2, blockRotation.rotate(BlockRotation.field_11463), BlockMirror.field_11300
					)
				);
			} else if (direction2 == Direction.field_11035 && direction == Direction.field_11039) {
				BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 7);
				list.add(
					new WoodlandMansionGenerator.Piece(
						this.field_15444, roomPool.getMediumFunctionalRoom(this.random, bl), blockPos2, blockRotation.rotate(BlockRotation.field_11463)
					)
				);
			} else if (direction2 == Direction.field_11043 && direction == Direction.field_11039) {
				BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 7);
				blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.field_11035), 6);
				list.add(
					new WoodlandMansionGenerator.Piece(
						this.field_15444, roomPool.getMediumFunctionalRoom(this.random, bl), blockPos2, blockRotation.rotate(BlockRotation.field_11463), BlockMirror.field_11301
					)
				);
			} else if (direction2 == Direction.field_11043 && direction == Direction.field_11034) {
				BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 1);
				blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.field_11035), 6);
				list.add(
					new WoodlandMansionGenerator.Piece(
						this.field_15444, roomPool.getMediumFunctionalRoom(this.random, bl), blockPos2, blockRotation.rotate(BlockRotation.field_11465)
					)
				);
			} else if (direction2 == Direction.field_11035 && direction == Direction.field_11043) {
				BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 1);
				blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.field_11043), 8);
				list.add(new WoodlandMansionGenerator.Piece(this.field_15444, roomPool.getMediumGenericRoom(this.random, bl), blockPos2, blockRotation));
			} else if (direction2 == Direction.field_11043 && direction == Direction.field_11035) {
				BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 7);
				blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.field_11035), 14);
				list.add(
					new WoodlandMansionGenerator.Piece(
						this.field_15444, roomPool.getMediumGenericRoom(this.random, bl), blockPos2, blockRotation.rotate(BlockRotation.field_11464)
					)
				);
			} else if (direction2 == Direction.field_11039 && direction == Direction.field_11034) {
				BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 15);
				list.add(
					new WoodlandMansionGenerator.Piece(
						this.field_15444, roomPool.getMediumGenericRoom(this.random, bl), blockPos2, blockRotation.rotate(BlockRotation.field_11463)
					)
				);
			} else if (direction2 == Direction.field_11034 && direction == Direction.field_11039) {
				BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11039), 7);
				blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.field_11035), 6);
				list.add(
					new WoodlandMansionGenerator.Piece(
						this.field_15444, roomPool.getMediumGenericRoom(this.random, bl), blockPos2, blockRotation.rotate(BlockRotation.field_11465)
					)
				);
			} else if (direction2 == Direction.field_11036 && direction == Direction.field_11034) {
				BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 15);
				list.add(
					new WoodlandMansionGenerator.Piece(this.field_15444, roomPool.getMediumSecretRoom(this.random), blockPos2, blockRotation.rotate(BlockRotation.field_11463))
				);
			} else if (direction2 == Direction.field_11036 && direction == Direction.field_11035) {
				BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 1);
				blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.field_11043), 0);
				list.add(new WoodlandMansionGenerator.Piece(this.field_15444, roomPool.getMediumSecretRoom(this.random), blockPos2, blockRotation));
			}
		}

		private void addBigRoom(
			List<WoodlandMansionGenerator.Piece> list,
			BlockPos blockPos,
			BlockRotation blockRotation,
			Direction direction,
			Direction direction2,
			WoodlandMansionGenerator.RoomPool roomPool
		) {
			int i = 0;
			int j = 0;
			BlockRotation blockRotation2 = blockRotation;
			BlockMirror blockMirror = BlockMirror.field_11302;
			if (direction2 == Direction.field_11034 && direction == Direction.field_11035) {
				i = -7;
			} else if (direction2 == Direction.field_11034 && direction == Direction.field_11043) {
				i = -7;
				j = 6;
				blockMirror = BlockMirror.field_11300;
			} else if (direction2 == Direction.field_11043 && direction == Direction.field_11034) {
				i = 1;
				j = 14;
				blockRotation2 = blockRotation.rotate(BlockRotation.field_11465);
			} else if (direction2 == Direction.field_11043 && direction == Direction.field_11039) {
				i = 7;
				j = 14;
				blockRotation2 = blockRotation.rotate(BlockRotation.field_11465);
				blockMirror = BlockMirror.field_11300;
			} else if (direction2 == Direction.field_11035 && direction == Direction.field_11039) {
				i = 7;
				j = -8;
				blockRotation2 = blockRotation.rotate(BlockRotation.field_11463);
			} else if (direction2 == Direction.field_11035 && direction == Direction.field_11034) {
				i = 1;
				j = -8;
				blockRotation2 = blockRotation.rotate(BlockRotation.field_11463);
				blockMirror = BlockMirror.field_11300;
			} else if (direction2 == Direction.field_11039 && direction == Direction.field_11043) {
				i = 15;
				j = 6;
				blockRotation2 = blockRotation.rotate(BlockRotation.field_11464);
			} else if (direction2 == Direction.field_11039 && direction == Direction.field_11035) {
				i = 15;
				blockMirror = BlockMirror.field_11301;
			}

			BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11034), i);
			blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.field_11035), j);
			list.add(new WoodlandMansionGenerator.Piece(this.field_15444, roomPool.getBigRoom(this.random), blockPos2, blockRotation2, blockMirror));
		}

		private void addBigSecretRoom(
			List<WoodlandMansionGenerator.Piece> list, BlockPos blockPos, BlockRotation blockRotation, WoodlandMansionGenerator.RoomPool roomPool
		) {
			BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 1);
			list.add(new WoodlandMansionGenerator.Piece(this.field_15444, roomPool.getBigSecretRoom(this.random), blockPos2, blockRotation, BlockMirror.field_11302));
		}
	}

	public static class Piece extends SimpleStructurePiece {
		private final String template;
		private final BlockRotation rotation;
		private final BlockMirror mirror;

		public Piece(StructureManager structureManager, String string, BlockPos blockPos, BlockRotation blockRotation) {
			this(structureManager, string, blockPos, blockRotation, BlockMirror.field_11302);
		}

		public Piece(StructureManager structureManager, String string, BlockPos blockPos, BlockRotation blockRotation, BlockMirror blockMirror) {
			super(StructurePieceType.WOODLAND_MANSION, 0);
			this.template = string;
			this.pos = blockPos;
			this.rotation = blockRotation;
			this.mirror = blockMirror;
			this.method_15068(structureManager);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.WOODLAND_MANSION, compoundTag);
			this.template = compoundTag.getString("Template");
			this.rotation = BlockRotation.valueOf(compoundTag.getString("Rot"));
			this.mirror = BlockMirror.valueOf(compoundTag.getString("Mi"));
			this.method_15068(structureManager);
		}

		private void method_15068(StructureManager structureManager) {
			Structure structure = structureManager.method_15091(new Identifier("woodland_mansion/" + this.template));
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.setIgnoreEntities(true)
				.setRotation(this.rotation)
				.setMirrored(this.mirror)
				.method_16184(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
			this.method_15027(structure, this.pos, structurePlacementData);
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putString("Template", this.template);
			compoundTag.putString("Rot", this.field_15434.getRotation().name());
			compoundTag.putString("Mi", this.field_15434.getMirror().name());
		}

		@Override
		protected void handleMetadata(String string, BlockPos blockPos, IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox) {
			if (string.startsWith("Chest")) {
				BlockRotation blockRotation = this.field_15434.getRotation();
				BlockState blockState = Blocks.field_10034.method_9564();
				if ("ChestWest".equals(string)) {
					blockState = blockState.method_11657(ChestBlock.field_10768, blockRotation.rotate(Direction.field_11039));
				} else if ("ChestEast".equals(string)) {
					blockState = blockState.method_11657(ChestBlock.field_10768, blockRotation.rotate(Direction.field_11034));
				} else if ("ChestSouth".equals(string)) {
					blockState = blockState.method_11657(ChestBlock.field_10768, blockRotation.rotate(Direction.field_11035));
				} else if ("ChestNorth".equals(string)) {
					blockState = blockState.method_11657(ChestBlock.field_10768, blockRotation.rotate(Direction.field_11043));
				}

				this.addChest(iWorld, mutableIntBoundingBox, random, blockPos, LootTables.field_484, blockState);
			} else {
				IllagerEntity illagerEntity;
				switch (string) {
					case "Mage":
						illagerEntity = EntityType.field_6090.method_5883(iWorld.getWorld());
						break;
					case "Warrior":
						illagerEntity = EntityType.field_6117.method_5883(iWorld.getWorld());
						break;
					default:
						return;
				}

				illagerEntity.setPersistent();
				illagerEntity.setPositionAndAngles(blockPos, 0.0F, 0.0F);
				illagerEntity.method_5943(iWorld, iWorld.getLocalDifficulty(new BlockPos(illagerEntity)), SpawnType.field_16474, null, null);
				iWorld.spawnEntity(illagerEntity);
				iWorld.method_8652(blockPos, Blocks.field_10124.method_9564(), 2);
			}
		}
	}

	abstract static class RoomPool {
		private RoomPool() {
		}

		public abstract String getSmallRoom(Random random);

		public abstract String getSmallSecretRoom(Random random);

		public abstract String getMediumFunctionalRoom(Random random, boolean bl);

		public abstract String getMediumGenericRoom(Random random, boolean bl);

		public abstract String getMediumSecretRoom(Random random);

		public abstract String getBigRoom(Random random);

		public abstract String getBigSecretRoom(Random random);
	}

	static class SecondFloorRoomPool extends WoodlandMansionGenerator.RoomPool {
		private SecondFloorRoomPool() {
		}

		@Override
		public String getSmallRoom(Random random) {
			return "1x1_b" + (random.nextInt(4) + 1);
		}

		@Override
		public String getSmallSecretRoom(Random random) {
			return "1x1_as" + (random.nextInt(4) + 1);
		}

		@Override
		public String getMediumFunctionalRoom(Random random, boolean bl) {
			return bl ? "1x2_c_stairs" : "1x2_c" + (random.nextInt(4) + 1);
		}

		@Override
		public String getMediumGenericRoom(Random random, boolean bl) {
			return bl ? "1x2_d_stairs" : "1x2_d" + (random.nextInt(5) + 1);
		}

		@Override
		public String getMediumSecretRoom(Random random) {
			return "1x2_se" + (random.nextInt(1) + 1);
		}

		@Override
		public String getBigRoom(Random random) {
			return "2x2_b" + (random.nextInt(5) + 1);
		}

		@Override
		public String getBigSecretRoom(Random random) {
			return "2x2_s1";
		}
	}

	static class ThirdFloorRoomPool extends WoodlandMansionGenerator.SecondFloorRoomPool {
		private ThirdFloorRoomPool() {
		}
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
			this.method_15045(this.field_15440, this.field_15442, this.field_15441 - 2, Direction.field_11039, 6);
			this.method_15045(this.field_15440, this.field_15442, this.field_15441 + 3, Direction.field_11039, 6);
			this.method_15045(this.field_15440, this.field_15442 - 2, this.field_15441 - 1, Direction.field_11039, 3);
			this.method_15045(this.field_15440, this.field_15442 - 2, this.field_15441 + 2, Direction.field_11039, 3);

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
			for (Direction direction : Direction.Type.field_11062) {
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
					if (direction2 != direction.getOpposite() && (direction2 != Direction.field_11034 || !this.field_15438.nextBoolean())) {
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

				for (Direction direction2 : Direction.Type.field_11062) {
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
}
