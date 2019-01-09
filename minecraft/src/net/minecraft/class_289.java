package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_289 {
	private final class_287 field_1574;
	private final class_286 field_1572 = new class_286();
	private static final class_289 field_1573 = new class_289(2097152);

	public static class_289 method_1348() {
		return field_1573;
	}

	public class_289(int i) {
		this.field_1574 = new class_287(i);
	}

	public void method_1350() {
		this.field_1574.method_1326();
		this.field_1572.method_1309(this.field_1574);
	}

	public class_287 method_1349() {
		return this.field_1574;
	}
}
