package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;

public class BirdPathNodeMaker extends LandPathNodeMaker {
	@Override
	public void init(CollisionView collisionView, MobEntity mobEntity) {
		super.init(collisionView, mobEntity);
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
		if (this.canSwim() && this.entity.isTouchingWater()) {
			i = MathHelper.floor(this.entity.getBoundingBox().y1);
			BlockPos.Mutable mutable = new BlockPos.Mutable(this.entity.x, (double)i, this.entity.z);

			for (Block block = this.blockView.getBlockState(mutable).getBlock(); block == Blocks.WATER; block = this.blockView.getBlockState(mutable).getBlock()) {
				mutable.set(this.entity.x, (double)(++i), this.entity.z);
			}
		} else {
			i = MathHelper.floor(this.entity.getBoundingBox().y1 + 0.5);
		}

		BlockPos blockPos = new BlockPos(this.entity);
		PathNodeType pathNodeType = this.method_9(this.entity, blockPos.getX(), i, blockPos.getZ());
		if (this.entity.getPathfindingPenalty(pathNodeType) < 0.0F) {
			Set<BlockPos> set = Sets.<BlockPos>newHashSet();
			set.add(new BlockPos(this.entity.getBoundingBox().x1, (double)i, this.entity.getBoundingBox().z1));
			set.add(new BlockPos(this.entity.getBoundingBox().x1, (double)i, this.entity.getBoundingBox().z2));
			set.add(new BlockPos(this.entity.getBoundingBox().x2, (double)i, this.entity.getBoundingBox().z1));
			set.add(new BlockPos(this.entity.getBoundingBox().x2, (double)i, this.entity.getBoundingBox().z2));

			for (BlockPos blockPos2 : set) {
				PathNodeType pathNodeType2 = this.method_10(this.entity, blockPos2);
				if (this.entity.getPathfindingPenalty(pathNodeType2) >= 0.0F) {
					return super.getNode(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
				}
			}
		}

		return super.getNode(blockPos.getX(), i, blockPos.getZ());
	}

	@Override
	public TargetPathNode getNode(double x, double y, double z) {
		return new TargetPathNode(super.getNode(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)));
	}

	@Override
	public int getSuccessors(PathNode[] successors, PathNode node) {
		int i = 0;
		PathNode pathNode = this.getNode(node.x, node.y, node.z + 1);
		PathNode pathNode2 = this.getNode(node.x - 1, node.y, node.z);
		PathNode pathNode3 = this.getNode(node.x + 1, node.y, node.z);
		PathNode pathNode4 = this.getNode(node.x, node.y, node.z - 1);
		PathNode pathNode5 = this.getNode(node.x, node.y + 1, node.z);
		PathNode pathNode6 = this.getNode(node.x, node.y - 1, node.z);
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

		boolean bl = pathNode4 == null || pathNode4.field_43 != 0.0F;
		boolean bl2 = pathNode == null || pathNode.field_43 != 0.0F;
		boolean bl3 = pathNode3 == null || pathNode3.field_43 != 0.0F;
		boolean bl4 = pathNode2 == null || pathNode2.field_43 != 0.0F;
		boolean bl5 = pathNode5 == null || pathNode5.field_43 != 0.0F;
		boolean bl6 = pathNode6 == null || pathNode6.field_43 != 0.0F;
		if (bl && bl4) {
			PathNode pathNode7 = this.getNode(node.x - 1, node.y, node.z - 1);
			if (pathNode7 != null && !pathNode7.field_42) {
				successors[i++] = pathNode7;
			}
		}

		if (bl && bl3) {
			PathNode pathNode7 = this.getNode(node.x + 1, node.y, node.z - 1);
			if (pathNode7 != null && !pathNode7.field_42) {
				successors[i++] = pathNode7;
			}
		}

		if (bl2 && bl4) {
			PathNode pathNode7 = this.getNode(node.x - 1, node.y, node.z + 1);
			if (pathNode7 != null && !pathNode7.field_42) {
				successors[i++] = pathNode7;
			}
		}

		if (bl2 && bl3) {
			PathNode pathNode7 = this.getNode(node.x + 1, node.y, node.z + 1);
			if (pathNode7 != null && !pathNode7.field_42) {
				successors[i++] = pathNode7;
			}
		}

		if (bl && bl5) {
			PathNode pathNode7 = this.getNode(node.x, node.y + 1, node.z - 1);
			if (pathNode7 != null && !pathNode7.field_42) {
				successors[i++] = pathNode7;
			}
		}

		if (bl2 && bl5) {
			PathNode pathNode7 = this.getNode(node.x, node.y + 1, node.z + 1);
			if (pathNode7 != null && !pathNode7.field_42) {
				successors[i++] = pathNode7;
			}
		}

		if (bl3 && bl5) {
			PathNode pathNode7 = this.getNode(node.x + 1, node.y + 1, node.z);
			if (pathNode7 != null && !pathNode7.field_42) {
				successors[i++] = pathNode7;
			}
		}

		if (bl4 && bl5) {
			PathNode pathNode7 = this.getNode(node.x - 1, node.y + 1, node.z);
			if (pathNode7 != null && !pathNode7.field_42) {
				successors[i++] = pathNode7;
			}
		}

		if (bl && bl6) {
			PathNode pathNode7 = this.getNode(node.x, node.y - 1, node.z - 1);
			if (pathNode7 != null && !pathNode7.field_42) {
				successors[i++] = pathNode7;
			}
		}

		if (bl2 && bl6) {
			PathNode pathNode7 = this.getNode(node.x, node.y - 1, node.z + 1);
			if (pathNode7 != null && !pathNode7.field_42) {
				successors[i++] = pathNode7;
			}
		}

		if (bl3 && bl6) {
			PathNode pathNode7 = this.getNode(node.x + 1, node.y - 1, node.z);
			if (pathNode7 != null && !pathNode7.field_42) {
				successors[i++] = pathNode7;
			}
		}

		if (bl4 && bl6) {
			PathNode pathNode7 = this.getNode(node.x - 1, node.y - 1, node.z);
			if (pathNode7 != null && !pathNode7.field_42) {
				successors[i++] = pathNode7;
			}
		}

		return i;
	}

