package net.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;

public class class_1361 extends Goal {
	protected final MobEntity field_6486;
	protected Entity field_6484;
	protected final float field_6482;
	private int field_6483;
	private final float field_6481;
	protected final Class<? extends Entity> field_6485;

	public class_1361(MobEntity mobEntity, Class<? extends Entity> class_, float f) {
		this(mobEntity, class_, f, 0.02F);
	}

	public class_1361(MobEntity mobEntity, Class<? extends Entity> class_, float f, float g) {
		this.field_6486 = mobEntity;
		this.field_6485 = class_;
		this.field_6482 = f;
		this.field_6481 = g;
		this.setControlBits(2);
	}

	@Override
	public boolean canStart() {
		if (this.field_6486.getRand().nextFloat() >= this.field_6481) {
			return false;
		} else {
			if (this.field_6486.getTarget() != null) {
				this.field_6484 = this.field_6486.getTarget();
			}

			if (this.field_6485 == PlayerEntity.class) {
				this.field_6484 = this.field_6486
					.world
					.getClosestPlayer(
						this.field_6486.x,
						this.field_6486.y,
						this.field_6486.z,
						(double)this.field_6482,
						EntityPredicates.EXCEPT_SPECTATOR.and(EntityPredicates.method_5913(this.field_6486))
					);
			} else {
				this.field_6484 = this.field_6486
					.world
					.getClosestVisibleEntityTo(
						this.field_6485, this.field_6486.getBoundingBox().expand((double)this.field_6482, 3.0, (double)this.field_6482), this.field_6486
					);
			}

			return this.field_6484 != null;
		}
	}

	@Override
	public boolean shouldContinue() {
		if (!this.field_6484.isValid()) {
			return false;
		} else {
			return this.field_6486.squaredDistanceTo(this.field_6484) > (double)(this.field_6482 * this.field_6482) ? false : this.field_6483 > 0;
		}
	}

	@Override
	public void start() {
		this.field_6483 = 40 + this.field_6486.getRand().nextInt(40);
	}

	@Override
	public void onRemove() {
		this.field_6484 = null;
	}

	@Override
	public void tick() {
		this.field_6486
			.getLookControl()
			.lookAt(
				this.field_6484.x,
				this.field_6484.y + (double)this.field_6484.getEyeHeight(),
				this.field_6484.z,
				(float)this.field_6486.method_5986(),
				(float)this.field_6486.method_5978()
			);
		this.field_6483--;
	}
}
