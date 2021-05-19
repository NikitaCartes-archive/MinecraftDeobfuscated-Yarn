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
			Entity entity2 = entity.getFirstPassenger();
			if (entity.hasPassengers() && entity.canBeControlledByRider() && entity2 instanceof PlayerEntity) {
				entity.setYaw(entity2.getYaw());
				entity.prevYaw = entity.getYaw();
				entity.setPitch(entity2.getPitch() * 0.5F);
				entity.setRotation(entity.getYaw(), entity.getPitch());
				entity.bodyYaw = entity.getYaw();
				entity.headYaw = entity.getYaw();
				entity.stepHeight = 1.0F;
				entity.flyingSpeed = entity.getMovementSpeed() * 0.1F;
				if (saddledEntity.boosted && saddledEntity.boostedTime++ > saddledEntity.currentBoostTime) {
					saddledEntity.boosted = false;
				}

				if (entity.isLogicalSideForUpdatingMovement()) {
					float f = this.getSaddledSpeed();
					if (saddledEntity.boosted) {
						f += f * 1.15F * MathHelper.sin((float)saddledEntity.boostedTime / (float)saddledEntity.currentBoostTime * (float) Math.PI);
					}

					entity.setMovementSpeed(f);
					this.setMovementInput(new Vec3d(0.0, 0.0, 1.0));
					entity.bodyTrackingIncrements = 0;
				} else {
					entity.updateLimbs(entity, false);
					entity.setVelocity(Vec3d.ZERO);
				}

				entity.tryCheckBlockCollision();
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
