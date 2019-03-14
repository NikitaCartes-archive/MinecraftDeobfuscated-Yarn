package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BoundingBox;

@Environment(EnvType.CLIENT)
public class FrustumWithOrigin implements VisibleRegion {
	private final Frustum frustum;
	private double originX;
	private double originY;
	private double originZ;

	public FrustumWithOrigin() {
		this(GlMatrixFrustum.get());
	}

	public FrustumWithOrigin(Frustum frustum) {
		this.frustum = frustum;
	}

	@Override
	public void setOrigin(double d, double e, double f) {
		this.originX = d;
		this.originY = e;
		this.originZ = f;
	}

	public boolean intersects(double d, double e, double f, double g, double h, double i) {
		return this.frustum.intersects(d - this.originX, e - this.originY, f - this.originZ, g - this.originX, h - this.originY, i - this.originZ);
	}

	@Override
	public boolean intersects(BoundingBox boundingBox) {
		return this.intersects(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
	}
}
