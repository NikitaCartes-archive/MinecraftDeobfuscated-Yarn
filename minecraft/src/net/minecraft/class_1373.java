package net.minecraft;

public class class_1373 extends class_1367 {
	private final class_1451 field_6545;

	public class_1373(class_1451 arg, double d) {
		super(arg, d, 8);
		this.field_6545 = arg;
	}

	@Override
	public boolean method_6264() {
		return this.field_6545.method_6181() && !this.field_6545.method_6172() && super.method_6264();
	}

	@Override
	public void method_6269() {
		super.method_6269();
		this.field_6545.method_6176().method_6311(false);
	}

	@Override
	public void method_6270() {
		super.method_6270();
		this.field_6545.method_6179(false);
	}

	@Override
	public void method_6268() {
		super.method_6268();
		this.field_6545.method_6176().method_6311(false);
		if (!this.method_6295()) {
			this.field_6545.method_6179(false);
		} else if (!this.field_6545.method_6172()) {
			this.field_6545.method_6179(true);
		}
	}

	@Override
	protected boolean method_6296(class_1941 arg, class_2338 arg2) {
		if (!arg.method_8623(arg2.method_10084())) {
			return false;
		} else {
			class_2680 lv = arg.method_8320(arg2);
			class_2248 lv2 = lv.method_11614();
			if (lv2 == class_2246.field_10034) {
				return class_2595.method_11048(arg, arg2) < 1;
			} else {
				return lv2 == class_2246.field_10181 && lv.method_11654(class_3865.field_11105)
					? true
					: lv2.method_9525(class_3481.field_16443) && lv.method_11654(class_2244.field_9967) != class_2742.field_12560;
			}
		}
	}
}
