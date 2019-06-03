/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
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
    private Vec3d horizontalPlane;
    private Vec3d verticalPlane;
    private Vec3d diagonalPlane;
    private float pitch;
    private float yaw;
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
        this.setPos(MathHelper.lerp((double)f, entity.prevX, entity.x), MathHelper.lerp((double)f, entity.prevY, entity.y) + (double)MathHelper.lerp(f, this.lastCameraY, this.cameraY), MathHelper.lerp((double)f, entity.prevZ, entity.z));
        if (bl) {
            if (bl2) {
                this.yaw += 180.0f;
                this.pitch += -this.pitch * 2.0f;
                this.updateRotation();
            }
            this.moveBy(-this.clipToSpace(4.0), 0.0, 0.0);
        } else if (entity instanceof LivingEntity && ((LivingEntity)entity).isSleeping()) {
            Direction direction = ((LivingEntity)entity).getSleepingDirection();
            this.setRotation(direction != null ? direction.asRotation() - 180.0f : 0.0f, 0.0f);
            this.moveBy(0.0, 0.3, 0.0);
        } else {
            this.moveBy(-0.05f, 0.0, 0.0);
        }
        GlStateManager.rotatef(this.pitch, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(this.yaw + 180.0f, 0.0f, 1.0f, 0.0f);
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
            if (((HitResult)(hitResult = this.area.rayTrace(new RayTraceContext(vec3d, vec3d2 = new Vec3d(this.pos.x - this.horizontalPlane.x * d + (double)f + (double)h, this.pos.y - this.horizontalPlane.y * d + (double)g, this.pos.z - this.horizontalPlane.z * d + (double)h), RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.NONE, this.focusedEntity)))).getType() == HitResult.Type.MISS || !((e = hitResult.getPos().distanceTo(this.pos)) < d)) continue;
            d = e;
        }
        return d;
    }

    protected void moveBy(double d, double e, double f) {
        double g = this.horizontalPlane.x * d + this.verticalPlane.x * e + this.diagonalPlane.x * f;
        double h = this.horizontalPlane.y * d + this.verticalPlane.y * e + this.diagonalPlane.y * f;
        double i = this.horizontalPlane.z * d + this.verticalPlane.z * e + this.diagonalPlane.z * f;
        this.setPos(new Vec3d(this.pos.x + g, this.pos.y + h, this.pos.z + i));
    }

    protected void updateRotation() {
        float f = MathHelper.cos((this.yaw + 90.0f) * ((float)Math.PI / 180));
        float g = MathHelper.sin((this.yaw + 90.0f) * ((float)Math.PI / 180));
        float h = MathHelper.cos(-this.pitch * ((float)Math.PI / 180));
        float i = MathHelper.sin(-this.pitch * ((float)Math.PI / 180));
        float j = MathHelper.cos((-this.pitch + 90.0f) * ((float)Math.PI / 180));
        float k = MathHelper.sin((-this.pitch + 90.0f) * ((float)Math.PI / 180));
        this.horizontalPlane = new Vec3d(f * h, i, g * h);
        this.verticalPlane = new Vec3d(f * j, k, g * j);
        this.diagonalPlane = this.horizontalPlane.crossProduct(this.verticalPlane).multiply(-1.0);
    }

    protected void setRotation(float f, float g) {
        this.pitch = g;
        this.yaw = f;
        this.updateRotation();
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

    public final Vec3d getHorizontalPlane() {
        return this.horizontalPlane;
    }

    public final Vec3d getVerticalPlane() {
        return this.verticalPlane;
    }

    public void reset() {
        this.area = null;
        this.focusedEntity = null;
        this.ready = false;
    }
}

