package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class HandledScreens {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Map<ScreenHandlerType<?>, HandledScreens.Provider<?, ?>> PROVIDERS = Maps.<ScreenHandlerType<?>, HandledScreens.Provider<?, ?>>newHashMap();

	public static <T extends ScreenHandler> void open(@Nullable ScreenHandlerType<T> type, MinecraftClient client, int id, Text title) {
		if (type == null) {
			LOGGER.warn("Trying to open invalid screen with name: {}", title.getString());
		} else {
			HandledScreens.Provider<T, ?> provider = getProvider(type);
			if (provider == null) {
				LOGGER.warn("Failed to create screen for menu type: {}", Registries.SCREEN_HANDLER.getId(type));
			} else {
				provider.open(title, type, client, id);
			}
		}
	}

	@Nullable
	private static <T extends ScreenHandler> HandledScreens.Provider<T, ?> getProvider(ScreenHandlerType<T> type) {
		return (HandledScreens.Provider<T, ?>)PROVIDERS.get(type);
	}

	private static <M extends ScreenHandler, U extends Screen & ScreenHandlerProvider<M>> void register(
		ScreenHandlerType<? extends M> type, HandledScreens.Provider<M, U> provider
	) {
		HandledScreens.Provider<?, ?> provider2 = (HandledScreens.Provider<?, ?>)PROVIDERS.put(type, provider);
		if (provider2 != null) {
			throw new IllegalStateException("Duplicate registration for " + Registries.SCREEN_HANDLER.getId(type));
		}
	}

	public static boolean isMissingScreens() {
		boolean bl = false;

		for (ScreenHandlerType<?> screenHandlerType : Registries.SCREEN_HANDLER) {
			if (!PROVIDERS.containsKey(screenHandlerType)) {
				LOGGER.debug("Menu {} has no matching screen", Registries.SCREEN_HANDLER.getId(screenHandlerType));
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
		register(ScreenHandlerType.CRAFTER_3X3, CrafterScreen::new);
		register(ScreenHandlerType.ANVIL, AnvilScreen::new);
		register(ScreenHandlerType.BEACON, BeaconScreen::new);
		register(ScreenHandlerType.BLAST_FURNACE, BlastFurnaceScreen::new);
		register(ScreenHandlerType.BREWING_STAND, BrewingStandScreen::new);
		register(ScreenHandlerType.CRAFTING, CraftingScreen::new);
		register(ScreenHandlerType.ENCHANTMENT, EnchantmentScreen::new);
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
			U screen = this.create(type.create(id, client.player.getInventory()), client.player.getInventory(), name);
			client.player.currentScreenHandler = screen.getScreenHandler();
			client.setScreen(screen);
		}

		U create(T handler, PlayerInventory playerInventory, Text title);
	}
}
