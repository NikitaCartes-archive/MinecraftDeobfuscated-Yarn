package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.registry.Registry;

public class ContainerType<T extends Container> {
	public static final ContainerType<GenericContainer.Generic9x3> GENERIC_9X3 = register("generic_9x3", GenericContainer.Generic9x3::new);
	public static final ContainerType<GenericContainer.Generic9x6> GENERIC_9X6 = register("generic_9x6", GenericContainer.Generic9x6::new);
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
	public static final ContainerType<CartographyTableContainer> CARTOGRAPHY = register("cartography", CartographyTableContainer::new);
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
