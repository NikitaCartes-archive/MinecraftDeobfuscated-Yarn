package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.math.Vec3d;

public class FormCaravanGoal extends Goal {
	public final LlamaEntity llama;
	private double speed;
	private static final int MAX_CARAVAN_LENGTH = 8;
	private int counter;

	public FormCaravanGoal(LlamaEntity llama, double speed) {
		this.llama = llama;
		this.speed = speed;
		this.setControls(EnumSet.of(Goal.Control.MOVE));
	}

	@Override
	public boolean canStart() {
		if (!this.llama.isLeashed() && !this.llama.isFollowing()) {
			List<Entity> list = this.llama.getWorld().getOtherEntities(this.llama, this.llama.getBoundingBox().expand(9.0, 4.0, 9.0), entityx -> {
				EntityType<?> entityType = entityx.getType();
				return entityType == EntityType.LLAMA || entityType == EntityType.TRADER_LLAMA;
			});
			LlamaEntity llamaEntity = null;
			double d = Double.MAX_VALUE;

			for (Entity entity : list) {
				LlamaEntity llamaEntity2 = (LlamaEntity)entity;
				if (llamaEntity2.isFollowing() && !llamaEntity2.hasFollower()) {
					double e = this.llama.squaredDistanceTo(llamaEntity2);
					if (!(e > d)) {
						d = e;
						llamaEntity = llamaEntity2;
					}
				}
			}

			if (llamaEntity == null) {
				for (Entity entityx : list) {
					LlamaEntity llamaEntity2 = (LlamaEntity)entityx;
					if (llamaEntity2.isLeashed() && !llamaEntity2.hasFollower()) {
						double e = this.llama.squaredDistanceTo(llamaEntity2);
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
				this.llama.follow(llamaEntity);
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean shouldContinue() {
		if (this.llama.isFollowing() && this.llama.getFollowing().isAlive() && this.canFollow(this.llama, 0)) {
			double d = this.llama.squaredDistanceTo(this.llama.getFollowing());
			if (d > 676.0) {
				if (this.speed <= 3.0) {
					this.speed *= 1.2;
					this.counter = toGoalTicks(40);
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
	public void stop() {
		this.llama.stopFollowing();
		this.speed = 2.1;
	}

	@Override
	public void tick() {
		if (this.llama.isFollowing()) {
			if (!(this.llama.getLeashHolder() instanceof LeashKnotEntity)) {
				LlamaEntity llamaEntity = this.llama.getFollowing();
				double d = (double)this.llama.distanceTo(llamaEntity);
				float f = 2.0F;
				Vec3d vec3d = new Vec3d(llamaEntity.getX() - this.llama.getX(), llamaEntity.getY() - this.llama.getY(), llamaEntity.getZ() - this.llama.getZ())
					.normalize()
					.multiply(Math.max(d - 2.0, 0.0));
				this.llama.getNavigation().startMovingTo(this.llama.getX() + vec3d.x, this.llama.getY() + vec3d.y, this.llama.getZ() + vec3d.z, this.speed);
			}
		}
	}

	private boolean canFollow(LlamaEntity llama, int length) {
		if (length > 8) {
			return false;
		} else if (llama.isFollowing()) {
			return llama.getFollowing().isLeashed() ? true : this.canFollow(llama.getFollowing(), ++length);
		} else {
			return false;
		}
	}
}
