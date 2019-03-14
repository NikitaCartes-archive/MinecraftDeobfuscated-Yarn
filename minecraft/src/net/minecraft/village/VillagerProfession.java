package net.minecraft.village;

import com.google.common.collect.ImmutableSet;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class VillagerProfession {
	public static final VillagerProfession field_17051 = register("none", PointOfInterestType.field_18502);
	public static final VillagerProfession field_17052 = register("armorer", PointOfInterestType.field_18503);
	public static final VillagerProfession field_17053 = register("butcher", PointOfInterestType.field_18504);
	public static final VillagerProfession field_17054 = register("cartographer", PointOfInterestType.field_18505);
	public static final VillagerProfession field_17055 = register("cleric", PointOfInterestType.field_18506);
	public static final VillagerProfession field_17056 = register(
		"farmer", PointOfInterestType.field_18507, ImmutableSet.of(Items.field_8861, Items.field_8317, Items.field_8309)
	);
	public static final VillagerProfession field_17057 = register("fisherman", PointOfInterestType.field_18508);
	public static final VillagerProfession field_17058 = register("fletcher", PointOfInterestType.field_18509);
	public static final VillagerProfession field_17059 = register("leatherworker", PointOfInterestType.field_18510);
	public static final VillagerProfession field_17060 = register("librarian", PointOfInterestType.field_18511);
	public static final VillagerProfession field_17061 = register("mason", PointOfInterestType.field_18512);
	public static final VillagerProfession field_17062 = register("nitwit", PointOfInterestType.field_18513);
	public static final VillagerProfession field_17063 = register("shepherd", PointOfInterestType.field_18514);
	public static final VillagerProfession field_17064 = register("toolsmith", PointOfInterestType.field_18515);
	public static final VillagerProfession field_17065 = register("weaponsmith", PointOfInterestType.field_18516);
	private final String id;
	private final PointOfInterestType workStation;
	private final ImmutableSet<Item> gatherableItems;

	private VillagerProfession(String string, PointOfInterestType pointOfInterestType, ImmutableSet<Item> immutableSet) {
		this.id = string;
		this.workStation = pointOfInterestType;
		this.gatherableItems = immutableSet;
	}

	public PointOfInterestType getWorkStation() {
		return this.workStation;
	}

	public ImmutableSet<Item> getGatherableItems() {
		return this.gatherableItems;
	}

	public String toString() {
		return this.id;
	}

	static VillagerProfession register(String string, PointOfInterestType pointOfInterestType) {
		return register(string, pointOfInterestType, ImmutableSet.of());
	}

	static VillagerProfession register(String string, PointOfInterestType pointOfInterestType, ImmutableSet<Item> immutableSet) {
		return Registry.register(Registry.VILLAGER_PROFESSION, new Identifier(string), new VillagerProfession(string, pointOfInterestType, immutableSet));
	}
}
