package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.LongJumpUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;

public class LongJumpTask<E extends MobEntity> extends MultiTickTask<E> {
	protected static final int MAX_TARGET_SEARCH_TIME = 20;
	private static final int JUMP_WINDUP_TIME = 40;
	protected static final int PATHING_DISTANCE = 8;
	private static final int RUN_TIME = 200;
	private static final List<Integer> RAM_RANGES = Lists.<Integer>newArrayList(65, 70, 75, 80);
	private final UniformIntProvider cooldownRange;
	protected final int verticalRange;
	protected final int horizontalRange;
	protected final float maxRange;
	protected List<LongJumpTask.Target> potentialTargets = Lists.<LongJumpTask.Target>newArrayList();
	protected Optional<Vec3d> startPos = Optional.empty();
	@Nullable
	protected Vec3d currentTarget;
	protected int targetSearchTime;
	protected long targetPickedTime;
	private final Function<E, SoundEvent> entityToSound;
	private final BiPredicate<E, BlockPos> jumpToPredicate;

	public LongJumpTask(UniformIntProvider cooldownRange, int verticalRange, int horizontalRange, float maxRange, Function<E, SoundEvent> entityToSound) {
		this(cooldownRange, verticalRange, horizontalRange, maxRange, entityToSound, LongJumpTask::shouldJumpTo);
	}

	public static <E extends MobEntity> boolean shouldJumpTo(E entity, BlockPos pos) {
		World world = entity.getWorld();
		BlockPos blockPos = pos.down();
		return world.getBlockState(blockPos).isOpaqueFullCube() && entity.getPathfindingPenalty(LandPathNodeMaker.getLandNodeType(entity, pos)) == 0.0F;
	}

	public LongJumpTask(
		UniformIntProvider cooldownRange,
		int verticalRange,
		int horizontalRange,
		float maxRange,
		Function<E, SoundEvent> entityToSound,
		BiPredicate<E, BlockPos> jumpToPredicate
	) {
		super(
			ImmutableMap.of(
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.LONG_JUMP_COOLING_DOWN,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.LONG_JUMP_MID_JUMP,
				MemoryModuleState.VALUE_ABSENT
			),
			200
		);
		this.cooldownRange = cooldownRange;
		this.verticalRange = verticalRange;
		this.horizontalRange = horizontalRange;
		this.maxRange = maxRange;
		this.entityToSound = entityToSound;
		this.jumpToPredicate = jumpToPredicate;
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
		boolean bl = mobEntity.isOnGround()
			&& !mobEntity.isTouchingWater()
			&& !mobEntity.isInLava()
			&& !serverWorld.getBlockState(mobEntity.getBlockPos()).isOf(Blocks.HONEY_BLOCK);
		if (!bl) {
			mobEntity.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, this.cooldownRange.get(serverWorld.random) / 2);
		}

		return bl;
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		boolean bl = this.startPos.isPresent()
			&& ((Vec3d)this.startPos.get()).equals(mobEntity.getPos())
			&& this.targetSearchTime > 0
			&& !mobEntity.isInsideWaterOrBubbleColumn()
			&& (this.currentTarget != null || !this.potentialTargets.isEmpty());
		if (!bl && mobEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.LONG_JUMP_MID_JUMP).isEmpty()) {
			mobEntity.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, this.cooldownRange.get(serverWorld.random) / 2);
			mobEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
		}

		return bl;
	}

	protected void run(ServerWorld serverWorld, E mobEntity, long l) {
		this.currentTarget = null;
		this.targetSearchTime = 20;
		this.startPos = Optional.of(mobEntity.getPos());
		BlockPos blockPos = mobEntity.getBlockPos();
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		this.potentialTargets = (List<LongJumpTask.Target>)BlockPos.stream(
				i - this.horizontalRange, j - this.verticalRange, k - this.horizontalRange, i + this.horizontalRange, j + this.verticalRange, k + this.horizontalRange
			)
			.filter(pos -> !pos.equals(blockPos))
			.map(pos -> new LongJumpTask.Target(pos.toImmutable(), MathHelper.ceil(blockPos.getSquaredDistance(pos))))
			.collect(Collectors.toCollection(Lists::newArrayList));
	}

	protected void keepRunning(ServerWorld serverWorld, E mobEntity, long l) {
		if (this.currentTarget != null) {
			if (l - this.targetPickedTime >= 40L) {
				mobEntity.setYaw(mobEntity.bodyYaw);
				mobEntity.setNoDrag(true);
				double d = this.currentTarget.length();
				double e = d + (double)mobEntity.getJumpBoostVelocityModifier();
				mobEntity.setVelocity(this.currentTarget.multiply(e / d));
				mobEntity.getBrain().remember(MemoryModuleType.LONG_JUMP_MID_JUMP, true);
				serverWorld.playSoundFromEntity(null, mobEntity, (SoundEvent)this.entityToSound.apply(mobEntity), SoundCategory.NEUTRAL, 1.0F, 1.0F);
			}
		} else {
			this.targetSearchTime--;
			this.pickTarget(serverWorld, mobEntity, l);
		}
	}

	protected void pickTarget(ServerWorld world, E entity, long time) {
		while (!this.potentialTargets.isEmpty()) {
			Optional<LongJumpTask.Target> optional = this.removeRandomTarget(world);
			if (!optional.isEmpty()) {
				LongJumpTask.Target target = (LongJumpTask.Target)optional.get();
				BlockPos blockPos = target.getPos();
				if (this.canJumpTo(world, entity, blockPos)) {
					Vec3d vec3d = Vec3d.ofCenter(blockPos);
					Vec3d vec3d2 = this.getJumpingVelocity(entity, vec3d);
					if (vec3d2 != null) {
						entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(blockPos));
						EntityNavigation entityNavigation = entity.getNavigation();
						Path path = entityNavigation.findPathTo(blockPos, 0, 8);
						if (path == null || !path.reachesTarget()) {
							this.currentTarget = vec3d2;
							this.targetPickedTime = time;
							return;
						}
					}
				}
			}
		}
	}

	protected Optional<LongJumpTask.Target> removeRandomTarget(ServerWorld world) {
		Optional<LongJumpTask.Target> optional = Weighting.getRandom(world.random, this.potentialTargets);
		optional.ifPresent(this.potentialTargets::remove);
		return optional;
	}

	private boolean canJumpTo(ServerWorld world, E entity, BlockPos pos) {
		BlockPos blockPos = entity.getBlockPos();
		int i = blockPos.getX();
		int j = blockPos.getZ();
		return i == pos.getX() && j == pos.getZ() ? false : this.jumpToPredicate.test(entity, pos);
	}

	@Nullable
	protected Vec3d getJumpingVelocity(MobEntity entity, Vec3d targetPos) {
		List<Integer> list = Lists.<Integer>newArrayList(RAM_RANGES);
		Collections.shuffle(list);
		float f = (float)(entity.getAttributeValue(EntityAttributes.JUMP_STRENGTH) * (double)this.maxRange);

		for (int i : list) {
			Optional<Vec3d> optional = LongJumpUtil.getJumpingVelocity(entity, targetPos, f, i, true);
			if (optional.isPresent()) {
				return (Vec3d)optional.get();
			}
		}

		return null;
	}

	public static class Target extends Weighted.Absent {
		private final BlockPos pos;

		public Target(BlockPos pos, int weight) {
			super(weight);
			this.pos = pos;
		}

		public BlockPos getPos() {
			return this.pos;
		}
	}
}
