package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class StayAboveWaterTask extends Task<MobEntity> {
	private final float minWaterHeight;
	private final float chance;

	public StayAboveWaterTask(float minWaterHeight, float chance) {
		super(ImmutableMap.of());
		this.minWaterHeight = minWaterHeight;
		this.chance = chance;
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
		return mobEntity.isInsideWater() && mobEntity.getWaterHeight() > (double)this.minWaterHeight || mobEntity.isInLava();
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		return this.shouldRun(serverWorld, mobEntity);
	}

	protected void keepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		if (mobEntity.getRandom().nextFloat() < this.chance) {
			mobEntity.getJumpControl().setActive();
		}
	}
}
