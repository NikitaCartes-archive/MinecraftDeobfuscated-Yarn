package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormatElement;

@Environment(EnvType.CLIENT)
public class RealmsVertexFormatElement {
	private final VertexFormatElement field_13809;

	public RealmsVertexFormatElement(VertexFormatElement vertexFormatElement) {
		this.field_13809 = vertexFormatElement;
	}

	public VertexFormatElement getVertexFormatElement() {
		return this.field_13809;
	}

	public boolean isPosition() {
		return this.field_13809.isPosition();
	}

	public int getIndex() {
		return this.field_13809.getIndex();
	}

	public int getByteSize() {
		return this.field_13809.getSize();
	}

	public int getCount() {
		return this.field_13809.getCount();
	}

	public int hashCode() {
		return this.field_13809.hashCode();
	}

	public boolean equals(Object object) {
		return this.field_13809.equals(object);
	}

	public String toString() {
		return this.field_13809.toString();
	}
}
