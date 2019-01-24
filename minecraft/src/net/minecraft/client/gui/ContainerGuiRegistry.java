package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.container.AnvilGui;
import net.minecraft.client.gui.container.BeaconGui;
import net.minecraft.client.gui.container.BlastFurnaceGui;
import net.minecraft.client.gui.container.ContainerGui54;
import net.minecraft.client.gui.container.ContainerGui9;
import net.minecraft.client.gui.container.CraftingTableGui;
import net.minecraft.client.gui.container.EnchantingGui;
import net.minecraft.client.gui.container.FurnaceGui;
import net.minecraft.client.gui.container.GrindstoneGui;
import net.minecraft.client.gui.container.HopperGui;
import net.minecraft.client.gui.container.LoomGui;
import net.minecraft.client.gui.container.ShulkerBoxGui;
import net.minecraft.client.gui.container.SmokerGui;
import net.minecraft.client.gui.container.StonecutterGui;
import net.minecraft.client.gui.container.VillagerGui;
import net.minecraft.client.gui.ingame.BrewingStandGui;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ContainerGuiRegistry {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<ContainerType<?>, ContainerGuiRegistry.GuiFactory<?, ?>> GUI_FACTORIES = Maps.<ContainerType<?>, ContainerGuiRegistry.GuiFactory<?, ?>>newHashMap();

	public static <T extends Container> void openGui(@Nullable ContainerType<T> containerType, MinecraftClient minecraftClient, int i, TextComponent textComponent) {
		if (containerType == null) {
			LOGGER.warn("Trying to open invalid screen with name: {}", textComponent.getString());
		} else {
			ContainerGuiRegistry.GuiFactory<T, ?> guiFactory = method_17540(containerType);
			if (guiFactory == null) {
				LOGGER.warn("Failed to create screen for menu type: {}", Registry.CONTAINER.getId(containerType));
			} else {
				guiFactory.openGui(textComponent, containerType, minecraftClient, i);
			}
		}
	}

	@Nullable
	private static <T extends Container> ContainerGuiRegistry.GuiFactory<T, ?> method_17540(ContainerType<T> containerType) {
		return (ContainerGuiRegistry.GuiFactory<T, ?>)GUI_FACTORIES.get(containerType);
	}

	private static <M extends Container, U extends Gui & ContainerProvider<M>> void registerGui(
		ContainerType<? extends M> containerType, ContainerGuiRegistry.GuiFactory<M, U> guiFactory
	) {
		ContainerGuiRegistry.GuiFactory<?, ?> guiFactory2 = (ContainerGuiRegistry.GuiFactory<?, ?>)GUI_FACTORIES.put(containerType, guiFactory);
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
		registerGui(ContainerType.GENERIC_9X3, ContainerGui54::new);
		registerGui(ContainerType.GENERIC_9X6, ContainerGui54::new);
		registerGui(ContainerType.GENERIC_3X3, ContainerGui9::new);
		registerGui(ContainerType.ANVIL, AnvilGui::new);
		registerGui(ContainerType.BEACON, BeaconGui::new);
		registerGui(ContainerType.BLAST_FURNACE, BlastFurnaceGui::new);
		registerGui(ContainerType.BREWING_STAND, BrewingStandGui::new);
		registerGui(ContainerType.CRAFTING, CraftingTableGui::new);
		registerGui(ContainerType.ENCHANTMENT, EnchantingGui::new);
		registerGui(ContainerType.FURNACE, FurnaceGui::new);
		registerGui(ContainerType.GRINDSTONE, GrindstoneGui::new);
		registerGui(ContainerType.HOPPER, HopperGui::new);
		registerGui(ContainerType.LECTERN, LecternGui::new);
		registerGui(ContainerType.LOOM, LoomGui::new);
		registerGui(ContainerType.MERCHANT, VillagerGui::new);
		registerGui(ContainerType.SHULKER_BOX, ShulkerBoxGui::new);
		registerGui(ContainerType.SMOKER, SmokerGui::new);
		registerGui(ContainerType.CARTOGRAPHY, CartographyTableGui::new);
		registerGui(ContainerType.field_17625, StonecutterGui::new);
	}

	@Environment(EnvType.CLIENT)
	interface GuiFactory<T extends Container, U extends Gui & ContainerProvider<T>> {
		default void openGui(TextComponent textComponent, ContainerType<T> containerType, MinecraftClient minecraftClient, int i) {
			U gui = this.create(containerType.create(i, minecraftClient.player.inventory), minecraftClient.player.inventory, textComponent);
			minecraftClient.player.container = gui.getContainer();
			minecraftClient.openGui(gui);
		}

		U create(T container, PlayerInventory playerInventory, TextComponent textComponent);
	}
}
