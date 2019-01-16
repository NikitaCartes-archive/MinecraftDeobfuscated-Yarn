package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BoundingBox;

@Environment(EnvType.CLIENT)
public interface VisibleRegion {
	boolean intersects(BoundingBox boundingBox);

	void setOrigin(double d, double e, double f);
}
