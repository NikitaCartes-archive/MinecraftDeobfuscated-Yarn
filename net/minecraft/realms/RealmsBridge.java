/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4325;
import net.minecraft.class_4399;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsScreenProxy;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsBridge
extends RealmsScreen {
    private Screen previousScreen;

    public void switchToRealms(Screen screen) {
        this.previousScreen = screen;
        Realms.setScreen(new class_4325(this));
    }

    @Nullable
    public RealmsScreenProxy getNotificationScreen(Screen screen) {
        this.previousScreen = screen;
        return new class_4399(this).getProxy();
    }

    @Override
    public void init() {
        MinecraftClient.getInstance().openScreen(this.previousScreen);
    }
}

