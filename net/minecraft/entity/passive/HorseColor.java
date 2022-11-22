/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.StringIdentifiable;

public enum HorseColor implements StringIdentifiable
{
    WHITE(0, "white"),
    CREAMY(1, "creamy"),
    CHESTNUT(2, "chestnut"),
    BROWN(3, "brown"),
    BLACK(4, "black"),
    GRAY(5, "gray"),
    DARK_BROWN(6, "dark_brown");

    public static final Codec<HorseColor> CODEC;
    private static final HorseColor[] VALUES;
    private final int index;
    private final String name;

    private HorseColor(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return this.index;
    }

    public static HorseColor byIndex(int index) {
        return VALUES[index % VALUES.length];
    }

    @Override
    public String asString() {
        return this.name;
    }

    static {
        CODEC = StringIdentifiable.createCodec(HorseColor::values);
        VALUES = (HorseColor[])Arrays.stream(HorseColor.values()).sorted(Comparator.comparingInt(HorseColor::getIndex)).toArray(HorseColor[]::new);
    }
}

