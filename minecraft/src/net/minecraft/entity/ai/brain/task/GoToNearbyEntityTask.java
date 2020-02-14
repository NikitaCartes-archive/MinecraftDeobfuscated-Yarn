package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class GoToNearbyEntityTask<T> extends Task<MobEntityWithAi> {
	private final MemoryModuleType<T> entityMemory;
	private final float speed;
	private final int field_22321;
	private final Function<T, Vec3d> field_22322;

	public GoToNearbyEntityTask(MemoryModuleType<T> entityMemory, float speed, int i, boolean bl, Function<T, Vec3d> function) {
		super(
			ImmutableMap.of(
				MemoryModuleType.WALK_TARGET, bl ? MemoryModuleState.REGISTERED : MemoryModuleState.VALUE_ABSENT, entityMemory, MemoryModuleState.VALUE_PRESENT
			)
		);
		this.entityMemory = entityMemory;
		this.speed = speed;
		this.field_22321 = i;
		this.field_22322 = function;
	}

	public static GoToNearbyEntityTask<BlockPos> method_24601(MemoryModuleType<BlockPos> memoryModuleType, float f, int i, boolean bl) {
		return new GoToNearbyEntityTask<>(memoryModuleType, f, i, bl, Vec3d::new);
	}

	public static GoToNearbyEntityTask<? extends Entity> method_24603(MemoryModuleType<? extends Entity> memoryModuleType, float f, int i, boolean bl) {
		return new GoToNearbyEntityTask<>(memoryModuleType, f, i, bl, Entity::getPos);
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
		return this.method_24602(mobEntityWithAi) ? false : mobEntityWithAi.getPos().method_24802(this.method_24600(mobEntityWithAi), (double)this.field_22321);
	}

	private Vec3d method_24600(MobEntityWithAi mobEntityWithAi) {
		return (Vec3d)this.field_22322.apply(mobEntityWithAi.getBrain().getOptionalMemory(this.entityMemory).get());
	}

	private boolean method_24602(MobEntityWithAi mobEntityWithAi) {
		if (!mobEntityWithAi.getBrain().hasMemoryModule(MemoryModuleType.WALK_TARGET)) {
			return false;
		} else {
			Optional<WalkTarget> optional = mobEntityWithAi.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET);
			LookTarget lookTarget = ((WalkTarget)optional.get()).getLookTarget();
			return optional.isPresent() && !lookTarget.getBlockPos().isWithinDistance(this.method_24600(mobEntityWithAi), (double)this.field_22321);
		}
	}

	protected void run(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		setWalkTarget(mobEntityWithAi, this.method_24600(mobEntityWithAi), this.speed);
	}

	private static void setWalkTarget(MobEntityWithAi entity, Vec3d vec3d, float speed) {
		for (int i = 0; i < 10; i++) {
			Vec3d vec3d2 = TargetFinder.findGroundTargetAwayFrom(entity, 16, 7, vec3d);
			if (vec3d2 != null) {
				entity.getBrain().putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d2, speed, 0));
				return;
			}
		}
	}
}
