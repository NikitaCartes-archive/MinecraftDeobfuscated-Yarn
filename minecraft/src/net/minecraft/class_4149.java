package net.minecraft;

import java.util.function.Supplier;

public class class_4149<U extends class_4148<?>> {
	public static final class_4149<class_4143> field_18465 = method_19103("dummy", class_4143::new);
	public static final class_4149<class_4146> field_18466 = method_19103("nearest_living_entities", class_4146::new);
	public static final class_4149<class_4147> field_18467 = method_19103("nearest_players", class_4147::new);
	public static final class_4149<class_4145> field_18468 = method_19103("interactable_doors", class_4145::new);
	public static final class_4149<class_4256> field_19010 = method_19103("nearest_bed", class_4256::new);
	public static final class_4149<class_4144> field_18469 = method_19103("hurt_by", class_4144::new);
	public static final class_4149<class_4150> field_18470 = method_19103("villager_hostiles", class_4150::new);
	public static final class_4149<class_4257> field_19011 = method_19103("villager_babies", class_4257::new);
	public static final class_4149<class_4221> field_18875 = method_19103("secondary_pois", class_4221::new);
	public static final class_4149<class_4307> field_19356 = method_19103("golem_last_seen", class_4307::new);
	private final Supplier<U> field_18471;

	private class_4149(Supplier<U> supplier) {
		this.field_18471 = supplier;
	}

	public U method_19102() {
		return (U)this.field_18471.get();
	}

	private static <U extends class_4148<?>> class_4149<U> method_19103(String string, Supplier<U> supplier) {
		return class_2378.method_10230(class_2378.field_18794, new class_2960(string), new class_4149<>(supplier));
	}
}
