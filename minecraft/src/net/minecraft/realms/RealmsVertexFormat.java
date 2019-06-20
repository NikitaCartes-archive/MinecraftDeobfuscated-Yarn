package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_293;
import net.minecraft.class_296;

@Environment(EnvType.CLIENT)
public class RealmsVertexFormat {
	private class_293 field_23666;

	public RealmsVertexFormat(class_293 arg) {
		this.field_23666 = arg;
	}

	public RealmsVertexFormat from(class_293 arg) {
		this.field_23666 = arg;
		return this;
	}

	public class_293 getVertexFormat() {
		return this.field_23666;
	}

	public void clear() {
		this.field_23666.method_1366();
	}

	public int getUvOffset(int i) {
		return this.field_23666.method_1370(i);
	}

	public int getElementCount() {
		return this.field_23666.method_1363();
	}

	public boolean hasColor() {
		return this.field_23666.method_1369();
	}

	public boolean hasUv(int i) {
		return this.field_23666.method_1367(i);
	}

	public RealmsVertexFormatElement getElement(int i) {
		return new RealmsVertexFormatElement(this.field_23666.method_1364(i));
	}

	public RealmsVertexFormat addElement(RealmsVertexFormatElement realmsVertexFormatElement) {
		return this.from(this.field_23666.method_1361(realmsVertexFormatElement.getVertexFormatElement()));
	}

	public int getColorOffset() {
		return this.field_23666.method_1360();
	}

	public List<RealmsVertexFormatElement> getElements() {
		List<RealmsVertexFormatElement> list = Lists.<RealmsVertexFormatElement>newArrayList();

		for (class_296 lv : this.field_23666.method_1357()) {
			list.add(new RealmsVertexFormatElement(lv));
		}

		return list;
	}

	public boolean hasNormal() {
		return this.field_23666.method_1368();
	}

	public int getVertexSize() {
		return this.field_23666.method_1362();
	}

	public int getOffset(int i) {
		return this.field_23666.method_1365(i);
	}

	public int getNormalOffset() {
		return this.field_23666.method_1358();
	}

	public int getIntegerSize() {
		return this.field_23666.method_1359();
	}

	public boolean equals(Object object) {
		return this.field_23666.equals(object);
	}

	public int hashCode() {
		return this.field_23666.hashCode();
	}

	public String toString() {
		return this.field_23666.toString();
	}
}
