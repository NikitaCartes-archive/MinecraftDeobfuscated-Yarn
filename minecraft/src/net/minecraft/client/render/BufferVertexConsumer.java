package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface BufferVertexConsumer extends VertexConsumer {
	VertexFormatElement getCurrentElement();

	void nextElement();

	void putByte(int index, byte value);

	void putShort(int index, short value);

	void putFloat(int index, float value);

	@Override
	default VertexConsumer vertex(double x, double y, double z) {
		if (this.getCurrentElement().getFormat() != VertexFormatElement.Format.FLOAT) {
			throw new IllegalStateException();
		} else {
			this.putFloat(0, (float)x);
			this.putFloat(4, (float)y);
			this.putFloat(8, (float)z);
			this.nextElement();
			return this;
		}
	}

	@Override
	default VertexConsumer color(int red, int green, int blue, int alpha) {
		VertexFormatElement vertexFormatElement = this.getCurrentElement();
		if (vertexFormatElement.getType() != VertexFormatElement.Type.COLOR) {
			return this;
		} else if (vertexFormatElement.getFormat() != VertexFormatElement.Format.UBYTE) {
			throw new IllegalStateException();
		} else {
			this.putByte(0, (byte)red);
			this.putByte(1, (byte)green);
			this.putByte(2, (byte)blue);
			this.putByte(3, (byte)alpha);
			this.nextElement();
			return this;
		}
	}

	@Override
	default VertexConsumer texture(float u, float v) {
		VertexFormatElement vertexFormatElement = this.getCurrentElement();
		if (vertexFormatElement.getType() == VertexFormatElement.Type.UV && vertexFormatElement.getIndex() == 0) {
			if (vertexFormatElement.getFormat() != VertexFormatElement.Format.FLOAT) {
				throw new IllegalStateException();
			} else {
				this.putFloat(0, u);
				this.putFloat(4, v);
				this.nextElement();
				return this;
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
		} else if (vertexFormatElement.getFormat() != VertexFormatElement.Format.SHORT) {
			throw new IllegalStateException();
		} else {
			this.putShort(0, u);
			this.putShort(2, v);
			this.nextElement();
			return this;
		}
	}

	@Override
	default VertexConsumer normal(float x, float y, float z) {
		VertexFormatElement vertexFormatElement = this.getCurrentElement();
		if (vertexFormatElement.getType() != VertexFormatElement.Type.NORMAL) {
			return this;
		} else if (vertexFormatElement.getFormat() != VertexFormatElement.Format.BYTE) {
			throw new IllegalStateException();
		} else {
			this.putByte(0, (byte)((int)(x * 127.0F) & 0xFF));
			this.putByte(1, (byte)((int)(y * 127.0F) & 0xFF));
			this.putByte(2, (byte)((int)(z * 127.0F) & 0xFF));
			this.nextElement();
			return this;
		}
	}
}
