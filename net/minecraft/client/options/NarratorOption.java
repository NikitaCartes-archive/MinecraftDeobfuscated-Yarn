/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.options;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public enum NarratorOption {
    OFF(0, "options.narrator.off"),
    ALL(1, "options.narrator.all"),
    CHAT(2, "options.narrator.chat"),
    SYSTEM(3, "options.narrator.system");

    private static final NarratorOption[] VALUES;
    private final int id;
    private final String translationKey;

    private NarratorOption(int j, String string2) {
        this.id = j;
        this.translationKey = string2;
    }

    public int getId() {
        return this.id;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public static NarratorOption byId(int i) {
        return VALUES[MathHelper.floorMod(i, VALUES.length)];
    }

    static {
        VALUES = (NarratorOption[])Arrays.stream(NarratorOption.values()).sorted(Comparator.comparingInt(NarratorOption::getId)).toArray(NarratorOption[]::new);
    }
}

