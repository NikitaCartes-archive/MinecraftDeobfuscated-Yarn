package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.RealmsGetServerDetailsTask;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsTermsScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Screen field_22727;
	private final RealmsMainScreen mainScreen;
	private final RealmsServer realmsServer;
	private boolean onLink;
	private final String realmsToSUrl = "https://minecraft.net/realms/terms";

	public RealmsTermsScreen(Screen screen, RealmsMainScreen mainScreen, RealmsServer realmsServer) {
		this.field_22727 = screen;
		this.mainScreen = mainScreen;
		this.realmsServer = realmsServer;
	}

	@Override
	public void init() {
		this.client.keyboard.enableRepeatEvents(true);
		int i = this.width / 4 - 2;
		this.addButton(new ButtonWidget(this.width / 4, row(12), i, 20, new TranslatableText("mco.terms.buttons.agree"), buttonWidget -> this.agreedToTos()));
		this.addButton(
			new ButtonWidget(
				this.width / 2 + 4, row(12), i, 20, new TranslatableText("mco.terms.buttons.disagree"), buttonWidget -> this.client.openScreen(this.field_22727)
			)
		);
	}

	@Override
	public void removed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.client.openScreen(this.field_22727);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	private void agreedToTos() {
		RealmsClient realmsClient = RealmsClient.createRealmsClient();

		try {
			realmsClient.agreeToTos();
			this.client
				.openScreen(
					new RealmsLongRunningMcoTaskScreen(
						this.field_22727, new RealmsGetServerDetailsTask(this.mainScreen, this.field_22727, this.realmsServer, new ReentrantLock())
					)
				);
		} catch (RealmsServiceException var3) {
			LOGGER.error("Couldn't agree to TOS");
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.onLink) {
			this.client.keyboard.setClipboard("https://minecraft.net/realms/terms");
			Util.getOperatingSystem().open("https://minecraft.net/realms/terms");
			return true;
		} else {
			return super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.drawCenteredString(matrices, this.textRenderer, I18n.translate("mco.terms.title"), this.width / 2, 17, 16777215);
		this.textRenderer.draw(matrices, I18n.translate("mco.terms.sentence.1"), (float)(this.width / 2 - 120), (float)row(5), 16777215);
		int i = this.textRenderer.getWidth(I18n.translate("mco.terms.sentence.1"));
		int j = this.width / 2 - 121 + i;
		int k = row(5);
		int l = j + this.textRenderer.getWidth("mco.terms.sentence.2") + 1;
		int m = k + 1 + 9;
		if (j <= mouseX && mouseX <= l && k <= mouseY && mouseY <= m) {
			this.onLink = true;
			this.textRenderer.draw(matrices, " " + I18n.translate("mco.terms.sentence.2"), (float)(this.width / 2 - 120 + i), (float)row(5), 7107012);
		} else {
			this.onLink = false;
			this.textRenderer.draw(matrices, " " + I18n.translate("mco.terms.sentence.2"), (float)(this.width / 2 - 120 + i), (float)row(5), 3368635);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}
}
