package net.minecraft.entity.ai.goal;

import net.minecraft.class_1414;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class HorseBondWithPlayerGoal extends Goal {
	private final HorseBaseEntity owner;
	private final double speed;
	private double targetX;
	private double targetY;
	private double targetZ;

	public HorseBondWithPlayerGoal(HorseBaseEntity horseBaseEntity, double d) {
		this.owner = horseBaseEntity;
		this.speed = d;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		if (!this.owner.isTame() && this.owner.hasPassengers()) {
			Vec3d vec3d = class_1414.method_6375(this.owner, 5, 4);
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
		this.owner.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
	}

	@Override
	public boolean shouldContinue() {
		return !this.owner.isTame() && !this.owner.getNavigation().isIdle() && this.owner.hasPassengers();
	}

	@Override
	public void tick() {
		if (!this.owner.isTame() && this.owner.getRand().nextInt(50) == 0) {
			Entity entity = (Entity)this.owner.getPassengerList().get(0);
			if (entity == null) {
				return;
			}

			if (entity instanceof PlayerEntity) {
				int i = this.owner.getTemper();
				int j = this.owner.method_6755();
				if (j > 0 && this.owner.getRand().nextInt(j) < i) {
					this.owner.method_6752((PlayerEntity)entity);
					return;
				}

				this.owner.method_6745(5);
			}

			this.owner.removeAllPassengers();
			this.owner.method_6757();
			this.owner.world.summonParticle(this.owner, (byte)6);
		}
	}
}
