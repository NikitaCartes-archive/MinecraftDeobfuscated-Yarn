package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2907 implements class_2596<class_2896> {
	private int field_13232;

	public class_2907() {
	}

	public class_2907(int i) {
		this.field_13232 = i;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13232 = arg.method_10816();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_13232);
	}

	public void method_12633(class_2896 arg) {
		arg.method_12585(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_12634() {
		return this.field_13232;
	}
}
