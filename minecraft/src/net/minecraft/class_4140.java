package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class class_4140<U> {
	public static final class_4140<Void> field_18437 = method_19092("dummy", Optional.empty());
	public static final class_4140<class_4208> field_18438 = method_19092("home", Optional.of(class_4208::method_19444));
	public static final class_4140<class_4208> field_18439 = method_19092("job_site", Optional.of(class_4208::method_19444));
	public static final class_4140<class_4208> field_18440 = method_19092("meeting_point", Optional.of(class_4208::method_19444));
	public static final class_4140<List<class_4208>> field_18873 = method_19092("secondary_job_site", Optional.empty());
	public static final class_4140<List<class_1309>> field_18441 = method_19092("mobs", Optional.empty());
	public static final class_4140<List<class_1309>> field_18442 = method_19092("visible_mobs", Optional.empty());
	public static final class_4140<List<class_1309>> field_19006 = method_19092("visible_villager_babies", Optional.empty());
	public static final class_4140<List<class_1657>> field_18443 = method_19092("nearest_players", Optional.empty());
	public static final class_4140<class_1657> field_18444 = method_19092("nearest_visible_player", Optional.empty());
	public static final class_4140<class_4142> field_18445 = method_19092("walk_target", Optional.empty());
	public static final class_4140<class_4115> field_18446 = method_19092("look_target", Optional.empty());
	public static final class_4140<class_1309> field_18447 = method_19092("interaction_target", Optional.empty());
	public static final class_4140<class_1646> field_18448 = method_19092("breed_target", Optional.empty());
	public static final class_4140<class_11> field_18449 = method_19092("path", Optional.empty());
	public static final class_4140<List<class_4208>> field_18450 = method_19092("interactable_doors", Optional.empty());
	public static final class_4140<class_2338> field_19007 = method_19092("nearest_bed", Optional.empty());
	public static final class_4140<class_1282> field_18451 = method_19092("hurt_by", Optional.empty());
	public static final class_4140<class_1309> field_18452 = method_19092("hurt_by_entity", Optional.empty());
	public static final class_4140<class_1309> field_18453 = method_19092("nearest_hostile", Optional.empty());
	public static final class_4140<class_1646.class_4222> field_18874 = method_19092("golem_spawn_conditions", Optional.empty());
	public static final class_4140<class_4208> field_19008 = method_19092("hiding_place", Optional.empty());
	public static final class_4140<Long> field_19009 = method_19092("heard_bell_time", Optional.empty());
	private final Optional<Function<Dynamic<?>, U>> field_18454;

	private class_4140(Optional<Function<Dynamic<?>, U>> optional) {
		this.field_18454 = optional;
	}

	public class_2960 method_19091() {
		return class_2378.field_18793.method_10221(this);
	}

	public String toString() {
		return this.method_19091().toString();
	}

	public Optional<Function<Dynamic<?>, U>> method_19093() {
		return this.field_18454;
	}

	private static <U> class_4140<U> method_19092(String string, Optional<Function<Dynamic<?>, U>> optional) {
		return class_2378.method_10230(class_2378.field_18793, new class_2960(string), new class_4140<>(optional));
	}
}
