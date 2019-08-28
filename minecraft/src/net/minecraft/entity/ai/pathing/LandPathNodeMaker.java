package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_4459;
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
		this.waterPathNodeTypeWeight = mobEntity.getPathNodeTypeWeight(PathNodeType.WATER);
	}

	@Override
	public void clear() {
		this.entity.setPathNodeTypeWeight(PathNodeType.WATER, this.waterPathNodeTypeWeight);
		super.clear();
	}

	@Override
	public PathNode getStart() {
		int i;
		if (this.canSwim() && this.entity.isInsideWater()) {
			i = MathHelper.floor(this.entity.getBoundingBox().minY);
			BlockPos.Mutable mutable = new BlockPos.Mutable(this.entity.x, (double)i, this.entity.z);

			for (BlockState blockState = this.field_20622.getBlockState(mutable);
				blockState.getBlock() == Blocks.WATER || blockState.getFluidState() == Fluids.WATER.getStill(false);
				blockState = this.field_20622.getBlockState(mutable)
			) {
				mutable.set(this.entity.x, (double)(++i), this.entity.z);
			}

			i--;
		} else if (this.entity.onGround) {
			i = MathHelper.floor(this.entity.getBoundingBox().minY + 0.5);
		} else {
			BlockPos blockPos = new BlockPos(this.entity);

			while (
				(
						this.field_20622.getBlockState(blockPos).isAir()
							|| this.field_20622.getBlockState(blockPos).canPlaceAtSide(this.field_20622, blockPos, BlockPlacementEnvironment.LAND)
					)
					&& blockPos.getY() > 0
			) {
				blockPos = blockPos.down();
			}

			i = blockPos.up().getY();
		}

		BlockPos blockPos = new BlockPos(this.entity);
		PathNodeType pathNodeType = this.getPathNodeType(this.entity, blockPos.getX(), i, blockPos.getZ());
		if (this.entity.getPathNodeTypeWeight(pathNodeType) < 0.0F) {
			Set<BlockPos> set = Sets.<BlockPos>newHashSet();
			set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)i, this.entity.getBoundingBox().minZ));
			set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)i, this.entity.getBoundingBox().maxZ));
			set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)i, this.entity.getBoundingBox().minZ));
			set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)i, this.entity.getBoundingBox().maxZ));

			for (BlockPos blockPos2 : set) {
				PathNodeType pathNodeType2 = this.getPathNodeType(this.entity, blockPos2);
				if (this.entity.getPathNodeTypeWeight(pathNodeType2) >= 0.0F) {
					return this.getPathNode(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
				}
			}
		}

		return this.getPathNode(blockPos.getX(), i, blockPos.getZ());
	}

	@Override
	public class_4459 getPathNode(double d, double e, double f) {
		return new class_4459(this.getPathNode(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f)));
	}

	@Override
	public int getPathNodes(PathNode[] pathNodes, PathNode pathNode) {
		int i = 0;
		int j = 0;
		PathNodeType pathNodeType = this.getPathNodeType(this.entity, pathNode.x, pathNode.y + 1, pathNode.z);
		if (this.entity.getPathNodeTypeWeight(pathNodeType) >= 0.0F) {
			j = MathHelper.floor(Math.max(1.0F, this.entity.stepHeight));
		}

		double d = method_60(this.field_20622, new BlockPos(pathNode.x, pathNode.y, pathNode.z));
		PathNode pathNode2 = this.getPathNode(pathNode.x, pathNode.y, pathNode.z + 1, j, d, Direction.SOUTH);
		if (pathNode2 != null && !pathNode2.field_42 && pathNode2.field_43 >= 0.0F) {
			pathNodes[i++] = pathNode2;
		}

		PathNode pathNode3 = this.getPathNode(pathNode.x - 1, pathNode.y, pathNode.z, j, d, Direction.WEST);
		if (pathNode3 != null && !pathNode3.field_42 && pathNode3.field_43 >= 0.0F) {
			pathNodes[i++] = pathNode3;
		}

		PathNode pathNode4 = this.getPathNode(pathNode.x + 1, pathNode.y, pathNode.z, j, d, Direction.EAST);
		if (pathNode4 != null && !pathNode4.field_42 && pathNode4.field_43 >= 0.0F) {
			pathNodes[i++] = pathNode4;
		}

		PathNode pathNode5 = this.getPathNode(pathNode.x, pathNode.y, pathNode.z - 1, j, d, Direction.NORTH);
		if (pathNode5 != null && !pathNode5.field_42 && pathNode5.field_43 >= 0.0F) {
			pathNodes[i++] = pathNode5;
		}

		PathNode pathNode6 = this.getPathNode(pathNode.x - 1, pathNode.y, pathNode.z - 1, j, d, Direction.NORTH);
		if (this.method_20536(pathNode, pathNode3, pathNode5, pathNode6)) {
			pathNodes[i++] = pathNode6;
		}

		PathNode pathNode7 = this.getPathNode(pathNode.x + 1, pathNode.y, pathNode.z - 1, j, d, Direction.NORTH);
		if (this.method_20536(pathNode, pathNode4, pathNode5, pathNode7)) {
			pathNodes[i++] = pathNode7;
		}

		PathNode pathNode8 = this.getPathNode(pathNode.x - 1, pathNode.y, pathNode.z + 1, j, d, Direction.SOUTH);
		if (this.method_20536(pathNode, pathNode3, pathNode2, pathNode8)) {
			pathNodes[i++] = pathNode8;
		}

		PathNode pathNode9 = this.getPathNode(pathNode.x + 1, pathNode.y, pathNode.z + 1, j, d, Direction.SOUTH);
		if (this.method_20536(pathNode, pathNode4, pathNode2, pathNode9)) {
			pathNodes[i++] = pathNode9;
		}

		return i;
	}

	private boolean method_20536(PathNode pathNode, @Nullable PathNode pathNode2, @Nullable PathNode pathNode3, @Nullable PathNode pathNode4) {
		if (pathNode4 == null || pathNode3 == null || pathNode2 == null) {
			return false;
		} else if (pathNode4.field_42) {
			return false;
		} else {
			return pathNode3.y <= pathNode.y && pathNode2.y <= pathNode.y
				? pathNode4.field_43 >= 0.0F && (pathNode3.y < pathNode.y || pathNode3.field_43 >= 0.0F) && (pathNode2.y < pathNode.y || pathNode2.field_43 >= 0.0F)
				: false;
		}
	}

	public static double method_60(BlockView blockView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		VoxelShape voxelShape = blockView.getBlockState(blockPos2).getCollisionShape(blockView, blockPos2);
		return (double)blockPos2.getY() + (voxelShape.isEmpty() ? 0.0 : voxelShape.getMaximum(Direction.Axis.Y));
	}

	@Nullable
	private PathNode getPathNode(int i, int j, int k, int l, double d, Direction direction) {
		PathNode pathNode = null;
		BlockPos blockPos = new BlockPos(i, j, k);
		double e = method_60(this.field_20622, blockPos);
		if (e - d > 1.125) {
			return null;
		} else {
			PathNodeType pathNodeType = this.getPathNodeType(this.entity, i, j, k);
			float f = this.entity.getPathNodeTypeWeight(pathNodeType);
			double g = (double)this.entity.getWidth() / 2.0;
			if (f >= 0.0F) {
				pathNode = this.getPathNode(i, j, k);
				pathNode.type = pathNodeType;
				pathNode.field_43 = Math.max(pathNode.field_43, f);
			}

			if (pathNodeType == PathNodeType.WALKABLE) {
				return pathNode;
			} else {
				if ((pathNode == null || pathNode.field_43 < 0.0F) && l > 0 && pathNodeType != PathNodeType.FENCE && pathNodeType != PathNodeType.TRAPDOOR) {
					pathNode = this.getPathNode(i, j + 1, k, l - 1, d, direction);
					if (pathNode != null && (pathNode.type == PathNodeType.OPEN || pathNode.type == PathNodeType.WALKABLE) && this.entity.getWidth() < 1.0F) {
						double h = (double)(i - direction.getOffsetX()) + 0.5;
						double m = (double)(k - direction.getOffsetZ()) + 0.5;
						Box box = new Box(
							h - g,
							method_60(this.field_20622, new BlockPos(h, (double)(j + 1), m)) + 0.001,
							m - g,
							h + g,
							(double)this.entity.getHeight() + method_60(this.field_20622, new BlockPos(pathNode.x, pathNode.y, pathNode.z)) - 0.002,
							m + g
						);
						if (!this.field_20622.doesNotCollide(this.entity, box)) {
							pathNode = null;
						}
					}
				}

				if (pathNodeType == PathNodeType.WATER && !this.canSwim()) {
					if (this.getPathNodeType(this.entity, i, j - 1, k) != PathNodeType.WATER) {
						return pathNode;
					}

					while (j > 0) {
						pathNodeType = this.getPathNodeType(this.entity, i, --j, k);
						if (pathNodeType != PathNodeType.WATER) {
							return pathNode;
						}

						pathNode = this.getPathNode(i, j, k);
						pathNode.type = pathNodeType;
						pathNode.field_43 = Math.max(pathNode.field_43, this.entity.getPathNodeTypeWeight(pathNodeType));
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
						PathNodeType pathNodeType2 = this.getPathNodeType(this.entity, i, j - 1, k);
						if (pathNodeType2 == PathNodeType.BLOCKED) {
							pathNode = this.getPathNode(i, j, k);
							pathNode.type = PathNodeType.WALKABLE;
							pathNode.field_43 = Math.max(pathNode.field_43, f);
							return pathNode;
						}
					}

					int n = 0;
					int o = j;

					while (pathNodeType == PathNodeType.OPEN) {
						if (--j < 0) {
							PathNode pathNode2 = this.getPathNode(i, o, k);
							pathNode2.type = PathNodeType.BLOCKED;
							pathNode2.field_43 = -1.0F;
							return pathNode2;
						}

						PathNode pathNode2 = this.getPathNode(i, j, k);
						if (n++ >= this.entity.getSafeFallDistance()) {
							pathNode2.type = PathNodeType.BLOCKED;
							pathNode2.field_43 = -1.0F;
							return pathNode2;
						}

						pathNodeType = this.getPathNodeType(this.entity, i, j, k);
						f = this.entity.getPathNodeTypeWeight(pathNodeType);
						if (pathNodeType != PathNodeType.OPEN && f >= 0.0F) {
							pathNode = pathNode2;
							pathNode2.type = pathNodeType;
							pathNode2.field_43 = Math.max(pathNode2.field_43, f);
							break;
						}

						if (f < 0.0F) {
							pathNode2.type = PathNodeType.BLOCKED;
							pathNode2.field_43 = -1.0F;
							return pathNode2;
						}
					}
				}

				return pathNode;
			}
		}
	}

	@Override
	public PathNodeType getPathNodeType(BlockView blockView, int i, int j, int k, MobEntity mobEntity, int l, int m, int n, boolean bl, boolean bl2) {
		EnumSet<PathNodeType> enumSet = EnumSet.noneOf(PathNodeType.class);
		PathNodeType pathNodeType = PathNodeType.BLOCKED;
		double d = (double)mobEntity.getWidth() / 2.0;
		BlockPos blockPos = new BlockPos(mobEntity);
		pathNodeType = this.method_64(blockView, i, j, k, l, m, n, bl, bl2, enumSet, pathNodeType, blockPos);
		if (enumSet.contains(PathNodeType.FENCE)) {
			return PathNodeType.FENCE;
		} else {
			PathNodeType pathNodeType2 = PathNodeType.BLOCKED;

			for (PathNodeType pathNodeType3 : enumSet) {
				if (mobEntity.getPathNodeTypeWeight(pathNodeType3) < 0.0F) {
					return pathNodeType3;
				}

				if (mobEntity.getPathNodeTypeWeight(pathNodeType3) >= mobEntity.getPathNodeTypeWeight(pathNodeType2)) {
					pathNodeType2 = pathNodeType3;
				}
			}

			return pathNodeType == PathNodeType.OPEN && mobEntity.getPathNodeTypeWeight(pathNodeType2) == 0.0F ? PathNodeType.OPEN : pathNodeType2;
		}
	}

	public PathNodeType method_64(
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
					PathNodeType pathNodeType2 = this.getPathNodeType(blockView, r, s, t);
					pathNodeType2 = this.method_61(blockView, bl, bl2, blockPos, pathNodeType2);
					if (o == 0 && p == 0 && q == 0) {
						pathNodeType = pathNodeType2;
					}

					enumSet.add(pathNodeType2);
				}
			}
		}

		return pathNodeType;
	}

	protected PathNodeType method_61(BlockView blockView, boolean bl, boolean bl2, BlockPos blockPos, PathNodeType pathNodeType) {
		if (pathNodeType == PathNodeType.DOOR_WOOD_CLOSED && bl && bl2) {
			pathNodeType = PathNodeType.WALKABLE;
		}

		if (pathNodeType == PathNodeType.DOOR_OPEN && !bl2) {
			pathNodeType = PathNodeType.BLOCKED;
		}

		if (pathNodeType == PathNodeType.RAIL
			&& !(blockView.getBlockState(blockPos).getBlock() instanceof AbstractRailBlock)
			&& !(blockView.getBlockState(blockPos.down()).getBlock() instanceof AbstractRailBlock)) {
			pathNodeType = PathNodeType.FENCE;
		}

		if (pathNodeType == PathNodeType.LEAVES) {
			pathNodeType = PathNodeType.BLOCKED;
		}

		return pathNodeType;
	}

	private PathNodeType getPathNodeType(MobEntity mobEntity, BlockPos blockPos) {
		return this.getPathNodeType(mobEntity, blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	private PathNodeType getPathNodeType(MobEntity mobEntity, int i, int j, int k) {
		return this.getPathNodeType(
			this.field_20622, i, j, k, mobEntity, this.field_31, this.field_30, this.field_28, this.canPathThroughDoors(), this.canEnterOpenDoors()
		);
	}

	@Override
	public PathNodeType getPathNodeType(BlockView blockView, int i, int j, int k) {
		PathNodeType pathNodeType = this.getBasicPathNodeType(blockView, i, j, k);
		if (pathNodeType == PathNodeType.OPEN && j >= 1) {
			Block block = blockView.getBlockState(new BlockPos(i, j - 1, k)).getBlock();
			PathNodeType pathNodeType2 = this.getBasicPathNodeType(blockView, i, j - 1, k);
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
		}

		return this.method_59(blockView, i, j, k, pathNodeType);
	}

	public PathNodeType method_59(BlockView blockView, int i, int j, int k, PathNodeType pathNodeType) {
		if (pathNodeType == PathNodeType.WALKABLE) {
			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (int l = -1; l <= 1; l++) {
					for (int m = -1; m <= 1; m++) {
						if (l != 0 || m != 0) {
							Block block = blockView.getBlockState(pooledMutable.method_10113(l + i, j, m + k)).getBlock();
							if (block == Blocks.CACTUS) {
								pathNodeType = PathNodeType.DANGER_CACTUS;
							} else if (block == Blocks.FIRE) {
								pathNodeType = PathNodeType.DANGER_FIRE;
							} else if (block == Blocks.SWEET_BERRY_BUSH) {
								pathNodeType = PathNodeType.DANGER_OTHER;
							}
						}
					}
				}
			}
		}

		return pathNodeType;
	}

	protected PathNodeType getBasicPathNodeType(BlockView blockView, int i, int j, int k) {
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
