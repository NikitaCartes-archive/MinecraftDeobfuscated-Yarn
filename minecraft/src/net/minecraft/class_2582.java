package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.tuple.Pair;

public enum class_2582 {
	field_11834("base", "b"),
	field_11839("square_bottom_left", "bl", "   ", "   ", "#  "),
	field_11806("square_bottom_right", "br", "   ", "   ", "  #"),
	field_11831("square_top_left", "tl", "#  ", "   ", "   "),
	field_11848("square_top_right", "tr", "  #", "   ", "   "),
	field_11810("stripe_bottom", "bs", "   ", "   ", "###"),
	field_11829("stripe_top", "ts", "###", "   ", "   "),
	field_11837("stripe_left", "ls", "#  ", "#  ", "#  "),
	field_11813("stripe_right", "rs", "  #", "  #", "  #"),
	field_11819("stripe_center", "cs", " # ", " # ", " # "),
	field_11838("stripe_middle", "ms", "   ", "###", "   "),
	field_11807("stripe_downright", "drs", "#  ", " # ", "  #"),
	field_11820("stripe_downleft", "dls", "  #", " # ", "#  "),
	field_11814("small_stripes", "ss", "# #", "# #", "   "),
	field_11844("cross", "cr", "# #", " # ", "# #"),
	field_11830("straight_cross", "sc", " # ", "###", " # "),
	field_11811("triangle_bottom", "bt", "   ", " # ", "# #"),
	field_11849("triangle_top", "tt", "# #", " # ", "   "),
	field_11822("triangles_bottom", "bts", "   ", "# #", " # "),
	field_11815("triangles_top", "tts", " # ", "# #", "   "),
	field_11847("diagonal_left", "ld", "## ", "#  ", "   "),
	field_11835("diagonal_up_right", "rd", "   ", "  #", " ##"),
	field_11817("diagonal_up_left", "lud", "   ", "#  ", "## "),
	field_11842("diagonal_right", "rud", " ##", "  #", "   "),
	field_11826("circle", "mc", "   ", " # ", "   "),
	field_11821("rhombus", "mr", " # ", "# #", " # "),
	field_11828("half_vertical", "vh", "## ", "## ", "## "),
	field_11843("half_horizontal", "hh", "###", "###", "   "),
	field_11818("half_vertical_right", "vhr", " ##", " ##", " ##"),
	field_11836("half_horizontal_bottom", "hhb", "   ", "###", "###"),
	field_11840("border", "bo", "###", "# #", "###"),
	field_11816("curly_border", "cbo", new class_1799(class_2246.field_10597)),
	field_11827("gradient", "gra", "# #", " # ", " # "),
	field_11850("gradient_up", "gru", " # ", " # ", "# #"),
	field_11809("bricks", "bri", new class_1799(class_2246.field_10104)),
	field_11823("creeper", "cre", new class_1799(class_1802.field_8681)),
	field_11845("skull", "sku", new class_1799(class_1802.field_8791)),
	field_11812("flower", "flo", new class_1799(class_2246.field_10554)),
	field_11825("mojang", "moj", new class_1799(class_1802.field_8367));

	public static final int field_11846 = values().length;
	private final String field_11808;
	private final String field_11824;
	private final String[] field_11841 = new String[3];
	private class_1799 field_11832 = class_1799.field_8037;

	private class_2582(String string2, String string3) {
		this.field_11808 = string2;
		this.field_11824 = string3;
	}

	private class_2582(String string2, String string3, class_1799 arg) {
		this(string2, string3);
		this.field_11832 = arg;
	}

	private class_2582(String string2, String string3, String string4, String string5, String string6) {
		this(string2, string3);
		this.field_11841[0] = string4;
		this.field_11841[1] = string5;
		this.field_11841[2] = string6;
	}

	@Environment(EnvType.CLIENT)
	public String method_10947() {
		return this.field_11808;
	}

	public String method_10945() {
		return this.field_11824;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static class_2582 method_10946(String string) {
		for (class_2582 lv : values()) {
			if (lv.field_11824.equals(string)) {
				return lv;
			}
		}

		return null;
	}

	public static class class_3750 {
		private final List<Pair<class_2582, class_1767>> field_16548 = Lists.<Pair<class_2582, class_1767>>newArrayList();

		public class_2582.class_3750 method_16376(class_2582 arg, class_1767 arg2) {
			this.field_16548.add(Pair.of(arg, arg2));
			return this;
		}

		public class_2499 method_16375() {
			class_2499 lv = new class_2499();

			for (Pair<class_2582, class_1767> pair : this.field_16548) {
				class_2487 lv2 = new class_2487();
				lv2.method_10582("Pattern", pair.getLeft().field_11824);
				lv2.method_10569("Color", pair.getRight().method_7789());
				lv.method_10606(lv2);
			}

			return lv;
		}
	}
}
