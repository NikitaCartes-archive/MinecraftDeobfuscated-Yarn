package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2848 implements class_2596<class_2792> {
	private int field_12977;
	private class_2848.class_2849 field_12978;
	private int field_12976;

	public class_2848() {
	}

	@Environment(EnvType.CLIENT)
	public class_2848(class_1297 arg, class_2848.class_2849 arg2) {
		this(arg, arg2, 0);
	}

	@Environment(EnvType.CLIENT)
	public class_2848(class_1297 arg, class_2848.class_2849 arg2, int i) {
		this.field_12977 = arg.method_5628();
		this.field_12978 = arg2;
		this.field_12976 = i;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12977 = arg.method_10816();
		this.field_12978 = arg.method_10818(class_2848.class_2849.class);
		this.field_12976 = arg.method_10816();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12977);
		arg.method_10817(this.field_12978);
		arg.method_10804(this.field_12976);
	}

	public void method_12364(class_2792 arg) {
		arg.method_12045(this);
	}

	public class_2848.class_2849 method_12365() {
		return this.field_12978;
	}

	public int method_12366() {
		return this.field_12976;
	}

	public static enum class_2849 {
		field_12979,
		field_12984,
		field_12986,
		field_12981,
		field_12985,
		field_12987,
		field_12980,
		field_12988,
		field_12982;
	}
}
