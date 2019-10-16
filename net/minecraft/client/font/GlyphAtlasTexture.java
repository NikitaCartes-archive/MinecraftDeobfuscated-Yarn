/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import java.io.Closeable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GlyphAtlasTexture
extends AbstractTexture
implements Closeable {
    private final Identifier id;
    private final boolean hasColor;
    private final Slot rootSlot;

    public GlyphAtlasTexture(Identifier identifier, boolean bl) {
        this.id = identifier;
        this.hasColor = bl;
        this.rootSlot = new Slot(0, 0, 256, 256);
        TextureUtil.prepareImage(bl ? NativeImage.GLFormat.RGBA : NativeImage.GLFormat.INTENSITY, this.getGlId(), 256, 256);
    }

    @Override
    public void load(ResourceManager resourceManager) {
    }

    @Override
    public void close() {
        this.clearGlId();
    }

    @Nullable
    public GlyphRenderer getGlyphRenderer(RenderableGlyph renderableGlyph) {
        if (renderableGlyph.hasColor() != this.hasColor) {
            return null;
        }
        Slot slot = this.rootSlot.findSlotFor(renderableGlyph);
        if (slot != null) {
            this.bindTexture();
            renderableGlyph.upload(slot.x, slot.y);
            float f = 256.0f;
            float g = 256.0f;
            float h = 0.01f;
            return new GlyphRenderer(this.id, ((float)slot.x + 0.01f) / 256.0f, ((float)slot.x - 0.01f + (float)renderableGlyph.getWidth()) / 256.0f, ((float)slot.y + 0.01f) / 256.0f, ((float)slot.y - 0.01f + (float)renderableGlyph.getHeight()) / 256.0f, renderableGlyph.getXMin(), renderableGlyph.getXMax(), renderableGlyph.getYMin(), renderableGlyph.getYMax());
        }
        return null;
    }

    public Identifier getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    static class Slot {
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private Slot subSlot1;
        private Slot subSlot2;
        private boolean isOccupied;

        private Slot(int i, int j, int k, int l) {
            this.x = i;
            this.y = j;
            this.width = k;
            this.height = l;
        }

        @Nullable
        Slot findSlotFor(RenderableGlyph renderableGlyph) {
            if (this.subSlot1 != null && this.subSlot2 != null) {
                Slot slot = this.subSlot1.findSlotFor(renderableGlyph);
                if (slot == null) {
                    slot = this.subSlot2.findSlotFor(renderableGlyph);
                }
                return slot;
            }
            if (this.isOccupied) {
                return null;
            }
            int i = renderableGlyph.getWidth();
            int j = renderableGlyph.getHeight();
            if (i > this.width || j > this.height) {
                return null;
            }
            if (i == this.width && j == this.height) {
                this.isOccupied = true;
                return this;
            }
            int k = this.width - i;
            int l = this.height - j;
            if (k > l) {
                this.subSlot1 = new Slot(this.x, this.y, i, this.height);
                this.subSlot2 = new Slot(this.x + i + 1, this.y, this.width - i - 1, this.height);
            } else {
                this.subSlot1 = new Slot(this.x, this.y, this.width, j);
                this.subSlot2 = new Slot(this.x, this.y + j + 1, this.width, this.height - j - 1);
            }
            return this.subSlot1.findSlotFor(renderableGlyph);
        }
    }
}

