package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.util.Errable;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class LongRunningTask implements Errable, Runnable {
	protected static final int MAX_RETRIES = 25;
	private static final Logger LOGGER = LogUtils.getLogger();
	protected RealmsLongRunningMcoTaskScreen screen;

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

	public void setScreen(RealmsLongRunningMcoTaskScreen screen) {
		this.screen = screen;
	}

	@Override
	public void error(Text errorMessage) {
		this.screen.error(errorMessage);
	}

	public void setTitle(Text title) {
		this.screen.setTitle(title);
	}

	public boolean aborted() {
		return this.screen.aborted();
	}

	public void tick() {
	}

	public void init() {
	}

	public void abortTask() {
	}
}
