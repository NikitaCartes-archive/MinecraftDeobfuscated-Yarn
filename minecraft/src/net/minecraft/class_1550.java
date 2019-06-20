package net.minecraft;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1550 extends class_1577 {
	public static final float field_17492 = class_1299.field_6086.method_17685() / class_1299.field_6118.method_17685();

	public class_1550(class_1299<? extends class_1550> arg, class_1937 arg2) {
		super(arg, arg2);
		this.method_5971();
		if (this.field_7289 != null) {
			this.field_7289.method_6303(400);
		}
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7357).method_6192(0.3F);
		this.method_5996(class_1612.field_7363).method_6192(8.0);
		this.method_5996(class_1612.field_7359).method_6192(80.0);
	}

	@Override
	public int method_7055() {
		return 60;
	}

	@Environment(EnvType.CLIENT)
	public void method_7010() {
		this.field_7285 = 1.0F;
		this.field_7287 = this.field_7285;
	}

	@Override
	protected class_3414 method_5994() {
		return this.method_5816() ? class_3417.field_15127 : class_3417.field_14569;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return this.method_5816() ? class_3417.field_14868 : class_3417.field_14652;
	}

	@Override
	protected class_3414 method_6002() {
		return this.method_5816() ? class_3417.field_15052 : class_3417.field_14973;
	}

	@Override
	protected class_3414 method_7062() {
		return class_3417.field_14939;
	}

	@Override
	protected void method_5958() {
		super.method_5958();
		int i = 1200;
		if ((this.field_6012 + this.method_5628()) % 1200 == 0) {
			class_1291 lv = class_1294.field_5901;
			List<class_3222> list = ((class_3218)this.field_6002).method_18766(arg -> this.method_5858(arg) < 2500.0 && arg.field_13974.method_14267());
			int j = 2;
			int k = 6000;
			int l = 1200;

			for (class_3222 lv2 : list) {
				if (!lv2.method_6059(lv) || lv2.method_6112(lv).method_5578() < 2 || lv2.method_6112(lv).method_5584() < 1200) {
					lv2.field_13987.method_14364(new class_2668(10, 0.0F));
					lv2.method_6092(new class_1293(lv, 6000, 2));
				}
			}
		}

		if (!this.method_18410()) {
			this.method_18408(new class_2338(this), 16);
		}
	}
}
