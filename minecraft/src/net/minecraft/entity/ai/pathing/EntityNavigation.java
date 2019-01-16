package net.minecraft.entity.ai.pathing;

import javax.annotation.Nullable;
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
	protected Path field_6681;
	protected double field_6668;
	private final EntityAttributeInstance followRange;
	protected int tickCount;
	protected int field_6674;
	protected Vec3d field_6672 = Vec3d.ZERO;
	protected Vec3d field_6680 = Vec3d.ZERO;
	protected long field_6670;
	protected long field_6669;
	protected double field_6682;
	protected float field_6683 = 0.5F;
	protected boolean idle;
	protected long field_6685;
	protected PathNodeMaker field_6678;
	private BlockPos field_6676;
	private PathNodeNavigator pathNodeNavigator;

	public EntityNavigation(MobEntity mobEntity, World world) {
		this.entity = mobEntity;
		this.world = world;
		this.followRange = mobEntity.getAttributeInstance(EntityAttributes.FOLLOW_RANGE);
		this.pathNodeNavigator = this.createPathNodeNavigator();
	}

	public BlockPos method_6355() {
		return this.field_6676;
	}

	protected abstract PathNodeNavigator createPathNodeNavigator();

	public void method_6344(double d) {
		this.field_6668 = d;
	}

	public float getFollowRange() {
		return (float)this.followRange.getValue();
	}

	public boolean isIdle() {
		return this.idle;
	}

	public void method_6356() {
		if (this.world.getTime() - this.field_6685 > 20L) {
			if (this.field_6676 != null) {
				this.field_6681 = null;
				this.field_6681 = this.findPathTo(this.field_6676);
				this.field_6685 = this.world.getTime();
				this.idle = false;
			}
		} else {
			this.idle = true;
		}
	}

	@Nullable
	public final Path findPathTo(double d, double e, double f) {
		return this.findPathTo(new BlockPos(d, e, f));
	}

	@Nullable
	public Path findPathTo(BlockPos blockPos) {
		if (!this.isAtValidPosition()) {
			return null;
		} else if (this.field_6681 != null && !this.field_6681.isFinished() && blockPos.equals(this.field_6676)) {
			return this.field_6681;
		} else {
			this.field_6676 = blockPos;
			float f = this.getFollowRange();
			this.world.getProfiler().push("pathfind");
			BlockPos blockPos2 = new BlockPos(this.entity);
			int i = (int)(f + 8.0F);
			BlockView blockView = new ChunkCache(this.world, blockPos2.add(-i, -i, -i), blockPos2.add(i, i, i), 0);
			Path path = this.pathNodeNavigator.pathfind(blockView, this.entity, this.field_6676, f);
			this.world.getProfiler().pop();
			return path;
		}
	}

	@Nullable
	public Path findPathTo(Entity entity) {
		if (!this.isAtValidPosition()) {
			return null;
		} else {
			BlockPos blockPos = new BlockPos(entity);
			if (this.field_6681 != null && !this.field_6681.isFinished() && blockPos.equals(this.field_6676)) {
				return this.field_6681;
			} else {
				this.field_6676 = blockPos;
				float f = this.getFollowRange();
				this.world.getProfiler().push("pathfind");
				BlockPos blockPos2 = new BlockPos(this.entity).up();
				int i = (int)(f + 16.0F);
				BlockView blockView = new ChunkCache(this.world, blockPos2.add(-i, -i, -i), blockPos2.add(i, i, i), 0);
				Path path = this.pathNodeNavigator.pathfind(blockView, this.entity, entity, f);
				this.world.getProfiler().pop();
				return path;
			}
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
			this.field_6681 = null;
			return false;
		} else {
			if (!path.equalsPath(this.field_6681)) {
				this.field_6681 = path;
			}

			this.method_6359();
			if (this.field_6681.getPathLength() <= 0) {
				return false;
			} else {
				this.field_6668 = d;
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
		if (this.idle) {
			this.method_6356();
		}

		if (!this.method_6357()) {
			if (this.isAtValidPosition()) {
				this.method_6339();
			} else if (this.field_6681 != null && this.field_6681.getCurrentNodeIndex() < this.field_6681.getPathLength()) {
				Vec3d vec3d = this.method_6347();
				Vec3d vec3d2 = this.field_6681.getNodePosition(this.entity, this.field_6681.getCurrentNodeIndex());
				if (vec3d.y > vec3d2.y
					&& !this.entity.onGround
					&& MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d2.x)
					&& MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d2.z)) {
					this.field_6681.setCurrentPosition(this.field_6681.getCurrentNodeIndex() + 1);
				}
			}

			this.method_6353();
			if (!this.method_6357()) {
				Vec3d vec3d = this.field_6681.getNodePosition(this.entity);
				BlockPos blockPos = new BlockPos(vec3d);
				this.entity
					.getMoveControl()
					.method_6239(
						vec3d.x, this.world.getBlockState(blockPos.down()).isAir() ? vec3d.y : LandPathNodeMaker.method_60(this.world, blockPos), vec3d.z, this.field_6668
					);
			}
		}
	}

	protected void method_6353() {
	}

	protected void method_6339() {
		Vec3d vec3d = this.method_6347();
		int i = this.field_6681.getPathLength();

		for (int j = this.field_6681.getCurrentNodeIndex(); j < this.field_6681.getPathLength(); j++) {
			if ((double)this.field_6681.getNode(j).y != Math.floor(vec3d.y)) {
				i = j;
				break;
			}
		}

		this.field_6683 = this.entity.getWidth() > 0.75F ? this.entity.getWidth() / 2.0F : 0.75F - this.entity.getWidth() / 2.0F;
		Vec3d vec3d2 = this.field_6681.getCurrentPosition();
		if (MathHelper.abs((float)(this.entity.x - (vec3d2.x + 0.5))) < this.field_6683
			&& MathHelper.abs((float)(this.entity.z - (vec3d2.z + 0.5))) < this.field_6683
			&& Math.abs(this.entity.y - vec3d2.y) < 1.0) {
			this.field_6681.setCurrentPosition(this.field_6681.getCurrentNodeIndex() + 1);
		}

		int k = MathHelper.ceil(this.entity.getWidth());
		int l = MathHelper.ceil(this.entity.getHeight());
		int m = k;

		for (int n = i - 1; n >= this.field_6681.getCurrentNodeIndex(); n--) {
			if (this.method_6341(vec3d, this.field_6681.getNodePosition(this.entity, n), k, l, m)) {
				this.field_6681.setCurrentPosition(n);
				break;
			}
		}

		this.method_6346(vec3d);
	}

	protected void method_6346(Vec3d vec3d) {
		if (this.tickCount - this.field_6674 > 100) {
			if (vec3d.squaredDistanceTo(this.field_6672) < 2.25) {
				this.method_6340();
			}

			this.field_6674 = this.tickCount;
			this.field_6672 = vec3d;
		}

		if (this.field_6681 != null && !this.field_6681.isFinished()) {
			Vec3d vec3d2 = this.field_6681.getCurrentPosition();
			if (vec3d2.equals(this.field_6680)) {
				this.field_6670 = this.field_6670 + (SystemUtil.getMeasuringTimeMs() - this.field_6669);
			} else {
				this.field_6680 = vec3d2;
				double d = vec3d.distanceTo(this.field_6680);
				this.field_6682 = this.entity.method_6029() > 0.0F ? d / (double)this.entity.method_6029() * 1000.0 : 0.0;
			}

			if (this.field_6682 > 0.0 && (double)this.field_6670 > this.field_6682 * 3.0) {
				this.field_6680 = Vec3d.ZERO;
				this.field_6670 = 0L;
				this.field_6682 = 0.0;
				this.method_6340();
			}

			this.field_6669 = SystemUtil.getMeasuringTimeMs();
		}
	}

	public boolean method_6357() {
		return this.field_6681 == null || this.field_6681.isFinished();
	}

	public void method_6340() {
		this.field_6681 = null;
	}

	protected abstract Vec3d method_6347();

	protected abstract boolean isAtValidPosition();

	protected boolean isInLiquid() {
		return this.entity.isInsideWaterOrBubbleColumn() || this.entity.isTouchingLava();
	}

	protected void method_6359() {
		if (this.field_6681 != null) {
			for (int i = 0; i < this.field_6681.getPathLength(); i++) {
				PathNode pathNode = this.field_6681.getNode(i);
				PathNode pathNode2 = i + 1 < this.field_6681.getPathLength() ? this.field_6681.getNode(i + 1) : null;
				BlockState blockState = this.world.getBlockState(new BlockPos(pathNode.x, pathNode.y, pathNode.z));
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
		return this.world.getBlockState(blockPos2).isFullOpaque(this.world, blockPos2);
	}

	public PathNodeMaker method_6342() {
		return this.field_6678;
	}

	public void method_6354(boolean bl) {
		this.field_6678.setCanSwim(bl);
	}

	public boolean method_6350() {
		return this.field_6678.canSwim();
	}
}
