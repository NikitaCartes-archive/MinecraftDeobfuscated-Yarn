package net.minecraft.entity;

import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.random.Random;

@FunctionalInterface
public interface ProjectileDeflector {
	ProjectileDeflector NONE = (projectile, hitEntity, random) -> {
	};
	ProjectileDeflector SIMPLE = (projectile, hitEntity, random) -> {
		float f = 180.0F + random.nextFloat() * 20.0F;
		projectile.setVelocity(projectile.getVelocity().multiply(-0.5));
		projectile.setYaw(projectile.getYaw() + f);
		projectile.prevYaw += f;
		projectile.velocityModified = true;
		projectile.onDeflected();
	};

	void deflect(ProjectileEntity projectile, Entity hitEntity, Random random);
}
