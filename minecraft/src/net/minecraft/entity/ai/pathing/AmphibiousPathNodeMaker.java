package net.minecraft.entity.ai.pathing;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkCache;

public class AmphibiousPathNodeMaker extends LandPathNodeMaker {
	private float oldWalkablePenalty;
	private float oldWaterBorderPenalty;

	@Override
	public void init(ChunkCache cachedWorld, MobEntity entity) {
		super.init(cachedWorld, entity);
		entity.setPathfindingPenalty(PathNodeType.field_18, 0.0F);
		this.oldWalkablePenalty = entity.getPathfindingPenalty(PathNodeType.field_12);
		entity.setPathfindingPenalty(PathNodeType.field_12, 6.0F);
		this.oldWaterBorderPenalty = entity.getPathfindingPenalty(PathNodeType.field_4);
		entity.setPathfindingPenalty(PathNodeType.field_4, 4.0F);
	}

	@Override
	public void clear() {
		this.entity.setPathfindingPenalty(PathNodeType.field_12, this.oldWalkablePenalty);
		this.entity.setPathfindingPenalty(PathNodeType.field_4, this.oldWaterBorderPenalty);
		super.clear();
	}

	@Override
	public PathNode getStart() {
		return this.getNode(
			MathHelper.floor(this.entity.getBoundingBox().minX),
			MathHelper.floor(this.entity.getBoundingBox().minY + 0.5),
			MathHelper.floor(this.entity.getBoundingBox().minZ)
		);
	}

	@Override
	public TargetPathNode getNode(double x, double y, double z) {
		return new TargetPathNode(this.getNode(MathHelper.floor(x), MathHelper.floor(y + 0.5), MathHelper.floor(z)));
	}

	@Override
	public int getSuccessors(PathNode[] successors, PathNode node) {
		int i = 0;
		int j = 1;
		BlockPos blockPos = new BlockPos(node.x, node.y, node.z);
		double d = this.getFeetY(blockPos);
		PathNode pathNode = this.getPathNode(node.x, node.y, node.z + 1, 1, d);
		PathNode pathNode2 = this.getPathNode(node.x - 1, node.y, node.z, 1, d);
		PathNode pathNode3 = this.getPathNode(node.x + 1, node.y, node.z, 1, d);
		PathNode pathNode4 = this.getPathNode(node.x, node.y, node.z - 1, 1, d);
		PathNode pathNode5 = this.getPathNode(node.x, node.y + 1, node.z, 0, d);
		PathNode pathNode6 = this.getPathNode(node.x, node.y - 1, node.z, 1, d);
		if (pathNode != null && !pathNode.visited) {
			successors[i++] = pathNode;
		}

		if (pathNode2 != null && !pathNode2.visited) {
			successors[i++] = pathNode2;
		}

		if (pathNode3 != null && !pathNode3.visited) {
			successors[i++] = pathNode3;
		}

		if (pathNode4 != null && !pathNode4.visited) {
			successors[i++] = pathNode4;
		}

		if (pathNode5 != null && !pathNode5.visited) {
			successors[i++] = pathNode5;
		}

		if (pathNode6 != null && !pathNode6.visited) {
			successors[i++] = pathNode6;
		}

		boolean bl = pathNode4 == null || pathNode4.type == PathNodeType.field_7 || pathNode4.penalty != 0.0F;
		boolean bl2 = pathNode == null || pathNode.type == PathNodeType.field_7 || pathNode.penalty != 0.0F;
		boolean bl3 = pathNode3 == null || pathNode3.type == PathNodeType.field_7 || pathNode3.penalty != 0.0F;
		boolean bl4 = pathNode2 == null || pathNode2.type == PathNodeType.field_7 || pathNode2.penalty != 0.0F;
		if (bl && bl4) {
			PathNode pathNode7 = this.getPathNode(node.x - 1, node.y, node.z - 1, 1, d);
			if (pathNode7 != null && !pathNode7.visited) {
				successors[i++] = pathNode7;
			}
		}

		if (bl && bl3) {
			PathNode pathNode7 = this.getPathNode(node.x + 1, node.y, node.z - 1, 1, d);
			if (pathNode7 != null && !pathNode7.visited) {
				successors[i++] = pathNode7;
			}
		}

		if (bl2 && bl4) {
			PathNode pathNode7 = this.getPathNode(node.x - 1, node.y, node.z + 1, 1, d);
			if (pathNode7 != null && !pathNode7.visited) {
				successors[i++] = pathNode7;
			}
		}

		if (bl2 && bl3) {
			PathNode pathNode7 = this.getPathNode(node.x + 1, node.y, node.z + 1, 1, d);
			if (pathNode7 != null && !pathNode7.visited) {
				successors[i++] = pathNode7;
			}
		}

		return i;
	}

	private double getFeetY(BlockPos pos) {
		if (!this.entity.isTouchingWater()) {
			BlockPos blockPos = pos.method_10074();
			VoxelShape voxelShape = this.cachedWorld.getBlockState(blockPos).getCollisionShape(this.cachedWorld, blockPos);
			return (double)blockPos.getY() + (voxelShape.isEmpty() ? 0.0 : voxelShape.getMax(Direction.Axis.field_11052));
		} else {
			return (double)pos.getY() + 0.5;
		}
	}

