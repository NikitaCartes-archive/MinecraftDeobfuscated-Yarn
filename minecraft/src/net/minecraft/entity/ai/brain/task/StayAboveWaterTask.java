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

	protected boolean method_19010(ServerWorld serverWorld, MobEntity mobEntity) {
		return mobEntity.isTouchingWater() && mobEntity.getFluidHeight(FluidTags.field_15517) > mobEntity.method_29241() || mobEntity.isInLava();
	}

	protected boolean method_19011(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		return this.method_19010(serverWorld, mobEntity);
	}

	protected void method_19012(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		if (mobEntity.getRandom().nextFloat() < this.chance) {
			mobEntity.getJumpControl().setActive();
		}
	}
}
