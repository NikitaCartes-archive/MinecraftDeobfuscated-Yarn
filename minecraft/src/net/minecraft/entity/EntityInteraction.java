package net.minecraft.entity;

public interface EntityInteraction {
	EntityInteraction field_18474 = create("zombie_villager_cured");
	EntityInteraction field_18475 = create("golem_killed");
	EntityInteraction field_18476 = create("villager_hurt");
	EntityInteraction field_18477 = create("villager_killed");
	EntityInteraction field_18478 = create("trade");

	static EntityInteraction create(String string) {
		return new EntityInteraction() {
			public String toString() {
				return string;
			}
		};
	}
}
