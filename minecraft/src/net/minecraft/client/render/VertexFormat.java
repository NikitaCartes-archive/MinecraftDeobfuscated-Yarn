package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.VertexBuffer;

/**
 * Defines what {@link VertexFormatElement elements} a vertex has.
 */
@Environment(EnvType.CLIENT)
public class VertexFormat {
	private final ImmutableList<VertexFormatElement> elements;
	private final ImmutableMap<String, VertexFormatElement> elementMap;
	private final IntList offsets = new IntArrayList();
	private final int vertexSizeByte;
	@Nullable
	private VertexBuffer buffer;

	public VertexFormat(ImmutableMap<String, VertexFormatElement> elementMap) {
		this.elementMap = elementMap;
		this.elements = elementMap.values().asList();
		int i = 0;

		for (VertexFormatElement vertexFormatElement : elementMap.values()) {
			this.offsets.add(i);
			i += vertexFormatElement.getByteLength();
		}

		this.vertexSizeByte = i;
	}

	public String toString() {
		return "format: "
			+ this.elementMap.size()
			+ " elements: "
			+ (String)this.elementMap.entrySet().stream().map(Object::toString).collect(Collectors.joining(" "));
	}

	public int getVertexSizeInteger() {
		return this.getVertexSizeByte() / 4;
	}

	public int getVertexSizeByte() {
		return this.vertexSizeByte;
	}

	public ImmutableList<VertexFormatElement> getElements() {
		return this.elements;
	}

	public ImmutableList<String> getAttributeNames() {
		return this.elementMap.keySet().asList();
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			VertexFormat vertexFormat = (VertexFormat)o;
			return this.vertexSizeByte != vertexFormat.vertexSizeByte ? false : this.elementMap.equals(vertexFormat.elementMap);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.elementMap.hashCode();
	}

	/**
	 * Specifies for OpenGL how the vertex data should be interpreted.
	 */
	public void setupState() {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(this::setupStateInternal);
		} else {
			this.setupStateInternal();
		}
	}

	private void setupStateInternal() {
		int i = this.getVertexSizeByte();
		List<VertexFormatElement> list = this.getElements();

		for (int j = 0; j < list.size(); j++) {
			((VertexFormatElement)list.get(j)).setupState(j, (long)this.offsets.getInt(j), i);
		}
	}

	public void clearState() {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(this::clearStateInternal);
		} else {
			this.clearStateInternal();
		}
	}

	private void clearStateInternal() {
		ImmutableList<VertexFormatElement> immutableList = this.getElements();

		for (int i = 0; i < immutableList.size(); i++) {
			VertexFormatElement vertexFormatElement = (VertexFormatElement)immutableList.get(i);
			vertexFormatElement.clearState(i);
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
			this.buffer = vertexBuffer = new VertexBuffer();
		}

		return vertexBuffer;
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

		private DrawMode(int glMode, int firstVertexCount, int additionalVertexCount, boolean shareVertices) {
			this.glMode = glMode;
			this.firstVertexCount = firstVertexCount;
			this.additionalVertexCount = additionalVertexCount;
			this.shareVertices = shareVertices;
		}

		public int getIndexCount(int vertexCount) {
			return switch (this) {
				case LINE_STRIP, DEBUG_LINES, DEBUG_LINE_STRIP, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN -> vertexCount;
				case LINES, QUADS -> vertexCount / 4 * 6;
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

		private IndexType(int glType, int size) {
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
