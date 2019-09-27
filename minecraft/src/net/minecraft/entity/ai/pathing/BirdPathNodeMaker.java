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
import net.minecraft.world.chunk.ChunkCache;

public class BirdPathNodeMaker extends LandPathNodeMaker {
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
			i = MathHelper.floor(this.entity.getBoundingBox().minY);
			BlockPos.Mutable mutable = new BlockPos.Mutable(this.entity.x, (double)i, this.entity.z);

			for (Block block = this.field_20622.getBlockState(mutable).getBlock(); block == Blocks.WATER; block = this.field_20622.getBlockState(mutable).getBlock()) {
				mutable.set(this.entity.x, (double)(++i), this.entity.z);
			}
		} else {
			i = MathHelper.floor(this.entity.getBoundingBox().minY + 0.5);
		}

		BlockPos blockPos = new BlockPos(this.entity);
		PathNodeType pathNodeType = this.method_9(this.entity, blockPos.getX(), i, blockPos.getZ());
		if (this.entity.getPathfindingPenalty(pathNodeType) < 0.0F) {
			Set<BlockPos> set = Sets.<BlockPos>newHashSet();
			set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)i, this.entity.getBoundingBox().minZ));
			set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)i, this.entity.getBoundingBox().maxZ));
			set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)i, this.entity.getBoundingBox().minZ));
			set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)i, this.entity.getBoundingBox().maxZ));

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
	public TargetPathNode getNode(double d, double e, double f) {
		return new TargetPathNode(super.getNode(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f)));
	}

	@Override
	public int getSuccessors(PathNode[] pathNodes, PathNode pathNode) {
		int i = 0;
		PathNode pathNode2 = this.getNode(pathNode.x, pathNode.y, pathNode.z + 1);
		if (this.method_22878(pathNode2)) {
			pathNodes[i++] = pathNode2;
		}

		PathNode pathNode3 = this.getNode(pathNode.x - 1, pathNode.y, pathNode.z);
		if (this.method_22878(pathNode3)) {
			pathNodes[i++] = pathNode3;
		}

		PathNode pathNode4 = this.getNode(pathNode.x + 1, pathNode.y, pathNode.z);
		if (this.method_22878(pathNode4)) {
			pathNodes[i++] = pathNode4;
		}

		PathNode pathNode5 = this.getNode(pathNode.x, pathNode.y, pathNode.z - 1);
		if (this.method_22878(pathNode5)) {
			pathNodes[i++] = pathNode5;
		}

		PathNode pathNode6 = this.getNode(pathNode.x, pathNode.y + 1, pathNode.z);
		if (this.method_22878(pathNode6)) {
			pathNodes[i++] = pathNode6;
		}

		PathNode pathNode7 = this.getNode(pathNode.x, pathNode.y - 1, pathNode.z);
		if (this.method_22878(pathNode7)) {
			pathNodes[i++] = pathNode7;
		}

		PathNode pathNode8 = this.getNode(pathNode.x, pathNode.y + 1, pathNode.z + 1);
		if (this.method_22878(pathNode8) && this.method_22877(pathNode2) && this.method_22877(pathNode6)) {
			pathNodes[i++] = pathNode8;
		}

		PathNode pathNode9 = this.getNode(pathNode.x - 1, pathNode.y + 1, pathNode.z);
		if (this.method_22878(pathNode9) && this.method_22877(pathNode3) && this.method_22877(pathNode6)) {
			pathNodes[i++] = pathNode9;
		}

		PathNode pathNode10 = this.getNode(pathNode.x + 1, pathNode.y + 1, pathNode.z);
		if (this.method_22878(pathNode10) && this.method_22877(pathNode4) && this.method_22877(pathNode6)) {
			pathNodes[i++] = pathNode10;
		}

		PathNode pathNode11 = this.getNode(pathNode.x, pathNode.y + 1, pathNode.z - 1);
		if (this.method_22878(pathNode11) && this.method_22877(pathNode5) && this.method_22877(pathNode6)) {
			pathNodes[i++] = pathNode11;
		}

		PathNode pathNode12 = this.getNode(pathNode.x, pathNode.y - 1, pathNode.z + 1);
		if (this.method_22878(pathNode12) && this.method_22877(pathNode2) && this.method_22877(pathNode7)) {
			pathNodes[i++] = pathNode12;
		}

		PathNode pathNode13 = this.getNode(pathNode.x - 1, pathNode.y - 1, pathNode.z);
		if (this.method_22878(pathNode13) && this.method_22877(pathNode3) && this.method_22877(pathNode7)) {
			pathNodes[i++] = pathNode13;
		}

		PathNode pathNode14 = this.getNode(pathNode.x + 1, pathNode.y - 1, pathNode.z);
		if (this.method_22878(pathNode14) && this.method_22877(pathNode4) && this.method_22877(pathNode7)) {
			pathNodes[i++] = pathNode14;
		}

		PathNode pathNode15 = this.getNode(pathNode.x, pathNode.y - 1, pathNode.z - 1);
		if (this.method_22878(pathNode15) && this.method_22877(pathNode5) && this.method_22877(pathNode7)) {
			pathNodes[i++] = pathNode15;
		}

		PathNode pathNode16 = this.getNode(pathNode.x + 1, pathNode.y, pathNode.z - 1);
		if (this.method_22878(pathNode16) && this.method_22877(pathNode5) && this.method_22877(pathNode4)) {
			pathNodes[i++] = pathNode16;
		}

		PathNode pathNode17 = this.getNode(pathNode.x + 1, pathNode.y, pathNode.z + 1);
		if (this.method_22878(pathNode17) && this.method_22877(pathNode2) && this.method_22877(pathNode4)) {
			pathNodes[i++] = pathNode17;
		}

		PathNode pathNode18 = this.getNode(pathNode.x - 1, pathNode.y, pathNode.z - 1);
		if (this.method_22878(pathNode18) && this.method_22877(pathNode5) && this.method_22877(pathNode3)) {
			pathNodes[i++] = pathNode18;
		}

		PathNode pathNode19 = this.getNode(pathNode.x - 1, pathNode.y, pathNode.z + 1);
		if (this.method_22878(pathNode19) && this.method_22877(pathNode2) && this.method_22877(pathNode3)) {
			pathNodes[i++] = pathNode19;
		}

		PathNode pathNode20 = this.getNode(pathNode.x + 1, pathNode.y + 1, pathNode.z - 1);
		if (this.method_22878(pathNode20) && this.method_22877(pathNode16) && this.method_22877(pathNode11) && this.method_22877(pathNode10)) {
			pathNodes[i++] = pathNode20;
		}

		PathNode pathNode21 = this.getNode(pathNode.x + 1, pathNode.y + 1, pathNode.z + 1);
		if (this.method_22878(pathNode21) && this.method_22877(pathNode17) && this.method_22877(pathNode8) && this.method_22877(pathNode10)) {
			pathNodes[i++] = pathNode21;
		}

		PathNode pathNode22 = this.getNode(pathNode.x - 1, pathNode.y + 1, pathNode.z - 1);
		if (this.method_22878(pathNode22) && this.method_22877(pathNode18) && this.method_22877(pathNode11) && this.method_22877(pathNode9)) {
			pathNodes[i++] = pathNode22;
		}

		PathNode pathNode23 = this.getNode(pathNode.x - 1, pathNode.y + 1, pathNode.z + 1);
		if (this.method_22878(pathNode23) && this.method_22877(pathNode19) && this.method_22877(pathNode8) && this.method_22877(pathNode9)) {
			pathNodes[i++] = pathNode23;
		}

		PathNode pathNode24 = this.getNode(pathNode.x + 1, pathNode.y - 1, pathNode.z - 1);
		if (this.method_22878(pathNode24) && this.method_22877(pathNode16) && this.method_22877(pathNode15) && this.method_22877(pathNode14)) {
			pathNodes[i++] = pathNode24;
		}

		PathNode pathNode25 = this.getNode(pathNode.x + 1, pathNode.y - 1, pathNode.z + 1);
		if (this.method_22878(pathNode25) && this.method_22877(pathNode17) && this.method_22877(pathNode12) && this.method_22877(pathNode14)) {
			pathNodes[i++] = pathNode25;
		}

		PathNode pathNode26 = this.getNode(pathNode.x - 1, pathNode.y - 1, pathNode.z - 1);
		if (this.method_22878(pathNode26) && this.method_22877(pathNode18) && this.method_22877(pathNode15) && this.method_22877(pathNode13)) {
			pathNodes[i++] = pathNode26;
		}

		PathNode pathNode27 = this.getNode(pathNode.x - 1, pathNode.y - 1, pathNode.z + 1);
		if (this.method_22878(pathNode27) && this.method_22877(pathNode19) && this.method_22877(pathNode12) && this.method_22877(pathNode13)) {
			pathNodes[i++] = pathNode27;
		}

		return i;
	}

	private boolean method_22877(@Nullable PathNode pathNode) {
		return pathNode != null && pathNode.penalty >= 0.0F;
	}

	private boolean method_22878(@Nullable PathNode pathNode) {
		return pathNode != null && !pathNode.visited;
	}

	@Nullable
	@Override
	protected PathNode getNode(int i, int j, int k) {
		PathNode pathNode = null;
		PathNodeType pathNodeType = this.method_9(this.entity, i, j, k);
		float f = this.entity.getPathfindingPenalty(pathNodeType);
		if (f >= 0.0F) {
			pathNode = super.getNode(i, j, k);
			pathNode.type = pathNodeType;
			pathNode.penalty = Math.max(pathNode.penalty, f);
			if (pathNodeType == PathNodeType.WALKABLE) {
				pathNode.penalty++;
			}
		}

		return pathNodeType != PathNodeType.OPEN && pathNodeType != PathNodeType.WALKABLE ? pathNode : pathNode;
	}

	@Override
	public PathNodeType getNodeType(BlockView blockView, int i, int j, int k, MobEntity mobEntity, int l, int m, int n, boolean bl, boolean bl2) {
		EnumSet<PathNodeType> enumSet = EnumSet.noneOf(PathNodeType.class);
		PathNodeType pathNodeType = PathNodeType.BLOCKED;
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

	@Override
	public PathNodeType getNodeType(BlockView blockView, int i, int j, int k) {
		PathNodeType pathNodeType = this.getBasicPathNodeType(blockView, i, j, k);
		if (pathNodeType == PathNodeType.OPEN && j >= 1) {
			Block block = blockView.getBlockState(new BlockPos(i, j - 1, k)).getBlock();
			PathNodeType pathNodeType2 = this.getBasicPathNodeType(blockView, i, j - 1, k);
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

		return this.method_59(blockView, i, j, k, pathNodeType);
	}

	private PathNodeType method_10(MobEntity mobEntity, BlockPos blockPos) {
		return this.method_9(mobEntity, blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	private PathNodeType method_9(MobEntity mobEntity, int i, int j, int k) {
		return this.getNodeType(this.field_20622, i, j, k, mobEntity, this.field_31, this.field_30, this.field_28, this.canOpenDoors(), this.canEnterOpenDoors());
	}
}
