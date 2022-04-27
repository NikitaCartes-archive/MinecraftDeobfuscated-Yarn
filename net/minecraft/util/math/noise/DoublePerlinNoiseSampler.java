/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.noise;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import java.util.List;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

public class DoublePerlinNoiseSampler {
    private static final double DOMAIN_SCALE = 1.0181268882175227;
    private static final double field_31703 = 0.3333333333333333;
    private final double amplitude;
    private final OctavePerlinNoiseSampler firstSampler;
    private final OctavePerlinNoiseSampler secondSampler;
    private final double field_36631;
    private final NoiseParameters field_37207;

    @Deprecated
    public static DoublePerlinNoiseSampler createLegacy(AbstractRandom random, NoiseParameters parameters) {
        return new DoublePerlinNoiseSampler(random, parameters, false);
    }

    public static DoublePerlinNoiseSampler create(AbstractRandom random, int offset, double ... octaves) {
        return DoublePerlinNoiseSampler.create(random, new NoiseParameters(offset, new DoubleArrayList(octaves)));
    }

    public static DoublePerlinNoiseSampler create(AbstractRandom random, NoiseParameters parameters) {
        return new DoublePerlinNoiseSampler(random, parameters, true);
    }

    private DoublePerlinNoiseSampler(AbstractRandom random, NoiseParameters noiseParameters, boolean bl) {
        int i = noiseParameters.firstOctave;
        DoubleList doubleList = noiseParameters.amplitudes;
        this.field_37207 = noiseParameters;
        if (bl) {
            this.firstSampler = OctavePerlinNoiseSampler.create(random, i, doubleList);
            this.secondSampler = OctavePerlinNoiseSampler.create(random, i, doubleList);
        } else {
            this.firstSampler = OctavePerlinNoiseSampler.createLegacy(random, i, doubleList);
            this.secondSampler = OctavePerlinNoiseSampler.createLegacy(random, i, doubleList);
        }
        int j = Integer.MAX_VALUE;
        int k = Integer.MIN_VALUE;
        DoubleListIterator doubleListIterator = doubleList.iterator();
        while (doubleListIterator.hasNext()) {
            int l = doubleListIterator.nextIndex();
            double d = doubleListIterator.nextDouble();
            if (d == 0.0) continue;
            j = Math.min(j, l);
            k = Math.max(k, l);
        }
        this.amplitude = 0.16666666666666666 / DoublePerlinNoiseSampler.createAmplitude(k - j);
        this.field_36631 = (this.firstSampler.method_40555() + this.secondSampler.method_40555()) * this.amplitude;
    }

    public double method_40554() {
        return this.field_36631;
    }

    private static double createAmplitude(int octaves) {
        return 0.1 * (1.0 + 1.0 / (double)(octaves + 1));
    }

    public double sample(double x, double y, double z) {
        double d = x * 1.0181268882175227;
        double e = y * 1.0181268882175227;
        double f = z * 1.0181268882175227;
        return (this.firstSampler.sample(x, y, z) + this.secondSampler.sample(d, e, f)) * this.amplitude;
    }

    public NoiseParameters copy() {
        return this.field_37207;
    }

    @VisibleForTesting
    public void addDebugInfo(StringBuilder info) {
        info.append("NormalNoise {");
        info.append("first: ");
        this.firstSampler.addDebugInfo(info);
        info.append(", second: ");
        this.secondSampler.addDebugInfo(info);
        info.append("}");
    }

    public record NoiseParameters(int firstOctave, DoubleList amplitudes) {
        public static final Codec<NoiseParameters> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("firstOctave")).forGetter(NoiseParameters::firstOctave), ((MapCodec)Codec.DOUBLE.listOf().fieldOf("amplitudes")).forGetter(NoiseParameters::amplitudes)).apply((Applicative<NoiseParameters, ?>)instance, NoiseParameters::new));
        public static final Codec<RegistryEntry<NoiseParameters>> REGISTRY_ENTRY_CODEC = RegistryElementCodec.of(Registry.NOISE_KEY, CODEC);

        public NoiseParameters(int firstOctave, List<Double> amplitudes) {
            this(firstOctave, new DoubleArrayList(amplitudes));
        }

        public NoiseParameters(int firstOctave, double firstAmplitude, double ... amplitudes) {
            this(firstOctave, Util.make(new DoubleArrayList(amplitudes), doubleArrayList -> doubleArrayList.add(0, firstAmplitude)));
        }
    }
}

