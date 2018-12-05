package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Tessellator;

@Environment(EnvType.CLIENT)
public class Tezzelator {
	public static Tessellator field_5668 = Tessellator.getInstance();
	public static final Tezzelator instance = new Tezzelator();

	public void end() {
		field_5668.draw();
	}

	public Tezzelator vertex(double d, double e, double f) {
		field_5668.getVertexBuffer().vertex(d, e, f);
		return this;
	}

	public void color(float f, float g, float h, float i) {
		field_5668.getVertexBuffer().color(f, g, h, i);
	}

	public void tex2(short s, short t) {
		field_5668.getVertexBuffer().texture(s, t);
	}

	public void normal(float f, float g, float h) {
		field_5668.getVertexBuffer().normal(f, g, h);
	}

	public void begin(int i, RealmsVertexFormat realmsVertexFormat) {
		field_5668.getVertexBuffer().begin(i, realmsVertexFormat.getVertexFormat());
	}

	public void endVertex() {
		field_5668.getVertexBuffer().next();
	}

	public void offset(double d, double e, double f) {
		field_5668.getVertexBuffer().setOffset(d, e, f);
	}

	public RealmsBufferBuilder color(int i, int j, int k, int l) {
		return new RealmsBufferBuilder(field_5668.getVertexBuffer().color(i, j, k, l));
	}

	public Tezzelator tex(double d, double e) {
		field_5668.getVertexBuffer().texture(d, e);
		return this;
	}
}
