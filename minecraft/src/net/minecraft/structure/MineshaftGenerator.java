package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
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
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.MineshaftFeature;

public class MineshaftGenerator {
	private static MineshaftGenerator.MineshaftPart pickPiece(
		List<StructurePiece> pieces, Random random, int x, int y, int z, @Nullable Direction orientation, int chainLength, MineshaftFeature.Type type
	) {
		int i = random.nextInt(100);
		if (i >= 80) {
			BlockBox blockBox = MineshaftGenerator.MineshaftCrossing.getBoundingBox(pieces, random, x, y, z, orientation);
			if (blockBox != null) {
				return new MineshaftGenerator.MineshaftCrossing(chainLength, blockBox, orientation, type);
			}
		} else if (i >= 70) {
			BlockBox blockBox = MineshaftGenerator.MineshaftStairs.getBoundingBox(pieces, random, x, y, z, orientation);
			if (blockBox != null) {
				return new MineshaftGenerator.MineshaftStairs(chainLength, blockBox, orientation, type);
			}
		} else {
			BlockBox blockBox = MineshaftGenerator.MineshaftCorridor.getBoundingBox(pieces, random, x, y, z, orientation);
			if (blockBox != null) {
				return new MineshaftGenerator.MineshaftCorridor(chainLength, random, blockBox, orientation, type);
			}
		}

		return null;
	}

