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

    public TextureResourceMetadata(boolean blur, boolean clamp) {
        this.blur = blur;
        this.clamp = clamp;
    }

    public boolean shouldBlur() {
        return this.blur;
    }

    public boolean shouldClamp() {
        return this.clamp;
    }
}

