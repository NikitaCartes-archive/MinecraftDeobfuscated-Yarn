package net.minecraft;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_798 extends class_777 {
	private final class_1058 field_4267;

	public class_798(class_777 arg, class_1058 arg2) {
		super(Arrays.copyOf(arg.method_3357(), arg.method_3357().length), arg.field_4174, class_796.method_3467(arg.method_3357()), arg.method_3356());
		this.field_4267 = arg2;
		this.method_3471();
	}

	private void method_3471() {
		for (int i = 0; i < 4; i++) {
			int j = 7 * i;
			this.field_4175[j + 4] = Float.floatToRawIntBits(
				this.field_4267.method_4580((double)this.field_4176.method_4582(Float.intBitsToFloat(this.field_4175[j + 4])))
			);
			this.field_4175[j + 4 + 1] = Float.floatToRawIntBits(
				this.field_4267.method_4570((double)this.field_4176.method_4572(Float.intBitsToFloat(this.field_4175[j + 4 + 1])))
			);
		}
	}
}
