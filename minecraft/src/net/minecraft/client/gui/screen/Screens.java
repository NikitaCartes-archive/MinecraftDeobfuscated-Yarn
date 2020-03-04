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
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.client.gui.screen.ingame.SmokerScreen;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class Screens {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<ScreenHandlerType<?>, Screens.Provider<?, ?>> PROVIDERS = Maps.<ScreenHandlerType<?>, Screens.Provider<?, ?>>newHashMap();

	public static <T extends ScreenHandler> void open(@Nullable ScreenHandlerType<T> type, MinecraftClient client, int id, Text name) {
		if (type == null) {
			LOGGER.warn("Trying to open invalid screen with name: {}", name.getString());
		} else {
			Screens.Provider<T, ?> provider = getProvider(type);
			if (provider == null) {
				LOGGER.warn("Failed to create screen for menu type: {}", Registry.SCREEN_HANDLER.getId(type));
			} else {
				provider.open(name, type, client, id);
			}
		}
	}

	@Nullable
	private static <T extends ScreenHandler> Screens.Provider<T, ?> getProvider(ScreenHandlerType<T> type) {
		return (Screens.Provider<T, ?>)PROVIDERS.get(type);
	}

	private static <M extends ScreenHandler, U extends Screen & ScreenHandlerProvider<M>> void register(
		ScreenHandlerType<? extends M> type, Screens.Provider<M, U> provider
	) {
		Screens.Provider<?, ?> provider2 = (Screens.Provider<?, ?>)PROVIDERS.put(type, provider);
		if (provider2 != null) {
			throw new IllegalStateException("Duplicate registration for " + Registry.SCREEN_HANDLER.getId(type));
		}
	}

	public static boolean validateScreens() {
		boolean bl = false;

		for (ScreenHandlerType<?> screenHandlerType : Registry.SCREEN_HANDLER) {
			if (!PROVIDERS.containsKey(screenHandlerType)) {
				LOGGER.debug("Menu {} has no matching screen", Registry.SCREEN_HANDLER.getId(screenHandlerType));
				bl = true;
			}
		}

		return bl;
	}

	static {
		register(ScreenHandlerType.GENERIC_9X1, GenericContainerScreen::new);
		register(ScreenHandlerType.GENERIC_9X2, GenericContainerScreen::new);
		register(ScreenHandlerType.GENERIC_9X3, GenericContainerScreen::new);
		register(ScreenHandlerType.GENERIC_9X4, GenericContainerScreen::new);
		register(ScreenHandlerType.GENERIC_9X5, GenericContainerScreen::new);
		register(ScreenHandlerType.GENERIC_9X6, GenericContainerScreen::new);
		register(ScreenHandlerType.GENERIC_3X3, Generic3x3ContainerScreen::new);
		register(ScreenHandlerType.ANVIL, AnvilScreen::new);
		register(ScreenHandlerType.BEACON, BeaconScreen::new);
		register(ScreenHandlerType.BLAST_FURNACE, BlastFurnaceScreen::new);
		register(ScreenHandlerType.BREWING_STAND, BrewingStandScreen::new);
		register(ScreenHandlerType.CRAFTING, CraftingTableScreen::new);
		register(ScreenHandlerType.ENCHANTMENT, EnchantingScreen::new);
		register(ScreenHandlerType.FURNACE, FurnaceScreen::new);
		register(ScreenHandlerType.GRINDSTONE, GrindstoneScreen::new);
		register(ScreenHandlerType.HOPPER, HopperScreen::new);
		register(ScreenHandlerType.LECTERN, LecternScreen::new);
		register(ScreenHandlerType.LOOM, LoomScreen::new);
		register(ScreenHandlerType.MERCHANT, MerchantScreen::new);
		register(ScreenHandlerType.SHULKER_BOX, ShulkerBoxScreen::new);
		register(ScreenHandlerType.SMITHING, SmithingScreen::new);
		register(ScreenHandlerType.SMOKER, SmokerScreen::new);
		register(ScreenHandlerType.CARTOGRAPHY_TABLE, CartographyTableScreen::new);
		register(ScreenHandlerType.STONECUTTER, StonecutterScreen::new);
	}

	@Environment(EnvType.CLIENT)
	interface Provider<T extends ScreenHandler, U extends Screen & ScreenHandlerProvider<T>> {
		default void open(Text name, ScreenHandlerType<T> type, MinecraftClient client, int id) {
			U screen = this.create(type.create(id, client.player.inventory), client.player.inventory, name);
			client.player.currentScreenHandler = screen.getScreenHandler();
			client.openScreen(screen);
		}

		U create(T screenHandler, PlayerInventory playerInventory, Text text);
	}
}
