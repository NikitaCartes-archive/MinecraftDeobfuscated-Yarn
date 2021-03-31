package net.minecraft.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.registry.Registry;

public class ScreenHandlerType<T extends ScreenHandler> {
	public static final ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X1 = register("generic_9x1", GenericContainerScreenHandler::createGeneric9x1);
	public static final ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X2 = register("generic_9x2", GenericContainerScreenHandler::createGeneric9x2);
	public static final ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X3 = register("generic_9x3", GenericContainerScreenHandler::createGeneric9x3);
	public static final ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X4 = register("generic_9x4", GenericContainerScreenHandler::createGeneric9x4);
	public static final ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X5 = register("generic_9x5", GenericContainerScreenHandler::createGeneric9x5);
	public static final ScreenHandlerType<GenericContainerScreenHandler> GENERIC_9X6 = register("generic_9x6", GenericContainerScreenHandler::createGeneric9x6);
	public static final ScreenHandlerType<Generic3x3ContainerScreenHandler> GENERIC_3X3 = register("generic_3x3", Generic3x3ContainerScreenHandler::new);
	public static final ScreenHandlerType<AnvilScreenHandler> ANVIL = register("anvil", AnvilScreenHandler::new);
	public static final ScreenHandlerType<BeaconScreenHandler> BEACON = register("beacon", BeaconScreenHandler::new);
	public static final ScreenHandlerType<BlastFurnaceScreenHandler> BLAST_FURNACE = register("blast_furnace", BlastFurnaceScreenHandler::new);
	public static final ScreenHandlerType<BrewingStandScreenHandler> BREWING_STAND = register("brewing_stand", BrewingStandScreenHandler::new);
	public static final ScreenHandlerType<CraftingScreenHandler> CRAFTING = register("crafting", CraftingScreenHandler::new);
	public static final ScreenHandlerType<EnchantmentScreenHandler> ENCHANTMENT = register("enchantment", EnchantmentScreenHandler::new);
	public static final ScreenHandlerType<FurnaceScreenHandler> FURNACE = register("furnace", FurnaceScreenHandler::new);
	public static final ScreenHandlerType<GrindstoneScreenHandler> GRINDSTONE = register("grindstone", GrindstoneScreenHandler::new);
	public static final ScreenHandlerType<HopperScreenHandler> HOPPER = register("hopper", HopperScreenHandler::new);
	public static final ScreenHandlerType<LecternScreenHandler> LECTERN = register("lectern", (i, playerInventory) -> new LecternScreenHandler(i));
	public static final ScreenHandlerType<LoomScreenHandler> LOOM = register("loom", LoomScreenHandler::new);
	public static final ScreenHandlerType<MerchantScreenHandler> MERCHANT = register("merchant", MerchantScreenHandler::new);
	public static final ScreenHandlerType<ShulkerBoxScreenHandler> SHULKER_BOX = register("shulker_box", ShulkerBoxScreenHandler::new);
	public static final ScreenHandlerType<SmithingScreenHandler> SMITHING = register("smithing", SmithingScreenHandler::new);
	public static final ScreenHandlerType<SmokerScreenHandler> SMOKER = register("smoker", SmokerScreenHandler::new);
	public static final ScreenHandlerType<CartographyTableScreenHandler> CARTOGRAPHY_TABLE = register("cartography_table", CartographyTableScreenHandler::new);
	public static final ScreenHandlerType<StonecutterScreenHandler> STONECUTTER = register("stonecutter", StonecutterScreenHandler::new);
	private final ScreenHandlerType.Factory<T> factory;

	private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
		return Registry.register(Registry.SCREEN_HANDLER, id, new ScreenHandlerType<>(factory));
	}

	private ScreenHandlerType(ScreenHandlerType.Factory<T> factory) {
		this.factory = factory;
	}

	public T create(int syncId, PlayerInventory playerInventory) {
		return this.factory.create(syncId, playerInventory);
	}

	interface Factory<T extends ScreenHandler> {
		T create(int syncId, PlayerInventory playerInventory);
	}
}
