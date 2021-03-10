package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public interface BufferVertexConsumer extends VertexConsumer {
	VertexFormatElement getCurrentElement();

	void nextElement();

	void putByte(int index, byte value);

	void putShort(int index, short value);

	void putFloat(int index, float value);

	@Override
	default VertexConsumer vertex(double x, double y, double z) {
		if (this.getCurrentElement().getType() != VertexFormatElement.Type.POSITION) {
			return this;
		} else if (this.getCurrentElement().getFormat() == VertexFormatElement.Format.FLOAT && this.getCurrentElement().method_34451() == 3) {
			this.putFloat(0, (float)x);
			this.putFloat(4, (float)y);
			this.putFloat(8, (float)z);
			this.nextElement();
			return this;
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	default VertexConsumer color(int red, int green, int blue, int alpha) {
		VertexFormatElement vertexFormatElement = this.getCurrentElement();
		if (vertexFormatElement.getType() != VertexFormatElement.Type.COLOR) {
			return this;
		} else if (vertexFormatElement.getFormat() == VertexFormatElement.Format.UBYTE && vertexFormatElement.method_34451() == 4) {
			this.putByte(0, (byte)red);
			this.putByte(1, (byte)green);
			this.putByte(2, (byte)blue);
			this.putByte(3, (byte)alpha);
			this.nextElement();
			return this;
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	default VertexConsumer texture(float u, float v) {
		VertexFormatElement vertexFormatElement = this.getCurrentElement();
		if (vertexFormatElement.getType() == VertexFormatElement.Type.UV && vertexFormatElement.getIndex() == 0) {
			if (vertexFormatElement.getFormat() == VertexFormatElement.Format.FLOAT && vertexFormatElement.method_34451() == 2) {
				this.putFloat(0, u);
				this.putFloat(4, v);
				this.nextElement();
				return this;
			} else {
				throw new IllegalStateException();
			}
		} else {
			return this;
		}
	}

	@Override
	default VertexConsumer overlay(int u, int v) {
		return this.texture((short)u, (short)v, 1);
	}

	@Override
	default VertexConsumer light(int u, int v) {
		return this.texture((short)u, (short)v, 2);
	}

	default VertexConsumer texture(short u, short v, int index) {
		VertexFormatElement vertexFormatElement = this.getCurrentElement();
		if (vertexFormatElement.getType() != VertexFormatElement.Type.UV || vertexFormatElement.getIndex() != index) {
			return this;
		} else if (vertexFormatElement.getFormat() == VertexFormatElement.Format.SHORT && vertexFormatElement.method_34451() == 2) {
			this.putShort(0, u);
			this.putShort(2, v);
			this.nextElement();
			return this;
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	default VertexConsumer normal(float x, float y, float z) {
		VertexFormatElement vertexFormatElement = this.getCurrentElement();
		if (vertexFormatElement.getType() != VertexFormatElement.Type.NORMAL) {
			return this;
		} else if (vertexFormatElement.getFormat() == VertexFormatElement.Format.BYTE && vertexFormatElement.method_34451() == 3) {
			this.putByte(0, method_24212(x));
			this.putByte(1, method_24212(y));
			this.putByte(2, method_24212(z));
			this.nextElement();
			return this;
		} else {
			throw new IllegalStateException();
		}
	}

	static byte method_24212(float f) {
		return (byte)((int)(MathHelper.clamp(f, -1.0F, 1.0F) * 127.0F) & 0xFF);
	}
}
