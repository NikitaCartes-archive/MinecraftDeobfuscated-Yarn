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
	public void method_6091(Vec3d vec3d) {
		if (this.isInsideWater()) {
			this.method_5724(0.02F, vec3d);
			this.method_5784(MovementType.field_6308, this.method_18798());
			this.method_18799(this.method_18798().multiply(0.8F));
		} else if (this.isInLava()) {
			this.method_5724(0.02F, vec3d);
			this.method_5784(MovementType.field_6308, this.method_18798());
			this.method_18799(this.method_18798().multiply(0.5));
		} else {
			float f = 0.91F;
			if (this.onGround) {
				f = this.field_6002.method_8320(new BlockPos(this.x, this.method_5829().minY - 1.0, this.z)).getBlock().getSlipperiness() * 0.91F;
			}

			float g = 0.16277137F / (f * f * f);
			f = 0.91F;
			if (this.onGround) {
				f = this.field_6002.method_8320(new BlockPos(this.x, this.method_5829().minY - 1.0, this.z)).getBlock().getSlipperiness() * 0.91F;
			}

			this.method_5724(this.onGround ? 0.1F * g : 0.02F, vec3d);
			this.method_5784(MovementType.field_6308, this.method_18798());
			this.method_18799(this.method_18798().multiply((double)f));
		}

		this.lastLimbDistance = this.limbDistance;
		double d = this.x - this.prevX;
		double e = this.z - this.prevZ;
		float h = MathHelper.sqrt(d * d + e * e) * 4.0F;
		if (h > 1.0F) {
			h = 1.0F;
		}

		this.limbDistance = this.limbDistance + (h - this.limbDistance) * 0.4F;
		this.limbAngle = this.limbAngle + this.limbDistance;
	}

	@Override
	public boolean isClimbing() {
		return false;
	}
}
