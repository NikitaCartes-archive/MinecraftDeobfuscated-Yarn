package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class LongRunningTask implements Runnable {
	protected RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen;

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
