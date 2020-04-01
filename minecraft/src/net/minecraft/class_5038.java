package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;

public class class_5038 extends OverworldDimension {
	public class_5038(World world, DimensionType dimensionType) {
		super(world, dimensionType);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_26493(int i, int j, Vector3f vector3f) {
		vector3f.set(1.0F - vector3f.getX(), 1.0F - vector3f.getY(), 1.0F - vector3f.getZ());
	}
}
