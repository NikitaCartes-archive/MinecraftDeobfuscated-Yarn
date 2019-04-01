package net.minecraft;

import java.util.EnumSet;

public class class_3697 extends class_1367 {
	private final class_1451 field_16282;

	public class_3697(class_1451 arg, double d, int i) {
		super(arg, d, i, 6);
		this.field_16282 = arg;
		this.field_6515 = -2;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18407, class_1352.class_4134.field_18405));
	}

	@Override
	public boolean method_6264() {
		return this.field_16282.method_6181() && !this.field_16282.method_6172() && !this.field_16282.method_16086() && super.method_6264();
	}

	@Override
	public void method_6269() {
		super.method_6269();
		this.field_16282.method_6176().method_6311(false);
	}

	@Override
	protected int method_6293(class_1314 arg) {
		return 40;
	}

	@Override
	public void method_6270() {
		super.method_6270();
		this.field_16282.method_16088(false);
	}

	@Override
	public void method_6268() {
		super.method_6268();
		this.field_16282.method_6176().method_6311(false);
		if (!this.method_6295()) {
			this.field_16282.method_16088(false);
		} else if (!this.field_16282.method_16086()) {
			this.field_16282.method_16088(true);
		}
	}

	@Override
	protected boolean method_6296(class_1941 arg, class_2338 arg2) {
		return arg.method_8623(arg2.method_10084()) && arg.method_8320(arg2).method_11614().method_9525(class_3481.field_16443);
	}
}
