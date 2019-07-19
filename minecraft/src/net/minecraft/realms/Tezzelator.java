package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Tessellator;

@Environment(EnvType.CLIENT)
public class Tezzelator {
	public static final Tessellator t = Tessellator.getInstance();
	public static final Tezzelator instance = new Tezzelator();

	public void end() {
		t.draw();
	}

	public Tezzelator vertex(double d, double e, double f) {
		t.getBuffer().vertex(d, e, f);
		return this;
	}

	public void color(float f, float g, float h, float i) {
		t.getBuffer().color(f, g, h, i);
	}

	public void tex2(short s, short t) {
		Tezzelator.t.getBuffer().texture(s, t);
	}

	public void normal(float f, float g, float h) {
		t.getBuffer().normal(f, g, h);
	}

	public void begin(int i, RealmsVertexFormat realmsVertexFormat) {
		t.getBuffer().begin(i, realmsVertexFormat.getVertexFormat());
	}

	public void endVertex() {
		t.getBuffer().next();
	}

	public void offset(double d, double e, double f) {
		t.getBuffer().setOffset(d, e, f);
	}

	public RealmsBufferBuilder color(int i, int j, int k, int l) {
		return new RealmsBufferBuilder(t.getBuffer().color(i, j, k, l));
	}

	public Tezzelator tex(double d, double e) {
		t.getBuffer().texture(d, e);
		return this;
	}
}
