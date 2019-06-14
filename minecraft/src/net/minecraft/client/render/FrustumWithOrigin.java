package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Box;

@Environment(EnvType.CLIENT)
public class FrustumWithOrigin implements VisibleRegion {
	private final Frustum field_4504;
	private double originX;
	private double originY;
	private double originZ;

	public FrustumWithOrigin() {
		this(GlMatrixFrustum.method_3696());
	}

	public FrustumWithOrigin(Frustum frustum) {
		this.field_4504 = frustum;
	}

	@Override
	public void setOrigin(double d, double e, double f) {
		this.originX = d;
		this.originY = e;
		this.originZ = f;
	}

	public boolean intersects(double d, double e, double f, double g, double h, double i) {
		return this.field_4504.intersects(d - this.originX, e - this.originY, f - this.originZ, g - this.originX, h - this.originY, i - this.originZ);
	}

	@Override
	public boolean intersects(Box box) {
		return this.intersects(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}
}
