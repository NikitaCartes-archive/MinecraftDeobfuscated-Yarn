package net.minecraft.util.math;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.function.ToFloatFunction;

public final class Spline<C> implements ToFloatFunction<C> {
	private final ToFloatFunction<C> locationFunction;
	private final float[] locations;
	private final List<ToFloatFunction<C>> values;
	private final float[] derivatives;

	Spline(ToFloatFunction<C> locationFunction, float[] locations, List<ToFloatFunction<C>> values, float[] derivatives) {
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
		int i = MathHelper.binarySearch(0, this.locations.length, ix -> f < this.locations[ix]) - 1;
		int j = this.locations.length - 1;
		if (i < 0) {
			return ((ToFloatFunction)this.values.get(0)).apply(object) + this.derivatives[0] * (f - this.locations[0]);
		} else if (i == j) {
			return ((ToFloatFunction)this.values.get(j)).apply(object) + this.derivatives[j] * (f - this.locations[j]);
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

	public static <C> Spline.Builder<C> builder(ToFloatFunction<C> locationFunction) {
		return new Spline.Builder<>(locationFunction);
	}

	private String getListAsString(float[] locations) {
		return "["
			+ (String)IntStream.range(0, locations.length)
				.mapToDouble(i -> (double)locations[i])
				.mapToObj(d -> String.format("%.3f", d))
				.collect(Collectors.joining(", "))
			+ "]";
	}

	@Debug
	protected ToFloatFunction<C> getLocationFunction() {
		return this.locationFunction;
	}

	@Debug
	public List<Float> getLocations() {
		return Collections.unmodifiableList(Floats.asList(this.locations));
	}

	@Debug
	public ToFloatFunction<C> getValue(int index) {
		return (ToFloatFunction<C>)this.values.get(index);
	}

	@Debug
	public float getDerivative(int index) {
		return this.derivatives[index];
	}

	public String toString() {
		return "Spline{coordinate="
			+ this.locationFunction
			+ ", locations="
			+ this.getListAsString(this.locations)
			+ ", derivatives="
			+ this.getListAsString(this.derivatives)
			+ ", values="
			+ this.values
			+ "}";
	}

	public static final class Builder<C> {
		private final ToFloatFunction<C> locationFunction;
		private final FloatList locations = new FloatArrayList();
		private final List<ToFloatFunction<C>> values = Lists.<ToFloatFunction<C>>newArrayList();
		private final FloatList derivatives = new FloatArrayList();

		protected Builder(ToFloatFunction<C> locationFunction) {
			this.locationFunction = locationFunction;
		}

		public Spline.Builder<C> add(float location, float value, float derivative) {
			return this.addSplinePoint(location, new Spline.FixedFloatFunction<>(value), derivative);
		}

		public Spline.Builder<C> add(float location, ToFloatFunction<C> value, float derivative) {
			return this.addSplinePoint(location, value, derivative);
		}

		public Spline.Builder<C> add(float location, Spline<C> value, float derivative) {
			return this.addSplinePoint(location, value, derivative);
		}

		private Spline.Builder<C> addSplinePoint(float location, ToFloatFunction<C> value, float derivative) {
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
				return new Spline<>(this.locationFunction, this.locations.toFloatArray(), ImmutableList.copyOf(this.values), this.derivatives.toFloatArray());
			}
		}
	}

	static class FixedFloatFunction<C> implements ToFloatFunction<C> {
		private final float value;

		public FixedFloatFunction(float value) {
			this.value = value;
		}

		@Override
		public float apply(C object) {
			return this.value;
		}

		public String toString() {
			return String.format("k=%.3f", this.value);
		}
	}
}
