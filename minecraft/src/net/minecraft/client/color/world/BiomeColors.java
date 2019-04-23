package net.minecraft.client.color.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT)
public class BiomeColors {
	private static final BiomeColors.Provider GRASS_COLOR = Biome::getGrassColorAt;
	private static final BiomeColors.Provider FOLIAGE_COLOR = Biome::getFoliageColorAt;
	private static final BiomeColors.Provider WATER_COLOR = (biome, blockPos) -> biome.getWaterColor();
	private static final BiomeColors.Provider WATER_FOG_COLOR = (biome, blockPos) -> biome.getWaterFogColor();

	private static int getColor(ExtendedBlockView extendedBlockView, BlockPos blockPos, BiomeColors.Provider provider) {
		int i = 0;
		int j = 0;
		int k = 0;
		int l = MinecraftClient.getInstance().options.biomeBlendRadius;
		int m = (l * 2 + 1) * (l * 2 + 1);

		for (BlockPos blockPos2 : BlockPos.iterate(
			blockPos.getX() - l, blockPos.getY(), blockPos.getZ() - l, blockPos.getX() + l, blockPos.getY(), blockPos.getZ() + l
		)) {
			int n = provider.getColor(extendedBlockView.getBiome(blockPos2), blockPos2);
			i += (n & 0xFF0000) >> 16;
			j += (n & 0xFF00) >> 8;
			k += n & 0xFF;
		}

		return (i / m & 0xFF) << 16 | (j / m & 0xFF) << 8 | k / m & 0xFF;
	}

	public static int getGrassColor(ExtendedBlockView extendedBlockView, BlockPos blockPos) {
		return getColor(extendedBlockView, blockPos, GRASS_COLOR);
	}

	public static int getFoliageColor(ExtendedBlockView extendedBlockView, BlockPos blockPos) {
		return getColor(extendedBlockView, blockPos, FOLIAGE_COLOR);
	}

	public static int getWaterColor(ExtendedBlockView extendedBlockView, BlockPos blockPos) {
		return getColor(extendedBlockView, blockPos, WATER_COLOR);
	}

	@Environment(EnvType.CLIENT)
	interface Provider {
		int getColor(Biome biome, BlockPos blockPos);
	}
}
