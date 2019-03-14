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
	private final int field_18367 = 5;
	private long field_18368 = 0L;

	public VillagerBreedTask() {
		super(350, 350);
	}

	protected boolean method_18972(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		Brain<?> brain = villagerEntity.getBrain();
		VillagerEntity villagerEntity2 = (VillagerEntity)brain.getMemory(MemoryModuleType.field_18448).get();
		return villagerEntity.getType() == EntityType.VILLAGER
			&& this.method_18918(brain, MemoryModuleType.field_18448, EntityType.VILLAGER)
			&& villagerEntity.isReadyToBreed()
			&& villagerEntity2.isReadyToBreed();
	}

	protected boolean method_18973(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		Brain<?> brain = villagerEntity.getBrain();
		return l <= this.field_18368
			&& this.method_18918(brain, MemoryModuleType.field_18448, EntityType.VILLAGER)
			&& villagerEntity.isReadyToBreed()
			&& ((VillagerEntity)brain.getMemory(MemoryModuleType.field_18448).get()).isReadyToBreed();
	}

	protected void method_18974(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = (VillagerEntity)villagerEntity.getBrain().getMemory(MemoryModuleType.field_18448).get();
		this.method_18916(villagerEntity, villagerEntity2);
		serverWorld.summonParticle(villagerEntity2, (byte)18);
		serverWorld.summonParticle(villagerEntity, (byte)18);
		this.field_18368 = l + 275L + (long)villagerEntity.getRand().nextInt(50);
	}

	protected void method_18975(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		VillagerEntity villagerEntity2 = (VillagerEntity)villagerEntity.getBrain().getMemory(MemoryModuleType.field_18448).get();
		if (!(villagerEntity.squaredDistanceTo(villagerEntity2) > 5.0)) {
			this.method_18916(villagerEntity, villagerEntity2);
			if (l >= this.field_18368) {
				Optional<BlockPos> optional = serverWorld.getPointOfInterestStorage()
					.getPosition(PointOfInterestType.field_18517.getCompletedCondition(), blockPos -> true, new BlockPos(villagerEntity), 48);
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
					((VillagerEntity)optional2.get())
						.getBrain()
						.putMemory(MemoryModuleType.field_18438, GlobalPos.create(serverWorld.getDimension().getType(), (BlockPos)optional.get()));
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

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(
			Pair.of(MemoryModuleType.field_18448, MemoryModuleState.field_18456), Pair.of(MemoryModuleType.field_18442, MemoryModuleState.field_18456)
		);
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
}
