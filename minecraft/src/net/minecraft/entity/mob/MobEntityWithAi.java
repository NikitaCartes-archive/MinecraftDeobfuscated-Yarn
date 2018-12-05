package net.minecraft.entity.mob;

import net.minecraft.class_3730;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class MobEntityWithAi extends MobEntity {
	private BlockPos aiHome = BlockPos.ORIGIN;
	private float aiRadius = -1.0F;

	protected MobEntityWithAi(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	public float method_6149(BlockPos blockPos) {
		return this.method_6144(blockPos, this.world);
	}

	public float method_6144(BlockPos blockPos, ViewableWorld viewableWorld) {
		return 0.0F;
	}

	@Override
	public boolean method_5979(IWorld iWorld, class_3730 arg) {
		return super.method_5979(iWorld, arg) && this.method_6144(new BlockPos(this.x, this.getBoundingBox().minY, this.z), iWorld) >= 0.0F;
	}

	public boolean method_6150() {
		return !this.getNavigation().method_6357();
	}

	public boolean isInAiRange() {
		return this.isInAiRange(new BlockPos(this));
	}

	public boolean isInAiRange(BlockPos blockPos) {
		return this.aiRadius == -1.0F ? true : this.aiHome.squaredDistanceTo(blockPos) < (double)(this.aiRadius * this.aiRadius);
	}

	public void setAiHome(BlockPos blockPos, int i) {
		this.aiHome = blockPos;
		this.aiRadius = (float)i;
	}

	public BlockPos getAiHome() {
		return this.aiHome;
	}

	public float getAiRadius() {
		return this.aiRadius;
	}

	public void setAiRangeUnlimited() {
		this.aiRadius = -1.0F;
	}

	public boolean hasLimitedAiRange() {
		return this.aiRadius != -1.0F;
	}

	@Override
	protected void method_5995() {
		super.method_5995();
		if (this.isLeashed() && this.getHoldingEntity() != null && this.getHoldingEntity().world == this.world) {
			Entity entity = this.getHoldingEntity();
			this.setAiHome(new BlockPos((int)entity.x, (int)entity.y, (int)entity.z), 5);
			float f = this.distanceTo(entity);
			if (this instanceof TameableEntity && ((TameableEntity)this).isSitting()) {
				if (f > 10.0F) {
					this.detachLeash(true, true);
				}

				return;
			}

			this.method_6142(f);
			if (f > 10.0F) {
				this.detachLeash(true, true);
				this.goalSelector.addBits(1);
			} else if (f > 6.0F) {
				double d = (entity.x - this.x) / (double)f;
				double e = (entity.y - this.y) / (double)f;
				double g = (entity.z - this.z) / (double)f;
				this.velocityX = this.velocityX + d * Math.abs(d) * 0.4;
				this.velocityY = this.velocityY + e * Math.abs(e) * 0.4;
				this.velocityZ = this.velocityZ + g * Math.abs(g) * 0.4;
			} else {
				this.goalSelector.removeBits(1);
				float h = 2.0F;
				Vec3d vec3d = new Vec3d(entity.x - this.x, entity.y - this.y, entity.z - this.z).normalize().multiply((double)Math.max(f - 2.0F, 0.0F));
				this.getNavigation().method_6337(this.x + vec3d.x, this.y + vec3d.y, this.z + vec3d.z, this.method_6148());
			}
		}
	}

	protected double method_6148() {
		return 1.0;
	}

	protected void method_6142(float f) {
	}
}
