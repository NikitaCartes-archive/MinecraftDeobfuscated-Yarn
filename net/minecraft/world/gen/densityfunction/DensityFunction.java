/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.densityfunction;

import com.mojang.serialization.Codec;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import org.jetbrains.annotations.Nullable;

public interface DensityFunction {
    public static final Codec<DensityFunction> field_37057 = DensityFunctionTypes.field_37061;
    public static final Codec<RegistryEntry<DensityFunction>> REGISTRY_ENTRY_CODEC = RegistryElementCodec.of(Registry.DENSITY_FUNCTION_KEY, field_37057);
    public static final Codec<DensityFunction> field_37059 = REGISTRY_ENTRY_CODEC.xmap(DensityFunctionTypes.RegistryEntryHolder::new, densityFunction -> {
        if (densityFunction instanceof DensityFunctionTypes.RegistryEntryHolder) {
            DensityFunctionTypes.RegistryEntryHolder registryEntryHolder = (DensityFunctionTypes.RegistryEntryHolder)densityFunction;
            return registryEntryHolder.function();
        }
        return new RegistryEntry.Direct<DensityFunction>((DensityFunction)densityFunction);
    });

    public double sample(NoisePos var1);

    public void method_40470(double[] var1, class_6911 var2);

    public DensityFunction apply(DensityFunctionVisitor var1);

    public double minValue();

    public double maxValue();

    public CodecHolder<? extends DensityFunction> getCodec();

    default public DensityFunction clamp(double min, double max) {
        return new DensityFunctionTypes.Clamp(this, min, max);
    }

    default public DensityFunction abs() {
        return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.Type.ABS);
    }

    default public DensityFunction square() {
        return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.Type.SQUARE);
    }

    default public DensityFunction cube() {
        return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.Type.CUBE);
    }

    default public DensityFunction halfNegative() {
        return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.Type.HALF_NEGATIVE);
    }

    default public DensityFunction quarterNegative() {
        return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.Type.QUARTER_NEGATIVE);
    }

    default public DensityFunction squeeze() {
        return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.Type.SQUEEZE);
    }

    public record UnblendedNoisePos(int blockX, int blockY, int blockZ) implements NoisePos
    {
    }

    public static interface NoisePos {
        public int blockX();

        public int blockY();

        public int blockZ();

        default public Blender getBlender() {
            return Blender.getNoBlending();
        }
    }

    public static interface class_6913
    extends DensityFunction {
        @Override
        default public void method_40470(double[] ds, class_6911 arg) {
            arg.method_40478(ds, this);
        }

        @Override
        default public DensityFunction apply(DensityFunctionVisitor visitor) {
            return visitor.apply(this);
        }
    }

    public static interface DensityFunctionVisitor {
        public DensityFunction apply(DensityFunction var1);

        default public class_7270 method_42358(class_7270 arg) {
            return arg;
        }
    }

    public record class_7270(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler noise) {
        public static final Codec<class_7270> field_38248 = DoublePerlinNoiseSampler.NoiseParameters.CODEC.xmap(registryEntry -> new class_7270((RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters>)registryEntry, null), class_7270::noiseData);

        public class_7270(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry) {
            this(registryEntry, null);
        }

        public double method_42356(double d, double e, double f) {
            return this.noise == null ? 0.0 : this.noise.sample(d, e, f);
        }

        public double method_42355() {
            return this.noise == null ? 2.0 : this.noise.method_40554();
        }

        @Nullable
        public DoublePerlinNoiseSampler noise() {
            return this.noise;
        }
    }

    public static interface class_6911 {
        public NoisePos method_40477(int var1);

        public void method_40478(double[] var1, DensityFunction var2);
    }
}

