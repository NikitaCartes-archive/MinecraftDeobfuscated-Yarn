package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class ProjectileAttackGoal extends Goal {
	private final MobEntity mobEntity;
	private final RangedAttacker field_6582;
	private LivingEntity target;
	private int field_6581 = -1;
	private final double field_6586;
	private int field_6579;
	private final int field_6578;
	private final int field_6577;
	private final float field_6585;
	private final float field_6584;

	public ProjectileAttackGoal(RangedAttacker rangedAttacker, double d, int i, float f) {
		this(rangedAttacker, d, i, i, f);
	}

	public ProjectileAttackGoal(RangedAttacker rangedAttacker, double d, int i, int j, float f) {
		if (!(rangedAttacker instanceof LivingEntity)) {
			throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
		} else {
			this.field_6582 = rangedAttacker;
			this.mobEntity = (MobEntity)rangedAttacker;
			this.field_6586 = d;
			this.field_6578 = i;
			this.field_6577 = j;
			this.field_6585 = f;
			this.field_6584 = f * f;
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405, Goal.class_4134.field_18406));
		}
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.mobEntity.getTarget();
		if (livingEntity == null) {
			return false;
		} else {
			this.target = livingEntity;
			return true;
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.canStart() || !this.mobEntity.method_5942().isIdle();
	}

	@Override
	public void onRemove() {
		this.target = null;
		this.field_6579 = 0;
		this.field_6581 = -1;
	}

	@Override
	public void tick() {
		double d = this.mobEntity.squaredDistanceTo(this.target.x, this.target.method_5829().minY, this.target.z);
		boolean bl = this.mobEntity.method_5985().canSee(this.target);
		if (bl) {
			this.field_6579++;
		} else {
			this.field_6579 = 0;
		}

		if (!(d > (double)this.field_6584) && this.field_6579 >= 5) {
			this.mobEntity.method_5942().stop();
		} else {
			this.mobEntity.method_5942().startMovingTo(this.target, this.field_6586);
		}

		this.mobEntity.method_5988().lookAt(this.target, 30.0F, 30.0F);
		if (--this.field_6581 == 0) {
			if (!bl) {
				return;
			}

			float f = MathHelper.sqrt(d) / this.field_6585;
			float g = MathHelper.clamp(f, 0.1F, 1.0F);
			this.field_6582.attack(this.target, g);
			this.field_6581 = MathHelper.floor(f * (float)(this.field_6577 - this.field_6578) + (float)this.field_6578);
		} else if (this.field_6581 < 0) {
			float f = MathHelper.sqrt(d) / this.field_6585;
			this.field_6581 = MathHelper.floor(f * (float)(this.field_6577 - this.field_6578) + (float)this.field_6578);
		}
	}
}
