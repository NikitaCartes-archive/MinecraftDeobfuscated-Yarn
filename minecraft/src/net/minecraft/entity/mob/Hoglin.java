package net.minecraft.entity.mob;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public interface Hoglin {
	int field_30546 = 10;

	int getMovementCooldownTicks();

	static boolean tryAttack(LivingEntity livingEntity, LivingEntity livingEntity2) {
		float f = (float)livingEntity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
		float g;
		if (!livingEntity.isBaby() && (int)f > 0) {
			g = f / 2.0F + (float)livingEntity.getWorld().random.nextInt((int)f);
		} else {
			g = f;
		}

		DamageSource damageSource = livingEntity.getDamageSources().mobAttack(livingEntity);
		boolean bl = livingEntity2.damage(damageSource, g);
		if (bl) {
			if (livingEntity.getWorld() instanceof ServerWorld serverWorld) {
				EnchantmentHelper.onTargetDamaged(serverWorld, livingEntity2, damageSource);
			}

			if (!livingEntity.isBaby()) {
				knockback(livingEntity, livingEntity2);
			}
		}

		return bl;
	}

	static void knockback(LivingEntity attacker, LivingEntity target) {
		double d = attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
		double e = target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
		double f = d - e;
		if (!(f <= 0.0)) {
			double g = target.getX() - attacker.getX();
			double h = target.getZ() - attacker.getZ();
			float i = (float)(attacker.getWorld().random.nextInt(21) - 10);
			double j = f * (double)(attacker.getWorld().random.nextFloat() * 0.5F + 0.2F);
			Vec3d vec3d = new Vec3d(g, 0.0, h).normalize().multiply(j).rotateY(i);
			double k = f * (double)attacker.getWorld().random.nextFloat() * 0.5;
			target.addVelocity(vec3d.x, k, vec3d.z);
			target.velocityModified = true;
		}
	}
}
