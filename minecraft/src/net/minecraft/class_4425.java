package net.minecraft;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.Subscription;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4425 extends RealmsScreen {
	private static final Logger field_20146 = LogManager.getLogger();
	private final RealmsScreen field_20147;
	private final RealmsServer field_20148;
	private final RealmsScreen field_20149;
	private final int field_20150 = 0;
	private final int field_20151 = 1;
	private final int field_20152 = 2;
	private final String field_20153;
	private final String field_20154;
	private final String field_20155;
	private final String field_20156;
	private int field_20157;
	private String field_20158;
	private Subscription.class_4322 field_20159;
	private final String field_20160 = "https://account.mojang.com/buy/realms";

	public class_4425(RealmsScreen realmsScreen, RealmsServer realmsServer, RealmsScreen realmsScreen2) {
		this.field_20147 = realmsScreen;
		this.field_20148 = realmsServer;
		this.field_20149 = realmsScreen2;
		this.field_20153 = getLocalizedString("mco.configure.world.subscription.title");
		this.field_20154 = getLocalizedString("mco.configure.world.subscription.start");
		this.field_20155 = getLocalizedString("mco.configure.world.subscription.timeleft");
		this.field_20156 = getLocalizedString("mco.configure.world.subscription.recurring.daysleft");
	}

	@Override
	public void init() {
		this.method_21500(this.field_20148.id);
		Realms.narrateNow(this.field_20153, this.field_20154, this.field_20158, this.field_20155, this.method_21499(this.field_20157));
		this.setKeyboardHandlerSendRepeatsToGui(true);
		this.buttonsAdd(new RealmsButton(2, this.width() / 2 - 100, class_4359.method_21072(6), getLocalizedString("mco.configure.world.subscription.extend")) {
			@Override
			public void onPress() {
				String string = "https://account.mojang.com/buy/realms?sid=" + class_4425.this.field_20148.remoteSubscriptionId + "&pid=" + Realms.getUUID();
				Realms.setClipboard(string);
				class_4448.method_21570(string);
			}
		});
		this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, class_4359.method_21072(12), getLocalizedString("gui.back")) {
			@Override
			public void onPress() {
				Realms.setScreen(class_4425.this.field_20147);
			}
		});
		if (this.field_20148.expired) {
			this.buttonsAdd(new RealmsButton(1, this.width() / 2 - 100, class_4359.method_21072(10), getLocalizedString("mco.configure.world.delete.button")) {
				@Override
				public void onPress() {
					String string = RealmsScreen.getLocalizedString("mco.configure.world.delete.question.line1");
					String string2 = RealmsScreen.getLocalizedString("mco.configure.world.delete.question.line2");
					Realms.setScreen(new class_4396(class_4425.this, class_4396.class_4397.WARNING, string, string2, true, 1));
				}
			});
		}
	}

	private void method_21500(long l) {
		class_4341 lv = class_4341.method_20989();

		try {
			Subscription subscription = lv.method_21025(l);
			this.field_20157 = subscription.daysLeft;
			this.field_20158 = this.method_21502(subscription.startDate);
			this.field_20159 = subscription.type;
		} catch (class_4355 var5) {
			field_20146.error("Couldn't get subscription");
			Realms.setScreen(new class_4394(var5, this.field_20147));
		} catch (IOException var6) {
			field_20146.error("Couldn't parse response subscribing");
		}
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		if (i == 1 && bl) {
			(new Thread("Realms-delete-realm") {
				public void run() {
					try {
						class_4341 lv = class_4341.method_20989();
						lv.method_21028(class_4425.this.field_20148.id);
					} catch (class_4355 var2) {
						class_4425.field_20146.error("Couldn't delete world");
						class_4425.field_20146.error(var2);
					} catch (IOException var3) {
						class_4425.field_20146.error("Couldn't delete world");
						var3.printStackTrace();
					}

					Realms.setScreen(class_4425.this.field_20149);
				}
			}).start();
		}

		Realms.setScreen(this);
	}

	private String method_21502(long l) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(l);
		return DateFormat.getDateTimeInstance().format(calendar.getTime());
	}

	@Override
	public void removed() {
		this.setKeyboardHandlerSendRepeatsToGui(false);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			Realms.setScreen(this.field_20147);
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		int k = this.width() / 2 - 100;
		this.drawCenteredString(this.field_20153, this.width() / 2, 17, 16777215);
		this.drawString(this.field_20154, k, class_4359.method_21072(0), 10526880);
		this.drawString(this.field_20158, k, class_4359.method_21072(1), 16777215);
		if (this.field_20159 == Subscription.class_4322.NORMAL) {
			this.drawString(this.field_20155, k, class_4359.method_21072(3), 10526880);
		} else if (this.field_20159 == Subscription.class_4322.RECURRING) {
			this.drawString(this.field_20156, k, class_4359.method_21072(3), 10526880);
		}

		this.drawString(this.method_21499(this.field_20157), k, class_4359.method_21072(4), 16777215);
		super.render(i, j, f);
	}

	private String method_21499(int i) {
		if (i == -1 && this.field_20148.expired) {
			return getLocalizedString("mco.configure.world.subscription.expired");
		} else if (i <= 1) {
			return getLocalizedString("mco.configure.world.subscription.less_than_a_day");
		} else {
			int j = i / 30;
			int k = i % 30;
			StringBuilder stringBuilder = new StringBuilder();
			if (j > 0) {
				stringBuilder.append(j).append(" ");
				if (j == 1) {
					stringBuilder.append(getLocalizedString("mco.configure.world.subscription.month").toLowerCase(Locale.ROOT));
				} else {
					stringBuilder.append(getLocalizedString("mco.configure.world.subscription.months").toLowerCase(Locale.ROOT));
				}
			}

			if (k > 0) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(", ");
				}

				stringBuilder.append(k).append(" ");
				if (k == 1) {
					stringBuilder.append(getLocalizedString("mco.configure.world.subscription.day").toLowerCase(Locale.ROOT));
				} else {
					stringBuilder.append(getLocalizedString("mco.configure.world.subscription.days").toLowerCase(Locale.ROOT));
				}
			}

			return stringBuilder.toString();
		}
	}
}
