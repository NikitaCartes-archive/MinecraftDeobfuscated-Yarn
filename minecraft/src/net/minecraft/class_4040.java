package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4040 extends class_357 {
	private final class_4067 field_18012;

	public class_4040(class_315 arg, int i, int j, int k, int l, class_4067 arg2) {
		super(arg, i, j, k, l, (double)((float)arg2.method_18611(arg2.method_18613(arg))));
		this.field_18012 = arg2;
		this.updateMessage();
	}

	@Override
	public void renderButton(int i, int j, float f) {
		if (this.field_18012 == class_316.field_1931) {
			this.updateMessage();
		}

		super.renderButton(i, j, f);
	}

	@Override
	protected void applyValue() {
		this.field_18012.method_18614(this.options, this.field_18012.method_18616(this.value));
		this.options.method_1640();
	}

	@Override
	protected void updateMessage() {
		this.setMessage(this.field_18012.method_18619(this.options));
	}
}
