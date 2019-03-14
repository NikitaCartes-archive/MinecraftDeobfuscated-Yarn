package net.minecraft.entity.ai.brain;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Activity {
	public static final Activity field_18594 = register("core");
	public static final Activity field_18595 = register("idle");
	public static final Activity field_18596 = register("work");
	public static final Activity field_18597 = register("rest");
	public static final Activity field_18598 = register("meet");
	public static final Activity field_18599 = register("panic");
	private final String id;

	private Activity(String string) {
		this.id = string;
	}

	private static Activity register(String string) {
		return Registry.ACTIVITY.add(new Identifier(string), new Activity(string));
	}
}
