/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource.metadata;

import net.minecraft.network.chat.Component;
import net.minecraft.resource.metadata.PackResourceMetadataReader;

public class PackResourceMetadata {
    public static final PackResourceMetadataReader READER = new PackResourceMetadataReader();
    private final Component description;
    private final int packFormat;

    public PackResourceMetadata(Component component, int i) {
        this.description = component;
        this.packFormat = i;
    }

    public Component getDescription() {
        return this.description;
    }

    public int getPackFormat() {
        return this.packFormat;
    }
}

