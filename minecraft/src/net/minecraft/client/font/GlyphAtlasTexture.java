package net.minecraft.client.font;

import com.mojang.blaze3d.platform.TextureUtil;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GlyphAtlasTexture extends AbstractTexture {
	private static final int field_32227 = 256;
	private final Identifier id;
	private final RenderLayer textLayer;
	private final RenderLayer seeThroughTextLayer;
	private final RenderLayer field_33998;
	private final boolean hasColor;
	private final GlyphAtlasTexture.Slot rootSlot;

	public GlyphAtlasTexture(Identifier id, boolean hasColor) {
		this.id = id;
		this.hasColor = hasColor;
		this.rootSlot = new GlyphAtlasTexture.Slot(0, 0, 256, 256);
		TextureUtil.prepareImage(hasColor ? NativeImage.GLFormat.ABGR : NativeImage.GLFormat.RED, this.getGlId(), 256, 256);
		this.textLayer = hasColor ? RenderLayer.getText(id) : RenderLayer.method_36434(id);
		this.seeThroughTextLayer = hasColor ? RenderLayer.getTextSeeThrough(id) : RenderLayer.method_36435(id);
		this.field_33998 = hasColor ? RenderLayer.method_37345(id) : RenderLayer.method_37346(id);
	}

	@Override
	public void load(ResourceManager manager) {
	}

	@Override
	public void close() {
		this.clearGlId();
	}

	@Nullable
	public GlyphRenderer getGlyphRenderer(RenderableGlyph glyph) {
		if (glyph.hasColor() != this.hasColor) {
			return null;
		} else {
			GlyphAtlasTexture.Slot slot = this.rootSlot.findSlotFor(glyph);
			if (slot != null) {
				this.bindTexture();
				glyph.upload(slot.x, slot.y);
				float f = 256.0F;
				float g = 256.0F;
				float h = 0.01F;
				return new GlyphRenderer(
					this.textLayer,
					this.seeThroughTextLayer,
					this.field_33998,
					((float)slot.x + 0.01F) / 256.0F,
					((float)slot.x - 0.01F + (float)glyph.getWidth()) / 256.0F,
					((float)slot.y + 0.01F) / 256.0F,
					((float)slot.y - 0.01F + (float)glyph.getHeight()) / 256.0F,
					glyph.getXMin(),
					glyph.getXMax(),
					glyph.getYMin(),
					glyph.getYMax()
				);
			} else {
				return null;
			}
		}
	}

	public Identifier getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	static class Slot {
		final int x;
		final int y;
		private final int width;
		private final int height;
		private GlyphAtlasTexture.Slot subSlot1;
		private GlyphAtlasTexture.Slot subSlot2;
		private boolean occupied;

		Slot(int i, int j, int k, int l) {
			this.x = i;
			this.y = j;
			this.width = k;
			this.height = l;
		}

		@Nullable
		GlyphAtlasTexture.Slot findSlotFor(RenderableGlyph glyph) {
			if (this.subSlot1 != null && this.subSlot2 != null) {
				GlyphAtlasTexture.Slot slot = this.subSlot1.findSlotFor(glyph);
				if (slot == null) {
					slot = this.subSlot2.findSlotFor(glyph);
				}

				return slot;
			} else if (this.occupied) {
				return null;
			} else {
				int i = glyph.getWidth();
				int j = glyph.getHeight();
				if (i > this.width || j > this.height) {
					return null;
				} else if (i == this.width && j == this.height) {
					this.occupied = true;
					return this;
				} else {
					int k = this.width - i;
					int l = this.height - j;
					if (k > l) {
						this.subSlot1 = new GlyphAtlasTexture.Slot(this.x, this.y, i, this.height);
						this.subSlot2 = new GlyphAtlasTexture.Slot(this.x + i + 1, this.y, this.width - i - 1, this.height);
					} else {
						this.subSlot1 = new GlyphAtlasTexture.Slot(this.x, this.y, this.width, j);
						this.subSlot2 = new GlyphAtlasTexture.Slot(this.x, this.y + j + 1, this.width, this.height - j - 1);
					}

					return this.subSlot1.findSlotFor(glyph);
				}
			}
		}
	}
}
