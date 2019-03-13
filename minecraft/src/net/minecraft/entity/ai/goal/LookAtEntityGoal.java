package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.class_4051;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;

public class LookAtEntityGoal extends Goal {
	protected final MobEntity owner;
	protected Entity target;
	protected final float range;
	private int lookTime;
	private final float chance;
	protected final Class<? extends LivingEntity> targetType;
	protected final class_4051 field_18087;

	public LookAtEntityGoal(MobEntity mobEntity, Class<? extends LivingEntity> class_, float f) {
		this(mobEntity, class_, f, 0.02F);
	}

	public LookAtEntityGoal(MobEntity mobEntity, Class<? extends LivingEntity> class_, float f, float g) {
		this.owner = mobEntity;
		this.targetType = class_;
		this.range = f;
		this.chance = g;
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18406));
		if (class_ == PlayerEntity.class) {
			this.field_18087 = new class_4051()
				.method_18418((double)f)
				.method_18421()
				.method_18417()
				.method_18423()
				.method_18420(livingEntity -> EntityPredicates.method_5913(mobEntity).test(livingEntity));
		} else {
			this.field_18087 = new class_4051().method_18418((double)f).method_18421().method_18417().method_18423();
		}
	}

	@Override
	public boolean canStart() {
		if (this.owner.getRand().nextFloat() >= this.chance) {
			return false;
		} else {
			if (this.owner.getTarget() != null) {
				this.target = this.owner.getTarget();
			}

			if (this.targetType == PlayerEntity.class) {
				this.target = this.owner
					.field_6002
					.method_18463(this.field_18087, this.owner, this.owner.x, this.owner.y + (double)this.owner.getStandingEyeHeight(), this.owner.z);
			} else {
				this.target = this.owner
					.field_6002
					.method_18465(
						this.targetType,
						this.field_18087,
						this.owner,
						this.owner.x,
						this.owner.y + (double)this.owner.getStandingEyeHeight(),
						this.owner.z,
						this.owner.method_5829().expand((double)this.range, 3.0, (double)this.range)
					);
			}

			return this.target != null;
		}
	}

	@Override
	public boolean shouldContinue() {
		if (!this.target.isValid()) {
			return false;
		} else {
			return this.owner.squaredDistanceTo(this.target) > (double)(this.range * this.range) ? false : this.lookTime > 0;
		}
	}

	@Override
	public void start() {
		this.lookTime = 40 + this.owner.getRand().nextInt(40);
	}

	@Override
	public void onRemove() {
		this.target = null;
	}

	@Override
	public void tick() {
		this.owner
			.method_5988()
			.lookAt(
				this.target.x, this.target.y + (double)this.target.getStandingEyeHeight(), this.target.z, (float)this.owner.method_5986(), (float)this.owner.method_5978()
			);
		this.lookTime--;
	}
}
