/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.options;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum CloudRenderMode {
    OFF(0, "options.off"),
    FAST(1, "options.clouds.fast"),
    FANCY(2, "options.clouds.fancy");

    private static final CloudRenderMode[] VALUES;
    private final int id;
    private final String translationKey;

    private CloudRenderMode(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    public int getId() {
        return this.id;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    static {
        VALUES = (CloudRenderMode[])Arrays.stream(CloudRenderMode.values()).sorted(Comparator.comparingInt(CloudRenderMode::getId)).toArray(CloudRenderMode[]::new);
    }
}

