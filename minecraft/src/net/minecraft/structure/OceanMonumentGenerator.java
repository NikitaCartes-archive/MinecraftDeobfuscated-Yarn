package net.minecraft.structure;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class OceanMonumentGenerator {
	private OceanMonumentGenerator() {
	}

	public static class Base extends OceanMonumentGenerator.Piece {
		private static final int field_31602 = 58;
		private static final int field_31603 = 22;
		private static final int field_31604 = 58;
		public static final int field_31606 = 29;
		private static final int field_31605 = 61;
		private OceanMonumentGenerator.PieceSetting field_14464;
		private OceanMonumentGenerator.PieceSetting field_14466;
		private final List<OceanMonumentGenerator.Piece> field_14465 = Lists.<OceanMonumentGenerator.Piece>newArrayList();

		public Base(Random random, int i, int j, Direction orientation) {
			super(StructurePieceType.OCEAN_MONUMENT_BASE, orientation, 0, method_35454(i, 39, j, orientation, 58, 23, 58));
			this.setOrientation(orientation);
			List<OceanMonumentGenerator.PieceSetting> list = this.method_14760(random);
			this.field_14464.used = true;
			this.field_14465.add(new OceanMonumentGenerator.Entry(orientation, this.field_14464));
			this.field_14465.add(new OceanMonumentGenerator.CoreRoom(orientation, this.field_14466));
			List<OceanMonumentGenerator.PieceFactory> list2 = Lists.<OceanMonumentGenerator.PieceFactory>newArrayList();
			list2.add(new OceanMonumentGenerator.DoubleXYRoomFactory());
			list2.add(new OceanMonumentGenerator.DoubleYZRoomFactory());
			list2.add(new OceanMonumentGenerator.DoubleZRoomFactory());
			list2.add(new OceanMonumentGenerator.DoubleXRoomFactory());
			list2.add(new OceanMonumentGenerator.DoubleYRoomFactory());
			list2.add(new OceanMonumentGenerator.SimpleRoomTopFactory());
			list2.add(new OceanMonumentGenerator.SimpleRoomFactory());

			for (OceanMonumentGenerator.PieceSetting pieceSetting : list) {
				if (!pieceSetting.used && !pieceSetting.isAboveLevelThree()) {
					for (OceanMonumentGenerator.PieceFactory pieceFactory : list2) {
						if (pieceFactory.canGenerate(pieceSetting)) {
							this.field_14465.add(pieceFactory.generate(orientation, pieceSetting, random));
							break;
						}
					}
				}
			}

			BlockPos blockPos = this.offsetPos(9, 0, 22);

			for (OceanMonumentGenerator.Piece piece : this.field_14465) {
				piece.getBoundingBox().move(blockPos);
			}

			BlockBox blockBox = BlockBox.create(this.offsetPos(1, 1, 1), this.offsetPos(23, 8, 21));
			BlockBox blockBox2 = BlockBox.create(this.offsetPos(34, 1, 1), this.offsetPos(56, 8, 21));
			BlockBox blockBox3 = BlockBox.create(this.offsetPos(22, 13, 22), this.offsetPos(35, 17, 35));
			int k = random.nextInt();
			this.field_14465.add(new OceanMonumentGenerator.WingRoom(orientation, blockBox, k++));
			this.field_14465.add(new OceanMonumentGenerator.WingRoom(orientation, blockBox2, k++));
			this.field_14465.add(new OceanMonumentGenerator.Penthouse(orientation, blockBox3));
		}

		public Base(ServerWorld serverWorld, NbtCompound nbt) {
			super(StructurePieceType.OCEAN_MONUMENT_BASE, nbt);
		}

		private List<OceanMonumentGenerator.PieceSetting> method_14760(Random random) {
			OceanMonumentGenerator.PieceSetting[] pieceSettings = new OceanMonumentGenerator.PieceSetting[75];

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 4; j++) {
					int k = 0;
					int l = getIndex(i, 0, j);
					pieceSettings[l] = new OceanMonumentGenerator.PieceSetting(l);
				}
			}

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 4; j++) {
					int k = 1;
					int l = getIndex(i, 1, j);
					pieceSettings[l] = new OceanMonumentGenerator.PieceSetting(l);
				}
			}

			for (int i = 1; i < 4; i++) {
				for (int j = 0; j < 2; j++) {
					int k = 2;
					int l = getIndex(i, 2, j);
					pieceSettings[l] = new OceanMonumentGenerator.PieceSetting(l);
				}
			}

			this.field_14464 = pieceSettings[TWO_ZERO_ZERO_INDEX];

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					for (int k = 0; k < 3; k++) {
						int l = getIndex(i, k, j);
						if (pieceSettings[l] != null) {
							for (Direction direction : Direction.values()) {
								int m = i + direction.getOffsetX();
								int n = k + direction.getOffsetY();
								int o = j + direction.getOffsetZ();
								if (m >= 0 && m < 5 && o >= 0 && o < 5 && n >= 0 && n < 3) {
									int p = getIndex(m, n, o);
									if (pieceSettings[p] != null) {
										if (o == j) {
											pieceSettings[l].setNeighbor(direction, pieceSettings[p]);
										} else {
											pieceSettings[l].setNeighbor(direction.getOpposite(), pieceSettings[p]);
										}
									}
								}
							}
						}
					}
				}
			}

			OceanMonumentGenerator.PieceSetting pieceSetting = new OceanMonumentGenerator.PieceSetting(1003);
			OceanMonumentGenerator.PieceSetting pieceSetting2 = new OceanMonumentGenerator.PieceSetting(1001);
			OceanMonumentGenerator.PieceSetting pieceSetting3 = new OceanMonumentGenerator.PieceSetting(1002);
			pieceSettings[TWO_TWO_ZERO_INDEX].setNeighbor(Direction.UP, pieceSetting);
			pieceSettings[ZERO_ONE_ZERO_INDEX].setNeighbor(Direction.SOUTH, pieceSetting2);
			pieceSettings[FOUR_ONE_ZERO_INDEX].setNeighbor(Direction.SOUTH, pieceSetting3);
			pieceSetting.used = true;
			pieceSetting2.used = true;
			pieceSetting3.used = true;
			this.field_14464.field_14484 = true;
			this.field_14466 = pieceSettings[getIndex(random.nextInt(4), 0, 2)];
			this.field_14466.used = true;
			this.field_14466.neighbors[Direction.EAST.getId()].used = true;
			this.field_14466.neighbors[Direction.NORTH.getId()].used = true;
			this.field_14466.neighbors[Direction.EAST.getId()].neighbors[Direction.NORTH.getId()].used = true;
			this.field_14466.neighbors[Direction.UP.getId()].used = true;
			this.field_14466.neighbors[Direction.EAST.getId()].neighbors[Direction.UP.getId()].used = true;
			this.field_14466.neighbors[Direction.NORTH.getId()].neighbors[Direction.UP.getId()].used = true;
			this.field_14466.neighbors[Direction.EAST.getId()].neighbors[Direction.NORTH.getId()].neighbors[Direction.UP.getId()].used = true;
			List<OceanMonumentGenerator.PieceSetting> list = Lists.<OceanMonumentGenerator.PieceSetting>newArrayList();

			for (OceanMonumentGenerator.PieceSetting pieceSetting4 : pieceSettings) {
				if (pieceSetting4 != null) {
					pieceSetting4.checkNeighborStates();
					list.add(pieceSetting4);
				}
			}

			pieceSetting.checkNeighborStates();
			Collections.shuffle(list, random);
			int q = 1;

			for (OceanMonumentGenerator.PieceSetting pieceSetting5 : list) {
				int r = 0;
				int m = 0;

				while (r < 2 && m < 5) {
					m++;
					int n = random.nextInt(6);
					if (pieceSetting5.neighborPresences[n]) {
						int o = Direction.byId(n).getOpposite().getId();
						pieceSetting5.neighborPresences[n] = false;
						pieceSetting5.neighbors[n].neighborPresences[o] = false;
						if (pieceSetting5.method_14783(q++) && pieceSetting5.neighbors[n].method_14783(q++)) {
							r++;
						} else {
							pieceSetting5.neighborPresences[n] = true;
							pieceSetting5.neighbors[n].neighborPresences[o] = true;
						}
					}
				}
			}

			list.add(pieceSetting);
			list.add(pieceSetting2);
			list.add(pieceSetting3);
			return list;
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			int i = Math.max(world.getSeaLevel(), 64) - this.boundingBox.getMinY();
			this.setAirAndWater(world, boundingBox, 0, 0, 0, 58, i, 58);
			this.method_14761(false, 0, world, random, boundingBox);
			this.method_14761(true, 33, world, random, boundingBox);
			this.method_14763(world, random, boundingBox);
			this.method_14762(world, random, boundingBox);
			this.method_14765(world, random, boundingBox);
			this.method_14764(world, random, boundingBox);
			this.method_14766(world, random, boundingBox);
			this.method_14767(world, random, boundingBox);

			for (int j = 0; j < 7; j++) {
				int k = 0;

				while (k < 7) {
					if (k == 0 && j == 3) {
						k = 6;
					}

					int l = j * 9;
					int m = k * 9;

					for (int n = 0; n < 4; n++) {
						for (int o = 0; o < 4; o++) {
							this.addBlock(world, PRISMARINE_BRICKS, l + n, 0, m + o, boundingBox);
							this.fillDownwards(world, PRISMARINE_BRICKS, l + n, -1, m + o, boundingBox);
						}
					}

					if (j != 0 && j != 6) {
						k += 6;
					} else {
						k++;
					}
				}
			}

			for (int j = 0; j < 5; j++) {
				this.setAirAndWater(world, boundingBox, -1 - j, 0 + j * 2, -1 - j, -1 - j, 23, 58 + j);
				this.setAirAndWater(world, boundingBox, 58 + j, 0 + j * 2, -1 - j, 58 + j, 23, 58 + j);
				this.setAirAndWater(world, boundingBox, 0 - j, 0 + j * 2, -1 - j, 57 + j, 23, -1 - j);
				this.setAirAndWater(world, boundingBox, 0 - j, 0 + j * 2, 58 + j, 57 + j, 23, 58 + j);
			}

			for (OceanMonumentGenerator.Piece piece : this.field_14465) {
				if (piece.getBoundingBox().intersects(boundingBox)) {
					piece.generate(world, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, pos);
				}
			}

			return true;
		}

		private void method_14761(boolean bl, int i, StructureWorldAccess structureWorldAccess, Random random, BlockBox blockBox) {
			int j = 24;
			if (this.method_14775(blockBox, i, 0, i + 23, 20)) {
				this.fillWithOutline(structureWorldAccess, blockBox, i + 0, 0, 0, i + 24, 0, 20, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(structureWorldAccess, blockBox, i + 0, 1, 0, i + 24, 10, 20);

				for (int k = 0; k < 4; k++) {
					this.fillWithOutline(structureWorldAccess, blockBox, i + k, k + 1, k, i + k, k + 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(structureWorldAccess, blockBox, i + k + 7, k + 5, k + 7, i + k + 7, k + 5, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(structureWorldAccess, blockBox, i + 17 - k, k + 5, k + 7, i + 17 - k, k + 5, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(structureWorldAccess, blockBox, i + 24 - k, k + 1, k, i + 24 - k, k + 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(structureWorldAccess, blockBox, i + k + 1, k + 1, k, i + 23 - k, k + 1, k, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(structureWorldAccess, blockBox, i + k + 8, k + 5, k + 7, i + 16 - k, k + 5, k + 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				this.fillWithOutline(structureWorldAccess, blockBox, i + 4, 4, 4, i + 6, 4, 20, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, i + 7, 4, 4, i + 17, 4, 6, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, i + 18, 4, 4, i + 20, 4, 20, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, i + 11, 8, 11, i + 13, 8, 20, PRISMARINE, PRISMARINE, false);
				this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, i + 12, 9, 12, blockBox);
				this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, i + 12, 9, 15, blockBox);
				this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, i + 12, 9, 18, blockBox);
				int k = i + (bl ? 19 : 5);
				int l = i + (bl ? 5 : 19);

				for (int m = 20; m >= 5; m -= 3) {
					this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, k, 5, m, blockBox);
				}

				for (int m = 19; m >= 7; m -= 3) {
					this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, l, 5, m, blockBox);
				}

				for (int m = 0; m < 4; m++) {
					int n = bl ? i + 24 - (17 - m * 3) : i + 17 - m * 3;
					this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, n, 5, 5, blockBox);
				}

				this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, l, 5, 5, blockBox);
				this.fillWithOutline(structureWorldAccess, blockBox, i + 11, 1, 12, i + 13, 7, 12, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, i + 12, 1, 11, i + 12, 7, 13, PRISMARINE, PRISMARINE, false);
			}
		}

		private void method_14763(StructureWorldAccess structureWorldAccess, Random random, BlockBox blockBox) {
			if (this.method_14775(blockBox, 22, 5, 35, 17)) {
				this.setAirAndWater(structureWorldAccess, blockBox, 25, 0, 0, 32, 8, 20);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(structureWorldAccess, blockBox, 24, 2, 5 + i * 4, 24, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(structureWorldAccess, blockBox, 22, 4, 5 + i * 4, 23, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.addBlock(structureWorldAccess, PRISMARINE_BRICKS, 25, 5, 5 + i * 4, blockBox);
					this.addBlock(structureWorldAccess, PRISMARINE_BRICKS, 26, 6, 5 + i * 4, blockBox);
					this.addBlock(structureWorldAccess, SEA_LANTERN, 26, 5, 5 + i * 4, blockBox);
					this.fillWithOutline(structureWorldAccess, blockBox, 33, 2, 5 + i * 4, 33, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(structureWorldAccess, blockBox, 34, 4, 5 + i * 4, 35, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.addBlock(structureWorldAccess, PRISMARINE_BRICKS, 32, 5, 5 + i * 4, blockBox);
					this.addBlock(structureWorldAccess, PRISMARINE_BRICKS, 31, 6, 5 + i * 4, blockBox);
					this.addBlock(structureWorldAccess, SEA_LANTERN, 31, 5, 5 + i * 4, blockBox);
					this.fillWithOutline(structureWorldAccess, blockBox, 27, 6, 5 + i * 4, 30, 6, 5 + i * 4, PRISMARINE, PRISMARINE, false);
				}
			}
		}

		private void method_14762(StructureWorldAccess structureWorldAccess, Random random, BlockBox blockBox) {
			if (this.method_14775(blockBox, 15, 20, 42, 21)) {
				this.fillWithOutline(structureWorldAccess, blockBox, 15, 0, 21, 42, 0, 21, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(structureWorldAccess, blockBox, 26, 1, 21, 31, 3, 21);
				this.fillWithOutline(structureWorldAccess, blockBox, 21, 12, 21, 36, 12, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 17, 11, 21, 40, 11, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 16, 10, 21, 41, 10, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 15, 7, 21, 42, 9, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 16, 6, 21, 41, 6, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 17, 5, 21, 40, 5, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 21, 4, 21, 36, 4, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 22, 3, 21, 26, 3, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 31, 3, 21, 35, 3, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 23, 2, 21, 25, 2, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 32, 2, 21, 34, 2, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 28, 4, 20, 29, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.addBlock(structureWorldAccess, PRISMARINE_BRICKS, 27, 3, 21, blockBox);
				this.addBlock(structureWorldAccess, PRISMARINE_BRICKS, 30, 3, 21, blockBox);
				this.addBlock(structureWorldAccess, PRISMARINE_BRICKS, 26, 2, 21, blockBox);
				this.addBlock(structureWorldAccess, PRISMARINE_BRICKS, 31, 2, 21, blockBox);
				this.addBlock(structureWorldAccess, PRISMARINE_BRICKS, 25, 1, 21, blockBox);
				this.addBlock(structureWorldAccess, PRISMARINE_BRICKS, 32, 1, 21, blockBox);

				for (int i = 0; i < 7; i++) {
					this.addBlock(structureWorldAccess, DARK_PRISMARINE, 28 - i, 6 + i, 21, blockBox);
					this.addBlock(structureWorldAccess, DARK_PRISMARINE, 29 + i, 6 + i, 21, blockBox);
				}

				for (int i = 0; i < 4; i++) {
					this.addBlock(structureWorldAccess, DARK_PRISMARINE, 28 - i, 9 + i, 21, blockBox);
					this.addBlock(structureWorldAccess, DARK_PRISMARINE, 29 + i, 9 + i, 21, blockBox);
				}

				this.addBlock(structureWorldAccess, DARK_PRISMARINE, 28, 12, 21, blockBox);
				this.addBlock(structureWorldAccess, DARK_PRISMARINE, 29, 12, 21, blockBox);

				for (int i = 0; i < 3; i++) {
					this.addBlock(structureWorldAccess, DARK_PRISMARINE, 22 - i * 2, 8, 21, blockBox);
					this.addBlock(structureWorldAccess, DARK_PRISMARINE, 22 - i * 2, 9, 21, blockBox);
					this.addBlock(structureWorldAccess, DARK_PRISMARINE, 35 + i * 2, 8, 21, blockBox);
					this.addBlock(structureWorldAccess, DARK_PRISMARINE, 35 + i * 2, 9, 21, blockBox);
				}

				this.setAirAndWater(structureWorldAccess, blockBox, 15, 13, 21, 42, 15, 21);
				this.setAirAndWater(structureWorldAccess, blockBox, 15, 1, 21, 15, 6, 21);
				this.setAirAndWater(structureWorldAccess, blockBox, 16, 1, 21, 16, 5, 21);
				this.setAirAndWater(structureWorldAccess, blockBox, 17, 1, 21, 20, 4, 21);
				this.setAirAndWater(structureWorldAccess, blockBox, 21, 1, 21, 21, 3, 21);
				this.setAirAndWater(structureWorldAccess, blockBox, 22, 1, 21, 22, 2, 21);
				this.setAirAndWater(structureWorldAccess, blockBox, 23, 1, 21, 24, 1, 21);
				this.setAirAndWater(structureWorldAccess, blockBox, 42, 1, 21, 42, 6, 21);
				this.setAirAndWater(structureWorldAccess, blockBox, 41, 1, 21, 41, 5, 21);
				this.setAirAndWater(structureWorldAccess, blockBox, 37, 1, 21, 40, 4, 21);
				this.setAirAndWater(structureWorldAccess, blockBox, 36, 1, 21, 36, 3, 21);
				this.setAirAndWater(structureWorldAccess, blockBox, 33, 1, 21, 34, 1, 21);
				this.setAirAndWater(structureWorldAccess, blockBox, 35, 1, 21, 35, 2, 21);
			}
		}

		private void method_14765(StructureWorldAccess structureWorldAccess, Random random, BlockBox blockBox) {
			if (this.method_14775(blockBox, 21, 21, 36, 36)) {
				this.fillWithOutline(structureWorldAccess, blockBox, 21, 0, 22, 36, 0, 36, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(structureWorldAccess, blockBox, 21, 1, 22, 36, 23, 36);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(structureWorldAccess, blockBox, 21 + i, 13 + i, 21 + i, 36 - i, 13 + i, 21 + i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(structureWorldAccess, blockBox, 21 + i, 13 + i, 36 - i, 36 - i, 13 + i, 36 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(structureWorldAccess, blockBox, 21 + i, 13 + i, 22 + i, 21 + i, 13 + i, 35 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(structureWorldAccess, blockBox, 36 - i, 13 + i, 22 + i, 36 - i, 13 + i, 35 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				this.fillWithOutline(structureWorldAccess, blockBox, 25, 16, 25, 32, 16, 32, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 25, 17, 25, 25, 19, 25, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 32, 17, 25, 32, 19, 25, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 25, 17, 32, 25, 19, 32, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 32, 17, 32, 32, 19, 32, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.addBlock(structureWorldAccess, PRISMARINE_BRICKS, 26, 20, 26, blockBox);
				this.addBlock(structureWorldAccess, PRISMARINE_BRICKS, 27, 21, 27, blockBox);
				this.addBlock(structureWorldAccess, SEA_LANTERN, 27, 20, 27, blockBox);
				this.addBlock(structureWorldAccess, PRISMARINE_BRICKS, 26, 20, 31, blockBox);
				this.addBlock(structureWorldAccess, PRISMARINE_BRICKS, 27, 21, 30, blockBox);
				this.addBlock(structureWorldAccess, SEA_LANTERN, 27, 20, 30, blockBox);
				this.addBlock(structureWorldAccess, PRISMARINE_BRICKS, 31, 20, 31, blockBox);
				this.addBlock(structureWorldAccess, PRISMARINE_BRICKS, 30, 21, 30, blockBox);
				this.addBlock(structureWorldAccess, SEA_LANTERN, 30, 20, 30, blockBox);
				this.addBlock(structureWorldAccess, PRISMARINE_BRICKS, 31, 20, 26, blockBox);
				this.addBlock(structureWorldAccess, PRISMARINE_BRICKS, 30, 21, 27, blockBox);
				this.addBlock(structureWorldAccess, SEA_LANTERN, 30, 20, 27, blockBox);
				this.fillWithOutline(structureWorldAccess, blockBox, 28, 21, 27, 29, 21, 27, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 27, 21, 28, 27, 21, 29, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 28, 21, 30, 29, 21, 30, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 30, 21, 28, 30, 21, 29, PRISMARINE, PRISMARINE, false);
			}
		}

		private void method_14764(StructureWorldAccess structureWorldAccess, Random random, BlockBox blockBox) {
			if (this.method_14775(blockBox, 0, 21, 6, 58)) {
				this.fillWithOutline(structureWorldAccess, blockBox, 0, 0, 21, 6, 0, 57, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(structureWorldAccess, blockBox, 0, 1, 21, 6, 7, 57);
				this.fillWithOutline(structureWorldAccess, blockBox, 4, 4, 21, 6, 4, 53, PRISMARINE, PRISMARINE, false);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(structureWorldAccess, blockBox, i, i + 1, 21, i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int i = 23; i < 53; i += 3) {
					this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, 5, 5, i, blockBox);
				}

				this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, 5, 5, 52, blockBox);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(structureWorldAccess, blockBox, i, i + 1, 21, i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				this.fillWithOutline(structureWorldAccess, blockBox, 4, 1, 52, 6, 3, 52, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 5, 1, 51, 5, 3, 53, PRISMARINE, PRISMARINE, false);
			}

			if (this.method_14775(blockBox, 51, 21, 58, 58)) {
				this.fillWithOutline(structureWorldAccess, blockBox, 51, 0, 21, 57, 0, 57, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(structureWorldAccess, blockBox, 51, 1, 21, 57, 7, 57);
				this.fillWithOutline(structureWorldAccess, blockBox, 51, 4, 21, 53, 4, 53, PRISMARINE, PRISMARINE, false);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(structureWorldAccess, blockBox, 57 - i, i + 1, 21, 57 - i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int i = 23; i < 53; i += 3) {
					this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, 52, 5, i, blockBox);
				}

				this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, 52, 5, 52, blockBox);
				this.fillWithOutline(structureWorldAccess, blockBox, 51, 1, 52, 53, 3, 52, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 52, 1, 51, 52, 3, 53, PRISMARINE, PRISMARINE, false);
			}

			if (this.method_14775(blockBox, 0, 51, 57, 57)) {
				this.fillWithOutline(structureWorldAccess, blockBox, 7, 0, 51, 50, 0, 57, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(structureWorldAccess, blockBox, 7, 1, 51, 50, 10, 57);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(structureWorldAccess, blockBox, i + 1, i + 1, 57 - i, 56 - i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}
			}
		}

		private void method_14766(StructureWorldAccess structureWorldAccess, Random random, BlockBox blockBox) {
			if (this.method_14775(blockBox, 7, 21, 13, 50)) {
				this.fillWithOutline(structureWorldAccess, blockBox, 7, 0, 21, 13, 0, 50, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(structureWorldAccess, blockBox, 7, 1, 21, 13, 10, 50);
				this.fillWithOutline(structureWorldAccess, blockBox, 11, 8, 21, 13, 8, 53, PRISMARINE, PRISMARINE, false);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(structureWorldAccess, blockBox, i + 7, i + 5, 21, i + 7, i + 5, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int i = 21; i <= 45; i += 3) {
					this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, 12, 9, i, blockBox);
				}
			}

			if (this.method_14775(blockBox, 44, 21, 50, 54)) {
				this.fillWithOutline(structureWorldAccess, blockBox, 44, 0, 21, 50, 0, 50, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(structureWorldAccess, blockBox, 44, 1, 21, 50, 10, 50);
				this.fillWithOutline(structureWorldAccess, blockBox, 44, 8, 21, 46, 8, 53, PRISMARINE, PRISMARINE, false);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(structureWorldAccess, blockBox, 50 - i, i + 5, 21, 50 - i, i + 5, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int i = 21; i <= 45; i += 3) {
					this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, 45, 9, i, blockBox);
				}
			}

			if (this.method_14775(blockBox, 8, 44, 49, 54)) {
				this.fillWithOutline(structureWorldAccess, blockBox, 14, 0, 44, 43, 0, 50, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(structureWorldAccess, blockBox, 14, 1, 44, 43, 10, 50);

				for (int i = 12; i <= 45; i += 3) {
					this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, i, 9, 45, blockBox);
					this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, i, 9, 52, blockBox);
					if (i == 12 || i == 18 || i == 24 || i == 33 || i == 39 || i == 45) {
						this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, i, 9, 47, blockBox);
						this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, i, 9, 50, blockBox);
						this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, i, 10, 45, blockBox);
						this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, i, 10, 46, blockBox);
						this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, i, 10, 51, blockBox);
						this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, i, 10, 52, blockBox);
						this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, i, 11, 47, blockBox);
						this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, i, 11, 50, blockBox);
						this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, i, 12, 48, blockBox);
						this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, i, 12, 49, blockBox);
					}
				}

				for (int ix = 0; ix < 3; ix++) {
					this.fillWithOutline(structureWorldAccess, blockBox, 8 + ix, 5 + ix, 54, 49 - ix, 5 + ix, 54, PRISMARINE, PRISMARINE, false);
				}

				this.fillWithOutline(structureWorldAccess, blockBox, 11, 8, 54, 46, 8, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 14, 8, 44, 43, 8, 53, PRISMARINE, PRISMARINE, false);
			}
		}

		private void method_14767(StructureWorldAccess structureWorldAccess, Random random, BlockBox blockBox) {
			if (this.method_14775(blockBox, 14, 21, 20, 43)) {
				this.fillWithOutline(structureWorldAccess, blockBox, 14, 0, 21, 20, 0, 43, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(structureWorldAccess, blockBox, 14, 1, 22, 20, 14, 43);
				this.fillWithOutline(structureWorldAccess, blockBox, 18, 12, 22, 20, 12, 39, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 18, 12, 21, 20, 12, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(structureWorldAccess, blockBox, i + 14, i + 9, 21, i + 14, i + 9, 43 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int i = 23; i <= 39; i += 3) {
					this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, 19, 13, i, blockBox);
				}
			}

			if (this.method_14775(blockBox, 37, 21, 43, 43)) {
				this.fillWithOutline(structureWorldAccess, blockBox, 37, 0, 21, 43, 0, 43, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(structureWorldAccess, blockBox, 37, 1, 22, 43, 14, 43);
				this.fillWithOutline(structureWorldAccess, blockBox, 37, 12, 22, 39, 12, 39, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, 37, 12, 21, 39, 12, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(structureWorldAccess, blockBox, 43 - i, i + 9, 21, 43 - i, i + 9, 43 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int i = 23; i <= 39; i += 3) {
					this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, 38, 13, i, blockBox);
				}
			}

			if (this.method_14775(blockBox, 15, 37, 42, 43)) {
				this.fillWithOutline(structureWorldAccess, blockBox, 21, 0, 37, 36, 0, 43, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(structureWorldAccess, blockBox, 21, 1, 37, 36, 14, 43);
				this.fillWithOutline(structureWorldAccess, blockBox, 21, 12, 37, 36, 12, 39, PRISMARINE, PRISMARINE, false);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(structureWorldAccess, blockBox, 15 + i, i + 9, 43 - i, 42 - i, i + 9, 43 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int i = 21; i <= 36; i += 3) {
					this.addBlock(structureWorldAccess, ALSO_PRISMARINE_BRICKS, i, 13, 38, blockBox);
				}
			}
		}
	}

	public static class CoreRoom extends OceanMonumentGenerator.Piece {
		public CoreRoom(Direction orientation, OceanMonumentGenerator.PieceSetting setting) {
			super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, 1, orientation, setting, 2, 2, 2);
		}

		public CoreRoom(ServerWorld serverWorld, NbtCompound nbt) {
			super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, nbt);
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			this.method_14771(world, boundingBox, 1, 8, 0, 14, 8, 14, PRISMARINE);
			int i = 7;
			BlockState blockState = PRISMARINE_BRICKS;
			this.fillWithOutline(world, boundingBox, 0, 7, 0, 0, 7, 15, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 15, 7, 0, 15, 7, 15, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 1, 7, 0, 15, 7, 0, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 1, 7, 15, 14, 7, 15, blockState, blockState, false);

			for (int ix = 1; ix <= 6; ix++) {
				blockState = PRISMARINE_BRICKS;
				if (ix == 2 || ix == 6) {
					blockState = PRISMARINE;
				}

				for (int j = 0; j <= 15; j += 15) {
					this.fillWithOutline(world, boundingBox, j, ix, 0, j, ix, 1, blockState, blockState, false);
					this.fillWithOutline(world, boundingBox, j, ix, 6, j, ix, 9, blockState, blockState, false);
					this.fillWithOutline(world, boundingBox, j, ix, 14, j, ix, 15, blockState, blockState, false);
				}

				this.fillWithOutline(world, boundingBox, 1, ix, 0, 1, ix, 0, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 6, ix, 0, 9, ix, 0, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 14, ix, 0, 14, ix, 0, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 1, ix, 15, 14, ix, 15, blockState, blockState, false);
			}

			this.fillWithOutline(world, boundingBox, 6, 3, 6, 9, 6, 9, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 7, 4, 7, 8, 5, 8, Blocks.GOLD_BLOCK.getDefaultState(), Blocks.GOLD_BLOCK.getDefaultState(), false);

			for (int ix = 3; ix <= 6; ix += 3) {
				for (int k = 6; k <= 9; k += 3) {
					this.addBlock(world, SEA_LANTERN, k, ix, 6, boundingBox);
					this.addBlock(world, SEA_LANTERN, k, ix, 9, boundingBox);
				}
			}

			this.fillWithOutline(world, boundingBox, 5, 1, 6, 5, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 1, 9, 5, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 10, 1, 6, 10, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 10, 1, 9, 10, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 1, 5, 6, 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 9, 1, 5, 9, 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 1, 10, 6, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 9, 1, 10, 9, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 2, 5, 5, 6, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 2, 10, 5, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 10, 2, 5, 10, 6, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 10, 2, 10, 10, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 7, 1, 5, 7, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 10, 7, 1, 10, 7, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 7, 9, 5, 7, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 10, 7, 9, 10, 7, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 7, 5, 6, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 7, 10, 6, 7, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 9, 7, 5, 14, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 9, 7, 10, 14, 7, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 2, 1, 2, 2, 1, 3, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 3, 1, 2, 3, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 13, 1, 2, 13, 1, 3, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 12, 1, 2, 12, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 2, 1, 12, 2, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 3, 1, 13, 3, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 13, 1, 12, 13, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 12, 1, 13, 12, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			return true;
		}
	}

	public static class DoubleXRoom extends OceanMonumentGenerator.Piece {
		public DoubleXRoom(Direction orientation, OceanMonumentGenerator.PieceSetting setting) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, 1, orientation, setting, 2, 1, 1);
		}

		public DoubleXRoom(ServerWorld serverWorld, NbtCompound nbt) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, nbt);
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			OceanMonumentGenerator.PieceSetting pieceSetting = this.setting.neighbors[Direction.EAST.getId()];
			OceanMonumentGenerator.PieceSetting pieceSetting2 = this.setting;
			if (this.setting.roomIndex / 25 > 0) {
				this.method_14774(world, boundingBox, 8, 0, pieceSetting.neighborPresences[Direction.DOWN.getId()]);
				this.method_14774(world, boundingBox, 0, 0, pieceSetting2.neighborPresences[Direction.DOWN.getId()]);
			}

			if (pieceSetting2.neighbors[Direction.UP.getId()] == null) {
				this.method_14771(world, boundingBox, 1, 4, 1, 7, 4, 6, PRISMARINE);
			}

			if (pieceSetting.neighbors[Direction.UP.getId()] == null) {
				this.method_14771(world, boundingBox, 8, 4, 1, 14, 4, 6, PRISMARINE);
			}

			this.fillWithOutline(world, boundingBox, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 15, 3, 0, 15, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 3, 0, 15, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 3, 7, 14, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 2, 7, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 15, 2, 0, 15, 2, 7, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 1, 2, 0, 15, 2, 0, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 1, 2, 7, 14, 2, 7, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 15, 1, 0, 15, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 1, 0, 15, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 1, 7, 14, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 1, 0, 10, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 2, 0, 9, 2, 3, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 5, 3, 0, 10, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.addBlock(world, SEA_LANTERN, 6, 2, 3, boundingBox);
			this.addBlock(world, SEA_LANTERN, 9, 2, 3, boundingBox);
			if (pieceSetting2.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 0, 4, 2, 0);
			}

			if (pieceSetting2.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 7, 4, 2, 7);
			}

			if (pieceSetting2.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 1, 3, 0, 2, 4);
			}

			if (pieceSetting.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 11, 1, 0, 12, 2, 0);
			}

			if (pieceSetting.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 11, 1, 7, 12, 2, 7);
			}

			if (pieceSetting.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 15, 1, 3, 15, 2, 4);
			}

			return true;
		}
	}

	static class DoubleXRoomFactory implements OceanMonumentGenerator.PieceFactory {
		private DoubleXRoomFactory() {
		}

		@Override
		public boolean canGenerate(OceanMonumentGenerator.PieceSetting setting) {
			return setting.neighborPresences[Direction.EAST.getId()] && !setting.neighbors[Direction.EAST.getId()].used;
		}

		@Override
		public OceanMonumentGenerator.Piece generate(Direction direction, OceanMonumentGenerator.PieceSetting setting, Random random) {
			setting.used = true;
			setting.neighbors[Direction.EAST.getId()].used = true;
			return new OceanMonumentGenerator.DoubleXRoom(direction, setting);
		}
	}

	public static class DoubleXYRoom extends OceanMonumentGenerator.Piece {
		public DoubleXYRoom(Direction orientation, OceanMonumentGenerator.PieceSetting setting) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_Y_ROOM, 1, orientation, setting, 2, 2, 1);
		}

		public DoubleXYRoom(ServerWorld serverWorld, NbtCompound nbt) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_Y_ROOM, nbt);
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			OceanMonumentGenerator.PieceSetting pieceSetting = this.setting.neighbors[Direction.EAST.getId()];
			OceanMonumentGenerator.PieceSetting pieceSetting2 = this.setting;
			OceanMonumentGenerator.PieceSetting pieceSetting3 = pieceSetting2.neighbors[Direction.UP.getId()];
			OceanMonumentGenerator.PieceSetting pieceSetting4 = pieceSetting.neighbors[Direction.UP.getId()];
			if (this.setting.roomIndex / 25 > 0) {
				this.method_14774(world, boundingBox, 8, 0, pieceSetting.neighborPresences[Direction.DOWN.getId()]);
				this.method_14774(world, boundingBox, 0, 0, pieceSetting2.neighborPresences[Direction.DOWN.getId()]);
			}

			if (pieceSetting3.neighbors[Direction.UP.getId()] == null) {
				this.method_14771(world, boundingBox, 1, 8, 1, 7, 8, 6, PRISMARINE);
			}

			if (pieceSetting4.neighbors[Direction.UP.getId()] == null) {
				this.method_14771(world, boundingBox, 8, 8, 1, 14, 8, 6, PRISMARINE);
			}

			for (int i = 1; i <= 7; i++) {
				BlockState blockState = PRISMARINE_BRICKS;
				if (i == 2 || i == 6) {
					blockState = PRISMARINE;
				}

				this.fillWithOutline(world, boundingBox, 0, i, 0, 0, i, 7, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 15, i, 0, 15, i, 7, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 1, i, 0, 15, i, 0, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 1, i, 7, 14, i, 7, blockState, blockState, false);
			}

			this.fillWithOutline(world, boundingBox, 2, 1, 3, 2, 7, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 3, 1, 2, 4, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 3, 1, 5, 4, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 13, 1, 3, 13, 7, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 11, 1, 2, 12, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 11, 1, 5, 12, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 1, 3, 5, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 10, 1, 3, 10, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 7, 2, 10, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 5, 2, 5, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 10, 5, 2, 10, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 5, 5, 5, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 10, 5, 5, 10, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.addBlock(world, PRISMARINE_BRICKS, 6, 6, 2, boundingBox);
			this.addBlock(world, PRISMARINE_BRICKS, 9, 6, 2, boundingBox);
			this.addBlock(world, PRISMARINE_BRICKS, 6, 6, 5, boundingBox);
			this.addBlock(world, PRISMARINE_BRICKS, 9, 6, 5, boundingBox);
			this.fillWithOutline(world, boundingBox, 5, 4, 3, 6, 4, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 9, 4, 3, 10, 4, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.addBlock(world, SEA_LANTERN, 5, 4, 2, boundingBox);
			this.addBlock(world, SEA_LANTERN, 5, 4, 5, boundingBox);
			this.addBlock(world, SEA_LANTERN, 10, 4, 2, boundingBox);
			this.addBlock(world, SEA_LANTERN, 10, 4, 5, boundingBox);
			if (pieceSetting2.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 0, 4, 2, 0);
			}

			if (pieceSetting2.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 7, 4, 2, 7);
			}

			if (pieceSetting2.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 1, 3, 0, 2, 4);
			}

			if (pieceSetting.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 11, 1, 0, 12, 2, 0);
			}

			if (pieceSetting.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 11, 1, 7, 12, 2, 7);
			}

			if (pieceSetting.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 15, 1, 3, 15, 2, 4);
			}

			if (pieceSetting3.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 5, 0, 4, 6, 0);
			}

			if (pieceSetting3.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 5, 7, 4, 6, 7);
			}

			if (pieceSetting3.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 5, 3, 0, 6, 4);
			}

			if (pieceSetting4.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 11, 5, 0, 12, 6, 0);
			}

			if (pieceSetting4.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 11, 5, 7, 12, 6, 7);
			}

			if (pieceSetting4.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 15, 5, 3, 15, 6, 4);
			}

			return true;
		}
	}

	static class DoubleXYRoomFactory implements OceanMonumentGenerator.PieceFactory {
		private DoubleXYRoomFactory() {
		}

		@Override
		public boolean canGenerate(OceanMonumentGenerator.PieceSetting setting) {
			if (setting.neighborPresences[Direction.EAST.getId()]
				&& !setting.neighbors[Direction.EAST.getId()].used
				&& setting.neighborPresences[Direction.UP.getId()]
				&& !setting.neighbors[Direction.UP.getId()].used) {
				OceanMonumentGenerator.PieceSetting pieceSetting = setting.neighbors[Direction.EAST.getId()];
				return pieceSetting.neighborPresences[Direction.UP.getId()] && !pieceSetting.neighbors[Direction.UP.getId()].used;
			} else {
				return false;
			}
		}

		@Override
		public OceanMonumentGenerator.Piece generate(Direction direction, OceanMonumentGenerator.PieceSetting setting, Random random) {
			setting.used = true;
			setting.neighbors[Direction.EAST.getId()].used = true;
			setting.neighbors[Direction.UP.getId()].used = true;
			setting.neighbors[Direction.EAST.getId()].neighbors[Direction.UP.getId()].used = true;
			return new OceanMonumentGenerator.DoubleXYRoom(direction, setting);
		}
	}

	public static class DoubleYRoom extends OceanMonumentGenerator.Piece {
		public DoubleYRoom(Direction direction, OceanMonumentGenerator.PieceSetting pieceSetting) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, 1, direction, pieceSetting, 1, 2, 1);
		}

		public DoubleYRoom(ServerWorld serverWorld, NbtCompound nbtCompound) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, nbtCompound);
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			if (this.setting.roomIndex / 25 > 0) {
				this.method_14774(world, boundingBox, 0, 0, this.setting.neighborPresences[Direction.DOWN.getId()]);
			}

			OceanMonumentGenerator.PieceSetting pieceSetting = this.setting.neighbors[Direction.UP.getId()];
			if (pieceSetting.neighbors[Direction.UP.getId()] == null) {
				this.method_14771(world, boundingBox, 1, 8, 1, 6, 8, 6, PRISMARINE);
			}

			this.fillWithOutline(world, boundingBox, 0, 4, 0, 0, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 7, 4, 0, 7, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 4, 0, 6, 4, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 4, 7, 6, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 2, 4, 1, 2, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 4, 2, 1, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 4, 1, 5, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 4, 2, 6, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 2, 4, 5, 2, 4, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 4, 5, 1, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 4, 5, 5, 4, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 4, 5, 6, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			OceanMonumentGenerator.PieceSetting pieceSetting2 = this.setting;

			for (int i = 1; i <= 5; i += 4) {
				int j = 0;
				if (pieceSetting2.neighborPresences[Direction.SOUTH.getId()]) {
					this.fillWithOutline(world, boundingBox, 2, i, j, 2, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 5, i, j, 5, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 3, i + 2, j, 4, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(world, boundingBox, 0, i, j, 7, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 0, i + 1, j, 7, i + 1, j, PRISMARINE, PRISMARINE, false);
				}

				int var13 = 7;
				if (pieceSetting2.neighborPresences[Direction.NORTH.getId()]) {
					this.fillWithOutline(world, boundingBox, 2, i, var13, 2, i + 2, var13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 5, i, var13, 5, i + 2, var13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 3, i + 2, var13, 4, i + 2, var13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(world, boundingBox, 0, i, var13, 7, i + 2, var13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 0, i + 1, var13, 7, i + 1, var13, PRISMARINE, PRISMARINE, false);
				}

				int k = 0;
				if (pieceSetting2.neighborPresences[Direction.WEST.getId()]) {
					this.fillWithOutline(world, boundingBox, k, i, 2, k, i + 2, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, k, i, 5, k, i + 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, k, i + 2, 3, k, i + 2, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(world, boundingBox, k, i, 0, k, i + 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, k, i + 1, 0, k, i + 1, 7, PRISMARINE, PRISMARINE, false);
				}

				int var14 = 7;
				if (pieceSetting2.neighborPresences[Direction.EAST.getId()]) {
					this.fillWithOutline(world, boundingBox, var14, i, 2, var14, i + 2, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, var14, i, 5, var14, i + 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, var14, i + 2, 3, var14, i + 2, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(world, boundingBox, var14, i, 0, var14, i + 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, var14, i + 1, 0, var14, i + 1, 7, PRISMARINE, PRISMARINE, false);
				}

				pieceSetting2 = pieceSetting;
			}

			return true;
		}
	}

	static class DoubleYRoomFactory implements OceanMonumentGenerator.PieceFactory {
		private DoubleYRoomFactory() {
		}

		@Override
		public boolean canGenerate(OceanMonumentGenerator.PieceSetting setting) {
			return setting.neighborPresences[Direction.UP.getId()] && !setting.neighbors[Direction.UP.getId()].used;
		}

		@Override
		public OceanMonumentGenerator.Piece generate(Direction direction, OceanMonumentGenerator.PieceSetting setting, Random random) {
			setting.used = true;
			setting.neighbors[Direction.UP.getId()].used = true;
			return new OceanMonumentGenerator.DoubleYRoom(direction, setting);
		}
	}

	public static class DoubleYZRoom extends OceanMonumentGenerator.Piece {
		public DoubleYZRoom(Direction orientation, OceanMonumentGenerator.PieceSetting setting) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_Z_ROOM, 1, orientation, setting, 1, 2, 2);
		}

		public DoubleYZRoom(ServerWorld serverWorld, NbtCompound nbt) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_Z_ROOM, nbt);
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			OceanMonumentGenerator.PieceSetting pieceSetting = this.setting.neighbors[Direction.NORTH.getId()];
			OceanMonumentGenerator.PieceSetting pieceSetting2 = this.setting;
			OceanMonumentGenerator.PieceSetting pieceSetting3 = pieceSetting.neighbors[Direction.UP.getId()];
			OceanMonumentGenerator.PieceSetting pieceSetting4 = pieceSetting2.neighbors[Direction.UP.getId()];
			if (this.setting.roomIndex / 25 > 0) {
				this.method_14774(world, boundingBox, 0, 8, pieceSetting.neighborPresences[Direction.DOWN.getId()]);
				this.method_14774(world, boundingBox, 0, 0, pieceSetting2.neighborPresences[Direction.DOWN.getId()]);
			}

			if (pieceSetting4.neighbors[Direction.UP.getId()] == null) {
				this.method_14771(world, boundingBox, 1, 8, 1, 6, 8, 7, PRISMARINE);
			}

			if (pieceSetting3.neighbors[Direction.UP.getId()] == null) {
				this.method_14771(world, boundingBox, 1, 8, 8, 6, 8, 14, PRISMARINE);
			}

			for (int i = 1; i <= 7; i++) {
				BlockState blockState = PRISMARINE_BRICKS;
				if (i == 2 || i == 6) {
					blockState = PRISMARINE;
				}

				this.fillWithOutline(world, boundingBox, 0, i, 0, 0, i, 15, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 7, i, 0, 7, i, 15, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 1, i, 0, 6, i, 0, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 1, i, 15, 6, i, 15, blockState, blockState, false);
			}

			for (int i = 1; i <= 7; i++) {
				BlockState blockState = DARK_PRISMARINE;
				if (i == 2 || i == 6) {
					blockState = SEA_LANTERN;
				}

				this.fillWithOutline(world, boundingBox, 3, i, 7, 4, i, 8, blockState, blockState, false);
			}

			if (pieceSetting2.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 0, 4, 2, 0);
			}

			if (pieceSetting2.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 7, 1, 3, 7, 2, 4);
			}

			if (pieceSetting2.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 1, 3, 0, 2, 4);
			}

			if (pieceSetting.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 15, 4, 2, 15);
			}

			if (pieceSetting.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 1, 11, 0, 2, 12);
			}

			if (pieceSetting.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 7, 1, 11, 7, 2, 12);
			}

			if (pieceSetting4.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 5, 0, 4, 6, 0);
			}

			if (pieceSetting4.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 7, 5, 3, 7, 6, 4);
				this.fillWithOutline(world, boundingBox, 5, 4, 2, 6, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 6, 1, 2, 6, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 6, 1, 5, 6, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			}

			if (pieceSetting4.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 5, 3, 0, 6, 4);
				this.fillWithOutline(world, boundingBox, 1, 4, 2, 2, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 1, 1, 2, 1, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 1, 1, 5, 1, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			}

			if (pieceSetting3.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 5, 15, 4, 6, 15);
			}

			if (pieceSetting3.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 5, 11, 0, 6, 12);
				this.fillWithOutline(world, boundingBox, 1, 4, 10, 2, 4, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 1, 1, 10, 1, 3, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 1, 1, 13, 1, 3, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			}

			if (pieceSetting3.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 7, 5, 11, 7, 6, 12);
				this.fillWithOutline(world, boundingBox, 5, 4, 10, 6, 4, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 6, 1, 10, 6, 3, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 6, 1, 13, 6, 3, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			}

			return true;
		}
	}

	static class DoubleYZRoomFactory implements OceanMonumentGenerator.PieceFactory {
		private DoubleYZRoomFactory() {
		}

		@Override
		public boolean canGenerate(OceanMonumentGenerator.PieceSetting setting) {
			if (setting.neighborPresences[Direction.NORTH.getId()]
				&& !setting.neighbors[Direction.NORTH.getId()].used
				&& setting.neighborPresences[Direction.UP.getId()]
				&& !setting.neighbors[Direction.UP.getId()].used) {
				OceanMonumentGenerator.PieceSetting pieceSetting = setting.neighbors[Direction.NORTH.getId()];
				return pieceSetting.neighborPresences[Direction.UP.getId()] && !pieceSetting.neighbors[Direction.UP.getId()].used;
			} else {
				return false;
			}
		}

		@Override
		public OceanMonumentGenerator.Piece generate(Direction direction, OceanMonumentGenerator.PieceSetting setting, Random random) {
			setting.used = true;
			setting.neighbors[Direction.NORTH.getId()].used = true;
			setting.neighbors[Direction.UP.getId()].used = true;
			setting.neighbors[Direction.NORTH.getId()].neighbors[Direction.UP.getId()].used = true;
			return new OceanMonumentGenerator.DoubleYZRoom(direction, setting);
		}
	}

	public static class DoubleZRoom extends OceanMonumentGenerator.Piece {
		public DoubleZRoom(Direction orientation, OceanMonumentGenerator.PieceSetting setting) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, 1, orientation, setting, 1, 1, 2);
		}

		public DoubleZRoom(ServerWorld serverWorld, NbtCompound nbt) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, nbt);
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			OceanMonumentGenerator.PieceSetting pieceSetting = this.setting.neighbors[Direction.NORTH.getId()];
			OceanMonumentGenerator.PieceSetting pieceSetting2 = this.setting;
			if (this.setting.roomIndex / 25 > 0) {
				this.method_14774(world, boundingBox, 0, 8, pieceSetting.neighborPresences[Direction.DOWN.getId()]);
				this.method_14774(world, boundingBox, 0, 0, pieceSetting2.neighborPresences[Direction.DOWN.getId()]);
			}

			if (pieceSetting2.neighbors[Direction.UP.getId()] == null) {
				this.method_14771(world, boundingBox, 1, 4, 1, 6, 4, 7, PRISMARINE);
			}

			if (pieceSetting.neighbors[Direction.UP.getId()] == null) {
				this.method_14771(world, boundingBox, 1, 4, 8, 6, 4, 14, PRISMARINE);
			}

			this.fillWithOutline(world, boundingBox, 0, 3, 0, 0, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 7, 3, 0, 7, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 3, 0, 7, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 3, 15, 6, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 2, 15, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 7, 2, 0, 7, 2, 15, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 1, 2, 0, 7, 2, 0, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 1, 2, 15, 6, 2, 15, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 0, 1, 0, 0, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 7, 1, 0, 7, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 1, 0, 7, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 1, 15, 6, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 1, 1, 1, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 1, 1, 6, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 3, 1, 1, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 3, 1, 6, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 1, 13, 1, 1, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 1, 13, 6, 1, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 3, 13, 1, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 3, 13, 6, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 2, 1, 6, 2, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 1, 6, 5, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 2, 1, 9, 2, 3, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 1, 9, 5, 3, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 3, 2, 6, 4, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 3, 2, 9, 4, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 2, 2, 7, 2, 2, 8, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 2, 7, 5, 2, 8, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.addBlock(world, SEA_LANTERN, 2, 2, 5, boundingBox);
			this.addBlock(world, SEA_LANTERN, 5, 2, 5, boundingBox);
			this.addBlock(world, SEA_LANTERN, 2, 2, 10, boundingBox);
			this.addBlock(world, SEA_LANTERN, 5, 2, 10, boundingBox);
			this.addBlock(world, PRISMARINE_BRICKS, 2, 3, 5, boundingBox);
			this.addBlock(world, PRISMARINE_BRICKS, 5, 3, 5, boundingBox);
			this.addBlock(world, PRISMARINE_BRICKS, 2, 3, 10, boundingBox);
			this.addBlock(world, PRISMARINE_BRICKS, 5, 3, 10, boundingBox);
			if (pieceSetting2.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 0, 4, 2, 0);
			}

			if (pieceSetting2.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 7, 1, 3, 7, 2, 4);
			}

			if (pieceSetting2.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 1, 3, 0, 2, 4);
			}

			if (pieceSetting.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 15, 4, 2, 15);
			}

			if (pieceSetting.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 1, 11, 0, 2, 12);
			}

			if (pieceSetting.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 7, 1, 11, 7, 2, 12);
			}

			return true;
		}
	}

	static class DoubleZRoomFactory implements OceanMonumentGenerator.PieceFactory {
		private DoubleZRoomFactory() {
		}

		@Override
		public boolean canGenerate(OceanMonumentGenerator.PieceSetting setting) {
			return setting.neighborPresences[Direction.NORTH.getId()] && !setting.neighbors[Direction.NORTH.getId()].used;
		}

		@Override
		public OceanMonumentGenerator.Piece generate(Direction direction, OceanMonumentGenerator.PieceSetting setting, Random random) {
			OceanMonumentGenerator.PieceSetting pieceSetting = setting;
			if (!setting.neighborPresences[Direction.NORTH.getId()] || setting.neighbors[Direction.NORTH.getId()].used) {
				pieceSetting = setting.neighbors[Direction.SOUTH.getId()];
			}

			pieceSetting.used = true;
			pieceSetting.neighbors[Direction.NORTH.getId()].used = true;
			return new OceanMonumentGenerator.DoubleZRoom(direction, pieceSetting);
		}
	}

	public static class Entry extends OceanMonumentGenerator.Piece {
		public Entry(Direction orientation, OceanMonumentGenerator.PieceSetting setting) {
			super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, 1, orientation, setting, 1, 1, 1);
		}

		public Entry(ServerWorld serverWorld, NbtCompound nbt) {
			super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, nbt);
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			this.fillWithOutline(world, boundingBox, 0, 3, 0, 2, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 1, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 2, 0, 7, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 0, 1, 7, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 1, 0, 2, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 1, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			if (this.setting.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 7, 4, 2, 7);
			}

			if (this.setting.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 1, 3, 1, 2, 4);
			}

			if (this.setting.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 6, 1, 3, 7, 2, 4);
			}

			return true;
		}
	}

	public static class Penthouse extends OceanMonumentGenerator.Piece {
		public Penthouse(Direction orientation, BlockBox box) {
			super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, orientation, 1, box);
		}

		public Penthouse(ServerWorld serverWorld, NbtCompound nbt) {
			super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, nbt);
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			this.fillWithOutline(world, boundingBox, 2, -1, 2, 11, -1, 11, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 0, -1, 0, 1, -1, 11, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 12, -1, 0, 13, -1, 11, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 2, -1, 0, 11, -1, 1, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 2, -1, 12, 11, -1, 13, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 0, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 13, 0, 0, 13, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 0, 0, 12, 0, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 0, 13, 12, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);

			for (int i = 2; i <= 11; i += 3) {
				this.addBlock(world, SEA_LANTERN, 0, 0, i, boundingBox);
				this.addBlock(world, SEA_LANTERN, 13, 0, i, boundingBox);
				this.addBlock(world, SEA_LANTERN, i, 0, 0, boundingBox);
			}

			this.fillWithOutline(world, boundingBox, 2, 0, 3, 4, 0, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 9, 0, 3, 11, 0, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 4, 0, 9, 9, 0, 11, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.addBlock(world, PRISMARINE_BRICKS, 5, 0, 8, boundingBox);
			this.addBlock(world, PRISMARINE_BRICKS, 8, 0, 8, boundingBox);
			this.addBlock(world, PRISMARINE_BRICKS, 10, 0, 10, boundingBox);
			this.addBlock(world, PRISMARINE_BRICKS, 3, 0, 10, boundingBox);
			this.fillWithOutline(world, boundingBox, 3, 0, 3, 3, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 10, 0, 3, 10, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 6, 0, 10, 7, 0, 10, DARK_PRISMARINE, DARK_PRISMARINE, false);
			int i = 3;

			for (int j = 0; j < 2; j++) {
				for (int k = 2; k <= 8; k += 3) {
					this.fillWithOutline(world, boundingBox, i, 0, k, i, 2, k, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				i = 10;
			}

			this.fillWithOutline(world, boundingBox, 5, 0, 10, 5, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 8, 0, 10, 8, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, -1, 7, 7, -1, 8, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.setAirAndWater(world, boundingBox, 6, -1, 3, 7, -1, 4);
			this.spawnElderGuardian(world, boundingBox, 6, 1, 6);
			return true;
		}
	}

	public abstract static class Piece extends StructurePiece {
		protected static final BlockState PRISMARINE = Blocks.PRISMARINE.getDefaultState();
		protected static final BlockState PRISMARINE_BRICKS = Blocks.PRISMARINE_BRICKS.getDefaultState();
		protected static final BlockState DARK_PRISMARINE = Blocks.DARK_PRISMARINE.getDefaultState();
		protected static final BlockState ALSO_PRISMARINE_BRICKS = PRISMARINE_BRICKS;
		protected static final BlockState SEA_LANTERN = Blocks.SEA_LANTERN.getDefaultState();
		protected static final boolean field_31607 = true;
		protected static final BlockState WATER = Blocks.WATER.getDefaultState();
		protected static final Set<Block> ICE_BLOCKS = ImmutableSet.<Block>builder()
			.add(Blocks.ICE)
			.add(Blocks.PACKED_ICE)
			.add(Blocks.BLUE_ICE)
			.add(WATER.getBlock())
			.build();
		protected static final int field_31608 = 8;
		protected static final int field_31609 = 8;
		protected static final int field_31610 = 4;
		protected static final int field_31611 = 5;
		protected static final int field_31612 = 5;
		protected static final int field_31613 = 3;
		protected static final int field_31614 = 25;
		protected static final int field_31615 = 75;
		protected static final int TWO_ZERO_ZERO_INDEX = getIndex(2, 0, 0);
		protected static final int TWO_TWO_ZERO_INDEX = getIndex(2, 2, 0);
		protected static final int ZERO_ONE_ZERO_INDEX = getIndex(0, 1, 0);
		protected static final int FOUR_ONE_ZERO_INDEX = getIndex(4, 1, 0);
		protected static final int field_31616 = 1001;
		protected static final int field_31617 = 1002;
		protected static final int field_31618 = 1003;
		protected OceanMonumentGenerator.PieceSetting setting;

		protected static int getIndex(int x, int y, int z) {
			return y * 25 + z * 5 + x;
		}

		public Piece(StructurePieceType type, Direction orientation, int i, BlockBox blockBox) {
			super(type, i, blockBox);
			this.setOrientation(orientation);
		}

		protected Piece(StructurePieceType type, int length, Direction orientation, OceanMonumentGenerator.PieceSetting setting, int i, int j, int k) {
			super(type, length, method_35445(orientation, setting, i, j, k));
			this.setOrientation(orientation);
			this.setting = setting;
		}

		private static BlockBox method_35445(Direction direction, OceanMonumentGenerator.PieceSetting pieceSetting, int i, int j, int k) {
			int l = pieceSetting.roomIndex;
			int m = l % 5;
			int n = l / 5 % 5;
			int o = l / 25;
			BlockBox blockBox = method_35454(0, 0, 0, direction, i * 8, j * 4, k * 8);
			switch (direction) {
				case NORTH:
					blockBox.move(m * 8, o * 4, -(n + k) * 8 + 1);
					break;
				case SOUTH:
					blockBox.move(m * 8, o * 4, n * 8);
					break;
				case WEST:
					blockBox.move(-(n + k) * 8 + 1, o * 4, m * 8);
					break;
				case EAST:
				default:
					blockBox.move(n * 8, o * 4, m * 8);
			}

			return blockBox;
		}

		public Piece(StructurePieceType structurePieceType, NbtCompound nbtCompound) {
			super(structurePieceType, nbtCompound);
		}

		@Override
		protected void writeNbt(ServerWorld world, NbtCompound nbt) {
		}

		protected void setAirAndWater(StructureWorldAccess world, BlockBox box, int x, int y, int z, int width, int height, int depth) {
			for (int i = y; i <= height; i++) {
				for (int j = x; j <= width; j++) {
					for (int k = z; k <= depth; k++) {
						BlockState blockState = this.getBlockAt(world, j, i, k, box);
						if (!ICE_BLOCKS.contains(blockState.getBlock())) {
							if (this.applyYTransform(i) >= world.getSeaLevel() && blockState != WATER) {
								this.addBlock(world, Blocks.AIR.getDefaultState(), j, i, k, box);
							} else {
								this.addBlock(world, WATER, j, i, k, box);
							}
						}
					}
				}
			}
		}

		protected void method_14774(StructureWorldAccess structureWorldAccess, BlockBox blockBox, int i, int j, boolean bl) {
			if (bl) {
				this.fillWithOutline(structureWorldAccess, blockBox, i + 0, 0, j + 0, i + 2, 0, j + 8 - 1, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, i + 5, 0, j + 0, i + 8 - 1, 0, j + 8 - 1, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, i + 3, 0, j + 0, i + 4, 0, j + 2, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, i + 3, 0, j + 5, i + 4, 0, j + 8 - 1, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(structureWorldAccess, blockBox, i + 3, 0, j + 2, i + 4, 0, j + 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(structureWorldAccess, blockBox, i + 3, 0, j + 5, i + 4, 0, j + 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(structureWorldAccess, blockBox, i + 2, 0, j + 3, i + 2, 0, j + 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(structureWorldAccess, blockBox, i + 5, 0, j + 3, i + 5, 0, j + 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			} else {
				this.fillWithOutline(structureWorldAccess, blockBox, i + 0, 0, j + 0, i + 8 - 1, 0, j + 8 - 1, PRISMARINE, PRISMARINE, false);
			}
		}

		protected void method_14771(StructureWorldAccess structureWorldAccess, BlockBox blockBox, int i, int j, int k, int l, int m, int n, BlockState blockState) {
			for (int o = j; o <= m; o++) {
				for (int p = i; p <= l; p++) {
					for (int q = k; q <= n; q++) {
						if (this.getBlockAt(structureWorldAccess, p, o, q, blockBox) == WATER) {
							this.addBlock(structureWorldAccess, blockState, p, o, q, blockBox);
						}
					}
				}
			}
		}

		protected boolean method_14775(BlockBox blockBox, int i, int j, int k, int l) {
			int m = this.applyXTransform(i, j);
			int n = this.applyZTransform(i, j);
			int o = this.applyXTransform(k, l);
			int p = this.applyZTransform(k, l);
			return blockBox.intersectsXZ(Math.min(m, o), Math.min(n, p), Math.max(m, o), Math.max(n, p));
		}

		protected boolean spawnElderGuardian(StructureWorldAccess world, BlockBox box, int i, int j, int k) {
			BlockPos blockPos = this.offsetPos(i, j, k);
			if (box.contains(blockPos)) {
				ElderGuardianEntity elderGuardianEntity = EntityType.ELDER_GUARDIAN.create(world.toServerWorld());
				elderGuardianEntity.heal(elderGuardianEntity.getMaxHealth());
				elderGuardianEntity.refreshPositionAndAngles((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, 0.0F, 0.0F);
				elderGuardianEntity.initialize(world, world.getLocalDifficulty(elderGuardianEntity.getBlockPos()), SpawnReason.STRUCTURE, null, null);
				world.spawnEntityAndPassengers(elderGuardianEntity);
				return true;
			} else {
				return false;
			}
		}
	}

	interface PieceFactory {
		boolean canGenerate(OceanMonumentGenerator.PieceSetting setting);

		OceanMonumentGenerator.Piece generate(Direction direction, OceanMonumentGenerator.PieceSetting setting, Random random);
	}

	static class PieceSetting {
		private final int roomIndex;
		private final OceanMonumentGenerator.PieceSetting[] neighbors = new OceanMonumentGenerator.PieceSetting[6];
		private final boolean[] neighborPresences = new boolean[6];
		private boolean used;
		private boolean field_14484;
		private int field_14483;

		public PieceSetting(int index) {
			this.roomIndex = index;
		}

		public void setNeighbor(Direction orientation, OceanMonumentGenerator.PieceSetting setting) {
			this.neighbors[orientation.getId()] = setting;
			setting.neighbors[orientation.getOpposite().getId()] = this;
		}

		public void checkNeighborStates() {
			for (int i = 0; i < 6; i++) {
				this.neighborPresences[i] = this.neighbors[i] != null;
			}
		}

		public boolean method_14783(int i) {
			if (this.field_14484) {
				return true;
			} else {
				this.field_14483 = i;

				for (int j = 0; j < 6; j++) {
					if (this.neighbors[j] != null && this.neighborPresences[j] && this.neighbors[j].field_14483 != i && this.neighbors[j].method_14783(i)) {
						return true;
					}
				}

				return false;
			}
		}

		public boolean isAboveLevelThree() {
			return this.roomIndex >= 75;
		}

		public int countNeighbors() {
			int i = 0;

			for (int j = 0; j < 6; j++) {
				if (this.neighborPresences[j]) {
					i++;
				}
			}

			return i;
		}
	}

	public static class SimpleRoom extends OceanMonumentGenerator.Piece {
		private int field_14480;

		public SimpleRoom(Direction orientation, OceanMonumentGenerator.PieceSetting setting, Random random) {
			super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, 1, orientation, setting, 1, 1, 1);
			this.field_14480 = random.nextInt(3);
		}

		public SimpleRoom(ServerWorld serverWorld, NbtCompound nbt) {
			super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, nbt);
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			if (this.setting.roomIndex / 25 > 0) {
				this.method_14774(world, boundingBox, 0, 0, this.setting.neighborPresences[Direction.DOWN.getId()]);
			}

			if (this.setting.neighbors[Direction.UP.getId()] == null) {
				this.method_14771(world, boundingBox, 1, 4, 1, 6, 4, 6, PRISMARINE);
			}

			boolean bl = this.field_14480 != 0
				&& random.nextBoolean()
				&& !this.setting.neighborPresences[Direction.DOWN.getId()]
				&& !this.setting.neighborPresences[Direction.UP.getId()]
				&& this.setting.countNeighbors() > 1;
			if (this.field_14480 == 0) {
				this.fillWithOutline(world, boundingBox, 0, 1, 0, 2, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 0, 3, 0, 2, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 2, 2, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 1, 2, 0, 2, 2, 0, PRISMARINE, PRISMARINE, false);
				this.addBlock(world, SEA_LANTERN, 1, 2, 1, boundingBox);
				this.fillWithOutline(world, boundingBox, 5, 1, 0, 7, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 5, 3, 0, 7, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 7, 2, 0, 7, 2, 2, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 5, 2, 0, 6, 2, 0, PRISMARINE, PRISMARINE, false);
				this.addBlock(world, SEA_LANTERN, 6, 2, 1, boundingBox);
				this.fillWithOutline(world, boundingBox, 0, 1, 5, 2, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 0, 3, 5, 2, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 0, 2, 5, 0, 2, 7, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 1, 2, 7, 2, 2, 7, PRISMARINE, PRISMARINE, false);
				this.addBlock(world, SEA_LANTERN, 1, 2, 6, boundingBox);
				this.fillWithOutline(world, boundingBox, 5, 1, 5, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 5, 3, 5, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 7, 2, 5, 7, 2, 7, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 5, 2, 7, 6, 2, 7, PRISMARINE, PRISMARINE, false);
				this.addBlock(world, SEA_LANTERN, 6, 2, 6, boundingBox);
				if (this.setting.neighborPresences[Direction.SOUTH.getId()]) {
					this.fillWithOutline(world, boundingBox, 3, 3, 0, 4, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(world, boundingBox, 3, 3, 0, 4, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 3, 2, 0, 4, 2, 0, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(world, boundingBox, 3, 1, 0, 4, 1, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				if (this.setting.neighborPresences[Direction.NORTH.getId()]) {
					this.fillWithOutline(world, boundingBox, 3, 3, 7, 4, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(world, boundingBox, 3, 3, 6, 4, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 3, 2, 7, 4, 2, 7, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(world, boundingBox, 3, 1, 6, 4, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				if (this.setting.neighborPresences[Direction.WEST.getId()]) {
					this.fillWithOutline(world, boundingBox, 0, 3, 3, 0, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(world, boundingBox, 0, 3, 3, 1, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 0, 2, 3, 0, 2, 4, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(world, boundingBox, 0, 1, 3, 1, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				if (this.setting.neighborPresences[Direction.EAST.getId()]) {
					this.fillWithOutline(world, boundingBox, 7, 3, 3, 7, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(world, boundingBox, 6, 3, 3, 7, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 7, 2, 3, 7, 2, 4, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(world, boundingBox, 6, 1, 3, 7, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}
			} else if (this.field_14480 == 1) {
				this.fillWithOutline(world, boundingBox, 2, 1, 2, 2, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 2, 1, 5, 2, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 5, 1, 5, 5, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 5, 1, 2, 5, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.addBlock(world, SEA_LANTERN, 2, 2, 2, boundingBox);
				this.addBlock(world, SEA_LANTERN, 2, 2, 5, boundingBox);
				this.addBlock(world, SEA_LANTERN, 5, 2, 5, boundingBox);
				this.addBlock(world, SEA_LANTERN, 5, 2, 2, boundingBox);
				this.fillWithOutline(world, boundingBox, 0, 1, 0, 1, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 0, 1, 1, 0, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 0, 1, 7, 1, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 0, 1, 6, 0, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 6, 1, 7, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 7, 1, 6, 7, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 6, 1, 0, 7, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 7, 1, 1, 7, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.addBlock(world, PRISMARINE, 1, 2, 0, boundingBox);
				this.addBlock(world, PRISMARINE, 0, 2, 1, boundingBox);
				this.addBlock(world, PRISMARINE, 1, 2, 7, boundingBox);
				this.addBlock(world, PRISMARINE, 0, 2, 6, boundingBox);
				this.addBlock(world, PRISMARINE, 6, 2, 7, boundingBox);
				this.addBlock(world, PRISMARINE, 7, 2, 6, boundingBox);
				this.addBlock(world, PRISMARINE, 6, 2, 0, boundingBox);
				this.addBlock(world, PRISMARINE, 7, 2, 1, boundingBox);
				if (!this.setting.neighborPresences[Direction.SOUTH.getId()]) {
					this.fillWithOutline(world, boundingBox, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 1, 2, 0, 6, 2, 0, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(world, boundingBox, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				if (!this.setting.neighborPresences[Direction.NORTH.getId()]) {
					this.fillWithOutline(world, boundingBox, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 1, 2, 7, 6, 2, 7, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(world, boundingBox, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				if (!this.setting.neighborPresences[Direction.WEST.getId()]) {
					this.fillWithOutline(world, boundingBox, 0, 3, 1, 0, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 0, 2, 1, 0, 2, 6, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(world, boundingBox, 0, 1, 1, 0, 1, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				if (!this.setting.neighborPresences[Direction.EAST.getId()]) {
					this.fillWithOutline(world, boundingBox, 7, 3, 1, 7, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 7, 2, 1, 7, 2, 6, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(world, boundingBox, 7, 1, 1, 7, 1, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}
			} else if (this.field_14480 == 2) {
				this.fillWithOutline(world, boundingBox, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 7, 2, 0, 7, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 1, 2, 0, 6, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 1, 2, 7, 6, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 7, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 0, 1, 3, 0, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 7, 1, 3, 7, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 3, 1, 0, 4, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 3, 1, 7, 4, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
				if (this.setting.neighborPresences[Direction.SOUTH.getId()]) {
					this.setAirAndWater(world, boundingBox, 3, 1, 0, 4, 2, 0);
				}

				if (this.setting.neighborPresences[Direction.NORTH.getId()]) {
					this.setAirAndWater(world, boundingBox, 3, 1, 7, 4, 2, 7);
				}

				if (this.setting.neighborPresences[Direction.WEST.getId()]) {
					this.setAirAndWater(world, boundingBox, 0, 1, 3, 0, 2, 4);
				}

				if (this.setting.neighborPresences[Direction.EAST.getId()]) {
					this.setAirAndWater(world, boundingBox, 7, 1, 3, 7, 2, 4);
				}
			}

			if (bl) {
				this.fillWithOutline(world, boundingBox, 3, 1, 3, 4, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 3, 2, 3, 4, 2, 4, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 3, 3, 3, 4, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			}

			return true;
		}
	}

	static class SimpleRoomFactory implements OceanMonumentGenerator.PieceFactory {
		private SimpleRoomFactory() {
		}

		@Override
		public boolean canGenerate(OceanMonumentGenerator.PieceSetting setting) {
			return true;
		}

		@Override
		public OceanMonumentGenerator.Piece generate(Direction direction, OceanMonumentGenerator.PieceSetting setting, Random random) {
			setting.used = true;
			return new OceanMonumentGenerator.SimpleRoom(direction, setting, random);
		}
	}

	public static class SimpleRoomTop extends OceanMonumentGenerator.Piece {
		public SimpleRoomTop(Direction direction, OceanMonumentGenerator.PieceSetting setting) {
			super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, 1, direction, setting, 1, 1, 1);
		}

		public SimpleRoomTop(ServerWorld serverWorld, NbtCompound nbt) {
			super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, nbt);
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			if (this.setting.roomIndex / 25 > 0) {
				this.method_14774(world, boundingBox, 0, 0, this.setting.neighborPresences[Direction.DOWN.getId()]);
			}

			if (this.setting.neighbors[Direction.UP.getId()] == null) {
				this.method_14771(world, boundingBox, 1, 4, 1, 6, 4, 6, PRISMARINE);
			}

			for (int i = 1; i <= 6; i++) {
				for (int j = 1; j <= 6; j++) {
					if (random.nextInt(3) != 0) {
						int k = 2 + (random.nextInt(4) == 0 ? 0 : 1);
						BlockState blockState = Blocks.WET_SPONGE.getDefaultState();
						this.fillWithOutline(world, boundingBox, i, k, j, i, 3, j, blockState, blockState, false);
					}
				}
			}

			this.fillWithOutline(world, boundingBox, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 7, 2, 0, 7, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 1, 2, 0, 6, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 1, 2, 7, 6, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 7, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 0, 1, 3, 0, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 7, 1, 3, 7, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 3, 1, 0, 4, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 3, 1, 7, 4, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
			if (this.setting.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 0, 4, 2, 0);
			}

			return true;
		}
	}

	static class SimpleRoomTopFactory implements OceanMonumentGenerator.PieceFactory {
		private SimpleRoomTopFactory() {
		}

		@Override
		public boolean canGenerate(OceanMonumentGenerator.PieceSetting setting) {
			return !setting.neighborPresences[Direction.WEST.getId()]
				&& !setting.neighborPresences[Direction.EAST.getId()]
				&& !setting.neighborPresences[Direction.NORTH.getId()]
				&& !setting.neighborPresences[Direction.SOUTH.getId()]
				&& !setting.neighborPresences[Direction.UP.getId()];
		}

		@Override
		public OceanMonumentGenerator.Piece generate(Direction direction, OceanMonumentGenerator.PieceSetting setting, Random random) {
			setting.used = true;
			return new OceanMonumentGenerator.SimpleRoomTop(direction, setting);
		}
	}

	public static class WingRoom extends OceanMonumentGenerator.Piece {
		private int field_14481;

		public WingRoom(Direction orientation, BlockBox box, int i) {
			super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, orientation, 1, box);
			this.field_14481 = i & 1;
		}

		public WingRoom(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, nbt);
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			if (this.field_14481 == 0) {
				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(world, boundingBox, 10 - i, 3 - i, 20 - i, 12 + i, 3 - i, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				this.fillWithOutline(world, boundingBox, 7, 0, 6, 15, 0, 16, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 6, 0, 6, 6, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 16, 0, 6, 16, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 7, 1, 7, 7, 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 15, 1, 7, 15, 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 7, 1, 6, 9, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 13, 1, 6, 15, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 8, 1, 7, 9, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 13, 1, 7, 14, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 9, 0, 5, 13, 0, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 10, 0, 7, 12, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 8, 0, 10, 8, 0, 12, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 14, 0, 10, 14, 0, 12, DARK_PRISMARINE, DARK_PRISMARINE, false);

				for (int i = 18; i >= 7; i -= 3) {
					this.addBlock(world, SEA_LANTERN, 6, 3, i, boundingBox);
					this.addBlock(world, SEA_LANTERN, 16, 3, i, boundingBox);
				}

				this.addBlock(world, SEA_LANTERN, 10, 0, 10, boundingBox);
				this.addBlock(world, SEA_LANTERN, 12, 0, 10, boundingBox);
				this.addBlock(world, SEA_LANTERN, 10, 0, 12, boundingBox);
				this.addBlock(world, SEA_LANTERN, 12, 0, 12, boundingBox);
				this.addBlock(world, SEA_LANTERN, 8, 3, 6, boundingBox);
				this.addBlock(world, SEA_LANTERN, 14, 3, 6, boundingBox);
				this.addBlock(world, PRISMARINE_BRICKS, 4, 2, 4, boundingBox);
				this.addBlock(world, SEA_LANTERN, 4, 1, 4, boundingBox);
				this.addBlock(world, PRISMARINE_BRICKS, 4, 0, 4, boundingBox);
				this.addBlock(world, PRISMARINE_BRICKS, 18, 2, 4, boundingBox);
				this.addBlock(world, SEA_LANTERN, 18, 1, 4, boundingBox);
				this.addBlock(world, PRISMARINE_BRICKS, 18, 0, 4, boundingBox);
				this.addBlock(world, PRISMARINE_BRICKS, 4, 2, 18, boundingBox);
				this.addBlock(world, SEA_LANTERN, 4, 1, 18, boundingBox);
				this.addBlock(world, PRISMARINE_BRICKS, 4, 0, 18, boundingBox);
				this.addBlock(world, PRISMARINE_BRICKS, 18, 2, 18, boundingBox);
				this.addBlock(world, SEA_LANTERN, 18, 1, 18, boundingBox);
				this.addBlock(world, PRISMARINE_BRICKS, 18, 0, 18, boundingBox);
				this.addBlock(world, PRISMARINE_BRICKS, 9, 7, 20, boundingBox);
				this.addBlock(world, PRISMARINE_BRICKS, 13, 7, 20, boundingBox);
				this.fillWithOutline(world, boundingBox, 6, 0, 21, 7, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 15, 0, 21, 16, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.spawnElderGuardian(world, boundingBox, 11, 2, 16);
			} else if (this.field_14481 == 1) {
				this.fillWithOutline(world, boundingBox, 9, 3, 18, 13, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 9, 0, 18, 9, 2, 18, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 13, 0, 18, 13, 2, 18, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				int i = 9;
				int j = 20;
				int k = 5;

				for (int l = 0; l < 2; l++) {
					this.addBlock(world, PRISMARINE_BRICKS, i, 6, 20, boundingBox);
					this.addBlock(world, SEA_LANTERN, i, 5, 20, boundingBox);
					this.addBlock(world, PRISMARINE_BRICKS, i, 4, 20, boundingBox);
					i = 13;
				}

				this.fillWithOutline(world, boundingBox, 7, 3, 7, 15, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				int var14 = 10;

				for (int l = 0; l < 2; l++) {
					this.fillWithOutline(world, boundingBox, var14, 0, 10, var14, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, var14, 0, 12, var14, 6, 12, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.addBlock(world, SEA_LANTERN, var14, 0, 10, boundingBox);
					this.addBlock(world, SEA_LANTERN, var14, 0, 12, boundingBox);
					this.addBlock(world, SEA_LANTERN, var14, 4, 10, boundingBox);
					this.addBlock(world, SEA_LANTERN, var14, 4, 12, boundingBox);
					var14 = 12;
				}

				var14 = 8;

				for (int l = 0; l < 2; l++) {
					this.fillWithOutline(world, boundingBox, var14, 0, 7, var14, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, var14, 0, 14, var14, 2, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					var14 = 14;
				}

				this.fillWithOutline(world, boundingBox, 8, 3, 8, 8, 3, 13, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 14, 3, 8, 14, 3, 13, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.spawnElderGuardian(world, boundingBox, 11, 5, 13);
			}

			return true;
		}
	}
}
