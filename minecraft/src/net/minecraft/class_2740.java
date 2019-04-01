package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2740 implements class_2596<class_2602> {
	private int field_12479;
	private int field_12478;

	public class_2740() {
	}

	public class_2740(class_1297 arg, @Nullable class_1297 arg2) {
		this.field_12479 = arg.method_5628();
		this.field_12478 = arg2 != null ? arg2.method_5628() : 0;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12479 = arg.readInt();
		this.field_12478 = arg.readInt();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeInt(this.field_12479);
		arg.writeInt(this.field_12478);
	}

	public void method_11811(class_2602 arg) {
		arg.method_11110(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11812() {
		return this.field_12479;
	}

	@Environment(EnvType.CLIENT)
	public int method_11810() {
		return this.field_12478;
	}
}
