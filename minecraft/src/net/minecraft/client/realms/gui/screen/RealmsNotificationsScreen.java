package net.minecraft.client.realms.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_7578;
import net.minecraft.class_7581;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class RealmsNotificationsScreen extends RealmsScreen {
	private static final Identifier INVITE_ICON = new Identifier("realms", "textures/gui/realms/invite_icon.png");
	private static final Identifier TRIAL_ICON = new Identifier("realms", "textures/gui/realms/trial_icon.png");
	private static final Identifier NEWS_NOTIFICATION = new Identifier("realms", "textures/gui/realms/news_notification_mainscreen.png");
	@Nullable
	private class_7581.class_7584 field_39695;
	private volatile int numberOfPendingInvites;
	static boolean checkedMcoAvailability;
	private static boolean trialAvailable;
	static boolean validClient;
	private static boolean hasUnreadNews;

	public RealmsNotificationsScreen() {
		super(NarratorManager.EMPTY);
	}

	@Override
	public void init() {
		this.checkIfMcoEnabled();
		this.client.keyboard.setRepeatEvents(true);
		if (this.field_39695 != null) {
			this.field_39695.method_44634();
		}
	}

	@Override
	public void tick() {
		boolean bl = this.shouldShowNotifications() && this.isTitleScreen() && validClient;
		if (this.field_39695 == null && bl) {
			this.field_39695 = this.method_44624(this.client.method_44646());
		} else if (this.field_39695 != null && !bl) {
			this.field_39695 = null;
		}

		if (this.field_39695 != null) {
			this.field_39695.method_44636();
		}
	}

	private class_7581.class_7584 method_44624(class_7578 arg) {
		class_7581.class_7584 lv = arg.field_39682.method_44628();
		lv.method_44635(arg.field_39685, integer -> this.numberOfPendingInvites = integer);
		lv.method_44635(arg.field_39686, boolean_ -> trialAvailable = boolean_);
		lv.method_44635(arg.field_39687, realmsNews -> {
			arg.field_39688.method_44619(realmsNews);
			hasUnreadNews = arg.field_39688.method_44618();
		});
		return lv;
	}

	private boolean shouldShowNotifications() {
		return this.client.options.getRealmsNotifications().getValue();
	}

	private boolean isTitleScreen() {
		return this.client.currentScreen instanceof TitleScreen;
	}

	private void checkIfMcoEnabled() {
		if (!checkedMcoAvailability) {
			checkedMcoAvailability = true;
			(new Thread("Realms Notification Availability checker #1") {
				public void run() {
					RealmsClient realmsClient = RealmsClient.method_44616();

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

	private void drawIcons(MatrixStack matrices, int mouseX, int mouseY) {
		int i = this.numberOfPendingInvites;
		int j = 24;
		int k = this.height / 4 + 48;
		int l = this.width / 2 + 80;
		int m = k + 48 + 2;
		int n = 0;
		if (hasUnreadNews) {
			RenderSystem.setShaderTexture(0, NEWS_NOTIFICATION);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			matrices.push();
			matrices.scale(0.4F, 0.4F, 0.4F);
			DrawableHelper.drawTexture(matrices, (int)((double)(l + 2 - n) * 2.5), (int)((double)m * 2.5), 0.0F, 0.0F, 40, 40, 40, 40);
			matrices.pop();
			n += 14;
		}

		if (i != 0) {
			RenderSystem.setShaderTexture(0, INVITE_ICON);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.drawTexture(matrices, l - n, m - 6, 0.0F, 0.0F, 15, 25, 31, 25);
			n += 16;
		}

		if (trialAvailable) {
			RenderSystem.setShaderTexture(0, TRIAL_ICON);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			int o = 0;
			if ((Util.getMeasuringTimeMs() / 800L & 1L) == 1L) {
				o = 8;
			}

			DrawableHelper.drawTexture(matrices, l + 4 - n, m + 4, 0.0F, (float)o, 8, 8, 8, 16);
		}
	}
}
