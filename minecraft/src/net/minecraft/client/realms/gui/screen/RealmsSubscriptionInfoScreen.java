package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ScrollableTextWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.Subscription;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsPopups;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Urls;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsSubscriptionInfoScreen extends RealmsScreen {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final Text SUBSCRIPTION_TITLE = Text.translatable("mco.configure.world.subscription.title");
	private static final Text SUBSCRIPTION_START_LABEL_TEXT = Text.translatable("mco.configure.world.subscription.start");
	private static final Text TIME_LEFT_LABEL_TEXT = Text.translatable("mco.configure.world.subscription.timeleft");
	private static final Text DAYS_LEFT_LABEL_TEXT = Text.translatable("mco.configure.world.subscription.recurring.daysleft");
	private static final Text EXPIRED_TEXT = Text.translatable("mco.configure.world.subscription.expired");
	private static final Text EXPIRES_IN_LESS_THAN_A_DAY_TEXT = Text.translatable("mco.configure.world.subscription.less_than_a_day");
	private static final Text UNKNOWN_TEXT = Text.translatable("mco.configure.world.subscription.unknown");
	private static final Text RECURRING_INFO_TEXT = Text.translatable("mco.configure.world.subscription.recurring.info");
	private final Screen parent;
	final RealmsServer serverData;
	final Screen mainScreen;
	private Text daysLeft = UNKNOWN_TEXT;
	private Text startDate = UNKNOWN_TEXT;
	@Nullable
	private Subscription.SubscriptionType type;

	public RealmsSubscriptionInfoScreen(Screen parent, RealmsServer serverData, Screen mainScreen) {
		super(NarratorManager.EMPTY);
		this.parent = parent;
		this.serverData = serverData;
		this.mainScreen = mainScreen;
	}

	@Override
	public void init() {
		this.getSubscription(this.serverData.id);
		this.addDrawableChild(
			ButtonWidget.builder(
					Text.translatable("mco.configure.world.subscription.extend"),
					button -> ConfirmLinkScreen.open(this, Urls.getExtendJavaRealmsUrl(this.serverData.remoteSubscriptionId, this.client.getSession().getUuidOrNull()))
				)
				.dimensions(this.width / 2 - 100, row(6), 200, 20)
				.build()
		);
		if (this.serverData.expired) {
			this.addDrawableChild(
				ButtonWidget.builder(
						Text.translatable("mco.configure.world.delete.button"),
						button -> this.client
								.setScreen(
									RealmsPopups.createContinuableWarningPopup(
										this, Text.translatable("mco.configure.world.delete.question.line1"), popupScreen -> this.onDeletionConfirmed()
									)
								)
					)
					.dimensions(this.width / 2 - 100, row(10), 200, 20)
					.build()
			);
		} else if (RealmsMainScreen.isSnapshotRealmsEligible() && this.serverData.parentWorldName != null) {
			this.addDrawableChild(
				new ScrollableTextWidget(
					this.width / 2 - 100, row(8), 200, 46, Text.translatable("mco.snapshot.subscription.info", this.serverData.parentWorldName), this.textRenderer
				)
			);
		} else {
			this.addDrawableChild(new ScrollableTextWidget(this.width / 2 - 100, row(8), 200, 46, RECURRING_INFO_TEXT, this.textRenderer));
		}

		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).dimensions(this.width / 2 - 100, row(12), 200, 20).build());
	}

	@Override
	public Text getNarratedTitle() {
		return ScreenTexts.joinLines(SUBSCRIPTION_TITLE, SUBSCRIPTION_START_LABEL_TEXT, this.startDate, TIME_LEFT_LABEL_TEXT, this.daysLeft);
	}

	private void onDeletionConfirmed() {
		(new Thread("Realms-delete-realm") {
			public void run() {
				try {
					RealmsClient realmsClient = RealmsClient.create();
					realmsClient.deleteWorld(RealmsSubscriptionInfoScreen.this.serverData.id);
				} catch (RealmsServiceException var2) {
					RealmsSubscriptionInfoScreen.LOGGER.error("Couldn't delete world", (Throwable)var2);
				}

				RealmsSubscriptionInfoScreen.this.client.execute(() -> RealmsSubscriptionInfoScreen.this.client.setScreen(RealmsSubscriptionInfoScreen.this.mainScreen));
			}
		}).start();
		this.client.setScreen(this);
	}

	private void getSubscription(long worldId) {
		RealmsClient realmsClient = RealmsClient.create();

		try {
			Subscription subscription = realmsClient.subscriptionFor(worldId);
			this.daysLeft = this.daysLeftPresentation(subscription.daysLeft);
			this.startDate = localPresentation(subscription.startDate);
			this.type = subscription.type;
		} catch (RealmsServiceException var5) {
			LOGGER.error("Couldn't get subscription", (Throwable)var5);
			this.client.setScreen(new RealmsGenericErrorScreen(var5, this.parent));
		}
	}

	private static Text localPresentation(long time) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(time);
		return Text.literal(DateFormat.getDateTimeInstance().format(calendar.getTime()));
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		int i = this.width / 2 - 100;
		context.drawCenteredTextWithShadow(this.textRenderer, SUBSCRIPTION_TITLE, this.width / 2, 17, Colors.WHITE);
		context.drawText(this.textRenderer, SUBSCRIPTION_START_LABEL_TEXT, i, row(0), Colors.LIGHT_GRAY, false);
		context.drawText(this.textRenderer, this.startDate, i, row(1), Colors.WHITE, false);
		if (this.type == Subscription.SubscriptionType.NORMAL) {
			context.drawText(this.textRenderer, TIME_LEFT_LABEL_TEXT, i, row(3), Colors.LIGHT_GRAY, false);
		} else if (this.type == Subscription.SubscriptionType.RECURRING) {
			context.drawText(this.textRenderer, DAYS_LEFT_LABEL_TEXT, i, row(3), Colors.LIGHT_GRAY, false);
		}

		context.drawText(this.textRenderer, this.daysLeft, i, row(4), Colors.WHITE, false);
	}

	private Text daysLeftPresentation(int daysLeft) {
		if (daysLeft < 0 && this.serverData.expired) {
			return EXPIRED_TEXT;
		} else if (daysLeft <= 1) {
			return EXPIRES_IN_LESS_THAN_A_DAY_TEXT;
		} else {
			int i = daysLeft / 30;
			int j = daysLeft % 30;
			boolean bl = i > 0;
			boolean bl2 = j > 0;
			if (bl && bl2) {
				return Text.translatable("mco.configure.world.subscription.remaining.months.days", i, j);
			} else if (bl) {
				return Text.translatable("mco.configure.world.subscription.remaining.months", i);
			} else {
				return bl2 ? Text.translatable("mco.configure.world.subscription.remaining.days", j) : Text.empty();
			}
		}
	}
}
