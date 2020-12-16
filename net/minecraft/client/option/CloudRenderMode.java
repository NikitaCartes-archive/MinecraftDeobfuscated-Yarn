/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum CloudRenderMode {
    OFF("options.off"),
    FAST("options.clouds.fast"),
    FANCY("options.clouds.fancy");

    private final String translationKey;

    private CloudRenderMode(String string2) {
        this.translationKey = string2;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }
}

