/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk.placement;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.random.Random;

public enum SpreadType implements StringIdentifiable
{
    LINEAR("linear"),
    TRIANGULAR("triangular");

    public static final Codec<SpreadType> CODEC;
    private final String name;

    private SpreadType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public int get(Random random, int bound) {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case LINEAR -> random.nextInt(bound);
            case TRIANGULAR -> (random.nextInt(bound) + random.nextInt(bound)) / 2;
        };
    }

    static {
        CODEC = StringIdentifiable.createCodec(SpreadType::values);
    }
}

