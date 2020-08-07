package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.MineshaftFeature;

public class MineshaftGenerator {
	private static MineshaftGenerator.MineshaftPart getRandomJigsaw(
		List<StructurePiece> list, Random random, int i, int j, int k, @Nullable Direction direction, int l, MineshaftFeature.Type type
	) {
		int m = random.nextInt(100);
		if (m >= 80) {
			BlockBox blockBox = MineshaftGenerator.MineshaftCrossing.getBoundingBox(list, random, i, j, k, direction);
			if (blockBox != null) {
				return new MineshaftGenerator.MineshaftCrossing(l, blockBox, direction, type);
			}
		} else if (m >= 70) {
			BlockBox blockBox = MineshaftGenerator.MineshaftStairs.getBoundingBox(list, random, i, j, k, direction);
			if (blockBox != null) {
				return new MineshaftGenerator.MineshaftStairs(l, blockBox, direction, type);
			}
		} else {
			BlockBox blockBox = MineshaftGenerator.MineshaftCorridor.getBoundingBox(list, random, i, j, k, direction);
			if (blockBox != null) {
				return new MineshaftGenerator.MineshaftCorridor(l, random, blockBox, direction, type);
			}
		}

		return null;
	}

	private static MineshaftGenerator.MineshaftPart tryGenerateJigsaw(
		StructurePiece structurePiece, List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l
	) {
		if (l > 8) {
			return null;
		} else if (Math.abs(i - structurePiece.getBoundingBox().minX) <= 80 && Math.abs(k - structurePiece.getBoundingBox().minZ) <= 80) {
			MineshaftFeature.Type type = ((MineshaftGenerator.MineshaftPart)structurePiece).mineshaftType;
			MineshaftGenerator.MineshaftPart mineshaftPart = getRandomJigsaw(list, random, i, j, k, direction, l + 1, type);
			if (mineshaftPart != null) {
				list.add(mineshaftPart);
				mineshaftPart.placeJigsaw(structurePiece, list, random);
			}

			return mineshaftPart;
		} else {
			return null;
		}
	}

	public static class MineshaftCorridor extends MineshaftGenerator.MineshaftPart {
		private final boolean hasRails;
		private final boolean hasCobwebs;
		private boolean hasSpawner;
		private final int length;

		public MineshaftCorridor(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.MINESHAFT_CORRIDOR, compoundTag);
			this.hasRails = compoundTag.getBoolean("hr");
			this.hasCobwebs = compoundTag.getBoolean("sc");
			this.hasSpawner = compoundTag.getBoolean("hps");
			this.length = compoundTag.getInt("Num");
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putBoolean("hr", this.hasRails);
			tag.putBoolean("sc", this.hasCobwebs);
			tag.putBoolean("hps", this.hasSpawner);
			tag.putInt("Num", this.length);
		}

		public MineshaftCorridor(int i, Random random, BlockBox blockBox, Direction direction, MineshaftFeature.Type type) {
			super(StructurePieceType.MINESHAFT_CORRIDOR, i, type);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
			this.hasRails = random.nextInt(3) == 0;
			this.hasCobwebs = !this.hasRails && random.nextInt(23) == 0;
			if (this.getFacing().getAxis() == Direction.Axis.field_11051) {
				this.length = blockBox.getBlockCountZ() / 5;
			} else {
				this.length = blockBox.getBlockCountX() / 5;
			}
		}

