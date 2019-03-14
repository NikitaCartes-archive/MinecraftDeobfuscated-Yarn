package net.minecraft.entity.ai;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

public class TargetPredicate {
	public static final TargetPredicate DEFAULT = new TargetPredicate();
	private double baseMaxDistance = -1.0;
	private boolean includeInvulnerable = false;
	private boolean includeTeammates = false;
	private boolean includeHidden = false;
	private boolean ignoreEntityTargetRules = false;
	private boolean useDistanceScalingFactor = true;
	private Predicate<LivingEntity> predicate = null;

	public TargetPredicate setBaseMaxDistance(double d) {
		this.baseMaxDistance = d;
		return this;
	}

	public TargetPredicate includeInvulnerable() {
		this.includeInvulnerable = true;
		return this;
	}

	public TargetPredicate includeTeammates() {
		this.includeTeammates = true;
		return this;
	}

	public TargetPredicate includeHidden() {
		this.includeHidden = true;
		return this;
	}

	public TargetPredicate ignoreEntityTargetRules() {
		this.ignoreEntityTargetRules = true;
		return this;
	}

	public TargetPredicate ignoreDistanceScalingFactor() {
		this.useDistanceScalingFactor = false;
		return this;
	}

	public TargetPredicate setPredicate(@Nullable Predicate<LivingEntity> predicate) {
		this.predicate = predicate;
		return this;
	}

	public boolean test(@Nullable LivingEntity livingEntity, LivingEntity livingEntity2) {
		if (livingEntity == livingEntity2) {
			return false;
		} else if (livingEntity2.isSpectator()) {
			return false;
		} else if (!livingEntity2.isValid()) {
			return false;
		} else if (!this.includeInvulnerable && livingEntity2.isInvulnerable()) {
			return false;
		} else if (this.predicate != null && !this.predicate.test(livingEntity2)) {
			return false;
		} else {
			if (livingEntity != null) {
				if (!this.ignoreEntityTargetRules) {
					if (!livingEntity.canTarget(livingEntity2)) {
						return false;
					}

					if (!livingEntity.canTarget(livingEntity2.getType())) {
						return false;
					}
				}

				if (!this.includeTeammates && livingEntity.isTeammate(livingEntity2)) {
					return false;
				}

				if (this.baseMaxDistance > 0.0) {
					double d = this.useDistanceScalingFactor ? livingEntity2.getAttackDistanceScalingFactor(livingEntity) : 1.0;
					double e = this.baseMaxDistance * d;
					double f = livingEntity.squaredDistanceTo(livingEntity2.x, livingEntity2.y, livingEntity2.z);
					if (f > e * e) {
						return false;
					}
				}

				if (!this.includeHidden && livingEntity instanceof MobEntity && !((MobEntity)livingEntity).getVisibilityCache().canSee(livingEntity2)) {
					return false;
				}
			}

			return true;
		}
	}
}
