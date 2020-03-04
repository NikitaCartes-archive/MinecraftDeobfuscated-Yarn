package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FlyOntoTreeGoal extends WanderAroundFarGoal {
	public FlyOntoTreeGoal(MobEntityWithAi mobEntityWithAi, double d) {
		super(mobEntityWithAi, d);
	}

	@Nullable
	@Override
	protected Vec3d getWanderTarget() {
		Vec3d vec3d = null;
		if (this.mob.isTouchingWater()) {
			vec3d = TargetFinder.findGroundTarget(this.mob, 15, 15);
		}

		if (this.mob.getRandom().nextFloat() >= this.probability) {
			vec3d = this.getTreeTarget();
		}

		return vec3d == null ? super.getWanderTarget() : vec3d;
	}

	@Nullable
	private Vec3d getTreeTarget() {
		BlockPos blockPos = this.mob.getSenseCenterPos();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		BlockPos.Mutable mutable2 = new BlockPos.Mutable();

		for (BlockPos blockPos2 : BlockPos.iterate(
			MathHelper.floor(this.mob.getX() - 3.0),
			MathHelper.floor(this.mob.getY() - 6.0),
			MathHelper.floor(this.mob.getZ() - 3.0),
			MathHelper.floor(this.mob.getX() + 3.0),
			MathHelper.floor(this.mob.getY() + 6.0),
			MathHelper.floor(this.mob.getZ() + 3.0)
		)) {
			if (!blockPos.equals(blockPos2)) {
				Block block = this.mob.world.getBlockState(mutable2.move(blockPos2, Direction.DOWN)).getBlock();
				boolean bl = block instanceof LeavesBlock || block.isIn(BlockTags.LOGS);
				if (bl && this.mob.world.isAir(blockPos2) && this.mob.world.isAir(mutable.move(blockPos2, Direction.UP))) {
					return Vec3d.method_24955(blockPos2);
				}
			}
		}

		return null;
	}
}
