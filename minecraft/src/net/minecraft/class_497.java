package net.minecraft;

import com.google.common.collect.Lists;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_497 extends class_437 {
	private static final Logger field_2989 = LogManager.getLogger();
	private final class_2633 field_2980;
	private class_2415 field_2996 = class_2415.field_11302;
	private class_2470 field_3003 = class_2470.field_11467;
	private class_2776 field_3004 = class_2776.field_12696;
	private boolean field_2985;
	private boolean field_2997;
	private boolean field_2983;
	private class_342 field_3005;
	private class_342 field_2982;
	private class_342 field_2999;
	private class_342 field_3010;
	private class_342 field_2988;
	private class_342 field_2998;
	private class_342 field_2978;
	private class_342 field_3000;
	private class_342 field_2992;
	private class_342 field_2986;
	private class_339 field_3002;
	private class_339 field_2994;
	private class_339 field_2987;
	private class_339 field_3006;
	private class_339 field_2995;
	private class_339 field_2981;
	private class_339 field_3007;
	private class_339 field_2993;
	private class_339 field_2977;
	private class_339 field_3009;
	private class_339 field_2990;
	private class_339 field_2979;
	private class_339 field_3008;
	private class_339 field_3001;
	private final List<class_342> field_2984 = Lists.<class_342>newArrayList();
	private final DecimalFormat field_2991 = new DecimalFormat("0.0###");

	public class_497(class_2633 arg) {
		this.field_2980 = arg;
		this.field_2991.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
	}

	@Override
	public void method_2225() {
		this.field_3005.method_1865();
		this.field_2982.method_1865();
		this.field_2999.method_1865();
		this.field_3010.method_1865();
		this.field_2988.method_1865();
		this.field_2998.method_1865();
		this.field_2978.method_1865();
		this.field_3000.method_1865();
		this.field_2992.method_1865();
		this.field_2986.method_1865();
	}

	private void method_2515() {
		if (this.method_2516(class_2633.class_2634.field_12108)) {
			this.field_2563.method_1507(null);
		}
	}

	private void method_2514() {
		this.field_2980.method_11356(this.field_2996);
		this.field_2980.method_11385(this.field_3003);
		this.field_2980.method_11381(this.field_3004);
		this.field_2980.method_11352(this.field_2985);
		this.field_2980.method_11347(this.field_2997);
		this.field_2980.method_11360(this.field_2983);
		this.field_2563.method_1507(null);
	}

	@Override
	protected void method_2224() {
		this.field_2563.field_1774.method_1462(true);
		this.field_3002 = this.method_2219(new class_339(0, this.field_2561 / 2 - 4 - 150, 210, 150, 20, class_1074.method_4662("gui.done")) {
			@Override
			public void method_1826(double d, double e) {
				class_497.this.method_2515();
			}
		});
		this.field_2994 = this.method_2219(new class_339(1, this.field_2561 / 2 + 4, 210, 150, 20, class_1074.method_4662("gui.cancel")) {
			@Override
			public void method_1826(double d, double e) {
				class_497.this.method_2514();
			}
		});
		this.field_2987 = this.method_2219(new class_339(9, this.field_2561 / 2 + 4 + 100, 185, 50, 20, class_1074.method_4662("structure_block.button.save")) {
			@Override
			public void method_1826(double d, double e) {
				if (class_497.this.field_2980.method_11374() == class_2776.field_12695) {
					class_497.this.method_2516(class_2633.class_2634.field_12110);
					class_497.this.field_2563.method_1507(null);
				}
			}
		});
		this.field_3006 = this.method_2219(new class_339(10, this.field_2561 / 2 + 4 + 100, 185, 50, 20, class_1074.method_4662("structure_block.button.load")) {
			@Override
			public void method_1826(double d, double e) {
				if (class_497.this.field_2980.method_11374() == class_2776.field_12697) {
					class_497.this.method_2516(class_2633.class_2634.field_12109);
					class_497.this.field_2563.method_1507(null);
				}
			}
		});
		this.field_2977 = this.method_2219(new class_339(18, this.field_2561 / 2 - 4 - 150, 185, 50, 20, "MODE") {
			@Override
			public void method_1826(double d, double e) {
				class_497.this.field_2980.method_11380();
				class_497.this.method_2509();
			}
		});
		this.field_3009 = this.method_2219(
			new class_339(19, this.field_2561 / 2 + 4 + 100, 120, 50, 20, class_1074.method_4662("structure_block.button.detect_size")) {
				@Override
				public void method_1826(double d, double e) {
					if (class_497.this.field_2980.method_11374() == class_2776.field_12695) {
						class_497.this.method_2516(class_2633.class_2634.field_12106);
						class_497.this.field_2563.method_1507(null);
					}
				}
			}
		);
		this.field_2990 = this.method_2219(new class_339(20, this.field_2561 / 2 + 4 + 100, 160, 50, 20, "ENTITIES") {
			@Override
			public void method_1826(double d, double e) {
				class_497.this.field_2980.method_11352(!class_497.this.field_2980.method_11367());
				class_497.this.method_2524();
			}
		});
		this.field_2979 = this.method_2219(new class_339(21, this.field_2561 / 2 - 20, 185, 40, 20, "MIRROR") {
			@Override
			public void method_1826(double d, double e) {
				switch (class_497.this.field_2980.method_11345()) {
					case field_11302:
						class_497.this.field_2980.method_11356(class_2415.field_11300);
						break;
					case field_11300:
						class_497.this.field_2980.method_11356(class_2415.field_11301);
						break;
					case field_11301:
						class_497.this.field_2980.method_11356(class_2415.field_11302);
				}

				class_497.this.method_2508();
			}
		});
		this.field_3008 = this.method_2219(new class_339(22, this.field_2561 / 2 + 4 + 100, 80, 50, 20, "SHOWAIR") {
			@Override
			public void method_1826(double d, double e) {
				class_497.this.field_2980.method_11347(!class_497.this.field_2980.method_11375());
				class_497.this.method_2513();
			}
		});
		this.field_3001 = this.method_2219(new class_339(23, this.field_2561 / 2 + 4 + 100, 80, 50, 20, "SHOWBB") {
			@Override
			public void method_1826(double d, double e) {
				class_497.this.field_2980.method_11360(!class_497.this.field_2980.method_11357());
				class_497.this.method_2511();
			}
		});
		this.field_2995 = this.method_2219(new class_339(11, this.field_2561 / 2 - 1 - 40 - 1 - 40 - 20, 185, 40, 20, "0") {
			@Override
			public void method_1826(double d, double e) {
				class_497.this.field_2980.method_11385(class_2470.field_11467);
				class_497.this.method_2510();
			}
		});
		this.field_2981 = this.method_2219(new class_339(12, this.field_2561 / 2 - 1 - 40 - 20, 185, 40, 20, "90") {
			@Override
			public void method_1826(double d, double e) {
				class_497.this.field_2980.method_11385(class_2470.field_11463);
				class_497.this.method_2510();
			}
		});
		this.field_3007 = this.method_2219(new class_339(13, this.field_2561 / 2 + 1 + 20, 185, 40, 20, "180") {
			@Override
			public void method_1826(double d, double e) {
				class_497.this.field_2980.method_11385(class_2470.field_11464);
				class_497.this.method_2510();
			}
		});
		this.field_2993 = this.method_2219(new class_339(14, this.field_2561 / 2 + 1 + 40 + 1 + 20, 185, 40, 20, "270") {
			@Override
			public void method_1826(double d, double e) {
				class_497.this.field_2980.method_11385(class_2470.field_11465);
				class_497.this.method_2510();
			}
		});
		this.field_2984.clear();
		this.field_3005 = new class_342(2, this.field_2554, this.field_2561 / 2 - 152, 40, 300, 20) {
			@Override
			public boolean method_16806(char c, int i) {
				return !class_497.this.method_16016(this.method_1882(), c, this.method_1881()) ? false : super.method_16806(c, i);
			}
		};
		this.field_3005.method_1880(64);
		this.field_3005.method_1852(this.field_2980.method_11362());
		this.field_2984.add(this.field_3005);
		class_2338 lv = this.field_2980.method_11359();
		this.field_2982 = new class_342(3, this.field_2554, this.field_2561 / 2 - 152, 80, 80, 20);
		this.field_2982.method_1880(15);
		this.field_2982.method_1852(Integer.toString(lv.method_10263()));
		this.field_2984.add(this.field_2982);
		this.field_2999 = new class_342(4, this.field_2554, this.field_2561 / 2 - 72, 80, 80, 20);
		this.field_2999.method_1880(15);
		this.field_2999.method_1852(Integer.toString(lv.method_10264()));
		this.field_2984.add(this.field_2999);
		this.field_3010 = new class_342(5, this.field_2554, this.field_2561 / 2 + 8, 80, 80, 20);
		this.field_3010.method_1880(15);
		this.field_3010.method_1852(Integer.toString(lv.method_10260()));
		this.field_2984.add(this.field_3010);
		class_2338 lv2 = this.field_2980.method_11349();
		this.field_2988 = new class_342(6, this.field_2554, this.field_2561 / 2 - 152, 120, 80, 20);
		this.field_2988.method_1880(15);
		this.field_2988.method_1852(Integer.toString(lv2.method_10263()));
		this.field_2984.add(this.field_2988);
		this.field_2998 = new class_342(7, this.field_2554, this.field_2561 / 2 - 72, 120, 80, 20);
		this.field_2998.method_1880(15);
		this.field_2998.method_1852(Integer.toString(lv2.method_10264()));
		this.field_2984.add(this.field_2998);
		this.field_2978 = new class_342(8, this.field_2554, this.field_2561 / 2 + 8, 120, 80, 20);
		this.field_2978.method_1880(15);
		this.field_2978.method_1852(Integer.toString(lv2.method_10260()));
		this.field_2984.add(this.field_2978);
		this.field_3000 = new class_342(15, this.field_2554, this.field_2561 / 2 - 152, 120, 80, 20);
		this.field_3000.method_1880(15);
		this.field_3000.method_1852(this.field_2991.format((double)this.field_2980.method_11346()));
		this.field_2984.add(this.field_3000);
		this.field_2992 = new class_342(16, this.field_2554, this.field_2561 / 2 - 72, 120, 80, 20);
		this.field_2992.method_1880(31);
		this.field_2992.method_1852(Long.toString(this.field_2980.method_11371()));
		this.field_2984.add(this.field_2992);
		this.field_2986 = new class_342(17, this.field_2554, this.field_2561 / 2 - 152, 120, 240, 20);
		this.field_2986.method_1880(128);
		this.field_2986.method_1852(this.field_2980.method_11358());
		this.field_2984.add(this.field_2986);
		this.field_2557.addAll(this.field_2984);
		this.field_2996 = this.field_2980.method_11345();
		this.method_2508();
		this.field_3003 = this.field_2980.method_11353();
		this.method_2510();
		this.field_3004 = this.field_2980.method_11374();
		this.method_2509();
		this.field_2985 = this.field_2980.method_11367();
		this.method_2524();
		this.field_2997 = this.field_2980.method_11375();
		this.method_2513();
		this.field_2983 = this.field_2980.method_11357();
		this.method_2511();
		this.field_3005.method_1876(true);
		this.method_1967(this.field_3005);
	}

	@Override
	public void method_2228(class_310 arg, int i, int j) {
		String string = this.field_3005.method_1882();
		String string2 = this.field_2982.method_1882();
		String string3 = this.field_2999.method_1882();
		String string4 = this.field_3010.method_1882();
		String string5 = this.field_2988.method_1882();
		String string6 = this.field_2998.method_1882();
		String string7 = this.field_2978.method_1882();
		String string8 = this.field_3000.method_1882();
		String string9 = this.field_2992.method_1882();
		String string10 = this.field_2986.method_1882();
		this.method_2233(arg, i, j);
		this.field_3005.method_1852(string);
		this.field_2982.method_1852(string2);
		this.field_2999.method_1852(string3);
		this.field_3010.method_1852(string4);
		this.field_2988.method_1852(string5);
		this.field_2998.method_1852(string6);
		this.field_2978.method_1852(string7);
		this.field_3000.method_1852(string8);
		this.field_2992.method_1852(string9);
		this.field_2986.method_1852(string10);
	}

	@Override
	public void method_2234() {
		this.field_2563.field_1774.method_1462(false);
	}

	private void method_2524() {
		boolean bl = !this.field_2980.method_11367();
		if (bl) {
			this.field_2990.field_2074 = class_1074.method_4662("options.on");
		} else {
			this.field_2990.field_2074 = class_1074.method_4662("options.off");
		}
	}

	private void method_2513() {
		boolean bl = this.field_2980.method_11375();
		if (bl) {
			this.field_3008.field_2074 = class_1074.method_4662("options.on");
		} else {
			this.field_3008.field_2074 = class_1074.method_4662("options.off");
		}
	}

	private void method_2511() {
		boolean bl = this.field_2980.method_11357();
		if (bl) {
			this.field_3001.field_2074 = class_1074.method_4662("options.on");
		} else {
			this.field_3001.field_2074 = class_1074.method_4662("options.off");
		}
	}

	private void method_2508() {
		class_2415 lv = this.field_2980.method_11345();
		switch (lv) {
			case field_11302:
				this.field_2979.field_2074 = "|";
				break;
			case field_11300:
				this.field_2979.field_2074 = "< >";
				break;
			case field_11301:
				this.field_2979.field_2074 = "^ v";
		}
	}

	private void method_2510() {
		this.field_2995.field_2078 = true;
		this.field_2981.field_2078 = true;
		this.field_3007.field_2078 = true;
		this.field_2993.field_2078 = true;
		switch (this.field_2980.method_11353()) {
			case field_11467:
				this.field_2995.field_2078 = false;
				break;
			case field_11464:
				this.field_3007.field_2078 = false;
				break;
			case field_11465:
				this.field_2993.field_2078 = false;
				break;
			case field_11463:
				this.field_2981.field_2078 = false;
		}
	}

	private void method_2509() {
		this.field_3005.method_1876(false);
		this.field_2982.method_1876(false);
		this.field_2999.method_1876(false);
		this.field_3010.method_1876(false);
		this.field_2988.method_1876(false);
		this.field_2998.method_1876(false);
		this.field_2978.method_1876(false);
		this.field_3000.method_1876(false);
		this.field_2992.method_1876(false);
		this.field_2986.method_1876(false);
		this.field_3005.method_1862(false);
		this.field_3005.method_1876(false);
		this.field_2982.method_1862(false);
		this.field_2999.method_1862(false);
		this.field_3010.method_1862(false);
		this.field_2988.method_1862(false);
		this.field_2998.method_1862(false);
		this.field_2978.method_1862(false);
		this.field_3000.method_1862(false);
		this.field_2992.method_1862(false);
		this.field_2986.method_1862(false);
		this.field_2987.field_2076 = false;
		this.field_3006.field_2076 = false;
		this.field_3009.field_2076 = false;
		this.field_2990.field_2076 = false;
		this.field_2979.field_2076 = false;
		this.field_2995.field_2076 = false;
		this.field_2981.field_2076 = false;
		this.field_3007.field_2076 = false;
		this.field_2993.field_2076 = false;
		this.field_3008.field_2076 = false;
		this.field_3001.field_2076 = false;
		switch (this.field_2980.method_11374()) {
			case field_12695:
				this.field_3005.method_1862(true);
				this.field_2982.method_1862(true);
				this.field_2999.method_1862(true);
				this.field_3010.method_1862(true);
				this.field_2988.method_1862(true);
				this.field_2998.method_1862(true);
				this.field_2978.method_1862(true);
				this.field_2987.field_2076 = true;
				this.field_3009.field_2076 = true;
				this.field_2990.field_2076 = true;
				this.field_3008.field_2076 = true;
				break;
			case field_12697:
				this.field_3005.method_1862(true);
				this.field_2982.method_1862(true);
				this.field_2999.method_1862(true);
				this.field_3010.method_1862(true);
				this.field_3000.method_1862(true);
				this.field_2992.method_1862(true);
				this.field_3006.field_2076 = true;
				this.field_2990.field_2076 = true;
				this.field_2979.field_2076 = true;
				this.field_2995.field_2076 = true;
				this.field_2981.field_2076 = true;
				this.field_3007.field_2076 = true;
				this.field_2993.field_2076 = true;
				this.field_3001.field_2076 = true;
				this.method_2510();
				break;
			case field_12699:
				this.field_3005.method_1862(true);
				break;
			case field_12696:
				this.field_2986.method_1862(true);
		}

		this.field_2977.field_2074 = class_1074.method_4662("structure_block.mode." + this.field_2980.method_11374().method_15434());
	}

	private boolean method_2516(class_2633.class_2634 arg) {
		class_2338 lv = new class_2338(
			this.method_2517(this.field_2982.method_1882()), this.method_2517(this.field_2999.method_1882()), this.method_2517(this.field_3010.method_1882())
		);
		class_2338 lv2 = new class_2338(
			this.method_2517(this.field_2988.method_1882()), this.method_2517(this.field_2998.method_1882()), this.method_2517(this.field_2978.method_1882())
		);
		float f = this.method_2500(this.field_3000.method_1882());
		long l = this.method_2504(this.field_2992.method_1882());
		this.field_2563
			.method_1562()
			.method_2883(
				new class_2875(
					this.field_2980.method_11016(),
					arg,
					this.field_2980.method_11374(),
					this.field_3005.method_1882(),
					lv,
					lv2,
					this.field_2980.method_11345(),
					this.field_2980.method_11353(),
					this.field_2986.method_1882(),
					this.field_2980.method_11367(),
					this.field_2980.method_11375(),
					this.field_2980.method_11357(),
					f,
					l
				)
			);
		return true;
	}

	private long method_2504(String string) {
		try {
			return Long.valueOf(string);
		} catch (NumberFormatException var3) {
			return 0L;
		}
	}

	private float method_2500(String string) {
		try {
			return Float.valueOf(string);
		} catch (NumberFormatException var3) {
			return 1.0F;
		}
	}

	private int method_2517(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException var3) {
			return 0;
		}
	}

	@Override
	public void method_2210() {
		this.method_2514();
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		if (super.method_16807(d, e, i)) {
			for (class_342 lv : this.field_2984) {
				lv.method_1876(this.getFocused() == lv);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		if (i != 258) {
			if (i != 257 && i != 335) {
				return super.method_16805(i, j, k);
			} else {
				this.method_2515();
				return true;
			}
		} else {
			class_342 lv = null;
			class_342 lv2 = null;

			for (class_342 lv3 : this.field_2984) {
				if (lv != null && lv3.method_1885()) {
					lv2 = lv3;
					break;
				}

				if (lv3.method_1871() && lv3.method_1885()) {
					lv = lv3;
				}
			}

			if (lv != null && lv2 == null) {
				for (class_342 lv3 : this.field_2984) {
					if (lv3.method_1885() && lv3 != lv) {
						lv2 = lv3;
						break;
					}
				}
			}

			if (lv2 != null && lv2 != lv) {
				lv.method_1876(false);
				lv2.method_1876(true);
				this.method_1967(lv2);
			}

			return true;
		}
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		class_2776 lv = this.field_2980.method_11374();
		this.method_1789(this.field_2554, class_1074.method_4662(class_2246.field_10465.method_9539()), this.field_2561 / 2, 10, 16777215);
		if (lv != class_2776.field_12696) {
			this.method_1780(this.field_2554, class_1074.method_4662("structure_block.structure_name"), this.field_2561 / 2 - 153, 30, 10526880);
			this.field_3005.method_1857(i, j, f);
		}

		if (lv == class_2776.field_12697 || lv == class_2776.field_12695) {
			this.method_1780(this.field_2554, class_1074.method_4662("structure_block.position"), this.field_2561 / 2 - 153, 70, 10526880);
			this.field_2982.method_1857(i, j, f);
			this.field_2999.method_1857(i, j, f);
			this.field_3010.method_1857(i, j, f);
			String string = class_1074.method_4662("structure_block.include_entities");
			int k = this.field_2554.method_1727(string);
			this.method_1780(this.field_2554, string, this.field_2561 / 2 + 154 - k, 150, 10526880);
		}

		if (lv == class_2776.field_12695) {
			this.method_1780(this.field_2554, class_1074.method_4662("structure_block.size"), this.field_2561 / 2 - 153, 110, 10526880);
			this.field_2988.method_1857(i, j, f);
			this.field_2998.method_1857(i, j, f);
			this.field_2978.method_1857(i, j, f);
			String string = class_1074.method_4662("structure_block.detect_size");
			int k = this.field_2554.method_1727(string);
			this.method_1780(this.field_2554, string, this.field_2561 / 2 + 154 - k, 110, 10526880);
			String string2 = class_1074.method_4662("structure_block.show_air");
			int l = this.field_2554.method_1727(string2);
			this.method_1780(this.field_2554, string2, this.field_2561 / 2 + 154 - l, 70, 10526880);
		}

		if (lv == class_2776.field_12697) {
			this.method_1780(this.field_2554, class_1074.method_4662("structure_block.integrity"), this.field_2561 / 2 - 153, 110, 10526880);
			this.field_3000.method_1857(i, j, f);
			this.field_2992.method_1857(i, j, f);
			String string = class_1074.method_4662("structure_block.show_boundingbox");
			int k = this.field_2554.method_1727(string);
			this.method_1780(this.field_2554, string, this.field_2561 / 2 + 154 - k, 70, 10526880);
		}

		if (lv == class_2776.field_12696) {
			this.method_1780(this.field_2554, class_1074.method_4662("structure_block.custom_data"), this.field_2561 / 2 - 153, 110, 10526880);
			this.field_2986.method_1857(i, j, f);
		}

		String string = "structure_block.mode_info." + lv.method_15434();
		this.method_1780(this.field_2554, class_1074.method_4662(string), this.field_2561 / 2 - 153, 174, 10526880);
		super.method_2214(i, j, f);
	}

	@Override
	public boolean method_2222() {
		return false;
	}
}
