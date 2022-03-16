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

public interface Spline<C, I extends ToFloatFunction<C>>
extends ToFloatFunction<C> {
    @Debug
    public String getDebugString();

    public Spline<C, I> method_41187(class_7073<I> var1);

    public static <C, I extends ToFloatFunction<C>> Codec<Spline<C, I>> createCodec(Codec<I> locationFunctionCodec) {
        record Serialized<C, I extends ToFloatFunction<C>>(float location, Spline<C, I> value, float derivative) {
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
            return Implementation.method_41299(locationFunction, fs, builder.build(), gs);
        }));
        mutableObject.setValue(Codec.either(Codec.FLOAT, codec2).xmap(either -> (Spline)((Object)either.map(FixedFloatFunction::new, spline -> spline)), spline -> {
            Either either;
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

    public static <C, I extends ToFloatFunction<C>> Spline<C, I> fixedFloatFunction(float value) {
        return new FixedFloatFunction(value);
    }

    public static <C, I extends ToFloatFunction<C>> Builder<C, I> builder(I locationFunction) {
        return new Builder(locationFunction);
    }

    public static <C, I extends ToFloatFunction<C>> Builder<C, I> builder(I locationFunction, ToFloatFunction<Float> amplifier) {
        return new Builder(locationFunction, amplifier);
    }

    @Debug
    public record FixedFloatFunction<C, I extends ToFloatFunction<C>>(float value) implements Spline<C, I>
    {
        @Override
        public float apply(C x) {
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
        public Spline<C, I> method_41187(class_7073<I> arg) {
            return this;
        }
    }

    public static final class Builder<C, I extends ToFloatFunction<C>> {
        private final I locationFunction;
        private final ToFloatFunction<Float> amplifier;
        private final FloatList locations = new FloatArrayList();
        private final List<Spline<C, I>> values = Lists.newArrayList();
        private final FloatList derivatives = new FloatArrayList();

        protected Builder(I locationFunction) {
            this(locationFunction, ToFloatFunction.field_37409);
        }

        protected Builder(I locationFunction, ToFloatFunction<Float> amplifier) {
            this.locationFunction = locationFunction;
            this.amplifier = amplifier;
        }

        public Builder<C, I> method_41294(float f, float g) {
            return this.add(f, new FixedFloatFunction(this.amplifier.apply(Float.valueOf(g))), 0.0f);
        }

        public Builder<C, I> add(float location, float value, float derivative) {
            return this.add(location, new FixedFloatFunction(this.amplifier.apply(Float.valueOf(value))), derivative);
        }

        public Builder<C, I> method_41295(float f, Spline<C, I> spline) {
            return this.add(f, spline, 0.0f);
        }

        private Builder<C, I> add(float location, Spline<C, I> value, float derivative) {
            if (!this.locations.isEmpty() && location <= this.locations.getFloat(this.locations.size() - 1)) {
                throw new IllegalArgumentException("Please register points in ascending order");
            }
            this.locations.add(location);
            this.values.add(value);
            this.derivatives.add(derivative);
            return this;
        }

        public Spline<C, I> build() {
            if (this.locations.isEmpty()) {
                throw new IllegalStateException("No elements added");
            }
            return Implementation.method_41299(this.locationFunction, this.locations.toFloatArray(), ImmutableList.copyOf(this.values), this.derivatives.toFloatArray());
        }
    }

    @Debug
    public record Implementation<C, I extends ToFloatFunction<C>>(I locationFunction, float[] locations, List<Spline<C, I>> values, float[] derivatives, float min, float max) implements Spline<C, I>
    {
        public Implementation {
            Implementation.method_41301(fs, list, gs);
        }

        static <C, I extends ToFloatFunction<C>> Implementation<C, I> method_41299(I toFloatFunction, float[] fs, List<Spline<C, I>> list, float[] gs) {
            float l;
            float k;
            Implementation.method_41301(fs, list, gs);
            int i = fs.length - 1;
            float f = Float.POSITIVE_INFINITY;
            float g = Float.NEGATIVE_INFINITY;
            float h = toFloatFunction.min();
            float j = toFloatFunction.max();
            if (h < fs[0]) {
                k = Implementation.method_41297(h, fs, list.get(0).min(), gs, 0);
                l = Implementation.method_41297(h, fs, list.get(0).max(), gs, 0);
                f = Math.min(f, Math.min(k, l));
                g = Math.max(g, Math.max(k, l));
            }
            if (j > fs[i]) {
                k = Implementation.method_41297(j, fs, list.get(i).min(), gs, i);
                l = Implementation.method_41297(j, fs, list.get(i).max(), gs, i);
                f = Math.min(f, Math.min(k, l));
                g = Math.max(g, Math.max(k, l));
            }
            for (Spline<C, I> spline : list) {
                f = Math.min(f, spline.min());
                g = Math.max(g, spline.max());
            }
            for (int m = 0; m < i; ++m) {
                l = fs[m];
                float n = fs[m + 1];
                float o = n - l;
                Spline<C, I> spline2 = list.get(m);
                Spline<C, I> spline3 = list.get(m + 1);
                float p = spline2.min();
                float q = spline2.max();
                float r = spline3.min();
                float s = spline3.max();
                float t = gs[m];
                float u = gs[m + 1];
                if (t == 0.0f && u == 0.0f) continue;
                float v = t * o;
                float w = u * o;
                float x = Math.min(p, r);
                float y = Math.max(q, s);
                float z = v - s + p;
                float aa = v - r + q;
                float ab = -w + r - q;
                float ac = -w + s - p;
                float ad = Math.min(z, ab);
                float ae = Math.max(aa, ac);
                f = Math.min(f, x + 0.25f * ad);
                g = Math.max(g, y + 0.25f * ae);
            }
            return new Implementation<C, I>(toFloatFunction, fs, list, gs, f, g);
        }

        private static float method_41297(float f, float[] fs, float g, float[] gs, int i) {
            float h = gs[i];
            if (h == 0.0f) {
                return g;
            }
            return g + h * (f - fs[i]);
        }

        private static <C, I extends ToFloatFunction<C>> void method_41301(float[] fs, List<Spline<C, I>> list, float[] gs) {
            if (fs.length != list.size() || fs.length != gs.length) {
                throw new IllegalArgumentException("All lengths must be equal, got: " + fs.length + " " + list.size() + " " + gs.length);
            }
            if (fs.length == 0) {
                throw new IllegalArgumentException("Cannot create a multipoint spline with no points");
            }
        }

        @Override
        public float apply(C x) {
            float f = this.locationFunction.apply(x);
            int i = Implementation.method_41300(this.locations, f);
            int j = this.locations.length - 1;
            if (i < 0) {
                return Implementation.method_41297(f, this.locations, this.values.get(0).apply(x), this.derivatives, 0);
            }
            if (i == j) {
                return Implementation.method_41297(f, this.locations, this.values.get(j).apply(x), this.derivatives, j);
            }
            float g = this.locations[i];
            float h = this.locations[i + 1];
            float k = (f - g) / (h - g);
            ToFloatFunction toFloatFunction = this.values.get(i);
            ToFloatFunction toFloatFunction2 = this.values.get(i + 1);
            float l = this.derivatives[i];
            float m = this.derivatives[i + 1];
            float n = toFloatFunction.apply(x);
            float o = toFloatFunction2.apply(x);
            float p = l * (h - g) - (o - n);
            float q = -m * (h - g) + (o - n);
            float r = MathHelper.lerp(k, n, o) + k * (1.0f - k) * MathHelper.lerp(k, p, q);
            return r;
        }

        private static int method_41300(float[] fs, float f) {
            return MathHelper.binarySearch(0, fs.length, i -> f < fs[i]) - 1;
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
        public Spline<C, I> method_41187(class_7073<I> arg) {
            return Implementation.method_41299((ToFloatFunction)arg.visit(this.locationFunction), this.locations, this.values().stream().map(spline -> spline.method_41187(arg)).toList(), this.derivatives);
        }
    }

    public static interface class_7073<I> {
        public I visit(I var1);
    }
}

