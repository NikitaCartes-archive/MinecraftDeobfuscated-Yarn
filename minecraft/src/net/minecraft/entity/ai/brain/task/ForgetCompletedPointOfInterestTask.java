package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.DebugRendererInfoManager;
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
		BlockPos blockPos = globalPos.getPos();
		ServerWorld serverWorld = world.getServer().getWorld(globalPos.getDimension());
		if (this.hasCompletedPointOfInterest(serverWorld, blockPos)) {
			brain.forget(this.memoryModule);
		} else if (this.isBedOccupiedByOthers(serverWorld, blockPos, entity)) {
			brain.forget(this.memoryModule);
			world.getPointOfInterestStorage().releaseTicket(blockPos);
			DebugRendererInfoManager.sendPointOfInterest(world, blockPos);
		}
	}

	private boolean isBedOccupiedByOthers(ServerWorld world, BlockPos pos, LivingEntity entity) {
		BlockState blockState = world.getBlockState(pos);
		return blockState.getBlock().matches(BlockTags.BEDS) && (Boolean)blockState.get(BedBlock.OCCUPIED) && !entity.isSleeping();
	}

	private boolean hasCompletedPointOfInterest(ServerWorld world, BlockPos pos) {
		return !world.getPointOfInterestStorage().test(pos, this.condition);
	}
}
