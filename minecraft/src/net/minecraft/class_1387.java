package net.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class class_1387 extends Goal {
	private final HorseBaseEntity field_6602;
	private final double field_6601;
	private double field_6600;
	private double field_6599;
	private double field_6603;

	public class_1387(HorseBaseEntity horseBaseEntity, double d) {
		this.field_6602 = horseBaseEntity;
		this.field_6601 = d;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		if (!this.field_6602.isTame() && this.field_6602.hasPassengers()) {
			Vec3d vec3d = class_1414.method_6375(this.field_6602, 5, 4);
			if (vec3d == null) {
				return false;
			} else {
				this.field_6600 = vec3d.x;
				this.field_6599 = vec3d.y;
				this.field_6603 = vec3d.z;
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public void start() {
		this.field_6602.getNavigation().method_6337(this.field_6600, this.field_6599, this.field_6603, this.field_6601);
	}

	@Override
	public boolean shouldContinue() {
		return !this.field_6602.isTame() && !this.field_6602.getNavigation().method_6357() && this.field_6602.hasPassengers();
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
			this.field_6602.world.method_8421(this.field_6602, (byte)6);
		}
	}
}
