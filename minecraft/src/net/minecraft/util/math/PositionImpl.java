package net.minecraft.util.math;

public class PositionImpl implements Position {
	protected final double x;
	protected final double y;
	protected final double z;

	public PositionImpl(double d, double e, double f) {
		this.x = d;
		this.y = e;
		this.z = f;
	}

	@Override
	public double getX() {
		return this.x;
	}

	@Override
	public double getY() {
		return this.y;
	}

	@Override
	public double getZ() {
		return this.z;
	}
}
