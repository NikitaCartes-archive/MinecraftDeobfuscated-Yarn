/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.realms;

import com.mojang.datafixers.util.Either;
import java.lang.reflect.Constructor;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsScreenProxy;
import net.minecraft.realms.pluginapi.LoadedRealmsPlugin;
import net.minecraft.realms.pluginapi.RealmsPlugin;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsBridge
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private Screen previousScreen;

    public void switchToRealms(Screen screen) {
        this.previousScreen = screen;
        Optional<LoadedRealmsPlugin> optional = this.tryLoadRealms();
        if (optional.isPresent()) {
            Realms.setScreen(optional.get().getMainScreen(this));
        } else {
            this.showMissingRealmsErrorScreen();
        }
    }

    @Nullable
    public RealmsScreenProxy getNotificationScreen(Screen screen) {
        this.previousScreen = screen;
        return this.tryLoadRealms().map(loadedRealmsPlugin -> loadedRealmsPlugin.getNotificationsScreen(this).getProxy()).orElse(null);
    }

    private Optional<LoadedRealmsPlugin> tryLoadRealms() {
        try {
            Class<?> class_ = Class.forName("com.mojang.realmsclient.plugin.RealmsPluginImpl");
            Constructor<?> constructor = class_.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);
            Object object = constructor.newInstance(new Object[0]);
            RealmsPlugin realmsPlugin = (RealmsPlugin)object;
            Either<LoadedRealmsPlugin, String> either = realmsPlugin.tryLoad(Realms.getMinecraftVersionString());
            Optional<String> optional = either.right();
            if (optional.isPresent()) {
                LOGGER.error("Failed to load Realms module: {}", (Object)optional.get());
                return Optional.empty();
            }
            return either.left();
        } catch (ClassNotFoundException classNotFoundException) {
            LOGGER.error("Realms module missing");
        } catch (Exception exception) {
            LOGGER.error("Failed to load Realms module", (Throwable)exception);
        }
        return Optional.empty();
    }

    @Override
    public void init() {
        MinecraftClient.getInstance().openScreen(this.previousScreen);
    }

    private void showMissingRealmsErrorScreen() {
        MinecraftClient.getInstance().openScreen(new NoticeScreen(() -> MinecraftClient.getInstance().openScreen(this.previousScreen), new LiteralText(""), new TranslatableText(SharedConstants.getGameVersion().isStable() ? "realms.missing.module.error.text" : "realms.missing.snapshot.error.text", new Object[0])));
    }
}

