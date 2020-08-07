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
		super(ImmutableMap.of(MemoryModuleType.field_25160, MemoryModuleState.field_18456), 1200);
		this.speed = speed;
	}

	protected boolean method_29251(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return (Boolean)villagerEntity.getBrain()
			.getFirstPossibleNonCoreActivity()
			.map(activity -> activity == Activity.field_18595 || activity == Activity.field_18596 || activity == Activity.field_18885)
			.orElse(true);
	}

	protected boolean method_29523(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return villagerEntity.getBrain().hasMemoryModule(MemoryModuleType.field_25160);
	}

	protected void method_29252(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		LookTargetUtil.walkTowards(
			villagerEntity, ((GlobalPos)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_25160).get()).getPos(), this.speed, 1
		);
	}

	protected void method_29525(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		Optional<GlobalPos> optional = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_25160);
		optional.ifPresent(globalPos -> {
			BlockPos blockPos = globalPos.getPos();
			ServerWorld serverWorld2 = serverWorld.getServer().getWorld(globalPos.getDimension());
			if (serverWorld2 != null) {
				PointOfInterestStorage pointOfInterestStorage = serverWorld2.getPointOfInterestStorage();
				if (pointOfInterestStorage.test(blockPos, pointOfInterestType -> true)) {
					pointOfInterestStorage.releaseTicket(blockPos);
				}

				DebugInfoSender.sendPointOfInterest(serverWorld, blockPos);
			}
		});
		villagerEntity.getBrain().forget(MemoryModuleType.field_25160);
	}
}
