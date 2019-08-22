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

	public GlyphRenderer(Identifier identifier, float f, float g, float h, float i, float j, float k, float l, float m) {
		this.id = identifier;
		this.uMin = f;
		this.uMax = g;
		this.vMin = h;
		this.vMax = i;
		this.xMin = j;
		this.xMax = k;
		this.yMin = l;
		this.yMax = m;
	}

	public void draw(TextureManager textureManager, boolean bl, float f, float g, BufferBuilder bufferBuilder, float h, float i, float j, float k) {
		int l = 3;
		float m = f + this.xMin;
		float n = f + this.xMax;
		float o = this.yMin - 3.0F;
		float p = this.yMax - 3.0F;
		float q = g + o;
		float r = g + p;
		float s = bl ? 1.0F - 0.25F * o : 0.0F;
		float t = bl ? 1.0F - 0.25F * p : 0.0F;
		bufferBuilder.vertex((double)(m + s), (double)q, 0.0).texture((double)this.uMin, (double)this.vMin).color(h, i, j, k).next();
		bufferBuilder.vertex((double)(m + t), (double)r, 0.0).texture((double)this.uMin, (double)this.vMax).color(h, i, j, k).next();
		bufferBuilder.vertex((double)(n + t), (double)r, 0.0).texture((double)this.uMax, (double)this.vMax).color(h, i, j, k).next();
		bufferBuilder.vertex((double)(n + s), (double)q, 0.0).texture((double)this.uMax, (double)this.vMin).color(h, i, j, k).next();
	}

	@Nullable
	public Identifier getId() {
		return this.id;
	}
}
