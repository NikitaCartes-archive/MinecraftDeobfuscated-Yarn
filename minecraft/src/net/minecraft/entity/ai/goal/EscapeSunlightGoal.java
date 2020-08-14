package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EscapeSunlightGoal extends Goal {
	protected final PathAwareEntity mob;
	private double targetX;
	private double targetY;
	private double targetZ;
	private final double speed;
	private final World world;

	public EscapeSunlightGoal(PathAwareEntity mob, double speed) {
		this.mob = mob;
		this.speed = speed;
		this.world = mob.world;
		this.setControls(EnumSet.of(Goal.Control.MOVE));
	}

	@Override
	public boolean canStart() {
		if (this.mob.getTarget() != null) {
			return false;
		} else if (!this.world.isDay()) {
			return false;
		} else if (!this.mob.isOnFire()) {
			return false;
		} else if (!this.world.isSkyVisible(this.mob.getBlockPos())) {
			return false;
		} else {
			return !this.mob.getEquippedStack(EquipmentSlot.HEAD).isEmpty() ? false : this.targetShadedPos();
		}
	}

	protected boolean targetShadedPos() {
		Vec3d vec3d = this.locateShadedPos();
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
	protected Vec3d locateShadedPos() {
		Random random = this.mob.getRandom();
		BlockPos blockPos = this.mob.getBlockPos();

		for (int i = 0; i < 10; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
			if (!this.world.isSkyVisible(blockPos2) && this.mob.getPathfindingFavor(blockPos2) < 0.0F) {
				return Vec3d.ofBottomCenter(blockPos2);
			}
		}

		return null;
	}
}
