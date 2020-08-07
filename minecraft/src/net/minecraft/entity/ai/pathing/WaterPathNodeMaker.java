package net.minecraft.entity.ai.pathing;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public class WaterPathNodeMaker extends PathNodeMaker {
	private final boolean canJumpOutOfWater;

	public WaterPathNodeMaker(boolean canJumpOutOfWater) {
		this.canJumpOutOfWater = canJumpOutOfWater;
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
	public TargetPathNode getNode(double x, double y, double z) {
		return new TargetPathNode(
			super.getNode(
				MathHelper.floor(x - (double)(this.entity.getWidth() / 2.0F)), MathHelper.floor(y + 0.5), MathHelper.floor(z - (double)(this.entity.getWidth() / 2.0F))
			)
		);
	}

	@Override
	public int getSuccessors(PathNode[] successors, PathNode node) {
		int i = 0;

		for (Direction direction : Direction.values()) {
			PathNode pathNode = this.getPathNodeInWater(node.x + direction.getOffsetX(), node.y + direction.getOffsetY(), node.z + direction.getOffsetZ());
			if (pathNode != null && !pathNode.visited) {
				successors[i++] = pathNode;
			}
		}

		return i;
	}

	@Override
	public PathNodeType getNodeType(
		BlockView world, int x, int y, int z, MobEntity mob, int sizeX, int sizeY, int sizeZ, boolean canOpenDoors, boolean canEnterOpenDoors
	) {
		return this.getDefaultNodeType(world, x, y, z);
	}

	@Override
	public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
		BlockPos blockPos = new BlockPos(x, y, z);
		FluidState fluidState = world.getFluidState(blockPos);
		BlockState blockState = world.getBlockState(blockPos);
		if (fluidState.isEmpty() && blockState.canPathfindThrough(world, blockPos.method_10074(), NavigationType.field_48) && blockState.isAir()) {
			return PathNodeType.field_16;
		} else {
			return fluidState.isIn(FluidTags.field_15517) && blockState.canPathfindThrough(world, blockPos, NavigationType.field_48)
				? PathNodeType.field_18
				: PathNodeType.field_22;
		}
	}

	@Nullable
	private PathNode getPathNodeInWater(int x, int y, int z) {
		PathNodeType pathNodeType = this.getNodeType(x, y, z);
		return (!this.canJumpOutOfWater || pathNodeType != PathNodeType.field_16) && pathNodeType != PathNodeType.field_18 ? null : this.getNode(x, y, z);
	}

	@Nullable
	@Override
	protected PathNode getNode(int x, int y, int z) {
		PathNode pathNode = null;
		PathNodeType pathNodeType = this.getDefaultNodeType(this.entity.world, x, y, z);
		float f = this.entity.getPathfindingPenalty(pathNodeType);
		if (f >= 0.0F) {
			pathNode = super.getNode(x, y, z);
			pathNode.type = pathNodeType;
			pathNode.penalty = Math.max(pathNode.penalty, f);
			if (this.cachedWorld.getFluidState(new BlockPos(x, y, z)).isEmpty()) {
				pathNode.penalty += 8.0F;
			}
		}

		return pathNodeType == PathNodeType.field_7 ? pathNode : pathNode;
	}

	private PathNodeType getNodeType(int x, int y, int z) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = x; i < x + this.entityBlockXSize; i++) {
			for (int j = y; j < y + this.entityBlockYSize; j++) {
				for (int k = z; k < z + this.entityBlockZSize; k++) {
					FluidState fluidState = this.cachedWorld.getFluidState(mutable.set(i, j, k));
					BlockState blockState = this.cachedWorld.getBlockState(mutable.set(i, j, k));
					if (fluidState.isEmpty() && blockState.canPathfindThrough(this.cachedWorld, mutable.method_10074(), NavigationType.field_48) && blockState.isAir()) {
						return PathNodeType.field_16;
					}

					if (!fluidState.isIn(FluidTags.field_15517)) {
						return PathNodeType.field_22;
					}
				}
			}
		}

		BlockState blockState2 = this.cachedWorld.getBlockState(mutable);
		return blockState2.canPathfindThrough(this.cachedWorld, mutable, NavigationType.field_48) ? PathNodeType.field_18 : PathNodeType.field_22;
	}
}
