package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
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
	protected final TargetPredicate targetPredicate;

	public LookAtEntityGoal(MobEntity mobEntity, Class<? extends LivingEntity> class_, float f) {
		this(mobEntity, class_, f, 0.02F);
	}

	public LookAtEntityGoal(MobEntity mobEntity, Class<? extends LivingEntity> class_, float f, float g) {
		this.owner = mobEntity;
		this.targetType = class_;
		this.range = f;
		this.chance = g;
		this.setControls(EnumSet.of(Goal.Control.field_18406));
		if (class_ == PlayerEntity.class) {
			this.targetPredicate = new TargetPredicate()
				.setBaseMaxDistance((double)f)
				.includeTeammates()
				.includeInvulnerable()
				.ignoreEntityTargetRules()
				.setPredicate(livingEntity -> EntityPredicates.rides(mobEntity).test(livingEntity));
		} else {
			this.targetPredicate = new TargetPredicate().setBaseMaxDistance((double)f).includeTeammates().includeInvulnerable().ignoreEntityTargetRules();
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
					.world
					.getClosestPlayer(this.targetPredicate, this.owner, this.owner.x, this.owner.y + (double)this.owner.getStandingEyeHeight(), this.owner.z);
			} else {
				this.target = this.owner
					.world
					.getClosestEntity(
						this.targetType,
						this.targetPredicate,
						this.owner,
						this.owner.x,
						this.owner.y + (double)this.owner.getStandingEyeHeight(),
						this.owner.z,
						this.owner.getBoundingBox().expand((double)this.range, 3.0, (double)this.range)
					);
			}

			return this.target != null;
		}
	}

	@Override
	public boolean shouldContinue() {
		if (!this.target.isAlive()) {
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
	public void stop() {
		this.target = null;
	}

	@Override
	public void tick() {
		this.owner.getLookControl().method_20248(this.target.x, this.target.y + (double)this.target.getStandingEyeHeight(), this.target.z);
		this.lookTime--;
	}
}
