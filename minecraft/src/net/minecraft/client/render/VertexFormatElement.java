package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents a singular field within a larger vertex format.
 * <p>
 * This element comprises a data type, a field length,
 * and the corresponding GL element type to which this field corresponds.
 */
@Environment(EnvType.CLIENT)
public class VertexFormatElement {
	private final VertexFormatElement.DataType dataType;
	private final VertexFormatElement.Type type;
	private final int textureIndex;
	private final int length;
	/**
	 * The total length of this element (in bytes).
	 */
	private final int byteLength;

	public VertexFormatElement(int textureIndex, VertexFormatElement.DataType dataType, VertexFormatElement.Type type, int length) {
		if (this.isValidType(textureIndex, type)) {
			this.type = type;
			this.dataType = dataType;
			this.textureIndex = textureIndex;
			this.length = length;
			this.byteLength = dataType.getByteLength() * this.length;
		} else {
			throw new IllegalStateException("Multiple vertex elements of the same type other than UVs are not supported");
		}
	}

	private boolean isValidType(int index, VertexFormatElement.Type type) {
		return index == 0 || type == VertexFormatElement.Type.UV;
	}

	public final VertexFormatElement.DataType getDataType() {
		return this.dataType;
	}

	public final VertexFormatElement.Type getType() {
		return this.type;
	}

	public final int getLength() {
		return this.length;
	}

	public final int getTextureIndex() {
		return this.textureIndex;
	}

	public String toString() {
		return this.length + "," + this.type.getName() + "," + this.dataType.getName();
	}

	public final int getByteLength() {
		return this.byteLength;
	}

	public final boolean isPosition() {
		return this.type == VertexFormatElement.Type.POSITION;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			VertexFormatElement vertexFormatElement = (VertexFormatElement)o;
			if (this.length != vertexFormatElement.length) {
				return false;
			} else if (this.textureIndex != vertexFormatElement.textureIndex) {
				return false;
			} else {
				return this.dataType != vertexFormatElement.dataType ? false : this.type == vertexFormatElement.type;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = this.dataType.hashCode();
		i = 31 * i + this.type.hashCode();
		i = 31 * i + this.textureIndex;
		return 31 * i + this.length;
	}

	public void startDrawing(int elementIndex, long pointer, int stride) {
		this.type.startDrawing(this.length, this.dataType.getId(), stride, pointer, this.textureIndex, elementIndex);
	}

	public void endDrawing(int elementIndex) {
		this.type.endDrawing(this.textureIndex, elementIndex);
	}

	@Environment(EnvType.CLIENT)
	public static enum DataType {
		FLOAT(4, "Float", 5126),
		UBYTE(1, "Unsigned Byte", 5121),
		BYTE(1, "Byte", 5120),
		USHORT(2, "Unsigned Short", 5123),
		SHORT(2, "Short", 5122),
		UINT(4, "Unsigned Int", 5125),
		INT(4, "Int", 5124);

		private final int byteLength;
		private final String name;
		private final int id;

		private DataType(int byteCount, String name, int id) {
			this.byteLength = byteCount;
			this.name = name;
			this.id = id;
		}

		public int getByteLength() {
			return this.byteLength;
		}

		public String getName() {
			return this.name;
		}

		public int getId() {
			return this.id;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		POSITION("Position", (size, type, stride, pointer, textureIndex, elementIndex) -> {
			GlStateManager._enableVertexAttribArray(elementIndex);
			GlStateManager._vertexAttribPointer(elementIndex, size, type, false, stride, pointer);
		}, (textureIndex, elementIndex) -> GlStateManager._disableVertexAttribArray(elementIndex)),
		NORMAL("Normal", (size, type, stride, pointer, textureIndex, elementIndex) -> {
			GlStateManager._enableVertexAttribArray(elementIndex);
			GlStateManager._vertexAttribPointer(elementIndex, size, type, true, stride, pointer);
		}, (textureIndex, elementIndex) -> GlStateManager._disableVertexAttribArray(elementIndex)),
		COLOR("Vertex Color", (size, type, stride, pointer, textureIndex, elementIndex) -> {
			GlStateManager._enableVertexAttribArray(elementIndex);
			GlStateManager._vertexAttribPointer(elementIndex, size, type, true, stride, pointer);
		}, (textureIndex, elementIndex) -> GlStateManager._disableVertexAttribArray(elementIndex)),
		UV("UV", (size, type, stride, pointer, textureIndex, elementIndex) -> {
			GlStateManager._enableVertexAttribArray(elementIndex);
			if (type == 5126) {
				GlStateManager._vertexAttribPointer(elementIndex, size, type, false, stride, pointer);
			} else {
				GlStateManager._vertexAttribIPointer(elementIndex, size, type, stride, pointer);
			}
		}, (textureIndex, elementIndex) -> GlStateManager._disableVertexAttribArray(elementIndex)),
		PADDING("Padding", (size, type, stride, pointer, textureIndex, elementIndex) -> {
		}, (textureIndex, elementIndex) -> {
		}),
		GENERIC("Generic", (size, type, stride, pointer, textureIndex, elementIndex) -> {
			GlStateManager._enableVertexAttribArray(elementIndex);
			GlStateManager._vertexAttribPointer(elementIndex, size, type, false, stride, pointer);
		}, (textureIndex, elementIndex) -> GlStateManager._disableVertexAttribArray(elementIndex));

		private final String name;
		private final VertexFormatElement.Type.Starter starter;
		private final VertexFormatElement.Type.Finisher finisher;

		private Type(String name, VertexFormatElement.Type.Starter starter, VertexFormatElement.Type.Finisher finisher) {
			this.name = name;
			this.starter = starter;
			this.finisher = finisher;
		}

		private void startDrawing(int size, int type, int stride, long pointer, int textureIndex, int elementIndex) {
			this.starter.setupBufferState(size, type, stride, pointer, textureIndex, elementIndex);
		}

		public void endDrawing(int textureIndex, int elementIndex) {
			this.finisher.clearBufferState(textureIndex, elementIndex);
		}

		public String getName() {
			return this.name;
		}

		@FunctionalInterface
		@Environment(EnvType.CLIENT)
		interface Finisher {
			void clearBufferState(int textureIndex, int elementIndex);
		}

		@FunctionalInterface
		@Environment(EnvType.CLIENT)
		interface Starter {
			void setupBufferState(int size, int type, int stride, long pointer, int textureIndex, int elementIndex);
		}
	}
}
