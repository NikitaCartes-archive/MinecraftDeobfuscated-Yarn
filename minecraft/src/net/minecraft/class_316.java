package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_316 {
	public static final class_4067 field_18189 = new class_4067("options.biomeBlendRadius", 0.0, 7.0, 1.0F, arg -> (double)arg.field_1878, (arg, double_) -> {
		arg.field_1878 = class_3532.method_15340((int)double_.doubleValue(), 0, 7);
		class_310.method_1551().field_1769.method_3279();
	}, (arg, arg2) -> {
		double d = arg2.method_18613(arg);
		String string = arg2.method_18518();
		if (d == 0.0) {
			return string + class_1074.method_4662("options.off");
		} else {
			int i = (int)d * 2 + 1;
			return string + i + "x" + i;
		}
	});
	public static final class_4067 field_1940 = new class_4067("options.chat.height.focused", 0.0, 1.0, 0.0F, arg -> arg.field_1838, (arg, double_) -> {
		arg.field_1838 = double_;
		class_310.method_1551().field_1705.method_1743().method_1817();
	}, (arg, arg2) -> {
		double d = arg2.method_18611(arg2.method_18613(arg));
		return arg2.method_18518() + class_338.method_1818(d) + "px";
	});
	public static final class_4067 field_1939 = new class_4067("options.chat.height.unfocused", 0.0, 1.0, 0.0F, arg -> arg.field_1825, (arg, double_) -> {
		arg.field_1825 = double_;
		class_310.method_1551().field_1705.method_1743().method_1817();
	}, (arg, arg2) -> {
		double d = arg2.method_18611(arg2.method_18613(arg));
		return arg2.method_18518() + class_338.method_1818(d) + "px";
	});
	public static final class_4067 field_1921 = new class_4067("options.chat.opacity", 0.0, 1.0, 0.0F, arg -> arg.field_1820, (arg, double_) -> {
		arg.field_1820 = double_;
		class_310.method_1551().field_1705.method_1743().method_1817();
	}, (arg, arg2) -> {
		double d = arg2.method_18611(arg2.method_18613(arg));
		return arg2.method_18518() + (int)(d * 90.0 + 10.0) + "%";
	});
	public static final class_4067 field_1946 = new class_4067("options.chat.scale", 0.0, 1.0, 0.0F, arg -> arg.field_1908, (arg, double_) -> {
		arg.field_1908 = double_;
		class_310.method_1551().field_1705.method_1743().method_1817();
	}, (arg, arg2) -> {
		double d = arg2.method_18611(arg2.method_18613(arg));
		String string = arg2.method_18518();
		return d == 0.0 ? string + class_1074.method_4662("options.off") : string + (int)(d * 100.0) + "%";
	});
	public static final class_4067 field_1941 = new class_4067("options.chat.width", 0.0, 1.0, 0.0F, arg -> arg.field_1915, (arg, double_) -> {
		arg.field_1915 = double_;
		class_310.method_1551().field_1705.method_1743().method_1817();
	}, (arg, arg2) -> {
		double d = arg2.method_18611(arg2.method_18613(arg));
		return arg2.method_18518() + class_338.method_1806(d) + "px";
	});
	public static final class_4067 field_1964 = new class_4067(
		"options.fov", 30.0, 110.0, 1.0F, arg -> arg.field_1826, (arg, double_) -> arg.field_1826 = double_, (arg, arg2) -> {
			double d = arg2.method_18613(arg);
			String string = arg2.method_18518();
			if (d == 70.0) {
				return string + class_1074.method_4662("options.fov.min");
			} else {
				return d == arg2.method_18617() ? string + class_1074.method_4662("options.fov.max") : string + (int)d;
			}
		}
	);
	public static final class_4067 field_1935 = new class_4067(
		"options.framerateLimit",
		10.0,
		260.0,
		10.0F,
		arg -> (double)arg.field_1909,
		(arg, double_) -> {
			arg.field_1909 = (int)double_.doubleValue();
			class_310.method_1551().field_1704.method_15999(arg.field_1909);
		},
		(arg, arg2) -> {
			double d = arg2.method_18613(arg);
			String string = arg2.method_18518();
			return d == arg2.method_18617()
				? string + class_1074.method_4662("options.framerateLimit.max")
				: string + class_1074.method_4662("options.framerate", (int)d);
		}
	);
	public static final class_4067 field_1931 = new class_4067(
		"options.fullscreen.resolution",
		0.0,
		0.0,
		1.0F,
		arg -> (double)class_310.method_1551().field_1704.method_4508(),
		(arg, double_) -> class_310.method_1551().field_1704.method_4505((int)double_.doubleValue()),
		(arg, arg2) -> {
			double d = arg2.method_18613(arg);
			String string = arg2.method_18518();
			return d == 0.0 ? string + class_1074.method_4662("options.fullscreen.current") : string + class_310.method_1551().field_1704.method_4487((int)d - 1);
		}
	);
	public static final class_4067 field_1945 = new class_4067(
		"options.gamma", 0.0, 1.0, 0.0F, arg -> arg.field_1840, (arg, double_) -> arg.field_1840 = double_, (arg, arg2) -> {
			double d = arg2.method_18611(arg2.method_18613(arg));
			String string = arg2.method_18518();
			if (d == 0.0) {
				return string + class_1074.method_4662("options.gamma.min");
			} else {
				return d == 1.0 ? string + class_1074.method_4662("options.gamma.max") : string + "+" + (int)(d * 100.0) + "%";
			}
		}
	);
	public static final class_4067 field_18190 = new class_4067(
		"options.mipmapLevels", 0.0, 4.0, 1.0F, arg -> (double)arg.field_1856, (arg, double_) -> arg.field_1856 = (int)double_.doubleValue(), (arg, arg2) -> {
			double d = arg2.method_18613(arg);
			String string = arg2.method_18518();
			return d == 0.0 ? string + class_1074.method_4662("options.off") : string + (int)d;
		}
	);
	public static final class_4067 field_18191 = new class_4287(
		"options.mouseWheelSensitivity", 0.01, 10.0, 0.01F, arg -> arg.field_1889, (arg, double_) -> arg.field_1889 = double_, (arg, arg2) -> {
			double d = arg2.method_18611(arg2.method_18613(arg));
			return arg2.method_18518() + String.format("%.2f", arg2.method_18616(d));
		}
	);
	public static final class_4067 field_1933 = new class_4067("options.renderDistance", 2.0, 16.0, 1.0F, arg -> (double)arg.field_1870, (arg, double_) -> {
		arg.field_1870 = (int)double_.doubleValue();
		class_310.method_1551().field_1769.method_3292();
	}, (arg, arg2) -> {
		double d = arg2.method_18613(arg);
		return arg2.method_18518() + class_1074.method_4662("options.chunks", (int)d);
	});
	public static final class_4067 field_1944 = new class_4067(
		"options.sensitivity", 0.0, 1.0, 0.0F, arg -> arg.field_1843, (arg, double_) -> arg.field_1843 = double_, (arg, arg2) -> {
			double d = arg2.method_18611(arg2.method_18613(arg));
			String string = arg2.method_18518();
			if (d == 0.0) {
				return string + class_1074.method_4662("options.sensitivity.min");
			} else {
				return d == 1.0 ? string + class_1074.method_4662("options.sensitivity.max") : string + (int)(d * 200.0) + "%";
			}
		}
	);
	public static final class_4067 field_18723 = new class_4067(
		"options.accessibility.text_background_opacity", 0.0, 1.0, 0.0F, arg -> arg.field_18726, (arg, double_) -> {
			arg.field_18726 = double_;
			class_310.method_1551().field_1705.method_1743().method_1817();
		}, (arg, arg2) -> arg2.method_18518() + (int)(arg2.method_18611(arg2.method_18613(arg)) * 100.0) + "%"
	);
	public static final class_4064 field_1924 = new class_4064("options.ao", (arg, integer) -> {
		arg.field_1841 = class_4060.method_18484(arg.field_1841.method_18483() + integer);
		class_310.method_1551().field_1769.method_3279();
	}, (arg, arg2) -> arg2.method_18518() + class_1074.method_4662(arg.field_1841.method_18485()));
	public static final class_4064 field_18192 = new class_4064(
		"options.attackIndicator",
		(arg, integer) -> arg.field_1895 = class_4061.method_18488(arg.field_1895.method_18487() + integer),
		(arg, arg2) -> arg2.method_18518() + class_1074.method_4662(arg.field_1895.method_18489())
	);
	public static final class_4064 field_1923 = new class_4064(
		"options.chat.visibility",
		(arg, integer) -> arg.field_1877 = class_1659.method_7360((arg.field_1877.method_7362() + integer) % 3),
		(arg, arg2) -> arg2.method_18518() + class_1074.method_4662(arg.field_1877.method_7359())
	);
	public static final class_4064 field_1938 = new class_4064(
		"options.graphics",
		(arg, integer) -> {
			arg.field_1833 = !arg.field_1833;
			class_310.method_1551().field_1769.method_3279();
		},
		(arg, arg2) -> arg.field_1833
				? arg2.method_18518() + class_1074.method_4662("options.graphics.fancy")
				: arg2.method_18518() + class_1074.method_4662("options.graphics.fast")
	);
	public static final class_4064 field_1922 = new class_4064(
		"options.guiScale",
		(arg, integer) -> arg.field_1868 = Integer.remainderUnsigned(
				arg.field_1868 + integer, class_310.method_1551().field_1704.method_4476(0, class_310.method_1551().method_1573()) + 1
			),
		(arg, arg2) -> arg2.method_18518() + (arg.field_1868 == 0 ? class_1074.method_4662("options.guiScale.auto") : arg.field_1868)
	);
	public static final class_4064 field_18193 = new class_4064(
		"options.mainHand", (arg, integer) -> arg.field_1829 = arg.field_1829.method_5928(), (arg, arg2) -> arg2.method_18518() + arg.field_1829
	);
	public static final class_4064 field_18194 = new class_4064(
		"options.narrator",
		(arg, integer) -> {
			if (class_333.field_2054.method_1791()) {
				arg.field_1896 = class_4065.method_18510(arg.field_1896.method_18509() + integer);
			} else {
				arg.field_1896 = class_4065.field_18176;
			}

			class_333.field_2054.method_1792(arg.field_1896);
		},
		(arg, arg2) -> class_333.field_2054.method_1791()
				? arg2.method_18518() + class_1074.method_4662(arg.field_1896.method_18511())
				: arg2.method_18518() + class_1074.method_4662("options.narrator.notavailable")
	);
	public static final class_4064 field_1919 = new class_4064(
		"options.particles",
		(arg, integer) -> arg.field_1882 = class_4066.method_18608(arg.field_1882.method_18609() + integer),
		(arg, arg2) -> arg2.method_18518() + class_1074.method_4662(arg.field_1882.method_18607())
	);
	public static final class_4064 field_1937 = new class_4064(
		"options.renderClouds",
		(arg, integer) -> arg.field_1814 = class_4063.method_18497(arg.field_1814.method_18496() + integer),
		(arg, arg2) -> arg2.method_18518() + class_1074.method_4662(arg.field_1814.method_18498())
	);
	public static final class_4064 field_18724 = new class_4064(
		"options.accessibility.text_background",
		(arg, integer) -> arg.field_18725 = !arg.field_18725,
		(arg, arg2) -> arg2.method_18518()
				+ class_1074.method_4662(arg.field_18725 ? "options.accessibility.text_background.chat" : "options.accessibility.text_background.everywhere")
	);
	public static final class_4062 field_18195 = new class_4062("options.autoJump", arg -> arg.field_1848, (arg, boolean_) -> arg.field_1848 = boolean_);
	public static final class_4062 field_18196 = new class_4062("options.autoSuggestCommands", arg -> arg.field_1873, (arg, boolean_) -> arg.field_1873 = boolean_);
	public static final class_4062 field_1917 = new class_4062("options.chat.color", arg -> arg.field_1900, (arg, boolean_) -> arg.field_1900 = boolean_);
	public static final class_4062 field_1920 = new class_4062("options.chat.links", arg -> arg.field_1911, (arg, boolean_) -> arg.field_1911 = boolean_);
	public static final class_4062 field_1925 = new class_4062("options.chat.links.prompt", arg -> arg.field_1817, (arg, boolean_) -> arg.field_1817 = boolean_);
	public static final class_4062 field_19243 = new class_4062(
		"options.discrete_mouse_scroll", arg -> arg.field_19244, (arg, boolean_) -> arg.field_19244 = boolean_
	);
	public static final class_4062 field_1927 = new class_4062("options.vsync", arg -> arg.field_1884, (arg, boolean_) -> {
		arg.field_1884 = boolean_;
		if (class_310.method_1551().field_1704 != null) {
			class_310.method_1551().field_1704.method_4497(arg.field_1884);
		}
	});
	public static final class_4062 field_18184 = new class_4062("options.entityShadows", arg -> arg.field_1888, (arg, boolean_) -> arg.field_1888 = boolean_);
	public static final class_4062 field_18185 = new class_4062("options.forceUnicodeFont", arg -> arg.field_1819, (arg, boolean_) -> {
		arg.field_1819 = boolean_;
		class_310 lv = class_310.method_1551();
		if (lv.method_1568() != null) {
			lv.method_1568().method_2018(arg.field_1819, class_156.method_18349(), lv);
		}
	});
	public static final class_4062 field_1963 = new class_4062("options.invertMouse", arg -> arg.field_1865, (arg, boolean_) -> arg.field_1865 = boolean_);
	public static final class_4062 field_18186 = new class_4062("options.realmsNotifications", arg -> arg.field_1830, (arg, boolean_) -> arg.field_1830 = boolean_);
	public static final class_4062 field_18187 = new class_4062("options.reducedDebugInfo", arg -> arg.field_1910, (arg, boolean_) -> arg.field_1910 = boolean_);
	public static final class_4062 field_18188 = new class_4062("options.showSubtitles", arg -> arg.field_1818, (arg, boolean_) -> arg.field_1818 = boolean_);
	public static final class_4062 field_1926 = new class_4062("options.snooper", arg -> {
		if (arg.field_1847) {
		}

		return false;
	}, (arg, boolean_) -> arg.field_1847 = boolean_);
	public static final class_4062 field_1930 = new class_4062("options.touchscreen", arg -> arg.field_1854, (arg, boolean_) -> arg.field_1854 = boolean_);
	public static final class_4062 field_1932 = new class_4062("options.fullscreen", arg -> arg.field_1857, (arg, boolean_) -> {
		arg.field_1857 = boolean_;
		class_310 lv = class_310.method_1551();
		if (lv.field_1704 != null && lv.field_1704.method_4498() != arg.field_1857) {
			lv.field_1704.method_4500();
			arg.field_1857 = lv.field_1704.method_4498();
		}
	});
	public static final class_4062 field_1934 = new class_4062("options.viewBobbing", arg -> arg.field_1891, (arg, boolean_) -> arg.field_1891 = boolean_);
	private final String field_1966;

	public class_316(String string) {
		this.field_1966 = string;
	}

	public abstract class_339 method_18520(class_315 arg, int i, int j, int k);

	public String method_18518() {
		return class_1074.method_4662(this.field_1966) + ": ";
	}
}
