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
	protected boolean method_50669() {
		return true;
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.ILLAGER;
	}

	public IllagerEntity.State getState() {
		return IllagerEntity.State.CROSSED;
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
