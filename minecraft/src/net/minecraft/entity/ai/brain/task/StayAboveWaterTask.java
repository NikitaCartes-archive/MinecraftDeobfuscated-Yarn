package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;

public class StayAboveWaterTask extends MultiTickTask<MobEntity> {
	private final float chance;

	public StayAboveWaterTask(float chance) {
		super(ImmutableMap.of());
		this.chance = chance;
	}

	public static boolean isUnderwater(MobEntity entity) {
		return entity.isTouchingWater() && entity.getFluidHeight(FluidTags.WATER) > entity.getSwimHeight() || entity.isInLava();
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
		return isUnderwater(mobEntity);
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
