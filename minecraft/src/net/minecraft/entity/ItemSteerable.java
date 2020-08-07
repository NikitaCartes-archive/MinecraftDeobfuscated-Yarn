package net.minecraft.entity;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public interface ItemSteerable {
	boolean consumeOnAStickItem();

	void setMovementInput(Vec3d movementInput);

	float getSaddledSpeed();

	default boolean travel(MobEntity entity, SaddledComponent saddledEntity, Vec3d movementInput) {
		if (!entity.isAlive()) {
			return false;
		} else {
			Entity entity2 = entity.getPassengerList().isEmpty() ? null : (Entity)entity.getPassengerList().get(0);
			if (entity.hasPassengers() && entity.canBeControlledByRider() && entity2 instanceof PlayerEntity) {
				entity.yaw = entity2.yaw;
				entity.prevYaw = entity.yaw;
				entity.pitch = entity2.pitch * 0.5F;
				entity.setRotation(entity.yaw, entity.pitch);
				entity.bodyYaw = entity.yaw;
				entity.headYaw = entity.yaw;
				entity.stepHeight = 1.0F;
				entity.flyingSpeed = entity.getMovementSpeed() * 0.1F;
				if (saddledEntity.boosted && saddledEntity.field_23216++ > saddledEntity.currentBoostTime) {
					saddledEntity.boosted = false;
				}

				if (entity.isLogicalSideForUpdatingMovement()) {
					float f = this.getSaddledSpeed();
					if (saddledEntity.boosted) {
						f += f * 1.15F * MathHelper.sin((float)saddledEntity.field_23216 / (float)saddledEntity.currentBoostTime * (float) Math.PI);
					}

					entity.setMovementSpeed(f);
					this.setMovementInput(new Vec3d(0.0, 0.0, 1.0));
					entity.bodyTrackingIncrements = 0;
				} else {
					entity.method_29242(entity, false);
					entity.setVelocity(Vec3d.ZERO);
				}

				return true;
			} else {
				entity.stepHeight = 0.5F;
				entity.flyingSpeed = 0.02F;
				this.setMovementInput(movementInput);
				return false;
			}
		}
	}
}
