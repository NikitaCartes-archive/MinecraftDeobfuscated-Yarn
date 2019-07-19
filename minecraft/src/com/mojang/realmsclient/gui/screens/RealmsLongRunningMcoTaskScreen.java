package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsLongRunningMcoTaskScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final int BUTTON_CANCEL_ID = 666;
	private final int BUTTON_BACK_ID = 667;
	private final RealmsScreen lastScreen;
	private final LongRunningTask taskThread;
	private volatile String title = "";
	private volatile boolean error;
	private volatile String errorMessage;
	private volatile boolean aborted;
	private int animTicks;
	private final LongRunningTask task;
	private final int buttonLength = 212;
	public static final String[] symbols = new String[]{
		"▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃",
		"_ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄",
		"_ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅",
		"_ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆",
		"_ _ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇",
		"_ _ _ _ _ ▃ ▄ ▅ ▆ ▇ █",
		"_ _ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇",
		"_ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆",
		"_ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅",
		"_ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄",
		"▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃",
		"▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _",
		"▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _",
		"▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _",
		"▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _ _",
		"█ ▇ ▆ ▅ ▄ ▃ _ _ _ _ _",
		"▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _ _",
		"▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _",
		"▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _",
		"▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _"
	};

	public RealmsLongRunningMcoTaskScreen(RealmsScreen lastScreen, LongRunningTask task) {
		this.lastScreen = lastScreen;
		this.task = task;
		task.setScreen(this);
		this.taskThread = task;
	}

	public void start() {
		Thread thread = new Thread(this.taskThread, "Realms-long-running-task");
		thread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
		thread.start();
	}

	@Override
	public void tick() {
		super.tick();
		Realms.narrateRepeatedly(this.title);
		this.animTicks++;
		this.task.tick();
	}

	@Override
	public boolean keyPressed(int eventKey, int scancode, int mods) {
		if (eventKey == 256) {
			this.cancelOrBackButtonClicked();
			return true;
		} else {
			return super.keyPressed(eventKey, scancode, mods);
		}
	}

	@Override
	public void init() {
		this.task.init();
		this.buttonsAdd(new RealmsButton(666, this.width() / 2 - 106, RealmsConstants.row(12), 212, 20, getLocalizedString("gui.cancel")) {
			@Override
			public void onPress() {
				RealmsLongRunningMcoTaskScreen.this.cancelOrBackButtonClicked();
			}
		});
	}

	private void cancelOrBackButtonClicked() {
		this.aborted = true;
		this.task.abortTask();
		Realms.setScreen(this.lastScreen);
	}

	@Override
	public void render(int xm, int ym, float a) {
		this.renderBackground();
		this.drawCenteredString(this.title, this.width() / 2, RealmsConstants.row(3), 16777215);
		if (!this.error) {
			this.drawCenteredString(symbols[this.animTicks % symbols.length], this.width() / 2, RealmsConstants.row(8), 8421504);
		}

		if (this.error) {
			this.drawCenteredString(this.errorMessage, this.width() / 2, RealmsConstants.row(8), 16711680);
		}

		super.render(xm, ym, a);
	}

	public void method_21290(String string) {
		this.error = true;
		this.errorMessage = string;
		Realms.narrateNow(string);
		this.buttonsClear();
		this.buttonsAdd(new RealmsButton(667, this.width() / 2 - 106, this.height() / 4 + 120 + 12, getLocalizedString("gui.back")) {
			@Override
			public void onPress() {
				RealmsLongRunningMcoTaskScreen.this.cancelOrBackButtonClicked();
			}
		});
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean aborted() {
		return this.aborted;
	}
}
