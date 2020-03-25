package net.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public interface class_4981 {
	boolean method_6577();

	void setSaddled(boolean bl);

	boolean isSaddled();

	void method_26315(Vec3d vec3d);

	float method_26316();

	default boolean method_26313(MobEntity mobEntity, class_4980 arg, Vec3d vec3d) {
		if (!mobEntity.isAlive()) {
			return false;
		} else {
			Entity entity = mobEntity.getPassengerList().isEmpty() ? null : (Entity)mobEntity.getPassengerList().get(0);
			if (mobEntity.hasPassengers() && mobEntity.canBeControlledByRider() && entity instanceof PlayerEntity) {
				mobEntity.yaw = entity.yaw;
				mobEntity.prevYaw = mobEntity.yaw;
				mobEntity.pitch = entity.pitch * 0.5F;
				mobEntity.setRotation(mobEntity.yaw, mobEntity.pitch);
				mobEntity.bodyYaw = mobEntity.yaw;
				mobEntity.headYaw = mobEntity.yaw;
				mobEntity.stepHeight = 1.0F;
				mobEntity.flyingSpeed = mobEntity.getMovementSpeed() * 0.1F;
				if (arg.field_23215 && arg.field_23216++ > arg.field_23217) {
					arg.field_23215 = false;
				}

				if (mobEntity.isLogicalSideForUpdatingMovement()) {
					float f = this.method_26316();
					if (arg.field_23215) {
						f += f * 1.15F * MathHelper.sin((float)arg.field_23216 / (float)arg.field_23217 * (float) Math.PI);
					}

					mobEntity.setMovementSpeed(f);
					this.method_26315(new Vec3d(0.0, 0.0, 1.0));
					mobEntity.bodyTrackingIncrements = 0;
				} else {
					mobEntity.setVelocity(Vec3d.ZERO);
				}

				return true;
			} else {
				mobEntity.stepHeight = 0.5F;
				mobEntity.flyingSpeed = 0.02F;
				this.method_26315(vec3d);
				return false;
			}
		}
	}

	default boolean method_26314(MobEntity mobEntity, PlayerEntity playerEntity, Hand hand, boolean bl) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.NAME_TAG) {
			itemStack.useOnEntity(playerEntity, mobEntity, hand);
			return true;
		} else if (!this.isSaddled() || mobEntity.hasPassengers() || !bl && mobEntity.isBaby()) {
			return itemStack.getItem() == Items.SADDLE && itemStack.useOnEntity(playerEntity, mobEntity, hand);
		} else {
			if (!mobEntity.world.isClient) {
				playerEntity.startRiding(mobEntity);
			}

			return true;
		}
	}
}
