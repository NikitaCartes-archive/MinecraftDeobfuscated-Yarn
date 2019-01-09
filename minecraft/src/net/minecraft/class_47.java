package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public class class_47 {
	private final Random field_923;
	private final float field_926;
	private final class_3218 field_928;
	private final class_60 field_924;
	private final Set<class_52> field_927 = Sets.<class_52>newLinkedHashSet();
	private final Map<class_169<?>, Object> field_925;
	private final Map<class_2960, class_47.class_49> field_929;

	private class_47(Random random, float f, class_3218 arg, class_60 arg2, Map<class_169<?>, Object> map, Map<class_2960, class_47.class_49> map2) {
		this.field_923 = random;
		this.field_926 = f;
		this.field_928 = arg;
		this.field_924 = arg2;
		this.field_925 = ImmutableMap.copyOf(map);
		this.field_929 = ImmutableMap.copyOf(map2);
	}

	public boolean method_300(class_169<?> arg) {
		return this.field_925.containsKey(arg);
	}

	public void method_297(class_2960 arg, Consumer<class_1799> consumer) {
		class_47.class_49 lv = (class_47.class_49)this.field_929.get(arg);
		if (lv != null) {
			lv.add(this, consumer);
		}
	}

	@Nullable
	public <T> T method_296(class_169<T> arg) {
		return (T)this.field_925.get(arg);
	}

	public boolean method_298(class_52 arg) {
		return this.field_927.add(arg);
	}

	public void method_295(class_52 arg) {
		this.field_927.remove(arg);
	}

	public class_60 method_301() {
		return this.field_924;
	}

	public Random method_294() {
		return this.field_923;
	}

	public float method_302() {
		return this.field_926;
	}

	public class_3218 method_299() {
		return this.field_928;
	}

	public static class class_48 {
		private final class_3218 field_930;
		private final Map<class_169<?>, Object> field_932 = Maps.<class_169<?>, Object>newIdentityHashMap();
		private final Map<class_2960, class_47.class_49> field_933 = Maps.<class_2960, class_47.class_49>newHashMap();
		private Random field_934;
		private float field_931;

		public class_48(class_3218 arg) {
			this.field_930 = arg;
		}

		public class_47.class_48 method_311(Random random) {
			this.field_934 = random;
			return this;
		}

		public class_47.class_48 method_304(long l) {
			if (l != 0L) {
				this.field_934 = new Random(l);
			}

			return this;
		}

		public class_47.class_48 method_310(long l, Random random) {
			if (l == 0L) {
				this.field_934 = random;
			} else {
				this.field_934 = new Random(l);
			}

			return this;
		}

		public class_47.class_48 method_303(float f) {
			this.field_931 = f;
			return this;
		}

		public <T> class_47.class_48 method_312(class_169<T> arg, T object) {
			this.field_932.put(arg, object);
			return this;
		}

		public <T> class_47.class_48 method_306(class_169<T> arg, @Nullable T object) {
			if (object == null) {
				this.field_932.remove(arg);
			} else {
				this.field_932.put(arg, object);
			}

			return this;
		}

		public class_47.class_48 method_307(class_2960 arg, class_47.class_49 arg2) {
			class_47.class_49 lv = (class_47.class_49)this.field_933.put(arg, arg2);
			if (lv != null) {
				throw new IllegalStateException("Duplicated dynamic drop '" + this.field_933 + "'");
			} else {
				return this;
			}
		}

		public class_3218 method_313() {
			return this.field_930;
		}

		public <T> T method_308(class_169<T> arg) {
			T object = (T)this.field_932.get(arg);
			if (object == null) {
				throw new IllegalArgumentException("No parameter " + arg);
			} else {
				return object;
			}
		}

		@Nullable
		public <T> T method_305(class_169<T> arg) {
			return (T)this.field_932.get(arg);
		}

		public class_47 method_309(class_176 arg) {
			Set<class_169<?>> set = Sets.<class_169<?>>difference(this.field_932.keySet(), arg.method_777());
			if (!set.isEmpty()) {
				throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + set);
			} else {
				Set<class_169<?>> set2 = Sets.<class_169<?>>difference(arg.method_778(), this.field_932.keySet());
				if (!set2.isEmpty()) {
					throw new IllegalArgumentException("Missing required parameters: " + set2);
				} else {
					Random random = this.field_934;
					if (random == null) {
						random = new Random();
					}

					return new class_47(random, this.field_931, this.field_930, this.field_930.method_8503().method_3857(), this.field_932, this.field_933);
				}
			}
		}
	}

	@FunctionalInterface
	public interface class_49 {
		void add(class_47 arg, Consumer<class_1799> consumer);
	}

	public static enum class_50 {
		field_935("this", class_181.field_1226),
		field_936("killer", class_181.field_1230),
		field_939("direct_killer", class_181.field_1227),
		field_937("killer_player", class_181.field_1233);

		private final String field_941;
		private final class_169<? extends class_1297> field_938;

		private class_50(String string2, class_169<? extends class_1297> arg) {
			this.field_941 = string2;
			this.field_938 = arg;
		}

		public class_169<? extends class_1297> method_315() {
			return this.field_938;
		}

		public static class_47.class_50 method_314(String string) {
			for (class_47.class_50 lv : values()) {
				if (lv.field_941.equals(string)) {
					return lv;
				}
			}

			throw new IllegalArgumentException("Invalid entity target " + string);
		}

		public static class class_51 extends TypeAdapter<class_47.class_50> {
			public void method_318(JsonWriter jsonWriter, class_47.class_50 arg) throws IOException {
				jsonWriter.value(arg.field_941);
			}

			public class_47.class_50 method_317(JsonReader jsonReader) throws IOException {
				return class_47.class_50.method_314(jsonReader.nextString());
			}
		}
	}
}
