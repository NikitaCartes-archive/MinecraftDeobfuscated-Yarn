/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class EmptyGlyphRenderer
extends GlyphRenderer {
    public EmptyGlyphRenderer() {
        super(new Identifier(""), 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void draw(TextureManager textureManager, boolean bl, float f, float g, BufferBuilder bufferBuilder, float h, float i, float j, float k) {
    }

    @Override
    @Nullable
    public Identifier getId() {
        return null;
    }
}

