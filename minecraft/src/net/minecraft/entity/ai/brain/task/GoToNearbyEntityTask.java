package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class GoToNearbyEntityTask extends Task<MobEntityWithAi> {
	private final MemoryModuleType<? extends Entity> entityMemory;
	private final float field_18381;

	public GoToNearbyEntityTask(MemoryModuleType<? extends Entity> memoryModuleType, float f) {
		super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, memoryModuleType, MemoryModuleState.VALUE_PRESENT));
		this.entityMemory = memoryModuleType;
		this.field_18381 = f;
	}

	protected boolean method_19002(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
		Entity entity = (Entity)mobEntityWithAi.getBrain().getOptionalMemory(this.entityMemory).get();
		return mobEntityWithAi.squaredDistanceTo(entity) < 36.0;
	}

	protected void method_19003(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		Entity entity = (Entity)mobEntityWithAi.getBrain().getOptionalMemory(this.entityMemory).get();
		method_19596(mobEntityWithAi, entity, this.field_18381);
	}

	public static void method_19596(MobEntityWithAi mobEntityWithAi, Entity entity, float f) {
		for (int i = 0; i < 10; i++) {
			Vec3d vec3d = TargetFinder.findGroundTargetAwayFrom(mobEntityWithAi, 16, 7, entity.getPos());
			if (vec3d != null) {
				mobEntityWithAi.getBrain().putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, f, 0));
				return;
			}
		}
	}
}
