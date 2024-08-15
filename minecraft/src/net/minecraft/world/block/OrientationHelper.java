package net.minecraft.world.block;

import javax.annotation.Nullable;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class OrientationHelper {
	@Nullable
	public static WireOrientation getEmissionOrientation(World world, @Nullable Direction up, @Nullable Direction front) {
		if (world.getEnabledFeatures().contains(FeatureFlags.REDSTONE_EXPERIMENTS)) {
			WireOrientation wireOrientation = WireOrientation.random(world.random);
			if (front != null) {
				wireOrientation = wireOrientation.withUp(front);
			}

			if (up != null) {
				wireOrientation = wireOrientation.withFront(up);
			}

			return wireOrientation;
		} else {
			return null;
		}
	}

	@Nullable
	public static WireOrientation withFrontNullable(@Nullable WireOrientation orientation, Direction direction) {
		return orientation == null ? null : orientation.withFront(direction);
	}
}
