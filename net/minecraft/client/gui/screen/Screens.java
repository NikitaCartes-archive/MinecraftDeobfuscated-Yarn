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
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Screens {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ContainerType<?>, Provider<?, ?>> PROVIDERS = Maps.newHashMap();

    public static <T extends Container> void open(@Nullable ContainerType<T> type, MinecraftClient client, int id, Text name) {
        if (type == null) {
            LOGGER.warn("Trying to open invalid screen with name: {}", (Object)name.getString());
            return;
        }
        Provider<T, ?> provider = Screens.getProvider(type);
        if (provider == null) {
            LOGGER.warn("Failed to create screen for menu type: {}", (Object)Registry.CONTAINER.getId(type));
            return;
        }
        provider.open(name, type, client, id);
    }

    @Nullable
    private static <T extends Container> Provider<T, ?> getProvider(ContainerType<T> type) {
        return PROVIDERS.get(type);
    }

    private static <M extends Container, U extends Screen> void register(ContainerType<? extends M> type, Provider<M, U> provider) {
        Provider<M, U> provider2 = PROVIDERS.put(type, provider);
        if (provider2 != null) {
            throw new IllegalStateException("Duplicate registration for " + Registry.CONTAINER.getId(type));
        }
    }

    public static boolean validateScreens() {
        boolean bl = false;
        for (ContainerType containerType : Registry.CONTAINER) {
            if (PROVIDERS.containsKey(containerType)) continue;
            LOGGER.debug("Menu {} has no matching screen", (Object)Registry.CONTAINER.getId(containerType));
            bl = true;
        }
        return bl;
    }

    static {
        Screens.register(ContainerType.GENERIC_9X1, ContainerScreen54::new);
        Screens.register(ContainerType.GENERIC_9X2, ContainerScreen54::new);
        Screens.register(ContainerType.GENERIC_9X3, ContainerScreen54::new);
        Screens.register(ContainerType.GENERIC_9X4, ContainerScreen54::new);
        Screens.register(ContainerType.GENERIC_9X5, ContainerScreen54::new);
        Screens.register(ContainerType.GENERIC_9X6, ContainerScreen54::new);
        Screens.register(ContainerType.GENERIC_3X3, ContainerScreen9::new);
        Screens.register(ContainerType.ANVIL, AnvilScreen::new);
        Screens.register(ContainerType.BEACON, BeaconScreen::new);
        Screens.register(ContainerType.BLAST_FURNACE, BlastFurnaceScreen::new);
        Screens.register(ContainerType.BREWING_STAND, BrewingStandScreen::new);
        Screens.register(ContainerType.CRAFTING, CraftingTableScreen::new);
        Screens.register(ContainerType.ENCHANTMENT, EnchantingScreen::new);
        Screens.register(ContainerType.FURNACE, FurnaceScreen::new);
        Screens.register(ContainerType.GRINDSTONE, GrindstoneScreen::new);
        Screens.register(ContainerType.HOPPER, HopperScreen::new);
        Screens.register(ContainerType.LECTERN, LecternScreen::new);
        Screens.register(ContainerType.LOOM, LoomScreen::new);
        Screens.register(ContainerType.MERCHANT, MerchantScreen::new);
        Screens.register(ContainerType.SHULKER_BOX, ShulkerBoxScreen::new);
        Screens.register(ContainerType.SMOKER, SmokerScreen::new);
        Screens.register(ContainerType.CARTOGRAPHY_TABLE, CartographyTableScreen::new);
        Screens.register(ContainerType.STONECUTTER, StonecutterScreen::new);
    }

    @Environment(value=EnvType.CLIENT)
    static interface Provider<T extends Container, U extends Screen> {
        default public void open(Text name, ContainerType<T> type, MinecraftClient client, int id) {
            U screen = this.create(type.create(id, client.player.inventory), client.player.inventory, name);
            client.player.container = ((ContainerProvider)screen).getContainer();
            client.openScreen((Screen)screen);
        }

        public U create(T var1, PlayerInventory var2, Text var3);
    }
}

