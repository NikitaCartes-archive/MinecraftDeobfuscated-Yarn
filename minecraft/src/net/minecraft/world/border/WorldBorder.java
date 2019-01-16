package net.minecraft.world.border;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkPos;

public class WorldBorder {
	private final List<WorldBorderListener> listeners = Lists.<WorldBorderListener>newArrayList();
	private double damagePerBlock = 0.2;
	private double safeZone = 5.0;
	private int warningTime = 15;
	private int warningBlocks = 5;
	private double centerX;
	private double centerZ;
	private int maxWorldBorderRadius = 29999984;
	private WorldBorder.Area area = new WorldBorder.StaticArea(6.0E7);

	public boolean contains(BlockPos blockPos) {
		return (double)(blockPos.getX() + 1) > this.getBoundWest()
			&& (double)blockPos.getX() < this.getBoundEast()
			&& (double)(blockPos.getZ() + 1) > this.getBoundNorth()
			&& (double)blockPos.getZ() < this.getBoundSouth();
	}

	public boolean contains(ChunkPos chunkPos) {
		return (double)chunkPos.getXEnd() > this.getBoundWest()
			&& (double)chunkPos.getXStart() < this.getBoundEast()
			&& (double)chunkPos.getZEnd() > this.getBoundNorth()
			&& (double)chunkPos.getZStart() < this.getBoundSouth();
	}

	public boolean contains(BoundingBox boundingBox) {
		return boundingBox.maxX > this.getBoundWest()
			&& boundingBox.minX < this.getBoundEast()
			&& boundingBox.maxZ > this.getBoundNorth()
			&& boundingBox.minZ < this.getBoundSouth();
	}

	public double contains(Entity entity) {
		return this.contains(entity.x, entity.z);
	}

	public double contains(double d, double e) {
		double f = e - this.getBoundNorth();
		double g = this.getBoundSouth() - e;
		double h = d - this.getBoundWest();
		double i = this.getBoundEast() - d;
		double j = Math.min(h, i);
		j = Math.min(j, f);
		return Math.min(j, g);
	}

	@Environment(EnvType.CLIENT)
	public WorldBorderStage getStage() {
		return this.area.getStage();
	}

	public double getBoundWest() {
		return this.area.getBoundWest();
	}

	public double getBoundNorth() {
		return this.area.getBoundNorth();
	}

	public double getBoundEast() {
		return this.area.getBoundEast();
	}

	public double getBoundSouth() {
		return this.area.getBoundSouth();
	}

	public double getCenterX() {
		return this.centerX;
	}

	public double getCenterZ() {
		return this.centerZ;
	}

