package net.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;

public class class_9075 {
	private static final double field_47817 = 50.0;

	public static Vec3d method_55751(LivingEntity livingEntity, Random random) {
		int i = 90;
		float f = livingEntity.headYaw + 180.0F + (float)random.nextGaussian() * 90.0F / 2.0F;
		float g = MathHelper.lerp(random.nextFloat(), 4.0F, 8.0F);
		Vec3d vec3d = Vec3d.fromPolar(0.0F, f).multiply((double)g);
		return livingEntity.getPos().add(vec3d);
	}

	public static boolean method_55752(BreezeEntity breezeEntity, Vec3d vec3d) {
		Vec3d vec3d2 = new Vec3d(breezeEntity.getX(), breezeEntity.getY(), breezeEntity.getZ());
		return vec3d.distanceTo(vec3d2) > 50.0
			? false
			: breezeEntity.getWorld()
					.raycast(new RaycastContext(vec3d2, vec3d, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, breezeEntity))
					.getType()
				== HitResult.Type.MISS;
	}
}
