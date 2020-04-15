package net.minecraft.entity.mob;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;

public interface Hoglin {
	@Environment(EnvType.CLIENT)
	int getMovementCooldownTicks();

	static boolean tryAttack(LivingEntity livingEntity, LivingEntity livingEntity2) {
		float f = (float)livingEntity.getAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE);
		float g;
		if (!livingEntity.isBaby() && (int)f > 0) {
			g = f / 2.0F + (float)livingEntity.world.random.nextInt((int)f);
		} else {
			g = f;
		}

		boolean bl = livingEntity2.damage(DamageSource.mob(livingEntity), g);
		if (bl) {
			livingEntity.dealDamage(livingEntity, livingEntity2);
			if (!livingEntity.isBaby()) {
				knockback(livingEntity, livingEntity2);
			}
		}

		return bl;
	}

	static void knockback(LivingEntity livingEntity, LivingEntity livingEntity2) {
		double d = livingEntity.getAttribute(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
		double e = livingEntity2.getAttribute(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
		double f = d - e;
		if (!(f <= 0.0)) {
			double g = livingEntity2.getX() - livingEntity.getX();
			double h = livingEntity2.getZ() - livingEntity.getZ();
			float i = (float)(livingEntity.world.random.nextInt(21) - 10);
			double j = f * (double)(livingEntity.world.random.nextFloat() * 0.5F + 0.2F);
			Vec3d vec3d = new Vec3d(g, 0.0, h).normalize().multiply(j).rotateY(i);
			double k = f * (double)livingEntity.world.random.nextFloat() * 0.5;
			livingEntity2.addVelocity(vec3d.x, k, vec3d.z);
			livingEntity2.velocityModified = true;
		}
	}
}
