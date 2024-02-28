package net.minecraft.entity.ai.control;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;

public class MoveControl implements Control {
	public static final float field_30197 = 5.0E-4F;
	public static final float REACHED_DESTINATION_DISTANCE_SQUARED = 2.5000003E-7F;
	protected static final int field_30199 = 90;
	protected final MobEntity entity;
	protected double targetX;
	protected double targetY;
	protected double targetZ;
	protected double speed;
	protected float forwardMovement;
	protected float sidewaysMovement;
	protected MoveControl.State state = MoveControl.State.WAIT;

	public MoveControl(MobEntity entity) {
		this.entity = entity;
	}

	public boolean isMoving() {
		return this.state == MoveControl.State.MOVE_TO;
	}

	public double getSpeed() {
		return this.speed;
	}

	public void moveTo(double x, double y, double z, double speed) {
		this.targetX = x;
		this.targetY = y;
		this.targetZ = z;
		this.speed = speed;
		if (this.state != MoveControl.State.JUMPING) {
			this.state = MoveControl.State.MOVE_TO;
		}
	}

	public void strafeTo(float forward, float sideways) {
		this.state = MoveControl.State.STRAFE;
		this.forwardMovement = forward;
		this.sidewaysMovement = sideways;
		this.speed = 0.25;
	}

	public void tick() {
		if (this.state == MoveControl.State.STRAFE) {
			float f = (float)this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
			float g = (float)this.speed * f;
			float h = this.forwardMovement;
			float i = this.sidewaysMovement;
			float j = MathHelper.sqrt(h * h + i * i);
			if (j < 1.0F) {
				j = 1.0F;
			}

			j = g / j;
			h *= j;
			i *= j;
			float k = MathHelper.sin(this.entity.getYaw() * (float) (Math.PI / 180.0));
			float l = MathHelper.cos(this.entity.getYaw() * (float) (Math.PI / 180.0));
			float m = h * l - i * k;
			float n = i * l + h * k;
			if (!this.isPosWalkable(m, n)) {
				this.forwardMovement = 1.0F;
				this.sidewaysMovement = 0.0F;
			}

			this.entity.setMovementSpeed(g);
			this.entity.setForwardSpeed(this.forwardMovement);
			this.entity.setSidewaysSpeed(this.sidewaysMovement);
			this.state = MoveControl.State.WAIT;
		} else if (this.state == MoveControl.State.MOVE_TO) {
			this.state = MoveControl.State.WAIT;
			double d = this.targetX - this.entity.getX();
			double e = this.targetZ - this.entity.getZ();
			double o = this.targetY - this.entity.getY();
			double p = d * d + o * o + e * e;
			if (p < 2.5000003E-7F) {
				this.entity.setForwardSpeed(0.0F);
				return;
			}

			float n = (float)(MathHelper.atan2(e, d) * 180.0F / (float)Math.PI) - 90.0F;
			this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), n, 90.0F));
			this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
			BlockPos blockPos = this.entity.getBlockPos();
			BlockState blockState = this.entity.getWorld().getBlockState(blockPos);
			VoxelShape voxelShape = blockState.getCollisionShape(this.entity.getWorld(), blockPos);
			if (o > (double)this.entity.getStepHeight() && d * d + e * e < (double)Math.max(1.0F, this.entity.getWidth())
				|| !voxelShape.isEmpty()
					&& this.entity.getY() < voxelShape.getMax(Direction.Axis.Y) + (double)blockPos.getY()
					&& !blockState.isIn(BlockTags.DOORS)
					&& !blockState.isIn(BlockTags.FENCES)) {
				this.entity.getJumpControl().setActive();
				this.state = MoveControl.State.JUMPING;
			}
		} else if (this.state == MoveControl.State.JUMPING) {
			this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
			if (this.entity.isOnGround()) {
				this.state = MoveControl.State.WAIT;
			}
		} else {
			this.entity.setForwardSpeed(0.0F);
		}
	}

	private boolean isPosWalkable(float x, float z) {
		EntityNavigation entityNavigation = this.entity.getNavigation();
		if (entityNavigation != null) {
			PathNodeMaker pathNodeMaker = entityNavigation.getNodeMaker();
			if (pathNodeMaker != null
				&& pathNodeMaker.getDefaultNodeType(
						this.entity, BlockPos.ofFloored(this.entity.getX() + (double)x, (double)this.entity.getBlockY(), this.entity.getZ() + (double)z)
					)
					!= PathNodeType.WALKABLE) {
				return false;
			}
		}

		return true;
	}

	protected float wrapDegrees(float from, float to, float max) {
		float f = MathHelper.wrapDegrees(to - from);
		if (f > max) {
			f = max;
		}

		if (f < -max) {
			f = -max;
		}

		float g = from + f;
		if (g < 0.0F) {
			g += 360.0F;
		} else if (g > 360.0F) {
			g -= 360.0F;
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

	protected static enum State {
		WAIT,
		MOVE_TO,
		STRAFE,
		JUMPING;
	}
}
