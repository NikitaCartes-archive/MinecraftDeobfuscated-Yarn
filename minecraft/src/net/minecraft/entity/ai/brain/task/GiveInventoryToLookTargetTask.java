package net.minecraft.entity.ai.brain.task;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.AllayBrain;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class GiveInventoryToLookTargetTask<E extends LivingEntity & InventoryOwner> extends Task<E> {
	private static final int field_38387 = 3;
	private static final int field_38388 = 100;
	private final Function<LivingEntity, Optional<LookTarget>> lookTargetFunction;
	private final float speed;

	public GiveInventoryToLookTargetTask(Function<LivingEntity, Optional<LookTarget>> lookTargetFunction, float speed) {
		super(
			Map.of(
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
				MemoryModuleState.REGISTERED
			)
		);
		this.lookTargetFunction = lookTargetFunction;
		this.speed = speed;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E entity) {
		return this.hasItemAndTarget(entity);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
		return this.hasItemAndTarget(entity);
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		((Optional)this.lookTargetFunction.apply(entity)).ifPresent(target -> LookTargetUtil.walkTowards(entity, target, this.speed, 3));
	}

	@Override
	protected void keepRunning(ServerWorld world, E entity, long time) {
		Optional<LookTarget> optional = (Optional<LookTarget>)this.lookTargetFunction.apply(entity);
		if (!optional.isEmpty()) {
			LookTarget lookTarget = (LookTarget)optional.get();
			double d = lookTarget.getPos().distanceTo(entity.getEyePos());
			if (d < 3.0) {
				ItemStack itemStack = entity.getInventory().removeStack(0, 1);
				if (!itemStack.isEmpty()) {
					LookTargetUtil.give(entity, itemStack, offsetTarget(lookTarget));
					if (lookTarget instanceof EntityLookTarget entityLookTarget && entityLookTarget.getEntity() instanceof ServerPlayerEntity serverPlayerEntity) {
						Criteria.ITEM_DELIVERED_TO_PLAYER.trigger(serverPlayerEntity);
					}

					if (entity instanceof AllayEntity allayEntity) {
						AllayBrain.getLikedPlayer(allayEntity).ifPresent(player -> this.triggerCriterion(lookTarget, itemStack, player));
					}

					entity.getBrain().remember(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, 100);
				}
			}
		}
	}

	private void triggerCriterion(LookTarget target, ItemStack stack, ServerPlayerEntity player) {
		BlockPos blockPos = target.getBlockPos().down();
		Criteria.ALLAY_DROP_ITEM_ON_BLOCK.trigger(player, blockPos, stack);
	}

	private boolean hasItemAndTarget(E entity) {
		return !entity.getInventory().isEmpty() && ((Optional)this.lookTargetFunction.apply(entity)).isPresent();
	}

	private static Vec3d offsetTarget(LookTarget target) {
		return target.getPos().add(0.0, 1.0, 0.0);
	}
}
