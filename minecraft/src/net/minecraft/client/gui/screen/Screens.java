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
import net.minecraft.client.gui.screen.ingame.ContainerScreen54;
import net.minecraft.client.gui.screen.ingame.ContainerScreen9;
import net.minecraft.client.gui.screen.ingame.CraftingTableScreen;
import net.minecraft.client.gui.screen.ingame.EnchantingScreen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
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

	public static <T extends Container> void open(@Nullable ContainerType<T> containerType, MinecraftClient minecraftClient, int i, Text text) {
		if (containerType == null) {
			LOGGER.warn("Trying to open invalid screen with name: {}", text.getString());
		} else {
			Screens.Provider<T, ?> provider = getProvider(containerType);
			if (provider == null) {
				LOGGER.warn("Failed to create screen for menu type: {}", Registry.CONTAINER.getId(containerType));
			} else {
				provider.open(text, containerType, minecraftClient, i);
			}
		}
	}

	@Nullable
	private static <T extends Container> Screens.Provider<T, ?> getProvider(ContainerType<T> containerType) {
		return (Screens.Provider<T, ?>)PROVIDERS.get(containerType);
	}

	private static <M extends Container, U extends Screen & ContainerProvider<M>> void register(
		ContainerType<? extends M> containerType, Screens.Provider<M, U> provider
	) {
		Screens.Provider<?, ?> provider2 = (Screens.Provider<?, ?>)PROVIDERS.put(containerType, provider);
		if (provider2 != null) {
			throw new IllegalStateException("Duplicate registration for " + Registry.CONTAINER.getId(containerType));
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
		register(ContainerType.field_18664, ContainerScreen54::new);
		register(ContainerType.field_18665, ContainerScreen54::new);
		register(ContainerType.field_17326, ContainerScreen54::new);
		register(ContainerType.field_18666, ContainerScreen54::new);
		register(ContainerType.field_18667, ContainerScreen54::new);
		register(ContainerType.field_17327, ContainerScreen54::new);
		register(ContainerType.field_17328, ContainerScreen9::new);
		register(ContainerType.field_17329, AnvilScreen::new);
		register(ContainerType.field_17330, BeaconScreen::new);
		register(ContainerType.field_17331, BlastFurnaceScreen::new);
		register(ContainerType.field_17332, BrewingStandScreen::new);
		register(ContainerType.field_17333, CraftingTableScreen::new);
		register(ContainerType.field_17334, EnchantingScreen::new);
		register(ContainerType.field_17335, FurnaceScreen::new);
		register(ContainerType.field_17336, GrindstoneScreen::new);
		register(ContainerType.field_17337, HopperScreen::new);
		register(ContainerType.field_17338, LecternScreen::new);
		register(ContainerType.field_17339, LoomScreen::new);
		register(ContainerType.field_17340, MerchantScreen::new);
		register(ContainerType.field_17341, ShulkerBoxScreen::new);
		register(ContainerType.field_17342, SmokerScreen::new);
		register(ContainerType.field_17343, CartographyTableScreen::new);
		register(ContainerType.field_17625, StonecutterScreen::new);
	}

	@Environment(EnvType.CLIENT)
	interface Provider<T extends Container, U extends Screen & ContainerProvider<T>> {
		default void open(Text text, ContainerType<T> containerType, MinecraftClient minecraftClient, int i) {
			U screen = this.create(containerType.create(i, minecraftClient.player.inventory), minecraftClient.player.inventory, text);
			minecraftClient.player.container = screen.getContainer();
			minecraftClient.openScreen(screen);
		}

		U create(T container, PlayerInventory playerInventory, Text text);
	}
}
