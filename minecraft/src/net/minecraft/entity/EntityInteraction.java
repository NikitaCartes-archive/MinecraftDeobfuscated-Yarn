package net.minecraft.entity;

public interface EntityInteraction {
	EntityInteraction ZOMBIE_VILLAGER_CURED = create("zombie_villager_cured");
	EntityInteraction GOLEM_KILLED = create("golem_killed");
	EntityInteraction VILLAGER_HURT = create("villager_hurt");
	EntityInteraction VILLAGER_KILLED = create("villager_killed");
	EntityInteraction TRADE = create("trade");

	static EntityInteraction create(String key) {
		return new EntityInteraction() {
			public String toString() {
				return key;
			}
		};
	}
}
