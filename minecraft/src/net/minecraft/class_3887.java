package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_3887<T extends class_1297, M extends class_583<T>> {
	private final class_3883<T, M> field_17155;

	public class_3887(class_3883<T, M> arg) {
		this.field_17155 = arg;
	}

	public M method_17165() {
		return this.field_17155.method_4038();
	}

	public void method_17164(class_2960 arg) {
		this.field_17155.method_3924(arg);
	}

	public void method_17163(T arg) {
		this.field_17155.method_17146(arg);
	}

	public abstract void method_4199(T arg, float f, float g, float h, float i, float j, float k, float l);

	public abstract boolean method_4200();
}
