package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents the surface color of a block when rendered from the {@link net.minecraft.client.gui.MapRenderer}.
 */
public class MapColor {
	public static final MapColor[] COLORS = new MapColor[64];
	public static final MapColor CLEAR = new MapColor(0, 0);
	public static final MapColor GRASS = new MapColor(1, 8368696);
	public static final MapColor SAND = new MapColor(2, 16247203);
	public static final MapColor WEB = new MapColor(3, 13092807);
	public static final MapColor LAVA = new MapColor(4, 16711680);
	public static final MapColor ICE = new MapColor(5, 10526975);
	public static final MapColor IRON = new MapColor(6, 10987431);
	public static final MapColor FOLIAGE = new MapColor(7, 31744);
	public static final MapColor WHITE = new MapColor(8, 16777215);
	public static final MapColor CLAY = new MapColor(9, 10791096);
	public static final MapColor DIRT = new MapColor(10, 9923917);
	public static final MapColor STONE = new MapColor(11, 7368816);
	public static final MapColor WATER = new MapColor(12, 4210943);
	public static final MapColor WOOD = new MapColor(13, 9402184);
	public static final MapColor QUARTZ = new MapColor(14, 16776437);
	public static final MapColor ORANGE = new MapColor(15, 14188339);
	public static final MapColor MAGENTA = new MapColor(16, 11685080);
	public static final MapColor LIGHT_BLUE = new MapColor(17, 6724056);
	public static final MapColor YELLOW = new MapColor(18, 15066419);
	public static final MapColor LIME = new MapColor(19, 8375321);
	public static final MapColor PINK = new MapColor(20, 15892389);
	public static final MapColor GRAY = new MapColor(21, 5000268);
	public static final MapColor LIGHT_GRAY = new MapColor(22, 10066329);
	public static final MapColor CYAN = new MapColor(23, 5013401);
	public static final MapColor PURPLE = new MapColor(24, 8339378);
	public static final MapColor BLUE = new MapColor(25, 3361970);
	public static final MapColor BROWN = new MapColor(26, 6704179);
	public static final MapColor GREEN = new MapColor(27, 6717235);
	public static final MapColor RED = new MapColor(28, 10040115);
	public static final MapColor BLACK = new MapColor(29, 1644825);
	public static final MapColor GOLD = new MapColor(30, 16445005);
	public static final MapColor DIAMOND = new MapColor(31, 6085589);
	public static final MapColor LAPIS = new MapColor(32, 4882687);
	public static final MapColor EMERALD = new MapColor(33, 55610);
	public static final MapColor SPRUCE = new MapColor(34, 8476209);
	public static final MapColor NETHER = new MapColor(35, 7340544);
	public static final MapColor WHITE_TERRACOTTA = new MapColor(36, 13742497);
	public static final MapColor ORANGE_TERRACOTTA = new MapColor(37, 10441252);
	public static final MapColor MAGENTA_TERRACOTTA = new MapColor(38, 9787244);
	public static final MapColor LIGHT_BLUE_TERRACOTTA = new MapColor(39, 7367818);
	public static final MapColor YELLOW_TERRACOTTA = new MapColor(40, 12223780);
	public static final MapColor LIME_TERRACOTTA = new MapColor(41, 6780213);
	public static final MapColor PINK_TERRACOTTA = new MapColor(42, 10505550);
	public static final MapColor GRAY_TERRACOTTA = new MapColor(43, 3746083);
	public static final MapColor LIGHT_GRAY_TERRACOTTA = new MapColor(44, 8874850);
	public static final MapColor CYAN_TERRACOTTA = new MapColor(45, 5725276);
	public static final MapColor PURPLE_TERRACOTTA = new MapColor(46, 8014168);
	public static final MapColor BLUE_TERRACOTTA = new MapColor(47, 4996700);
	public static final MapColor BROWN_TERRACOTTA = new MapColor(48, 4993571);
	public static final MapColor GREEN_TERRACOTTA = new MapColor(49, 5001770);
	public static final MapColor RED_TERRACOTTA = new MapColor(50, 9321518);
	public static final MapColor BLACK_TERRACOTTA = new MapColor(51, 2430480);
	public static final MapColor CRIMSON_NYLIUM = new MapColor(52, 12398641);
	public static final MapColor CRIMSON_STEM = new MapColor(53, 9715553);
	public static final MapColor CRIMSON_HYPHAE = new MapColor(54, 6035741);
	public static final MapColor WARPED_NYLIUM = new MapColor(55, 1474182);
	public static final MapColor WARPED_STEM = new MapColor(56, 3837580);
	public static final MapColor WARPED_HYPHAE = new MapColor(57, 5647422);
	public static final MapColor WARPED_WART = new MapColor(58, 1356933);
	public final int color;
	public final int id;

	private MapColor(int id, int color) {
		if (id >= 0 && id <= 63) {
			this.id = id;
			this.color = color;
			COLORS[id] = this;
		} else {
			throw new IndexOutOfBoundsException("Map colour ID must be between 0 and 63 (inclusive)");
		}
	}

	@Environment(EnvType.CLIENT)
	public int getRenderColor(int shade) {
		int i = 220;
		if (shade == 3) {
			i = 135;
		}

		if (shade == 2) {
			i = 255;
		}

		if (shade == 1) {
			i = 220;
		}

		if (shade == 0) {
			i = 180;
		}

		int j = (this.color >> 16 & 0xFF) * i / 255;
		int k = (this.color >> 8 & 0xFF) * i / 255;
		int l = (this.color & 0xFF) * i / 255;
		return 0xFF000000 | l << 16 | k << 8 | j;
	}
}
