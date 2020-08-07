package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class HandledScreens {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<ScreenHandlerType<?>, HandledScreens.Provider<?, ?>> PROVIDERS = Maps.<ScreenHandlerType<?>, HandledScreens.Provider<?, ?>>newHashMap();

	public static <T extends ScreenHandler> void open(@Nullable ScreenHandlerType<T> type, MinecraftClient client, int id, Text title) {
		if (type == null) {
			LOGGER.warn("Trying to open invalid screen with name: {}", title.getString());
		} else {
			HandledScreens.Provider<T, ?> provider = getProvider(type);
			if (provider == null) {
				LOGGER.warn("Failed to create screen for menu type: {}", Registry.SCREEN_HANDLER.getId(type));
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
		register(ScreenHandlerType.field_18664, GenericContainerScreen::new);
		register(ScreenHandlerType.field_18665, GenericContainerScreen::new);
		register(ScreenHandlerType.field_17326, GenericContainerScreen::new);
		register(ScreenHandlerType.field_18666, GenericContainerScreen::new);
		register(ScreenHandlerType.field_18667, GenericContainerScreen::new);
		register(ScreenHandlerType.field_17327, GenericContainerScreen::new);
		register(ScreenHandlerType.field_17328, Generic3x3ContainerScreen::new);
		register(ScreenHandlerType.field_17329, AnvilScreen::new);
		register(ScreenHandlerType.field_17330, BeaconScreen::new);
		register(ScreenHandlerType.field_17331, BlastFurnaceScreen::new);
		register(ScreenHandlerType.field_17332, BrewingStandScreen::new);
		register(ScreenHandlerType.field_17333, CraftingScreen::new);
		register(ScreenHandlerType.field_17334, EnchantmentScreen::new);
		register(ScreenHandlerType.field_17335, FurnaceScreen::new);
		register(ScreenHandlerType.field_17336, GrindstoneScreen::new);
		register(ScreenHandlerType.field_17337, HopperScreen::new);
		register(ScreenHandlerType.field_17338, LecternScreen::new);
		register(ScreenHandlerType.field_17339, LoomScreen::new);
		register(ScreenHandlerType.field_17340, MerchantScreen::new);
		register(ScreenHandlerType.field_17341, ShulkerBoxScreen::new);
		register(ScreenHandlerType.field_22484, SmithingScreen::new);
		register(ScreenHandlerType.field_17342, SmokerScreen::new);
		register(ScreenHandlerType.field_17343, CartographyTableScreen::new);
		register(ScreenHandlerType.field_17625, StonecutterScreen::new);
	}

	@Environment(EnvType.CLIENT)
	interface Provider<T extends ScreenHandler, U extends Screen & ScreenHandlerProvider<T>> {
		default void open(Text name, ScreenHandlerType<T> type, MinecraftClient client, int id) {
			U screen = this.create(type.create(id, client.player.inventory), client.player.inventory, name);
			client.player.currentScreenHandler = screen.getScreenHandler();
			client.openScreen(screen);
		}

		U create(T handler, PlayerInventory playerInventory, Text title);
	}
}
