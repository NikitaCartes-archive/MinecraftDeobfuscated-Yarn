package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BoundingBox;

public class FollowTargetGoal<T extends LivingEntity> extends TrackTargetGoal {
	protected final Class<T> targetClass;
	protected final int reciprocalChance;
	protected LivingEntity targetEntity;
	protected TargetPredicate targetPredicate;

	public FollowTargetGoal(MobEntity mobEntity, Class<T> class_, boolean bl) {
		this(mobEntity, class_, bl, false);
	}

	public FollowTargetGoal(MobEntity mobEntity, Class<T> class_, boolean bl, boolean bl2) {
		this(mobEntity, class_, 10, bl, bl2, null);
	}

	public FollowTargetGoal(MobEntity mobEntity, Class<T> class_, int i, boolean bl, boolean bl2, @Nullable Predicate<LivingEntity> predicate) {
		super(mobEntity, bl, bl2);
		this.targetClass = class_;
		this.reciprocalChance = i;
		this.setControls(EnumSet.of(Goal.Control.field_18408));
		this.targetPredicate = new TargetPredicate().setBaseMaxDistance(this.getFollowRange()).setPredicate(predicate);
	}

	@Override
	public boolean canStart() {
		if (this.reciprocalChance > 0 && this.entity.getRand().nextInt(this.reciprocalChance) != 0) {
			return false;
		} else {
			this.findClosestTarget();
			return this.targetEntity != null;
		}
	}

	protected BoundingBox getSearchBox(double d) {
		return this.entity.getBoundingBox().expand(d, 4.0, d);
	}

	protected void findClosestTarget() {
		if (this.targetClass != PlayerEntity.class && this.targetClass != ServerPlayerEntity.class) {
			this.targetEntity = this.entity
				.world
				.getClosestEntity(
					this.targetClass,
					this.targetPredicate,
					this.entity,
					this.entity.x,
					this.entity.y + (double)this.entity.getStandingEyeHeight(),
					this.entity.z,
					this.getSearchBox(this.getFollowRange())
				);
		} else {
			this.targetEntity = this.entity
				.world
				.getClosestPlayer(this.targetPredicate, this.entity, this.entity.x, this.entity.y + (double)this.entity.getStandingEyeHeight(), this.entity.z);
		}
	}

	@Override
	public void start() {
		this.entity.setTarget(this.targetEntity);
		super.start();
	}
}
