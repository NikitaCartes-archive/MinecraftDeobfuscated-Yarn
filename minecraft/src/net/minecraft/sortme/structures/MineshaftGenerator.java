package net.minecraft.sortme.structures;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3443;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sortme.StructurePiece;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.loot.LootTables;

public class MineshaftGenerator {
	private static MineshaftGenerator.MineshaftPart method_14712(
		List<class_3443> list, Random random, int i, int j, int k, @Nullable Direction direction, int l, MineshaftFeature.Type type
	) {
		int m = random.nextInt(100);
		if (m >= 80) {
			MutableIntBoundingBox mutableIntBoundingBox = MineshaftGenerator.MineshaftCrossing.method_14717(list, random, i, j, k, direction);
			if (mutableIntBoundingBox != null) {
				return new MineshaftGenerator.MineshaftCrossing(l, mutableIntBoundingBox, direction, type);
			}
		} else if (m >= 70) {
			MutableIntBoundingBox mutableIntBoundingBox = MineshaftGenerator.MineshaftStairs.method_14720(list, random, i, j, k, direction);
			if (mutableIntBoundingBox != null) {
				return new MineshaftGenerator.MineshaftStairs(l, mutableIntBoundingBox, direction, type);
			}
		} else {
			MutableIntBoundingBox mutableIntBoundingBox = MineshaftGenerator.MineshaftCorridor.method_14714(list, random, i, j, k, direction);
			if (mutableIntBoundingBox != null) {
				return new MineshaftGenerator.MineshaftCorridor(l, random, mutableIntBoundingBox, direction, type);
			}
		}

		return null;
	}

	private static MineshaftGenerator.MineshaftPart method_14711(
		class_3443 arg, List<class_3443> list, Random random, int i, int j, int k, Direction direction, int l
	) {
		if (l > 8) {
			return null;
		} else if (Math.abs(i - arg.method_14935().minX) <= 80 && Math.abs(k - arg.method_14935().minZ) <= 80) {
			MineshaftFeature.Type type = ((MineshaftGenerator.MineshaftPart)arg).mineshaftType;
			MineshaftGenerator.MineshaftPart mineshaftPart = method_14712(list, random, i, j, k, direction, l + 1, type);
			if (mineshaftPart != null) {
				list.add(mineshaftPart);
				mineshaftPart.method_14918(arg, list, random);
			}

			return mineshaftPart;
		} else {
			return null;
		}
	}

	public static class MineshaftCorridor extends MineshaftGenerator.MineshaftPart {
		private final boolean field_14416;
		private final boolean field_14415;
		private boolean field_14414;
		private final int field_14413;

		public MineshaftCorridor(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16969, compoundTag);
			this.field_14416 = compoundTag.getBoolean("hr");
			this.field_14415 = compoundTag.getBoolean("sc");
			this.field_14414 = compoundTag.getBoolean("hps");
			this.field_14413 = compoundTag.getInt("Num");
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putBoolean("hr", this.field_14416);
			compoundTag.putBoolean("sc", this.field_14415);
			compoundTag.putBoolean("hps", this.field_14414);
			compoundTag.putInt("Num", this.field_14413);
		}

		public MineshaftCorridor(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction, MineshaftFeature.Type type) {
			super(StructurePiece.field_16969, i, type);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
			this.field_14416 = random.nextInt(3) == 0;
			this.field_14415 = !this.field_14416 && random.nextInt(23) == 0;
			if (this.getFacing().getAxis() == Direction.Axis.Z) {
				this.field_14413 = mutableIntBoundingBox.getBlockCountZ() / 5;
			} else {
				this.field_14413 = mutableIntBoundingBox.getBlockCountX() / 5;
			}
		}

