package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class RealmsNotificationsScreen extends RealmsScreen {
	private static final RealmsDataFetcher realmsDataFetcher = new RealmsDataFetcher();
	private volatile int numberOfPendingInvites;
	private static boolean checkedMcoAvailability;
	private static boolean trialAvailable;
	private static boolean validClient;
	private static boolean hasUnreadNews;
	private static final List<RealmsDataFetcher.Task> tasks = Arrays.asList(
		RealmsDataFetcher.Task.PENDING_INVITE, RealmsDataFetcher.Task.TRIAL_AVAILABLE, RealmsDataFetcher.Task.UNREAD_NEWS
	);

	public RealmsNotificationsScreen(RealmsScreen lastScreen) {
	}

	@Override
	public void init() {
		this.checkIfMcoEnabled();
		this.setKeyboardHandlerSendRepeatsToGui(true);
	}

	@Override
	public void tick() {
		if ((!Realms.getRealmsNotificationsEnabled() || !Realms.inTitleScreen() || !validClient) && !realmsDataFetcher.isStopped()) {
			realmsDataFetcher.stop();
		} else if (validClient && Realms.getRealmsNotificationsEnabled()) {
			realmsDataFetcher.initWithSpecificTaskList(tasks);
			if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
				this.numberOfPendingInvites = realmsDataFetcher.getPendingInvitesCount();
			}

			if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE)) {
				trialAvailable = realmsDataFetcher.isTrialAvailable();
			}

			if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
				hasUnreadNews = realmsDataFetcher.hasUnreadNews();
			}

			realmsDataFetcher.markClean();
		}
	}

	private void checkIfMcoEnabled() {
		if (!checkedMcoAvailability) {
			checkedMcoAvailability = true;
			(new Thread("Realms Notification Availability checker #1") {
				public void run() {
					RealmsClient realmsClient = RealmsClient.createRealmsClient();

					try {
						RealmsClient.CompatibleVersionResponse compatibleVersionResponse = realmsClient.clientCompatible();
						if (!compatibleVersionResponse.equals(RealmsClient.CompatibleVersionResponse.COMPATIBLE)) {
							return;
						}
					} catch (RealmsServiceException var3) {
						if (var3.httpResultCode != 401) {
							RealmsNotificationsScreen.checkedMcoAvailability = false;
						}

						return;
					} catch (IOException var4) {
						RealmsNotificationsScreen.checkedMcoAvailability = false;
						return;
					}

					RealmsNotificationsScreen.validClient = true;
				}
			}).start();
		}
	}

	@Override
	public void render(int xm, int ym, float a) {
		if (validClient) {
			this.drawIcons(xm, ym);
		}

		super.render(xm, ym, a);
	}

	@Override
	public boolean mouseClicked(double xm, double ym, int button) {
		return super.mouseClicked(xm, ym, button);
	}

	private void drawIcons(int xm, int ym) {
		int i = this.numberOfPendingInvites;
		int j = 24;
		int k = this.height() / 4 + 48;
		int l = this.width() / 2 + 80;
		int m = k + 48 + 2;
		int n = 0;
		if (hasUnreadNews) {
			RealmsScreen.bind("realms:textures/gui/realms/news_notification_mainscreen.png");
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.4F, 0.4F, 0.4F);
			RealmsScreen.blit((int)((double)(l + 2 - n) * 2.5), (int)((double)m * 2.5), 0.0F, 0.0F, 40, 40, 40, 40);
			RenderSystem.popMatrix();
			n += 14;
		}

		if (i != 0) {
			RealmsScreen.bind("realms:textures/gui/realms/invite_icon.png");
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.pushMatrix();
			RealmsScreen.blit(l - n, m - 6, 0.0F, 0.0F, 15, 25, 31, 25);
			RenderSystem.popMatrix();
			n += 16;
		}

		if (trialAvailable) {
			RealmsScreen.bind("realms:textures/gui/realms/trial_icon.png");
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.pushMatrix();
			int o = 0;
			if ((System.currentTimeMillis() / 800L & 1L) == 1L) {
				o = 8;
			}

			RealmsScreen.blit(l + 4 - n, m + 4, 0.0F, (float)o, 8, 8, 8, 16);
			RenderSystem.popMatrix();
		}
	}

	@Override
	public void removed() {
		realmsDataFetcher.stop();
	}
}
