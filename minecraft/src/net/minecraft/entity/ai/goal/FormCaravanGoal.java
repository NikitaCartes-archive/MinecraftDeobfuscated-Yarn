package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.math.Vec3d;

public class FormCaravanGoal extends Goal {
	public final LlamaEntity field_6488;
	private double speed;
	private int counter;

	public FormCaravanGoal(LlamaEntity llamaEntity, double d) {
		this.field_6488 = llamaEntity;
		this.speed = d;
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18405));
	}

	@Override
	public boolean canStart() {
		if (!this.field_6488.isLeashed() && !this.field_6488.isFollowing()) {
			List<LlamaEntity> list = this.field_6488.field_6002.method_18467(this.field_6488.getClass(), this.field_6488.method_5829().expand(9.0, 4.0, 9.0));
			LlamaEntity llamaEntity = null;
			double d = Double.MAX_VALUE;

			for (LlamaEntity llamaEntity2 : list) {
				if (llamaEntity2.isFollowing() && !llamaEntity2.method_6793()) {
					double e = this.field_6488.squaredDistanceTo(llamaEntity2);
					if (!(e > d)) {
						d = e;
						llamaEntity = llamaEntity2;
					}
				}
			}

			if (llamaEntity == null) {
				for (LlamaEntity llamaEntity2x : list) {
					if (llamaEntity2x.isLeashed() && !llamaEntity2x.method_6793()) {
						double e = this.field_6488.squaredDistanceTo(llamaEntity2x);
						if (!(e > d)) {
							d = e;
							llamaEntity = llamaEntity2x;
						}
					}
				}
			}

			if (llamaEntity == null) {
				return false;
			} else if (d < 4.0) {
				return false;
			} else if (!llamaEntity.isLeashed() && !this.method_6285(llamaEntity, 1)) {
				return false;
			} else {
				this.field_6488.method_6791(llamaEntity);
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean shouldContinue() {
		if (this.field_6488.isFollowing() && this.field_6488.getFollowing().isValid() && this.method_6285(this.field_6488, 0)) {
			double d = this.field_6488.squaredDistanceTo(this.field_6488.getFollowing());
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
		this.field_6488.method_6797();
		this.speed = 2.1;
	}

	@Override
	public void tick() {
		if (this.field_6488.isFollowing()) {
			LlamaEntity llamaEntity = this.field_6488.getFollowing();
			double d = (double)this.field_6488.distanceTo(llamaEntity);
			float f = 2.0F;
			Vec3d vec3d = new Vec3d(llamaEntity.x - this.field_6488.x, llamaEntity.y - this.field_6488.y, llamaEntity.z - this.field_6488.z)
				.normalize()
				.multiply(Math.max(d - 2.0, 0.0));
			this.field_6488.method_5942().startMovingTo(this.field_6488.x + vec3d.x, this.field_6488.y + vec3d.y, this.field_6488.z + vec3d.z, this.speed);
		}
	}

	private boolean method_6285(LlamaEntity llamaEntity, int i) {
		if (i > 8) {
			return false;
		} else if (llamaEntity.isFollowing()) {
			return llamaEntity.getFollowing().isLeashed() ? true : this.method_6285(llamaEntity.getFollowing(), ++i);
		} else {
			return false;
		}
	}
}
