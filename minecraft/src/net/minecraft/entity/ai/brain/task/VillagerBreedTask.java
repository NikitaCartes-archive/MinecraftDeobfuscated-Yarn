package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestType;

public class VillagerBreedTask extends Task<VillagerEntity> {
	private long breedEndTime;

	public VillagerBreedTask() {
		super(ImmutableMap.of(MemoryModuleType.field_18448, MemoryModuleState.field_18456, MemoryModuleType.field_18442, MemoryModuleState.field_18456), 350, 350);
	}

	protected boolean method_19571(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return this.isReadyToBreed(villagerEntity);
	}

	protected boolean method_18973(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return l <= this.breedEndTime && this.isReadyToBreed(villagerEntity);
	}

	protected void method_18974(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		PassiveEntity passiveEntity = (PassiveEntity)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18448).get();
		LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, passiveEntity, 0.5F);
		serverWorld.sendEntityStatus(passiveEntity, (byte)18);
		serverWorld.sendEntityStatus(villagerEntity, (byte)18);
		int i = 275 + villagerEntity.getRandom().nextInt(50);
		this.breedEndTime = l + (long)i;
	}

	protected void method_18975(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = (VillagerEntity)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18448).get();
		if (!(villagerEntity.squaredDistanceTo(villagerEntity2) > 5.0)) {
			LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2, 0.5F);
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
			Optional<VillagerEntity> optional2 = this.createChild(world, first, second);
			if (optional2.isPresent()) {
				this.setChildHome(world, (VillagerEntity)optional2.get(), (BlockPos)optional.get());
			} else {
				world.getPointOfInterestStorage().releaseTicket((BlockPos)optional.get());
				DebugInfoSender.sendPointOfInterest(world, (BlockPos)optional.get());
			}
		}
	}

	protected void method_18976(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		villagerEntity.getBrain().forget(MemoryModuleType.field_18448);
	}

	private boolean isReadyToBreed(VillagerEntity villager) {
		Brain<VillagerEntity> brain = villager.getBrain();
		Optional<PassiveEntity> optional = brain.getOptionalMemory(MemoryModuleType.field_18448)
			.filter(passiveEntity -> passiveEntity.getType() == EntityType.field_6077);
		return !optional.isPresent()
			? false
			: LookTargetUtil.canSee(brain, MemoryModuleType.field_18448, EntityType.field_6077)
				&& villager.isReadyToBreed()
				&& ((PassiveEntity)optional.get()).isReadyToBreed();
	}

	private Optional<BlockPos> getReachableHome(ServerWorld world, VillagerEntity villager) {
		return world.getPointOfInterestStorage()
			.getPosition(PointOfInterestType.field_18517.getCompletionCondition(), blockPos -> this.canReachHome(villager, blockPos), villager.getBlockPos(), 48);
	}

	private boolean canReachHome(VillagerEntity villager, BlockPos pos) {
		Path path = villager.getNavigation().findPathTo(pos, PointOfInterestType.field_18517.getSearchDistance());
		return path != null && path.reachesTarget();
	}

	private Optional<VillagerEntity> createChild(ServerWorld serverWorld, VillagerEntity villagerEntity, VillagerEntity villagerEntity2) {
		VillagerEntity villagerEntity3 = villagerEntity.method_7225(serverWorld, villagerEntity2);
		if (villagerEntity3 == null) {
			return Optional.empty();
		} else {
			villagerEntity.setBreedingAge(6000);
			villagerEntity2.setBreedingAge(6000);
			villagerEntity3.setBreedingAge(-24000);
			villagerEntity3.refreshPositionAndAngles(villagerEntity.getX(), villagerEntity.getY(), villagerEntity.getZ(), 0.0F, 0.0F);
			serverWorld.spawnEntityAndPassengers(villagerEntity3);
			serverWorld.sendEntityStatus(villagerEntity3, (byte)12);
			return Optional.of(villagerEntity3);
		}
	}

	private void setChildHome(ServerWorld world, VillagerEntity child, BlockPos pos) {
		GlobalPos globalPos = GlobalPos.create(world.getRegistryKey(), pos);
		child.getBrain().remember(MemoryModuleType.field_18438, globalPos);
	}
}
