package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.Goal;
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

	public float method_6149(BlockPos blockPos) {
		return this.method_6144(blockPos, this.field_6002);
	}

	public float method_6144(BlockPos blockPos, ViewableWorld viewableWorld) {
		return 0.0F;
	}

	@Override
	public boolean method_5979(IWorld iWorld, SpawnType spawnType) {
		return super.method_5979(iWorld, spawnType) && this.method_6144(new BlockPos(this.x, this.method_5829().minY, this.z), iWorld) >= 0.0F;
	}

	public boolean isNavigating() {
		return !this.method_5942().isIdle();
	}

	@Override
	protected void method_5995() {
		super.method_5995();
		Entity entity = this.getHoldingEntity();
		if (entity != null && entity.field_6002 == this.field_6002) {
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
				this.field_6201.addBits(Goal.class_4134.field_18405);
			} else if (f > 6.0F) {
				double d = (entity.x - this.x) / (double)f;
				double e = (entity.y - this.y) / (double)f;
				double g = (entity.z - this.z) / (double)f;
				this.method_18799(this.method_18798().add(Math.copySign(d * d * 0.4, d), Math.copySign(e * e * 0.4, e), Math.copySign(g * g * 0.4, g)));
			} else {
				this.field_6201.removeBits(Goal.class_4134.field_18405);
				float h = 2.0F;
				Vec3d vec3d = new Vec3d(entity.x - this.x, entity.y - this.y, entity.z - this.z).normalize().multiply((double)Math.max(f - 2.0F, 0.0F));
				this.method_5942().startMovingTo(this.x + vec3d.x, this.y + vec3d.y, this.z + vec3d.z, this.method_6148());
			}
		}
	}

	protected double method_6148() {
		return 1.0;
	}

	protected void method_6142(float f) {
	}
}
