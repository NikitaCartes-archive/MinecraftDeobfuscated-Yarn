package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;

public class StayAboveWaterTask extends Task<MobEntity> {
	private final float chance;

	public StayAboveWaterTask(float minWaterHeight) {
		super(ImmutableMap.of());
		this.chance = minWaterHeight;
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
		return mobEntity.isTouchingWater() && mobEntity.getFluidHeight(FluidTags.WATER) > mobEntity.method_29241() || mobEntity.isInLava();
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
