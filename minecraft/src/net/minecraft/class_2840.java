package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2840 implements class_2596<class_2792> {
	private int field_12933;
	private class_2960 field_12931;
	private boolean field_12932;

	public class_2840() {
	}

	@Environment(EnvType.CLIENT)
	public class_2840(int i, class_1860<?> arg, boolean bl) {
		this.field_12933 = i;
		this.field_12931 = arg.method_8114();
		this.field_12932 = bl;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12933 = arg.readByte();
		this.field_12931 = arg.method_10810();
		this.field_12932 = arg.readBoolean();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeByte(this.field_12933);
		arg.method_10812(this.field_12931);
		arg.writeBoolean(this.field_12932);
	}

	public void method_12317(class_2792 arg) {
		arg.method_12061(this);
	}

	public int method_12318() {
		return this.field_12933;
	}

	public class_2960 method_12320() {
		return this.field_12931;
	}

	public boolean method_12319() {
		return this.field_12932;
	}
}
