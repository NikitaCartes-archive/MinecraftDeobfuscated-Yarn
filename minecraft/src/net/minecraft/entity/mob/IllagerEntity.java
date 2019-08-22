package net.minecraft.entity.mob;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
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

	@Environment(EnvType.CLIENT)
	public IllagerEntity.State getState() {
		return IllagerEntity.State.CROSSED;
	}

	public class LongDoorInteractGoal extends net.minecraft.entity.ai.goal.LongDoorInteractGoal {
		public LongDoorInteractGoal(RaiderEntity raiderEntity) {
			super(raiderEntity, false);
		}

		@Override
		public boolean canStart() {
			return super.canStart() && IllagerEntity.this.hasActiveRaid();
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum State {
		CROSSED,
		ATTACKING,
		SPELLCASTING,
		BOW_AND_ARROW,
		CROSSBOW_HOLD,
		CROSSBOW_CHARGE,
		CELEBRATING;
	}
}
