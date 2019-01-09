package net.minecraft;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum class_2350 implements class_3542 {
	field_11033(0, 1, -1, "down", class_2350.class_2352.field_11060, class_2350.class_2351.field_11052, new class_2382(0, -1, 0)),
	field_11036(1, 0, -1, "up", class_2350.class_2352.field_11056, class_2350.class_2351.field_11052, new class_2382(0, 1, 0)),
	field_11043(2, 3, 2, "north", class_2350.class_2352.field_11060, class_2350.class_2351.field_11051, new class_2382(0, 0, -1)),
	field_11035(3, 2, 0, "south", class_2350.class_2352.field_11056, class_2350.class_2351.field_11051, new class_2382(0, 0, 1)),
	field_11039(4, 5, 1, "west", class_2350.class_2352.field_11060, class_2350.class_2351.field_11048, new class_2382(-1, 0, 0)),
	field_11034(5, 4, 3, "east", class_2350.class_2352.field_11056, class_2350.class_2351.field_11048, new class_2382(1, 0, 0));

	private final int field_11032;
	private final int field_11031;
	private final int field_11030;
	private final String field_11046;
	private final class_2350.class_2351 field_11047;
	private final class_2350.class_2352 field_11044;
	private final class_2382 field_11042;
	private static final class_2350[] field_11040 = values();
	private static final Map<String, class_2350> field_11045 = (Map<String, class_2350>)Arrays.stream(field_11040)
		.collect(Collectors.toMap(class_2350::method_10151, arg -> arg));
	private static final class_2350[] field_11038 = (class_2350[])Arrays.stream(field_11040)
		.sorted(Comparator.comparingInt(arg -> arg.field_11032))
		.toArray(class_2350[]::new);
	private static final class_2350[] field_11041 = (class_2350[])Arrays.stream(field_11040)
		.filter(arg -> arg.method_10166().method_10179())
		.sorted(Comparator.comparingInt(arg -> arg.field_11030))
		.toArray(class_2350[]::new);
	private static final Long2ObjectMap<class_2350> field_16542 = (Long2ObjectMap<class_2350>)Arrays.stream(field_11040)
		.collect(Collectors.toMap(arg -> new class_2338(arg.method_10163()).method_10063(), arg -> arg, (arg, arg2) -> {
			throw new IllegalArgumentException("Duplicate keys");
		}, Long2ObjectOpenHashMap::new));

	private class_2350(int j, int k, int l, String string2, class_2350.class_2352 arg, class_2350.class_2351 arg2, class_2382 arg3) {
		this.field_11032 = j;
		this.field_11030 = l;
		this.field_11031 = k;
		this.field_11046 = string2;
		this.field_11047 = arg2;
		this.field_11044 = arg;
		this.field_11042 = arg3;
	}

	public static class_2350[] method_10159(class_1297 arg) {
		float f = arg.method_5695(1.0F) * (float) (Math.PI / 180.0);
		float g = -arg.method_5705(1.0F) * (float) (Math.PI / 180.0);
		float h = class_3532.method_15374(f);
		float i = class_3532.method_15362(f);
		float j = class_3532.method_15374(g);
		float k = class_3532.method_15362(g);
		boolean bl = j > 0.0F;
		boolean bl2 = h < 0.0F;
		boolean bl3 = k > 0.0F;
		float l = bl ? j : -j;
		float m = bl2 ? -h : h;
		float n = bl3 ? k : -k;
		float o = l * i;
		float p = n * i;
		class_2350 lv = bl ? field_11034 : field_11039;
		class_2350 lv2 = bl2 ? field_11036 : field_11033;
		class_2350 lv3 = bl3 ? field_11035 : field_11043;
		if (l > n) {
			if (m > o) {
				return method_10145(lv2, lv, lv3);
			} else {
				return p > m ? method_10145(lv, lv3, lv2) : method_10145(lv, lv2, lv3);
			}
		} else if (m > p) {
			return method_10145(lv2, lv3, lv);
		} else {
			return o > m ? method_10145(lv3, lv, lv2) : method_10145(lv3, lv2, lv);
		}
	}

	private static class_2350[] method_10145(class_2350 arg, class_2350 arg2, class_2350 arg3) {
		return new class_2350[]{arg, arg2, arg3, arg3.method_10153(), arg2.method_10153(), arg.method_10153()};
	}

	public int method_10146() {
		return this.field_11032;
	}

	public int method_10161() {
		return this.field_11030;
	}

	public class_2350.class_2352 method_10171() {
		return this.field_11044;
	}

	public class_2350 method_10153() {
		return method_10143(this.field_11031);
	}

	@Environment(EnvType.CLIENT)
	public class_2350 method_10152(class_2350.class_2351 arg) {
		switch (arg) {
			case field_11048:
				if (this != field_11039 && this != field_11034) {
					return this.method_10167();
				}

				return this;
			case field_11052:
				if (this != field_11036 && this != field_11033) {
					return this.method_10170();
				}

				return this;
			case field_11051:
				if (this != field_11043 && this != field_11035) {
					return this.method_10154();
				}

				return this;
			default:
				throw new IllegalStateException("Unable to get CW facing for axis " + arg);
		}
	}

	public class_2350 method_10170() {
		switch (this) {
			case field_11043:
				return field_11034;
			case field_11034:
				return field_11035;
			case field_11035:
				return field_11039;
			case field_11039:
				return field_11043;
			default:
				throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
		}
	}

	@Environment(EnvType.CLIENT)
	private class_2350 method_10167() {
		switch (this) {
			case field_11043:
				return field_11033;
			case field_11034:
			case field_11039:
			default:
				throw new IllegalStateException("Unable to get X-rotated facing of " + this);
			case field_11035:
				return field_11036;
			case field_11036:
				return field_11043;
			case field_11033:
				return field_11035;
		}
	}

	@Environment(EnvType.CLIENT)
	private class_2350 method_10154() {
		switch (this) {
			case field_11034:
				return field_11033;
			case field_11035:
			default:
				throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
			case field_11039:
				return field_11036;
			case field_11036:
				return field_11034;
			case field_11033:
				return field_11039;
		}
	}

	public class_2350 method_10160() {
		switch (this) {
			case field_11043:
				return field_11039;
			case field_11034:
				return field_11043;
			case field_11035:
				return field_11034;
			case field_11039:
				return field_11035;
			default:
				throw new IllegalStateException("Unable to get CCW facing of " + this);
		}
	}

	public int method_10148() {
		return this.field_11047 == class_2350.class_2351.field_11048 ? this.field_11044.method_10181() : 0;
	}

	public int method_10164() {
		return this.field_11047 == class_2350.class_2351.field_11052 ? this.field_11044.method_10181() : 0;
	}

	public int method_10165() {
		return this.field_11047 == class_2350.class_2351.field_11051 ? this.field_11044.method_10181() : 0;
	}

	public String method_10151() {
		return this.field_11046;
	}

	public class_2350.class_2351 method_10166() {
		return this.field_11047;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static class_2350 method_10168(@Nullable String string) {
		return string == null ? null : (class_2350)field_11045.get(string.toLowerCase(Locale.ROOT));
	}

	public static class_2350 method_10143(int i) {
		return field_11038[class_3532.method_15382(i % field_11038.length)];
	}

	public static class_2350 method_10139(int i) {
		return field_11041[class_3532.method_15382(i % field_11041.length)];
	}

	@Nullable
	public static class_2350 method_16365(class_2338 arg) {
		return field_16542.get(arg.method_10063());
	}

	public static class_2350 method_10150(double d) {
		return method_10139(class_3532.method_15357(d / 90.0 + 0.5) & 3);
	}

	public static class_2350 method_10169(class_2350.class_2351 arg, class_2350.class_2352 arg2) {
		switch (arg) {
			case field_11048:
				return arg2 == class_2350.class_2352.field_11056 ? field_11034 : field_11039;
			case field_11052:
				return arg2 == class_2350.class_2352.field_11056 ? field_11036 : field_11033;
			case field_11051:
			default:
				return arg2 == class_2350.class_2352.field_11056 ? field_11035 : field_11043;
		}
	}

	public float method_10144() {
		return (float)((this.field_11030 & 3) * 90);
	}

	public static class_2350 method_10162(Random random) {
		return values()[random.nextInt(values().length)];
	}

	public static class_2350 method_10142(double d, double e, double f) {
		return method_10147((float)d, (float)e, (float)f);
	}

	public static class_2350 method_10147(float f, float g, float h) {
		class_2350 lv = field_11043;
		float i = Float.MIN_VALUE;

		for (class_2350 lv2 : field_11040) {
			float j = f * (float)lv2.field_11042.method_10263() + g * (float)lv2.field_11042.method_10264() + h * (float)lv2.field_11042.method_10260();
			if (j > i) {
				i = j;
				lv = lv2;
			}
		}

		return lv;
	}

	public String toString() {
		return this.field_11046;
	}

	@Override
	public String method_15434() {
		return this.field_11046;
	}

	public static class_2350 method_10156(class_2350.class_2352 arg, class_2350.class_2351 arg2) {
		for (class_2350 lv : values()) {
			if (lv.method_10171() == arg && lv.method_10166() == arg2) {
				return lv;
			}
		}

		throw new IllegalArgumentException("No such direction: " + arg + " " + arg2);
	}

	public class_2382 method_10163() {
		return this.field_11042;
	}

	public static enum class_2351 implements Predicate<class_2350>, class_3542 {
		field_11048("x") {
			@Override
			public int method_10173(int i, int j, int k) {
				return i;
			}

			@Override
			public double method_10172(double d, double e, double f) {
				return d;
			}
		},
		field_11052("y") {
			@Override
			public int method_10173(int i, int j, int k) {
				return j;
			}

			@Override
			public double method_10172(double d, double e, double f) {
				return e;
			}
		},
		field_11051("z") {
			@Override
			public int method_10173(int i, int j, int k) {
				return k;
			}

			@Override
			public double method_10172(double d, double e, double f) {
				return f;
			}
		};

		private static final Map<String, class_2350.class_2351> field_11050 = (Map<String, class_2350.class_2351>)Arrays.stream(values())
			.collect(Collectors.toMap(class_2350.class_2351::method_10174, arg -> arg));
		private final String field_11053;

		private class_2351(String string2) {
			this.field_11053 = string2;
		}

		@Nullable
		@Environment(EnvType.CLIENT)
		public static class_2350.class_2351 method_10177(String string) {
			return (class_2350.class_2351)field_11050.get(string.toLowerCase(Locale.ROOT));
		}

		public String method_10174() {
			return this.field_11053;
		}

		public boolean method_10178() {
			return this == field_11052;
		}

		public boolean method_10179() {
			return this == field_11048 || this == field_11051;
		}

		public String toString() {
			return this.field_11053;
		}

		public static class_2350.class_2351 method_16699(Random random) {
			return values()[random.nextInt(values().length)];
		}

		public boolean method_10176(@Nullable class_2350 arg) {
			return arg != null && arg.method_10166() == this;
		}

		public class_2350.class_2353 method_10180() {
			switch (this) {
				case field_11048:
				case field_11051:
					return class_2350.class_2353.field_11062;
				case field_11052:
					return class_2350.class_2353.field_11064;
				default:
					throw new Error("Someone's been tampering with the universe!");
			}
		}

		@Override
		public String method_15434() {
			return this.field_11053;
		}

		public abstract int method_10173(int i, int j, int k);

		public abstract double method_10172(double d, double e, double f);
	}

	public static enum class_2352 {
		field_11056(1, "Towards positive"),
		field_11060(-1, "Towards negative");

		private final int field_11059;
		private final String field_11057;

		private class_2352(int j, String string2) {
			this.field_11059 = j;
			this.field_11057 = string2;
		}

		public int method_10181() {
			return this.field_11059;
		}

		public String toString() {
			return this.field_11057;
		}
	}

	public static enum class_2353 implements Iterable<class_2350>, Predicate<class_2350> {
		field_11062(
			new class_2350[]{class_2350.field_11043, class_2350.field_11034, class_2350.field_11035, class_2350.field_11039},
			new class_2350.class_2351[]{class_2350.class_2351.field_11048, class_2350.class_2351.field_11051}
		),
		field_11064(new class_2350[]{class_2350.field_11036, class_2350.field_11033}, new class_2350.class_2351[]{class_2350.class_2351.field_11052});

		private final class_2350[] field_11061;
		private final class_2350.class_2351[] field_11065;

		private class_2353(class_2350[] args, class_2350.class_2351[] args2) {
			this.field_11061 = args;
			this.field_11065 = args2;
		}

		public class_2350 method_10183(Random random) {
			return this.field_11061[random.nextInt(this.field_11061.length)];
		}

		public boolean method_10182(@Nullable class_2350 arg) {
			return arg != null && arg.method_10166().method_10180() == this;
		}

		public Iterator<class_2350> iterator() {
			return Iterators.forArray(this.field_11061);
		}
	}
}
