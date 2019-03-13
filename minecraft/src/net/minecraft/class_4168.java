package net.minecraft;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_4168 {
	public static final class_4168 field_18594 = method_19210("core");
	public static final class_4168 field_18595 = method_19210("idle");
	public static final class_4168 field_18596 = method_19210("work");
	public static final class_4168 field_18597 = method_19210("rest");
	public static final class_4168 field_18598 = method_19210("meet");
	public static final class_4168 field_18599 = method_19210("panic");
	private final String field_18600;

	private class_4168(String string) {
		this.field_18600 = string;
	}

	private static class_4168 method_19210(String string) {
		return Registry.field_18796.method_10272(new Identifier(string), new class_4168(string));
	}
}
