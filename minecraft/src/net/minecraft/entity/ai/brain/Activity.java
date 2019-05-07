package net.minecraft.entity.ai.brain;

import net.minecraft.util.registry.Registry;

public class Activity {
	public static final Activity field_18594 = register("core");
	public static final Activity field_18595 = register("idle");
	public static final Activity field_18596 = register("work");
	public static final Activity field_18885 = register("play");
	public static final Activity field_18597 = register("rest");
	public static final Activity field_18598 = register("meet");
	public static final Activity field_18599 = register("panic");
	public static final Activity field_19041 = register("raid");
	public static final Activity field_19042 = register("pre_raid");
	public static final Activity field_19043 = register("hide");
	private final String id;

	private Activity(String string) {
		this.id = string;
	}

	public String getId() {
		return this.id;
	}

	private static Activity register(String string) {
		return Registry.register(Registry.ACTIVITY, string, new Activity(string));
	}

	public String toString() {
		return this.getId();
	}
}
