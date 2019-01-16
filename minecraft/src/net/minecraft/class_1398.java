package net.minecraft;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1398 extends Goal {
	private static final Logger field_6635 = LogManager.getLogger();
	private final MobEntity field_6636;
	private final Predicate<LivingEntity> field_6634;
	private final FollowTargetGoal.ClosestSelector field_6631;
	private LivingEntity field_6632;
	private final Class<? extends LivingEntity> field_6633;

	public class_1398(MobEntity mobEntity, Class<? extends LivingEntity> class_) {
		this.field_6636 = mobEntity;
		this.field_6633 = class_;
		if (mobEntity instanceof MobEntityWithAi) {
			field_6635.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
		}

		this.field_6634 = livingEntity -> {
			double d = this.method_6315();
			if (livingEntity.isSneaking()) {
				d *= 0.8F;
			}

			if (livingEntity.isInvisible()) {
				return false;
			} else {
				return (double)livingEntity.distanceTo(this.field_6636) > d ? false : TrackTargetGoal.canTrack(this.field_6636, livingEntity, false, true);
			}
		};
		this.field_6631 = new FollowTargetGoal.ClosestSelector(mobEntity);
	}

	@Override
	public boolean canStart() {
		double d = this.method_6315();
		List<LivingEntity> list = this.field_6636.world.getEntities(this.field_6633, this.field_6636.getBoundingBox().expand(d, 4.0, d), this.field_6634);
		Collections.sort(list, this.field_6631);
		if (list.isEmpty()) {
			return false;
		} else {
			this.field_6632 = (LivingEntity)list.get(0);
			return true;
		}
	}

	@Override
	public boolean shouldContinue() {
		LivingEntity livingEntity = this.field_6636.getTarget();
		if (livingEntity == null) {
			return false;
		} else if (!livingEntity.isValid()) {
			return false;
		} else {
			double d = this.method_6315();
			return this.field_6636.squaredDistanceTo(livingEntity) > d * d
				? false
				: !(livingEntity instanceof ServerPlayerEntity) || !((ServerPlayerEntity)livingEntity).interactionManager.isCreative();
		}
	}

	@Override
	public void start() {
		this.field_6636.setTarget(this.field_6632);
		super.start();
	}

	@Override
	public void onRemove() {
		this.field_6636.setTarget(null);
		super.start();
	}

	protected double method_6315() {
		EntityAttributeInstance entityAttributeInstance = this.field_6636.getAttributeInstance(EntityAttributes.FOLLOW_RANGE);
		return entityAttributeInstance == null ? 16.0 : entityAttributeInstance.getValue();
	}
}
