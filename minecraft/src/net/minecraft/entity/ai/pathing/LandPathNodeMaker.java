package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkCache;

public class LandPathNodeMaker extends PathNodeMaker {
	protected float waterPathNodeTypeWeight;

	@Override
	public void init(ChunkCache chunkCache, MobEntity mobEntity) {
		super.init(chunkCache, mobEntity);
		this.waterPathNodeTypeWeight = mobEntity.getPathfindingPenalty(PathNodeType.WATER);
	}

	@Override
	public void clear() {
		this.entity.setPathfindingPenalty(PathNodeType.WATER, this.waterPathNodeTypeWeight);
		super.clear();
	}

	@Override
	public PathNode getStart() {
		int i;
		if (this.canSwim() && this.entity.isInsideWater()) {
			i = MathHelper.floor(this.entity.getY());
			BlockPos.Mutable mutable = new BlockPos.Mutable(this.entity.getX(), (double)i, this.entity.getZ());

			for (BlockState blockState = this.field_20622.getBlockState(mutable);
				blockState.getBlock() == Blocks.WATER || blockState.getFluidState() == Fluids.WATER.getStill(false);
				blockState = this.field_20622.getBlockState(mutable)
			) {
				mutable.set(this.entity.getX(), (double)(++i), this.entity.getZ());
			}

			i--;
		} else if (this.entity.onGround) {
			i = MathHelper.floor(this.entity.getY() + 0.5);
		} else {
			BlockPos blockPos = new BlockPos(this.entity);

			while (
				(
						this.field_20622.getBlockState(blockPos).isAir()
							|| this.field_20622.getBlockState(blockPos).canPlaceAtSide(this.field_20622, blockPos, BlockPlacementEnvironment.LAND)
					)
					&& blockPos.getY() > 0
			) {
				blockPos = blockPos.method_10074();
			}

			i = blockPos.up().getY();
		}

		BlockPos blockPos = new BlockPos(this.entity);
		PathNodeType pathNodeType = this.getNodeType(this.entity, blockPos.getX(), i, blockPos.getZ());
		if (this.entity.getPathfindingPenalty(pathNodeType) < 0.0F) {
			Set<BlockPos> set = Sets.<BlockPos>newHashSet();
			set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)i, this.entity.getBoundingBox().minZ));
			set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)i, this.entity.getBoundingBox().maxZ));
			set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)i, this.entity.getBoundingBox().minZ));
			set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)i, this.entity.getBoundingBox().maxZ));

			for (BlockPos blockPos2 : set) {
				PathNodeType pathNodeType2 = this.getNodeType(this.entity, blockPos2);
				if (this.entity.getPathfindingPenalty(pathNodeType2) >= 0.0F) {
					return this.getNode(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
				}
			}
		}

		return this.getNode(blockPos.getX(), i, blockPos.getZ());
	}

	@Override
	public TargetPathNode getNode(double d, double e, double f) {
		return new TargetPathNode(this.getNode(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f)));
	}

	@Override
	public int getSuccessors(PathNode[] pathNodes, PathNode pathNode) {
		int i = 0;
		int j = 0;
		PathNodeType pathNodeType = this.getNodeType(this.entity, pathNode.x, pathNode.y + 1, pathNode.z);
		if (this.entity.getPathfindingPenalty(pathNodeType) >= 0.0F) {
			PathNodeType pathNodeType2 = this.getNodeType(this.entity, pathNode.x, pathNode.y, pathNode.z);
			if (pathNodeType2 == PathNodeType.STICKY_HONEY) {
				j = 0;
			} else {
				j = MathHelper.floor(Math.max(1.0F, this.entity.stepHeight));
			}
		}

		double d = getHeight(this.field_20622, new BlockPos(pathNode.x, pathNode.y, pathNode.z));
		PathNode pathNode2 = this.getPathNode(pathNode.x, pathNode.y, pathNode.z + 1, j, d, Direction.SOUTH);
		if (pathNode2 != null && !pathNode2.visited && pathNode2.penalty >= 0.0F) {
			pathNodes[i++] = pathNode2;
		}

		PathNode pathNode3 = this.getPathNode(pathNode.x - 1, pathNode.y, pathNode.z, j, d, Direction.WEST);
		if (pathNode3 != null && !pathNode3.visited && pathNode3.penalty >= 0.0F) {
			pathNodes[i++] = pathNode3;
		}

		PathNode pathNode4 = this.getPathNode(pathNode.x + 1, pathNode.y, pathNode.z, j, d, Direction.EAST);
		if (pathNode4 != null && !pathNode4.visited && pathNode4.penalty >= 0.0F) {
			pathNodes[i++] = pathNode4;
		}

		PathNode pathNode5 = this.getPathNode(pathNode.x, pathNode.y, pathNode.z - 1, j, d, Direction.NORTH);
		if (pathNode5 != null && !pathNode5.visited && pathNode5.penalty >= 0.0F) {
			pathNodes[i++] = pathNode5;
		}

		PathNode pathNode6 = this.getPathNode(pathNode.x - 1, pathNode.y, pathNode.z - 1, j, d, Direction.NORTH);
		if (this.isValidDiagonalSuccessor(pathNode, pathNode3, pathNode5, pathNode6)) {
			pathNodes[i++] = pathNode6;
		}

		PathNode pathNode7 = this.getPathNode(pathNode.x + 1, pathNode.y, pathNode.z - 1, j, d, Direction.NORTH);
		if (this.isValidDiagonalSuccessor(pathNode, pathNode4, pathNode5, pathNode7)) {
			pathNodes[i++] = pathNode7;
		}

		PathNode pathNode8 = this.getPathNode(pathNode.x - 1, pathNode.y, pathNode.z + 1, j, d, Direction.SOUTH);
		if (this.isValidDiagonalSuccessor(pathNode, pathNode3, pathNode2, pathNode8)) {
			pathNodes[i++] = pathNode8;
		}

		PathNode pathNode9 = this.getPathNode(pathNode.x + 1, pathNode.y, pathNode.z + 1, j, d, Direction.SOUTH);
		if (this.isValidDiagonalSuccessor(pathNode, pathNode4, pathNode2, pathNode9)) {
			pathNodes[i++] = pathNode9;
		}

		return i;
	}

	private boolean isValidDiagonalSuccessor(PathNode pathNode, @Nullable PathNode pathNode2, @Nullable PathNode pathNode3, @Nullable PathNode pathNode4) {
		if (pathNode4 == null || pathNode3 == null || pathNode2 == null) {
			return false;
		} else if (pathNode4.visited) {
			return false;
		} else {
			return pathNode3.y <= pathNode.y && pathNode2.y <= pathNode.y
				? pathNode4.penalty >= 0.0F && (pathNode3.y < pathNode.y || pathNode3.penalty >= 0.0F) && (pathNode2.y < pathNode.y || pathNode2.penalty >= 0.0F)
				: false;
		}
	}

	public static double getHeight(BlockView blockView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.method_10074();
		VoxelShape voxelShape = blockView.getBlockState(blockPos2).getCollisionShape(blockView, blockPos2);
		return (double)blockPos2.getY() + (voxelShape.isEmpty() ? 0.0 : voxelShape.getMaximum(Direction.Axis.Y));
	}

	@Nullable
	private PathNode getPathNode(int i, int j, int k, int l, double d, Direction direction) {
		PathNode pathNode = null;
		BlockPos blockPos = new BlockPos(i, j, k);
		double e = getHeight(this.field_20622, blockPos);
		if (e - d > 1.125) {
			return null;
		} else {
			PathNodeType pathNodeType = this.getNodeType(this.entity, i, j, k);
			float f = this.entity.getPathfindingPenalty(pathNodeType);
			double g = (double)this.entity.getWidth() / 2.0;
			if (f >= 0.0F) {
				pathNode = this.getNode(i, j, k);
				pathNode.type = pathNodeType;
				pathNode.penalty = Math.max(pathNode.penalty, f);
			}

			if (pathNodeType == PathNodeType.WALKABLE) {
				return pathNode;
			} else {
				if ((pathNode == null || pathNode.penalty < 0.0F) && l > 0 && pathNodeType != PathNodeType.FENCE && pathNodeType != PathNodeType.TRAPDOOR) {
					pathNode = this.getPathNode(i, j + 1, k, l - 1, d, direction);
					if (pathNode != null && (pathNode.type == PathNodeType.OPEN || pathNode.type == PathNodeType.WALKABLE) && this.entity.getWidth() < 1.0F) {
						double h = (double)(i - direction.getOffsetX()) + 0.5;
						double m = (double)(k - direction.getOffsetZ()) + 0.5;
						Box box = new Box(
							h - g,
							getHeight(this.field_20622, new BlockPos(h, (double)(j + 1), m)) + 0.001,
							m - g,
							h + g,
							(double)this.entity.getHeight() + getHeight(this.field_20622, new BlockPos(pathNode.x, pathNode.y, pathNode.z)) - 0.002,
							m + g
						);
						if (!this.field_20622.doesNotCollide(this.entity, box)) {
							pathNode = null;
						}
					}
				}

				if (pathNodeType == PathNodeType.WATER && !this.canSwim()) {
					if (this.getNodeType(this.entity, i, j - 1, k) != PathNodeType.WATER) {
						return pathNode;
					}

					while (j > 0) {
						pathNodeType = this.getNodeType(this.entity, i, --j, k);
						if (pathNodeType != PathNodeType.WATER) {
							return pathNode;
						}

						pathNode = this.getNode(i, j, k);
						pathNode.type = pathNodeType;
						pathNode.penalty = Math.max(pathNode.penalty, this.entity.getPathfindingPenalty(pathNodeType));
					}
				}

				if (pathNodeType == PathNodeType.OPEN) {
					Box box2 = new Box(
						(double)i - g + 0.5, (double)j + 0.001, (double)k - g + 0.5, (double)i + g + 0.5, (double)((float)j + this.entity.getHeight()), (double)k + g + 0.5
					);
					if (!this.field_20622.doesNotCollide(this.entity, box2)) {
						return null;
					}

					if (this.entity.getWidth() >= 1.0F) {
						PathNodeType pathNodeType2 = this.getNodeType(this.entity, i, j - 1, k);
						if (pathNodeType2 == PathNodeType.BLOCKED) {
							pathNode = this.getNode(i, j, k);
							pathNode.type = PathNodeType.WALKABLE;
							pathNode.penalty = Math.max(pathNode.penalty, f);
							return pathNode;
						}
					}

					int n = 0;
					int o = j;

					while (pathNodeType == PathNodeType.OPEN) {
						if (--j < 0) {
							PathNode pathNode2 = this.getNode(i, o, k);
							pathNode2.type = PathNodeType.BLOCKED;
							pathNode2.penalty = -1.0F;
							return pathNode2;
						}

						PathNode pathNode2 = this.getNode(i, j, k);
						if (n++ >= this.entity.getSafeFallDistance()) {
							pathNode2.type = PathNodeType.BLOCKED;
							pathNode2.penalty = -1.0F;
							return pathNode2;
						}

						pathNodeType = this.getNodeType(this.entity, i, j, k);
						f = this.entity.getPathfindingPenalty(pathNodeType);
						if (pathNodeType != PathNodeType.OPEN && f >= 0.0F) {
							pathNode = pathNode2;
							pathNode2.type = pathNodeType;
							pathNode2.penalty = Math.max(pathNode2.penalty, f);
							break;
						}

						if (f < 0.0F) {
							pathNode2.type = PathNodeType.BLOCKED;
							pathNode2.penalty = -1.0F;
							return pathNode2;
						}
					}
				}

				return pathNode;
			}
		}
	}

	@Override
	public PathNodeType getNodeType(BlockView blockView, int i, int j, int k, MobEntity mobEntity, int l, int m, int n, boolean bl, boolean bl2) {
		EnumSet<PathNodeType> enumSet = EnumSet.noneOf(PathNodeType.class);
		PathNodeType pathNodeType = PathNodeType.BLOCKED;
		double d = (double)mobEntity.getWidth() / 2.0;
		BlockPos blockPos = new BlockPos(mobEntity);
		pathNodeType = this.getNodeType(blockView, i, j, k, l, m, n, bl, bl2, enumSet, pathNodeType, blockPos);
		if (enumSet.contains(PathNodeType.FENCE)) {
			return PathNodeType.FENCE;
		} else {
			PathNodeType pathNodeType2 = PathNodeType.BLOCKED;

			for (PathNodeType pathNodeType3 : enumSet) {
				if (mobEntity.getPathfindingPenalty(pathNodeType3) < 0.0F) {
					return pathNodeType3;
				}

				if (mobEntity.getPathfindingPenalty(pathNodeType3) >= mobEntity.getPathfindingPenalty(pathNodeType2)) {
					pathNodeType2 = pathNodeType3;
				}
			}

			return pathNodeType == PathNodeType.OPEN && mobEntity.getPathfindingPenalty(pathNodeType2) == 0.0F ? PathNodeType.OPEN : pathNodeType2;
		}
	}

	public PathNodeType getNodeType(
		BlockView blockView,
		int i,
		int j,
		int k,
		int l,
		int m,
		int n,
		boolean bl,
		boolean bl2,
		EnumSet<PathNodeType> enumSet,
		PathNodeType pathNodeType,
		BlockPos blockPos
	) {
		for (int o = 0; o < l; o++) {
			for (int p = 0; p < m; p++) {
				for (int q = 0; q < n; q++) {
					int r = o + i;
					int s = p + j;
					int t = q + k;
					PathNodeType pathNodeType2 = this.getNodeType(blockView, r, s, t);
					pathNodeType2 = this.adjustNodeType(blockView, bl, bl2, blockPos, pathNodeType2);
					if (o == 0 && p == 0 && q == 0) {
						pathNodeType = pathNodeType2;
					}

					enumSet.add(pathNodeType2);
				}
			}
		}

		return pathNodeType;
	}

	protected PathNodeType adjustNodeType(BlockView blockView, boolean bl, boolean bl2, BlockPos blockPos, PathNodeType pathNodeType) {
		if (pathNodeType == PathNodeType.DOOR_WOOD_CLOSED && bl && bl2) {
			pathNodeType = PathNodeType.WALKABLE;
		}

		if (pathNodeType == PathNodeType.DOOR_OPEN && !bl2) {
			pathNodeType = PathNodeType.BLOCKED;
		}

		if (pathNodeType == PathNodeType.RAIL
			&& !(blockView.getBlockState(blockPos).getBlock() instanceof AbstractRailBlock)
			&& !(blockView.getBlockState(blockPos.method_10074()).getBlock() instanceof AbstractRailBlock)) {
			pathNodeType = PathNodeType.FENCE;
		}

		if (pathNodeType == PathNodeType.LEAVES) {
			pathNodeType = PathNodeType.BLOCKED;
		}

		return pathNodeType;
	}

	private PathNodeType getNodeType(MobEntity mobEntity, BlockPos blockPos) {
		return this.getNodeType(mobEntity, blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	private PathNodeType getNodeType(MobEntity mobEntity, int i, int j, int k) {
		return this.getNodeType(this.field_20622, i, j, k, mobEntity, this.field_31, this.field_30, this.field_28, this.canOpenDoors(), this.canEnterOpenDoors());
	}

	@Override
	public PathNodeType getNodeType(BlockView blockView, int i, int j, int k) {
		return method_23476(blockView, i, j, k);
	}

	public static PathNodeType method_23476(BlockView blockView, int i, int j, int k) {
		PathNodeType pathNodeType = getBasicPathNodeType(blockView, i, j, k);
		if (pathNodeType == PathNodeType.OPEN && j >= 1) {
			Block block = blockView.getBlockState(new BlockPos(i, j - 1, k)).getBlock();
			PathNodeType pathNodeType2 = getBasicPathNodeType(blockView, i, j - 1, k);
			pathNodeType = pathNodeType2 != PathNodeType.WALKABLE
					&& pathNodeType2 != PathNodeType.OPEN
					&& pathNodeType2 != PathNodeType.WATER
					&& pathNodeType2 != PathNodeType.LAVA
				? PathNodeType.WALKABLE
				: PathNodeType.OPEN;
			if (pathNodeType2 == PathNodeType.DAMAGE_FIRE || block == Blocks.MAGMA_BLOCK || block == Blocks.CAMPFIRE) {
				pathNodeType = PathNodeType.DAMAGE_FIRE;
			}

			if (pathNodeType2 == PathNodeType.DAMAGE_CACTUS) {
				pathNodeType = PathNodeType.DAMAGE_CACTUS;
			}

			if (pathNodeType2 == PathNodeType.DAMAGE_OTHER) {
				pathNodeType = PathNodeType.DAMAGE_OTHER;
			}

			if (pathNodeType2 == PathNodeType.STICKY_HONEY) {
				pathNodeType = PathNodeType.STICKY_HONEY;
			}
		}

		return method_59(blockView, i, j, k, pathNodeType);
	}

	public static PathNodeType method_59(BlockView blockView, int i, int j, int k, PathNodeType pathNodeType) {
		if (pathNodeType == PathNodeType.WALKABLE) {
			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (int l = -1; l <= 1; l++) {
					for (int m = -1; m <= 1; m++) {
						for (int n = -1; n <= 1; n++) {
							if (l != 0 || n != 0) {
								Block block = blockView.getBlockState(pooledMutable.method_10113(l + i, m + j, n + k)).getBlock();
								if (block == Blocks.CACTUS) {
									pathNodeType = PathNodeType.DANGER_CACTUS;
								} else if (block == Blocks.FIRE || block == Blocks.LAVA) {
									pathNodeType = PathNodeType.DANGER_FIRE;
								} else if (block == Blocks.SWEET_BERRY_BUSH) {
									pathNodeType = PathNodeType.DANGER_OTHER;
								}
							}
						}
					}
				}
			}
		}

		return pathNodeType;
	}

	protected static PathNodeType getBasicPathNodeType(BlockView blockView, int i, int j, int k) {
		BlockPos blockPos = new BlockPos(i, j, k);
		BlockState blockState = blockView.getBlockState(blockPos);
		Block block = blockState.getBlock();
		Material material = blockState.getMaterial();
		if (blockState.isAir()) {
			return PathNodeType.OPEN;
		} else if (block.matches(BlockTags.TRAPDOORS) || block == Blocks.LILY_PAD) {
			return PathNodeType.TRAPDOOR;
		} else if (block == Blocks.FIRE) {
			return PathNodeType.DAMAGE_FIRE;
		} else if (block == Blocks.CACTUS) {
			return PathNodeType.DAMAGE_CACTUS;
		} else if (block == Blocks.SWEET_BERRY_BUSH) {
			return PathNodeType.DAMAGE_OTHER;
		} else if (block == Blocks.HONEY_BLOCK) {
			return PathNodeType.STICKY_HONEY;
		} else if (block instanceof DoorBlock && material == Material.WOOD && !(Boolean)blockState.get(DoorBlock.OPEN)) {
			return PathNodeType.DOOR_WOOD_CLOSED;
		} else if (block instanceof DoorBlock && material == Material.METAL && !(Boolean)blockState.get(DoorBlock.OPEN)) {
			return PathNodeType.DOOR_IRON_CLOSED;
		} else if (block instanceof DoorBlock && (Boolean)blockState.get(DoorBlock.OPEN)) {
			return PathNodeType.DOOR_OPEN;
		} else if (block instanceof AbstractRailBlock) {
			return PathNodeType.RAIL;
		} else if (block instanceof LeavesBlock) {
			return PathNodeType.LEAVES;
		} else if (!block.matches(BlockTags.FENCES)
			&& !block.matches(BlockTags.WALLS)
			&& (!(block instanceof FenceGateBlock) || (Boolean)blockState.get(FenceGateBlock.OPEN))) {
			FluidState fluidState = blockView.getFluidState(blockPos);
			if (fluidState.matches(FluidTags.WATER)) {
				return PathNodeType.WATER;
			} else if (fluidState.matches(FluidTags.LAVA)) {
				return PathNodeType.LAVA;
			} else {
				return blockState.canPlaceAtSide(blockView, blockPos, BlockPlacementEnvironment.LAND) ? PathNodeType.OPEN : PathNodeType.BLOCKED;
			}
		} else {
			return PathNodeType.FENCE;
		}
	}
}
