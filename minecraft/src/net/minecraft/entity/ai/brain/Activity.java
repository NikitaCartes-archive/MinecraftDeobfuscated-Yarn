package net.minecraft.entity.ai.brain;

import net.minecraft.util.registry.Registry;

public class Activity {
	public static final Activity CORE = register("core");
	public static final Activity IDLE = register("idle");
	public static final Activity WORK = register("work");
	public static final Activity PLAY = register("play");
	public static final Activity REST = register("rest");
	public static final Activity MEET = register("meet");
	public static final Activity PANIC = register("panic");
	public static final Activity RAID = register("raid");
	public static final Activity PRE_RAID = register("pre_raid");
	public static final Activity HIDE = register("hide");
	public static final Activity FIGHT = register("fight");
	public static final Activity CELEBRATE = register("celebrate");
	public static final Activity ADMIRE_ITEM = register("admire_item");
	public static final Activity AVOID = register("avoid");
	public static final Activity RIDE = register("ride");
	public static final Activity PLAY_DEAD = register("play_dead");
	public static final Activity LONG_JUMP = register("long_jump");
	public static final Activity RAM = register("ram");
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

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			Activity activity = (Activity)o;
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
