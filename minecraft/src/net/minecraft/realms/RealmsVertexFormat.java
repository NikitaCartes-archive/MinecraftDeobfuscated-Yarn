package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;

@Environment(EnvType.CLIENT)
public class RealmsVertexFormat {
	private VertexFormat field_5663;

	public RealmsVertexFormat(VertexFormat vertexFormat) {
		this.field_5663 = vertexFormat;
	}

	public RealmsVertexFormat from(VertexFormat vertexFormat) {
		this.field_5663 = vertexFormat;
		return this;
	}

	public VertexFormat getVertexFormat() {
		return this.field_5663;
	}

	public void clear() {
		this.field_5663.clear();
	}

	public int getUvOffset(int i) {
		return this.field_5663.getUvOffset(i);
	}

	public int getElementCount() {
		return this.field_5663.getElementCount();
	}

	public boolean hasColor() {
		return this.field_5663.hasColorElement();
	}

	public boolean hasUv(int i) {
		return this.field_5663.hasUvElement(i);
	}

	public RealmsVertexFormatElement getElement(int i) {
		return new RealmsVertexFormatElement(this.field_5663.method_1364(i));
	}

	public RealmsVertexFormat addElement(RealmsVertexFormatElement realmsVertexFormatElement) {
		return this.from(this.field_5663.method_1361(realmsVertexFormatElement.getVertexFormatElement()));
	}

	public int getColorOffset() {
		return this.field_5663.getColorOffset();
	}

	public List<RealmsVertexFormatElement> getElements() {
		List<RealmsVertexFormatElement> list = Lists.<RealmsVertexFormatElement>newArrayList();

		for (VertexFormatElement vertexFormatElement : this.field_5663.getElements()) {
			list.add(new RealmsVertexFormatElement(vertexFormatElement));
		}

		return list;
	}

	public boolean hasNormal() {
		return this.field_5663.hasNormalElement();
	}

	public int getVertexSize() {
		return this.field_5663.getVertexSize();
	}

	public int getOffset(int i) {
		return this.field_5663.getElementOffset(i);
	}

	public int getNormalOffset() {
		return this.field_5663.getNormalOffset();
	}

	public int getIntegerSize() {
		return this.field_5663.getVertexSizeInteger();
	}

	public boolean equals(Object object) {
		return this.field_5663.equals(object);
	}

	public int hashCode() {
		return this.field_5663.hashCode();
	}

	public String toString() {
		return this.field_5663.toString();
	}
}
