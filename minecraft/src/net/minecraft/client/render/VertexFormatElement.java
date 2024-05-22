package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents a singular field within a larger {@link
 * net.minecraft.client.render.VertexFormat vertex format}.
 * 
 * <p>This element comprises a component type, the number of components,
 * and a type that describes how the components should be interpreted.
 */
@Environment(EnvType.CLIENT)
public record VertexFormatElement(int id, int uvIndex, VertexFormatElement.ComponentType componentType, VertexFormatElement.Type type, int componentCount) {
	public static final int field_52106 = 32;
	private static final VertexFormatElement[] field_52114 = new VertexFormatElement[32];
	private static final List<VertexFormatElement> field_52115 = new ArrayList(32);
	public static final VertexFormatElement field_52107 = method_60845(0, 0, VertexFormatElement.ComponentType.FLOAT, VertexFormatElement.Type.POSITION, 3);
	public static final VertexFormatElement field_52108 = method_60845(1, 0, VertexFormatElement.ComponentType.UBYTE, VertexFormatElement.Type.COLOR, 4);
	public static final VertexFormatElement field_52109 = method_60845(2, 0, VertexFormatElement.ComponentType.FLOAT, VertexFormatElement.Type.UV, 2);
	public static final VertexFormatElement field_52110 = field_52109;
	public static final VertexFormatElement field_52111 = method_60845(3, 1, VertexFormatElement.ComponentType.SHORT, VertexFormatElement.Type.UV, 2);
	public static final VertexFormatElement field_52112 = method_60845(4, 2, VertexFormatElement.ComponentType.SHORT, VertexFormatElement.Type.UV, 2);
	public static final VertexFormatElement field_52113 = method_60845(5, 0, VertexFormatElement.ComponentType.BYTE, VertexFormatElement.Type.NORMAL, 3);

	public VertexFormatElement(int id, int uvIndex, VertexFormatElement.ComponentType componentType, VertexFormatElement.Type type, int componentCount) {
		if (id < 0 || id >= field_52114.length) {
			throw new IllegalArgumentException("Element ID must be in range [0; " + field_52114.length + ")");
		} else if (!this.isValidType(uvIndex, type)) {
			throw new IllegalStateException("Multiple vertex elements of the same type other than UVs are not supported");
		} else {
			this.id = id;
			this.uvIndex = uvIndex;
			this.componentType = componentType;
			this.type = type;
			this.componentCount = componentCount;
		}
	}

	public static VertexFormatElement method_60845(int i, int j, VertexFormatElement.ComponentType componentType, VertexFormatElement.Type type, int k) {
		VertexFormatElement vertexFormatElement = new VertexFormatElement(i, j, componentType, type, k);
		if (field_52114[i] != null) {
			throw new IllegalArgumentException("Duplicate element registration for: " + i);
		} else {
			field_52114[i] = vertexFormatElement;
			field_52115.add(vertexFormatElement);
			return vertexFormatElement;
		}
	}

	private boolean isValidType(int uvIndex, VertexFormatElement.Type type) {
		return uvIndex == 0 || type == VertexFormatElement.Type.UV;
	}

	public String toString() {
		return this.componentCount + "," + this.type + "," + this.componentType + " (" + this.id + ")";
	}

	public int method_60843() {
		return 1 << this.id;
	}

	public int method_60847() {
		return this.componentType.getByteLength() * this.componentCount;
	}

	/**
	 * Specifies for OpenGL how the vertex data corresponding to this element
	 * should be interpreted.
	 * 
	 * @param elementIndex the index of the element in a vertex format
	 * @param offset the distance between the start of the buffer and the first instance of
	 * the element in the buffer
	 * @param stride the distance between consecutive instances of the element in the buffer
	 */
	public void setupState(int elementIndex, long offset, int stride) {
		this.type.setupTask.setupBufferState(this.componentCount, this.componentType.getGlType(), stride, offset, elementIndex);
	}

	@Nullable
	public static VertexFormatElement method_60844(int i) {
		return field_52114[i];
	}

	public static Stream<VertexFormatElement> method_60848(int i) {
		return field_52115.stream().filter(vertexFormatElement -> vertexFormatElement != null && (i & vertexFormatElement.method_60843()) != 0);
	}

	/**
	 * Represents a type of components in an element.
	 */
	@Environment(EnvType.CLIENT)
	public static enum ComponentType {
		FLOAT(4, "Float", GlConst.GL_FLOAT),
		UBYTE(1, "Unsigned Byte", GlConst.GL_UNSIGNED_BYTE),
		BYTE(1, "Byte", GlConst.GL_BYTE),
		USHORT(2, "Unsigned Short", GlConst.GL_UNSIGNED_SHORT),
		SHORT(2, "Short", GlConst.GL_SHORT),
		UINT(4, "Unsigned Int", GlConst.GL_UNSIGNED_INT),
		INT(4, "Int", GlConst.GL_INT);

		private final int byteLength;
		private final String name;
		private final int glType;

		private ComponentType(final int byteLength, final String name, final int glType) {
			this.byteLength = byteLength;
			this.name = name;
			this.glType = glType;
		}

		public int getByteLength() {
			return this.byteLength;
		}

		public int getGlType() {
			return this.glType;
		}

		public String toString() {
			return this.name;
		}
	}

	/**
	 * Describes how the components should be interpreted.
	 */
	@Environment(EnvType.CLIENT)
	public static enum Type {
		POSITION(
			"Position",
			(componentCount, componentType, stride, offset, uvIndex) -> GlStateManager._vertexAttribPointer(
					uvIndex, componentCount, componentType, false, stride, offset
				)
		),
		NORMAL("Normal", (i, j, k, l, m) -> GlStateManager._vertexAttribPointer(m, i, j, true, k, l)),
		COLOR("Vertex Color", (i, j, k, l, m) -> GlStateManager._vertexAttribPointer(m, i, j, true, k, l)),
		UV("UV", (componentCount, componentType, stride, offset, uvIndex) -> {
			if (componentType == 5126) {
				GlStateManager._vertexAttribPointer(uvIndex, componentCount, componentType, false, stride, offset);
			} else {
				GlStateManager._vertexAttribIPointer(uvIndex, componentCount, componentType, stride, offset);
			}
		}),
		GENERIC("Generic", (i, j, k, l, m) -> GlStateManager._vertexAttribPointer(m, i, j, false, k, l));

		private final String name;
		final VertexFormatElement.Type.SetupTask setupTask;

		private Type(final String name, final VertexFormatElement.Type.SetupTask setupTask) {
			this.name = name;
			this.setupTask = setupTask;
		}

		public String toString() {
			return this.name;
		}

		@FunctionalInterface
		@Environment(EnvType.CLIENT)
		interface SetupTask {
			/**
			 * Specifies for OpenGL how the vertex data corresponding to the element
			 * should be interpreted.
			 * 
			 * @param stride the distance between consecutive instances of the element in the buffer
			 * @param componentType the GL type of components in the element
			 * @param offset the distance between the start of the buffer and the first instance of
			 * the element in the buffer; be aware that {@code pointer} is a legacy
			 * name from OpenGL 2
			 * @param componentCount the number of components in the element
			 */
			void setupBufferState(int componentCount, int componentType, int stride, long offset, int uvIndex);
		}
	}
}
