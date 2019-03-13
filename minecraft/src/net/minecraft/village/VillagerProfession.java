package net.minecraft.village;

import com.google.common.collect.ImmutableSet;
import net.minecraft.class_4158;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class VillagerProfession {
	public static final VillagerProfession field_17051 = register("none", class_4158.field_18502);
	public static final VillagerProfession field_17052 = register("armorer", class_4158.field_18503);
	public static final VillagerProfession field_17053 = register("butcher", class_4158.field_18504);
	public static final VillagerProfession field_17054 = register("cartographer", class_4158.field_18505);
	public static final VillagerProfession field_17055 = register("cleric", class_4158.field_18506);
	public static final VillagerProfession field_17056 = method_19197(
		"farmer", class_4158.field_18507, ImmutableSet.of(Items.field_8861, Items.field_8317, Items.field_8309)
	);
	public static final VillagerProfession field_17057 = register("fisherman", class_4158.field_18508);
	public static final VillagerProfession field_17058 = register("fletcher", class_4158.field_18509);
	public static final VillagerProfession field_17059 = register("leatherworker", class_4158.field_18510);
	public static final VillagerProfession field_17060 = register("librarian", class_4158.field_18511);
	public static final VillagerProfession field_17061 = register("mason", class_4158.field_18512);
	public static final VillagerProfession field_17062 = register("nitwit", class_4158.field_18513);
	public static final VillagerProfession field_17063 = register("shepherd", class_4158.field_18514);
	public static final VillagerProfession field_17064 = register("toolsmith", class_4158.field_18515);
	public static final VillagerProfession field_17065 = register("weaponsmith", class_4158.field_18516);
	private final String field_18541;
	private final class_4158 field_18542;
	private final ImmutableSet<Item> field_18543;

	private VillagerProfession(String string, class_4158 arg, ImmutableSet<Item> immutableSet) {
		this.field_18541 = string;
		this.field_18542 = arg;
		this.field_18543 = immutableSet;
	}

	public class_4158 method_19198() {
		return this.field_18542;
	}

	public ImmutableSet<Item> method_19199() {
		return this.field_18543;
	}

	public String toString() {
		return this.field_18541;
	}

	static VillagerProfession register(String string, class_4158 arg) {
		return method_19197(string, arg, ImmutableSet.of());
	}

	static VillagerProfession method_19197(String string, class_4158 arg, ImmutableSet<Item> immutableSet) {
		return Registry.method_10230(Registry.VILLAGER_PROFESSION, new Identifier(string), new VillagerProfession(string, arg, immutableSet));
	}
}
