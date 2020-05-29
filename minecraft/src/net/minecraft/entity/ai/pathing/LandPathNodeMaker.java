package net.minecraft.entity.ai.pathing;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
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
	private final Long2ObjectMap<PathNodeType> field_25190 = new Long2ObjectOpenHashMap<>();
	private final Object2BooleanMap<Box> field_25191 = new Object2BooleanOpenHashMap<>();

	@Override
	public void init(ChunkCache cachedWorld, MobEntity entity) {
		super.init(cachedWorld, entity);
		this.waterPathNodeTypeWeight = entity.getPathfindingPenalty(PathNodeType.WATER);
	}

	@Override
	public void clear() {
		this.entity.setPathfindingPenalty(PathNodeType.WATER, this.waterPathNodeTypeWeight);
		this.field_25190.clear();
		this.field_25191.clear();
		super.clear();
	}

	@Override
	public PathNode getStart() {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = MathHelper.floor(this.entity.getY());
		BlockState blockState = this.cachedWorld.getBlockState(mutable.set(this.entity.getX(), (double)i, this.entity.getZ()));
		if (!this.entity.canWalkOnLava(blockState.getFluidState().getFluid())) {
			if (this.canSwim() && this.entity.isTouchingWater()) {
				while (true) {
					if (blockState.getBlock() != Blocks.WATER && blockState.getFluidState() != Fluids.WATER.getStill(false)) {
						i--;
						break;
					}

					blockState = this.cachedWorld.getBlockState(mutable.set(this.entity.getX(), (double)(++i), this.entity.getZ()));
				}
			} else if (this.entity.isOnGround()) {
				i = MathHelper.floor(this.entity.getY() + 0.5);
			} else {
				BlockPos blockPos = this.entity.getBlockPos();

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
		} else {
			while (this.entity.canWalkOnLava(blockState.getFluidState().getFluid())) {
				blockState = this.cachedWorld.getBlockState(mutable.set(this.entity.getX(), (double)(++i), this.entity.getZ()));
			}

			i--;
		}

		BlockPos blockPos = this.entity.getBlockPos();
		PathNodeType pathNodeType = this.method_29303(this.entity, blockPos.getX(), i, blockPos.getZ());
		if (this.entity.getPathfindingPenalty(pathNodeType) < 0.0F) {
			Box box = this.entity.getBoundingBox();
			if (this.method_27139(mutable.set(box.minX, (double)i, box.minZ))
				|| this.method_27139(mutable.set(box.minX, (double)i, box.maxZ))
				|| this.method_27139(mutable.set(box.maxX, (double)i, box.minZ))
				|| this.method_27139(mutable.set(box.maxX, (double)i, box.maxZ))) {
				PathNode pathNode = this.method_27137(mutable);
				pathNode.type = this.getNodeType(this.entity, pathNode.getPos());
				pathNode.penalty = this.entity.getPathfindingPenalty(pathNode.type);
				return pathNode;
			}
		}

		PathNode pathNode2 = this.getNode(blockPos.getX(), i, blockPos.getZ());
		pathNode2.type = this.getNodeType(this.entity, pathNode2.getPos());
		pathNode2.penalty = this.entity.getPathfindingPenalty(pathNode2.type);
		return pathNode2;
	}

	private boolean method_27139(BlockPos blockPos) {
		PathNodeType pathNodeType = this.getNodeType(this.entity, blockPos);
		return this.entity.getPathfindingPenalty(pathNodeType) >= 0.0F;
	}

	@Override
	public TargetPathNode getNode(double x, double y, double z) {
		return new TargetPathNode(this.getNode(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)));
	}

	@Override
	public int getSuccessors(PathNode[] successors, PathNode node) {
		int i = 0;
		int j = 0;
		PathNodeType pathNodeType = this.method_29303(this.entity, node.x, node.y + 1, node.z);
		if (this.entity.getPathfindingPenalty(pathNodeType) >= 0.0F) {
			PathNodeType pathNodeType2 = this.method_29303(this.entity, node.x, node.y, node.z);
			if (pathNodeType2 == PathNodeType.STICKY_HONEY) {
				j = 0;
			} else {
				j = MathHelper.floor(Math.max(1.0F, this.entity.stepHeight));
			}
		}

		double d = getFeetY(this.cachedWorld, new BlockPos(node.x, node.y, node.z));
		PathNode pathNode = this.getPathNode(node.x, node.y, node.z + 1, j, d, Direction.SOUTH);
		if (pathNode != null && !pathNode.visited && (pathNode.penalty >= 0.0F || node.penalty < 0.0F)) {
			successors[i++] = pathNode;
		}

		PathNode pathNode2 = this.getPathNode(node.x - 1, node.y, node.z, j, d, Direction.WEST);
		if (pathNode2 != null && !pathNode2.visited && (pathNode2.penalty >= 0.0F || node.penalty < 0.0F)) {
			successors[i++] = pathNode2;
		}

		PathNode pathNode3 = this.getPathNode(node.x + 1, node.y, node.z, j, d, Direction.EAST);
		if (pathNode3 != null && !pathNode3.visited && (pathNode3.penalty >= 0.0F || node.penalty < 0.0F)) {
			successors[i++] = pathNode3;
		}

		PathNode pathNode4 = this.getPathNode(node.x, node.y, node.z - 1, j, d, Direction.NORTH);
		if (pathNode4 != null && !pathNode4.visited && (pathNode4.penalty >= 0.0F || node.penalty < 0.0F)) {
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
		return (double)blockPos.getY() + (voxelShape.isEmpty() ? 0.0 : voxelShape.getMax(Direction.Axis.Y));
	}

	@Nullable
	private PathNode getPathNode(int x, int y, int z, int maxYStep, double prevFeetY, Direction direction) {
		PathNode pathNode = null;
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		double d = getFeetY(this.cachedWorld, mutable.set(x, y, z));
		if (d - prevFeetY > 1.125) {
			return null;
		} else {
			PathNodeType pathNodeType = this.method_29303(this.entity, x, y, z);
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
							getFeetY(this.cachedWorld, mutable.set(g, (double)(y + 1), h)) + 0.001,
							h - e,
							g + e,
							(double)this.entity.getHeight() + getFeetY(this.cachedWorld, mutable.set((double)pathNode.x, (double)pathNode.y, (double)pathNode.z)) - 0.002,
							h + e
						);
						if (this.method_29304(box)) {
							pathNode = null;
						}
					}
				}

				if (pathNodeType == PathNodeType.WATER && !this.canSwim()) {
					if (this.method_29303(this.entity, x, y - 1, z) != PathNodeType.WATER) {
						return pathNode;
					}

					while (y > 0) {
						pathNodeType = this.method_29303(this.entity, x, --y, z);
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
					if (this.method_29304(box2)) {
						return null;
					}

					if (this.entity.getWidth() >= 1.0F) {
						PathNodeType pathNodeType2 = this.method_29303(this.entity, x, y - 1, z);
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

						pathNodeType = this.method_29303(this.entity, x, y, z);
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

	private boolean method_29304(Box box) {
		return (Boolean)this.field_25191.computeIfAbsent(box, box2 -> !this.cachedWorld.doesNotCollide(this.entity, box));
	}

	@Override
	public PathNodeType getNodeType(
		BlockView world, int x, int y, int z, MobEntity mob, int sizeX, int sizeY, int sizeZ, boolean canOpenDoors, boolean canEnterOpenDoors
	) {
		EnumSet<PathNodeType> enumSet = EnumSet.noneOf(PathNodeType.class);
		PathNodeType pathNodeType = PathNodeType.BLOCKED;
		double d = (double)mob.getWidth() / 2.0;
		BlockPos blockPos = mob.getBlockPos();
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
		return this.method_29303(entity, pos.getX(), pos.getY(), pos.getZ());
	}

	private PathNodeType method_29303(MobEntity mobEntity, int i, int j, int k) {
		return this.field_25190
			.computeIfAbsent(
				BlockPos.asLong(i, j, k),
				l -> this.getNodeType(
						this.cachedWorld, i, j, k, mobEntity, this.entityBlockXSize, this.entityBlockYSize, this.entityBlockZSize, this.canOpenDoors(), this.canEnterOpenDoors()
					)
			);
	}

	@Override
	public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
		return getLandNodeType(world, new BlockPos.Mutable(x, y, z));
	}

	public static PathNodeType getLandNodeType(BlockView blockView, BlockPos.Mutable mutable) {
		int i = mutable.getX();
		int j = mutable.getY();
		int k = mutable.getZ();
		PathNodeType pathNodeType = getCommonNodeType(blockView, mutable);
		if (pathNodeType == PathNodeType.OPEN && j >= 1) {
			PathNodeType pathNodeType2 = getCommonNodeType(blockView, mutable.set(i, j - 1, k));
			pathNodeType = pathNodeType2 != PathNodeType.WALKABLE
					&& pathNodeType2 != PathNodeType.OPEN
					&& pathNodeType2 != PathNodeType.WATER
					&& pathNodeType2 != PathNodeType.LAVA
				? PathNodeType.WALKABLE
				: PathNodeType.OPEN;
			if (pathNodeType2 == PathNodeType.DAMAGE_FIRE) {
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
			pathNodeType = getNodeTypeFromNeighbors(blockView, mutable.set(i, j, k), pathNodeType);
		}

		return pathNodeType;
	}

	public static PathNodeType getNodeTypeFromNeighbors(BlockView blockView, BlockPos.Mutable mutable, PathNodeType pathNodeType) {
		int i = mutable.getX();
		int j = mutable.getY();
		int k = mutable.getZ();

		for (int l = -1; l <= 1; l++) {
			for (int m = -1; m <= 1; m++) {
				for (int n = -1; n <= 1; n++) {
					if (l != 0 || n != 0) {
						mutable.set(i + l, j + m, k + n);
						BlockState blockState = blockView.getBlockState(mutable);
						if (blockState.isOf(Blocks.CACTUS)) {
							return PathNodeType.DANGER_CACTUS;
						}

						if (blockState.isOf(Blocks.SWEET_BERRY_BUSH)) {
							return PathNodeType.DANGER_OTHER;
						}

						if (method_27138(blockState)) {
							return PathNodeType.DANGER_FIRE;
						}
					}
				}
			}
		}

		return pathNodeType;
	}

	protected static PathNodeType getCommonNodeType(BlockView blockView, BlockPos blockPos) {
		BlockState blockState = blockView.getBlockState(blockPos);
		Block block = blockState.getBlock();
		Material material = blockState.getMaterial();
		if (blockState.isAir()) {
			return PathNodeType.OPEN;
		} else if (blockState.isIn(BlockTags.TRAPDOORS) || blockState.isOf(Blocks.LILY_PAD)) {
			return PathNodeType.TRAPDOOR;
		} else if (blockState.isOf(Blocks.CACTUS)) {
			return PathNodeType.DAMAGE_CACTUS;
		} else if (blockState.isOf(Blocks.SWEET_BERRY_BUSH)) {
			return PathNodeType.DAMAGE_OTHER;
		} else if (blockState.isOf(Blocks.HONEY_BLOCK)) {
			return PathNodeType.STICKY_HONEY;
		} else if (blockState.isOf(Blocks.COCOA)) {
			return PathNodeType.COCOA;
		} else if (method_27138(blockState)) {
			return PathNodeType.DAMAGE_FIRE;
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
			if (!blockState.canPathfindThrough(blockView, blockPos, NavigationType.LAND)) {
				return PathNodeType.BLOCKED;
			} else {
				FluidState fluidState = blockView.getFluidState(blockPos);
				if (fluidState.matches(FluidTags.WATER)) {
					return PathNodeType.WATER;
				} else {
					return fluidState.matches(FluidTags.LAVA) ? PathNodeType.LAVA : PathNodeType.OPEN;
				}
			}
		} else {
			return PathNodeType.FENCE;
		}
	}

	private static boolean method_27138(BlockState blockState) {
		return blockState.isIn(BlockTags.FIRE) || blockState.isOf(Blocks.LAVA) || blockState.isOf(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire(blockState);
	}
}
