/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import java.util.function.IntFunction;
import net.minecraft.util.function.ValueLists;

public enum HorseMarking {
    NONE(0),
    WHITE(1),
    WHITE_FIELD(2),
    WHITE_DOTS(3),
    BLACK_DOTS(4);

    private static final IntFunction<HorseMarking> BY_ID;
    private final int id;

    private HorseMarking(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static HorseMarking byIndex(int index) {
        return BY_ID.apply(index);
    }

    static {
        BY_ID = ValueLists.createIdToValueFunction(HorseMarking::getId, HorseMarking.values(), ValueLists.OutOfBoundsHandling.WRAP);
    }
}

