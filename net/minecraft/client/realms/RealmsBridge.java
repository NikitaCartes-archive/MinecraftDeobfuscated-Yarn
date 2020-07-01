/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsMainScreen;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsBridge
extends RealmsScreen {
    private Screen previousScreen;

    public void switchToRealms(Screen parentScreen) {
        this.previousScreen = parentScreen;
        MinecraftClient.getInstance().openScreen(new RealmsMainScreen(this));
    }

    @Nullable
    public RealmsScreen getNotificationScreen(Screen parentScreen) {
        this.previousScreen = parentScreen;
        return new RealmsNotificationsScreen();
    }

    @Override
    public void init() {
        MinecraftClient.getInstance().openScreen(this.previousScreen);
    }
}

