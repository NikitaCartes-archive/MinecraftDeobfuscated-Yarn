package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;

public class SleepTask extends Task<LivingEntity> {
	private long field_18848;

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		GlobalPos globalPos = (GlobalPos)livingEntity.getBrain().getMemory(MemoryModuleType.field_18438).get();
		if (!Objects.equals(serverWorld.getDimension().getType(), globalPos.getDimension())) {
			return false;
		} else {
			BlockState blockState = serverWorld.getBlockState(globalPos.getPos());
			return globalPos.getPos().method_19769(livingEntity.getPos(), 2.0)
				&& blockState.getBlock().matches(BlockTags.field_16443)
				&& !(Boolean)blockState.get(BedBlock.OCCUPIED);
		}
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(Pair.of(MemoryModuleType.field_18438, MemoryModuleState.field_18456));
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Optional<GlobalPos> optional = livingEntity.getBrain().getMemory(MemoryModuleType.field_18438);
		if (!optional.isPresent()) {
			return false;
		} else {
			BlockPos blockPos = ((GlobalPos)optional.get()).getPos();
			return livingEntity.getBrain().hasActivity(Activity.field_18597)
				&& livingEntity.y > (double)blockPos.getY() + 0.4
				&& blockPos.method_19769(livingEntity.getPos(), 1.14);
		}
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		if (l > this.field_18848) {
			livingEntity.sleep(((GlobalPos)livingEntity.getBrain().getMemory(MemoryModuleType.field_18438).get()).getPos());
		}
	}

	@Override
	protected boolean isTimeLimitExceeded(long l) {
		return false;
	}

	@Override
	protected void stopInternal(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		if (livingEntity.isSleeping()) {
			livingEntity.wakeUp();
			this.field_18848 = l + 40L;
		}
	}
}
