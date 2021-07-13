package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class class_6452 {
	private static final int field_34168 = 7;

	public static class_6452.class_6460 method_37622(float f, float g, float h, float i, float j, float k) {
		return new class_6452.class_6460(f, g, h, i, j, k);
	}

	public static class_6452.MixedNoisePoint method_37623(float f, float g, float h, float i, float j, float k, float l) {
		return new class_6452.MixedNoisePoint(method_37620(f), method_37620(g), method_37620(h), method_37620(i), method_37620(j), method_37620(k), l);
	}

	public static class_6452.MixedNoisePoint method_37625(
		class_6452.class_6454 arg,
		class_6452.class_6454 arg2,
		class_6452.class_6454 arg3,
		class_6452.class_6454 arg4,
		class_6452.class_6454 arg5,
		class_6452.class_6454 arg6,
		float f
	) {
		return new class_6452.MixedNoisePoint(arg, arg2, arg3, arg4, arg5, arg6, f);
	}

	public static class_6452.class_6454 method_37620(float f) {
		return method_37621(f, f);
	}

	public static class_6452.class_6454 method_37621(float f, float g) {
		return new class_6452.class_6454(f, g);
	}

	public static class_6452.class_6454 method_37624(class_6452.class_6454 arg, class_6452.class_6454 arg2) {
		return new class_6452.class_6454(arg.method_37626(), arg2.method_37632());
	}

	/**
	 * Represents a point in a multi-dimensional cartesian plane. Mixed-noise
	 * biome generator picks the closest noise point from its selected point
	 * and choose the biome associated to that closest point. Another factor,
	 * rarity potential, favors larger differences in values instead, contrary
	 * to other point values.
	 */
	public static final class MixedNoisePoint {
		public static final Codec<class_6452.MixedNoisePoint> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						class_6452.class_6454.field_34169.fieldOf("temperature").forGetter(mixedNoisePoint -> mixedNoisePoint.temperature),
						class_6452.class_6454.field_34169.fieldOf("humidity").forGetter(mixedNoisePoint -> mixedNoisePoint.humidity),
						class_6452.class_6454.field_34169.fieldOf("continentalness").forGetter(mixedNoisePoint -> mixedNoisePoint.field_34174),
						class_6452.class_6454.field_34169.fieldOf("erosion").forGetter(mixedNoisePoint -> mixedNoisePoint.field_34175),
						class_6452.class_6454.field_34169.fieldOf("depth").forGetter(mixedNoisePoint -> mixedNoisePoint.field_34176),
						class_6452.class_6454.field_34169.fieldOf("weirdness").forGetter(mixedNoisePoint -> mixedNoisePoint.weirdness),
						Codec.floatRange(0.0F, 1.0F).fieldOf("offset").forGetter(mixedNoisePoint -> mixedNoisePoint.weight)
					)
					.apply(instance, class_6452.MixedNoisePoint::new)
		);
		private final class_6452.class_6454 temperature;
		private final class_6452.class_6454 humidity;
		private final class_6452.class_6454 field_34174;
		private final class_6452.class_6454 field_34175;
		private final class_6452.class_6454 field_34176;
		private final class_6452.class_6454 weirdness;
		/**
		 * This value awards another point with value farthest from this one; i.e.
		 * unlike other points where closer distance is better, for this value the
		 * farther the better. The result of the different values can be
		 * approximately modeled by a hyperbola weight=cosh(peak-1) as used by the
		 * mixed-noise generator.
		 */
		private final float weight;
		private final List<class_6452.class_6454> field_34177;

		MixedNoisePoint(
			class_6452.class_6454 arg,
			class_6452.class_6454 arg2,
			class_6452.class_6454 arg3,
			class_6452.class_6454 arg4,
			class_6452.class_6454 arg5,
			class_6452.class_6454 arg6,
			float f
		) {
			this.temperature = arg;
			this.humidity = arg2;
			this.field_34174 = arg3;
			this.field_34175 = arg4;
			this.field_34176 = arg5;
			this.weirdness = arg6;
			this.weight = f;
			this.field_34177 = ImmutableList.of(arg, arg2, arg3, arg4, arg5, arg6, new class_6452.class_6454(f, f));
		}

		public String toString() {
			return "temp: "
				+ this.temperature
				+ ", hum: "
				+ this.humidity
				+ ", alt: "
				+ this.field_34174
				+ ", str: "
				+ this.field_34175
				+ ", depth: "
				+ this.field_34176
				+ ", weird: "
				+ this.weirdness
				+ ", offset: "
				+ this.weight;
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				class_6452.MixedNoisePoint mixedNoisePoint = (class_6452.MixedNoisePoint)o;
				if (!this.temperature.equals(mixedNoisePoint.temperature)) {
					return false;
				} else if (!this.humidity.equals(mixedNoisePoint.humidity)) {
					return false;
				} else if (!this.field_34174.equals(mixedNoisePoint.field_34174)) {
					return false;
				} else if (!this.field_34175.equals(mixedNoisePoint.field_34175)) {
					return false;
				} else if (!this.field_34176.equals(mixedNoisePoint.field_34176)) {
					return false;
				} else if (this.weirdness.equals(mixedNoisePoint.weirdness)) {
					return true;
				} else {
					return Float.compare(mixedNoisePoint.weight, this.weight) != 0 ? false : false;
				}
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.temperature, this.humidity, this.field_34174, this.field_34175, this.field_34176, this.weirdness, this.weight});
		}

		private float method_37643(class_6452.MixedNoisePoint mixedNoisePoint) {
			return MathHelper.square(this.temperature.method_37628(mixedNoisePoint.temperature))
				+ MathHelper.square(this.humidity.method_37628(mixedNoisePoint.humidity))
				+ MathHelper.square(this.field_34174.method_37628(mixedNoisePoint.field_34174))
				+ MathHelper.square(this.field_34175.method_37628(mixedNoisePoint.field_34175))
				+ MathHelper.square(this.field_34176.method_37628(mixedNoisePoint.field_34176))
				+ MathHelper.square(this.weirdness.method_37628(mixedNoisePoint.weirdness))
				+ MathHelper.square(this.weight - mixedNoisePoint.weight);
		}

		float method_37644(class_6452.class_6460 arg) {
			return MathHelper.square(this.temperature.method_37627(arg.field_34183))
				+ MathHelper.square(this.humidity.method_37627(arg.field_34184))
				+ MathHelper.square(this.field_34174.method_37627(arg.field_34185))
				+ MathHelper.square(this.field_34175.method_37627(arg.field_34186))
				+ MathHelper.square(this.field_34176.method_37627(arg.field_34187))
				+ MathHelper.square(this.weirdness.method_37627(arg.field_34188))
				+ MathHelper.square(this.weight);
		}

		public class_6452.class_6454 method_37642() {
			return this.temperature;
		}

		public class_6452.class_6454 method_37646() {
			return this.humidity;
		}

		public class_6452.class_6454 method_37648() {
			return this.field_34174;
		}

		public class_6452.class_6454 method_37650() {
			return this.field_34175;
		}

		public class_6452.class_6454 method_37652() {
			return this.field_34176;
		}

		public class_6452.class_6454 method_37654() {
			return this.weirdness;
		}

		public float method_37656() {
			return this.weight;
		}

		protected List<class_6452.class_6454> method_37658() {
			return this.field_34177;
		}
	}

	interface class_6453<T> {
		float distance(class_6452.class_6456.class_6458<T> arg, float[] fs);
	}

	public static final class class_6454 {
		public static final Codec<class_6452.class_6454> field_34169 = Codec.either(Codec.floatRange(-2.0F, 2.0F), Codec.list(Codec.floatRange(-2.0F, 2.0F)))
			.comapFlatMap(
				either -> either.map(
						float_ -> DataResult.success(new class_6452.class_6454(float_, float_)),
						list -> Util.toArray(list, 2).map(listx -> new class_6452.class_6454((Float)listx.get(0), (Float)listx.get(1)))
					),
				arg -> arg.method_37626() == arg.method_37632() ? Either.left(arg.method_37626()) : Either.right(ImmutableList.of(arg.method_37626(), arg.method_37632()))
			);
		private final float field_34170;
		private final float field_34171;

		class_6454(float f, float g) {
			this.field_34170 = f;
			this.field_34171 = g;
		}

		public float method_37626() {
			return this.field_34170;
		}

		public float method_37632() {
			return this.field_34171;
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				class_6452.class_6454 lv = (class_6452.class_6454)object;
				return Float.compare(lv.field_34170, this.field_34170) == 0 && Float.compare(lv.field_34171, this.field_34171) == 0;
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.field_34170, this.field_34171});
		}

		public String toString() {
			return this.field_34170 == this.field_34171 ? String.format("%.2f", this.field_34170) : String.format("[%.2f-%.2f]", this.field_34170, this.field_34171);
		}

		public float method_37627(float f) {
			float g = f - this.field_34171;
			float h = this.field_34170 - f;
			return g > 0.0F ? g : Math.max(h, 0.0F);
		}

		public float method_37628(class_6452.class_6454 arg) {
			float f = arg.method_37626() - this.field_34171;
			float g = this.field_34170 - arg.method_37632();
			return f > 0.0F ? f : Math.max(g, 0.0F);
		}

		public class_6452.class_6454 method_37633(class_6452.class_6454 arg) {
			return new class_6452.class_6454(Math.min(this.field_34170, arg.method_37626()), Math.max(this.field_34171, arg.method_37632()));
		}
	}

	public static class class_6455<T> {
		private final List<Pair<class_6452.MixedNoisePoint, Supplier<T>>> field_34172;
		private final class_6452.class_6456<T> field_34173;

		public class_6455(List<Pair<class_6452.MixedNoisePoint, Supplier<T>>> list) {
			this.field_34172 = list;
			this.field_34173 = class_6452.class_6456.method_37667(list);
		}

		public List<Pair<class_6452.MixedNoisePoint, Supplier<T>>> method_37636() {
			return this.field_34172;
		}

		public T method_37640(class_6452.class_6460 arg, Supplier<T> supplier) {
			return this.method_37638(arg);
		}

		public T method_37641(class_6452.class_6460 arg, Supplier<T> supplier) {
			float f = Float.MAX_VALUE;
			Supplier<T> supplier2 = supplier;

			for (Pair<class_6452.MixedNoisePoint, Supplier<T>> pair : this.method_37636()) {
				float g = pair.getFirst().method_37644(arg);
				if (g < f) {
					f = g;
					supplier2 = pair.getSecond();
				}
			}

			return (T)supplier2.get();
		}

		public T method_37638(class_6452.class_6460 arg) {
			return this.method_37639(arg, (argx, fs) -> argx.method_37675(fs));
		}

		T method_37639(class_6452.class_6460 arg, class_6452.class_6453<T> arg2) {
			return this.field_34173.method_37665(arg, arg2);
		}
	}

	static final class class_6456<T> {
		private static final int field_34178 = 10;
		private final class_6452.class_6456.class_6458<T> field_34179;

		private class_6456(class_6452.class_6456.class_6458<T> arg) {
			this.field_34179 = arg;
		}

		public static <T> class_6452.class_6456<T> method_37667(List<Pair<class_6452.MixedNoisePoint, Supplier<T>>> list) {
			if (list.isEmpty()) {
				throw new IllegalArgumentException("Need at least one biome to build the search tree.");
			} else {
				int i = ((class_6452.MixedNoisePoint)((Pair)list.get(0)).getFirst()).method_37658().size();
				if (i != 7) {
					throw new IllegalStateException("Expecting parameter space to be 7, got " + i);
				} else {
					List<class_6452.class_6456.class_6457<T>> list2 = new ArrayList(
						(Collection)list.stream()
							.map(pair -> new class_6452.class_6456.class_6457((class_6452.MixedNoisePoint)pair.getFirst(), (Supplier<T>)pair.getSecond()))
							.collect(Collectors.toList())
					);
					return new class_6452.class_6456<>(method_37663(i, list2));
				}
			}
		}

		private static <T> class_6452.class_6456.class_6458<T> method_37663(int i, List<? extends class_6452.class_6456.class_6458<T>> list) {
			if (list.isEmpty()) {
				throw new IllegalStateException("Need at least one child to build a node");
			} else if (list.size() == 1) {
				return (class_6452.class_6456.class_6458<T>)list.get(0);
			} else if (list.size() <= 10) {
				list.sort(Comparator.comparingDouble(arg -> {
					float fx = 0.0F;

					for (int jx = 0; jx < i; jx++) {
						class_6452.class_6454 lvx = arg.field_34181[jx];
						fx += Math.abs((lvx.method_37626() + lvx.method_37632()) / 2.0F);
					}

					return (double)fx;
				}));
				return new class_6452.class_6456.class_6459<>(list);
			} else {
				float f = Float.POSITIVE_INFINITY;
				int j = -1;

				for (int k = 0; k < i; k++) {
					method_37668(list, k, false);
					List<class_6452.class_6456.class_6459<T>> list2 = method_37672(list);
					float g = 0.0F;

					for (class_6452.class_6456.class_6459<T> lv : list2) {
						g += method_37670(lv.field_34181);
					}

					if (f > g) {
						f = g;
						j = k;
					}
				}

				method_37668(list, j, false);
				List<class_6452.class_6456.class_6459<T>> list2 = method_37672(list);
				method_37668(list2, j, true);
				return new class_6452.class_6456.class_6459<>(
					(List<? extends class_6452.class_6456.class_6458<T>>)list2.stream()
						.map(arg -> method_37663(i, Arrays.asList(arg.field_34182)))
						.collect(Collectors.toList())
				);
			}
		}

		private static <T> void method_37668(List<? extends class_6452.class_6456.class_6458<T>> list, int i, boolean bl) {
			list.sort(Comparator.comparingDouble(arg -> {
				class_6452.class_6454 lv = arg.field_34181[i];
				float f = (lv.method_37626() + lv.method_37632()) / 2.0F;
				return bl ? (double)Math.abs(f) : (double)f;
			}));
		}

		private static <T> List<class_6452.class_6456.class_6459<T>> method_37672(List<? extends class_6452.class_6456.class_6458<T>> list) {
			List<class_6452.class_6456.class_6459<T>> list2 = Lists.<class_6452.class_6456.class_6459<T>>newArrayList();
			List<class_6452.class_6456.class_6458<T>> list3 = Lists.<class_6452.class_6456.class_6458<T>>newArrayList();
			int i = (int)Math.pow(10.0, Math.floor(Math.log((double)list.size() - 0.01) / Math.log(10.0)));

			for (class_6452.class_6456.class_6458<T> lv : list) {
				list3.add(lv);
				if (list3.size() >= i) {
					list2.add(new class_6452.class_6456.class_6459(list3));
					list3 = Lists.<class_6452.class_6456.class_6458<T>>newArrayList();
				}
			}

			if (!list3.isEmpty()) {
				list2.add(new class_6452.class_6456.class_6459(list3));
			}

			return list2;
		}

		private static float method_37670(class_6452.class_6454[] args) {
			float f = 0.0F;

			for (class_6452.class_6454 lv : args) {
				f += Math.abs(lv.method_37632() - lv.method_37626());
			}

			return f;
		}

		static List<class_6452.class_6454> method_37660(int i) {
			return (List<class_6452.class_6454>)IntStream.range(0, i)
				.mapToObj(ix -> new class_6452.class_6454(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY))
				.collect(Collectors.toList());
		}

		static <T> List<class_6452.class_6454> method_37673(List<? extends class_6452.class_6456.class_6458<T>> list) {
			if (list.isEmpty()) {
				throw new IllegalArgumentException("SubTree needs at least one child");
			} else {
				int i = 7;
				List<class_6452.class_6454> list2 = method_37660(7);

				for (class_6452.class_6456.class_6458<T> lv : list) {
					List<class_6452.class_6454> list3 = list2;
					list2 = (List<class_6452.class_6454>)IntStream.range(0, 7)
						.mapToObj(ix -> ((class_6452.class_6454)list3.get(ix)).method_37633(lv.field_34181[ix]))
						.collect(Collectors.toList());
				}

				return list2;
			}
		}

		public T method_37665(class_6452.class_6460 arg, class_6452.class_6453<T> arg2) {
			float[] fs = arg.method_37682();
			if (fs.length != 7) {
				throw new IllegalArgumentException(String.format("Target size (%s) does not match expected size (%s)", fs.length, this.field_34179.field_34181.length));
			} else {
				class_6452.class_6456.class_6457<T> lv = this.field_34179.method_37674(fs, arg2);
				return (T)lv.field_34180.get();
			}
		}

		static final class class_6457<T> extends class_6452.class_6456.class_6458<T> {
			final Supplier<T> field_34180;

			class_6457(class_6452.MixedNoisePoint mixedNoisePoint, Supplier<T> supplier) {
				super(mixedNoisePoint.method_37658());
				this.field_34180 = supplier;
			}

			@Override
			protected class_6452.class_6456.class_6457<T> method_37674(float[] fs, class_6452.class_6453<T> arg) {
				return this;
			}
		}

		abstract static class class_6458<T> {
			protected final class_6452.class_6454[] field_34181;

			protected class_6458(List<class_6452.class_6454> list) {
				this.field_34181 = (class_6452.class_6454[])list.toArray(new class_6452.class_6454[0]);
			}

			protected abstract class_6452.class_6456.class_6457<T> method_37674(float[] fs, class_6452.class_6453<T> arg);

			protected float method_37675(float[] fs) {
				float f = 0.0F;

				for (int i = 0; i < 7; i++) {
					f += MathHelper.square(this.field_34181[i].method_37627(fs[i]));
				}

				return f;
			}

			public String toString() {
				return Arrays.toString(this.field_34181);
			}
		}

		static final class class_6459<T> extends class_6452.class_6456.class_6458<T> {
			final class_6452.class_6456.class_6458<T>[] field_34182;

			protected class_6459(List<? extends class_6452.class_6456.class_6458<T>> list) {
				this(class_6452.class_6456.method_37673(list), list);
			}

			protected class_6459(List<class_6452.class_6454> list, List<? extends class_6452.class_6456.class_6458<T>> list2) {
				super(list);
				this.field_34182 = (class_6452.class_6456.class_6458<T>[])list2.toArray(new class_6452.class_6456.class_6458[0]);
			}

			@Override
			protected class_6452.class_6456.class_6457<T> method_37674(float[] fs, class_6452.class_6453<T> arg) {
				float f = Float.POSITIVE_INFINITY;
				class_6452.class_6456.class_6457<T> lv = null;

				for (class_6452.class_6456.class_6458<T> lv2 : this.field_34182) {
					float g = arg.distance(lv2, fs);
					if (f > g) {
						class_6452.class_6456.class_6457<T> lv3 = lv2.method_37674(fs, arg);
						float h = lv2 == lv3 ? g : arg.distance(lv3, fs);
						if (f > h) {
							f = h;
							lv = lv3;
						}
					}
				}

				return lv;
			}
		}
	}

	public static final class class_6460 {
		final float field_34183;
		final float field_34184;
		final float field_34185;
		final float field_34186;
		final float field_34187;
		final float field_34188;

		class_6460(float f, float g, float h, float i, float j, float k) {
			this.field_34183 = f;
			this.field_34184 = g;
			this.field_34185 = h;
			this.field_34186 = i;
			this.field_34187 = j;
			this.field_34188 = k;
		}

		public float method_37676() {
			return this.field_34183;
		}

		public float method_37677() {
			return this.field_34184;
		}

		public float method_37678() {
			return this.field_34185;
		}

		public float method_37679() {
			return this.field_34186;
		}

		public float method_37680() {
			return this.field_34187;
		}

		public float method_37681() {
			return this.field_34188;
		}

		public float[] method_37682() {
			return new float[]{this.field_34183, this.field_34184, this.field_34185, this.field_34186, this.field_34187, this.field_34188, 0.0F};
		}
	}
}
