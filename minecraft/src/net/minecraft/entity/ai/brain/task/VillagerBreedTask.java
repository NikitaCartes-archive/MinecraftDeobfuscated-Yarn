package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestType;

public class VillagerBreedTask extends Task<VillagerEntity> {
	private long field_18368;

	public VillagerBreedTask() {
		super(350, 350);
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(
			Pair.of(MemoryModuleType.field_18448, MemoryModuleState.field_18456), Pair.of(MemoryModuleType.field_18442, MemoryModuleState.field_18456)
		);
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
		serverWorld.summonParticle(villagerEntity2, (byte)18);
		serverWorld.summonParticle(villagerEntity, (byte)18);
		int i = 275 + villagerEntity.getRand().nextInt(50);
		this.field_18368 = l + (long)i;
	}

	protected void method_18975(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = this.method_19570(villagerEntity);
		if (!(villagerEntity.squaredDistanceTo(villagerEntity2) > 5.0)) {
			LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2);
			if (l >= this.field_18368) {
				Optional<BlockPos> optional = this.method_19573(serverWorld, villagerEntity);
				if (!optional.isPresent()) {
					serverWorld.summonParticle(villagerEntity2, (byte)13);
					serverWorld.summonParticle(villagerEntity, (byte)13);
					return;
				}

				villagerEntity.consumeAvailableFood();
				villagerEntity2.consumeAvailableFood();
				Optional<VillagerEntity> optional2 = this.method_18970(villagerEntity, villagerEntity2);
				if (optional2.isPresent()) {
					villagerEntity.depleteFood(12);
					villagerEntity2.depleteFood(12);
					this.method_19572(serverWorld, (VillagerEntity)optional2.get(), (BlockPos)optional.get());
				} else {
					serverWorld.getPointOfInterestStorage().releaseTicket((BlockPos)optional.get());
				}
			}

			if (villagerEntity.getRand().nextInt(35) == 0) {
				serverWorld.summonParticle(villagerEntity2, (byte)12);
				serverWorld.summonParticle(villagerEntity, (byte)12);
			}
		}
	}

	protected void method_18976(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		villagerEntity.getBrain().forget(MemoryModuleType.field_18448);
	}

	private VillagerEntity method_19570(VillagerEntity villagerEntity) {
		return (VillagerEntity)villagerEntity.getBrain().getMemory(MemoryModuleType.field_18448).get();
	}

	private boolean method_18972(VillagerEntity villagerEntity) {
		Brain<VillagerEntity> brain = villagerEntity.getBrain();
		if (!brain.getMemory(MemoryModuleType.field_18448).isPresent()) {
			return false;
		} else {
			VillagerEntity villagerEntity2 = this.method_19570(villagerEntity);
			return LookTargetUtil.canSee(brain, MemoryModuleType.field_18448, EntityType.VILLAGER)
				&& villagerEntity.isReadyToBreed()
				&& villagerEntity2.isReadyToBreed();
		}
	}

	private Optional<BlockPos> method_19573(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return serverWorld.getPointOfInterestStorage()
			.getPosition(PointOfInterestType.field_18517.getCompletedCondition(), blockPos -> true, new BlockPos(villagerEntity), 48);
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
			villagerEntity.world.summonParticle(villagerEntity3, (byte)12);
			return Optional.of(villagerEntity3);
		}
	}

	private void method_19572(ServerWorld serverWorld, VillagerEntity villagerEntity, BlockPos blockPos) {
		GlobalPos globalPos = GlobalPos.create(serverWorld.getDimension().getType(), blockPos);
		villagerEntity.getBrain().putMemory(MemoryModuleType.field_18438, globalPos);
	}
}
