package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1645 implements class_1915 {
	private final class_1725 field_7443;
	private final class_1657 field_7441;
	private class_1916 field_7442 = new class_1916();
	private int field_18525;

	public class_1645(class_1657 arg) {
		this.field_7441 = arg;
		this.field_7443 = new class_1725(this);
	}

	@Nullable
	@Override
	public class_1657 method_8257() {
		return this.field_7441;
	}

	@Override
	public void method_8259(@Nullable class_1657 arg) {
	}

	@Override
	public class_1916 method_8264() {
		return this.field_7442;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_8261(@Nullable class_1916 arg) {
		this.field_7442 = arg;
	}

	@Override
	public void method_8262(class_1914 arg) {
		arg.method_8244();
	}

	@Override
	public void method_8258(class_1799 arg) {
	}

	@Override
	public class_1937 method_8260() {
		return this.field_7441.field_6002;
	}

	@Override
	public int method_19269() {
		return this.field_18525;
	}

	@Override
	public void method_19271(int i) {
		this.field_18525 = i;
	}

	@Override
	public boolean method_19270() {
		return true;
	}
}
