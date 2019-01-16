package net.minecraft.entity.mob;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class FlyingEntity extends MobEntity {
	protected FlyingEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public void handleFallDamage(float f, float g) {
	}

	@Override
	protected void method_5623(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
	}

	@Override
	public void method_6091(float f, float g, float h) {
		if (this.isInsideWater()) {
			this.method_5724(f, g, h, 0.02F);
			this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.8F;
			this.velocityY *= 0.8F;
			this.velocityZ *= 0.8F;
		} else if (this.isTouchingLava()) {
			this.method_5724(f, g, h, 0.02F);
			this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.5;
			this.velocityY *= 0.5;
			this.velocityZ *= 0.5;
		} else {
			float i = 0.91F;
			if (this.onGround) {
				i = this.world
						.getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.z)))
						.getBlock()
						.getFrictionCoefficient()
					* 0.91F;
			}

			float j = 0.16277137F / (i * i * i);
			this.method_5724(f, g, h, this.onGround ? 0.1F * j : 0.02F);
			i = 0.91F;
			if (this.onGround) {
				i = this.world
						.getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.z)))
						.getBlock()
						.getFrictionCoefficient()
					* 0.91F;
			}

			this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
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
