package net.minecraft;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

public class class_4051 {
	public static final class_4051 field_18092 = new class_4051();
	private double field_18093 = -1.0;
	private boolean field_18094 = false;
	private boolean field_18095 = false;
	private boolean field_18096 = false;
	private boolean field_18097 = false;
	private boolean field_18098 = true;
	private Predicate<LivingEntity> field_18099 = null;

	public class_4051 method_18418(double d) {
		this.field_18093 = d;
		return this;
	}

	public class_4051 method_18417() {
		this.field_18094 = true;
		return this;
	}

	public class_4051 method_18421() {
		this.field_18095 = true;
		return this;
	}

	public class_4051 method_18422() {
		this.field_18096 = true;
		return this;
	}

	public class_4051 method_18423() {
		this.field_18097 = true;
		return this;
	}

	public class_4051 method_18424() {
		this.field_18098 = false;
		return this;
	}

	public class_4051 method_18420(@Nullable Predicate<LivingEntity> predicate) {
		this.field_18099 = predicate;
		return this;
	}

	public boolean method_18419(@Nullable LivingEntity livingEntity, LivingEntity livingEntity2) {
		if (livingEntity == livingEntity2) {
			return false;
		} else if (livingEntity2.isSpectator()) {
			return false;
		} else if (!livingEntity2.isValid()) {
			return false;
		} else if (!this.field_18094 && livingEntity2.isInvulnerable()) {
			return false;
		} else if (this.field_18099 != null && !this.field_18099.test(livingEntity2)) {
			return false;
		} else {
			if (livingEntity != null) {
				if (!this.field_18097) {
					if (!livingEntity.method_18395(livingEntity2)) {
						return false;
					}

					if (!livingEntity.method_5973(livingEntity2.method_5864())) {
						return false;
					}
				}

				if (!this.field_18095 && livingEntity.isTeammate(livingEntity2)) {
					return false;
				}

				if (this.field_18093 > 0.0) {
					double d = this.field_18098 ? livingEntity2.method_18390(livingEntity) : 1.0;
					double e = this.field_18093 * d;
					double f = livingEntity.squaredDistanceTo(livingEntity2.x, livingEntity2.y, livingEntity2.z);
					if (f > e * e) {
						return false;
					}
				}

				if (!this.field_18096 && livingEntity instanceof MobEntity && !((MobEntity)livingEntity).method_5985().canSee(livingEntity2)) {
					return false;
				}
			}

			return true;
		}
	}
}
