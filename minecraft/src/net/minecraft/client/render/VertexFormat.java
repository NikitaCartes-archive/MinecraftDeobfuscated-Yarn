package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
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
	private final IntList offsets = new IntArrayList();
	private final int size;

	public VertexFormat(ImmutableList<VertexFormatElement> immutableList) {
		this.elements = immutableList;
		int i = 0;

		for (VertexFormatElement vertexFormatElement : immutableList) {
			this.offsets.add(i);
			i += vertexFormatElement.getSize();
		}

		this.size = i;
	}

	public String toString() {
		return "format: " + this.elements.size() + " elements: " + (String)this.elements.stream().map(Object::toString).collect(Collectors.joining(" "));
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

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			VertexFormat vertexFormat = (VertexFormat)object;
			return this.size != vertexFormat.size ? false : this.elements.equals(vertexFormat.elements);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.elements.hashCode();
	}

	public void startDrawing(long l) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> this.startDrawing(l));
		} else {
			int i = this.getVertexSize();
			List<VertexFormatElement> list = this.getElements();

			for (int j = 0; j < list.size(); j++) {
				((VertexFormatElement)list.get(j)).startDrawing(l + (long)this.offsets.getInt(j), i);
			}
		}
	}

	public void endDrawing() {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(this::endDrawing);
		} else {
			for (VertexFormatElement vertexFormatElement : this.getElements()) {
				vertexFormatElement.endDrawing();
			}
		}
	}
}
