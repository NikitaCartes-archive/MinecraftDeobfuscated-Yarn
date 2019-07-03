package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class class_4399 extends RealmsScreen {
	private static final class_4360 field_19923 = new class_4360();
	private volatile int field_19924;
	private static boolean field_19925;
	private static boolean field_19926;
	private static boolean field_19927;
	private static boolean field_19928;
	private static final List<class_4360.class_4364> field_19929 = Arrays.asList(
		class_4360.class_4364.PENDING_INVITE, class_4360.class_4364.TRIAL_AVAILABLE, class_4360.class_4364.UNREAD_NEWS
	);

	public class_4399(RealmsScreen realmsScreen) {
	}

	@Override
	public void init() {
		this.method_21294();
		this.setKeyboardHandlerSendRepeatsToGui(true);
	}

	@Override
	public void tick() {
		if ((!Realms.getRealmsNotificationsEnabled() || !Realms.inTitleScreen() || !field_19927) && !field_19923.method_21073()) {
			field_19923.method_21097();
		} else if (field_19927 && Realms.getRealmsNotificationsEnabled()) {
			field_19923.method_21082(field_19929);
			if (field_19923.method_21075(class_4360.class_4364.PENDING_INVITE)) {
				this.field_19924 = field_19923.method_21092();
			}

			if (field_19923.method_21075(class_4360.class_4364.TRIAL_AVAILABLE)) {
				field_19926 = field_19923.method_21093();
			}

			if (field_19923.method_21075(class_4360.class_4364.UNREAD_NEWS)) {
				field_19928 = field_19923.method_21095();
			}

			field_19923.method_21088();
		}
	}

	private void method_21294() {
		if (!field_19925) {
			field_19925 = true;
			(new Thread("Realms Notification Availability checker #1") {
				public void run() {
					class_4341 lv = class_4341.method_20989();

					try {
						class_4341.class_4342 lv2 = lv.method_21027();
						if (!lv2.equals(class_4341.class_4342.COMPATIBLE)) {
							return;
						}
					} catch (class_4355 var3) {
						if (var3.field_19604 != 401) {
							class_4399.field_19925 = false;
						}

						return;
					} catch (IOException var4) {
						class_4399.field_19925 = false;
						return;
					}

					class_4399.field_19927 = true;
				}
			}).start();
		}
	}

	@Override
	public void render(int i, int j, float f) {
		if (field_19927) {
			this.method_21295(i, j);
		}

		super.render(i, j, f);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return super.mouseClicked(d, e, i);
	}

	private void method_21295(int i, int j) {
		int k = this.field_19924;
		int l = 24;
		int m = this.height() / 4 + 48;
		int n = this.width() / 2 + 80;
		int o = m + 48 + 2;
		int p = 0;
		if (field_19928) {
			RealmsScreen.bind("realms:textures/gui/realms/news_notification_mainscreen.png");
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.4F, 0.4F, 0.4F);
			RealmsScreen.blit((int)((double)(n + 2 - p) * 2.5), (int)((double)o * 2.5), 0.0F, 0.0F, 40, 40, 40, 40);
			GlStateManager.popMatrix();
			p += 14;
		}

		if (k != 0) {
			RealmsScreen.bind("realms:textures/gui/realms/invite_icon.png");
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			RealmsScreen.blit(n - p, o - 6, 0.0F, 0.0F, 15, 25, 31, 25);
			GlStateManager.popMatrix();
			p += 16;
		}

		if (field_19926) {
			RealmsScreen.bind("realms:textures/gui/realms/trial_icon.png");
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			int q = 0;
			if ((System.currentTimeMillis() / 800L & 1L) == 1L) {
				q = 8;
			}

			RealmsScreen.blit(n + 4 - p, o + 4, 0.0F, (float)q, 8, 8, 8, 16);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void removed() {
		field_19923.method_21097();
	}
}
