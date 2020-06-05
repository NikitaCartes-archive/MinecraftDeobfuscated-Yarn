package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;

public class WalkTowardJobSiteTask extends Task<VillagerEntity> {
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
			villagerEntity, ((GlobalPos)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get()).getPos(), this.speed, 1
		);
	}

	protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		Optional<GlobalPos> optional = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
		optional.ifPresent(globalPos -> {
			BlockPos blockPos = globalPos.getPos();
			PointOfInterestStorage pointOfInterestStorage = serverWorld.getServer().getWorld(globalPos.getDimension()).getPointOfInterestStorage();
			if (pointOfInterestStorage.test(blockPos, pointOfInterestType -> true)) {
				pointOfInterestStorage.releaseTicket(blockPos);
			}

			DebugInfoSender.sendPointOfInterest(serverWorld, blockPos);
		});
		villagerEntity.getBrain().forget(MemoryModuleType.POTENTIAL_JOB_SITE);
	}
}
