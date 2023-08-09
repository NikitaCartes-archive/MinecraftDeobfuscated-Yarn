package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.IconWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.realms.Ping;
import net.minecraft.client.realms.RealmsAvailability;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.realms.dto.PingResult;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerPlayerList;
import net.minecraft.client.realms.dto.RegionPingResult;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsLoadingWidget;
import net.minecraft.client.realms.task.RealmsGetServerDetailsTask;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.realms.util.RealmsPersistence;
import net.minecraft.client.realms.util.RealmsServerFilterer;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Urls;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsMainScreen extends RealmsScreen {
	static final Identifier INFO_ICON_TEXTURE = new Identifier("icon/info");
	static final Identifier NEW_REALM_ICON_TEXTURE = new Identifier("icon/new_realm");
	static final Identifier EXPIRED_STATUS_TEXTURE = new Identifier("realm_status/expired");
	static final Identifier EXPIRES_SOON_STATUS_TEXTURE = new Identifier("realm_status/expires_soon");
	static final Identifier OPEN_STATUS_TEXTURE = new Identifier("realm_status/open");
	static final Identifier CLOSED_STATUS_TEXTURE = new Identifier("realm_status/closed");
	private static final Identifier INVITE_ICON_TEXTURE = new Identifier("icon/invite");
	private static final Identifier NEWS_ICON_TEXTURE = new Identifier("icon/news");
	static final Logger LOGGER = LogUtils.getLogger();
	private static final Identifier REALMS_TITLE_TEXTURE = new Identifier("textures/gui/title/realms.png");
	private static final Identifier NO_REALMS_TEXTURE = new Identifier("textures/gui/realms/no_realms.png");
	private static final Text MENU_TEXT = Text.translatable("menu.online");
	private static final Text LOADING_TEXT = Text.translatable("mco.selectServer.loading");
	static final Text UNINITIALIZED_TEXT = Text.translatable("mco.selectServer.uninitialized");
	static final Text EXPIRED_LIST_TEXT = Text.translatable("mco.selectServer.expiredList");
	private static final Text EXPIRED_RENEW_TEXT = Text.translatable("mco.selectServer.expiredRenew");
	static final Text EXPIRED_TRIAL_TEXT = Text.translatable("mco.selectServer.expiredTrial");
	static final Text MINIGAME_TEXT = Text.translatable("mco.selectServer.minigame").append(ScreenTexts.SPACE);
	private static final Text PLAY_TEXT = Text.translatable("mco.selectServer.play");
	private static final Text LEAVE_TEXT = Text.translatable("mco.selectServer.leave");
	private static final Text CONFIGURE_TEXT = Text.translatable("mco.selectServer.configure");
	static final Text EXPIRED_TEXT = Text.translatable("mco.selectServer.expired");
	static final Text EXPIRES_SOON_TEXT = Text.translatable("mco.selectServer.expires.soon");
	static final Text EXPIRES_IN_A_DAY_TEXT = Text.translatable("mco.selectServer.expires.day");
	static final Text OPEN_TEXT = Text.translatable("mco.selectServer.open");
	static final Text CLOSED_TEXT = Text.translatable("mco.selectServer.closed");
	static final Text UNINITIALIZED_BUTTON_NARRATION = Text.translatable("gui.narrate.button", UNINITIALIZED_TEXT);
	private static final Text NO_REALMS_TEXT = Text.translatable("mco.selectServer.noRealms");
	private static final Tooltip NO_PENDING_TOOLTIP = Tooltip.of(Text.translatable("mco.invites.nopending"));
	private static final Tooltip PENDING_TOOLTIP = Tooltip.of(Text.translatable("mco.invites.pending"));
	private static final int field_42862 = 100;
	private static final int field_45209 = 3;
	private static final int field_45210 = 4;
	private static final int field_45211 = 308;
	private static final int field_44509 = 128;
	private static final int field_44510 = 34;
	private static final int field_44511 = 128;
	private static final int field_44512 = 64;
	private static final int field_44513 = 5;
	private static final int field_44514 = 44;
	private static final int field_45212 = 10;
	private final CompletableFuture<RealmsAvailability.Info> availabilityInfo = RealmsAvailability.check();
	@Nullable
	private PeriodicRunnerFactory.RunnersManager periodicRunnersManager;
	private final Set<UUID> seenNotifications = new HashSet();
	private static boolean regionsPinged;
	private final RateLimiter rateLimiter;
	private final Screen parent;
	private ButtonWidget playButton;
	private ButtonWidget backButton;
	private ButtonWidget renewButton;
	private ButtonWidget configureButton;
	private ButtonWidget leaveButton;
	private RealmsMainScreen.RealmSelectionList realmSelectionList;
	private boolean hasFetchedServers;
	private RealmsServerFilterer serverFilterer;
	private volatile int pendingInvitesCount;
	int animTick;
	private volatile boolean trialAvailable;
	private volatile boolean hasUnreadNews;
	@Nullable
	private volatile String newsLink;
	long lastPlayButtonClickTime;
	private ReentrantLock connectLock = new ReentrantLock();
	private final List<RealmsNotification> notifications = new ArrayList();
	private ButtonWidget purchaseButton;
	private RealmsMainScreen.NotificationButtonWidget inviteButton;
	private RealmsMainScreen.NotificationButtonWidget newsButton;
	private RealmsMainScreen.LoadStatus loadStatus;
	@Nullable
	private ThreePartsLayoutWidget layout;

	public RealmsMainScreen(Screen parent) {
		super(MENU_TEXT);
		this.parent = parent;
		this.rateLimiter = RateLimiter.create(0.016666668F);
	}

	@Override
	public void init() {
		this.connectLock = new ReentrantLock();
		this.serverFilterer = new RealmsServerFilterer(this.client);
		this.realmSelectionList = this.addDrawableChild(new RealmsMainScreen.RealmSelectionList());
		Text text = Text.translatable("mco.invites.title");
		this.inviteButton = new RealmsMainScreen.NotificationButtonWidget(
			text, INVITE_ICON_TEXTURE, button -> this.client.setScreen(new RealmsPendingInvitesScreen(this, text))
		);
		Text text2 = Text.translatable("mco.news");
		this.newsButton = new RealmsMainScreen.NotificationButtonWidget(text2, NEWS_ICON_TEXTURE, button -> {
			if (this.newsLink != null) {
				ConfirmLinkScreen.open(this.newsLink, this, true);
				if (this.hasUnreadNews) {
					RealmsPersistence.RealmsPersistenceData realmsPersistenceData = RealmsPersistence.readFile();
					realmsPersistenceData.hasUnreadNews = false;
					this.hasUnreadNews = false;
					RealmsPersistence.writeFile(realmsPersistenceData);
					this.refreshButtons();
				}
			}
		});
		this.newsButton.setTooltip(Tooltip.of(text2));
		this.playButton = ButtonWidget.builder(PLAY_TEXT, button -> this.play(this.findServer(), this)).width(100).build();
		this.configureButton = ButtonWidget.builder(CONFIGURE_TEXT, button -> this.configureClicked(this.findServer())).width(100).build();
		this.renewButton = ButtonWidget.builder(EXPIRED_RENEW_TEXT, button -> this.onRenew(this.findServer())).width(100).build();
		this.leaveButton = ButtonWidget.builder(LEAVE_TEXT, button -> this.leaveClicked(this.findServer())).width(100).build();
		this.purchaseButton = ButtonWidget.builder(Text.translatable("mco.selectServer.purchase"), button -> this.showBuyRealmsScreen()).size(100, 20).build();
		this.backButton = ButtonWidget.builder(ScreenTexts.BACK, button -> this.client.setScreen(this.parent)).width(100).build();
		this.onLoadStatusChange(RealmsMainScreen.LoadStatus.LOADING);
		this.refreshButtons();
		this.availabilityInfo.thenAcceptAsync(availabilityInfo -> {
			Screen screen = availabilityInfo.createScreen(this.parent);
			if (screen == null) {
				this.periodicRunnersManager = this.createPeriodicRunnersManager(this.client.getRealmsPeriodicCheckers());
			} else {
				this.client.setScreen(screen);
			}
		}, this.executor);
	}

	@Override
	protected void initTabNavigation() {
		if (this.layout != null) {
			this.realmSelectionList.updateSize(this.width, this.height, this.layout.getHeaderHeight(), this.height - this.layout.getFooterHeight());
			this.layout.refreshPositions();
		}
	}

	private void onLoadStatusChange(RealmsMainScreen.LoadStatus loadStatus) {
		if (this.loadStatus != loadStatus) {
			if (this.layout != null) {
				this.layout.forEachChild(child -> this.remove(child));
			}

			this.layout = this.makeLayoutFor(loadStatus);
			this.loadStatus = loadStatus;
			this.layout.forEachChild(child -> {
				ClickableWidget var10000 = this.addDrawableChild(child);
			});
			this.initTabNavigation();
		}
	}

	private ThreePartsLayoutWidget makeLayoutFor(RealmsMainScreen.LoadStatus loadStatus) {
		ThreePartsLayoutWidget threePartsLayoutWidget = new ThreePartsLayoutWidget(this);
		threePartsLayoutWidget.setHeaderHeight(44);
		threePartsLayoutWidget.addHeader(this.makeHeader());
		LayoutWidget layoutWidget = this.makeInnerLayout(loadStatus);
		layoutWidget.refreshPositions();
		threePartsLayoutWidget.setFooterHeight(layoutWidget.getHeight() + 20);
		threePartsLayoutWidget.addFooter(layoutWidget);
		switch (loadStatus) {
			case LOADING:
				threePartsLayoutWidget.addBody(new RealmsLoadingWidget(this.textRenderer, LOADING_TEXT));
				break;
			case NO_REALMS:
				threePartsLayoutWidget.addBody(this.makeNoRealmsLayout());
		}

		return threePartsLayoutWidget;
	}

	private LayoutWidget makeHeader() {
		int i = 90;
		DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(4);
		directionalLayoutWidget.getMainPositioner().alignVerticalCenter();
		directionalLayoutWidget.add(this.inviteButton);
		directionalLayoutWidget.add(this.newsButton);
		DirectionalLayoutWidget directionalLayoutWidget2 = DirectionalLayoutWidget.horizontal();
		directionalLayoutWidget2.getMainPositioner().alignVerticalCenter();
		directionalLayoutWidget2.add(EmptyWidget.ofWidth(90));
		directionalLayoutWidget2.add(IconWidget.create(128, 34, REALMS_TITLE_TEXTURE, 128, 64), Positioner::alignHorizontalCenter);
		directionalLayoutWidget2.add(new SimplePositioningWidget(90, 44)).add(directionalLayoutWidget, Positioner::alignRight);
		return directionalLayoutWidget2;
	}

	private LayoutWidget makeInnerLayout(RealmsMainScreen.LoadStatus loadStatus) {
		GridWidget gridWidget = new GridWidget().setSpacing(4);
		GridWidget.Adder adder = gridWidget.createAdder(3);
		if (loadStatus == RealmsMainScreen.LoadStatus.LIST) {
			adder.add(this.playButton);
			adder.add(this.configureButton);
			adder.add(this.renewButton);
			adder.add(this.leaveButton);
		}

		adder.add(this.purchaseButton);
		adder.add(this.backButton);
		return gridWidget;
	}

	private DirectionalLayoutWidget makeNoRealmsLayout() {
		DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.vertical().spacing(10);
		directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
		directionalLayoutWidget.add(IconWidget.create(130, 64, NO_REALMS_TEXTURE, 130, 64));
		NarratedMultilineTextWidget narratedMultilineTextWidget = new NarratedMultilineTextWidget(308, NO_REALMS_TEXT, this.textRenderer, false);
		directionalLayoutWidget.add(narratedMultilineTextWidget);
		this.setFocused(narratedMultilineTextWidget);
		return directionalLayoutWidget;
	}

	void refreshButtons() {
		RealmsServer realmsServer = this.findServer();
		this.purchaseButton.active = this.hasFetchedServers;
		this.playButton.active = this.shouldPlayButtonBeActive(realmsServer);
		this.renewButton.active = this.shouldRenewButtonBeActive(realmsServer);
		this.leaveButton.active = this.shouldLeaveButtonBeActive(realmsServer);
		this.configureButton.active = this.shouldConfigureButtonBeActive(realmsServer);
		this.inviteButton.setNotificationCount(this.pendingInvitesCount);
		this.inviteButton.setTooltip(this.pendingInvitesCount == 0 ? NO_PENDING_TOOLTIP : PENDING_TOOLTIP);
		this.newsButton.setNotificationCount(this.hasUnreadNews ? Integer.MAX_VALUE : 0);
	}

	boolean shouldPlayButtonBeActive(@Nullable RealmsServer server) {
		return server != null && !server.expired && server.state == RealmsServer.State.OPEN;
	}

	private boolean shouldRenewButtonBeActive(@Nullable RealmsServer server) {
		return server != null && server.expired && this.isSelfOwnedServer(server);
	}

	private boolean shouldConfigureButtonBeActive(@Nullable RealmsServer server) {
		return server != null && this.isSelfOwnedServer(server);
	}

	private boolean shouldLeaveButtonBeActive(@Nullable RealmsServer server) {
		return server != null && !this.isSelfOwnedServer(server);
	}

	@Override
	public void tick() {
		super.tick();
		this.animTick++;
		if (this.periodicRunnersManager != null) {
			this.periodicRunnersManager.runAll();
		}
	}

	private PeriodicRunnerFactory.RunnersManager createPeriodicRunnersManager(RealmsPeriodicCheckers periodicCheckers) {
		PeriodicRunnerFactory.RunnersManager runnersManager = periodicCheckers.runnerFactory.create();
		runnersManager.add(periodicCheckers.serverList, servers -> {
			this.serverFilterer.filterAndSort(servers);
			boolean bl = false;

			for (RealmsServer realmsServer : this.serverFilterer) {
				if (this.isOwnedNotExpired(realmsServer)) {
					bl = true;
				}
			}

			this.hasFetchedServers = true;
			this.onLoadStatusChange(this.serverFilterer.isEmpty() ? RealmsMainScreen.LoadStatus.NO_REALMS : RealmsMainScreen.LoadStatus.LIST);
			this.refresh();
			if (!regionsPinged && bl) {
				regionsPinged = true;
				this.pingRegions();
			}
		});
		request(RealmsClient::listNotifications, notifications -> {
			this.notifications.clear();
			this.notifications.addAll(notifications);
			this.refresh();
		});
		runnersManager.add(periodicCheckers.pendingInvitesCount, pendingInvitesCount -> {
			this.pendingInvitesCount = pendingInvitesCount;
			this.refreshButtons();
			if (this.pendingInvitesCount > 0 && this.rateLimiter.tryAcquire(1)) {
				this.client.getNarratorManager().narrate(Text.translatable("mco.configure.world.invite.narration", this.pendingInvitesCount));
			}
		});
		runnersManager.add(periodicCheckers.trialAvailability, trialAvailable -> this.trialAvailable = trialAvailable);
		runnersManager.add(periodicCheckers.liveStats, liveStats -> {
			for (RealmsServerPlayerList realmsServerPlayerList : liveStats.servers) {
				for (RealmsServer realmsServer : this.serverFilterer) {
					if (realmsServer.id == realmsServerPlayerList.serverId) {
						realmsServer.updateServerPing(realmsServerPlayerList);
						break;
					}
				}
			}
		});
		runnersManager.add(periodicCheckers.news, news -> {
			periodicCheckers.newsUpdater.updateNews(news);
			this.hasUnreadNews = periodicCheckers.newsUpdater.hasUnreadNews();
			this.newsLink = periodicCheckers.newsUpdater.getNewsLink();
			this.refreshButtons();
		});
		return runnersManager;
	}

	private static <T> void request(RealmsMainScreen.Request<T> request, Consumer<T> resultConsumer) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		CompletableFuture.supplyAsync(() -> {
			try {
				return request.request(RealmsClient.createRealmsClient(minecraftClient));
			} catch (RealmsServiceException var3) {
				throw new RuntimeException(var3);
			}
		}).thenAcceptAsync(resultConsumer, minecraftClient).exceptionally(throwable -> {
			LOGGER.error("Failed to execute call to Realms Service", throwable);
			return null;
		});
	}

	private void refresh() {
		RealmsServer realmsServer = this.findServer();
		this.realmSelectionList.clear();
		List<UUID> list = new ArrayList();

		for (RealmsNotification realmsNotification : this.notifications) {
			this.addNotificationEntry(this.realmSelectionList, realmsNotification);
			if (!realmsNotification.isSeen() && !this.seenNotifications.contains(realmsNotification.getUuid())) {
				list.add(realmsNotification.getUuid());
			}
		}

		if (!list.isEmpty()) {
			request(client -> {
				client.markNotificationsAsSeen(list);
				return null;
			}, void_ -> this.seenNotifications.addAll(list));
		}

		for (RealmsServer realmsServer2 : this.serverFilterer) {
			RealmsMainScreen.RealmSelectionListEntry realmSelectionListEntry = new RealmsMainScreen.RealmSelectionListEntry(realmsServer2);
			this.realmSelectionList.addEntry(realmSelectionListEntry);
			if (realmsServer != null && realmsServer.id == realmsServer2.id) {
				this.realmSelectionList.setSelected((RealmsMainScreen.Entry)realmSelectionListEntry);
			}
		}

		this.refreshButtons();
	}

	private void addNotificationEntry(RealmsMainScreen.RealmSelectionList selectionList, RealmsNotification notification) {
		if (notification instanceof RealmsNotification.VisitUrl visitUrl) {
			selectionList.addEntry(new RealmsMainScreen.VisitUrlNotification(visitUrl.getDefaultMessage(), visitUrl));
			selectionList.addEntry(new RealmsMainScreen.VisitButtonEntry(visitUrl.createButton(this)));
		}
	}

	private void pingRegions() {
		new Thread(() -> {
			List<RegionPingResult> list = Ping.pingAllRegions();
			RealmsClient realmsClient = RealmsClient.create();
			PingResult pingResult = new PingResult();
			pingResult.pingResults = list;
			pingResult.worldIds = this.getOwnedNonExpiredWorldIds();

			try {
				realmsClient.sendPingResults(pingResult);
			} catch (Throwable var5) {
				LOGGER.warn("Could not send ping result to Realms: ", var5);
			}
		}).start();
	}

	private List<Long> getOwnedNonExpiredWorldIds() {
		List<Long> list = Lists.<Long>newArrayList();

		for (RealmsServer realmsServer : this.serverFilterer) {
			if (this.isOwnedNotExpired(realmsServer)) {
				list.add(realmsServer.id);
			}
		}

		return list;
	}

	private void onRenew(@Nullable RealmsServer realmsServer) {
		if (realmsServer != null) {
			String string = Urls.getExtendJavaRealmsUrl(realmsServer.remoteSubscriptionId, this.client.getSession().getUuidOrNull(), realmsServer.expiredTrial);
			this.client.keyboard.setClipboard(string);
			Util.getOperatingSystem().open(string);
		}
	}

	private void configureClicked(@Nullable RealmsServer serverData) {
		if (serverData != null && this.client.uuidEquals(serverData.ownerUUID)) {
			this.client.setScreen(new RealmsConfigureWorldScreen(this, serverData.id));
		}
	}

	private void leaveClicked(@Nullable RealmsServer selectedServer) {
		if (selectedServer != null && !this.client.uuidEquals(selectedServer.ownerUUID)) {
			Text text = Text.translatable("mco.configure.world.leave.question.line1");
			Text text2 = Text.translatable("mco.configure.world.leave.question.line2");
			this.client
				.setScreen(
					new RealmsLongConfirmationScreen(confirmed -> this.leaveServer(confirmed, selectedServer), RealmsLongConfirmationScreen.Type.INFO, text, text2, true)
				);
		}
	}

	@Nullable
	private RealmsServer findServer() {
		if (this.realmSelectionList == null) {
			return null;
		} else {
			RealmsMainScreen.Entry entry = this.realmSelectionList.getSelectedOrNull();
			return entry != null ? entry.getRealmsServer() : null;
		}
	}

	private void leaveServer(boolean confirmed, RealmsServer realmsServer) {
		if (confirmed) {
			(new Thread("Realms-leave-server") {
				public void run() {
					try {
						RealmsClient realmsClient = RealmsClient.create();
						realmsClient.uninviteMyselfFrom(realmsServer.id);
						RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.removeServer(realmsServer));
					} catch (RealmsServiceException var2) {
						RealmsMainScreen.LOGGER.error("Couldn't configure world", (Throwable)var2);
						RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(new RealmsGenericErrorScreen(var2, RealmsMainScreen.this)));
					}
				}
			}).start();
		}

		this.client.setScreen(this);
	}

	void removeServer(RealmsServer serverData) {
		this.serverFilterer.remove(serverData);
		this.realmSelectionList.children().removeIf(child -> {
			RealmsServer realmsServer2 = child.getRealmsServer();
			return realmsServer2 != null && realmsServer2.id == serverData.id;
		});
		this.realmSelectionList.setSelected(null);
		this.refreshButtons();
	}

	void dismissNotification(UUID notification) {
		request(client -> {
			client.dismissNotifications(List.of(notification));
			return null;
		}, void_ -> {
			this.notifications.removeIf(notificationId -> notificationId.isDismissable() && notification.equals(notificationId.getUuid()));
			this.refresh();
		});
	}

	public void removeSelection() {
		if (this.realmSelectionList != null) {
			this.realmSelectionList.setSelected(null);
		}
	}

	@Override
	public Text getNarratedTitle() {
		return (Text)(this.loadStatus == RealmsMainScreen.LoadStatus.LOADING
			? ScreenTexts.joinSentences(super.getNarratedTitle(), LOADING_TEXT)
			: super.getNarratedTitle());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		if (this.trialAvailable && this.purchaseButton.active) {
			BuyRealmsScreen.drawTrialAvailableTexture(context, this.purchaseButton);
		}

		switch (RealmsClient.ENVIRONMENT) {
			case STAGE:
				this.drawEnvironmentText(context, "STAGE!", -256);
				break;
			case LOCAL:
				this.drawEnvironmentText(context, "LOCAL!", 8388479);
		}
	}

	private void showBuyRealmsScreen() {
		this.client.setScreen(new BuyRealmsScreen(this, this.trialAvailable));
	}

	public void play(@Nullable RealmsServer serverData, Screen parent) {
		if (serverData != null) {
			try {
				if (!this.connectLock.tryLock(1L, TimeUnit.SECONDS)) {
					return;
				}

				if (this.connectLock.getHoldCount() > 1) {
					return;
				}
			} catch (InterruptedException var4) {
				return;
			}

			this.client.setScreen(new RealmsLongRunningMcoTaskScreen(parent, new RealmsGetServerDetailsTask(this, parent, serverData, this.connectLock)));
		}
	}

	boolean isSelfOwnedServer(RealmsServer server) {
		return this.client.uuidEquals(server.ownerUUID);
	}

	private boolean isOwnedNotExpired(RealmsServer serverData) {
		return this.isSelfOwnedServer(serverData) && !serverData.expired;
	}

	private void drawEnvironmentText(DrawContext context, String text, int color) {
		context.getMatrices().push();
		context.getMatrices().translate((float)(this.width / 2 - 25), 20.0F, 0.0F);
		context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-20.0F));
		context.getMatrices().scale(1.5F, 1.5F, 1.5F);
		context.drawText(this.textRenderer, text, 0, 0, color, false);
		context.getMatrices().pop();
	}

	public RealmsMainScreen newScreen() {
		RealmsMainScreen realmsMainScreen = new RealmsMainScreen(this.parent);
		realmsMainScreen.init(this.client, this.width, this.height);
		return realmsMainScreen;
	}

	@Environment(EnvType.CLIENT)
	static class CrossButton extends TexturedButtonWidget {
		private static final ButtonTextures TEXTURES = new ButtonTextures(new Identifier("widget/cross_button"), new Identifier("widget/cross_button_highlighted"));

		protected CrossButton(ButtonWidget.PressAction onPress, Text tooltip) {
			super(0, 0, 14, 14, TEXTURES, onPress);
			this.setTooltip(Tooltip.of(tooltip));
		}
	}

	@Environment(EnvType.CLIENT)
	abstract class Entry extends AlwaysSelectedEntryListWidget.Entry<RealmsMainScreen.Entry> {
		@Nullable
		public RealmsServer getRealmsServer() {
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	static enum LoadStatus {
		LOADING,
		NO_REALMS,
		LIST;
	}

	@Environment(EnvType.CLIENT)
	static class NotificationButtonWidget extends TextIconButtonWidget.IconOnly {
		private static final Identifier[] TEXTURES = new Identifier[]{
			new Identifier("notification/1"),
			new Identifier("notification/2"),
			new Identifier("notification/3"),
			new Identifier("notification/4"),
			new Identifier("notification/5"),
			new Identifier("notification/more")
		};
		private static final int field_45228 = Integer.MAX_VALUE;
		private static final int SIZE = 20;
		private static final int TEXTURE_SIZE = 14;
		private int notificationCount;

		public NotificationButtonWidget(Text message, Identifier texture, ButtonWidget.PressAction onPress) {
			super(20, 20, message, 14, 14, texture, onPress);
		}

		public void setNotificationCount(int notificationCount) {
			this.notificationCount = notificationCount;
		}

		@Override
		public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
			super.renderButton(context, mouseX, mouseY, delta);
			if (this.active && this.notificationCount != 0) {
				this.render(context);
			}
		}

		private void render(DrawContext context) {
			context.drawGuiTexture(TEXTURES[Math.min(this.notificationCount, 6) - 1], this.getX() + this.getWidth() - 5, this.getY() - 3, 8, 8);
		}
	}

	@Environment(EnvType.CLIENT)
	class RealmSelectionList extends RealmsObjectSelectionList<RealmsMainScreen.Entry> {
		public RealmSelectionList() {
			super(RealmsMainScreen.this.width, RealmsMainScreen.this.height, 0, RealmsMainScreen.this.height, 36);
		}

		public void setSelected(@Nullable RealmsMainScreen.Entry entry) {
			super.setSelected(entry);
			RealmsMainScreen.this.refreshButtons();
		}

		@Override
		public int getMaxPosition() {
			return this.getEntryCount() * 36;
		}

		@Override
		public int getRowWidth() {
			return 300;
		}
	}

	@Environment(EnvType.CLIENT)
	class RealmSelectionListEntry extends RealmsMainScreen.Entry {
		private static final int field_32054 = 36;
		private final RealmsServer server;

		public RealmSelectionListEntry(RealmsServer server) {
			this.server = server;
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.render(this.server, context, x, y, mouseX, mouseY);
		}

		private void play() {
			RealmsMainScreen.this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			RealmsMainScreen.this.play(this.server, RealmsMainScreen.this);
		}

		private void createRealm() {
			RealmsMainScreen.this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			RealmsCreateRealmScreen realmsCreateRealmScreen = new RealmsCreateRealmScreen(this.server, RealmsMainScreen.this);
			RealmsMainScreen.this.client.setScreen(realmsCreateRealmScreen);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (this.server.state == RealmsServer.State.UNINITIALIZED) {
				this.createRealm();
			} else if (RealmsMainScreen.this.shouldPlayButtonBeActive(this.server)) {
				if (Util.getMeasuringTimeMs() - RealmsMainScreen.this.lastPlayButtonClickTime < 250L && this.isFocused()) {
					this.play();
				}

				RealmsMainScreen.this.lastPlayButtonClickTime = Util.getMeasuringTimeMs();
			}

			return true;
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (KeyCodes.isToggle(keyCode)) {
				if (this.server.state == RealmsServer.State.UNINITIALIZED) {
					this.createRealm();
					return true;
				}

				if (RealmsMainScreen.this.shouldPlayButtonBeActive(this.server)) {
					this.play();
					return true;
				}
			}

			return super.keyPressed(keyCode, scanCode, modifiers);
		}

		private void render(RealmsServer server, DrawContext context, int x, int y, int mouseX, int mouseY) {
			this.renderRealmsServerItem(server, context, x + 36, y, mouseX, mouseY);
		}

		private void renderRealmsServerItem(RealmsServer server, DrawContext context, int x, int y, int mouseX, int mouseY) {
			if (server.state == RealmsServer.State.UNINITIALIZED) {
				context.drawGuiTexture(RealmsMainScreen.NEW_REALM_ICON_TEXTURE, x + 10, y + 6, 40, 20);
				float f = 0.5F + (1.0F + MathHelper.sin((float)RealmsMainScreen.this.animTick * 0.25F)) * 0.25F;
				int i = 0xFF000000 | (int)(127.0F * f) << 16 | (int)(255.0F * f) << 8 | (int)(127.0F * f);
				context.drawCenteredTextWithShadow(RealmsMainScreen.this.textRenderer, RealmsMainScreen.UNINITIALIZED_TEXT, x + 10 + 40 + 75, y + 12, i);
			} else {
				int j = 225;
				int i = 2;
				this.drawServerState(server, context, x, y, mouseX, mouseY, 225, 2);
				if (!"0".equals(server.serverPing.nrOfPlayers)) {
					String string = Formatting.GRAY + server.serverPing.nrOfPlayers;
					context.drawText(RealmsMainScreen.this.textRenderer, string, x + 207 - RealmsMainScreen.this.textRenderer.getWidth(string), y + 3, -8355712, false);
					if (mouseX >= x + 207 - RealmsMainScreen.this.textRenderer.getWidth(string)
						&& mouseX <= x + 207
						&& mouseY >= y + 1
						&& mouseY <= y + 10
						&& mouseY < RealmsMainScreen.this.height - 40
						&& mouseY > 32) {
						RealmsMainScreen.this.setTooltip(Text.literal(server.serverPing.playerList));
					}
				}

				if (RealmsMainScreen.this.isSelfOwnedServer(server) && server.expired) {
					Text text = server.expiredTrial ? RealmsMainScreen.EXPIRED_TRIAL_TEXT : RealmsMainScreen.EXPIRED_LIST_TEXT;
					int k = y + 11 + 5;
					context.drawText(RealmsMainScreen.this.textRenderer, text, x + 2, k + 1, 15553363, false);
				} else {
					if (server.worldType == RealmsServer.WorldType.MINIGAME) {
						int l = 13413468;
						int k = RealmsMainScreen.this.textRenderer.getWidth(RealmsMainScreen.MINIGAME_TEXT);
						context.drawText(RealmsMainScreen.this.textRenderer, RealmsMainScreen.MINIGAME_TEXT, x + 2, y + 12, 13413468, false);
						context.drawText(RealmsMainScreen.this.textRenderer, server.getMinigameName(), x + 2 + k, y + 12, 7105644, false);
					} else {
						context.drawText(RealmsMainScreen.this.textRenderer, server.getDescription(), x + 2, y + 12, 7105644, false);
					}

					if (!RealmsMainScreen.this.isSelfOwnedServer(server)) {
						context.drawText(RealmsMainScreen.this.textRenderer, server.owner, x + 2, y + 12 + 11, 5000268, false);
					}
				}

				context.drawText(RealmsMainScreen.this.textRenderer, server.getName(), x + 2, y + 1, -1, false);
				RealmsUtil.drawPlayerHead(context, x - 36, y, 32, server.ownerUUID);
			}
		}

		private void drawServerState(RealmsServer server, DrawContext context, int x, int y, int mouseX, int mouseY, int xOffset, int yOffset) {
			int i = x + xOffset + 22;
			if (server.expired) {
				this.drawServerState(context, i, y + yOffset, mouseX, mouseY, RealmsMainScreen.EXPIRED_STATUS_TEXTURE, () -> RealmsMainScreen.EXPIRED_TEXT);
			} else if (server.state == RealmsServer.State.CLOSED) {
				this.drawServerState(context, i, y + yOffset, mouseX, mouseY, RealmsMainScreen.CLOSED_STATUS_TEXTURE, () -> RealmsMainScreen.CLOSED_TEXT);
			} else if (RealmsMainScreen.this.isSelfOwnedServer(server) && server.daysLeft < 7) {
				this.drawServerState(context, i, y + yOffset, mouseX, mouseY, RealmsMainScreen.EXPIRES_SOON_STATUS_TEXTURE, () -> {
					if (server.daysLeft <= 0) {
						return RealmsMainScreen.EXPIRES_SOON_TEXT;
					} else {
						return (Text)(server.daysLeft == 1 ? RealmsMainScreen.EXPIRES_IN_A_DAY_TEXT : Text.translatable("mco.selectServer.expires.days", server.daysLeft));
					}
				});
			} else if (server.state == RealmsServer.State.OPEN) {
				this.drawServerState(context, i, y + yOffset, mouseX, mouseY, RealmsMainScreen.OPEN_STATUS_TEXTURE, () -> RealmsMainScreen.OPEN_TEXT);
			}
		}

		private void drawServerState(DrawContext context, int x, int y, int mouseX, int mouseY, Identifier texture, Supplier<Text> tooltipGetter) {
			context.drawGuiTexture(texture, x, y, 10, 28);
			if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27 && mouseY < RealmsMainScreen.this.height - 40 && mouseY > 32) {
				RealmsMainScreen.this.setTooltip((Text)tooltipGetter.get());
			}
		}

		@Override
		public Text getNarration() {
			return (Text)(this.server.state == RealmsServer.State.UNINITIALIZED
				? RealmsMainScreen.UNINITIALIZED_BUTTON_NARRATION
				: Text.translatable("narrator.select", this.server.name));
		}

		@Nullable
		@Override
		public RealmsServer getRealmsServer() {
			return this.server;
		}
	}

	@Environment(EnvType.CLIENT)
	interface Request<T> {
		T request(RealmsClient client) throws RealmsServiceException;
	}

	@Environment(EnvType.CLIENT)
	class VisitButtonEntry extends RealmsMainScreen.Entry {
		private final ButtonWidget button;
		private final int x = RealmsMainScreen.this.width / 2 - 75;

		public VisitButtonEntry(ButtonWidget button) {
			this.button = button;
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			this.button.mouseClicked(mouseX, mouseY, button);
			return true;
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			return this.button.keyPressed(keyCode, scanCode, modifiers) ? true : super.keyPressed(keyCode, scanCode, modifiers);
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.button.setPosition(this.x, y + 4);
			this.button.render(context, mouseX, mouseY, tickDelta);
		}

		@Override
		public Text getNarration() {
			return this.button.getMessage();
		}
	}

	@Environment(EnvType.CLIENT)
	class VisitUrlNotification extends RealmsMainScreen.Entry {
		private static final int field_43002 = 40;
		private static final int field_43003 = 36;
		private static final int field_43004 = -12303292;
		private final Text message;
		private final List<ClickableWidget> gridChildren = new ArrayList();
		@Nullable
		private final RealmsMainScreen.CrossButton dismissButton;
		private final MultilineTextWidget textWidget;
		private final GridWidget grid;
		private final SimplePositioningWidget textGrid;
		private int width = -1;

		public VisitUrlNotification(Text message, RealmsNotification notification) {
			this.message = message;
			this.grid = new GridWidget();
			int i = 7;
			this.grid.add(IconWidget.create(20, 20, RealmsMainScreen.INFO_ICON_TEXTURE), 0, 0, this.grid.copyPositioner().margin(7, 7, 0, 0));
			this.grid.add(EmptyWidget.ofWidth(40), 0, 0);
			this.textGrid = this.grid.add(new SimplePositioningWidget(0, 9 * 3), 0, 1, this.grid.copyPositioner().marginTop(7));
			this.textWidget = this.textGrid
				.add(
					new MultilineTextWidget(message, RealmsMainScreen.this.textRenderer).setCentered(true).setMaxRows(3),
					this.textGrid.copyPositioner().alignHorizontalCenter().alignTop()
				);
			this.grid.add(EmptyWidget.ofWidth(40), 0, 2);
			if (notification.isDismissable()) {
				this.dismissButton = this.grid
					.add(
						new RealmsMainScreen.CrossButton(
							button -> RealmsMainScreen.this.dismissNotification(notification.getUuid()), Text.translatable("mco.notification.dismiss")
						),
						0,
						2,
						this.grid.copyPositioner().alignRight().margin(0, 7, 7, 0)
					);
			} else {
				this.dismissButton = null;
			}

			this.grid.forEachChild(this.gridChildren::add);
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			return this.dismissButton != null && this.dismissButton.keyPressed(keyCode, scanCode, modifiers) ? true : super.keyPressed(keyCode, scanCode, modifiers);
		}

		private void setWidth(int width) {
			if (this.width != width) {
				this.updateWidth(width);
				this.width = width;
			}
		}

		private void updateWidth(int width) {
			int i = width - 80;
			this.textGrid.setMinWidth(i);
			this.textWidget.setMaxWidth(i);
			this.grid.refreshPositions();
		}

		@Override
		public void drawBorder(
			DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
		) {
			super.drawBorder(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
			context.drawBorder(x - 2, y - 2, entryWidth, 70, -12303292);
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.grid.setPosition(x, y);
			this.setWidth(entryWidth - 4);
			this.gridChildren.forEach(child -> child.render(context, mouseX, mouseY, tickDelta));
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (this.dismissButton != null) {
				this.dismissButton.mouseClicked(mouseX, mouseY, button);
			}

			return true;
		}

		@Override
		public Text getNarration() {
			return this.message;
		}
	}
}
