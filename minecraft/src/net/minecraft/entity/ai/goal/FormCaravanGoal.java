package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.math.Vec3d;

public class FormCaravanGoal extends Goal {
	public final LlamaEntity owner;
	private double speed;
	private int counter;

	public FormCaravanGoal(LlamaEntity llamaEntity, double d) {
		this.owner = llamaEntity;
		this.speed = d;
		this.setControlBits(EnumSet.of(Goal.ControlBit.field_18405));
	}

	@Override
	public boolean canStart() {
		if (!this.owner.isLeashed() && !this.owner.isFollowing()) {
			List<Entity> list = this.owner.world.getEntities(this.owner, this.owner.getBoundingBox().expand(9.0, 4.0, 9.0), entityx -> {
				EntityType<?> entityType = entityx.getType();
				return entityType == EntityType.LLAMA || entityType == EntityType.field_17714;
			});
			LlamaEntity llamaEntity = null;
			double d = Double.MAX_VALUE;

			for (Entity entity : list) {
				LlamaEntity llamaEntity2 = (LlamaEntity)entity;
				if (llamaEntity2.isFollowing() && !llamaEntity2.method_6793()) {
					double e = this.owner.squaredDistanceTo(llamaEntity2);
					if (!(e > d)) {
						d = e;
						llamaEntity = llamaEntity2;
					}
				}
			}

			if (llamaEntity == null) {
				for (Entity entityx : list) {
					LlamaEntity llamaEntity2 = (LlamaEntity)entityx;
					if (llamaEntity2.isLeashed() && !llamaEntity2.method_6793()) {
						double e = this.owner.squaredDistanceTo(llamaEntity2);
						if (!(e > d)) {
							d = e;
							llamaEntity = llamaEntity2;
						}
					}
				}
			}

			if (llamaEntity == null) {
				return false;
			} else if (d < 4.0) {
				return false;
			} else if (!llamaEntity.isLeashed() && !this.canFollow(llamaEntity, 1)) {
				return false;
			} else {
				this.owner.method_6791(llamaEntity);
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean shouldContinue() {
		if (this.owner.isFollowing() && this.owner.getFollowing().isValid() && this.canFollow(this.owner, 0)) {
			double d = this.owner.squaredDistanceTo(this.owner.getFollowing());
			if (d > 676.0) {
				if (this.speed <= 3.0) {
					this.speed *= 1.2;
					this.counter = 40;
					return true;
				}

				if (this.counter == 0) {
					return false;
				}
			}

			if (this.counter > 0) {
				this.counter--;
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onRemove() {
		this.owner.method_6797();
		this.speed = 2.1;
	}

	@Override
	public void tick() {
		if (this.owner.isFollowing()) {
			LlamaEntity llamaEntity = this.owner.getFollowing();
			double d = (double)this.owner.distanceTo(llamaEntity);
			float f = 2.0F;
			Vec3d vec3d = new Vec3d(llamaEntity.x - this.owner.x, llamaEntity.y - this.owner.y, llamaEntity.z - this.owner.z)
				.normalize()
				.multiply(Math.max(d - 2.0, 0.0));
			this.owner.getNavigation().startMovingTo(this.owner.x + vec3d.x, this.owner.y + vec3d.y, this.owner.z + vec3d.z, this.speed);
		}
	}

	private boolean canFollow(LlamaEntity llamaEntity, int i) {
		if (i > 8) {
			return false;
		} else if (llamaEntity.isFollowing()) {
			return llamaEntity.getFollowing().isLeashed() ? true : this.canFollow(llamaEntity.getFollowing(), ++i);
		} else {
			return false;
		}
	}
}
