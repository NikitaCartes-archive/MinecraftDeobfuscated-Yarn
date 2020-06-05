package net.minecraft.client.font;

import java.io.Closeable;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GlyphAtlasTexture extends AbstractTexture implements Closeable {
	private final Identifier id;
	private final RenderLayer field_21690;
	private final RenderLayer field_21691;
	private final boolean hasColor;
	private final GlyphAtlasTexture.Slot rootSlot;

	public GlyphAtlasTexture(Identifier id, boolean hasColor) {
		this.id = id;
		this.hasColor = hasColor;
		this.rootSlot = new GlyphAtlasTexture.Slot(0, 0, 256, 256);
		TextureUtil.allocate(hasColor ? NativeImage.GLFormat.ABGR : NativeImage.GLFormat.INTENSITY, this.getGlId(), 256, 256);
		this.field_21690 = RenderLayer.getText(id);
		this.field_21691 = RenderLayer.getTextSeeThrough(id);
	}

	@Override
	public void load(ResourceManager manager) {
	}

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
					this.field_21690,
					this.field_21691,
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
		private final int x;
		private final int y;
		private final int width;
		private final int height;
		private GlyphAtlasTexture.Slot subSlot1;
		private GlyphAtlasTexture.Slot subSlot2;
		private boolean occupied;

		private Slot(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
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
