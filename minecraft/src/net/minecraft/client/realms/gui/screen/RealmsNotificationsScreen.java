package net.minecraft.client.realms.gui.screen;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.realms.RealmsAvailability;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RealmsNotificationsScreen extends RealmsScreen {
	private static final Identifier UNSEEN_NOTIFICATION_ICON_TEXTURE = Identifier.method_60656("icon/unseen_notification");
	private static final Identifier NEWS_ICON_TEXTURE = Identifier.method_60656("icon/news");
	private static final Identifier INVITE_ICON_TEXTURE = Identifier.method_60656("icon/invite");
	private static final Identifier TRIAL_AVAILABLE_ICON_TEXTURE = Identifier.method_60656("icon/trial_available");
	private final CompletableFuture<Boolean> validClient = RealmsAvailability.check().thenApply(info -> info.type() == RealmsAvailability.Type.SUCCESS);
	@Nullable
	private PeriodicRunnerFactory.RunnersManager periodicRunnersManager;
	@Nullable
	private RealmsNotificationsScreen.NotificationRunnersFactory currentRunnersFactory;
	private volatile int pendingInvitesCount;
	private static boolean trialAvailable;
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
		boolean bl = this.isTitleScreen() && (Boolean)this.validClient.getNow(false);
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

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		if ((Boolean)this.validClient.getNow(false)) {
			this.drawIcons(context);
		}
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
	}

	private void drawIcons(DrawContext context) {
		int i = this.pendingInvitesCount;
		int j = 24;
		int k = this.height / 4 + 48;
		int l = this.width / 2 + 100;
		int m = k + 48 + 2;
		int n = l - 3;
		if (hasUnseenNotification) {
			context.drawGuiTexture(UNSEEN_NOTIFICATION_ICON_TEXTURE, n - 12, m + 3, 10, 10);
			n -= 16;
		}

		if (this.currentRunnersFactory != null && this.currentRunnersFactory.isNews()) {
			if (hasUnreadNews) {
				context.drawGuiTexture(NEWS_ICON_TEXTURE, n - 14, m + 1, 14, 14);
				n -= 16;
			}

			if (i != 0) {
				context.drawGuiTexture(INVITE_ICON_TEXTURE, n - 14, m + 1, 14, 14);
				n -= 16;
			}

			if (trialAvailable) {
				context.drawGuiTexture(TRIAL_AVAILABLE_ICON_TEXTURE, n - 10, m + 4, 8, 8);
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
