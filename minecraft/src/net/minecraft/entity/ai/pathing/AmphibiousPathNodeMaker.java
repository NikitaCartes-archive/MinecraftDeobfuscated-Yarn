package net.minecraft.entity.ai.pathing;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;

public class AmphibiousPathNodeMaker extends LandPathNodeMaker {
	private float field_65;
	private float field_64;

	@Override
	public void init(CollisionView collisionView, MobEntity mobEntity) {
		super.init(collisionView, mobEntity);
		mobEntity.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
		this.field_65 = mobEntity.getPathfindingPenalty(PathNodeType.WALKABLE);
		mobEntity.setPathfindingPenalty(PathNodeType.WALKABLE, 6.0F);
		this.field_64 = mobEntity.getPathfindingPenalty(PathNodeType.WATER_BORDER);
		mobEntity.setPathfindingPenalty(PathNodeType.WATER_BORDER, 4.0F);
	}

	@Override
	public void clear() {
		this.entity.setPathfindingPenalty(PathNodeType.WALKABLE, this.field_65);
		this.entity.setPathfindingPenalty(PathNodeType.WATER_BORDER, this.field_64);
		super.clear();
	}

	@Override
	public PathNode getStart() {
		return this.getNode(
			MathHelper.floor(this.entity.getBoundingBox().x1),
			MathHelper.floor(this.entity.getBoundingBox().y1 + 0.5),
			MathHelper.floor(this.entity.getBoundingBox().z1)
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
		double d = this.method_66(blockPos);
		PathNode pathNode = this.method_65(node.x, node.y, node.z + 1, 1, d);
		PathNode pathNode2 = this.method_65(node.x - 1, node.y, node.z, 1, d);
		PathNode pathNode3 = this.method_65(node.x + 1, node.y, node.z, 1, d);
		PathNode pathNode4 = this.method_65(node.x, node.y, node.z - 1, 1, d);
		PathNode pathNode5 = this.method_65(node.x, node.y + 1, node.z, 0, d);
		PathNode pathNode6 = this.method_65(node.x, node.y - 1, node.z, 1, d);
		if (pathNode != null && !pathNode.field_42) {
			successors[i++] = pathNode;
		}

		if (pathNode2 != null && !pathNode2.field_42) {
			successors[i++] = pathNode2;
		}

		if (pathNode3 != null && !pathNode3.field_42) {
			successors[i++] = pathNode3;
		}

		if (pathNode4 != null && !pathNode4.field_42) {
			successors[i++] = pathNode4;
		}

		if (pathNode5 != null && !pathNode5.field_42) {
			successors[i++] = pathNode5;
		}

		if (pathNode6 != null && !pathNode6.field_42) {
			successors[i++] = pathNode6;
		}

		boolean bl = pathNode4 == null || pathNode4.type == PathNodeType.OPEN || pathNode4.field_43 != 0.0F;
		boolean bl2 = pathNode == null || pathNode.type == PathNodeType.OPEN || pathNode.field_43 != 0.0F;
		boolean bl3 = pathNode3 == null || pathNode3.type == PathNodeType.OPEN || pathNode3.field_43 != 0.0F;
		boolean bl4 = pathNode2 == null || pathNode2.type == PathNodeType.OPEN || pathNode2.field_43 != 0.0F;
		if (bl && bl4) {
			PathNode pathNode7 = this.method_65(node.x - 1, node.y, node.z - 1, 1, d);
			if (pathNode7 != null && !pathNode7.field_42) {
				successors[i++] = pathNode7;
			}
		}

		if (bl && bl3) {
			PathNode pathNode7 = this.method_65(node.x + 1, node.y, node.z - 1, 1, d);
			if (pathNode7 != null && !pathNode7.field_42) {
				successors[i++] = pathNode7;
			}
		}

		if (bl2 && bl4) {
			PathNode pathNode7 = this.method_65(node.x - 1, node.y, node.z + 1, 1, d);
			if (pathNode7 != null && !pathNode7.field_42) {
				successors[i++] = pathNode7;
			}
		}

		if (bl2 && bl3) {
			PathNode pathNode7 = this.method_65(node.x + 1, node.y, node.z + 1, 1, d);
			if (pathNode7 != null && !pathNode7.field_42) {
				successors[i++] = pathNode7;
			}
		}

		return i;
	}

	private double method_66(BlockPos blockPos) {
		if (!this.entity.isTouchingWater()) {
			BlockPos blockPos2 = blockPos.down();
			VoxelShape voxelShape = this.blockView.getBlockState(blockPos2).getCollisionShape(this.blockView, blockPos2);
			return (double)blockPos2.getY() + (voxelShape.isEmpty() ? 0.0 : voxelShape.getMaximum(Direction.Axis.Y));
		} else {
			return (double)blockPos.getY() + 0.5;
		}
	}

	@Nullable
	private PathNode method_65(int i, int j, int k, int l, double d) {
		PathNode pathNode = null;
		BlockPos blockPos = new BlockPos(i, j, k);
		double e = this.method_66(blockPos);
		if (e - d > 1.125) {
			return null;
		} else {
			PathNodeType pathNodeType = this.getNodeType(this.blockView, i, j, k, this.entity, this.field_31, this.field_30, this.field_28, false, false);
			float f = this.entity.getPathfindingPenalty(pathNodeType);
			double g = (double)this.entity.getWidth() / 2.0;
			if (f >= 0.0F) {
				pathNode = this.getNode(i, j, k);
				pathNode.type = pathNodeType;
				pathNode.field_43 = Math.max(pathNode.field_43, f);
			}

			if (pathNodeType != PathNodeType.WATER && pathNodeType != PathNodeType.WALKABLE) {
				if (pathNode == null && l > 0 && pathNodeType != PathNodeType.FENCE && pathNodeType != PathNodeType.TRAPDOOR) {
					pathNode = this.method_65(i, j + 1, k, l - 1, d);
				}

				if (pathNodeType == PathNodeType.OPEN) {
					Box box = new Box(
						(double)i - g + 0.5, (double)j + 0.001, (double)k - g + 0.5, (double)i + g + 0.5, (double)((float)j + this.entity.getHeight()), (double)k + g + 0.5
					);
					if (!this.entity.world.doesNotCollide(this.entity, box)) {
						return null;
					}

					PathNodeType pathNodeType2 = this.getNodeType(this.blockView, i, j - 1, k, this.entity, this.field_31, this.field_30, this.field_28, false, false);
					if (pathNodeType2 == PathNodeType.BLOCKED) {
						pathNode = this.getNode(i, j, k);
						pathNode.type = PathNodeType.WALKABLE;
						pathNode.field_43 = Math.max(pathNode.field_43, f);
						return pathNode;
					}

					if (pathNodeType2 == PathNodeType.WATER) {
						pathNode = this.getNode(i, j, k);
						pathNode.type = PathNodeType.WATER;
						pathNode.field_43 = Math.max(pathNode.field_43, f);
						return pathNode;
					}

					int m = 0;

					while (j > 0 && pathNodeType == PathNodeType.OPEN) {
						j--;
						if (m++ >= this.entity.getSafeFallDistance()) {
							return null;
						}

						pathNodeType = this.getNodeType(this.blockView, i, j, k, this.entity, this.field_31, this.field_30, this.field_28, false, false);
						f = this.entity.getPathfindingPenalty(pathNodeType);
						if (pathNodeType != PathNodeType.OPEN && f >= 0.0F) {
							pathNode = this.getNode(i, j, k);
							pathNode.type = pathNodeType;
							pathNode.field_43 = Math.max(pathNode.field_43, f);
							break;
						}

						if (f < 0.0F) {
							return null;
						}
					}
				}

				return pathNode;
			} else {
				if (j < this.entity.world.getSeaLevel() - 10 && pathNode != null) {
					pathNode.field_43++;
				}

				return pathNode;
			}
		}
	}

	@Override
	protected PathNodeType method_61(BlockView blockView, boolean bl, boolean bl2, BlockPos blockPos, PathNodeType pathNodeType) {
		if (pathNodeType == PathNodeType.RAIL
			&& !(blockView.getBlockState(blockPos).getBlock() instanceof AbstractRailBlock)
			&& !(blockView.getBlockState(blockPos.down()).getBlock() instanceof AbstractRailBlock)) {
			pathNodeType = PathNodeType.FENCE;
		}

		if (pathNodeType == PathNodeType.DOOR_OPEN || pathNodeType == PathNodeType.DOOR_WOOD_CLOSED || pathNodeType == PathNodeType.DOOR_IRON_CLOSED) {
			pathNodeType = PathNodeType.BLOCKED;
		}

		if (pathNodeType == PathNodeType.LEAVES) {
			pathNodeType = PathNodeType.BLOCKED;
		}

		return pathNodeType;
	}

	@Override
	public PathNodeType getNodeType(BlockView world, int x, int y, int z) {
		PathNodeType pathNodeType = this.getBasicPathNodeType(world, x, y, z);
		if (pathNodeType == PathNodeType.WATER) {
			for (Direction direction : Direction.values()) {
				PathNodeType pathNodeType2 = this.getBasicPathNodeType(world, x + direction.getOffsetX(), y + direction.getOffsetY(), z + direction.getOffsetZ());
				if (pathNodeType2 == PathNodeType.BLOCKED) {
					return PathNodeType.WATER_BORDER;
				}
			}

			return PathNodeType.WATER;
		} else {
			if (pathNodeType == PathNodeType.OPEN && y >= 1) {
				Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
				PathNodeType pathNodeType3 = this.getBasicPathNodeType(world, x, y - 1, z);
				if (pathNodeType3 != PathNodeType.WALKABLE && pathNodeType3 != PathNodeType.OPEN && pathNodeType3 != PathNodeType.LAVA) {
					pathNodeType = PathNodeType.WALKABLE;
				} else {
					pathNodeType = PathNodeType.OPEN;
				}

				if (pathNodeType3 == PathNodeType.DAMAGE_FIRE || block == Blocks.MAGMA_BLOCK || block == Blocks.CAMPFIRE) {
					pathNodeType = PathNodeType.DAMAGE_FIRE;
				}

				if (pathNodeType3 == PathNodeType.DAMAGE_CACTUS) {
					pathNodeType = PathNodeType.DAMAGE_CACTUS;
				}

				if (pathNodeType3 == PathNodeType.DAMAGE_OTHER) {
					pathNodeType = PathNodeType.DAMAGE_OTHER;
				}
			}

			return this.method_59(world, x, y, z, pathNodeType);
		}
	}
}
