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
		return super.getPathNode(
			MathHelper.floor(this.entity.method_5829().minX), MathHelper.floor(this.entity.method_5829().minY + 0.5), MathHelper.floor(this.entity.method_5829().minZ)
		);
	}

	@Override
	public PathNode getPathNode(double d, double e, double f) {
		return super.getPathNode(
			MathHelper.floor(d - (double)(this.entity.getWidth() / 2.0F)), MathHelper.floor(e + 0.5), MathHelper.floor(f - (double)(this.entity.getWidth() / 2.0F))
		);
	}

	@Override
	public int getPathNodes(PathNode[] pathNodes, PathNode pathNode, PathNode pathNode2, float f) {
		int i = 0;

		for (Direction direction : Direction.values()) {
			PathNode pathNode3 = this.getPathNodeInWater(pathNode.x + direction.getOffsetX(), pathNode.y + direction.getOffsetY(), pathNode.z + direction.getOffsetZ());
			if (pathNode3 != null && !pathNode3.field_42 && pathNode3.distance(pathNode2) < f) {
				pathNodes[i++] = pathNode3;
			}
		}

		return i;
	}

	@Override
	public PathNodeType getPathNodeType(BlockView blockView, int i, int j, int k, MobEntity mobEntity, int l, int m, int n, boolean bl, boolean bl2) {
		return this.getPathNodeType(blockView, i, j, k);
	}

	@Override
	public PathNodeType getPathNodeType(BlockView blockView, int i, int j, int k) {
		BlockPos blockPos = new BlockPos(i, j, k);
		FluidState fluidState = blockView.method_8316(blockPos);
		BlockState blockState = blockView.method_8320(blockPos);
		if (fluidState.isEmpty() && blockState.method_11609(blockView, blockPos.down(), BlockPlacementEnvironment.field_48) && blockState.isAir()) {
			return PathNodeType.field_16;
		} else {
			return fluidState.method_15767(FluidTags.field_15517) && blockState.method_11609(blockView, blockPos, BlockPlacementEnvironment.field_48)
				? PathNodeType.field_18
				: PathNodeType.field_22;
		}
	}

	@Nullable
	private PathNode getPathNodeInWater(int i, int j, int k) {
		PathNodeType pathNodeType = this.getPathNodeType(i, j, k);
		return (!this.field_58 || pathNodeType != PathNodeType.field_16) && pathNodeType != PathNodeType.field_18 ? null : this.getPathNode(i, j, k);
	}

	@Nullable
	@Override
	protected PathNode getPathNode(int i, int j, int k) {
		PathNode pathNode = null;
		PathNodeType pathNodeType = this.getPathNodeType(this.entity.field_6002, i, j, k);
		float f = this.entity.method_5944(pathNodeType);
		if (f >= 0.0F) {
			pathNode = super.getPathNode(i, j, k);
			pathNode.type = pathNodeType;
			pathNode.field_43 = Math.max(pathNode.field_43, f);
			if (this.blockView.method_8316(new BlockPos(i, j, k)).isEmpty()) {
				pathNode.field_43 += 8.0F;
			}
		}

		return pathNodeType == PathNodeType.field_7 ? pathNode : pathNode;
	}

	private PathNodeType getPathNodeType(int i, int j, int k) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int l = i; l < i + this.field_31; l++) {
			for (int m = j; m < j + this.field_30; m++) {
				for (int n = k; n < k + this.field_28; n++) {
					FluidState fluidState = this.blockView.method_8316(mutable.set(l, m, n));
					BlockState blockState = this.blockView.method_8320(mutable.set(l, m, n));
					if (fluidState.isEmpty() && blockState.method_11609(this.blockView, mutable.down(), BlockPlacementEnvironment.field_48) && blockState.isAir()) {
						return PathNodeType.field_16;
					}

					if (!fluidState.method_15767(FluidTags.field_15517)) {
						return PathNodeType.field_22;
					}
				}
			}
		}

		BlockState blockState2 = this.blockView.method_8320(mutable);
		return blockState2.method_11609(this.blockView, mutable, BlockPlacementEnvironment.field_48) ? PathNodeType.field_18 : PathNodeType.field_22;
	}
}
