package net.minecraft.realms;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class RealmsBridge extends RealmsScreen {
	private Screen previousScreen;

	public void switchToRealms(Screen screen) {
		this.previousScreen = screen;
		MinecraftClient.getInstance().openScreen(new RealmsMainScreen(this));
	}

	@Nullable
	public RealmsScreen getNotificationScreen(Screen screen) {
		this.previousScreen = screen;
		return new RealmsNotificationsScreen();
	}

	@Override
	public void init() {
		MinecraftClient.getInstance().openScreen(this.previousScreen);
	}
}
