package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestType;

public class ForgetCompletedPointOfInterestTask extends Task<LivingEntity> {
	private final MemoryModuleType<GlobalPos> memoryModule;
	private final Predicate<PointOfInterestType> condition;

	public ForgetCompletedPointOfInterestTask(PointOfInterestType pointOfInterestType, MemoryModuleType<GlobalPos> memoryModuleType) {
		super(ImmutableMap.of(memoryModuleType, MemoryModuleState.field_18456));
		this.condition = pointOfInterestType.getCompletionCondition();
		this.memoryModule = memoryModuleType;
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		GlobalPos globalPos = (GlobalPos)livingEntity.getBrain().getOptionalMemory(this.memoryModule).get();
		return Objects.equals(serverWorld.getDimension().getType(), globalPos.getDimension()) && globalPos.getPos().isWithinDistance(livingEntity.getPos(), 5.0);
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		GlobalPos globalPos = (GlobalPos)brain.getOptionalMemory(this.memoryModule).get();
		ServerWorld serverWorld2 = serverWorld.getServer().getWorld(globalPos.getDimension());
		if (this.method_20499(serverWorld2, globalPos.getPos()) || this.method_20500(serverWorld2, globalPos.getPos(), livingEntity)) {
			brain.forget(this.memoryModule);
		}
	}

	private boolean method_20500(ServerWorld serverWorld, BlockPos blockPos, LivingEntity livingEntity) {
		BlockState blockState = serverWorld.getBlockState(blockPos);
		return blockState.getBlock().matches(BlockTags.field_16443) && (Boolean)blockState.get(BedBlock.OCCUPIED) && !livingEntity.isSleeping();
	}

	private boolean method_20499(ServerWorld serverWorld, BlockPos blockPos) {
		return !serverWorld.getPointOfInterestStorage().test(blockPos, this.condition);
	}
}
