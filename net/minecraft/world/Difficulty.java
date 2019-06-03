/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

public enum Difficulty {
    PEACEFUL(0, "peaceful"),
    EASY(1, "easy"),
    NORMAL(2, "normal"),
    HARD(3, "hard");

    private static final Difficulty[] BY_NAME;
    private final int id;
    private final String name;

    private Difficulty(int j, String string2) {
        this.id = j;
        this.name = string2;
    }

    public int getId() {
        return this.id;
    }

    public Text getTranslatableName() {
        return new TranslatableText("options.difficulty." + this.name, new Object[0]);
    }

    public static Difficulty byOrdinal(int i) {
        return BY_NAME[i % BY_NAME.length];
    }

    @Nullable
    public static Difficulty byName(String string) {
        for (Difficulty difficulty : Difficulty.values()) {
            if (!difficulty.name.equals(string)) continue;
            return difficulty;
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    static {
        BY_NAME = (Difficulty[])Arrays.stream(Difficulty.values()).sorted(Comparator.comparingInt(Difficulty::getId)).toArray(Difficulty[]::new);
    }
}

