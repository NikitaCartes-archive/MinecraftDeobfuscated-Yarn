package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VertexFormatElement {
	private final VertexFormatElement.Format format;
	private final VertexFormatElement.Type type;
	private final int index;
	private final int count;
	private final int size;

	public VertexFormatElement(int index, VertexFormatElement.Format format, VertexFormatElement.Type type, int count) {
		if (this.isValidType(index, type)) {
			this.type = type;
			this.format = format;
			this.index = index;
			this.count = count;
			this.size = format.getSize() * this.count;
		} else {
			throw new IllegalStateException("Multiple vertex elements of the same type other than UVs are not supported");
		}
	}

	private boolean isValidType(int index, VertexFormatElement.Type type) {
		return index == 0 || type == VertexFormatElement.Type.UV;
	}

	public final VertexFormatElement.Format getFormat() {
		return this.format;
	}

	public final VertexFormatElement.Type getType() {
		return this.type;
	}

	public final int method_34451() {
		return this.count;
	}

	public final int getIndex() {
		return this.index;
	}

	public String toString() {
		return this.count + "," + this.type.getName() + "," + this.format.getName();
	}

	public final int getSize() {
		return this.size;
	}

	public final boolean method_35667() {
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

	public void startDrawing(int i, long l, int j) {
		this.type.startDrawing(this.count, this.format.getGlId(), j, l, this.index, i);
	}

	public void endDrawing(int i) {
		this.type.endDrawing(this.index, i);
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
		POSITION("Position", (i, j, k, l, m, n) -> {
			GlStateManager._enableVertexAttribArray(n);
			GlStateManager._vertexAttribPointer(n, i, j, false, k, l);
		}, (i, j) -> GlStateManager._disableVertexAttribArray(j)),
		NORMAL("Normal", (i, j, k, l, m, n) -> {
			GlStateManager._enableVertexAttribArray(n);
			GlStateManager._vertexAttribPointer(n, i, j, true, k, l);
		}, (i, j) -> GlStateManager._disableVertexAttribArray(j)),
		COLOR("Vertex Color", (i, j, k, l, m, n) -> {
			GlStateManager._enableVertexAttribArray(n);
			GlStateManager._vertexAttribPointer(n, i, j, true, k, l);
		}, (i, j) -> GlStateManager._disableVertexAttribArray(j)),
		UV("UV", (i, j, k, l, m, n) -> {
			GlStateManager._enableVertexAttribArray(n);
			if (j == 5126) {
				GlStateManager._vertexAttribPointer(n, i, j, false, k, l);
			} else {
				GlStateManager._vertexAttribIPointer(n, i, j, k, l);
			}
		}, (i, j) -> GlStateManager._disableVertexAttribArray(j)),
		PADDING("Padding", (i, j, k, l, m, n) -> {
		}, (i, j) -> {
		}),
		GENERIC("Generic", (i, j, k, l, m, n) -> {
			GlStateManager._enableVertexAttribArray(n);
			GlStateManager._vertexAttribPointer(n, i, j, false, k, l);
		}, (i, j) -> GlStateManager._disableVertexAttribArray(j));

		private final String name;
		private final VertexFormatElement.Type.Starter starter;
		private final VertexFormatElement.Type.class_5938 finisher;

		private Type(String name, VertexFormatElement.Type.Starter starter, VertexFormatElement.Type.class_5938 arg) {
			this.name = name;
			this.starter = starter;
			this.finisher = arg;
		}

		private void startDrawing(int count, int glId, int stride, long pointer, int elementIndex, int i) {
			this.starter.setupBufferState(count, glId, stride, pointer, elementIndex, i);
		}

		public void endDrawing(int elementIndex, int i) {
			this.finisher.clearBufferState(elementIndex, i);
		}

		public String getName() {
			return this.name;
		}

		@FunctionalInterface
		@Environment(EnvType.CLIENT)
		interface Starter {
			void setupBufferState(int count, int glId, int stride, long pointer, int elementIndex, int i);
		}

		@FunctionalInterface
		@Environment(EnvType.CLIENT)
		interface class_5938 {
			void clearBufferState(int i, int j);
		}
	}
}
