package net.minecraft.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT)
public class BiomeColors {
	private static final BiomeColors.Proxy GRASS_COLOR = Biome::getGrassColorAt;
	private static final BiomeColors.Proxy FOLIAGE_COLOR = Biome::getFoliageColorAt;
	private static final BiomeColors.Proxy WATER_COLOR = (biome, blockPos) -> biome.getWaterColor();
	private static final BiomeColors.Proxy WATER_FOG_COLOR = (biome, blockPos) -> biome.getWaterFogColor();

	private static int colorAt(ExtendedBlockView extendedBlockView, BlockPos blockPos, BiomeColors.Proxy proxy) {
		int i = 0;
		int j = 0;
		int k = 0;
		int l = MinecraftClient.getInstance().field_1690.biomeBlendRadius;
		int m = (l * 2 + 1) * (l * 2 + 1);

		for (BlockPos.Mutable mutable : BlockPos.method_10068(
			blockPos.getX() - l, blockPos.getY(), blockPos.getZ() - l, blockPos.getX() + l, blockPos.getY(), blockPos.getZ() + l
		)) {
			int n = proxy.getColor(extendedBlockView.getBiome(mutable), mutable);
			i += (n & 0xFF0000) >> 16;
			j += (n & 0xFF00) >> 8;
			k += n & 0xFF;
		}

		return (i / m & 0xFF) << 16 | (j / m & 0xFF) << 8 | k / m & 0xFF;
	}

	public static int grassColorAt(ExtendedBlockView extendedBlockView, BlockPos blockPos) {
		return colorAt(extendedBlockView, blockPos, GRASS_COLOR);
	}

	public static int foliageColorAt(ExtendedBlockView extendedBlockView, BlockPos blockPos) {
		return colorAt(extendedBlockView, blockPos, FOLIAGE_COLOR);
	}

	public static int waterColorAt(ExtendedBlockView extendedBlockView, BlockPos blockPos) {
		return colorAt(extendedBlockView, blockPos, WATER_COLOR);
	}

	@Environment(EnvType.CLIENT)
	interface Proxy {
		int getColor(Biome biome, BlockPos blockPos);
	}
}
