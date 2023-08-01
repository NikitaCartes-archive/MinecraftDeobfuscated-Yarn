package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.world.World;
import org.joml.Vector3f;

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

	public IllagerEntity.State getState() {
		return IllagerEntity.State.CROSSED;
	}

	@Override
	public boolean canTarget(LivingEntity target) {
		return target instanceof MerchantEntity && target.isBaby() ? false : super.canTarget(target);
	}

	@Override
	protected float getUnscaledRidingOffset(Entity vehicle) {
		return -0.6F;
	}

	@Override
	protected Vector3f getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
		return new Vector3f(0.0F, dimensions.height + 0.05F * scaleFactor, 0.0F);
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
