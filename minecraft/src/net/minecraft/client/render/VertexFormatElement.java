package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class VertexFormatElement {
	private static final Logger LOGGER = LogManager.getLogger();
	private final VertexFormatElement.Format format;
	private final VertexFormatElement.Type type;
	private final int index;
	private final int count;

	public VertexFormatElement(int index, VertexFormatElement.Format format, VertexFormatElement.Type type, int count) {
		if (this.isValidType(index, type)) {
			this.type = type;
		} else {
			LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
			this.type = VertexFormatElement.Type.UV;
		}

		this.format = format;
		this.index = index;
		this.count = count;
	}

	private final boolean isValidType(int index, VertexFormatElement.Type type) {
		return index == 0 || type == VertexFormatElement.Type.UV;
	}

	public final VertexFormatElement.Format getFormat() {
		return this.format;
	}

	public final VertexFormatElement.Type getType() {
		return this.type;
	}

	public final int getCount() {
		return this.count;
	}

	public final int getIndex() {
		return this.index;
	}

	public String toString() {
		return this.count + "," + this.type.getName() + "," + this.format.getName();
	}

	public final int getSize() {
		return this.format.getSize() * this.count;
	}

	public final boolean isPosition() {
		return this.type == VertexFormatElement.Type.POSITION;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			VertexFormatElement vertexFormatElement = (VertexFormatElement)o;
			if (this.count != vertexFormatElement.count) {
				return false;
			} else if (this.index != vertexFormatElement.index) {
				return false;
			} else {
				return this.format != vertexFormatElement.format ? false : this.type == vertexFormatElement.type;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = this.format.hashCode();
		i = 31 * i + this.type.hashCode();
		i = 31 * i + this.index;
		return 31 * i + this.count;
	}

	@Environment(EnvType.CLIENT)
	public static enum Format {
		FLOAT(4, "Float", 5126),
		UBYTE(1, "Unsigned Byte", 5121),
		BYTE(1, "Byte", 5120),
		USHORT(2, "Unsigned Short", 5123),
		SHORT(2, "Short", 5122),
		UINT(4, "Unsigned Int", 5125),
		INT(4, "Int", 5124);

		private final int size;
		private final String name;
		private final int glId;

		private Format(int size, String name, int glId) {
			this.size = size;
			this.name = name;
			this.glId = glId;
		}

		public int getSize() {
			return this.size;
		}

		public String getName() {
			return this.name;
		}

		public int getGlId() {
			return this.glId;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		POSITION("Position"),
		NORMAL("Normal"),
		COLOR("Vertex Color"),
		UV("UV"),
		MATRIX("Bone Matrix"),
		BLEND_WEIGHT("Blend Weight"),
		PADDING("Padding");

		private final String name;

		private Type(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}
}
