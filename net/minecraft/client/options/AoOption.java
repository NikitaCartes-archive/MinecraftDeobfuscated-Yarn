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
public enum AoOption {
    OFF(0, "options.ao.off"),
    MIN(1, "options.ao.min"),
    MAX(2, "options.ao.max");

    private static final AoOption[] OPTIONS;
    private final int value;
    private final String translationKey;

    private AoOption(int j, String string2) {
        this.value = j;
        this.translationKey = string2;
    }

    public int getValue() {
        return this.value;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public static AoOption getOption(int i) {
        return OPTIONS[MathHelper.floorMod(i, OPTIONS.length)];
    }

    static {
        OPTIONS = (AoOption[])Arrays.stream(AoOption.values()).sorted(Comparator.comparingInt(AoOption::getValue)).toArray(AoOption[]::new);
    }
}

