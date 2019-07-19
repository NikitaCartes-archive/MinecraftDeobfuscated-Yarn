package net.minecraft.client.font;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GlyphRenderer {
	private final Identifier id;
	private final float uMin;
	private final float uMax;
	private final float vMin;
	private final float vMax;
	private final float xMin;
	private final float xMax;
	private final float yMin;
	private final float yMax;

	public GlyphRenderer(Identifier identifier, float uMin, float uMax, float vMin, float vMax, float xMin, float xMax, float yMin, float yMax) {
		this.id = identifier;
		this.uMin = uMin;
		this.uMax = uMax;
		this.vMin = vMin;
		this.vMax = vMax;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}

	public void draw(TextureManager textureManager, boolean italic, float x, float y, BufferBuilder buffer, float red, float green, float blue, float alpha) {
		int i = 3;
		float f = x + this.xMin;
		float g = x + this.xMax;
		float h = this.yMin - 3.0F;
		float j = this.yMax - 3.0F;
		float k = y + h;
		float l = y + j;
		float m = italic ? 1.0F - 0.25F * h : 0.0F;
		float n = italic ? 1.0F - 0.25F * j : 0.0F;
		buffer.vertex((double)(f + m), (double)k, 0.0).texture((double)this.uMin, (double)this.vMin).color(red, green, blue, alpha).next();
		buffer.vertex((double)(f + n), (double)l, 0.0).texture((double)this.uMin, (double)this.vMax).color(red, green, blue, alpha).next();
		buffer.vertex((double)(g + n), (double)l, 0.0).texture((double)this.uMax, (double)this.vMax).color(red, green, blue, alpha).next();
		buffer.vertex((double)(g + m), (double)k, 0.0).texture((double)this.uMax, (double)this.vMin).color(red, green, blue, alpha).next();
	}

	@Nullable
	public Identifier getId() {
		return this.id;
	}
}
