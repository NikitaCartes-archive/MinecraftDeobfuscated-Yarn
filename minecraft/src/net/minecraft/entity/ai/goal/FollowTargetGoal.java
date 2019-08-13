package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;

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
		if (this.reciprocalChance > 0 && this.mob.getRand().nextInt(this.reciprocalChance) != 0) {
			return false;
		} else {
			this.findClosestTarget();
			return this.targetEntity != null;
		}
	}

	protected Box getSearchBox(double d) {
		return this.mob.getBoundingBox().expand(d, 4.0, d);
	}

	protected void findClosestTarget() {
		if (this.targetClass != PlayerEntity.class && this.targetClass != ServerPlayerEntity.class) {
			this.targetEntity = this.mob
				.world
				.method_21727(
					this.targetClass,
					this.targetPredicate,
					this.mob,
					this.mob.x,
					this.mob.y + (double)this.mob.getStandingEyeHeight(),
					this.mob.z,
					this.getSearchBox(this.getFollowRange())
				);
		} else {
			this.targetEntity = this.mob
				.world
				.getClosestPlayer(this.targetPredicate, this.mob, this.mob.x, this.mob.y + (double)this.mob.getStandingEyeHeight(), this.mob.z);
		}
	}

	@Override
	public void start() {
		this.mob.setTarget(this.targetEntity);
		super.start();
	}
}
