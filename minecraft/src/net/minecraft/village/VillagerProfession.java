package net.minecraft.village;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.poi.PointOfInterestType;

public class VillagerProfession {
	public static final VillagerProfession NONE = register("none", PointOfInterestType.UNEMPLOYED);
	public static final VillagerProfession ARMORER = register("armorer", PointOfInterestType.ARMORER);
	public static final VillagerProfession BUTCHER = register("butcher", PointOfInterestType.BUTCHER);
	public static final VillagerProfession CARTOGRAPHER = register("cartographer", PointOfInterestType.CARTOGRAPHER);
	public static final VillagerProfession CLERIC = register("cleric", PointOfInterestType.CLERIC);
	public static final VillagerProfession FARMER = register(
		"farmer", PointOfInterestType.FARMER, ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS), ImmutableSet.of(Blocks.FARMLAND)
	);
	public static final VillagerProfession FISHERMAN = register("fisherman", PointOfInterestType.FISHERMAN);
	public static final VillagerProfession FLETCHER = register("fletcher", PointOfInterestType.FLETCHER);
	public static final VillagerProfession LEATHERWORKER = register("leatherworker", PointOfInterestType.LEATHERWORKER);
	public static final VillagerProfession LIBRARIAN = register("librarian", PointOfInterestType.LIBRARIAN);
	public static final VillagerProfession MASON = register("mason", PointOfInterestType.MASON);
	public static final VillagerProfession NITWIT = register("nitwit", PointOfInterestType.NITWIT);
	public static final VillagerProfession SHEPHERD = register("shepherd", PointOfInterestType.SHEPHERD);
	public static final VillagerProfession TOOLSMITH = register("toolsmith", PointOfInterestType.TOOLSMITH);
	public static final VillagerProfession WEAPONSMITH = register("weaponsmith", PointOfInterestType.WEAPONSMITH);
	private final String id;
	private final PointOfInterestType workStation;
	private final ImmutableSet<Item> gatherableItems;
	private final ImmutableSet<Block> secondaryJobSites;

	private VillagerProfession(String id, PointOfInterestType workStation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites) {
		this.id = id;
		this.workStation = workStation;
		this.gatherableItems = gatherableItems;
		this.secondaryJobSites = secondaryJobSites;
	}

	public PointOfInterestType getWorkStation() {
		return this.workStation;
	}

	public ImmutableSet<Item> getGatherableItems() {
		return this.gatherableItems;
	}

	public ImmutableSet<Block> getSecondaryJobSites() {
		return this.secondaryJobSites;
	}

	public String toString() {
		return this.id;
	}

	static VillagerProfession register(String key, PointOfInterestType pointOfInterestType) {
		return register(key, pointOfInterestType, ImmutableSet.of(), ImmutableSet.of());
	}

	static VillagerProfession register(String string, PointOfInterestType pointOfInterestType, ImmutableSet<Item> immutableSet, ImmutableSet<Block> immutableSet2) {
		return Registry.register(
			Registry.VILLAGER_PROFESSION, new Identifier(string), new VillagerProfession(string, pointOfInterestType, immutableSet, immutableSet2)
		);
	}
}
