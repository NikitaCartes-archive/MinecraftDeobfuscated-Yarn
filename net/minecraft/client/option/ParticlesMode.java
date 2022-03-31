/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.option;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public enum ParticlesMode implements TranslatableOption
{
    ALL(0, "options.particles.all"),
    DECREASED(1, "options.particles.decreased"),
    MINIMAL(2, "options.particles.minimal");

    private static final ParticlesMode[] VALUES;
    private final int id;
    private final String translationKey;

    private ParticlesMode(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public static ParticlesMode byId(int id) {
        return VALUES[MathHelper.floorMod(id, VALUES.length)];
    }

    static {
        VALUES = (ParticlesMode[])Arrays.stream(ParticlesMode.values()).sorted(Comparator.comparingInt(ParticlesMode::getId)).toArray(ParticlesMode[]::new);
    }
}