	private static MineshaftGenerator.MineshaftPart pieceGenerator(
		StructurePiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
	) {
		if (chainLength > 8) {
			return null;
		} else if (Math.abs(x - start.getBoundingBox().minX) <= 80 && Math.abs(z - start.getBoundingBox().minZ) <= 80) {
			MineshaftFeature.Type type = ((MineshaftGenerator.MineshaftPart)start).mineshaftType;
			MineshaftGenerator.MineshaftPart mineshaftPart = pickPiece(pieces, random, x, y, z, orientation, chainLength + 1, type);
			if (mineshaftPart != null) {
				pieces.add(mineshaftPart);
				mineshaftPart.fillOpenings(start, pieces, random);
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

		public MineshaftCorridor(StructureManager structureManager, CompoundTag nbt) {
			super(StructurePieceType.MINESHAFT_CORRIDOR, nbt);
			this.hasRails = nbt.getBoolean("hr");
			this.hasCobwebs = nbt.getBoolean("sc");
			this.hasSpawner = nbt.getBoolean("hps");
			this.length = nbt.getInt("Num");
		}

		@Override
		protected void writeNbt(CompoundTag tag) {
			super.writeNbt(tag);
			tag.putBoolean("hr", this.hasRails);
			tag.putBoolean("sc", this.hasCobwebs);
			tag.putBoolean("hps", this.hasSpawner);
			tag.putInt("Num", this.length);
		}

		public MineshaftCorridor(int chainLength, Random random, BlockBox boundingBox, Direction orientation, MineshaftFeature.Type type) {
			super(StructurePieceType.MINESHAFT_CORRIDOR, chainLength, type);
			this.setOrientation(orientation);
			this.boundingBox = boundingBox;
			this.hasRails = random.nextInt(3) == 0;
			this.hasCobwebs = !this.hasRails && random.nextInt(23) == 0;
			if (this.getFacing().getAxis() == Direction.Axis.Z) {
				this.length = boundingBox.getBlockCountZ() / 5;
			} else {
				this.length = boundingBox.getBlockCountX() / 5;
			}
		}

		public static BlockBox getBoundingBox(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation) {
			BlockBox blockBox = new BlockBox(x, y, z, x, y + 3 - 1, z);

			int i;
			for (i = random.nextInt(3) + 2; i > 0; i--) {
				int j = i * 5;
				switch (orientation) {
					case NORTH:
					default:
						blockBox.maxX = x + 3 - 1;
						blockBox.minZ = z - (j - 1);
						break;
					case SOUTH:
						blockBox.maxX = x + 3 - 1;
						blockBox.maxZ = z + j - 1;
						break;
					case WEST:
						blockBox.minX = x - (j - 1);
						blockBox.maxZ = z + 3 - 1;
						break;
					case EAST:
						blockBox.maxX = x + j - 1;
						blockBox.maxZ = z + 3 - 1;
				}

				if (StructurePiece.getOverlappingPiece(pieces, blockBox) == null) {
					break;
				}
			}

			return i > 0 ? blockBox : null;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			int i = this.getChainLength();
			int j = random.nextInt(4);
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
					default:
						if (j <= 1) {
							MineshaftGenerator.pieceGenerator(
								start, pieces, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, direction, i
							);
						} else if (j == 2) {
							MineshaftGenerator.pieceGenerator(
								start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, Direction.WEST, i
							);
						} else {
							MineshaftGenerator.pieceGenerator(
								start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, Direction.EAST, i
							);
						}
						break;
					case SOUTH:
						if (j <= 1) {
							MineshaftGenerator.pieceGenerator(
								start, pieces, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, direction, i
							);
						} else if (j == 2) {
							MineshaftGenerator.pieceGenerator(
								start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ - 3, Direction.WEST, i
							);
						} else {
							MineshaftGenerator.pieceGenerator(
								start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ - 3, Direction.EAST, i
							);
						}
						break;
					case WEST:
						if (j <= 1) {
							MineshaftGenerator.pieceGenerator(
								start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, direction, i
							);
						} else if (j == 2) {
							MineshaftGenerator.pieceGenerator(
								start, pieces, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, Direction.NORTH, i
							);
						} else {
							MineshaftGenerator.pieceGenerator(
								start, pieces, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, Direction.SOUTH, i
							);
						}
						break;
					case EAST:
						if (j <= 1) {
							MineshaftGenerator.pieceGenerator(
								start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, direction, i
							);
						} else if (j == 2) {
							MineshaftGenerator.pieceGenerator(
								start, pieces, random, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, Direction.NORTH, i
							);
						} else {
							MineshaftGenerator.pieceGenerator(
								start, pieces, random, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, Direction.SOUTH, i
							);
						}
				}
			}

			if (i < 8) {
				if (direction != Direction.NORTH && direction != Direction.SOUTH) {
					for (int k = this.boundingBox.minX + 3; k + 3 <= this.boundingBox.maxX; k += 5) {
						int l = random.nextInt(5);
						if (l == 0) {
							MineshaftGenerator.pieceGenerator(start, pieces, random, k, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i + 1);
						} else if (l == 1) {
							MineshaftGenerator.pieceGenerator(start, pieces, random, k, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i + 1);
						}
					}
				} else {
					for (int kx = this.boundingBox.minZ + 3; kx + 3 <= this.boundingBox.maxZ; kx += 5) {
						int l = random.nextInt(5);
						if (l == 0) {
							MineshaftGenerator.pieceGenerator(start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, kx, Direction.WEST, i + 1);
						} else if (l == 1) {
							MineshaftGenerator.pieceGenerator(start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, kx, Direction.EAST, i + 1);
						}
					}
				}
			}
		}

		@Override
		protected boolean addChest(StructureWorldAccess world, BlockBox boundingBox, Random random, int x, int y, int z, Identifier lootTableId) {
			BlockPos blockPos = new BlockPos(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
			if (boundingBox.contains(blockPos) && world.getBlockState(blockPos).isAir() && !world.getBlockState(blockPos.down()).isAir()) {
				BlockState blockState = Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, random.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
				this.addBlock(world, blockState, x, y, z, boundingBox);
				ChestMinecartEntity chestMinecartEntity = new ChestMinecartEntity(
					world.toServerWorld(), (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5
				);
				chestMinecartEntity.setLootTable(lootTableId, random.nextLong());
				world.spawnEntity(chestMinecartEntity);
				return true;
			} else {
				return false;
			}
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
			if (this.method_33999(world, boundingBox)) {
				return false;
			} else {
				int i = 0;
				int j = 2;
				int k = 0;
				int l = 2;
				int m = this.length * 5 - 1;
				BlockState blockState = this.mineshaftType.getPlanks();
				this.fillWithOutline(world, boundingBox, 0, 0, 0, 2, 1, m, AIR, AIR, false);
				this.fillWithOutlineUnderSeaLevel(world, boundingBox, random, 0.8F, 0, 2, 0, 2, 2, m, AIR, AIR, false, false);
				if (this.hasCobwebs) {
					this.fillWithOutlineUnderSeaLevel(world, boundingBox, random, 0.6F, 0, 0, 0, 2, 1, m, Blocks.COBWEB.getDefaultState(), AIR, false, true);
				}

				for (int n = 0; n < this.length; n++) {
					int o = 2 + n * 5;
					this.generateSupports(world, boundingBox, 0, 0, o, 2, 2, random);
					this.addCobwebsUnderground(world, boundingBox, random, 0.1F, 0, 2, o - 1);
					this.addCobwebsUnderground(world, boundingBox, random, 0.1F, 2, 2, o - 1);
					this.addCobwebsUnderground(world, boundingBox, random, 0.1F, 0, 2, o + 1);
					this.addCobwebsUnderground(world, boundingBox, random, 0.1F, 2, 2, o + 1);
					this.addCobwebsUnderground(world, boundingBox, random, 0.05F, 0, 2, o - 2);
					this.addCobwebsUnderground(world, boundingBox, random, 0.05F, 2, 2, o - 2);
					this.addCobwebsUnderground(world, boundingBox, random, 0.05F, 0, 2, o + 2);
					this.addCobwebsUnderground(world, boundingBox, random, 0.05F, 2, 2, o + 2);
					if (random.nextInt(100) == 0) {
						this.addChest(world, boundingBox, random, 2, 0, o - 1, LootTables.ABANDONED_MINESHAFT_CHEST);
					}

					if (random.nextInt(100) == 0) {
						this.addChest(world, boundingBox, random, 0, 0, o + 1, LootTables.ABANDONED_MINESHAFT_CHEST);
					}

					if (this.hasCobwebs && !this.hasSpawner) {
						int p = this.applyYTransform(0);
						int q = o - 1 + random.nextInt(3);
						int r = this.applyXTransform(1, q);
						int s = this.applyZTransform(1, q);
						BlockPos blockPos = new BlockPos(r, p, s);
						if (boundingBox.contains(blockPos) && this.isUnderSeaLevel(world, 1, 0, q, boundingBox)) {
							this.hasSpawner = true;
							world.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 2);
							BlockEntity blockEntity = world.getBlockEntity(blockPos);
							if (blockEntity instanceof MobSpawnerBlockEntity) {
								((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(EntityType.CAVE_SPIDER);
							}
						}
					}
				}

				for (int n = 0; n <= 2; n++) {
					for (int ox = 0; ox <= m; ox++) {
						this.method_33880(world, boundingBox, blockState, n, -1, ox);
					}
				}

				int n = 2;
				this.fillSupportBeam(world, boundingBox, 0, -1, 2);
				if (this.length > 1) {
					int ox = m - 2;
					this.fillSupportBeam(world, boundingBox, 0, -1, ox);
				}

				if (this.hasRails) {
					BlockState blockState2 = Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, RailShape.NORTH_SOUTH);

					for (int p = 0; p <= m; p++) {
						BlockState blockState3 = this.getBlockAt(world, 1, -1, p, boundingBox);
						if (!blockState3.isAir()
							&& blockState3.isOpaqueFullCube(world, new BlockPos(this.applyXTransform(1, p), this.applyYTransform(-1), this.applyZTransform(1, p)))) {
							float f = this.isUnderSeaLevel(world, 1, 0, p, boundingBox) ? 0.7F : 0.9F;
							this.addBlockWithRandomThreshold(world, boundingBox, random, f, 1, 0, p, blockState2, false);
						}
					}
				}

				return true;
			}
		}

		private void fillSupportBeam(StructureWorldAccess world, BlockBox box, int x, int y, int z) {
			BlockState blockState = this.mineshaftType.getWood();
			BlockState blockState2 = this.mineshaftType.getPlanks();
			if (this.getBlockAt(world, x, y, z, box).isOf(blockState2.getBlock())) {
				this.method_33879(world, blockState, x, y, z, box);
			}

			if (this.getBlockAt(world, x + 2, y, z, box).isOf(blockState2.getBlock())) {
				this.method_33879(world, blockState, x + 2, y, z, box);
			}
		}

		@Override
		protected void fillDownwards(StructureWorldAccess world, BlockState state, int x, int y, int z, BlockBox box) {
			int i = this.applyXTransform(x, z);
			int j = this.applyYTransform(y);
			int k = this.applyZTransform(x, z);
			BlockPos.Mutable mutable = new BlockPos.Mutable(i, j, k);
			if (box.contains(mutable)) {
				while (this.method_33881(world.getBlockState(mutable)) && mutable.getY() > world.getBottomY() + 1) {
					mutable.move(Direction.DOWN);
				}

				if (this.isNotRailOrLava(world.getBlockState(mutable))) {
					while (mutable.getY() < j) {
						mutable.move(Direction.UP);
						world.setBlockState(mutable, state, 2);
					}
				}
			}
		}

		protected void method_33879(StructureWorldAccess structureWorldAccess, BlockState blockState, int i, int j, int k, BlockBox blockBox) {
			int l = this.applyXTransform(i, k);
			int m = this.applyYTransform(j);
			int n = this.applyZTransform(i, k);
			BlockPos.Mutable mutable = new BlockPos.Mutable(l, m, n);
			if (blockBox.contains(mutable)) {
				int o = 1;
				boolean bl = true;

				for (boolean bl2 = true; bl || bl2; o++) {
					if (bl) {
						mutable.setY(m - o);
						BlockState blockState2 = structureWorldAccess.getBlockState(mutable);
						boolean bl3 = this.method_33881(blockState2);
						if (!bl3 && this.isNotRailOrLava(blockState2)) {
							method_33878(structureWorldAccess, blockState, mutable, m - o + 1, m);
							return;
						}

						bl = o <= 10 && bl3 && mutable.getY() > structureWorldAccess.getBottomY() + 1;
					}

					if (bl2) {
						mutable.setY(m + o);
						BlockState blockState2 = structureWorldAccess.getBlockState(mutable);
						boolean bl3 = this.method_33881(blockState2);
						if (!bl3 && this.method_33877(structureWorldAccess, mutable, blockState2)) {
							structureWorldAccess.setBlockState(mutable.setY(m + 1), this.mineshaftType.getFence(), 2);
							method_33878(structureWorldAccess, Blocks.CHAIN.getDefaultState(), mutable, m + 2, m + o);
							return;
						}

						bl2 = o <= 20 && bl3 && mutable.getY() < structureWorldAccess.getTopY() - 1;
					}
				}
			}
		}

		private static void method_33878(StructureWorldAccess structureWorldAccess, BlockState blockState, BlockPos.Mutable mutable, int i, int j) {
			for (int k = i; k < j; k++) {
				structureWorldAccess.setBlockState(mutable.setY(k), blockState, 2);
			}
		}

		private boolean isNotRailOrLava(BlockState state) {
			return !state.isOf(Blocks.RAIL) && !state.isOf(Blocks.LAVA);
		}

		private boolean method_33877(WorldView worldView, BlockPos blockPos, BlockState blockState) {
			return Block.sideCoversSmallSquare(worldView, blockPos, Direction.DOWN) && !(blockState.getBlock() instanceof FallingBlock);
		}

		private void generateSupports(StructureWorldAccess world, BlockBox boundingBox, int minX, int minY, int z, int maxY, int maxX, Random random) {
			if (this.isSolidCeiling(world, boundingBox, minX, maxX, maxY, z)) {
				BlockState blockState = this.mineshaftType.getPlanks();
				BlockState blockState2 = this.mineshaftType.getFence();
				this.fillWithOutline(world, boundingBox, minX, minY, z, minX, maxY - 1, z, blockState2.with(FenceBlock.WEST, Boolean.valueOf(true)), AIR, false);
				this.fillWithOutline(world, boundingBox, maxX, minY, z, maxX, maxY - 1, z, blockState2.with(FenceBlock.EAST, Boolean.valueOf(true)), AIR, false);
				if (random.nextInt(4) == 0) {
					this.fillWithOutline(world, boundingBox, minX, maxY, z, minX, maxY, z, blockState, AIR, false);
					this.fillWithOutline(world, boundingBox, maxX, maxY, z, maxX, maxY, z, blockState, AIR, false);
				} else {
					this.fillWithOutline(world, boundingBox, minX, maxY, z, maxX, maxY, z, blockState, AIR, false);
					this.addBlockWithRandomThreshold(
						world, boundingBox, random, 0.05F, minX + 1, maxY, z - 1, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.NORTH), false
					);
					this.addBlockWithRandomThreshold(
						world, boundingBox, random, 0.05F, minX + 1, maxY, z + 1, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH), false
					);
				}
			}
		}

		private void addCobwebsUnderground(StructureWorldAccess world, BlockBox boundingBox, Random random, float threshold, int x, int y, int z) {
			if (this.isUnderSeaLevel(world, x, y, z, boundingBox)) {
				this.addBlockWithRandomThreshold(world, boundingBox, random, threshold, x, y, z, Blocks.COBWEB.getDefaultState(), true);
			}
		}
	}

