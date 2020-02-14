package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.projectile.Projectile;
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

	void shoot(LivingEntity target, ItemStack crossbow, Projectile projectile, float multiShotSpray);

	@Nullable
	LivingEntity getTarget();

	void method_24651();

	default void method_24654(LivingEntity livingEntity, float f) {
		Hand hand = ProjectileUtil.getHandPossiblyHolding(livingEntity, Items.CROSSBOW);
		ItemStack itemStack = livingEntity.getStackInHand(hand);
		if (livingEntity.method_24518(Items.CROSSBOW)) {
			CrossbowItem.shootAll(livingEntity.world, livingEntity, hand, itemStack, f, (float)(14 - livingEntity.world.getDifficulty().getId() * 4));
		}

		this.method_24651();
	}

	default void method_24652(LivingEntity livingEntity, LivingEntity livingEntity2, Projectile projectile, float f, float g) {
		Entity entity = (Entity)projectile;
		double d = livingEntity2.getX() - livingEntity.getX();
		double e = livingEntity2.getZ() - livingEntity.getZ();
		double h = (double)MathHelper.sqrt(d * d + e * e);
		double i = livingEntity2.getBodyY(0.3333333333333333) - entity.getY() + h * 0.2F;
		Vector3f vector3f = this.method_24653(livingEntity, new Vec3d(d, i, e), f);
		projectile.setVelocity(
			(double)vector3f.getX(), (double)vector3f.getY(), (double)vector3f.getZ(), g, (float)(14 - livingEntity.world.getDifficulty().getId() * 4)
		);
		livingEntity.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0F, 1.0F / (livingEntity.getRandom().nextFloat() * 0.4F + 0.8F));
	}

	default Vector3f method_24653(LivingEntity livingEntity, Vec3d vec3d, float f) {
		Vec3d vec3d2 = vec3d.normalize();
		Vec3d vec3d3 = vec3d2.crossProduct(new Vec3d(0.0, 1.0, 0.0));
		if (vec3d3.lengthSquared() <= 1.0E-7) {
			vec3d3 = vec3d2.crossProduct(livingEntity.getOppositeRotationVector(1.0F));
		}

		Quaternion quaternion = new Quaternion(new Vector3f(vec3d3), 90.0F, true);
		Vector3f vector3f = new Vector3f(vec3d2);
		vector3f.rotate(quaternion);
		Quaternion quaternion2 = new Quaternion(vector3f, f, true);
		Vector3f vector3f2 = new Vector3f(vec3d2);
		vector3f2.rotate(quaternion2);
		return vector3f2;
	}
}
