/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
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
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ContainerScreenRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ContainerType<?>, GuiFactory<?, ?>> GUI_FACTORIES = Maps.newHashMap();

    public static <T extends Container> void openScreen(@Nullable ContainerType<T> containerType, MinecraftClient minecraftClient, int i, Component component) {
        if (containerType == null) {
            LOGGER.warn("Trying to open invalid screen with name: {}", (Object)component.getString());
            return;
        }
        GuiFactory<T, ?> guiFactory = ContainerScreenRegistry.getFactory(containerType);
        if (guiFactory == null) {
            LOGGER.warn("Failed to create screen for menu type: {}", (Object)Registry.CONTAINER.getId(containerType));
            return;
        }
        guiFactory.openScreen(component, containerType, minecraftClient, i);
    }

    @Nullable
    private static <T extends Container> GuiFactory<T, ?> getFactory(ContainerType<T> containerType) {
        return GUI_FACTORIES.get(containerType);
    }

    private static <M extends Container, U extends Screen> void registerGui(ContainerType<? extends M> containerType, GuiFactory<M, U> guiFactory) {
        GuiFactory<M, U> guiFactory2 = GUI_FACTORIES.put(containerType, guiFactory);
        if (guiFactory2 != null) {
            throw new IllegalStateException("Duplicate registration for " + Registry.CONTAINER.getId(containerType));
        }
    }

    public static boolean checkData() {
        boolean bl = false;
        for (ContainerType containerType : Registry.CONTAINER) {
            if (GUI_FACTORIES.containsKey(containerType)) continue;
            LOGGER.debug("Menu {} has no matching screen", (Object)Registry.CONTAINER.getId(containerType));
            bl = true;
        }
        return bl;
    }

    static {
        ContainerScreenRegistry.registerGui(ContainerType.GENERIC_9X1, ContainerScreen54::new);
        ContainerScreenRegistry.registerGui(ContainerType.GENERIC_9X2, ContainerScreen54::new);
        ContainerScreenRegistry.registerGui(ContainerType.GENERIC_9X3, ContainerScreen54::new);
        ContainerScreenRegistry.registerGui(ContainerType.GENERIC_9X4, ContainerScreen54::new);
        ContainerScreenRegistry.registerGui(ContainerType.GENERIC_9X5, ContainerScreen54::new);
        ContainerScreenRegistry.registerGui(ContainerType.GENERIC_9X6, ContainerScreen54::new);
        ContainerScreenRegistry.registerGui(ContainerType.GENERIC_3X3, ContainerScreen9::new);
        ContainerScreenRegistry.registerGui(ContainerType.ANVIL, AnvilScreen::new);
        ContainerScreenRegistry.registerGui(ContainerType.BEACON, BeaconScreen::new);
        ContainerScreenRegistry.registerGui(ContainerType.BLAST_FURNACE, BlastFurnaceScreen::new);
        ContainerScreenRegistry.registerGui(ContainerType.BREWING_STAND, BrewingStandScreen::new);
        ContainerScreenRegistry.registerGui(ContainerType.CRAFTING, CraftingTableScreen::new);
        ContainerScreenRegistry.registerGui(ContainerType.ENCHANTMENT, EnchantingScreen::new);
        ContainerScreenRegistry.registerGui(ContainerType.FURNACE, FurnaceScreen::new);
        ContainerScreenRegistry.registerGui(ContainerType.GRINDSTONE, GrindstoneScreen::new);
        ContainerScreenRegistry.registerGui(ContainerType.HOPPER, HopperScreen::new);
        ContainerScreenRegistry.registerGui(ContainerType.LECTERN, LecternScreen::new);
        ContainerScreenRegistry.registerGui(ContainerType.LOOM, LoomScreen::new);
        ContainerScreenRegistry.registerGui(ContainerType.MERCHANT, MerchantScreen::new);
        ContainerScreenRegistry.registerGui(ContainerType.SHULKER_BOX, ShulkerBoxScreen::new);
        ContainerScreenRegistry.registerGui(ContainerType.SMOKER, SmokerScreen::new);
        ContainerScreenRegistry.registerGui(ContainerType.CARTOGRAPHY, CartographyTableScreen::new);
        ContainerScreenRegistry.registerGui(ContainerType.STONECUTTER, StonecutterScreen::new);
    }

    @Environment(value=EnvType.CLIENT)
    static interface GuiFactory<T extends Container, U extends Screen> {
        default public void openScreen(Component component, ContainerType<T> containerType, MinecraftClient minecraftClient, int i) {
            U screen = this.create(containerType.create(i, minecraftClient.player.inventory), minecraftClient.player.inventory, component);
            minecraftClient.player.container = ((ContainerProvider)screen).getContainer();
            minecraftClient.openScreen((Screen)screen);
        }

        public U create(T var1, PlayerInventory var2, Component var3);
    }
}

