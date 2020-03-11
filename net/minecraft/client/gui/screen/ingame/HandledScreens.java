/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

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
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
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
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class HandledScreens {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ScreenHandlerType<?>, Provider<?, ?>> PROVIDERS = Maps.newHashMap();

    public static <T extends ScreenHandler> void open(@Nullable ScreenHandlerType<T> type, MinecraftClient client, int id, Text title) {
        if (type == null) {
            LOGGER.warn("Trying to open invalid screen with name: {}", (Object)title.getString());
            return;
        }
        Provider<T, ?> provider = HandledScreens.getProvider(type);
        if (provider == null) {
            LOGGER.warn("Failed to create screen for menu type: {}", (Object)Registry.SCREEN_HANDLER.getId(type));
            return;
        }
        provider.open(title, type, client, id);
    }

    @Nullable
    private static <T extends ScreenHandler> Provider<T, ?> getProvider(ScreenHandlerType<T> type) {
        return PROVIDERS.get(type);
    }

    private static <M extends ScreenHandler, U extends Screen> void register(ScreenHandlerType<? extends M> type, Provider<M, U> provider) {
        Provider<M, U> provider2 = PROVIDERS.put(type, provider);
        if (provider2 != null) {
            throw new IllegalStateException("Duplicate registration for " + Registry.SCREEN_HANDLER.getId(type));
        }
    }

    public static boolean validateScreens() {
        boolean bl = false;
        for (ScreenHandlerType screenHandlerType : Registry.SCREEN_HANDLER) {
            if (PROVIDERS.containsKey(screenHandlerType)) continue;
            LOGGER.debug("Menu {} has no matching screen", (Object)Registry.SCREEN_HANDLER.getId(screenHandlerType));
            bl = true;
        }
        return bl;
    }

    static {
        HandledScreens.register(ScreenHandlerType.GENERIC_9X1, GenericContainerScreen::new);
        HandledScreens.register(ScreenHandlerType.GENERIC_9X2, GenericContainerScreen::new);
        HandledScreens.register(ScreenHandlerType.GENERIC_9X3, GenericContainerScreen::new);
        HandledScreens.register(ScreenHandlerType.GENERIC_9X4, GenericContainerScreen::new);
        HandledScreens.register(ScreenHandlerType.GENERIC_9X5, GenericContainerScreen::new);
        HandledScreens.register(ScreenHandlerType.GENERIC_9X6, GenericContainerScreen::new);
        HandledScreens.register(ScreenHandlerType.GENERIC_3X3, Generic3x3ContainerScreen::new);
        HandledScreens.register(ScreenHandlerType.ANVIL, AnvilScreen::new);
        HandledScreens.register(ScreenHandlerType.BEACON, BeaconScreen::new);
        HandledScreens.register(ScreenHandlerType.BLAST_FURNACE, BlastFurnaceScreen::new);
        HandledScreens.register(ScreenHandlerType.BREWING_STAND, BrewingStandScreen::new);
        HandledScreens.register(ScreenHandlerType.CRAFTING, CraftingScreen::new);
        HandledScreens.register(ScreenHandlerType.ENCHANTMENT, EnchantmentScreen::new);
        HandledScreens.register(ScreenHandlerType.FURNACE, FurnaceScreen::new);
        HandledScreens.register(ScreenHandlerType.GRINDSTONE, GrindstoneScreen::new);
        HandledScreens.register(ScreenHandlerType.HOPPER, HopperScreen::new);
        HandledScreens.register(ScreenHandlerType.LECTERN, LecternScreen::new);
        HandledScreens.register(ScreenHandlerType.LOOM, LoomScreen::new);
        HandledScreens.register(ScreenHandlerType.MERCHANT, MerchantScreen::new);
        HandledScreens.register(ScreenHandlerType.SHULKER_BOX, ShulkerBoxScreen::new);
        HandledScreens.register(ScreenHandlerType.SMITHING, SmithingScreen::new);
        HandledScreens.register(ScreenHandlerType.SMOKER, SmokerScreen::new);
        HandledScreens.register(ScreenHandlerType.CARTOGRAPHY_TABLE, CartographyTableScreen::new);
        HandledScreens.register(ScreenHandlerType.STONECUTTER, StonecutterScreen::new);
    }

    @Environment(value=EnvType.CLIENT)
    static interface Provider<T extends ScreenHandler, U extends Screen> {
        default public void open(Text name, ScreenHandlerType<T> type, MinecraftClient client, int id) {
            U screen = this.create(type.create(id, client.player.inventory), client.player.inventory, name);
            client.player.currentScreenHandler = ((ScreenHandlerProvider)screen).getScreenHandler();
            client.openScreen((Screen)screen);
        }

        public U create(T var1, PlayerInventory var2, Text var3);
    }
}

