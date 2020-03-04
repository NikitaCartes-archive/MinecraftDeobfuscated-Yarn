package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsSubscriptionInfoScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Screen lastScreen;
	private final RealmsServer serverData;
	private final Screen mainScreen;
	private final String subscriptionTitle;
	private final String subscriptionStartLabelText;
	private final String timeLeftLabelText;
	private final String daysLeftLabelText;
	private int daysLeft;
	private String startDate;
	private Subscription.SubscriptionType type;

	public RealmsSubscriptionInfoScreen(Screen screen, RealmsServer serverData, Screen screen2) {
		this.lastScreen = screen;
		this.serverData = serverData;
		this.mainScreen = screen2;
		this.subscriptionTitle = I18n.translate("mco.configure.world.subscription.title");
		this.subscriptionStartLabelText = I18n.translate("mco.configure.world.subscription.start");
		this.timeLeftLabelText = I18n.translate("mco.configure.world.subscription.timeleft");
		this.daysLeftLabelText = I18n.translate("mco.configure.world.subscription.recurring.daysleft");
	}

	@Override
	public void init() {
		this.getSubscription(this.serverData.id);
		Realms.narrateNow(this.subscriptionTitle, this.subscriptionStartLabelText, this.startDate, this.timeLeftLabelText, this.daysLeftPresentation(this.daysLeft));
		this.client.keyboard.enableRepeatEvents(true);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 100,
				row(6),
				200,
				20,
				I18n.translate("mco.configure.world.subscription.extend"),
				buttonWidget -> {
					String string = "https://aka.ms/ExtendJavaRealms?subscriptionId="
						+ this.serverData.remoteSubscriptionId
						+ "&profileId="
						+ this.client.getSession().getUuid();
					this.client.keyboard.setClipboard(string);
					Util.getOperatingSystem().open(string);
				}
			)
		);
		this.addButton(new ButtonWidget(this.width / 2 - 100, row(12), 200, 20, I18n.translate("gui.back"), buttonWidget -> this.client.openScreen(this.lastScreen)));
		if (this.serverData.expired) {
			this.addButton(new ButtonWidget(this.width / 2 - 100, row(10), 200, 20, I18n.translate("mco.configure.world.delete.button"), buttonWidget -> {
				String string = I18n.translate("mco.configure.world.delete.question.line1");
				String string2 = I18n.translate("mco.configure.world.delete.question.line2");
				this.client.openScreen(new RealmsLongConfirmationScreen(this::method_25271, RealmsLongConfirmationScreen.Type.Warning, string, string2, true));
			}));
		}
	}

	private void method_25271(boolean bl) {
		if (bl) {
			(new Thread("Realms-delete-realm") {
				public void run() {
					try {
						RealmsClient realmsClient = RealmsClient.createRealmsClient();
						realmsClient.deleteWorld(RealmsSubscriptionInfoScreen.this.serverData.id);
					} catch (RealmsServiceException var2) {
						RealmsSubscriptionInfoScreen.LOGGER.error("Couldn't delete world");
						RealmsSubscriptionInfoScreen.LOGGER.error(var2);
					}

					RealmsSubscriptionInfoScreen.this.client.execute(() -> RealmsSubscriptionInfoScreen.this.client.openScreen(RealmsSubscriptionInfoScreen.this.mainScreen));
				}
			}).start();
		}

		this.client.openScreen(this);
	}

	private void getSubscription(long worldId) {
		RealmsClient realmsClient = RealmsClient.createRealmsClient();

		try {
			Subscription subscription = realmsClient.subscriptionFor(worldId);
			this.daysLeft = subscription.daysLeft;
			this.startDate = this.localPresentation(subscription.startDate);
			this.type = subscription.type;
		} catch (RealmsServiceException var5) {
			LOGGER.error("Couldn't get subscription");
			this.client.openScreen(new RealmsGenericErrorScreen(var5, this.lastScreen));
		}
	}

	private String localPresentation(long cetTime) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(cetTime);
		return DateFormat.getDateTimeInstance().format(calendar.getTime());
	}

	@Override
	public void removed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.client.openScreen(this.lastScreen);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		int i = this.width / 2 - 100;
		this.drawCenteredString(this.textRenderer, this.subscriptionTitle, this.width / 2, 17, 16777215);
		this.textRenderer.draw(this.subscriptionStartLabelText, (float)i, (float)row(0), 10526880);
		this.textRenderer.draw(this.startDate, (float)i, (float)row(1), 16777215);
		if (this.type == Subscription.SubscriptionType.NORMAL) {
			this.textRenderer.draw(this.timeLeftLabelText, (float)i, (float)row(3), 10526880);
		} else if (this.type == Subscription.SubscriptionType.RECURRING) {
			this.textRenderer.draw(this.daysLeftLabelText, (float)i, (float)row(3), 10526880);
		}

		this.textRenderer.draw(this.daysLeftPresentation(this.daysLeft), (float)i, (float)row(4), 16777215);
		super.render(mouseX, mouseY, delta);
	}

	private String daysLeftPresentation(int daysLeft) {
		if (daysLeft == -1 && this.serverData.expired) {
			return I18n.translate("mco.configure.world.subscription.expired");
		} else if (daysLeft <= 1) {
			return I18n.translate("mco.configure.world.subscription.less_than_a_day");
		} else {
			int i = daysLeft / 30;
			int j = daysLeft % 30;
			StringBuilder stringBuilder = new StringBuilder();
			if (i > 0) {
				stringBuilder.append(i).append(" ");
				if (i == 1) {
					stringBuilder.append(I18n.translate("mco.configure.world.subscription.month").toLowerCase(Locale.ROOT));
				} else {
					stringBuilder.append(I18n.translate("mco.configure.world.subscription.months").toLowerCase(Locale.ROOT));
				}
			}

			if (j > 0) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(", ");
				}

				stringBuilder.append(j).append(" ");
				if (j == 1) {
					stringBuilder.append(I18n.translate("mco.configure.world.subscription.day").toLowerCase(Locale.ROOT));
				} else {
					stringBuilder.append(I18n.translate("mco.configure.world.subscription.days").toLowerCase(Locale.ROOT));
				}
			}

			return stringBuilder.toString();
		}
	}
}
