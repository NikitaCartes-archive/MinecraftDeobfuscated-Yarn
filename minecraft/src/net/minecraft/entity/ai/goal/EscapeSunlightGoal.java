package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EscapeSunlightGoal extends Goal {
	protected final MobEntityWithAi mob;
	private double targetX;
	private double targetY;
	private double targetZ;
	private final double speed;
	private final World field_6418;

	public EscapeSunlightGoal(MobEntityWithAi mobEntityWithAi, double d) {
		this.mob = mobEntityWithAi;
		this.speed = d;
		this.field_6418 = mobEntityWithAi.field_6002;
		this.setControls(EnumSet.of(Goal.Control.field_18405));
	}

	@Override
	public boolean canStart() {
		if (this.mob.getTarget() != null) {
			return false;
		} else if (!this.field_6418.isDaylight()) {
			return false;
		} else if (!this.mob.isOnFire()) {
			return false;
		} else if (!this.field_6418.isSkyVisible(new BlockPos(this.mob.x, this.mob.method_5829().minY, this.mob.z))) {
			return false;
		} else {
			return !this.mob.getEquippedStack(EquipmentSlot.field_6169).isEmpty() ? false : this.method_18250();
		}
	}

	protected boolean method_18250() {
		Vec3d vec3d = this.method_6257();
		if (vec3d == null) {
			return false;
		} else {
			this.targetX = vec3d.x;
			this.targetY = vec3d.y;
			this.targetZ = vec3d.z;
			return true;
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.mob.getNavigation().isIdle();
	}

	@Override
	public void start() {
		this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
	}

	@Nullable
	protected Vec3d method_6257() {
		Random random = this.mob.getRand();
		BlockPos blockPos = new BlockPos(this.mob.x, this.mob.method_5829().minY, this.mob.z);

		for (int i = 0; i < 10; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
			if (!this.field_6418.isSkyVisible(blockPos2) && this.mob.getPathfindingFavor(blockPos2) < 0.0F) {
				return new Vec3d((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
			}
		}

		return null;
	}
}
