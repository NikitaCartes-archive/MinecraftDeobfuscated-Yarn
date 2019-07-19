package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Box;

@Environment(EnvType.CLIENT)
public interface VisibleRegion {
	boolean intersects(Box boundingBox);

	void setOrigin(double x, double y, double z);
}
