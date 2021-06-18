package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class LongJumpTask<E extends MobEntity> extends Task<E> {
	private static final int MAX_COOLDOWN = 20;
	private static final int TARGET_RETAIN_TIME = 40;
	private static final int PATHING_DISTANCE = 8;
	public static final int RUN_TIME = 200;
	private final UniformIntProvider cooldownRange;
	private final int verticalRange;
	private final int horizontalRange;
	private final float maxRange;
	private final List<LongJumpTask.Target> targets = new ArrayList();
	private Optional<Vec3d> lastPos = Optional.empty();
	private Optional<LongJumpTask.Target> lastTarget = Optional.empty();
	private int cooldown;
	private long targetTime;
	private Function<E, SoundEvent> field_33460;

	public LongJumpTask(UniformIntProvider cooldownRange, int verticalRange, int horizontalRange, float maxRange, Function<E, SoundEvent> function) {
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
		this.field_33460 = function;
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
		return mobEntity.isOnGround() && !serverWorld.getBlockState(mobEntity.getBlockPos()).isOf(Blocks.HONEY_BLOCK);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		boolean bl = this.lastPos.isPresent()
			&& ((Vec3d)this.lastPos.get()).equals(mobEntity.getPos())
			&& this.cooldown > 0
			&& (this.lastTarget.isPresent() || !this.targets.isEmpty());
		if (!bl && !mobEntity.getBrain().getOptionalMemory(MemoryModuleType.LONG_JUMP_MID_JUMP).isPresent()) {
			mobEntity.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, this.cooldownRange.get(serverWorld.random) / 2);
		}

		return bl;
	}

	protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		this.lastTarget = Optional.empty();
		this.cooldown = 20;
		this.targets.clear();
		this.lastPos = Optional.of(mobEntity.getPos());
		BlockPos blockPos = mobEntity.getBlockPos();
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		Iterable<BlockPos> iterable = BlockPos.iterate(
			i - this.horizontalRange, j - this.verticalRange, k - this.horizontalRange, i + this.horizontalRange, j + this.verticalRange, k + this.horizontalRange
		);
		EntityNavigation entityNavigation = mobEntity.getNavigation();

		for (BlockPos blockPos2 : iterable) {
			double d = blockPos2.getSquaredDistance(blockPos);
			if ((i != blockPos2.getX() || k != blockPos2.getZ())
				&& entityNavigation.isValidPosition(blockPos2)
				&& mobEntity.getPathfindingPenalty(LandPathNodeMaker.getLandNodeType(mobEntity.world, blockPos2.mutableCopy())) == 0.0F) {
				Optional<Vec3d> optional = this.getRammingVelocity(mobEntity, Vec3d.ofCenter(blockPos2));
				optional.ifPresent(vel -> this.targets.add(new LongJumpTask.Target(new BlockPos(blockPos2), vel, MathHelper.ceil(d))));
			}
		}
	}

	protected void keepRunning(ServerWorld serverWorld, E mobEntity, long l) {
		if (this.lastTarget.isPresent()) {
			if (l - this.targetTime >= 40L) {
				mobEntity.setYaw(mobEntity.bodyYaw);
				mobEntity.setNoDrag(true);
				Vec3d vec3d = ((LongJumpTask.Target)this.lastTarget.get()).getRammingVelocity();
				double d = vec3d.length();
				double e = d + mobEntity.getJumpBoostVelocityModifier();
				mobEntity.setVelocity(vec3d.multiply(e / d));
				mobEntity.getBrain().remember(MemoryModuleType.LONG_JUMP_MID_JUMP, true);
				serverWorld.playSoundFromEntity(null, mobEntity, (SoundEvent)this.field_33460.apply(mobEntity), SoundCategory.NEUTRAL, 1.0F, 1.0F);
			}
		} else {
			this.cooldown--;
			Optional<LongJumpTask.Target> optional = WeightedPicker.getRandom(serverWorld.random, this.targets);
			if (optional.isPresent()) {
				this.targets.remove(optional.get());
				mobEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(((LongJumpTask.Target)optional.get()).getPos()));
				EntityNavigation entityNavigation = mobEntity.getNavigation();
				Path path = entityNavigation.findPathTo(((LongJumpTask.Target)optional.get()).getPos(), 0, 8);
				if (path == null || !path.reachesTarget()) {
					this.lastTarget = optional;
					this.targetTime = l;
				}
			}
		}
	}

	private Optional<Vec3d> getRammingVelocity(MobEntity entity, Vec3d pos) {
		Optional<Vec3d> optional = Optional.empty();

		for (int i = 65; i < 85; i += 5) {
			Optional<Vec3d> optional2 = this.getRammingVelocity(entity, pos, i);
			if (!optional.isPresent() || optional2.isPresent() && ((Vec3d)optional2.get()).lengthSquared() < ((Vec3d)optional.get()).lengthSquared()) {
				optional = optional2;
			}
		}

		return optional;
	}

	private Optional<Vec3d> getRammingVelocity(MobEntity entity, Vec3d pos, int range) {
		Vec3d vec3d = entity.getPos();
		Vec3d vec3d2 = new Vec3d(pos.x - vec3d.x, 0.0, pos.z - vec3d.z).normalize().multiply(0.5);
		pos = pos.subtract(vec3d2);
		Vec3d vec3d3 = pos.subtract(vec3d);
		float f = (float)range * (float) Math.PI / 180.0F;
		double d = Math.atan2(vec3d3.z, vec3d3.x);
		double e = vec3d3.subtract(0.0, vec3d3.y, 0.0).lengthSquared();
		double g = Math.sqrt(e);
		double h = vec3d3.y;
		double i = Math.sin((double)(2.0F * f));
		double j = 0.08;
		double k = Math.pow(Math.cos((double)f), 2.0);
		double l = Math.sin((double)f);
		double m = Math.cos((double)f);
		double n = Math.sin(d);
		double o = Math.cos(d);
		double p = e * 0.08 / (g * i - 2.0 * h * k);
		if (p < 0.0) {
			return Optional.empty();
		} else {
			double q = Math.sqrt(p);
			if (q > (double)this.maxRange) {
				return Optional.empty();
			} else {
				double r = q * m;
				double s = q * l;
				int t = MathHelper.ceil(g / r) * 2;
				double u = 0.0;
				Vec3d vec3d4 = null;

				for (int v = 0; v < t - 1; v++) {
					u += g / (double)t;
					double w = l / m * u - Math.pow(u, 2.0) * 0.08 / (2.0 * p * Math.pow(m, 2.0));
					double x = u * o;
					double y = u * n;
					Vec3d vec3d5 = new Vec3d(vec3d.x + x, vec3d.y + w, vec3d.z + y);
					if (vec3d4 != null && !this.canReach(entity, vec3d4, vec3d5)) {
						return Optional.empty();
					}

					vec3d4 = vec3d5;
				}

				return Optional.of(new Vec3d(r * o, s, r * n).multiply(0.95F));
			}
		}
	}

	private boolean canReach(MobEntity entity, Vec3d startPos, Vec3d endPos) {
		EntityDimensions entityDimensions = entity.getDimensions(EntityPose.LONG_JUMPING);
		Vec3d vec3d = endPos.subtract(startPos);
		double d = (double)Math.min(entityDimensions.width, entityDimensions.height);
		int i = MathHelper.ceil(vec3d.length() / d);
		Vec3d vec3d2 = vec3d.normalize();
		Vec3d vec3d3 = startPos;

		for (int j = 0; j < i; j++) {
			vec3d3 = j == i - 1 ? endPos : vec3d3.add(vec3d2.multiply(d * 0.9F));
			Box box = entityDimensions.getBoxAt(vec3d3);
			if (!entity.world.isSpaceEmpty(entity, box)) {
				return false;
			}
		}

		return true;
	}

	public static class Target extends WeightedPicker.Entry {
		private final BlockPos pos;
		private final Vec3d ramVelocity;

		public Target(BlockPos pos, Vec3d ramVelocity, int weight) {
			super(weight);
			this.pos = pos;
			this.ramVelocity = ramVelocity;
		}

		public BlockPos getPos() {
			return this.pos;
		}

		public Vec3d getRammingVelocity() {
			return this.ramVelocity;
		}
	}
}
