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

    public static <C> Codec<Spline<C>> method_39232(Codec<ToFloatFunction<C>> codec) {
        record class_6737(float location, Spline<C> value, float derivative) {
        }
        MutableObject<Codec<Spline>> mutableObject = new MutableObject<Codec<Spline>>();
        Codec codec2 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.FLOAT.fieldOf("location")).forGetter(class_6737::location), ((MapCodec)Codecs.createLazy(mutableObject::getValue).fieldOf("value")).forGetter(class_6737::value), ((MapCodec)Codec.FLOAT.fieldOf("derivative")).forGetter(class_6737::derivative)).apply((Applicative<class_6737, ?>)instance, (f, spline, g) -> new class_6737((float)f, spline, (float)g)));
        Codec codec3 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)codec.fieldOf("coordinate")).forGetter(class_6738::coordinate), ((MapCodec)codec2.listOf().fieldOf("points")).forGetter(arg -> IntStream.range(0, arg.locations.length).mapToObj(i -> new class_6737(arg.locations()[i], arg.values().get(i), arg.derivatives()[i])).toList())).apply((Applicative<class_6738, ?>)instance, (toFloatFunction, list) -> {
            float[] fs = new float[list.size()];
            ImmutableList.Builder builder = ImmutableList.builder();
            float[] gs = new float[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                class_6737 lv = (class_6737)list.get(i);
                fs[i] = lv.location();
                builder.add(lv.value());
                gs[i] = lv.derivative();
            }
            return new class_6738(toFloatFunction, fs, builder.build(), gs);
        }));
        mutableObject.setValue(Codec.either(Codec.FLOAT, codec3).xmap(either -> (Spline)((Object)either.map(FixedFloatFunction::new, arg -> arg)), spline -> {
            Either<Object, class_6738<Object>> either;
            Spline spline2 = spline;
            if (spline2 instanceof FixedFloatFunction) {
                FixedFloatFunction fixedFloatFunction = (FixedFloatFunction)spline2;
                either = Either.left(Float.valueOf(fixedFloatFunction.value()));
            } else {
                either = Either.right((class_6738)spline);
            }
            return either;
        }));
        return (Codec)mutableObject.getValue();
    }

    public static <C> Spline<C> method_39427(float f) {
        return new FixedFloatFunction(f);
    }

    public static <C> Builder<C> builder(ToFloatFunction<C> locationFunction) {
        return new Builder<C>(locationFunction);
    }

    public static <C> Builder<C> method_39502(ToFloatFunction<C> toFloatFunction, ToFloatFunction<Float> toFloatFunction2) {
        return new Builder<C>(toFloatFunction, toFloatFunction2);
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
    }

    public static final class Builder<C> {
        private final ToFloatFunction<C> locationFunction;
        private final ToFloatFunction<Float> field_35661;
        private final FloatList locations = new FloatArrayList();
        private final List<Spline<C>> values = Lists.newArrayList();
        private final FloatList derivatives = new FloatArrayList();

        protected Builder(ToFloatFunction<C> locationFunction) {
            this(locationFunction, float_ -> float_.floatValue());
        }

        protected Builder(ToFloatFunction<C> toFloatFunction, ToFloatFunction<Float> toFloatFunction2) {
            this.locationFunction = toFloatFunction;
            this.field_35661 = toFloatFunction2;
        }

        public Builder<C> add(float location, float value, float derivative) {
            return this.add(location, new FixedFloatFunction(this.field_35661.apply(Float.valueOf(value))), derivative);
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
            return new class_6738<C>(this.locationFunction, this.locations.toFloatArray(), ImmutableList.copyOf(this.values), this.derivatives.toFloatArray());
        }
    }

    @Debug
    public record class_6738<C>(ToFloatFunction<C> coordinate, float[] locations, List<Spline<C>> values, float[] derivatives) implements Spline
    {
        public class_6738 {
            if (fs.length != list.size() || fs.length != gs.length) {
                throw new IllegalArgumentException("All lengths must be equal, got: " + fs.length + " " + list.size() + " " + gs.length);
            }
        }

        @Override
        public float apply(C object) {
            float f = this.coordinate.apply(object);
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
            ToFloatFunction toFloatFunction = this.values.get(i2);
            ToFloatFunction toFloatFunction2 = this.values.get(i2 + 1);
            float l = this.derivatives[i2];
            float m = this.derivatives[i2 + 1];
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
            return "Spline{coordinate=" + this.coordinate + ", locations=" + this.method_39238(this.locations) + ", derivatives=" + this.method_39238(this.derivatives) + ", values=" + this.values.stream().map(Spline::getDebugString).collect(Collectors.joining(", ", "[", "]")) + "}";
        }

        private String method_39238(float[] fs) {
            return "[" + IntStream.range(0, fs.length).mapToDouble(i -> fs[i]).mapToObj(d -> String.format(Locale.ROOT, "%.3f", d)).collect(Collectors.joining(", ")) + "]";
        }
    }
}

