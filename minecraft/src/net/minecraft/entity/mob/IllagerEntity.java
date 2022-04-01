package net.minecraft.entity.mob;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.world.World;

public abstract class IllagerEntity extends RaiderEntity {
	protected IllagerEntity(EntityType<? extends IllagerEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.ILLAGER;
	}

	@Override
	public boolean method_42808() {
		return true;
	}

	public IllagerEntity.State getState() {
		return this.method_42803() != LivingEntity.class_7316.NONE ? IllagerEntity.State.CROSSBOW_HOLD : IllagerEntity.State.CROSSED;
	}

	@Override
	public boolean canTarget(LivingEntity target) {
		return target instanceof MerchantEntity && target.isBaby() ? false : super.canTarget(target);
	}

	protected class LongDoorInteractGoal extends net.minecraft.entity.ai.goal.LongDoorInteractGoal {
		public LongDoorInteractGoal(RaiderEntity raider) {
			super(raider, false);
		}

		@Override
		public boolean canStart() {
			return super.canStart() && IllagerEntity.this.hasActiveRaid();
		}
	}

	public static enum State {
		CROSSED,
		ATTACKING,
		SPELLCASTING,
		BOW_AND_ARROW,
		CROSSBOW_HOLD,
		CROSSBOW_CHARGE,
		CELEBRATING,
		NEUTRAL;
	}
}
