package net.minecraft.realms;

import com.mojang.bridge.game.GameVersion;
import java.lang.reflect.Constructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_155;
import net.minecraft.class_156;
import net.minecraft.class_2585;
import net.minecraft.class_2588;
import net.minecraft.class_310;
import net.minecraft.class_403;
import net.minecraft.class_437;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsBridge extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private class_437 previousScreen;

	public void switchToRealms(class_437 arg) {
		this.previousScreen = arg;

		try {
			Class<?> class_ = Class.forName("com.mojang.realmsclient.RealmsMainScreen");
			Constructor<?> constructor = class_.getDeclaredConstructor(RealmsScreen.class);
			constructor.setAccessible(true);
			Object object = constructor.newInstance(this);
			class_310.method_1551().method_1507(((RealmsScreen)object).getProxy());
		} catch (ClassNotFoundException var5) {
			LOGGER.error("Realms module missing");
			this.showMissingRealmsErrorScreen();
		} catch (Exception var6) {
			LOGGER.error("Failed to load Realms module", (Throwable)var6);
			this.showMissingRealmsErrorScreen();
		}
	}

	public RealmsScreenProxy getNotificationScreen(class_437 arg) {
		try {
			this.previousScreen = arg;
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
		class_310.method_1551().method_1507(this.previousScreen);
	}

	public static void openUri(String string) {
		class_156.method_668().method_670(string);
	}

	public static void setClipboard(String string) {
		class_310.method_1551().field_1774.method_1455(string);
	}

	private void showMissingRealmsErrorScreen() {
		class_310.method_1551()
			.method_1507(
				new class_403(
					() -> class_310.method_1551().method_1507(this.previousScreen),
					new class_2585(""),
					new class_2588(class_155.method_16673().isStable() ? "realms.missing.module.error.text" : "realms.missing.snapshot.error.text")
				)
			);
	}

	public static String getVersionString() {
		return class_155.method_16673().getName();
	}

	public static GameVersion getCurrentVersion() {
		return class_155.method_16673();
	}
}
