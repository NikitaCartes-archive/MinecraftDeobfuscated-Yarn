package net.minecraft.entity.projectile;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public final class ProjectileUtil {
	public static HitResult getCollision(Entity entity, Predicate<Entity> predicate, RayTraceContext.ShapeType shapeType) {
		Vec3d vec3d = entity.getVelocity();
		World world = entity.world;
		Vec3d vec3d2 = entity.getPos();
		Vec3d vec3d3 = vec3d2.add(vec3d);
		HitResult hitResult = world.rayTrace(new RayTraceContext(vec3d2, vec3d3, shapeType, RayTraceContext.FluidHandling.NONE, entity));
		if (hitResult.getType() != HitResult.Type.MISS) {
			vec3d3 = hitResult.getPos();
		}

		HitResult hitResult2 = getEntityCollision(world, entity, vec3d2, vec3d3, entity.getBoundingBox().stretch(entity.getVelocity()).expand(1.0), predicate);
		if (hitResult2 != null) {
			hitResult = hitResult2;
		}

		return hitResult;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static EntityHitResult rayTrace(Entity entity, Vec3d vec3d, Vec3d vec3d2, Box box, Predicate<Entity> predicate, double d) {
		World world = entity.world;
		double e = d;
		Entity entity2 = null;
		Vec3d vec3d3 = null;

		for (Entity entity3 : world.getOtherEntities(entity, box, predicate)) {
			Box box2 = entity3.getBoundingBox().expand((double)entity3.getTargetingMargin());
			Optional<Vec3d> optional = box2.rayTrace(vec3d, vec3d2);
			if (box2.contains(vec3d)) {
				if (e >= 0.0) {
					entity2 = entity3;
					vec3d3 = (Vec3d)optional.orElse(vec3d);
					e = 0.0;
				}
			} else if (optional.isPresent()) {
				Vec3d vec3d4 = (Vec3d)optional.get();
				double f = vec3d.squaredDistanceTo(vec3d4);
				if (f < e || e == 0.0) {
					if (entity3.getRootVehicle() == entity.getRootVehicle()) {
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
	public static EntityHitResult getEntityCollision(World world, Entity entity, Vec3d vec3d, Vec3d vec3d2, Box box, Predicate<Entity> predicate) {
		double d = Double.MAX_VALUE;
		Entity entity2 = null;

		for (Entity entity3 : world.getOtherEntities(entity, box, predicate)) {
			Box box2 = entity3.getBoundingBox().expand(0.3F);
			Optional<Vec3d> optional = box2.rayTrace(vec3d, vec3d2);
			if (optional.isPresent()) {
				double e = vec3d.squaredDistanceTo((Vec3d)optional.get());
				if (e < d) {
					entity2 = entity3;
					d = e;
				}
			}
		}

		return entity2 == null ? null : new EntityHitResult(entity2);
	}

	public static final void method_7484(Entity entity, float f) {
		Vec3d vec3d = entity.getVelocity();
		if (vec3d.lengthSquared() != 0.0) {
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

	public static Hand getHandPossiblyHolding(LivingEntity entity, Item item) {
		return entity.getMainHandStack().getItem() == item ? Hand.MAIN_HAND : Hand.OFF_HAND;
	}

	public static PersistentProjectileEntity createArrowProjectile(LivingEntity entity, ItemStack stack, float damageModifier) {
		ArrowItem arrowItem = (ArrowItem)(stack.getItem() instanceof ArrowItem ? stack.getItem() : Items.ARROW);
		PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(entity.world, stack, entity);
		persistentProjectileEntity.applyEnchantmentEffects(entity, damageModifier);
		if (stack.getItem() == Items.TIPPED_ARROW && persistentProjectileEntity instanceof ArrowEntity) {
			((ArrowEntity)persistentProjectileEntity).initFromStack(stack);
		}

		return persistentProjectileEntity;
	}
}
