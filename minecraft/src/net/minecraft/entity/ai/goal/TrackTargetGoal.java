package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractScoreboardTeam;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public abstract class TrackTargetGoal extends Goal {
	protected final MobEntity entity;
	protected final boolean checkVisibility;
	private final boolean checkCanNavigate;
	private int canNavigateFlag;
	private int checkCanNavigateCooldown;
	private int timeWithoutVisibility;
	protected LivingEntity target;
	protected int maxTimeWithoutVisibility = 60;

	public TrackTargetGoal(MobEntity mobEntity, boolean bl) {
		this(mobEntity, bl, false);
	}

	public TrackTargetGoal(MobEntity mobEntity, boolean bl, boolean bl2) {
		this.entity = mobEntity;
		this.checkVisibility = bl;
		this.checkCanNavigate = bl2;
	}

	@Override
	public boolean shouldContinue() {
		LivingEntity livingEntity = this.entity.getTarget();
		if (livingEntity == null) {
			livingEntity = this.target;
		}

		if (livingEntity == null) {
			return false;
		} else if (!livingEntity.isValid()) {
			return false;
		} else {
			AbstractScoreboardTeam abstractScoreboardTeam = this.entity.getScoreboardTeam();
			AbstractScoreboardTeam abstractScoreboardTeam2 = livingEntity.getScoreboardTeam();
			if (abstractScoreboardTeam != null && abstractScoreboardTeam2 == abstractScoreboardTeam) {
				return false;
			} else {
				double d = this.getFollowRange();
				if (this.entity.squaredDistanceTo(livingEntity) > d * d) {
					return false;
				} else {
					if (this.checkVisibility) {
						if (this.entity.getVisibilityCache().canSee(livingEntity)) {
							this.timeWithoutVisibility = 0;
						} else if (++this.timeWithoutVisibility > this.maxTimeWithoutVisibility) {
							return false;
						}
					}

					if (livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.invulnerable) {
						return false;
					} else {
						this.entity.setTarget(livingEntity);
						return true;
					}
				}
			}
		}
	}

	protected double getFollowRange() {
		EntityAttributeInstance entityAttributeInstance = this.entity.getAttributeInstance(EntityAttributes.FOLLOW_RANGE);
		return entityAttributeInstance == null ? 16.0 : entityAttributeInstance.getValue();
	}

	@Override
	public void start() {
		this.canNavigateFlag = 0;
		this.checkCanNavigateCooldown = 0;
		this.timeWithoutVisibility = 0;
	}

	@Override
	public void onRemove() {
		this.entity.setTarget(null);
		this.target = null;
	}

	protected boolean canTrack(@Nullable LivingEntity livingEntity, TargetPredicate targetPredicate) {
		if (livingEntity == null) {
			return false;
		} else if (!targetPredicate.test(this.entity, livingEntity)) {
			return false;
		} else if (!this.entity.method_18407(new BlockPos(livingEntity))) {
			return false;
		} else {
			if (this.checkCanNavigate) {
				if (--this.checkCanNavigateCooldown <= 0) {
					this.canNavigateFlag = 0;
				}

				if (this.canNavigateFlag == 0) {
					this.canNavigateFlag = this.canNavigateToEntity(livingEntity) ? 1 : 2;
				}

				if (this.canNavigateFlag == 2) {
					return false;
				}
			}

			return true;
		}
	}

	private boolean canNavigateToEntity(LivingEntity livingEntity) {
		this.checkCanNavigateCooldown = 10 + this.entity.getRand().nextInt(5);
		Path path = this.entity.getNavigation().findPathTo(livingEntity);
		if (path == null) {
			return false;
		} else {
			PathNode pathNode = path.getEnd();
			if (pathNode == null) {
				return false;
			} else {
				int i = pathNode.x - MathHelper.floor(livingEntity.x);
				int j = pathNode.z - MathHelper.floor(livingEntity.z);
				return (double)(i * i + j * j) <= 2.25;
			}
		}
	}

	public TrackTargetGoal setMaxTimeWithoutVisibility(int i) {
		this.maxTimeWithoutVisibility = i;
		return this;
	}
}
