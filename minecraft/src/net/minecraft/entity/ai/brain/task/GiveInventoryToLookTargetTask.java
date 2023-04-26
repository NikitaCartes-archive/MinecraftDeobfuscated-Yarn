package net.minecraft.entity.ai.brain.task;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.AllayBrain;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GiveInventoryToLookTargetTask<E extends LivingEntity & InventoryOwner> extends MultiTickTask<E> {
	private static final int COMPLETION_RANGE = 3;
	private static final int ITEM_PICKUP_COOLDOWN_TICKS = 60;
	private final Function<LivingEntity, Optional<LookTarget>> lookTargetFunction;
	private final float speed;

	public GiveInventoryToLookTargetTask(Function<LivingEntity, Optional<LookTarget>> lookTargetFunction, float speed, int runTime) {
		super(
			Map.of(
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
				MemoryModuleState.REGISTERED
			),
			runTime
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
					playThrowSound(entity, itemStack, offsetTarget(lookTarget));
					if (entity instanceof AllayEntity allayEntity) {
						AllayBrain.getLikedPlayer(allayEntity).ifPresent(player -> this.triggerCriterion(lookTarget, itemStack, player));
					}

					entity.getBrain().remember(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, 60);
				}
			}
		}
	}

	private void triggerCriterion(LookTarget target, ItemStack stack, ServerPlayerEntity player) {
		BlockPos blockPos = target.getBlockPos().down();
		Criteria.ALLAY_DROP_ITEM_ON_BLOCK.trigger(player, blockPos, stack);
	}

	private boolean hasItemAndTarget(E entity) {
		if (entity.getInventory().isEmpty()) {
			return false;
		} else {
			Optional<LookTarget> optional = (Optional<LookTarget>)this.lookTargetFunction.apply(entity);
			return optional.isPresent();
		}
	}

	private static Vec3d offsetTarget(LookTarget target) {
		return target.getPos().add(0.0, 1.0, 0.0);
	}

	public static void playThrowSound(LivingEntity entity, ItemStack stack, Vec3d target) {
		Vec3d vec3d = new Vec3d(0.2F, 0.3F, 0.2F);
		LookTargetUtil.give(entity, stack, target, vec3d, 0.2F);
		World world = entity.getWorld();
		if (world.getTime() % 7L == 0L && world.random.nextDouble() < 0.9) {
			float f = Util.<Float>getRandom(AllayEntity.THROW_SOUND_PITCHES, world.getRandom());
			world.playSoundFromEntity(null, entity, SoundEvents.ENTITY_ALLAY_ITEM_THROWN, SoundCategory.NEUTRAL, 1.0F, f);
		}
	}
}
