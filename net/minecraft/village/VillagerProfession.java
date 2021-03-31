/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.village;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;

public class VillagerProfession {
    public static final VillagerProfession NONE = VillagerProfession.register("none", PointOfInterestType.UNEMPLOYED, null);
    public static final VillagerProfession ARMORER = VillagerProfession.register("armorer", PointOfInterestType.ARMORER, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER);
    public static final VillagerProfession BUTCHER = VillagerProfession.register("butcher", PointOfInterestType.BUTCHER, SoundEvents.ENTITY_VILLAGER_WORK_BUTCHER);
    public static final VillagerProfession CARTOGRAPHER = VillagerProfession.register("cartographer", PointOfInterestType.CARTOGRAPHER, SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER);
    public static final VillagerProfession CLERIC = VillagerProfession.register("cleric", PointOfInterestType.CLERIC, SoundEvents.ENTITY_VILLAGER_WORK_CLERIC);
    public static final VillagerProfession FARMER = VillagerProfession.register("farmer", PointOfInterestType.FARMER, ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.BONE_MEAL), ImmutableSet.of(Blocks.FARMLAND), SoundEvents.ENTITY_VILLAGER_WORK_FARMER);
    public static final VillagerProfession FISHERMAN = VillagerProfession.register("fisherman", PointOfInterestType.FISHERMAN, SoundEvents.ENTITY_VILLAGER_WORK_FISHERMAN);
    public static final VillagerProfession FLETCHER = VillagerProfession.register("fletcher", PointOfInterestType.FLETCHER, SoundEvents.ENTITY_VILLAGER_WORK_FLETCHER);
    public static final VillagerProfession LEATHERWORKER = VillagerProfession.register("leatherworker", PointOfInterestType.LEATHERWORKER, SoundEvents.ENTITY_VILLAGER_WORK_LEATHERWORKER);
    public static final VillagerProfession LIBRARIAN = VillagerProfession.register("librarian", PointOfInterestType.LIBRARIAN, SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN);
    public static final VillagerProfession MASON = VillagerProfession.register("mason", PointOfInterestType.MASON, SoundEvents.ENTITY_VILLAGER_WORK_MASON);
    public static final VillagerProfession NITWIT = VillagerProfession.register("nitwit", PointOfInterestType.NITWIT, null);
    public static final VillagerProfession SHEPHERD = VillagerProfession.register("shepherd", PointOfInterestType.SHEPHERD, SoundEvents.ENTITY_VILLAGER_WORK_SHEPHERD);
    public static final VillagerProfession TOOLSMITH = VillagerProfession.register("toolsmith", PointOfInterestType.TOOLSMITH, SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH);
    public static final VillagerProfession WEAPONSMITH = VillagerProfession.register("weaponsmith", PointOfInterestType.WEAPONSMITH, SoundEvents.ENTITY_VILLAGER_WORK_WEAPONSMITH);
    private final String id;
    private final PointOfInterestType workStation;
    private final ImmutableSet<Item> gatherableItems;
    private final ImmutableSet<Block> secondaryJobSites;
    @Nullable
    private final SoundEvent workSound;

    private VillagerProfession(String id, PointOfInterestType workStation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound) {
        this.id = id;
        this.workStation = workStation;
        this.gatherableItems = gatherableItems;
        this.secondaryJobSites = secondaryJobSites;
        this.workSound = workSound;
    }

    public String getId() {
        return this.id;
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
        return VillagerProfession.register(id, workStation, ImmutableSet.of(), ImmutableSet.of(), workSound);
    }

    static VillagerProfession register(String id, PointOfInterestType workStation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound) {
        return Registry.register(Registry.VILLAGER_PROFESSION, new Identifier(id), new VillagerProfession(id, workStation, gatherableItems, secondaryJobSites, workSound));
    }
}

