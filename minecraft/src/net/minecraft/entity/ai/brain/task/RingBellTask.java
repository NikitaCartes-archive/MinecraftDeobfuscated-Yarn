package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class RingBellTask extends Task<LivingEntity> {
	public RingBellTask() {
		super(ImmutableMap.of(MemoryModuleType.field_18440, MemoryModuleState.field_18456));
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		return serverWorld.random.nextFloat() > 0.95F;
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		BlockPos blockPos = ((GlobalPos)brain.getOptionalMemory(MemoryModuleType.field_18440).get()).getPos();
		if (blockPos.isWithinDistance(new BlockPos(livingEntity), 3.0)) {
			BlockState blockState = serverWorld.getBlockState(blockPos);
			if (blockState.getBlock() == Blocks.field_16332) {
				BellBlock bellBlock = (BellBlock)blockState.getBlock();

				for (Direction direction : Direction.Type.field_11062) {
					if (bellBlock.ring(
						serverWorld, blockState, serverWorld.getBlockEntity(blockPos), new BlockHitResult(new Vec3d(0.5, 0.5, 0.5), direction, blockPos, false), null, false
					)) {
						break;
					}
				}
			}
		}
	}
}
