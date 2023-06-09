package net.minecraft.client.realms.gui.screen;

import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class RealmsNotificationsScreen extends RealmsScreen {
	private static final Identifier INVITE_ICON = new Identifier("realms", "textures/gui/realms/invite_icon.png");
	private static final Identifier TRIAL_ICON = new Identifier("realms", "textures/gui/realms/trial_icon.png");
	private static final Identifier NEWS_NOTIFICATION = new Identifier("realms", "textures/gui/realms/news_notification_mainscreen.png");
	private static final Identifier UNSEEN_NOTIFICATION = new Identifier("minecraft", "textures/gui/unseen_notification.png");
	@Nullable
	private PeriodicRunnerFactory.RunnersManager periodicRunnersManager;
	@Nullable
	private RealmsNotificationsScreen.NotificationRunnersFactory currentRunnersFactory;
	private volatile int pendingInvitesCount;
	static boolean checkedMcoAvailability;
	private static boolean trialAvailable;
	static boolean validClient;
	private static boolean hasUnreadNews;
	private static boolean hasUnseenNotification;
	private final RealmsNotificationsScreen.NotificationRunnersFactory newsAndNotifications = new RealmsNotificationsScreen.NotificationRunnersFactory() {
		@Override
		public PeriodicRunnerFactory.RunnersManager createPeriodicRunnersManager(RealmsPeriodicCheckers checkers) {
			PeriodicRunnerFactory.RunnersManager runnersManager = checkers.runnerFactory.create();
			RealmsNotificationsScreen.this.addRunners(checkers, runnersManager);
			RealmsNotificationsScreen.this.addNotificationRunner(checkers, runnersManager);
			return runnersManager;
		}

		@Override
		public boolean isNews() {
			return true;
		}
	};
	private final RealmsNotificationsScreen.NotificationRunnersFactory notificationsOnly = new RealmsNotificationsScreen.NotificationRunnersFactory() {
		@Override
		public PeriodicRunnerFactory.RunnersManager createPeriodicRunnersManager(RealmsPeriodicCheckers checkers) {
			PeriodicRunnerFactory.RunnersManager runnersManager = checkers.runnerFactory.create();
			RealmsNotificationsScreen.this.addNotificationRunner(checkers, runnersManager);
			return runnersManager;
		}

		@Override
		public boolean isNews() {
			return false;
		}
	};

	public RealmsNotificationsScreen() {
		super(NarratorManager.EMPTY);
	}

	@Override
	public void init() {
		this.checkIfMcoEnabled();
		if (this.periodicRunnersManager != null) {
			this.periodicRunnersManager.forceRunListeners();
		}
	}

	@Override
	public void onDisplayed() {
		super.onDisplayed();
		this.client.getRealmsPeriodicCheckers().notifications.reset();
	}

	@Nullable
	private RealmsNotificationsScreen.NotificationRunnersFactory getRunnersFactory() {
		boolean bl = this.isTitleScreen() && validClient;
		if (!bl) {
			return null;
		} else {
			return this.shouldShowRealmsNews() ? this.newsAndNotifications : this.notificationsOnly;
		}
	}

	@Override
	public void tick() {
		RealmsNotificationsScreen.NotificationRunnersFactory notificationRunnersFactory = this.getRunnersFactory();
		if (!Objects.equals(this.currentRunnersFactory, notificationRunnersFactory)) {
			this.currentRunnersFactory = notificationRunnersFactory;
			if (this.currentRunnersFactory != null) {
				this.periodicRunnersManager = this.currentRunnersFactory.createPeriodicRunnersManager(this.client.getRealmsPeriodicCheckers());
			} else {
				this.periodicRunnersManager = null;
			}
		}

		if (this.periodicRunnersManager != null) {
			this.periodicRunnersManager.runAll();
		}
	}

	private boolean shouldShowRealmsNews() {
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
					RealmsClient realmsClient = RealmsClient.create();

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
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (validClient) {
			this.drawIcons(context);
		}

		super.render(context, mouseX, mouseY, delta);
	}

	private void drawIcons(DrawContext context) {
		int i = this.pendingInvitesCount;
		int j = 24;
		int k = this.height / 4 + 48;
		int l = this.width / 2 + 80;
		int m = k + 48 + 2;
		int n = 0;
		if (hasUnseenNotification) {
			context.drawTexture(UNSEEN_NOTIFICATION, l - n + 5, m + 3, 0.0F, 0.0F, 10, 10, 10, 10);
			n += 14;
		}

		if (this.currentRunnersFactory != null && this.currentRunnersFactory.isNews()) {
			if (hasUnreadNews) {
				context.getMatrices().push();
				context.getMatrices().scale(0.4F, 0.4F, 0.4F);
				context.drawTexture(NEWS_NOTIFICATION, (int)((double)(l + 2 - n) * 2.5), (int)((double)m * 2.5), 0.0F, 0.0F, 40, 40, 40, 40);
				context.getMatrices().pop();
				n += 14;
			}

			if (i != 0) {
				context.drawTexture(INVITE_ICON, l - n, m, 0.0F, 0.0F, 18, 15, 18, 30);
				n += 16;
			}

			if (trialAvailable) {
				int o = 0;
				if ((Util.getMeasuringTimeMs() / 800L & 1L) == 1L) {
					o = 8;
				}

				context.drawTexture(TRIAL_ICON, l + 4 - n, m + 4, 0.0F, (float)o, 8, 8, 8, 16);
			}
		}
	}

	void addRunners(RealmsPeriodicCheckers checkers, PeriodicRunnerFactory.RunnersManager manager) {
		manager.add(checkers.pendingInvitesCount, pendingInvitesCount -> this.pendingInvitesCount = pendingInvitesCount);
		manager.add(checkers.trialAvailability, trialAvailable -> RealmsNotificationsScreen.trialAvailable = trialAvailable);
		manager.add(checkers.news, news -> {
			checkers.newsUpdater.updateNews(news);
			hasUnreadNews = checkers.newsUpdater.hasUnreadNews();
		});
	}

	void addNotificationRunner(RealmsPeriodicCheckers checkers, PeriodicRunnerFactory.RunnersManager manager) {
		manager.add(checkers.notifications, notifications -> {
			hasUnseenNotification = false;

			for (RealmsNotification realmsNotification : notifications) {
				if (!realmsNotification.isSeen()) {
					hasUnseenNotification = true;
					break;
				}
			}
		});
	}

	@Environment(EnvType.CLIENT)
	interface NotificationRunnersFactory {
		PeriodicRunnerFactory.RunnersManager createPeriodicRunnersManager(RealmsPeriodicCheckers checkers);

		boolean isNews();
	}
}
