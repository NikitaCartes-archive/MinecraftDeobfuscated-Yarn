package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.PopupScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipState;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
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
import net.minecraft.client.realms.dto.RegionPingResult;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsLoadingWidget;
import net.minecraft.client.realms.task.RealmsPrepareConnectionTask;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.realms.util.RealmsPersistence;
import net.minecraft.client.realms.util.RealmsServerFilterer;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Urls;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.apache.commons.lang3.StringUtils;
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
	private static final Text NO_PENDING_TOOLTIP = Text.translatable("mco.invites.nopending");
	private static final Text PENDING_TOOLTIP = Text.translatable("mco.invites.pending");
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
	private static final int field_45212 = 11;
	private static final int field_46670 = 40;
	private static final int field_46671 = 20;
	private static final int field_46215 = 216;
	private static final int field_46216 = 36;
	private static final boolean gameOnSnapshot = !SharedConstants.getGameVersion().isStable();
	private static boolean showingSnapshotRealms = gameOnSnapshot;
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
	RealmsMainScreen.RealmSelectionList realmSelectionList;
	private RealmsServerFilterer serverFilterer;
	private List<RealmsServer> availableSnapshotServers = List.of();
	private volatile boolean trialAvailable;
	@Nullable
	private volatile String newsLink;
	long lastPlayButtonClickTime;
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
		this.serverFilterer = new RealmsServerFilterer(this.client);
		this.realmSelectionList = new RealmsMainScreen.RealmSelectionList();
		Text text = Text.translatable("mco.invites.title");
		this.inviteButton = new RealmsMainScreen.NotificationButtonWidget(
			text, INVITE_ICON_TEXTURE, button -> this.client.setScreen(new RealmsPendingInvitesScreen(this, text))
		);
		Text text2 = Text.translatable("mco.news");
		this.newsButton = new RealmsMainScreen.NotificationButtonWidget(text2, NEWS_ICON_TEXTURE, button -> {
			String string = this.newsLink;
			if (string != null) {
				ConfirmLinkScreen.open(this, string);
				if (this.newsButton.getNotificationCount() != 0) {
					RealmsPersistence.RealmsPersistenceData realmsPersistenceData = RealmsPersistence.readFile();
					realmsPersistenceData.hasUnreadNews = false;
					RealmsPersistence.writeFile(realmsPersistenceData);
					this.newsButton.setNotificationCount(0);
				}
			}
		});
		this.newsButton.setTooltip(Tooltip.of(text2));
		this.playButton = ButtonWidget.builder(PLAY_TEXT, button -> play(this.findServer(), this)).width(100).build();
		this.configureButton = ButtonWidget.builder(CONFIGURE_TEXT, button -> this.configureClicked(this.findServer())).width(100).build();
		this.renewButton = ButtonWidget.builder(EXPIRED_RENEW_TEXT, button -> this.onRenew(this.findServer())).width(100).build();
		this.leaveButton = ButtonWidget.builder(LEAVE_TEXT, button -> this.leaveClicked(this.findServer())).width(100).build();
		this.purchaseButton = ButtonWidget.builder(Text.translatable("mco.selectServer.purchase"), button -> this.showBuyRealmsScreen()).size(100, 20).build();
		this.backButton = ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).width(100).build();
		if (RealmsClient.ENVIRONMENT == RealmsClient.Environment.STAGE) {
			this.addDrawableChild(
				CyclingButtonWidget.onOffBuilder(Text.literal("Snapshot"), Text.literal("Release")).build(5, 5, 100, 20, Text.literal("Realm"), (button, snapshot) -> {
					showingSnapshotRealms = snapshot;
					this.availableSnapshotServers = List.of();
					this.resetPeriodicCheckers();
				})
			);
		}

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

	public static boolean isSnapshotRealmsEligible() {
		return gameOnSnapshot && showingSnapshotRealms;
	}

	@Override
	protected void initTabNavigation() {
		if (this.layout != null) {
			this.realmSelectionList.setDimensions(this.width, this.height - this.layout.getFooterHeight() - this.layout.getHeaderHeight());
			this.layout.refreshPositions();
		}
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	private void updateLoadStatus() {
		if (this.serverFilterer.isEmpty() && this.availableSnapshotServers.isEmpty() && this.notifications.isEmpty()) {
			this.onLoadStatusChange(RealmsMainScreen.LoadStatus.NO_REALMS);
		} else {
			this.onLoadStatusChange(RealmsMainScreen.LoadStatus.LIST);
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
		threePartsLayoutWidget.setFooterHeight(layoutWidget.getHeight() + 22);
		threePartsLayoutWidget.addFooter(layoutWidget);
		switch (loadStatus) {
			case LOADING:
				threePartsLayoutWidget.addBody(new RealmsLoadingWidget(this.textRenderer, LOADING_TEXT));
				break;
			case NO_REALMS:
				threePartsLayoutWidget.addBody(this.makeNoRealmsLayout());
				break;
			case LIST:
				threePartsLayoutWidget.addBody(this.realmSelectionList);
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
		return directionalLayoutWidget;
	}

	void refreshButtons() {
		RealmsServer realmsServer = this.findServer();
		this.purchaseButton.active = this.loadStatus != RealmsMainScreen.LoadStatus.LOADING;
		this.playButton.active = realmsServer != null && this.shouldPlayButtonBeActive(realmsServer);
		this.renewButton.active = realmsServer != null && this.shouldRenewButtonBeActive(realmsServer);
		this.leaveButton.active = realmsServer != null && this.shouldLeaveButtonBeActive(realmsServer);
		this.configureButton.active = realmsServer != null && this.shouldConfigureButtonBeActive(realmsServer);
	}

	boolean shouldPlayButtonBeActive(RealmsServer server) {
		boolean bl = !server.expired && server.state == RealmsServer.State.OPEN;
		return bl && (server.isCompatible() || this.isSelfOwnedServer(server));
	}

	private boolean shouldRenewButtonBeActive(RealmsServer server) {
		return server.expired && this.isSelfOwnedServer(server);
	}

	private boolean shouldConfigureButtonBeActive(RealmsServer server) {
		return this.isSelfOwnedServer(server) && server.state != RealmsServer.State.UNINITIALIZED;
	}

	private boolean shouldLeaveButtonBeActive(RealmsServer server) {
		return !this.isSelfOwnedServer(server);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.periodicRunnersManager != null) {
			this.periodicRunnersManager.runAll();
		}
	}

	public static void resetPendingInvitesCount() {
		MinecraftClient.getInstance().getRealmsPeriodicCheckers().pendingInvitesCount.reset();
	}

	public static void resetServerList() {
		MinecraftClient.getInstance().getRealmsPeriodicCheckers().serverList.reset();
	}

	private void resetPeriodicCheckers() {
		for (PeriodicRunnerFactory.PeriodicRunner<?> periodicRunner : this.client.getRealmsPeriodicCheckers().getCheckers()) {
			periodicRunner.reset();
		}
	}

	private PeriodicRunnerFactory.RunnersManager createPeriodicRunnersManager(RealmsPeriodicCheckers periodicCheckers) {
		PeriodicRunnerFactory.RunnersManager runnersManager = periodicCheckers.runnerFactory.create();
		runnersManager.add(periodicCheckers.serverList, availableServers -> {
			this.serverFilterer.filterAndSort(availableServers.serverList());
			this.availableSnapshotServers = availableServers.availableSnapshotServers();
			this.refresh();
			boolean bl = false;

			for (RealmsServer realmsServer : this.serverFilterer) {
				if (this.isOwnedNotExpired(realmsServer)) {
					bl = true;
				}
			}

			if (!regionsPinged && bl) {
				regionsPinged = true;
				this.pingRegions();
			}
		});
		request(RealmsClient::listNotifications, notifications -> {
			this.notifications.clear();
			this.notifications.addAll(notifications);

			for (RealmsNotification realmsNotification : notifications) {
				if (realmsNotification instanceof RealmsNotification.InfoPopup infoPopup) {
					PopupScreen popupScreen = infoPopup.createScreen(this, this::dismissNotification);
					if (popupScreen != null) {
						this.client.setScreen(popupScreen);
						this.markAsSeen(List.of(realmsNotification));
						break;
					}
				}
			}

			if (!this.notifications.isEmpty() && this.loadStatus != RealmsMainScreen.LoadStatus.LOADING) {
				this.refresh();
			}
		});
		runnersManager.add(periodicCheckers.pendingInvitesCount, pendingInvitesCount -> {
			this.inviteButton.setNotificationCount(pendingInvitesCount);
			this.inviteButton.setTooltip(pendingInvitesCount == 0 ? Tooltip.of(NO_PENDING_TOOLTIP) : Tooltip.of(PENDING_TOOLTIP));
			if (pendingInvitesCount > 0 && this.rateLimiter.tryAcquire(1)) {
				this.client.getNarratorManager().narrate(Text.translatable("mco.configure.world.invite.narration", pendingInvitesCount));
			}
		});
		runnersManager.add(periodicCheckers.trialAvailability, trialAvailable -> this.trialAvailable = trialAvailable);
		runnersManager.add(periodicCheckers.news, news -> {
			periodicCheckers.newsUpdater.updateNews(news);
			this.newsLink = periodicCheckers.newsUpdater.getNewsLink();
			this.newsButton.setNotificationCount(periodicCheckers.newsUpdater.hasUnreadNews() ? Integer.MAX_VALUE : 0);
		});
		return runnersManager;
	}

	private void markAsSeen(Collection<RealmsNotification> notifications) {
		List<UUID> list = new ArrayList(notifications.size());

		for (RealmsNotification realmsNotification : notifications) {
			if (!realmsNotification.isSeen() && !this.seenNotifications.contains(realmsNotification.getUuid())) {
				list.add(realmsNotification.getUuid());
			}
		}

		if (!list.isEmpty()) {
			request(client -> {
				client.markNotificationsAsSeen(list);
				return null;
			}, result -> this.seenNotifications.addAll(list));
		}
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

		for (RealmsNotification realmsNotification : this.notifications) {
			if (this.addNotificationEntry(realmsNotification)) {
				this.markAsSeen(List.of(realmsNotification));
				break;
			}
		}

		for (RealmsServer realmsServer2 : this.availableSnapshotServers) {
			this.realmSelectionList.addEntry(new RealmsMainScreen.SnapshotEntry(realmsServer2));
		}

		for (RealmsServer realmsServer2 : this.serverFilterer) {
			RealmsMainScreen.Entry entry;
			if (isSnapshotRealmsEligible() && !realmsServer2.hasParentWorld()) {
				if (realmsServer2.state == RealmsServer.State.UNINITIALIZED) {
					continue;
				}

				entry = new RealmsMainScreen.ParentRealmSelectionListEntry(realmsServer2);
			} else {
				entry = new RealmsMainScreen.RealmSelectionListEntry(realmsServer2);
			}

			this.realmSelectionList.addEntry(entry);
			if (realmsServer != null && realmsServer.id == realmsServer2.id) {
				this.realmSelectionList.setSelected(entry);
			}
		}

		this.updateLoadStatus();
		this.refreshButtons();
	}

	private boolean addNotificationEntry(RealmsNotification notification) {
		if (!(notification instanceof RealmsNotification.VisitUrl visitUrl)) {
			return false;
		} else {
			Text text = visitUrl.getDefaultMessage();
			int i = this.textRenderer.getWrappedLinesHeight(text, 216);
			int j = MathHelper.ceilDiv(i + 7, 36) - 1;
			this.realmSelectionList.addEntry(new RealmsMainScreen.VisitUrlNotification(text, j + 2, visitUrl));

			for (int k = 0; k < j; k++) {
				this.realmSelectionList.addEntry(new RealmsMainScreen.EmptyEntry());
			}

			this.realmSelectionList.addEntry(new RealmsMainScreen.VisitButtonEntry(visitUrl.createButton(this)));
			return true;
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
		return this.realmSelectionList.getSelectedOrNull() instanceof RealmsMainScreen.RealmSelectionListEntry realmSelectionListEntry
			? realmSelectionListEntry.getRealmsServer()
			: null;
	}

	private void leaveServer(boolean confirmed, RealmsServer realmsServer) {
		if (confirmed) {
			(new Thread("Realms-leave-server") {
				public void run() {
					try {
						RealmsClient realmsClient = RealmsClient.create();
						realmsClient.uninviteMyselfFrom(realmsServer.id);
						RealmsMainScreen.this.client.execute(RealmsMainScreen::resetServerList);
					} catch (RealmsServiceException var2) {
						RealmsMainScreen.LOGGER.error("Couldn't configure world", (Throwable)var2);
						RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(new RealmsGenericErrorScreen(var2, RealmsMainScreen.this)));
					}
				}
			}).start();
		}

		this.client.setScreen(this);
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
		this.realmSelectionList.setSelected(null);
		resetServerList();
	}

	@Override
	public Text getNarratedTitle() {
		return (Text)(switch (this.loadStatus) {
			case LOADING -> ScreenTexts.joinSentences(super.getNarratedTitle(), LOADING_TEXT);
			case NO_REALMS -> ScreenTexts.joinSentences(super.getNarratedTitle(), NO_REALMS_TEXT);
			case LIST -> super.getNarratedTitle();
		});
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		if (isSnapshotRealmsEligible()) {
			context.drawTextWithShadow(this.textRenderer, "Minecraft " + SharedConstants.getGameVersion().getName(), 2, this.height - 10, Colors.WHITE);
		}

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

	public static void play(@Nullable RealmsServer serverData, Screen parent) {
		play(serverData, parent, false);
	}

	public static void play(@Nullable RealmsServer serverData, Screen parent, boolean needsPreparation) {
		if (serverData != null) {
			if (!isSnapshotRealmsEligible() || needsPreparation) {
				MinecraftClient.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen(parent, new RealmsPrepareConnectionTask(parent, serverData)));
				return;
			}

			switch (serverData.compatibility) {
				case COMPATIBLE:
					MinecraftClient.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen(parent, new RealmsPrepareConnectionTask(parent, serverData)));
					break;
				case UNVERIFIABLE:
					showCompatibilityScreen(
						serverData,
						parent,
						Text.translatable("mco.compatibility.unverifiable.title").withColor(Colors.LIGHT_YELLOW),
						Text.translatable("mco.compatibility.unverifiable.message"),
						ScreenTexts.CONTINUE
					);
					break;
				case NEEDS_DOWNGRADE:
					showCompatibilityScreen(
						serverData,
						parent,
						Text.translatable("selectWorld.backupQuestion.downgrade").withColor(Colors.LIGHT_RED),
						Text.translatable(
							"mco.compatibility.downgrade.description",
							Text.literal(serverData.activeVersion).withColor(Colors.LIGHT_YELLOW),
							Text.literal(SharedConstants.getGameVersion().getName()).withColor(Colors.LIGHT_YELLOW)
						),
						Text.translatable("mco.compatibility.downgrade")
					);
					break;
				case NEEDS_UPGRADE:
					showCompatibilityScreen(
						serverData,
						parent,
						Text.translatable("mco.compatibility.upgrade.title").withColor(Colors.LIGHT_YELLOW),
						Text.translatable(
							"mco.compatibility.upgrade.description",
							Text.literal(serverData.activeVersion).withColor(Colors.LIGHT_YELLOW),
							Text.literal(SharedConstants.getGameVersion().getName()).withColor(Colors.LIGHT_YELLOW)
						),
						Text.translatable("mco.compatibility.upgrade")
					);
			}
		}
	}

	private static void showCompatibilityScreen(RealmsServer serverData, Screen parent, Text title, Text description, Text confirmText) {
		MinecraftClient.getInstance().setScreen(new ConfirmScreen(confirmed -> {
			Screen screen2;
			if (confirmed) {
				screen2 = new RealmsLongRunningMcoTaskScreen(parent, new RealmsPrepareConnectionTask(parent, serverData));
				resetServerList();
			} else {
				screen2 = parent;
			}

			MinecraftClient.getInstance().setScreen(screen2);
		}, title, description, confirmText, ScreenTexts.CANCEL));
	}

	public static Text getVersionText(String version, boolean compatible) {
		return getVersionText(version, compatible ? -8355712 : -2142128);
	}

	public static Text getVersionText(String version, int color) {
		return (Text)(StringUtils.isBlank(version) ? ScreenTexts.EMPTY : Text.translatable("mco.version", Text.literal(version).withColor(color)));
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

	@Environment(EnvType.CLIENT)
	static class CrossButton extends TexturedButtonWidget {
		private static final ButtonTextures TEXTURES = new ButtonTextures(new Identifier("widget/cross_button"), new Identifier("widget/cross_button_highlighted"));

		protected CrossButton(ButtonWidget.PressAction onPress, Text tooltip) {
			super(0, 0, 14, 14, TEXTURES, onPress);
			this.setTooltip(Tooltip.of(tooltip));
		}
	}

	@Environment(EnvType.CLIENT)
	class EmptyEntry extends RealmsMainScreen.Entry {
		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		}

		@Override
		public Text getNarration() {
			return Text.empty();
		}
	}

	@Environment(EnvType.CLIENT)
	abstract class Entry extends AlwaysSelectedEntryListWidget.Entry<RealmsMainScreen.Entry> {
		private static final int field_46680 = 10;
		private static final int field_46681 = 28;
		private static final int field_46682 = 7;

		protected void renderStatusIcon(RealmsServer server, DrawContext context, int x, int y, int mouseX, int mouseY) {
			int i = x - 10 - 7;
			int j = y + 2;
			if (server.expired) {
				this.drawTextureWithTooltip(context, i, j, mouseX, mouseY, RealmsMainScreen.EXPIRED_STATUS_TEXTURE, () -> RealmsMainScreen.EXPIRED_TEXT);
			} else if (server.state == RealmsServer.State.CLOSED) {
				this.drawTextureWithTooltip(context, i, j, mouseX, mouseY, RealmsMainScreen.CLOSED_STATUS_TEXTURE, () -> RealmsMainScreen.CLOSED_TEXT);
			} else if (RealmsMainScreen.this.isSelfOwnedServer(server) && server.daysLeft < 7) {
				this.drawTextureWithTooltip(context, i, j, mouseX, mouseY, RealmsMainScreen.EXPIRES_SOON_STATUS_TEXTURE, () -> {
					if (server.daysLeft <= 0) {
						return RealmsMainScreen.EXPIRES_SOON_TEXT;
					} else {
						return (Text)(server.daysLeft == 1 ? RealmsMainScreen.EXPIRES_IN_A_DAY_TEXT : Text.translatable("mco.selectServer.expires.days", server.daysLeft));
					}
				});
			} else if (server.state == RealmsServer.State.OPEN) {
				this.drawTextureWithTooltip(context, i, j, mouseX, mouseY, RealmsMainScreen.OPEN_STATUS_TEXTURE, () -> RealmsMainScreen.OPEN_TEXT);
			}
		}

		private void drawTextureWithTooltip(DrawContext context, int x, int y, int mouseX, int mouseY, Identifier texture, Supplier<Text> tooltip) {
			context.drawGuiTexture(texture, x, y, 10, 28);
			if (RealmsMainScreen.this.realmSelectionList.isMouseOver((double)mouseX, (double)mouseY)
				&& mouseX >= x
				&& mouseX <= x + 10
				&& mouseY >= y
				&& mouseY <= y + 28) {
				RealmsMainScreen.this.setTooltip((Text)tooltip.get());
			}
		}

		protected void drawOwnerOrExpiredText(DrawContext context, int y, int x, RealmsServer server) {
			int i = this.getNameX(x);
			int j = this.getNameY(y);
			int k = this.getStatusY(j);
			if (!RealmsMainScreen.this.isSelfOwnedServer(server)) {
				context.drawText(RealmsMainScreen.this.textRenderer, server.owner, i, this.getStatusY(j), Colors.GRAY, false);
			} else if (server.expired) {
				Text text = server.expiredTrial ? RealmsMainScreen.EXPIRED_TRIAL_TEXT : RealmsMainScreen.EXPIRED_LIST_TEXT;
				context.drawText(RealmsMainScreen.this.textRenderer, text, i, k, Colors.LIGHT_RED, false);
			}
		}

		protected void drawTrimmedText(DrawContext context, String string, int left, int y, int right, int color) {
			int i = right - left;
			if (RealmsMainScreen.this.textRenderer.getWidth(string) > i) {
				String string2 = RealmsMainScreen.this.textRenderer.trimToWidth(string, i - RealmsMainScreen.this.textRenderer.getWidth("... "));
				context.drawText(RealmsMainScreen.this.textRenderer, string2 + "...", left, y, color, false);
			} else {
				context.drawText(RealmsMainScreen.this.textRenderer, string, left, y, color, false);
			}
		}

		protected int getVersionRight(int x, int width, Text version) {
			return x + width - RealmsMainScreen.this.textRenderer.getWidth(version) - 20;
		}

		protected int getNameY(int y) {
			return y + 1;
		}

		protected int getTextHeight() {
			return 2 + 9;
		}

		protected int getNameX(int x) {
			return x + 36 + 2;
		}

		protected int getDescriptionY(int y) {
			return y + this.getTextHeight();
		}

		protected int getStatusY(int y) {
			return y + this.getTextHeight() * 2;
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

		int getNotificationCount() {
			return this.notificationCount;
		}

		public void setNotificationCount(int notificationCount) {
			this.notificationCount = notificationCount;
		}

		@Override
		public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
			super.renderWidget(context, mouseX, mouseY, delta);
			if (this.active && this.notificationCount != 0) {
				this.render(context);
			}
		}

		private void render(DrawContext context) {
			context.drawGuiTexture(TEXTURES[Math.min(this.notificationCount, 6) - 1], this.getX() + this.getWidth() - 5, this.getY() - 3, 8, 8);
		}
	}

	@Environment(EnvType.CLIENT)
	class ParentRealmSelectionListEntry extends RealmsMainScreen.Entry {
		private final RealmsServer server;
		private final TooltipState tooltip = new TooltipState();

		public ParentRealmSelectionListEntry(RealmsServer server) {
			this.server = server;
			this.tooltip.setTooltip(Tooltip.of(Text.translatable("mco.snapshot.parent.tooltip")));
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			return true;
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			int i = this.getNameX(x);
			int j = this.getNameY(y);
			RealmsUtil.drawPlayerHead(context, x, y, 32, this.server.ownerUUID);
			Text text = RealmsMainScreen.getVersionText(this.server.activeVersion, -8355712);
			int k = this.getVersionRight(x, entryWidth, text);
			this.drawTrimmedText(context, this.server.getName(), i, j, k, -8355712);
			if (text != ScreenTexts.EMPTY) {
				context.drawText(RealmsMainScreen.this.textRenderer, text, k, j, Colors.GRAY, false);
			}

			context.drawText(RealmsMainScreen.this.textRenderer, this.server.getDescription(), i, this.getDescriptionY(j), Colors.GRAY, false);
			this.drawOwnerOrExpiredText(context, y, x, this.server);
			this.renderStatusIcon(this.server, context, x + entryWidth, y, mouseX, mouseY);
			this.tooltip.render(hovered, this.isFocused(), new ScreenRect(x, y, entryWidth, entryHeight));
		}

		@Override
		public Text getNarration() {
			return Text.literal(this.server.name);
		}
	}

	@Environment(EnvType.CLIENT)
	class RealmSelectionList extends RealmsObjectSelectionList<RealmsMainScreen.Entry> {
		public RealmSelectionList() {
			super(RealmsMainScreen.this.width, RealmsMainScreen.this.height, 0, 36);
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
		private final TooltipState tooltip = new TooltipState();

		public RealmSelectionListEntry(RealmsServer server) {
			this.server = server;
			boolean bl = RealmsMainScreen.this.isSelfOwnedServer(server);
			if (RealmsMainScreen.isSnapshotRealmsEligible() && bl && server.hasParentWorld()) {
				this.tooltip.setTooltip(Tooltip.of(Text.translatable("mco.snapshot.paired", server.parentWorldName)));
			} else if (!bl && server.needsUpgrade()) {
				this.tooltip.setTooltip(Tooltip.of(Text.translatable("mco.snapshot.friendsRealm.upgrade", server.owner)));
			} else if (!bl && server.needsDowngrade()) {
				this.tooltip.setTooltip(Tooltip.of(Text.translatable("mco.snapshot.friendsRealm.downgrade", server.activeVersion)));
			}
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			if (this.server.state == RealmsServer.State.UNINITIALIZED) {
				context.drawGuiTexture(RealmsMainScreen.NEW_REALM_ICON_TEXTURE, x - 5, y + entryHeight / 2 - 10, 40, 20);
				int i = y + entryHeight / 2 - 9 / 2;
				context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, RealmsMainScreen.UNINITIALIZED_TEXT, x + 40 - 2, i, 8388479);
			} else {
				RealmsUtil.drawPlayerHead(context, x, y, 32, this.server.ownerUUID);
				this.drawServerNameAndVersion(context, y, x, entryWidth);
				this.drawDescription(context, y, x);
				this.drawOwnerOrExpiredText(context, y, x, this.server);
				this.renderStatusIcon(this.server, context, x + entryWidth, y, mouseX, mouseY);
				this.tooltip.render(hovered, this.isFocused(), new ScreenRect(x, y, entryWidth, entryHeight));
			}
		}

		private void drawServerNameAndVersion(DrawContext context, int y, int x, int width) {
			int i = this.getNameX(x);
			int j = this.getNameY(y);
			Text text = RealmsMainScreen.getVersionText(this.server.activeVersion, this.server.isCompatible());
			int k = this.getVersionRight(x, width, text);
			this.drawTrimmedText(context, this.server.getName(), i, j, k, -1);
			if (text != ScreenTexts.EMPTY) {
				context.drawText(RealmsMainScreen.this.textRenderer, text, k, j, Colors.GRAY, false);
			}
		}

		private void drawDescription(DrawContext context, int y, int x) {
			int i = this.getNameX(x);
			int j = this.getNameY(y);
			int k = this.getDescriptionY(j);
			String string = this.server.getMinigameName();
			if (this.server.worldType == RealmsServer.WorldType.MINIGAME && string != null) {
				Text text = Text.literal(string).formatted(Formatting.GRAY);
				context.drawText(
					RealmsMainScreen.this.textRenderer, Text.translatable("mco.selectServer.minigameName", text).withColor(Colors.LIGHT_YELLOW), i, k, Colors.WHITE, false
				);
			} else {
				context.drawText(RealmsMainScreen.this.textRenderer, this.server.getDescription(), i, this.getDescriptionY(j), Colors.GRAY, false);
			}
		}

		private void play() {
			RealmsMainScreen.this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			RealmsMainScreen.play(this.server, RealmsMainScreen.this);
		}

		private void createRealm() {
			RealmsMainScreen.this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			RealmsCreateRealmScreen realmsCreateRealmScreen = new RealmsCreateRealmScreen(RealmsMainScreen.this, this.server);
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

		@Override
		public Text getNarration() {
			return (Text)(this.server.state == RealmsServer.State.UNINITIALIZED
				? RealmsMainScreen.UNINITIALIZED_BUTTON_NARRATION
				: Text.translatable("narrator.select", this.server.name));
		}

		public RealmsServer getRealmsServer() {
			return this.server;
		}
	}

	@Environment(EnvType.CLIENT)
	interface Request<T> {
		T request(RealmsClient client) throws RealmsServiceException;
	}

	@Environment(EnvType.CLIENT)
	class SnapshotEntry extends RealmsMainScreen.Entry {
		private static final Text START_TEXT = Text.translatable("mco.snapshot.start");
		private static final int field_46677 = 5;
		private final TooltipState tooltip = new TooltipState();
		private final RealmsServer server;

		public SnapshotEntry(RealmsServer server) {
			this.server = server;
			this.tooltip.setTooltip(Tooltip.of(Text.translatable("mco.snapshot.tooltip")));
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			context.drawGuiTexture(RealmsMainScreen.NEW_REALM_ICON_TEXTURE, x - 5, y + entryHeight / 2 - 10, 40, 20);
			int i = y + entryHeight / 2 - 9 / 2;
			context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, START_TEXT, x + 40 - 2, i - 5, 8388479);
			context.drawTextWithShadow(
				RealmsMainScreen.this.textRenderer, Text.translatable("mco.snapshot.description", this.server.name), x + 40 - 2, i + 5, Colors.GRAY
			);
			this.tooltip.render(hovered, this.isFocused(), new ScreenRect(x, y, entryWidth, entryHeight));
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			this.showPopup();
			return true;
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (KeyCodes.isToggle(keyCode)) {
				this.showPopup();
				return true;
			} else {
				return super.keyPressed(keyCode, scanCode, modifiers);
			}
		}

		private void showPopup() {
			RealmsMainScreen.this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			RealmsMainScreen.this.client
				.setScreen(
					new PopupScreen.Builder(RealmsMainScreen.this, Text.translatable("mco.snapshot.createSnapshotPopup.title"))
						.message(Text.translatable("mco.snapshot.createSnapshotPopup.text"))
						.button(
							Text.translatable("mco.selectServer.create"),
							screen -> RealmsMainScreen.this.client.setScreen(new RealmsCreateRealmScreen(RealmsMainScreen.this, this.server.id))
						)
						.button(ScreenTexts.CANCEL, PopupScreen::close)
						.build()
				);
		}

		@Override
		public Text getNarration() {
			return Text.translatable("gui.narrate.button", ScreenTexts.joinSentences(START_TEXT, Text.translatable("mco.snapshot.description", this.server.name)));
		}
	}

	@Environment(EnvType.CLIENT)
	class VisitButtonEntry extends RealmsMainScreen.Entry {
		private final ButtonWidget button;

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
			this.button.setPosition(RealmsMainScreen.this.width / 2 - 75, y + 4);
			this.button.render(context, mouseX, mouseY, tickDelta);
		}

		@Override
		public void setFocused(boolean focused) {
			super.setFocused(focused);
			this.button.setFocused(focused);
		}

		@Override
		public Text getNarration() {
			return this.button.getMessage();
		}
	}

	@Environment(EnvType.CLIENT)
	class VisitUrlNotification extends RealmsMainScreen.Entry {
		private static final int field_43002 = 40;
		private static final int field_43004 = -12303292;
		private final Text message;
		private final int lines;
		private final List<ClickableWidget> gridChildren = new ArrayList();
		@Nullable
		private final RealmsMainScreen.CrossButton dismissButton;
		private final MultilineTextWidget textWidget;
		private final GridWidget grid;
		private final SimplePositioningWidget textGrid;
		private int width = -1;

		public VisitUrlNotification(Text message, int lines, RealmsNotification notification) {
			this.message = message;
			this.lines = lines;
			this.grid = new GridWidget();
			int i = 7;
			this.grid.add(IconWidget.create(20, 20, RealmsMainScreen.INFO_ICON_TEXTURE), 0, 0, this.grid.copyPositioner().margin(7, 7, 0, 0));
			this.grid.add(EmptyWidget.ofWidth(40), 0, 0);
			this.textGrid = this.grid.add(new SimplePositioningWidget(0, 9 * 3 * (lines - 1)), 0, 1, this.grid.copyPositioner().marginTop(7));
			this.textWidget = this.textGrid
				.add(
					new MultilineTextWidget(message, RealmsMainScreen.this.textRenderer).setCentered(true), this.textGrid.copyPositioner().alignHorizontalCenter().alignTop()
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
			context.drawBorder(x - 2, y - 2, entryWidth, 36 * this.lines - 2, -12303292);
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
