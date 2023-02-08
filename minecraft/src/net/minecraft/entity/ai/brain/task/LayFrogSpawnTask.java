package net.minecraft.entity.ai.brain.task;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.GameEvent;

public class LayFrogSpawnTask {
	public static Task<LivingEntity> create(Block frogSpawn) {
		return TaskTriggerer.task(
			context -> context.group(
						context.queryMemoryAbsent(MemoryModuleType.ATTACK_TARGET),
						context.queryMemoryValue(MemoryModuleType.WALK_TARGET),
						context.queryMemoryValue(MemoryModuleType.IS_PREGNANT)
					)
					.apply(
						context,
						(attackTarget, walkTarget, isPregnant) -> (world, entity, time) -> {
								if (!entity.isTouchingWater() && entity.isOnGround()) {
									BlockPos blockPos = entity.getBlockPos().down();

									for (Direction direction : Direction.Type.HORIZONTAL) {
										BlockPos blockPos2 = blockPos.offset(direction);
										if (world.getBlockState(blockPos2).getCollisionShape(world, blockPos2).getFace(Direction.UP).isEmpty()
											&& world.getFluidState(blockPos2).isOf(Fluids.WATER)) {
											BlockPos blockPos3 = blockPos2.up();
											if (world.getBlockState(blockPos3).isAir()) {
												BlockState blockState = frogSpawn.getDefaultState();
												world.setBlockState(blockPos3, blockState, Block.NOTIFY_ALL);
												world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos3, GameEvent.Emitter.of(entity, blockState));
												world.playSoundFromEntity(null, entity, SoundEvents.ENTITY_FROG_LAY_SPAWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
												isPregnant.forget();
												return true;
											}
										}
									}

									return true;
								} else {
									return false;
								}
							}
					)
		);
	}
}
