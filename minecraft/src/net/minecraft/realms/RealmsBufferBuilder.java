package net.minecraft.realms;

import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;

@Environment(EnvType.CLIENT)
public class RealmsBufferBuilder {
	private BufferBuilder b;

	public RealmsBufferBuilder(BufferBuilder bufferBuilder) {
		this.b = bufferBuilder;
	}

	public RealmsBufferBuilder from(BufferBuilder bufferBuilder) {
		this.b = bufferBuilder;
		return this;
	}

	public void sortQuads(float f, float g, float h) {
		this.b.sortQuads(f, g, h);
	}

	public void fixupQuadColor(int i) {
		this.b.setQuadColor(i);
	}

	public ByteBuffer getBuffer() {
		return this.b.getByteBuffer();
	}

	public void postNormal(float f, float g, float h) {
		this.b.postNormal(f, g, h);
	}

	public int getDrawMode() {
		return this.b.getDrawMode();
	}

	public void offset(double d, double e, double f) {
		this.b.setOffset(d, e, f);
	}

	public void restoreState(BufferBuilder.State state) {
		this.b.restoreState(state);
	}

	public void endVertex() {
		this.b.next();
	}

	public RealmsBufferBuilder normal(float f, float g, float h) {
		return this.from(this.b.normal(f, g, h));
	}

	public void end() {
		this.b.end();
	}

	public void begin(int i, VertexFormat vertexFormat) {
		this.b.begin(i, vertexFormat);
	}

	public RealmsBufferBuilder color(int i, int j, int k, int l) {
		return this.from(this.b.color(i, j, k, l));
	}

	public void faceTex2(int i, int j, int k, int l) {
		this.b.brightness(i, j, k, l);
	}

	public void postProcessFacePosition(double d, double e, double f) {
		this.b.postPosition(d, e, f);
	}

	public void fixupVertexColor(float f, float g, float h, int i) {
		this.b.setColor(f, g, h, i);
	}

	public RealmsBufferBuilder color(float f, float g, float h, float i) {
		return this.from(this.b.color(f, g, h, i));
	}

	public RealmsVertexFormat getVertexFormat() {
		return new RealmsVertexFormat(this.b.getVertexFormat());
	}

	public void faceTint(float f, float g, float h, int i) {
		this.b.multiplyColor(f, g, h, i);
	}

	public RealmsBufferBuilder tex2(int i, int j) {
		return this.from(this.b.texture(i, j));
	}

	public void putBulkData(int[] is) {
		this.b.putVertexData(is);
	}

	public RealmsBufferBuilder tex(double d, double e) {
		return this.from(this.b.texture(d, e));
	}

	public int getVertexCount() {
		return this.b.getVertexCount();
	}

	public void clear() {
		this.b.clear();
	}

	public RealmsBufferBuilder vertex(double d, double e, double f) {
		return this.from(this.b.vertex(d, e, f));
	}

	public void fixupQuadColor(float f, float g, float h) {
		this.b.setQuadColor(f, g, h);
	}

	public void noColor() {
		this.b.disableColor();
	}
}
