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
import net.minecraft.network.chat.Component;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ContainerScreenRegistry {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<ContainerType<?>, ContainerScreenRegistry.GuiFactory<?, ?>> GUI_FACTORIES = Maps.<ContainerType<?>, ContainerScreenRegistry.GuiFactory<?, ?>>newHashMap();

	public static <T extends Container> void openScreen(@Nullable ContainerType<T> containerType, MinecraftClient minecraftClient, int i, Component component) {
		if (containerType == null) {
			LOGGER.warn("Trying to open invalid screen with name: {}", component.getString());
		} else {
			ContainerScreenRegistry.GuiFactory<T, ?> guiFactory = getFactory(containerType);
			if (guiFactory == null) {
				LOGGER.warn("Failed to create screen for menu type: {}", Registry.CONTAINER.getId(containerType));
			} else {
				guiFactory.openScreen(component, containerType, minecraftClient, i);
			}
		}
	}

	@Nullable
	private static <T extends Container> ContainerScreenRegistry.GuiFactory<T, ?> getFactory(ContainerType<T> containerType) {
		return (ContainerScreenRegistry.GuiFactory<T, ?>)GUI_FACTORIES.get(containerType);
	}

	private static <M extends Container, U extends Screen & ContainerProvider<M>> void registerGui(
		ContainerType<? extends M> containerType, ContainerScreenRegistry.GuiFactory<M, U> guiFactory
	) {
		ContainerScreenRegistry.GuiFactory<?, ?> guiFactory2 = (ContainerScreenRegistry.GuiFactory<?, ?>)GUI_FACTORIES.put(containerType, guiFactory);
		if (guiFactory2 != null) {
			throw new IllegalStateException("Duplicate registration for " + Registry.CONTAINER.getId(containerType));
		}
	}

	public static boolean checkData() {
		boolean bl = false;

		for (ContainerType<?> containerType : Registry.CONTAINER) {
			if (!GUI_FACTORIES.containsKey(containerType)) {
				LOGGER.debug("Menu {} has no matching screen", Registry.CONTAINER.getId(containerType));
				bl = true;
			}
		}

		return bl;
	}

	static {
		registerGui(ContainerType.field_18664, ContainerScreen54::new);
		registerGui(ContainerType.field_18665, ContainerScreen54::new);
		registerGui(ContainerType.field_17326, ContainerScreen54::new);
		registerGui(ContainerType.field_18666, ContainerScreen54::new);
		registerGui(ContainerType.field_18667, ContainerScreen54::new);
		registerGui(ContainerType.field_17327, ContainerScreen54::new);
		registerGui(ContainerType.field_17328, ContainerScreen9::new);
		registerGui(ContainerType.field_17329, AnvilScreen::new);
		registerGui(ContainerType.field_17330, BeaconScreen::new);
		registerGui(ContainerType.field_17331, BlastFurnaceScreen::new);
		registerGui(ContainerType.field_17332, BrewingStandScreen::new);
		registerGui(ContainerType.field_17333, CraftingTableScreen::new);
		registerGui(ContainerType.field_17334, EnchantingScreen::new);
		registerGui(ContainerType.field_17335, FurnaceScreen::new);
		registerGui(ContainerType.field_17336, GrindstoneScreen::new);
		registerGui(ContainerType.field_17337, HopperScreen::new);
		registerGui(ContainerType.field_17338, LecternScreen::new);
		registerGui(ContainerType.field_17339, LoomScreen::new);
		registerGui(ContainerType.field_17340, MerchantScreen::new);
		registerGui(ContainerType.field_17341, ShulkerBoxScreen::new);
		registerGui(ContainerType.field_17342, SmokerScreen::new);
		registerGui(ContainerType.field_17343, CartographyTableScreen::new);
		registerGui(ContainerType.field_17625, StonecutterScreen::new);
	}

	@Environment(EnvType.CLIENT)
	interface GuiFactory<T extends Container, U extends Screen & ContainerProvider<T>> {
		default void openScreen(Component component, ContainerType<T> containerType, MinecraftClient minecraftClient, int i) {
			U screen = this.create(containerType.create(i, minecraftClient.player.inventory), minecraftClient.player.inventory, component);
			minecraftClient.player.container = screen.getContainer();
			minecraftClient.method_1507(screen);
		}

		U create(T container, PlayerInventory playerInventory, Component component);
	}
}
