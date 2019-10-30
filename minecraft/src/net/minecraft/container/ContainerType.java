package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.registry.Registry;

public class ContainerType<T extends Container> {
	public static final ContainerType<GenericContainer> GENERIC_9X1 = register("generic_9x1", GenericContainer::createGeneric9x1);
	public static final ContainerType<GenericContainer> GENERIC_9X2 = register("generic_9x2", GenericContainer::createGeneric9x2);
	public static final ContainerType<GenericContainer> GENERIC_9X3 = register("generic_9x3", GenericContainer::createGeneric9x3);
	public static final ContainerType<GenericContainer> GENERIC_9X4 = register("generic_9x4", GenericContainer::createGeneric9x4);
	public static final ContainerType<GenericContainer> GENERIC_9X5 = register("generic_9x5", GenericContainer::createGeneric9x5);
	public static final ContainerType<GenericContainer> GENERIC_9X6 = register("generic_9x6", GenericContainer::createGeneric9x6);
	public static final ContainerType<Generic3x3Container> GENERIC_3X3 = register("generic_3x3", Generic3x3Container::new);
	public static final ContainerType<AnvilContainer> ANVIL = register("anvil", AnvilContainer::new);
	public static final ContainerType<BeaconContainer> BEACON = register("beacon", BeaconContainer::new);
	public static final ContainerType<BlastFurnaceContainer> BLAST_FURNACE = register("blast_furnace", BlastFurnaceContainer::new);
	public static final ContainerType<BrewingStandContainer> BREWING_STAND = register("brewing_stand", BrewingStandContainer::new);
	public static final ContainerType<CraftingTableContainer> CRAFTING = register("crafting", CraftingTableContainer::new);
	public static final ContainerType<EnchantingTableContainer> ENCHANTMENT = register("enchantment", EnchantingTableContainer::new);
	public static final ContainerType<FurnaceContainer> FURNACE = register("furnace", FurnaceContainer::new);
	public static final ContainerType<GrindstoneContainer> GRINDSTONE = register("grindstone", GrindstoneContainer::new);
	public static final ContainerType<HopperContainer> HOPPER = register("hopper", HopperContainer::new);
	public static final ContainerType<LecternContainer> LECTERN = register("lectern", (i, playerInventory) -> new LecternContainer(i));
	public static final ContainerType<LoomContainer> LOOM = register("loom", LoomContainer::new);
	public static final ContainerType<MerchantContainer> MERCHANT = register("merchant", MerchantContainer::new);
	public static final ContainerType<ShulkerBoxContainer> SHULKER_BOX = register("shulker_box", ShulkerBoxContainer::new);
	public static final ContainerType<SmokerContainer> SMOKER = register("smoker", SmokerContainer::new);
	public static final ContainerType<CartographyTableContainer> CARTOGRAPHY_TABLE = register("cartography_table", CartographyTableContainer::new);
	public static final ContainerType<StonecutterContainer> STONECUTTER = register("stonecutter", StonecutterContainer::new);
	private final ContainerType.Factory<T> factory;

	private static <T extends Container> ContainerType<T> register(String id, ContainerType.Factory<T> factory) {
		return Registry.register(Registry.MENU, id, new ContainerType<>(factory));
	}

	private ContainerType(ContainerType.Factory<T> factory) {
		this.factory = factory;
	}

	@Environment(EnvType.CLIENT)
	public T create(int syncId, PlayerInventory playerInventory) {
		return this.factory.create(syncId, playerInventory);
	}

	interface Factory<T extends Container> {
		@Environment(EnvType.CLIENT)
		T create(int syncId, PlayerInventory playerInventory);
	}
}
