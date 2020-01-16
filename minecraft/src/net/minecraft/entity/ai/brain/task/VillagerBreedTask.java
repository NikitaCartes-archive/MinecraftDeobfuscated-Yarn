package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestType;

public class VillagerBreedTask extends Task<VillagerEntity> {
	private long breedEndTime;

	public VillagerBreedTask() {
		super(
			ImmutableMap.of(MemoryModuleType.BREED_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT), 350, 350
		);
	}

	protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return this.isReadyToBreed(villagerEntity);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return l <= this.breedEndTime && this.isReadyToBreed(villagerEntity);
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = this.getBreedTarget(villagerEntity);
		LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2);
		serverWorld.sendEntityStatus(villagerEntity2, (byte)18);
		serverWorld.sendEntityStatus(villagerEntity, (byte)18);
		int i = 275 + villagerEntity.getRandom().nextInt(50);
		this.breedEndTime = l + (long)i;
	}

	protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = this.getBreedTarget(villagerEntity);
		if (!(villagerEntity.squaredDistanceTo(villagerEntity2) > 5.0)) {
			LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2);
			if (l >= this.breedEndTime) {
				villagerEntity.eatForBreeding();
				villagerEntity2.eatForBreeding();
				this.goHome(serverWorld, villagerEntity, villagerEntity2);
			} else if (villagerEntity.getRandom().nextInt(35) == 0) {
				serverWorld.sendEntityStatus(villagerEntity2, (byte)12);
				serverWorld.sendEntityStatus(villagerEntity, (byte)12);
			}
		}
	}

	private void goHome(ServerWorld world, VillagerEntity first, VillagerEntity second) {
		Optional<BlockPos> optional = this.getReachableHome(world, first);
		if (!optional.isPresent()) {
			world.sendEntityStatus(second, (byte)13);
			world.sendEntityStatus(first, (byte)13);
		} else {
			Optional<VillagerEntity> optional2 = this.createChild(first, second);
			if (optional2.isPresent()) {
				this.setChildHome(world, (VillagerEntity)optional2.get(), (BlockPos)optional.get());
			} else {
				world.getPointOfInterestStorage().releaseTicket((BlockPos)optional.get());
				DebugRendererInfoManager.sendPointOfInterest(world, (BlockPos)optional.get());
			}
		}
	}

	protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		villagerEntity.getBrain().forget(MemoryModuleType.BREED_TARGET);
	}

	private VillagerEntity getBreedTarget(VillagerEntity villager) {
		return (VillagerEntity)villager.getBrain().getOptionalMemory(MemoryModuleType.BREED_TARGET).get();
	}

	private boolean isReadyToBreed(VillagerEntity villager) {
		Brain<VillagerEntity> brain = villager.getBrain();
		if (!brain.getOptionalMemory(MemoryModuleType.BREED_TARGET).isPresent()) {
			return false;
		} else {
			VillagerEntity villagerEntity = this.getBreedTarget(villager);
			return LookTargetUtil.canSee(brain, MemoryModuleType.BREED_TARGET, EntityType.VILLAGER) && villager.isReadyToBreed() && villagerEntity.isReadyToBreed();
		}
	}

	private Optional<BlockPos> getReachableHome(ServerWorld world, VillagerEntity villager) {
		return world.getPointOfInterestStorage()
			.getPosition(PointOfInterestType.HOME.getCompletionCondition(), blockPos -> this.canReachHome(villager, blockPos), new BlockPos(villager), 48);
	}

	private boolean canReachHome(VillagerEntity villager, BlockPos pos) {
		Path path = villager.getNavigation().findPathTo(pos, PointOfInterestType.HOME.getSearchDistance());
		return path != null && path.reachesTarget();
	}

	private Optional<VillagerEntity> createChild(VillagerEntity first, VillagerEntity second) {
		VillagerEntity villagerEntity = first.createChild(second);
		if (villagerEntity == null) {
			return Optional.empty();
		} else {
			first.setBreedingAge(6000);
			second.setBreedingAge(6000);
			villagerEntity.setBreedingAge(-24000);
			villagerEntity.refreshPositionAndAngles(first.getX(), first.getY(), first.getZ(), 0.0F, 0.0F);
			first.world.spawnEntity(villagerEntity);
			first.world.sendEntityStatus(villagerEntity, (byte)12);
			return Optional.of(villagerEntity);
		}
	}

	private void setChildHome(ServerWorld world, VillagerEntity child, BlockPos pos) {
		GlobalPos globalPos = GlobalPos.create(world.getDimension().getType(), pos);
		child.getBrain().putMemory(MemoryModuleType.HOME, globalPos);
	}
}
