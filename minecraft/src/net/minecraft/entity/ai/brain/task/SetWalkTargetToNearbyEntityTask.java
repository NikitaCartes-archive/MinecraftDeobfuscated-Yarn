package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.AiUtil;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class SetWalkTargetToNearbyEntityTask extends Task<MobEntityWithAi> {
	private final MemoryModuleType<? extends Entity> entityMemory;
	private final float field_18381;

	public SetWalkTargetToNearbyEntityTask(MemoryModuleType<? extends Entity> memoryModuleType, float f) {
		this.entityMemory = memoryModuleType;
		this.field_18381 = f;
	}

	protected boolean method_19002(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
		return mobEntityWithAi.squaredDistanceTo((Entity)mobEntityWithAi.getBrain().getMemory(this.entityMemory).get()) < 16.0;
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(Pair.of(MemoryModuleType.field_18445, MemoryModuleState.field_18457), Pair.of(this.entityMemory, MemoryModuleState.field_18456));
	}

	protected void method_19003(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		Entity entity = (Entity)mobEntityWithAi.getBrain().getMemory(this.entityMemory).get();

		for (int i = 0; i < 10; i++) {
			Vec3d vec3d = AiUtil.method_6377(
				mobEntityWithAi,
				16,
				7,
				new Vec3d(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z).subtract(new Vec3d(entity.x, entity.y, entity.z)).normalize(),
				(float) (Math.PI / 10)
			);
			if (vec3d != null) {
				mobEntityWithAi.getBrain().putMemory(MemoryModuleType.field_18445, new WalkTarget(vec3d, this.field_18381, 0));
				return;
			}
		}
	}
}
