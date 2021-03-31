package net.minecraft.world.biome.layer.util;

import net.minecraft.world.biome.BiomeIds;

public interface CoordinateTransformer extends BiomeIds {
	int transformX(int x);

	int transformZ(int y);
}
