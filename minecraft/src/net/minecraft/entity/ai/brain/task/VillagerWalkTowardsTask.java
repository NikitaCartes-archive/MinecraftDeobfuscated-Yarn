package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class VillagerWalkTowardsTask extends Task<VillagerEntity> {
	private final MemoryModuleType<GlobalPos> destination;
	private final float speed;
	private final int completionRange;
	private final int maxRange;
	private final int maxRunTime;

	public VillagerWalkTowardsTask(MemoryModuleType<GlobalPos> destination, float speed, int completionRange, int maxRange, int maxRunTime) {
		super(
			ImmutableMap.of(
				MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				destination,
				MemoryModuleState.VALUE_PRESENT
			)
		);
		this.destination = destination;
		this.speed = speed;
		this.completionRange = completionRange;
		this.maxRange = maxRange;
		this.maxRunTime = maxRunTime;
	}

	private void giveUp(VillagerEntity villager, long time) {
		Brain<?> brain = villager.getBrain();
		villager.releaseTicketFor(this.destination);
		brain.forget(this.destination);
		brain.remember(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, time);
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		Brain<?> brain = villagerEntity.getBrain();
		brain.getOptionalMemory(this.destination)
			.ifPresent(
				globalPos -> {
					if (this.method_30952(serverWorld, globalPos) || this.shouldGiveUp(serverWorld, villagerEntity)) {
						this.giveUp(villagerEntity, l);
					} else if (this.exceedsMaxRange(villagerEntity, globalPos)) {
						Vec3d vec3d = null;
						int i = 0;

						for (int j = 1000;
							i < 1000 && (vec3d == null || this.exceedsMaxRange(villagerEntity, GlobalPos.create(serverWorld.getRegistryKey(), new BlockPos(vec3d))));
							i++
						) {
							vec3d = TargetFinder.findTargetTowards(villagerEntity, 15, 7, Vec3d.ofBottomCenter(globalPos.getPos()));
						}

						if (i == 1000) {
							this.giveUp(villagerEntity, l);
							return;
						}

						brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, this.speed, this.completionRange));
					} else if (!this.reachedDestination(serverWorld, villagerEntity, globalPos)) {
						brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(globalPos.getPos(), this.speed, this.completionRange));
					}
				}
			);
	}

	private boolean shouldGiveUp(ServerWorld world, VillagerEntity villager) {
		Optional<Long> optional = villager.getBrain().getOptionalMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
		return optional.isPresent() ? world.getTime() - (Long)optional.get() > (long)this.maxRunTime : false;
	}

	private boolean exceedsMaxRange(VillagerEntity villagerEntity, GlobalPos globalPos) {
		return globalPos.getPos().getManhattanDistance(villagerEntity.getBlockPos()) > this.maxRange;
	}

	private boolean method_30952(ServerWorld serverWorld, GlobalPos globalPos) {
		return globalPos.getDimension() != serverWorld.getRegistryKey();
	}

	private boolean reachedDestination(ServerWorld world, VillagerEntity villager, GlobalPos pos) {
		return pos.getDimension() == world.getRegistryKey() && pos.getPos().getManhattanDistance(villager.getBlockPos()) <= this.completionRange;
	}
}
