/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

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
    private static final IntFunction<HorseColor> BY_ID;
    private final int id;
    private final String name;

    private HorseColor(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public static HorseColor byId(int id) {
        return BY_ID.apply(id);
    }

    @Override
    public String asString() {
        return this.name;
    }

    static {
        CODEC = StringIdentifiable.createCodec(HorseColor::values);
        BY_ID = ValueLists.createIdToValueFunction(HorseColor::getId, HorseColor.values(), ValueLists.OutOfBoundsHandling.WRAP);
    }
}

