package net.minecraft.util.math;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
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

	float method_40435();

	float method_40436();

	static <C> Codec<Spline<C>> createCodec(Codec<ToFloatFunction<C>> locationFunctionCodec) {
		MutableObject<Codec<Spline<C>>> mutableObject = new MutableObject<>();

		record Serialized<C>(float location, Spline<C> value, float derivative) {
		}

		Codec<Serialized<C>> codec = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.FLOAT.fieldOf("location").forGetter(Serialized::location),
						Codecs.createLazy(mutableObject::getValue).fieldOf("value").forGetter(Serialized::value),
						Codec.FLOAT.fieldOf("derivative").forGetter(Serialized::derivative)
					)
					.apply(instance, (location, value, derivative) -> new Serialized(location, value, derivative))
		);
		Codec<Spline.Implementation<C>> codec2 = RecordCodecBuilder.create(
			instance -> instance.group(
						locationFunctionCodec.fieldOf("coordinate").forGetter(Spline.Implementation::locationFunction),
						Codecs.nonEmptyList(codec.listOf())
							.fieldOf("points")
							.forGetter(
								spline -> IntStream.range(0, spline.locations.length)
										.mapToObj(index -> new Serialized(spline.locations()[index], (Spline<C>)spline.values().get(index), spline.derivatives()[index]))
										.toList()
							)
					)
					.apply(instance, (locationFunction, splines) -> {
						float[] fs = new float[splines.size()];
						ImmutableList.Builder<Spline<C>> builder = ImmutableList.builder();
						float[] gs = new float[splines.size()];

						for (int i = 0; i < splines.size(); i++) {
							Serialized<C> serialized = (Serialized<C>)splines.get(i);
							fs[i] = serialized.location();
							builder.add(serialized.value());
							gs[i] = serialized.derivative();
						}

						return new Spline.Implementation(locationFunction, fs, builder.build(), gs);
					})
		);
		mutableObject.setValue(
			Codec.either(Codec.FLOAT, codec2)
				.xmap(
					either -> either.map(Spline.FixedFloatFunction::new, spline -> spline),
					spline -> spline instanceof Spline.FixedFloatFunction<C> fixedFloatFunction
							? Either.left(fixedFloatFunction.value())
							: Either.right((Spline.Implementation)spline)
				)
		);
		return mutableObject.getValue();
	}

	static <C> Spline<C> fixedFloatFunction(float value) {
		return new Spline.FixedFloatFunction<>(value);
	}

	static <C> Spline.Builder<C> builder(ToFloatFunction<C> locationFunction) {
		return new Spline.Builder<>(locationFunction);
	}

	static <C> Spline.Builder<C> builder(ToFloatFunction<C> locationFunction, ToFloatFunction<Float> amplifier) {
		return new Spline.Builder<>(locationFunction, amplifier);
	}

	public static final class Builder<C> {
		private final ToFloatFunction<C> locationFunction;
		private final ToFloatFunction<Float> amplifier;
		private final FloatList locations = new FloatArrayList();
		private final List<Spline<C>> values = Lists.<Spline<C>>newArrayList();
		private final FloatList derivatives = new FloatArrayList();

		protected Builder(ToFloatFunction<C> locationFunction) {
			this(locationFunction, value -> value);
		}

		protected Builder(ToFloatFunction<C> locationFunction, ToFloatFunction<Float> amplifier) {
			this.locationFunction = locationFunction;
			this.amplifier = amplifier;
		}

		public Spline.Builder<C> add(float location, float value, float derivative) {
			return this.add(location, new Spline.FixedFloatFunction<>(this.amplifier.apply(value)), derivative);
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
				return new Spline.Implementation<>(this.locationFunction, this.locations.toFloatArray(), ImmutableList.copyOf(this.values), this.derivatives.toFloatArray());
			}
		}
	}

	@Debug
	public static record FixedFloatFunction<C>(float value) implements Spline<C> {
		@Override
		public float apply(C object) {
			return this.value;
		}

		@Override
		public String getDebugString() {
			return String.format("k=%.3f", this.value);
		}

		@Override
		public float method_40435() {
			return this.value;
		}

		@Override
		public float method_40436() {
			return this.value;
		}
	}

	@Debug
	public static record Implementation<C>(ToFloatFunction<C> locationFunction, float[] locations, List<Spline<C>> values, float[] derivatives)
		implements Spline<C> {

		public Implementation(ToFloatFunction<C> locationFunction, float[] locations, List<Spline<C>> values, float[] derivatives) {
			if (locations.length == values.size() && locations.length == derivatives.length) {
				this.locationFunction = locationFunction;
				this.locations = locations;
				this.values = values;
				this.derivatives = derivatives;
			} else {
				throw new IllegalArgumentException("All lengths must be equal, got: " + locations.length + " " + values.size() + " " + derivatives.length);
			}
		}

		@Override
		public float apply(C object) {
			float f = this.locationFunction.apply(object);
			int i = MathHelper.binarySearch(0, this.locations.length, index -> f < this.locations[index]) - 1;
			int j = this.locations.length - 1;
			if (i < 0) {
				return ((Spline)this.values.get(0)).apply(object) + this.derivatives[0] * (f - this.locations[0]);
			} else if (i == j) {
				return ((Spline)this.values.get(j)).apply(object) + this.derivatives[j] * (f - this.locations[j]);
			} else {
				float g = this.locations[i];
				float h = this.locations[i + 1];
				float k = (f - g) / (h - g);
				ToFloatFunction<C> toFloatFunction = (ToFloatFunction<C>)this.values.get(i);
				ToFloatFunction<C> toFloatFunction2 = (ToFloatFunction<C>)this.values.get(i + 1);
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
				+ this.locationFunction
				+ ", locations="
				+ this.format(this.locations)
				+ ", derivatives="
				+ this.format(this.derivatives)
				+ ", values="
				+ (String)this.values.stream().map(Spline::getDebugString).collect(Collectors.joining(", ", "[", "]"))
				+ "}";
		}

		private String format(float[] values) {
			return "["
				+ (String)IntStream.range(0, values.length)
					.mapToDouble(index -> (double)values[index])
					.mapToObj(value -> String.format(Locale.ROOT, "%.3f", value))
					.collect(Collectors.joining(", "))
				+ "]";
		}

		@Override
		public float method_40435() {
			return (float)this.values().stream().mapToDouble(Spline::method_40435).min().orElseThrow();
		}

		@Override
		public float method_40436() {
			return (float)this.values().stream().mapToDouble(Spline::method_40436).max().orElseThrow();
		}
	}
}
