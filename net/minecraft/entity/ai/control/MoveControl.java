/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.control;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.control.Control;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;

public class MoveControl
implements Control {
    public static final float field_30197 = 5.0E-4f;
    public static final float field_30198 = 2.5000003E-7f;
    protected static final int field_30199 = 90;
    protected final MobEntity entity;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected double speed;
    protected float forwardMovement;
    protected float sidewaysMovement;
    protected State state = State.WAIT;

    public MoveControl(MobEntity entity) {
        this.entity = entity;
    }

    public boolean isMoving() {
        return this.state == State.MOVE_TO;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void moveTo(double x, double y, double z, double speed) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
        this.speed = speed;
        if (this.state != State.JUMPING) {
            this.state = State.MOVE_TO;
        }
    }

    public void strafeTo(float forward, float sideways) {
        this.state = State.STRAFE;
        this.forwardMovement = forward;
        this.sidewaysMovement = sideways;
        this.speed = 0.25;
    }

    public void tick() {
        if (this.state == State.STRAFE) {
            float n;
            float f = (float)this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            float g = (float)this.speed * f;
            float h = this.forwardMovement;
            float i = this.sidewaysMovement;
            float j = MathHelper.sqrt(h * h + i * i);
            if (j < 1.0f) {
                j = 1.0f;
            }
            j = g / j;
            float k = MathHelper.sin(this.entity.yaw * ((float)Math.PI / 180));
            float l = MathHelper.cos(this.entity.yaw * ((float)Math.PI / 180));
            float m = (h *= j) * l - (i *= j) * k;
            if (!this.method_25946(m, n = i * l + h * k)) {
                this.forwardMovement = 1.0f;
                this.sidewaysMovement = 0.0f;
            }
            this.entity.setMovementSpeed(g);
            this.entity.setForwardSpeed(this.forwardMovement);
            this.entity.setSidewaysSpeed(this.sidewaysMovement);
            this.state = State.WAIT;
        } else if (this.state == State.MOVE_TO) {
            this.state = State.WAIT;
            double d = this.targetX - this.entity.getX();
            double e = this.targetZ - this.entity.getZ();
            double o = this.targetY - this.entity.getY();
            double p = d * d + o * o + e * e;
            if (p < 2.500000277905201E-7) {
                this.entity.setForwardSpeed(0.0f);
                return;
            }
            float n = (float)(MathHelper.atan2(e, d) * 57.2957763671875) - 90.0f;
            this.entity.yaw = this.wrapDegrees(this.entity.yaw, n, 90.0f);
            this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
            BlockPos blockPos = this.entity.getBlockPos();
            BlockState blockState = this.entity.world.getBlockState(blockPos);
            VoxelShape voxelShape = blockState.getCollisionShape(this.entity.world, blockPos);
            if (o > (double)this.entity.stepHeight && d * d + e * e < (double)Math.max(1.0f, this.entity.getWidth()) || !voxelShape.isEmpty() && this.entity.getY() < voxelShape.getMax(Direction.Axis.Y) + (double)blockPos.getY() && !blockState.isIn(BlockTags.DOORS) && !blockState.isIn(BlockTags.FENCES)) {
                this.entity.getJumpControl().setActive();
                this.state = State.JUMPING;
            }
        } else if (this.state == State.JUMPING) {
            this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
            if (this.entity.isOnGround()) {
                this.state = State.WAIT;
            }
        } else {
            this.entity.setForwardSpeed(0.0f);
        }
    }

    private boolean method_25946(float f, float g) {
        PathNodeMaker pathNodeMaker;
        EntityNavigation entityNavigation = this.entity.getNavigation();
        return entityNavigation == null || (pathNodeMaker = entityNavigation.getNodeMaker()) == null || pathNodeMaker.getDefaultNodeType(this.entity.world, MathHelper.floor(this.entity.getX() + (double)f), this.entity.getBlockY(), MathHelper.floor(this.entity.getZ() + (double)g)) == PathNodeType.WALKABLE;
    }

    protected float wrapDegrees(float from, float to, float max) {
        float g;
        float f = MathHelper.wrapDegrees(to - from);
        if (f > max) {
            f = max;
        }
        if (f < -max) {
            f = -max;
        }
        if ((g = from + f) < 0.0f) {
            g += 360.0f;
        } else if (g > 360.0f) {
            g -= 360.0f;
        }
        return g;
    }

    public double getTargetX() {
        return this.targetX;
    }

    public double getTargetY() {
        return this.targetY;
    }

    public double getTargetZ() {
        return this.targetZ;
    }

    public static enum State {
        WAIT,
        MOVE_TO,
        STRAFE,
        JUMPING;

    }
}

