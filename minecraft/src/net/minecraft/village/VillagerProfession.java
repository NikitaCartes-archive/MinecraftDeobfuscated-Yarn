package net.minecraft.village;

import com.google.common.collect.ImmutableSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.PointOfInterestTypeTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

public record VillagerProfession(
	String id,
	Predicate<RegistryEntry<PointOfInterestType>> heldWorkstation,
	Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkstation,
	ImmutableSet<Item> gatherableItems,
	ImmutableSet<Block> secondaryJobSites,
	@Nullable SoundEvent workSound
) {
	public static final Predicate<RegistryEntry<PointOfInterestType>> IS_ACQUIRABLE_JOB_SITE = poiType -> poiType.isIn(PointOfInterestTypeTags.ACQUIRABLE_JOB_SITE);
	public static final VillagerProfession NONE = register("none", PointOfInterestType.NONE, IS_ACQUIRABLE_JOB_SITE, null);
	public static final VillagerProfession ARMORER = register("armorer", PointOfInterestTypes.ARMORER, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER);
	public static final VillagerProfession BUTCHER = register("butcher", PointOfInterestTypes.BUTCHER, SoundEvents.ENTITY_VILLAGER_WORK_BUTCHER);
	public static final VillagerProfession CARTOGRAPHER = register(
		"cartographer", PointOfInterestTypes.CARTOGRAPHER, SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER
	);
	public static final VillagerProfession CLERIC = register("cleric", PointOfInterestTypes.CLERIC, SoundEvents.ENTITY_VILLAGER_WORK_CLERIC);
	public static final VillagerProfession FARMER = register(
		"farmer",
		PointOfInterestTypes.FARMER,
		ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.BONE_MEAL),
		ImmutableSet.of(Blocks.FARMLAND),
		SoundEvents.ENTITY_VILLAGER_WORK_FARMER
	);
	public static final VillagerProfession FISHERMAN = register("fisherman", PointOfInterestTypes.FISHERMAN, SoundEvents.ENTITY_VILLAGER_WORK_FISHERMAN);
	public static final VillagerProfession FLETCHER = register("fletcher", PointOfInterestTypes.FLETCHER, SoundEvents.ENTITY_VILLAGER_WORK_FLETCHER);
	public static final VillagerProfession LEATHERWORKER = register(
		"leatherworker", PointOfInterestTypes.LEATHERWORKER, SoundEvents.ENTITY_VILLAGER_WORK_LEATHERWORKER
	);
	public static final VillagerProfession LIBRARIAN = register("librarian", PointOfInterestTypes.LIBRARIAN, SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN);
	public static final VillagerProfession MASON = register("mason", PointOfInterestTypes.MASON, SoundEvents.ENTITY_VILLAGER_WORK_MASON);
	public static final VillagerProfession NITWIT = register("nitwit", PointOfInterestType.NONE, PointOfInterestType.NONE, null);
	public static final VillagerProfession SHEPHERD = register("shepherd", PointOfInterestTypes.SHEPHERD, SoundEvents.ENTITY_VILLAGER_WORK_SHEPHERD);
	public static final VillagerProfession TOOLSMITH = register("toolsmith", PointOfInterestTypes.TOOLSMITH, SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH);
	public static final VillagerProfession WEAPONSMITH = register("weaponsmith", PointOfInterestTypes.WEAPONSMITH, SoundEvents.ENTITY_VILLAGER_WORK_WEAPONSMITH);

	public String toString() {
		return this.id;
	}

	private static VillagerProfession register(String id, RegistryKey<PointOfInterestType> heldWorkstation, @Nullable SoundEvent workSound) {
		return register(id, entry -> entry.matchesKey(heldWorkstation), entry -> entry.matchesKey(heldWorkstation), workSound);
	}

	private static VillagerProfession register(
		String id,
		Predicate<RegistryEntry<PointOfInterestType>> heldWorkstation,
		Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkstation,
		@Nullable SoundEvent workSound
	) {
		return register(id, heldWorkstation, acquirableWorkstation, ImmutableSet.of(), ImmutableSet.of(), workSound);
	}

	private static VillagerProfession register(
		String id,
		RegistryKey<PointOfInterestType> heldWorkstation,
		ImmutableSet<Item> gatherableItems,
		ImmutableSet<Block> secondaryJobSites,
		@Nullable SoundEvent workSound
	) {
		return register(id, entry -> entry.matchesKey(heldWorkstation), entry -> entry.matchesKey(heldWorkstation), gatherableItems, secondaryJobSites, workSound);
	}

	private static VillagerProfession register(
		String id,
		Predicate<RegistryEntry<PointOfInterestType>> heldWorkstation,
		Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkstation,
		ImmutableSet<Item> gatherableItems,
		ImmutableSet<Block> secondaryJobSites,
		@Nullable SoundEvent workSound
	) {
		return Registry.register(
			Registries.VILLAGER_PROFESSION,
			new Identifier(id),
			new VillagerProfession(id, heldWorkstation, acquirableWorkstation, gatherableItems, secondaryJobSites, workSound)
		);
	}
}
