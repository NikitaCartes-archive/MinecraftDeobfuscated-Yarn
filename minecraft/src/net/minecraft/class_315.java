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
	private static final String[] field_1858 = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
	private static final String[] field_1861 = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
	private static final String[] field_1860 = new String[]{"options.off", "options.clouds.fast", "options.clouds.fancy"};
	private static final String[] field_1862 = new String[]{"options.off", "options.attack.crosshair", "options.attack.hotbar"};
	public static final String[] field_1898 = new String[]{"options.narrator.off", "options.narrator.all", "options.narrator.chat", "options.narrator.system"};
	public double field_1843 = 0.5;
	public boolean field_1865;
	public int field_1870 = -1;
	public boolean field_1891 = true;
	public int field_1909 = 120;
	public int field_1814 = 2;
	public boolean field_1833 = true;
	public int field_1841 = 2;
	public List<String> field_1887 = Lists.<String>newArrayList();
	public List<String> field_1846 = Lists.<String>newArrayList();
	public class_1657.class_1659 field_1877 = class_1657.class_1659.field_7538;
	public boolean field_1900 = true;
	public boolean field_1911 = true;
	public boolean field_1817 = true;
	public double field_1820 = 1.0;
	public boolean field_1847 = true;
	public boolean field_1857;
	@Nullable
	public String field_1828;
	public boolean field_1884 = true;
	public boolean field_1910;
	public boolean field_1815;
	public boolean field_1827;
	public boolean field_1837 = true;
	private final Set<class_1664> field_1892 = Sets.<class_1664>newHashSet(class_1664.values());
	public boolean field_1854;
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
	public boolean field_1888 = true;
	public int field_1895 = 1;
	public boolean field_1912;
	public boolean field_1818;
	public boolean field_1830 = true;
	public boolean field_1848 = true;
	public class_1157 field_1875 = class_1157.field_5650;
	public boolean field_1873 = true;
	public int field_1878 = 2;
	public double field_1889 = 1.0;
	public int field_1901 = 1;
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
			this.field_1844
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
	public boolean field_1821;
	public double field_1826 = 70.0;
	public double field_1840;
	public float field_1855;
	public int field_1868;
	public int field_1882;
	public int field_1896;
	public String field_1883 = "en_us";
	public boolean field_1819;

	public class_315(class_310 arg, File file) {
		this.field_1863 = arg;
		this.field_1897 = new File(file, "options.txt");
		if (arg.method_1540() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
			class_315.class_316.field_1933.method_1646(32.0F);
		} else {
			class_315.class_316.field_1933.method_1646(16.0F);
		}

		this.field_1870 = arg.method_1540() ? 12 : 8;
		this.method_1636();
	}

	public class_315() {
	}

	public void method_1641(class_304 arg, class_3675.class_306 arg2) {
		arg.method_1422(arg2);
		this.method_1640();
	}

	public void method_1625(class_315.class_316 arg, double d) {
		if (arg == class_315.class_316.field_1944) {
			this.field_1843 = d;
		}

		if (arg == class_315.class_316.field_1964) {
			this.field_1826 = d;
		}

		if (arg == class_315.class_316.field_1945) {
			this.field_1840 = d;
		}

		if (arg == class_315.class_316.field_1935) {
			this.field_1909 = (int)d;
			this.field_1863.field_1704.method_15999(this.field_1909);
		}

		if (arg == class_315.class_316.field_1921) {
			this.field_1820 = d;
			this.field_1863.field_1705.method_1743().method_1817();
		}

		if (arg == class_315.class_316.field_1940) {
			this.field_1838 = d;
			this.field_1863.field_1705.method_1743().method_1817();
		}

		if (arg == class_315.class_316.field_1942) {
			this.field_1825 = d;
			this.field_1863.field_1705.method_1743().method_1817();
		}

		if (arg == class_315.class_316.field_1941) {
			this.field_1915 = d;
			this.field_1863.field_1705.method_1743().method_1817();
		}

		if (arg == class_315.class_316.field_1946) {
			this.field_1908 = d;
			this.field_1863.field_1705.method_1743().method_1817();
		}

		if (arg == class_315.class_316.field_1943) {
			int i = this.field_1856;
			this.field_1856 = (int)d;
			if ((double)i != d) {
				this.field_1863.method_1549().method_4605(this.field_1856);
				this.field_1863.method_1531().method_4618(class_1059.field_5275);
				this.field_1863.method_1549().method_4527(false, this.field_1856 > 0);
				this.field_1863.method_1513();
			}
		}

		if (arg == class_315.class_316.field_1933) {
			this.field_1870 = (int)d;
			this.field_1863.field_1769.method_3292();
		}

		if (arg == class_315.class_316.field_1958) {
			this.field_1878 = class_3532.method_15340((int)d, 0, 7);
			this.field_1863.field_1769.method_3279();
		}

		if (arg == class_315.class_316.field_1931) {
			this.field_1863.field_1704.method_4505((int)d);
		}

		if (arg == class_315.class_316.field_1948) {
			this.field_1889 = d;
		}
	}

	public void method_1629(class_315.class_316 arg, int i) {
		if (arg == class_315.class_316.field_1933) {
			this.method_1625(arg, class_3532.method_15350((double)(this.field_1870 + i), arg.method_1649(), arg.method_1652()));
		}

		if (arg == class_315.class_316.field_1955) {
			this.field_1829 = this.field_1829.method_5928();
		}

		if (arg == class_315.class_316.field_1963) {
			this.field_1865 = !this.field_1865;
		}

		if (arg == class_315.class_316.field_1922) {
			this.field_1868 = Integer.remainderUnsigned(this.field_1868 + i, this.field_1863.field_1704.method_4476(0, this.field_1863.method_1573()) + 1);
		}

		if (arg == class_315.class_316.field_1919) {
			this.field_1882 = (this.field_1882 + i) % 3;
		}

		if (arg == class_315.class_316.field_1934) {
			this.field_1891 = !this.field_1891;
		}

		if (arg == class_315.class_316.field_1937) {
			this.field_1814 = (this.field_1814 + i) % 3;
		}

		if (arg == class_315.class_316.field_1961) {
			this.field_1819 = !this.field_1819;
			this.field_1863.method_1568().method_2018(this.field_1819);
		}

		if (arg == class_315.class_316.field_1938) {
			this.field_1833 = !this.field_1833;
			this.field_1863.field_1769.method_3279();
		}

		if (arg == class_315.class_316.field_1924) {
			this.field_1841 = (this.field_1841 + i) % 3;
			this.field_1863.field_1769.method_3279();
		}

		if (arg == class_315.class_316.field_1923) {
			this.field_1877 = class_1657.class_1659.method_7360((this.field_1877.method_7362() + i) % 3);
		}

		if (arg == class_315.class_316.field_1917) {
			this.field_1900 = !this.field_1900;
		}

		if (arg == class_315.class_316.field_1920) {
			this.field_1911 = !this.field_1911;
		}

		if (arg == class_315.class_316.field_1925) {
			this.field_1817 = !this.field_1817;
		}

		if (arg == class_315.class_316.field_1926) {
			this.field_1847 = !this.field_1847;
		}

		if (arg == class_315.class_316.field_1930) {
			this.field_1854 = !this.field_1854;
		}

		if (arg == class_315.class_316.field_1932) {
			this.field_1857 = !this.field_1857;
			if (this.field_1863.field_1704.method_4498() != this.field_1857) {
				this.field_1863.field_1704.method_4500();
				this.field_1857 = this.field_1863.field_1704.method_4498();
			}
		}

		if (arg == class_315.class_316.field_1927) {
			this.field_1884 = !this.field_1884;
			this.field_1863.field_1704.method_4497(this.field_1884);
		}

		if (arg == class_315.class_316.field_1962) {
			this.field_1910 = !this.field_1910;
		}

		if (arg == class_315.class_316.field_1954) {
			this.field_1888 = !this.field_1888;
		}

		if (arg == class_315.class_316.field_1959) {
			this.field_1895 = (this.field_1895 + i) % 3;
		}

		if (arg == class_315.class_316.field_1951) {
			this.field_1818 = !this.field_1818;
		}

		if (arg == class_315.class_316.field_1953) {
			this.field_1830 = !this.field_1830;
		}

		if (arg == class_315.class_316.field_1960) {
			this.field_1848 = !this.field_1848;
		}

		if (arg == class_315.class_316.field_1957) {
			this.field_1873 = !this.field_1873;
		}

		if (arg == class_315.class_316.field_1956) {
			if (class_333.field_2054.method_1791()) {
				this.field_1896 = (this.field_1896 + i) % field_1898.length;
			} else {
				this.field_1896 = 0;
			}

			class_333.field_2054.method_1792(this.field_1896);
		}

		this.method_1640();
	}

	public double method_1637(class_315.class_316 arg) {
		if (arg == class_315.class_316.field_1958) {
			return (double)this.field_1878;
		} else if (arg == class_315.class_316.field_1964) {
			return this.field_1826;
		} else if (arg == class_315.class_316.field_1945) {
			return this.field_1840;
		} else if (arg == class_315.class_316.field_1939) {
			return (double)this.field_1855;
		} else if (arg == class_315.class_316.field_1944) {
			return this.field_1843;
		} else if (arg == class_315.class_316.field_1921) {
			return this.field_1820;
		} else if (arg == class_315.class_316.field_1940) {
			return this.field_1838;
		} else if (arg == class_315.class_316.field_1942) {
			return this.field_1825;
		} else if (arg == class_315.class_316.field_1946) {
			return this.field_1908;
		} else if (arg == class_315.class_316.field_1941) {
			return this.field_1915;
		} else if (arg == class_315.class_316.field_1935) {
			return (double)this.field_1909;
		} else if (arg == class_315.class_316.field_1943) {
			return (double)this.field_1856;
		} else if (arg == class_315.class_316.field_1933) {
			return (double)this.field_1870;
		} else if (arg == class_315.class_316.field_1931) {
			return (double)this.field_1863.field_1704.method_4508();
		} else {
			return arg == class_315.class_316.field_1948 ? this.field_1889 : 0.0;
		}
	}

	public boolean method_1628(class_315.class_316 arg) {
		switch (arg) {
			case field_1963:
				return this.field_1865;
			case field_1934:
				return this.field_1891;
			case field_1917:
				return this.field_1900;
			case field_1920:
				return this.field_1911;
			case field_1925:
				return this.field_1817;
			case field_1926:
				if (this.field_1847) {
				}

				return false;
			case field_1932:
				return this.field_1857;
			case field_1927:
				return this.field_1884;
			case field_1930:
				return this.field_1854;
			case field_1961:
				return this.field_1819;
			case field_1962:
				return this.field_1910;
			case field_1954:
				return this.field_1888;
			case field_1951:
				return this.field_1818;
			case field_1953:
				return this.field_1830;
			case field_1952:
				return this.field_1912;
			case field_1960:
				return this.field_1848;
			case field_1957:
				return this.field_1873;
			default:
				return false;
		}
	}

	private static String method_1638(String[] strings, int i) {
		if (i < 0 || i >= strings.length) {
			i = 0;
		}

		return class_1074.method_4662(strings[i]);
	}

	public String method_1642(class_315.class_316 arg) {
		String string = class_1074.method_4662(arg.method_1644()) + ": ";
		if (arg.method_1653()) {
			double d = this.method_1637(arg);
			double e = arg.method_1651(d);
			if (arg == class_315.class_316.field_1944) {
				if (e == 0.0) {
					return string + class_1074.method_4662("options.sensitivity.min");
				} else {
					return e == 1.0 ? string + class_1074.method_4662("options.sensitivity.max") : string + (int)(e * 200.0) + "%";
				}
			} else if (arg == class_315.class_316.field_1958) {
				if (e == 0.0) {
					return string + class_1074.method_4662("options.off");
				} else {
					int i = this.field_1878 * 2 + 1;
					return string + i + "x" + i;
				}
			} else if (arg == class_315.class_316.field_1964) {
				if (d == 70.0) {
					return string + class_1074.method_4662("options.fov.min");
				} else {
					return d == 110.0 ? string + class_1074.method_4662("options.fov.max") : string + (int)d;
				}
			} else if (arg == class_315.class_316.field_1935) {
				return d == arg.field_1950 ? string + class_1074.method_4662("options.framerateLimit.max") : string + class_1074.method_4662("options.framerate", (int)d);
			} else if (arg == class_315.class_316.field_1937) {
				return d == arg.field_1947 ? string + class_1074.method_4662("options.cloudHeight.min") : string + ((int)d + 128);
			} else if (arg == class_315.class_316.field_1945) {
				if (e == 0.0) {
					return string + class_1074.method_4662("options.gamma.min");
				} else {
					return e == 1.0 ? string + class_1074.method_4662("options.gamma.max") : string + "+" + (int)(e * 100.0) + "%";
				}
			} else if (arg == class_315.class_316.field_1939) {
				return string + (int)(e * 400.0) + "%";
			} else if (arg == class_315.class_316.field_1921) {
				return string + (int)(e * 90.0 + 10.0) + "%";
			} else if (arg == class_315.class_316.field_1942) {
				return string + class_338.method_1818(e) + "px";
			} else if (arg == class_315.class_316.field_1940) {
				return string + class_338.method_1818(e) + "px";
			} else if (arg == class_315.class_316.field_1941) {
				return string + class_338.method_1806(e) + "px";
			} else if (arg == class_315.class_316.field_1933) {
				return string + class_1074.method_4662("options.chunks", (int)d);
			} else if (arg == class_315.class_316.field_1948) {
				return e == 1.0 ? string + class_1074.method_4662("options.mouseWheelSensitivity.default") : string + "+" + (int)e + "." + (int)(e * 10.0) % 10;
			} else if (arg == class_315.class_316.field_1943) {
				return d == 0.0 ? string + class_1074.method_4662("options.off") : string + (int)d;
			} else if (arg == class_315.class_316.field_1931) {
				return d == 0.0 ? string + class_1074.method_4662("options.fullscreen.current") : string + this.field_1863.field_1704.method_4487((int)d - 1);
			} else {
				return e == 0.0 ? string + class_1074.method_4662("options.off") : string + (int)(e * 100.0) + "%";
			}
		} else if (arg.method_1654()) {
			boolean bl = this.method_1628(arg);
			return bl ? string + class_1074.method_4662("options.on") : string + class_1074.method_4662("options.off");
		} else if (arg == class_315.class_316.field_1955) {
			return string + this.field_1829;
		} else if (arg == class_315.class_316.field_1922) {
			return string + (this.field_1868 == 0 ? class_1074.method_4662("options.guiScale.auto") : this.field_1868);
		} else if (arg == class_315.class_316.field_1923) {
			return string + class_1074.method_4662(this.field_1877.method_7359());
		} else if (arg == class_315.class_316.field_1919) {
			return string + method_1638(field_1858, this.field_1882);
		} else if (arg == class_315.class_316.field_1924) {
			return string + method_1638(field_1861, this.field_1841);
		} else if (arg == class_315.class_316.field_1937) {
			return string + method_1638(field_1860, this.field_1814);
		} else if (arg == class_315.class_316.field_1938) {
			if (this.field_1833) {
				return string + class_1074.method_4662("options.graphics.fancy");
			} else {
				String string2 = "options.graphics.fast";
				return string + class_1074.method_4662("options.graphics.fast");
			}
		} else if (arg == class_315.class_316.field_1959) {
			return string + method_1638(field_1862, this.field_1895);
		} else if (arg == class_315.class_316.field_1956) {
			return class_333.field_2054.method_1791()
				? string + method_1638(field_1898, this.field_1896)
				: string + class_1074.method_4662("options.narrator.notavailable");
		} else {
			return string;
		}
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
					if ("mouseSensitivity".equals(string)) {
						this.field_1843 = (double)this.method_1634(string2);
					}

					if ("fov".equals(string)) {
						this.field_1826 = (double)(this.method_1634(string2) * 40.0F + 70.0F);
					}

					if ("gamma".equals(string)) {
						this.field_1840 = (double)this.method_1634(string2);
					}

					if ("saturation".equals(string)) {
						this.field_1855 = this.method_1634(string2);
					}

					if ("invertYMouse".equals(string)) {
						this.field_1865 = "true".equals(string2);
					}

					if ("renderDistance".equals(string)) {
						this.field_1870 = Integer.parseInt(string2);
					}

					if ("guiScale".equals(string)) {
						this.field_1868 = Integer.parseInt(string2);
					}

					if ("particles".equals(string)) {
						this.field_1882 = Integer.parseInt(string2);
					}

					if ("bobView".equals(string)) {
						this.field_1891 = "true".equals(string2);
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
							this.field_1841 = 2;
						} else if ("false".equals(string2)) {
							this.field_1841 = 0;
						} else {
							this.field_1841 = Integer.parseInt(string2);
						}
					}

					if ("renderClouds".equals(string)) {
						if ("true".equals(string2)) {
							this.field_1814 = 2;
						} else if ("false".equals(string2)) {
							this.field_1814 = 0;
						} else if ("fast".equals(string2)) {
							this.field_1814 = 1;
						}
					}

					if ("attackIndicator".equals(string)) {
						if ("0".equals(string2)) {
							this.field_1895 = 0;
						} else if ("1".equals(string2)) {
							this.field_1895 = 1;
						} else if ("2".equals(string2)) {
							this.field_1895 = 2;
						}
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
						this.field_1877 = class_1657.class_1659.method_7360(Integer.parseInt(string2));
					}

					if ("chatColors".equals(string)) {
						this.field_1900 = "true".equals(string2);
					}

					if ("chatLinks".equals(string)) {
						this.field_1911 = "true".equals(string2);
					}

					if ("chatLinksPrompt".equals(string)) {
						this.field_1817 = "true".equals(string2);
					}

					if ("chatOpacity".equals(string)) {
						this.field_1820 = (double)this.method_1634(string2);
					}

					if ("snooperEnabled".equals(string)) {
						this.field_1847 = "true".equals(string2);
					}

					if ("fullscreen".equals(string)) {
						this.field_1857 = "true".equals(string2);
					}

					if ("fullscreenResolution".equals(string)) {
						this.field_1828 = string2;
					}

					if ("enableVsync".equals(string)) {
						this.field_1884 = "true".equals(string2);
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

					if ("touchscreen".equals(string)) {
						this.field_1854 = "true".equals(string2);
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
						this.field_1838 = (double)this.method_1634(string2);
					}

					if ("chatHeightUnfocused".equals(string)) {
						this.field_1825 = (double)this.method_1634(string2);
					}

					if ("chatScale".equals(string)) {
						this.field_1908 = (double)this.method_1634(string2);
					}

					if ("chatWidth".equals(string)) {
						this.field_1915 = (double)this.method_1634(string2);
					}

					if ("mipmapLevels".equals(string)) {
						this.field_1856 = Integer.parseInt(string2);
					}

					if ("forceUnicodeFont".equals(string)) {
						this.field_1819 = "true".equals(string2);
					}

					if ("reducedDebugInfo".equals(string)) {
						this.field_1910 = "true".equals(string2);
					}

					if ("useNativeTransport".equals(string)) {
						this.field_1876 = "true".equals(string2);
					}

					if ("entityShadows".equals(string)) {
						this.field_1888 = "true".equals(string2);
					}

					if ("mainHand".equals(string)) {
						this.field_1829 = "left".equals(string2) ? class_1306.field_6182 : class_1306.field_6183;
					}

					if ("showSubtitles".equals(string)) {
						this.field_1818 = "true".equals(string2);
					}

					if ("realmsNotifications".equals(string)) {
						this.field_1830 = "true".equals(string2);
					}

					if ("enableWeakAttacks".equals(string)) {
						this.field_1912 = "true".equals(string2);
					}

					if ("autoJump".equals(string)) {
						this.field_1848 = "true".equals(string2);
					}

					if ("narrator".equals(string)) {
						this.field_1896 = Integer.parseInt(string2);
					}

					if ("autoSuggestions".equals(string)) {
						this.field_1873 = "true".equals(string2);
					}

					if ("biomeBlendRadius".equals(string)) {
						this.field_1878 = Integer.parseInt(string2);
					}

					if ("mouseWheelSensitivity".equals(string)) {
						this.field_1889 = (double)this.method_1634(string2);
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
							this.field_1916.put(lv3, this.method_1634(string2));
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

	private float method_1634(String string) {
		if ("true".equals(string)) {
			return 1.0F;
		} else {
			return "false".equals(string) ? 0.0F : Float.parseFloat(string);
		}
	}

	public void method_1640() {
		PrintWriter printWriter = null;

		try {
			printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.field_1897), StandardCharsets.UTF_8));
			printWriter.println("version:" + class_155.method_16673().getWorldVersion());
			printWriter.println("invertYMouse:" + this.field_1865);
			printWriter.println("mouseSensitivity:" + this.field_1843);
			printWriter.println("fov:" + (this.field_1826 - 70.0) / 40.0);
			printWriter.println("gamma:" + this.field_1840);
			printWriter.println("saturation:" + this.field_1855);
			printWriter.println("renderDistance:" + this.field_1870);
			printWriter.println("guiScale:" + this.field_1868);
			printWriter.println("particles:" + this.field_1882);
			printWriter.println("bobView:" + this.field_1891);
			printWriter.println("maxFps:" + this.field_1909);
			printWriter.println("difficulty:" + this.field_1851.method_5461());
			printWriter.println("fancyGraphics:" + this.field_1833);
			printWriter.println("ao:" + this.field_1841);
			printWriter.println("biomeBlendRadius:" + this.field_1878);
			switch (this.field_1814) {
				case 0:
					printWriter.println("renderClouds:false");
					break;
				case 1:
					printWriter.println("renderClouds:fast");
					break;
				case 2:
					printWriter.println("renderClouds:true");
			}

			printWriter.println("resourcePacks:" + field_1823.toJson(this.field_1887));
			printWriter.println("incompatibleResourcePacks:" + field_1823.toJson(this.field_1846));
			printWriter.println("lastServer:" + this.field_1864);
			printWriter.println("lang:" + this.field_1883);
			printWriter.println("chatVisibility:" + this.field_1877.method_7362());
			printWriter.println("chatColors:" + this.field_1900);
			printWriter.println("chatLinks:" + this.field_1911);
			printWriter.println("chatLinksPrompt:" + this.field_1817);
			printWriter.println("chatOpacity:" + this.field_1820);
			printWriter.println("snooperEnabled:" + this.field_1847);
			printWriter.println("fullscreen:" + this.field_1857);
			if (this.field_1863.field_1704.method_4511().isPresent()) {
				printWriter.println("fullscreenResolution:" + ((class_319)this.field_1863.field_1704.method_4511().get()).method_1670());
			}

			printWriter.println("enableVsync:" + this.field_1884);
			printWriter.println("hideServerAddress:" + this.field_1815);
			printWriter.println("advancedItemTooltips:" + this.field_1827);
			printWriter.println("pauseOnLostFocus:" + this.field_1837);
			printWriter.println("touchscreen:" + this.field_1854);
			printWriter.println("overrideWidth:" + this.field_1872);
			printWriter.println("overrideHeight:" + this.field_1885);
			printWriter.println("heldItemTooltips:" + this.field_1905);
			printWriter.println("chatHeightFocused:" + this.field_1838);
			printWriter.println("chatHeightUnfocused:" + this.field_1825);
			printWriter.println("chatScale:" + this.field_1908);
			printWriter.println("chatWidth:" + this.field_1915);
			printWriter.println("mipmapLevels:" + this.field_1856);
			printWriter.println("forceUnicodeFont:" + this.field_1819);
			printWriter.println("reducedDebugInfo:" + this.field_1910);
			printWriter.println("useNativeTransport:" + this.field_1876);
			printWriter.println("entityShadows:" + this.field_1888);
			printWriter.println("mainHand:" + (this.field_1829 == class_1306.field_6182 ? "left" : "right"));
			printWriter.println("attackIndicator:" + this.field_1895);
			printWriter.println("showSubtitles:" + this.field_1818);
			printWriter.println("realmsNotifications:" + this.field_1830);
			printWriter.println("enableWeakAttacks:" + this.field_1912);
			printWriter.println("autoJump:" + this.field_1848);
			printWriter.println("narrator:" + this.field_1896);
			printWriter.println("tutorialStep:" + this.field_1875.method_4920());
			printWriter.println("autoSuggestions:" + this.field_1873);
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
		} catch (Exception var9) {
			field_1834.error("Failed to save options", (Throwable)var9);
		} finally {
			IOUtils.closeQuietly(printWriter);
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

	public int method_1632() {
		return this.field_1870 >= 4 ? this.field_1814 : 0;
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

	@Environment(EnvType.CLIENT)
	public static enum class_316 {
		field_1963("options.invertMouse", false, true),
		field_1944("options.sensitivity", true, false),
		field_1964("options.fov", true, false, 30.0, 110.0, 1.0F),
		field_1945("options.gamma", true, false),
		field_1939("options.saturation", true, false),
		field_1933("options.renderDistance", true, false, 2.0, 16.0, 1.0F),
		field_1934("options.viewBobbing", false, true),
		field_1935("options.framerateLimit", true, false, 10.0, 260.0, 10.0F),
		field_1937("options.renderClouds", false, false),
		field_1938("options.graphics", false, false),
		field_1924("options.ao", false, false),
		field_1922("options.guiScale", false, false),
		field_1919("options.particles", false, false),
		field_1923("options.chat.visibility", false, false),
		field_1917("options.chat.color", false, true),
		field_1920("options.chat.links", false, true),
		field_1921("options.chat.opacity", true, false),
		field_1925("options.chat.links.prompt", false, true),
		field_1926("options.snooper", false, true),
		field_1931("options.fullscreen.resolution", true, false, 0.0, 0.0, 1.0F),
		field_1932("options.fullscreen", false, true),
		field_1927("options.vsync", false, true),
		field_1930("options.touchscreen", false, true),
		field_1946("options.chat.scale", true, false),
		field_1941("options.chat.width", true, false),
		field_1940("options.chat.height.focused", true, false),
		field_1942("options.chat.height.unfocused", true, false),
		field_1943("options.mipmapLevels", true, false, 0.0, 4.0, 1.0F),
		field_1961("options.forceUnicodeFont", false, true),
		field_1962("options.reducedDebugInfo", false, true),
		field_1954("options.entityShadows", false, true),
		field_1955("options.mainHand", false, false),
		field_1959("options.attackIndicator", false, false),
		field_1952("options.enableWeakAttacks", false, true),
		field_1951("options.showSubtitles", false, true),
		field_1953("options.realmsNotifications", false, true),
		field_1960("options.autoJump", false, true),
		field_1956("options.narrator", false, false),
		field_1957("options.autoSuggestCommands", false, true),
		field_1958("options.biomeBlendRadius", true, false, 0.0, 7.0, 1.0F),
		field_1948("options.mouseWheelSensitivity", true, false, 1.0, 10.0, 0.5F);

		private final boolean field_1949;
		private final boolean field_1965;
		private final String field_1966;
		private final float field_1928;
		private double field_1947;
		private double field_1950;

		public static class_315.class_316 method_1655(int i) {
			for (class_315.class_316 lv : values()) {
				if (lv.method_1647() == i) {
					return lv;
				}
			}

			return null;
		}

		private class_316(String string2, boolean bl, boolean bl2) {
			this(string2, bl, bl2, 0.0, 1.0, 0.0F);
		}

		private class_316(String string2, boolean bl, boolean bl2, double d, double e, float f) {
			this.field_1966 = string2;
			this.field_1949 = bl;
			this.field_1965 = bl2;
			this.field_1947 = d;
			this.field_1950 = e;
			this.field_1928 = f;
		}

		public boolean method_1653() {
			return this.field_1949;
		}

		public boolean method_1654() {
			return this.field_1965;
		}

		public int method_1647() {
			return this.ordinal();
		}

		public String method_1644() {
			return this.field_1966;
		}

		public double method_1649() {
			return this.field_1947;
		}

		public double method_1652() {
			return this.field_1950;
		}

		public void method_1646(float f) {
			this.field_1950 = (double)f;
		}

		public double method_1651(double d) {
			return class_3532.method_15350((this.method_1657(d) - this.field_1947) / (this.field_1950 - this.field_1947), 0.0, 1.0);
		}

		public double method_1645(double d) {
			return this.method_1657(class_3532.method_16436(class_3532.method_15350(d, 0.0, 1.0), this.field_1947, this.field_1950));
		}

		public double method_1657(double d) {
			d = this.method_1648(d);
			return class_3532.method_15350(d, this.field_1947, this.field_1950);
		}

		private double method_1648(double d) {
			if (this.field_1928 > 0.0F) {
				d = (double)(this.field_1928 * (float)Math.round(d / (double)this.field_1928));
			}

			return d;
		}
	}
}
