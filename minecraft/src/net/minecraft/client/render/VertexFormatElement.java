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

	public VertexFormatElement(int i, VertexFormatElement.Format format, VertexFormatElement.Type type, int j) {
		if (this.isValidType(i, type)) {
			this.type = type;
		} else {
			LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
			this.type = VertexFormatElement.Type.field_1636;
		}

		this.format = format;
		this.index = i;
		this.count = j;
	}

	private final boolean isValidType(int i, VertexFormatElement.Type type) {
		return i == 0 || type == VertexFormatElement.Type.field_1636;
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
		return this.type == VertexFormatElement.Type.field_1633;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			VertexFormatElement vertexFormatElement = (VertexFormatElement)object;
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
		field_1623(4, "Float", 5126),
		UBYTE(1, "Unsigned Byte", 5121),
		field_1621(1, "Byte", 5120),
		USHORT(2, "Unsigned Short", 5123),
		field_1625(2, "Short", 5122),
		UINT(4, "Unsigned Int", 5125),
		field_1617(4, "Int", 5124);

		private final int size;
		private final String name;
		private final int glId;

		private Format(int j, String string2, int k) {
			this.size = j;
			this.name = string2;
			this.glId = k;
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
		field_1633("Position"),
		field_1635("Normal"),
		COLOR("Vertex Color"),
		field_1636("UV"),
		MATRIX("Bone Matrix"),
		field_1628("Blend Weight"),
		field_1629("Padding");

		private final String name;

		private Type(String string2) {
			this.name = string2;
		}

		public String getName() {
			return this.name;
		}
	}
}
