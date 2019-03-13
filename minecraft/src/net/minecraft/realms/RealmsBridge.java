package net.minecraft.realms;

import com.mojang.bridge.game.GameVersion;
import java.lang.reflect.Constructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_403;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.SystemUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsBridge extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private Screen previousScreen;

	public void switchToRealms(Screen screen) {
		this.previousScreen = screen;

		try {
			Class<?> class_ = Class.forName("com.mojang.realmsclient.RealmsMainScreen");
			Constructor<?> constructor = class_.getDeclaredConstructor(RealmsScreen.class);
			constructor.setAccessible(true);
			Object object = constructor.newInstance(this);
			MinecraftClient.getInstance().method_1507(((RealmsScreen)object).getProxy());
		} catch (ClassNotFoundException var5) {
			LOGGER.error("Realms module missing");
			this.showMissingRealmsErrorScreen();
		} catch (Exception var6) {
			LOGGER.error("Failed to load Realms module", (Throwable)var6);
			this.showMissingRealmsErrorScreen();
		}
	}

	public net.minecraft.client.gui.menu.RealmsScreen getNotificationScreen(Screen screen) {
		try {
			this.previousScreen = screen;
			Class<?> class_ = Class.forName("com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen");
			Constructor<?> constructor = class_.getDeclaredConstructor(RealmsScreen.class);
			constructor.setAccessible(true);
			Object object = constructor.newInstance(this);
			return ((RealmsScreen)object).getProxy();
		} catch (ClassNotFoundException var5) {
			LOGGER.error("Realms module missing");
		} catch (Exception var6) {
			LOGGER.error("Failed to load Realms module", (Throwable)var6);
		}

		return null;
	}

	@Override
	public void init() {
		MinecraftClient.getInstance().method_1507(this.previousScreen);
	}

	public static void openUri(String string) {
		SystemUtil.getOperatingSystem().open(string);
	}

	public static void setClipboard(String string) {
		MinecraftClient.getInstance().keyboard.setClipboard(string);
	}

	private void showMissingRealmsErrorScreen() {
		MinecraftClient.getInstance()
			.method_1507(
				new class_403(
					() -> MinecraftClient.getInstance().method_1507(this.previousScreen),
					new StringTextComponent(""),
					new TranslatableTextComponent(SharedConstants.getGameVersion().isStable() ? "realms.missing.module.error.text" : "realms.missing.snapshot.error.text")
				)
			);
	}

	public static String getVersionString() {
		return SharedConstants.getGameVersion().getName();
	}

	public static GameVersion getCurrentVersion() {
		return SharedConstants.getGameVersion();
	}
}
