package net.minecraft.client.color.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT)
public class BiomeColors {
	private static final BiomeColors.Provider GRASS_COLOR = Biome::getGrassColorAt;
	private static final BiomeColors.Provider FOLIAGE_COLOR = Biome::getFoliageColorAt;
	private static final BiomeColors.Provider WATER_COLOR = (biome, pos) -> biome.getWaterColor();
	private static final BiomeColors.Provider WATER_FOG_COLOR = (biome, pos) -> biome.getWaterFogColor();

	private static int getColor(BlockRenderView view, BlockPos pos, BiomeColors.Provider provider) {
		int i = 0;
		int j = 0;
		int k = 0;
		int l = MinecraftClient.getInstance().options.biomeBlendRadius;
		if (l == 0) {
			return provider.getColor(view.getBiome(pos), pos);
		} else {
			int m = (l * 2 + 1) * (l * 2 + 1);
			CuboidBlockIterator cuboidBlockIterator = new CuboidBlockIterator(pos.getX() - l, pos.getY(), pos.getZ() - l, pos.getX() + l, pos.getY(), pos.getZ() + l);
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			while (cuboidBlockIterator.step()) {
				mutable.set(cuboidBlockIterator.getX(), cuboidBlockIterator.getY(), cuboidBlockIterator.getZ());
				int n = provider.getColor(view.getBiome(mutable), mutable);
				i += (n & 0xFF0000) >> 16;
				j += (n & 0xFF00) >> 8;
				k += n & 0xFF;
			}

			return (i / m & 0xFF) << 16 | (j / m & 0xFF) << 8 | k / m & 0xFF;
		}
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

	@Environment(EnvType.CLIENT)
	interface Provider {
		int getColor(Biome biome, BlockPos pos);
	}
}
