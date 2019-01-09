package net.minecraft;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.DataFixUtils;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ClientBrandRetriever;

@Environment(EnvType.CLIENT)
public class class_340 extends class_332 {
	private final class_310 field_2079;
	private final class_327 field_2081;
	private class_239 field_2082;
	private class_239 field_2083;
	@Nullable
	private class_1923 field_2085;
	@Nullable
	private class_2818 field_2084;
	@Nullable
	private CompletableFuture<class_2818> field_2080;

	public class_340(class_310 arg) {
		this.field_2079 = arg;
		this.field_2081 = arg.field_1772;
	}

	public void method_1842() {
		this.field_2080 = null;
		this.field_2084 = null;
	}

	public void method_1846() {
		this.field_2079.method_16011().method_15396("debug");
		GlStateManager.pushMatrix();
		class_1297 lv = this.field_2079.method_1560();
		this.field_2082 = lv.method_5745(20.0, 0.0F, class_242.field_1348);
		this.field_2083 = lv.method_5745(20.0, 0.0F, class_242.field_1347);
		this.method_1847();
		this.method_1848();
		GlStateManager.popMatrix();
		if (this.field_2079.field_1690.field_1893) {
			int i = this.field_2079.field_1704.method_4486();
			this.method_15870(this.field_2079.method_1570(), 0, i / 2, true);
			class_1132 lv2 = this.field_2079.method_1576();
			if (lv2 != null) {
				this.method_15870(lv2.method_15876(), i - Math.min(i / 2, 240), i / 2, false);
			}
		}

		this.field_2079.method_16011().method_15407();
	}

	protected void method_1847() {
		List<String> list = this.method_1835();
		list.add("");
		list.add(
			"Debug: Pie [shift]: "
				+ (this.field_2079.field_1690.field_1880 ? "visible" : "hidden")
				+ " FPS [alt]: "
				+ (this.field_2079.field_1690.field_1893 ? "visible" : "hidden")
		);
		list.add("For help: press F3 + Q");

		for (int i = 0; i < list.size(); i++) {
			String string = (String)list.get(i);
			if (!Strings.isNullOrEmpty(string)) {
				int j = 9;
				int k = this.field_2081.method_1727(string);
				int l = 2;
				int m = 2 + j * i;
				method_1785(1, m - 1, 2 + k + 1, m + j - 1, -1873784752);
				this.field_2081.method_1729(string, 2.0F, (float)m, 14737632);
			}
		}
	}

	protected void method_1848() {
		List<String> list = this.method_1839();

		for (int i = 0; i < list.size(); i++) {
			String string = (String)list.get(i);
			if (!Strings.isNullOrEmpty(string)) {
				int j = 9;
				int k = this.field_2081.method_1727(string);
				int l = this.field_2079.field_1704.method_4486() - 2 - k;
				int m = 2 + j * i;
				method_1785(l - 1, m - 1, l + k + 1, m + j - 1, -1873784752);
				this.field_2081.method_1729(string, (float)l, (float)m, 14737632);
			}
		}
	}

