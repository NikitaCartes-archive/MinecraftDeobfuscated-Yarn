package net.minecraft.entity.mob;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class FlyingEntity extends MobEntity {
	protected FlyingEntity(EntityType<? extends FlyingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public void handleFallDamage(float f, float g) {
	}

	@Override
	protected void method_5623(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
	}

	@Override
	public void travel(Vec3d vec3d) {
		if (this.isInsideWater()) {
			this.updateVelocity(0.02F, vec3d);
			this.move(MovementType.field_6308, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply(0.8F));
		} else if (this.isTouchingLava()) {
			this.updateVelocity(0.02F, vec3d);
			this.move(MovementType.field_6308, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply(0.5));
		} else {
			float f = 0.91F;
			if (this.onGround) {
				f = this.world.getBlockState(new BlockPos(this.x, this.getBoundingBox().minY - 1.0, this.z)).getBlock().getFrictionCoefficient() * 0.91F;
			}

			float g = 0.16277137F / (f * f * f);
			f = 0.91F;
			if (this.onGround) {
				f = this.world.getBlockState(new BlockPos(this.x, this.getBoundingBox().minY - 1.0, this.z)).getBlock().getFrictionCoefficient() * 0.91F;
			}

			this.updateVelocity(this.onGround ? 0.1F * g : 0.02F, vec3d);
			this.move(MovementType.field_6308, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply((double)f));
		}

		this.field_6211 = this.field_6225;
		double d = this.x - this.prevX;
		double e = this.z - this.prevZ;
		float h = MathHelper.sqrt(d * d + e * e) * 4.0F;
		if (h > 1.0F) {
			h = 1.0F;
		}

		this.field_6225 = this.field_6225 + (h - this.field_6225) * 0.4F;
		this.field_6249 = this.field_6249 + this.field_6225;
	}

	@Override
	public boolean canClimb() {
		return false;
	}
}
