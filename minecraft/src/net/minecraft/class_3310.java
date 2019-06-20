package net.minecraft;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class class_3310 extends class_3031<class_3666> {
	private static final LoadingCache<Long, List<class_3310.class_3181>> field_14309 = CacheBuilder.newBuilder()
		.expireAfterWrite(5L, TimeUnit.MINUTES)
		.build(new class_3310.class_3311());

	public class_3310(Function<Dynamic<?>, ? extends class_3666> function) {
		super(function);
	}

	public static List<class_3310.class_3181> method_14506(class_1936 arg) {
		Random random = new Random(arg.method_8412());
		long l = random.nextLong() & 65535L;
		return field_14309.getUnchecked(l);
	}

	public boolean method_15887(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3666 arg4) {
		List<class_3310.class_3181> list = arg4.method_15885();
		if (list.isEmpty()) {
			list = method_14506(arg);
		}

		for (class_3310.class_3181 lv : list) {
			if (lv.method_13962(arg3)) {
				this.method_15888(arg, random, arg4, lv);
			}
		}

		return true;
	}

	private void method_15888(class_1936 arg, Random random, class_3666 arg2, class_3310.class_3181 arg3) {
		int i = arg3.method_13963();

		for (class_2338 lv : class_2338.method_10097(
			new class_2338(arg3.method_13966() - i, 0, arg3.method_13967() - i),
			new class_2338(arg3.method_13966() + i, arg3.method_13964() + 10, arg3.method_13967() + i)
		)) {
			if (lv.method_19771(new class_2338(arg3.method_13966(), lv.method_10264(), arg3.method_13967()), (double)i) && lv.method_10264() < arg3.method_13964()) {
				this.method_13153(arg, lv, class_2246.field_10540.method_9564());
			} else if (lv.method_10264() > 65) {
				this.method_13153(arg, lv, class_2246.field_10124.method_9564());
			}
		}

		if (arg3.method_13965()) {
			int j = -2;
			int k = 2;
			int l = 3;
			class_2338.class_2339 lv2 = new class_2338.class_2339();

			for (int m = -2; m <= 2; m++) {
				for (int n = -2; n <= 2; n++) {
					for (int o = 0; o <= 3; o++) {
						boolean bl = class_3532.method_15382(m) == 2;
						boolean bl2 = class_3532.method_15382(n) == 2;
						boolean bl3 = o == 3;
						if (bl || bl2 || bl3) {
							boolean bl4 = m == -2 || m == 2 || bl3;
							boolean bl5 = n == -2 || n == 2 || bl3;
							class_2680 lv3 = class_2246.field_10576
								.method_9564()
								.method_11657(class_2389.field_10905, Boolean.valueOf(bl4 && n != -2))
								.method_11657(class_2389.field_10904, Boolean.valueOf(bl4 && n != 2))
								.method_11657(class_2389.field_10903, Boolean.valueOf(bl5 && m != -2))
								.method_11657(class_2389.field_10907, Boolean.valueOf(bl5 && m != 2));
							this.method_13153(arg, lv2.method_10103(arg3.method_13966() + m, arg3.method_13964() + o, arg3.method_13967() + n), lv3);
						}
					}
				}
			}
		}

		class_1511 lv4 = class_1299.field_6110.method_5883(arg.method_8410());
		lv4.method_6837(arg2.method_15884());
		lv4.method_5684(arg2.method_15883());
		lv4.method_5808(
			(double)((float)arg3.method_13966() + 0.5F),
			(double)(arg3.method_13964() + 1),
			(double)((float)arg3.method_13967() + 0.5F),
			random.nextFloat() * 360.0F,
			0.0F
		);
		arg.method_8649(lv4);
		this.method_13153(arg, new class_2338(arg3.method_13966(), arg3.method_13964(), arg3.method_13967()), class_2246.field_9987.method_9564());
	}

	public static class class_3181 {
		private final int field_13836;
		private final int field_13834;
		private final int field_13833;
		private final int field_13831;
		private final boolean field_13832;
		private final class_238 field_13835;

		public class_3181(int i, int j, int k, int l, boolean bl) {
			this.field_13836 = i;
			this.field_13834 = j;
			this.field_13833 = k;
			this.field_13831 = l;
			this.field_13832 = bl;
			this.field_13835 = new class_238((double)(i - k), 0.0, (double)(j - k), (double)(i + k), 256.0, (double)(j + k));
		}

		public boolean method_13962(class_2338 arg) {
			return arg.method_10263() >> 4 == this.field_13836 >> 4 && arg.method_10260() >> 4 == this.field_13834 >> 4;
		}

		public int method_13966() {
			return this.field_13836;
		}

		public int method_13967() {
			return this.field_13834;
		}

		public int method_13963() {
			return this.field_13833;
		}

		public int method_13964() {
			return this.field_13831;
		}

		public boolean method_13965() {
			return this.field_13832;
		}

		public class_238 method_13968() {
			return this.field_13835;
		}

		<T> Dynamic<T> method_16597(DynamicOps<T> dynamicOps) {
			Builder<T, T> builder = ImmutableMap.builder();
			builder.put(dynamicOps.createString("centerX"), dynamicOps.createInt(this.field_13836));
			builder.put(dynamicOps.createString("centerZ"), dynamicOps.createInt(this.field_13834));
			builder.put(dynamicOps.createString("radius"), dynamicOps.createInt(this.field_13833));
			builder.put(dynamicOps.createString("height"), dynamicOps.createInt(this.field_13831));
			builder.put(dynamicOps.createString("guarded"), dynamicOps.createBoolean(this.field_13832));
			return new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build()));
		}

		public static <T> class_3310.class_3181 method_15889(Dynamic<T> dynamic) {
			return new class_3310.class_3181(
				dynamic.get("centerX").asInt(0),
				dynamic.get("centerZ").asInt(0),
				dynamic.get("radius").asInt(0),
				dynamic.get("height").asInt(0),
				dynamic.get("guarded").asBoolean(false)
			);
		}
	}

	static class class_3311 extends CacheLoader<Long, List<class_3310.class_3181>> {
		private class_3311() {
		}

		public List<class_3310.class_3181> method_14507(Long long_) {
			List<Integer> list = (List<Integer>)IntStream.range(0, 10).boxed().collect(Collectors.toList());
			Collections.shuffle(list, new Random(long_));
			List<class_3310.class_3181> list2 = Lists.<class_3310.class_3181>newArrayList();

			for (int i = 0; i < 10; i++) {
				int j = class_3532.method_15357(42.0 * Math.cos(2.0 * (-Math.PI + (Math.PI / 10) * (double)i)));
				int k = class_3532.method_15357(42.0 * Math.sin(2.0 * (-Math.PI + (Math.PI / 10) * (double)i)));
				int l = (Integer)list.get(i);
				int m = 2 + l / 3;
				int n = 76 + l * 3;
				boolean bl = l == 1 || l == 2;
				list2.add(new class_3310.class_3181(j, k, m, n, bl));
			}

			return list2;
		}
	}
}
