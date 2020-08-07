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
import net.minecraft.world.poi.PointOfInterestType;

public class VillagerProfession {
	public static final VillagerProfession field_17051 = register("none", PointOfInterestType.field_18502, null);
	public static final VillagerProfession field_17052 = register("armorer", PointOfInterestType.field_18503, SoundEvents.field_20669);
	public static final VillagerProfession field_17053 = register("butcher", PointOfInterestType.field_18504, SoundEvents.field_20670);
	public static final VillagerProfession field_17054 = register("cartographer", PointOfInterestType.field_18505, SoundEvents.field_20671);
	public static final VillagerProfession field_17055 = register("cleric", PointOfInterestType.field_18506, SoundEvents.field_20672);
	public static final VillagerProfession field_17056 = register(
		"farmer",
		PointOfInterestType.field_18507,
		ImmutableSet.of(Items.field_8861, Items.field_8317, Items.field_8309, Items.field_8324),
		ImmutableSet.of(Blocks.field_10362),
		SoundEvents.field_20673
	);
	public static final VillagerProfession field_17057 = register("fisherman", PointOfInterestType.field_18508, SoundEvents.field_20674);
	public static final VillagerProfession field_17058 = register("fletcher", PointOfInterestType.field_18509, SoundEvents.field_20675);
	public static final VillagerProfession field_17059 = register("leatherworker", PointOfInterestType.field_18510, SoundEvents.field_20676);
	public static final VillagerProfession field_17060 = register("librarian", PointOfInterestType.field_18511, SoundEvents.field_20677);
	public static final VillagerProfession field_17061 = register("mason", PointOfInterestType.field_18512, SoundEvents.field_20678);
	public static final VillagerProfession field_17062 = register("nitwit", PointOfInterestType.field_18513, null);
	public static final VillagerProfession field_17063 = register("shepherd", PointOfInterestType.field_18514, SoundEvents.field_20679);
	public static final VillagerProfession field_17064 = register("toolsmith", PointOfInterestType.field_18515, SoundEvents.field_20680);
	public static final VillagerProfession field_17065 = register("weaponsmith", PointOfInterestType.field_18516, SoundEvents.field_20681);
	private final String id;
	private final PointOfInterestType workStation;
	private final ImmutableSet<Item> gatherableItems;
	private final ImmutableSet<Block> secondaryJobSites;
	@Nullable
	private final SoundEvent workSound;

	private VillagerProfession(
		String id, PointOfInterestType workStation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound
	) {
		this.id = id;
		this.workStation = workStation;
		this.gatherableItems = gatherableItems;
		this.secondaryJobSites = secondaryJobSites;
		this.workSound = workSound;
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
	public SoundEvent getWorkSound() {
		return this.workSound;
	}

	public String toString() {
		return this.id;
	}

	static VillagerProfession register(String id, PointOfInterestType workStation, @Nullable SoundEvent workSound) {
		return register(id, workStation, ImmutableSet.of(), ImmutableSet.of(), workSound);
	}

	static VillagerProfession register(
		String id, PointOfInterestType workStation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound
	) {
		return Registry.register(
			Registry.VILLAGER_PROFESSION, new Identifier(id), new VillagerProfession(id, workStation, gatherableItems, secondaryJobSites, workSound)
		);
	}
}
