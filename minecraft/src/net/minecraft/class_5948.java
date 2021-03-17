package net.minecraft;

import java.util.function.DoubleSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_5948 {
	private final class_5951 field_29548;
	private final DoubleSupplier field_29549;
	private final class_5949 field_29550;

	public class_5948(class_5951 arg, DoubleSupplier doubleSupplier, class_5949 arg2) {
		this.field_29548 = arg;
		this.field_29549 = doubleSupplier;
		this.field_29550 = arg2;
	}

	public class_5951 method_34697() {
		return this.field_29548;
	}

	public DoubleSupplier method_34698() {
		return this.field_29549;
	}

	public class_5949 method_34699() {
		return this.field_29550;
	}
}
