/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Activity {
    public static final Activity CORE = Activity.register("core");
    public static final Activity IDLE = Activity.register("idle");
    public static final Activity WORK = Activity.register("work");
    public static final Activity PLAY = Activity.register("play");
    public static final Activity REST = Activity.register("rest");
    public static final Activity MEET = Activity.register("meet");
    public static final Activity PANIC = Activity.register("panic");
    public static final Activity RAID = Activity.register("raid");
    public static final Activity PRE_RAID = Activity.register("pre_raid");
    public static final Activity HIDE = Activity.register("hide");
    private final String id;

    private Activity(String string) {
        this.id = string;
    }

    public String getId() {
        return this.id;
    }

    private static Activity register(String string) {
        return Registry.ACTIVITY.add(new Identifier(string), new Activity(string));
    }

    public String toString() {
        return this.getId();
    }
}

