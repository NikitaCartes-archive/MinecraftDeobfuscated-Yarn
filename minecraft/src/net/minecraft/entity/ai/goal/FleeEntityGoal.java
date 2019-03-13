package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.class_1414;
import net.minecraft.class_4051;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Vec3d;

public class FleeEntityGoal<T extends LivingEntity> extends Goal {
	protected final MobEntityWithAi field_6391;
	private final double field_6385;
	private final double field_6395;
	protected T field_6390;
	protected final float field_6386;
	protected Path field_6387;
	protected final EntityNavigation field_6394;
	protected final Class<T> field_6392;
	protected final Predicate<LivingEntity> field_6393;
	protected final Predicate<LivingEntity> field_6388;
	private final class_4051 field_18084;

	public FleeEntityGoal(MobEntityWithAi mobEntityWithAi, Class<T> class_, float f, double d, double e) {
		this(mobEntityWithAi, class_, livingEntity -> true, f, d, e, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
	}

	public FleeEntityGoal(
		MobEntityWithAi mobEntityWithAi, Class<T> class_, Predicate<LivingEntity> predicate, float f, double d, double e, Predicate<LivingEntity> predicate2
	) {
		this.field_6391 = mobEntityWithAi;
		this.field_6392 = class_;
		this.field_6393 = predicate;
		this.field_6386 = f;
		this.field_6385 = d;
		this.field_6395 = e;
		this.field_6388 = predicate2;
		this.field_6394 = mobEntityWithAi.method_5942();
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18405));
		this.field_18084 = new class_4051().method_18418((double)f).method_18420(predicate2.and(predicate));
	}

	public FleeEntityGoal(MobEntityWithAi mobEntityWithAi, Class<T> class_, float f, double d, double e, Predicate<LivingEntity> predicate) {
		this(mobEntityWithAi, class_, livingEntity -> true, f, d, e, predicate);
	}

	@Override
	public boolean canStart() {
		this.field_6390 = this.field_6391
			.field_6002
			.method_18465(
				this.field_6392,
				this.field_18084,
				this.field_6391,
				this.field_6391.x,
				this.field_6391.y,
				this.field_6391.z,
				this.field_6391.method_5829().expand((double)this.field_6386, 3.0, (double)this.field_6386)
			);
		if (this.field_6390 == null) {
			return false;
		} else {
			Vec3d vec3d = class_1414.method_6379(this.field_6391, 16, 7, new Vec3d(this.field_6390.x, this.field_6390.y, this.field_6390.z));
			if (vec3d == null) {
				return false;
			} else if (this.field_6390.squaredDistanceTo(vec3d.x, vec3d.y, vec3d.z) < this.field_6390.squaredDistanceTo(this.field_6391)) {
				return false;
			} else {
				this.field_6387 = this.field_6394.method_6352(vec3d.x, vec3d.y, vec3d.z);
				return this.field_6387 != null;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.field_6394.isIdle();
	}

	@Override
	public void start() {
		this.field_6394.method_6334(this.field_6387, this.field_6385);
	}

	@Override
	public void onRemove() {
		this.field_6390 = null;
	}

	@Override
	public void tick() {
		if (this.field_6391.squaredDistanceTo(this.field_6390) < 49.0) {
			this.field_6391.method_5942().setSpeed(this.field_6395);
		} else {
			this.field_6391.method_5942().setSpeed(this.field_6385);
		}
	}
}
