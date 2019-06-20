package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3916 extends class_1703 {
	private final class_1263 field_17313;
	private final class_3913 field_17314;

	public class_3916(int i) {
		this(i, new class_1277(1), new class_3919(1));
	}

	public class_3916(int i, class_1263 arg, class_3913 arg2) {
		super(class_3917.field_17338, i);
		method_17359(arg, 1);
		method_17361(arg2, 1);
		this.field_17313 = arg;
		this.field_17314 = arg2;
		this.method_7621(new class_1735(arg, 0, 0, 0) {
			@Override
			public void method_7668() {
				super.method_7668();
				class_3916.this.method_7609(this.field_7871);
			}
		});
		this.method_17360(arg2);
	}

	@Override
	public boolean method_7604(class_1657 arg, int i) {
		if (i >= 100) {
			int j = i - 100;
			this.method_7606(0, j);
			return true;
		} else {
			switch (i) {
				case 1: {
					int j = this.field_17314.method_17390(0);
					this.method_7606(0, j - 1);
					return true;
				}
				case 2: {
					int j = this.field_17314.method_17390(0);
					this.method_7606(0, j + 1);
					return true;
				}
				case 3:
					if (!arg.method_7294()) {
						return false;
					}

					class_1799 lv = this.field_17313.method_5441(0);
					this.field_17313.method_5431();
					if (!arg.field_7514.method_7394(lv)) {
						arg.method_7328(lv, false);
					}

					return true;
				default:
					return false;
			}
		}
	}

	@Override
	public void method_7606(int i, int j) {
		super.method_7606(i, j);
		this.method_7623();
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return this.field_17313.method_5443(arg);
	}

	@Environment(EnvType.CLIENT)
	public class_1799 method_17418() {
		return this.field_17313.method_5438(0);
	}

	@Environment(EnvType.CLIENT)
	public int method_17419() {
		return this.field_17314.method_17390(0);
	}
}
