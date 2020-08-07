package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class TakeJobSiteTask extends Task<VillagerEntity> {
	private final float speed;

	public TakeJobSiteTask(float speed) {
		super(
			ImmutableMap.of(
				MemoryModuleType.field_25160,
				MemoryModuleState.field_18456,
				MemoryModuleType.field_18439,
				MemoryModuleState.field_18457,
				MemoryModuleType.field_18441,
				MemoryModuleState.field_18456
			)
		);
		this.speed = speed;
	}

	protected boolean method_29264(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return villagerEntity.isBaby() ? false : villagerEntity.getVillagerData().getProfession() == VillagerProfession.field_17051;
	}

	protected void method_29265(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		BlockPos blockPos = ((GlobalPos)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_25160).get()).getPos();
		Optional<PointOfInterestType> optional = serverWorld.getPointOfInterestStorage().getType(blockPos);
		if (optional.isPresent()) {
			LookTargetUtil.streamSeenVillagers(villagerEntity, villagerEntityx -> this.canUseJobSite((PointOfInterestType)optional.get(), villagerEntityx, blockPos))
				.findFirst()
				.ifPresent(
					villagerEntity2 -> this.claimSite(
							serverWorld, villagerEntity, villagerEntity2, blockPos, villagerEntity2.getBrain().getOptionalMemory(MemoryModuleType.field_18439).isPresent()
						)
				);
		}
	}

	private boolean canUseJobSite(PointOfInterestType poiType, VillagerEntity villager, BlockPos pos) {
		boolean bl = villager.getBrain().getOptionalMemory(MemoryModuleType.field_25160).isPresent();
		if (bl) {
			return false;
		} else {
			Optional<GlobalPos> optional = villager.getBrain().getOptionalMemory(MemoryModuleType.field_18439);
			VillagerProfession villagerProfession = villager.getVillagerData().getProfession();
			if (villager.getVillagerData().getProfession() == VillagerProfession.field_17051
				|| !villagerProfession.getWorkStation().getCompletionCondition().test(poiType)) {
				return false;
			} else {
				return !optional.isPresent() ? this.canReachJobSite(villager, pos, poiType) : ((GlobalPos)optional.get()).getPos().equals(pos);
			}
		}
	}

	private void claimSite(ServerWorld world, VillagerEntity previousOwner, VillagerEntity newOwner, BlockPos pos, boolean jobSitePresent) {
		this.forgetJobSiteAndWalkTarget(previousOwner);
		if (!jobSitePresent) {
			LookTargetUtil.walkTowards(newOwner, pos, this.speed, 1);
			newOwner.getBrain().remember(MemoryModuleType.field_25160, GlobalPos.create(world.getRegistryKey(), pos));
			DebugInfoSender.sendPointOfInterest(world, pos);
		}
	}

	private boolean canReachJobSite(VillagerEntity villager, BlockPos pos, PointOfInterestType poiType) {
		Path path = villager.getNavigation().findPathTo(pos, poiType.getSearchDistance());
		return path != null && path.reachesTarget();
	}

	private void forgetJobSiteAndWalkTarget(VillagerEntity villager) {
		villager.getBrain().forget(MemoryModuleType.field_18445);
		villager.getBrain().forget(MemoryModuleType.field_18446);
		villager.getBrain().forget(MemoryModuleType.field_25160);
	}
}
