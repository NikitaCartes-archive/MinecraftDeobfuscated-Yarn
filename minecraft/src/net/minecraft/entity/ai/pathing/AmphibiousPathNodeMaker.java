package net.minecraft.entity.ai.pathing;

import javax.annotation.Nullable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkCache;

public class AmphibiousPathNodeMaker extends LandPathNodeMaker {
	private final boolean penalizeDeepWater;
	private float oldWalkablePenalty;
	private float oldWaterBorderPenalty;

	public AmphibiousPathNodeMaker(boolean penalizeDeepWater) {
		this.penalizeDeepWater = penalizeDeepWater;
	}

	@Override
	public void init(ChunkCache cachedWorld, MobEntity entity) {
		super.init(cachedWorld, entity);
		entity.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
		this.oldWalkablePenalty = entity.getPathfindingPenalty(PathNodeType.WALKABLE);
		entity.setPathfindingPenalty(PathNodeType.WALKABLE, 6.0F);
		this.oldWaterBorderPenalty = entity.getPathfindingPenalty(PathNodeType.WATER_BORDER);
		entity.setPathfindingPenalty(PathNodeType.WATER_BORDER, 4.0F);
	}

	@Override
	public void clear() {
		this.entity.setPathfindingPenalty(PathNodeType.WALKABLE, this.oldWalkablePenalty);
		this.entity.setPathfindingPenalty(PathNodeType.WATER_BORDER, this.oldWaterBorderPenalty);
		super.clear();
	}

	@Override
	public PathNode getStart() {
		return !this.entity.isTouchingWater()
			? super.getStart()
			: this.getStart(
				new BlockPos(
					MathHelper.floor(this.entity.getBoundingBox().minX),
					MathHelper.floor(this.entity.getBoundingBox().minY + 0.5),
					MathHelper.floor(this.entity.getBoundingBox().minZ)
				)
			);
	}

	@Override
	public TargetPathNode getNode(double x, double y, double z) {
		return this.createNode(x, y + 0.5, z);
	}

	@Override
	public int getSuccessors(PathNode[] successors, PathNode node) {
		int i = super.getSuccessors(successors, node);
		PathNodeType pathNodeType = this.getNodeType(node.x, node.y + 1, node.z);
		PathNodeType pathNodeType2 = this.getNodeType(node.x, node.y, node.z);
		int j;
		if (this.entity.getPathfindingPenalty(pathNodeType) >= 0.0F && pathNodeType2 != PathNodeType.STICKY_HONEY) {
			j = MathHelper.floor(Math.max(1.0F, this.entity.getStepHeight()));
		} else {
			j = 0;
		}

		double d = this.getFeetY(new BlockPos(node.x, node.y, node.z));
		PathNode pathNode = this.getPathNode(node.x, node.y + 1, node.z, Math.max(0, j - 1), d, Direction.UP, pathNodeType2);
		PathNode pathNode2 = this.getPathNode(node.x, node.y - 1, node.z, j, d, Direction.DOWN, pathNodeType2);
		if (this.isValidAquaticAdjacentSuccessor(pathNode, node)) {
			successors[i++] = pathNode;
		}

		if (this.isValidAquaticAdjacentSuccessor(pathNode2, node) && pathNodeType2 != PathNodeType.TRAPDOOR) {
			successors[i++] = pathNode2;
		}

		for (int k = 0; k < i; k++) {
			PathNode pathNode3 = successors[k];
			if (pathNode3.type == PathNodeType.WATER && this.penalizeDeepWater && pathNode3.y < this.entity.getWorld().getSeaLevel() - 10) {
				pathNode3.penalty++;
			}
		}

		return i;
	}

	private boolean isValidAquaticAdjacentSuccessor(@Nullable PathNode node, PathNode successor) {
		return this.isValidAdjacentSuccessor(node, successor) && node.type == PathNodeType.WATER;
	}

	@Override
	protected boolean isAmphibious() {
		return true;
	}

	@Override
	public PathNodeType getDefaultNodeType(PathContext context, int x, int y, int z) {
		PathNodeType pathNodeType = context.getNodeType(x, y, z);
		if (pathNodeType == PathNodeType.WATER) {
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (Direction direction : Direction.values()) {
				mutable.set(x, y, z).move(direction);
				PathNodeType pathNodeType2 = context.getNodeType(mutable.getX(), mutable.getY(), mutable.getZ());
				if (pathNodeType2 == PathNodeType.BLOCKED) {
					return PathNodeType.WATER_BORDER;
				}
			}

			return PathNodeType.WATER;
		} else {
			return super.getDefaultNodeType(context, x, y, z);
		}
	}
}
