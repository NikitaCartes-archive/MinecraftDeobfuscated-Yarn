package net.minecraft.world.border;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class WorldBorder {
	private final List<WorldBorderListener> listeners = Lists.<WorldBorderListener>newArrayList();
	private double damagePerBlock = 0.2;
	private double buffer = 5.0;
	private int warningTime = 15;
	private int warningBlocks = 5;
	private double centerX;
	private double centerZ;
	private int maxWorldBorderRadius = 29999984;
	private WorldBorder.Area area = new WorldBorder.StaticArea(6.0E7);
	public static final WorldBorder.class_5200 field_24122 = new WorldBorder.class_5200(0.0, 0.0, 0.2, 5.0, 5, 15, 6.0E7, 0L, 0.0);

	public boolean contains(BlockPos pos) {
		return (double)(pos.getX() + 1) > this.getBoundWest()
			&& (double)pos.getX() < this.getBoundEast()
			&& (double)(pos.getZ() + 1) > this.getBoundNorth()
			&& (double)pos.getZ() < this.getBoundSouth();
	}

	public boolean contains(ChunkPos pos) {
		return (double)pos.getEndX() > this.getBoundWest()
			&& (double)pos.getStartX() < this.getBoundEast()
			&& (double)pos.getEndZ() > this.getBoundNorth()
			&& (double)pos.getStartZ() < this.getBoundSouth();
	}

	public boolean contains(Box box) {
		return box.x2 > this.getBoundWest() && box.x1 < this.getBoundEast() && box.z2 > this.getBoundNorth() && box.z1 < this.getBoundSouth();
	}

	public double getDistanceInsideBorder(Entity entity) {
		return this.getDistanceInsideBorder(entity.getX(), entity.getZ());
	}

	public VoxelShape asVoxelShape() {
		return this.area.asVoxelShape();
	}

	public double getDistanceInsideBorder(double x, double z) {
		double d = z - this.getBoundNorth();
		double e = this.getBoundSouth() - z;
		double f = x - this.getBoundWest();
		double g = this.getBoundEast() - x;
		double h = Math.min(f, g);
		h = Math.min(h, d);
		return Math.min(h, e);
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

	public void setCenter(double x, double z) {
		this.centerX = x;
		this.centerZ = z;
		this.area.onCenterChanged();

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onCenterChanged(this, x, z);
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

	public void setSize(double size) {
		this.area = new WorldBorder.StaticArea(size);

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onSizeChange(this, size);
		}
	}

	public void interpolateSize(double fromSize, double toSize, long time) {
		this.area = (WorldBorder.Area)(fromSize == toSize ? new WorldBorder.StaticArea(toSize) : new WorldBorder.MovingArea(fromSize, toSize, time));

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onInterpolateSize(this, fromSize, toSize, time);
		}
	}

	protected List<WorldBorderListener> getListeners() {
		return Lists.<WorldBorderListener>newArrayList(this.listeners);
	}

	public void addListener(WorldBorderListener listener) {
		this.listeners.add(listener);
	}

	public void setMaxWorldBorderRadius(int i) {
		this.maxWorldBorderRadius = i;
		this.area.onMaxWorldBorderRadiusChanged();
	}

	public int getMaxWorldBorderRadius() {
		return this.maxWorldBorderRadius;
	}

	public double getBuffer() {
		return this.buffer;
	}

	public void setBuffer(double buffer) {
		this.buffer = buffer;

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onSafeZoneChanged(this, buffer);
		}
	}

	public double getDamagePerBlock() {
		return this.damagePerBlock;
	}

	public void setDamagePerBlock(double damagePerBlock) {
		this.damagePerBlock = damagePerBlock;

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onDamagePerBlockChanged(this, damagePerBlock);
		}
	}

	@Environment(EnvType.CLIENT)
	public double getShrinkingSpeed() {
		return this.area.getShrinkingSpeed();
	}

	public int getWarningTime() {
		return this.warningTime;
	}

	public void setWarningTime(int warningTime) {
		this.warningTime = warningTime;

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onWarningTimeChanged(this, warningTime);
		}
	}

	public int getWarningBlocks() {
		return this.warningBlocks;
	}

	public void setWarningBlocks(int warningBlocks) {
		this.warningBlocks = warningBlocks;

		for (WorldBorderListener worldBorderListener : this.getListeners()) {
			worldBorderListener.onWarningBlocksChanged(this, warningBlocks);
		}
	}

	public void tick() {
		this.area = this.area.getAreaInstance();
	}

	public WorldBorder.class_5200 method_27355() {
		return new WorldBorder.class_5200(this);
	}

	public void load(WorldBorder.class_5200 levelProperties) {
		this.setCenter(levelProperties.method_27356(), levelProperties.method_27359());
		this.setDamagePerBlock(levelProperties.method_27360());
		this.setBuffer(levelProperties.method_27361());
		this.setWarningBlocks(levelProperties.method_27362());
		this.setWarningTime(levelProperties.method_27363());
		if (levelProperties.method_27365() > 0L) {
			this.interpolateSize(levelProperties.method_27364(), levelProperties.method_27366(), levelProperties.method_27365());
		} else {
			this.setSize(levelProperties.method_27364());
		}
	}

	interface Area {
		double getBoundWest();

		double getBoundEast();

		double getBoundNorth();

		double getBoundSouth();

		double getSize();

		@Environment(EnvType.CLIENT)
		double getShrinkingSpeed();

		long getTargetRemainingTime();

		double getTargetSize();

		@Environment(EnvType.CLIENT)
		WorldBorderStage getStage();

		void onMaxWorldBorderRadiusChanged();

		void onCenterChanged();

		WorldBorder.Area getAreaInstance();

		VoxelShape asVoxelShape();
	}

	class MovingArea implements WorldBorder.Area {
		private final double oldSize;
		private final double newSize;
		private final long timeEnd;
		private final long timeStart;
		private final double timeDuration;

		private MovingArea(double oldSize, double newSize, long duration) {
			this.oldSize = oldSize;
			this.newSize = newSize;
			this.timeDuration = (double)duration;
			this.timeStart = Util.getMeasuringTimeMs();
			this.timeEnd = this.timeStart + duration;
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
			double d = (double)(Util.getMeasuringTimeMs() - this.timeStart) / this.timeDuration;
			return d < 1.0 ? MathHelper.lerp(d, this.oldSize, this.newSize) : this.newSize;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public double getShrinkingSpeed() {
			return Math.abs(this.oldSize - this.newSize) / (double)(this.timeEnd - this.timeStart);
		}

		@Override
		public long getTargetRemainingTime() {
			return this.timeEnd - Util.getMeasuringTimeMs();
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

		@Override
		public VoxelShape asVoxelShape() {
			return VoxelShapes.combineAndSimplify(
				VoxelShapes.UNBOUNDED,
				VoxelShapes.cuboid(
					Math.floor(this.getBoundWest()),
					Double.NEGATIVE_INFINITY,
					Math.floor(this.getBoundNorth()),
					Math.ceil(this.getBoundEast()),
					Double.POSITIVE_INFINITY,
					Math.ceil(this.getBoundSouth())
				),
				BooleanBiFunction.ONLY_FIRST
			);
		}
	}

	class StaticArea implements WorldBorder.Area {
		private final double size;
		private double boundWest;
		private double boundNorth;
		private double boundEast;
		private double boundSouth;
		private VoxelShape shape;

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
			return WorldBorderStage.STATIONARY;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public double getShrinkingSpeed() {
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
			this.shape = VoxelShapes.combineAndSimplify(
				VoxelShapes.UNBOUNDED,
				VoxelShapes.cuboid(
					Math.floor(this.getBoundWest()),
					Double.NEGATIVE_INFINITY,
					Math.floor(this.getBoundNorth()),
					Math.ceil(this.getBoundEast()),
					Double.POSITIVE_INFINITY,
					Math.ceil(this.getBoundSouth())
				),
				BooleanBiFunction.ONLY_FIRST
			);
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

		@Override
		public VoxelShape asVoxelShape() {
			return this.shape;
		}
	}

	public static class class_5200 {
		private final double field_24123;
		private final double field_24124;
		private final double field_24125;
		private final double field_24126;
		private final int field_24127;
		private final int field_24128;
		private final double field_24129;
		private final long field_24130;
		private final double field_24131;

		private class_5200(double d, double e, double f, double g, int i, int j, double h, long l, double k) {
			this.field_24123 = d;
			this.field_24124 = e;
			this.field_24125 = f;
			this.field_24126 = g;
			this.field_24127 = i;
			this.field_24128 = j;
			this.field_24129 = h;
			this.field_24130 = l;
			this.field_24131 = k;
		}

		private class_5200(WorldBorder worldBorder) {
			this.field_24123 = worldBorder.getCenterX();
			this.field_24124 = worldBorder.getCenterZ();
			this.field_24125 = worldBorder.getDamagePerBlock();
			this.field_24126 = worldBorder.getBuffer();
			this.field_24127 = worldBorder.getWarningBlocks();
			this.field_24128 = worldBorder.getWarningTime();
			this.field_24129 = worldBorder.getSize();
			this.field_24130 = worldBorder.getTargetRemainingTime();
			this.field_24131 = worldBorder.getTargetSize();
		}

		public double method_27356() {
			return this.field_24123;
		}

		public double method_27359() {
			return this.field_24124;
		}

		public double method_27360() {
			return this.field_24125;
		}

		public double method_27361() {
			return this.field_24126;
		}

		public int method_27362() {
			return this.field_24127;
		}

		public int method_27363() {
			return this.field_24128;
		}

		public double method_27364() {
			return this.field_24129;
		}

		public long method_27365() {
			return this.field_24130;
		}

		public double method_27366() {
			return this.field_24131;
		}

		public static WorldBorder.class_5200 method_27358(CompoundTag compoundTag, WorldBorder.class_5200 arg) {
			double d = arg.field_24123;
			double e = arg.field_24124;
			double f = arg.field_24129;
			long l = arg.field_24130;
			double g = arg.field_24131;
			double h = arg.field_24126;
			double i = arg.field_24125;
			int j = arg.field_24127;
			int k = arg.field_24128;
			if (compoundTag.contains("BorderCenterX", 99)) {
				d = compoundTag.getDouble("BorderCenterX");
			}

			if (compoundTag.contains("BorderCenterZ", 99)) {
				e = compoundTag.getDouble("BorderCenterZ");
			}

			if (compoundTag.contains("BorderSize", 99)) {
				f = compoundTag.getDouble("BorderSize");
			}

			if (compoundTag.contains("BorderSizeLerpTime", 99)) {
				l = compoundTag.getLong("BorderSizeLerpTime");
			}

			if (compoundTag.contains("BorderSizeLerpTarget", 99)) {
				g = compoundTag.getDouble("BorderSizeLerpTarget");
			}

			if (compoundTag.contains("BorderSafeZone", 99)) {
				h = compoundTag.getDouble("BorderSafeZone");
			}

			if (compoundTag.contains("BorderDamagePerBlock", 99)) {
				i = compoundTag.getDouble("BorderDamagePerBlock");
			}

			if (compoundTag.contains("BorderWarningBlocks", 99)) {
				j = compoundTag.getInt("BorderWarningBlocks");
			}

			if (compoundTag.contains("BorderWarningTime", 99)) {
				k = compoundTag.getInt("BorderWarningTime");
			}

			return new WorldBorder.class_5200(d, e, i, h, j, k, f, l, g);
		}

		public void method_27357(CompoundTag compoundTag) {
			compoundTag.putDouble("BorderCenterX", this.field_24123);
			compoundTag.putDouble("BorderCenterZ", this.field_24124);
			compoundTag.putDouble("BorderSize", this.field_24129);
			compoundTag.putLong("BorderSizeLerpTime", this.field_24130);
			compoundTag.putDouble("BorderSafeZone", this.field_24126);
			compoundTag.putDouble("BorderDamagePerBlock", this.field_24125);
			compoundTag.putDouble("BorderSizeLerpTarget", this.field_24131);
			compoundTag.putDouble("BorderWarningBlocks", (double)this.field_24127);
			compoundTag.putDouble("BorderWarningTime", (double)this.field_24128);
		}
	}
}
