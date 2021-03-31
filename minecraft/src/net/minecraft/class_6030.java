package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class class_6030 extends Task<MobEntity> {
	private static final int field_30135 = 20;
	private static final int field_30136 = 40;
	private static final int field_30137 = 7;
	public static final int field_30134 = 200;
	private final UniformIntProvider field_30138;
	private final int field_30139;
	private final int field_30140;
	private final float field_30141;
	private final List<class_6030.class_6031> field_30142 = new ArrayList();
	private Optional<Vec3d> field_30143 = Optional.empty();
	private Optional<class_6030.class_6031> field_30144 = Optional.empty();
	private int field_30145;
	private long field_30146;

	public class_6030(UniformIntProvider uniformIntProvider, int i, int j, float f) {
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
		this.field_30138 = uniformIntProvider;
		this.field_30139 = i;
		this.field_30140 = j;
		this.field_30141 = f;
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
		return mobEntity.isOnGround() && !serverWorld.getBlockState(mobEntity.getBlockPos().down()).isOf(Blocks.HONEY_BLOCK);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		boolean bl = this.field_30143.isPresent()
			&& ((Vec3d)this.field_30143.get()).equals(mobEntity.getPos())
			&& this.field_30145 > 0
			&& (this.field_30144.isPresent() || !this.field_30142.isEmpty());
		if (!bl && !mobEntity.getBrain().getOptionalMemory(MemoryModuleType.LONG_JUMP_MID_JUMP).isPresent()) {
			mobEntity.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, this.field_30138.get(serverWorld.random) / 2);
		}

		return bl;
	}

	protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		this.field_30144 = Optional.empty();
		this.field_30145 = 20;
		this.field_30142.clear();
		this.field_30143 = Optional.of(mobEntity.getPos());
		BlockPos blockPos = mobEntity.getBlockPos();
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		Iterable<BlockPos> iterable = BlockPos.iterate(
			i - this.field_30140, j - this.field_30139, k - this.field_30140, i + this.field_30140, j + this.field_30139, k + this.field_30140
		);
		EntityNavigation entityNavigation = mobEntity.getNavigation();

		for (BlockPos blockPos2 : iterable) {
			double d = blockPos2.getSquaredDistance(blockPos);
			if ((i != blockPos2.getX() || k != blockPos2.getZ())
				&& entityNavigation.isValidPosition(blockPos2)
				&& mobEntity.getPathfindingPenalty(LandPathNodeMaker.getLandNodeType(mobEntity.world, blockPos2.mutableCopy())) == 0.0F) {
				Optional<Vec3d> optional = this.method_35078(mobEntity, Vec3d.ofCenter(blockPos2));
				optional.ifPresent(vec3d -> this.field_30142.add(new class_6030.class_6031(new BlockPos(blockPos2), vec3d, MathHelper.ceil(d))));
			}
		}
	}

	protected void keepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		if (this.field_30144.isPresent()) {
			if (l - this.field_30146 >= 40L) {
				mobEntity.yaw = mobEntity.bodyYaw;
				mobEntity.method_35054(true);
				mobEntity.setVelocity(((class_6030.class_6031)this.field_30144.get()).method_35085());
				mobEntity.getBrain().remember(MemoryModuleType.LONG_JUMP_MID_JUMP, true);
			}
		} else {
			this.field_30145--;
			Optional<class_6030.class_6031> optional = WeightedPicker.getRandom(serverWorld.random, this.field_30142);
			if (optional.isPresent()) {
				this.field_30142.remove(optional.get());
				mobEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(((class_6030.class_6031)optional.get()).method_35084()));
				EntityNavigation entityNavigation = mobEntity.getNavigation();
				Path path = entityNavigation.method_35141(((class_6030.class_6031)optional.get()).method_35084(), 0, 7);
				if (path == null || !path.reachesTarget()) {
					this.field_30144 = optional;
					this.field_30146 = l;
				}
			}
		}
	}

	private Optional<Vec3d> method_35078(MobEntity mobEntity, Vec3d vec3d) {
		Optional<Vec3d> optional = Optional.empty();

		for (int i = 65; i < 85; i += 5) {
			Optional<Vec3d> optional2 = this.method_35079(mobEntity, vec3d, i);
			if (!optional.isPresent() || optional2.isPresent() && ((Vec3d)optional2.get()).lengthSquared() < ((Vec3d)optional.get()).lengthSquared()) {
				optional = optional2;
			}
		}

		return optional;
	}

	private Optional<Vec3d> method_35079(MobEntity mobEntity, Vec3d vec3d, int i) {
		Vec3d vec3d2 = mobEntity.getPos();
		Vec3d vec3d3 = new Vec3d(vec3d.x - vec3d2.x, 0.0, vec3d.z - vec3d2.z).normalize().multiply(0.5);
		vec3d = vec3d.subtract(vec3d3);
		Vec3d vec3d4 = vec3d.subtract(vec3d2);
		float f = (float)i * (float) Math.PI / 180.0F;
		double d = Math.atan2(vec3d4.z, vec3d4.x);
		double e = vec3d4.subtract(0.0, vec3d4.y, 0.0).lengthSquared();
		double g = Math.sqrt(e);
		double h = vec3d4.y;
		double j = Math.sin((double)(2.0F * f));
		double k = 0.08;
		double l = Math.pow(Math.cos((double)f), 2.0);
		double m = Math.sin((double)f);
		double n = Math.cos((double)f);
		double o = Math.sin(d);
		double p = Math.cos(d);
		double q = e * 0.08 / (g * j - 2.0 * h * l);
		if (q < 0.0) {
			return Optional.empty();
		} else {
			double r = Math.sqrt(q);
			if (r > (double)this.field_30141) {
				return Optional.empty();
			} else {
				double s = r * n;
				double t = r * m;
				int u = MathHelper.ceil(g / s) * 2;
				double v = 0.0;
				Vec3d vec3d5 = null;

				for (int w = 0; w < u - 1; w++) {
					v += g / (double)u;
					double x = m / n * v - Math.pow(v, 2.0) * 0.08 / (2.0 * q * Math.pow(n, 2.0));
					double y = v * p;
					double z = v * o;
					Vec3d vec3d6 = new Vec3d(vec3d2.x + y, vec3d2.y + x, vec3d2.z + z);
					if (vec3d5 != null && !this.method_35080(mobEntity, vec3d5, vec3d6)) {
						return Optional.empty();
					}

					vec3d5 = vec3d6;
				}

				return Optional.of(new Vec3d(s * p, t, s * o).multiply(0.95F));
			}
		}
	}

	private boolean method_35080(MobEntity mobEntity, Vec3d vec3d, Vec3d vec3d2) {
		EntityDimensions entityDimensions = mobEntity.getDimensions(EntityPose.LONG_JUMPING);
		Vec3d vec3d3 = vec3d2.subtract(vec3d);
		double d = (double)Math.min(entityDimensions.width, entityDimensions.height);
		int i = MathHelper.ceil(vec3d3.length() / d);
		Vec3d vec3d4 = vec3d3.normalize();
		Vec3d vec3d5 = vec3d;

		for (int j = 0; j < i; j++) {
			vec3d5 = j == i - 1 ? vec3d2 : vec3d5.add(vec3d4.multiply(d * 0.9F));
			Box box = entityDimensions.getBoxAt(vec3d5);
			if (!mobEntity.world.isSpaceEmpty(mobEntity, box)) {
				return false;
			}
		}

		return true;
	}

	public static class class_6031 extends WeightedPicker.Entry {
		private final BlockPos field_30147;
		private final Vec3d field_30148;

		public class_6031(BlockPos blockPos, Vec3d vec3d, int i) {
			super(i);
			this.field_30147 = blockPos;
			this.field_30148 = vec3d;
		}

		public BlockPos method_35084() {
			return this.field_30147;
		}

		public Vec3d method_35085() {
			return this.field_30148;
		}
	}
}
