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
import net.minecraft.world.chunk.ChunkCache;

public class AmphibiousPathNodeMaker extends LandPathNodeMaker {
	private float field_65;
	private float field_64;

	@Override
	public void init(ChunkCache chunkCache, MobEntity mobEntity) {
		super.init(chunkCache, mobEntity);
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
			MathHelper.floor(this.entity.getBoundingBox().minX),
			MathHelper.floor(this.entity.getBoundingBox().minY + 0.5),
			MathHelper.floor(this.entity.getBoundingBox().minZ)
		);
	}

	@Override
	public TargetPathNode getNode(double d, double e, double f) {
		return new TargetPathNode(this.getNode(MathHelper.floor(d), MathHelper.floor(e + 0.5), MathHelper.floor(f)));
	}

	@Override
	public int getSuccessors(PathNode[] pathNodes, PathNode pathNode) {
		int i = 0;
		int j = 1;
		BlockPos blockPos = new BlockPos(pathNode.x, pathNode.y, pathNode.z);
		double d = this.method_66(blockPos);
		PathNode pathNode2 = this.method_65(pathNode.x, pathNode.y, pathNode.z + 1, 1, d);
		PathNode pathNode3 = this.method_65(pathNode.x - 1, pathNode.y, pathNode.z, 1, d);
		PathNode pathNode4 = this.method_65(pathNode.x + 1, pathNode.y, pathNode.z, 1, d);
		PathNode pathNode5 = this.method_65(pathNode.x, pathNode.y, pathNode.z - 1, 1, d);
		PathNode pathNode6 = this.method_65(pathNode.x, pathNode.y + 1, pathNode.z, 0, d);
		PathNode pathNode7 = this.method_65(pathNode.x, pathNode.y - 1, pathNode.z, 1, d);
		if (pathNode2 != null && !pathNode2.visited) {
			pathNodes[i++] = pathNode2;
		}

		if (pathNode3 != null && !pathNode3.visited) {
			pathNodes[i++] = pathNode3;
		}

		if (pathNode4 != null && !pathNode4.visited) {
			pathNodes[i++] = pathNode4;
		}

		if (pathNode5 != null && !pathNode5.visited) {
			pathNodes[i++] = pathNode5;
		}

		if (pathNode6 != null && !pathNode6.visited) {
			pathNodes[i++] = pathNode6;
		}

		if (pathNode7 != null && !pathNode7.visited) {
			pathNodes[i++] = pathNode7;
		}

		boolean bl = pathNode5 == null || pathNode5.type == PathNodeType.OPEN || pathNode5.penalty != 0.0F;
		boolean bl2 = pathNode2 == null || pathNode2.type == PathNodeType.OPEN || pathNode2.penalty != 0.0F;
		boolean bl3 = pathNode4 == null || pathNode4.type == PathNodeType.OPEN || pathNode4.penalty != 0.0F;
		boolean bl4 = pathNode3 == null || pathNode3.type == PathNodeType.OPEN || pathNode3.penalty != 0.0F;
		if (bl && bl4) {
			PathNode pathNode8 = this.method_65(pathNode.x - 1, pathNode.y, pathNode.z - 1, 1, d);
			if (pathNode8 != null && !pathNode8.visited) {
				pathNodes[i++] = pathNode8;
			}
		}

		if (bl && bl3) {
			PathNode pathNode8 = this.method_65(pathNode.x + 1, pathNode.y, pathNode.z - 1, 1, d);
			if (pathNode8 != null && !pathNode8.visited) {
				pathNodes[i++] = pathNode8;
			}
		}

		if (bl2 && bl4) {
			PathNode pathNode8 = this.method_65(pathNode.x - 1, pathNode.y, pathNode.z + 1, 1, d);
			if (pathNode8 != null && !pathNode8.visited) {
				pathNodes[i++] = pathNode8;
			}
		}

		if (bl2 && bl3) {
			PathNode pathNode8 = this.method_65(pathNode.x + 1, pathNode.y, pathNode.z + 1, 1, d);
			if (pathNode8 != null && !pathNode8.visited) {
				pathNodes[i++] = pathNode8;
			}
		}

		return i;
	}

	private double method_66(BlockPos blockPos) {
		if (!this.entity.isInsideWater()) {
			BlockPos blockPos2 = blockPos.method_10074();
			VoxelShape voxelShape = this.field_20622.getBlockState(blockPos2).getCollisionShape(this.field_20622, blockPos2);
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
			PathNodeType pathNodeType = this.getNodeType(this.field_20622, i, j, k, this.entity, this.field_31, this.field_30, this.field_28, false, false);
			float f = this.entity.getPathfindingPenalty(pathNodeType);
			double g = (double)this.entity.getWidth() / 2.0;
			if (f >= 0.0F) {
				pathNode = this.getNode(i, j, k);
				pathNode.type = pathNodeType;
				pathNode.penalty = Math.max(pathNode.penalty, f);
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

					PathNodeType pathNodeType2 = this.getNodeType(this.field_20622, i, j - 1, k, this.entity, this.field_31, this.field_30, this.field_28, false, false);
					if (pathNodeType2 == PathNodeType.BLOCKED) {
						pathNode = this.getNode(i, j, k);
						pathNode.type = PathNodeType.WALKABLE;
						pathNode.penalty = Math.max(pathNode.penalty, f);
						return pathNode;
					}

					if (pathNodeType2 == PathNodeType.WATER) {
						pathNode = this.getNode(i, j, k);
						pathNode.type = PathNodeType.WATER;
						pathNode.penalty = Math.max(pathNode.penalty, f);
						return pathNode;
					}

					int m = 0;

					while (j > 0 && pathNodeType == PathNodeType.OPEN) {
						j--;
						if (m++ >= this.entity.getSafeFallDistance()) {
							return null;
						}

						pathNodeType = this.getNodeType(this.field_20622, i, j, k, this.entity, this.field_31, this.field_30, this.field_28, false, false);
						f = this.entity.getPathfindingPenalty(pathNodeType);
						if (pathNodeType != PathNodeType.OPEN && f >= 0.0F) {
							pathNode = this.getNode(i, j, k);
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
				if (j < this.entity.world.getSeaLevel() - 10 && pathNode != null) {
					pathNode.penalty++;
				}

				return pathNode;
			}
		}
	}

	@Override
	protected PathNodeType adjustNodeType(BlockView blockView, boolean bl, boolean bl2, BlockPos blockPos, PathNodeType pathNodeType) {
		if (pathNodeType == PathNodeType.RAIL
			&& !(blockView.getBlockState(blockPos).getBlock() instanceof AbstractRailBlock)
			&& !(blockView.getBlockState(blockPos.method_10074()).getBlock() instanceof AbstractRailBlock)) {
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
	public PathNodeType getNodeType(BlockView blockView, int i, int j, int k) {
		PathNodeType pathNodeType = getBasicPathNodeType(blockView, i, j, k);
		if (pathNodeType == PathNodeType.WATER) {
			for (Direction direction : Direction.values()) {
				PathNodeType pathNodeType2 = getBasicPathNodeType(blockView, i + direction.getOffsetX(), j + direction.getOffsetY(), k + direction.getOffsetZ());
				if (pathNodeType2 == PathNodeType.BLOCKED) {
					return PathNodeType.WATER_BORDER;
				}
			}

			return PathNodeType.WATER;
		} else {
			if (pathNodeType == PathNodeType.OPEN && j >= 1) {
				Block block = blockView.getBlockState(new BlockPos(i, j - 1, k)).getBlock();
				PathNodeType pathNodeType3 = getBasicPathNodeType(blockView, i, j - 1, k);
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

			return method_59(blockView, i, j, k, pathNodeType);
		}
	}
}
