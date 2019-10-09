package net.minecraft;

import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Environment(EnvType.CLIENT)
public class class_4666 extends KeyBinding {
	private final BooleanSupplier field_21334;

	public class_4666(String string, int i, String string2, BooleanSupplier booleanSupplier) {
		super(string, InputUtil.Type.KEYSYM, i, string2);
		this.field_21334 = booleanSupplier;
	}

	@Override
	public void method_23481(boolean bl) {
		if (this.field_21334.getAsBoolean()) {
			if (bl) {
				super.method_23481(!this.isPressed());
			}
		} else {
			super.method_23481(bl);
		}
	}
}
