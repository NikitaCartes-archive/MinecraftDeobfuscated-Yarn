package net.minecraft.util.math;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.function.ToFloatFunction;
import org.apache.commons.lang3.mutable.MutableObject;

public interface Spline<C> extends ToFloatFunction<C> {
	@Debug
	String getDebugString();

	static <C> Codec<Spline<C>> method_39232(Codec<ToFloatFunction<C>> codec) {
		MutableObject<Codec<Spline<C>>> mutableObject = new MutableObject<>();

		final class class_6737 extends Record {
			private final float location;
			private final Spline<C> value;
			private final float derivative;

			class_6737(float f, Spline<C> spline, float g) {
				this.location = f;
				this.value = spline;
				this.derivative = g;
			}

			public final String toString() {
				return ObjectMethods.bootstrap<"toString",class_6737,"location;value;derivative",class_6737::location,class_6737::value,class_6737::derivative>(this);
			}

			public final int hashCode() {
				return ObjectMethods.bootstrap<"hashCode",class_6737,"location;value;derivative",class_6737::location,class_6737::value,class_6737::derivative>(this);
			}

			public final boolean equals(Object object) {
				return ObjectMethods.bootstrap<"equals",class_6737,"location;value;derivative",class_6737::location,class_6737::value,class_6737::derivative>(this, object);
			}

			public float location() {
				return this.location;
			}

			public Spline<C> value() {
				return this.value;
			}

			public float derivative() {
				return this.derivative;
			}
		}

		Codec<class_6737<C>> codec2 = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.FLOAT.fieldOf("location").forGetter(class_6737::location),
						Codecs.createLazy(mutableObject::getValue).fieldOf("value").forGetter(class_6737::value),
						Codec.FLOAT.fieldOf("derivative").forGetter(class_6737::derivative)
					)
					.apply(instance, (f, spline, g) -> new class_6737(f, spline, g))
		);
		Codec<Spline.class_6738<C>> codec3 = RecordCodecBuilder.create(
			instance -> instance.group(
						codec.fieldOf("coordinate").forGetter(Spline.class_6738::coordinate),
						codec2.listOf()
							.fieldOf("points")
							.forGetter(
								arg -> IntStream.range(0, arg.locations.length)
										.mapToObj(i -> new class_6737(arg.locations()[i], (Spline<C>)arg.values().get(i), arg.derivatives()[i]))
										.toList()
							)
					)
					.apply(instance, (toFloatFunction, list) -> {
						float[] fs = new float[list.size()];
						ImmutableList.Builder<Spline<C>> builder = ImmutableList.builder();
						float[] gs = new float[list.size()];
		
						for(int i = 0; i < list.size(); ++i) {
							class_6737<C> lv = (class_6737)list.get(i);
							fs[i] = lv.location();
							builder.add(lv.value());
							gs[i] = lv.derivative();
						}
		
						return new Spline.class_6738(toFloatFunction, fs, builder.build(), gs);
					})
		);
		mutableObject.setValue(
			Codec.either(Codec.FLOAT, codec3)
				.xmap(
					either -> either.map(Spline.FixedFloatFunction::new, arg -> arg),
					spline -> spline instanceof Spline.FixedFloatFunction fixedFloatFunction
							? Either.left(fixedFloatFunction.value())
							: Either.right((Spline.class_6738)spline)
				)
		);
		return mutableObject.getValue();
	}

	static <C> Spline<C> method_39427(float f) {
		return new Spline.FixedFloatFunction<>(f);
	}

	static <C> Spline.Builder<C> builder(ToFloatFunction<C> locationFunction) {
		return new Spline.Builder<>(locationFunction);
	}

	public static final class Builder<C> {
		private final ToFloatFunction<C> locationFunction;
		private final FloatList locations = new FloatArrayList();
		private final List<Spline<C>> values = Lists.<Spline<C>>newArrayList();
		private final FloatList derivatives = new FloatArrayList();

		protected Builder(ToFloatFunction<C> locationFunction) {
			this.locationFunction = locationFunction;
		}

		public Spline.Builder<C> add(float location, float value, float derivative) {
			return this.add(location, new Spline.FixedFloatFunction<>(value), derivative);
		}

		public Spline.Builder<C> add(float location, Spline<C> value, float derivative) {
			if (!this.locations.isEmpty() && location <= this.locations.getFloat(this.locations.size() - 1)) {
				throw new IllegalArgumentException("Please register points in ascending order");
			} else {
				this.locations.add(location);
				this.values.add(value);
				this.derivatives.add(derivative);
				return this;
			}
		}

		public Spline<C> build() {
			if (this.locations.isEmpty()) {
				throw new IllegalStateException("No elements added");
			} else {
				return new Spline.class_6738(this.locationFunction, this.locations.toFloatArray(), ImmutableList.copyOf(this.values), this.derivatives.toFloatArray());
			}
		}
	}

	@Debug
	public static final class FixedFloatFunction<C> extends Record implements Spline<C> {
		private final float value;

		public FixedFloatFunction(float value) {
			this.value = value;
		}

		@Override
		public float apply(C object) {
			return this.value;
		}

		@Override
		public String getDebugString() {
			return String.format("k=%.3f", this.value);
		}

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",Spline.FixedFloatFunction,"value",Spline.FixedFloatFunction::value>(this);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",Spline.FixedFloatFunction,"value",Spline.FixedFloatFunction::value>(this);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",Spline.FixedFloatFunction,"value",Spline.FixedFloatFunction::value>(this, object);
		}

		public float value() {
			return this.value;
		}
	}

	@Debug
	public static final class class_6738 extends Record implements Spline {
		private final ToFloatFunction<C> coordinate;
		final float[] locations;
		private final List<Spline<C>> values;
		private final float[] derivatives;

		public class_6738(ToFloatFunction<C> toFloatFunction, float[] fs, List<Spline<C>> list, float[] gs) {
			if (fs.length == list.size() && fs.length == gs.length) {
				this.coordinate = toFloatFunction;
				this.locations = fs;
				this.values = list;
				this.derivatives = gs;
			} else {
				throw new IllegalArgumentException("All lengths must be equal, got: " + fs.length + " " + list.size() + " " + gs.length);
			}
		}

		@Override
		public float apply(C object) {
			float f = this.coordinate.apply(object);
			int i = MathHelper.binarySearch(0, this.locations.length, ix -> f < this.locations[ix]) - 1;
			int j = this.locations.length - 1;
			if (i < 0) {
				return ((Spline)this.values.get(0)).apply(object) + this.derivatives[0] * (f - this.locations[0]);
			} else if (i == j) {
				return ((Spline)this.values.get(j)).apply(object) + this.derivatives[j] * (f - this.locations[j]);
			} else {
				float g = this.locations[i];
				float h = this.locations[i + 1];
				float k = (f - g) / (h - g);
				ToFloatFunction<C> toFloatFunction = (ToFloatFunction)this.values.get(i);
				ToFloatFunction<C> toFloatFunction2 = (ToFloatFunction)this.values.get(i + 1);
				float l = this.derivatives[i];
				float m = this.derivatives[i + 1];
				float n = toFloatFunction.apply(object);
				float o = toFloatFunction2.apply(object);
				float p = l * (h - g) - (o - n);
				float q = -m * (h - g) + (o - n);
				return MathHelper.lerp(k, n, o) + k * (1.0F - k) * MathHelper.lerp(k, p, q);
			}
		}

		@VisibleForTesting
		@Override
		public String getDebugString() {
			return "Spline{coordinate="
				+ this.coordinate
				+ ", locations="
				+ this.method_39238(this.locations)
				+ ", derivatives="
				+ this.method_39238(this.derivatives)
				+ ", values="
				+ (String)this.values.stream().map(Spline::getDebugString).collect(Collectors.joining(", ", "[", "]"))
				+ "}";
		}

		private String method_39238(float[] fs) {
			return "["
				+ (String)IntStream.range(0, fs.length)
					.mapToDouble(i -> (double)fs[i])
					.mapToObj(d -> String.format(Locale.ROOT, "%.3f", d))
					.collect(Collectors.joining(", "))
				+ "]";
		}

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",Spline.class_6738,"coordinate;locations;values;derivatives",Spline.class_6738::coordinate,Spline.class_6738::locations,Spline.class_6738::values,Spline.class_6738::derivatives>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",Spline.class_6738,"coordinate;locations;values;derivatives",Spline.class_6738::coordinate,Spline.class_6738::locations,Spline.class_6738::values,Spline.class_6738::derivatives>(
				this
			);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",Spline.class_6738,"coordinate;locations;values;derivatives",Spline.class_6738::coordinate,Spline.class_6738::locations,Spline.class_6738::values,Spline.class_6738::derivatives>(
				this, object
			);
		}

		public ToFloatFunction<C> coordinate() {
			return this.coordinate;
		}

		public float[] locations() {
			return this.locations;
		}

		public List<Spline<C>> values() {
			return this.values;
		}

		public float[] derivatives() {
			return this.derivatives;
		}
	}
}