		public static MutableIntBoundingBox method_14714(List<class_3443> list, Random random, int i, int j, int k, Direction direction) {
			MutableIntBoundingBox mutableIntBoundingBox = new MutableIntBoundingBox(i, j, k, i, j + 3 - 1, k);

			int l;
			for (l = random.nextInt(3) + 2; l > 0; l--) {
				int m = l * 5;
				switch (direction) {
					case NORTH:
					default:
						mutableIntBoundingBox.maxX = i + 3 - 1;
						mutableIntBoundingBox.minZ = k - (m - 1);
						break;
					case SOUTH:
						mutableIntBoundingBox.maxX = i + 3 - 1;
						mutableIntBoundingBox.maxZ = k + m - 1;
						break;
					case WEST:
						mutableIntBoundingBox.minX = i - (m - 1);
						mutableIntBoundingBox.maxZ = k + 3 - 1;
						break;
					case EAST:
						mutableIntBoundingBox.maxX = i + m - 1;
						mutableIntBoundingBox.maxZ = k + 3 - 1;
				}

				if (class_3443.method_14932(list, mutableIntBoundingBox) == null) {
					break;
				}
			}

			return l > 0 ? mutableIntBoundingBox : null;
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			int i = this.method_14923();
			int j = random.nextInt(4);
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
					default:
						if (j <= 1) {
							MineshaftGenerator.method_14711(
								arg, list, random, this.structureBounds.minX, this.structureBounds.minY - 1 + random.nextInt(3), this.structureBounds.minZ - 1, direction, i
							);
						} else if (j == 2) {
							MineshaftGenerator.method_14711(
								arg, list, random, this.structureBounds.minX - 1, this.structureBounds.minY - 1 + random.nextInt(3), this.structureBounds.minZ, Direction.WEST, i
							);
						} else {
							MineshaftGenerator.method_14711(
								arg, list, random, this.structureBounds.maxX + 1, this.structureBounds.minY - 1 + random.nextInt(3), this.structureBounds.minZ, Direction.EAST, i
							);
						}
						break;
					case SOUTH:
						if (j <= 1) {
							MineshaftGenerator.method_14711(
								arg, list, random, this.structureBounds.minX, this.structureBounds.minY - 1 + random.nextInt(3), this.structureBounds.maxZ + 1, direction, i
							);
						} else if (j == 2) {
							MineshaftGenerator.method_14711(
								arg, list, random, this.structureBounds.minX - 1, this.structureBounds.minY - 1 + random.nextInt(3), this.structureBounds.maxZ - 3, Direction.WEST, i
							);
						} else {
							MineshaftGenerator.method_14711(
								arg, list, random, this.structureBounds.maxX + 1, this.structureBounds.minY - 1 + random.nextInt(3), this.structureBounds.maxZ - 3, Direction.EAST, i
							);
						}
						break;
					case WEST:
						if (j <= 1) {
							MineshaftGenerator.method_14711(
								arg, list, random, this.structureBounds.minX - 1, this.structureBounds.minY - 1 + random.nextInt(3), this.structureBounds.minZ, direction, i
							);
						} else if (j == 2) {
							MineshaftGenerator.method_14711(
								arg, list, random, this.structureBounds.minX, this.structureBounds.minY - 1 + random.nextInt(3), this.structureBounds.minZ - 1, Direction.NORTH, i
							);
						} else {
							MineshaftGenerator.method_14711(
								arg, list, random, this.structureBounds.minX, this.structureBounds.minY - 1 + random.nextInt(3), this.structureBounds.maxZ + 1, Direction.SOUTH, i
							);
						}
						break;
					case EAST:
						if (j <= 1) {
							MineshaftGenerator.method_14711(
								arg, list, random, this.structureBounds.maxX + 1, this.structureBounds.minY - 1 + random.nextInt(3), this.structureBounds.minZ, direction, i
							);
						} else if (j == 2) {
							MineshaftGenerator.method_14711(
								arg, list, random, this.structureBounds.maxX - 3, this.structureBounds.minY - 1 + random.nextInt(3), this.structureBounds.minZ - 1, Direction.NORTH, i
							);
						} else {
							MineshaftGenerator.method_14711(
								arg, list, random, this.structureBounds.maxX - 3, this.structureBounds.minY - 1 + random.nextInt(3), this.structureBounds.maxZ + 1, Direction.SOUTH, i
							);
						}
				}
			}

			if (i < 8) {
				if (direction != Direction.NORTH && direction != Direction.SOUTH) {
					for (int k = this.structureBounds.minX + 3; k + 3 <= this.structureBounds.maxX; k += 5) {
						int l = random.nextInt(5);
						if (l == 0) {
							MineshaftGenerator.method_14711(arg, list, random, k, this.structureBounds.minY, this.structureBounds.minZ - 1, Direction.NORTH, i + 1);
						} else if (l == 1) {
							MineshaftGenerator.method_14711(arg, list, random, k, this.structureBounds.minY, this.structureBounds.maxZ + 1, Direction.SOUTH, i + 1);
						}
					}
				} else {
					for (int kx = this.structureBounds.minZ + 3; kx + 3 <= this.structureBounds.maxZ; kx += 5) {
						int l = random.nextInt(5);
						if (l == 0) {
							MineshaftGenerator.method_14711(arg, list, random, this.structureBounds.minX - 1, this.structureBounds.minY, kx, Direction.WEST, i + 1);
						} else if (l == 1) {
							MineshaftGenerator.method_14711(arg, list, random, this.structureBounds.maxX + 1, this.structureBounds.minY, kx, Direction.EAST, i + 1);
						}
					}
				}
			}
		}

		@Override
		protected boolean method_14915(IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, Random random, int i, int j, int k, Identifier identifier) {
			BlockPos blockPos = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
			if (mutableIntBoundingBox.contains(blockPos) && iWorld.getBlockState(blockPos).isAir() && !iWorld.getBlockState(blockPos.down()).isAir()) {
				BlockState blockState = Blocks.field_10167
					.getDefaultState()
					.with(RailBlock.field_11369, random.nextBoolean() ? RailShape.field_12665 : RailShape.field_12674);
				this.addBlock(iWorld, blockState, i, j, k, mutableIntBoundingBox);
				ChestMinecartEntity chestMinecartEntity = new ChestMinecartEntity(
					iWorld.getWorld(), (double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.5F), (double)((float)blockPos.getZ() + 0.5F)
				);
				chestMinecartEntity.setLootTable(identifier, random.nextLong());
				iWorld.spawnEntity(chestMinecartEntity);
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			if (this.method_14937(iWorld, mutableIntBoundingBox)) {
				return false;
			} else {
				int i = 0;
				int j = 2;
				int k = 0;
				int l = 2;
				int m = this.field_14413 * 5 - 1;
				BlockState blockState = this.method_16443();
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 2, 1, m, blockAir, blockAir, false);
				this.fillWithOutlineUnderSealevel(iWorld, mutableIntBoundingBox, random, 0.8F, 0, 2, 0, 2, 2, m, blockAir, blockAir, false, false);
				if (this.field_14415) {
					this.fillWithOutlineUnderSealevel(
						iWorld, mutableIntBoundingBox, random, 0.6F, 0, 0, 0, 2, 1, m, Blocks.field_10343.getDefaultState(), blockAir, false, true
					);
				}

				for (int n = 0; n < this.field_14413; n++) {
					int o = 2 + n * 5;
					this.method_14713(iWorld, mutableIntBoundingBox, 0, 0, o, 2, 2, random);
					this.method_14715(iWorld, mutableIntBoundingBox, random, 0.1F, 0, 2, o - 1);
					this.method_14715(iWorld, mutableIntBoundingBox, random, 0.1F, 2, 2, o - 1);
					this.method_14715(iWorld, mutableIntBoundingBox, random, 0.1F, 0, 2, o + 1);
					this.method_14715(iWorld, mutableIntBoundingBox, random, 0.1F, 2, 2, o + 1);
					this.method_14715(iWorld, mutableIntBoundingBox, random, 0.05F, 0, 2, o - 2);
					this.method_14715(iWorld, mutableIntBoundingBox, random, 0.05F, 2, 2, o - 2);
					this.method_14715(iWorld, mutableIntBoundingBox, random, 0.05F, 0, 2, o + 2);
					this.method_14715(iWorld, mutableIntBoundingBox, random, 0.05F, 2, 2, o + 2);
					if (random.nextInt(100) == 0) {
						this.method_14915(iWorld, mutableIntBoundingBox, random, 2, 0, o - 1, LootTables.CHEST_ABANDONED_MINESHAFT);
					}

					if (random.nextInt(100) == 0) {
						this.method_14915(iWorld, mutableIntBoundingBox, random, 0, 0, o + 1, LootTables.CHEST_ABANDONED_MINESHAFT);
					}

					if (this.field_14415 && !this.field_14414) {
						int p = this.applyYTransform(0);
						int q = o - 1 + random.nextInt(3);
						int r = this.applyXTransform(1, q);
						int s = this.applyZTransform(1, q);
						BlockPos blockPos = new BlockPos(r, p, s);
						if (mutableIntBoundingBox.contains(blockPos) && this.isUnderSeaLevel(iWorld, 1, 0, q, mutableIntBoundingBox)) {
							this.field_14414 = true;
							iWorld.setBlockState(blockPos, Blocks.field_10260.getDefaultState(), 2);
							BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
							if (blockEntity instanceof MobSpawnerBlockEntity) {
								((MobSpawnerBlockEntity)blockEntity).getLogic().method_8274(EntityType.CAVE_SPIDER);
							}
						}
					}
				}

				for (int n = 0; n <= 2; n++) {
					for (int ox = 0; ox <= m; ox++) {
						int p = -1;
						BlockState blockState2 = this.getBlockAt(iWorld, n, -1, ox, mutableIntBoundingBox);
						if (blockState2.isAir() && this.isUnderSeaLevel(iWorld, n, -1, ox, mutableIntBoundingBox)) {
							int r = -1;
							this.addBlock(iWorld, blockState, n, -1, ox, mutableIntBoundingBox);
						}
					}
				}

				if (this.field_14416) {
					BlockState blockState3 = Blocks.field_10167.getDefaultState().with(RailBlock.field_11369, RailShape.field_12665);

					for (int oxx = 0; oxx <= m; oxx++) {
						BlockState blockState4 = this.getBlockAt(iWorld, 1, -1, oxx, mutableIntBoundingBox);
						if (!blockState4.isAir()
							&& blockState4.isFullOpaque(iWorld, new BlockPos(this.applyXTransform(1, oxx), this.applyYTransform(-1), this.applyZTransform(1, oxx)))) {
							float f = this.isUnderSeaLevel(iWorld, 1, 0, oxx, mutableIntBoundingBox) ? 0.7F : 0.9F;
							this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, f, 1, 0, oxx, blockState3);
						}
					}
				}

				return true;
			}
		}

		private void method_14713(IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, int i, int j, int k, int l, int m, Random random) {
			if (this.method_14719(iWorld, mutableIntBoundingBox, i, m, l, k)) {
				BlockState blockState = this.method_16443();
				BlockState blockState2 = this.method_14718();
				this.fillWithOutline(iWorld, mutableIntBoundingBox, i, j, k, i, l - 1, k, blockState2.with(FenceBlock.WEST, Boolean.valueOf(true)), blockAir, false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, m, j, k, m, l - 1, k, blockState2.with(FenceBlock.EAST, Boolean.valueOf(true)), blockAir, false);
				if (random.nextInt(4) == 0) {
					this.fillWithOutline(iWorld, mutableIntBoundingBox, i, l, k, i, l, k, blockState, blockAir, false);
					this.fillWithOutline(iWorld, mutableIntBoundingBox, m, l, k, m, l, k, blockState, blockAir, false);
				} else {
					this.fillWithOutline(iWorld, mutableIntBoundingBox, i, l, k, m, l, k, blockState, blockAir, false);
					this.addBlockWithRandomThreshold(
						iWorld, mutableIntBoundingBox, random, 0.05F, i + 1, l, k - 1, Blocks.field_10099.getDefaultState().with(WallTorchBlock.FACING, Direction.NORTH)
					);
					this.addBlockWithRandomThreshold(
						iWorld, mutableIntBoundingBox, random, 0.05F, i + 1, l, k + 1, Blocks.field_10099.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH)
					);
				}
			}
		}

		private void method_14715(IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, Random random, float f, int i, int j, int k) {
			if (this.isUnderSeaLevel(iWorld, i, j, k, mutableIntBoundingBox)) {
				this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, f, i, j, k, Blocks.field_10343.getDefaultState());
			}
		}
	}

	public static class MineshaftCrossing extends MineshaftGenerator.MineshaftPart {
		private final Direction field_14420;
		private final boolean field_14419;

		public MineshaftCrossing(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16919, compoundTag);
			this.field_14419 = compoundTag.getBoolean("tf");
			this.field_14420 = Direction.fromHorizontal(compoundTag.getInt("D"));
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putBoolean("tf", this.field_14419);
			compoundTag.putInt("D", this.field_14420.getHorizontal());
		}

		public MineshaftCrossing(int i, MutableIntBoundingBox mutableIntBoundingBox, @Nullable Direction direction, MineshaftFeature.Type type) {
			super(StructurePiece.field_16919, i, type);
			this.field_14420 = direction;
			this.structureBounds = mutableIntBoundingBox;
			this.field_14419 = mutableIntBoundingBox.getBlockCountY() > 3;
		}

		public static MutableIntBoundingBox method_14717(List<class_3443> list, Random random, int i, int j, int k, Direction direction) {
			MutableIntBoundingBox mutableIntBoundingBox = new MutableIntBoundingBox(i, j, k, i, j + 3 - 1, k);
			if (random.nextInt(4) == 0) {
				mutableIntBoundingBox.maxY += 4;
			}

			switch (direction) {
				case NORTH:
				default:
					mutableIntBoundingBox.minX = i - 1;
					mutableIntBoundingBox.maxX = i + 3;
					mutableIntBoundingBox.minZ = k - 4;
					break;
				case SOUTH:
					mutableIntBoundingBox.minX = i - 1;
					mutableIntBoundingBox.maxX = i + 3;
					mutableIntBoundingBox.maxZ = k + 3 + 1;
					break;
				case WEST:
					mutableIntBoundingBox.minX = i - 4;
					mutableIntBoundingBox.minZ = k - 1;
					mutableIntBoundingBox.maxZ = k + 3;
					break;
				case EAST:
					mutableIntBoundingBox.maxX = i + 3 + 1;
					mutableIntBoundingBox.minZ = k - 1;
					mutableIntBoundingBox.maxZ = k + 3;
			}

			return class_3443.method_14932(list, mutableIntBoundingBox) != null ? null : mutableIntBoundingBox;
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			int i = this.method_14923();
			switch (this.field_14420) {
				case NORTH:
				default:
					MineshaftGenerator.method_14711(
						arg, list, random, this.structureBounds.minX + 1, this.structureBounds.minY, this.structureBounds.minZ - 1, Direction.NORTH, i
					);
					MineshaftGenerator.method_14711(
						arg, list, random, this.structureBounds.minX - 1, this.structureBounds.minY, this.structureBounds.minZ + 1, Direction.WEST, i
					);
					MineshaftGenerator.method_14711(
						arg, list, random, this.structureBounds.maxX + 1, this.structureBounds.minY, this.structureBounds.minZ + 1, Direction.EAST, i
					);
					break;
				case SOUTH:
					MineshaftGenerator.method_14711(
						arg, list, random, this.structureBounds.minX + 1, this.structureBounds.minY, this.structureBounds.maxZ + 1, Direction.SOUTH, i
					);
					MineshaftGenerator.method_14711(
						arg, list, random, this.structureBounds.minX - 1, this.structureBounds.minY, this.structureBounds.minZ + 1, Direction.WEST, i
					);
					MineshaftGenerator.method_14711(
						arg, list, random, this.structureBounds.maxX + 1, this.structureBounds.minY, this.structureBounds.minZ + 1, Direction.EAST, i
					);
					break;
				case WEST:
					MineshaftGenerator.method_14711(
						arg, list, random, this.structureBounds.minX + 1, this.structureBounds.minY, this.structureBounds.minZ - 1, Direction.NORTH, i
					);
					MineshaftGenerator.method_14711(
						arg, list, random, this.structureBounds.minX + 1, this.structureBounds.minY, this.structureBounds.maxZ + 1, Direction.SOUTH, i
					);
					MineshaftGenerator.method_14711(
						arg, list, random, this.structureBounds.minX - 1, this.structureBounds.minY, this.structureBounds.minZ + 1, Direction.WEST, i
					);
					break;
				case EAST:
					MineshaftGenerator.method_14711(
						arg, list, random, this.structureBounds.minX + 1, this.structureBounds.minY, this.structureBounds.minZ - 1, Direction.NORTH, i
					);
					MineshaftGenerator.method_14711(
						arg, list, random, this.structureBounds.minX + 1, this.structureBounds.minY, this.structureBounds.maxZ + 1, Direction.SOUTH, i
					);
					MineshaftGenerator.method_14711(
						arg, list, random, this.structureBounds.maxX + 1, this.structureBounds.minY, this.structureBounds.minZ + 1, Direction.EAST, i
					);
			}

			if (this.field_14419) {
				if (random.nextBoolean()) {
					MineshaftGenerator.method_14711(
						arg, list, random, this.structureBounds.minX + 1, this.structureBounds.minY + 3 + 1, this.structureBounds.minZ - 1, Direction.NORTH, i
					);
				}

				if (random.nextBoolean()) {
					MineshaftGenerator.method_14711(
						arg, list, random, this.structureBounds.minX - 1, this.structureBounds.minY + 3 + 1, this.structureBounds.minZ + 1, Direction.WEST, i
					);
				}

				if (random.nextBoolean()) {
					MineshaftGenerator.method_14711(
						arg, list, random, this.structureBounds.maxX + 1, this.structureBounds.minY + 3 + 1, this.structureBounds.minZ + 1, Direction.EAST, i
					);
				}

				if (random.nextBoolean()) {
					MineshaftGenerator.method_14711(
						arg, list, random, this.structureBounds.minX + 1, this.structureBounds.minY + 3 + 1, this.structureBounds.maxZ + 1, Direction.SOUTH, i
					);
				}
			}
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			if (this.method_14937(iWorld, mutableIntBoundingBox)) {
				return false;
			} else {
				BlockState blockState = this.method_16443();
				if (this.field_14419) {
					this.fillWithOutline(
						iWorld,
						mutableIntBoundingBox,
						this.structureBounds.minX + 1,
						this.structureBounds.minY,
						this.structureBounds.minZ,
						this.structureBounds.maxX - 1,
						this.structureBounds.minY + 3 - 1,
						this.structureBounds.maxZ,
						blockAir,
						blockAir,
						false
					);
					this.fillWithOutline(
						iWorld,
						mutableIntBoundingBox,
						this.structureBounds.minX,
						this.structureBounds.minY,
						this.structureBounds.minZ + 1,
						this.structureBounds.maxX,
						this.structureBounds.minY + 3 - 1,
						this.structureBounds.maxZ - 1,
						blockAir,
						blockAir,
						false
					);
					this.fillWithOutline(
						iWorld,
						mutableIntBoundingBox,
						this.structureBounds.minX + 1,
						this.structureBounds.maxY - 2,
						this.structureBounds.minZ,
						this.structureBounds.maxX - 1,
						this.structureBounds.maxY,
						this.structureBounds.maxZ,
						blockAir,
						blockAir,
						false
					);
					this.fillWithOutline(
						iWorld,
						mutableIntBoundingBox,
						this.structureBounds.minX,
						this.structureBounds.maxY - 2,
						this.structureBounds.minZ + 1,
						this.structureBounds.maxX,
						this.structureBounds.maxY,
						this.structureBounds.maxZ - 1,
						blockAir,
						blockAir,
						false
					);
					this.fillWithOutline(
						iWorld,
						mutableIntBoundingBox,
						this.structureBounds.minX + 1,
						this.structureBounds.minY + 3,
						this.structureBounds.minZ + 1,
						this.structureBounds.maxX - 1,
						this.structureBounds.minY + 3,
						this.structureBounds.maxZ - 1,
						blockAir,
						blockAir,
						false
					);
				} else {
					this.fillWithOutline(
						iWorld,
						mutableIntBoundingBox,
						this.structureBounds.minX + 1,
						this.structureBounds.minY,
						this.structureBounds.minZ,
						this.structureBounds.maxX - 1,
						this.structureBounds.maxY,
						this.structureBounds.maxZ,
						blockAir,
						blockAir,
						false
					);
					this.fillWithOutline(
						iWorld,
						mutableIntBoundingBox,
						this.structureBounds.minX,
						this.structureBounds.minY,
						this.structureBounds.minZ + 1,
						this.structureBounds.maxX,
						this.structureBounds.maxY,
						this.structureBounds.maxZ - 1,
						blockAir,
						blockAir,
						false
					);
				}

				this.method_14716(
					iWorld, mutableIntBoundingBox, this.structureBounds.minX + 1, this.structureBounds.minY, this.structureBounds.minZ + 1, this.structureBounds.maxY
				);
				this.method_14716(
					iWorld, mutableIntBoundingBox, this.structureBounds.minX + 1, this.structureBounds.minY, this.structureBounds.maxZ - 1, this.structureBounds.maxY
				);
				this.method_14716(
					iWorld, mutableIntBoundingBox, this.structureBounds.maxX - 1, this.structureBounds.minY, this.structureBounds.minZ + 1, this.structureBounds.maxY
				);
				this.method_14716(
					iWorld, mutableIntBoundingBox, this.structureBounds.maxX - 1, this.structureBounds.minY, this.structureBounds.maxZ - 1, this.structureBounds.maxY
				);

				for (int i = this.structureBounds.minX; i <= this.structureBounds.maxX; i++) {
					for (int j = this.structureBounds.minZ; j <= this.structureBounds.maxZ; j++) {
						if (this.getBlockAt(iWorld, i, this.structureBounds.minY - 1, j, mutableIntBoundingBox).isAir()
							&& this.isUnderSeaLevel(iWorld, i, this.structureBounds.minY - 1, j, mutableIntBoundingBox)) {
							this.addBlock(iWorld, blockState, i, this.structureBounds.minY - 1, j, mutableIntBoundingBox);
						}
					}
				}

				return true;
			}
		}

		private void method_14716(IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, int i, int j, int k, int l) {
			if (!this.getBlockAt(iWorld, i, l + 1, k, mutableIntBoundingBox).isAir()) {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, i, j, k, i, l, k, this.method_16443(), blockAir, false);
			}
		}
	}

	abstract static class MineshaftPart extends class_3443 {
		protected MineshaftFeature.Type mineshaftType;

		public MineshaftPart(StructurePiece structurePiece, int i, MineshaftFeature.Type type) {
			super(structurePiece, i);
			this.mineshaftType = type;
		}

		public MineshaftPart(StructurePiece structurePiece, CompoundTag compoundTag) {
			super(structurePiece, compoundTag);
			this.mineshaftType = MineshaftFeature.Type.byIndex(compoundTag.getInt("MST"));
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			compoundTag.putInt("MST", this.mineshaftType.ordinal());
		}

		protected BlockState method_16443() {
			switch (this.mineshaftType) {
				case NORMAL:
				default:
					return Blocks.field_10161.getDefaultState();
				case MESA:
					return Blocks.field_10075.getDefaultState();
			}
		}

		protected BlockState method_14718() {
			switch (this.mineshaftType) {
				case NORMAL:
				default:
					return Blocks.field_10620.getDefaultState();
				case MESA:
					return Blocks.field_10132.getDefaultState();
			}
		}

		protected boolean method_14719(BlockView blockView, MutableIntBoundingBox mutableIntBoundingBox, int i, int j, int k, int l) {
			for (int m = i; m <= j; m++) {
				if (this.getBlockAt(blockView, m, k + 1, l, mutableIntBoundingBox).isAir()) {
					return false;
				}
			}

			return true;
		}
	}

	public static class MineshaftRoom extends MineshaftGenerator.MineshaftPart {
		private final List<MutableIntBoundingBox> field_14422 = Lists.<MutableIntBoundingBox>newLinkedList();

		public MineshaftRoom(int i, Random random, int j, int k, MineshaftFeature.Type type) {
			super(StructurePiece.field_16915, i, type);
			this.mineshaftType = type;
			this.structureBounds = new MutableIntBoundingBox(j, 50, k, j + 7 + random.nextInt(6), 54 + random.nextInt(6), k + 7 + random.nextInt(6));
		}

		public MineshaftRoom(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16915, compoundTag);
			ListTag listTag = compoundTag.getList("Entrances", 11);

			for (int i = 0; i < listTag.size(); i++) {
				this.field_14422.add(new MutableIntBoundingBox(listTag.getIntArray(i)));
			}
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			int i = this.method_14923();
			int j = this.structureBounds.getBlockCountY() - 3 - 1;
			if (j <= 0) {
				j = 1;
			}

			int k = 0;

			while (k < this.structureBounds.getBlockCountX()) {
				k += random.nextInt(this.structureBounds.getBlockCountX());
				if (k + 3 > this.structureBounds.getBlockCountX()) {
					break;
				}

				MineshaftGenerator.MineshaftPart mineshaftPart = MineshaftGenerator.method_14711(
					arg, list, random, this.structureBounds.minX + k, this.structureBounds.minY + random.nextInt(j) + 1, this.structureBounds.minZ - 1, Direction.NORTH, i
				);
				if (mineshaftPart != null) {
					MutableIntBoundingBox mutableIntBoundingBox = mineshaftPart.method_14935();
					this.field_14422
						.add(
							new MutableIntBoundingBox(
								mutableIntBoundingBox.minX,
								mutableIntBoundingBox.minY,
								this.structureBounds.minZ,
								mutableIntBoundingBox.maxX,
								mutableIntBoundingBox.maxY,
								this.structureBounds.minZ + 1
							)
						);
				}

				k += 4;
			}

			k = 0;

			while (k < this.structureBounds.getBlockCountX()) {
				k += random.nextInt(this.structureBounds.getBlockCountX());
				if (k + 3 > this.structureBounds.getBlockCountX()) {
					break;
				}

				MineshaftGenerator.MineshaftPart mineshaftPart = MineshaftGenerator.method_14711(
					arg, list, random, this.structureBounds.minX + k, this.structureBounds.minY + random.nextInt(j) + 1, this.structureBounds.maxZ + 1, Direction.SOUTH, i
				);
				if (mineshaftPart != null) {
					MutableIntBoundingBox mutableIntBoundingBox = mineshaftPart.method_14935();
					this.field_14422
						.add(
							new MutableIntBoundingBox(
								mutableIntBoundingBox.minX,
								mutableIntBoundingBox.minY,
								this.structureBounds.maxZ - 1,
								mutableIntBoundingBox.maxX,
								mutableIntBoundingBox.maxY,
								this.structureBounds.maxZ
							)
						);
				}

				k += 4;
			}

			k = 0;

			while (k < this.structureBounds.getBlockCountZ()) {
				k += random.nextInt(this.structureBounds.getBlockCountZ());
				if (k + 3 > this.structureBounds.getBlockCountZ()) {
					break;
				}

				MineshaftGenerator.MineshaftPart mineshaftPart = MineshaftGenerator.method_14711(
					arg, list, random, this.structureBounds.minX - 1, this.structureBounds.minY + random.nextInt(j) + 1, this.structureBounds.minZ + k, Direction.WEST, i
				);
				if (mineshaftPart != null) {
					MutableIntBoundingBox mutableIntBoundingBox = mineshaftPart.method_14935();
					this.field_14422
						.add(
							new MutableIntBoundingBox(
								this.structureBounds.minX,
								mutableIntBoundingBox.minY,
								mutableIntBoundingBox.minZ,
								this.structureBounds.minX + 1,
								mutableIntBoundingBox.maxY,
								mutableIntBoundingBox.maxZ
							)
						);
				}

				k += 4;
			}

			k = 0;

			while (k < this.structureBounds.getBlockCountZ()) {
				k += random.nextInt(this.structureBounds.getBlockCountZ());
				if (k + 3 > this.structureBounds.getBlockCountZ()) {
					break;
				}

				class_3443 lv = MineshaftGenerator.method_14711(
					arg, list, random, this.structureBounds.maxX + 1, this.structureBounds.minY + random.nextInt(j) + 1, this.structureBounds.minZ + k, Direction.EAST, i
				);
				if (lv != null) {
					MutableIntBoundingBox mutableIntBoundingBox = lv.method_14935();
					this.field_14422
						.add(
							new MutableIntBoundingBox(
								this.structureBounds.maxX - 1,
								mutableIntBoundingBox.minY,
								mutableIntBoundingBox.minZ,
								this.structureBounds.maxX,
								mutableIntBoundingBox.maxY,
								mutableIntBoundingBox.maxZ
							)
						);
				}

				k += 4;
			}
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			if (this.method_14937(iWorld, mutableIntBoundingBox)) {
				return false;
			} else {
				this.fillWithOutline(
					iWorld,
					mutableIntBoundingBox,
					this.structureBounds.minX,
					this.structureBounds.minY,
					this.structureBounds.minZ,
					this.structureBounds.maxX,
					this.structureBounds.minY,
					this.structureBounds.maxZ,
					Blocks.field_10566.getDefaultState(),
					blockAir,
					true
				);
				this.fillWithOutline(
					iWorld,
					mutableIntBoundingBox,
					this.structureBounds.minX,
					this.structureBounds.minY + 1,
					this.structureBounds.minZ,
					this.structureBounds.maxX,
					Math.min(this.structureBounds.minY + 3, this.structureBounds.maxY),
					this.structureBounds.maxZ,
					blockAir,
					blockAir,
					false
				);

				for (MutableIntBoundingBox mutableIntBoundingBox2 : this.field_14422) {
					this.fillWithOutline(
						iWorld,
						mutableIntBoundingBox,
						mutableIntBoundingBox2.minX,
						mutableIntBoundingBox2.maxY - 2,
						mutableIntBoundingBox2.minZ,
						mutableIntBoundingBox2.maxX,
						mutableIntBoundingBox2.maxY,
						mutableIntBoundingBox2.maxZ,
						blockAir,
						blockAir,
						false
					);
				}

				this.method_14919(
					iWorld,
					mutableIntBoundingBox,
					this.structureBounds.minX,
					this.structureBounds.minY + 4,
					this.structureBounds.minZ,
					this.structureBounds.maxX,
					this.structureBounds.maxY,
					this.structureBounds.maxZ,
					blockAir,
					false
				);
				return true;
			}
		}

		@Override
		public void translate(int i, int j, int k) {
			super.translate(i, j, k);

			for (MutableIntBoundingBox mutableIntBoundingBox : this.field_14422) {
				mutableIntBoundingBox.translate(i, j, k);
			}
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			ListTag listTag = new ListTag();

			for (MutableIntBoundingBox mutableIntBoundingBox : this.field_14422) {
				listTag.add(mutableIntBoundingBox.toNbt());
			}

			compoundTag.put("Entrances", listTag);
		}
	}

	public static class MineshaftStairs extends MineshaftGenerator.MineshaftPart {
		public MineshaftStairs(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction, MineshaftFeature.Type type) {
			super(StructurePiece.field_16968, i, type);
			this.method_14926(direction);
			this.structureBounds = mutableIntBoundingBox;
		}

		public MineshaftStairs(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePiece.field_16968, compoundTag);
		}

		public static MutableIntBoundingBox method_14720(List<class_3443> list, Random random, int i, int j, int k, Direction direction) {
			MutableIntBoundingBox mutableIntBoundingBox = new MutableIntBoundingBox(i, j - 5, k, i, j + 3 - 1, k);
			switch (direction) {
				case NORTH:
				default:
					mutableIntBoundingBox.maxX = i + 3 - 1;
					mutableIntBoundingBox.minZ = k - 8;
					break;
				case SOUTH:
					mutableIntBoundingBox.maxX = i + 3 - 1;
					mutableIntBoundingBox.maxZ = k + 8;
					break;
				case WEST:
					mutableIntBoundingBox.minX = i - 8;
					mutableIntBoundingBox.maxZ = k + 3 - 1;
					break;
				case EAST:
					mutableIntBoundingBox.maxX = i + 8;
					mutableIntBoundingBox.maxZ = k + 3 - 1;
			}

			return class_3443.method_14932(list, mutableIntBoundingBox) != null ? null : mutableIntBoundingBox;
		}

		@Override
		public void method_14918(class_3443 arg, List<class_3443> list, Random random) {
			int i = this.method_14923();
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
					default:
						MineshaftGenerator.method_14711(
							arg, list, random, this.structureBounds.minX, this.structureBounds.minY, this.structureBounds.minZ - 1, Direction.NORTH, i
						);
						break;
					case SOUTH:
						MineshaftGenerator.method_14711(
							arg, list, random, this.structureBounds.minX, this.structureBounds.minY, this.structureBounds.maxZ + 1, Direction.SOUTH, i
						);
						break;
					case WEST:
						MineshaftGenerator.method_14711(arg, list, random, this.structureBounds.minX - 1, this.structureBounds.minY, this.structureBounds.minZ, Direction.WEST, i);
						break;
					case EAST:
						MineshaftGenerator.method_14711(arg, list, random, this.structureBounds.maxX + 1, this.structureBounds.minY, this.structureBounds.minZ, Direction.EAST, i);
				}
			}
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			if (this.method_14937(iWorld, mutableIntBoundingBox)) {
				return false;
			} else {
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 0, 2, 7, 1, blockAir, blockAir, false);
				this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 7, 2, 2, 8, blockAir, blockAir, false);

				for (int i = 0; i < 5; i++) {
					this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, blockAir, blockAir, false);
				}

				return true;
			}
		}
	}
}
