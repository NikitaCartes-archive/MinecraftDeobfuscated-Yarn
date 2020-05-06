/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class LocalDifficulty {
    private final Difficulty globalDifficulty;
    private final float localDifficulty;

    public LocalDifficulty(Difficulty difficulty, long timeOfDay, long inhabitedTime, float moonSize) {
        this.globalDifficulty = difficulty;
        this.localDifficulty = this.setLocalDifficulty(difficulty, timeOfDay, inhabitedTime, moonSize);
    }

    public Difficulty getGlobalDifficulty() {
        return this.globalDifficulty;
    }

    public float getLocalDifficulty() {
        return this.localDifficulty;
    }

    public boolean isHarderThan(float difficulty) {
        return this.localDifficulty > difficulty;
    }

    public float getClampedLocalDifficulty() {
        if (this.localDifficulty < 2.0f) {
            return 0.0f;
        }
        if (this.localDifficulty > 4.0f) {
            return 1.0f;
        }
        return (this.localDifficulty - 2.0f) / 2.0f;
    }

    private float setLocalDifficulty(Difficulty difficulty, long timeOfDay, long inhabitedTime, float moonSize) {
        if (difficulty == Difficulty.PEACEFUL) {
            return 0.0f;
        }
        boolean bl = difficulty == Difficulty.HARD;
        float f = 0.75f;
        float g = MathHelper.clamp(((float)timeOfDay + -72000.0f) / 1440000.0f, 0.0f, 1.0f) * 0.25f;
        f += g;
        float h = 0.0f;
        h += MathHelper.clamp((float)inhabitedTime / 3600000.0f, 0.0f, 1.0f) * (bl ? 1.0f : 0.75f);
        h += MathHelper.clamp(moonSize * 0.25f, 0.0f, g);
        if (difficulty == Difficulty.EASY) {
            h *= 0.5f;
        }
        return (float)difficulty.getId() * (f += h);
    }
}

