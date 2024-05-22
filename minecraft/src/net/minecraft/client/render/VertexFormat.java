package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
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
	private final List<String> field_52100;
	private final int vertexSizeByte;
	private final int field_52101;
	private final int[] field_52102 = new int[32];
	@Nullable
	private VertexBuffer buffer;

	VertexFormat(List<VertexFormatElement> list, List<String> list2, IntList intList, int i) {
		this.elements = list;
		this.field_52100 = list2;
		this.vertexSizeByte = i;
		this.field_52101 = list.stream().mapToInt(VertexFormatElement::method_60843).reduce(0, (ix, jx) -> ix | jx);

		for (int j = 0; j < this.field_52102.length; j++) {
			VertexFormatElement vertexFormatElement = VertexFormatElement.method_60844(j);
			int k = vertexFormatElement != null ? list.indexOf(vertexFormatElement) : -1;
			this.field_52102[j] = k != -1 ? intList.getInt(k) : -1;
		}
	}

	public static VertexFormat.class_9803 method_60833() {
		return new VertexFormat.class_9803();
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("Vertex format (").append(this.vertexSizeByte).append(" bytes):\n");

		for (int i = 0; i < this.elements.size(); i++) {
			VertexFormatElement vertexFormatElement = (VertexFormatElement)this.elements.get(i);
			stringBuilder.append(i)
				.append(". ")
				.append((String)this.field_52100.get(i))
				.append(": ")
				.append(vertexFormatElement)
				.append(" @ ")
				.append(this.method_60835(vertexFormatElement))
				.append('\n');
		}

		return stringBuilder.toString();
	}

	public int getVertexSizeByte() {
		return this.vertexSizeByte;
	}

	public List<VertexFormatElement> getElements() {
		return this.elements;
	}

	public List<String> getAttributeNames() {
		return this.field_52100;
	}

	public int[] method_60838() {
		return this.field_52102;
	}

	public int method_60835(VertexFormatElement vertexFormatElement) {
		return this.field_52102[vertexFormatElement.id()];
	}

	public boolean method_60836(VertexFormatElement vertexFormatElement) {
		return (this.field_52101 & vertexFormatElement.method_60843()) != 0;
	}

	public int method_60839() {
		return this.field_52101;
	}

	public String method_60837(VertexFormatElement vertexFormatElement) {
		int i = this.elements.indexOf(vertexFormatElement);
		if (i == -1) {
			throw new IllegalArgumentException(vertexFormatElement + " is not contained in format");
		} else {
			return (String)this.field_52100.get(i);
		}
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			if (object instanceof VertexFormat vertexFormat
				&& this.field_52101 == vertexFormat.field_52101
				&& this.vertexSizeByte == vertexFormat.vertexSizeByte
				&& this.field_52100.equals(vertexFormat.field_52100)
				&& Arrays.equals(this.field_52102, vertexFormat.field_52102)) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		return this.field_52101 * 31 + Arrays.hashCode(this.field_52102);
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

		for (int j = 0; j < this.elements.size(); j++) {
			GlStateManager._enableVertexAttribArray(j);
			VertexFormatElement vertexFormatElement = (VertexFormatElement)this.elements.get(j);
			vertexFormatElement.setupState(j, (long)this.method_60835(vertexFormatElement), i);
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

	@Environment(EnvType.CLIENT)
	public static class class_9803 {
		private final Builder<String, VertexFormatElement> field_52103 = ImmutableMap.builder();
		private final IntList field_52104 = new IntArrayList();
		private int field_52105;

		class_9803() {
		}

		public VertexFormat.class_9803 method_60842(String string, VertexFormatElement vertexFormatElement) {
			this.field_52103.put(string, vertexFormatElement);
			this.field_52104.add(this.field_52105);
			this.field_52105 = this.field_52105 + vertexFormatElement.method_60847();
			return this;
		}

		public VertexFormat.class_9803 method_60841(int i) {
			this.field_52105 += i;
			return this;
		}

		public VertexFormat method_60840() {
			ImmutableMap<String, VertexFormatElement> immutableMap = this.field_52103.buildOrThrow();
			ImmutableList<VertexFormatElement> immutableList = immutableMap.values().asList();
			ImmutableList<String> immutableList2 = immutableMap.keySet().asList();
			return new VertexFormat(immutableList, immutableList2, this.field_52104, this.field_52105);
		}
	}
}
