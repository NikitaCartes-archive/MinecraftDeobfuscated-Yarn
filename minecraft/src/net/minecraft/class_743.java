package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_743 extends class_744 {
	private final class_315 field_3902;

	public class_743(class_315 arg) {
		this.field_3902 = arg;
	}

	@Override
	public void method_3129(boolean bl, boolean bl2) {
		this.field_3910 = this.field_3902.field_1894.method_1434();
		this.field_3909 = this.field_3902.field_1881.method_1434();
		this.field_3908 = this.field_3902.field_1913.method_1434();
		this.field_3906 = this.field_3902.field_1849.method_1434();
		this.field_3905 = this.field_3910 == this.field_3909 ? 0.0F : (float)(this.field_3910 ? 1 : -1);
		this.field_3907 = this.field_3908 == this.field_3906 ? 0.0F : (float)(this.field_3908 ? 1 : -1);
		this.field_3904 = this.field_3902.field_1903.method_1434();
		this.field_3903 = this.field_3902.field_1832.method_1434();
		if (!bl2 && (this.field_3903 || bl)) {
			this.field_3907 = (float)((double)this.field_3907 * 0.3);
			this.field_3905 = (float)((double)this.field_3905 * 0.3);
		}
	}
}
