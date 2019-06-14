package net.minecraft.entity.ai.pathing;

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
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkCache;

public abstract class EntityNavigation {
	protected final MobEntity entity;
	protected final World field_6677;
	@Nullable
	protected Path field_6681;
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
	protected PathNodeMaker field_6678;
	private BlockPos targetPos;
	private PathNodeNavigator field_6673;

	public EntityNavigation(MobEntity mobEntity, World world) {
		this.entity = mobEntity;
		this.field_6677 = world;
		this.followRange = mobEntity.getAttributeInstance(EntityAttributes.FOLLOW_RANGE);
		this.field_6673 = this.method_6336(MathHelper.floor(this.followRange.getValue() * 16.0));
	}

	public BlockPos getTargetPos() {
		return this.targetPos;
	}

	protected abstract PathNodeNavigator method_6336(int i);

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
		if (this.field_6677.getTime() - this.lastRecalculateTime > 20L) {
			if (this.targetPos != null) {
				this.field_6681 = null;
				this.field_6681 = this.method_6348(this.targetPos);
				this.lastRecalculateTime = this.field_6677.getTime();
				this.shouldRecalculate = false;
			}
		} else {
			this.shouldRecalculate = true;
		}
	}

	@Nullable
	public final Path method_6352(double d, double e, double f) {
		return this.method_6348(new BlockPos(d, e, f));
	}

	@Nullable
	public Path method_6348(BlockPos blockPos) {
		float f = (float)blockPos.getX() + 0.5F;
		float g = (float)blockPos.getY() + 0.5F;
		float h = (float)blockPos.getZ() + 0.5F;
		return this.method_18416(blockPos, (double)f, (double)g, (double)h, 8, false);
	}

	@Nullable
	public Path method_6349(Entity entity) {
		BlockPos blockPos = new BlockPos(entity);
		double d = entity.x;
		double e = entity.method_5829().minY;
		double f = entity.z;
		return this.method_18416(blockPos, d, e, f, 16, true);
	}

	@Nullable
	protected Path method_18416(BlockPos blockPos, double d, double e, double f, int i, boolean bl) {
		if (this.entity.y < 0.0) {
			return null;
		} else if (!this.isAtValidPosition()) {
			return null;
		} else if (this.field_6681 != null && !this.field_6681.isFinished() && blockPos.equals(this.targetPos)) {
			return this.field_6681;
		} else {
			this.targetPos = blockPos.toImmutable();
			float g = this.getFollowRange();
			this.field_6677.getProfiler().push("pathfind");
			BlockPos blockPos2 = bl ? new BlockPos(this.entity).up() : new BlockPos(this.entity);
			int j = (int)(g + (float)i);
			ViewableWorld viewableWorld = new ChunkCache(this.field_6677, blockPos2.add(-j, -j, -j), blockPos2.add(j, j, j));
			Path path = this.field_6673.pathfind(viewableWorld, this.entity, d, e, f, g);
			this.field_6677.getProfiler().pop();
			return path;
		}
	}

	public boolean startMovingTo(double d, double e, double f, double g) {
		return this.method_6334(this.method_6352(d, e, f), g);
	}

	public boolean startMovingTo(Entity entity, double d) {
		Path path = this.method_6349(entity);
		return path != null && this.method_6334(path, d);
	}

	public boolean method_6334(@Nullable Path path, double d) {
		if (path == null) {
			this.field_6681 = null;
			return false;
		} else {
			if (!path.equalsPath(this.field_6681)) {
				this.field_6681 = path;
			}

			this.method_6359();
			if (this.field_6681.getLength() <= 0) {
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
	public Path method_6345() {
		return this.field_6681;
	}

	public void tick() {
		this.tickCount++;
		if (this.shouldRecalculate) {
			this.recalculatePath();
		}

		if (!this.isIdle()) {
			if (this.isAtValidPosition()) {
				this.method_6339();
			} else if (this.field_6681 != null && this.field_6681.getCurrentNodeIndex() < this.field_6681.getLength()) {
				Vec3d vec3d = this.method_6347();
				Vec3d vec3d2 = this.field_6681.method_47(this.entity, this.field_6681.getCurrentNodeIndex());
				if (vec3d.y > vec3d2.y
					&& !this.entity.onGround
					&& MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d2.x)
					&& MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d2.z)) {
					this.field_6681.setCurrentNodeIndex(this.field_6681.getCurrentNodeIndex() + 1);
				}
			}

			DebugRendererInfoManager.sendPathfindingData(this.field_6677, this.entity, this.field_6681, this.field_6683);
			if (!this.isIdle()) {
				Vec3d vec3d = this.field_6681.method_49(this.entity);
				BlockPos blockPos = new BlockPos(vec3d);
				this.entity
					.getMoveControl()
					.moveTo(
						vec3d.x, this.field_6677.method_8320(blockPos.down()).isAir() ? vec3d.y : LandPathNodeMaker.method_60(this.field_6677, blockPos), vec3d.z, this.speed
					);
			}
		}
	}

	protected void method_6339() {
		Vec3d vec3d = this.method_6347();
		this.field_6683 = this.entity.getWidth() > 0.75F ? this.entity.getWidth() / 2.0F : 0.75F - this.entity.getWidth() / 2.0F;
		Vec3d vec3d2 = this.field_6681.method_35();
		if (Math.abs(this.entity.x - (vec3d2.x + 0.5)) < (double)this.field_6683
			&& Math.abs(this.entity.z - (vec3d2.z + 0.5)) < (double)this.field_6683
			&& Math.abs(this.entity.y - vec3d2.y) < 1.0) {
			this.field_6681.setCurrentNodeIndex(this.field_6681.getCurrentNodeIndex() + 1);
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

		if (this.field_6681 != null && !this.field_6681.isFinished()) {
			Vec3d vec3d2 = this.field_6681.method_35();
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
		return this.field_6681 == null || this.field_6681.isFinished();
	}

	public void stop() {
		this.field_6681 = null;
	}

	protected abstract Vec3d method_6347();

	protected abstract boolean isAtValidPosition();

	protected boolean isInLiquid() {
		return this.entity.isInsideWaterOrBubbleColumn() || this.entity.isInLava();
	}

	protected void method_6359() {
		if (this.field_6681 != null) {
			for (int i = 0; i < this.field_6681.getLength(); i++) {
				PathNode pathNode = this.field_6681.getNode(i);
				PathNode pathNode2 = i + 1 < this.field_6681.getLength() ? this.field_6681.getNode(i + 1) : null;
				BlockState blockState = this.field_6677.method_8320(new BlockPos(pathNode.x, pathNode.y, pathNode.z));
				Block block = blockState.getBlock();
				if (block == Blocks.field_10593) {
					this.field_6681.setNode(i, pathNode.copyWithNewPosition(pathNode.x, pathNode.y + 1, pathNode.z));
					if (pathNode2 != null && pathNode.y >= pathNode2.y) {
						this.field_6681.setNode(i + 1, pathNode2.copyWithNewPosition(pathNode2.x, pathNode.y + 1, pathNode2.z));
					}
				}
			}
		}
	}

	protected abstract boolean method_6341(Vec3d vec3d, Vec3d vec3d2, int i, int j, int k);

	public boolean isValidPosition(BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		return this.field_6677.method_8320(blockPos2).isFullOpaque(this.field_6677, blockPos2);
	}

	public PathNodeMaker method_6342() {
		return this.field_6678;
	}

	public void setCanSwim(boolean bl) {
		this.field_6678.setCanSwim(bl);
	}

	public boolean canSwim() {
		return this.field_6678.canSwim();
	}

	public void method_18053(BlockPos blockPos) {
		if (this.field_6681 != null && !this.field_6681.isFinished() && this.field_6681.getLength() != 0) {
			PathNode pathNode = this.field_6681.getEnd();
			Vec3d vec3d = new Vec3d(((double)pathNode.x + this.entity.x) / 2.0, ((double)pathNode.y + this.entity.y) / 2.0, ((double)pathNode.z + this.entity.z) / 2.0);
			if (blockPos.isWithinDistance(vec3d, (double)(this.field_6681.getLength() - this.field_6681.getCurrentNodeIndex()))) {
				this.recalculatePath();
			}
		}
	}
}
