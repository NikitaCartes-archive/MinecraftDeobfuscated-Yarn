package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
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
	public void init(ChunkCache cachedWorld, MobEntity entity) {
		super.init(cachedWorld, entity);
		this.waterPathNodeTypeWeight = entity.getPathfindingPenalty(PathNodeType.WATER);
	}

	@Override
	public void clear() {
		this.entity.setPathfindingPenalty(PathNodeType.WATER, this.waterPathNodeTypeWeight);
		super.clear();
	}

	@Override
	public PathNode getStart() {
		int i;
		if (this.canSwim() && this.entity.isTouchingWater()) {
			i = MathHelper.floor(this.entity.getY());
			BlockPos.Mutable mutable = new BlockPos.Mutable(this.entity.getX(), (double)i, this.entity.getZ());

			for (BlockState blockState = this.cachedWorld.getBlockState(mutable);
				blockState.getBlock() == Blocks.WATER || blockState.getFluidState() == Fluids.WATER.getStill(false);
				blockState = this.cachedWorld.getBlockState(mutable)
			) {
				mutable.set(this.entity.getX(), (double)(++i), this.entity.getZ());
			}

			i--;
		} else if (this.entity.isOnGround()) {
			i = MathHelper.floor(this.entity.getY() + 0.5);
		} else {
			BlockPos blockPos = this.entity.getSenseCenterPos();

			while (
				(
						this.cachedWorld.getBlockState(blockPos).isAir()
							|| this.cachedWorld.getBlockState(blockPos).canPathfindThrough(this.cachedWorld, blockPos, NavigationType.LAND)
					)
					&& blockPos.getY() > 0
			) {
				blockPos = blockPos.down();
			}

			i = blockPos.up().getY();
		}

		BlockPos blockPos = this.entity.getSenseCenterPos();
		PathNodeType pathNodeType = this.getNodeType(this.entity, blockPos.getX(), i, blockPos.getZ());
		if (this.entity.getPathfindingPenalty(pathNodeType) < 0.0F) {
			Set<BlockPos> set = Sets.<BlockPos>newHashSet();
			set.add(new BlockPos(this.entity.getBoundingBox().x1, (double)i, this.entity.getBoundingBox().z1));
			set.add(new BlockPos(this.entity.getBoundingBox().x1, (double)i, this.entity.getBoundingBox().z2));
			set.add(new BlockPos(this.entity.getBoundingBox().x2, (double)i, this.entity.getBoundingBox().z1));
			set.add(new BlockPos(this.entity.getBoundingBox().x2, (double)i, this.entity.getBoundingBox().z2));

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
	public TargetPathNode getNode(double x, double y, double z) {
		return new TargetPathNode(this.getNode(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)));
	}

	@Override
	public int getSuccessors(PathNode[] successors, PathNode node) {
		int i = 0;
		int j = 0;
		PathNodeType pathNodeType = this.getNodeType(this.entity, node.x, node.y + 1, node.z);
		if (this.entity.getPathfindingPenalty(pathNodeType) >= 0.0F) {
			PathNodeType pathNodeType2 = this.getNodeType(this.entity, node.x, node.y, node.z);
			if (pathNodeType2 == PathNodeType.STICKY_HONEY) {
				j = 0;
			} else {
				j = MathHelper.floor(Math.max(1.0F, this.entity.stepHeight));
			}
		}

		double d = getFeetY(this.cachedWorld, new BlockPos(node.x, node.y, node.z));
		PathNode pathNode = this.getPathNode(node.x, node.y, node.z + 1, j, d, Direction.SOUTH);
		if (pathNode != null && !pathNode.visited && pathNode.penalty >= 0.0F) {
			successors[i++] = pathNode;
		}

		PathNode pathNode2 = this.getPathNode(node.x - 1, node.y, node.z, j, d, Direction.WEST);
		if (pathNode2 != null && !pathNode2.visited && pathNode2.penalty >= 0.0F) {
			successors[i++] = pathNode2;
		}

		PathNode pathNode3 = this.getPathNode(node.x + 1, node.y, node.z, j, d, Direction.EAST);
		if (pathNode3 != null && !pathNode3.visited && pathNode3.penalty >= 0.0F) {
			successors[i++] = pathNode3;
		}

		PathNode pathNode4 = this.getPathNode(node.x, node.y, node.z - 1, j, d, Direction.NORTH);
		if (pathNode4 != null && !pathNode4.visited && pathNode4.penalty >= 0.0F) {
			successors[i++] = pathNode4;
		}

		PathNode pathNode5 = this.getPathNode(node.x - 1, node.y, node.z - 1, j, d, Direction.NORTH);
		if (this.isValidDiagonalSuccessor(node, pathNode2, pathNode4, pathNode5)) {
			successors[i++] = pathNode5;
		}

		PathNode pathNode6 = this.getPathNode(node.x + 1, node.y, node.z - 1, j, d, Direction.NORTH);
		if (this.isValidDiagonalSuccessor(node, pathNode3, pathNode4, pathNode6)) {
			successors[i++] = pathNode6;
		}

		PathNode pathNode7 = this.getPathNode(node.x - 1, node.y, node.z + 1, j, d, Direction.SOUTH);
		if (this.isValidDiagonalSuccessor(node, pathNode2, pathNode, pathNode7)) {
			successors[i++] = pathNode7;
		}

		PathNode pathNode8 = this.getPathNode(node.x + 1, node.y, node.z + 1, j, d, Direction.SOUTH);
		if (this.isValidDiagonalSuccessor(node, pathNode3, pathNode, pathNode8)) {
			successors[i++] = pathNode8;
		}

		return i;
	}

	private boolean isValidDiagonalSuccessor(PathNode node, @Nullable PathNode successor1, @Nullable PathNode successor2, @Nullable PathNode diagonalSuccessor) {
		if (diagonalSuccessor == null || successor2 == null || successor1 == null) {
			return false;
		} else if (diagonalSuccessor.visited) {
			return false;
		} else {
			return successor2.y <= node.y && successor1.y <= node.y
				? diagonalSuccessor.penalty >= 0.0F && (successor2.y < node.y || successor2.penalty >= 0.0F) && (successor1.y < node.y || successor1.penalty >= 0.0F)
				: false;
		}
	}

	public static double getFeetY(BlockView world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		VoxelShape voxelShape = world.getBlockState(blockPos).getCollisionShape(world, blockPos);
		return (double)blockPos.getY() + (voxelShape.isEmpty() ? 0.0 : voxelShape.getMaximum(Direction.Axis.Y));
	}

	@Nullable
	private PathNode getPathNode(int x, int y, int z, int maxYStep, double prevFeetY, Direction direction) {
		PathNode pathNode = null;
		BlockPos blockPos = new BlockPos(x, y, z);
		double d = getFeetY(this.cachedWorld, blockPos);
		if (d - prevFeetY > 1.125) {
			return null;
		} else {
			PathNodeType pathNodeType = this.getNodeType(this.entity, x, y, z);
			float f = this.entity.getPathfindingPenalty(pathNodeType);
			double e = (double)this.entity.getWidth() / 2.0;
			if (f >= 0.0F) {
				pathNode = this.getNode(x, y, z);
				pathNode.type = pathNodeType;
				pathNode.penalty = Math.max(pathNode.penalty, f);
			}

			if (pathNodeType == PathNodeType.WALKABLE) {
				return pathNode;
			} else {
				if ((pathNode == null || pathNode.penalty < 0.0F) && maxYStep > 0 && pathNodeType != PathNodeType.FENCE && pathNodeType != PathNodeType.TRAPDOOR) {
					pathNode = this.getPathNode(x, y + 1, z, maxYStep - 1, prevFeetY, direction);
					if (pathNode != null && (pathNode.type == PathNodeType.OPEN || pathNode.type == PathNodeType.WALKABLE) && this.entity.getWidth() < 1.0F) {
						double g = (double)(x - direction.getOffsetX()) + 0.5;
						double h = (double)(z - direction.getOffsetZ()) + 0.5;
						Box box = new Box(
							g - e,
							getFeetY(this.cachedWorld, new BlockPos(g, (double)(y + 1), h)) + 0.001,
							h - e,
							g + e,
							(double)this.entity.getHeight() + getFeetY(this.cachedWorld, new BlockPos(pathNode.x, pathNode.y, pathNode.z)) - 0.002,
							h + e
						);
						if (!this.cachedWorld.doesNotCollide(this.entity, box)) {
							pathNode = null;
						}
					}
				}

				if (pathNodeType == PathNodeType.WATER && !this.canSwim()) {
					if (this.getNodeType(this.entity, x, y - 1, z) != PathNodeType.WATER) {
						return pathNode;
					}

					while (y > 0) {
						pathNodeType = this.getNodeType(this.entity, x, --y, z);
						if (pathNodeType != PathNodeType.WATER) {
							return pathNode;
						}

						pathNode = this.getNode(x, y, z);
						pathNode.type = pathNodeType;
						pathNode.penalty = Math.max(pathNode.penalty, this.entity.getPathfindingPenalty(pathNodeType));
					}
				}

				if (pathNodeType == PathNodeType.OPEN) {
					Box box2 = new Box(
						(double)x - e + 0.5, (double)y + 0.001, (double)z - e + 0.5, (double)x + e + 0.5, (double)((float)y + this.entity.getHeight()), (double)z + e + 0.5
					);
					if (!this.cachedWorld.doesNotCollide(this.entity, box2)) {
						return null;
					}

					if (this.entity.getWidth() >= 1.0F) {
						PathNodeType pathNodeType2 = this.getNodeType(this.entity, x, y - 1, z);
						if (pathNodeType2 == PathNodeType.BLOCKED) {
							pathNode = this.getNode(x, y, z);
							pathNode.type = PathNodeType.WALKABLE;
							pathNode.penalty = Math.max(pathNode.penalty, f);
							return pathNode;
						}
					}

					int i = 0;
					int j = y;

					while (pathNodeType == PathNodeType.OPEN) {
						if (--y < 0) {
							PathNode pathNode2 = this.getNode(x, j, z);
							pathNode2.type = PathNodeType.BLOCKED;
							pathNode2.penalty = -1.0F;
							return pathNode2;
						}

						PathNode pathNode2 = this.getNode(x, y, z);
						if (i++ >= this.entity.getSafeFallDistance()) {
							pathNode2.type = PathNodeType.BLOCKED;
							pathNode2.penalty = -1.0F;
							return pathNode2;
						}

						pathNodeType = this.getNodeType(this.entity, x, y, z);
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
	public PathNodeType getNodeType(
		BlockView world, int x, int y, int z, MobEntity mob, int sizeX, int sizeY, int sizeZ, boolean canOpenDoors, boolean canEnterOpenDoors
	) {
		EnumSet<PathNodeType> enumSet = EnumSet.noneOf(PathNodeType.class);
		PathNodeType pathNodeType = PathNodeType.BLOCKED;
		double d = (double)mob.getWidth() / 2.0;
		BlockPos blockPos = mob.getSenseCenterPos();
		pathNodeType = this.findNearbyNodeTypes(world, x, y, z, sizeX, sizeY, sizeZ, canOpenDoors, canEnterOpenDoors, enumSet, pathNodeType, blockPos);
		if (enumSet.contains(PathNodeType.FENCE)) {
			return PathNodeType.FENCE;
		} else {
			PathNodeType pathNodeType2 = PathNodeType.BLOCKED;

			for (PathNodeType pathNodeType3 : enumSet) {
				if (mob.getPathfindingPenalty(pathNodeType3) < 0.0F) {
					return pathNodeType3;
				}

				if (mob.getPathfindingPenalty(pathNodeType3) >= mob.getPathfindingPenalty(pathNodeType2)) {
					pathNodeType2 = pathNodeType3;
				}
			}

			return pathNodeType == PathNodeType.OPEN && mob.getPathfindingPenalty(pathNodeType2) == 0.0F ? PathNodeType.OPEN : pathNodeType2;
		}
	}

	/**
	 * Adds the node types in the box with the given size to the input EnumSet.
	 * @return The node type at the least coordinates of the input box.
	 */
	public PathNodeType findNearbyNodeTypes(
		BlockView world,
		int x,
		int y,
		int z,
		int sizeX,
		int sizeY,
		int sizeZ,
		boolean canOpenDoors,
		boolean canEnterOpenDoors,
		EnumSet<PathNodeType> nearbyTypes,
		PathNodeType type,
		BlockPos pos
	) {
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				for (int k = 0; k < sizeZ; k++) {
					int l = i + x;
					int m = j + y;
					int n = k + z;
					PathNodeType pathNodeType = this.getDefaultNodeType(world, l, m, n);
					pathNodeType = this.adjustNodeType(world, canOpenDoors, canEnterOpenDoors, pos, pathNodeType);
					if (i == 0 && j == 0 && k == 0) {
						type = pathNodeType;
					}

					nearbyTypes.add(pathNodeType);
				}
			}
		}

		return type;
	}

	protected PathNodeType adjustNodeType(BlockView world, boolean canOpenDoors, boolean canEnterOpenDoors, BlockPos pos, PathNodeType type) {
		if (type == PathNodeType.DOOR_WOOD_CLOSED && canOpenDoors && canEnterOpenDoors) {
			type = PathNodeType.WALKABLE;
		}

		if (type == PathNodeType.DOOR_OPEN && !canEnterOpenDoors) {
			type = PathNodeType.BLOCKED;
		}

		if (type == PathNodeType.RAIL
			&& !(world.getBlockState(pos).getBlock() instanceof AbstractRailBlock)
			&& !(world.getBlockState(pos.down()).getBlock() instanceof AbstractRailBlock)) {
			type = PathNodeType.FENCE;
		}

		if (type == PathNodeType.LEAVES) {
			type = PathNodeType.BLOCKED;
		}

		return type;
	}

	private PathNodeType getNodeType(MobEntity entity, BlockPos pos) {
		return this.getNodeType(entity, pos.getX(), pos.getY(), pos.getZ());
	}

	private PathNodeType getNodeType(MobEntity entity, int x, int y, int z) {
		return this.getNodeType(
			this.cachedWorld, x, y, z, entity, this.entityBlockXSize, this.entityBlockYSize, this.entityBlockZSize, this.canOpenDoors(), this.canEnterOpenDoors()
		);
	}

	@Override
	public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
		return getLandNodeType(world, x, y, z);
	}

	public static PathNodeType getLandNodeType(BlockView world, int x, int y, int z) {
		PathNodeType pathNodeType = getCommonNodeType(world, x, y, z);
		if (pathNodeType == PathNodeType.OPEN && y >= 1) {
			Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
			PathNodeType pathNodeType2 = getCommonNodeType(world, x, y - 1, z);
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

		if (pathNodeType == PathNodeType.WALKABLE) {
			pathNodeType = getNodeTypeFromNeighbors(world, x, y, z, pathNodeType);
		}

		return pathNodeType;
	}

	public static PathNodeType getNodeTypeFromNeighbors(BlockView world, int x, int y, int z, PathNodeType type) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					if (i != 0 || k != 0) {
						Block block = world.getBlockState(mutable.set(i + x, j + y, k + z)).getBlock();
						if (block == Blocks.CACTUS) {
							type = PathNodeType.DANGER_CACTUS;
						} else if (block.isIn(BlockTags.FIRE) || block == Blocks.LAVA) {
							type = PathNodeType.DANGER_FIRE;
						} else if (block == Blocks.SWEET_BERRY_BUSH) {
							type = PathNodeType.DANGER_OTHER;
						}
					}
				}
			}
		}

		return type;
	}

	protected static PathNodeType getCommonNodeType(BlockView world, int x, int y, int z) {
		BlockPos blockPos = new BlockPos(x, y, z);
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		Material material = blockState.getMaterial();
		if (blockState.isAir()) {
			return PathNodeType.OPEN;
		} else if (block.isIn(BlockTags.TRAPDOORS) || block == Blocks.LILY_PAD) {
			return PathNodeType.TRAPDOOR;
		} else if (blockState.matches(BlockTags.FIRE)) {
			return PathNodeType.DAMAGE_FIRE;
		} else if (block == Blocks.CACTUS) {
			return PathNodeType.DAMAGE_CACTUS;
		} else if (block == Blocks.SWEET_BERRY_BUSH) {
			return PathNodeType.DAMAGE_OTHER;
		} else if (block == Blocks.HONEY_BLOCK) {
			return PathNodeType.STICKY_HONEY;
		} else if (block == Blocks.COCOA) {
			return PathNodeType.COCOA;
		} else if (DoorBlock.isWoodenDoor(blockState) && !(Boolean)blockState.get(DoorBlock.OPEN)) {
			return PathNodeType.DOOR_WOOD_CLOSED;
		} else if (block instanceof DoorBlock && material == Material.METAL && !(Boolean)blockState.get(DoorBlock.OPEN)) {
			return PathNodeType.DOOR_IRON_CLOSED;
		} else if (block instanceof DoorBlock && (Boolean)blockState.get(DoorBlock.OPEN)) {
			return PathNodeType.DOOR_OPEN;
		} else if (block instanceof AbstractRailBlock) {
			return PathNodeType.RAIL;
		} else if (block instanceof LeavesBlock) {
			return PathNodeType.LEAVES;
		} else if (!block.isIn(BlockTags.FENCES)
			&& !block.isIn(BlockTags.WALLS)
			&& (!(block instanceof FenceGateBlock) || (Boolean)blockState.get(FenceGateBlock.OPEN))) {
			FluidState fluidState = world.getFluidState(blockPos);
			if (fluidState.matches(FluidTags.WATER)) {
				return PathNodeType.WATER;
			} else if (fluidState.matches(FluidTags.LAVA)) {
				return PathNodeType.LAVA;
			} else {
				return blockState.canPathfindThrough(world, blockPos, NavigationType.LAND) ? PathNodeType.OPEN : PathNodeType.BLOCKED;
			}
		} else {
			return PathNodeType.FENCE;
		}
	}
}
