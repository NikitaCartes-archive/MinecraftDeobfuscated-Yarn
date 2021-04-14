/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.intprovider;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;

public class ConstantIntProvider
extends IntProvider {
    public static final ConstantIntProvider ZERO = new ConstantIntProvider(0);
    public static final Codec<ConstantIntProvider> CODEC = Codec.either(Codec.INT, RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("value")).forGetter(provider -> provider.value)).apply((Applicative<ConstantIntProvider, ?>)instance, ConstantIntProvider::new))).xmap(either -> either.map(ConstantIntProvider::create, provider -> provider), provider -> Either.left(provider.value));
    private final int value;

    public static ConstantIntProvider create(int value) {
        if (value == 0) {
            return ZERO;
        }
        return new ConstantIntProvider(value);
    }

    private ConstantIntProvider(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public int get(Random random) {
        return this.value;
    }

    @Override
    public int getMin() {
        return this.value;
    }

    @Override
    public int getMax() {
        return this.value;
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.CONSTANT;
    }

    public String toString() {
        return Integer.toString(this.value);
    }
}

