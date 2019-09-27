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

	public void begin(int i, RealmsVertexFormat realmsVertexFormat) {
		t.getBufferBuilder().begin(i, realmsVertexFormat.getVertexFormat());
	}

	public void endVertex() {
		t.getBufferBuilder().next();
	}

	public Tezzelator color(int i, int j, int k, int l) {
		t.getBufferBuilder().color(i, j, k, l);
		return this;
	}

	public Tezzelator tex(float f, float g) {
		t.getBufferBuilder().texture(f, g);
		return this;
	}
}
