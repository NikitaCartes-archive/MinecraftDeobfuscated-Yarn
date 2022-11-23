/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.option;

import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

@Environment(value=EnvType.CLIENT)
public enum AoMode implements TranslatableOption
{
    OFF(0, "options.ao.off"),
    MIN(1, "options.ao.min"),
    MAX(2, "options.ao.max");

    private static final IntFunction<AoMode> BY_ID;
    private final int id;
    private final String translationKey;

    private AoMode(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    public static AoMode byId(int id) {
        return BY_ID.apply(id);
    }

    static {
        BY_ID = ValueLists.createIdToValueFunction(AoMode::getId, AoMode.values(), ValueLists.OutOfBoundsHandling.WRAP);
    }
}

