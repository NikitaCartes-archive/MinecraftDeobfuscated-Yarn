package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class StayAboveWaterTask extends Task<MobEntity> {
	private final float minWaterHeight;
	private final float chance;

	public StayAboveWaterTask(float f, float g) {
		super(ImmutableMap.of());
		this.minWaterHeight = f;
		this.chance = g;
	}

	protected boolean method_19010(ServerWorld serverWorld, MobEntity mobEntity) {
		return mobEntity.isInsideWater() && mobEntity.getWaterHeight() > (double)this.minWaterHeight || mobEntity.isInLava();
	}

	protected boolean method_19011(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		return this.method_19010(serverWorld, mobEntity);
	}

	protected void method_19012(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		if (mobEntity.getRand().nextFloat() < this.chance) {
			mobEntity.getJumpControl().setActive();
		}
	}
}
