package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_521 extends class_350<class_520> {
	protected final class_310 field_3166;

	public class_521(class_310 arg, int i, int j) {
		super(arg, i, j, 32, j - 55 + 4, 36);
		this.field_3166 = arg;
		this.field_2173 = false;
		this.method_1927(true, (int)(9.0F * 1.5F));
	}

	@Override
	protected void method_1940(int i, int j, class_289 arg) {
		String string = class_124.field_1073 + "" + class_124.field_1067 + this.method_2689();
		this.field_3166
			.field_1772
			.method_1729(
				string, (float)(i + this.field_2168 / 2 - this.field_3166.field_1772.method_1727(string) / 2), (float)Math.min(this.field_2166 + 3, j), 16777215
			);
	}

	protected abstract String method_2689();

	@Override
	public int method_1932() {
		return this.field_2168;
	}

	@Override
	protected int method_1948() {
		return this.field_2181 - 6;
	}

	public void method_2690(class_520 arg) {
		super.method_1901(arg);
	}
}
