package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class ProjectileAttackGoal extends Goal {
	private final MobEntity mob;
	private final RangedAttackMob owner;
	private LivingEntity target;
	private int field_6581 = -1;
	private final double mobSpeed;
	private int field_6579;
	private final int field_6578;
	private final int field_6577;
	private final float maxShootRange;
	private final float squaredMaxShootRange;

	public ProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int i, float maxShootRange) {
		this(mob, mobSpeed, i, i, maxShootRange);
	}

	public ProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int i, int j, float maxShootRange) {
		if (!(mob instanceof LivingEntity)) {
			throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
		} else {
			this.owner = mob;
			this.mob = (MobEntity)mob;
			this.mobSpeed = mobSpeed;
			this.field_6578 = i;
			this.field_6577 = j;
			this.maxShootRange = maxShootRange;
			this.squaredMaxShootRange = maxShootRange * maxShootRange;
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		}
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.mob.getTarget();
		if (livingEntity != null && livingEntity.isAlive()) {
			this.target = livingEntity;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.canStart() || !this.mob.getNavigation().isIdle();
	}

	@Override
	public void stop() {
		this.target = null;
		this.field_6579 = 0;
		this.field_6581 = -1;
	}

	@Override
	public void tick() {
		double d = this.mob.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());
		boolean bl = this.mob.getVisibilityCache().canSee(this.target);
		if (bl) {
			this.field_6579++;
		} else {
			this.field_6579 = 0;
		}

		if (!(d > (double)this.squaredMaxShootRange) && this.field_6579 >= 5) {
			this.mob.getNavigation().stop();
		} else {
			this.mob.getNavigation().startMovingTo(this.target, this.mobSpeed);
		}

		this.mob.getLookControl().lookAt(this.target, 30.0F, 30.0F);
		if (--this.field_6581 == 0) {
			if (!bl) {
				return;
			}

			float f = MathHelper.sqrt(d) / this.maxShootRange;
			float g = MathHelper.clamp(f, 0.1F, 1.0F);
			this.owner.attack(this.target, g);
			this.field_6581 = MathHelper.floor(f * (float)(this.field_6577 - this.field_6578) + (float)this.field_6578);
		} else if (this.field_6581 < 0) {
			float f = MathHelper.sqrt(d) / this.maxShootRange;
			this.field_6581 = MathHelper.floor(f * (float)(this.field_6577 - this.field_6578) + (float)this.field_6578);
		}
	}
}
