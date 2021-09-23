/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.util.math.MathHelper;

public final class Spline<C>
implements ToFloatFunction<C> {
    private final ToFloatFunction<C> locationFunction;
    private final float[] locations;
    private final List<ToFloatFunction<C>> values;
    private final float[] derivatives;

    Spline(ToFloatFunction<C> locationFunction, float[] locations, List<ToFloatFunction<C>> values, float[] derivatives) {
        if (locations.length != values.size() || locations.length != derivatives.length) {
            throw new IllegalArgumentException("All lengths must be equal, got: " + locations.length + " " + values.size() + " " + derivatives.length);
        }
        this.locationFunction = locationFunction;
        this.locations = locations;
        this.values = values;
        this.derivatives = derivatives;
    }

    @Override
    public float apply(C object) {
        float f = this.locationFunction.apply(object);
        int i2 = MathHelper.binarySearch(0, this.locations.length, i -> f < this.locations[i]) - 1;
        int j = this.locations.length - 1;
        if (i2 < 0) {
            return this.values.get(0).apply(object) + this.derivatives[0] * (f - this.locations[0]);
        }
        if (i2 == j) {
            return this.values.get(j).apply(object) + this.derivatives[j] * (f - this.locations[j]);
        }
        float g = this.locations[i2];
        float h = this.locations[i2 + 1];
        float k = (f - g) / (h - g);
        ToFloatFunction<C> toFloatFunction = this.values.get(i2);
        ToFloatFunction<C> toFloatFunction2 = this.values.get(i2 + 1);
        float l = this.derivatives[i2];
        float m = this.derivatives[i2 + 1];
        float n = toFloatFunction.apply(object);
        float o = toFloatFunction2.apply(object);
        float p = l * (h - g) - (o - n);
        float q = -m * (h - g) + (o - n);
        float r = MathHelper.lerp(k, n, o) + k * (1.0f - k) * MathHelper.lerp(k, p, q);
        return r;
    }

    public static <C> Builder<C> builder(ToFloatFunction<C> locationFunction) {
        return new Builder<C>(locationFunction);
    }

    private String getListAsString(float[] locations) {
        return "[" + IntStream.range(0, locations.length).mapToDouble(i -> locations[i]).mapToObj(d -> String.format("%.3f", d)).collect(Collectors.joining(", ")) + "]";
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
        return this.values.get(index);
    }

    @Debug
    public float getDerivative(int index) {
        return this.derivatives[index];
    }

    public String toString() {
        return "Spline{coordinate=" + this.locationFunction + ", locations=" + this.getListAsString(this.locations) + ", derivatives=" + this.getListAsString(this.derivatives) + ", values=" + this.values + "}";
    }

    public static final class Builder<C> {
        private final ToFloatFunction<C> locationFunction;
        private final FloatList locations = new FloatArrayList();
        private final List<ToFloatFunction<C>> values = Lists.newArrayList();
        private final FloatList derivatives = new FloatArrayList();

        protected Builder(ToFloatFunction<C> locationFunction) {
            this.locationFunction = locationFunction;
        }

        public Builder<C> add(float location, float value, float derivative) {
            return this.addSplinePoint(location, new FixedFloatFunction(value), derivative);
        }

        public Builder<C> add(float location, ToFloatFunction<C> value, float derivative) {
            return this.addSplinePoint(location, value, derivative);
        }

        public Builder<C> add(float location, Spline<C> value, float derivative) {
            return this.addSplinePoint(location, value, derivative);
        }

        private Builder<C> addSplinePoint(float location, ToFloatFunction<C> value, float derivative) {
            if (!this.locations.isEmpty() && location <= this.locations.getFloat(this.locations.size() - 1)) {
                throw new IllegalArgumentException("Please register points in ascending order");
            }
            this.locations.add(location);
            this.values.add(value);
            this.derivatives.add(derivative);
            return this;
        }

        public Spline<C> build() {
            if (this.locations.isEmpty()) {
                throw new IllegalStateException("No elements added");
            }
            return new Spline<C>(this.locationFunction, this.locations.toFloatArray(), ImmutableList.copyOf(this.values), this.derivatives.toFloatArray());
        }
    }

    static class FixedFloatFunction<C>
    implements ToFloatFunction<C> {
        private final float value;

        public FixedFloatFunction(float value) {
            this.value = value;
        }

        @Override
        public float apply(C object) {
            return this.value;
        }

        public String toString() {
            return String.format("k=%.3f", Float.valueOf(this.value));
        }
    }
}

