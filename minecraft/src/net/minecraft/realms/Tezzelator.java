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
		t.getBufferBuilder().vertex(d, e, f);
		return this;
	}

	public void color(float f, float g, float h, float i) {
		t.getBufferBuilder().color(f, g, h, i);
	}

	public void tex2(short s, short t) {
		Tezzelator.t.getBufferBuilder().texture(s, t);
	}

	public void normal(float f, float g, float h) {
		t.getBufferBuilder().normal(f, g, h);
	}

	public void begin(int i, RealmsVertexFormat realmsVertexFormat) {
		t.getBufferBuilder().method_1328(i, realmsVertexFormat.getVertexFormat());
	}

	public void endVertex() {
		t.getBufferBuilder().next();
	}

	public void offset(double d, double e, double f) {
		t.getBufferBuilder().setOffset(d, e, f);
	}

	public RealmsBufferBuilder color(int i, int j, int k, int l) {
		return new RealmsBufferBuilder(t.getBufferBuilder().color(i, j, k, l));
	}

	public Tezzelator tex(double d, double e) {
		t.getBufferBuilder().texture(d, e);
		return this;
	}
}
