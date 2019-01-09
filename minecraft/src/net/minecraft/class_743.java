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
	public void method_3129() {
		this.field_3907 = 0.0F;
		this.field_3905 = 0.0F;
		if (this.field_3902.field_1894.method_1434()) {
			this.field_3905++;
			this.field_3910 = true;
		} else {
			this.field_3910 = false;
		}

		if (this.field_3902.field_1881.method_1434()) {
			this.field_3905--;
			this.field_3909 = true;
		} else {
			this.field_3909 = false;
		}

		if (this.field_3902.field_1913.method_1434()) {
			this.field_3907++;
			this.field_3908 = true;
		} else {
			this.field_3908 = false;
		}

		if (this.field_3902.field_1849.method_1434()) {
			this.field_3907--;
			this.field_3906 = true;
		} else {
			this.field_3906 = false;
		}

		this.field_3904 = this.field_3902.field_1903.method_1434();
		this.field_3903 = this.field_3902.field_1832.method_1434();
		if (this.field_3903) {
			this.field_3907 = (float)((double)this.field_3907 * 0.3);
			this.field_3905 = (float)((double)this.field_3905 * 0.3);
		}
	}
}
