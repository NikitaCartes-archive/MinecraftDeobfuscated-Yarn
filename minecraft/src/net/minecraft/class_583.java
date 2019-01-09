package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_583<T extends class_1297> extends class_3879 {
	public float field_3447;
	public boolean field_3449;
	public boolean field_3448 = true;

	public void method_2819(T arg, float f, float g, float h, float i, float j, float k) {
	}

	public void method_17080(T arg, float f, float g, float h, float i, float j, float k) {
	}

	public void method_2816(T arg, float f, float g, float h) {
	}

	public void method_17081(class_583<T> arg) {
		arg.field_3447 = this.field_3447;
		arg.field_3449 = this.field_3449;
		arg.field_3448 = this.field_3448;
	}
}
