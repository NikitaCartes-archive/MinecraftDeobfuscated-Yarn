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
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;

public class LandPathNodeMaker extends PathNodeMaker {
	protected float waterPathNodeTypeWeight;

	@Override
	public void init(ViewableWorld viewableWorld, MobEntity mobEntity) {
		super.init(viewableWorld, mobEntity);
		this.waterPathNodeTypeWeight = mobEntity.getPathNodeTypeWeight(PathNodeType.field_18);
	}

	@Override
	public void clear() {
		this.entity.setPathNodeTypeWeight(PathNodeType.field_18, this.waterPathNodeTypeWeight);
		super.clear();
	}

	@Override
	public PathNode getStart() {
		int i;
		if (this.canSwim() && this.entity.isInsideWater()) {
			i = MathHelper.floor(this.entity.getBoundingBox().minY);
			BlockPos.Mutable mutable = new BlockPos.Mutable(this.entity.x, (double)i, this.entity.z);

			for (Block block = this.blockView.getBlockState(mutable).getBlock(); block == Blocks.field_10382; block = this.blockView.getBlockState(mutable).getBlock()) {
				mutable.set(this.entity.x, (double)(++i), this.entity.z);
			}

			i--;
		} else if (this.entity.onGround) {
			i = MathHelper.floor(this.entity.getBoundingBox().minY + 0.5);
		} else {
			BlockPos blockPos = new BlockPos(this.entity);

			while (
				(
						this.blockView.getBlockState(blockPos).isAir()
							|| this.blockView.getBlockState(blockPos).canPlaceAtSide(this.blockView, blockPos, BlockPlacementEnvironment.field_50)
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
	public PathNode getPathNode(double d, double e, double f) {
		return this.getPathNode(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
	}

	@Override
	public int getPathNodes(PathNode[] pathNodes, PathNode pathNode) {
		int i = 0;
		int j = 0;
		PathNodeType pathNodeType = this.getPathNodeType(this.entity, pathNode.x, pathNode.y + 1, pathNode.z);
		if (this.entity.getPathNodeTypeWeight(pathNodeType) >= 0.0F) {
			j = MathHelper.floor(Math.max(1.0F, this.entity.stepHeight));
		}

		double d = method_60(this.blockView, new BlockPos(pathNode.x, pathNode.y, pathNode.z));
		PathNode pathNode2 = this.getPathNode(pathNode.x, pathNode.y, pathNode.z + 1, j, d, Direction.field_11035);
		if (pathNode2 != null && !pathNode2.field_42 && pathNode2.field_43 >= 0.0F) {
			pathNodes[i++] = pathNode2;
		}

		PathNode pathNode3 = this.getPathNode(pathNode.x - 1, pathNode.y, pathNode.z, j, d, Direction.field_11039);
		if (pathNode3 != null && !pathNode3.field_42 && pathNode3.field_43 >= 0.0F) {
			pathNodes[i++] = pathNode3;
		}

		PathNode pathNode4 = this.getPathNode(pathNode.x + 1, pathNode.y, pathNode.z, j, d, Direction.field_11034);
		if (pathNode4 != null && !pathNode4.field_42 && pathNode4.field_43 >= 0.0F) {
			pathNodes[i++] = pathNode4;
		}

		PathNode pathNode5 = this.getPathNode(pathNode.x, pathNode.y, pathNode.z - 1, j, d, Direction.field_11043);
		if (pathNode5 != null && !pathNode5.field_42 && pathNode5.field_43 >= 0.0F) {
			pathNodes[i++] = pathNode5;
		}

		PathNode pathNode6 = this.getPathNode(pathNode.x - 1, pathNode.y, pathNode.z - 1, j, d, Direction.field_11043);
		if (this.method_20536(pathNode, pathNode3, pathNode5, pathNode6)) {
			pathNodes[i++] = pathNode6;
		}

		PathNode pathNode7 = this.getPathNode(pathNode.x + 1, pathNode.y, pathNode.z - 1, j, d, Direction.field_11043);
		if (this.method_20536(pathNode, pathNode4, pathNode5, pathNode7)) {
			pathNodes[i++] = pathNode7;
		}

		PathNode pathNode8 = this.getPathNode(pathNode.x - 1, pathNode.y, pathNode.z + 1, j, d, Direction.field_11035);
		if (this.method_20536(pathNode, pathNode3, pathNode2, pathNode8)) {
			pathNodes[i++] = pathNode8;
		}

		PathNode pathNode9 = this.getPathNode(pathNode.x + 1, pathNode.y, pathNode.z + 1, j, d, Direction.field_11035);
		if (this.method_20536(pathNode, pathNode4, pathNode2, pathNode9)) {
			pathNodes[i++] = pathNode9;
		}

		return i;
	}

	private boolean method_20536(PathNode pathNode, @Nullable PathNode pathNode2, @Nullable PathNode pathNode3, @Nullable PathNode pathNode4) {
		return pathNode4 != null
			&& !pathNode4.field_42
			&& pathNode3 != null
			&& pathNode3.field_43 >= 0.0F
			&& pathNode3.y <= pathNode.y
			&& pathNode2 != null
			&& pathNode2.field_43 >= 0.0F
			&& pathNode2.y <= pathNode.y;
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
		double e = method_60(this.blockView, blockPos);
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

			if (pathNodeType == PathNodeType.field_12) {
				return pathNode;
			} else {
				if ((pathNode == null || pathNode.field_43 < 0.0F) && l > 0 && pathNodeType != PathNodeType.field_10 && pathNodeType != PathNodeType.field_19) {
					pathNode = this.getPathNode(i, j + 1, k, l - 1, d, direction);
					if (pathNode != null && (pathNode.type == PathNodeType.field_7 || pathNode.type == PathNodeType.field_12) && this.entity.getWidth() < 1.0F) {
						double h = (double)(i - direction.getOffsetX()) + 0.5;
						double m = (double)(k - direction.getOffsetZ()) + 0.5;
						Box box = new Box(
							h - g,
							method_60(this.blockView, new BlockPos(h, (double)(j + 1), m)) + 0.001,
							m - g,
							h + g,
							(double)this.entity.getHeight() + method_60(this.blockView, new BlockPos(pathNode.x, pathNode.y, pathNode.z)) - 0.002,
							m + g
						);
						if (!this.blockView.doesNotCollide(this.entity, box)) {
							pathNode = null;
						}
					}
				}

				if (pathNodeType == PathNodeType.field_18 && !this.canSwim()) {
					if (this.getPathNodeType(this.entity, i, j - 1, k) != PathNodeType.field_18) {
						return pathNode;
					}

					while (j > 0) {
						pathNodeType = this.getPathNodeType(this.entity, i, --j, k);
						if (pathNodeType != PathNodeType.field_18) {
							return pathNode;
						}

						pathNode = this.getPathNode(i, j, k);
						pathNode.type = pathNodeType;
						pathNode.field_43 = Math.max(pathNode.field_43, this.entity.getPathNodeTypeWeight(pathNodeType));
					}
				}

				if (pathNodeType == PathNodeType.field_7) {
					Box box2 = new Box(
						(double)i - g + 0.5, (double)j + 0.001, (double)k - g + 0.5, (double)i + g + 0.5, (double)((float)j + this.entity.getHeight()), (double)k + g + 0.5
					);
					if (!this.blockView.doesNotCollide(this.entity, box2)) {
						return null;
					}

					if (this.entity.getWidth() >= 1.0F) {
						PathNodeType pathNodeType2 = this.getPathNodeType(this.entity, i, j - 1, k);
						if (pathNodeType2 == PathNodeType.field_22) {
							pathNode = this.getPathNode(i, j, k);
							pathNode.type = PathNodeType.field_12;
							pathNode.field_43 = Math.max(pathNode.field_43, f);
							return pathNode;
						}
					}

					int n = 0;
					int o = j;

					while (pathNodeType == PathNodeType.field_7) {
						if (--j < 0) {
							PathNode pathNode2 = this.getPathNode(i, o, k);
							pathNode2.type = PathNodeType.field_22;
							pathNode2.field_43 = -1.0F;
							return pathNode2;
						}

						PathNode pathNode2 = this.getPathNode(i, j, k);
						if (n++ >= this.entity.getSafeFallDistance()) {
							pathNode2.type = PathNodeType.field_22;
							pathNode2.field_43 = -1.0F;
							return pathNode2;
						}

						pathNodeType = this.getPathNodeType(this.entity, i, j, k);
						f = this.entity.getPathNodeTypeWeight(pathNodeType);
						if (pathNodeType != PathNodeType.field_7 && f >= 0.0F) {
							pathNode = pathNode2;
							pathNode2.type = pathNodeType;
							pathNode2.field_43 = Math.max(pathNode2.field_43, f);
							break;
						}

						if (f < 0.0F) {
							pathNode2.type = PathNodeType.field_22;
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
		PathNodeType pathNodeType = PathNodeType.field_22;
		double d = (double)mobEntity.getWidth() / 2.0;
		BlockPos blockPos = new BlockPos(mobEntity);
		pathNodeType = this.method_64(blockView, i, j, k, l, m, n, bl, bl2, enumSet, pathNodeType, blockPos);
		if (enumSet.contains(PathNodeType.field_10)) {
			return PathNodeType.field_10;
		} else {
			PathNodeType pathNodeType2 = PathNodeType.field_22;

			for (PathNodeType pathNodeType3 : enumSet) {
				if (mobEntity.getPathNodeTypeWeight(pathNodeType3) < 0.0F) {
					return pathNodeType3;
				}

				if (mobEntity.getPathNodeTypeWeight(pathNodeType3) >= mobEntity.getPathNodeTypeWeight(pathNodeType2)) {
					pathNodeType2 = pathNodeType3;
				}
			}

			return pathNodeType == PathNodeType.field_7 && mobEntity.getPathNodeTypeWeight(pathNodeType2) == 0.0F ? PathNodeType.field_7 : pathNodeType2;
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
		if (pathNodeType == PathNodeType.field_23 && bl && bl2) {
			pathNodeType = PathNodeType.field_12;
		}

		if (pathNodeType == PathNodeType.field_15 && !bl2) {
			pathNodeType = PathNodeType.field_22;
		}

		if (pathNodeType == PathNodeType.field_21
			&& !(blockView.getBlockState(blockPos).getBlock() instanceof AbstractRailBlock)
			&& !(blockView.getBlockState(blockPos.down()).getBlock() instanceof AbstractRailBlock)) {
			pathNodeType = PathNodeType.field_10;
		}

		if (pathNodeType == PathNodeType.field_6) {
			pathNodeType = PathNodeType.field_22;
		}

		return pathNodeType;
	}

	private PathNodeType getPathNodeType(MobEntity mobEntity, BlockPos blockPos) {
		return this.getPathNodeType(mobEntity, blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	private PathNodeType getPathNodeType(MobEntity mobEntity, int i, int j, int k) {
		return this.getPathNodeType(
			this.blockView, i, j, k, mobEntity, this.field_31, this.field_30, this.field_28, this.canPathThroughDoors(), this.canEnterOpenDoors()
		);
	}

	@Override
	public PathNodeType getPathNodeType(BlockView blockView, int i, int j, int k) {
		PathNodeType pathNodeType = this.getBasicPathNodeType(blockView, i, j, k);
		if (pathNodeType == PathNodeType.field_7 && j >= 1) {
			Block block = blockView.getBlockState(new BlockPos(i, j - 1, k)).getBlock();
			PathNodeType pathNodeType2 = this.getBasicPathNodeType(blockView, i, j - 1, k);
			pathNodeType = pathNodeType2 != PathNodeType.field_12
					&& pathNodeType2 != PathNodeType.field_7
					&& pathNodeType2 != PathNodeType.field_18
					&& pathNodeType2 != PathNodeType.field_14
				? PathNodeType.field_12
				: PathNodeType.field_7;
			if (pathNodeType2 == PathNodeType.field_3 || block == Blocks.field_10092 || block == Blocks.field_17350) {
				pathNodeType = PathNodeType.field_3;
			}

			if (pathNodeType2 == PathNodeType.field_11) {
				pathNodeType = PathNodeType.field_11;
			}

			if (pathNodeType2 == PathNodeType.field_17) {
				pathNodeType = PathNodeType.field_17;
			}
		}

		return this.method_59(blockView, i, j, k, pathNodeType);
	}

	public PathNodeType method_59(BlockView blockView, int i, int j, int k, PathNodeType pathNodeType) {
		if (pathNodeType == PathNodeType.field_12) {
			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (int l = -1; l <= 1; l++) {
					for (int m = -1; m <= 1; m++) {
						if (l != 0 || m != 0) {
							Block block = blockView.getBlockState(pooledMutable.method_10113(l + i, j, m + k)).getBlock();
							if (block == Blocks.field_10029) {
								pathNodeType = PathNodeType.field_20;
							} else if (block == Blocks.field_10036) {
								pathNodeType = PathNodeType.field_9;
							} else if (block == Blocks.field_16999) {
								pathNodeType = PathNodeType.field_5;
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
			return PathNodeType.field_7;
		} else if (block.matches(BlockTags.field_15487) || block == Blocks.field_10588) {
			return PathNodeType.field_19;
		} else if (block == Blocks.field_10036) {
			return PathNodeType.field_3;
		} else if (block == Blocks.field_10029) {
			return PathNodeType.field_11;
		} else if (block == Blocks.field_16999) {
			return PathNodeType.field_17;
		} else if (block instanceof DoorBlock && material == Material.WOOD && !(Boolean)blockState.get(DoorBlock.OPEN)) {
			return PathNodeType.field_23;
		} else if (block instanceof DoorBlock && material == Material.METAL && !(Boolean)blockState.get(DoorBlock.OPEN)) {
			return PathNodeType.field_8;
		} else if (block instanceof DoorBlock && (Boolean)blockState.get(DoorBlock.OPEN)) {
			return PathNodeType.field_15;
		} else if (block instanceof AbstractRailBlock) {
			return PathNodeType.field_21;
		} else if (block instanceof LeavesBlock) {
			return PathNodeType.field_6;
		} else if (!block.matches(BlockTags.field_16584)
			&& !block.matches(BlockTags.field_15504)
			&& (!(block instanceof FenceGateBlock) || (Boolean)blockState.get(FenceGateBlock.OPEN))) {
			FluidState fluidState = blockView.getFluidState(blockPos);
			if (fluidState.matches(FluidTags.field_15517)) {
				return PathNodeType.field_18;
			} else if (fluidState.matches(FluidTags.field_15518)) {
				return PathNodeType.field_14;
			} else {
				return blockState.canPlaceAtSide(blockView, blockPos, BlockPlacementEnvironment.field_50) ? PathNodeType.field_7 : PathNodeType.field_22;
			}
		} else {
			return PathNodeType.field_10;
		}
	}
}
