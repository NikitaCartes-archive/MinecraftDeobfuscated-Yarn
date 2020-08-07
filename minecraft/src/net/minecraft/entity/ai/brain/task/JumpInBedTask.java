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
	private final float walkSpeed;
	@Nullable
	private BlockPos bedPos;
	private int ticksOutOfBedUntilStopped;
	private int jumpsRemaining;
	private int ticksToNextJump;

	public JumpInBedTask(float walkSpeed) {
		super(ImmutableMap.of(MemoryModuleType.field_19007, MemoryModuleState.field_18456, MemoryModuleType.field_18445, MemoryModuleState.field_18457));
		this.walkSpeed = walkSpeed;
	}

	protected boolean method_19971(ServerWorld serverWorld, MobEntity mobEntity) {
		return mobEntity.isBaby() && this.shouldStartJumping(serverWorld, mobEntity);
	}

	protected void method_19972(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		super.run(serverWorld, mobEntity, l);
		this.getNearestBed(mobEntity).ifPresent(blockPos -> {
			this.bedPos = blockPos;
			this.ticksOutOfBedUntilStopped = 100;
			this.jumpsRemaining = 3 + serverWorld.random.nextInt(4);
			this.ticksToNextJump = 0;
			this.setWalkTarget(mobEntity, blockPos);
		});
	}

	protected void method_19976(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		super.finishRunning(serverWorld, mobEntity, l);
		this.bedPos = null;
		this.ticksOutOfBedUntilStopped = 0;
		this.jumpsRemaining = 0;
		this.ticksToNextJump = 0;
	}

	protected boolean method_19978(ServerWorld serverWorld, MobEntity mobEntity, long l) {
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

	protected void method_19980(ServerWorld serverWorld, MobEntity mobEntity, long l) {
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
		mob.getBrain().remember(MemoryModuleType.field_18445, new WalkTarget(pos, this.walkSpeed, 0));
	}

	private boolean shouldStartJumping(ServerWorld world, MobEntity mob) {
		return this.isAboveBed(world, mob) || this.getNearestBed(mob).isPresent();
	}

	private boolean isAboveBed(ServerWorld world, MobEntity mob) {
		BlockPos blockPos = mob.getBlockPos();
		BlockPos blockPos2 = blockPos.method_10074();
		return this.isBedAt(world, blockPos) || this.isBedAt(world, blockPos2);
	}

	private boolean isOnBed(ServerWorld world, MobEntity mob) {
		return this.isBedAt(world, mob.getBlockPos());
	}

	private boolean isBedAt(ServerWorld world, BlockPos pos) {
		return world.getBlockState(pos).isIn(BlockTags.field_16443);
	}

	private Optional<BlockPos> getNearestBed(MobEntity mob) {
		return mob.getBrain().getOptionalMemory(MemoryModuleType.field_19007);
	}

	private boolean isBedGoneTooLong(ServerWorld world, MobEntity mob) {
		return !this.isAboveBed(world, mob) && this.ticksOutOfBedUntilStopped <= 0;
	}

	private boolean isDoneJumping(ServerWorld world, MobEntity mob) {
		return this.isAboveBed(world, mob) && this.jumpsRemaining <= 0;
	}
}
