package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class JumpTask extends Task<MobEntity> {
	private final float field_18386;
	private final float field_18387;

	public JumpTask(float f, float g) {
		this.field_18386 = f;
		this.field_18387 = g;
	}

	protected boolean method_19010(ServerWorld serverWorld, MobEntity mobEntity) {
		return mobEntity.isInsideWater() && mobEntity.method_5861() > (double)this.field_18386 || mobEntity.isTouchingLava();
	}

	protected boolean method_19011(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		return this.method_19010(serverWorld, mobEntity);
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of();
	}

	protected void method_19012(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		if (mobEntity.getRand().nextFloat() < this.field_18387) {
			mobEntity.getJumpControl().setActive();
		}
	}
}
