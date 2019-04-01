package net.minecraft;

public final class class_747 implements class_3908 {
	private final class_2561 field_3947;
	private final class_1270 field_17280;

	public class_747(class_1270 arg, class_2561 arg2) {
		this.field_17280 = arg;
		this.field_3947 = arg2;
	}

	@Override
	public class_2561 method_5476() {
		return this.field_3947;
	}

	@Override
	public class_1703 createMenu(int i, class_1661 arg, class_1657 arg2) {
		return this.field_17280.createMenu(i, arg, arg2);
	}
}
