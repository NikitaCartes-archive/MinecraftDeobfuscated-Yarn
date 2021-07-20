package net.minecraft.util.math;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.util.function.ToFloatFunction;

public final class Spline<C> implements ToFloatFunction<C> {
	final ToFloatFunction<C> locationFunction;
	final float[] locations;
	final List<ToFloatFunction<C>> values;
	final float[] derivatives;
	String name;

	Spline(String name, ToFloatFunction<C> locationFunction, float[] locations, List<ToFloatFunction<C>> values, float[] derivatives) {
		this.name = name;
		if (locations.length == values.size() && locations.length == derivatives.length) {
			this.locationFunction = locationFunction;
			this.locations = locations;
			this.values = values;
			this.derivatives = derivatives;
		} else {
			throw new IllegalArgumentException("All lengths must be equal, got: " + locations.length + " " + values.size() + " " + derivatives.length);
		}
	}

	public Spline<C> getThis() {
		return this;
	}

	@Override
	public float apply(C object) {
		return MathHelper.getSplineFunction(this.locationFunction.apply(object), this.locations, this.values, this.derivatives).apply(object);
	}

	public static <C> Spline.Builder<C> builder(ToFloatFunction<C> locationFunction) {
		return new Spline.Builder<>(locationFunction);
	}

	private String getListAsString(float[] locations) {
		return "["
			+ (String)IntStream.range(0, locations.length)
				.mapToDouble(i -> (double)locations[i])
				.mapToObj(d -> String.format("%.2f", d))
				.collect(Collectors.joining(", "))
			+ "]";
	}

	public String toString() {
		return "Spline{name="
			+ this.name
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
		@Nullable
		private Float lastLocation = null;
		private String name = null;

		public Builder(ToFloatFunction<C> locationFunction) {
			this.locationFunction = locationFunction;
		}

		public Spline.Builder<C> setName(String name) {
			this.name = name;
			return this;
		}

		public Spline.Builder<C> add(float location, float value, float derivative) {
			return this.addSplinePoint(location, new Spline.FixedFloatFunction<>(value), derivative);
		}

		public Spline.Builder<C> add(float location, ToFloatFunction<C> value, float derivative) {
			return this.addSplinePoint(location, value, derivative);
		}

		public Spline.Builder<C> add(float location, Spline<C> value, float derivative) {
			return this.addSplinePoint(location, value.getThis(), derivative);
		}

		private Spline.Builder<C> addSplinePoint(float location, ToFloatFunction<C> value, float derivative) {
			if (this.lastLocation != null && location <= this.lastLocation) {
				throw new IllegalArgumentException("The way things are right now, we depend on registration in descending order");
			} else {
				this.locations.add(location);
				this.values.add(value);
				this.derivatives.add(derivative);
				this.lastLocation = location;
				return this;
			}
		}

		public Spline<C> build() {
			if (this.name == null) {
				throw new IllegalStateException("Splines require a name");
			} else if (this.locations.isEmpty()) {
				throw new IllegalStateException("No elements added");
			} else {
				return new Spline<>(this.name, this.locationFunction, this.locations.toFloatArray(), ImmutableList.copyOf(this.values), this.derivatives.toFloatArray());
			}
		}
	}

	static class FixedFloatFunction<C> implements ToFloatFunction<C> {
		private float value;

		public FixedFloatFunction(float value) {
			this.value = value;
		}

		@Override
		public float apply(C object) {
			return this.value;
		}

		public String toString() {
			return String.format("k=%.2f", this.value);
		}
	}

	public interface FloatBinaryOperator {
		float combine(float a, float b);
	}
}
