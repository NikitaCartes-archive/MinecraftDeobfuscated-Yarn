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

public class GiveInventoryToLookTargetTask<E extends LivingEntity & InventoryOwner> extends Task<E> {
	private static final int field_38387 = 3;
	private static final int field_38388 = 60;
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
					method_43393(entity, itemStack, offsetTarget(lookTarget));
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
			return optional.isPresent() && ((LookTarget)optional.get()).isSeenBy(entity);
		}
	}

	private static Vec3d offsetTarget(LookTarget target) {
		return target.getPos().add(0.0, 1.0, 0.0);
	}

	public static void method_43393(LivingEntity livingEntity, ItemStack itemStack, Vec3d vec3d) {
		Vec3d vec3d2 = new Vec3d(0.2F, 0.3F, 0.2F);
		LookTargetUtil.method_43392(livingEntity, itemStack, vec3d, vec3d2, 0.2F);
		World world = livingEntity.world;
		if (world.getTime() % 7L == 0L && world.random.nextDouble() < 0.9) {
			float f = Util.<Float>getRandom(AllayEntity.field_38937, world.getRandom());
			world.playSoundFromEntity(null, livingEntity, SoundEvents.ENTITY_ALLAY_ITEM_THROWN, SoundCategory.NEUTRAL, 1.0F, f);
		}
	}
}
