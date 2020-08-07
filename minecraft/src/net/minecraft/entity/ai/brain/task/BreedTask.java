package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;

public class BreedTask extends Task<AnimalEntity> {
	private final EntityType<? extends AnimalEntity> targetType;
	private final float field_23129;
	private long breedTime;

	public BreedTask(EntityType<? extends AnimalEntity> targetType, float f) {
		super(
			ImmutableMap.of(
				MemoryModuleType.field_18442,
				MemoryModuleState.field_18456,
				MemoryModuleType.field_18448,
				MemoryModuleState.field_18457,
				MemoryModuleType.field_18445,
				MemoryModuleState.field_18458,
				MemoryModuleType.field_18446,
				MemoryModuleState.field_18458
			),
			325
		);
		this.targetType = targetType;
		this.field_23129 = f;
	}

	protected boolean method_24543(ServerWorld serverWorld, AnimalEntity animalEntity) {
		return animalEntity.isInLove() && this.findBreedTarget(animalEntity).isPresent();
	}

	protected void method_24544(ServerWorld serverWorld, AnimalEntity animalEntity, long l) {
		AnimalEntity animalEntity2 = (AnimalEntity)this.findBreedTarget(animalEntity).get();
		animalEntity.getBrain().remember(MemoryModuleType.field_18448, animalEntity2);
		animalEntity2.getBrain().remember(MemoryModuleType.field_18448, animalEntity);
		LookTargetUtil.lookAtAndWalkTowardsEachOther(animalEntity, animalEntity2, this.field_23129);
		int i = 275 + animalEntity.getRandom().nextInt(50);
		this.breedTime = l + (long)i;
	}

	protected boolean method_24547(ServerWorld serverWorld, AnimalEntity animalEntity, long l) {
		if (!this.hasBreedTarget(animalEntity)) {
			return false;
		} else {
			AnimalEntity animalEntity2 = this.getBreedTarget(animalEntity);
			return animalEntity2.isAlive()
				&& animalEntity.canBreedWith(animalEntity2)
				&& LookTargetUtil.canSee(animalEntity.getBrain(), animalEntity2)
				&& l <= this.breedTime;
		}
	}

	protected void method_24549(ServerWorld serverWorld, AnimalEntity animalEntity, long l) {
		AnimalEntity animalEntity2 = this.getBreedTarget(animalEntity);
		LookTargetUtil.lookAtAndWalkTowardsEachOther(animalEntity, animalEntity2, this.field_23129);
		if (animalEntity.isInRange(animalEntity2, 3.0)) {
			if (l >= this.breedTime) {
				animalEntity.breed(serverWorld, animalEntity2);
				animalEntity.getBrain().forget(MemoryModuleType.field_18448);
				animalEntity2.getBrain().forget(MemoryModuleType.field_18448);
			}
		}
	}

	protected void method_24550(ServerWorld serverWorld, AnimalEntity animalEntity, long l) {
		animalEntity.getBrain().forget(MemoryModuleType.field_18448);
		animalEntity.getBrain().forget(MemoryModuleType.field_18445);
		animalEntity.getBrain().forget(MemoryModuleType.field_18446);
		this.breedTime = 0L;
	}

	private AnimalEntity getBreedTarget(AnimalEntity animal) {
		return (AnimalEntity)animal.getBrain().getOptionalMemory(MemoryModuleType.field_18448).get();
	}

	private boolean hasBreedTarget(AnimalEntity animal) {
		Brain<?> brain = animal.getBrain();
		return brain.hasMemoryModule(MemoryModuleType.field_18448)
			&& ((PassiveEntity)brain.getOptionalMemory(MemoryModuleType.field_18448).get()).getType() == this.targetType;
	}

	private Optional<? extends AnimalEntity> findBreedTarget(AnimalEntity animal) {
		return ((List)animal.getBrain().getOptionalMemory(MemoryModuleType.field_18442).get())
			.stream()
			.filter(livingEntity -> livingEntity.getType() == this.targetType)
			.map(livingEntity -> (AnimalEntity)livingEntity)
			.filter(animal::canBreedWith)
			.findFirst();
	}
}
