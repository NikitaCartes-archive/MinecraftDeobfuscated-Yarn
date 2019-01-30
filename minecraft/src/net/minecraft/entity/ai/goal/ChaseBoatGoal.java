package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.class_1340;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class ChaseBoatGoal extends Goal {
	private int field_6428;
	private final MobEntityWithAi owner;
	private LivingEntity passenger;
	private class_1340 state;

	public ChaseBoatGoal(MobEntityWithAi mobEntityWithAi) {
		this.owner = mobEntityWithAi;
	}

	@Override
	public boolean canStart() {
		List<BoatEntity> list = this.owner.world.getVisibleEntities(BoatEntity.class, this.owner.getBoundingBox().expand(5.0));
		boolean bl = false;

		for (BoatEntity boatEntity : list) {
			if (boatEntity.getPrimaryPassenger() != null
				&& (
					MathHelper.abs(((LivingEntity)boatEntity.getPrimaryPassenger()).field_6212) > 0.0F
						|| MathHelper.abs(((LivingEntity)boatEntity.getPrimaryPassenger()).field_6250) > 0.0F
				)) {
				bl = true;
				break;
			}
		}

		return this.passenger != null && (MathHelper.abs(this.passenger.field_6212) > 0.0F || MathHelper.abs(this.passenger.field_6250) > 0.0F) || bl;
	}

	@Override
	public boolean canStop() {
		return true;
	}

	@Override
	public boolean shouldContinue() {
		return this.passenger != null
			&& this.passenger.hasVehicle()
			&& (MathHelper.abs(this.passenger.field_6212) > 0.0F || MathHelper.abs(this.passenger.field_6250) > 0.0F);
	}

	@Override
	public void start() {
		for (BoatEntity boatEntity : this.owner.world.getVisibleEntities(BoatEntity.class, this.owner.getBoundingBox().expand(5.0))) {
			if (boatEntity.getPrimaryPassenger() != null && boatEntity.getPrimaryPassenger() instanceof LivingEntity) {
				this.passenger = (LivingEntity)boatEntity.getPrimaryPassenger();
				break;
			}
		}

		this.field_6428 = 0;
		this.state = class_1340.field_6401;
	}

	@Override
	public void onRemove() {
		this.passenger = null;
	}

	@Override
	public void tick() {
		boolean bl = MathHelper.abs(this.passenger.field_6212) > 0.0F || MathHelper.abs(this.passenger.field_6250) > 0.0F;
		float f = this.state == class_1340.field_6400 ? (bl ? 0.17999999F : 0.0F) : 0.135F;
		this.owner.method_5724(this.owner.field_6212, this.owner.field_6227, this.owner.field_6250, f);
		this.owner.move(MovementType.field_6308, this.owner.velocityX, this.owner.velocityY, this.owner.velocityZ);
		if (--this.field_6428 <= 0) {
			this.field_6428 = 10;
			if (this.state == class_1340.field_6401) {
				BlockPos blockPos = new BlockPos(this.passenger).offset(this.passenger.getHorizontalFacing().getOpposite());
				blockPos = blockPos.add(0, -1, 0);
				this.owner.getNavigation().startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0);
				if (this.owner.distanceTo(this.passenger) < 4.0F) {
					this.field_6428 = 0;
					this.state = class_1340.field_6400;
				}
			} else if (this.state == class_1340.field_6400) {
				Direction direction = this.passenger.method_5755();
				BlockPos blockPos2 = new BlockPos(this.passenger).offset(direction, 10);
				this.owner.getNavigation().startMovingTo((double)blockPos2.getX(), (double)(blockPos2.getY() - 1), (double)blockPos2.getZ(), 1.0);
				if (this.owner.distanceTo(this.passenger) > 12.0F) {
					this.field_6428 = 0;
					this.state = class_1340.field_6401;
				}
			}
		}
	}
}
