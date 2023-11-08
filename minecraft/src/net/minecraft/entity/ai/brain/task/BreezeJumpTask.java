package net.minecraft.entity.ai.brain.task;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.LongJumpUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;

public class BreezeJumpTask extends MultiTickTask<BreezeEntity> {
	private static final int REQUIRED_SPACE_ABOVE = 4;
	private static final double MAX_JUMP_DISTANCE = 50.0;
	private static final int JUMP_COOLDOWN_EXPIRY = 10;
	private static final int JUMP_COOLDOWN_EXPIRY_WHEN_HURT = 2;
	private static final int JUMP_INHALING_EXPIRY = Math.round(10.0F);
	private static final float MAX_JUMP_VELOCITY = 1.4F;
	private static final ObjectArrayList<Integer> POSSIBLE_JUMP_ANGLES = new ObjectArrayList<>(Lists.newArrayList(40, 55, 60, 75, 80));

	@VisibleForTesting
	public BreezeJumpTask() {
		super(
			Map.of(
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.BREEZE_JUMP_COOLDOWN,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.BREEZE_JUMP_INHALING,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.BREEZE_JUMP_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.BREEZE_SHOOT,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_ABSENT
			),
			200
		);
	}

	protected boolean shouldRun(ServerWorld serverWorld, BreezeEntity breezeEntity) {
		if (!breezeEntity.isOnGround() && !breezeEntity.isTouchingWater()) {
			return false;
		} else if (breezeEntity.getBrain().isMemoryInState(MemoryModuleType.BREEZE_JUMP_TARGET, MemoryModuleState.VALUE_PRESENT)) {
			return true;
		} else {
			LivingEntity livingEntity = (LivingEntity)breezeEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
			if (livingEntity == null) {
				return false;
			} else if (isTargetOutOfRange(breezeEntity, livingEntity)) {
				breezeEntity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
				return false;
			} else if (isTargetTooClose(breezeEntity, livingEntity)) {
				return false;
			} else if (!hasRoomToJump(serverWorld, breezeEntity)) {
				return false;
			} else {
				BlockPos blockPos = getPosToJumpTo(breezeEntity, getRandomPosBehindTarget(livingEntity, breezeEntity.getRandom()));
				if (blockPos == null) {
					return false;
				} else if (!canJumpTo(breezeEntity, blockPos.toCenterPos()) && !canJumpTo(breezeEntity, blockPos.up(4).toCenterPos())) {
					return false;
				} else {
					breezeEntity.getBrain().remember(MemoryModuleType.BREEZE_JUMP_TARGET, blockPos);
					return true;
				}
			}
		}
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, BreezeEntity breezeEntity, long l) {
		return breezeEntity.getPose() != EntityPose.STANDING && !breezeEntity.getBrain().hasMemoryModule(MemoryModuleType.BREEZE_JUMP_COOLDOWN);
	}

