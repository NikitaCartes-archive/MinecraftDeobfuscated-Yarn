package net.minecraft.realms;

import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_287;
import net.minecraft.class_293;

@Environment(EnvType.CLIENT)
public class RealmsBufferBuilder {
	private class_287 field_23661;

	public RealmsBufferBuilder(class_287 arg) {
		this.field_23661 = arg;
	}

	public RealmsBufferBuilder from(class_287 arg) {
		this.field_23661 = arg;
		return this;
	}

	public void sortQuads(float f, float g, float h) {
		this.field_23661.method_1341(f, g, h);
	}

	public void fixupQuadColor(int i) {
		this.field_23661.method_1332(i);
	}

	public ByteBuffer getBuffer() {
		return this.field_23661.method_1342();
	}

	public void postNormal(float f, float g, float h) {
		this.field_23661.method_1320(f, g, h);
	}

	public int getDrawMode() {
		return this.field_23661.method_1338();
	}

	public void offset(double d, double e, double f) {
		this.field_23661.method_1331(d, e, f);
	}

	public void restoreState(class_287.class_288 arg) {
		this.field_23661.method_1324(arg);
	}

	public void endVertex() {
		this.field_23661.method_1344();
	}

	public RealmsBufferBuilder normal(float f, float g, float h) {
		return this.from(this.field_23661.method_1318(f, g, h));
	}

	public void end() {
		this.field_23661.method_1326();
	}

	public void begin(int i, class_293 arg) {
		this.field_23661.method_1328(i, arg);
	}

	public RealmsBufferBuilder color(int i, int j, int k, int l) {
		return this.from(this.field_23661.method_1323(i, j, k, l));
	}

	public void faceTex2(int i, int j, int k, int l) {
		this.field_23661.method_1339(i, j, k, l);
	}

	public void postProcessFacePosition(double d, double e, double f) {
		this.field_23661.method_1322(d, e, f);
	}

	public void fixupVertexColor(float f, float g, float h, int i) {
		this.field_23661.method_1314(f, g, h, i);
	}

	public RealmsBufferBuilder color(float f, float g, float h, float i) {
		return this.from(this.field_23661.method_1336(f, g, h, i));
	}

	public RealmsVertexFormat getVertexFormat() {
		return new RealmsVertexFormat(this.field_23661.method_1311());
	}

	public void faceTint(float f, float g, float h, int i) {
		this.field_23661.method_1317(f, g, h, i);
	}

	public RealmsBufferBuilder tex2(int i, int j) {
		return this.from(this.field_23661.method_1313(i, j));
	}

	public void putBulkData(int[] is) {
		this.field_23661.method_1333(is);
	}

	public RealmsBufferBuilder tex(double d, double e) {
		return this.from(this.field_23661.method_1312(d, e));
	}

	public int getVertexCount() {
		return this.field_23661.method_1337();
	}

	public void clear() {
		this.field_23661.method_1343();
	}

	public RealmsBufferBuilder vertex(double d, double e, double f) {
		return this.from(this.field_23661.method_1315(d, e, f));
	}

	public void fixupQuadColor(float f, float g, float h) {
		this.field_23661.method_1330(f, g, h);
	}

	public void noColor() {
		this.field_23661.method_1327();
	}
}