		public static BlockBox getBoundingBox(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction) {
			BlockBox blockBox = new BlockBox(i, j, k, i, j + 3 - 1, k);

			int l;
			for (l = random.nextInt(3) + 2; l > 0; l--) {
				int m = l * 5;
				switch (direction) {
					case field_11043:
					default:
						blockBox.maxX = i + 3 - 1;
						blockBox.minZ = k - (m - 1);
						break;
					case field_11035:
						blockBox.maxX = i + 3 - 1;
						blockBox.maxZ = k + m - 1;
						break;
					case field_11039:
						blockBox.minX = i - (m - 1);
						blockBox.maxZ = k + 3 - 1;
						break;
					case field_11034:
						blockBox.maxX = i + m - 1;
						blockBox.maxZ = k + 3 - 1;
				}

				if (StructurePiece.getOverlappingPiece(list, blockBox) == null) {
					break;
				}
			}

			return l > 0 ? blockBox : null;
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			int i = this.getLength();
			int j = random.nextInt(4);
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case field_11043:
					default:
						if (j <= 1) {
							MineshaftGenerator.tryGenerateJigsaw(
								structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, direction, i
							);
						} else if (j == 2) {
							MineshaftGenerator.tryGenerateJigsaw(
								structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, Direction.field_11039, i
							);
						} else {
							MineshaftGenerator.tryGenerateJigsaw(
								structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, Direction.field_11034, i
							);
						}
						break;
					case field_11035:
						if (j <= 1) {
							MineshaftGenerator.tryGenerateJigsaw(
								structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, direction, i
							);
						} else if (j == 2) {
							MineshaftGenerator.tryGenerateJigsaw(
								structurePiece,
								list,
								random,
								this.boundingBox.minX - 1,
								this.boundingBox.minY - 1 + random.nextInt(3),
								this.boundingBox.maxZ - 3,
								Direction.field_11039,
								i
							);
						} else {
							MineshaftGenerator.tryGenerateJigsaw(
								structurePiece,
								list,
								random,
								this.boundingBox.maxX + 1,
								this.boundingBox.minY - 1 + random.nextInt(3),
								this.boundingBox.maxZ - 3,
								Direction.field_11034,
								i
							);
						}
						break;
					case field_11039:
						if (j <= 1) {
							MineshaftGenerator.tryGenerateJigsaw(
								structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, direction, i
							);
						} else if (j == 2) {
							MineshaftGenerator.tryGenerateJigsaw(
								structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, Direction.field_11043, i
							);
						} else {
							MineshaftGenerator.tryGenerateJigsaw(
								structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, Direction.field_11035, i
							);
						}
						break;
					case field_11034:
						if (j <= 1) {
							MineshaftGenerator.tryGenerateJigsaw(
								structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, direction, i
							);
						} else if (j == 2) {
							MineshaftGenerator.tryGenerateJigsaw(
								structurePiece,
								list,
								random,
								this.boundingBox.maxX - 3,
								this.boundingBox.minY - 1 + random.nextInt(3),
								this.boundingBox.minZ - 1,
								Direction.field_11043,
								i
							);
						} else {
							MineshaftGenerator.tryGenerateJigsaw(
								structurePiece,
								list,
								random,
								this.boundingBox.maxX - 3,
								this.boundingBox.minY - 1 + random.nextInt(3),
								this.boundingBox.maxZ + 1,
								Direction.field_11035,
								i
							);
						}
				}
			}

			if (i < 8) {
				if (direction != Direction.field_11043 && direction != Direction.field_11035) {
					for (int k = this.boundingBox.minX + 3; k + 3 <= this.boundingBox.maxX; k += 5) {
						int l = random.nextInt(5);
						if (l == 0) {
							MineshaftGenerator.tryGenerateJigsaw(structurePiece, list, random, k, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.field_11043, i + 1);
						} else if (l == 1) {
							MineshaftGenerator.tryGenerateJigsaw(structurePiece, list, random, k, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.field_11035, i + 1);
						}
					}
				} else {
					for (int kx = this.boundingBox.minZ + 3; kx + 3 <= this.boundingBox.maxZ; kx += 5) {
						int l = random.nextInt(5);
						if (l == 0) {
							MineshaftGenerator.tryGenerateJigsaw(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, kx, Direction.field_11039, i + 1);
						} else if (l == 1) {
							MineshaftGenerator.tryGenerateJigsaw(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, kx, Direction.field_11034, i + 1);
						}
					}
				}
			}
		}

		@Override
		protected boolean addChest(StructureWorldAccess structureWorldAccess, BlockBox boundingBox, Random random, int x, int y, int z, Identifier lootTableId) {
			BlockPos blockPos = new BlockPos(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
			if (boundingBox.contains(blockPos)
				&& structureWorldAccess.getBlockState(blockPos).isAir()
				&& !structureWorldAccess.getBlockState(blockPos.method_10074()).isAir()) {
				BlockState blockState = Blocks.field_10167.getDefaultState().with(RailBlock.SHAPE, random.nextBoolean() ? RailShape.field_12665 : RailShape.field_12674);
				this.addBlock(structureWorldAccess, blockState, x, y, z, boundingBox);
				ChestMinecartEntity chestMinecartEntity = new ChestMinecartEntity(
					structureWorldAccess.toServerWorld(), (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5
				);
				chestMinecartEntity.setLootTable(lootTableId, random.nextLong());
				structureWorldAccess.spawnEntity(chestMinecartEntity);
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean generate(
			StructureWorldAccess structureWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			if (this.method_14937(structureWorldAccess, boundingBox)) {
				return false;
			} else {
				int i = 0;
				int j = 2;
				int k = 0;
				int l = 2;
				int m = this.length * 5 - 1;
				BlockState blockState = this.getPlanksType();
				this.fillWithOutline(structureWorldAccess, boundingBox, 0, 0, 0, 2, 1, m, AIR, AIR, false);
				this.fillWithOutlineUnderSeaLevel(structureWorldAccess, boundingBox, random, 0.8F, 0, 2, 0, 2, 2, m, AIR, AIR, false, false);
				if (this.hasCobwebs) {
					this.fillWithOutlineUnderSeaLevel(
						structureWorldAccess, boundingBox, random, 0.6F, 0, 0, 0, 2, 1, m, Blocks.field_10343.getDefaultState(), AIR, false, true
					);
				}

				for (int n = 0; n < this.length; n++) {
					int o = 2 + n * 5;
					this.method_14713(structureWorldAccess, boundingBox, 0, 0, o, 2, 2, random);
					this.method_14715(structureWorldAccess, boundingBox, random, 0.1F, 0, 2, o - 1);
					this.method_14715(structureWorldAccess, boundingBox, random, 0.1F, 2, 2, o - 1);
					this.method_14715(structureWorldAccess, boundingBox, random, 0.1F, 0, 2, o + 1);
					this.method_14715(structureWorldAccess, boundingBox, random, 0.1F, 2, 2, o + 1);
					this.method_14715(structureWorldAccess, boundingBox, random, 0.05F, 0, 2, o - 2);
					this.method_14715(structureWorldAccess, boundingBox, random, 0.05F, 2, 2, o - 2);
					this.method_14715(structureWorldAccess, boundingBox, random, 0.05F, 0, 2, o + 2);
					this.method_14715(structureWorldAccess, boundingBox, random, 0.05F, 2, 2, o + 2);
					if (random.nextInt(100) == 0) {
						this.addChest(structureWorldAccess, boundingBox, random, 2, 0, o - 1, LootTables.field_472);
					}

					if (random.nextInt(100) == 0) {
						this.addChest(structureWorldAccess, boundingBox, random, 0, 0, o + 1, LootTables.field_472);
					}

					if (this.hasCobwebs && !this.hasSpawner) {
						int p = this.applyYTransform(0);
						int q = o - 1 + random.nextInt(3);
						int r = this.applyXTransform(1, q);
						int s = this.applyZTransform(1, q);
						BlockPos blockPos2 = new BlockPos(r, p, s);
						if (boundingBox.contains(blockPos2) && this.isUnderSeaLevel(structureWorldAccess, 1, 0, q, boundingBox)) {
							this.hasSpawner = true;
							structureWorldAccess.setBlockState(blockPos2, Blocks.field_10260.getDefaultState(), 2);
							BlockEntity blockEntity = structureWorldAccess.getBlockEntity(blockPos2);
							if (blockEntity instanceof MobSpawnerBlockEntity) {
								((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(EntityType.field_6084);
							}
						}
					}
				}

				for (int n = 0; n <= 2; n++) {
					for (int ox = 0; ox <= m; ox++) {
						int p = -1;
						BlockState blockState2 = this.getBlockAt(structureWorldAccess, n, -1, ox, boundingBox);
						if (blockState2.isAir() && this.isUnderSeaLevel(structureWorldAccess, n, -1, ox, boundingBox)) {
							int r = -1;
							this.addBlock(structureWorldAccess, blockState, n, -1, ox, boundingBox);
						}
					}
				}

				if (this.hasRails) {
					BlockState blockState3 = Blocks.field_10167.getDefaultState().with(RailBlock.SHAPE, RailShape.field_12665);

					for (int oxx = 0; oxx <= m; oxx++) {
						BlockState blockState4 = this.getBlockAt(structureWorldAccess, 1, -1, oxx, boundingBox);
						if (!blockState4.isAir()
							&& blockState4.isOpaqueFullCube(structureWorldAccess, new BlockPos(this.applyXTransform(1, oxx), this.applyYTransform(-1), this.applyZTransform(1, oxx)))
							)
						 {
							float f = this.isUnderSeaLevel(structureWorldAccess, 1, 0, oxx, boundingBox) ? 0.7F : 0.9F;
							this.addBlockWithRandomThreshold(structureWorldAccess, boundingBox, random, f, 1, 0, oxx, blockState3);
						}
					}
				}

				return true;
			}
		}

		private void method_14713(StructureWorldAccess structureWorldAccess, BlockBox blockBox, int i, int j, int k, int l, int m, Random random) {
			if (this.method_14719(structureWorldAccess, blockBox, i, m, l, k)) {
				BlockState blockState = this.getPlanksType();
				BlockState blockState2 = this.getFenceType();
				this.fillWithOutline(structureWorldAccess, blockBox, i, j, k, i, l - 1, k, blockState2.with(FenceBlock.WEST, Boolean.valueOf(true)), AIR, false);
				this.fillWithOutline(structureWorldAccess, blockBox, m, j, k, m, l - 1, k, blockState2.with(FenceBlock.EAST, Boolean.valueOf(true)), AIR, false);
				if (random.nextInt(4) == 0) {
					this.fillWithOutline(structureWorldAccess, blockBox, i, l, k, i, l, k, blockState, AIR, false);
					this.fillWithOutline(structureWorldAccess, blockBox, m, l, k, m, l, k, blockState, AIR, false);
				} else {
					this.fillWithOutline(structureWorldAccess, blockBox, i, l, k, m, l, k, blockState, AIR, false);
					this.addBlockWithRandomThreshold(
						structureWorldAccess, blockBox, random, 0.05F, i + 1, l, k - 1, Blocks.field_10099.getDefaultState().with(WallTorchBlock.FACING, Direction.field_11043)
					);
					this.addBlockWithRandomThreshold(
						structureWorldAccess, blockBox, random, 0.05F, i + 1, l, k + 1, Blocks.field_10099.getDefaultState().with(WallTorchBlock.FACING, Direction.field_11035)
					);
				}
			}
		}

		private void method_14715(StructureWorldAccess structureWorldAccess, BlockBox blockBox, Random random, float f, int i, int j, int k) {
			if (this.isUnderSeaLevel(structureWorldAccess, i, j, k, blockBox)) {
				this.addBlockWithRandomThreshold(structureWorldAccess, blockBox, random, f, i, j, k, Blocks.field_10343.getDefaultState());
			}
		}
	}

	public static class MineshaftCrossing extends MineshaftGenerator.MineshaftPart {
		private final Direction direction;
		private final boolean twoFloors;

		public MineshaftCrossing(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.MINESHAFT_CROSSING, compoundTag);
			this.twoFloors = compoundTag.getBoolean("tf");
			this.direction = Direction.fromHorizontal(compoundTag.getInt("D"));
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putBoolean("tf", this.twoFloors);
			tag.putInt("D", this.direction.getHorizontal());
		}

		public MineshaftCrossing(int i, BlockBox blockBox, @Nullable Direction direction, MineshaftFeature.Type type) {
			super(StructurePieceType.MINESHAFT_CROSSING, i, type);
			this.direction = direction;
			this.boundingBox = blockBox;
			this.twoFloors = blockBox.getBlockCountY() > 3;
		}

		public static BlockBox getBoundingBox(List<StructurePiece> list, Random random, int i, int j, int k, Direction facing) {
			BlockBox blockBox = new BlockBox(i, j, k, i, j + 3 - 1, k);
			if (random.nextInt(4) == 0) {
				blockBox.maxY += 4;
			}

			switch (facing) {
				case field_11043:
				default:
					blockBox.minX = i - 1;
					blockBox.maxX = i + 3;
					blockBox.minZ = k - 4;
					break;
				case field_11035:
					blockBox.minX = i - 1;
					blockBox.maxX = i + 3;
					blockBox.maxZ = k + 3 + 1;
					break;
				case field_11039:
					blockBox.minX = i - 4;
					blockBox.minZ = k - 1;
					blockBox.maxZ = k + 3;
					break;
				case field_11034:
					blockBox.maxX = i + 3 + 1;
					blockBox.minZ = k - 1;
					blockBox.maxZ = k + 3;
			}

			return StructurePiece.getOverlappingPiece(list, blockBox) != null ? null : blockBox;
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			int i = this.getLength();
			switch (this.direction) {
				case field_11043:
				default:
					MineshaftGenerator.tryGenerateJigsaw(
						structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.field_11043, i
					);
					MineshaftGenerator.tryGenerateJigsaw(
						structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.field_11039, i
					);
					MineshaftGenerator.tryGenerateJigsaw(
						structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.field_11034, i
					);
					break;
				case field_11035:
					MineshaftGenerator.tryGenerateJigsaw(
						structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.field_11035, i
					);
					MineshaftGenerator.tryGenerateJigsaw(
						structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.field_11039, i
					);
					MineshaftGenerator.tryGenerateJigsaw(
						structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.field_11034, i
					);
					break;
				case field_11039:
					MineshaftGenerator.tryGenerateJigsaw(
						structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.field_11043, i
					);
					MineshaftGenerator.tryGenerateJigsaw(
						structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.field_11035, i
					);
					MineshaftGenerator.tryGenerateJigsaw(
						structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.field_11039, i
					);
					break;
				case field_11034:
					MineshaftGenerator.tryGenerateJigsaw(
						structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.field_11043, i
					);
					MineshaftGenerator.tryGenerateJigsaw(
						structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.field_11035, i
					);
					MineshaftGenerator.tryGenerateJigsaw(
						structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.field_11034, i
					);
			}

			if (this.twoFloors) {
				if (random.nextBoolean()) {
					MineshaftGenerator.tryGenerateJigsaw(
						structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, Direction.field_11043, i
					);
				}

				if (random.nextBoolean()) {
					MineshaftGenerator.tryGenerateJigsaw(
						structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.field_11039, i
					);
				}

				if (random.nextBoolean()) {
					MineshaftGenerator.tryGenerateJigsaw(
						structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.field_11034, i
					);
				}

				if (random.nextBoolean()) {
					MineshaftGenerator.tryGenerateJigsaw(
						structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, Direction.field_11035, i
					);
				}
			}
		}

		@Override
		public boolean generate(
			StructureWorldAccess structureWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			if (this.method_14937(structureWorldAccess, boundingBox)) {
				return false;
			} else {
				BlockState blockState = this.getPlanksType();
				if (this.twoFloors) {
					this.fillWithOutline(
						structureWorldAccess,
						boundingBox,
						this.boundingBox.minX + 1,
						this.boundingBox.minY,
						this.boundingBox.minZ,
						this.boundingBox.maxX - 1,
						this.boundingBox.minY + 3 - 1,
						this.boundingBox.maxZ,
						AIR,
						AIR,
						false
					);
					this.fillWithOutline(
						structureWorldAccess,
						boundingBox,
						this.boundingBox.minX,
						this.boundingBox.minY,
						this.boundingBox.minZ + 1,
						this.boundingBox.maxX,
						this.boundingBox.minY + 3 - 1,
						this.boundingBox.maxZ - 1,
						AIR,
						AIR,
						false
					);
					this.fillWithOutline(
						structureWorldAccess,
						boundingBox,
						this.boundingBox.minX + 1,
						this.boundingBox.maxY - 2,
						this.boundingBox.minZ,
						this.boundingBox.maxX - 1,
						this.boundingBox.maxY,
						this.boundingBox.maxZ,
						AIR,
						AIR,
						false
					);
					this.fillWithOutline(
						structureWorldAccess,
						boundingBox,
						this.boundingBox.minX,
						this.boundingBox.maxY - 2,
						this.boundingBox.minZ + 1,
						this.boundingBox.maxX,
						this.boundingBox.maxY,
						this.boundingBox.maxZ - 1,
						AIR,
						AIR,
						false
					);
					this.fillWithOutline(
						structureWorldAccess,
						boundingBox,
						this.boundingBox.minX + 1,
						this.boundingBox.minY + 3,
						this.boundingBox.minZ + 1,
						this.boundingBox.maxX - 1,
						this.boundingBox.minY + 3,
						this.boundingBox.maxZ - 1,
						AIR,
						AIR,
						false
					);
				} else {
					this.fillWithOutline(
						structureWorldAccess,
						boundingBox,
						this.boundingBox.minX + 1,
						this.boundingBox.minY,
						this.boundingBox.minZ,
						this.boundingBox.maxX - 1,
						this.boundingBox.maxY,
						this.boundingBox.maxZ,
						AIR,
						AIR,
						false
					);
					this.fillWithOutline(
						structureWorldAccess,
						boundingBox,
						this.boundingBox.minX,
						this.boundingBox.minY,
						this.boundingBox.minZ + 1,
						this.boundingBox.maxX,
						this.boundingBox.maxY,
						this.boundingBox.maxZ - 1,
						AIR,
						AIR,
						false
					);
				}

				this.method_14716(structureWorldAccess, boundingBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
				this.method_14716(structureWorldAccess, boundingBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);
				this.method_14716(structureWorldAccess, boundingBox, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
				this.method_14716(structureWorldAccess, boundingBox, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);

				for (int i = this.boundingBox.minX; i <= this.boundingBox.maxX; i++) {
					for (int j = this.boundingBox.minZ; j <= this.boundingBox.maxZ; j++) {
						if (this.getBlockAt(structureWorldAccess, i, this.boundingBox.minY - 1, j, boundingBox).isAir()
							&& this.isUnderSeaLevel(structureWorldAccess, i, this.boundingBox.minY - 1, j, boundingBox)) {
							this.addBlock(structureWorldAccess, blockState, i, this.boundingBox.minY - 1, j, boundingBox);
						}
					}
				}

				return true;
			}
		}

		private void method_14716(StructureWorldAccess structureWorldAccess, BlockBox blockBox, int i, int j, int k, int l) {
			if (!this.getBlockAt(structureWorldAccess, i, l + 1, k, blockBox).isAir()) {
				this.fillWithOutline(structureWorldAccess, blockBox, i, j, k, i, l, k, this.getPlanksType(), AIR, false);
			}
		}
	}

	abstract static class MineshaftPart extends StructurePiece {
		protected MineshaftFeature.Type mineshaftType;

		public MineshaftPart(StructurePieceType structurePieceType, int i, MineshaftFeature.Type type) {
			super(structurePieceType, i);
			this.mineshaftType = type;
		}

		public MineshaftPart(StructurePieceType structurePieceType, CompoundTag compoundTag) {
			super(structurePieceType, compoundTag);
			this.mineshaftType = MineshaftFeature.Type.byIndex(compoundTag.getInt("MST"));
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			tag.putInt("MST", this.mineshaftType.ordinal());
		}

		protected BlockState getPlanksType() {
			switch (this.mineshaftType) {
				case field_13692:
				default:
					return Blocks.field_10161.getDefaultState();
				case field_13691:
					return Blocks.field_10075.getDefaultState();
			}
		}

		protected BlockState getFenceType() {
			switch (this.mineshaftType) {
				case field_13692:
				default:
					return Blocks.field_10620.getDefaultState();
				case field_13691:
					return Blocks.field_10132.getDefaultState();
			}
		}

		protected boolean method_14719(BlockView blockView, BlockBox blockBox, int i, int j, int k, int l) {
			for (int m = i; m <= j; m++) {
				if (this.getBlockAt(blockView, m, k + 1, l, blockBox).isAir()) {
					return false;
				}
			}

			return true;
		}
	}

	public static class MineshaftRoom extends MineshaftGenerator.MineshaftPart {
		private final List<BlockBox> entrances = Lists.<BlockBox>newLinkedList();

		public MineshaftRoom(int i, Random random, int j, int k, MineshaftFeature.Type type) {
			super(StructurePieceType.MINESHAFT_ROOM, i, type);
			this.mineshaftType = type;
			this.boundingBox = new BlockBox(j, 50, k, j + 7 + random.nextInt(6), 54 + random.nextInt(6), k + 7 + random.nextInt(6));
		}

		public MineshaftRoom(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.MINESHAFT_ROOM, compoundTag);
			ListTag listTag = compoundTag.getList("Entrances", 11);

			for (int i = 0; i < listTag.size(); i++) {
				this.entrances.add(new BlockBox(listTag.getIntArray(i)));
			}
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			int i = this.getLength();
			int j = this.boundingBox.getBlockCountY() - 3 - 1;
			if (j <= 0) {
				j = 1;
			}

			int k = 0;

			while (k < this.boundingBox.getBlockCountX()) {
				k += random.nextInt(this.boundingBox.getBlockCountX());
				if (k + 3 > this.boundingBox.getBlockCountX()) {
					break;
				}

				MineshaftGenerator.MineshaftPart mineshaftPart = MineshaftGenerator.tryGenerateJigsaw(
					structurePiece,
					list,
					random,
					this.boundingBox.minX + k,
					this.boundingBox.minY + random.nextInt(j) + 1,
					this.boundingBox.minZ - 1,
					Direction.field_11043,
					i
				);
				if (mineshaftPart != null) {
					BlockBox blockBox = mineshaftPart.getBoundingBox();
					this.entrances.add(new BlockBox(blockBox.minX, blockBox.minY, this.boundingBox.minZ, blockBox.maxX, blockBox.maxY, this.boundingBox.minZ + 1));
				}

				k += 4;
			}

			k = 0;

			while (k < this.boundingBox.getBlockCountX()) {
				k += random.nextInt(this.boundingBox.getBlockCountX());
				if (k + 3 > this.boundingBox.getBlockCountX()) {
					break;
				}

				MineshaftGenerator.MineshaftPart mineshaftPart = MineshaftGenerator.tryGenerateJigsaw(
					structurePiece,
					list,
					random,
					this.boundingBox.minX + k,
					this.boundingBox.minY + random.nextInt(j) + 1,
					this.boundingBox.maxZ + 1,
					Direction.field_11035,
					i
				);
				if (mineshaftPart != null) {
					BlockBox blockBox = mineshaftPart.getBoundingBox();
					this.entrances.add(new BlockBox(blockBox.minX, blockBox.minY, this.boundingBox.maxZ - 1, blockBox.maxX, blockBox.maxY, this.boundingBox.maxZ));
				}

				k += 4;
			}

			k = 0;

			while (k < this.boundingBox.getBlockCountZ()) {
				k += random.nextInt(this.boundingBox.getBlockCountZ());
				if (k + 3 > this.boundingBox.getBlockCountZ()) {
					break;
				}

				MineshaftGenerator.MineshaftPart mineshaftPart = MineshaftGenerator.tryGenerateJigsaw(
					structurePiece,
					list,
					random,
					this.boundingBox.minX - 1,
					this.boundingBox.minY + random.nextInt(j) + 1,
					this.boundingBox.minZ + k,
					Direction.field_11039,
					i
				);
				if (mineshaftPart != null) {
					BlockBox blockBox = mineshaftPart.getBoundingBox();
					this.entrances.add(new BlockBox(this.boundingBox.minX, blockBox.minY, blockBox.minZ, this.boundingBox.minX + 1, blockBox.maxY, blockBox.maxZ));
				}

				k += 4;
			}

			k = 0;

			while (k < this.boundingBox.getBlockCountZ()) {
				k += random.nextInt(this.boundingBox.getBlockCountZ());
				if (k + 3 > this.boundingBox.getBlockCountZ()) {
					break;
				}

				StructurePiece structurePiece2 = MineshaftGenerator.tryGenerateJigsaw(
					structurePiece,
					list,
					random,
					this.boundingBox.maxX + 1,
					this.boundingBox.minY + random.nextInt(j) + 1,
					this.boundingBox.minZ + k,
					Direction.field_11034,
					i
				);
				if (structurePiece2 != null) {
					BlockBox blockBox = structurePiece2.getBoundingBox();
					this.entrances.add(new BlockBox(this.boundingBox.maxX - 1, blockBox.minY, blockBox.minZ, this.boundingBox.maxX, blockBox.maxY, blockBox.maxZ));
				}

				k += 4;
			}
		}

		@Override
		public boolean generate(
			StructureWorldAccess structureWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			if (this.method_14937(structureWorldAccess, boundingBox)) {
				return false;
			} else {
				this.fillWithOutline(
					structureWorldAccess,
					boundingBox,
					this.boundingBox.minX,
					this.boundingBox.minY,
					this.boundingBox.minZ,
					this.boundingBox.maxX,
					this.boundingBox.minY,
					this.boundingBox.maxZ,
					Blocks.field_10566.getDefaultState(),
					AIR,
					true
				);
				this.fillWithOutline(
					structureWorldAccess,
					boundingBox,
					this.boundingBox.minX,
					this.boundingBox.minY + 1,
					this.boundingBox.minZ,
					this.boundingBox.maxX,
					Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY),
					this.boundingBox.maxZ,
					AIR,
					AIR,
					false
				);

				for (BlockBox blockBox : this.entrances) {
					this.fillWithOutline(
						structureWorldAccess, boundingBox, blockBox.minX, blockBox.maxY - 2, blockBox.minZ, blockBox.maxX, blockBox.maxY, blockBox.maxZ, AIR, AIR, false
					);
				}

				this.method_14919(
					structureWorldAccess,
					boundingBox,
					this.boundingBox.minX,
					this.boundingBox.minY + 4,
					this.boundingBox.minZ,
					this.boundingBox.maxX,
					this.boundingBox.maxY,
					this.boundingBox.maxZ,
					AIR,
					false
				);
				return true;
			}
		}

		@Override
		public void translate(int x, int y, int z) {
			super.translate(x, y, z);

			for (BlockBox blockBox : this.entrances) {
				blockBox.offset(x, y, z);
			}
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			ListTag listTag = new ListTag();

			for (BlockBox blockBox : this.entrances) {
				listTag.add(blockBox.toNbt());
			}

			tag.put("Entrances", listTag);
		}
	}

	public static class MineshaftStairs extends MineshaftGenerator.MineshaftPart {
		public MineshaftStairs(int i, BlockBox blockBox, Direction direction, MineshaftFeature.Type type) {
			super(StructurePieceType.MINESHAFT_STAIRS, i, type);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
		}

		public MineshaftStairs(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.MINESHAFT_STAIRS, compoundTag);
		}

		public static BlockBox getBoundingBox(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction) {
			BlockBox blockBox = new BlockBox(i, j - 5, k, i, j + 3 - 1, k);
			switch (direction) {
				case field_11043:
				default:
					blockBox.maxX = i + 3 - 1;
					blockBox.minZ = k - 8;
					break;
				case field_11035:
					blockBox.maxX = i + 3 - 1;
					blockBox.maxZ = k + 8;
					break;
				case field_11039:
					blockBox.minX = i - 8;
					blockBox.maxZ = k + 3 - 1;
					break;
				case field_11034:
					blockBox.maxX = i + 8;
					blockBox.maxZ = k + 3 - 1;
			}

			return StructurePiece.getOverlappingPiece(list, blockBox) != null ? null : blockBox;
		}

		@Override
		public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
			int i = this.getLength();
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case field_11043:
					default:
						MineshaftGenerator.tryGenerateJigsaw(
							structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.field_11043, i
						);
						break;
					case field_11035:
						MineshaftGenerator.tryGenerateJigsaw(
							structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.field_11035, i
						);
						break;
					case field_11039:
						MineshaftGenerator.tryGenerateJigsaw(
							structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.field_11039, i
						);
						break;
					case field_11034:
						MineshaftGenerator.tryGenerateJigsaw(
							structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.field_11034, i
						);
				}
			}
		}

		@Override
		public boolean generate(
			StructureWorldAccess structureWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			if (this.method_14937(structureWorldAccess, boundingBox)) {
				return false;
			} else {
				this.fillWithOutline(structureWorldAccess, boundingBox, 0, 5, 0, 2, 7, 1, AIR, AIR, false);
				this.fillWithOutline(structureWorldAccess, boundingBox, 0, 0, 7, 2, 2, 8, AIR, AIR, false);

				for (int i = 0; i < 5; i++) {
					this.fillWithOutline(structureWorldAccess, boundingBox, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, AIR, AIR, false);
				}

				return true;
			}
		}
	}
}
