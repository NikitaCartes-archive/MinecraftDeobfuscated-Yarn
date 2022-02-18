/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.noise;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.class_6916;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.chunk.Blender;

public interface NoiseType {
    public static final Codec<NoiseType> field_37057 = class_6916.field_37061;
    public static final Codec<RegistryEntry<NoiseType>> field_37058 = RegistryElementCodec.of(Registry.DENSITY_FUNCTION_WORLDGEN, field_37057);
    public static final Codec<NoiseType> field_37059 = field_37058.xmap(class_6916.class_7051::new, noiseType -> {
        if (noiseType instanceof class_6916.class_7051) {
            class_6916.class_7051 lv = (class_6916.class_7051)noiseType;
            return lv.function();
        }
        return new RegistryEntry.Direct<NoiseType>((NoiseType)noiseType);
    });

    public double sample(NoisePos var1);

    public void method_40470(double[] var1, class_6911 var2);

    public NoiseType method_40469(class_6915 var1);

    public double minValue();

    public double maxValue();

    public Codec<? extends NoiseType> method_41062();

    default public NoiseType method_40468(double d, double e) {
        return new class_6916.class_6922(this, d, e);
    }

    default public NoiseType method_40471() {
        return class_6916.method_40490(this, class_6916.class_6925.class_6926.ABS);
    }

    default public NoiseType method_40472() {
        return class_6916.method_40490(this, class_6916.class_6925.class_6926.SQUARE);
    }

    default public NoiseType method_40473() {
        return class_6916.method_40490(this, class_6916.class_6925.class_6926.CUBE);
    }

    default public NoiseType method_40474() {
        return class_6916.method_40490(this, class_6916.class_6925.class_6926.HALF_NEGATIVE);
    }

    default public NoiseType method_40475() {
        return class_6916.method_40490(this, class_6916.class_6925.class_6926.QUARTER_NEGATIVE);
    }

    default public NoiseType method_40476() {
        return class_6916.method_40490(this, class_6916.class_6925.class_6926.SQUEEZE);
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
    extends NoiseType {
        @Override
        default public void method_40470(double[] ds, class_6911 arg) {
            arg.method_40478(ds, this);
        }

        @Override
        default public NoiseType method_40469(class_6915 arg) {
            return (NoiseType)arg.apply(this);
        }
    }

    public static interface class_6915
    extends Function<NoiseType, NoiseType> {
    }

    public static interface class_6911 {
        public NoisePos method_40477(int var1);

        public void method_40478(double[] var1, NoiseType var2);
    }
}

