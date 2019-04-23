/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
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
import org.jetbrains.annotations.Nullable;

public final class ProjectileUtil {
    public static HitResult getCollision(Entity entity, boolean bl, boolean bl2, @Nullable Entity entity22, RayTraceContext.ShapeType shapeType) {
        return ProjectileUtil.getCollision(entity, bl, bl2, entity22, shapeType, true, entity2 -> !entity2.isSpectator() && entity2.collides() && (bl2 || !entity2.isPartOf(entity22)) && !entity2.noClip, entity.getBoundingBox().stretch(entity.getVelocity()).expand(1.0));
    }

    public static HitResult getCollision(Entity entity, BoundingBox boundingBox, Predicate<Entity> predicate, RayTraceContext.ShapeType shapeType, boolean bl) {
        return ProjectileUtil.getCollision(entity, bl, false, null, shapeType, false, predicate, boundingBox);
    }

    @Nullable
    public static EntityHitResult getEntityCollision(World world, Entity entity, Vec3d vec3d, Vec3d vec3d2, BoundingBox boundingBox, Predicate<Entity> predicate) {
        return ProjectileUtil.getEntityCollision(world, entity, vec3d, vec3d2, boundingBox, predicate, Double.MAX_VALUE);
    }

    private static HitResult getCollision(Entity entity, boolean bl, boolean bl2, @Nullable Entity entity2, RayTraceContext.ShapeType shapeType, boolean bl3, Predicate<Entity> predicate, BoundingBox boundingBox) {
        double d = entity.x;
        double e = entity.y;
        double f = entity.z;
        Vec3d vec3d = entity.getVelocity();
        World world = entity.world;
        Vec3d vec3d2 = new Vec3d(d, e, f);
        if (bl3 && !world.doesNotCollide(entity, entity.getBoundingBox(), bl2 || entity2 == null ? ImmutableSet.of() : ProjectileUtil.getEntityAndRidingEntity(entity2))) {
            return new BlockHitResult(vec3d2, Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), new BlockPos(entity), false);
        }
        Vec3d vec3d3 = vec3d2.add(vec3d);
        HitResult hitResult = world.rayTrace(new RayTraceContext(vec3d2, vec3d3, shapeType, RayTraceContext.FluidHandling.NONE, entity));
        if (bl) {
            EntityHitResult hitResult2;
            if (((HitResult)hitResult).getType() != HitResult.Type.MISS) {
                vec3d3 = hitResult.getPos();
            }
            if ((hitResult2 = ProjectileUtil.getEntityCollision(world, entity, vec3d2, vec3d3, boundingBox, predicate)) != null) {
                hitResult = hitResult2;
            }
        }
        return hitResult;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static EntityHitResult rayTrace(Entity entity, Vec3d vec3d, Vec3d vec3d2, BoundingBox boundingBox, Predicate<Entity> predicate, double d) {
        World world = entity.world;
        double e = d;
        Entity entity2 = null;
        Vec3d vec3d3 = null;
        for (Entity entity3 : world.getEntities(entity, boundingBox, predicate)) {
            Vec3d vec3d4;
            double f;
            BoundingBox boundingBox2 = entity3.getBoundingBox().expand(entity3.getBoundingBoxMarginForTargeting());
            Optional<Vec3d> optional = boundingBox2.rayTrace(vec3d, vec3d2);
            if (boundingBox2.contains(vec3d)) {
                if (!(e >= 0.0)) continue;
                entity2 = entity3;
                vec3d3 = optional.orElse(vec3d);
                e = 0.0;
                continue;
            }
            if (!optional.isPresent() || !((f = vec3d.squaredDistanceTo(vec3d4 = optional.get())) < e) && e != 0.0) continue;
            if (entity3.getTopmostVehicle() == entity.getTopmostVehicle()) {
                if (e != 0.0) continue;
                entity2 = entity3;
                vec3d3 = vec3d4;
                continue;
            }
            entity2 = entity3;
            vec3d3 = vec3d4;
            e = f;
        }
        if (entity2 == null) {
            return null;
        }
        return new EntityHitResult(entity2, vec3d3);
    }

    @Nullable
    public static EntityHitResult getEntityCollision(World world, Entity entity, Vec3d vec3d, Vec3d vec3d2, BoundingBox boundingBox, Predicate<Entity> predicate, double d) {
        double e = d;
        Entity entity2 = null;
        for (Entity entity3 : world.getEntities(entity, boundingBox, predicate)) {
            double f;
            BoundingBox boundingBox2 = entity3.getBoundingBox().expand(0.3f);
            Optional<Vec3d> optional = boundingBox2.rayTrace(vec3d, vec3d2);
            if (!optional.isPresent() || !((f = vec3d.squaredDistanceTo(optional.get())) < e)) continue;
            entity2 = entity3;
            e = f;
        }
        if (entity2 == null) {
            return null;
        }
        return new EntityHitResult(entity2);
    }

    private static Set<Entity> getEntityAndRidingEntity(Entity entity) {
        Entity entity2 = entity.getVehicle();
        return entity2 != null ? ImmutableSet.of(entity, entity2) : ImmutableSet.of(entity);
    }

    public static final void method_7484(Entity entity, float f) {
        Vec3d vec3d = entity.getVelocity();
        float g = MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d));
        entity.yaw = (float)(MathHelper.atan2(vec3d.z, vec3d.x) * 57.2957763671875) + 90.0f;
        entity.pitch = (float)(MathHelper.atan2(g, vec3d.y) * 57.2957763671875) - 90.0f;
        while (entity.pitch - entity.prevPitch < -180.0f) {
            entity.prevPitch -= 360.0f;
        }
        while (entity.pitch - entity.prevPitch >= 180.0f) {
            entity.prevPitch += 360.0f;
        }
        while (entity.yaw - entity.prevYaw < -180.0f) {
            entity.prevYaw -= 360.0f;
        }
        while (entity.yaw - entity.prevYaw >= 180.0f) {
            entity.prevYaw += 360.0f;
        }
        entity.pitch = MathHelper.lerp(f, entity.prevPitch, entity.pitch);
        entity.yaw = MathHelper.lerp(f, entity.prevYaw, entity.yaw);
    }

    public static Hand getHandPossiblyHolding(LivingEntity livingEntity, Item item) {
        return livingEntity.getMainHandStack().getItem() == item ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }

    public static ProjectileEntity createArrowProjectile(LivingEntity livingEntity, ItemStack itemStack, float f) {
        ArrowItem arrowItem = (ArrowItem)(itemStack.getItem() instanceof ArrowItem ? itemStack.getItem() : Items.ARROW);
        ProjectileEntity projectileEntity = arrowItem.createProjectile(livingEntity.world, itemStack, livingEntity);
        projectileEntity.method_7435(livingEntity, f);
        if (itemStack.getItem() == Items.TIPPED_ARROW && projectileEntity instanceof ArrowEntity) {
            ((ArrowEntity)projectileEntity).initFromStack(itemStack);
        }
        return projectileEntity;
    }
}

