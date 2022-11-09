package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

public class VillagerBreedTask extends MultiTickTask<VillagerEntity> {
	private static final int MAX_DISTANCE = 5;
	private static final float APPROACH_SPEED = 0.5F;
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
		PassiveEntity passiveEntity = (PassiveEntity)villagerEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.BREED_TARGET).get();
		LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, passiveEntity, 0.5F);
		serverWorld.sendEntityStatus(passiveEntity, EntityStatuses.ADD_BREEDING_PARTICLES);
		serverWorld.sendEntityStatus(villagerEntity, EntityStatuses.ADD_BREEDING_PARTICLES);
		int i = 275 + villagerEntity.getRandom().nextInt(50);
		this.breedEndTime = l + (long)i;
	}

	protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = (VillagerEntity)villagerEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.BREED_TARGET).get();
		if (!(villagerEntity.squaredDistanceTo(villagerEntity2) > 5.0)) {
			LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2, 0.5F);
			if (l >= this.breedEndTime) {
				villagerEntity.eatForBreeding();
				villagerEntity2.eatForBreeding();
				this.goHome(serverWorld, villagerEntity, villagerEntity2);
			} else if (villagerEntity.getRandom().nextInt(35) == 0) {
				serverWorld.sendEntityStatus(villagerEntity2, EntityStatuses.ADD_VILLAGER_HEART_PARTICLES);
				serverWorld.sendEntityStatus(villagerEntity, EntityStatuses.ADD_VILLAGER_HEART_PARTICLES);
			}
		}
	}

	private void goHome(ServerWorld world, VillagerEntity first, VillagerEntity second) {
		Optional<BlockPos> optional = this.getReachableHome(world, first);
		if (!optional.isPresent()) {
			world.sendEntityStatus(second, EntityStatuses.ADD_VILLAGER_ANGRY_PARTICLES);
			world.sendEntityStatus(first, EntityStatuses.ADD_VILLAGER_ANGRY_PARTICLES);
		} else {
			Optional<VillagerEntity> optional2 = this.createChild(world, first, second);
			if (optional2.isPresent()) {
				this.setChildHome(world, (VillagerEntity)optional2.get(), (BlockPos)optional.get());
			} else {
				world.getPointOfInterestStorage().releaseTicket((BlockPos)optional.get());
				DebugInfoSender.sendPointOfInterest(world, (BlockPos)optional.get());
			}
		}
	}

	protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		villagerEntity.getBrain().forget(MemoryModuleType.BREED_TARGET);
	}

	private boolean isReadyToBreed(VillagerEntity villager) {
		Brain<VillagerEntity> brain = villager.getBrain();
		Optional<PassiveEntity> optional = brain.getOptionalRegisteredMemory(MemoryModuleType.BREED_TARGET)
			.filter(passiveEntity -> passiveEntity.getType() == EntityType.VILLAGER);
		return !optional.isPresent()
			? false
			: LookTargetUtil.canSee(brain, MemoryModuleType.BREED_TARGET, EntityType.VILLAGER)
				&& villager.isReadyToBreed()
				&& ((PassiveEntity)optional.get()).isReadyToBreed();
	}

	private Optional<BlockPos> getReachableHome(ServerWorld world, VillagerEntity villager) {
		return world.getPointOfInterestStorage()
			.getPosition(
				poiType -> poiType.matchesKey(PointOfInterestTypes.HOME), (poiType, pos) -> this.canReachHome(villager, pos, poiType), villager.getBlockPos(), 48
			);
	}

	private boolean canReachHome(VillagerEntity villager, BlockPos pos, RegistryEntry<PointOfInterestType> poiType) {
		Path path = villager.getNavigation().findPathTo(pos, poiType.value().searchDistance());
		return path != null && path.reachesTarget();
	}

	private Optional<VillagerEntity> createChild(ServerWorld world, VillagerEntity parent, VillagerEntity partner) {
		VillagerEntity villagerEntity = parent.createChild(world, partner);
		if (villagerEntity == null) {
			return Optional.empty();
		} else {
			parent.setBreedingAge(6000);
			partner.setBreedingAge(6000);
			villagerEntity.setBreedingAge(-24000);
			villagerEntity.refreshPositionAndAngles(parent.getX(), parent.getY(), parent.getZ(), 0.0F, 0.0F);
			world.spawnEntityAndPassengers(villagerEntity);
			world.sendEntityStatus(villagerEntity, EntityStatuses.ADD_VILLAGER_HEART_PARTICLES);
			return Optional.of(villagerEntity);
		}
	}

	private void setChildHome(ServerWorld world, VillagerEntity child, BlockPos pos) {
		GlobalPos globalPos = GlobalPos.create(world.getRegistryKey(), pos);
		child.getBrain().remember(MemoryModuleType.HOME, globalPos);
	}
}
