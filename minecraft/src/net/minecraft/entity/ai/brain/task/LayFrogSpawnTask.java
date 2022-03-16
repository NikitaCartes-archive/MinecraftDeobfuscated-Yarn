package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class LayFrogSpawnTask extends Task<FrogEntity> {
	private final Block frogSpawn;
	private final MemoryModuleType<?> field_37441;

	public LayFrogSpawnTask(Block frogSpawn, MemoryModuleType<?> memoryModuleType) {
		super(
			ImmutableMap.of(
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.IS_PREGNANT,
				MemoryModuleState.VALUE_PRESENT
			)
		);
		this.frogSpawn = frogSpawn;
		this.field_37441 = memoryModuleType;
	}

	protected boolean shouldRun(ServerWorld serverWorld, FrogEntity frogEntity) {
		return !frogEntity.isTouchingWater() && frogEntity.isOnGround();
	}

	protected void run(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
		BlockPos blockPos = frogEntity.getBlockPos().down();

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.offset(direction);
			if (serverWorld.getBlockState(blockPos2).isOf(Blocks.WATER)) {
				BlockPos blockPos3 = blockPos2.up();
				if (serverWorld.getBlockState(blockPos3).isAir()) {
					serverWorld.setBlockState(blockPos3, this.frogSpawn.getDefaultState(), Block.NOTIFY_ALL);
					serverWorld.playSoundFromEntity(null, frogEntity, SoundEvents.ENTITY_FROG_LAY_SPAWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
					frogEntity.getBrain().forget(this.field_37441);
					return;
				}
			}
		}
	}
}
