package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class LongRunningTask implements Runnable {
	protected static final int MAX_RETRIES = 25;
	private static final Logger LOGGER = LogUtils.getLogger();
	private boolean aborted = false;

	/**
	 * Moved from RealmsTasks in 20w10a.
	 */
	protected static void pause(long seconds) {
		try {
			Thread.sleep(seconds * 1000L);
		} catch (InterruptedException var3) {
			Thread.currentThread().interrupt();
			LOGGER.error("", (Throwable)var3);
		}
	}

	/**
	 * Moved from Realms in 20w10a.
	 */
	public static void setScreen(Screen screen) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.execute(() -> minecraftClient.setScreen(screen));
	}

	protected void error(Text message) {
		this.abortTask();
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.execute(() -> minecraftClient.setScreen(new RealmsGenericErrorScreen(message, new RealmsMainScreen(new TitleScreen()))));
	}

	protected void error(Exception exception) {
		if (exception instanceof RealmsServiceException realmsServiceException) {
			this.error(realmsServiceException.error.getText());
		} else {
			this.error(Text.literal(exception.getMessage()));
		}
	}

	protected void error(RealmsServiceException exception) {
		this.error(exception.error.getText());
	}

	public abstract Text getTitle();

	public boolean aborted() {
		return this.aborted;
	}

	public void tick() {
	}

	public void init() {
	}

	public void abortTask() {
		this.aborted = true;
	}
}
