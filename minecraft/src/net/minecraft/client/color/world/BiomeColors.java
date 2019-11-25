package net.minecraft.client.color.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.ColorResolver;

@Environment(EnvType.CLIENT)
public class BiomeColors {
	public static final ColorResolver GRASS_COLOR = Biome::getGrassColorAt;
	public static final ColorResolver FOLIAGE_COLOR = (biome, d, e) -> biome.getFoliageColorAt();
	public static final ColorResolver WATER_COLOR = (biome, d, e) -> biome.getWaterColor();

	private static int getColor(BlockRenderView blockRenderView, BlockPos blockPos, ColorResolver colorResolver) {
		return blockRenderView.getColor(blockPos, colorResolver);
	}

	public static int getGrassColor(BlockRenderView view, BlockPos pos) {
		return getColor(view, pos, GRASS_COLOR);
	}

	public static int getFoliageColor(BlockRenderView view, BlockPos pos) {
		return getColor(view, pos, FOLIAGE_COLOR);
	}

	public static int getWaterColor(BlockRenderView view, BlockPos pos) {
		return getColor(view, pos, WATER_COLOR);
	}
}
