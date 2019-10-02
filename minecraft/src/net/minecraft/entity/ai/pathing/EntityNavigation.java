package net.minecraft.entity.ai.pathing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkCache;

public abstract class EntityNavigation {
	protected final MobEntity entity;
	protected final World world;
	@Nullable
	protected Path currentPath;
	protected double speed;
	private final EntityAttributeInstance followRange;
	protected int tickCount;
	protected int field_6674;
	protected Vec3d field_6672 = Vec3d.ZERO;
	protected Vec3d field_6680 = Vec3d.ZERO;
	protected long field_6670;
	protected long field_6669;
	protected double field_6682;
	protected float field_6683 = 0.5F;
	protected boolean shouldRecalculate;
	protected long lastRecalculateTime;
	protected PathNodeMaker nodeMaker;
	private BlockPos currentTarget;
	private int field_20294;
	private PathNodeNavigator pathNodeNavigator;

	public EntityNavigation(MobEntity mobEntity, World world) {
		this.entity = mobEntity;
		this.world = world;
		this.followRange = mobEntity.getAttributeInstance(EntityAttributes.FOLLOW_RANGE);
		this.pathNodeNavigator = this.createPathNodeNavigator(MathHelper.floor(this.followRange.getValue() * 16.0));
	}

	public BlockPos getTargetPos() {
		return this.currentTarget;
	}

	protected abstract PathNodeNavigator createPathNodeNavigator(int i);

	public void setSpeed(double d) {
		this.speed = d;
	}

	public float getFollowRange() {
		return (float)this.followRange.getValue();
	}

	public boolean shouldRecalculatePath() {
		return this.shouldRecalculate;
	}

	public void recalculatePath() {
		if (this.world.getTime() - this.lastRecalculateTime > 20L) {
			if (this.currentTarget != null) {
				this.currentPath = null;
				this.currentPath = this.findPathTo(this.currentTarget, this.field_20294);
				this.lastRecalculateTime = this.world.getTime();
				this.shouldRecalculate = false;
			}
		} else {
			this.shouldRecalculate = true;
		}
	}

	@Nullable
	public final Path findPathTo(double d, double e, double f, int i) {
		return this.findPathTo(new BlockPos(d, e, f), i);
	}

	@Nullable
	public Path findPathToAny(Stream<BlockPos> stream, int i) {
		return this.findPathToAny((Set<BlockPos>)stream.collect(Collectors.toSet()), 8, false, i);
	}

	@Nullable
	public Path findPathTo(BlockPos blockPos, int i) {
		return this.findPathToAny(ImmutableSet.of(blockPos), 8, false, i);
	}

	@Nullable
	public Path findPathTo(Entity entity, int i) {
		return this.findPathToAny(ImmutableSet.of(new BlockPos(entity)), 16, true, i);
	}

	@Nullable
	protected Path findPathToAny(Set<BlockPos> set, int i, boolean bl, int j) {
		if (set.isEmpty()) {
			return null;
		} else if (this.entity.y < 0.0) {
			return null;
		} else if (!this.isAtValidPosition()) {
			return null;
		} else if (this.currentPath != null && !this.currentPath.isFinished() && set.contains(this.currentTarget)) {
			return this.currentPath;
		} else {
			this.world.getProfiler().push("pathfind");
			float f = this.getFollowRange();
			BlockPos blockPos = bl ? new BlockPos(this.entity).up() : new BlockPos(this.entity);
			int k = (int)(f + (float)i);
			ChunkCache chunkCache = new ChunkCache(this.world, blockPos.add(-k, -k, -k), blockPos.add(k, k, k));
			Path path = this.pathNodeNavigator.findPathToAny(chunkCache, this.entity, set, f, j);
			this.world.getProfiler().pop();
			if (path != null && path.getTarget() != null) {
				this.currentTarget = path.getTarget();
				this.field_20294 = j;
			}

			return path;
		}
	}

	public boolean startMovingTo(double d, double e, double f, double g) {
		return this.startMovingAlong(this.findPathTo(d, e, f, 1), g);
	}

	public boolean startMovingTo(Entity entity, double d) {
		Path path = this.findPathTo(entity, 1);
		return path != null && this.startMovingAlong(path, d);
	}

	public boolean startMovingAlong(@Nullable Path path, double d) {
		if (path == null) {
			this.currentPath = null;
			return false;
		} else {
			if (!path.equalsPath(this.currentPath)) {
				this.currentPath = path;
			}

			if (this.isIdle()) {
				return false;
			} else {
				this.method_6359();
				if (this.currentPath.getLength() <= 0) {
					return false;
				} else {
					this.speed = d;
					Vec3d vec3d = this.getPos();
					this.field_6674 = this.tickCount;
					this.field_6672 = vec3d;
					return true;
				}
			}
		}
	}

	@Nullable
	public Path getCurrentPath() {
		return this.currentPath;
	}

	public void tick() {
		this.tickCount++;
		if (this.shouldRecalculate) {
			this.recalculatePath();
		}

		if (!this.isIdle()) {
			if (this.isAtValidPosition()) {
				this.method_6339();
			} else if (this.currentPath != null && this.currentPath.getCurrentNodeIndex() < this.currentPath.getLength()) {
				Vec3d vec3d = this.getPos();
				Vec3d vec3d2 = this.currentPath.getNodePosition(this.entity, this.currentPath.getCurrentNodeIndex());
				if (vec3d.y > vec3d2.y
					&& !this.entity.onGround
					&& MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d2.x)
					&& MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d2.z)) {
					this.currentPath.setCurrentNodeIndex(this.currentPath.getCurrentNodeIndex() + 1);
				}
			}

