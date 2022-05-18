package net.minecraft.structure;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;

/**
 * The generator for the woodland mansion structure.
 * 
 * <p>The cobblestones underneath the mansion are generated {@linkplain
 * net.minecraft.world.gen.structure.WoodlandMansionStructure#postPlace after
 * the mansion placement}.
 */
public class WoodlandMansionGenerator {
	public static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, List<WoodlandMansionGenerator.Piece> pieces, Random random) {
		WoodlandMansionGenerator.MansionParameters mansionParameters = new WoodlandMansionGenerator.MansionParameters(random);
		WoodlandMansionGenerator.LayoutGenerator layoutGenerator = new WoodlandMansionGenerator.LayoutGenerator(manager, random);
		layoutGenerator.generate(pos, rotation, pieces, mansionParameters);
	}

	public static void printRandomFloorLayouts(String[] args) {
		Random random = Random.create();
		long l = random.nextLong();
		System.out.println("Seed: " + l);
		random.setSeed(l);
		WoodlandMansionGenerator.MansionParameters mansionParameters = new WoodlandMansionGenerator.MansionParameters(random);
		mansionParameters.printFloorLayouts();
	}

	/**
	 * The {@link RoomPool} used for the first floor.
	 */
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

	/**
	 * A matrix used to store floor information. Each element in this matrix
	 * corresponds to the cell on the floor.
	 */
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

		/**
		 * Updates the element in {@code (i, j)} to {@code newValue} if the
		 * current value is equal to {@code expected}.
		 */
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

	/**
	 * Populates structure pieces based on the given {@link MansionParameters
	 * parameters}.
	 */
	static class LayoutGenerator {
		private final StructureManager manager;
		private final Random random;
		private int entranceI;
		private int entranceJ;

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

			WoodlandMansionGenerator.FlagMatrix flagMatrix = parameters.baseLayout;
			WoodlandMansionGenerator.FlagMatrix flagMatrix2 = parameters.thirdFloorLayout;
			this.entranceI = parameters.entranceI + 1;
			this.entranceJ = parameters.entranceJ + 1;
			int i = parameters.entranceI + 1;
			int j = parameters.entranceJ;
			this.addOuterWall(pieces, generationPiece, flagMatrix, Direction.SOUTH, this.entranceI, this.entranceJ, i, j);
			this.addOuterWall(pieces, generationPiece2, flagMatrix, Direction.SOUTH, this.entranceI, this.entranceJ, i, j);
			WoodlandMansionGenerator.GenerationPiece generationPiece3 = new WoodlandMansionGenerator.GenerationPiece();
			generationPiece3.position = generationPiece.position.up(19);
			generationPiece3.rotation = generationPiece.rotation;
			generationPiece3.template = "wall_window";
			boolean bl = false;

			for (int k = 0; k < flagMatrix2.m && !bl; k++) {
				for (int l = flagMatrix2.n - 1; l >= 0 && !bl; l--) {
					if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(flagMatrix2, l, k)) {
						generationPiece3.position = generationPiece3.position.offset(rotation.rotate(Direction.SOUTH), 8 + (k - this.entranceJ) * 8);
						generationPiece3.position = generationPiece3.position.offset(rotation.rotate(Direction.EAST), (l - this.entranceI) * 8);
						this.addWallPiece(pieces, generationPiece3);
						this.addOuterWall(pieces, generationPiece3, flagMatrix2, Direction.SOUTH, l, k, l, k);
						bl = true;
					}
				}
			}

			this.addRoof(pieces, pos.up(16), rotation, flagMatrix, flagMatrix2);
			this.addRoof(pieces, pos.up(27), rotation, flagMatrix2, null);
			if (!pieces.isEmpty()) {
			}

			WoodlandMansionGenerator.RoomPool[] roomPools = new WoodlandMansionGenerator.RoomPool[]{
				new WoodlandMansionGenerator.FirstFloorRoomPool(), new WoodlandMansionGenerator.SecondFloorRoomPool(), new WoodlandMansionGenerator.ThirdFloorRoomPool()
			};

			for (int lx = 0; lx < 3; lx++) {
				BlockPos blockPos = pos.up(8 * lx + (lx == 2 ? 3 : 0));
				WoodlandMansionGenerator.FlagMatrix flagMatrix3 = parameters.roomFlagsByFloor[lx];
				WoodlandMansionGenerator.FlagMatrix flagMatrix4 = lx == 2 ? flagMatrix2 : flagMatrix;
				String string = lx == 0 ? "carpet_south_1" : "carpet_south_2";
				String string2 = lx == 0 ? "carpet_west_1" : "carpet_west_2";

				for (int m = 0; m < flagMatrix4.m; m++) {
					for (int n = 0; n < flagMatrix4.n; n++) {
						if (flagMatrix4.get(n, m) == WoodlandMansionGenerator.MansionParameters.CORRIDOR) {
							BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (m - this.entranceJ) * 8);
							blockPos2 = blockPos2.offset(rotation.rotate(Direction.EAST), (n - this.entranceI) * 8);
							pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "corridor_floor", blockPos2, rotation));
							if (flagMatrix4.get(n, m - 1) == WoodlandMansionGenerator.MansionParameters.CORRIDOR || (flagMatrix3.get(n, m - 1) & 8388608) == 8388608) {
								pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "carpet_north", blockPos2.offset(rotation.rotate(Direction.EAST), 1).up(), rotation));
							}

							if (flagMatrix4.get(n + 1, m) == WoodlandMansionGenerator.MansionParameters.CORRIDOR || (flagMatrix3.get(n + 1, m) & 8388608) == 8388608) {
								pieces.add(
									new WoodlandMansionGenerator.Piece(
										this.manager, "carpet_east", blockPos2.offset(rotation.rotate(Direction.SOUTH), 1).offset(rotation.rotate(Direction.EAST), 5).up(), rotation
									)
								);
							}

							if (flagMatrix4.get(n, m + 1) == WoodlandMansionGenerator.MansionParameters.CORRIDOR || (flagMatrix3.get(n, m + 1) & 8388608) == 8388608) {
								pieces.add(
									new WoodlandMansionGenerator.Piece(
										this.manager, string, blockPos2.offset(rotation.rotate(Direction.SOUTH), 5).offset(rotation.rotate(Direction.WEST), 1), rotation
									)
								);
							}

							if (flagMatrix4.get(n - 1, m) == WoodlandMansionGenerator.MansionParameters.CORRIDOR || (flagMatrix3.get(n - 1, m) & 8388608) == 8388608) {
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
						boolean bl2 = lx == 2 && flagMatrix4.get(p, o) == WoodlandMansionGenerator.MansionParameters.STAIRCASE;
						if (flagMatrix4.get(p, o) == WoodlandMansionGenerator.MansionParameters.ROOM || bl2) {
							int q = flagMatrix3.get(p, o);
							int r = q & 983040;
							int s = q & 65535;
							bl2 = bl2 && (q & 8388608) == 8388608;
							list.clear();
							if ((q & 2097152) == 2097152) {
								for (Direction direction : Direction.Type.HORIZONTAL) {
									if (flagMatrix4.get(p + direction.getOffsetX(), o + direction.getOffsetZ()) == WoodlandMansionGenerator.MansionParameters.CORRIDOR) {
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

							BlockPos blockPos3 = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (o - this.entranceJ) * 8);
							blockPos3 = blockPos3.offset(rotation.rotate(Direction.EAST), -1 + (p - this.entranceI) * 8);
							if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(flagMatrix4, p - 1, o) && !parameters.isRoomId(flagMatrix4, p - 1, o, lx, s)) {
								pieces.add(new WoodlandMansionGenerator.Piece(this.manager, direction2 == Direction.WEST ? string4 : string3, blockPos3, rotation));
							}

							if (flagMatrix4.get(p + 1, o) == WoodlandMansionGenerator.MansionParameters.CORRIDOR && !bl2) {
								BlockPos blockPos4 = blockPos3.offset(rotation.rotate(Direction.EAST), 8);
								pieces.add(new WoodlandMansionGenerator.Piece(this.manager, direction2 == Direction.EAST ? string4 : string3, blockPos4, rotation));
							}

							if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(flagMatrix4, p, o + 1) && !parameters.isRoomId(flagMatrix4, p, o + 1, lx, s)) {
								BlockPos blockPos4 = blockPos3.offset(rotation.rotate(Direction.SOUTH), 7);
								blockPos4 = blockPos4.offset(rotation.rotate(Direction.EAST), 7);
								pieces.add(
									new WoodlandMansionGenerator.Piece(
										this.manager, direction2 == Direction.SOUTH ? string4 : string3, blockPos4, rotation.rotate(BlockRotation.CLOCKWISE_90)
									)
								);
							}

							if (flagMatrix4.get(p, o - 1) == WoodlandMansionGenerator.MansionParameters.CORRIDOR && !bl2) {
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
								Direction direction3 = parameters.findConnectedRoomDirection(flagMatrix4, p, o, lx, s);
								boolean bl3 = (q & 4194304) == 4194304;
								this.addMediumRoom(pieces, blockPos3, rotation, direction3, direction2, roomPools[lx], bl3);
							} else if (r == 262144 && direction2 != null && direction2 != Direction.UP) {
								Direction direction3 = direction2.rotateYClockwise();
								if (!parameters.isRoomId(flagMatrix4, p + direction3.getOffsetX(), o + direction3.getOffsetZ(), lx, s)) {
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

		private void addOuterWall(
			List<WoodlandMansionGenerator.Piece> pieces,
			WoodlandMansionGenerator.GenerationPiece wallPiece,
			WoodlandMansionGenerator.FlagMatrix layout,
			Direction direction,
			int startI,
			int startJ,
			int endI,
			int endJ
		) {
			int i = startI;
			int j = startJ;
			Direction direction2 = direction;

			do {
				if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, i + direction.getOffsetX(), j + direction.getOffsetZ())) {
					this.turnLeft(pieces, wallPiece);
					direction = direction.rotateYClockwise();
					if (i != endI || j != endJ || direction2 != direction) {
						this.addWallPiece(pieces, wallPiece);
					}
				} else if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, i + direction.getOffsetX(), j + direction.getOffsetZ())
					&& WoodlandMansionGenerator.MansionParameters.isInsideMansion(
						layout,
						i + direction.getOffsetX() + direction.rotateYCounterclockwise().getOffsetX(),
						j + direction.getOffsetZ() + direction.rotateYCounterclockwise().getOffsetZ()
					)) {
					this.turnRight(pieces, wallPiece);
					i += direction.getOffsetX();
					j += direction.getOffsetZ();
					direction = direction.rotateYCounterclockwise();
				} else {
					i += direction.getOffsetX();
					j += direction.getOffsetZ();
					if (i != endI || j != endJ || direction2 != direction) {
						this.addWallPiece(pieces, wallPiece);
					}
				}
			} while (i != endI || j != endJ || direction2 != direction);
		}

		private void addRoof(
			List<WoodlandMansionGenerator.Piece> pieces,
			BlockPos pos,
			BlockRotation rotation,
			WoodlandMansionGenerator.FlagMatrix layout,
			@Nullable WoodlandMansionGenerator.FlagMatrix nextFloorLayout
		) {
			for (int i = 0; i < layout.m; i++) {
				for (int j = 0; j < layout.n; j++) {
					BlockPos blockPos = pos.offset(rotation.rotate(Direction.SOUTH), 8 + (i - this.entranceJ) * 8);
					blockPos = blockPos.offset(rotation.rotate(Direction.EAST), (j - this.entranceI) * 8);
					boolean bl = nextFloorLayout != null && WoodlandMansionGenerator.MansionParameters.isInsideMansion(nextFloorLayout, j, i);
					if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i) && !bl) {
						pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof", blockPos.up(3), rotation));
						if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i)) {
							BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 6);
							pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_front", blockPos2, rotation));
						}

						if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i)) {
							BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 0);
							blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 7);
							pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_front", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180)));
						}

						if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
							BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.WEST), 1);
							pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_front", blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
						}

						if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) {
							BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 6);
							blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
							pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_front", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_90)));
						}
					}
				}
			}

			if (nextFloorLayout != null) {
				for (int i = 0; i < layout.m; i++) {
					for (int jx = 0; jx < layout.n; jx++) {
						BlockPos var17 = pos.offset(rotation.rotate(Direction.SOUTH), 8 + (i - this.entranceJ) * 8);
						var17 = var17.offset(rotation.rotate(Direction.EAST), (jx - this.entranceI) * 8);
						boolean bl = WoodlandMansionGenerator.MansionParameters.isInsideMansion(nextFloorLayout, jx, i);
						if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jx, i) && bl) {
							if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jx + 1, i)) {
								BlockPos blockPos2 = var17.offset(rotation.rotate(Direction.EAST), 7);
								pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall", blockPos2, rotation));
							}

							if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jx - 1, i)) {
								BlockPos blockPos2 = var17.offset(rotation.rotate(Direction.WEST), 1);
								blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
								pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180)));
							}

							if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jx, i - 1)) {
								BlockPos blockPos2 = var17.offset(rotation.rotate(Direction.WEST), 0);
								blockPos2 = blockPos2.offset(rotation.rotate(Direction.NORTH), 1);
								pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall", blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
							}

							if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jx, i + 1)) {
								BlockPos blockPos2 = var17.offset(rotation.rotate(Direction.EAST), 6);
								blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 7);
								pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_90)));
							}

							if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jx + 1, i)) {
								if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jx, i - 1)) {
									BlockPos blockPos2 = var17.offset(rotation.rotate(Direction.EAST), 7);
									blockPos2 = blockPos2.offset(rotation.rotate(Direction.NORTH), 2);
									pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall_corner", blockPos2, rotation));
								}

								if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jx, i + 1)) {
									BlockPos blockPos2 = var17.offset(rotation.rotate(Direction.EAST), 8);
									blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 7);
									pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall_corner", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_90)));
								}
							}

							if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jx - 1, i)) {
								if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jx, i - 1)) {
									BlockPos blockPos2 = var17.offset(rotation.rotate(Direction.WEST), 2);
									blockPos2 = blockPos2.offset(rotation.rotate(Direction.NORTH), 1);
									pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall_corner", blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
								}

								if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jx, i + 1)) {
									BlockPos blockPos2 = var17.offset(rotation.rotate(Direction.WEST), 1);
									blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 8);
									pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall_corner", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180)));
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < layout.m; i++) {
				for (int jxx = 0; jxx < layout.n; jxx++) {
					BlockPos var19 = pos.offset(rotation.rotate(Direction.SOUTH), 8 + (i - this.entranceJ) * 8);
					var19 = var19.offset(rotation.rotate(Direction.EAST), (jxx - this.entranceI) * 8);
					boolean bl = nextFloorLayout != null && WoodlandMansionGenerator.MansionParameters.isInsideMansion(nextFloorLayout, jxx, i);
					if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jxx, i) && !bl) {
						if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jxx + 1, i)) {
							BlockPos blockPos2 = var19.offset(rotation.rotate(Direction.EAST), 6);
							if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jxx, i + 1)) {
								BlockPos blockPos3 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
								pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_corner", blockPos3, rotation));
							} else if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jxx + 1, i + 1)) {
								BlockPos blockPos3 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 5);
								pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_inner_corner", blockPos3, rotation));
							}

							if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jxx, i - 1)) {
								pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_corner", blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
							} else if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jxx + 1, i - 1)) {
								BlockPos blockPos3 = var19.offset(rotation.rotate(Direction.EAST), 9);
								blockPos3 = blockPos3.offset(rotation.rotate(Direction.NORTH), 2);
								pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_inner_corner", blockPos3, rotation.rotate(BlockRotation.CLOCKWISE_90)));
							}
						}

						if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jxx - 1, i)) {
							BlockPos blockPos2x = var19.offset(rotation.rotate(Direction.EAST), 0);
							blockPos2x = blockPos2x.offset(rotation.rotate(Direction.SOUTH), 0);
							if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jxx, i + 1)) {
								BlockPos blockPos3 = blockPos2x.offset(rotation.rotate(Direction.SOUTH), 6);
								pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_corner", blockPos3, rotation.rotate(BlockRotation.CLOCKWISE_90)));
							} else if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jxx - 1, i + 1)) {
								BlockPos blockPos3 = blockPos2x.offset(rotation.rotate(Direction.SOUTH), 8);
								blockPos3 = blockPos3.offset(rotation.rotate(Direction.WEST), 3);
								pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_inner_corner", blockPos3, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
							}

							if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jxx, i - 1)) {
								pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_corner", blockPos2x, rotation.rotate(BlockRotation.CLOCKWISE_180)));
							} else if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, jxx - 1, i - 1)) {
								BlockPos blockPos3 = blockPos2x.offset(rotation.rotate(Direction.SOUTH), 1);
								pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_inner_corner", blockPos3, rotation.rotate(BlockRotation.CLOCKWISE_180)));
							}
						}
					}
				}
			}
		}

		private void addEntrance(List<WoodlandMansionGenerator.Piece> pieces, WoodlandMansionGenerator.GenerationPiece wallPiece) {
			Direction direction = wallPiece.rotation.rotate(Direction.WEST);
			pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "entrance", wallPiece.position.offset(direction, 9), wallPiece.rotation));
			wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), 16);
		}

		private void addWallPiece(List<WoodlandMansionGenerator.Piece> pieces, WoodlandMansionGenerator.GenerationPiece wallPiece) {
			pieces.add(
				new WoodlandMansionGenerator.Piece(
					this.manager, wallPiece.template, wallPiece.position.offset(wallPiece.rotation.rotate(Direction.EAST), 7), wallPiece.rotation
				)
			);
			wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), 8);
		}

		private void turnLeft(List<WoodlandMansionGenerator.Piece> pieces, WoodlandMansionGenerator.GenerationPiece wallPiece) {
			wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), -1);
			pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "wall_corner", wallPiece.position, wallPiece.rotation));
			wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), -7);
			wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.WEST), -6);
			wallPiece.rotation = wallPiece.rotation.rotate(BlockRotation.CLOCKWISE_90);
		}

		private void turnRight(List<WoodlandMansionGenerator.Piece> pieces, WoodlandMansionGenerator.GenerationPiece wallPiece) {
			wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), 6);
			wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.EAST), 8);
			wallPiece.rotation = wallPiece.rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
		}

		private void addSmallRoom(
			List<WoodlandMansionGenerator.Piece> pieces, BlockPos pos, BlockRotation rotation, Direction direction, WoodlandMansionGenerator.RoomPool pool
		) {
			BlockRotation blockRotation = BlockRotation.NONE;
			String string = pool.getSmallRoom(this.random);
			if (direction != Direction.EAST) {
				if (direction == Direction.NORTH) {
					blockRotation = blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
				} else if (direction == Direction.WEST) {
					blockRotation = blockRotation.rotate(BlockRotation.CLOCKWISE_180);
				} else if (direction == Direction.SOUTH) {
					blockRotation = blockRotation.rotate(BlockRotation.CLOCKWISE_90);
				} else {
					string = pool.getSmallSecretRoom(this.random);
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
			Direction connectedRoomDirection,
			Direction entranceDirection,
			WoodlandMansionGenerator.RoomPool pool,
			boolean staircase
		) {
			if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.SOUTH) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
				pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation));
			} else if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.NORTH) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
				blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
				pieces.add(
					new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation, BlockMirror.LEFT_RIGHT)
				);
			} else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.NORTH) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
				blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
				pieces.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_180)
					)
				);
			} else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.SOUTH) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
				pieces.add(
					new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation, BlockMirror.FRONT_BACK)
				);
			} else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.EAST) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
				pieces.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.LEFT_RIGHT
					)
				);
			} else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.WEST) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
				pieces.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90)
					)
				);
			} else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.WEST) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
				blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
				pieces.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.FRONT_BACK
					)
				);
			} else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.EAST) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
				blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
				pieces.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)
					)
				);
			} else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.NORTH) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
				blockPos = blockPos.offset(rotation.rotate(Direction.NORTH), 8);
				pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumGenericRoom(this.random, staircase), blockPos, rotation));
			} else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.SOUTH) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
				blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 14);
				pieces.add(
					new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumGenericRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_180))
				);
			} else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.EAST) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 15);
				pieces.add(
					new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumGenericRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90))
				);
			} else if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.WEST) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.WEST), 7);
				blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
				pieces.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, pool.getMediumGenericRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)
					)
				);
			} else if (entranceDirection == Direction.UP && connectedRoomDirection == Direction.EAST) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 15);
				pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumSecretRoom(this.random), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90)));
			} else if (entranceDirection == Direction.UP && connectedRoomDirection == Direction.SOUTH) {
				BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
				blockPos = blockPos.offset(rotation.rotate(Direction.NORTH), 0);
				pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumSecretRoom(this.random), blockPos, rotation));
			}
		}

		private void addBigRoom(
			List<WoodlandMansionGenerator.Piece> pieces,
			BlockPos pos,
			BlockRotation rotation,
			Direction connectedRoomDirection,
			Direction entranceDirection,
			WoodlandMansionGenerator.RoomPool pool
		) {
			int i = 0;
			int j = 0;
			BlockRotation blockRotation = rotation;
			BlockMirror blockMirror = BlockMirror.NONE;
			if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.SOUTH) {
				i = -7;
			} else if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.NORTH) {
				i = -7;
				j = 6;
				blockMirror = BlockMirror.LEFT_RIGHT;
			} else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.EAST) {
				i = 1;
				j = 14;
				blockRotation = rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
			} else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.WEST) {
				i = 7;
				j = 14;
				blockRotation = rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
				blockMirror = BlockMirror.LEFT_RIGHT;
			} else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.WEST) {
				i = 7;
				j = -8;
				blockRotation = rotation.rotate(BlockRotation.CLOCKWISE_90);
			} else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.EAST) {
				i = 1;
				j = -8;
				blockRotation = rotation.rotate(BlockRotation.CLOCKWISE_90);
				blockMirror = BlockMirror.LEFT_RIGHT;
			} else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.NORTH) {
				i = 15;
				j = 6;
				blockRotation = rotation.rotate(BlockRotation.CLOCKWISE_180);
			} else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.SOUTH) {
				i = 15;
				blockMirror = BlockMirror.FRONT_BACK;
			}

			BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), i);
			blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), j);
			pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getBigRoom(this.random), blockPos, blockRotation, blockMirror));
		}

		private void addBigSecretRoom(List<WoodlandMansionGenerator.Piece> pieces, BlockPos pos, BlockRotation rotation, WoodlandMansionGenerator.RoomPool pool) {
			BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
			pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getBigSecretRoom(this.random), blockPos, rotation, BlockMirror.NONE));
		}
	}

	/**
	 * The parameters that control how the mansion will look like. It's
	 * generated before adding structure pieces and passed to {@link
	 * LayoutGenerator}.
	 */
	static class MansionParameters {
		private static final int SIZE = 11;
		private static final int UNSET = 0;
		private static final int CORRIDOR = 1;
		private static final int ROOM = 2;
		private static final int STAIRCASE = 3;
		private static final int UNUSED = 4;
		private static final int OUTSIDE = 5;
		/**
		 * Whether the room has size 1x1.
		 */
		private static final int SMALL_ROOM_FLAG = 65536;
		/**
		 * Whether the room has size 1x2.
		 */
		private static final int MEDIUM_ROOM_FLAG = 131072;
		/**
		 * Whether the room has size 2x2.
		 */
		private static final int BIG_ROOM_FLAG = 262144;
		/**
		 * Whether the cell is an origin of the room.
		 */
		private static final int ORIGIN_CELL_FLAG = 1048576;
		/**
		 * Whether the cell is an entrance of the room.
		 */
		private static final int ENTRANCE_CELL_FLAG = 2097152;
		/**
		 * Whether the cell is a part of a staircase room.
		 */
		private static final int STAIRCASE_CELL_FLAG = 4194304;
		/**
		 * Whether the cell has carpet.
		 */
		private static final int CARPET_CELL_FLAG = 8388608;
		/**
		 * The mask for the room size. The value takes one of {@link
		 * #SMALL_ROOM_FLAG}, {@link #MEDIUM_ROOM_FLAG} or {@link #BIG_ROOM_FLAG}.
		 */
		private static final int ROOM_SIZE_MASK = 983040;
		/**
		 * The mask for the room ID. Connected rooms share the same ID.
		 */
		private static final int ROOM_ID_MASK = 65535;
		private final Random random;
		/**
		 * Determines a rough shape of the first floor and the second floor.
		 * 
		 * <p>Each element in this matrix is one of {@link #UNSET},
		 * {@link #CORRIDOR}, {@link #ROOM}, {@link #STAIRCASE}, {@link #UNUSED} or
		 * {@link #OUTSIDE}.
		 */
		final WoodlandMansionGenerator.FlagMatrix baseLayout;
		/**
		 * Determines a rough shape of the third floor.
		 * 
		 * <p>Each element in this matrix is one of {@link #UNSET},
		 * {@link #CORRIDOR}, {@link #ROOM}, {@link #STAIRCASE}, {@link #UNUSED} or
		 * {@link #OUTSIDE}.
		 */
		final WoodlandMansionGenerator.FlagMatrix thirdFloorLayout;
		/**
		 * Contains flags for each room cell on each floor.
		 * 
		 * <p>Each element in the matrix consists of the following fields:
		 * <ul>
		 *   <li>{@linkplain #ROOM_ID_MASK room ID} (16 bit)
		 *   <li>{@linkplain #ROOM_SIZE_MASK room size} (4 bit)
		 *   <li>{@link #ORIGIN_CELL_FLAG} (1 bit)
		 *   <li>{@link #ENTRANCE_CELL_FLAG} (1 bit)
		 *   <li>{@link #STAIRCASE_CELL_FLAG} (1 bit)
		 *   <li>{@link #CARPET_CELL_FLAG} (1 bit)
		 * </ul>
		 */
		final WoodlandMansionGenerator.FlagMatrix[] roomFlagsByFloor;
		final int entranceI;
		final int entranceJ;

		public MansionParameters(Random random) {
			this.random = random;
			int i = 11;
			this.entranceI = 7;
			this.entranceJ = 4;
			this.baseLayout = new WoodlandMansionGenerator.FlagMatrix(SIZE, SIZE, OUTSIDE);
			this.baseLayout.fill(this.entranceI, this.entranceJ, this.entranceI + 1, this.entranceJ + 1, STAIRCASE);
			this.baseLayout.fill(this.entranceI - 1, this.entranceJ, this.entranceI - 1, this.entranceJ + 1, ROOM);
			this.baseLayout.fill(this.entranceI + 2, this.entranceJ - 2, this.entranceI + 3, this.entranceJ + 3, OUTSIDE);
			this.baseLayout.fill(this.entranceI + 1, this.entranceJ - 2, this.entranceI + 1, this.entranceJ - 1, CORRIDOR);
			this.baseLayout.fill(this.entranceI + 1, this.entranceJ + 2, this.entranceI + 1, this.entranceJ + 3, CORRIDOR);
			this.baseLayout.set(this.entranceI - 1, this.entranceJ - 1, CORRIDOR);
			this.baseLayout.set(this.entranceI - 1, this.entranceJ + 2, CORRIDOR);
			this.baseLayout.fill(0, 0, 11, 1, OUTSIDE);
			this.baseLayout.fill(0, 9, 11, 11, OUTSIDE);
			this.layoutCorridor(this.baseLayout, this.entranceI, this.entranceJ - 2, Direction.WEST, 6);
			this.layoutCorridor(this.baseLayout, this.entranceI, this.entranceJ + 3, Direction.WEST, 6);
			this.layoutCorridor(this.baseLayout, this.entranceI - 2, this.entranceJ - 1, Direction.WEST, 3);
			this.layoutCorridor(this.baseLayout, this.entranceI - 2, this.entranceJ + 2, Direction.WEST, 3);

			while (this.adjustLayoutWithRooms(this.baseLayout)) {
			}

			this.roomFlagsByFloor = new WoodlandMansionGenerator.FlagMatrix[3];
			this.roomFlagsByFloor[0] = new WoodlandMansionGenerator.FlagMatrix(SIZE, SIZE, OUTSIDE);
			this.roomFlagsByFloor[1] = new WoodlandMansionGenerator.FlagMatrix(SIZE, SIZE, OUTSIDE);
			this.roomFlagsByFloor[2] = new WoodlandMansionGenerator.FlagMatrix(SIZE, SIZE, OUTSIDE);
			this.updateRoomFlags(this.baseLayout, this.roomFlagsByFloor[0]);
			this.updateRoomFlags(this.baseLayout, this.roomFlagsByFloor[1]);
			this.roomFlagsByFloor[0].fill(this.entranceI + 1, this.entranceJ, this.entranceI + 1, this.entranceJ + 1, 8388608);
			this.roomFlagsByFloor[1].fill(this.entranceI + 1, this.entranceJ, this.entranceI + 1, this.entranceJ + 1, 8388608);
			this.thirdFloorLayout = new WoodlandMansionGenerator.FlagMatrix(this.baseLayout.n, this.baseLayout.m, OUTSIDE);
			this.layoutThirdFloor();
			this.updateRoomFlags(this.thirdFloorLayout, this.roomFlagsByFloor[2]);
		}

		public static boolean isInsideMansion(WoodlandMansionGenerator.FlagMatrix layout, int i, int j) {
			int k = layout.get(i, j);
			return k == CORRIDOR || k == ROOM || k == STAIRCASE || k == UNUSED;
		}

		public boolean isRoomId(WoodlandMansionGenerator.FlagMatrix layout, int i, int j, int floor, int roomId) {
			return (this.roomFlagsByFloor[floor].get(i, j) & 65535) == roomId;
		}

		@Nullable
		public Direction findConnectedRoomDirection(WoodlandMansionGenerator.FlagMatrix layout, int i, int j, int floor, int roomId) {
			for (Direction direction : Direction.Type.HORIZONTAL) {
				if (this.isRoomId(layout, i + direction.getOffsetX(), j + direction.getOffsetZ(), floor, roomId)) {
					return direction;
				}
			}

			return null;
		}

		private void layoutCorridor(WoodlandMansionGenerator.FlagMatrix layout, int i, int j, Direction direction, int length) {
			if (length > 0) {
				layout.set(i, j, CORRIDOR);
				layout.update(i + direction.getOffsetX(), j + direction.getOffsetZ(), UNSET, CORRIDOR);

				for (int k = 0; k < 8; k++) {
					Direction direction2 = Direction.fromHorizontal(this.random.nextInt(4));
					if (direction2 != direction.getOpposite() && (direction2 != Direction.EAST || !this.random.nextBoolean())) {
						int l = i + direction.getOffsetX();
						int m = j + direction.getOffsetZ();
						if (layout.get(l + direction2.getOffsetX(), m + direction2.getOffsetZ()) == 0
							&& layout.get(l + direction2.getOffsetX() * 2, m + direction2.getOffsetZ() * 2) == 0) {
							this.layoutCorridor(
								layout, i + direction.getOffsetX() + direction2.getOffsetX(), j + direction.getOffsetZ() + direction2.getOffsetZ(), direction2, length - 1
							);
							break;
						}
					}
				}

				Direction direction3 = direction.rotateYClockwise();
				Direction direction2 = direction.rotateYCounterclockwise();
				layout.update(i + direction3.getOffsetX(), j + direction3.getOffsetZ(), UNSET, ROOM);
				layout.update(i + direction2.getOffsetX(), j + direction2.getOffsetZ(), UNSET, ROOM);
				layout.update(i + direction.getOffsetX() + direction3.getOffsetX(), j + direction.getOffsetZ() + direction3.getOffsetZ(), UNSET, ROOM);
				layout.update(i + direction.getOffsetX() + direction2.getOffsetX(), j + direction.getOffsetZ() + direction2.getOffsetZ(), UNSET, ROOM);
				layout.update(i + direction.getOffsetX() * 2, j + direction.getOffsetZ() * 2, UNSET, ROOM);
				layout.update(i + direction3.getOffsetX() * 2, j + direction3.getOffsetZ() * 2, UNSET, ROOM);
				layout.update(i + direction2.getOffsetX() * 2, j + direction2.getOffsetZ() * 2, UNSET, ROOM);
			}
		}

		private boolean adjustLayoutWithRooms(WoodlandMansionGenerator.FlagMatrix layout) {
			boolean bl = false;

			for (int i = 0; i < layout.m; i++) {
				for (int j = 0; j < layout.n; j++) {
					if (layout.get(j, i) == 0) {
						int k = 0;
						k += isInsideMansion(layout, j + 1, i) ? 1 : 0;
						k += isInsideMansion(layout, j - 1, i) ? 1 : 0;
						k += isInsideMansion(layout, j, i + 1) ? 1 : 0;
						k += isInsideMansion(layout, j, i - 1) ? 1 : 0;
						if (k >= 3) {
							layout.set(j, i, ROOM);
							bl = true;
						} else if (k == 2) {
							int l = 0;
							l += isInsideMansion(layout, j + 1, i + 1) ? 1 : 0;
							l += isInsideMansion(layout, j - 1, i + 1) ? 1 : 0;
							l += isInsideMansion(layout, j + 1, i - 1) ? 1 : 0;
							l += isInsideMansion(layout, j - 1, i - 1) ? 1 : 0;
							if (l <= 1) {
								layout.set(j, i, ROOM);
								bl = true;
							}
						}
					}
				}
			}

			return bl;
		}

		private void layoutThirdFloor() {
			List<Pair<Integer, Integer>> list = Lists.<Pair<Integer, Integer>>newArrayList();
			WoodlandMansionGenerator.FlagMatrix flagMatrix = this.roomFlagsByFloor[1];

			for (int i = 0; i < this.thirdFloorLayout.m; i++) {
				for (int j = 0; j < this.thirdFloorLayout.n; j++) {
					int k = flagMatrix.get(j, i);
					int l = k & 983040;
					if (l == 131072 && (k & 2097152) == 2097152) {
						list.add(new Pair<>(j, i));
					}
				}
			}

			if (list.isEmpty()) {
				this.thirdFloorLayout.fill(0, 0, this.thirdFloorLayout.n, this.thirdFloorLayout.m, OUTSIDE);
			} else {
				Pair<Integer, Integer> pair = (Pair<Integer, Integer>)list.get(this.random.nextInt(list.size()));
				int jx = flagMatrix.get(pair.getLeft(), pair.getRight());
				flagMatrix.set(pair.getLeft(), pair.getRight(), jx | 4194304);
				Direction direction = this.findConnectedRoomDirection(this.baseLayout, pair.getLeft(), pair.getRight(), 1, jx & 65535);
				int l = pair.getLeft() + direction.getOffsetX();
				int m = pair.getRight() + direction.getOffsetZ();

				for (int n = 0; n < this.thirdFloorLayout.m; n++) {
					for (int o = 0; o < this.thirdFloorLayout.n; o++) {
						if (!isInsideMansion(this.baseLayout, o, n)) {
							this.thirdFloorLayout.set(o, n, OUTSIDE);
						} else if (o == pair.getLeft() && n == pair.getRight()) {
							this.thirdFloorLayout.set(o, n, STAIRCASE);
						} else if (o == l && n == m) {
							this.thirdFloorLayout.set(o, n, STAIRCASE);
							this.roomFlagsByFloor[2].set(o, n, 8388608);
						}
					}
				}

				List<Direction> list2 = Lists.<Direction>newArrayList();

				for (Direction direction2 : Direction.Type.HORIZONTAL) {
					if (this.thirdFloorLayout.get(l + direction2.getOffsetX(), m + direction2.getOffsetZ()) == 0) {
						list2.add(direction2);
					}
				}

				if (list2.isEmpty()) {
					this.thirdFloorLayout.fill(0, 0, this.thirdFloorLayout.n, this.thirdFloorLayout.m, OUTSIDE);
					flagMatrix.set(pair.getLeft(), pair.getRight(), jx);
				} else {
					Direction direction3 = (Direction)list2.get(this.random.nextInt(list2.size()));
					this.layoutCorridor(this.thirdFloorLayout, l + direction3.getOffsetX(), m + direction3.getOffsetZ(), direction3, 4);

					while (this.adjustLayoutWithRooms(this.thirdFloorLayout)) {
					}
				}
			}
		}

		private void updateRoomFlags(WoodlandMansionGenerator.FlagMatrix layout, WoodlandMansionGenerator.FlagMatrix roomFlags) {
			ObjectArrayList<Pair<Integer, Integer>> objectArrayList = new ObjectArrayList<>();

			for (int i = 0; i < layout.m; i++) {
				for (int j = 0; j < layout.n; j++) {
					if (layout.get(j, i) == ROOM) {
						objectArrayList.add(new Pair<>(j, i));
					}
				}
			}

			Util.shuffle(objectArrayList, this.random);
			int i = 10;

			for (Pair<Integer, Integer> pair : objectArrayList) {
				int k = pair.getLeft();
				int l = pair.getRight();
				if (roomFlags.get(k, l) == 0) {
					int m = k;
					int n = k;
					int o = l;
					int p = l;
					int q = 65536;
					if (roomFlags.get(k + 1, l) == 0
						&& roomFlags.get(k, l + 1) == 0
						&& roomFlags.get(k + 1, l + 1) == 0
						&& layout.get(k + 1, l) == ROOM
						&& layout.get(k, l + 1) == ROOM
						&& layout.get(k + 1, l + 1) == ROOM) {
						n = k + 1;
						p = l + 1;
						q = 262144;
					} else if (roomFlags.get(k - 1, l) == 0
						&& roomFlags.get(k, l + 1) == 0
						&& roomFlags.get(k - 1, l + 1) == 0
						&& layout.get(k - 1, l) == ROOM
						&& layout.get(k, l + 1) == ROOM
						&& layout.get(k - 1, l + 1) == ROOM) {
						m = k - 1;
						p = l + 1;
						q = 262144;
					} else if (roomFlags.get(k - 1, l) == 0
						&& roomFlags.get(k, l - 1) == 0
						&& roomFlags.get(k - 1, l - 1) == 0
						&& layout.get(k - 1, l) == ROOM
						&& layout.get(k, l - 1) == ROOM
						&& layout.get(k - 1, l - 1) == ROOM) {
						m = k - 1;
						o = l - 1;
						q = 262144;
					} else if (roomFlags.get(k + 1, l) == 0 && layout.get(k + 1, l) == ROOM) {
						n = k + 1;
						q = 131072;
					} else if (roomFlags.get(k, l + 1) == 0 && layout.get(k, l + 1) == ROOM) {
						p = l + 1;
						q = 131072;
					} else if (roomFlags.get(k - 1, l) == 0 && layout.get(k - 1, l) == ROOM) {
						m = k - 1;
						q = 131072;
					} else if (roomFlags.get(k, l - 1) == 0 && layout.get(k, l - 1) == ROOM) {
						o = l - 1;
						q = 131072;
					}

					int r = this.random.nextBoolean() ? m : n;
					int s = this.random.nextBoolean() ? o : p;
					int t = 2097152;
					if (!layout.anyMatchAround(r, s, CORRIDOR)) {
						r = r == m ? n : m;
						s = s == o ? p : o;
						if (!layout.anyMatchAround(r, s, CORRIDOR)) {
							s = s == o ? p : o;
							if (!layout.anyMatchAround(r, s, CORRIDOR)) {
								r = r == m ? n : m;
								s = s == o ? p : o;
								if (!layout.anyMatchAround(r, s, CORRIDOR)) {
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
								roomFlags.set(v, u, 1048576 | t | q | i);
							} else {
								roomFlags.set(v, u, q | i);
							}
						}
					}

					i++;
				}
			}
		}

		/**
		 * Prints a string representation of {@link #baseLayout} and {@link
		 * #thirdFloorLayout}. Useful for debugging.
		 * 
		 * @see WoodlandMansionGenerator#printRandomFloorLayouts
		 */
		public void printFloorLayouts() {
			for (int i = 0; i < 2; i++) {
				WoodlandMansionGenerator.FlagMatrix flagMatrix = i == 0 ? this.baseLayout : this.thirdFloorLayout;

				for (int j = 0; j < flagMatrix.m; j++) {
					for (int k = 0; k < flagMatrix.n; k++) {
						int l = flagMatrix.get(k, j);
						if (l == CORRIDOR) {
							System.out.print("+");
						} else if (l == UNUSED) {
							System.out.print("x");
						} else if (l == ROOM) {
							System.out.print("X");
						} else if (l == STAIRCASE) {
							System.out.print("O");
						} else if (l == OUTSIDE) {
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
				id -> createPlacementData(BlockMirror.valueOf(nbt.getString("Mi")), BlockRotation.valueOf(nbt.getString("Rot")))
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
				List<MobEntity> list = new ArrayList();
				switch (metadata) {
					case "Mage":
						list.add(EntityType.EVOKER.create(world.toServerWorld()));
						break;
					case "Warrior":
						list.add(EntityType.VINDICATOR.create(world.toServerWorld()));
						break;
					case "Group of Allays":
						int i = world.getRandom().nextInt(3) + 1;

						for (int j = 0; j < i; j++) {
							list.add(EntityType.ALLAY.create(world.toServerWorld()));
						}
						break;
					default:
						return;
				}

				for (MobEntity mobEntity : list) {
					mobEntity.setPersistent();
					mobEntity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
					mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.STRUCTURE, null, null);
					world.spawnEntityAndPassengers(mobEntity);
					world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
				}
			}
		}
	}

	/**
	 * Provides methods that sample room template identifiers.
	 */
	abstract static class RoomPool {
		public abstract String getSmallRoom(Random random);

		public abstract String getSmallSecretRoom(Random random);

		public abstract String getMediumFunctionalRoom(Random random, boolean staircase);

		public abstract String getMediumGenericRoom(Random random, boolean staircase);

		public abstract String getMediumSecretRoom(Random random);

		public abstract String getBigRoom(Random random);

		public abstract String getBigSecretRoom(Random random);
	}

	/**
	 * The {@link RoomPool} used for the second floor.
	 */
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

	/**
	 * The {@link RoomPool} used for the third floor.
	 */
	static class ThirdFloorRoomPool extends WoodlandMansionGenerator.SecondFloorRoomPool {
	}
}
