package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.VertexBuffer;

/**
 * Defines what {@link VertexFormatElement elements} a vertex has.
 */
@Environment(EnvType.CLIENT)
public class VertexFormat {
	public static final int field_52099 = -1;
	private final List<VertexFormatElement> elements;
	private final List<String> names;
	private final int vertexSizeByte;
	private final int requiredMask;
	private final int[] offsetsByElementId = new int[32];
	@Nullable
	private VertexBuffer buffer;

	VertexFormat(List<VertexFormatElement> elements, List<String> names, IntList offsets, int vertexSizeByte) {
		this.elements = elements;
		this.names = names;
		this.vertexSizeByte = vertexSizeByte;
		this.requiredMask = elements.stream().mapToInt(VertexFormatElement::getBit).reduce(0, (a, b) -> a | b);

		for (int i = 0; i < this.offsetsByElementId.length; i++) {
			VertexFormatElement vertexFormatElement = VertexFormatElement.get(i);
			int j = vertexFormatElement != null ? elements.indexOf(vertexFormatElement) : -1;
			this.offsetsByElementId[i] = j != -1 ? offsets.getInt(j) : -1;
		}
	}

	public static VertexFormat.Builder builder() {
		return new VertexFormat.Builder();
	}

	public void bindAttributes(int program) {
		int i = 0;

		for (String string : this.getAttributeNames()) {
			GlStateManager._glBindAttribLocation(program, i, string);
			i++;
		}
	}

	public String toString() {
		return "VertexFormat" + this.names;
	}

	public int getVertexSizeByte() {
		return this.vertexSizeByte;
	}

	public List<VertexFormatElement> getElements() {
		return this.elements;
	}

	public List<String> getAttributeNames() {
		return this.names;
	}

	public int[] getOffsetsByElementId() {
		return this.offsetsByElementId;
	}

	public int getOffset(VertexFormatElement element) {
		return this.offsetsByElementId[element.id()];
	}

	public boolean has(VertexFormatElement element) {
		return (this.requiredMask & element.getBit()) != 0;
	}

	public int getRequiredMask() {
		return this.requiredMask;
	}

	public String getName(VertexFormatElement element) {
		int i = this.elements.indexOf(element);
		if (i == -1) {
			throw new IllegalArgumentException(element + " is not contained in format");
		} else {
			return (String)this.names.get(i);
		}
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			if (o instanceof VertexFormat vertexFormat
				&& this.requiredMask == vertexFormat.requiredMask
				&& this.vertexSizeByte == vertexFormat.vertexSizeByte
				&& this.names.equals(vertexFormat.names)
				&& Arrays.equals(this.offsetsByElementId, vertexFormat.offsetsByElementId)) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		return this.requiredMask * 31 + Arrays.hashCode(this.offsetsByElementId);
	}

	/**
	 * Specifies for OpenGL how the vertex data should be interpreted.
	 */
	public void setupState() {
		RenderSystem.assertOnRenderThread();
		int i = this.getVertexSizeByte();

		for (int j = 0; j < this.elements.size(); j++) {
			GlStateManager._enableVertexAttribArray(j);
			VertexFormatElement vertexFormatElement = (VertexFormatElement)this.elements.get(j);
			vertexFormatElement.setupState(j, (long)this.getOffset(vertexFormatElement), i);
		}
	}

	public void clearState() {
		RenderSystem.assertOnRenderThread();

		for (int i = 0; i < this.elements.size(); i++) {
			GlStateManager._disableVertexAttribArray(i);
		}
	}

	/**
	 * {@return a vertex buffer shared with the users of this vertex format}
	 * 
	 * <p>The data uploaded to the returned vertex buffer cannot be reused as
	 * it can be overwritten by other users of this method.
	 */
	public VertexBuffer getBuffer() {
		VertexBuffer vertexBuffer = this.buffer;
		if (vertexBuffer == null) {
			this.buffer = vertexBuffer = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
		}

		return vertexBuffer;
	}

	@Environment(EnvType.CLIENT)
	public static class Builder {
		private final ImmutableMap.Builder<String, VertexFormatElement> elements = ImmutableMap.builder();
		private final IntList offsets = new IntArrayList();
		private int currentOffset;

		Builder() {
		}

		public VertexFormat.Builder add(String name, VertexFormatElement element) {
			this.elements.put(name, element);
			this.offsets.add(this.currentOffset);
			this.currentOffset = this.currentOffset + element.getSizeInBytes();
			return this;
		}

		public VertexFormat.Builder skip(int offset) {
			this.currentOffset += offset;
			return this;
		}

		public VertexFormat build() {
			ImmutableMap<String, VertexFormatElement> immutableMap = this.elements.buildOrThrow();
			ImmutableList<VertexFormatElement> immutableList = immutableMap.values().asList();
			ImmutableList<String> immutableList2 = immutableMap.keySet().asList();
			return new VertexFormat(immutableList, immutableList2, this.offsets, this.currentOffset);
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum DrawMode {
		LINES(4, 2, 2, false),
		LINE_STRIP(5, 2, 1, true),
		DEBUG_LINES(1, 2, 2, false),
		DEBUG_LINE_STRIP(3, 2, 1, true),
		TRIANGLES(4, 3, 3, false),
		TRIANGLE_STRIP(5, 3, 1, true),
		TRIANGLE_FAN(6, 3, 1, true),
		QUADS(4, 4, 4, false);

		public final int glMode;
		/**
		 * The number of vertices needed to form a first shape.
		 */
		public final int firstVertexCount;
		/**
		 * The number of vertices needed to form an additional shape. In other
		 * words, it's {@code firstVertexCount - s} where {@code s} is the number
		 * of vertices shared with the previous shape.
		 */
		public final int additionalVertexCount;
		/**
		 * Whether there are shared vertices in consecutive shapes.
		 */
		public final boolean shareVertices;

		private DrawMode(final int glMode, final int firstVertexCount, final int additionalVertexCount, final boolean shareVertices) {
			this.glMode = glMode;
			this.firstVertexCount = firstVertexCount;
			this.additionalVertexCount = additionalVertexCount;
			this.shareVertices = shareVertices;
		}

		public int getIndexCount(int vertexCount) {
			return switch (this) {
				case LINES, QUADS -> vertexCount / 4 * 6;
				case LINE_STRIP, DEBUG_LINES, DEBUG_LINE_STRIP, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN -> vertexCount;
				default -> 0;
			};
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum IndexType {
		SHORT(GlConst.GL_UNSIGNED_SHORT, 2),
		INT(GlConst.GL_UNSIGNED_INT, 4);

		public final int glType;
		public final int size;

		private IndexType(final int glType, final int size) {
			this.glType = glType;
			this.size = size;
		}

		/**
		 * {@return the smallest type in which {@code indexCount} fits}
		 */
		public static VertexFormat.IndexType smallestFor(int indexCount) {
			return (indexCount & -65536) != 0 ? INT : SHORT;
		}
	}
}
