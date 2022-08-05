/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.util.registry.Registry;

/**
 * Screen handler type is used to create screen handlers on the client.
 * It is a holder object holding a factory (usually a reference to the constructor).
 * They are registered in the registry under {@link
 * net.minecraft.util.registry.Registry#SCREEN_HANDLER}.
 * 
 * <p>Technically speaking, screen handlers do not have to register screen handler
 * types. However, such screen handlers are practically useless as they cannot be
 * opened server-side using conventional methods.
 * 
 * @apiNote Screen handler types should not be used to create a new screen handler
 * on the server. See {@link ScreenHandlerFactory} for server-side creation.
 * 
 * @see ScreenHandler
 */
public class ScreenHandlerType<T extends ScreenHandler> {
    public static final ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X1 = ScreenHandlerType.register("generic_9x1", GenericContainerScreenHandler::createGeneric9x1);
    public static final ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X2 = ScreenHandlerType.register("generic_9x2", GenericContainerScreenHandler::createGeneric9x2);
    public static final ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X3 = ScreenHandlerType.register("generic_9x3", GenericContainerScreenHandler::createGeneric9x3);
    public static final ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X4 = ScreenHandlerType.register("generic_9x4", GenericContainerScreenHandler::createGeneric9x4);
    public static final ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X5 = ScreenHandlerType.register("generic_9x5", GenericContainerScreenHandler::createGeneric9x5);
    public static final ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X6 = ScreenHandlerType.register("generic_9x6", GenericContainerScreenHandler::createGeneric9x6);
    public static final ScreenHandlerType<Generic3x3ContainerScreenHandler> GENERIC_3X3 = ScreenHandlerType.register("generic_3x3", Generic3x3ContainerScreenHandler::new);
    public static final ScreenHandlerType<AnvilScreenHandler> ANVIL = ScreenHandlerType.register("anvil", AnvilScreenHandler::new);
    public static final ScreenHandlerType<BeaconScreenHandler> BEACON = ScreenHandlerType.register("beacon", BeaconScreenHandler::new);
    public static final ScreenHandlerType<BlastFurnaceScreenHandler> BLAST_FURNACE = ScreenHandlerType.register("blast_furnace", BlastFurnaceScreenHandler::new);
    public static final ScreenHandlerType<BrewingStandScreenHandler> BREWING_STAND = ScreenHandlerType.register("brewing_stand", BrewingStandScreenHandler::new);
    public static final ScreenHandlerType<CraftingScreenHandler> CRAFTING = ScreenHandlerType.register("crafting", CraftingScreenHandler::new);
    public static final ScreenHandlerType<EnchantmentScreenHandler> ENCHANTMENT = ScreenHandlerType.register("enchantment", EnchantmentScreenHandler::new);
    public static final ScreenHandlerType<FurnaceScreenHandler> FURNACE = ScreenHandlerType.register("furnace", FurnaceScreenHandler::new);
    public static final ScreenHandlerType<GrindstoneScreenHandler> GRINDSTONE = ScreenHandlerType.register("grindstone", GrindstoneScreenHandler::new);
    public static final ScreenHandlerType<HopperScreenHandler> HOPPER = ScreenHandlerType.register("hopper", HopperScreenHandler::new);
    public static final ScreenHandlerType<LecternScreenHandler> LECTERN = ScreenHandlerType.register("lectern", (syncId, playerInventory) -> new LecternScreenHandler(syncId));
    public static final ScreenHandlerType<LoomScreenHandler> LOOM = ScreenHandlerType.register("loom", LoomScreenHandler::new);
    public static final ScreenHandlerType<MerchantScreenHandler> MERCHANT = ScreenHandlerType.register("merchant", MerchantScreenHandler::new);
    public static final ScreenHandlerType<ShulkerBoxScreenHandler> SHULKER_BOX = ScreenHandlerType.register("shulker_box", ShulkerBoxScreenHandler::new);
    public static final ScreenHandlerType<SmithingScreenHandler> SMITHING = ScreenHandlerType.register("smithing", SmithingScreenHandler::new);
    public static final ScreenHandlerType<SmokerScreenHandler> SMOKER = ScreenHandlerType.register("smoker", SmokerScreenHandler::new);
    public static final ScreenHandlerType<CartographyTableScreenHandler> CARTOGRAPHY_TABLE = ScreenHandlerType.register("cartography_table", CartographyTableScreenHandler::new);
    public static final ScreenHandlerType<StonecutterScreenHandler> STONECUTTER = ScreenHandlerType.register("stonecutter", StonecutterScreenHandler::new);
    private final Factory<T> factory;

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, Factory<T> factory) {
        return Registry.register(Registry.SCREEN_HANDLER, id, new ScreenHandlerType<T>(factory));
    }

    private ScreenHandlerType(Factory<T> factory) {
        this.factory = factory;
    }

    public T create(int syncId, PlayerInventory playerInventory) {
        return this.factory.create(syncId, playerInventory);
    }

    static interface Factory<T extends ScreenHandler> {
        public T create(int var1, PlayerInventory var2);
    }
}

