package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FlyAroundGoal extends WanderAroundFarGoal {
	public FlyAroundGoal(MobEntityWithAi mobEntityWithAi, double d) {
		super(mobEntityWithAi, d);
	}

	@Nullable
	@Override
	protected Vec3d getWanderTarget() {
		Vec3d vec3d = null;
		if (this.mob.isInsideWater()) {
			vec3d = PathfindingUtil.findTargetStraight(this.mob, 15, 15);
		}

		if (this.mob.getRand().nextFloat() >= this.probability) {
			vec3d = this.method_6314();
		}

		return vec3d == null ? super.getWanderTarget() : vec3d;
	}

	@Nullable
	private Vec3d method_6314() {
		BlockPos blockPos = new BlockPos(this.mob);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		BlockPos.Mutable mutable2 = new BlockPos.Mutable();

		for (BlockPos blockPos2 : BlockPos.iterate(
			MathHelper.floor(this.mob.x - 3.0),
			MathHelper.floor(this.mob.y - 6.0),
			MathHelper.floor(this.mob.z - 3.0),
			MathHelper.floor(this.mob.x + 3.0),
			MathHelper.floor(this.mob.y + 6.0),
			MathHelper.floor(this.mob.z + 3.0)
		)) {
			if (!blockPos.equals(blockPos2)) {
				Block block = this.mob.world.getBlockState(mutable2.set(blockPos2).setOffset(Direction.field_11033)).getBlock();
				boolean bl = block instanceof LeavesBlock || block.matches(BlockTags.field_15475);
				if (bl && this.mob.world.isAir(blockPos2) && this.mob.world.isAir(mutable.set(blockPos2).setOffset(Direction.field_11036))) {
					return new Vec3d((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
				}
			}
		}

		return null;
	}
}
