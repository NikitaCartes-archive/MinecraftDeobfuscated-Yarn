package net.minecraft;

import java.util.EnumSet;
import java.util.function.Predicate;

public class class_1345 extends class_1352 {
	private static final Predicate<class_2680> field_6423 = class_2715.method_11758(class_2246.field_10479);
	private final class_1308 field_6424;
	private final class_1937 field_6421;
	private int field_6422;

	public class_1345(class_1308 arg) {
		this.field_6424 = arg;
		this.field_6421 = arg.field_6002;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18405, class_1352.class_4134.field_18406, class_1352.class_4134.field_18407));
	}

	@Override
	public boolean method_6264() {
		if (this.field_6424.method_6051().nextInt(this.field_6424.method_6109() ? 50 : 1000) != 0) {
			return false;
		} else {
			class_2338 lv = new class_2338(this.field_6424.field_5987, this.field_6424.field_6010, this.field_6424.field_6035);
			return field_6423.test(this.field_6421.method_8320(lv)) ? true : this.field_6421.method_8320(lv.method_10074()).method_11614() == class_2246.field_10219;
		}
	}

	@Override
	public void method_6269() {
		this.field_6422 = 40;
		this.field_6421.method_8421(this.field_6424, (byte)10);
		this.field_6424.method_5942().method_6340();
	}

	@Override
	public void method_6270() {
		this.field_6422 = 0;
	}

	@Override
	public boolean method_6266() {
		return this.field_6422 > 0;
	}

	public int method_6258() {
		return this.field_6422;
	}

	@Override
	public void method_6268() {
		this.field_6422 = Math.max(0, this.field_6422 - 1);
		if (this.field_6422 == 4) {
			class_2338 lv = new class_2338(this.field_6424.field_5987, this.field_6424.field_6010, this.field_6424.field_6035);
			if (field_6423.test(this.field_6421.method_8320(lv))) {
				if (this.field_6421.method_8450().method_8355("mobGriefing")) {
					this.field_6421.method_8651(lv, false);
				}

				this.field_6424.method_5983();
			} else {
				class_2338 lv2 = lv.method_10074();
				if (this.field_6421.method_8320(lv2).method_11614() == class_2246.field_10219) {
					if (this.field_6421.method_8450().method_8355("mobGriefing")) {
						this.field_6421.method_8535(2001, lv2, class_2248.method_9507(class_2246.field_10219.method_9564()));
						this.field_6421.method_8652(lv2, class_2246.field_10566.method_9564(), 2);
					}

					this.field_6424.method_5983();
				}
			}
		}
	}
}
