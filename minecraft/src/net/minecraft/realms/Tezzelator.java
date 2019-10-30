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

	public void begin(int i, RealmsVertexFormat realmsVertexFormat) {
		t.getBuffer().begin(i, realmsVertexFormat.getVertexFormat());
	}

	public void endVertex() {
		t.getBuffer().next();
	}

	public Tezzelator color(int i, int j, int k, int l) {
		t.getBuffer().color(i, j, k, l);
		return this;
	}

	public Tezzelator tex(float f, float g) {
		t.getBuffer().texture(f, g);
		return this;
	}
}
