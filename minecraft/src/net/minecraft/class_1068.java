package net.minecraft;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1068 {
	private static final class_2960 field_5301 = new class_2960("textures/entity/steve.png");
	private static final class_2960 field_5300 = new class_2960("textures/entity/alex.png");

	public static class_2960 method_4649() {
		return field_5301;
	}

	public static class_2960 method_4648(UUID uUID) {
		return method_4650(uUID) ? field_5300 : field_5301;
	}

	public static String method_4647(UUID uUID) {
		return method_4650(uUID) ? "slim" : "default";
	}

	private static boolean method_4650(UUID uUID) {
		return (uUID.hashCode() & 1) == 1;
	}
}
