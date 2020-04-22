package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class RealmsNotificationsScreen extends RealmsScreen {
	private static final Identifier field_22698 = new Identifier("realms", "textures/gui/realms/invite_icon.png");
	private static final Identifier field_22699 = new Identifier("realms", "textures/gui/realms/trial_icon.png");
	private static final Identifier field_22700 = new Identifier("realms", "textures/gui/realms/news_notification_mainscreen.png");
	private static final RealmsDataFetcher realmsDataFetcher = new RealmsDataFetcher();
	private volatile int numberOfPendingInvites;
	private static boolean checkedMcoAvailability;
	private static boolean trialAvailable;
	private static boolean validClient;
	private static boolean hasUnreadNews;

	@Override
	public void init() {
		this.checkIfMcoEnabled();
		this.client.keyboard.enableRepeatEvents(true);
	}

	@Override
	public void tick() {
		if ((!this.method_25169() || !this.method_25170() || !validClient) && !realmsDataFetcher.isStopped()) {
			realmsDataFetcher.stop();
		} else if (validClient && this.method_25169()) {
			realmsDataFetcher.initWithSpecificTaskList();
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

	private boolean method_25169() {
		return this.client.options.realmsNotifications;
	}

	private boolean method_25170() {
		return this.client.currentScreen instanceof TitleScreen;
	}

	private void checkIfMcoEnabled() {
		if (!checkedMcoAvailability) {
			checkedMcoAvailability = true;
			(new Thread("Realms Notification Availability checker #1") {
				public void run() {
					RealmsClient realmsClient = RealmsClient.createRealmsClient();

					try {
						RealmsClient.CompatibleVersionResponse compatibleVersionResponse = realmsClient.clientCompatible();
						if (compatibleVersionResponse != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
							return;
						}
					} catch (RealmsServiceException var3) {
						if (var3.httpResultCode != 401) {
							RealmsNotificationsScreen.checkedMcoAvailability = false;
						}

						return;
					}

					RealmsNotificationsScreen.validClient = true;
				}
			}).start();
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (validClient) {
			this.drawIcons(matrices, mouseX, mouseY);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	private void drawIcons(MatrixStack matrixStack, int i, int j) {
		int k = this.numberOfPendingInvites;
		int l = 24;
		int m = this.height / 4 + 48;
		int n = this.width / 2 + 80;
		int o = m + 48 + 2;
		int p = 0;
		if (hasUnreadNews) {
			this.client.getTextureManager().bindTexture(field_22700);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.4F, 0.4F, 0.4F);
			DrawableHelper.drawTexture(matrixStack, (int)((double)(n + 2 - p) * 2.5), (int)((double)o * 2.5), 0.0F, 0.0F, 40, 40, 40, 40);
			RenderSystem.popMatrix();
			p += 14;
		}

		if (k != 0) {
			this.client.getTextureManager().bindTexture(field_22698);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.drawTexture(matrixStack, n - p, o - 6, 0.0F, 0.0F, 15, 25, 31, 25);
			p += 16;
		}

		if (trialAvailable) {
			this.client.getTextureManager().bindTexture(field_22699);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			int q = 0;
			if ((Util.getMeasuringTimeMs() / 800L & 1L) == 1L) {
				q = 8;
			}

			DrawableHelper.drawTexture(matrixStack, n + 4 - p, o + 4, 0.0F, (float)q, 8, 8, 8, 16);
		}
	}

	@Override
	public void removed() {
		realmsDataFetcher.stop();
	}
}
