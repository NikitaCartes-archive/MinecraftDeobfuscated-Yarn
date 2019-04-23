/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum ResourcePackCompatibility {
    TOO_OLD("old"),
    TOO_NEW("new"),
    COMPATIBLE("compatible");

    private final Component notification;
    private final Component confirmMessage;

    private ResourcePackCompatibility(String string2) {
        this.notification = new TranslatableComponent("resourcePack.incompatible." + string2, new Object[0]);
        this.confirmMessage = new TranslatableComponent("resourcePack.incompatible.confirm." + string2, new Object[0]);
    }

    public boolean isCompatible() {
        return this == COMPATIBLE;
    }

    public static ResourcePackCompatibility from(int i) {
        if (i < SharedConstants.getGameVersion().getPackVersion()) {
            return TOO_OLD;
        }
        if (i > SharedConstants.getGameVersion().getPackVersion()) {
            return TOO_NEW;
        }
        return COMPATIBLE;
    }

    @Environment(value=EnvType.CLIENT)
    public Component getNotification() {
        return this.notification;
    }

    @Environment(value=EnvType.CLIENT)
    public Component getConfirmMessage() {
        return this.confirmMessage;
    }
}

