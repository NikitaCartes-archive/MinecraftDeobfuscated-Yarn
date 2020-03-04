package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class LongRunningTask implements Runnable {
	public static final Logger LOGGER = LogManager.getLogger();
	protected RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen;

	/**
	 * Moved from RealmsTasks in 20w10a.
	 */
	protected static void pause(int seconds) {
		try {
			Thread.sleep((long)(seconds * 1000));
		} catch (InterruptedException var2) {
			LOGGER.error("", (Throwable)var2);
		}
	}

	/**
	 * Moved from Realms in 20w10a.
	 */
	public static void setScreen(Screen screen) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.execute(() -> minecraftClient.openScreen(screen));
	}

	public void setScreen(RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen) {
		this.longRunningMcoTaskScreen = longRunningMcoTaskScreen;
	}

	public void error(String errorMessage) {
		this.longRunningMcoTaskScreen.method_21290(errorMessage);
	}

	public void setTitle(String title) {
		this.longRunningMcoTaskScreen.setTitle(title);
	}

	public boolean aborted() {
		return this.longRunningMcoTaskScreen.aborted();
	}

	public void tick() {
	}

	public void init() {
	}

	public void abortTask() {
	}
}
