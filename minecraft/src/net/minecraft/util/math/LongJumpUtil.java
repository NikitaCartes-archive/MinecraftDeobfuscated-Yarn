package net.minecraft.util.math;

import java.util.Optional;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.mob.MobEntity;

public final class LongJumpUtil {
	public static Optional<Vec3d> getJumpingVelocity(MobEntity entity, Vec3d jumpTarget, float maxVelocity, int angle, boolean requireClearPath) {
		Vec3d vec3d = entity.getPos();
		Vec3d vec3d2 = new Vec3d(jumpTarget.x - vec3d.x, 0.0, jumpTarget.z - vec3d.z).normalize().multiply(0.5);
		Vec3d vec3d3 = jumpTarget.subtract(vec3d2);
		Vec3d vec3d4 = vec3d3.subtract(vec3d);
		float f = (float)angle * (float) Math.PI / 180.0F;
		double d = Math.atan2(vec3d4.z, vec3d4.x);
		double e = vec3d4.subtract(0.0, vec3d4.y, 0.0).lengthSquared();
		double g = Math.sqrt(e);
		double h = vec3d4.y;
		double i = entity.getFinalGravity();
		double j = Math.sin((double)(2.0F * f));
		double k = Math.pow(Math.cos((double)f), 2.0);
		double l = Math.sin((double)f);
		double m = Math.cos((double)f);
		double n = Math.sin(d);
		double o = Math.cos(d);
		double p = e * i / (g * j - 2.0 * h * k);
		if (p < 0.0) {
			return Optional.empty();
		} else {
			double q = Math.sqrt(p);
			if (q > (double)maxVelocity) {
				return Optional.empty();
			} else {
				double r = q * m;
				double s = q * l;
				if (requireClearPath) {
					int t = MathHelper.ceil(g / r) * 2;
					double u = 0.0;
					Vec3d vec3d5 = null;
					EntityDimensions entityDimensions = entity.getDimensions(EntityPose.LONG_JUMPING);

					for (int v = 0; v < t - 1; v++) {
						u += g / (double)t;
						double w = l / m * u - Math.pow(u, 2.0) * i / (2.0 * p * Math.pow(m, 2.0));
						double x = u * o;
						double y = u * n;
						Vec3d vec3d6 = new Vec3d(vec3d.x + x, vec3d.y + w, vec3d.z + y);
						if (vec3d5 != null && !isPathClear(entity, entityDimensions, vec3d5, vec3d6)) {
							return Optional.empty();
						}

						vec3d5 = vec3d6;
					}
				}

				return Optional.of(new Vec3d(r * o, s, r * n).multiply(0.95F));
			}
		}
	}

	private static boolean isPathClear(MobEntity entity, EntityDimensions dimensions, Vec3d prevPos, Vec3d nextPos) {
		Vec3d vec3d = nextPos.subtract(prevPos);
		double d = (double)Math.min(dimensions.width(), dimensions.height());
		int i = MathHelper.ceil(vec3d.length() / d);
		Vec3d vec3d2 = vec3d.normalize();
		Vec3d vec3d3 = prevPos;

		for (int j = 0; j < i; j++) {
			vec3d3 = j == i - 1 ? nextPos : vec3d3.add(vec3d2.multiply(d * 0.9F));
			if (!entity.getWorld().isSpaceEmpty(entity, dimensions.getBoxAt(vec3d3))) {
				return false;
			}
		}

		return true;
	}
}
