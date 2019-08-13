package net.minecraft.entity.ai.pathing;

import javax.annotation.Nullable;
import net.minecraft.class_4459;
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
import net.minecraft.world.ViewableWorld;

public class AmphibiousPathNodeMaker extends LandPathNodeMaker {
	private float field_65;
	private float field_64;

	@Override
	public void init(ViewableWorld viewableWorld, MobEntity mobEntity) {
		super.init(viewableWorld, mobEntity);
		mobEntity.setPathNodeTypeWeight(PathNodeType.field_18, 0.0F);
		this.field_65 = mobEntity.getPathNodeTypeWeight(PathNodeType.field_12);
		mobEntity.setPathNodeTypeWeight(PathNodeType.field_12, 6.0F);
		this.field_64 = mobEntity.getPathNodeTypeWeight(PathNodeType.field_4);
		mobEntity.setPathNodeTypeWeight(PathNodeType.field_4, 4.0F);
	}

	@Override
	public void clear() {
		this.entity.setPathNodeTypeWeight(PathNodeType.field_12, this.field_65);
		this.entity.setPathNodeTypeWeight(PathNodeType.field_4, this.field_64);
		super.clear();
	}

	@Override
	public PathNode getStart() {
		return this.getPathNode(
			MathHelper.floor(this.entity.getBoundingBox().minX),
			MathHelper.floor(this.entity.getBoundingBox().minY + 0.5),
			MathHelper.floor(this.entity.getBoundingBox().minZ)
		);
	}

	@Override
	public class_4459 getPathNode(double d, double e, double f) {
		return new class_4459(this.getPathNode(MathHelper.floor(d), MathHelper.floor(e + 0.5), MathHelper.floor(f)));
	}

	@Override
	public int getPathNodes(PathNode[] pathNodes, PathNode pathNode) {
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
		if (pathNode2 != null && !pathNode2.field_42) {
			pathNodes[i++] = pathNode2;
		}

		if (pathNode3 != null && !pathNode3.field_42) {
			pathNodes[i++] = pathNode3;
		}

		if (pathNode4 != null && !pathNode4.field_42) {
			pathNodes[i++] = pathNode4;
		}

		if (pathNode5 != null && !pathNode5.field_42) {
			pathNodes[i++] = pathNode5;
		}

		if (pathNode6 != null && !pathNode6.field_42) {
			pathNodes[i++] = pathNode6;
		}

		if (pathNode7 != null && !pathNode7.field_42) {
			pathNodes[i++] = pathNode7;
		}

		boolean bl = pathNode5 == null || pathNode5.type == PathNodeType.field_7 || pathNode5.field_43 != 0.0F;
		boolean bl2 = pathNode2 == null || pathNode2.type == PathNodeType.field_7 || pathNode2.field_43 != 0.0F;
		boolean bl3 = pathNode4 == null || pathNode4.type == PathNodeType.field_7 || pathNode4.field_43 != 0.0F;
		boolean bl4 = pathNode3 == null || pathNode3.type == PathNodeType.field_7 || pathNode3.field_43 != 0.0F;
		if (bl && bl4) {
			PathNode pathNode8 = this.method_65(pathNode.x - 1, pathNode.y, pathNode.z - 1, 1, d);
			if (pathNode8 != null && !pathNode8.field_42) {
				pathNodes[i++] = pathNode8;
			}
		}

		if (bl && bl3) {
			PathNode pathNode8 = this.method_65(pathNode.x + 1, pathNode.y, pathNode.z - 1, 1, d);
			if (pathNode8 != null && !pathNode8.field_42) {
				pathNodes[i++] = pathNode8;
			}
		}

		if (bl2 && bl4) {
			PathNode pathNode8 = this.method_65(pathNode.x - 1, pathNode.y, pathNode.z + 1, 1, d);
			if (pathNode8 != null && !pathNode8.field_42) {
				pathNodes[i++] = pathNode8;
			}
		}

		if (bl2 && bl3) {
			PathNode pathNode8 = this.method_65(pathNode.x + 1, pathNode.y, pathNode.z + 1, 1, d);
			if (pathNode8 != null && !pathNode8.field_42) {
				pathNodes[i++] = pathNode8;
			}
		}

		return i;
	}

