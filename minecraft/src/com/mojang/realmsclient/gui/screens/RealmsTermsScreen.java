package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsTermsScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final RealmsScreen lastScreen;
	private final RealmsMainScreen mainScreen;
	private final RealmsServer realmsServer;
	private RealmsButton agreeButton;
	private boolean onLink;
	private final String realmsToSUrl = "https://minecraft.net/realms/terms";

	public RealmsTermsScreen(RealmsScreen lastScreen, RealmsMainScreen mainScreen, RealmsServer realmsServer) {
		this.lastScreen = lastScreen;
		this.mainScreen = mainScreen;
		this.realmsServer = realmsServer;
	}

	@Override
	public void init() {
		this.setKeyboardHandlerSendRepeatsToGui(true);
		int i = this.width() / 4;
		int j = this.width() / 4 - 2;
		int k = this.width() / 2 + 4;
		this.buttonsAdd(this.agreeButton = new RealmsButton(1, i, RealmsConstants.row(12), j, 20, getLocalizedString("mco.terms.buttons.agree")) {
			@Override
			public void onPress() {
				RealmsTermsScreen.this.agreedToTos();
			}
		});
		this.buttonsAdd(new RealmsButton(2, k, RealmsConstants.row(12), j, 20, getLocalizedString("mco.terms.buttons.disagree")) {
			@Override
			public void onPress() {
				Realms.setScreen(RealmsTermsScreen.this.lastScreen);
			}
		});
	}

	@Override
	public void removed() {
		this.setKeyboardHandlerSendRepeatsToGui(false);
	}

	@Override
	public boolean keyPressed(int eventKey, int scancode, int mods) {
		if (eventKey == 256) {
			Realms.setScreen(this.lastScreen);
			return true;
		} else {
			return super.keyPressed(eventKey, scancode, mods);
		}
	}

	private void agreedToTos() {
		RealmsClient realmsClient = RealmsClient.createRealmsClient();

		try {
			realmsClient.agreeToTos();
			RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(
				this.lastScreen, new RealmsTasks.RealmsGetServerDetailsTask(this.mainScreen, this.lastScreen, this.realmsServer, new ReentrantLock())
			);
			realmsLongRunningMcoTaskScreen.start();
			Realms.setScreen(realmsLongRunningMcoTaskScreen);
		} catch (RealmsServiceException var3) {
			LOGGER.error("Couldn't agree to TOS");
		}
	}

	@Override
	public boolean mouseClicked(double x, double y, int buttonNum) {
		if (this.onLink) {
			Realms.setClipboard("https://minecraft.net/realms/terms");
			RealmsUtil.browseTo("https://minecraft.net/realms/terms");
			return true;
		} else {
			return super.mouseClicked(x, y, buttonNum);
		}
	}

	@Override
	public void render(int xm, int ym, float a) {
		this.renderBackground();
		this.drawCenteredString(getLocalizedString("mco.terms.title"), this.width() / 2, 17, 16777215);
		this.drawString(getLocalizedString("mco.terms.sentence.1"), this.width() / 2 - 120, RealmsConstants.row(5), 16777215);
		int i = this.fontWidth(getLocalizedString("mco.terms.sentence.1"));
		int j = this.width() / 2 - 121 + i;
		int k = RealmsConstants.row(5);
		int l = j + this.fontWidth("mco.terms.sentence.2") + 1;
		int m = k + 1 + this.fontLineHeight();
		if (j <= xm && xm <= l && k <= ym && ym <= m) {
			this.onLink = true;
			this.drawString(" " + getLocalizedString("mco.terms.sentence.2"), this.width() / 2 - 120 + i, RealmsConstants.row(5), 7107012);
		} else {
			this.onLink = false;
			this.drawString(" " + getLocalizedString("mco.terms.sentence.2"), this.width() / 2 - 120 + i, RealmsConstants.row(5), 3368635);
		}

		super.render(xm, ym, a);
	}
}