	protected List<String> method_1835() {
		class_1132 lv = this.field_2079.method_1576();
		class_2535 lv2 = this.field_2079.method_1562().method_2872();
		float f = lv2.method_10745();
		float g = lv2.method_10762();
		String string;
		if (lv != null) {
			string = String.format("Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", lv.method_3830(), f, g);
		} else {
			string = String.format("\"%s\" server, %.0f tx, %.0f rx", this.field_2079.field_1724.method_3135(), f, g);
		}

		class_2338 lv3 = new class_2338(
			this.field_2079.method_1560().field_5987, this.field_2079.method_1560().method_5829().field_1322, this.field_2079.method_1560().field_6035
		);
		if (this.field_2079.method_1555()) {
			return Lists.<String>newArrayList(
				"Minecraft " + class_155.method_16673().getName() + " (" + this.field_2079.method_1515() + "/" + ClientBrandRetriever.getClientModName() + ")",
				this.field_2079.field_1770,
				string,
				this.field_2079.field_1769.method_3289(),
				this.field_2079.field_1769.method_3272(),
				"P: " + this.field_2079.field_1713.method_3052() + ". T: " + this.field_2079.field_1687.method_8431(),
				this.field_2079.field_1687.method_8457(),
				"",
				String.format("Chunk-relative: %d %d %d", lv3.method_10263() & 15, lv3.method_10264() & 15, lv3.method_10260() & 15)
			);
		} else {
			class_1297 lv4 = this.field_2079.method_1560();
			class_2350 lv5 = lv4.method_5735();
			String string2;
			switch (lv5) {
				case field_11043:
					string2 = "Towards negative Z";
					break;
				case field_11035:
					string2 = "Towards positive Z";
					break;
				case field_11039:
					string2 = "Towards negative X";
					break;
				case field_11034:
					string2 = "Towards positive X";
					break;
				default:
					string2 = "Invalid";
			}

			class_2874 lv6 = this.field_2079.field_1687.field_9247.method_12460();
			class_1923 lv7 = new class_1923(lv3);
			if (!Objects.equals(this.field_2085, lv7)) {
				this.field_2085 = lv7;
				this.method_1842();
			}

			class_1937 lv8 = this.method_1840();
			class_1932 lv9 = lv8.method_8648(lv6, class_1932::new, "chunks");
			List<String> list = Lists.<String>newArrayList(
				"Minecraft "
					+ class_155.method_16673().getName()
					+ " ("
					+ this.field_2079.method_1515()
					+ "/"
					+ ClientBrandRetriever.getClientModName()
					+ ("release".equalsIgnoreCase(this.field_2079.method_1547()) ? "" : "/" + this.field_2079.method_1547())
					+ ")",
				this.field_2079.field_1770,
				string,
				this.field_2079.field_1769.method_3289(),
				this.field_2079.field_1769.method_3272(),
				"P: " + this.field_2079.field_1713.method_3052() + ". T: " + this.field_2079.field_1687.method_8431(),
				this.field_2079.field_1687.method_8457(),
				class_2874.method_12485(lv6).toString() + " FC: " + (lv9 == null ? "n/a" : Integer.toString(lv9.method_8375().size())),
				"",
				String.format(
					Locale.ROOT,
					"XYZ: %.3f / %.5f / %.3f",
					this.field_2079.method_1560().field_5987,
					this.field_2079.method_1560().method_5829().field_1322,
					this.field_2079.method_1560().field_6035
				),
				String.format("Block: %d %d %d", lv3.method_10263(), lv3.method_10264(), lv3.method_10260()),
				String.format(
					"Chunk: %d %d %d in %d %d %d",
					lv3.method_10263() & 15,
					lv3.method_10264() & 15,
					lv3.method_10260() & 15,
					lv3.method_10263() >> 4,
					lv3.method_10264() >> 4,
					lv3.method_10260() >> 4
				),
				String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", lv5, string2, class_3532.method_15393(lv4.field_6031), class_3532.method_15393(lv4.field_5965))
			);
			if (this.field_2079.field_1687 != null) {
				if (this.field_2079.field_1687.method_8591(lv3)) {
					class_2818 lv10 = this.method_1836();
					if (lv10.method_12223()) {
						list.add("Waiting for chunk...");
					} else {
						list.add(
							"Client Light: "
								+ lv10.method_12233(lv3, 0)
								+ " ("
								+ this.field_2079.field_1687.method_8314(class_1944.field_9284, lv3)
								+ " sky, "
								+ this.field_2079.field_1687.method_8314(class_1944.field_9282, lv3)
								+ " block)"
						);
						class_2818 lv11 = this.method_1834();
						if (lv11 != null) {
							class_3568 lv12 = lv8.method_8398().method_12130();
							list.add(
								"Server Light: ("
									+ lv12.method_15562(class_1944.field_9284).method_15543(lv3)
									+ " sky, "
									+ lv12.method_15562(class_1944.field_9282).method_15543(lv3)
									+ " block)"
							);
						}

						if (lv3.method_10264() >= 0 && lv3.method_10264() < 256) {
							list.add("Biome: " + class_2378.field_11153.method_10221(lv10.method_16552(lv3)));
							long l = 0L;
							float h = 0.0F;
							if (lv11 != null) {
								h = lv8.method_8391();
								l = lv11.method_12033();
							}

							class_1266 lv13 = new class_1266(lv8.method_8407(), lv8.method_8532(), l, h);
							list.add(
								String.format(
									Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day %d)", lv13.method_5457(), lv13.method_5458(), this.field_2079.field_1687.method_8532() / 24000L
								)
							);
						}
					}
				} else {
					list.add("Outside of world...");
				}
			} else {
				list.add("Outside of world...");
			}

			if (this.field_2079.field_1773 != null && this.field_2079.field_1773.method_3175()) {
				list.add("Shader: " + this.field_2079.field_1773.method_3183().method_1260());
			}

			if (this.field_2082 != null && this.field_2082.field_1330 == class_239.class_240.field_1332) {
				class_2338 lv14 = this.field_2082.method_1015();
				list.add(String.format("Looking at block: %d %d %d", lv14.method_10263(), lv14.method_10264(), lv14.method_10260()));
			}

			if (this.field_2083 != null && this.field_2083.field_1330 == class_239.class_240.field_1332) {
				class_2338 lv14 = this.field_2083.method_1015();
				list.add(String.format("Looking at liquid: %d %d %d", lv14.method_10263(), lv14.method_10264(), lv14.method_10260()));
			}

			return list;
		}
	}

	private class_1937 method_1840() {
		return DataFixUtils.orElse(
			Optional.ofNullable(this.field_2079.method_1576()).map(arg -> arg.method_3847(this.field_2079.field_1687.field_9247.method_12460())),
			this.field_2079.field_1687
		);
	}

	@Nullable
	private class_2818 method_1834() {
		if (this.field_2080 == null) {
			class_1132 lv = this.field_2079.method_1576();
			if (lv != null) {
				class_3218 lv2 = lv.method_3847(this.field_2079.field_1687.field_9247.method_12460());
				if (lv2 != null) {
					this.field_2080 = lv2.method_16177(this.field_2085.field_9181, this.field_2085.field_9180, false);
				}
			}

			if (this.field_2080 == null) {
				this.field_2080 = CompletableFuture.completedFuture(this.method_1836());
			}
		}

		return (class_2818)this.field_2080.getNow(null);
	}

	private class_2818 method_1836() {
		if (this.field_2084 == null) {
			this.field_2084 = this.field_2079.field_1687.method_8497(this.field_2085.field_9181, this.field_2085.field_9180);
		}

		return this.field_2084;
	}

	protected List<String> method_1839() {
		long l = Runtime.getRuntime().maxMemory();
		long m = Runtime.getRuntime().totalMemory();
		long n = Runtime.getRuntime().freeMemory();
		long o = m - n;
		List<String> list = Lists.<String>newArrayList(
			String.format("Java: %s %dbit", System.getProperty("java.version"), this.field_2079.method_1540() ? 64 : 32),
			String.format("Mem: % 2d%% %03d/%03dMB", o * 100L / l, method_1838(o), method_1838(l)),
			String.format("Allocated: % 2d%% %03dMB", m * 100L / l, method_1838(m)),
			"",
			String.format("CPU: %s", GLX.getCpuInfo()),
			"",
			String.format("Display: %dx%d (%s)", class_310.method_1551().field_1704.method_4489(), class_310.method_1551().field_1704.method_4506(), GLX.getVendor()),
			GLX.getRenderer(),
			GLX.getOpenGLVersion()
		);
		if (this.field_2079.method_1555()) {
			return list;
		} else {
			if (this.field_2082 != null && this.field_2082.field_1330 == class_239.class_240.field_1332) {
				class_2338 lv = this.field_2082.method_1015();
				class_2680 lv2 = this.field_2079.field_1687.method_8320(lv);
				list.add("");
				list.add(class_124.field_1073 + "Targeted Block");
				list.add(String.valueOf(class_2378.field_11146.method_10221(lv2.method_11614())));

				for (Entry<class_2769<?>, Comparable<?>> entry : lv2.method_11656().entrySet()) {
					list.add(this.method_1845(entry));
				}

				for (class_2960 lv3 : this.field_2079.method_1562().method_2867().method_15202().method_15191(lv2.method_11614())) {
					list.add("#" + lv3);
				}
			}

			if (this.field_2083 != null && this.field_2083.field_1330 == class_239.class_240.field_1332) {
				class_2338 lv = this.field_2083.method_1015();
				class_3610 lv4 = this.field_2079.field_1687.method_8316(lv);
				list.add("");
				list.add(class_124.field_1073 + "Targeted Fluid");
				list.add(String.valueOf(class_2378.field_11154.method_10221(lv4.method_15772())));

				for (Entry<class_2769<?>, Comparable<?>> entry : lv4.method_11656().entrySet()) {
					list.add(this.method_1845(entry));
				}

				for (class_2960 lv3 : this.field_2079.method_1562().method_2867().method_15205().method_15191(lv4.method_15772())) {
					list.add("#" + lv3);
				}
			}

			class_1297 lv5 = this.field_2079.field_1692;
			if (lv5 != null) {
				list.add("");
				list.add(class_124.field_1073 + "Targeted Entity");
				list.add(String.valueOf(class_2378.field_11145.method_10221(lv5.method_5864())));
			}

			return list;
		}
	}

	private String method_1845(Entry<class_2769<?>, Comparable<?>> entry) {
		class_2769<?> lv = (class_2769<?>)entry.getKey();
		Comparable<?> comparable = (Comparable<?>)entry.getValue();
		String string = class_156.method_650(lv, comparable);
		if (Boolean.TRUE.equals(comparable)) {
			string = class_124.field_1060 + string;
		} else if (Boolean.FALSE.equals(comparable)) {
			string = class_124.field_1061 + string;
		}

		return lv.method_11899() + ": " + string;
	}

	private void method_15870(class_3517 arg, int i, int j, boolean bl) {
		GlStateManager.disableDepthTest();
		int k = arg.method_15249();
		int l = arg.method_15250();
		long[] ls = arg.method_15246();
		int n = i;
		int o = Math.max(0, ls.length - j);
		int p = ls.length - o;
		int m = arg.method_15251(k + o);
		long q = 0L;
		int r = Integer.MAX_VALUE;
		int s = Integer.MIN_VALUE;

		for (int t = 0; t < p; t++) {
			int u = (int)(ls[arg.method_15251(m + t)] / 1000000L);
			r = Math.min(r, u);
			s = Math.max(s, u);
			q += (long)u;
		}

		int t = this.field_2079.field_1704.method_4502();
		method_1785(i, t - 60, i + p, t, -1873784752);

		while (m != l) {
			int u = arg.method_15248(ls[m], bl ? 30 : 60, bl ? 60 : 20);
			int v = bl ? 100 : 60;
			int w = this.method_1833(class_3532.method_15340(u, 0, v), 0, v / 2, v);
			this.method_1787(n, t, t - u, w);
			n++;
			m = arg.method_15251(m + 1);
		}

		if (bl) {
			method_1785(i + 1, t - 30 + 1, i + 14, t - 30 + 10, -1873784752);
			this.field_2081.method_1729("60 FPS", (float)(i + 2), (float)(t - 30 + 2), 14737632);
			this.method_1783(i, i + p - 1, t - 30, -1);
			method_1785(i + 1, t - 60 + 1, i + 14, t - 60 + 10, -1873784752);
			this.field_2081.method_1729("30 FPS", (float)(i + 2), (float)(t - 60 + 2), 14737632);
			this.method_1783(i, i + p - 1, t - 60, -1);
		} else {
			method_1785(i + 1, t - 60 + 1, i + 14, t - 60 + 10, -1873784752);
			this.field_2081.method_1729("20 TPS", (float)(i + 2), (float)(t - 60 + 2), 14737632);
			this.method_1783(i, i + p - 1, t - 60, -1);
		}

		this.method_1783(i, i + p - 1, t - 1, -1);
		this.method_1787(i, t - 60, t, -1);
		this.method_1787(i + p - 1, t - 60, t, -1);
		if (bl && this.field_2079.field_1690.field_1909 > 0 && this.field_2079.field_1690.field_1909 <= 250) {
			this.method_1783(i, i + p - 1, t - 1 - (int)(1800.0 / (double)this.field_2079.field_1690.field_1909), -16711681);
		}

		String string = r + " ms min";
		String string2 = q / (long)ls.length + " ms avg";
		String string3 = s + " ms max";
		this.field_2081.method_1720(string, (float)(i + 2), (float)(t - 60 - 9), 14737632);
		this.field_2081.method_1720(string2, (float)(i + p / 2 - this.field_2081.method_1727(string2) / 2), (float)(t - 60 - 9), 14737632);
		this.field_2081.method_1720(string3, (float)(i + p - this.field_2081.method_1727(string3)), (float)(t - 60 - 9), 14737632);
		GlStateManager.enableDepthTest();
	}

	private int method_1833(int i, int j, int k, int l) {
		return i < k ? this.method_1843(-16711936, -256, (float)i / (float)k) : this.method_1843(-256, -65536, (float)(i - k) / (float)(l - k));
	}

	private int method_1843(int i, int j, float f) {
		int k = i >> 24 & 0xFF;
		int l = i >> 16 & 0xFF;
		int m = i >> 8 & 0xFF;
		int n = i & 0xFF;
		int o = j >> 24 & 0xFF;
		int p = j >> 16 & 0xFF;
		int q = j >> 8 & 0xFF;
		int r = j & 0xFF;
		int s = class_3532.method_15340((int)class_3532.method_16439(f, (float)k, (float)o), 0, 255);
		int t = class_3532.method_15340((int)class_3532.method_16439(f, (float)l, (float)p), 0, 255);
		int u = class_3532.method_15340((int)class_3532.method_16439(f, (float)m, (float)q), 0, 255);
		int v = class_3532.method_15340((int)class_3532.method_16439(f, (float)n, (float)r), 0, 255);
		return s << 24 | t << 16 | u << 8 | v;
	}

	private static long method_1838(long l) {
		return l / 1024L / 1024L;
	}
}
