package net.minecraft;

public class class_1332 extends class_1333 {
	private final int field_6357;

	public class_1332(class_1308 arg, int i) {
		super(arg);
		this.field_6357 = i;
	}

	@Override
	public void method_6231() {
		if (this.field_6360) {
			this.field_6360 = false;
			this.field_6361.field_6241 = this.method_6229(this.field_6361.field_6241, this.method_20251() + 20.0F, this.field_6359);
			this.field_6361.field_5965 = this.method_6229(this.field_6361.field_5965, this.method_20250() + 10.0F, this.field_6358);
		} else {
			if (this.field_6361.method_5942().method_6357()) {
				this.field_6361.field_5965 = this.method_6229(this.field_6361.field_5965, 0.0F, 5.0F);
			}

			this.field_6361.field_6241 = this.method_6229(this.field_6361.field_6241, this.field_6361.field_6283, this.field_6359);
		}

		float f = class_3532.method_15393(this.field_6361.field_6241 - this.field_6361.field_6283);
		if (f < (float)(-this.field_6357)) {
			this.field_6361.field_6283 -= 4.0F;
		} else if (f > (float)this.field_6357) {
			this.field_6361.field_6283 += 4.0F;
		}
	}
}
