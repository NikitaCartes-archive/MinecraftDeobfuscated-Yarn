package net.minecraft;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.mojang.datafixers.DataFixTypes;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_315 {
	private static final Logger field_1834 = LogManager.getLogger();
	private static final Gson field_1823 = new Gson();
	private static final Type field_1859 = new ParameterizedType() {
		public Type[] getActualTypeArguments() {
			return new Type[]{String.class};
		}

		public Type getRawType() {
			return List.class;
		}

		public Type getOwnerType() {
			return null;
		}
	};
	public static final Splitter field_1853 = Splitter.on(':');
	public double field_1843 = 0.5;
	public int field_1870 = -1;
	public int field_1909 = 120;
	public class_4063 field_1814 = class_4063.field_18164;
	public boolean field_1833 = true;
	public class_4060 field_1841 = class_4060.field_18146;
	public List<String> field_1887 = Lists.<String>newArrayList();
	public List<String> field_1846 = Lists.<String>newArrayList();
	public class_1659 field_1877 = class_1659.field_7538;
	public double field_1820 = 1.0;
	public double field_18726 = 0.5;
	@Nullable
	public String field_1828;
	public boolean field_1815;
	public boolean field_1827;
	public boolean field_1837 = true;
	private final Set<class_1664> field_1892 = Sets.<class_1664>newHashSet(class_1664.values());
	public class_1306 field_1829 = class_1306.field_6183;
	public int field_1872;
	public int field_1885;
	public boolean field_1905 = true;
	public double field_1908 = 1.0;
	public double field_1915 = 1.0;
	public double field_1825 = 0.44366196F;
	public double field_1838 = 1.0;
	public int field_1856 = 4;
	private final Map<class_3419, Float> field_1916 = Maps.newEnumMap(class_3419.class);
	public boolean field_1876 = true;
	public class_4061 field_1895 = class_4061.field_18152;
	public class_1157 field_1875 = class_1157.field_5650;
	public int field_1878 = 2;
	public double field_1889 = 1.0;
	public int field_1901 = 1;
	public boolean field_1848 = true;
	public boolean field_1873 = true;
	public boolean field_1900 = true;
	public boolean field_1911 = true;
	public boolean field_1817 = true;
	public boolean field_1884 = true;
	public boolean field_1888 = true;
	public boolean field_1819;
	public boolean field_1865;
	public boolean field_1830 = true;
	public boolean field_1910;
	public boolean field_1847 = true;
	public boolean field_1818;
	public boolean field_18725 = true;
	public boolean field_1854;
	public boolean field_1857;
	public boolean field_1891 = true;
	public final class_304 field_1894 = new class_304("key.forward", 87, "key.categories.movement");
	public final class_304 field_1913 = new class_304("key.left", 65, "key.categories.movement");
	public final class_304 field_1881 = new class_304("key.back", 83, "key.categories.movement");
	public final class_304 field_1849 = new class_304("key.right", 68, "key.categories.movement");
	public final class_304 field_1903 = new class_304("key.jump", 32, "key.categories.movement");
	public final class_304 field_1832 = new class_304("key.sneak", 340, "key.categories.movement");
	public final class_304 field_1867 = new class_304("key.sprint", 341, "key.categories.movement");
	public final class_304 field_1822 = new class_304("key.inventory", 69, "key.categories.inventory");
	public final class_304 field_1831 = new class_304("key.swapHands", 70, "key.categories.inventory");
	public final class_304 field_1869 = new class_304("key.drop", 81, "key.categories.inventory");
	public final class_304 field_1904 = new class_304("key.use", class_3675.class_307.field_1672, 1, "key.categories.gameplay");
	public final class_304 field_1886 = new class_304("key.attack", class_3675.class_307.field_1672, 0, "key.categories.gameplay");
	public final class_304 field_1871 = new class_304("key.pickItem", class_3675.class_307.field_1672, 2, "key.categories.gameplay");
	public final class_304 field_1890 = new class_304("key.chat", 84, "key.categories.multiplayer");
	public final class_304 field_1907 = new class_304("key.playerlist", 258, "key.categories.multiplayer");
	public final class_304 field_1845 = new class_304("key.command", 47, "key.categories.multiplayer");
	public final class_304 field_1835 = new class_304("key.screenshot", 291, "key.categories.misc");
	public final class_304 field_1824 = new class_304("key.togglePerspective", 294, "key.categories.misc");
	public final class_304 field_1816 = new class_304("key.smoothCamera", class_3675.field_16237.method_1444(), "key.categories.misc");
	public final class_304 field_1836 = new class_304("key.fullscreen", 300, "key.categories.misc");
	public final class_304 field_1906 = new class_304("key.spectatorOutlines", class_3675.field_16237.method_1444(), "key.categories.misc");
	public final class_304 field_1844 = new class_304("key.advancements", 76, "key.categories.misc");
	public final class_304 field_19189 = new class_304("key.boss_mode", 66, "key.categories.misc");
	public final class_304 field_19190 = new class_304("key.increase_view", 296, "key.categories.misc");
	public final class_304 field_19191 = new class_304("key.decrease_view", 297, "key.categories.misc");
	public final class_304[] field_1852 = new class_304[]{
		new class_304("key.hotbar.1", 49, "key.categories.inventory"),
		new class_304("key.hotbar.2", 50, "key.categories.inventory"),
		new class_304("key.hotbar.3", 51, "key.categories.inventory"),
		new class_304("key.hotbar.4", 52, "key.categories.inventory"),
		new class_304("key.hotbar.5", 53, "key.categories.inventory"),
		new class_304("key.hotbar.6", 54, "key.categories.inventory"),
		new class_304("key.hotbar.7", 55, "key.categories.inventory"),
		new class_304("key.hotbar.8", 56, "key.categories.inventory"),
		new class_304("key.hotbar.9", 57, "key.categories.inventory")
	};
	public final class_304 field_1879 = new class_304("key.saveToolbarActivator", 67, "key.categories.creative");
	public final class_304 field_1874 = new class_304("key.loadToolbarActivator", 88, "key.categories.creative");
	public final class_304[] field_1839 = ArrayUtils.addAll(
		new class_304[]{
			this.field_1886,
			this.field_1904,
			this.field_1894,
			this.field_1913,
			this.field_1881,
			this.field_1849,
			this.field_1903,
			this.field_1832,
			this.field_1867,
			this.field_1869,
			this.field_1822,
			this.field_1890,
			this.field_1907,
			this.field_1871,
			this.field_1845,
			this.field_1835,
			this.field_1824,
			this.field_1816,
			this.field_1836,
			this.field_1906,
			this.field_1831,
			this.field_1879,
			this.field_1874,
			this.field_1844,
			this.field_19189,
			this.field_19190,
			this.field_19191
		},
		this.field_1852
	);
	protected class_310 field_1863;
	private File field_1897;
	public class_1267 field_1851 = class_1267.field_5802;
	public boolean field_1842;
	public int field_1850;
	public boolean field_1866;
	public boolean field_1880;
	public boolean field_1893;
	public String field_1864 = "";
	public boolean field_1914;
	public double field_1826 = 70.0;
	public double field_1840;
	public int field_1868;
	public class_4066 field_1882 = class_4066.field_18197;
	public class_4065 field_1896 = class_4065.field_18176;
	public String field_1883 = "en_us";

	public class_315(class_310 arg, File file) {
		this.field_1863 = arg;
		this.field_1897 = new File(file, "options.txt");
		if (arg.method_1540() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
			class_316.field_1933.method_18612(32.0F);
		} else {
			class_316.field_1933.method_18612(16.0F);
		}

		this.field_1870 = arg.method_1540() ? 12 : 8;
		this.method_1636();
	}

	public float method_19343(float f) {
		return this.field_18725 ? f : (float)this.field_18726;
	}

	public int method_19345(float f) {
		return (int)(this.method_19343(f) * 255.0F) << 24 & 0xFF000000;
	}

	public int method_19344(int i) {
		return this.field_18725 ? i : (int)(this.field_18726 * 255.0) << 24 & 0xFF000000;
	}

	public void method_1641(class_304 arg, class_3675.class_306 arg2) {
		arg.method_1422(arg2);
		this.method_1640();
	}

	public void method_1636() {
		try {
			if (!this.field_1897.exists()) {
				return;
			}

			this.field_1916.clear();
			List<String> list = IOUtils.readLines(new FileInputStream(this.field_1897));
			class_2487 lv = new class_2487();

			for (String string : list) {
				try {
					Iterator<String> iterator = field_1853.omitEmptyStrings().limit(2).split(string).iterator();
					lv.method_10582((String)iterator.next(), (String)iterator.next());
				} catch (Exception var10) {
					field_1834.warn("Skipping bad option: {}", string);
				}
			}

			lv = this.method_1626(lv);

			for (String string : lv.method_10541()) {
				String string2 = lv.method_10558(string);

				try {
					if ("autoJump".equals(string)) {
						class_316.field_18195.method_18492(this, string2);
					}

					if ("autoSuggestions".equals(string)) {
						class_316.field_18196.method_18492(this, string2);
					}

					if ("chatColors".equals(string)) {
						class_316.field_1917.method_18492(this, string2);
					}

					if ("chatLinks".equals(string)) {
						class_316.field_1920.method_18492(this, string2);
					}

					if ("chatLinksPrompt".equals(string)) {
						class_316.field_1925.method_18492(this, string2);
					}

					if ("enableVsync".equals(string)) {
						class_316.field_1927.method_18492(this, string2);
					}

					if ("entityShadows".equals(string)) {
						class_316.field_18184.method_18492(this, string2);
					}

					if ("forceUnicodeFont".equals(string)) {
						class_316.field_18185.method_18492(this, string2);
					}

					if ("invertYMouse".equals(string)) {
						class_316.field_1963.method_18492(this, string2);
					}

					if ("realmsNotifications".equals(string)) {
						class_316.field_18186.method_18492(this, string2);
					}

					if ("reducedDebugInfo".equals(string)) {
						class_316.field_18187.method_18492(this, string2);
					}

					if ("showSubtitles".equals(string)) {
						class_316.field_18188.method_18492(this, string2);
					}

					if ("snooperEnabled".equals(string)) {
						class_316.field_1926.method_18492(this, string2);
					}

					if ("touchscreen".equals(string)) {
						class_316.field_1930.method_18492(this, string2);
					}

					if ("fullscreen".equals(string)) {
						class_316.field_1932.method_18492(this, string2);
					}

					if ("bobView".equals(string)) {
						class_316.field_1934.method_18492(this, string2);
					}

					if ("mouseSensitivity".equals(string)) {
						this.field_1843 = (double)method_1634(string2);
					}

					if ("fov".equals(string)) {
						this.field_1826 = (double)(method_1634(string2) * 40.0F + 70.0F);
					}

					if ("gamma".equals(string)) {
						this.field_1840 = (double)method_1634(string2);
					}

					if ("renderDistance".equals(string)) {
						this.field_1870 = Integer.parseInt(string2);
					}

					if ("guiScale".equals(string)) {
						this.field_1868 = Integer.parseInt(string2);
					}

					if ("particles".equals(string)) {
						this.field_1882 = class_4066.method_18608(Integer.parseInt(string2));
					}

					if ("maxFps".equals(string)) {
						this.field_1909 = Integer.parseInt(string2);
						this.field_1863.field_1704.method_15999(this.field_1909);
					}

					if ("difficulty".equals(string)) {
						this.field_1851 = class_1267.method_5462(Integer.parseInt(string2));
					}

					if ("fancyGraphics".equals(string)) {
						this.field_1833 = "true".equals(string2);
					}

					if ("tutorialStep".equals(string)) {
						this.field_1875 = class_1157.method_4919(string2);
					}

					if ("ao".equals(string)) {
						if ("true".equals(string2)) {
							this.field_1841 = class_4060.field_18146;
						} else if ("false".equals(string2)) {
							this.field_1841 = class_4060.field_18144;
						} else {
							this.field_1841 = class_4060.method_18484(Integer.parseInt(string2));
						}
					}

					if ("renderClouds".equals(string)) {
						if ("true".equals(string2)) {
							this.field_1814 = class_4063.field_18164;
						} else if ("false".equals(string2)) {
							this.field_1814 = class_4063.field_18162;
						} else if ("fast".equals(string2)) {
							this.field_1814 = class_4063.field_18163;
						}
					}

					if ("attackIndicator".equals(string)) {
						this.field_1895 = class_4061.method_18488(Integer.parseInt(string2));
					}

					if ("resourcePacks".equals(string)) {
						this.field_1887 = class_3518.method_15290(field_1823, string2, field_1859);
						if (this.field_1887 == null) {
							this.field_1887 = Lists.<String>newArrayList();
						}
					}

					if ("incompatibleResourcePacks".equals(string)) {
						this.field_1846 = class_3518.method_15290(field_1823, string2, field_1859);
						if (this.field_1846 == null) {
							this.field_1846 = Lists.<String>newArrayList();
						}
					}

					if ("lastServer".equals(string)) {
						this.field_1864 = string2;
					}

					if ("lang".equals(string)) {
						this.field_1883 = string2;
					}

					if ("chatVisibility".equals(string)) {
						this.field_1877 = class_1659.method_7360(Integer.parseInt(string2));
					}

					if ("chatOpacity".equals(string)) {
						this.field_1820 = (double)method_1634(string2);
					}

					if ("textBackgroundOpacity".equals(string)) {
						this.field_18726 = (double)method_1634(string2);
					}

					if ("backgroundForChatOnly".equals(string)) {
						this.field_18725 = "true".equals(string2);
					}

					if ("fullscreenResolution".equals(string)) {
						this.field_1828 = string2;
					}

					if ("hideServerAddress".equals(string)) {
						this.field_1815 = "true".equals(string2);
					}

					if ("advancedItemTooltips".equals(string)) {
						this.field_1827 = "true".equals(string2);
					}

					if ("pauseOnLostFocus".equals(string)) {
						this.field_1837 = "true".equals(string2);
					}

					if ("overrideHeight".equals(string)) {
						this.field_1885 = Integer.parseInt(string2);
					}

					if ("overrideWidth".equals(string)) {
						this.field_1872 = Integer.parseInt(string2);
					}

					if ("heldItemTooltips".equals(string)) {
						this.field_1905 = "true".equals(string2);
					}

					if ("chatHeightFocused".equals(string)) {
						this.field_1838 = (double)method_1634(string2);
					}

					if ("chatHeightUnfocused".equals(string)) {
						this.field_1825 = (double)method_1634(string2);
					}

					if ("chatScale".equals(string)) {
						this.field_1908 = (double)method_1634(string2);
					}

					if ("chatWidth".equals(string)) {
						this.field_1915 = (double)method_1634(string2);
					}

					if ("mipmapLevels".equals(string)) {
						this.field_1856 = Integer.parseInt(string2);
					}

					if ("useNativeTransport".equals(string)) {
						this.field_1876 = "true".equals(string2);
					}

					if ("mainHand".equals(string)) {
						this.field_1829 = "left".equals(string2) ? class_1306.field_6182 : class_1306.field_6183;
					}

					if ("narrator".equals(string)) {
						this.field_1896 = class_4065.method_18510(Integer.parseInt(string2));
					}

					if ("biomeBlendRadius".equals(string)) {
						this.field_1878 = Integer.parseInt(string2);
					}

					if ("mouseWheelSensitivity".equals(string)) {
						this.field_1889 = (double)method_1634(string2);
					}

					if ("glDebugVerbosity".equals(string)) {
						this.field_1901 = Integer.parseInt(string2);
					}

					for (class_304 lv2 : this.field_1839) {
						if (string.equals("key_" + lv2.method_1431())) {
							lv2.method_1422(class_3675.method_15981(string2));
						}
					}

					for (class_3419 lv3 : class_3419.values()) {
						if (string.equals("soundCategory_" + lv3.method_14840())) {
							this.field_1916.put(lv3, method_1634(string2));
						}
					}

					for (class_1664 lv4 : class_1664.values()) {
						if (string.equals("modelPart_" + lv4.method_7429())) {
							this.method_1635(lv4, "true".equals(string2));
						}
					}
				} catch (Exception var11) {
					field_1834.warn("Skipping bad option: {}:{}", string, string2);
				}
			}

			class_304.method_1426();
		} catch (Exception var12) {
			field_1834.error("Failed to load options", (Throwable)var12);
		}
	}

	private class_2487 method_1626(class_2487 arg) {
		int i = 0;

		try {
			i = Integer.parseInt(arg.method_10558("version"));
		} catch (RuntimeException var4) {
		}

		return class_2512.method_10688(this.field_1863.method_1543(), DataFixTypes.OPTIONS, arg, i);
	}

	private static float method_1634(String string) {
		if ("true".equals(string)) {
			return 1.0F;
		} else {
			return "false".equals(string) ? 0.0F : Float.parseFloat(string);
		}
	}

	public void method_1640() {
		try {
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.field_1897), StandardCharsets.UTF_8));
			Throwable var2 = null;

			try {
				printWriter.println("version:" + class_155.method_16673().getWorldVersion());
				printWriter.println("autoJump:" + class_316.field_18195.method_18494(this));
				printWriter.println("autoSuggestions:" + class_316.field_18196.method_18494(this));
				printWriter.println("chatColors:" + class_316.field_1917.method_18494(this));
				printWriter.println("chatLinks:" + class_316.field_1920.method_18494(this));
				printWriter.println("chatLinksPrompt:" + class_316.field_1925.method_18494(this));
				printWriter.println("enableVsync:" + class_316.field_1927.method_18494(this));
				printWriter.println("entityShadows:" + class_316.field_18184.method_18494(this));
				printWriter.println("forceUnicodeFont:" + class_316.field_18185.method_18494(this));
				printWriter.println("invertYMouse:" + class_316.field_1963.method_18494(this));
				printWriter.println("realmsNotifications:" + class_316.field_18186.method_18494(this));
				printWriter.println("reducedDebugInfo:" + class_316.field_18187.method_18494(this));
				printWriter.println("snooperEnabled:" + class_316.field_1926.method_18494(this));
				printWriter.println("showSubtitles:" + class_316.field_18188.method_18494(this));
				printWriter.println("touchscreen:" + class_316.field_1930.method_18494(this));
				printWriter.println("fullscreen:" + class_316.field_1932.method_18494(this));
				printWriter.println("bobView:" + class_316.field_1934.method_18494(this));
				printWriter.println("mouseSensitivity:" + this.field_1843);
				printWriter.println("fov:" + (this.field_1826 - 70.0) / 40.0);
				printWriter.println("gamma:" + this.field_1840);
				printWriter.println("renderDistance:" + this.field_1870);
				printWriter.println("guiScale:" + this.field_1868);
				printWriter.println("particles:" + this.field_1882.method_18609());
				printWriter.println("maxFps:" + this.field_1909);
				printWriter.println("difficulty:" + this.field_1851.method_5461());
				printWriter.println("fancyGraphics:" + this.field_1833);
				printWriter.println("ao:" + this.field_1841.method_18483());
				printWriter.println("biomeBlendRadius:" + this.field_1878);
				switch (this.field_1814) {
					case field_18164:
						printWriter.println("renderClouds:true");
						break;
					case field_18163:
						printWriter.println("renderClouds:fast");
						break;
					case field_18162:
						printWriter.println("renderClouds:false");
				}

				printWriter.println("resourcePacks:" + field_1823.toJson(this.field_1887));
				printWriter.println("incompatibleResourcePacks:" + field_1823.toJson(this.field_1846));
				printWriter.println("lastServer:" + this.field_1864);
				printWriter.println("lang:" + this.field_1883);
				printWriter.println("chatVisibility:" + this.field_1877.method_7362());
				printWriter.println("chatOpacity:" + this.field_1820);
				printWriter.println("textBackgroundOpacity:" + this.field_18726);
				printWriter.println("backgroundForChatOnly:" + this.field_18725);
				if (this.field_1863.field_1704.method_4511().isPresent()) {
					printWriter.println("fullscreenResolution:" + ((class_319)this.field_1863.field_1704.method_4511().get()).method_1670());
				}

				printWriter.println("hideServerAddress:" + this.field_1815);
				printWriter.println("advancedItemTooltips:" + this.field_1827);
				printWriter.println("pauseOnLostFocus:" + this.field_1837);
				printWriter.println("overrideWidth:" + this.field_1872);
				printWriter.println("overrideHeight:" + this.field_1885);
				printWriter.println("heldItemTooltips:" + this.field_1905);
				printWriter.println("chatHeightFocused:" + this.field_1838);
				printWriter.println("chatHeightUnfocused:" + this.field_1825);
				printWriter.println("chatScale:" + this.field_1908);
				printWriter.println("chatWidth:" + this.field_1915);
				printWriter.println("mipmapLevels:" + this.field_1856);
				printWriter.println("useNativeTransport:" + this.field_1876);
				printWriter.println("mainHand:" + (this.field_1829 == class_1306.field_6182 ? "left" : "right"));
				printWriter.println("attackIndicator:" + this.field_1895.method_18487());
				printWriter.println("narrator:" + this.field_1896.method_18509());
				printWriter.println("tutorialStep:" + this.field_1875.method_4920());
				printWriter.println("mouseWheelSensitivity:" + this.field_1889);
				printWriter.println("glDebugVerbosity:" + this.field_1901);

				for (class_304 lv : this.field_1839) {
					printWriter.println("key_" + lv.method_1431() + ":" + lv.method_1428());
				}

				for (class_3419 lv2 : class_3419.values()) {
					printWriter.println("soundCategory_" + lv2.method_14840() + ":" + this.method_1630(lv2));
				}

				for (class_1664 lv3 : class_1664.values()) {
					printWriter.println("modelPart_" + lv3.method_7429() + ":" + this.field_1892.contains(lv3));
				}
			} catch (Throwable var15) {
				var2 = var15;
				throw var15;
			} finally {
				if (printWriter != null) {
					if (var2 != null) {
						try {
							printWriter.close();
						} catch (Throwable var14) {
							var2.addSuppressed(var14);
						}
					} else {
						printWriter.close();
					}
				}
			}
		} catch (Exception var17) {
			field_1834.error("Failed to save options", (Throwable)var17);
		}

		this.method_1643();
	}

	public float method_1630(class_3419 arg) {
		return this.field_1916.containsKey(arg) ? (Float)this.field_1916.get(arg) : 1.0F;
	}

	public void method_1624(class_3419 arg, float f) {
		this.field_1863.method_1483().method_4865(arg, f);
		this.field_1916.put(arg, f);
	}

	public void method_1643() {
		if (this.field_1863.field_1724 != null) {
			int i = 0;

			for (class_1664 lv : this.field_1892) {
				i |= lv.method_7430();
			}

			this.field_1863.field_1724.field_3944.method_2883(new class_2803(this.field_1883, this.field_1870, this.field_1877, this.field_1900, i, this.field_1829));
		}
	}

	public Set<class_1664> method_1633() {
		return ImmutableSet.copyOf(this.field_1892);
	}

	public void method_1635(class_1664 arg, boolean bl) {
		if (bl) {
			this.field_1892.add(arg);
		} else {
			this.field_1892.remove(arg);
		}

		this.method_1643();
	}

	public void method_1631(class_1664 arg) {
		if (this.method_1633().contains(arg)) {
			this.field_1892.remove(arg);
		} else {
			this.field_1892.add(arg);
		}

		this.method_1643();
	}

	public class_4063 method_1632() {
		return this.field_1870 >= 4 ? this.field_1814 : class_4063.field_18162;
	}

	public boolean method_1639() {
		return this.field_1876;
	}

	public void method_1627(class_3283<class_1075> arg) {
		arg.method_14445();
		Set<class_1075> set = Sets.<class_1075>newLinkedHashSet();
		Iterator<String> iterator = this.field_1887.iterator();

		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			class_1075 lv = arg.method_14449(string);
			if (lv == null && !string.startsWith("file/")) {
				lv = arg.method_14449("file/" + string);
			}

			if (lv == null) {
				field_1834.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", string);
				iterator.remove();
			} else if (!lv.method_14460().method_14437() && !this.field_1846.contains(string)) {
				field_1834.warn("Removed resource pack {} from options because it is no longer compatible", string);
				iterator.remove();
			} else if (lv.method_14460().method_14437() && this.field_1846.contains(string)) {
				field_1834.info("Removed resource pack {} from incompatibility list because it's now compatible", string);
				this.field_1846.remove(string);
			} else {
				set.add(lv);
			}
		}

		arg.method_14447(set);
	}
}
