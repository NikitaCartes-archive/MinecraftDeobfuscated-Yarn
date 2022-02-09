/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk.placement;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.gen.random.AbstractRandom;

public enum SpreadType implements StringIdentifiable
{
    LINEAR("linear"),
    TRIANGULAR("triangular");

    private static final SpreadType[] VALUES;
    public static final Codec<SpreadType> CODEC;
    private final String name;

    private SpreadType(String name) {
        this.name = name;
    }

    public static SpreadType byName(String name) {
        for (SpreadType spreadType : VALUES) {
            if (!spreadType.asString().equals(name)) continue;
            return spreadType;
        }
        throw new IllegalArgumentException("Unknown Random Spread type: " + name);
    }

    @Override
    public String asString() {
        return this.name;
    }

    public int get(AbstractRandom random, int bound) {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case LINEAR -> random.nextInt(bound);
            case TRIANGULAR -> (random.nextInt(bound) + random.nextInt(bound)) / 2;
        };
    }

    static {
        VALUES = SpreadType.values();
        CODEC = StringIdentifiable.createCodec(() -> VALUES, SpreadType::byName);
    }
}

