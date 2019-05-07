/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.control;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;

public class MoveControl {
    protected final MobEntity entity;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected double speed;
    protected float field_6368;
    protected float field_6373;
    protected State state = State.WAIT;

    public MoveControl(MobEntity mobEntity) {
        this.entity = mobEntity;
    }

    public boolean isMoving() {
        return this.state == State.MOVE_TO;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void moveTo(double d, double e, double f, double g) {
        this.targetX = d;
        this.targetY = e;
        this.targetZ = f;
        this.speed = g;
        if (this.state != State.JUMPING) {
            this.state = State.MOVE_TO;
        }
    }

    public void method_6243(float f, float g) {
        this.state = State.STRAFE;
        this.field_6368 = f;
        this.field_6373 = g;
        this.speed = 0.25;
    }

    public void tick() {
        if (this.state == State.STRAFE) {
            PathNodeMaker pathNodeMaker;
            float f = (float)this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
            float g = (float)this.speed * f;
            float h = this.field_6368;
            float i = this.field_6373;
            float j = MathHelper.sqrt(h * h + i * i);
            if (j < 1.0f) {
                j = 1.0f;
            }
            j = g / j;
            float k = MathHelper.sin(this.entity.yaw * ((float)Math.PI / 180));
            float l = MathHelper.cos(this.entity.yaw * ((float)Math.PI / 180));
            float m = (h *= j) * l - (i *= j) * k;
            float n = i * l + h * k;
            EntityNavigation entityNavigation = this.entity.getNavigation();
            if (entityNavigation != null && (pathNodeMaker = entityNavigation.getNodeMaker()) != null && pathNodeMaker.getPathNodeType(this.entity.world, MathHelper.floor(this.entity.x + (double)m), MathHelper.floor(this.entity.y), MathHelper.floor(this.entity.z + (double)n)) != PathNodeType.WALKABLE) {
                this.field_6368 = 1.0f;
                this.field_6373 = 0.0f;
                g = f;
            }
            this.entity.setMovementSpeed(g);
            this.entity.setForwardSpeed(this.field_6368);
            this.entity.setSidewaysSpeed(this.field_6373);
            this.state = State.WAIT;
        } else if (this.state == State.MOVE_TO) {
            this.state = State.WAIT;
            double d = this.targetX - this.entity.x;
            double o = this.targetY - this.entity.y;
            double e = this.targetZ - this.entity.z;
            double p = d * d + o * o + e * e;
            if (p < 2.500000277905201E-7) {
                this.entity.setForwardSpeed(0.0f);
                return;
            }
            float n = (float)(MathHelper.atan2(e, d) * 57.2957763671875) - 90.0f;
            this.entity.yaw = this.changeAngle(this.entity.yaw, n, 90.0f);
            this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue()));
            BlockPos blockPos = new BlockPos(this.entity);
            BlockState blockState = this.entity.world.getBlockState(blockPos);
            VoxelShape voxelShape = blockState.getCollisionShape(this.entity.world, blockPos);
            if (o > (double)this.entity.stepHeight && d * d + e * e < (double)Math.max(1.0f, this.entity.getWidth()) || !voxelShape.isEmpty() && this.entity.y < voxelShape.getMaximum(Direction.Axis.Y) + (double)blockPos.getY()) {
                this.entity.getJumpControl().setActive();
                this.state = State.JUMPING;
            }
        } else if (this.state == State.JUMPING) {
            this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue()));
            if (this.entity.onGround) {
                this.state = State.WAIT;
            }
        } else {
            this.entity.setForwardSpeed(0.0f);
        }
    }

    protected float changeAngle(float f, float g, float h) {
        float j;
        float i = MathHelper.wrapDegrees(g - f);
        if (i > h) {
            i = h;
        }
        if (i < -h) {
            i = -h;
        }
        if ((j = f + i) < 0.0f) {
            j += 360.0f;
        } else if (j > 360.0f) {
            j -= 360.0f;
        }
        return j;
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

