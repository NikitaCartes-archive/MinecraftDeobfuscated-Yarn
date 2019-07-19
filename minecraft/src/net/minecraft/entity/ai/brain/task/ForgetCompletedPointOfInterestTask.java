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
import net.minecraft.world.poi.PointOfInterestType;

public class ForgetCompletedPointOfInterestTask extends Task<LivingEntity> {
	private final MemoryModuleType<GlobalPos> memoryModule;
	private final Predicate<PointOfInterestType> condition;

	public ForgetCompletedPointOfInterestTask(PointOfInterestType poiType, MemoryModuleType<GlobalPos> memoryModule) {
		super(ImmutableMap.of(memoryModule, MemoryModuleState.VALUE_PRESENT));
		this.condition = poiType.getCompletionCondition();
		this.memoryModule = memoryModule;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
		GlobalPos globalPos = (GlobalPos)entity.getBrain().getOptionalMemory(this.memoryModule).get();
		return Objects.equals(world.getDimension().getType(), globalPos.getDimension()) && globalPos.getPos().isWithinDistance(entity.getPos(), 5.0);
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		Brain<?> brain = entity.getBrain();
		GlobalPos globalPos = (GlobalPos)brain.getOptionalMemory(this.memoryModule).get();
		ServerWorld serverWorld = world.getServer().getWorld(globalPos.getDimension());
		if (this.method_20499(serverWorld, globalPos.getPos()) || this.method_20500(serverWorld, globalPos.getPos(), entity)) {
			brain.forget(this.memoryModule);
		}
	}

	private boolean method_20500(ServerWorld serverWorld, BlockPos blockPos, LivingEntity livingEntity) {
		BlockState blockState = serverWorld.getBlockState(blockPos);
		return blockState.getBlock().matches(BlockTags.BEDS) && (Boolean)blockState.get(BedBlock.OCCUPIED) && !livingEntity.isSleeping();
	}

	private boolean method_20499(ServerWorld serverWorld, BlockPos blockPos) {
		return !serverWorld.getPointOfInterestStorage().test(blockPos, this.condition);
	}
}
