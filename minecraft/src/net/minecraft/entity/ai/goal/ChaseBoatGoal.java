package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ChaseBoatGoal extends Goal {
	private int updateCountdownTicks;
	private final PathAwareEntity mob;
	private PlayerEntity passenger;
	private ChaseBoatState state;

	public ChaseBoatGoal(PathAwareEntity mob) {
		this.mob = mob;
	}

	@Override
	public boolean canStart() {
		List<BoatEntity> list = this.mob.world.getNonSpectatingEntities(BoatEntity.class, this.mob.getBoundingBox().expand(5.0));
		boolean bl = false;

		for (BoatEntity boatEntity : list) {
			Entity entity = boatEntity.getPrimaryPassenger();
			if (entity instanceof PlayerEntity
				&& (MathHelper.abs(((PlayerEntity)entity).sidewaysSpeed) > 0.0F || MathHelper.abs(((PlayerEntity)entity).forwardSpeed) > 0.0F)) {
				bl = true;
				break;
			}
		}

		return this.passenger != null && (MathHelper.abs(this.passenger.sidewaysSpeed) > 0.0F || MathHelper.abs(this.passenger.forwardSpeed) > 0.0F) || bl;
	}

	@Override
	public boolean canStop() {
		return true;
	}

	@Override
	public boolean shouldContinue() {
		return this.passenger != null
			&& this.passenger.hasVehicle()
			&& (MathHelper.abs(this.passenger.sidewaysSpeed) > 0.0F || MathHelper.abs(this.passenger.forwardSpeed) > 0.0F);
	}

	@Override
	public void start() {
		for (BoatEntity boatEntity : this.mob.world.getNonSpectatingEntities(BoatEntity.class, this.mob.getBoundingBox().expand(5.0))) {
			if (boatEntity.getPrimaryPassenger() != null && boatEntity.getPrimaryPassenger() instanceof PlayerEntity) {
				this.passenger = (PlayerEntity)boatEntity.getPrimaryPassenger();
				break;
			}
		}

		this.updateCountdownTicks = 0;
		this.state = ChaseBoatState.GO_TO_BOAT;
	}

	@Override
	public void stop() {
		this.passenger = null;
	}

	@Override
	public void tick() {
		boolean bl = MathHelper.abs(this.passenger.sidewaysSpeed) > 0.0F || MathHelper.abs(this.passenger.forwardSpeed) > 0.0F;
		float f = this.state == ChaseBoatState.GO_IN_BOAT_DIRECTION ? (bl ? 0.01F : 0.0F) : 0.015F;
		this.mob.updateVelocity(f, new Vec3d((double)this.mob.sidewaysSpeed, (double)this.mob.upwardSpeed, (double)this.mob.forwardSpeed));
		this.mob.move(MovementType.SELF, this.mob.getVelocity());
		if (--this.updateCountdownTicks <= 0) {
			this.updateCountdownTicks = 10;
			if (this.state == ChaseBoatState.GO_TO_BOAT) {
				BlockPos blockPos = this.passenger.getBlockPos().offset(this.passenger.getHorizontalFacing().getOpposite());
				blockPos = blockPos.add(0, -1, 0);
				this.mob.getNavigation().startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0);
				if (this.mob.distanceTo(this.passenger) < 4.0F) {
					this.updateCountdownTicks = 0;
					this.state = ChaseBoatState.GO_IN_BOAT_DIRECTION;
				}
			} else if (this.state == ChaseBoatState.GO_IN_BOAT_DIRECTION) {
				Direction direction = this.passenger.getMovementDirection();
				BlockPos blockPos2 = this.passenger.getBlockPos().offset(direction, 10);
				this.mob.getNavigation().startMovingTo((double)blockPos2.getX(), (double)(blockPos2.getY() - 1), (double)blockPos2.getZ(), 1.0);
				if (this.mob.distanceTo(this.passenger) > 12.0F) {
					this.updateCountdownTicks = 0;
					this.state = ChaseBoatState.GO_TO_BOAT;
				}
			}
		}
	}
}
