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

@Environment(EnvType.CLIENT)
public class VertexFormat {
	private final ImmutableList<VertexFormatElement> elements;
	private final ImmutableMap<String, VertexFormatElement> elementMap;
	private final IntList offsets = new IntArrayList();
	private final int size;
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

		this.size = i;
	}

	public String toString() {
		return "format: "
			+ this.elementMap.size()
			+ " elements: "
			+ (String)this.elementMap.entrySet().stream().map(Object::toString).collect(Collectors.joining(" "));
	}

	public int getVertexSizeInteger() {
		return this.getVertexSize() / 4;
	}

	public int getVertexSize() {
		return this.size;
	}

	public ImmutableList<VertexFormatElement> getElements() {
		return this.elements;
	}

	public ImmutableList<String> getShaderAttributes() {
		return this.elementMap.keySet().asList();
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			VertexFormat vertexFormat = (VertexFormat)o;
			return this.size != vertexFormat.size ? false : this.elementMap.equals(vertexFormat.elementMap);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.elementMap.hashCode();
	}

	public void startDrawing() {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(this::innerStartDrawing);
		} else {
			this.innerStartDrawing();
		}
	}

	private void innerStartDrawing() {
		int i = this.getVertexSize();
		List<VertexFormatElement> list = this.getElements();

		for (int j = 0; j < list.size(); j++) {
			((VertexFormatElement)list.get(j)).startDrawing(j, (long)this.offsets.getInt(j), i);
		}
	}

	public void endDrawing() {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(this::innerEndDrawing);
		} else {
			this.innerEndDrawing();
		}
	}

	private void innerEndDrawing() {
		ImmutableList<VertexFormatElement> immutableList = this.getElements();

		for (int i = 0; i < immutableList.size(); i++) {
			VertexFormatElement vertexFormatElement = (VertexFormatElement)immutableList.get(i);
			vertexFormatElement.endDrawing(i);
		}
	}

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

		public final int mode;
		public final int vertexCount;
		public final int size;
		public final boolean shareVertices;

		private DrawMode(int mode, int vertexCount, int size, boolean shareVertices) {
			this.mode = mode;
			this.vertexCount = vertexCount;
			this.size = size;
			this.shareVertices = shareVertices;
		}

		public int getSize(int vertexCount) {
			return switch (this) {
				case LINE_STRIP, DEBUG_LINES, DEBUG_LINE_STRIP, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN -> vertexCount;
				case LINES, QUADS -> vertexCount / 4 * 6;
				default -> 0;
			};
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum IntType {
		BYTE(GlConst.GL_UNSIGNED_BYTE, 1),
		SHORT(GlConst.GL_UNSIGNED_SHORT, 2),
		INT(GlConst.GL_UNSIGNED_INT, 4);

		public final int type;
		public final int size;

		private IntType(int type, int size) {
			this.type = type;
			this.size = size;
		}

		/**
		 * Gets the smallest type in which the given number fits.
		 * 
		 * @return the smallest type
		 * 
		 * @param number a number from 8 to 32 bits of memory
		 */
		public static VertexFormat.IntType getSmallestTypeFor(int number) {
			if ((number & -65536) != 0) {
				return INT;
			} else {
				return (number & 0xFF00) != 0 ? SHORT : BYTE;
			}
		}
	}
}
