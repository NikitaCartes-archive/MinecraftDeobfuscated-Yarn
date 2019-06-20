package net.minecraft;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum class_1659 {
	field_7538(0, "options.chat.visibility.full"),
	field_7539(1, "options.chat.visibility.system"),
	field_7536(2, "options.chat.visibility.hidden");

	private static final class_1659[] field_7534 = (class_1659[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(class_1659::method_7362))
		.toArray(class_1659[]::new);
	private final int field_7535;
	private final String field_7540;

	private class_1659(int j, String string2) {
		this.field_7535 = j;
		this.field_7540 = string2;
	}

	public int method_7362() {
		return this.field_7535;
	}

	@Environment(EnvType.CLIENT)
	public String method_7359() {
		return this.field_7540;
	}

	@Environment(EnvType.CLIENT)
	public static class_1659 method_7360(int i) {
		return field_7534[class_3532.method_15387(i, field_7534.length)];
	}
}