	public void setCenter(double d, double e) {
		this.centerX = d;
		this.centerZ = e;
		this.area.onCenterChanged();

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onCenterChanged(this, d, e);
		}
	}

	public double getSize() {
		return this.area.getSize();
	}

	public long getTargetRemainingTime() {
		return this.area.getTargetRemainingTime();
	}

	public double getTargetSize() {
		return this.area.getTargetSize();
	}

	public void setSize(double d) {
		this.area = new WorldBorder.StaticArea(d);

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onSizeChange(this, d);
		}
	}

	public void method_11957(double d, double e, long l) {
		this.area = (WorldBorder.Area)(d == e ? new WorldBorder.StaticArea(e) : new WorldBorder.MovingArea(d, e, l));

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.method_11931(this, d, e, l);
		}
	}

	protected List<WorldBorderListener> getListeners() {
		return Lists.<WorldBorderListener>newArrayList(this.listeners);
	}

	public void addListener(WorldBorderListener worldBorderListener) {
		this.listeners.add(worldBorderListener);
	}

	public void setMaxWorldBorderRadius(int i) {
		this.maxWorldBorderRadius = i;
		this.area.onMaxWorldBorderRadiusChanged();
	}

	public int getMaxWorldBorderRadius() {
		return this.maxWorldBorderRadius;
	}

	public double getSafeZone() {
		return this.safeZone;
	}

	public void setSafeZone(double d) {
		this.safeZone = d;

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onSafeZoneChanged(this, d);
		}
	}

	public double getDamagePerBlock() {
		return this.damagePerBlock;
	}

	public void setDamagePerBlock(double d) {
		this.damagePerBlock = d;

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onDamagePerBlockChanged(this, d);
		}
	}

	@Environment(EnvType.CLIENT)
	public double method_11974() {
		return this.area.method_11987();
	}

	public int getWarningTime() {
		return this.warningTime;
	}

	public void setWarningTime(int i) {
		this.warningTime = i;

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onWarningTimeChanged(this, i);
		}
	}

	public int getWarningBlocks() {
		return this.warningBlocks;
	}

	public void setWarningBlocks(int i) {
		this.warningBlocks = i;

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onWarningBlocksChanged(this, i);
		}
	}

	public void update() {
		this.area = this.area.getAreaInstance();
	}

	interface Area {
		double getBoundWest();

		double getBoundEast();

		double getBoundNorth();

		double getBoundSouth();

		double getSize();

		@Environment(EnvType.CLIENT)
		double method_11987();

		long getTargetRemainingTime();

		double getTargetSize();

		@Environment(EnvType.CLIENT)
		WorldBorderStage getStage();

		void onMaxWorldBorderRadiusChanged();

		void onCenterChanged();

		WorldBorder.Area getAreaInstance();
	}

	class MovingArea implements WorldBorder.Area {
		private final double oldSize;
		private final double newSize;
		private final long timeEnd;
		private final long timeStart;
		private final double timeDuration;

		private MovingArea(double d, double e, long l) {
			this.oldSize = d;
			this.newSize = e;
			this.timeDuration = (double)l;
			this.timeStart = SystemUtil.getMeasuringTimeMs();
			this.timeEnd = this.timeStart + l;
		}

		@Override
		public double getBoundWest() {
			return Math.max(WorldBorder.this.getCenterX() - this.getSize() / 2.0, (double)(-WorldBorder.this.maxWorldBorderRadius));
		}

		@Override
		public double getBoundNorth() {
			return Math.max(WorldBorder.this.getCenterZ() - this.getSize() / 2.0, (double)(-WorldBorder.this.maxWorldBorderRadius));
		}

		@Override
		public double getBoundEast() {
			return Math.min(WorldBorder.this.getCenterX() + this.getSize() / 2.0, (double)WorldBorder.this.maxWorldBorderRadius);
		}

		@Override
		public double getBoundSouth() {
			return Math.min(WorldBorder.this.getCenterZ() + this.getSize() / 2.0, (double)WorldBorder.this.maxWorldBorderRadius);
		}

		@Override
		public double getSize() {
			double d = (double)(SystemUtil.getMeasuringTimeMs() - this.timeStart) / this.timeDuration;
			return d < 1.0 ? MathHelper.lerp(d, this.oldSize, this.newSize) : this.newSize;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public double method_11987() {
			return Math.abs(this.oldSize - this.newSize) / (double)(this.timeEnd - this.timeStart);
		}

		@Override
		public long getTargetRemainingTime() {
			return this.timeEnd - SystemUtil.getMeasuringTimeMs();
		}

		@Override
		public double getTargetSize() {
			return this.newSize;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public WorldBorderStage getStage() {
			return this.newSize < this.oldSize ? WorldBorderStage.SHRINKING : WorldBorderStage.GROWING;
		}

		@Override
		public void onCenterChanged() {
		}

		@Override
		public void onMaxWorldBorderRadiusChanged() {
		}

		@Override
		public WorldBorder.Area getAreaInstance() {
			return (WorldBorder.Area)(this.getTargetRemainingTime() <= 0L ? WorldBorder.this.new StaticArea(this.newSize) : this);
		}
	}

	class StaticArea implements WorldBorder.Area {
		private final double size;
		private double boundWest;
		private double boundNorth;
		private double boundEast;
		private double boundSouth;

		public StaticArea(double d) {
			this.size = d;
			this.recalculateBounds();
		}

		@Override
		public double getBoundWest() {
			return this.boundWest;
		}

		@Override
		public double getBoundEast() {
			return this.boundEast;
		}

		@Override
		public double getBoundNorth() {
			return this.boundNorth;
		}

		@Override
		public double getBoundSouth() {
			return this.boundSouth;
		}

		@Override
		public double getSize() {
			return this.size;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public WorldBorderStage getStage() {
			return WorldBorderStage.STATIC;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public double method_11987() {
			return 0.0;
		}

		@Override
		public long getTargetRemainingTime() {
			return 0L;
		}

		@Override
		public double getTargetSize() {
			return this.size;
		}

		private void recalculateBounds() {
			this.boundWest = Math.max(WorldBorder.this.getCenterX() - this.size / 2.0, (double)(-WorldBorder.this.maxWorldBorderRadius));
			this.boundNorth = Math.max(WorldBorder.this.getCenterZ() - this.size / 2.0, (double)(-WorldBorder.this.maxWorldBorderRadius));
			this.boundEast = Math.min(WorldBorder.this.getCenterX() + this.size / 2.0, (double)WorldBorder.this.maxWorldBorderRadius);
			this.boundSouth = Math.min(WorldBorder.this.getCenterZ() + this.size / 2.0, (double)WorldBorder.this.maxWorldBorderRadius);
		}

		@Override
		public void onMaxWorldBorderRadiusChanged() {
			this.recalculateBounds();
		}

		@Override
		public void onCenterChanged() {
			this.recalculateBounds();
		}

		@Override
		public WorldBorder.Area getAreaInstance() {
			return this;
		}
	}
}
