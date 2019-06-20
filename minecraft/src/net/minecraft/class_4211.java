package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_4211 implements class_2596<class_2792> {
	private boolean field_18806;

	public class_4211() {
	}

	@Environment(EnvType.CLIENT)
	public class_4211(boolean bl) {
		this.field_18806 = bl;
	}

	public void method_19484(class_2792 arg) {
		arg.method_19476(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_18806 = arg.readBoolean();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeBoolean(this.field_18806);
	}

	public boolean method_19485() {
		return this.field_18806;
	}
}
