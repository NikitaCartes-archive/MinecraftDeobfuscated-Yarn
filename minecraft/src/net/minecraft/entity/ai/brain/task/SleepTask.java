package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Objects;
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
import net.minecraft.util.GlobalPos;
import net.minecraft.util.Timestamp;
import net.minecraft.util.math.BlockPos;

public class SleepTask extends Task<LivingEntity> {
	private long field_18848;

	public SleepTask() {
		super(ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.LAST_WOKEN, MemoryModuleState.REGISTERED));
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		if (livingEntity.hasVehicle()) {
			return false;
		} else {
			Brain<?> brain = livingEntity.getBrain();
			GlobalPos globalPos = (GlobalPos)brain.getOptionalMemory(MemoryModuleType.HOME).get();
			if (!Objects.equals(serverWorld.getDimension().getType(), globalPos.getDimension())) {
				return false;
			} else {
				Optional<Timestamp> optional = brain.getOptionalMemory(MemoryModuleType.LAST_WOKEN);
				if (optional.isPresent() && serverWorld.getTime() - ((Timestamp)optional.get()).getTime() < 100L) {
					return false;
				} else {
					BlockState blockState = serverWorld.getBlockState(globalPos.getPos());
					return globalPos.getPos().isWithinDistance(livingEntity.getPos(), 2.0)
						&& blockState.getBlock().matches(BlockTags.BEDS)
						&& !(Boolean)blockState.get(BedBlock.OCCUPIED);
				}
			}
		}
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Optional<GlobalPos> optional = livingEntity.getBrain().getOptionalMemory(MemoryModuleType.HOME);
		if (!optional.isPresent()) {
			return false;
		} else {
			BlockPos blockPos = ((GlobalPos)optional.get()).getPos();
			return livingEntity.getBrain().hasActivity(Activity.REST)
				&& livingEntity.y > (double)blockPos.getY() + 0.4
				&& blockPos.isWithinDistance(livingEntity.getPos(), 1.14);
		}
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		if (l > this.field_18848) {
			livingEntity.getBrain()
				.getOptionalMemory(MemoryModuleType.OPENED_DOORS)
				.ifPresent(set -> OpenDoorsTask.method_21697(serverWorld, ImmutableList.of(), 0, livingEntity, livingEntity.getBrain()));
			livingEntity.sleep(((GlobalPos)livingEntity.getBrain().getOptionalMemory(MemoryModuleType.HOME).get()).getPos());
		}
	}

	@Override
	protected boolean isTimeLimitExceeded(long l) {
		return false;
	}

	@Override
	protected void finishRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		if (livingEntity.isSleeping()) {
			livingEntity.wakeUp();
			this.field_18848 = l + 40L;
		}
	}
}
