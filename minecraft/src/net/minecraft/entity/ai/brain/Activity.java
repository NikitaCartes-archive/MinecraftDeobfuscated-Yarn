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
	public static final Activity field_22396 = register("fight");
	public static final Activity field_22397 = register("celebrate");
	public static final Activity field_22398 = register("admire_item");
	public static final Activity field_22399 = register("avoid");
	public static final Activity field_22400 = register("ride");
	private final String id;
	private final int hashCode;

	private Activity(String id) {
		this.id = id;
		this.hashCode = id.hashCode();
	}

	public String getId() {
		return this.id;
	}

	private static Activity register(String id) {
		return Registry.register(Registry.ACTIVITY, id, new Activity(id));
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			Activity activity = (Activity)object;
			return this.id.equals(activity.id);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.hashCode;
	}

	public String toString() {
		return this.getId();
	}
}
