package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2868 implements class_2596<class_2792> {
	private int field_13052;

	public class_2868() {
	}

	@Environment(EnvType.CLIENT)
	public class_2868(int i) {
		this.field_13052 = i;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13052 = arg.readShort();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeShort(this.field_13052);
	}

	public void method_12441(class_2792 arg) {
		arg.method_12056(this);
	}

	public int method_12442() {
		return this.field_13052;
	}
}
