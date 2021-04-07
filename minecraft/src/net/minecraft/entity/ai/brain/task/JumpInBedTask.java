package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public class JumpInBedTask extends Task<MobEntity> {
	private static final int MAX_TICKS_OUT_OF_BED = 100;
	private static final int MIN_JUMP_TICKS = 3;
	private static final int JUMP_TIME_VARIANCE = 6;
	private static final int TICKS_TO_NEXT_JUMP = 5;
	private final float walkSpeed;
	@Nullable
	private BlockPos bedPos;
	private int ticksOutOfBedUntilStopped;
	private int jumpsRemaining;
	private int ticksToNextJump;

	public JumpInBedTask(float walkSpeed) {
		super(ImmutableMap.of(MemoryModuleType.NEAREST_BED, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
		this.walkSpeed = walkSpeed;
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
		return mobEntity.isBaby() && this.shouldStartJumping(serverWorld, mobEntity);
	}

	protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		super.run(serverWorld, mobEntity, l);
		this.getNearestBed(mobEntity).ifPresent(pos -> {
			this.bedPos = pos;
			this.ticksOutOfBedUntilStopped = 100;
			this.jumpsRemaining = 3 + serverWorld.random.nextInt(4);
			this.ticksToNextJump = 0;
			this.setWalkTarget(mobEntity, pos);
		});
	}

	protected void finishRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		super.finishRunning(serverWorld, mobEntity, l);
		this.bedPos = null;
		this.ticksOutOfBedUntilStopped = 0;
		this.jumpsRemaining = 0;
		this.ticksToNextJump = 0;
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		return mobEntity.isBaby()
			&& this.bedPos != null
			&& this.isBedAt(serverWorld, this.bedPos)
			&& !this.isBedGoneTooLong(serverWorld, mobEntity)
			&& !this.isDoneJumping(serverWorld, mobEntity);
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	protected void keepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		if (!this.isAboveBed(serverWorld, mobEntity)) {
			this.ticksOutOfBedUntilStopped--;
		} else if (this.ticksToNextJump > 0) {
			this.ticksToNextJump--;
		} else {
			if (this.isOnBed(serverWorld, mobEntity)) {
				mobEntity.getJumpControl().setActive();
				this.jumpsRemaining--;
				this.ticksToNextJump = 5;
			}
		}
	}

	private void setWalkTarget(MobEntity mob, BlockPos pos) {
		mob.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, this.walkSpeed, 0));
	}

	private boolean shouldStartJumping(ServerWorld world, MobEntity mob) {
		return this.isAboveBed(world, mob) || this.getNearestBed(mob).isPresent();
	}

	private boolean isAboveBed(ServerWorld world, MobEntity mob) {
		BlockPos blockPos = mob.getBlockPos();
		BlockPos blockPos2 = blockPos.down();
		return this.isBedAt(world, blockPos) || this.isBedAt(world, blockPos2);
	}

	private boolean isOnBed(ServerWorld world, MobEntity mob) {
		return this.isBedAt(world, mob.getBlockPos());
	}

	private boolean isBedAt(ServerWorld world, BlockPos pos) {
		return world.getBlockState(pos).isIn(BlockTags.BEDS);
	}

	private Optional<BlockPos> getNearestBed(MobEntity mob) {
		return mob.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_BED);
	}

	private boolean isBedGoneTooLong(ServerWorld world, MobEntity mob) {
		return !this.isAboveBed(world, mob) && this.ticksOutOfBedUntilStopped <= 0;
	}

	private boolean isDoneJumping(ServerWorld world, MobEntity mob) {
		return this.isAboveBed(world, mob) && this.jumpsRemaining <= 0;
	}
}
