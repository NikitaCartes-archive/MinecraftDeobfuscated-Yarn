package net.minecraft.entity.ai.pathing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkCache;

public abstract class EntityNavigation {
	private static final int RECALCULATE_COOLDOWN = 20;
	protected final MobEntity entity;
	protected final World world;
	@Nullable
	protected Path currentPath;
	protected double speed;
	protected int tickCount;
	protected int pathStartTime;
	protected Vec3d pathStartPos = Vec3d.ZERO;
	protected Vec3i lastNodePosition = Vec3i.ZERO;
	protected long currentNodeMs;
	protected long lastActiveTickMs;
	protected double currentNodeTimeout;
	/**
	 * If the Chebyshev distance from the entity to the next node is less than
	 * or equal to this value, the entity is considered "reached" the node.
	 */
	protected float nodeReachProximity = 0.5F;
	protected boolean shouldRecalculate;
	protected long lastRecalculateTime;
	protected PathNodeMaker nodeMaker;
	private BlockPos currentTarget;
	private int currentDistance;
	private float rangeMultiplier = 1.0F;
	private final PathNodeNavigator pathNodeNavigator;
	private boolean nearPathStartPos;

	public EntityNavigation(MobEntity mob, World world) {
		this.entity = mob;
		this.world = world;
		int i = MathHelper.floor(mob.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE) * 16.0);
		this.pathNodeNavigator = this.createPathNodeNavigator(i);
	}

	public void resetRangeMultiplier() {
		this.rangeMultiplier = 1.0F;
	}

	public void setRangeMultiplier(float rangeMultiplier) {
		this.rangeMultiplier = rangeMultiplier;
	}

	public BlockPos getTargetPos() {
		return this.currentTarget;
	}

	protected abstract PathNodeNavigator createPathNodeNavigator(int range);

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public boolean shouldRecalculatePath() {
		return this.shouldRecalculate;
	}

	public void recalculatePath() {
		if (this.world.getTime() - this.lastRecalculateTime > 20L) {
			if (this.currentTarget != null) {
				this.currentPath = null;
				this.currentPath = this.findPathTo(this.currentTarget, this.currentDistance);
				this.lastRecalculateTime = this.world.getTime();
				this.shouldRecalculate = false;
			}
		} else {
			this.shouldRecalculate = true;
		}
	}

	@Nullable
	public final Path findPathTo(double x, double y, double z, int distance) {
		return this.findPathTo(new BlockPos(x, y, z), distance);
	}

	@Nullable
	public Path findPathToAny(Stream<BlockPos> positions, int distance) {
		return this.findPathTo((Set<BlockPos>)positions.collect(Collectors.toSet()), 8, false, distance);
	}

	@Nullable
	public Path findPathTo(Set<BlockPos> positions, int distance) {
		return this.findPathTo(positions, 8, false, distance);
	}

	@Nullable
	public Path findPathTo(BlockPos target, int distance) {
		return this.findPathTo(ImmutableSet.of(target), 8, false, distance);
	}

	@Nullable
	public Path findPathTo(BlockPos target, int minDistance, int maxDistance) {
		return this.findPathToAny(ImmutableSet.of(target), 8, false, minDistance, (float)maxDistance);
	}

	@Nullable
	public Path findPathTo(Entity entity, int distance) {
		return this.findPathTo(ImmutableSet.of(entity.getBlockPos()), 16, true, distance);
	}

	@Nullable
	protected Path findPathTo(Set<BlockPos> positions, int range, boolean useHeadPos, int distance) {
		return this.findPathToAny(positions, range, useHeadPos, distance, (float)this.entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE));
	}

	@Nullable
	protected Path findPathToAny(Set<BlockPos> positions, int range, boolean useHeadPos, int distance, float followRange) {
		if (positions.isEmpty()) {
			return null;
		} else if (this.entity.getY() < (double)this.world.getBottomY()) {
			return null;
		} else if (!this.isAtValidPosition()) {
			return null;
		} else if (this.currentPath != null && !this.currentPath.isFinished() && positions.contains(this.currentTarget)) {
			return this.currentPath;
		} else {
			this.world.getProfiler().push("pathfind");
			BlockPos blockPos = useHeadPos ? this.entity.getBlockPos().up() : this.entity.getBlockPos();
			int i = (int)(followRange + (float)range);
			ChunkCache chunkCache = new ChunkCache(this.world, blockPos.add(-i, -i, -i), blockPos.add(i, i, i));
			Path path = this.pathNodeNavigator.findPathToAny(chunkCache, this.entity, positions, followRange, distance, this.rangeMultiplier);
			this.world.getProfiler().pop();
			if (path != null && path.getTarget() != null) {
				this.currentTarget = path.getTarget();
				this.currentDistance = distance;
				this.resetNode();
			}

			return path;
		}
	}

	public boolean startMovingTo(double x, double y, double z, double speed) {
		return this.startMovingAlong(this.findPathTo(x, y, z, 1), speed);
	}

	public boolean startMovingTo(Entity entity, double speed) {
		Path path = this.findPathTo(entity, 1);
		return path != null && this.startMovingAlong(path, speed);
	}

	public boolean startMovingAlong(@Nullable Path path, double speed) {
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
				this.adjustPath();
				if (this.currentPath.getLength() <= 0) {
					return false;
				} else {
					this.speed = speed;
					Vec3d vec3d = this.getPos();
					this.pathStartTime = this.tickCount;
					this.pathStartPos = vec3d;
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
				this.continueFollowingPath();
			} else if (this.currentPath != null && !this.currentPath.isFinished()) {
				Vec3d vec3d = this.getPos();
				Vec3d vec3d2 = this.currentPath.getNodePosition(this.entity);
				if (vec3d.y > vec3d2.y
					&& !this.entity.isOnGround()
					&& MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d2.x)
					&& MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d2.z)) {
					this.currentPath.next();
				}
			}

			DebugInfoSender.sendPathfindingData(this.world, this.entity, this.currentPath, this.nodeReachProximity);
			if (!this.isIdle()) {
				Vec3d vec3d = this.currentPath.getNodePosition(this.entity);
				BlockPos blockPos = new BlockPos(vec3d);
				this.entity
					.getMoveControl()
					.moveTo(vec3d.x, this.world.getBlockState(blockPos.down()).isAir() ? vec3d.y : LandPathNodeMaker.getFeetY(this.world, blockPos), vec3d.z, this.speed);
			}
		}
	}

	protected void continueFollowingPath() {
		Vec3d vec3d = this.getPos();
		this.nodeReachProximity = this.entity.getWidth() > 0.75F ? this.entity.getWidth() / 2.0F : 0.75F - this.entity.getWidth() / 2.0F;
		Vec3i vec3i = this.currentPath.getCurrentNodePos();
		double d = Math.abs(this.entity.getX() - ((double)vec3i.getX() + 0.5));
		double e = Math.abs(this.entity.getY() - (double)vec3i.getY());
		double f = Math.abs(this.entity.getZ() - ((double)vec3i.getZ() + 0.5));
		boolean bl = d < (double)this.nodeReachProximity && f < (double)this.nodeReachProximity && e < 1.0;
		if (bl || this.entity.canJumpToNextPathNode(this.currentPath.getCurrentNode().type) && this.shouldJumpToNextNode(vec3d)) {
			this.currentPath.next();
		}

		this.checkTimeouts(vec3d);
	}

	private boolean shouldJumpToNextNode(Vec3d currentPos) {
		if (this.currentPath.getCurrentNodeIndex() + 1 >= this.currentPath.getLength()) {
			return false;
		} else {
			Vec3d vec3d = Vec3d.ofBottomCenter(this.currentPath.getCurrentNodePos());
			if (!currentPos.isInRange(vec3d, 2.0)) {
				return false;
			} else {
				Vec3d vec3d2 = Vec3d.ofBottomCenter(this.currentPath.getNodePos(this.currentPath.getCurrentNodeIndex() + 1));
				Vec3d vec3d3 = vec3d2.subtract(vec3d);
				Vec3d vec3d4 = currentPos.subtract(vec3d);
				return vec3d3.dotProduct(vec3d4) > 0.0;
			}
		}
	}

	protected void checkTimeouts(Vec3d currentPos) {
		if (this.tickCount - this.pathStartTime > 100) {
			if (currentPos.squaredDistanceTo(this.pathStartPos) < 2.25) {
				this.nearPathStartPos = true;
				this.stop();
			} else {
				this.nearPathStartPos = false;
			}

			this.pathStartTime = this.tickCount;
			this.pathStartPos = currentPos;
		}

		if (this.currentPath != null && !this.currentPath.isFinished()) {
			Vec3i vec3i = this.currentPath.getCurrentNodePos();
			if (vec3i.equals(this.lastNodePosition)) {
				this.currentNodeMs = this.currentNodeMs + (Util.getMeasuringTimeMs() - this.lastActiveTickMs);
			} else {
				this.lastNodePosition = vec3i;
				double d = currentPos.distanceTo(Vec3d.ofBottomCenter(this.lastNodePosition));
				this.currentNodeTimeout = this.entity.getMovementSpeed() > 0.0F ? d / (double)this.entity.getMovementSpeed() * 1000.0 : 0.0;
			}

			if (this.currentNodeTimeout > 0.0 && (double)this.currentNodeMs > this.currentNodeTimeout * 3.0) {
				this.resetNodeAndStop();
			}

			this.lastActiveTickMs = Util.getMeasuringTimeMs();
		}
	}

	private void resetNodeAndStop() {
		this.resetNode();
		this.stop();
	}

	private void resetNode() {
		this.lastNodePosition = Vec3i.ZERO;
		this.currentNodeMs = 0L;
		this.currentNodeTimeout = 0.0;
		this.nearPathStartPos = false;
	}

	public boolean isIdle() {
		return this.currentPath == null || this.currentPath.isFinished();
	}

	public boolean isFollowingPath() {
		return !this.isIdle();
	}

	public void stop() {
		this.currentPath = null;
	}

	/**
	 * The position to act as if the entity is at for pathfinding purposes
	 */
	protected abstract Vec3d getPos();

	protected abstract boolean isAtValidPosition();

	protected boolean isInLiquid() {
		return this.entity.isInsideWaterOrBubbleColumn() || this.entity.isInLava();
	}

	/**
	 * Adjusts the current path according to various special obstacles that may be in the way, for example sunlight
	 */
	protected void adjustPath() {
		if (this.currentPath != null) {
			for (int i = 0; i < this.currentPath.getLength(); i++) {
				PathNode pathNode = this.currentPath.getNode(i);
				PathNode pathNode2 = i + 1 < this.currentPath.getLength() ? this.currentPath.getNode(i + 1) : null;
				BlockState blockState = this.world.getBlockState(new BlockPos(pathNode.x, pathNode.y, pathNode.z));
				if (blockState.isIn(BlockTags.CAULDRONS)) {
					this.currentPath.setNode(i, pathNode.copyWithNewPosition(pathNode.x, pathNode.y + 1, pathNode.z));
					if (pathNode2 != null && pathNode.y >= pathNode2.y) {
						this.currentPath.setNode(i + 1, pathNode.copyWithNewPosition(pathNode2.x, pathNode.y + 1, pathNode2.z));
					}
				}
			}
		}
	}

	protected abstract boolean canPathDirectlyThrough(Vec3d origin, Vec3d target, int sizeX, int sizeY, int sizeZ);

	public boolean isValidPosition(BlockPos pos) {
		BlockPos blockPos = pos.down();
		return this.world.getBlockState(blockPos).isOpaqueFullCube(this.world, blockPos);
	}

	public PathNodeMaker getNodeMaker() {
		return this.nodeMaker;
	}

	public void setCanSwim(boolean canSwim) {
		this.nodeMaker.setCanSwim(canSwim);
	}

	public boolean canSwim() {
		return this.nodeMaker.canSwim();
	}

	public void onBlockChanged(BlockPos pos) {
		if (this.currentPath != null && !this.currentPath.isFinished() && this.currentPath.getLength() != 0) {
			PathNode pathNode = this.currentPath.getEnd();
			Vec3d vec3d = new Vec3d(
				((double)pathNode.x + this.entity.getX()) / 2.0, ((double)pathNode.y + this.entity.getY()) / 2.0, ((double)pathNode.z + this.entity.getZ()) / 2.0
			);
			if (pos.isWithinDistance(vec3d, (double)(this.currentPath.getLength() - this.currentPath.getCurrentNodeIndex()))) {
				this.recalculatePath();
			}
		}
	}

	public float getNodeReachProximity() {
		return this.nodeReachProximity;
	}

	public boolean isNearPathStartPos() {
		return this.nearPathStartPos;
	}
}
