/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.Nullable;

public enum Difficulty {
    PEACEFUL(0, "peaceful"),
    EASY(1, "easy"),
    NORMAL(2, "normal"),
    HARD(3, "hard");

    private static final Difficulty[] DIFFICULTIES;
    private final int id;
    private final String translationKey;

    private Difficulty(int j, String string2) {
        this.id = j;
        this.translationKey = string2;
    }

    public int getId() {
        return this.id;
    }

    public Component toTextComponent() {
        return new TranslatableComponent("options.difficulty." + this.translationKey, new Object[0]);
    }

    public static Difficulty getDifficulty(int i) {
        return DIFFICULTIES[i % DIFFICULTIES.length];
    }

    @Nullable
    public static Difficulty getDifficulty(String string) {
        for (Difficulty difficulty : Difficulty.values()) {
            if (!difficulty.translationKey.equals(string)) continue;
            return difficulty;
        }
        return null;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    static {
        DIFFICULTIES = (Difficulty[])Arrays.stream(Difficulty.values()).sorted(Comparator.comparingInt(Difficulty::getId)).toArray(Difficulty[]::new);
    }
}

