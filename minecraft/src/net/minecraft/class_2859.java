package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2859 implements class_2596<class_2792> {
	private class_2859.class_2860 field_13021;
	private class_2960 field_13020;

	public class_2859() {
	}

	@Environment(EnvType.CLIENT)
	public class_2859(class_2859.class_2860 arg, @Nullable class_2960 arg2) {
		this.field_13021 = arg;
		this.field_13020 = arg2;
	}

	@Environment(EnvType.CLIENT)
	public static class_2859 method_12418(class_161 arg) {
		return new class_2859(class_2859.class_2860.field_13024, arg.method_688());
	}

	@Environment(EnvType.CLIENT)
	public static class_2859 method_12414() {
		return new class_2859(class_2859.class_2860.field_13023, null);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13021 = arg.method_10818(class_2859.class_2860.class);
		if (this.field_13021 == class_2859.class_2860.field_13024) {
			this.field_13020 = arg.method_10810();
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10817(this.field_13021);
		if (this.field_13021 == class_2859.class_2860.field_13024) {
			arg.method_10812(this.field_13020);
		}
	}

	public void method_12417(class_2792 arg) {
		arg.method_12058(this);
	}

	public class_2859.class_2860 method_12415() {
		return this.field_13021;
	}

	public class_2960 method_12416() {
		return this.field_13020;
	}

	public static enum class_2860 {
		field_13024,
		field_13023;
	}
}