	@Nullable
	@Override
	protected PathNode getNode(int x, int y, int z) {
		PathNode pathNode = null;
		PathNodeType pathNodeType = this.method_9(this.entity, x, y, z);
		float f = this.entity.getPathfindingPenalty(pathNodeType);
		if (f >= 0.0F) {
			pathNode = super.getNode(x, y, z);
			pathNode.type = pathNodeType;
			pathNode.field_43 = Math.max(pathNode.field_43, f);
			if (pathNodeType == PathNodeType.WALKABLE) {
				pathNode.field_43++;
			}
		}

		return pathNodeType != PathNodeType.OPEN && pathNodeType != PathNodeType.WALKABLE ? pathNode : pathNode;
	}

	@Override
	public PathNodeType getNodeType(
		BlockView world, int x, int y, int z, MobEntity mob, int sizeX, int sizeY, int sizeZ, boolean canOpenDoors, boolean canEnterOpenDoors
	) {
		EnumSet<PathNodeType> enumSet = EnumSet.noneOf(PathNodeType.class);
		PathNodeType pathNodeType = PathNodeType.BLOCKED;
		BlockPos blockPos = new BlockPos(mob);
		pathNodeType = this.method_64(world, x, y, z, sizeX, sizeY, sizeZ, canOpenDoors, canEnterOpenDoors, enumSet, pathNodeType, blockPos);
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

	@Override
	public PathNodeType getNodeType(BlockView world, int x, int y, int z) {
		PathNodeType pathNodeType = this.getBasicPathNodeType(world, x, y, z);
		if (pathNodeType == PathNodeType.OPEN && y >= 1) {
			Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
			PathNodeType pathNodeType2 = this.getBasicPathNodeType(world, x, y - 1, z);
			if (pathNodeType2 == PathNodeType.DAMAGE_FIRE || block == Blocks.MAGMA_BLOCK || pathNodeType2 == PathNodeType.LAVA || block == Blocks.CAMPFIRE) {
				pathNodeType = PathNodeType.DAMAGE_FIRE;
			} else if (pathNodeType2 == PathNodeType.DAMAGE_CACTUS) {
				pathNodeType = PathNodeType.DAMAGE_CACTUS;
			} else if (pathNodeType2 == PathNodeType.DAMAGE_OTHER) {
				pathNodeType = PathNodeType.DAMAGE_OTHER;
			} else {
				pathNodeType = pathNodeType2 != PathNodeType.WALKABLE && pathNodeType2 != PathNodeType.OPEN && pathNodeType2 != PathNodeType.WATER
					? PathNodeType.WALKABLE
					: PathNodeType.OPEN;
			}
		}

		return this.method_59(world, x, y, z, pathNodeType);
	}

	private PathNodeType method_10(MobEntity mobEntity, BlockPos blockPos) {
		return this.method_9(mobEntity, blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	private PathNodeType method_9(MobEntity mobEntity, int i, int j, int k) {
		return this.getNodeType(this.blockView, i, j, k, mobEntity, this.field_31, this.field_30, this.field_28, this.canOpenDoors(), this.canEnterOpenDoors());
	}
}
