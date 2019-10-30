package net.minecraft.village;

import com.google.common.collect.ImmutableSet;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class VillagerProfession {
	public static final VillagerProfession NONE = register("none", PointOfInterestType.UNEMPLOYED, null);
	public static final VillagerProfession ARMORER = register("armorer", PointOfInterestType.ARMORER, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER);
	public static final VillagerProfession BUTCHER = register("butcher", PointOfInterestType.BUTCHER, SoundEvents.ENTITY_VILLAGER_WORK_BUTCHER);
	public static final VillagerProfession CARTOGRAPHER = register("cartographer", PointOfInterestType.CARTOGRAPHER, SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER);
	public static final VillagerProfession CLERIC = register("cleric", PointOfInterestType.CLERIC, SoundEvents.ENTITY_VILLAGER_WORK_CLERIC);
	public static final VillagerProfession FARMER = register(
		"farmer",
		PointOfInterestType.FARMER,
		ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS),
		ImmutableSet.of(Blocks.FARMLAND),
		SoundEvents.ENTITY_VILLAGER_WORK_FARMER
	);
	public static final VillagerProfession FISHERMAN = register("fisherman", PointOfInterestType.FISHERMAN, SoundEvents.ENTITY_VILLAGER_WORK_FISHERMAN);
	public static final VillagerProfession FLETCHER = register("fletcher", PointOfInterestType.FLETCHER, SoundEvents.ENTITY_VILLAGER_WORK_FLETCHER);
	public static final VillagerProfession LEATHERWORKER = register(
		"leatherworker", PointOfInterestType.LEATHERWORKER, SoundEvents.ENTITY_VILLAGER_WORK_LEATHERWORKER
	);
	public static final VillagerProfession LIBRARIAN = register("librarian", PointOfInterestType.LIBRARIAN, SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN);
	public static final VillagerProfession MASON = register("mason", PointOfInterestType.MASON, SoundEvents.ENTITY_VILLAGER_WORK_MASON);
	public static final VillagerProfession NITWIT = register("nitwit", PointOfInterestType.NITWIT, null);
	public static final VillagerProfession SHEPHERD = register("shepherd", PointOfInterestType.SHEPHERD, SoundEvents.ENTITY_VILLAGER_WORK_SHEPHERD);
	public static final VillagerProfession TOOLSMITH = register("toolsmith", PointOfInterestType.TOOLSMITH, SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH);
	public static final VillagerProfession WEAPONSMITH = register("weaponsmith", PointOfInterestType.WEAPONSMITH, SoundEvents.ENTITY_VILLAGER_WORK_WEAPONSMITH);
	private final String id;
	private final PointOfInterestType workStation;
	private final ImmutableSet<Item> gatherableItems;
	private final ImmutableSet<Block> secondaryJobSites;
	@Nullable
	private final SoundEvent field_20633;

	private VillagerProfession(
		String id, PointOfInterestType workStation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent soundEvent
	) {
		this.id = id;
		this.workStation = workStation;
		this.gatherableItems = gatherableItems;
		this.secondaryJobSites = secondaryJobSites;
		this.field_20633 = soundEvent;
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

	@Nullable
	public SoundEvent method_22384() {
		return this.field_20633;
	}

	public String toString() {
		return this.id;
	}

	static VillagerProfession register(String key, PointOfInterestType pointOfInterestType, @Nullable SoundEvent soundEvent) {
		return register(key, pointOfInterestType, ImmutableSet.of(), ImmutableSet.of(), soundEvent);
	}

	static VillagerProfession register(
		String string, PointOfInterestType pointOfInterestType, ImmutableSet<Item> immutableSet, ImmutableSet<Block> immutableSet2, @Nullable SoundEvent soundEvent
	) {
		return Registry.register(
			Registry.VILLAGER_PROFESSION, new Identifier(string), new VillagerProfession(string, pointOfInterestType, immutableSet, immutableSet2, soundEvent)
		);
	}
}
