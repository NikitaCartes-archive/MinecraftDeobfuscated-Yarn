package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class MaterialColor {
	public static final MaterialColor[] COLORS = new MaterialColor[64];
	public static final MaterialColor AIR = new MaterialColor(0, 0);
	public static final MaterialColor GRASS = new MaterialColor(1, 8368696);
	public static final MaterialColor SAND = new MaterialColor(2, 16247203);
	public static final MaterialColor WEB = new MaterialColor(3, 13092807);
	public static final MaterialColor LAVA = new MaterialColor(4, 16711680);
	public static final MaterialColor ICE = new MaterialColor(5, 10526975);
	public static final MaterialColor IRON = new MaterialColor(6, 10987431);
	public static final MaterialColor FOLIAGE = new MaterialColor(7, 31744);
	public static final MaterialColor WHITE = new MaterialColor(8, 16777215);
	public static final MaterialColor CLAY = new MaterialColor(9, 10791096);
	public static final MaterialColor DIRT = new MaterialColor(10, 9923917);
	public static final MaterialColor STONE = new MaterialColor(11, 7368816);
	public static final MaterialColor WATER = new MaterialColor(12, 4210943);
	public static final MaterialColor WOOD = new MaterialColor(13, 9402184);
	public static final MaterialColor QUARTZ = new MaterialColor(14, 16776437);
	public static final MaterialColor ORANGE = new MaterialColor(15, 14188339);
	public static final MaterialColor MAGENTA = new MaterialColor(16, 11685080);
	public static final MaterialColor LIGHT_BLUE = new MaterialColor(17, 6724056);
	public static final MaterialColor YELLOW = new MaterialColor(18, 15066419);
	public static final MaterialColor LIME = new MaterialColor(19, 8375321);
	public static final MaterialColor PINK = new MaterialColor(20, 15892389);
	public static final MaterialColor GRAY = new MaterialColor(21, 5000268);
	public static final MaterialColor LIGHT_GRAY = new MaterialColor(22, 10066329);
	public static final MaterialColor CYAN = new MaterialColor(23, 5013401);
	public static final MaterialColor PURPLE = new MaterialColor(24, 8339378);
	public static final MaterialColor BLUE = new MaterialColor(25, 3361970);
	public static final MaterialColor BROWN = new MaterialColor(26, 6704179);
	public static final MaterialColor GREEN = new MaterialColor(27, 6717235);
	public static final MaterialColor RED = new MaterialColor(28, 10040115);
	public static final MaterialColor BLACK = new MaterialColor(29, 1644825);
	public static final MaterialColor GOLD = new MaterialColor(30, 16445005);
	public static final MaterialColor DIAMOND = new MaterialColor(31, 6085589);
	public static final MaterialColor LAPIS = new MaterialColor(32, 4882687);
	public static final MaterialColor EMERALD = new MaterialColor(33, 55610);
	public static final MaterialColor SPRUCE = new MaterialColor(34, 8476209);
	public static final MaterialColor NETHER = new MaterialColor(35, 7340544);
	public static final MaterialColor WHITE_TERRACOTTA = new MaterialColor(36, 13742497);
	public static final MaterialColor ORANGE_TERRACOTTA = new MaterialColor(37, 10441252);
	public static final MaterialColor MAGENTA_TERRACOTTA = new MaterialColor(38, 9787244);
	public static final MaterialColor LIGHT_BLUE_TERRACOTTA = new MaterialColor(39, 7367818);
	public static final MaterialColor YELLOW_TERRACOTTA = new MaterialColor(40, 12223780);
	public static final MaterialColor LIME_TERRACOTTA = new MaterialColor(41, 6780213);
	public static final MaterialColor PINK_TERRACOTTA = new MaterialColor(42, 10505550);
	public static final MaterialColor GRAY_TERRACOTTA = new MaterialColor(43, 3746083);
	public static final MaterialColor LIGHT_GRAY_TERRACOTTA = new MaterialColor(44, 8874850);
	public static final MaterialColor CYAN_TERRACOTTA = new MaterialColor(45, 5725276);
	public static final MaterialColor PURPLE_TERRACOTTA = new MaterialColor(46, 8014168);
	public static final MaterialColor BLUE_TERRACOTTA = new MaterialColor(47, 4996700);
	public static final MaterialColor BROWN_TERRACOTTA = new MaterialColor(48, 4993571);
	public static final MaterialColor GREEN_TERRACOTTA = new MaterialColor(49, 5001770);
	public static final MaterialColor RED_TERRACOTTA = new MaterialColor(50, 9321518);
	public static final MaterialColor BLACK_TERRACOTTA = new MaterialColor(51, 2430480);
	public static final MaterialColor field_25702 = new MaterialColor(52, 12398641);
	public static final MaterialColor field_25703 = new MaterialColor(53, 9715553);
	public static final MaterialColor field_25704 = new MaterialColor(54, 6035741);
	public static final MaterialColor field_25705 = new MaterialColor(55, 1474182);
	public static final MaterialColor field_25706 = new MaterialColor(56, 3837580);
	public static final MaterialColor field_25707 = new MaterialColor(57, 5647422);
	public static final MaterialColor field_25708 = new MaterialColor(58, 1356933);
	public final int color;
	public final int id;

	private MaterialColor(int id, int color) {
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
