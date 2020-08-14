package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public interface CrossbowUser extends RangedAttackMob {
	void setCharging(boolean charging);

	void shoot(LivingEntity target, ItemStack crossbow, ProjectileEntity projectile, float multiShotSpray);

	@Nullable
	LivingEntity getTarget();

	void postShoot();

	default void shoot(LivingEntity entity, float speed) {
		Hand hand = ProjectileUtil.getHandPossiblyHolding(entity, Items.CROSSBOW);
		ItemStack itemStack = entity.getStackInHand(hand);
		if (entity.isHolding(Items.CROSSBOW)) {
			CrossbowItem.shootAll(entity.world, entity, hand, itemStack, speed, (float)(14 - entity.world.getDifficulty().getId() * 4));
		}

		this.postShoot();
	}

	default void shoot(LivingEntity entity, LivingEntity target, ProjectileEntity projectile, float multishotSpray, float speed) {
		double d = target.getX() - entity.getX();
		double e = target.getZ() - entity.getZ();
		double f = (double)MathHelper.sqrt(d * d + e * e);
		double g = target.getBodyY(0.3333333333333333) - projectile.getY() + f * 0.2F;
		Vector3f vector3f = this.getProjectileLaunchVelocity(entity, new Vec3d(d, g, e), multishotSpray);
		projectile.setVelocity(
			(double)vector3f.getX(), (double)vector3f.getY(), (double)vector3f.getZ(), speed, (float)(14 - entity.world.getDifficulty().getId() * 4)
		);
		entity.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0F, 1.0F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
	}

	default Vector3f getProjectileLaunchVelocity(LivingEntity entity, Vec3d positionDelta, float multishotSpray) {
		Vec3d vec3d = positionDelta.normalize();
		Vec3d vec3d2 = vec3d.crossProduct(new Vec3d(0.0, 1.0, 0.0));
		if (vec3d2.lengthSquared() <= 1.0E-7) {
			vec3d2 = vec3d.crossProduct(entity.getOppositeRotationVector(1.0F));
		}

		Quaternion quaternion = new Quaternion(new Vector3f(vec3d2), 90.0F, true);
		Vector3f vector3f = new Vector3f(vec3d);
		vector3f.rotate(quaternion);
		Quaternion quaternion2 = new Quaternion(vector3f, multishotSpray, true);
		Vector3f vector3f2 = new Vector3f(vec3d);
		vector3f2.rotate(quaternion2);
		return vector3f2;
	}
}
