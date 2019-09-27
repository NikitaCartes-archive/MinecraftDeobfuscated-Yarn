package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormatElement;

@Environment(EnvType.CLIENT)
public interface class_4584 extends class_4588 {
	VertexFormatElement getCurrentElement();

	void nextElement();

	void putByte(int i, byte b);

	void putShort(int i, short s);

	void putFloat(int i, float f);

	@Override
	default class_4588 vertex(double d, double e, double f) {
		if (this.getCurrentElement().getFormat() != VertexFormatElement.Format.FLOAT) {
			throw new IllegalStateException();
		} else {
			this.putFloat(0, (float)d);
			this.putFloat(4, (float)e);
			this.putFloat(8, (float)f);
			this.nextElement();
			return this;
		}
	}

	@Override
	default class_4588 color(int i, int j, int k, int l) {
		if (this.getCurrentElement().getType() != VertexFormatElement.Type.COLOR) {
			return this;
		} else if (this.getCurrentElement().getFormat() != VertexFormatElement.Format.UBYTE) {
			throw new IllegalStateException();
		} else {
			this.putByte(0, (byte)i);
			this.putByte(1, (byte)j);
			this.putByte(2, (byte)k);
			this.putByte(3, (byte)l);
			this.nextElement();
			return this;
		}
	}

	@Override
	default class_4588 texture(float f, float g) {
		if (this.getCurrentElement().getType() == VertexFormatElement.Type.UV && this.getCurrentElement().getIndex() == 0) {
			if (this.getCurrentElement().getFormat() != VertexFormatElement.Format.FLOAT) {
				throw new IllegalStateException();
			} else {
				this.putFloat(0, f);
				this.putFloat(4, g);
				this.nextElement();
				return this;
			}
		} else {
			return this;
		}
	}

	@Override
	default class_4588 method_22917(int i, int j) {
		return this.method_22899((short)i, (short)j, 1);
	}

	@Override
	default class_4588 method_22921(int i, int j) {
		return this.method_22899((short)i, (short)j, 2);
	}

	default class_4588 method_22899(short s, short t, int i) {
		if (this.getCurrentElement().getType() != VertexFormatElement.Type.UV || this.getCurrentElement().getIndex() != i) {
			return this;
		} else if (this.getCurrentElement().getFormat() != VertexFormatElement.Format.SHORT) {
			throw new IllegalStateException();
		} else {
			this.putShort(0, s);
			this.putShort(2, t);
			this.nextElement();
			return this;
		}
	}

	@Override
	default class_4588 method_22914(float f, float g, float h) {
		if (this.getCurrentElement().getType() != VertexFormatElement.Type.NORMAL) {
			return this;
		} else if (this.getCurrentElement().getFormat() != VertexFormatElement.Format.BYTE) {
			throw new IllegalStateException();
		} else {
			this.putByte(0, (byte)((int)(f * 127.0F) & 0xFF));
			this.putByte(1, (byte)((int)(g * 127.0F) & 0xFF));
			this.putByte(2, (byte)((int)(h * 127.0F) & 0xFF));
			this.nextElement();
			return this;
		}
	}
}
