package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;

public class SleepTask extends Task<LivingEntity> {
	private long startTime;

	public SleepTask() {
		super(ImmutableMap.of(MemoryModuleType.field_18438, MemoryModuleState.field_18456, MemoryModuleType.field_20616, MemoryModuleState.field_18458));
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
		if (entity.hasVehicle()) {
			return false;
		} else {
			Brain<?> brain = entity.getBrain();
			GlobalPos globalPos = (GlobalPos)brain.getOptionalMemory(MemoryModuleType.field_18438).get();
			if (world.getRegistryKey() != globalPos.getDimension()) {
				return false;
			} else {
				Optional<Long> optional = brain.getOptionalMemory(MemoryModuleType.field_20616);
				if (optional.isPresent()) {
					long l = world.getTime() - (Long)optional.get();
					if (l > 0L && l < 100L) {
						return false;
					}
				}

				BlockState blockState = world.getBlockState(globalPos.getPos());
				return globalPos.getPos().isWithinDistance(entity.getPos(), 2.0)
					&& blockState.getBlock().isIn(BlockTags.field_16443)
					&& !(Boolean)blockState.get(BedBlock.OCCUPIED);
			}
		}
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
		Optional<GlobalPos> optional = entity.getBrain().getOptionalMemory(MemoryModuleType.field_18438);
		if (!optional.isPresent()) {
			return false;
		} else {
			BlockPos blockPos = ((GlobalPos)optional.get()).getPos();
			return entity.getBrain().hasActivity(Activity.field_18597)
				&& entity.getY() > (double)blockPos.getY() + 0.4
				&& blockPos.isWithinDistance(entity.getPos(), 1.14);
		}
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		if (time > this.startTime) {
			OpenDoorsTask.method_30760(world, entity, null, null);
			entity.sleep(((GlobalPos)entity.getBrain().getOptionalMemory(MemoryModuleType.field_18438).get()).getPos());
		}
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	@Override
	protected void finishRunning(ServerWorld world, LivingEntity entity, long time) {
		if (entity.isSleeping()) {
			entity.wakeUp();
			this.startTime = time + 40L;
		}
	}
}
