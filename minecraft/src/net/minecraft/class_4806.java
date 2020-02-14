package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4806 extends Task<AnimalEntity> {
	private final EntityType<? extends AnimalEntity> field_22283;
	private long field_22284;

	public class_4806(EntityType<? extends AnimalEntity> entityType) {
		super(
			ImmutableMap.of(
				MemoryModuleType.VISIBLE_MOBS,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.BREED_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED
			),
			325
		);
		this.field_22283 = entityType;
	}

	protected boolean shouldRun(ServerWorld serverWorld, AnimalEntity animalEntity) {
		return animalEntity.isInLove() && this.method_24548(animalEntity).isPresent();
	}

	protected void run(ServerWorld serverWorld, AnimalEntity animalEntity, long l) {
		AnimalEntity animalEntity2 = (AnimalEntity)this.method_24548(animalEntity).get();
		animalEntity.getBrain().putMemory(MemoryModuleType.BREED_TARGET, animalEntity2);
		animalEntity2.getBrain().putMemory(MemoryModuleType.BREED_TARGET, animalEntity);
		LookTargetUtil.lookAtAndWalkTowardsEachOther(animalEntity, animalEntity2);
		int i = 275 + animalEntity.getRandom().nextInt(50);
		this.field_22284 = l + (long)i;
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, AnimalEntity animalEntity, long l) {
		if (!this.method_24546(animalEntity)) {
			return false;
		} else {
			AnimalEntity animalEntity2 = this.method_24542(animalEntity);
			return animalEntity2.isAlive()
				&& animalEntity.canBreedWith(animalEntity2)
				&& LookTargetUtil.canSee(animalEntity.getBrain(), animalEntity2)
				&& l <= this.field_22284;
		}
	}

	protected void keepRunning(ServerWorld serverWorld, AnimalEntity animalEntity, long l) {
		AnimalEntity animalEntity2 = this.method_24542(animalEntity);
		LookTargetUtil.lookAtAndWalkTowardsEachOther(animalEntity, animalEntity2);
		if (animalEntity.method_24516(animalEntity2, 3.0)) {
			if (l >= this.field_22284) {
				animalEntity.method_24650(serverWorld, animalEntity2);
				animalEntity.getBrain().forget(MemoryModuleType.BREED_TARGET);
				animalEntity2.getBrain().forget(MemoryModuleType.BREED_TARGET);
			}
		}
	}

	protected void finishRunning(ServerWorld serverWorld, AnimalEntity animalEntity, long l) {
		animalEntity.getBrain().forget(MemoryModuleType.BREED_TARGET);
		animalEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
		animalEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
		this.field_22284 = 0L;
	}

	private AnimalEntity method_24542(AnimalEntity animalEntity) {
		return (AnimalEntity)animalEntity.getBrain().getOptionalMemory(MemoryModuleType.BREED_TARGET).get();
	}

	private boolean method_24546(AnimalEntity animalEntity) {
		Brain<?> brain = animalEntity.getBrain();
		return brain.hasMemoryModule(MemoryModuleType.BREED_TARGET)
			&& ((PassiveEntity)brain.getOptionalMemory(MemoryModuleType.BREED_TARGET).get()).getType() == this.field_22283;
	}

	private Optional<? extends AnimalEntity> method_24548(AnimalEntity animalEntity) {
		return ((List)animalEntity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get())
			.stream()
			.filter(livingEntity -> livingEntity.getType() == this.field_22283)
			.map(livingEntity -> (AnimalEntity)livingEntity)
			.filter(animalEntity::canBreedWith)
			.findFirst();
	}
}
