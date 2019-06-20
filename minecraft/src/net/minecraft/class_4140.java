package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class class_4140<U> {
	public static final class_4140<Void> field_18437 = method_20738("dummy");
	public static final class_4140<class_4208> field_18438 = method_19092("home", Optional.of(class_4208::method_19444));
	public static final class_4140<class_4208> field_18439 = method_19092("job_site", Optional.of(class_4208::method_19444));
	public static final class_4140<class_4208> field_18440 = method_19092("meeting_point", Optional.of(class_4208::method_19444));
	public static final class_4140<List<class_4208>> field_18873 = method_20738("secondary_job_site");
	public static final class_4140<List<class_1309>> field_18441 = method_20738("mobs");
	public static final class_4140<List<class_1309>> field_18442 = method_20738("visible_mobs");
	public static final class_4140<List<class_1309>> field_19006 = method_20738("visible_villager_babies");
	public static final class_4140<List<class_1657>> field_18443 = method_20738("nearest_players");
	public static final class_4140<class_1657> field_18444 = method_20738("nearest_visible_player");
	public static final class_4140<class_4142> field_18445 = method_20738("walk_target");
	public static final class_4140<class_4115> field_18446 = method_20738("look_target");
	public static final class_4140<class_1309> field_18447 = method_20738("interaction_target");
	public static final class_4140<class_1646> field_18448 = method_20738("breed_target");
	public static final class_4140<class_11> field_18449 = method_20738("path");
	public static final class_4140<List<class_4208>> field_18450 = method_20738("interactable_doors");
	public static final class_4140<class_2338> field_19007 = method_20738("nearest_bed");
	public static final class_4140<class_1282> field_18451 = method_20738("hurt_by");
	public static final class_4140<class_1309> field_18452 = method_20738("hurt_by_entity");
	public static final class_4140<class_1309> field_18453 = method_20738("nearest_hostile");
	public static final class_4140<class_4208> field_19008 = method_20738("hiding_place");
	public static final class_4140<Long> field_19009 = method_20738("heard_bell_time");
	public static final class_4140<Long> field_19293 = method_20738("cant_reach_walk_target_since");
	public static final class_4140<Long> field_19355 = method_20738("golem_last_seen_time");
	public static final class_4140<class_4316> field_19385 = method_19092("last_slept", Optional.of(class_4316::method_20792));
	public static final class_4140<class_4316> field_19386 = method_19092("last_worked_at_poi", Optional.of(class_4316::method_20792));
	private final Optional<Function<Dynamic<?>, U>> field_18454;

	private class_4140(Optional<Function<Dynamic<?>, U>> optional) {
		this.field_18454 = optional;
	}

	public String toString() {
		return class_2378.field_18793.method_10221(this).toString();
	}

	public Optional<Function<Dynamic<?>, U>> method_19093() {
		return this.field_18454;
	}

	private static <U extends class_4213> class_4140<U> method_19092(String string, Optional<Function<Dynamic<?>, U>> optional) {
		return class_2378.method_10230(class_2378.field_18793, new class_2960(string), new class_4140<>(optional));
	}

	private static <U> class_4140<U> method_20738(String string) {
		return class_2378.method_10230(class_2378.field_18793, new class_2960(string), new class_4140<>(Optional.empty()));
	}
}
