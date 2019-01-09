package net.minecraft;

import java.util.function.Predicate;

public enum class_242 {
	field_1348(arg -> false),
	field_1345(class_3610::method_15771),
	field_1347(arg -> !arg.method_15769());

	public final Predicate<class_3610> field_1346;

	private class_242(Predicate<class_3610> predicate) {
		this.field_1346 = predicate;
	}
}
