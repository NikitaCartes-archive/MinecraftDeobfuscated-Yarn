/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.BeaconContainer;
import net.minecraft.container.BlastFurnaceContainer;
import net.minecraft.container.BrewingStandContainer;
import net.minecraft.container.CartographyTableContainer;
import net.minecraft.container.Container;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.EnchantingTableContainer;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.container.Generic3x3Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.container.GrindstoneContainer;
import net.minecraft.container.HopperContainer;
import net.minecraft.container.LecternContainer;
import net.minecraft.container.LoomContainer;
import net.minecraft.container.MerchantContainer;
import net.minecraft.container.ShulkerBoxContainer;
import net.minecraft.container.SmokerContainer;
import net.minecraft.container.StonecutterContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.registry.Registry;

public class ContainerType<T extends Container> {
    public static final ContainerType<GenericContainer> GENERIC_9X1 = ContainerType.register("generic_9x1", GenericContainer::createGeneric9x1);
    public static final ContainerType<GenericContainer> GENERIC_9X2 = ContainerType.register("generic_9x2", GenericContainer::createGeneric9x2);
    public static final ContainerType<GenericContainer> GENERIC_9X3 = ContainerType.register("generic_9x3", GenericContainer::createGeneric9x3);
    public static final ContainerType<GenericContainer> GENERIC_9X4 = ContainerType.register("generic_9x4", GenericContainer::createGeneric9x4);
    public static final ContainerType<GenericContainer> GENERIC_9X5 = ContainerType.register("generic_9x5", GenericContainer::createGeneric9x5);
    public static final ContainerType<GenericContainer> GENERIC_9X6 = ContainerType.register("generic_9x6", GenericContainer::createGeneric9x6);
    public static final ContainerType<Generic3x3Container> GENERIC_3X3 = ContainerType.register("generic_3x3", Generic3x3Container::new);
    public static final ContainerType<AnvilContainer> ANVIL = ContainerType.register("anvil", AnvilContainer::new);
    public static final ContainerType<BeaconContainer> BEACON = ContainerType.register("beacon", BeaconContainer::new);
    public static final ContainerType<BlastFurnaceContainer> BLAST_FURNACE = ContainerType.register("blast_furnace", BlastFurnaceContainer::new);
    public static final ContainerType<BrewingStandContainer> BREWING_STAND = ContainerType.register("brewing_stand", BrewingStandContainer::new);
    public static final ContainerType<CraftingTableContainer> CRAFTING = ContainerType.register("crafting", CraftingTableContainer::new);
    public static final ContainerType<EnchantingTableContainer> ENCHANTMENT = ContainerType.register("enchantment", EnchantingTableContainer::new);
    public static final ContainerType<FurnaceContainer> FURNACE = ContainerType.register("furnace", FurnaceContainer::new);
    public static final ContainerType<GrindstoneContainer> GRINDSTONE = ContainerType.register("grindstone", GrindstoneContainer::new);
    public static final ContainerType<HopperContainer> HOPPER = ContainerType.register("hopper", HopperContainer::new);
    public static final ContainerType<LecternContainer> LECTERN = ContainerType.register("lectern", (i, playerInventory) -> new LecternContainer(i));
    public static final ContainerType<LoomContainer> LOOM = ContainerType.register("loom", LoomContainer::new);
    public static final ContainerType<MerchantContainer> MERCHANT = ContainerType.register("merchant", MerchantContainer::new);
    public static final ContainerType<ShulkerBoxContainer> SHULKER_BOX = ContainerType.register("shulker_box", ShulkerBoxContainer::new);
    public static final ContainerType<SmokerContainer> SMOKER = ContainerType.register("smoker", SmokerContainer::new);
    public static final ContainerType<CartographyTableContainer> CARTOGRAPHY_TABLE = ContainerType.register("cartography_table", CartographyTableContainer::new);
    public static final ContainerType<StonecutterContainer> STONECUTTER = ContainerType.register("stonecutter", StonecutterContainer::new);
    private final Factory<T> factory;

    private static <T extends Container> ContainerType<T> register(String string, Factory<T> factory) {
        return Registry.register(Registry.CONTAINER, string, new ContainerType<T>(factory));
    }

    private ContainerType(Factory<T> factory) {
        this.factory = factory;
    }

    @Environment(value=EnvType.CLIENT)
    public T create(int i, PlayerInventory playerInventory) {
        return this.factory.create(i, playerInventory);
    }

    static interface Factory<T extends Container> {
        @Environment(value=EnvType.CLIENT)
        public T create(int var1, PlayerInventory var2);
    }
}