	@Nullable
	private PathNode getPathNode(int x, int y, int z, int maxYStep, double prevFeetY) {
		PathNode pathNode = null;
		BlockPos blockPos = new BlockPos(x, y, z);
		double d = this.getFeetY(blockPos);
		if (d - prevFeetY > 1.125) {
			return null;
		} else {
			PathNodeType pathNodeType = this.getNodeType(
				this.cachedWorld, x, y, z, this.entity, this.entityBlockXSize, this.entityBlockYSize, this.entityBlockZSize, false, false
			);
			float f = this.entity.getPathfindingPenalty(pathNodeType);
			double e = (double)this.entity.getWidth() / 2.0;
			if (f >= 0.0F) {
				pathNode = this.getNode(x, y, z);
				pathNode.type = pathNodeType;
				pathNode.penalty = Math.max(pathNode.penalty, f);
			}

			if (pathNodeType != PathNodeType.field_18 && pathNodeType != PathNodeType.field_12) {
				if (pathNode == null
					&& maxYStep > 0
					&& pathNodeType != PathNodeType.field_10
					&& pathNodeType != PathNodeType.field_25418
					&& pathNodeType != PathNodeType.field_19) {
					pathNode = this.getPathNode(x, y + 1, z, maxYStep - 1, prevFeetY);
				}

				if (pathNodeType == PathNodeType.field_7) {
					Box box = new Box(
						(double)x - e + 0.5, (double)y + 0.001, (double)z - e + 0.5, (double)x + e + 0.5, (double)((float)y + this.entity.getHeight()), (double)z + e + 0.5
					);
					if (!this.entity.world.doesNotCollide(this.entity, box)) {
						return null;
					}

					PathNodeType pathNodeType2 = this.getNodeType(
						this.cachedWorld, x, y - 1, z, this.entity, this.entityBlockXSize, this.entityBlockYSize, this.entityBlockZSize, false, false
					);
					if (pathNodeType2 == PathNodeType.field_22) {
						pathNode = this.getNode(x, y, z);
						pathNode.type = PathNodeType.field_12;
						pathNode.penalty = Math.max(pathNode.penalty, f);
						return pathNode;
					}

					if (pathNodeType2 == PathNodeType.field_18) {
						pathNode = this.getNode(x, y, z);
						pathNode.type = PathNodeType.field_18;
						pathNode.penalty = Math.max(pathNode.penalty, f);
						return pathNode;
					}

					int i = 0;

					while (y > 0 && pathNodeType == PathNodeType.field_7) {
						y--;
						if (i++ >= this.entity.getSafeFallDistance()) {
							return null;
						}

						pathNodeType = this.getNodeType(this.cachedWorld, x, y, z, this.entity, this.entityBlockXSize, this.entityBlockYSize, this.entityBlockZSize, false, false);
						f = this.entity.getPathfindingPenalty(pathNodeType);
						if (pathNodeType != PathNodeType.field_7 && f >= 0.0F) {
							pathNode = this.getNode(x, y, z);
							pathNode.type = pathNodeType;
							pathNode.penalty = Math.max(pathNode.penalty, f);
							break;
						}

						if (f < 0.0F) {
							return null;
						}
					}
				}

				return pathNode;
			} else {
				if (y < this.entity.world.getSeaLevel() - 10 && pathNode != null) {
					pathNode.penalty++;
				}

				return pathNode;
			}
		}
	}

	@Override
	protected PathNodeType adjustNodeType(BlockView world, boolean canOpenDoors, boolean canEnterOpenDoors, BlockPos pos, PathNodeType type) {
		if (type == PathNodeType.field_21
			&& !(world.getBlockState(pos).getBlock() instanceof AbstractRailBlock)
			&& !(world.getBlockState(pos.method_10074()).getBlock() instanceof AbstractRailBlock)) {
			type = PathNodeType.field_25418;
		}

		if (type == PathNodeType.field_15 || type == PathNodeType.field_23 || type == PathNodeType.field_8) {
			type = PathNodeType.field_22;
		}

		if (type == PathNodeType.field_6) {
			type = PathNodeType.field_22;
		}

		return type;
	}

	@Override
	public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		PathNodeType pathNodeType = getCommonNodeType(world, mutable.set(x, y, z));
		if (pathNodeType == PathNodeType.field_18) {
			for (Direction direction : Direction.values()) {
				PathNodeType pathNodeType2 = getCommonNodeType(world, mutable.set(x, y, z).move(direction));
				if (pathNodeType2 == PathNodeType.field_22) {
					return PathNodeType.field_4;
				}
			}

			return PathNodeType.field_18;
		} else {
			if (pathNodeType == PathNodeType.field_7 && y >= 1) {
				BlockState blockState = world.getBlockState(new BlockPos(x, y - 1, z));
				PathNodeType pathNodeType3 = getCommonNodeType(world, mutable.set(x, y - 1, z));
				if (pathNodeType3 != PathNodeType.field_12 && pathNodeType3 != PathNodeType.field_7 && pathNodeType3 != PathNodeType.field_14) {
					pathNodeType = PathNodeType.field_12;
				} else {
					pathNodeType = PathNodeType.field_7;
				}

				if (pathNodeType3 == PathNodeType.field_3 || blockState.isOf(Blocks.field_10092) || blockState.isIn(BlockTags.field_23799)) {
					pathNodeType = PathNodeType.field_3;
				}

				if (pathNodeType3 == PathNodeType.field_11) {
					pathNodeType = PathNodeType.field_11;
				}

				if (pathNodeType3 == PathNodeType.field_17) {
					pathNodeType = PathNodeType.field_17;
				}
			}

			if (pathNodeType == PathNodeType.field_12) {
				pathNodeType = getNodeTypeFromNeighbors(world, mutable.set(x, y, z), pathNodeType);
			}

			return pathNodeType;
		}
	}
}
