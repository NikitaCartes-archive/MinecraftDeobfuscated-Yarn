package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.registry.Registry;

public class ContainerType<T extends Container> {
	public static final ContainerType<GenericContainer> field_18664 = register("generic_9x1", GenericContainer::createGeneric9x1);
	public static final ContainerType<GenericContainer> field_18665 = register("generic_9x2", GenericContainer::createGeneric9x2);
	public static final ContainerType<GenericContainer> field_17326 = register("generic_9x3", GenericContainer::createGeneric9x3);
	public static final ContainerType<GenericContainer> field_18666 = register("generic_9x4", GenericContainer::createGeneric9x4);
	public static final ContainerType<GenericContainer> field_18667 = register("generic_9x5", GenericContainer::createGeneric9x5);
	public static final ContainerType<GenericContainer> field_17327 = register("generic_9x6", GenericContainer::createGeneric9x6);
	public static final ContainerType<Generic3x3Container> field_17328 = register("generic_3x3", Generic3x3Container::new);
	public static final ContainerType<AnvilContainer> field_17329 = register("anvil", AnvilContainer::new);
	public static final ContainerType<BeaconContainer> field_17330 = register("beacon", BeaconContainer::new);
	public static final ContainerType<BlastFurnaceContainer> field_17331 = register("blast_furnace", BlastFurnaceContainer::new);
	public static final ContainerType<BrewingStandContainer> field_17332 = register("brewing_stand", BrewingStandContainer::new);
	public static final ContainerType<CraftingTableContainer> field_17333 = register("crafting", CraftingTableContainer::new);
	public static final ContainerType<EnchantingTableContainer> field_17334 = register("enchantment", EnchantingTableContainer::new);
	public static final ContainerType<FurnaceContainer> field_17335 = register("furnace", FurnaceContainer::new);
	public static final ContainerType<GrindstoneContainer> field_17336 = register("grindstone", GrindstoneContainer::new);
	public static final ContainerType<HopperContainer> field_17337 = register("hopper", HopperContainer::new);
	public static final ContainerType<LecternContainer> field_17338 = register("lectern", (i, playerInventory) -> new LecternContainer(i));
	public static final ContainerType<LoomContainer> field_17339 = register("loom", LoomContainer::new);
	public static final ContainerType<MerchantContainer> field_17340 = register("merchant", MerchantContainer::new);
	public static final ContainerType<ShulkerBoxContainer> field_17341 = register("shulker_box", ShulkerBoxContainer::new);
	public static final ContainerType<SmokerContainer> field_17342 = register("smoker", SmokerContainer::new);
	public static final ContainerType<CartographyTableContainer> field_17343 = register("cartography", CartographyTableContainer::new);
	public static final ContainerType<StonecutterContainer> field_17625 = register("stonecutter", StonecutterContainer::new);
	private final ContainerType.Factory<T> factory;

	private static <T extends Container> ContainerType<T> register(String string, ContainerType.Factory<T> factory) {
		return Registry.register(Registry.CONTAINER, string, new ContainerType<>(factory));
	}

	private ContainerType(ContainerType.Factory<T> factory) {
		this.factory = factory;
	}

	@Environment(EnvType.CLIENT)
	public T create(int i, PlayerInventory playerInventory) {
		return this.factory.create(i, playerInventory);
	}

	interface Factory<T extends Container> {
		@Environment(EnvType.CLIENT)
		T create(int i, PlayerInventory playerInventory);
	}
}
