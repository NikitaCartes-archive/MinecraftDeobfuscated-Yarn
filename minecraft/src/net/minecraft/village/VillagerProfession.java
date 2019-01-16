package net.minecraft.village;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface VillagerProfession {
	VillagerProfession field_17051 = register("none");
	VillagerProfession field_17052 = register("armorer");
	VillagerProfession field_17053 = register("butcher");
	VillagerProfession field_17054 = register("cartographer");
	VillagerProfession field_17055 = register("cleric");
	VillagerProfession field_17056 = register("farmer");
	VillagerProfession field_17057 = register("fisherman");
	VillagerProfession field_17058 = register("fletcher");
	VillagerProfession field_17059 = register("leatherworker");
	VillagerProfession field_17060 = register("librarian");
	VillagerProfession field_17061 = register("mason");
	VillagerProfession field_17062 = register("nitwit");
	VillagerProfession field_17063 = register("shepherd");
	VillagerProfession field_17064 = register("toolsmith");
	VillagerProfession field_17065 = register("weaponsmith");

	static VillagerProfession register(String string) {
		return Registry.register(Registry.VILLAGER_PROFESSION, new Identifier(string), new VillagerProfession() {
			public String toString() {
				return string;
			}
		});
	}
}
