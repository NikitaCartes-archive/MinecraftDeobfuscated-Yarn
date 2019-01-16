package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_1414;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Vec3d;

public class FleeEntityGoal<T extends Entity> extends Goal {
	private final Predicate<Entity> field_6389 = new Predicate<Entity>() {
		public boolean method_6248(@Nullable Entity entity) {
			return entity.isValid() && FleeEntityGoal.this.field_6391.getVisibilityCache().canSee(entity) && !FleeEntityGoal.this.field_6391.isTeammate(entity);
		}
	};
	protected final MobEntityWithAi field_6391;
	private final double field_6385;
	private final double field_6395;
	protected T field_6390;
	private final float field_6386;
	private Path field_6387;
	private final EntityNavigation field_6394;
	private final Class<T> field_6392;
	private final Predicate<? super Entity> field_6393;
	private final Predicate<? super Entity> field_6388;

	public FleeEntityGoal(MobEntityWithAi mobEntityWithAi, Class<T> class_, float f, double d, double e) {
		this(mobEntityWithAi, class_, entity -> true, f, d, e, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR);
	}

	public FleeEntityGoal(
		MobEntityWithAi mobEntityWithAi, Class<T> class_, Predicate<? super Entity> predicate, float f, double d, double e, Predicate<Entity> predicate2
	) {
		this.field_6391 = mobEntityWithAi;
		this.field_6392 = class_;
		this.field_6393 = predicate;
		this.field_6386 = f;
		this.field_6385 = d;
		this.field_6395 = e;
		this.field_6388 = predicate2;
		this.field_6394 = mobEntityWithAi.getNavigation();
		this.setControlBits(1);
	}

	public FleeEntityGoal(MobEntityWithAi mobEntityWithAi, Class<T> class_, float f, double d, double e, Predicate<Entity> predicate) {
		this(mobEntityWithAi, class_, entity -> true, f, d, e, predicate);
	}

	@Override
	public boolean canStart() {
		List<T> list = this.field_6391
			.world
			.getEntities(
				this.field_6392,
				this.field_6391.getBoundingBox().expand((double)this.field_6386, 3.0, (double)this.field_6386),
				entity -> this.field_6388.test(entity) && this.field_6389.test(entity) && this.field_6393.test(entity)
			);
		if (list.isEmpty()) {
			return false;
		} else {
			this.field_6390 = (T)list.get(0);
			Vec3d vec3d = class_1414.method_6379(this.field_6391, 16, 7, new Vec3d(this.field_6390.x, this.field_6390.y, this.field_6390.z));
			if (vec3d == null) {
				return false;
			} else if (this.field_6390.squaredDistanceTo(vec3d.x, vec3d.y, vec3d.z) < this.field_6390.squaredDistanceTo(this.field_6391)) {
				return false;
			} else {
				this.field_6387 = this.field_6394.findPathTo(vec3d.x, vec3d.y, vec3d.z);
				return this.field_6387 != null;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.field_6394.method_6357();
	}

	@Override
	public void start() {
		this.field_6394.startMovingAlong(this.field_6387, this.field_6385);
	}

	@Override
	public void onRemove() {
		this.field_6390 = null;
	}

	@Override
	public void tick() {
		if (this.field_6391.squaredDistanceTo(this.field_6390) < 49.0) {
			this.field_6391.getNavigation().method_6344(this.field_6395);
		} else {
			this.field_6391.getNavigation().method_6344(this.field_6385);
		}
	}
}
