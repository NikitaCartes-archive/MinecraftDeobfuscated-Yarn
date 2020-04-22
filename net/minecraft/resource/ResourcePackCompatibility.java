/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum ResourcePackCompatibility {
    TOO_OLD("old"),
    TOO_NEW("new"),
    COMPATIBLE("compatible");

    private final Text notification;
    private final Text confirmMessage;

    private ResourcePackCompatibility(String translationSuffix) {
        this.notification = new TranslatableText("resourcePack.incompatible." + translationSuffix);
        this.confirmMessage = new TranslatableText("resourcePack.incompatible.confirm." + translationSuffix);
    }

    public boolean isCompatible() {
        return this == COMPATIBLE;
    }

    public static ResourcePackCompatibility from(int packVersion) {
        if (packVersion < SharedConstants.getGameVersion().getPackVersion()) {
            return TOO_OLD;
        }
        if (packVersion > SharedConstants.getGameVersion().getPackVersion()) {
            return TOO_NEW;
        }
        return COMPATIBLE;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getNotification() {
        return this.notification;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getConfirmMessage() {
        return this.confirmMessage;
    }
}

