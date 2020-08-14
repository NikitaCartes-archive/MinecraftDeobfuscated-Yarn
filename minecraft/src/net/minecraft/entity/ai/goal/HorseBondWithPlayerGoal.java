package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class HorseBondWithPlayerGoal extends Goal {
	private final HorseBaseEntity horse;
	private final double speed;
	private double targetX;
	private double targetY;
	private double targetZ;

	public HorseBondWithPlayerGoal(HorseBaseEntity horse, double speed) {
		this.horse = horse;
		this.speed = speed;
		this.setControls(EnumSet.of(Goal.Control.MOVE));
	}

	@Override
	public boolean canStart() {
		if (!this.horse.isTame() && this.horse.hasPassengers()) {
			Vec3d vec3d = TargetFinder.findTarget(this.horse, 5, 4);
			if (vec3d == null) {
				return false;
			} else {
				this.targetX = vec3d.x;
				this.targetY = vec3d.y;
				this.targetZ = vec3d.z;
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public void start() {
		this.horse.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
	}

	@Override
	public boolean shouldContinue() {
		return !this.horse.isTame() && !this.horse.getNavigation().isIdle() && this.horse.hasPassengers();
	}

	@Override
	public void tick() {
		if (!this.horse.isTame() && this.horse.getRandom().nextInt(50) == 0) {
			Entity entity = (Entity)this.horse.getPassengerList().get(0);
			if (entity == null) {
				return;
			}

			if (entity instanceof PlayerEntity) {
				int i = this.horse.getTemper();
				int j = this.horse.getMaxTemper();
				if (j > 0 && this.horse.getRandom().nextInt(j) < i) {
					this.horse.bondWithPlayer((PlayerEntity)entity);
					return;
				}

				this.horse.addTemper(5);
			}

			this.horse.removeAllPassengers();
			this.horse.playAngrySound();
			this.horse.world.sendEntityStatus(this.horse, (byte)6);
		}
	}
}
