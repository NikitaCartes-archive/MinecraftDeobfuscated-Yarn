package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;

public class FollowMobGoal extends Goal {
	private final MobEntity mob;
	private final Predicate<MobEntity> targetPredicate;
	private MobEntity target;
	private final double speed;
	private final EntityNavigation navigation;
	private int field_6431;
	private final float minDistance;
	private float field_6437;
	private final float maxDistance;

	public FollowMobGoal(MobEntity mobEntity, double d, float f, float g) {
		this.mob = mobEntity;
		this.targetPredicate = mobEntity2 -> mobEntity2 != null && mobEntity.getClass() != mobEntity2.getClass();
		this.speed = d;
		this.navigation = mobEntity.getNavigation();
		this.minDistance = f;
		this.maxDistance = g;
		this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
		if (!(mobEntity.getNavigation() instanceof MobNavigation) && !(mobEntity.getNavigation() instanceof BirdNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
		}
	}

	@Override
	public boolean canStart() {
		List<MobEntity> list = this.mob.world.getEntities(MobEntity.class, this.mob.getBoundingBox().expand((double)this.maxDistance), this.targetPredicate);
		if (!list.isEmpty()) {
			for (MobEntity mobEntity : list) {
				if (!mobEntity.isInvisible()) {
					this.target = mobEntity;
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean shouldContinue() {
		return this.target != null && !this.navigation.isIdle() && this.mob.squaredDistanceTo(this.target) > (double)(this.minDistance * this.minDistance);
	}

	@Override
	public void start() {
		this.field_6431 = 0;
		this.field_6437 = this.mob.getPathNodeTypeWeight(PathNodeType.field_18);
		this.mob.setPathNodeTypeWeight(PathNodeType.field_18, 0.0F);
	}

	@Override
	public void stop() {
		this.target = null;
		this.navigation.stop();
		this.mob.setPathNodeTypeWeight(PathNodeType.field_18, this.field_6437);
	}

	@Override
	public void tick() {
		if (this.target != null && !this.mob.isLeashed()) {
			this.mob.getLookControl().lookAt(this.target, 10.0F, (float)this.mob.getLookPitchSpeed());
			if (--this.field_6431 <= 0) {
				this.field_6431 = 10;
				double d = this.mob.x - this.target.x;
				double e = this.mob.y - this.target.y;
				double f = this.mob.z - this.target.z;
				double g = d * d + e * e + f * f;
				if (!(g <= (double)(this.minDistance * this.minDistance))) {
					this.navigation.startMovingTo(this.target, this.speed);
				} else {
					this.navigation.stop();
					LookControl lookControl = this.target.getLookControl();
					if (g <= (double)this.minDistance || lookControl.getLookX() == this.mob.x && lookControl.getLookY() == this.mob.y && lookControl.getLookZ() == this.mob.z) {
						double h = this.target.x - this.mob.x;
						double i = this.target.z - this.mob.z;
						this.navigation.startMovingTo(this.mob.x - h, this.mob.y, this.mob.z - i, this.speed);
					}
				}
			}
		}
	}
}
