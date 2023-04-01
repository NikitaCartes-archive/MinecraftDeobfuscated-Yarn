package net.minecraft.client.color.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.ColorResolver;

@Environment(EnvType.CLIENT)
public class BiomeColors {
	public static final ColorResolver GRASS_COLOR = (registryEntry, x, z) -> registryEntry.value().getGrassColorAt(registryEntry, x, z);
	public static final ColorResolver FOLIAGE_COLOR = (registryEntry, x, z) -> registryEntry.value().getFoliageColor(registryEntry);
	public static final ColorResolver WATER_COLOR = (registryEntry, d, e) -> registryEntry.value().getWaterColor(registryEntry);

	private static int getColor(BlockRenderView world, BlockPos pos, ColorResolver resolver) {
		return world.getColor(pos, resolver);
	}

	public static int getGrassColor(BlockRenderView world, BlockPos pos) {
		return getColor(world, pos, GRASS_COLOR);
	}

	public static int getFoliageColor(BlockRenderView world, BlockPos pos) {
		return getColor(world, pos, FOLIAGE_COLOR);
	}

	public static int getWaterColor(BlockRenderView world, BlockPos pos) {
		return getColor(world, pos, WATER_COLOR);
	}
}
