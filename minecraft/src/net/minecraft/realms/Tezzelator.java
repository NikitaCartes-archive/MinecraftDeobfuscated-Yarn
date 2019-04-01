package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_289;

@Environment(EnvType.CLIENT)
public class Tezzelator {
	public static final class_289 field_5668 = class_289.method_1348();
	public static final Tezzelator instance = new Tezzelator();

	public void end() {
		field_5668.method_1350();
	}

	public Tezzelator vertex(double d, double e, double f) {
		field_5668.method_1349().method_1315(d, e, f);
		return this;
	}

	public void color(float f, float g, float h, float i) {
		field_5668.method_1349().method_1336(f, g, h, i);
	}

	public void tex2(short s, short t) {
		field_5668.method_1349().method_1313(s, t);
	}

	public void normal(float f, float g, float h) {
		field_5668.method_1349().method_1318(f, g, h);
	}

	public void begin(int i, RealmsVertexFormat realmsVertexFormat) {
		field_5668.method_1349().method_1328(i, realmsVertexFormat.getVertexFormat());
	}

	public void endVertex() {
		field_5668.method_1349().method_1344();
	}

	public void offset(double d, double e, double f) {
		field_5668.method_1349().method_1331(d, e, f);
	}

	public RealmsBufferBuilder color(int i, int j, int k, int l) {
		return new RealmsBufferBuilder(field_5668.method_1349().method_1323(i, j, k, l));
	}

	public Tezzelator tex(double d, double e) {
		field_5668.method_1349().method_1312(d, e);
		return this;
	}
}
