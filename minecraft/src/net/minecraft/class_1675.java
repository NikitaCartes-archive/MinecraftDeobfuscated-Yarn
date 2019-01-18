package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public final class class_1675 {
	public static HitResult method_7482(Entity entity, boolean bl, boolean bl2, @Nullable Entity entity2) {
		double d = entity.x;
		double e = entity.y;
		double f = entity.z;
		double g = entity.velocityX;
		double h = entity.velocityY;
		double i = entity.velocityZ;
		World world = entity.world;
		Vec3d vec3d = new Vec3d(d, e, f);
		if (!world.method_8590(entity, entity.getBoundingBox(), (Set<Entity>)(!bl2 && entity2 != null ? method_7483(entity2) : ImmutableSet.of()))) {
			return new BlockHitResult(vec3d, Direction.getFacing(g, h, i), new BlockPos(entity), false);
		} else {
			Vec3d vec3d2 = new Vec3d(d + g, e + h, f + i);
			HitResult hitResult = world.method_17742(
				new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.field_17558, RayTraceContext.FluidHandling.NONE, entity)
			);
			if (bl) {
				if (hitResult.getType() != HitResult.Type.NONE) {
					vec3d2 = hitResult.getPos();
				}

				Entity entity3 = null;
				List<Entity> list = world.getVisibleEntities(entity, entity.getBoundingBox().stretch(g, h, i).expand(1.0));
				double j = 0.0;

				for (int k = 0; k < list.size(); k++) {
					Entity entity4 = (Entity)list.get(k);
					if (entity4.doesCollide() && (bl2 || !entity4.isPartOf(entity2)) && !entity4.noClip) {
						BoundingBox boundingBox = entity4.getBoundingBox().expand(0.3F);
						Optional<Vec3d> optional = boundingBox.rayTrace(vec3d, vec3d2);
						if (optional.isPresent()) {
							double l = vec3d.squaredDistanceTo((Vec3d)optional.get());
							if (l < j || j == 0.0) {
								entity3 = entity4;
								j = l;
							}
						}
					}
				}

				if (entity3 != null) {
					hitResult = new EntityHitResult(entity3);
				}
			}

			return hitResult;
		}
	}

	private static Set<Entity> method_7483(Entity entity) {
		Entity entity2 = entity.getRiddenEntity();
		return entity2 != null ? ImmutableSet.of(entity, entity2) : ImmutableSet.of(entity);
	}

	public static final void method_7484(Entity entity, float f) {
		double d = entity.velocityX;
		double e = entity.velocityY;
		double g = entity.velocityZ;
		float h = MathHelper.sqrt(d * d + g * g);
		entity.yaw = (float)(MathHelper.atan2(g, d) * 180.0F / (float)Math.PI) + 90.0F;
		entity.pitch = (float)(MathHelper.atan2((double)h, e) * 180.0F / (float)Math.PI) - 90.0F;

		while (entity.pitch - entity.prevPitch < -180.0F) {
			entity.prevPitch -= 360.0F;
		}

		while (entity.pitch - entity.prevPitch >= 180.0F) {
			entity.prevPitch += 360.0F;
		}

		while (entity.yaw - entity.prevYaw < -180.0F) {
			entity.prevYaw -= 360.0F;
		}

		while (entity.yaw - entity.prevYaw >= 180.0F) {
			entity.prevYaw += 360.0F;
		}

		entity.pitch = MathHelper.lerp(f, entity.prevPitch, entity.pitch);
		entity.yaw = MathHelper.lerp(f, entity.prevYaw, entity.yaw);
	}
}
