package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class MobEntityWithAi extends MobEntity {
	protected MobEntityWithAi(EntityType<? extends MobEntityWithAi> entityType, World world) {
		super(entityType, world);
	}

	public float getPathfindingFavor(BlockPos blockPos) {
		return this.getPathfindingFavor(blockPos, this.world);
	}

	public float getPathfindingFavor(BlockPos blockPos, ViewableWorld viewableWorld) {
		return 0.0F;
	}

	@Override
	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		return super.canSpawn(iWorld, spawnType) && this.getPathfindingFavor(new BlockPos(this.x, this.getBoundingBox().minY, this.z), iWorld) >= 0.0F;
	}

	public boolean isNavigating() {
		return !this.getNavigation().isIdle();
	}

	@Override
	protected void method_5995() {
		super.method_5995();
		Entity entity = this.getHoldingEntity();
		if (entity != null && entity.world == this.world) {
			this.method_18408(new BlockPos((int)entity.x, (int)entity.y, (int)entity.z), 5);
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
				this.setVelocity(this.getVelocity().add(Math.copySign(d * d * 0.4, d), Math.copySign(e * e * 0.4, e), Math.copySign(g * g * 0.4, g)));
			} else {
				this.goalSelector.removeBits(1);
				float h = 2.0F;
				Vec3d vec3d = new Vec3d(entity.x - this.x, entity.y - this.y, entity.z - this.z).normalize().multiply((double)Math.max(f - 2.0F, 0.0F));
				this.getNavigation().startMovingTo(this.x + vec3d.x, this.y + vec3d.y, this.z + vec3d.z, this.method_6148());
			}
		}
	}

	protected double method_6148() {
		return 1.0;
	}

	protected void method_6142(float f) {
	}
}
