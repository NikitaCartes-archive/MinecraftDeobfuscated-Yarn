package net.minecraft.realms;

import com.mojang.datafixers.util.Either;
import java.lang.reflect.Constructor;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.pluginapi.LoadedRealmsPlugin;
import net.minecraft.realms.pluginapi.RealmsPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsBridge extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private Screen previousScreen;

	public void switchToRealms(Screen screen) {
		this.previousScreen = screen;
		Optional<LoadedRealmsPlugin> optional = this.tryLoadRealms();
		if (optional.isPresent()) {
			Realms.setScreen(((LoadedRealmsPlugin)optional.get()).getMainScreen(this));
		} else {
			this.showMissingRealmsErrorScreen();
		}
	}

	@Nullable
	public RealmsScreenProxy getNotificationScreen(Screen screen) {
		this.previousScreen = screen;
		return (RealmsScreenProxy)this.tryLoadRealms().map(loadedRealmsPlugin -> loadedRealmsPlugin.getNotificationsScreen(this).getProxy()).orElse(null);
	}

	private Optional<LoadedRealmsPlugin> tryLoadRealms() {
		try {
			Class<?> class_ = Class.forName("com.mojang.realmsclient.plugin.RealmsPluginImpl");
			Constructor<?> constructor = class_.getDeclaredConstructor();
			constructor.setAccessible(true);
			Object object = constructor.newInstance();
			RealmsPlugin realmsPlugin = (RealmsPlugin)object;
			Either<LoadedRealmsPlugin, String> either = realmsPlugin.tryLoad(Realms.getMinecraftVersionString());
			Optional<String> optional = either.right();
			if (optional.isPresent()) {
				LOGGER.error("Failed to load Realms module: {}", optional.get());
				return Optional.empty();
			}

			return either.left();
		} catch (ClassNotFoundException var7) {
			LOGGER.error("Realms module missing");
		} catch (Exception var8) {
			LOGGER.error("Failed to load Realms module", (Throwable)var8);
		}

		return Optional.empty();
	}

	@Override
	public void init() {
		MinecraftClient.getInstance().method_1507(this.previousScreen);
	}

	private void showMissingRealmsErrorScreen() {
		MinecraftClient.getInstance()
			.method_1507(
				new NoticeScreen(
					() -> MinecraftClient.getInstance().method_1507(this.previousScreen),
					new TextComponent(""),
					new TranslatableComponent(SharedConstants.getGameVersion().isStable() ? "realms.missing.module.error.text" : "realms.missing.snapshot.error.text")
				)
			);
	}
}
