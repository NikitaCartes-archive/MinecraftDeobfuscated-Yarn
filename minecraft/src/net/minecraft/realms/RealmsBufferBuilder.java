package net.minecraft.realms;

import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormat;

@Environment(EnvType.CLIENT)
public class RealmsBufferBuilder {
	private VertexBuffer field_5669;

	public RealmsBufferBuilder(VertexBuffer vertexBuffer) {
		this.field_5669 = vertexBuffer;
	}

	public RealmsBufferBuilder from(VertexBuffer vertexBuffer) {
		this.field_5669 = vertexBuffer;
		return this;
	}

	public void sortQuads(float f, float g, float h) {
		this.field_5669.sortQuads(f, g, h);
	}

	public void fixupQuadColor(int i) {
		this.field_5669.setQuadColor(i);
	}

	public ByteBuffer getBuffer() {
		return this.field_5669.getByteBuffer();
	}

	public void postNormal(float f, float g, float h) {
		this.field_5669.postNormal(f, g, h);
	}

	public int getDrawMode() {
		return this.field_5669.getDrawMode();
	}

	public void offset(double d, double e, double f) {
		this.field_5669.setOffset(d, e, f);
	}

	public void restoreState(VertexBuffer.State state) {
		this.field_5669.restoreState(state);
	}

	public void endVertex() {
		this.field_5669.next();
	}

	public RealmsBufferBuilder normal(float f, float g, float h) {
		return this.from(this.field_5669.normal(f, g, h));
	}

	public void end() {
		this.field_5669.end();
	}

	public void begin(int i, VertexFormat vertexFormat) {
		this.field_5669.begin(i, vertexFormat);
	}

	public RealmsBufferBuilder color(int i, int j, int k, int l) {
		return this.from(this.field_5669.color(i, j, k, l));
	}

	public void faceTex2(int i, int j, int k, int l) {
		this.field_5669.brightness(i, j, k, l);
	}

	public void postProcessFacePosition(double d, double e, double f) {
		this.field_5669.postPosition(d, e, f);
	}

	public void fixupVertexColor(float f, float g, float h, int i) {
		this.field_5669.setColor(f, g, h, i);
	}

	public RealmsBufferBuilder color(float f, float g, float h, float i) {
		return this.from(this.field_5669.color(f, g, h, i));
	}

	public RealmsVertexFormat getVertexFormat() {
		return new RealmsVertexFormat(this.field_5669.getVertexFormat());
	}

	public void faceTint(float f, float g, float h, int i) {
		this.field_5669.multiplyColor(f, g, h, i);
	}

	public RealmsBufferBuilder tex2(int i, int j) {
		return this.from(this.field_5669.texture(i, j));
	}

	public void putBulkData(int[] is) {
		this.field_5669.putVertexData(is);
	}

	public RealmsBufferBuilder tex(double d, double e) {
		return this.from(this.field_5669.texture(d, e));
	}

	public int getVertexCount() {
		return this.field_5669.getVertexCount();
	}

	public void clear() {
		this.field_5669.clear();
	}

	public RealmsBufferBuilder vertex(double d, double e, double f) {
		return this.from(this.field_5669.vertex(d, e, f));
	}

	public void fixupQuadColor(float f, float g, float h) {
		this.field_5669.setQuadColor(f, g, h);
	}

	public void noColor() {
		this.field_5669.disableColor();
	}
}
