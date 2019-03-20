package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.village.PointOfInterestType;

public class ForgetCompletedPointOfInterestTask extends Task<LivingEntity> {
	private final MemoryModuleType<GlobalPos> memoryModuleType;
	private final Predicate<PointOfInterestType> condition;

	public ForgetCompletedPointOfInterestTask(PointOfInterestType pointOfInterestType, MemoryModuleType<GlobalPos> memoryModuleType) {
		this.condition = pointOfInterestType.getCompletedCondition();
		this.memoryModuleType = memoryModuleType;
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(Pair.of(this.memoryModuleType, MemoryModuleState.field_18456));
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		GlobalPos globalPos = (GlobalPos)livingEntity.getBrain().getMemory(this.memoryModuleType).get();
		return Objects.equals(serverWorld.getDimension().getType(), globalPos.getDimension()) && globalPos.getPos().method_19769(livingEntity.getPos(), 3.0);
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		GlobalPos globalPos = (GlobalPos)brain.getMemory(this.memoryModuleType).get();
		ServerWorld serverWorld2 = serverWorld.getServer().getWorld(globalPos.getDimension());
		if (!serverWorld2.getPointOfInterestStorage().test(globalPos.getPos(), this.condition)) {
			brain.forget(this.memoryModuleType);
		}
	}
}
