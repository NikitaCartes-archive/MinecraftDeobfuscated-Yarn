package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

@FunctionalInterface
public interface ProjectileDeflection {
	ProjectileDeflection NONE = (projectile, hitEntity, random) -> {
	};
	ProjectileDeflection SIMPLE = (projectile, hitEntity, random) -> {
		float f = 170.0F + random.nextFloat() * 20.0F;
		projectile.setVelocity(projectile.getVelocity().multiply(-0.5));
		projectile.setYaw(projectile.getYaw() + f);
		projectile.prevYaw += f;
		projectile.velocityModified = true;
	};
	ProjectileDeflection REDIRECTED = (projectile, hitEntity, random) -> {
		if (hitEntity != null) {
			Vec3d vec3d = hitEntity.getRotationVector();
			projectile.setVelocity(vec3d.multiply(projectile.getVelocity().length()));
			projectile.velocityModified = true;
		}
	};
	ProjectileDeflection TRANSFER_VELOCITY_DIRECTION = (projectile, hitEntity, random) -> {
		if (hitEntity != null) {
			Vec3d vec3d = hitEntity.getVelocity().normalize();
			projectile.setVelocity(vec3d.multiply(projectile.getVelocity().length()));
			projectile.velocityModified = true;
		}
	};

	void deflect(ProjectileEntity projectile, @Nullable Entity hitEntity, Random random);
}