			DebugRendererInfoManager.sendPathfindingData(this.world, this.entity, this.currentPath, this.field_6683);
			if (!this.isIdle()) {
				Vec3d vec3d = this.currentPath.getNodePosition(this.entity);
				BlockPos blockPos = new BlockPos(vec3d);
				this.entity
					.getMoveControl()
					.moveTo(
						vec3d.x, this.world.getBlockState(blockPos.method_10074()).isAir() ? vec3d.y : LandPathNodeMaker.getHeight(this.world, blockPos), vec3d.z, this.speed
					);
			}
		}
	}

	protected void method_6339() {
		Vec3d vec3d = this.getPos();
		this.field_6683 = this.entity.getWidth() > 0.75F ? this.entity.getWidth() / 2.0F : 0.75F - this.entity.getWidth() / 2.0F;
		Vec3d vec3d2 = this.currentPath.getCurrentPosition();
		if (Math.abs(this.entity.x - (vec3d2.x + 0.5)) < (double)this.field_6683
			&& Math.abs(this.entity.z - (vec3d2.z + 0.5)) < (double)this.field_6683
			&& Math.abs(this.entity.y - vec3d2.y) < 1.0) {
			this.currentPath.setCurrentNodeIndex(this.currentPath.getCurrentNodeIndex() + 1);
		}

		this.method_6346(vec3d);
	}

	protected void method_6346(Vec3d vec3d) {
		if (this.tickCount - this.field_6674 > 100) {
			if (vec3d.squaredDistanceTo(this.field_6672) < 2.25) {
				this.stop();
			}

			this.field_6674 = this.tickCount;
			this.field_6672 = vec3d;
		}

		if (this.currentPath != null && !this.currentPath.isFinished()) {
			Vec3d vec3d2 = this.currentPath.getCurrentPosition();
			if (vec3d2.equals(this.field_6680)) {
				this.field_6670 = this.field_6670 + (SystemUtil.getMeasuringTimeMs() - this.field_6669);
			} else {
				this.field_6680 = vec3d2;
				double d = vec3d.distanceTo(this.field_6680);
				this.field_6682 = this.entity.getMovementSpeed() > 0.0F ? d / (double)this.entity.getMovementSpeed() * 1000.0 : 0.0;
			}

			if (this.field_6682 > 0.0 && (double)this.field_6670 > this.field_6682 * 3.0) {
				this.field_6680 = Vec3d.ZERO;
				this.field_6670 = 0L;
				this.field_6682 = 0.0;
				this.stop();
			}

			this.field_6669 = SystemUtil.getMeasuringTimeMs();
		}
	}

	public boolean isIdle() {
		return this.currentPath == null || this.currentPath.isFinished();
	}

	public void stop() {
		this.currentPath = null;
	}

	protected abstract Vec3d getPos();

	protected abstract boolean isAtValidPosition();

	protected boolean isInLiquid() {
		return this.entity.isInsideWaterOrBubbleColumn() || this.entity.isInLava();
	}

	protected void method_6359() {
		if (this.currentPath != null) {
			for (int i = 0; i < this.currentPath.getLength(); i++) {
				PathNode pathNode = this.currentPath.getNode(i);
				PathNode pathNode2 = i + 1 < this.currentPath.getLength() ? this.currentPath.getNode(i + 1) : null;
				BlockState blockState = this.world.getBlockState(new BlockPos(pathNode.x, pathNode.y, pathNode.z));
				Block block = blockState.getBlock();
				if (block == Blocks.CAULDRON) {
					this.currentPath.setNode(i, pathNode.copyWithNewPosition(pathNode.x, pathNode.y + 1, pathNode.z));
					if (pathNode2 != null && pathNode.y >= pathNode2.y) {
						this.currentPath.setNode(i + 1, pathNode2.copyWithNewPosition(pathNode2.x, pathNode.y + 1, pathNode2.z));
					}
				}
			}
		}
	}

	protected abstract boolean canPathDirectlyThrough(Vec3d vec3d, Vec3d vec3d2, int i, int j, int k);

	public boolean isValidPosition(BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.method_10074();
		return this.world.getBlockState(blockPos2).isFullOpaque(this.world, blockPos2);
	}

	public PathNodeMaker getNodeMaker() {
		return this.nodeMaker;
	}

	public void setCanSwim(boolean bl) {
		this.nodeMaker.setCanSwim(bl);
	}

	public boolean canSwim() {
		return this.nodeMaker.canSwim();
	}

	public void method_18053(BlockPos blockPos) {
		if (this.currentPath != null && !this.currentPath.isFinished() && this.currentPath.getLength() != 0) {
			PathNode pathNode = this.currentPath.getEnd();
			Vec3d vec3d = new Vec3d(((double)pathNode.x + this.entity.x) / 2.0, ((double)pathNode.y + this.entity.y) / 2.0, ((double)pathNode.z + this.entity.z) / 2.0);
			if (blockPos.isWithinDistance(vec3d, (double)(this.currentPath.getLength() - this.currentPath.getCurrentNodeIndex()))) {
				this.recalculatePath();
			}
		}
	}
}
