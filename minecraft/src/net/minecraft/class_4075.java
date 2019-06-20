package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_4075 extends class_4080<class_1059.class_4007> implements AutoCloseable {
	private final class_1059 field_18230;

	public class_4075(class_1060 arg, class_2960 arg2, String string) {
		this.field_18230 = new class_1059(string);
		arg.method_4620(arg2, this.field_18230);
	}

	protected abstract Iterable<class_2960> method_18665();

	protected class_1058 method_18667(class_2960 arg) {
		return this.field_18230.method_4608(arg);
	}

	protected class_1059.class_4007 method_18668(class_3300 arg, class_3695 arg2) {
		arg2.method_16065();
		arg2.method_15396("stitching");
		class_1059.class_4007 lv = this.field_18230.method_18163(arg, this.method_18665(), arg2);
		arg2.method_15407();
		arg2.method_16066();
		return lv;
	}

	protected void method_18666(class_1059.class_4007 arg, class_3300 arg2, class_3695 arg3) {
		arg3.method_16065();
		arg3.method_15396("upload");
		this.field_18230.method_18159(arg);
		arg3.method_15407();
		arg3.method_16066();
	}

	public void close() {
		this.field_18230.method_4601();
	}
}
