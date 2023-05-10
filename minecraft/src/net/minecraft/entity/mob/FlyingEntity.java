package net.minecraft.entity.mob;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class FlyingEntity extends MobEntity {
	protected FlyingEntity(EntityType<? extends FlyingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
	}

	@Override
	public void travel(Vec3d movementInput) {
		if (this.isLogicalSideForUpdatingMovement()) {
			if (this.isTouchingWater()) {
				this.updateVelocity(0.02F, movementInput);
				this.move(MovementType.SELF, this.getVelocity());
				this.setVelocity(this.getVelocity().multiply(0.8F));
			} else if (this.isInLava()) {
				this.updateVelocity(0.02F, movementInput);
				this.move(MovementType.SELF, this.getVelocity());
				this.setVelocity(this.getVelocity().multiply(0.5));
			} else {
				float f = 0.91F;
				if (this.isOnGround()) {
					f = this.getWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getSlipperiness() * 0.91F;
				}

				float g = 0.16277137F / (f * f * f);
				f = 0.91F;
				if (this.isOnGround()) {
					f = this.getWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getSlipperiness() * 0.91F;
				}

				this.updateVelocity(this.isOnGround() ? 0.1F * g : 0.02F, movementInput);
				this.move(MovementType.SELF, this.getVelocity());
				this.setVelocity(this.getVelocity().multiply((double)f));
			}
		}

		this.updateLimbs(false);
	}

	@Override
	public boolean isClimbing() {
		return false;
	}
}
