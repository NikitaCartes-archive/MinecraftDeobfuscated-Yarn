package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
	public static HitResult method_18076(Entity entity, boolean bl, boolean bl2, @Nullable Entity entity2, RayTraceContext.ShapeType shapeType) {
		return method_7482(
			entity,
			bl,
			bl2,
			entity2,
			shapeType,
			true,
			entity2x -> !entity2x.isSpectator() && entity2x.doesCollide() && (bl2 || !entity2x.isPartOf(entity2)) && !entity2x.noClip,
			entity.getBoundingBox().method_18804(entity.getVelocity()).expand(1.0)
		);
	}

	public static HitResult method_18074(Entity entity, BoundingBox boundingBox, Predicate<Entity> predicate, RayTraceContext.ShapeType shapeType, boolean bl) {
		return method_7482(entity, bl, false, null, shapeType, false, predicate, boundingBox);
	}

	@Nullable
	public static EntityHitResult method_18077(World world, Entity entity, Vec3d vec3d, Vec3d vec3d2, BoundingBox boundingBox, Predicate<Entity> predicate) {
		return method_18078(world, entity, vec3d, vec3d2, boundingBox, predicate, Double.MAX_VALUE);
	}

	private static HitResult method_7482(
		Entity entity,
		boolean bl,
		boolean bl2,
		@Nullable Entity entity2,
		RayTraceContext.ShapeType shapeType,
		boolean bl3,
		Predicate<Entity> predicate,
		BoundingBox boundingBox
	) {
		double d = entity.x;
		double e = entity.y;
		double f = entity.z;
		Vec3d vec3d = entity.getVelocity();
		World world = entity.world;
		Vec3d vec3d2 = new Vec3d(d, e, f);
		if (bl3 && !world.isEntityColliding(entity, entity.getBoundingBox(), (Set<Entity>)(!bl2 && entity2 != null ? method_7483(entity2) : ImmutableSet.of()))) {
			return new BlockHitResult(vec3d2, Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), new BlockPos(entity), false);
		} else {
			Vec3d vec3d3 = vec3d2.add(vec3d);
			HitResult hitResult = world.rayTrace(new RayTraceContext(vec3d2, vec3d3, shapeType, RayTraceContext.FluidHandling.NONE, entity));
			if (bl) {
				if (hitResult.getType() != HitResult.Type.NONE) {
					vec3d3 = hitResult.getPos();
				}

				HitResult hitResult2 = method_18077(world, entity, vec3d2, vec3d3, boundingBox, predicate);
				if (hitResult2 != null) {
					hitResult = hitResult2;
				}
			}

			return hitResult;
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static EntityHitResult method_18075(Entity entity, Vec3d vec3d, Vec3d vec3d2, BoundingBox boundingBox, Predicate<Entity> predicate, double d) {
		World world = entity.world;
		double e = d;
		Entity entity2 = null;
		Vec3d vec3d3 = null;

		for (Entity entity3 : world.getEntities(entity, boundingBox, predicate)) {
			BoundingBox boundingBox2 = entity3.getBoundingBox().expand((double)entity3.getBoundingBoxMarginForTargeting());
			Optional<Vec3d> optional = boundingBox2.rayTrace(vec3d, vec3d2);
			if (boundingBox2.contains(vec3d)) {
				if (e >= 0.0) {
					entity2 = entity3;
					vec3d3 = (Vec3d)optional.orElse(vec3d);
					e = 0.0;
				}
			} else if (optional.isPresent()) {
				Vec3d vec3d4 = (Vec3d)optional.get();
				double f = vec3d.squaredDistanceTo(vec3d4);
				if (f < e || e == 0.0) {
					if (entity3.getTopmostRiddenEntity() == entity.getTopmostRiddenEntity()) {
						if (e == 0.0) {
							entity2 = entity3;
							vec3d3 = vec3d4;
						}
					} else {
						entity2 = entity3;
						vec3d3 = vec3d4;
						e = f;
					}
				}
			}
		}

		return entity2 == null ? null : new EntityHitResult(entity2, vec3d3);
	}

	@Nullable
	public static EntityHitResult method_18078(
		World world, Entity entity, Vec3d vec3d, Vec3d vec3d2, BoundingBox boundingBox, Predicate<Entity> predicate, double d
	) {
		double e = d;
		Entity entity2 = null;

		for (Entity entity3 : world.getEntities(entity, boundingBox, predicate)) {
			BoundingBox boundingBox2 = entity3.getBoundingBox().expand(0.3F);
			Optional<Vec3d> optional = boundingBox2.rayTrace(vec3d, vec3d2);
			if (optional.isPresent()) {
				double f = vec3d.squaredDistanceTo((Vec3d)optional.get());
				if (f < e) {
					entity2 = entity3;
					e = f;
				}
			}
		}

		return entity2 == null ? null : new EntityHitResult(entity2);
	}

	private static Set<Entity> method_7483(Entity entity) {
		Entity entity2 = entity.getRiddenEntity();
		return entity2 != null ? ImmutableSet.of(entity, entity2) : ImmutableSet.of(entity);
	}

	public static final void method_7484(Entity entity, float f) {
		Vec3d vec3d = entity.getVelocity();
		float g = MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d));
		entity.yaw = (float)(MathHelper.atan2(vec3d.z, vec3d.x) * 180.0F / (float)Math.PI) + 90.0F;
		entity.pitch = (float)(MathHelper.atan2((double)g, vec3d.y) * 180.0F / (float)Math.PI) - 90.0F;

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
