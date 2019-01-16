package net.minecraft;

import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class class_1346 extends Goal {
	private int field_6428;
	private final MobEntityWithAi field_6426;
	private LivingEntity field_6427;
	private class_1340 field_6425;

	public class_1346(MobEntityWithAi mobEntityWithAi) {
		this.field_6426 = mobEntityWithAi;
	}

	@Override
	public boolean canStart() {
		List<BoatEntity> list = this.field_6426.world.getVisibleEntities(BoatEntity.class, this.field_6426.getBoundingBox().expand(5.0));
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

		return this.field_6427 != null && (MathHelper.abs(this.field_6427.field_6212) > 0.0F || MathHelper.abs(this.field_6427.field_6250) > 0.0F) || bl;
	}

	@Override
	public boolean canStop() {
		return true;
	}

	@Override
	public boolean shouldContinue() {
		return this.field_6427 != null
			&& this.field_6427.hasVehicle()
			&& (MathHelper.abs(this.field_6427.field_6212) > 0.0F || MathHelper.abs(this.field_6427.field_6250) > 0.0F);
	}

	@Override
	public void start() {
		for (BoatEntity boatEntity : this.field_6426.world.getVisibleEntities(BoatEntity.class, this.field_6426.getBoundingBox().expand(5.0))) {
			if (boatEntity.getPrimaryPassenger() != null && boatEntity.getPrimaryPassenger() instanceof LivingEntity) {
				this.field_6427 = (LivingEntity)boatEntity.getPrimaryPassenger();
				break;
			}
		}

		this.field_6428 = 0;
		this.field_6425 = class_1340.field_6401;
	}

	@Override
	public void onRemove() {
		this.field_6427 = null;
	}

	@Override
	public void tick() {
		boolean bl = MathHelper.abs(this.field_6427.field_6212) > 0.0F || MathHelper.abs(this.field_6427.field_6250) > 0.0F;
		float f = this.field_6425 == class_1340.field_6400 ? (bl ? 0.17999999F : 0.0F) : 0.135F;
		this.field_6426.method_5724(this.field_6426.field_6212, this.field_6426.field_6227, this.field_6426.field_6250, f);
		this.field_6426.move(MovementType.SELF, this.field_6426.velocityX, this.field_6426.velocityY, this.field_6426.velocityZ);
		if (--this.field_6428 <= 0) {
			this.field_6428 = 10;
			if (this.field_6425 == class_1340.field_6401) {
				BlockPos blockPos = new BlockPos(this.field_6427).offset(this.field_6427.getHorizontalFacing().getOpposite());
				blockPos = blockPos.add(0, -1, 0);
				this.field_6426.getNavigation().startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0);
				if (this.field_6426.distanceTo(this.field_6427) < 4.0F) {
					this.field_6428 = 0;
					this.field_6425 = class_1340.field_6400;
				}
			} else if (this.field_6425 == class_1340.field_6400) {
				Direction direction = this.field_6427.method_5755();
				BlockPos blockPos2 = new BlockPos(this.field_6427).offset(direction, 10);
				this.field_6426.getNavigation().startMovingTo((double)blockPos2.getX(), (double)(blockPos2.getY() - 1), (double)blockPos2.getZ(), 1.0);
				if (this.field_6426.distanceTo(this.field_6427) > 12.0F) {
					this.field_6428 = 0;
					this.field_6425 = class_1340.field_6401;
				}
			}
		}
	}
}
