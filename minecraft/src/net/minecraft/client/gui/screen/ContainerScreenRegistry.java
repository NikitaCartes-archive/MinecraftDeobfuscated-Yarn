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
public class ContainerScreenRegistry {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<ContainerType<?>, ContainerScreenRegistry.GuiFactory<?, ?>> GUI_FACTORIES = Maps.<ContainerType<?>, ContainerScreenRegistry.GuiFactory<?, ?>>newHashMap();

	public static <T extends Container> void openScreen(@Nullable ContainerType<T> containerType, MinecraftClient minecraftClient, int i, Text text) {
		if (containerType == null) {
			LOGGER.warn("Trying to open invalid screen with name: {}", text.getString());
		} else {
			ContainerScreenRegistry.GuiFactory<T, ?> guiFactory = getFactory(containerType);
			if (guiFactory == null) {
				LOGGER.warn("Failed to create screen for menu type: {}", Registry.MENU.getId(containerType));
			} else {
				guiFactory.openScreen(text, containerType, minecraftClient, i);
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
			throw new IllegalStateException("Duplicate registration for " + Registry.MENU.getId(containerType));
		}
	}

	public static boolean checkData() {
		boolean bl = false;

		for (ContainerType<?> containerType : Registry.MENU) {
			if (!GUI_FACTORIES.containsKey(containerType)) {
				LOGGER.debug("Menu {} has no matching screen", Registry.MENU.getId(containerType));
				bl = true;
			}
		}

		return bl;
	}

	static {
		registerGui(ContainerType.GENERIC_9X1, ContainerScreen54::new);
		registerGui(ContainerType.GENERIC_9X2, ContainerScreen54::new);
		registerGui(ContainerType.GENERIC_9X3, ContainerScreen54::new);
		registerGui(ContainerType.GENERIC_9X4, ContainerScreen54::new);
		registerGui(ContainerType.GENERIC_9X5, ContainerScreen54::new);
		registerGui(ContainerType.GENERIC_9X6, ContainerScreen54::new);
		registerGui(ContainerType.GENERIC_3X3, ContainerScreen9::new);
		registerGui(ContainerType.ANVIL, AnvilScreen::new);
		registerGui(ContainerType.BEACON, BeaconScreen::new);
		registerGui(ContainerType.BLAST_FURNACE, BlastFurnaceScreen::new);
		registerGui(ContainerType.BREWING_STAND, BrewingStandScreen::new);
		registerGui(ContainerType.CRAFTING, CraftingTableScreen::new);
		registerGui(ContainerType.ENCHANTMENT, EnchantingScreen::new);
		registerGui(ContainerType.FURNACE, FurnaceScreen::new);
		registerGui(ContainerType.GRINDSTONE, GrindstoneScreen::new);
		registerGui(ContainerType.HOPPER, HopperScreen::new);
		registerGui(ContainerType.LECTERN, LecternScreen::new);
		registerGui(ContainerType.LOOM, LoomScreen::new);
		registerGui(ContainerType.MERCHANT, MerchantScreen::new);
		registerGui(ContainerType.SHULKER_BOX, ShulkerBoxScreen::new);
		registerGui(ContainerType.SMOKER, SmokerScreen::new);
		registerGui(ContainerType.CARTOGRAPHY, CartographyTableScreen::new);
		registerGui(ContainerType.STONECUTTER, StonecutterScreen::new);
	}

	@Environment(EnvType.CLIENT)
	interface GuiFactory<T extends Container, U extends Screen & ContainerProvider<T>> {
		default void openScreen(Text text, ContainerType<T> containerType, MinecraftClient minecraftClient, int i) {
			U screen = this.create(containerType.create(i, minecraftClient.player.inventory), minecraftClient.player.inventory, text);
			minecraftClient.player.container = screen.getContainer();
			minecraftClient.openScreen(screen);
		}

		U create(T container, PlayerInventory playerInventory, Text text);
	}
}