	public static class MineshaftCrossing extends MineshaftGenerator.MineshaftPart {
		private final Direction direction;
		private final boolean twoFloors;

		public MineshaftCrossing(StructureManager structureManager, CompoundTag nbt) {
			super(StructurePieceType.MINESHAFT_CROSSING, nbt);
			this.twoFloors = nbt.getBoolean("tf");
			this.direction = Direction.fromHorizontal(nbt.getInt("D"));
		}

		@Override
		protected void writeNbt(CompoundTag tag) {
			super.writeNbt(tag);
			tag.putBoolean("tf", this.twoFloors);
			tag.putInt("D", this.direction.getHorizontal());
		}

		public MineshaftCrossing(int chainLength, BlockBox boundingBox, @Nullable Direction orientation, MineshaftFeature.Type type) {
			super(StructurePieceType.MINESHAFT_CROSSING, chainLength, type);
			this.direction = orientation;
			this.boundingBox = boundingBox;
			this.twoFloors = boundingBox.getBlockCountY() > 3;
		}

		public static BlockBox getBoundingBox(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation) {
			BlockBox blockBox = new BlockBox(x, y, z, x, y + 3 - 1, z);
			if (random.nextInt(4) == 0) {
				blockBox.maxY += 4;
			}

			switch (orientation) {
				case NORTH:
				default:
					blockBox.minX = x - 1;
					blockBox.maxX = x + 3;
					blockBox.minZ = z - 4;
					break;
				case SOUTH:
					blockBox.minX = x - 1;
					blockBox.maxX = x + 3;
					blockBox.maxZ = z + 3 + 1;
					break;
				case WEST:
					blockBox.minX = x - 4;
					blockBox.minZ = z - 1;
					blockBox.maxZ = z + 3;
					break;
				case EAST:
					blockBox.maxX = x + 3 + 1;
					blockBox.minZ = z - 1;
					blockBox.maxZ = z + 3;
			}

			return StructurePiece.getOverlappingPiece(pieces, blockBox) != null ? null : blockBox;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			int i = this.getChainLength();
			switch (this.direction) {
				case NORTH:
				default:
					MineshaftGenerator.pieceGenerator(start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
					MineshaftGenerator.pieceGenerator(start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
					MineshaftGenerator.pieceGenerator(start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
					break;
				case SOUTH:
					MineshaftGenerator.pieceGenerator(start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
					MineshaftGenerator.pieceGenerator(start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
					MineshaftGenerator.pieceGenerator(start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
					break;
				case WEST:
					MineshaftGenerator.pieceGenerator(start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
					MineshaftGenerator.pieceGenerator(start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
					MineshaftGenerator.pieceGenerator(start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
					break;
				case EAST:
					MineshaftGenerator.pieceGenerator(start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
					MineshaftGenerator.pieceGenerator(start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
					MineshaftGenerator.pieceGenerator(start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
			}

			if (this.twoFloors) {
				if (random.nextBoolean()) {
					MineshaftGenerator.pieceGenerator(
						start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, Direction.NORTH, i
					);
				}

				if (random.nextBoolean()) {
					MineshaftGenerator.pieceGenerator(
						start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.WEST, i
					);
				}

				if (random.nextBoolean()) {
					MineshaftGenerator.pieceGenerator(
						start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.EAST, i
					);
				}

				if (random.nextBoolean()) {
					MineshaftGenerator.pieceGenerator(
						start, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, Direction.SOUTH, i
					);
				}
			}
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
			if (this.method_33999(world, boundingBox)) {
				return false;
			} else {
				BlockState blockState = this.mineshaftType.getPlanks();
				if (this.twoFloors) {
					this.fillWithOutline(
						world,
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
						world,
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
						world,
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
						world,
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
						world,
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
						world,
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
						world,
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

				this.generateCrossingPillar(world, boundingBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
				this.generateCrossingPillar(world, boundingBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);
				this.generateCrossingPillar(world, boundingBox, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
				this.generateCrossingPillar(world, boundingBox, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);
				int i = this.boundingBox.minY - 1;

				for (int j = this.boundingBox.minX; j <= this.boundingBox.maxX; j++) {
					for (int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; k++) {
						this.method_33880(world, boundingBox, blockState, j, i, k);
					}
				}

				return true;
			}
		}

		private void generateCrossingPillar(StructureWorldAccess world, BlockBox boundingBox, int x, int minY, int z, int maxY) {
			if (!this.getBlockAt(world, x, maxY + 1, z, boundingBox).isAir()) {
				this.fillWithOutline(world, boundingBox, x, minY, z, x, maxY, z, this.mineshaftType.getPlanks(), AIR, false);
			}
		}
	}

	abstract static class MineshaftPart extends StructurePiece {
		protected MineshaftFeature.Type mineshaftType;

		public MineshaftPart(StructurePieceType structurePieceType, int chainLength, MineshaftFeature.Type type) {
			super(structurePieceType, chainLength);
			this.mineshaftType = type;
		}

		public MineshaftPart(StructurePieceType structurePieceType, CompoundTag compoundTag) {
			super(structurePieceType, compoundTag);
			this.mineshaftType = MineshaftFeature.Type.byIndex(compoundTag.getInt("MST"));
		}

		@Override
		protected boolean canAddBlock(WorldView world, int x, int y, int z, BlockBox box) {
			BlockState blockState = this.getBlockAt(world, x, y, z, box);
			return !blockState.isOf(this.mineshaftType.getPlanks().getBlock())
				&& !blockState.isOf(this.mineshaftType.getWood().getBlock())
				&& !blockState.isOf(this.mineshaftType.getFence().getBlock())
				&& !blockState.isOf(Blocks.CHAIN);
		}

		@Override
		protected void writeNbt(CompoundTag tag) {
			tag.putInt("MST", this.mineshaftType.ordinal());
		}

		protected boolean isSolidCeiling(BlockView world, BlockBox boundingBox, int minX, int maxX, int y, int z) {
			for (int i = minX; i <= maxX; i++) {
				if (this.getBlockAt(world, i, y + 1, z, boundingBox).isAir()) {
					return false;
				}
			}

			return true;
		}

		protected boolean method_33999(BlockView blockView, BlockBox blockBox) {
			int i = Math.max(this.boundingBox.minX - 1, blockBox.minX);
			int j = Math.max(this.boundingBox.minY - 1, blockBox.minY);
			int k = Math.max(this.boundingBox.minZ - 1, blockBox.minZ);
			int l = Math.min(this.boundingBox.maxX + 1, blockBox.maxX);
			int m = Math.min(this.boundingBox.maxY + 1, blockBox.maxY);
			int n = Math.min(this.boundingBox.maxZ + 1, blockBox.maxZ);
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int o = i; o <= l; o++) {
				for (int p = k; p <= n; p++) {
					if (blockView.getBlockState(mutable.set(o, j, p)).getMaterial().isLiquid()) {
						return true;
					}

					if (blockView.getBlockState(mutable.set(o, m, p)).getMaterial().isLiquid()) {
						return true;
					}
				}
			}

			for (int o = i; o <= l; o++) {
				for (int p = j; p <= m; p++) {
					if (blockView.getBlockState(mutable.set(o, p, k)).getMaterial().isLiquid()) {
						return true;
					}

					if (blockView.getBlockState(mutable.set(o, p, n)).getMaterial().isLiquid()) {
						return true;
					}
				}
			}

			for (int o = k; o <= n; o++) {
				for (int p = j; p <= m; p++) {
					if (blockView.getBlockState(mutable.set(i, p, o)).getMaterial().isLiquid()) {
						return true;
					}

					if (blockView.getBlockState(mutable.set(l, p, o)).getMaterial().isLiquid()) {
						return true;
					}
				}
			}

			return false;
		}

		protected void method_33880(StructureWorldAccess structureWorldAccess, BlockBox blockBox, BlockState blockState, int i, int j, int k) {
			if (this.isUnderSeaLevel(structureWorldAccess, i, j, k, blockBox)) {
				BlockPos blockPos = this.method_33781(i, j, k);
				BlockState blockState2 = structureWorldAccess.getBlockState(blockPos);
				if (blockState2.isAir() || blockState2.isOf(Blocks.CHAIN)) {
					structureWorldAccess.setBlockState(blockPos, blockState, 2);
				}
			}
		}
	}

	public static class MineshaftRoom extends MineshaftGenerator.MineshaftPart {
		private final List<BlockBox> entrances = Lists.<BlockBox>newLinkedList();

		public MineshaftRoom(int chainLength, Random random, int x, int z, MineshaftFeature.Type type) {
			super(StructurePieceType.MINESHAFT_ROOM, chainLength, type);
			this.mineshaftType = type;
			this.boundingBox = new BlockBox(x, 50, z, x + 7 + random.nextInt(6), 54 + random.nextInt(6), z + 7 + random.nextInt(6));
		}

		public MineshaftRoom(StructureManager structureManager, CompoundTag nbt) {
			super(StructurePieceType.MINESHAFT_ROOM, nbt);
			ListTag listTag = nbt.getList("Entrances", 11);

			for (int i = 0; i < listTag.size(); i++) {
				this.entrances.add(new BlockBox(listTag.getIntArray(i)));
			}
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			int i = this.getChainLength();
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

				MineshaftGenerator.MineshaftPart mineshaftPart = MineshaftGenerator.pieceGenerator(
					start, pieces, random, this.boundingBox.minX + k, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.minZ - 1, Direction.NORTH, i
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

				MineshaftGenerator.MineshaftPart mineshaftPart = MineshaftGenerator.pieceGenerator(
					start, pieces, random, this.boundingBox.minX + k, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.maxZ + 1, Direction.SOUTH, i
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

				MineshaftGenerator.MineshaftPart mineshaftPart = MineshaftGenerator.pieceGenerator(
					start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.minZ + k, Direction.WEST, i
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

				StructurePiece structurePiece = MineshaftGenerator.pieceGenerator(
					start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.minZ + k, Direction.EAST, i
				);
				if (structurePiece != null) {
					BlockBox blockBox = structurePiece.getBoundingBox();
					this.entrances.add(new BlockBox(this.boundingBox.maxX - 1, blockBox.minY, blockBox.minZ, this.boundingBox.maxX, blockBox.maxY, blockBox.maxZ));
				}

				k += 4;
			}
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
			if (this.method_33999(world, boundingBox)) {
				return false;
			} else {
				this.fillWithOutline(
					world,
					boundingBox,
					this.boundingBox.minX,
					this.boundingBox.minY,
					this.boundingBox.minZ,
					this.boundingBox.maxX,
					this.boundingBox.minY,
					this.boundingBox.maxZ,
					Blocks.DIRT.getDefaultState(),
					AIR,
					true
				);
				this.fillWithOutline(
					world,
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
					this.fillWithOutline(world, boundingBox, blockBox.minX, blockBox.maxY - 2, blockBox.minZ, blockBox.maxX, blockBox.maxY, blockBox.maxZ, AIR, AIR, false);
				}

				this.fillHalfEllipsoid(
					world,
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
				blockBox.move(x, y, z);
			}
		}

		@Override
		protected void writeNbt(CompoundTag tag) {
			super.writeNbt(tag);
			ListTag listTag = new ListTag();

			for (BlockBox blockBox : this.entrances) {
				listTag.add(blockBox.toNbt());
			}

			tag.put("Entrances", listTag);
		}
	}

	public static class MineshaftStairs extends MineshaftGenerator.MineshaftPart {
		public MineshaftStairs(int chainLength, BlockBox boundingBox, Direction orientation, MineshaftFeature.Type type) {
			super(StructurePieceType.MINESHAFT_STAIRS, chainLength, type);
			this.setOrientation(orientation);
			this.boundingBox = boundingBox;
		}

		public MineshaftStairs(StructureManager structureManager, CompoundTag nbt) {
			super(StructurePieceType.MINESHAFT_STAIRS, nbt);
		}

		public static BlockBox getBoundingBox(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation) {
			BlockBox blockBox = new BlockBox(x, y - 5, z, x, y + 3 - 1, z);
			switch (orientation) {
				case NORTH:
				default:
					blockBox.maxX = x + 3 - 1;
					blockBox.minZ = z - 8;
					break;
				case SOUTH:
					blockBox.maxX = x + 3 - 1;
					blockBox.maxZ = z + 8;
					break;
				case WEST:
					blockBox.minX = x - 8;
					blockBox.maxZ = z + 3 - 1;
					break;
				case EAST:
					blockBox.maxX = x + 8;
					blockBox.maxZ = z + 3 - 1;
			}

			return StructurePiece.getOverlappingPiece(pieces, blockBox) != null ? null : blockBox;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			int i = this.getChainLength();
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
					default:
						MineshaftGenerator.pieceGenerator(start, pieces, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
						break;
					case SOUTH:
						MineshaftGenerator.pieceGenerator(start, pieces, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
						break;
					case WEST:
						MineshaftGenerator.pieceGenerator(start, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.WEST, i);
						break;
					case EAST:
						MineshaftGenerator.pieceGenerator(start, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.EAST, i);
				}
			}
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
			if (this.method_33999(world, boundingBox)) {
				return false;
			} else {
				this.fillWithOutline(world, boundingBox, 0, 5, 0, 2, 7, 1, AIR, AIR, false);
				this.fillWithOutline(world, boundingBox, 0, 0, 7, 2, 2, 8, AIR, AIR, false);

				for (int i = 0; i < 5; i++) {
					this.fillWithOutline(world, boundingBox, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, AIR, AIR, false);
				}

				return true;
			}
		}
	}
}
