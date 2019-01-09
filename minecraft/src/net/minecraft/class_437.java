package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class class_437 extends class_362 implements class_411 {
	private static final Logger field_2565 = LogManager.getLogger();
	private static final Set<String> field_2556 = Sets.<String>newHashSet("http", "https");
	protected final List<class_364> field_2557 = Lists.<class_364>newArrayList();
	protected class_310 field_2563;
	protected class_918 field_2560;
	public int field_2561;
	public int field_2559;
	protected final List<class_339> field_2564 = Lists.<class_339>newArrayList();
	protected final List<class_343> field_2555 = Lists.<class_343>newArrayList();
	public boolean field_2558;
	protected class_327 field_2554;
	private URI field_2562;

	public void method_2214(int i, int j, float f) {
		for (int k = 0; k < this.field_2564.size(); k++) {
			((class_339)this.field_2564.get(k)).method_1824(i, j, f);
		}

		for (int k = 0; k < this.field_2555.size(); k++) {
			((class_343)this.field_2555.get(k)).method_1892(i, j, f);
		}
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		if (i == 256 && this.method_16890()) {
			this.method_2210();
			return true;
		} else {
			return super.method_16805(i, j, k);
		}
	}

	public boolean method_16890() {
		return true;
	}

	public void method_2210() {
		this.field_2563.method_1507(null);
	}

	protected <T extends class_339> T method_2219(T arg) {
		this.field_2564.add(arg);
		this.field_2557.add(arg);
		return arg;
	}

	protected void method_2218(class_1799 arg, int i, int j) {
		this.method_2211(this.method_2239(arg), i, j);
	}

	public List<String> method_2239(class_1799 arg) {
		List<class_2561> list = arg.method_7950(
			this.field_2563.field_1724, this.field_2563.field_1690.field_1827 ? class_1836.class_1837.field_8935 : class_1836.class_1837.field_8934
		);
		List<String> list2 = Lists.<String>newArrayList();

		for (class_2561 lv : list) {
			list2.add(lv.method_10863());
		}

		return list2;
	}

	public void method_2215(String string, int i, int j) {
		this.method_2211(Arrays.asList(string), i, j);
	}

	public void method_2211(List<String> list, int i, int j) {
		if (!list.isEmpty()) {
			GlStateManager.disableRescaleNormal();
			class_308.method_1450();
			GlStateManager.disableLighting();
			GlStateManager.disableDepthTest();
			int k = 0;

			for (String string : list) {
				int l = this.field_2554.method_1727(string);
				if (l > k) {
					k = l;
				}
			}

			int m = i + 12;
			int n = j - 12;
			int o = 8;
			if (list.size() > 1) {
				o += 2 + (list.size() - 1) * 10;
			}

			if (m + k > this.field_2561) {
				m -= 28 + k;
			}

			if (n + o + 6 > this.field_2559) {
				n = this.field_2559 - o - 6;
			}

			this.field_2050 = 300.0F;
			this.field_2560.field_4730 = 300.0F;
			int p = -267386864;
			this.method_1782(m - 3, n - 4, m + k + 3, n - 3, -267386864, -267386864);
			this.method_1782(m - 3, n + o + 3, m + k + 3, n + o + 4, -267386864, -267386864);
			this.method_1782(m - 3, n - 3, m + k + 3, n + o + 3, -267386864, -267386864);
			this.method_1782(m - 4, n - 3, m - 3, n + o + 3, -267386864, -267386864);
			this.method_1782(m + k + 3, n - 3, m + k + 4, n + o + 3, -267386864, -267386864);
			int q = 1347420415;
			int r = 1344798847;
			this.method_1782(m - 3, n - 3 + 1, m - 3 + 1, n + o + 3 - 1, 1347420415, 1344798847);
			this.method_1782(m + k + 2, n - 3 + 1, m + k + 3, n + o + 3 - 1, 1347420415, 1344798847);
			this.method_1782(m - 3, n - 3, m + k + 3, n - 3 + 1, 1347420415, 1347420415);
			this.method_1782(m - 3, n + o + 2, m + k + 3, n + o + 3, 1344798847, 1344798847);

			for (int s = 0; s < list.size(); s++) {
				String string2 = (String)list.get(s);
				this.field_2554.method_1720(string2, (float)m, (float)n, -1);
				if (s == 0) {
					n += 2;
				}

				n += 10;
			}

			this.field_2050 = 0.0F;
			this.field_2560.field_4730 = 0.0F;
			GlStateManager.enableLighting();
			GlStateManager.enableDepthTest();
			class_308.method_1452();
			GlStateManager.enableRescaleNormal();
		}
	}

	protected void method_2229(class_2561 arg, int i, int j) {
		if (arg != null && arg.method_10866().method_10969() != null) {
			class_2568 lv = arg.method_10866().method_10969();
			if (lv.method_10892() == class_2568.class_2569.field_11757) {
				class_1799 lv2 = class_1799.field_8037;

				try {
					class_2520 lv3 = class_2522.method_10718(lv.method_10891().getString());
					if (lv3 instanceof class_2487) {
						lv2 = class_1799.method_7915((class_2487)lv3);
					}
				} catch (CommandSyntaxException var10) {
				}

				if (lv2.method_7960()) {
					this.method_2215(class_124.field_1061 + "Invalid Item!", i, j);
				} else {
					this.method_2218(lv2, i, j);
				}
			} else if (lv.method_10892() == class_2568.class_2569.field_11761) {
				if (this.field_2563.field_1690.field_1827) {
					try {
						class_2487 lv4 = class_2522.method_10718(lv.method_10891().getString());
						List<String> list = Lists.<String>newArrayList();
						class_2561 lv5 = class_2561.class_2562.method_10877(lv4.method_10558("name"));
						if (lv5 != null) {
							list.add(lv5.method_10863());
						}

						if (lv4.method_10573("type", 8)) {
							String string = lv4.method_10558("type");
							list.add("Type: " + string);
						}

						list.add(lv4.method_10558("id"));
						this.method_2211(list, i, j);
					} catch (CommandSyntaxException | JsonSyntaxException var9) {
						this.method_2215(class_124.field_1061 + "Invalid Entity!", i, j);
					}
				}
			} else if (lv.method_10892() == class_2568.class_2569.field_11762) {
				this.method_2211(this.field_2563.field_1772.method_1728(lv.method_10891().method_10863(), Math.max(this.field_2561 / 2, 200)), i, j);
			}

			GlStateManager.disableLighting();
		}
	}

	protected void method_2237(String string, boolean bl) {
	}

	public boolean method_2216(class_2561 arg) {
		if (arg == null) {
			return false;
		} else {
			class_2558 lv = arg.method_10866().method_10970();
			if (method_2223()) {
				if (arg.method_10866().method_10955() != null) {
					this.method_2237(arg.method_10866().method_10955(), false);
				}
			} else if (lv != null) {
				if (lv.method_10845() == class_2558.class_2559.field_11749) {
					if (!this.field_2563.field_1690.field_1911) {
						return false;
					}

					try {
						URI uRI = new URI(lv.method_10844());
						String string = uRI.getScheme();
						if (string == null) {
							throw new URISyntaxException(lv.method_10844(), "Missing protocol");
						}

						if (!field_2556.contains(string.toLowerCase(Locale.ROOT))) {
							throw new URISyntaxException(lv.method_10844(), "Unsupported protocol: " + string.toLowerCase(Locale.ROOT));
						}

						if (this.field_2563.field_1690.field_1817) {
							this.field_2562 = uRI;
							this.field_2563.method_1507(new class_407(this, lv.method_10844(), 31102009, false));
						} else {
							this.method_2221(uRI);
						}
					} catch (URISyntaxException var5) {
						field_2565.error("Can't open url for {}", lv, var5);
					}
				} else if (lv.method_10845() == class_2558.class_2559.field_11746) {
					URI uRIx = new File(lv.method_10844()).toURI();
					this.method_2221(uRIx);
				} else if (lv.method_10845() == class_2558.class_2559.field_11745) {
					this.method_2237(lv.method_10844(), true);
				} else if (lv.method_10845() == class_2558.class_2559.field_11750) {
					this.method_2213(lv.method_10844(), false);
				} else {
					field_2565.error("Don't know how to handle {}", lv);
				}

				return true;
			}

			return false;
		}
	}

	public void method_2230(String string) {
		this.method_2213(string, true);
	}

	public void method_2213(String string, boolean bl) {
		if (bl) {
			this.field_2563.field_1705.method_1743().method_1803(string);
		}

		this.field_2563.field_1724.method_3142(string);
	}

	public void method_2233(class_310 arg, int i, int j) {
		this.field_2563 = arg;
		this.field_2560 = arg.method_1480();
		this.field_2554 = arg.field_1772;
		this.field_2561 = i;
		this.field_2559 = j;
		this.field_2564.clear();
		this.field_2557.clear();
		this.method_2224();
	}

	@Override
	public List<? extends class_364> method_1968() {
		return this.field_2557;
	}

	protected void method_2224() {
		this.field_2557.addAll(this.field_2555);
	}

	public void method_2225() {
	}

	public void method_2234() {
	}

	public void method_2240() {
		this.method_2236(0);
	}

	public void method_2236(int i) {
		if (this.field_2563.field_1687 != null) {
			this.method_1782(0, 0, this.field_2561, this.field_2559, -1072689136, -804253680);
		} else {
			this.method_2220(i);
		}
	}

	public void method_2220(int i) {
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		this.field_2563.method_1531().method_4618(field_2051);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		lv2.method_1328(7, class_290.field_1575);
		lv2.method_1315(0.0, (double)this.field_2559, 0.0)
			.method_1312(0.0, (double)((float)this.field_2559 / 32.0F + (float)i))
			.method_1323(64, 64, 64, 255)
			.method_1344();
		lv2.method_1315((double)this.field_2561, (double)this.field_2559, 0.0)
			.method_1312((double)((float)this.field_2561 / 32.0F), (double)((float)this.field_2559 / 32.0F + (float)i))
			.method_1323(64, 64, 64, 255)
			.method_1344();
		lv2.method_1315((double)this.field_2561, 0.0, 0.0)
			.method_1312((double)((float)this.field_2561 / 32.0F), (double)i)
			.method_1323(64, 64, 64, 255)
			.method_1344();
		lv2.method_1315(0.0, 0.0, 0.0).method_1312(0.0, (double)i).method_1323(64, 64, 64, 255).method_1344();
		lv.method_1350();
	}

	public boolean method_2222() {
		return true;
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		if (i == 31102009) {
			if (bl) {
				this.method_2221(this.field_2562);
			}

			this.field_2562 = null;
			this.field_2563.method_1507(this);
		}
	}

	private void method_2221(URI uRI) {
		class_156.method_668().method_673(uRI);
	}

	public static boolean method_2238() {
		return class_310.field_1703
			? class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 343)
				|| class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 347)
			: class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 341)
				|| class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 345);
	}

	public static boolean method_2223() {
		return class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 340)
			|| class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 344);
	}

	public static boolean method_2232() {
		return class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 342)
			|| class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 346);
	}

	public static boolean method_2212(int i) {
		return i == 88 && method_2238() && !method_2223() && !method_2232();
	}

	public static boolean method_2235(int i) {
		return i == 86 && method_2238() && !method_2223() && !method_2232();
	}

	public static boolean method_2227(int i) {
		return i == 67 && method_2238() && !method_2223() && !method_2232();
	}

	public static boolean method_2226(int i) {
		return i == 65 && method_2238() && !method_2223() && !method_2232();
	}

	public void method_2228(class_310 arg, int i, int j) {
		this.method_2233(arg, i, j);
	}

	public static void method_2217(Runnable runnable, String string, String string2) {
		try {
			runnable.run();
		} catch (Throwable var6) {
			class_128 lv = class_128.method_560(var6, string);
			class_129 lv2 = lv.method_562("Affected screen");
			lv2.method_577("Screen name", () -> string2);
			throw new class_148(lv);
		}
	}

	protected boolean method_16016(String string, char c, int i) {
		int j = string.indexOf(58);
		int k = string.indexOf(47);
		if (c == ':') {
			return (k == -1 || i <= k) && j == -1;
		} else {
			return c == '/' ? i > j : c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.';
		}
	}
}
