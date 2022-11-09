package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.poi.PointOfInterestStorage;

public class WalkTowardJobSiteTask extends MultiTickTask<VillagerEntity> {
	private static final int RUN_TIME = 1200;
	final float speed;

	public WalkTowardJobSiteTask(float speed) {
		super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleState.VALUE_PRESENT), 1200);
		this.speed = speed;
	}

	protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return (Boolean)villagerEntity.getBrain()
			.getFirstPossibleNonCoreActivity()
			.map(activity -> activity == Activity.IDLE || activity == Activity.WORK || activity == Activity.PLAY)
			.orElse(true);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return villagerEntity.getBrain().hasMemoryModule(MemoryModuleType.POTENTIAL_JOB_SITE);
	}

	protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		LookTargetUtil.walkTowards(
			villagerEntity, ((GlobalPos)villagerEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get()).getPos(), this.speed, 1
		);
	}

	protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		Optional<GlobalPos> optional = villagerEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
		optional.ifPresent(pos -> {
			BlockPos blockPos = pos.getPos();
			ServerWorld serverWorld2 = serverWorld.getServer().getWorld(pos.getDimension());
			if (serverWorld2 != null) {
				PointOfInterestStorage pointOfInterestStorage = serverWorld2.getPointOfInterestStorage();
				if (pointOfInterestStorage.test(blockPos, poiType -> true)) {
					pointOfInterestStorage.releaseTicket(blockPos);
				}

				DebugInfoSender.sendPointOfInterest(serverWorld, blockPos);
			}
		});
		villagerEntity.getBrain().forget(MemoryModuleType.POTENTIAL_JOB_SITE);
	}
}
