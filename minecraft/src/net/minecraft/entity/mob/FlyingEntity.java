package net.minecraft.entity.mob;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
	public void travel(float f, float g, float h) {
		if (this.isInsideWater()) {
			this.updateVelocity(f, g, h, 0.02F);
			this.move(MovementType.field_6308, this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.8F;
			this.velocityY *= 0.8F;
			this.velocityZ *= 0.8F;
		} else if (this.isTouchingLava()) {
			this.updateVelocity(f, g, h, 0.02F);
			this.move(MovementType.field_6308, this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.5;
			this.velocityY *= 0.5;
			this.velocityZ *= 0.5;
		} else {
			float i = 0.91F;
			if (this.onGround) {
				i = this.world.getBlockState(new BlockPos(this.x, this.getBoundingBox().minY - 1.0, this.z)).getBlock().getFrictionCoefficient() * 0.91F;
			}

			float j = 0.16277137F / (i * i * i);
			this.updateVelocity(f, g, h, this.onGround ? 0.1F * j : 0.02F);
			i = 0.91F;
			if (this.onGround) {
				i = this.world.getBlockState(new BlockPos(this.x, this.getBoundingBox().minY - 1.0, this.z)).getBlock().getFrictionCoefficient() * 0.91F;
			}

			this.move(MovementType.field_6308, this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= (double)i;
			this.velocityY *= (double)i;
			this.velocityZ *= (double)i;
		}

		this.field_6211 = this.field_6225;
		double d = this.x - this.prevX;
		double e = this.z - this.prevZ;
		float k = MathHelper.sqrt(d * d + e * e) * 4.0F;
		if (k > 1.0F) {
			k = 1.0F;
		}

		this.field_6225 = this.field_6225 + (k - this.field_6225) * 0.4F;
		this.field_6249 = this.field_6249 + this.field_6225;
	}

	@Override
	public boolean canClimb() {
		return false;
	}
}
