package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;

public class BreezeMovementUtil {
	private static final double MAX_MOVE_DISTANCE = 50.0;

	public static Vec3d getRandomPosBehindTarget(LivingEntity target, Random random) {
		int i = 90;
		float f = target.headYaw + 180.0F + (float)random.nextGaussian() * 90.0F / 2.0F;
		float g = MathHelper.lerp(random.nextFloat(), 4.0F, 8.0F);
		Vec3d vec3d = Vec3d.fromPolar(0.0F, f).multiply((double)g);
		return target.getPos().add(vec3d);
	}

	public static boolean canMoveTo(BreezeEntity breeze, Vec3d pos) {
		Vec3d vec3d = new Vec3d(breeze.getX(), breeze.getY(), breeze.getZ());
		return pos.distanceTo(vec3d) > 50.0
			? false
			: breeze.getWorld().raycast(new RaycastContext(vec3d, pos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, breeze)).getType()
				== HitResult.Type.MISS;
	}
}
