package net.minecraft.entity.mob;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.Vec3d;

public interface Hoglin {
	int field_30546 = 10;

	int getMovementCooldownTicks();

	static boolean tryAttack(LivingEntity attacker, LivingEntity target) {
		float f = (float)attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
		float g;
		if (!attacker.isBaby() && (int)f > 0) {
			g = f / 2.0F + (float)attacker.getWorld().random.nextInt((int)f);
		} else {
			g = f;
		}

		boolean bl = target.damage(attacker.getDamageSources().mobAttack(attacker), g);
		if (bl) {
			attacker.applyDamageEffects(attacker, target);
			if (!attacker.isBaby()) {
				knockback(attacker, target);
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
