package net.minecraft.entity.ai.pathing;

import javax.annotation.Nullable;
import net.minecraft.class_4209;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
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
	private BlockPos targetPos;
	private PathNodeNavigator pathNodeNavigator;

	public EntityNavigation(MobEntity mobEntity, World world) {
		this.entity = mobEntity;
		this.world = world;
		this.followRange = mobEntity.getAttributeInstance(EntityAttributes.FOLLOW_RANGE);
		this.pathNodeNavigator = this.createPathNodeNavigator(MathHelper.floor(this.followRange.getValue() * 16.0));
	}

	public BlockPos getTargetPos() {
		return this.targetPos;
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
			if (this.targetPos != null) {
				this.currentPath = null;
				this.currentPath = this.findPathTo(this.targetPos);
				this.lastRecalculateTime = this.world.getTime();
				this.shouldRecalculate = false;
			}
		} else {
			this.shouldRecalculate = true;
		}
	}

	@Nullable
	public final Path findPathTo(double d, double e, double f) {
		return this.findPathTo(new BlockPos(d, e, f));
	}

	@Nullable
	public Path findPathTo(BlockPos blockPos) {
		float f = (float)blockPos.getX() + 0.5F;
		float g = (float)blockPos.getY() + 0.5F;
		float h = (float)blockPos.getZ() + 0.5F;
		return this.method_18416(blockPos, (double)f, (double)g, (double)h, 8, false);
	}

	@Nullable
	public Path findPathTo(Entity entity) {
		BlockPos blockPos = new BlockPos(entity);
		double d = entity.x;
		double e = entity.getBoundingBox().minY;
		double f = entity.z;
		return this.method_18416(blockPos, d, e, f, 16, true);
	}

	@Nullable
	protected Path method_18416(BlockPos blockPos, double d, double e, double f, int i, boolean bl) {
		if (!this.isAtValidPosition()) {
			return null;
		} else if (this.currentPath != null && !this.currentPath.isFinished() && blockPos.equals(this.targetPos)) {
			return this.currentPath;
		} else {
			this.targetPos = blockPos;
			float g = this.getFollowRange();
			this.world.getProfiler().push("pathfind");
			BlockPos blockPos2 = bl ? new BlockPos(this.entity).up() : new BlockPos(this.entity);
			int j = (int)(g + (float)i);
			BlockView blockView = new ChunkCache(this.world, blockPos2.add(-j, -j, -j), blockPos2.add(j, j, j));
			Path path = this.pathNodeNavigator.pathfind(blockView, this.entity, d, e, f, g);
			this.world.getProfiler().pop();
			return path;
		}
	}

	public boolean startMovingTo(double d, double e, double f, double g) {
		return this.startMovingAlong(this.findPathTo(d, e, f), g);
	}

	public boolean startMovingTo(Entity entity, double d) {
		Path path = this.findPathTo(entity);
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

			this.method_6359();
			if (this.currentPath.getLength() <= 0) {
				return false;
			} else {
				this.speed = d;
				Vec3d vec3d = this.method_6347();
				this.field_6674 = this.tickCount;
				this.field_6672 = vec3d;
				return true;
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
				Vec3d vec3d = this.method_6347();
				Vec3d vec3d2 = this.currentPath.getNodePosition(this.entity, this.currentPath.getCurrentNodeIndex());
				if (vec3d.y > vec3d2.y
					&& !this.entity.onGround
					&& MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d2.x)
					&& MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d2.z)) {
					this.currentPath.setCurrentNodeIndex(this.currentPath.getCurrentNodeIndex() + 1);
				}
			}

			class_4209.method_19470(this.world, this.entity, this.currentPath, this.field_6683);
			if (!this.isIdle()) {
				Vec3d vec3d = this.currentPath.getNodePosition(this.entity);
				BlockPos blockPos = new BlockPos(vec3d);
				this.entity
					.getMoveControl()
					.moveTo(vec3d.x, this.world.getBlockState(blockPos.down()).isAir() ? vec3d.y : LandPathNodeMaker.method_60(this.world, blockPos), vec3d.z, this.speed);
			}
		}
	}

	protected void method_6339() {
		Vec3d vec3d = this.method_6347();
		int i = this.currentPath.getLength();

		for (int j = this.currentPath.getCurrentNodeIndex(); j < this.currentPath.getLength(); j++) {
			if ((double)this.currentPath.getNode(j).y != Math.floor(vec3d.y)) {
				i = j;
				break;
			}
		}

		this.field_6683 = this.entity.getWidth() > 0.75F ? this.entity.getWidth() / 2.0F : 0.75F - this.entity.getWidth() / 2.0F;
		Vec3d vec3d2 = this.currentPath.getCurrentPosition();
		if (Math.abs(this.entity.x - (vec3d2.x + 0.5)) < (double)this.field_6683
			&& Math.abs(this.entity.z - (vec3d2.z + 0.5)) < (double)this.field_6683
			&& Math.abs(this.entity.y - vec3d2.y) < 1.0) {
			this.currentPath.setCurrentNodeIndex(this.currentPath.getCurrentNodeIndex() + 1);
		}

		int k = MathHelper.ceil(this.entity.getWidth());
		int l = MathHelper.ceil(this.entity.getHeight());
		int m = k;

		for (int n = i - 1; n >= this.currentPath.getCurrentNodeIndex(); n--) {
			if (this.canPathDirectlyThrough(vec3d, this.currentPath.getNodePosition(this.entity, n), k, l, m)) {
				this.currentPath.setCurrentNodeIndex(n);
				break;
			}
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

	protected abstract Vec3d method_6347();

	protected abstract boolean isAtValidPosition();

	protected boolean isInLiquid() {
		return this.entity.isInsideWaterOrBubbleColumn() || this.entity.isTouchingLava();
	}

	protected void method_6359() {
		if (this.currentPath != null) {
			for (int i = 0; i < this.currentPath.getLength(); i++) {
				PathNode pathNode = this.currentPath.getNode(i);
				PathNode pathNode2 = i + 1 < this.currentPath.getLength() ? this.currentPath.getNode(i + 1) : null;
				BlockState blockState = this.world.getBlockState(new BlockPos(pathNode.x, pathNode.y, pathNode.z));
				Block block = blockState.getBlock();
				if (block == Blocks.field_10593) {
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
		BlockPos blockPos2 = blockPos.down();
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
			double d = blockPos.squaredDistanceTo(
				((double)pathNode.x + this.entity.x) / 2.0, ((double)pathNode.y + this.entity.y) / 2.0, ((double)pathNode.z + this.entity.z) / 2.0
			);
			int i = (this.currentPath.getLength() - this.currentPath.getCurrentNodeIndex()) * (this.currentPath.getLength() - this.currentPath.getCurrentNodeIndex());
			if (d < (double)i) {
				this.recalculatePath();
			}
		}
	}
}
