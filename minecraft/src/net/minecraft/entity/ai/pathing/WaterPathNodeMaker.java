package net.minecraft.entity.ai.pathing;

import javax.annotation.Nullable;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public class WaterPathNodeMaker extends PathNodeMaker {
	private final boolean field_58;

	public WaterPathNodeMaker(boolean bl) {
		this.field_58 = bl;
	}

	@Override
	public PathNode getStart() {
		return super.getNode(
			MathHelper.floor(this.entity.getBoundingBox().minX),
			MathHelper.floor(this.entity.getBoundingBox().minY + 0.5),
			MathHelper.floor(this.entity.getBoundingBox().minZ)
		);
	}

	@Override
	public TargetPathNode getNode(double d, double e, double f) {
		return new TargetPathNode(
			super.getNode(
				MathHelper.floor(d - (double)(this.entity.getWidth() / 2.0F)), MathHelper.floor(e + 0.5), MathHelper.floor(f - (double)(this.entity.getWidth() / 2.0F))
			)
		);
	}

	@Override
	public int getSuccessors(PathNode[] pathNodes, PathNode pathNode) {
		int i = 0;

		for (Direction direction : Direction.values()) {
			PathNode pathNode2 = this.getPathNodeInWater(pathNode.x + direction.getOffsetX(), pathNode.y + direction.getOffsetY(), pathNode.z + direction.getOffsetZ());
			if (pathNode2 != null && !pathNode2.visited) {
				pathNodes[i++] = pathNode2;
			}
		}

		return i;
	}

	@Override
	public PathNodeType getNodeType(BlockView blockView, int i, int j, int k, MobEntity mobEntity, int l, int m, int n, boolean bl, boolean bl2) {
		return this.getNodeType(blockView, i, j, k);
	}

	@Override
	public PathNodeType getNodeType(BlockView blockView, int i, int j, int k) {
		BlockPos blockPos = new BlockPos(i, j, k);
		FluidState fluidState = blockView.getFluidState(blockPos);
		BlockState blockState = blockView.getBlockState(blockPos);
		if (fluidState.isEmpty() && blockState.canPlaceAtSide(blockView, blockPos.method_10074(), BlockPlacementEnvironment.WATER) && blockState.isAir()) {
			return PathNodeType.BREACH;
		} else {
			return fluidState.matches(FluidTags.WATER) && blockState.canPlaceAtSide(blockView, blockPos, BlockPlacementEnvironment.WATER)
				? PathNodeType.WATER
				: PathNodeType.BLOCKED;
		}
	}

	@Nullable
	private PathNode getPathNodeInWater(int i, int j, int k) {
		PathNodeType pathNodeType = this.getNodeType(i, j, k);
		return (!this.field_58 || pathNodeType != PathNodeType.BREACH) && pathNodeType != PathNodeType.WATER ? null : this.getNode(i, j, k);
	}

	@Nullable
	@Override
	protected PathNode getNode(int i, int j, int k) {
		PathNode pathNode = null;
		PathNodeType pathNodeType = this.getNodeType(this.entity.world, i, j, k);
		float f = this.entity.getPathfindingPenalty(pathNodeType);
		if (f >= 0.0F) {
			pathNode = super.getNode(i, j, k);
			pathNode.type = pathNodeType;
			pathNode.penalty = Math.max(pathNode.penalty, f);
			if (this.field_20622.getFluidState(new BlockPos(i, j, k)).isEmpty()) {
				pathNode.penalty += 8.0F;
			}
		}

		return pathNodeType == PathNodeType.OPEN ? pathNode : pathNode;
	}

	private PathNodeType getNodeType(int i, int j, int k) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int l = i; l < i + this.field_31; l++) {
			for (int m = j; m < j + this.field_30; m++) {
				for (int n = k; n < k + this.field_28; n++) {
					FluidState fluidState = this.field_20622.getFluidState(mutable.set(l, m, n));
					BlockState blockState = this.field_20622.getBlockState(mutable.set(l, m, n));
					if (fluidState.isEmpty() && blockState.canPlaceAtSide(this.field_20622, mutable.method_10074(), BlockPlacementEnvironment.WATER) && blockState.isAir()) {
						return PathNodeType.BREACH;
					}

					if (!fluidState.matches(FluidTags.WATER)) {
						return PathNodeType.BLOCKED;
					}
				}
			}
		}

		BlockState blockState2 = this.field_20622.getBlockState(mutable);
		return blockState2.canPlaceAtSide(this.field_20622, mutable, BlockPlacementEnvironment.WATER) ? PathNodeType.WATER : PathNodeType.BLOCKED;
	}
}
