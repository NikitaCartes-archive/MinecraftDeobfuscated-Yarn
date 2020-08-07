package net.minecraft.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.registry.Registry;

public class ScreenHandlerType<T extends ScreenHandler> {
	public static final ScreenHandlerType<GenericContainerScreenHandler> field_18664 = register("generic_9x1", GenericContainerScreenHandler::createGeneric9x1);
	public static final ScreenHandlerType<GenericContainerScreenHandler> field_18665 = register("generic_9x2", GenericContainerScreenHandler::createGeneric9x2);
	public static final ScreenHandlerType<GenericContainerScreenHandler> field_17326 = register("generic_9x3", GenericContainerScreenHandler::createGeneric9x3);
	public static final ScreenHandlerType<GenericContainerScreenHandler> field_18666 = register("generic_9x4", GenericContainerScreenHandler::createGeneric9x4);
	public static final ScreenHandlerType<GenericContainerScreenHandler> field_18667 = register("generic_9x5", GenericContainerScreenHandler::createGeneric9x5);
	public static final ScreenHandlerType<GenericContainerScreenHandler> field_17327 = register("generic_9x6", GenericContainerScreenHandler::createGeneric9x6);
	public static final ScreenHandlerType<Generic3x3ContainerScreenHandler> field_17328 = register("generic_3x3", Generic3x3ContainerScreenHandler::new);
	public static final ScreenHandlerType<AnvilScreenHandler> field_17329 = register("anvil", AnvilScreenHandler::new);
	public static final ScreenHandlerType<BeaconScreenHandler> field_17330 = register("beacon", BeaconScreenHandler::new);
	public static final ScreenHandlerType<BlastFurnaceScreenHandler> field_17331 = register("blast_furnace", BlastFurnaceScreenHandler::new);
	public static final ScreenHandlerType<BrewingStandScreenHandler> field_17332 = register("brewing_stand", BrewingStandScreenHandler::new);
	public static final ScreenHandlerType<CraftingScreenHandler> field_17333 = register("crafting", CraftingScreenHandler::new);
	public static final ScreenHandlerType<EnchantmentScreenHandler> field_17334 = register("enchantment", EnchantmentScreenHandler::new);
	public static final ScreenHandlerType<FurnaceScreenHandler> field_17335 = register("furnace", FurnaceScreenHandler::new);
	public static final ScreenHandlerType<GrindstoneScreenHandler> field_17336 = register("grindstone", GrindstoneScreenHandler::new);
	public static final ScreenHandlerType<HopperScreenHandler> field_17337 = register("hopper", HopperScreenHandler::new);
	public static final ScreenHandlerType<LecternScreenHandler> field_17338 = register("lectern", (i, playerInventory) -> new LecternScreenHandler(i));
	public static final ScreenHandlerType<LoomScreenHandler> field_17339 = register("loom", LoomScreenHandler::new);
	public static final ScreenHandlerType<MerchantScreenHandler> field_17340 = register("merchant", MerchantScreenHandler::new);
	public static final ScreenHandlerType<ShulkerBoxScreenHandler> field_17341 = register("shulker_box", ShulkerBoxScreenHandler::new);
	public static final ScreenHandlerType<SmithingScreenHandler> field_22484 = register("smithing", SmithingScreenHandler::new);
	public static final ScreenHandlerType<SmokerScreenHandler> field_17342 = register("smoker", SmokerScreenHandler::new);
	public static final ScreenHandlerType<CartographyTableScreenHandler> field_17343 = register("cartography_table", CartographyTableScreenHandler::new);
	public static final ScreenHandlerType<StonecutterScreenHandler> field_17625 = register("stonecutter", StonecutterScreenHandler::new);
	private final ScreenHandlerType.Factory<T> factory;

	private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
		return Registry.register(Registry.SCREEN_HANDLER, id, new ScreenHandlerType<>(factory));
	}

	private ScreenHandlerType(ScreenHandlerType.Factory<T> factory) {
		this.factory = factory;
	}

	@Environment(EnvType.CLIENT)
	public T create(int syncId, PlayerInventory playerInventory) {
		return this.factory.create(syncId, playerInventory);
	}

	interface Factory<T extends ScreenHandler> {
		@Environment(EnvType.CLIENT)
		T create(int syncId, PlayerInventory playerInventory);
	}
}