	private double method_66(BlockPos blockPos) {
		if (!this.entity.isInsideWater()) {
			BlockPos blockPos2 = blockPos.down();
			VoxelShape voxelShape = this.blockView.getBlockState(blockPos2).getCollisionShape(this.blockView, blockPos2);
			return (double)blockPos2.getY() + (voxelShape.isEmpty() ? 0.0 : voxelShape.getMaximum(Direction.Axis.field_11052));
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
			PathNodeType pathNodeType = this.getPathNodeType(this.blockView, i, j, k, this.entity, this.field_31, this.field_30, this.field_28, false, false);
			float f = this.entity.getPathNodeTypeWeight(pathNodeType);
			double g = (double)this.entity.getWidth() / 2.0;
			if (f >= 0.0F) {
				pathNode = this.getPathNode(i, j, k);
				pathNode.type = pathNodeType;
				pathNode.field_43 = Math.max(pathNode.field_43, f);
			}

			if (pathNodeType != PathNodeType.field_18 && pathNodeType != PathNodeType.field_12) {
				if (pathNode == null && l > 0 && pathNodeType != PathNodeType.field_10 && pathNodeType != PathNodeType.field_19) {
					pathNode = this.method_65(i, j + 1, k, l - 1, d);
				}

				if (pathNodeType == PathNodeType.field_7) {
					Box box = new Box(
						(double)i - g + 0.5, (double)j + 0.001, (double)k - g + 0.5, (double)i + g + 0.5, (double)((float)j + this.entity.getHeight()), (double)k + g + 0.5
					);
					if (!this.entity.world.doesNotCollide(this.entity, box)) {
						return null;
					}

					PathNodeType pathNodeType2 = this.getPathNodeType(this.blockView, i, j - 1, k, this.entity, this.field_31, this.field_30, this.field_28, false, false);
					if (pathNodeType2 == PathNodeType.field_22) {
						pathNode = this.getPathNode(i, j, k);
						pathNode.type = PathNodeType.field_12;
						pathNode.field_43 = Math.max(pathNode.field_43, f);
						return pathNode;
					}

					if (pathNodeType2 == PathNodeType.field_18) {
						pathNode = this.getPathNode(i, j, k);
						pathNode.type = PathNodeType.field_18;
						pathNode.field_43 = Math.max(pathNode.field_43, f);
						return pathNode;
					}

					int m = 0;

					while (j > 0 && pathNodeType == PathNodeType.field_7) {
						j--;
						if (m++ >= this.entity.getSafeFallDistance()) {
							return null;
						}

						pathNodeType = this.getPathNodeType(this.blockView, i, j, k, this.entity, this.field_31, this.field_30, this.field_28, false, false);
						f = this.entity.getPathNodeTypeWeight(pathNodeType);
						if (pathNodeType != PathNodeType.field_7 && f >= 0.0F) {
							pathNode = this.getPathNode(i, j, k);
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
		if (pathNodeType == PathNodeType.field_21
			&& !(blockView.getBlockState(blockPos).getBlock() instanceof AbstractRailBlock)
			&& !(blockView.getBlockState(blockPos.down()).getBlock() instanceof AbstractRailBlock)) {
			pathNodeType = PathNodeType.field_10;
		}

		if (pathNodeType == PathNodeType.field_15 || pathNodeType == PathNodeType.field_23 || pathNodeType == PathNodeType.field_8) {
			pathNodeType = PathNodeType.field_22;
		}

		if (pathNodeType == PathNodeType.field_6) {
			pathNodeType = PathNodeType.field_22;
		}

		return pathNodeType;
	}

	@Override
	public PathNodeType getPathNodeType(BlockView blockView, int i, int j, int k) {
		PathNodeType pathNodeType = this.getBasicPathNodeType(blockView, i, j, k);
		if (pathNodeType == PathNodeType.field_18) {
			for (Direction direction : Direction.values()) {
				PathNodeType pathNodeType2 = this.getBasicPathNodeType(blockView, i + direction.getOffsetX(), j + direction.getOffsetY(), k + direction.getOffsetZ());
				if (pathNodeType2 == PathNodeType.field_22) {
					return PathNodeType.field_4;
				}
			}

			return PathNodeType.field_18;
		} else {
			if (pathNodeType == PathNodeType.field_7 && j >= 1) {
				Block block = blockView.getBlockState(new BlockPos(i, j - 1, k)).getBlock();
				PathNodeType pathNodeType3 = this.getBasicPathNodeType(blockView, i, j - 1, k);
				if (pathNodeType3 != PathNodeType.field_12 && pathNodeType3 != PathNodeType.field_7 && pathNodeType3 != PathNodeType.field_14) {
					pathNodeType = PathNodeType.field_12;
				} else {
					pathNodeType = PathNodeType.field_7;
				}

				if (pathNodeType3 == PathNodeType.field_3 || block == Blocks.field_10092 || block == Blocks.field_17350) {
					pathNodeType = PathNodeType.field_3;
				}

				if (pathNodeType3 == PathNodeType.field_11) {
					pathNodeType = PathNodeType.field_11;
				}

				if (pathNodeType3 == PathNodeType.field_17) {
					pathNodeType = PathNodeType.field_17;
				}
			}

			return this.method_59(blockView, i, j, k, pathNodeType);
		}
	}
}
