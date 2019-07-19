package net.minecraft.client.gui.screen;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.BlastFurnaceScreen;
import net.minecraft.client.gui.screen.ingame.BrewingStandScreen;
import net.minecraft.client.gui.screen.ingame.CartographyTableScreen;
import net.minecraft.client.gui.screen.ingame.ContainerProvider;
import net.minecraft.client.gui.screen.ingame.CraftingTableScreen;
import net.minecraft.client.gui.screen.ingame.EnchantingScreen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.GrindstoneScreen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.gui.screen.ingame.LecternScreen;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.gui.screen.ingame.SmokerScreen;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class Screens {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<ContainerType<?>, Screens.Provider<?, ?>> PROVIDERS = Maps.<ContainerType<?>, Screens.Provider<?, ?>>newHashMap();

	public static <T extends Container> void open(@Nullable ContainerType<T> type, MinecraftClient client, int id, Text name) {
		if (type == null) {
			LOGGER.warn("Trying to open invalid screen with name: {}", name.getString());
		} else {
			Screens.Provider<T, ?> provider = getProvider(type);
			if (provider == null) {
				LOGGER.warn("Failed to create screen for menu type: {}", Registry.CONTAINER.getId(type));
			} else {
				provider.open(name, type, client, id);
			}
		}
	}

	@Nullable
	private static <T extends Container> Screens.Provider<T, ?> getProvider(ContainerType<T> type) {
		return (Screens.Provider<T, ?>)PROVIDERS.get(type);
	}

	private static <M extends Container, U extends Screen & ContainerProvider<M>> void register(ContainerType<? extends M> type, Screens.Provider<M, U> provider) {
		Screens.Provider<?, ?> provider2 = (Screens.Provider<?, ?>)PROVIDERS.put(type, provider);
		if (provider2 != null) {
			throw new IllegalStateException("Duplicate registration for " + Registry.CONTAINER.getId(type));
		}
	}

	public static boolean validateScreens() {
		boolean bl = false;

		for (ContainerType<?> containerType : Registry.CONTAINER) {
			if (!PROVIDERS.containsKey(containerType)) {
				LOGGER.debug("Menu {} has no matching screen", Registry.CONTAINER.getId(containerType));
				bl = true;
			}
		}

		return bl;
	}

	static {
		register(ContainerType.GENERIC_9X1, GenericContainerScreen::new);
		register(ContainerType.GENERIC_9X2, GenericContainerScreen::new);
		register(ContainerType.GENERIC_9X3, GenericContainerScreen::new);
		register(ContainerType.GENERIC_9X4, GenericContainerScreen::new);
		register(ContainerType.GENERIC_9X5, GenericContainerScreen::new);
		register(ContainerType.GENERIC_9X6, GenericContainerScreen::new);
		register(ContainerType.GENERIC_3X3, Generic3x3ContainerScreen::new);
		register(ContainerType.ANVIL, AnvilScreen::new);
		register(ContainerType.BEACON, BeaconScreen::new);
		register(ContainerType.BLAST_FURNACE, BlastFurnaceScreen::new);
		register(ContainerType.BREWING_STAND, BrewingStandScreen::new);
		register(ContainerType.CRAFTING, CraftingTableScreen::new);
		register(ContainerType.ENCHANTMENT, EnchantingScreen::new);
		register(ContainerType.FURNACE, FurnaceScreen::new);
		register(ContainerType.GRINDSTONE, GrindstoneScreen::new);
		register(ContainerType.HOPPER, HopperScreen::new);
		register(ContainerType.LECTERN, LecternScreen::new);
		register(ContainerType.LOOM, LoomScreen::new);
		register(ContainerType.MERCHANT, MerchantScreen::new);
		register(ContainerType.SHULKER_BOX, ShulkerBoxScreen::new);
		register(ContainerType.SMOKER, SmokerScreen::new);
		register(ContainerType.CARTOGRAPHY, CartographyTableScreen::new);
		register(ContainerType.STONECUTTER, StonecutterScreen::new);
	}

	@Environment(EnvType.CLIENT)
	interface Provider<T extends Container, U extends Screen & ContainerProvider<T>> {
		default void open(Text name, ContainerType<T> type, MinecraftClient client, int id) {
			U screen = this.create(type.create(id, client.player.inventory), client.player.inventory, name);
			client.player.container = screen.getContainer();
			client.openScreen(screen);
		}

		U create(T container, PlayerInventory playerInventory, Text text);
	}
}
