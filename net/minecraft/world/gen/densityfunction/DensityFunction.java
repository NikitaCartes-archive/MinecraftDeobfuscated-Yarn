/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.densityfunction;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;

public interface DensityFunction {
    public static final Codec<DensityFunction> field_37057 = DensityFunctionTypes.field_37061;
    public static final Codec<RegistryEntry<DensityFunction>> REGISTRY_ENTRY_CODEC = RegistryElementCodec.of(Registry.DENSITY_FUNCTION_KEY, field_37057);
    public static final Codec<DensityFunction> field_37059 = REGISTRY_ENTRY_CODEC.xmap(DensityFunctionTypes.class_7051::new, densityFunction -> {
        if (densityFunction instanceof DensityFunctionTypes.class_7051) {
            DensityFunctionTypes.class_7051 lv = (DensityFunctionTypes.class_7051)densityFunction;
            return lv.function();
        }
        return new RegistryEntry.Direct<DensityFunction>((DensityFunction)densityFunction);
    });

    public double sample(NoisePos var1);

    public void method_40470(double[] var1, class_6911 var2);

    public DensityFunction method_40469(DensityFunctionVisitor var1);

    public double minValue();

    public double maxValue();

    public Codec<? extends DensityFunction> getCodec();

    default public DensityFunction clamp(double min, double max) {
        return new DensityFunctionTypes.Clamp(this, min, max);
    }

    default public DensityFunction abs() {
        return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.class_6926.ABS);
    }

    default public DensityFunction square() {
        return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.class_6926.SQUARE);
    }

    default public DensityFunction cube() {
        return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.class_6926.CUBE);
    }

    default public DensityFunction halfNegative() {
        return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.class_6926.HALF_NEGATIVE);
    }

    default public DensityFunction quarterNegative() {
        return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.class_6926.QUARTER_NEGATIVE);
    }

    default public DensityFunction squeeze() {
        return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.class_6926.SQUEEZE);
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
        default public DensityFunction method_40469(DensityFunctionVisitor densityFunctionVisitor) {
            return (DensityFunction)densityFunctionVisitor.apply(this);
        }
    }

    public static interface DensityFunctionVisitor
    extends Function<DensityFunction, DensityFunction> {
    }

    public static interface class_6911 {
        public NoisePos method_40477(int var1);

        public void method_40478(double[] var1, DensityFunction var2);
    }
}

