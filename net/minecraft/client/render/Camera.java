/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RayTraceContext;

@Environment(value=EnvType.CLIENT)
public class Camera {
    private boolean ready;
    private BlockView area;
    private Entity focusedEntity;
    private Vec3d pos = Vec3d.ZERO;
    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    private final Vector3f horizontalPlane = new Vector3f(0.0f, 0.0f, 1.0f);
    private final Vector3f verticalPlane = new Vector3f(0.0f, 1.0f, 0.0f);
    private final Vector3f diagonalPlane = new Vector3f(1.0f, 0.0f, 0.0f);
    private float pitch;
    private float yaw;
    private final Quaternion field_21518 = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
    private boolean thirdPerson;
    private boolean inverseView;
    private float cameraY;
    private float lastCameraY;

    public void update(BlockView blockView, Entity entity, boolean bl, boolean bl2, float f) {
        this.ready = true;
        this.area = blockView;
        this.focusedEntity = entity;
        this.thirdPerson = bl;
        this.inverseView = bl2;
        this.setRotation(entity.getYaw(f), entity.getPitch(f));
        this.setPos(MathHelper.lerp((double)f, entity.prevX, entity.getX()), MathHelper.lerp((double)f, entity.prevY, entity.getY()) + (double)MathHelper.lerp(f, this.lastCameraY, this.cameraY), MathHelper.lerp((double)f, entity.prevZ, entity.getZ()));
        if (bl) {
            if (bl2) {
                this.setRotation(this.yaw + 180.0f, -this.pitch);
            }
            this.moveBy(-this.clipToSpace(4.0), 0.0, 0.0);
        } else if (entity instanceof LivingEntity && ((LivingEntity)entity).isSleeping()) {
            Direction direction = ((LivingEntity)entity).getSleepingDirection();
            this.setRotation(direction != null ? direction.asRotation() - 180.0f : 0.0f, 0.0f);
            this.moveBy(0.0, 0.3, 0.0);
        }
    }

    public void updateEyeHeight() {
        if (this.focusedEntity != null) {
            this.lastCameraY = this.cameraY;
            this.cameraY += (this.focusedEntity.getStandingEyeHeight() - this.cameraY) * 0.5f;
        }
    }

    private double clipToSpace(double d) {
        for (int i = 0; i < 8; ++i) {
            double e;
            Vec3d vec3d2;
            BlockHitResult hitResult;
            float f = (i & 1) * 2 - 1;
            float g = (i >> 1 & 1) * 2 - 1;
            float h = (i >> 2 & 1) * 2 - 1;
            Vec3d vec3d = this.pos.add(f *= 0.1f, g *= 0.1f, h *= 0.1f);
            if (((HitResult)(hitResult = this.area.rayTrace(new RayTraceContext(vec3d, vec3d2 = new Vec3d(this.pos.x - (double)this.horizontalPlane.getX() * d + (double)f + (double)h, this.pos.y - (double)this.horizontalPlane.getY() * d + (double)g, this.pos.z - (double)this.horizontalPlane.getZ() * d + (double)h), RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.NONE, this.focusedEntity)))).getType() == HitResult.Type.MISS || !((e = hitResult.getPos().distanceTo(this.pos)) < d)) continue;
            d = e;
        }
        return d;
    }

    protected void moveBy(double d, double e, double f) {
        double g = (double)this.horizontalPlane.getX() * d + (double)this.verticalPlane.getX() * e + (double)this.diagonalPlane.getX() * f;
        double h = (double)this.horizontalPlane.getY() * d + (double)this.verticalPlane.getY() * e + (double)this.diagonalPlane.getY() * f;
        double i = (double)this.horizontalPlane.getZ() * d + (double)this.verticalPlane.getZ() * e + (double)this.diagonalPlane.getZ() * f;
        this.setPos(new Vec3d(this.pos.x + g, this.pos.y + h, this.pos.z + i));
    }

    protected void setRotation(float f, float g) {
        this.pitch = g;
        this.yaw = f;
        this.field_21518.set(0.0f, 0.0f, 0.0f, 1.0f);
        this.field_21518.hamiltonProduct(Vector3f.POSITIVE_Y.getDegreesQuaternion(-f));
        this.field_21518.hamiltonProduct(Vector3f.POSITIVE_X.getDegreesQuaternion(g));
        this.horizontalPlane.set(0.0f, 0.0f, 1.0f);
        this.horizontalPlane.rotate(this.field_21518);
        this.verticalPlane.set(0.0f, 1.0f, 0.0f);
        this.verticalPlane.rotate(this.field_21518);
        this.diagonalPlane.set(1.0f, 0.0f, 0.0f);
        this.diagonalPlane.rotate(this.field_21518);
    }

    protected void setPos(double d, double e, double f) {
        this.setPos(new Vec3d(d, e, f));
    }

    protected void setPos(Vec3d vec3d) {
        this.pos = vec3d;
        this.blockPos.set(vec3d.x, vec3d.y, vec3d.z);
    }

    public Vec3d getPos() {
        return this.pos;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public Quaternion method_23767() {
        return this.field_21518;
    }

    public Entity getFocusedEntity() {
        return this.focusedEntity;
    }

    public boolean isReady() {
        return this.ready;
    }

    public boolean isThirdPerson() {
        return this.thirdPerson;
    }

    public FluidState getSubmergedFluidState() {
        if (!this.ready) {
            return Fluids.EMPTY.getDefaultState();
        }
        FluidState fluidState = this.area.getFluidState(this.blockPos);
        if (!fluidState.isEmpty() && this.pos.y >= (double)((float)this.blockPos.getY() + fluidState.getHeight(this.area, this.blockPos))) {
            return Fluids.EMPTY.getDefaultState();
        }
        return fluidState;
    }

    public final Vector3f getHorizontalPlane() {
        return this.horizontalPlane;
    }

    public final Vector3f getVerticalPlane() {
        return this.verticalPlane;
    }

    public void reset() {
        this.area = null;
        this.focusedEntity = null;
        this.ready = false;
    }
}

