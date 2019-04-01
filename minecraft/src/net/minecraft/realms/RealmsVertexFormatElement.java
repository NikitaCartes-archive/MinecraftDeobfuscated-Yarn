package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_296;

@Environment(EnvType.CLIENT)
public class RealmsVertexFormatElement {
	private final class_296 field_13809;

	public RealmsVertexFormatElement(class_296 arg) {
		this.field_13809 = arg;
	}

	public class_296 getVertexFormatElement() {
		return this.field_13809;
	}

	public boolean isPosition() {
		return this.field_13809.method_1388();
	}

	public int getIndex() {
		return this.field_13809.method_1385();
	}

	public int getByteSize() {
		return this.field_13809.method_1387();
	}

	public int getCount() {
		return this.field_13809.method_1384();
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
