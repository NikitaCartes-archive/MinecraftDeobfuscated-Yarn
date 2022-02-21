/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.mutable.MutableObject;

public interface Spline<C>
extends ToFloatFunction<C> {
    @Debug
    public String getDebugString();

    public float min();

    public float max();

    public Spline<C> method_41187(class_7073<C> var1);

    public static <C> Codec<Spline<C>> createCodec(Codec<ToFloatFunction<C>> locationFunctionCodec) {
        record Serialized<C>(float location, Spline<C> value, float derivative) {
        }
        MutableObject<Codec<Spline>> mutableObject = new MutableObject<Codec<Spline>>();
        Codec codec = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.FLOAT.fieldOf("location")).forGetter(Serialized::location), ((MapCodec)Codecs.createLazy(mutableObject::getValue).fieldOf("value")).forGetter(Serialized::value), ((MapCodec)Codec.FLOAT.fieldOf("derivative")).forGetter(Serialized::derivative)).apply((Applicative<Serialized, ?>)instance, (location, value, derivative) -> new Serialized((float)location, value, (float)derivative)));
        Codec codec2 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)locationFunctionCodec.fieldOf("coordinate")).forGetter(Implementation::locationFunction), ((MapCodec)Codecs.nonEmptyList(codec.listOf()).fieldOf("points")).forGetter(spline -> IntStream.range(0, spline.locations.length).mapToObj(index -> new Serialized(spline.locations()[index], spline.values().get(index), spline.derivatives()[index])).toList())).apply((Applicative<Implementation, ?>)instance, (locationFunction, splines) -> {
            float[] fs = new float[splines.size()];
            ImmutableList.Builder builder = ImmutableList.builder();
            float[] gs = new float[splines.size()];
            for (int i = 0; i < splines.size(); ++i) {
                Serialized serialized = (Serialized)splines.get(i);
                fs[i] = serialized.location();
                builder.add(serialized.value());
                gs[i] = serialized.derivative();
            }
            return new Implementation(locationFunction, fs, builder.build(), gs);
        }));
        mutableObject.setValue(Codec.either(Codec.FLOAT, codec2).xmap(either -> (Spline)((Object)either.map(FixedFloatFunction::new, spline -> spline)), spline -> {
            Either<Object, Implementation<Object>> either;
            if (spline instanceof FixedFloatFunction) {
                FixedFloatFunction fixedFloatFunction = (FixedFloatFunction)spline;
                either = Either.left(Float.valueOf(fixedFloatFunction.value()));
            } else {
                either = Either.right((Implementation)spline);
            }
            return either;
        }));
        return (Codec)mutableObject.getValue();
    }

    public static <C> Spline<C> fixedFloatFunction(float value) {
        return new FixedFloatFunction(value);
    }

    public static <C> Builder<C> builder(ToFloatFunction<C> locationFunction) {
        return new Builder<C>(locationFunction);
    }

    public static <C> Builder<C> builder(ToFloatFunction<C> locationFunction, ToFloatFunction<Float> amplifier) {
        return new Builder<C>(locationFunction, amplifier);
    }

    @Debug
    public record FixedFloatFunction<C>(float value) implements Spline<C>
    {
        @Override
        public float apply(C object) {
            return this.value;
        }

        @Override
        public String getDebugString() {
            return String.format("k=%.3f", Float.valueOf(this.value));
        }

        @Override
        public float min() {
            return this.value;
        }

        @Override
        public float max() {
            return this.value;
        }

        @Override
        public Spline<C> method_41187(class_7073<C> arg) {
            return this;
        }
    }

    public static final class Builder<C> {
        private final ToFloatFunction<C> locationFunction;
        private final ToFloatFunction<Float> amplifier;
        private final FloatList locations = new FloatArrayList();
        private final List<Spline<C>> values = Lists.newArrayList();
        private final FloatList derivatives = new FloatArrayList();

        protected Builder(ToFloatFunction<C> locationFunction) {
            this(locationFunction, value -> value.floatValue());
        }

        protected Builder(ToFloatFunction<C> locationFunction, ToFloatFunction<Float> amplifier) {
            this.locationFunction = locationFunction;
            this.amplifier = amplifier;
        }

        public Builder<C> add(float location, float value, float derivative) {
            return this.add(location, new FixedFloatFunction(this.amplifier.apply(Float.valueOf(value))), derivative);
        }

        public Builder<C> add(float location, Spline<C> value, float derivative) {
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
            return new Implementation<C>(this.locationFunction, this.locations.toFloatArray(), ImmutableList.copyOf(this.values), this.derivatives.toFloatArray());
        }
    }

    @Debug
    public record Implementation<C>(ToFloatFunction<C> locationFunction, float[] locations, List<Spline<C>> values, float[] derivatives) implements Spline<C>
    {
        public Implementation {
            if (fs.length != list.size() || fs.length != gs.length) {
                throw new IllegalArgumentException("All lengths must be equal, got: " + fs.length + " " + list.size() + " " + gs.length);
            }
        }

        @Override
        public float apply(C object) {
            float f = this.locationFunction.apply(object);
            int i = MathHelper.binarySearch(0, this.locations.length, index -> f < this.locations[index]) - 1;
            int j = this.locations.length - 1;
            if (i < 0) {
                return this.values.get(0).apply(object) + this.derivatives[0] * (f - this.locations[0]);
            }
            if (i == j) {
                return this.values.get(j).apply(object) + this.derivatives[j] * (f - this.locations[j]);
            }
            float g = this.locations[i];
            float h = this.locations[i + 1];
            float k = (f - g) / (h - g);
            ToFloatFunction toFloatFunction = this.values.get(i);
            ToFloatFunction toFloatFunction2 = this.values.get(i + 1);
            float l = this.derivatives[i];
            float m = this.derivatives[i + 1];
            float n = toFloatFunction.apply(object);
            float o = toFloatFunction2.apply(object);
            float p = l * (h - g) - (o - n);
            float q = -m * (h - g) + (o - n);
            float r = MathHelper.lerp(k, n, o) + k * (1.0f - k) * MathHelper.lerp(k, p, q);
            return r;
        }

        @Override
        @VisibleForTesting
        public String getDebugString() {
            return "Spline{coordinate=" + this.locationFunction + ", locations=" + this.format(this.locations) + ", derivatives=" + this.format(this.derivatives) + ", values=" + this.values.stream().map(Spline::getDebugString).collect(Collectors.joining(", ", "[", "]")) + "}";
        }

        private String format(float[] values) {
            return "[" + IntStream.range(0, values.length).mapToDouble(index -> values[index]).mapToObj(value -> String.format(Locale.ROOT, "%.3f", value)).collect(Collectors.joining(", ")) + "]";
        }

        @Override
        public float min() {
            return (float)this.values().stream().mapToDouble(Spline::min).min().orElseThrow();
        }

        @Override
        public float max() {
            return (float)this.values().stream().mapToDouble(Spline::max).max().orElseThrow();
        }

        @Override
        public Spline<C> method_41187(class_7073<C> arg) {
            return new Implementation<C>(arg.visit(this.locationFunction), this.locations, this.values().stream().map(spline -> spline.method_41187(arg)).toList(), this.derivatives);
        }
    }

    public static interface class_7073<C> {
        public ToFloatFunction<C> visit(ToFloatFunction<C> var1);
    }
}

