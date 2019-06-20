package net.minecraft;

public class class_4168 {
	public static final class_4168 field_18594 = method_19210("core");
	public static final class_4168 field_18595 = method_19210("idle");
	public static final class_4168 field_18596 = method_19210("work");
	public static final class_4168 field_18885 = method_19210("play");
	public static final class_4168 field_18597 = method_19210("rest");
	public static final class_4168 field_18598 = method_19210("meet");
	public static final class_4168 field_18599 = method_19210("panic");
	public static final class_4168 field_19041 = method_19210("raid");
	public static final class_4168 field_19042 = method_19210("pre_raid");
	public static final class_4168 field_19043 = method_19210("hide");
	private final String field_18600;

	private class_4168(String string) {
		this.field_18600 = string;
	}

	public String method_19634() {
		return this.field_18600;
	}

	private static class_4168 method_19210(String string) {
		return class_2378.method_10226(class_2378.field_18796, string, new class_4168(string));
	}

	public String toString() {
		return this.method_19634();
	}
}
