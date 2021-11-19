package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;

public class WoodlandMansionGenerator {
	public static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, List<WoodlandMansionGenerator.Piece> pieces, Random random) {
		WoodlandMansionGenerator.MansionParameters mansionParameters = new WoodlandMansionGenerator.MansionParameters(random);
		WoodlandMansionGenerator.LayoutGenerator layoutGenerator = new WoodlandMansionGenerator.LayoutGenerator(manager, random);
		layoutGenerator.generate(pos, rotation, pieces, mansionParameters);
	}

	public static void method_35471(String[] strings) {
		Random random = new Random();
		long l = random.nextLong();
		System.out.println("Seed: " + l);
		random.setSeed(l);
		WoodlandMansionGenerator.MansionParameters mansionParameters = new WoodlandMansionGenerator.MansionParameters(random);
		mansionParameters.method_35472();
	}

	static class FirstFloorRoomPool extends WoodlandMansionGenerator.RoomPool {
		@Override
		public String getSmallRoom(Random random) {
			return "1x1_a" + (random.nextInt(5) + 1);
		}

		@Override
		public String getSmallSecretRoom(Random random) {
			return "1x1_as" + (random.nextInt(4) + 1);
		}

		@Override
		public String getMediumFunctionalRoom(Random random, boolean staircase) {
			return "1x2_a" + (random.nextInt(9) + 1);
		}

		@Override
		public String getMediumGenericRoom(Random random, boolean staircase) {
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

	static class FlagMatrix {
		private final int[][] array;
		final int n;
		final int m;
		private final int fallback;

		public FlagMatrix(int n, int m, int fallback) {
			this.n = n;
			this.m = m;
			this.fallback = fallback;
			this.array = new int[n][m];
		}

		public void set(int i, int j, int value) {
			if (i >= 0 && i < this.n && j >= 0 && j < this.m) {
				this.array[i][j] = value;
			}
		}

		public void fill(int i0, int j0, int i1, int j1, int value) {
			for (int i = j0; i <= j1; i++) {
				for (int j = i0; j <= i1; j++) {
					this.set(j, i, value);
				}
			}
		}

		public int get(int i, int j) {
			return i >= 0 && i < this.n && j >= 0 && j < this.m ? this.array[i][j] : this.fallback;
		}

		public void update(int i, int j, int expected, int newValue) {
			if (this.get(i, j) == expected) {
				this.set(i, j, newValue);
			}
		}

		public boolean anyMatchAround(int i, int j, int value) {
			return this.get(i - 1, j) == value || this.get(i + 1, j) == value || this.get(i, j + 1) == value || this.get(i, j - 1) == value;
		}
	}

	static class GenerationPiece {
		public BlockRotation rotation;
		public BlockPos position;
		public String template;
	}

	static class LayoutGenerator {
		private final StructureManager manager;
		private final Random random;
		private int field_15446;
		private int field_15445;

		public LayoutGenerator(StructureManager manager, Random random) {
			this.manager = manager;
			this.random = random;
		}

		public void generate(BlockPos pos, BlockRotation rotation, List<WoodlandMansionGenerator.Piece> pieces, WoodlandMansionGenerator.MansionParameters parameters) {
			WoodlandMansionGenerator.GenerationPiece generationPiece = new WoodlandMansionGenerator.GenerationPiece();
			generationPiece.position = pos;
			generationPiece.rotation = rotation;
			generationPiece.template = "wall_flat";
			WoodlandMansionGenerator.GenerationPiece generationPiece2 = new WoodlandMansionGenerator.GenerationPiece();
			this.addEntrance(pieces, generationPiece);
			generationPiece2.position = generationPiece.position.up(8);
			generationPiece2.rotation = generationPiece.rotation;
			generationPiece2.template = "wall_window";
			if (!pieces.isEmpty()) {
			}

			WoodlandMansionGenerator.FlagMatrix flagMatrix = parameters.field_15440;
			WoodlandMansionGenerator.FlagMatrix flagMatrix2 = parameters.field_15439;
			this.field_15446 = parameters.field_15442 + 1;
			this.field_15445 = parameters.field_15441 + 1;
			int i = parameters.field_15442 + 1;
			int j = parameters.field_15441;
			this.addRoof(pieces, generationPiece, flagMatrix, Direction.SOUTH, this.field_15446, this.field_15445, i, j);
			this.addRoof(pieces, generationPiece2, flagMatrix, Direction.SOUTH, this.field_15446, this.field_15445, i, j);
			WoodlandMansionGenerator.GenerationPiece generationPiece3 = new WoodlandMansionGenerator.GenerationPiece();
			generationPiece3.position = generationPiece.position.up(19);
			generationPiece3.rotation = generationPiece.rotation;
			generationPiece3.template = "wall_window";
			boolean bl = false;

			for (int k = 0; k < flagMatrix2.m && !bl; k++) {
				for (int l = flagMatrix2.n - 1; l >= 0 && !bl; l--) {
					if (WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix2, l, k)) {
						generationPiece3.position = generationPiece3.position.offset(rotation.rotate(Direction.SOUTH), 8 + (k - this.field_15445) * 8);
						generationPiece3.position = generationPiece3.position.offset(rotation.rotate(Direction.EAST), (l - this.field_15446) * 8);
						this.method_15052(pieces, generationPiece3);
						this.addRoof(pieces, generationPiece3, flagMatrix2, Direction.SOUTH, l, k, l, k);
						bl = true;
					}
				}
			}

			this.method_15055(pieces, pos.up(16), rotation, flagMatrix, flagMatrix2);
			this.method_15055(pieces, pos.up(27), rotation, flagMatrix2, null);
			if (!pieces.isEmpty()) {
			}

			WoodlandMansionGenerator.RoomPool[] roomPools = new WoodlandMansionGenerator.RoomPool[]{
				new WoodlandMansionGenerator.FirstFloorRoomPool(), new WoodlandMansionGenerator.SecondFloorRoomPool(), new WoodlandMansionGenerator.ThirdFloorRoomPool()
			};

			for (int lx = 0; lx < 3; lx++) {
				BlockPos blockPos = pos.up(8 * lx + (lx == 2 ? 3 : 0));
				WoodlandMansionGenerator.FlagMatrix flagMatrix3 = parameters.field_15443[lx];
				WoodlandMansionGenerator.FlagMatrix flagMatrix4 = lx == 2 ? flagMatrix2 : flagMatrix;
				String string = lx == 0 ? "carpet_south_1" : "carpet_south_2";
				String string2 = lx == 0 ? "carpet_west_1" : "carpet_west_2";

				for (int m = 0; m < flagMatrix4.m; m++) {
					for (int n = 0; n < flagMatrix4.n; n++) {
						if (flagMatrix4.get(n, m) == 1) {
							BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (m - this.field_15445) * 8);
							blockPos2 = blockPos2.offset(rotation.rotate(Direction.EAST), (n - this.field_15446) * 8);
							pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "corridor_floor", blockPos2, rotation));
							if (flagMatrix4.get(n, m - 1) == 1 || (flagMatrix3.get(n, m - 1) & 8388608) == 8388608) {
								pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "carpet_north", blockPos2.offset(rotation.rotate(Direction.EAST), 1).up(), rotation));
							}

							if (flagMatrix4.get(n + 1, m) == 1 || (flagMatrix3.get(n + 1, m) & 8388608) == 8388608) {
								pieces.add(
									new WoodlandMansionGenerator.Piece(
										this.manager, "carpet_east", blockPos2.offset(rotation.rotate(Direction.SOUTH), 1).offset(rotation.rotate(Direction.EAST), 5).up(), rotation
									)
								);
							}

							if (flagMatrix4.get(n, m + 1) == 1 || (flagMatrix3.get(n, m + 1) & 8388608) == 8388608) {
								pieces.add(
									new WoodlandMansionGenerator.Piece(
										this.manager, string, blockPos2.offset(rotation.rotate(Direction.SOUTH), 5).offset(rotation.rotate(Direction.WEST), 1), rotation
									)
								);
							}

							if (flagMatrix4.get(n - 1, m) == 1 || (flagMatrix3.get(n - 1, m) & 8388608) == 8388608) {
								pieces.add(
									new WoodlandMansionGenerator.Piece(
										this.manager, string2, blockPos2.offset(rotation.rotate(Direction.WEST), 1).offset(rotation.rotate(Direction.NORTH), 1), rotation
									)
								);
							}
						}
					}
				}

				String string3 = lx == 0 ? "indoors_wall_1" : "indoors_wall_2";
				String string4 = lx == 0 ? "indoors_door_1" : "indoors_door_2";
				List<Direction> list = Lists.<Direction>newArrayList();

				for (int o = 0; o < flagMatrix4.m; o++) {
					for (int p = 0; p < flagMatrix4.n; p++) {
						boolean bl2 = lx == 2 && flagMatrix4.get(p, o) == 3;
						if (flagMatrix4.get(p, o) == 2 || bl2) {
							int q = flagMatrix3.get(p, o);
							int r = q & 983040;
							int s = q & 65535;
							bl2 = bl2 && (q & 8388608) == 8388608;
							list.clear();
							if ((q & 2097152) == 2097152) {
								for (Direction direction : Direction.Type.HORIZONTAL) {
									if (flagMatrix4.get(p + direction.getOffsetX(), o + direction.getOffsetZ()) == 1) {
										list.add(direction);
									}
								}
							}

							Direction direction2 = null;
							if (!list.isEmpty()) {
								direction2 = (Direction)list.get(this.random.nextInt(list.size()));
							} else if ((q & 1048576) == 1048576) {
								direction2 = Direction.UP;
							}

							BlockPos blockPos3 = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (o - this.field_15445) * 8);
							blockPos3 = blockPos3.offset(rotation.rotate(Direction.EAST), -1 + (p - this.field_15446) * 8);
							if (WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix4, p - 1, o) && !parameters.method_15039(flagMatrix4, p - 1, o, lx, s)) {
								pieces.add(new WoodlandMansionGenerator.Piece(this.manager, direction2 == Direction.WEST ? string4 : string3, blockPos3, rotation));
							}

							if (flagMatrix4.get(p + 1, o) == 1 && !bl2) {
								BlockPos blockPos4 = blockPos3.offset(rotation.rotate(Direction.EAST), 8);
								pieces.add(new WoodlandMansionGenerator.Piece(this.manager, direction2 == Direction.EAST ? string4 : string3, blockPos4, rotation));
							}

							if (WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix4, p, o + 1) && !parameters.method_15039(flagMatrix4, p, o + 1, lx, s)) {
								BlockPos blockPos4 = blockPos3.offset(rotation.rotate(Direction.SOUTH), 7);
								blockPos4 = blockPos4.offset(rotation.rotate(Direction.EAST), 7);
								pieces.add(
									new WoodlandMansionGenerator.Piece(
										this.manager, direction2 == Direction.SOUTH ? string4 : string3, blockPos4, rotation.rotate(BlockRotation.CLOCKWISE_90)
									)
								);
							}

							if (flagMatrix4.get(p, o - 1) == 1 && !bl2) {
								BlockPos blockPos4 = blockPos3.offset(rotation.rotate(Direction.NORTH), 1);
								blockPos4 = blockPos4.offset(rotation.rotate(Direction.EAST), 7);
								pieces.add(
									new WoodlandMansionGenerator.Piece(
										this.manager, direction2 == Direction.NORTH ? string4 : string3, blockPos4, rotation.rotate(BlockRotation.CLOCKWISE_90)
									)
								);
							}

							if (r == 65536) {
								this.addSmallRoom(pieces, blockPos3, rotation, direction2, roomPools[lx]);
							} else if (r == 131072 && direction2 != null) {
								Direction direction3 = parameters.method_15040(flagMatrix4, p, o, lx, s);
								boolean bl3 = (q & 4194304) == 4194304;
								this.addMediumRoom(pieces, blockPos3, rotation, direction3, direction2, roomPools[lx], bl3);
							} else if (r == 262144 && direction2 != null && direction2 != Direction.UP) {
								Direction direction3 = direction2.rotateYClockwise();
								if (!parameters.method_15039(flagMatrix4, p + direction3.getOffsetX(), o + direction3.getOffsetZ(), lx, s)) {
									direction3 = direction3.getOpposite();
								}

								this.addBigRoom(pieces, blockPos3, rotation, direction3, direction2, roomPools[lx]);
							} else if (r == 262144 && direction2 == Direction.UP) {
								this.addBigSecretRoom(pieces, blockPos3, rotation, roomPools[lx]);
							}
						}
					}
				}
			}
		}

		private void addRoof(
			List<WoodlandMansionGenerator.Piece> pieces,
			WoodlandMansionGenerator.GenerationPiece generationPiece,
			WoodlandMansionGenerator.FlagMatrix flagMatrix,
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
				if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, m + direction.getOffsetX(), n + direction.getOffsetZ())) {
					this.method_15058(pieces, generationPiece);
					direction = direction.rotateYClockwise();
					if (m != k || n != l || direction2 != direction) {
						this.method_15052(pieces, generationPiece);
					}
				} else if (WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, m + direction.getOffsetX(), n + direction.getOffsetZ())
					&& WoodlandMansionGenerator.MansionParameters.method_15047(
						flagMatrix,
						m + direction.getOffsetX() + direction.rotateYCounterclockwise().getOffsetX(),
						n + direction.getOffsetZ() + direction.rotateYCounterclockwise().getOffsetZ()
					)) {
					this.method_15060(pieces, generationPiece);
					m += direction.getOffsetX();
					n += direction.getOffsetZ();
					direction = direction.rotateYCounterclockwise();
				} else {
					m += direction.getOffsetX();
					n += direction.getOffsetZ();
					if (m != k || n != l || direction2 != direction) {
						this.method_15052(pieces, generationPiece);
					}
				}
			} while (m != k || n != l || direction2 != direction);
		}

		private void method_15055(
			List<WoodlandMansionGenerator.Piece> list,
			BlockPos blockPos,
			BlockRotation blockRotation,
			WoodlandMansionGenerator.FlagMatrix flagMatrix,
			@Nullable WoodlandMansionGenerator.FlagMatrix flagMatrix2
		) {
			for (int i = 0; i < flagMatrix.m; i++) {
				for (int j = 0; j < flagMatrix.n; j++) {
					BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.SOUTH), 8 + (i - this.field_15445) * 8);
					blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.EAST), (j - this.field_15446) * 8);
					boolean bl = flagMatrix2 != null && WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix2, j, i);
					if (WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, j, i) && !bl) {
						list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof", blockPos2.up(3), blockRotation));
						if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, j + 1, i)) {
							BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 6);
							list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_front", blockPos3, blockRotation));
						}

						if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, j - 1, i)) {
							BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 0);
							blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 7);
							list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_front", blockPos3, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
						}

						if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, j, i - 1)) {
							BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.WEST), 1);
							list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_front", blockPos3, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
						}

						if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, j, i + 1)) {
							BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.EAST), 6);
							blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 6);
							list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_front", blockPos3, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
						}
					}
				}
			}

			if (flagMatrix2 != null) {
				for (int i = 0; i < flagMatrix.m; i++) {
					for (int jx = 0; jx < flagMatrix.n; jx++) {
						BlockPos var17 = blockPos.offset(blockRotation.rotate(Direction.SOUTH), 8 + (i - this.field_15445) * 8);
						var17 = var17.offset(blockRotation.rotate(Direction.EAST), (jx - this.field_15446) * 8);
						boolean bl = WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix2, jx, i);
						if (WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jx, i) && bl) {
							if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jx + 1, i)) {
								BlockPos blockPos3 = var17.offset(blockRotation.rotate(Direction.EAST), 7);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall", blockPos3, blockRotation));
							}

							if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jx - 1, i)) {
								BlockPos blockPos3 = var17.offset(blockRotation.rotate(Direction.WEST), 1);
								blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 6);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall", blockPos3, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
							}

							if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jx, i - 1)) {
								BlockPos blockPos3 = var17.offset(blockRotation.rotate(Direction.WEST), 0);
								blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.NORTH), 1);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall", blockPos3, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
							}

							if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jx, i + 1)) {
								BlockPos blockPos3 = var17.offset(blockRotation.rotate(Direction.EAST), 6);
								blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 7);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall", blockPos3, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
							}

							if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jx + 1, i)) {
								if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jx, i - 1)) {
									BlockPos blockPos3 = var17.offset(blockRotation.rotate(Direction.EAST), 7);
									blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.NORTH), 2);
									list.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall_corner", blockPos3, blockRotation));
								}

								if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jx, i + 1)) {
									BlockPos blockPos3 = var17.offset(blockRotation.rotate(Direction.EAST), 8);
									blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 7);
									list.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall_corner", blockPos3, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
								}
							}

							if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jx - 1, i)) {
								if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jx, i - 1)) {
									BlockPos blockPos3 = var17.offset(blockRotation.rotate(Direction.WEST), 2);
									blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.NORTH), 1);
									list.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall_corner", blockPos3, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
								}

								if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jx, i + 1)) {
									BlockPos blockPos3 = var17.offset(blockRotation.rotate(Direction.WEST), 1);
									blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 8);
									list.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall_corner", blockPos3, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < flagMatrix.m; i++) {
				for (int jxx = 0; jxx < flagMatrix.n; jxx++) {
					BlockPos var19 = blockPos.offset(blockRotation.rotate(Direction.SOUTH), 8 + (i - this.field_15445) * 8);
					var19 = var19.offset(blockRotation.rotate(Direction.EAST), (jxx - this.field_15446) * 8);
					boolean bl = flagMatrix2 != null && WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix2, jxx, i);
					if (WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jxx, i) && !bl) {
						if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jxx + 1, i)) {
							BlockPos blockPos3 = var19.offset(blockRotation.rotate(Direction.EAST), 6);
							if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jxx, i + 1)) {
								BlockPos blockPos4 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 6);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_corner", blockPos4, blockRotation));
							} else if (WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jxx + 1, i + 1)) {
								BlockPos blockPos4 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 5);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_inner_corner", blockPos4, blockRotation));
							}

							if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jxx, i - 1)) {
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_corner", blockPos3, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
							} else if (WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jxx + 1, i - 1)) {
								BlockPos blockPos4 = var19.offset(blockRotation.rotate(Direction.EAST), 9);
								blockPos4 = blockPos4.offset(blockRotation.rotate(Direction.NORTH), 2);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_inner_corner", blockPos4, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
							}
						}

						if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jxx - 1, i)) {
							BlockPos blockPos3x = var19.offset(blockRotation.rotate(Direction.EAST), 0);
							blockPos3x = blockPos3x.offset(blockRotation.rotate(Direction.SOUTH), 0);
							if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jxx, i + 1)) {
								BlockPos blockPos4 = blockPos3x.offset(blockRotation.rotate(Direction.SOUTH), 6);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_corner", blockPos4, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
							} else if (WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jxx - 1, i + 1)) {
								BlockPos blockPos4 = blockPos3x.offset(blockRotation.rotate(Direction.SOUTH), 8);
								blockPos4 = blockPos4.offset(blockRotation.rotate(Direction.WEST), 3);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_inner_corner", blockPos4, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
							}

							if (!WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jxx, i - 1)) {
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_corner", blockPos3x, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
							} else if (WoodlandMansionGenerator.MansionParameters.method_15047(flagMatrix, jxx - 1, i - 1)) {
								BlockPos blockPos4 = blockPos3x.offset(blockRotation.rotate(Direction.SOUTH), 1);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_inner_corner", blockPos4, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
							}
						}
					}
				}
			}
		}

		private void addEntrance(List<WoodlandMansionGenerator.Piece> pieces, WoodlandMansionGenerator.GenerationPiece generationPiece) {
			Direction direction = generationPiece.rotation.rotate(Direction.WEST);
			pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "entrance", generationPiece.position.offset(direction, 9), generationPiece.rotation));
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.SOUTH), 16);
		}

		private void method_15052(List<WoodlandMansionGenerator.Piece> list, WoodlandMansionGenerator.GenerationPiece generationPiece) {
			list.add(
				new WoodlandMansionGenerator.Piece(
					this.manager, generationPiece.template, generationPiece.position.offset(generationPiece.rotation.rotate(Direction.EAST), 7), generationPiece.rotation
				)
			);
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.SOUTH), 8);
		}

		private void method_15058(List<WoodlandMansionGenerator.Piece> list, WoodlandMansionGenerator.GenerationPiece generationPiece) {
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.SOUTH), -1);
			list.add(new WoodlandMansionGenerator.Piece(this.manager, "wall_corner", generationPiece.position, generationPiece.rotation));
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.SOUTH), -7);
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.WEST), -6);
			generationPiece.rotation = generationPiece.rotation.rotate(BlockRotation.CLOCKWISE_90);
		}

		private void method_15060(List<WoodlandMansionGenerator.Piece> list, WoodlandMansionGenerator.GenerationPiece generationPiece) {
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.SOUTH), 6);
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.EAST), 8);
			generationPiece.rotation = generationPiece.rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
		}

		private void addSmallRoom(
			List<WoodlandMansionGenerator.Piece> pieces, BlockPos pos, BlockRotation rotation, Direction direction, WoodlandMansionGenerator.RoomPool roomPool
		) {
			BlockRotation blockRotation = BlockRotation.NONE;
			String string = roomPool.getSmallRoom(this.random);
			if (direction != Direction.EAST) {
				if (direction == Direction.NORTH) {
					blockRotation = blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
				} else if (direction == Direction.WEST) {
					blockRotation = blockRotation.rotate(BlockRotation.CLOCKWISE_180);
				} else if (direction == Direction.SOUTH) {
					blockRotation = blockRotation.rotate(BlockRotation.CLOCKWISE_90);
				} else {
					string = roomPool.getSmallSecretRoom(this.random);
				}
			}

			BlockPos blockPos = Structure.applyTransformedOffset(new BlockPos(1, 0, 0), BlockMirror.NONE, blockRotation, 7, 7);
			blockRotation = blockRotation.rotate(rotation);
			blockPos = blockPos.rotate(rotation);
			BlockPos blockPos2 = pos.add(blockPos.getX(), 0, blockPos.getZ());
			pieces.add(new WoodlandMansionGenerator.Piece(this.manager, string, blockPos2, blockRotation));
		}

		private void addMediumRoom(
			List<WoodlandMansionGenerator.Piece> pieces,
			BlockPos pos,
			BlockRotation rotation,
			Direction direction,
			Direction direction2,
			WoodlandMansionGenerator.RoomPool roomPool,
			boolean staircase
		) {
			if (direction2 == Direction.EAST && direction == Direction.SOUTH) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
				pieces.add(new WoodlandMansionGenerator.Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation));
			} else if (direction2 == Direction.EAST && direction == Direction.NORTH) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
				blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
				pieces.add(
					new WoodlandMansionGenerator.Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation, BlockMirror.LEFT_RIGHT)
				);
			} else if (direction2 == Direction.WEST && direction == Direction.NORTH) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
				blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
				pieces.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_180)
					)
				);
			} else if (direction2 == Direction.WEST && direction == Direction.SOUTH) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
				pieces.add(
					new WoodlandMansionGenerator.Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation, BlockMirror.FRONT_BACK)
				);
			} else if (direction2 == Direction.SOUTH && direction == Direction.EAST) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
				pieces.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.LEFT_RIGHT
					)
				);
			} else if (direction2 == Direction.SOUTH && direction == Direction.WEST) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
				pieces.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90)
					)
				);
			} else if (direction2 == Direction.NORTH && direction == Direction.WEST) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
				blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
				pieces.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.FRONT_BACK
					)
				);
			} else if (direction2 == Direction.NORTH && direction == Direction.EAST) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
				blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
				pieces.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, roomPool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)
					)
				);
			} else if (direction2 == Direction.SOUTH && direction == Direction.NORTH) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
				blockPos = blockPos.offset(rotation.rotate(Direction.NORTH), 8);
				pieces.add(new WoodlandMansionGenerator.Piece(this.manager, roomPool.getMediumGenericRoom(this.random, staircase), blockPos, rotation));
			} else if (direction2 == Direction.NORTH && direction == Direction.SOUTH) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
				blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 14);
				pieces.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, roomPool.getMediumGenericRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_180)
					)
				);
			} else if (direction2 == Direction.WEST && direction == Direction.EAST) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 15);
				pieces.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, roomPool.getMediumGenericRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90)
					)
				);
			} else if (direction2 == Direction.EAST && direction == Direction.WEST) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.WEST), 7);
				blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
				pieces.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, roomPool.getMediumGenericRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)
					)
				);
			} else if (direction2 == Direction.UP && direction == Direction.EAST) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 15);
				pieces.add(
					new WoodlandMansionGenerator.Piece(this.manager, roomPool.getMediumSecretRoom(this.random), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90))
				);
			} else if (direction2 == Direction.UP && direction == Direction.SOUTH) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
				blockPos = blockPos.offset(rotation.rotate(Direction.NORTH), 0);
				pieces.add(new WoodlandMansionGenerator.Piece(this.manager, roomPool.getMediumSecretRoom(this.random), blockPos, rotation));
			}
		}

		private void addBigRoom(
			List<WoodlandMansionGenerator.Piece> pieces,
			BlockPos pos,
			BlockRotation rotation,
			Direction direction,
			Direction direction2,
			WoodlandMansionGenerator.RoomPool roomPool
		) {
			int i = 0;
			int j = 0;
			BlockRotation blockRotation = rotation;
			BlockMirror blockMirror = BlockMirror.NONE;
			if (direction2 == Direction.EAST && direction == Direction.SOUTH) {
				i = -7;
			} else if (direction2 == Direction.EAST && direction == Direction.NORTH) {
				i = -7;
				j = 6;
				blockMirror = BlockMirror.LEFT_RIGHT;
			} else if (direction2 == Direction.NORTH && direction == Direction.EAST) {
				i = 1;
				j = 14;
				blockRotation = rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
			} else if (direction2 == Direction.NORTH && direction == Direction.WEST) {
				i = 7;
				j = 14;
				blockRotation = rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
				blockMirror = BlockMirror.LEFT_RIGHT;
			} else if (direction2 == Direction.SOUTH && direction == Direction.WEST) {
				i = 7;
				j = -8;
				blockRotation = rotation.rotate(BlockRotation.CLOCKWISE_90);
			} else if (direction2 == Direction.SOUTH && direction == Direction.EAST) {
				i = 1;
				j = -8;
				blockRotation = rotation.rotate(BlockRotation.CLOCKWISE_90);
				blockMirror = BlockMirror.LEFT_RIGHT;
			} else if (direction2 == Direction.WEST && direction == Direction.NORTH) {
				i = 15;
				j = 6;
				blockRotation = rotation.rotate(BlockRotation.CLOCKWISE_180);
			} else if (direction2 == Direction.WEST && direction == Direction.SOUTH) {
				i = 15;
				blockMirror = BlockMirror.FRONT_BACK;
			}

			BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), i);
			blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), j);
			pieces.add(new WoodlandMansionGenerator.Piece(this.manager, roomPool.getBigRoom(this.random), blockPos, blockRotation, blockMirror));
		}

		private void addBigSecretRoom(
			List<WoodlandMansionGenerator.Piece> list, BlockPos blockPos, BlockRotation blockRotation, WoodlandMansionGenerator.RoomPool roomPool
		) {
			BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
			list.add(new WoodlandMansionGenerator.Piece(this.manager, roomPool.getBigSecretRoom(this.random), blockPos2, blockRotation, BlockMirror.NONE));
		}
	}

	static class MansionParameters {
		private static final int field_31665 = 11;
		private static final int field_31666 = 0;
		private static final int field_31667 = 1;
		private static final int field_31668 = 2;
		private static final int field_31669 = 3;
		private static final int field_31670 = 4;
		private static final int field_31671 = 5;
		private static final int field_31672 = 65536;
		private static final int field_31673 = 131072;
		private static final int field_31674 = 262144;
		private static final int field_31675 = 1048576;
		private static final int field_31676 = 2097152;
		private static final int field_31677 = 4194304;
		private static final int field_31678 = 8388608;
		private static final int field_31679 = 983040;
		private static final int field_31680 = 65535;
		private final Random random;
		final WoodlandMansionGenerator.FlagMatrix field_15440;
		final WoodlandMansionGenerator.FlagMatrix field_15439;
		final WoodlandMansionGenerator.FlagMatrix[] field_15443;
		final int field_15442;
		final int field_15441;

		public MansionParameters(Random random) {
			this.random = random;
			int i = 11;
			this.field_15442 = 7;
			this.field_15441 = 4;
			this.field_15440 = new WoodlandMansionGenerator.FlagMatrix(11, 11, 5);
			this.field_15440.fill(this.field_15442, this.field_15441, this.field_15442 + 1, this.field_15441 + 1, 3);
			this.field_15440.fill(this.field_15442 - 1, this.field_15441, this.field_15442 - 1, this.field_15441 + 1, 2);
			this.field_15440.fill(this.field_15442 + 2, this.field_15441 - 2, this.field_15442 + 3, this.field_15441 + 3, 5);
			this.field_15440.fill(this.field_15442 + 1, this.field_15441 - 2, this.field_15442 + 1, this.field_15441 - 1, 1);
			this.field_15440.fill(this.field_15442 + 1, this.field_15441 + 2, this.field_15442 + 1, this.field_15441 + 3, 1);
			this.field_15440.set(this.field_15442 - 1, this.field_15441 - 1, 1);
			this.field_15440.set(this.field_15442 - 1, this.field_15441 + 2, 1);
			this.field_15440.fill(0, 0, 11, 1, 5);
			this.field_15440.fill(0, 9, 11, 11, 5);
			this.method_15045(this.field_15440, this.field_15442, this.field_15441 - 2, Direction.WEST, 6);
			this.method_15045(this.field_15440, this.field_15442, this.field_15441 + 3, Direction.WEST, 6);
			this.method_15045(this.field_15440, this.field_15442 - 2, this.field_15441 - 1, Direction.WEST, 3);
			this.method_15045(this.field_15440, this.field_15442 - 2, this.field_15441 + 2, Direction.WEST, 3);

			while (this.method_15046(this.field_15440)) {
			}

			this.field_15443 = new WoodlandMansionGenerator.FlagMatrix[3];
			this.field_15443[0] = new WoodlandMansionGenerator.FlagMatrix(11, 11, 5);
			this.field_15443[1] = new WoodlandMansionGenerator.FlagMatrix(11, 11, 5);
			this.field_15443[2] = new WoodlandMansionGenerator.FlagMatrix(11, 11, 5);
			this.method_15042(this.field_15440, this.field_15443[0]);
			this.method_15042(this.field_15440, this.field_15443[1]);
			this.field_15443[0].fill(this.field_15442 + 1, this.field_15441, this.field_15442 + 1, this.field_15441 + 1, 8388608);
			this.field_15443[1].fill(this.field_15442 + 1, this.field_15441, this.field_15442 + 1, this.field_15441 + 1, 8388608);
			this.field_15439 = new WoodlandMansionGenerator.FlagMatrix(this.field_15440.n, this.field_15440.m, 5);
			this.method_15048();
			this.method_15042(this.field_15439, this.field_15443[2]);
		}

		public static boolean method_15047(WoodlandMansionGenerator.FlagMatrix flagMatrix, int i, int j) {
			int k = flagMatrix.get(i, j);
			return k == 1 || k == 2 || k == 3 || k == 4;
		}

		public boolean method_15039(WoodlandMansionGenerator.FlagMatrix flagMatrix, int i, int j, int k, int l) {
			return (this.field_15443[k].get(i, j) & 65535) == l;
		}

		@Nullable
		public Direction method_15040(WoodlandMansionGenerator.FlagMatrix flagMatrix, int i, int j, int k, int l) {
			for (Direction direction : Direction.Type.HORIZONTAL) {
				if (this.method_15039(flagMatrix, i + direction.getOffsetX(), j + direction.getOffsetZ(), k, l)) {
					return direction;
				}
			}

			return null;
		}

		private void method_15045(WoodlandMansionGenerator.FlagMatrix flagMatrix, int i, int j, Direction direction, int k) {
			if (k > 0) {
				flagMatrix.set(i, j, 1);
				flagMatrix.update(i + direction.getOffsetX(), j + direction.getOffsetZ(), 0, 1);

				for (int l = 0; l < 8; l++) {
					Direction direction2 = Direction.fromHorizontal(this.random.nextInt(4));
					if (direction2 != direction.getOpposite() && (direction2 != Direction.EAST || !this.random.nextBoolean())) {
						int m = i + direction.getOffsetX();
						int n = j + direction.getOffsetZ();
						if (flagMatrix.get(m + direction2.getOffsetX(), n + direction2.getOffsetZ()) == 0
							&& flagMatrix.get(m + direction2.getOffsetX() * 2, n + direction2.getOffsetZ() * 2) == 0) {
							this.method_15045(
								flagMatrix, i + direction.getOffsetX() + direction2.getOffsetX(), j + direction.getOffsetZ() + direction2.getOffsetZ(), direction2, k - 1
							);
							break;
						}
					}
				}

				Direction direction3 = direction.rotateYClockwise();
				Direction direction2 = direction.rotateYCounterclockwise();
				flagMatrix.update(i + direction3.getOffsetX(), j + direction3.getOffsetZ(), 0, 2);
				flagMatrix.update(i + direction2.getOffsetX(), j + direction2.getOffsetZ(), 0, 2);
				flagMatrix.update(i + direction.getOffsetX() + direction3.getOffsetX(), j + direction.getOffsetZ() + direction3.getOffsetZ(), 0, 2);
				flagMatrix.update(i + direction.getOffsetX() + direction2.getOffsetX(), j + direction.getOffsetZ() + direction2.getOffsetZ(), 0, 2);
				flagMatrix.update(i + direction.getOffsetX() * 2, j + direction.getOffsetZ() * 2, 0, 2);
				flagMatrix.update(i + direction3.getOffsetX() * 2, j + direction3.getOffsetZ() * 2, 0, 2);
				flagMatrix.update(i + direction2.getOffsetX() * 2, j + direction2.getOffsetZ() * 2, 0, 2);
			}
		}

		private boolean method_15046(WoodlandMansionGenerator.FlagMatrix flagMatrix) {
			boolean bl = false;

			for (int i = 0; i < flagMatrix.m; i++) {
				for (int j = 0; j < flagMatrix.n; j++) {
					if (flagMatrix.get(j, i) == 0) {
						int k = 0;
						k += method_15047(flagMatrix, j + 1, i) ? 1 : 0;
						k += method_15047(flagMatrix, j - 1, i) ? 1 : 0;
						k += method_15047(flagMatrix, j, i + 1) ? 1 : 0;
						k += method_15047(flagMatrix, j, i - 1) ? 1 : 0;
						if (k >= 3) {
							flagMatrix.set(j, i, 2);
							bl = true;
						} else if (k == 2) {
							int l = 0;
							l += method_15047(flagMatrix, j + 1, i + 1) ? 1 : 0;
							l += method_15047(flagMatrix, j - 1, i + 1) ? 1 : 0;
							l += method_15047(flagMatrix, j + 1, i - 1) ? 1 : 0;
							l += method_15047(flagMatrix, j - 1, i - 1) ? 1 : 0;
							if (l <= 1) {
								flagMatrix.set(j, i, 2);
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
			WoodlandMansionGenerator.FlagMatrix flagMatrix = this.field_15443[1];

			for (int i = 0; i < this.field_15439.m; i++) {
				for (int j = 0; j < this.field_15439.n; j++) {
					int k = flagMatrix.get(j, i);
					int l = k & 983040;
					if (l == 131072 && (k & 2097152) == 2097152) {
						list.add(new Pair<>(j, i));
					}
				}
			}

			if (list.isEmpty()) {
				this.field_15439.fill(0, 0, this.field_15439.n, this.field_15439.m, 5);
			} else {
				Pair<Integer, Integer> pair = (Pair<Integer, Integer>)list.get(this.random.nextInt(list.size()));
				int jx = flagMatrix.get(pair.getLeft(), pair.getRight());
				flagMatrix.set(pair.getLeft(), pair.getRight(), jx | 4194304);
				Direction direction = this.method_15040(this.field_15440, pair.getLeft(), pair.getRight(), 1, jx & 65535);
				int l = pair.getLeft() + direction.getOffsetX();
				int m = pair.getRight() + direction.getOffsetZ();

				for (int n = 0; n < this.field_15439.m; n++) {
					for (int o = 0; o < this.field_15439.n; o++) {
						if (!method_15047(this.field_15440, o, n)) {
							this.field_15439.set(o, n, 5);
						} else if (o == pair.getLeft() && n == pair.getRight()) {
							this.field_15439.set(o, n, 3);
						} else if (o == l && n == m) {
							this.field_15439.set(o, n, 3);
							this.field_15443[2].set(o, n, 8388608);
						}
					}
				}

				List<Direction> list2 = Lists.<Direction>newArrayList();

				for (Direction direction2 : Direction.Type.HORIZONTAL) {
					if (this.field_15439.get(l + direction2.getOffsetX(), m + direction2.getOffsetZ()) == 0) {
						list2.add(direction2);
					}
				}

				if (list2.isEmpty()) {
					this.field_15439.fill(0, 0, this.field_15439.n, this.field_15439.m, 5);
					flagMatrix.set(pair.getLeft(), pair.getRight(), jx);
				} else {
					Direction direction3 = (Direction)list2.get(this.random.nextInt(list2.size()));
					this.method_15045(this.field_15439, l + direction3.getOffsetX(), m + direction3.getOffsetZ(), direction3, 4);

					while (this.method_15046(this.field_15439)) {
					}
				}
			}
		}

		private void method_15042(WoodlandMansionGenerator.FlagMatrix flagMatrix, WoodlandMansionGenerator.FlagMatrix flagMatrix2) {
			List<Pair<Integer, Integer>> list = Lists.<Pair<Integer, Integer>>newArrayList();

			for (int i = 0; i < flagMatrix.m; i++) {
				for (int j = 0; j < flagMatrix.n; j++) {
					if (flagMatrix.get(j, i) == 2) {
						list.add(new Pair<>(j, i));
					}
				}
			}

			Collections.shuffle(list, this.random);
			int i = 10;

			for (Pair<Integer, Integer> pair : list) {
				int k = pair.getLeft();
				int l = pair.getRight();
				if (flagMatrix2.get(k, l) == 0) {
					int m = k;
					int n = k;
					int o = l;
					int p = l;
					int q = 65536;
					if (flagMatrix2.get(k + 1, l) == 0
						&& flagMatrix2.get(k, l + 1) == 0
						&& flagMatrix2.get(k + 1, l + 1) == 0
						&& flagMatrix.get(k + 1, l) == 2
						&& flagMatrix.get(k, l + 1) == 2
						&& flagMatrix.get(k + 1, l + 1) == 2) {
						n = k + 1;
						p = l + 1;
						q = 262144;
					} else if (flagMatrix2.get(k - 1, l) == 0
						&& flagMatrix2.get(k, l + 1) == 0
						&& flagMatrix2.get(k - 1, l + 1) == 0
						&& flagMatrix.get(k - 1, l) == 2
						&& flagMatrix.get(k, l + 1) == 2
						&& flagMatrix.get(k - 1, l + 1) == 2) {
						m = k - 1;
						p = l + 1;
						q = 262144;
					} else if (flagMatrix2.get(k - 1, l) == 0
						&& flagMatrix2.get(k, l - 1) == 0
						&& flagMatrix2.get(k - 1, l - 1) == 0
						&& flagMatrix.get(k - 1, l) == 2
						&& flagMatrix.get(k, l - 1) == 2
						&& flagMatrix.get(k - 1, l - 1) == 2) {
						m = k - 1;
						o = l - 1;
						q = 262144;
					} else if (flagMatrix2.get(k + 1, l) == 0 && flagMatrix.get(k + 1, l) == 2) {
						n = k + 1;
						q = 131072;
					} else if (flagMatrix2.get(k, l + 1) == 0 && flagMatrix.get(k, l + 1) == 2) {
						p = l + 1;
						q = 131072;
					} else if (flagMatrix2.get(k - 1, l) == 0 && flagMatrix.get(k - 1, l) == 2) {
						m = k - 1;
						q = 131072;
					} else if (flagMatrix2.get(k, l - 1) == 0 && flagMatrix.get(k, l - 1) == 2) {
						o = l - 1;
						q = 131072;
					}

					int r = this.random.nextBoolean() ? m : n;
					int s = this.random.nextBoolean() ? o : p;
					int t = 2097152;
					if (!flagMatrix.anyMatchAround(r, s, 1)) {
						r = r == m ? n : m;
						s = s == o ? p : o;
						if (!flagMatrix.anyMatchAround(r, s, 1)) {
							s = s == o ? p : o;
							if (!flagMatrix.anyMatchAround(r, s, 1)) {
								r = r == m ? n : m;
								s = s == o ? p : o;
								if (!flagMatrix.anyMatchAround(r, s, 1)) {
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
								flagMatrix2.set(v, u, 1048576 | t | q | i);
							} else {
								flagMatrix2.set(v, u, q | i);
							}
						}
					}

					i++;
				}
			}
		}

		public void method_35472() {
			for (int i = 0; i < 2; i++) {
				WoodlandMansionGenerator.FlagMatrix flagMatrix = i == 0 ? this.field_15440 : this.field_15439;

				for (int j = 0; j < flagMatrix.m; j++) {
					for (int k = 0; k < flagMatrix.n; k++) {
						int l = flagMatrix.get(k, j);
						if (l == 1) {
							System.out.print("+");
						} else if (l == 4) {
							System.out.print("x");
						} else if (l == 2) {
							System.out.print("X");
						} else if (l == 3) {
							System.out.print("O");
						} else if (l == 5) {
							System.out.print("#");
						} else {
							System.out.print(" ");
						}
					}

					System.out.println("");
				}

				System.out.println("");
			}
		}
	}

	public static class Piece extends SimpleStructurePiece {
		public Piece(StructureManager manager, String template, BlockPos pos, BlockRotation rotation) {
			this(manager, template, pos, rotation, BlockMirror.NONE);
		}

		public Piece(StructureManager manager, String template, BlockPos pos, BlockRotation rotation, BlockMirror mirror) {
			super(StructurePieceType.WOODLAND_MANSION, 0, manager, getId(template), template, createPlacementData(mirror, rotation), pos);
		}

		public Piece(StructureManager manager, NbtCompound nbt) {
			super(
				StructurePieceType.WOODLAND_MANSION,
				nbt,
				manager,
				identifier -> createPlacementData(BlockMirror.valueOf(nbt.getString("Mi")), BlockRotation.valueOf(nbt.getString("Rot")))
			);
		}

		@Override
		protected Identifier getId() {
			return getId(this.template);
		}

		private static Identifier getId(String identifier) {
			return new Identifier("woodland_mansion/" + identifier);
		}

		private static StructurePlacementData createPlacementData(BlockMirror mirror, BlockRotation rotation) {
			return new StructurePlacementData()
				.setIgnoreEntities(true)
				.setRotation(rotation)
				.setMirror(mirror)
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			super.writeNbt(context, nbt);
			nbt.putString("Rot", this.placementData.getRotation().name());
			nbt.putString("Mi", this.placementData.getMirror().name());
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
			if (metadata.startsWith("Chest")) {
				BlockRotation blockRotation = this.placementData.getRotation();
				BlockState blockState = Blocks.CHEST.getDefaultState();
				if ("ChestWest".equals(metadata)) {
					blockState = blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.WEST));
				} else if ("ChestEast".equals(metadata)) {
					blockState = blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.EAST));
				} else if ("ChestSouth".equals(metadata)) {
					blockState = blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.SOUTH));
				} else if ("ChestNorth".equals(metadata)) {
					blockState = blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.NORTH));
				}

				this.addChest(world, boundingBox, random, pos, LootTables.WOODLAND_MANSION_CHEST, blockState);
			} else {
				IllagerEntity illagerEntity;
				switch (metadata) {
					case "Mage":
						illagerEntity = EntityType.EVOKER.create(world.toServerWorld());
						break;
					case "Warrior":
						illagerEntity = EntityType.VINDICATOR.create(world.toServerWorld());
						break;
					default:
						return;
				}

				illagerEntity.setPersistent();
				illagerEntity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
				illagerEntity.initialize(world, world.getLocalDifficulty(illagerEntity.getBlockPos()), SpawnReason.STRUCTURE, null, null);
				world.spawnEntityAndPassengers(illagerEntity);
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
			}
		}
	}

	abstract static class RoomPool {
		public abstract String getSmallRoom(Random random);

		public abstract String getSmallSecretRoom(Random random);

		public abstract String getMediumFunctionalRoom(Random random, boolean staircase);

		public abstract String getMediumGenericRoom(Random random, boolean staircase);

		public abstract String getMediumSecretRoom(Random random);

		public abstract String getBigRoom(Random random);

		public abstract String getBigSecretRoom(Random random);
	}

	static class SecondFloorRoomPool extends WoodlandMansionGenerator.RoomPool {
		@Override
		public String getSmallRoom(Random random) {
			return "1x1_b" + (random.nextInt(4) + 1);
		}

		@Override
		public String getSmallSecretRoom(Random random) {
			return "1x1_as" + (random.nextInt(4) + 1);
		}

		@Override
		public String getMediumFunctionalRoom(Random random, boolean staircase) {
			return staircase ? "1x2_c_stairs" : "1x2_c" + (random.nextInt(4) + 1);
		}

		@Override
		public String getMediumGenericRoom(Random random, boolean staircase) {
			return staircase ? "1x2_d_stairs" : "1x2_d" + (random.nextInt(5) + 1);
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
	}
}
