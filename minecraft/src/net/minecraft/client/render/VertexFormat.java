package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VertexFormat {
	private final ImmutableList<VertexFormatElement> elements;
	private final ImmutableMap<String, VertexFormatElement> field_29340;
	private final IntList offsets = new IntArrayList();
	private final int size;
	private int field_29341;
	private int field_29342;
	private int field_29343;

	public VertexFormat(ImmutableMap<String, VertexFormatElement> immutableMap) {
		this.field_29340 = immutableMap;
		this.elements = immutableMap.values().asList();
		int i = 0;

		for (VertexFormatElement vertexFormatElement : immutableMap.values()) {
			this.offsets.add(i);
			i += vertexFormatElement.getSize();
		}

		this.size = i;
	}

	public String toString() {
		return "format: "
			+ this.field_29340.size()
			+ " elements: "
			+ (String)this.field_29340.entrySet().stream().map(Object::toString).collect(Collectors.joining(" "));
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
		return this.field_29340.keySet().asList();
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			VertexFormat vertexFormat = (VertexFormat)o;
			return this.size != vertexFormat.size ? false : this.field_29340.equals(vertexFormat.field_29340);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.field_29340.hashCode();
	}

	public void startDrawing() {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(this::method_34449);
		} else {
			this.method_34449();
		}
	}

	private void method_34449() {
		int i = this.getVertexSize();
		List<VertexFormatElement> list = this.getElements();

		for (int j = 0; j < list.size(); j++) {
			((VertexFormatElement)list.get(j)).startDrawing(j, (long)this.offsets.getInt(j), i);
		}
	}

	public void endDrawing() {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(this::method_34450);
		} else {
			this.method_34450();
		}
	}

	private void method_34450() {
		ImmutableList<VertexFormatElement> immutableList = this.getElements();

		for (int i = 0; i < immutableList.size(); i++) {
			VertexFormatElement vertexFormatElement = (VertexFormatElement)immutableList.get(i);
			vertexFormatElement.endDrawing(i);
		}
	}

	public int method_34446() {
		if (this.field_29341 == 0) {
			this.field_29341 = GlStateManager._glGenVertexArrays();
		}

		return this.field_29341;
	}

	public int method_34447() {
		if (this.field_29342 == 0) {
			this.field_29342 = GlStateManager._glGenBuffers();
		}

		return this.field_29342;
	}

	public int method_34448() {
		if (this.field_29343 == 0) {
			this.field_29343 = GlStateManager._glGenBuffers();
		}

		return this.field_29343;
	}

	@Environment(EnvType.CLIENT)
	public static enum DrawMode {
		LINES(4, 2, 2),
		LINE_STRIP(5, 2, 1),
		DEBUG_LINES(1, 2, 2),
		DEBUG_LINE_STRIP(3, 2, 1),
		TRIANGLES(4, 3, 3),
		TRIANGLE_STRIP(5, 3, 1),
		TRIANGLE_FAN(6, 3, 1),
		QUADS(4, 4, 4);

		public final int mode;
		public final int field_27384;
		public final int field_27385;

		private DrawMode(int mode, int j, int k) {
			this.mode = mode;
			this.field_27384 = j;
			this.field_27385 = k;
		}

		public int getSize(int vertexCount) {
			int i;
			switch (this) {
				case LINE_STRIP:
				case DEBUG_LINES:
				case DEBUG_LINE_STRIP:
				case TRIANGLES:
				case TRIANGLE_STRIP:
				case TRIANGLE_FAN:
					i = vertexCount;
					break;
				case LINES:
				case QUADS:
					i = vertexCount / 4 * 6;
					break;
				default:
					i = 0;
			}

			return i;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum IntType {
		BYTE(5121, 1),
		SHORT(5123, 2),
		INT(5125, 4);

		public final int field_27374;
		public final int size;

		private IntType(int j, int size) {
			this.field_27374 = j;
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
