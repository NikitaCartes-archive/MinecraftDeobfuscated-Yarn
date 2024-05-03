package net.minecraft.entity.projectile;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public final class ProjectileUtil {
	private static final float DEFAULT_MARGIN = 0.3F;

	public static HitResult getCollision(Entity entity, Predicate<Entity> predicate) {
		Vec3d vec3d = entity.getVelocity();
		World world = entity.getWorld();
		Vec3d vec3d2 = entity.getPos();
		return getCollision(vec3d2, entity, predicate, vec3d, world, 0.3F, RaycastContext.ShapeType.COLLIDER);
	}

	public static HitResult getCollision(Entity entity, Predicate<Entity> predicate, RaycastContext.ShapeType raycastShapeType) {
		Vec3d vec3d = entity.getVelocity();
		World world = entity.getWorld();
		Vec3d vec3d2 = entity.getPos();
		return getCollision(vec3d2, entity, predicate, vec3d, world, 0.3F, raycastShapeType);
	}

	public static HitResult getCollision(Entity entity, Predicate<Entity> predicate, double range) {
		Vec3d vec3d = entity.getRotationVec(0.0F).multiply(range);
		World world = entity.getWorld();
		Vec3d vec3d2 = entity.getEyePos();
		return getCollision(vec3d2, entity, predicate, vec3d, world, 0.0F, RaycastContext.ShapeType.COLLIDER);
	}

	private static HitResult getCollision(
		Vec3d pos, Entity entity, Predicate<Entity> predicate, Vec3d velocity, World world, float margin, RaycastContext.ShapeType raycastShapeType
	) {
		Vec3d vec3d = pos.add(velocity);
		HitResult hitResult = world.raycast(new RaycastContext(pos, vec3d, raycastShapeType, RaycastContext.FluidHandling.NONE, entity));
		if (hitResult.getType() != HitResult.Type.MISS) {
			vec3d = hitResult.getPos();
		}

		HitResult hitResult2 = getEntityCollision(world, entity, pos, vec3d, entity.getBoundingBox().stretch(velocity).expand(1.0), predicate, margin);
		if (hitResult2 != null) {
			hitResult = hitResult2;
		}

		return hitResult;
	}

	@Nullable
	public static EntityHitResult raycast(Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, double maxDistance) {
		World world = entity.getWorld();
		double d = maxDistance;
		Entity entity2 = null;
		Vec3d vec3d = null;

		for (Entity entity3 : world.getOtherEntities(entity, box, predicate)) {
			Box box2 = entity3.getBoundingBox().expand((double)entity3.getTargetingMargin());
			Optional<Vec3d> optional = box2.raycast(min, max);
			if (box2.contains(min)) {
				if (d >= 0.0) {
					entity2 = entity3;
					vec3d = (Vec3d)optional.orElse(min);
					d = 0.0;
				}
			} else if (optional.isPresent()) {
				Vec3d vec3d2 = (Vec3d)optional.get();
				double e = min.squaredDistanceTo(vec3d2);
				if (e < d || d == 0.0) {
					if (entity3.getRootVehicle() == entity.getRootVehicle()) {
						if (d == 0.0) {
							entity2 = entity3;
							vec3d = vec3d2;
						}
					} else {
						entity2 = entity3;
						vec3d = vec3d2;
						d = e;
					}
				}
			}
		}

		return entity2 == null ? null : new EntityHitResult(entity2, vec3d);
	}

	@Nullable
	public static EntityHitResult getEntityCollision(World world, Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate) {
		return getEntityCollision(world, entity, min, max, box, predicate, 0.3F);
	}

	@Nullable
	public static EntityHitResult getEntityCollision(World world, Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, float margin) {
		double d = Double.MAX_VALUE;
		Entity entity2 = null;

		for (Entity entity3 : world.getOtherEntities(entity, box, predicate)) {
			Box box2 = entity3.getBoundingBox().expand((double)margin);
			Optional<Vec3d> optional = box2.raycast(min, max);
			if (optional.isPresent()) {
				double e = min.squaredDistanceTo((Vec3d)optional.get());
				if (e < d) {
					entity2 = entity3;
					d = e;
				}
			}
		}

		return entity2 == null ? null : new EntityHitResult(entity2);
	}

	public static void setRotationFromVelocity(Entity entity, float delta) {
		Vec3d vec3d = entity.getVelocity();
		if (vec3d.lengthSquared() != 0.0) {
			double d = vec3d.horizontalLength();
			entity.setYaw((float)(MathHelper.atan2(vec3d.z, vec3d.x) * 180.0F / (float)Math.PI) + 90.0F);
			entity.setPitch((float)(MathHelper.atan2(d, vec3d.y) * 180.0F / (float)Math.PI) - 90.0F);

			while (entity.getPitch() - entity.prevPitch < -180.0F) {
				entity.prevPitch -= 360.0F;
			}

			while (entity.getPitch() - entity.prevPitch >= 180.0F) {
				entity.prevPitch += 360.0F;
			}

			while (entity.getYaw() - entity.prevYaw < -180.0F) {
				entity.prevYaw -= 360.0F;
			}

			while (entity.getYaw() - entity.prevYaw >= 180.0F) {
				entity.prevYaw += 360.0F;
			}

			entity.setPitch(MathHelper.lerp(delta, entity.prevPitch, entity.getPitch()));
			entity.setYaw(MathHelper.lerp(delta, entity.prevYaw, entity.getYaw()));
		}
	}

	public static Hand getHandPossiblyHolding(LivingEntity entity, Item item) {
		return entity.getMainHandStack().isOf(item) ? Hand.MAIN_HAND : Hand.OFF_HAND;
	}

	public static PersistentProjectileEntity createArrowProjectile(LivingEntity entity, ItemStack stack, float damageModifier, @Nullable ItemStack bow) {
		ArrowItem arrowItem = (ArrowItem)(stack.getItem() instanceof ArrowItem ? stack.getItem() : Items.ARROW);
		PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(entity.getWorld(), stack, entity, bow);
		persistentProjectileEntity.applyDamageModifier(damageModifier);
		return persistentProjectileEntity;
	}
}
