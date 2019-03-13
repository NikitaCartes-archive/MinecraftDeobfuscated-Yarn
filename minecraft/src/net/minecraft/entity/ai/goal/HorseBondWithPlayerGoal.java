package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.class_1414;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class HorseBondWithPlayerGoal extends Goal {
	private final HorseBaseEntity field_6602;
	private final double speed;
	private double targetX;
	private double targetY;
	private double targetZ;

	public HorseBondWithPlayerGoal(HorseBaseEntity horseBaseEntity, double d) {
		this.field_6602 = horseBaseEntity;
		this.speed = d;
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18405));
	}

	@Override
	public boolean canStart() {
		if (!this.field_6602.isTame() && this.field_6602.hasPassengers()) {
			Vec3d vec3d = class_1414.method_6375(this.field_6602, 5, 4);
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
		this.field_6602.method_5942().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
	}

	@Override
	public boolean shouldContinue() {
		return !this.field_6602.isTame() && !this.field_6602.method_5942().isIdle() && this.field_6602.hasPassengers();
	}

	@Override
	public void tick() {
		if (!this.field_6602.isTame() && this.field_6602.getRand().nextInt(50) == 0) {
			Entity entity = (Entity)this.field_6602.getPassengerList().get(0);
			if (entity == null) {
				return;
			}

			if (entity instanceof PlayerEntity) {
				int i = this.field_6602.getTemper();
				int j = this.field_6602.method_6755();
				if (j > 0 && this.field_6602.getRand().nextInt(j) < i) {
					this.field_6602.method_6752((PlayerEntity)entity);
					return;
				}

				this.field_6602.method_6745(5);
			}

			this.field_6602.removeAllPassengers();
			this.field_6602.method_6757();
			this.field_6602.field_6002.summonParticle(this.field_6602, (byte)6);
		}
	}
}
