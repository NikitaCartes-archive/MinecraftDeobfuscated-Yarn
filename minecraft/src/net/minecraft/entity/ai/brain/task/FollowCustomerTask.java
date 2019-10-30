package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class FollowCustomerTask extends Task<VillagerEntity> {
	private final float speed;

	public FollowCustomerTask(float speed) {
		super(
			ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED), Integer.MAX_VALUE
		);
		this.speed = speed;
	}

	protected boolean method_18954(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		PlayerEntity playerEntity = villagerEntity.getCurrentCustomer();
		return villagerEntity.isAlive()
			&& playerEntity != null
			&& !villagerEntity.isInsideWater()
			&& !villagerEntity.velocityModified
			&& villagerEntity.squaredDistanceTo(playerEntity) <= 16.0
			&& playerEntity.container != null;
	}

	protected boolean method_18955(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.method_18954(serverWorld, villagerEntity);
	}

	protected void method_18956(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		this.update(villagerEntity);
	}

	protected void method_18957(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		Brain<?> brain = villagerEntity.getBrain();
		brain.forget(MemoryModuleType.WALK_TARGET);
		brain.forget(MemoryModuleType.LOOK_TARGET);
	}

	protected void method_18958(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		this.update(villagerEntity);
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	private void update(VillagerEntity villager) {
		EntityPosWrapper entityPosWrapper = new EntityPosWrapper(villager.getCurrentCustomer());
		Brain<?> brain = villager.getBrain();
		brain.putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(entityPosWrapper, this.speed, 2));
		brain.putMemory(MemoryModuleType.LOOK_TARGET, entityPosWrapper);
	}
}
