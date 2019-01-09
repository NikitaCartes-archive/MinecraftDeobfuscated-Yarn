package net.minecraft;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class class_525 extends class_437 {
	private final class_437 field_3187;
	private class_342 field_3188;
	private class_342 field_3184;
	private String field_3196;
	private String field_3201 = "survival";
	private String field_3185;
	private boolean field_3180 = true;
	private boolean field_3192;
	private boolean field_3179;
	private boolean field_3191;
	private boolean field_3178;
	private boolean field_3190;
	private boolean field_3202;
	private class_339 field_3205;
	private class_339 field_3186;
	private class_339 field_3193;
	private class_339 field_3203;
	private class_339 field_3197;
	private class_339 field_3189;
	private class_339 field_3182;
	private class_339 field_3198;
	private String field_3194;
	private String field_3199;
	private String field_3181;
	private String field_3195;
	private int field_3204;
	public class_2487 field_3200 = new class_2487();
	private static final String[] field_3183 = new String[]{
		"CON",
		"COM",
		"PRN",
		"AUX",
		"CLOCK$",
		"NUL",
		"COM1",
		"COM2",
		"COM3",
		"COM4",
		"COM5",
		"COM6",
		"COM7",
		"COM8",
		"COM9",
		"LPT1",
		"LPT2",
		"LPT3",
		"LPT4",
		"LPT5",
		"LPT6",
		"LPT7",
		"LPT8",
		"LPT9"
	};

	public class_525(class_437 arg) {
		this.field_3187 = arg;
		this.field_3181 = "";
		this.field_3195 = class_1074.method_4662("selectWorld.newWorld");
	}

	@Override
	public void method_2225() {
		this.field_3188.method_1865();
		this.field_3184.method_1865();
	}

	@Override
	protected void method_2224() {
		this.field_2563.field_1774.method_1462(true);
		this.field_3205 = this.method_2219(new class_339(0, this.field_2561 / 2 - 155, this.field_2559 - 28, 150, 20, class_1074.method_4662("selectWorld.create")) {
			@Override
			public void method_1826(double d, double e) {
				class_525.this.method_2736();
			}
		});
		this.method_2219(new class_339(1, this.field_2561 / 2 + 5, this.field_2559 - 28, 150, 20, class_1074.method_4662("gui.cancel")) {
			@Override
			public void method_1826(double d, double e) {
				class_525.this.field_2563.method_1507(class_525.this.field_3187);
			}
		});
		this.field_3186 = this.method_2219(new class_339(2, this.field_2561 / 2 - 75, 115, 150, 20, class_1074.method_4662("selectWorld.gameMode")) {
			@Override
			public void method_1826(double d, double e) {
				if ("survival".equals(class_525.this.field_3201)) {
					if (!class_525.this.field_3179) {
						class_525.this.field_3192 = false;
					}

					class_525.this.field_3178 = false;
					class_525.this.field_3201 = "hardcore";
					class_525.this.field_3178 = true;
					class_525.this.field_3182.field_2078 = false;
					class_525.this.field_3197.field_2078 = false;
					class_525.this.method_2722();
				} else if ("hardcore".equals(class_525.this.field_3201)) {
					if (!class_525.this.field_3179) {
						class_525.this.field_3192 = true;
					}

					class_525.this.field_3178 = false;
					class_525.this.field_3201 = "creative";
					class_525.this.method_2722();
					class_525.this.field_3178 = false;
					class_525.this.field_3182.field_2078 = true;
					class_525.this.field_3197.field_2078 = true;
				} else {
					if (!class_525.this.field_3179) {
						class_525.this.field_3192 = false;
					}

					class_525.this.field_3201 = "survival";
					class_525.this.method_2722();
					class_525.this.field_3182.field_2078 = true;
					class_525.this.field_3197.field_2078 = true;
					class_525.this.field_3178 = false;
				}

				class_525.this.method_2722();
			}
		});
		this.field_3193 = this.method_2219(new class_339(3, this.field_2561 / 2 - 75, 187, 150, 20, class_1074.method_4662("selectWorld.moreWorldOptions")) {
			@Override
			public void method_1826(double d, double e) {
				class_525.this.method_2721();
			}
		});
		this.field_3203 = this.method_2219(new class_339(4, this.field_2561 / 2 - 155, 100, 150, 20, class_1074.method_4662("selectWorld.mapFeatures")) {
			@Override
			public void method_1826(double d, double e) {
				class_525.this.field_3180 = !class_525.this.field_3180;
				class_525.this.method_2722();
			}
		});
		this.field_3203.field_2076 = false;
		this.field_3197 = this.method_2219(new class_339(7, this.field_2561 / 2 + 5, 151, 150, 20, class_1074.method_4662("selectWorld.bonusItems")) {
			@Override
			public void method_1826(double d, double e) {
				class_525.this.field_3191 = !class_525.this.field_3191;
				class_525.this.method_2722();
			}
		});
		this.field_3197.field_2076 = false;
		this.field_3189 = this.method_2219(new class_339(5, this.field_2561 / 2 + 5, 100, 150, 20, class_1074.method_4662("selectWorld.mapType")) {
			@Override
			public void method_1826(double d, double e) {
				class_525.this.field_3204++;
				if (class_525.this.field_3204 >= class_1942.field_9279.length) {
					class_525.this.field_3204 = 0;
				}

				while (!class_525.this.method_2723()) {
					class_525.this.field_3204++;
					if (class_525.this.field_3204 >= class_1942.field_9279.length) {
						class_525.this.field_3204 = 0;
					}
				}

				class_525.this.field_3200 = new class_2487();
				class_525.this.method_2722();
				class_525.this.method_2710(class_525.this.field_3202);
			}
		});
		this.field_3189.field_2076 = false;
		this.field_3182 = this.method_2219(new class_339(6, this.field_2561 / 2 - 155, 151, 150, 20, class_1074.method_4662("selectWorld.allowCommands")) {
			@Override
			public void method_1826(double d, double e) {
				class_525.this.field_3179 = true;
				class_525.this.field_3192 = !class_525.this.field_3192;
				class_525.this.method_2722();
			}
		});
		this.field_3182.field_2076 = false;
		this.field_3198 = this.method_2219(new class_339(8, this.field_2561 / 2 + 5, 120, 150, 20, class_1074.method_4662("selectWorld.customizeType")) {
			@Override
			public void method_1826(double d, double e) {
				if (class_1942.field_9279[class_525.this.field_3204] == class_1942.field_9277) {
					class_525.this.field_2563.method_1507(new class_413(class_525.this, class_525.this.field_3200));
				}

				if (class_1942.field_9279[class_525.this.field_3204] == class_1942.field_9275) {
					class_525.this.field_2563.method_1507(new class_415(class_525.this, class_525.this.field_3200));
				}
			}
		});
		this.field_3198.field_2076 = false;
		this.field_3188 = new class_342(9, this.field_2554, this.field_2561 / 2 - 100, 60, 200, 20);
		this.field_3188.method_1876(true);
		this.field_3188.method_1852(this.field_3195);
		this.field_3184 = new class_342(10, this.field_2554, this.field_2561 / 2 - 100, 60, 200, 20);
		this.field_3184.method_1852(this.field_3181);
		this.method_2710(this.field_3202);
		this.method_2727();
		this.method_2722();
	}

	private void method_2727() {
		this.field_3196 = this.field_3188.method_1882().trim();

		for (char c : class_155.field_1126) {
			this.field_3196 = this.field_3196.replace(c, '_');
		}

		if (StringUtils.isEmpty(this.field_3196)) {
			this.field_3196 = "World";
		}

		this.field_3196 = method_2724(this.field_2563.method_1586(), this.field_3196);
	}

	private void method_2722() {
		this.field_3186.field_2074 = class_1074.method_4662("selectWorld.gameMode") + ": " + class_1074.method_4662("selectWorld.gameMode." + this.field_3201);
		this.field_3194 = class_1074.method_4662("selectWorld.gameMode." + this.field_3201 + ".line1");
		this.field_3199 = class_1074.method_4662("selectWorld.gameMode." + this.field_3201 + ".line2");
		this.field_3203.field_2074 = class_1074.method_4662("selectWorld.mapFeatures") + " ";
		if (this.field_3180) {
			this.field_3203.field_2074 = this.field_3203.field_2074 + class_1074.method_4662("options.on");
		} else {
			this.field_3203.field_2074 = this.field_3203.field_2074 + class_1074.method_4662("options.off");
		}

		this.field_3197.field_2074 = class_1074.method_4662("selectWorld.bonusItems") + " ";
		if (this.field_3191 && !this.field_3178) {
			this.field_3197.field_2074 = this.field_3197.field_2074 + class_1074.method_4662("options.on");
		} else {
			this.field_3197.field_2074 = this.field_3197.field_2074 + class_1074.method_4662("options.off");
		}

		this.field_3189.field_2074 = class_1074.method_4662("selectWorld.mapType")
			+ " "
			+ class_1074.method_4662(class_1942.field_9279[this.field_3204].method_8640());
		this.field_3182.field_2074 = class_1074.method_4662("selectWorld.allowCommands") + " ";
		if (this.field_3192 && !this.field_3178) {
			this.field_3182.field_2074 = this.field_3182.field_2074 + class_1074.method_4662("options.on");
		} else {
			this.field_3182.field_2074 = this.field_3182.field_2074 + class_1074.method_4662("options.off");
		}
	}

	public static String method_2724(class_32 arg, String string) {
		string = string.replaceAll("[\\./\"]", "_");

		for (String string2 : field_3183) {
			if (string.equalsIgnoreCase(string2)) {
				string = "_" + string + "_";
			}
		}

		while (arg.method_238(string) != null) {
			string = string + "-";
		}

		return string;
	}

	@Override
	public void method_2234() {
		this.field_2563.field_1774.method_1462(false);
	}

	private void method_2736() {
		this.field_2563.method_1507(null);
		if (!this.field_3190) {
			this.field_3190 = true;
			long l = new Random().nextLong();
			String string = this.field_3184.method_1882();
			if (!StringUtils.isEmpty(string)) {
				try {
					long m = Long.parseLong(string);
					if (m != 0L) {
						l = m;
					}
				} catch (NumberFormatException var6) {
					l = (long)string.hashCode();
				}
			}

			class_1940 lv = new class_1940(l, class_1934.method_8385(this.field_3201), this.field_3180, this.field_3178, class_1942.field_9279[this.field_3204]);
			lv.method_8579(Dynamic.convert(class_2509.field_11560, JsonOps.INSTANCE, this.field_3200));
			if (this.field_3191 && !this.field_3178) {
				lv.method_8575();
			}

			if (this.field_3192 && !this.field_3178) {
				lv.method_8578();
			}

			this.field_2563.method_1559(this.field_3196, this.field_3188.method_1882().trim(), lv);
		}
	}

	private boolean method_2723() {
		class_1942 lv = class_1942.field_9279[this.field_3204];
		if (lv == null || !lv.method_8642()) {
			return false;
		} else {
			return lv == class_1942.field_9266 ? method_2223() : true;
		}
	}

	private void method_2721() {
		this.method_2710(!this.field_3202);
	}

	private void method_2710(boolean bl) {
		this.field_3202 = bl;
		if (class_1942.field_9279[this.field_3204] == class_1942.field_9266) {
			this.field_3186.field_2076 = !this.field_3202;
			this.field_3186.field_2078 = false;
			if (this.field_3185 == null) {
				this.field_3185 = this.field_3201;
			}

			this.field_3201 = "spectator";
			this.field_3203.field_2076 = false;
			this.field_3197.field_2076 = false;
			this.field_3189.field_2076 = this.field_3202;
			this.field_3182.field_2076 = false;
			this.field_3198.field_2076 = false;
		} else {
			this.field_3186.field_2076 = !this.field_3202;
			this.field_3186.field_2078 = true;
			if (this.field_3185 != null) {
				this.field_3201 = this.field_3185;
				this.field_3185 = null;
			}

			this.field_3203.field_2076 = this.field_3202 && class_1942.field_9279[this.field_3204] != class_1942.field_9278;
			this.field_3197.field_2076 = this.field_3202;
			this.field_3189.field_2076 = this.field_3202;
			this.field_3182.field_2076 = this.field_3202;
			this.field_3198.field_2076 = this.field_3202 && class_1942.field_9279[this.field_3204].method_8641();
		}

		this.method_2722();
		if (this.field_3202) {
			this.field_3193.field_2074 = class_1074.method_4662("gui.done");
		} else {
			this.field_3193.field_2074 = class_1074.method_4662("selectWorld.moreWorldOptions");
		}
	}

	@Override
	public boolean method_16806(char c, int i) {
		if (this.field_3188.method_1871() && !this.field_3202) {
			this.field_3188.method_16806(c, i);
			this.field_3195 = this.field_3188.method_1882();
			this.field_3205.field_2078 = !this.field_3188.method_1882().isEmpty();
			this.method_2727();
			return true;
		} else if (this.field_3184.method_1871() && this.field_3202) {
			this.field_3184.method_16806(c, i);
			this.field_3181 = this.field_3184.method_1882();
			return true;
		} else {
			return super.method_16806(c, i);
		}
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		if (this.field_3188.method_1871() && !this.field_3202) {
			this.field_3188.method_16805(i, j, k);
			this.field_3195 = this.field_3188.method_1882();
			this.field_3205.field_2078 = !this.field_3188.method_1882().isEmpty();
			this.method_2727();
		} else if (this.field_3184.method_1871() && this.field_3202) {
			this.field_3184.method_16805(i, j, k);
			this.field_3181 = this.field_3184.method_1882();
		}

		if (this.field_3205.field_2078 && (i == 257 || i == 335)) {
			this.method_2736();
		}

		return true;
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		if (super.method_16807(d, e, i)) {
			return true;
		} else {
			return this.field_3202 ? this.field_3184.method_16807(d, e, i) : this.field_3188.method_16807(d, e, i);
		}
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.method_1789(this.field_2554, class_1074.method_4662("selectWorld.create"), this.field_2561 / 2, 20, -1);
		if (this.field_3202) {
			this.method_1780(this.field_2554, class_1074.method_4662("selectWorld.enterSeed"), this.field_2561 / 2 - 100, 47, -6250336);
			this.method_1780(this.field_2554, class_1074.method_4662("selectWorld.seedInfo"), this.field_2561 / 2 - 100, 85, -6250336);
			if (this.field_3203.field_2076) {
				this.method_1780(this.field_2554, class_1074.method_4662("selectWorld.mapFeatures.info"), this.field_2561 / 2 - 150, 122, -6250336);
			}

			if (this.field_3182.field_2076) {
				this.method_1780(this.field_2554, class_1074.method_4662("selectWorld.allowCommands.info"), this.field_2561 / 2 - 150, 172, -6250336);
			}

			this.field_3184.method_1857(i, j, f);
			if (class_1942.field_9279[this.field_3204].method_8644()) {
				this.field_2554
					.method_1712(
						class_1074.method_4662(class_1942.field_9279[this.field_3204].method_8630()),
						this.field_3189.field_2069 + 2,
						this.field_3189.field_2068 + 22,
						this.field_3189.method_1825(),
						10526880
					);
			}
		} else {
			this.method_1780(this.field_2554, class_1074.method_4662("selectWorld.enterName"), this.field_2561 / 2 - 100, 47, -6250336);
			this.method_1780(this.field_2554, class_1074.method_4662("selectWorld.resultFolder") + " " + this.field_3196, this.field_2561 / 2 - 100, 85, -6250336);
			this.field_3188.method_1857(i, j, f);
			this.method_1789(this.field_2554, this.field_3194, this.field_2561 / 2, 137, -6250336);
			this.method_1789(this.field_2554, this.field_3199, this.field_2561 / 2, 149, -6250336);
		}

		super.method_2214(i, j, f);
	}

	public void method_2737(class_31 arg) {
		this.field_3195 = class_1074.method_4662("selectWorld.newWorld.copyOf", arg.method_150());
		this.field_3181 = arg.method_184() + "";
		class_1942 lv = arg.method_153() == class_1942.field_9278 ? class_1942.field_9265 : arg.method_153();
		this.field_3204 = lv.method_8637();
		this.field_3200 = arg.method_169();
		this.field_3180 = arg.method_220();
		this.field_3192 = arg.method_194();
		if (arg.method_152()) {
			this.field_3201 = "hardcore";
		} else if (arg.method_210().method_8388()) {
			this.field_3201 = "survival";
		} else if (arg.method_210().method_8386()) {
			this.field_3201 = "creative";
		}
	}
}
