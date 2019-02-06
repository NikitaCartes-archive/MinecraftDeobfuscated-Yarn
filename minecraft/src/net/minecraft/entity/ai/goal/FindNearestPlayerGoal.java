package net.minecraft.entity.ai.goal;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractScoreboardTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FindNearestPlayerGoal extends Goal {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MobEntity owner;
	private final Predicate<Entity> field_6650;
	private final FollowTargetGoal.ClosestSelector field_6648;
	private LivingEntity target;

	public FindNearestPlayerGoal(MobEntity mobEntity) {
		this.owner = mobEntity;
		if (mobEntity instanceof MobEntityWithAi) {
			LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
		}

		this.field_6650 = entity -> {
			if (!(entity instanceof PlayerEntity)) {
				return false;
			} else if (((PlayerEntity)entity).abilities.invulnerable) {
				return false;
			} else {
				double d = this.method_6324();
				if (entity.isSneaking()) {
					d *= 0.8F;
				}

				if (entity.isInvisible()) {
					float f = ((PlayerEntity)entity).getWornArmorRatio();
					if (f < 0.1F) {
						f = 0.1F;
					}

					d *= (double)(0.7F * f);
				}

				return (double)entity.distanceTo(this.owner) > d ? false : TrackTargetGoal.canTrack(this.owner, (LivingEntity)entity, false, true);
			}
		};
		this.field_6648 = new FollowTargetGoal.ClosestSelector(mobEntity);
	}

	@Override
	public boolean canStart() {
		double d = this.method_6324();
		List<PlayerEntity> list = this.owner.world.getEntities(PlayerEntity.class, this.owner.getBoundingBox().expand(d, 4.0, d), this.field_6650);
		Collections.sort(list, this.field_6648);
		if (list.isEmpty()) {
			return false;
		} else {
			this.target = (LivingEntity)list.get(0);
			return true;
		}
	}

	@Override
	public boolean shouldContinue() {
		LivingEntity livingEntity = this.owner.getTarget();
		if (livingEntity == null) {
			return false;
		} else if (!livingEntity.isValid()) {
			return false;
		} else if (livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.invulnerable) {
			return false;
		} else {
			AbstractScoreboardTeam abstractScoreboardTeam = this.owner.getScoreboardTeam();
			AbstractScoreboardTeam abstractScoreboardTeam2 = livingEntity.getScoreboardTeam();
			if (abstractScoreboardTeam != null && abstractScoreboardTeam2 == abstractScoreboardTeam) {
				return false;
			} else {
				double d = this.method_6324();
				return this.owner.squaredDistanceTo(livingEntity) > d * d
					? false
					: !(livingEntity instanceof ServerPlayerEntity) || !((ServerPlayerEntity)livingEntity).interactionManager.isCreative();
			}
		}
	}

	@Override
	public void start() {
		this.owner.setTarget(this.target);
		super.start();
	}

	@Override
	public void onRemove() {
		this.owner.setTarget(null);
		super.start();
	}

	protected double method_6324() {
		EntityAttributeInstance entityAttributeInstance = this.owner.getAttributeInstance(EntityAttributes.FOLLOW_RANGE);
		return entityAttributeInstance == null ? 16.0 : entityAttributeInstance.getValue();
	}
}
