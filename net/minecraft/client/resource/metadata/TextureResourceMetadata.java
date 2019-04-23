/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource.metadata;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.TextureResourceMetadataReader;

@Environment(value=EnvType.CLIENT)
public class TextureResourceMetadata {
    public static final TextureResourceMetadataReader READER = new TextureResourceMetadataReader();
    private final boolean blur;
    private final boolean clamp;

    public TextureResourceMetadata(boolean bl, boolean bl2) {
        this.blur = bl;
        this.clamp = bl2;
    }

    public boolean shouldBlur() {
        return this.blur;
    }

    public boolean shouldClamp() {
        return this.clamp;
    }
}

