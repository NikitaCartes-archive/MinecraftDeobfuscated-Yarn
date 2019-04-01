package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_444 extends class_357 {
	private final class_3419 field_2622;

	public class_444(class_310 arg, int i, int j, class_3419 arg2, int k) {
		super(arg.field_1690, i, j, k, 20, (double)arg.field_1690.method_1630(arg2));
		this.field_2622 = arg2;
		this.updateMessage();
	}

	@Override
	protected void updateMessage() {
		String string = (float)this.value == (float)this.getYImage(false) ? class_1074.method_4662("options.off") : (int)((float)this.value * 100.0F) + "%";
		this.setMessage(class_1074.method_4662("soundCategory." + this.field_2622.method_14840()) + ": " + string);
	}

	@Override
	protected void applyValue() {
		this.options.method_1624(this.field_2622, (float)this.value);
		this.options.method_1640();
	}
}
