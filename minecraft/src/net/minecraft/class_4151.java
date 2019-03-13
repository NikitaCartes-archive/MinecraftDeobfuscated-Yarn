package net.minecraft;

public interface class_4151 {
	class_4151 field_18474 = method_19109("zombie_villager_cured");
	class_4151 field_18475 = method_19109("golem_killed");
	class_4151 field_18476 = method_19109("villager_hurt");
	class_4151 field_18477 = method_19109("villager_killed");
	class_4151 field_18478 = method_19109("trade");

	static class_4151 method_19109(String string) {
		return new class_4151() {
			public String toString() {
				return string;
			}
		};
	}
}
