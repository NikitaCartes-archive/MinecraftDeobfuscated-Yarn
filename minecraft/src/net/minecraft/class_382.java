package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_382 {
	private final Identifier field_2277;
	private final float uMin;
	private final float uMax;
	private final float vMin;
	private final float vMax;
	private final float field_2272;
	private final float field_2280;
	private final float field_2279;
	private final float field_2278;

	public class_382(Identifier identifier, float f, float g, float h, float i, float j, float k, float l, float m) {
		this.field_2277 = identifier;
		this.uMin = f;
		this.uMax = g;
		this.vMin = h;
		this.vMax = i;
		this.field_2272 = j;
		this.field_2280 = k;
		this.field_2279 = l;
		this.field_2278 = m;
	}

	public void method_2025(TextureManager textureManager, boolean bl, float f, float g, VertexBuffer vertexBuffer, float h, float i, float j, float k) {
		int l = 3;
		float m = f + this.field_2272;
		float n = f + this.field_2280;
		float o = this.field_2279 - 3.0F;
		float p = this.field_2278 - 3.0F;
		float q = g + o;
		float r = g + p;
		float s = bl ? 1.0F - 0.25F * o : 0.0F;
		float t = bl ? 1.0F - 0.25F * p : 0.0F;
		vertexBuffer.vertex((double)(m + s), (double)q, 0.0).texture((double)this.uMin, (double)this.vMin).color(h, i, j, k).next();
		vertexBuffer.vertex((double)(m + t), (double)r, 0.0).texture((double)this.uMin, (double)this.vMax).color(h, i, j, k).next();
		vertexBuffer.vertex((double)(n + t), (double)r, 0.0).texture((double)this.uMax, (double)this.vMax).color(h, i, j, k).next();
		vertexBuffer.vertex((double)(n + s), (double)q, 0.0).texture((double)this.uMax, (double)this.vMin).color(h, i, j, k).next();
	}

	@Nullable
	public Identifier method_2026() {
		return this.field_2277;
	}
}
