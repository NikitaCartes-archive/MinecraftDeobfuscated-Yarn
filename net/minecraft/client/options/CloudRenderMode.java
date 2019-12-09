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
public enum CloudRenderMode {
    OFF(0, "options.off"),
    FAST(1, "options.clouds.fast"),
    FANCY(2, "options.clouds.fancy");

    private static final CloudRenderMode[] RENDER_MODES;
    private final int value;
    private final String translationKey;

    private CloudRenderMode(int value, String translationKey) {
        this.value = value;
        this.translationKey = translationKey;
    }

    public int getValue() {
        return this.value;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public static CloudRenderMode getOption(int id) {
        return RENDER_MODES[MathHelper.floorMod(id, RENDER_MODES.length)];
    }

    static {
        RENDER_MODES = (CloudRenderMode[])Arrays.stream(CloudRenderMode.values()).sorted(Comparator.comparingInt(CloudRenderMode::getValue)).toArray(CloudRenderMode[]::new);
    }
}

