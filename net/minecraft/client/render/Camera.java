/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
    private final Quaternionf rotation = new Quaternionf(0.0f, 0.0f, 0.0f, 1.0f);
    private boolean thirdPerson;
    private float cameraY;
    private float lastCameraY;
    public static final float field_32133 = 0.083333336f;

    public void update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta) {
        this.ready = true;
        this.area = area;
        this.focusedEntity = focusedEntity;
        this.thirdPerson = thirdPerson;
        this.setRotation(focusedEntity.getYaw(tickDelta), focusedEntity.getPitch(tickDelta));
        this.setPos(MathHelper.lerp((double)tickDelta, focusedEntity.prevX, focusedEntity.getX()), MathHelper.lerp((double)tickDelta, focusedEntity.prevY, focusedEntity.getY()) + (double)MathHelper.lerp(tickDelta, this.lastCameraY, this.cameraY), MathHelper.lerp((double)tickDelta, focusedEntity.prevZ, focusedEntity.getZ()));
        if (thirdPerson) {
            if (inverseView) {
                this.setRotation(this.yaw + 180.0f, -this.pitch);
            }
            this.moveBy(-this.clipToSpace(4.0), 0.0, 0.0);
        } else if (focusedEntity instanceof LivingEntity && ((LivingEntity)focusedEntity).isSleeping()) {
            Direction direction = ((LivingEntity)focusedEntity).getSleepingDirection();
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

    private double clipToSpace(double desiredCameraDistance) {
        for (int i = 0; i < 8; ++i) {
            double d;
            Vec3d vec3d2;
            BlockHitResult hitResult;
            float f = (i & 1) * 2 - 1;
            float g = (i >> 1 & 1) * 2 - 1;
            float h = (i >> 2 & 1) * 2 - 1;
            Vec3d vec3d = this.pos.add(f *= 0.1f, g *= 0.1f, h *= 0.1f);
            if (((HitResult)(hitResult = this.area.raycast(new RaycastContext(vec3d, vec3d2 = new Vec3d(this.pos.x - (double)this.horizontalPlane.x() * desiredCameraDistance + (double)f, this.pos.y - (double)this.horizontalPlane.y() * desiredCameraDistance + (double)g, this.pos.z - (double)this.horizontalPlane.z() * desiredCameraDistance + (double)h), RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, this.focusedEntity)))).getType() == HitResult.Type.MISS || !((d = hitResult.getPos().distanceTo(this.pos)) < desiredCameraDistance)) continue;
            desiredCameraDistance = d;
        }
        return desiredCameraDistance;
    }

    protected void moveBy(double x, double y, double z) {
        double d = (double)this.horizontalPlane.x() * x + (double)this.verticalPlane.x() * y + (double)this.diagonalPlane.x() * z;
        double e = (double)this.horizontalPlane.y() * x + (double)this.verticalPlane.y() * y + (double)this.diagonalPlane.y() * z;
        double f = (double)this.horizontalPlane.z() * x + (double)this.verticalPlane.z() * y + (double)this.diagonalPlane.z() * z;
        this.setPos(new Vec3d(this.pos.x + d, this.pos.y + e, this.pos.z + f));
    }

    protected void setRotation(float yaw, float pitch) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.rotation.rotationYXZ(-yaw * ((float)Math.PI / 180), pitch * ((float)Math.PI / 180), 0.0f);
        this.horizontalPlane.set(0.0f, 0.0f, 1.0f).rotate(this.rotation);
        this.verticalPlane.set(0.0f, 1.0f, 0.0f).rotate(this.rotation);
        this.diagonalPlane.set(1.0f, 0.0f, 0.0f).rotate(this.rotation);
    }

    protected void setPos(double x, double y, double z) {
        this.setPos(new Vec3d(x, y, z));
    }

    protected void setPos(Vec3d pos) {
        this.pos = pos;
        this.blockPos.set(pos.x, pos.y, pos.z);
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

    public Quaternionf getRotation() {
        return this.rotation;
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

    /**
     * {@return the field of vision of this camera}
     * 
     * @see GameRenderer#CAMERA_DEPTH
     */
    public Projection getProjection() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        double d = (double)minecraftClient.getWindow().getFramebufferWidth() / (double)minecraftClient.getWindow().getFramebufferHeight();
        double e = Math.tan((double)((float)minecraftClient.options.getFov().getValue().intValue() * ((float)Math.PI / 180)) / 2.0) * (double)0.05f;
        double f = e * d;
        Vec3d vec3d = new Vec3d(this.horizontalPlane).multiply(0.05f);
        Vec3d vec3d2 = new Vec3d(this.diagonalPlane).multiply(f);
        Vec3d vec3d3 = new Vec3d(this.verticalPlane).multiply(e);
        return new Projection(vec3d, vec3d2, vec3d3);
    }

    public CameraSubmersionType getSubmersionType() {
        if (!this.ready) {
            return CameraSubmersionType.NONE;
        }
        FluidState fluidState = this.area.getFluidState(this.blockPos);
        if (fluidState.isIn(FluidTags.WATER) && this.pos.y < (double)((float)this.blockPos.getY() + fluidState.getHeight(this.area, this.blockPos))) {
            return CameraSubmersionType.WATER;
        }
        Projection projection = this.getProjection();
        List<Vec3d> list = Arrays.asList(projection.center, projection.getBottomRight(), projection.getTopRight(), projection.getBottomLeft(), projection.getTopLeft());
        for (Vec3d vec3d : list) {
            Vec3d vec3d2 = this.pos.add(vec3d);
            BlockPos blockPos = BlockPos.ofFloored(vec3d2);
            FluidState fluidState2 = this.area.getFluidState(blockPos);
            if (fluidState2.isIn(FluidTags.LAVA)) {
                if (!(vec3d2.y <= (double)(fluidState2.getHeight(this.area, blockPos) + (float)blockPos.getY()))) continue;
                return CameraSubmersionType.LAVA;
            }
            BlockState blockState = this.area.getBlockState(blockPos);
            if (!blockState.isOf(Blocks.POWDER_SNOW)) continue;
            return CameraSubmersionType.POWDER_SNOW;
        }
        return CameraSubmersionType.NONE;
    }

    public final Vector3f getHorizontalPlane() {
        return this.horizontalPlane;
    }

    public final Vector3f getVerticalPlane() {
        return this.verticalPlane;
    }

    public final Vector3f getDiagonalPlane() {
        return this.diagonalPlane;
    }

    public void reset() {
        this.area = null;
        this.focusedEntity = null;
        this.ready = false;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Projection {
        final Vec3d center;
        private final Vec3d x;
        private final Vec3d y;

        Projection(Vec3d center, Vec3d x, Vec3d y) {
            this.center = center;
            this.x = x;
            this.y = y;
        }

        public Vec3d getBottomRight() {
            return this.center.add(this.y).add(this.x);
        }

        public Vec3d getTopRight() {
            return this.center.add(this.y).subtract(this.x);
        }

        public Vec3d getBottomLeft() {
            return this.center.subtract(this.y).add(this.x);
        }

        public Vec3d getTopLeft() {
            return this.center.subtract(this.y).subtract(this.x);
        }

        public Vec3d getPosition(float factorX, float factorY) {
            return this.center.add(this.y.multiply(factorY)).subtract(this.x.multiply(factorX));
        }
    }
}

