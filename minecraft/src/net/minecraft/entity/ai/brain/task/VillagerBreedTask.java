package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestType;

public class VillagerBreedTask extends Task<VillagerEntity> {
	private long field_18368;

	public VillagerBreedTask() {
		super(ImmutableMap.of(MemoryModuleType.field_18448, MemoryModuleState.field_18456, MemoryModuleType.field_18442, MemoryModuleState.field_18456), 350, 350);
	}

	protected boolean method_19571(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return this.method_18972(villagerEntity);
	}

	protected boolean method_18973(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return l <= this.field_18368 && this.method_18972(villagerEntity);
	}

	protected void method_18974(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = this.method_19570(villagerEntity);
		LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2);
		serverWorld.sendEntityStatus(villagerEntity2, (byte)18);
		serverWorld.sendEntityStatus(villagerEntity, (byte)18);
		int i = 275 + villagerEntity.getRand().nextInt(50);
		this.field_18368 = l + (long)i;
	}

	protected void method_18975(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = this.method_19570(villagerEntity);
		if (!(villagerEntity.squaredDistanceTo(villagerEntity2) > 5.0)) {
			LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2);
			if (l >= this.field_18368) {
				villagerEntity.eatForBreeding();
				villagerEntity2.eatForBreeding();
				this.method_20643(serverWorld, villagerEntity, villagerEntity2);
			} else if (villagerEntity.getRand().nextInt(35) == 0) {
				serverWorld.sendEntityStatus(villagerEntity2, (byte)12);
				serverWorld.sendEntityStatus(villagerEntity, (byte)12);
			}
		}
	}

	private void method_20643(ServerWorld serverWorld, VillagerEntity villagerEntity, VillagerEntity villagerEntity2) {
		Optional<BlockPos> optional = this.method_19573(serverWorld, villagerEntity);
		if (!optional.isPresent()) {
			serverWorld.sendEntityStatus(villagerEntity2, (byte)13);
			serverWorld.sendEntityStatus(villagerEntity, (byte)13);
		} else {
			Optional<VillagerEntity> optional2 = this.method_18970(villagerEntity, villagerEntity2);
			if (optional2.isPresent()) {
				this.method_19572(serverWorld, (VillagerEntity)optional2.get(), (BlockPos)optional.get());
			} else {
				serverWorld.getPointOfInterestStorage().releaseTicket((BlockPos)optional.get());
			}
		}
	}

	protected void method_18976(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		villagerEntity.getBrain().forget(MemoryModuleType.field_18448);
	}

	private VillagerEntity method_19570(VillagerEntity villagerEntity) {
		return (VillagerEntity)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18448).get();
	}

	private boolean method_18972(VillagerEntity villagerEntity) {
		Brain<VillagerEntity> brain = villagerEntity.getBrain();
		if (!brain.getOptionalMemory(MemoryModuleType.field_18448).isPresent()) {
			return false;
		} else {
			VillagerEntity villagerEntity2 = this.method_19570(villagerEntity);
			return LookTargetUtil.canSee(brain, MemoryModuleType.field_18448, EntityType.field_6077)
				&& villagerEntity.isReadyToBreed()
				&& villagerEntity2.isReadyToBreed();
		}
	}

	private Optional<BlockPos> method_19573(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return serverWorld.getPointOfInterestStorage()
			.getPosition(
				PointOfInterestType.field_18517.getCompletionCondition(), blockPos -> this.method_20642(villagerEntity, blockPos), new BlockPos(villagerEntity), 48
			);
	}

	private boolean method_20642(VillagerEntity villagerEntity, BlockPos blockPos) {
		Path path = villagerEntity.getNavigation().findPathTo(blockPos, PointOfInterestType.field_18517.method_21648());
		return path != null && path.method_21655();
	}

	private Optional<VillagerEntity> method_18970(VillagerEntity villagerEntity, VillagerEntity villagerEntity2) {
		VillagerEntity villagerEntity3 = villagerEntity.method_7225(villagerEntity2);
		if (villagerEntity3 == null) {
			return Optional.empty();
		} else {
			villagerEntity.setBreedingAge(6000);
			villagerEntity2.setBreedingAge(6000);
			villagerEntity3.setBreedingAge(-24000);
			villagerEntity3.setPositionAndAngles(villagerEntity.x, villagerEntity.y, villagerEntity.z, 0.0F, 0.0F);
			villagerEntity.world.spawnEntity(villagerEntity3);
			villagerEntity.world.sendEntityStatus(villagerEntity3, (byte)12);
			return Optional.of(villagerEntity3);
		}
	}

	private void method_19572(ServerWorld serverWorld, VillagerEntity villagerEntity, BlockPos blockPos) {
		GlobalPos globalPos = GlobalPos.create(serverWorld.getDimension().getType(), blockPos);
		villagerEntity.getBrain().putMemory(MemoryModuleType.field_18438, globalPos);
	}
}