	protected void run(ServerWorld serverWorld, BreezeEntity breezeEntity, long l) {
		if (breezeEntity.getBrain().isMemoryInState(MemoryModuleType.BREEZE_JUMP_INHALING, MemoryModuleState.VALUE_ABSENT)) {
			breezeEntity.getBrain().remember(MemoryModuleType.BREEZE_JUMP_INHALING, Unit.INSTANCE, (long)JUMP_INHALING_EXPIRY);
		}

		breezeEntity.setPose(EntityPose.INHALING);
		breezeEntity.getBrain()
			.getOptionalRegisteredMemory(MemoryModuleType.BREEZE_JUMP_TARGET)
			.ifPresent(jumpTarget -> breezeEntity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, jumpTarget.toCenterPos()));
	}

	protected void keepRunning(ServerWorld serverWorld, BreezeEntity breezeEntity, long l) {
		if (shouldStopInhalingPose(breezeEntity)) {
			Vec3d vec3d = (Vec3d)breezeEntity.getBrain()
				.getOptionalRegisteredMemory(MemoryModuleType.BREEZE_JUMP_TARGET)
				.flatMap(jumpTarget -> getJumpingVelocity(breezeEntity, breezeEntity.getRandom(), Vec3d.ofBottomCenter(jumpTarget)))
				.orElse(null);
			if (vec3d == null) {
				breezeEntity.setPose(EntityPose.STANDING);
				return;
			}

			breezeEntity.playSound(SoundEvents.ENTITY_BREEZE_JUMP, 1.0F, 1.0F);
			breezeEntity.setPose(EntityPose.LONG_JUMPING);
			breezeEntity.setYaw(breezeEntity.bodyYaw);
			breezeEntity.setNoDrag(true);
			breezeEntity.setVelocity(vec3d);
		} else if (shouldStopLongJumpingPose(breezeEntity)) {
			breezeEntity.playSound(SoundEvents.ENTITY_BREEZE_LAND, 1.0F, 1.0F);
			breezeEntity.setPose(EntityPose.STANDING);
			breezeEntity.setNoDrag(false);
			boolean bl = breezeEntity.getBrain().hasMemoryModule(MemoryModuleType.HURT_BY);
			breezeEntity.getBrain().remember(MemoryModuleType.BREEZE_JUMP_COOLDOWN, Unit.INSTANCE, bl ? 2L : 10L);
			breezeEntity.getBrain().remember(MemoryModuleType.BREEZE_SHOOT, Unit.INSTANCE, 100L);
		}
	}

	protected void finishRunning(ServerWorld serverWorld, BreezeEntity breezeEntity, long l) {
		if (breezeEntity.getPose() == EntityPose.LONG_JUMPING || breezeEntity.getPose() == EntityPose.INHALING) {
			breezeEntity.setPose(EntityPose.STANDING);
		}

		breezeEntity.getBrain().forget(MemoryModuleType.BREEZE_JUMP_TARGET);
		breezeEntity.getBrain().forget(MemoryModuleType.BREEZE_JUMP_INHALING);
	}

	private static boolean shouldStopInhalingPose(BreezeEntity breeze) {
		return breeze.getBrain().getOptionalRegisteredMemory(MemoryModuleType.BREEZE_JUMP_INHALING).isEmpty() && breeze.getPose() == EntityPose.INHALING;
	}

	private static boolean shouldStopLongJumpingPose(BreezeEntity breeze) {
		return breeze.getPose() == EntityPose.LONG_JUMPING && breeze.isOnGround();
	}

	private static Vec3d getRandomPosBehindTarget(LivingEntity target, Random random) {
		int i = 90;
		float f = target.headYaw + 180.0F + (float)random.nextGaussian() * 90.0F / 2.0F;
		float g = MathHelper.lerp(random.nextFloat(), 4.0F, 8.0F);
		Vec3d vec3d = Vec3d.fromPolar(0.0F, f).multiply((double)g);
		return target.getPos().add(vec3d);
	}

	@Nullable
	private static BlockPos getPosToJumpTo(LivingEntity breeze, Vec3d pos) {
		RaycastContext raycastContext = new RaycastContext(
			pos, pos.offset(Direction.DOWN, 10.0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, breeze
		);
		HitResult hitResult = breeze.getWorld().raycast(raycastContext);
		if (hitResult.getType() == HitResult.Type.BLOCK) {
			return BlockPos.ofFloored(hitResult.getPos()).up();
		} else {
			RaycastContext raycastContext2 = new RaycastContext(
				pos, pos.offset(Direction.UP, 10.0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, breeze
			);
			HitResult hitResult2 = breeze.getWorld().raycast(raycastContext2);
			return hitResult2.getType() == HitResult.Type.BLOCK ? BlockPos.ofFloored(hitResult.getPos()).up() : null;
		}
	}

	@VisibleForTesting
	public static boolean canJumpTo(BreezeEntity breeze, Vec3d jumpPos) {
		Vec3d vec3d = new Vec3d(breeze.getX(), breeze.getY(), breeze.getZ());
		return jumpPos.distanceTo(vec3d) > 50.0
			? false
			: breeze.getWorld().raycast(new RaycastContext(vec3d, jumpPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, breeze)).getType()
				== HitResult.Type.MISS;
	}

	private static boolean isTargetOutOfRange(BreezeEntity breeze, LivingEntity target) {
		return !target.isInRange(breeze, 24.0);
	}

	private static boolean isTargetTooClose(BreezeEntity breeze, LivingEntity target) {
		return target.distanceTo(breeze) - 4.0F <= 0.0F;
	}

	private static boolean hasRoomToJump(ServerWorld world, BreezeEntity breeze) {
		BlockPos blockPos = breeze.getBlockPos();

		for (int i = 1; i <= 4; i++) {
			BlockPos blockPos2 = blockPos.offset(Direction.UP, i);
			if (!world.getBlockState(blockPos2).isAir() && !world.getFluidState(blockPos2).isIn(FluidTags.WATER)) {
				return false;
			}
		}

		return true;
	}

	private static Optional<Vec3d> getJumpingVelocity(BreezeEntity breeze, Random random, Vec3d jumpTarget) {
		for (int i : Util.copyShuffled(POSSIBLE_JUMP_ANGLES, random)) {
			Optional<Vec3d> optional = LongJumpUtil.getJumpingVelocity(breeze, jumpTarget, 1.4F, i, false);
			if (optional.isPresent()) {
				return optional;
			}
		}

		return Optional.empty();
	}
}
