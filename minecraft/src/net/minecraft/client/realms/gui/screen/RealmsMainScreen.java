package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.AxisGridWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.IconWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.realms.KeyCombo;
import net.minecraft.client.realms.Ping;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsNewsUpdater;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.realms.dto.PingResult;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerPlayerList;
import net.minecraft.client.realms.dto.RegionPingResult;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.task.RealmsGetServerDetailsTask;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.realms.util.RealmsPersistence;
import net.minecraft.client.realms.util.RealmsServerFilterer;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Urls;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsMainScreen extends RealmsScreen {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final Identifier ON_ICON = new Identifier("realms", "textures/gui/realms/on_icon.png");
	private static final Identifier OFF_ICON = new Identifier("realms", "textures/gui/realms/off_icon.png");
	private static final Identifier EXPIRED_ICON = new Identifier("realms", "textures/gui/realms/expired_icon.png");
	private static final Identifier EXPIRES_SOON_ICON = new Identifier("realms", "textures/gui/realms/expires_soon_icon.png");
	static final Identifier INVITATION_ICON = new Identifier("realms", "textures/gui/realms/invitation_icons.png");
	static final Identifier INVITE_ICON = new Identifier("realms", "textures/gui/realms/invite_icon.png");
	static final Identifier WORLD_ICON = new Identifier("realms", "textures/gui/realms/world_icon.png");
	private static final Identifier REALMS = new Identifier("realms", "textures/gui/title/realms.png");
	private static final Identifier NEWS_ICON = new Identifier("realms", "textures/gui/realms/news_icon.png");
	private static final Identifier POPUP = new Identifier("realms", "textures/gui/realms/popup.png");
	private static final Identifier DARKEN = new Identifier("realms", "textures/gui/realms/darken.png");
	static final Identifier CROSS_ICON = new Identifier("realms", "textures/gui/realms/cross_icon.png");
	private static final Identifier TRIAL_ICON = new Identifier("realms", "textures/gui/realms/trial_icon.png");
	static final Identifier INFO_ICON = new Identifier("minecraft", "textures/gui/info_icon.png");
	static final List<Text> TRIAL_MESSAGE_LINES = ImmutableList.of(Text.translatable("mco.trial.message.line1"), Text.translatable("mco.trial.message.line2"));
	static final Text UNINITIALIZED_TEXT = Text.translatable("mco.selectServer.uninitialized");
	static final Text EXPIRED_LIST_TEXT = Text.translatable("mco.selectServer.expiredList");
	private static final Text EXPIRED_RENEW_TEXT = Text.translatable("mco.selectServer.expiredRenew");
	static final Text EXPIRED_TRIAL_TEXT = Text.translatable("mco.selectServer.expiredTrial");
	static final Text MINIGAME_TEXT = Text.translatable("mco.selectServer.minigame").append(ScreenTexts.SPACE);
	private static final Text POPUP_TEXT = Text.translatable("mco.selectServer.popup");
	private static final Text PLAY_TEXT = Text.translatable("mco.selectServer.play");
	private static final Text LEAVE_TEXT = Text.translatable("mco.selectServer.leave");
	private static final Text CONFIGURE_TEXT = Text.translatable("mco.selectServer.configure");
	private static final Text EXPIRED_TEXT = Text.translatable("mco.selectServer.expired");
	private static final Text EXPIRES_SOON_TEXT = Text.translatable("mco.selectServer.expires.soon");
	private static final Text EXPIRES_IN_A_DAY_TEXT = Text.translatable("mco.selectServer.expires.day");
	private static final Text OPEN_TEXT = Text.translatable("mco.selectServer.open");
	private static final Text CLOSED_TEXT = Text.translatable("mco.selectServer.closed");
	private static final Text NEWS_TEXT = Text.translatable("mco.news");
	static final Text UNINITIALIZED_BUTTON_NARRATION = Text.translatable("gui.narrate.button", UNINITIALIZED_TEXT);
	static final Text TRIAL_NARRATION = ScreenTexts.joinLines(TRIAL_MESSAGE_LINES);
	private static final int field_42862 = 100;
	private static final int field_42863 = 308;
	private static final int field_42864 = 204;
	private static final int field_42865 = 64;
	private static final int field_44509 = 128;
	private static final int field_44510 = 34;
	private static final int field_44511 = 128;
	private static final int field_44512 = 64;
	private static final int field_44513 = 5;
	private static final int field_44514 = 44;
	private static List<Identifier> IMAGES = ImmutableList.of();
	@Nullable
	private PeriodicRunnerFactory.RunnersManager periodicRunnersManager;
	private RealmsServerFilterer serverFilterer;
	private final Set<UUID> seenNotifications = new HashSet();
	private static boolean overrideConfigure;
	private static int lastScrollYPosition = -1;
	static volatile boolean hasParentalConsent;
	static volatile boolean checkedParentalConsent;
	static volatile boolean checkedClientCompatibility;
	@Nullable
	static Screen realmsGenericErrorScreen;
	private static boolean regionsPinged;
	private final RateLimiter rateLimiter;
	private boolean dontSetConnectedToRealms;
	final Screen parent;
	RealmsMainScreen.RealmSelectionList realmSelectionList;
	private boolean hasSelectionList;
	private ButtonWidget playButton;
	private ButtonWidget backButton;
	private ButtonWidget renewButton;
	private ButtonWidget configureButton;
	private ButtonWidget leaveButton;
	private List<RealmsServer> realmsServers = ImmutableList.of();
	volatile int pendingInvitesCount;
	int animTick;
	private boolean hasFetchedServers;
	boolean popupOpenedByUser;
	private boolean justClosedPopup;
	private volatile boolean trialAvailable;
	private volatile boolean createdTrial;
	private volatile boolean showingPopup;
	volatile boolean hasUnreadNews;
	@Nullable
	volatile String newsLink;
	private int carouselIndex;
	private int carouselTick;
	private boolean hasSwitchedCarouselImage;
	private List<KeyCombo> keyCombos;
	long lastPlayButtonClickTime;
	private ReentrantLock connectLock = new ReentrantLock();
	private MultilineText popupText = MultilineText.EMPTY;
	private final List<RealmsNotification> notifications = new ArrayList();
	private ButtonWidget showPopupButton;
	private RealmsMainScreen.PendingInvitesButton pendingInvitesButton;
	private ButtonWidget newsButton;
	private ButtonWidget createTrialButton;
	private ButtonWidget buyARealmButton;
	private ButtonWidget closeButton;

	public RealmsMainScreen(Screen parent) {
		super(NarratorManager.EMPTY);
		this.parent = parent;
		this.rateLimiter = RateLimiter.create(0.016666668F);
	}

	private boolean shouldShowMessageInList() {
		if (hasParentalConsent() && this.hasFetchedServers) {
			if (this.trialAvailable && !this.createdTrial) {
				return true;
			} else {
				for (RealmsServer realmsServer : this.realmsServers) {
					if (realmsServer.ownerUUID.equals(this.client.getSession().getUuid())) {
						return false;
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}

	public boolean shouldShowPopup() {
		if (!hasParentalConsent() || !this.hasFetchedServers) {
			return false;
		} else {
			return this.popupOpenedByUser ? true : this.realmsServers.isEmpty();
		}
	}

	@Override
	public void init() {
		this.keyCombos = Lists.<KeyCombo>newArrayList(
			new KeyCombo(new char[]{'3', '2', '1', '4', '5', '6'}, () -> overrideConfigure = !overrideConfigure),
			new KeyCombo(new char[]{'9', '8', '7', '1', '2', '3'}, () -> {
				if (RealmsClient.currentEnvironment == RealmsClient.Environment.STAGE) {
					this.switchToProd();
				} else {
					this.switchToStage();
				}
			}),
			new KeyCombo(new char[]{'9', '8', '7', '4', '5', '6'}, () -> {
				if (RealmsClient.currentEnvironment == RealmsClient.Environment.LOCAL) {
					this.switchToProd();
				} else {
					this.switchToLocal();
				}
			})
		);
		if (realmsGenericErrorScreen != null) {
			this.client.setScreen(realmsGenericErrorScreen);
		} else {
			this.connectLock = new ReentrantLock();
			if (checkedClientCompatibility && !hasParentalConsent()) {
				this.checkParentalConsent();
			}

			this.checkClientCompatibility();
			if (!this.dontSetConnectedToRealms) {
				this.client.setConnectedToRealms(false);
			}

			this.showingPopup = false;
			this.realmSelectionList = new RealmsMainScreen.RealmSelectionList();
			if (lastScrollYPosition != -1) {
				this.realmSelectionList.setScrollAmount((double)lastScrollYPosition);
			}

			this.addSelectableChild(this.realmSelectionList);
			this.hasSelectionList = true;
			this.setInitialFocus(this.realmSelectionList);
			this.addPurchaseButtons();
			this.addLowerButtons();
			this.addInvitesAndNewsButtons();
			this.updateButtonStates(null);
			this.popupText = MultilineText.create(this.textRenderer, POPUP_TEXT, 100);
			RealmsNewsUpdater realmsNewsUpdater = this.client.getRealmsPeriodicCheckers().newsUpdater;
			this.hasUnreadNews = realmsNewsUpdater.hasUnreadNews();
			this.newsLink = realmsNewsUpdater.getNewsLink();
			if (this.serverFilterer == null) {
				this.serverFilterer = new RealmsServerFilterer(this.client);
			}

			if (this.periodicRunnersManager != null) {
				this.periodicRunnersManager.forceRunListeners();
			}
		}
	}

	private static boolean hasParentalConsent() {
		return checkedParentalConsent && hasParentalConsent;
	}

	public void addInvitesAndNewsButtons() {
		this.pendingInvitesButton = this.addDrawableChild(new RealmsMainScreen.PendingInvitesButton());
		this.newsButton = this.addDrawableChild(new RealmsMainScreen.NewsButton());
		this.showPopupButton = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("mco.selectServer.purchase"), button -> this.popupOpenedByUser = !this.popupOpenedByUser)
				.dimensions(this.width - 90, 12, 80, 20)
				.build()
		);
	}

	public void addPurchaseButtons() {
		this.createTrialButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.selectServer.trial"), button -> {
			if (this.trialAvailable && !this.createdTrial) {
				Util.getOperatingSystem().open("https://aka.ms/startjavarealmstrial");
				this.client.setScreen(this.parent);
			}
		}).dimensions(this.width / 2 + 52, this.popupY0() + 137 - 20, 98, 20).build());
		this.buyARealmButton = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("mco.selectServer.buy"), button -> Util.getOperatingSystem().open("https://aka.ms/BuyJavaRealms"))
				.dimensions(this.width / 2 + 52, this.popupY0() + 160 - 20, 98, 20)
				.build()
		);
		this.closeButton = this.addDrawableChild(new RealmsMainScreen.CloseButton());
	}

	public void addLowerButtons() {
		this.playButton = ButtonWidget.builder(PLAY_TEXT, button -> this.play(this.findServer(), this)).width(100).build();
		this.configureButton = ButtonWidget.builder(CONFIGURE_TEXT, button -> this.configureClicked(this.findServer())).width(100).build();
		this.renewButton = ButtonWidget.builder(EXPIRED_RENEW_TEXT, button -> this.onRenew(this.findServer())).width(100).build();
		this.leaveButton = ButtonWidget.builder(LEAVE_TEXT, button -> this.leaveClicked(this.findServer())).width(100).build();
		this.backButton = ButtonWidget.builder(ScreenTexts.BACK, button -> {
			if (!this.justClosedPopup) {
				this.client.setScreen(this.parent);
			}
		}).width(100).build();
		GridWidget gridWidget = new GridWidget();
		GridWidget.Adder adder = gridWidget.createAdder(1);
		AxisGridWidget axisGridWidget = adder.add(new AxisGridWidget(308, 20, AxisGridWidget.DisplayAxis.HORIZONTAL), adder.copyPositioner().marginBottom(4));
		axisGridWidget.add(this.playButton);
		axisGridWidget.add(this.configureButton);
		axisGridWidget.add(this.renewButton);
		AxisGridWidget axisGridWidget2 = adder.add(new AxisGridWidget(204, 20, AxisGridWidget.DisplayAxis.HORIZONTAL), adder.copyPositioner().alignHorizontalCenter());
		axisGridWidget2.add(this.leaveButton);
		axisGridWidget2.add(this.backButton);
		gridWidget.forEachChild(child -> {
			ClickableWidget var10000 = this.addDrawableChild(child);
		});
		gridWidget.refreshPositions();
		SimplePositioningWidget.setPos(gridWidget, 0, this.height - 64, this.width, 64);
	}

	void updateButtonStates(@Nullable RealmsServer server) {
		this.backButton.active = true;
		if (hasParentalConsent() && this.hasFetchedServers) {
			boolean bl = this.shouldShowPopup() && this.trialAvailable && !this.createdTrial;
			this.createTrialButton.visible = bl;
			this.createTrialButton.active = bl;
			this.buyARealmButton.visible = this.shouldShowPopup();
			this.closeButton.visible = this.shouldShowPopup();
			this.newsButton.active = true;
			this.newsButton.visible = this.newsLink != null;
			this.pendingInvitesButton.active = true;
			this.pendingInvitesButton.visible = true;
			this.showPopupButton.active = !this.shouldShowPopup();
			this.playButton.visible = !this.shouldShowPopup();
			this.renewButton.visible = !this.shouldShowPopup();
			this.leaveButton.visible = !this.shouldShowPopup();
			this.configureButton.visible = !this.shouldShowPopup();
			this.backButton.visible = !this.shouldShowPopup();
			this.playButton.active = this.shouldPlayButtonBeActive(server);
			this.renewButton.active = this.shouldRenewButtonBeActive(server);
			this.leaveButton.active = this.shouldLeaveButtonBeActive(server);
			this.configureButton.active = this.shouldConfigureButtonBeActive(server);
		} else {
			hide(
				new ClickableWidget[]{
					this.playButton,
					this.renewButton,
					this.configureButton,
					this.createTrialButton,
					this.buyARealmButton,
					this.closeButton,
					this.newsButton,
					this.pendingInvitesButton,
					this.showPopupButton,
					this.leaveButton
				}
			);
		}
	}

	private boolean shouldShowPopupButton() {
		return (!this.shouldShowPopup() || this.popupOpenedByUser) && hasParentalConsent() && this.hasFetchedServers;
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
		if (this.pendingInvitesButton != null) {
			this.pendingInvitesButton.updatePendingText();
		}

		this.justClosedPopup = false;
		this.animTick++;
		boolean bl = hasParentalConsent();
		if (this.periodicRunnersManager == null && bl) {
			this.periodicRunnersManager = this.createPeriodicRunnersManager(this.client.getRealmsPeriodicCheckers());
		} else if (this.periodicRunnersManager != null && !bl) {
			this.periodicRunnersManager = null;
		}

		if (this.periodicRunnersManager != null) {
			this.periodicRunnersManager.runAll();
		}

		if (this.shouldShowPopup()) {
			this.carouselTick++;
		}

		if (this.showPopupButton != null) {
			this.showPopupButton.visible = this.shouldShowPopupButton();
			this.showPopupButton.active = this.showPopupButton.visible;
		}
	}

	private PeriodicRunnerFactory.RunnersManager createPeriodicRunnersManager(RealmsPeriodicCheckers periodicCheckers) {
		PeriodicRunnerFactory.RunnersManager runnersManager = periodicCheckers.runnerFactory.create();
		runnersManager.add(periodicCheckers.serverList, servers -> {
			List<RealmsServer> list = this.serverFilterer.filterAndSort(servers);
			boolean bl = false;

			for (RealmsServer realmsServer : list) {
				if (this.isOwnedNotExpired(realmsServer)) {
					bl = true;
				}
			}

			this.realmsServers = list;
			this.hasFetchedServers = true;
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
			if (this.pendingInvitesCount > 0 && this.rateLimiter.tryAcquire(1)) {
				this.client.getNarratorManager().narrate(Text.translatable("mco.configure.world.invite.narration", this.pendingInvitesCount));
			}
		});
		runnersManager.add(periodicCheckers.trialAvailability, trialAvailable -> {
			if (!this.createdTrial) {
				if (trialAvailable != this.trialAvailable && this.shouldShowPopup()) {
					this.trialAvailable = trialAvailable;
					this.showingPopup = false;
				} else {
					this.trialAvailable = trialAvailable;
				}
			}
		});
		runnersManager.add(periodicCheckers.liveStats, liveStats -> {
			for (RealmsServerPlayerList realmsServerPlayerList : liveStats.servers) {
				for (RealmsServer realmsServer : this.realmsServers) {
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
			this.updateButtonStates(null);
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
		boolean bl = !this.hasFetchedServers;
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

		if (this.shouldShowMessageInList()) {
			this.realmSelectionList.addEntry(new RealmsMainScreen.RealmSelectionListTrialEntry());
		}

		RealmsMainScreen.Entry entry = null;
		RealmsServer realmsServer = this.findServer();

		for (RealmsServer realmsServer2 : this.realmsServers) {
			RealmsMainScreen.RealmSelectionListEntry realmSelectionListEntry = new RealmsMainScreen.RealmSelectionListEntry(realmsServer2);
			this.realmSelectionList.addEntry(realmSelectionListEntry);
			if (realmsServer != null && realmsServer.id == realmsServer2.id) {
				entry = realmSelectionListEntry;
			}
		}

		if (bl) {
			this.updateButtonStates(null);
		} else {
			this.realmSelectionList.setSelected(entry);
		}
	}

	private void addNotificationEntry(RealmsMainScreen.RealmSelectionList selectionList, RealmsNotification notification) {
		if (notification instanceof RealmsNotification.VisitUrl visitUrl) {
			selectionList.addEntry(new RealmsMainScreen.VisitUrlNotification(visitUrl.getDefaultMessage(), visitUrl));
			selectionList.addEntry(new RealmsMainScreen.VisitButtonEntry(visitUrl.createButton(this)));
		}
	}

	void resetPeriodicRunnersManager() {
		if (this.periodicRunnersManager != null) {
			this.periodicRunnersManager.resetAll();
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

		for (RealmsServer realmsServer : this.realmsServers) {
			if (this.isOwnedNotExpired(realmsServer)) {
				list.add(realmsServer.id);
			}
		}

		return list;
	}

	public void setCreatedTrial(boolean createdTrial) {
		this.createdTrial = createdTrial;
	}

	private void onRenew(@Nullable RealmsServer realmsServer) {
		if (realmsServer != null) {
			String string = Urls.getExtendJavaRealmsUrl(realmsServer.remoteSubscriptionId, this.client.getSession().getUuid(), realmsServer.expiredTrial);
			this.client.keyboard.setClipboard(string);
			Util.getOperatingSystem().open(string);
		}
	}

	private void checkClientCompatibility() {
		if (!checkedClientCompatibility) {
			checkedClientCompatibility = true;
			(new Thread("MCO Compatability Checker #1") {
					public void run() {
						RealmsClient realmsClient = RealmsClient.create();

						try {
							RealmsClient.CompatibleVersionResponse compatibleVersionResponse = realmsClient.clientCompatible();
							if (compatibleVersionResponse != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
								RealmsMainScreen.realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.parent);
								RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(RealmsMainScreen.realmsGenericErrorScreen));
								return;
							}

							RealmsMainScreen.this.checkParentalConsent();
						} catch (RealmsServiceException var3) {
							RealmsMainScreen.checkedClientCompatibility = false;
							RealmsMainScreen.LOGGER.error("Couldn't connect to realms", (Throwable)var3);
							if (var3.httpResultCode == 401) {
								RealmsMainScreen.realmsGenericErrorScreen = new RealmsGenericErrorScreen(
									Text.translatable("mco.error.invalid.session.title"), Text.translatable("mco.error.invalid.session.message"), RealmsMainScreen.this.parent
								);
								RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(RealmsMainScreen.realmsGenericErrorScreen));
							} else {
								RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(new RealmsGenericErrorScreen(var3, RealmsMainScreen.this.parent)));
							}
						}
					}
				})
				.start();
		}
	}

	void checkParentalConsent() {
		(new Thread("MCO Compatability Checker #1") {
			public void run() {
				RealmsClient realmsClient = RealmsClient.create();

				try {
					Boolean boolean_ = realmsClient.mcoEnabled();
					if (boolean_) {
						RealmsMainScreen.LOGGER.info("Realms is available for this user");
						RealmsMainScreen.hasParentalConsent = true;
					} else {
						RealmsMainScreen.LOGGER.info("Realms is not available for this user");
						RealmsMainScreen.hasParentalConsent = false;
						RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(new RealmsParentalConsentScreen(RealmsMainScreen.this.parent)));
					}

					RealmsMainScreen.checkedParentalConsent = true;
				} catch (RealmsServiceException var3) {
					RealmsMainScreen.LOGGER.error("Couldn't connect to realms", (Throwable)var3);
					RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(new RealmsGenericErrorScreen(var3, RealmsMainScreen.this.parent)));
				}
			}
		}).start();
	}

	private void switchToStage() {
		if (RealmsClient.currentEnvironment != RealmsClient.Environment.STAGE) {
			(new Thread("MCO Stage Availability Checker #1") {
				public void run() {
					RealmsClient realmsClient = RealmsClient.create();

					try {
						Boolean boolean_ = realmsClient.stageAvailable();
						if (boolean_) {
							RealmsClient.switchToStage();
							RealmsMainScreen.LOGGER.info("Switched to stage");
							RealmsMainScreen.this.resetPeriodicRunnersManager();
						}
					} catch (RealmsServiceException var3) {
						RealmsMainScreen.LOGGER.error("Couldn't connect to Realms: {}", var3.toString());
					}
				}
			}).start();
		}
	}

	private void switchToLocal() {
		if (RealmsClient.currentEnvironment != RealmsClient.Environment.LOCAL) {
			(new Thread("MCO Local Availability Checker #1") {
				public void run() {
					RealmsClient realmsClient = RealmsClient.create();

					try {
						Boolean boolean_ = realmsClient.stageAvailable();
						if (boolean_) {
							RealmsClient.switchToLocal();
							RealmsMainScreen.LOGGER.info("Switched to local");
							RealmsMainScreen.this.resetPeriodicRunnersManager();
						}
					} catch (RealmsServiceException var3) {
						RealmsMainScreen.LOGGER.error("Couldn't connect to Realms: {}", var3.toString());
					}
				}
			}).start();
		}
	}

	private void switchToProd() {
		RealmsClient.switchToProd();
		this.resetPeriodicRunnersManager();
	}

	private void configureClicked(@Nullable RealmsServer serverData) {
		if (serverData != null && (this.client.getSession().getUuid().equals(serverData.ownerUUID) || overrideConfigure)) {
			this.saveListScrollPosition();
			this.client.setScreen(new RealmsConfigureWorldScreen(this, serverData.id));
		}
	}

	private void leaveClicked(@Nullable RealmsServer selectedServer) {
		if (selectedServer != null && !this.client.getSession().getUuid().equals(selectedServer.ownerUUID)) {
			this.saveListScrollPosition();
			Text text = Text.translatable("mco.configure.world.leave.question.line1");
			Text text2 = Text.translatable("mco.configure.world.leave.question.line2");
			this.client
				.setScreen(
					new RealmsLongConfirmationScreen(confirmed -> this.leaveServer(confirmed, selectedServer), RealmsLongConfirmationScreen.Type.INFO, text, text2, true)
				);
		}
	}

	private void saveListScrollPosition() {
		lastScrollYPosition = (int)this.realmSelectionList.getScrollAmount();
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
						RealmsMainScreen.LOGGER.error("Couldn't configure world");
						RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(new RealmsGenericErrorScreen(var2, RealmsMainScreen.this)));
					}
				}
			}).start();
		}

		this.client.setScreen(this);
	}

	void removeServer(RealmsServer serverData) {
		this.realmsServers = this.serverFilterer.remove(serverData);
		this.realmSelectionList.children().removeIf(child -> {
			RealmsServer realmsServer2 = child.getRealmsServer();
			return realmsServer2 != null && realmsServer2.id == serverData.id;
		});
		this.realmSelectionList.setSelected(null);
		this.updateButtonStates(null);
		this.playButton.active = false;
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
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.keyCombos.forEach(KeyCombo::reset);
			this.onClosePopup();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	void onClosePopup() {
		if (this.shouldShowPopup() && this.popupOpenedByUser) {
			this.popupOpenedByUser = false;
		} else {
			this.client.setScreen(this.parent);
		}
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		this.keyCombos.forEach(keyCombo -> keyCombo.keyPressed(chr));
		return true;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		this.realmSelectionList.render(context, mouseX, mouseY, delta);
		context.drawTexture(REALMS, this.width / 2 - 64, 5, 0.0F, 0.0F, 128, 34, 128, 64);
		if (RealmsClient.currentEnvironment == RealmsClient.Environment.STAGE) {
			this.renderStage(context);
		}

		if (RealmsClient.currentEnvironment == RealmsClient.Environment.LOCAL) {
			this.renderLocal(context);
		}

		if (this.shouldShowPopup()) {
			context.getMatrices().push();
			context.getMatrices().translate(0.0F, 0.0F, 100.0F);
			this.drawPopup(context);
			context.getMatrices().pop();
		} else {
			if (this.showingPopup) {
				this.updateButtonStates(null);
				if (!this.hasSelectionList) {
					this.addSelectableChild(this.realmSelectionList);
					this.hasSelectionList = true;
				}

				this.playButton.active = this.shouldPlayButtonBeActive(this.findServer());
			}

			this.showingPopup = false;
		}

		super.render(context, mouseX, mouseY, delta);
		if (this.trialAvailable && !this.createdTrial && this.shouldShowPopup()) {
			int i = 8;
			int j = 8;
			int k = 0;
			if ((Util.getMeasuringTimeMs() / 800L & 1L) == 1L) {
				k = 8;
			}

			context.drawTexture(
				TRIAL_ICON,
				this.createTrialButton.getX() + this.createTrialButton.getWidth() - 8 - 4,
				this.createTrialButton.getY() + this.createTrialButton.getHeight() / 2 - 4,
				0.0F,
				(float)k,
				8,
				8,
				8,
				16
			);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.isOutsidePopup(mouseX, mouseY) && this.popupOpenedByUser) {
			this.popupOpenedByUser = false;
			this.justClosedPopup = true;
			return true;
		} else {
			return super.mouseClicked(mouseX, mouseY, button);
		}
	}

	private boolean isOutsidePopup(double xm, double ym) {
		int i = this.popupX0();
		int j = this.popupY0();
		return xm < (double)(i - 5) || xm > (double)(i + 315) || ym < (double)(j - 5) || ym > (double)(j + 171);
	}

	private void drawPopup(DrawContext context) {
		int i = this.popupX0();
		int j = this.popupY0();
		if (!this.showingPopup) {
			this.carouselIndex = 0;
			this.carouselTick = 0;
			this.hasSwitchedCarouselImage = true;
			this.updateButtonStates(null);
			if (this.hasSelectionList) {
				this.remove(this.realmSelectionList);
				this.hasSelectionList = false;
			}

			this.client.getNarratorManager().narrate(POPUP_TEXT);
		}

		if (this.hasFetchedServers) {
			this.showingPopup = true;
		}

		context.setShaderColor(1.0F, 1.0F, 1.0F, 0.7F);
		RenderSystem.enableBlend();
		int k = 0;
		int l = 32;
		context.drawTexture(DARKEN, 0, 32, 0.0F, 0.0F, this.width, this.height - 40 - 32, 310, 166);
		RenderSystem.disableBlend();
		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		context.drawTexture(POPUP, i, j, 0.0F, 0.0F, 310, 166, 310, 166);
		if (!IMAGES.isEmpty()) {
			context.drawTexture((Identifier)IMAGES.get(this.carouselIndex), i + 7, j + 7, 0.0F, 0.0F, 195, 152, 195, 152);
			if (this.carouselTick % 95 < 5) {
				if (!this.hasSwitchedCarouselImage) {
					this.carouselIndex = (this.carouselIndex + 1) % IMAGES.size();
					this.hasSwitchedCarouselImage = true;
				}
			} else {
				this.hasSwitchedCarouselImage = false;
			}
		}

		this.popupText.draw(context, this.width / 2 + 52, j + 7, 10, 16777215);
	}

	int popupX0() {
		return (this.width - 310) / 2;
	}

	int popupY0() {
		return this.height / 2 - 80;
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

			this.dontSetConnectedToRealms = true;
			this.client.setScreen(new RealmsLongRunningMcoTaskScreen(parent, new RealmsGetServerDetailsTask(this, parent, serverData, this.connectLock)));
		}
	}

	boolean isSelfOwnedServer(RealmsServer serverData) {
		return serverData.ownerUUID != null && serverData.ownerUUID.equals(this.client.getSession().getUuid());
	}

	private boolean isOwnedNotExpired(RealmsServer serverData) {
		return this.isSelfOwnedServer(serverData) && !serverData.expired;
	}

	void drawExpired(DrawContext context, int x, int y, int mouseX, int mouseY) {
		context.drawTexture(EXPIRED_ICON, x, y, 0.0F, 0.0F, 10, 28, 10, 28);
		if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27 && mouseY < this.height - 40 && mouseY > 32 && !this.shouldShowPopup()) {
			this.setTooltip(EXPIRED_TEXT);
		}
	}

	void drawExpiring(DrawContext context, int x, int y, int mouseX, int mouseY, int remainingDays) {
		if (this.animTick % 20 < 10) {
			context.drawTexture(EXPIRES_SOON_ICON, x, y, 0.0F, 0.0F, 10, 28, 20, 28);
		} else {
			context.drawTexture(EXPIRES_SOON_ICON, x, y, 10.0F, 0.0F, 10, 28, 20, 28);
		}

		if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27 && mouseY < this.height - 40 && mouseY > 32 && !this.shouldShowPopup()) {
			if (remainingDays <= 0) {
				this.setTooltip(EXPIRES_SOON_TEXT);
			} else if (remainingDays == 1) {
				this.setTooltip(EXPIRES_IN_A_DAY_TEXT);
			} else {
				this.setTooltip(Text.translatable("mco.selectServer.expires.days", remainingDays));
			}
		}
	}

	void drawOpen(DrawContext context, int x, int y, int mouseX, int mouseY) {
		context.drawTexture(ON_ICON, x, y, 0.0F, 0.0F, 10, 28, 10, 28);
		if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27 && mouseY < this.height - 40 && mouseY > 32 && !this.shouldShowPopup()) {
			this.setTooltip(OPEN_TEXT);
		}
	}

	void drawClose(DrawContext context, int x, int y, int mouseX, int mouseY) {
		context.drawTexture(OFF_ICON, x, y, 0.0F, 0.0F, 10, 28, 10, 28);
		if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27 && mouseY < this.height - 40 && mouseY > 32 && !this.shouldShowPopup()) {
			this.setTooltip(CLOSED_TEXT);
		}
	}

	void renderNews(DrawContext context, int mouseX, int mouseY, boolean hasUnread, int x, int y, boolean hovered, boolean active) {
		boolean bl = false;
		if (mouseX >= x && mouseX <= x + 20 && mouseY >= y && mouseY <= y + 20) {
			bl = true;
		}

		if (!active) {
			context.setShaderColor(0.5F, 0.5F, 0.5F, 1.0F);
		}

		boolean bl2 = active && hovered;
		float f = bl2 ? 20.0F : 0.0F;
		context.drawTexture(NEWS_ICON, x, y, f, 0.0F, 20, 20, 40, 20);
		if (bl && active) {
			this.setTooltip(NEWS_TEXT);
		}

		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		if (hasUnread && active) {
			int i = bl ? 0 : (int)(Math.max(0.0F, Math.max(MathHelper.sin((float)(10 + this.animTick) * 0.57F), MathHelper.cos((float)this.animTick * 0.35F))) * -6.0F);
			context.drawTexture(INVITATION_ICON, x + 10, y + 2 + i, 40.0F, 0.0F, 8, 8, 48, 16);
		}
	}

	private void renderLocal(DrawContext context) {
		String string = "LOCAL!";
		context.getMatrices().push();
		context.getMatrices().translate((float)(this.width / 2 - 25), 20.0F, 0.0F);
		context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-20.0F));
		context.getMatrices().scale(1.5F, 1.5F, 1.5F);
		context.drawText(this.textRenderer, "LOCAL!", 0, 0, 8388479, false);
		context.getMatrices().pop();
	}

	private void renderStage(DrawContext context) {
		String string = "STAGE!";
		context.getMatrices().push();
		context.getMatrices().translate((float)(this.width / 2 - 25), 20.0F, 0.0F);
		context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-20.0F));
		context.getMatrices().scale(1.5F, 1.5F, 1.5F);
		context.drawText(this.textRenderer, "STAGE!", 0, 0, -256, false);
		context.getMatrices().pop();
	}

	public RealmsMainScreen newScreen() {
		RealmsMainScreen realmsMainScreen = new RealmsMainScreen(this.parent);
		realmsMainScreen.init(this.client, this.width, this.height);
		return realmsMainScreen;
	}

	public static void loadImages(ResourceManager manager) {
		Collection<Identifier> collection = manager.findResources("textures/gui/images", filename -> filename.getPath().endsWith(".png")).keySet();
		IMAGES = collection.stream().filter(id -> id.getNamespace().equals("realms")).toList();
	}

	@Environment(EnvType.CLIENT)
	class CloseButton extends RealmsMainScreen.CrossButton {
		public CloseButton() {
			super(
				RealmsMainScreen.this.popupX0() + 4,
				RealmsMainScreen.this.popupY0() + 4,
				button -> RealmsMainScreen.this.onClosePopup(),
				Text.translatable("mco.selectServer.close")
			);
		}
	}

	@Environment(EnvType.CLIENT)
	static class CrossButton extends ButtonWidget {
		protected CrossButton(ButtonWidget.PressAction onPress, Text message) {
			this(0, 0, onPress, message);
		}

		protected CrossButton(int x, int y, ButtonWidget.PressAction onPress, Text message) {
			super(x, y, 14, 14, message, onPress, DEFAULT_NARRATION_SUPPLIER);
			this.setTooltip(Tooltip.of(message));
		}

		@Override
		public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
			float f = this.isSelected() ? 14.0F : 0.0F;
			context.drawTexture(RealmsMainScreen.CROSS_ICON, this.getX(), this.getY(), 0.0F, f, 14, 14, 14, 28);
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
	class NewsButton extends ButtonWidget {
		private static final int field_44515 = 20;

		public NewsButton() {
			super(RealmsMainScreen.this.width - 115, 12, 20, 20, Text.translatable("mco.news"), button -> {
				if (RealmsMainScreen.this.newsLink != null) {
					ConfirmLinkScreen.open(RealmsMainScreen.this.newsLink, RealmsMainScreen.this, true);
					if (RealmsMainScreen.this.hasUnreadNews) {
						RealmsPersistence.RealmsPersistenceData realmsPersistenceData = RealmsPersistence.readFile();
						realmsPersistenceData.hasUnreadNews = false;
						RealmsMainScreen.this.hasUnreadNews = false;
						RealmsPersistence.writeFile(realmsPersistenceData);
					}
				}
			}, DEFAULT_NARRATION_SUPPLIER);
		}

		@Override
		public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
			RealmsMainScreen.this.renderNews(context, mouseX, mouseY, RealmsMainScreen.this.hasUnreadNews, this.getX(), this.getY(), this.isSelected(), this.active);
		}
	}

	@Environment(EnvType.CLIENT)
	class PendingInvitesButton extends TexturedButtonWidget {
		private static final Text INVITES_TITLE = Text.translatable("mco.invites.title");
		private static final Tooltip NO_PENDING_TEXT = Tooltip.of(Text.translatable("mco.invites.nopending"));
		private static final Tooltip PENDING_TEXT = Tooltip.of(Text.translatable("mco.invites.pending"));
		private static final int field_44519 = 18;
		private static final int field_44520 = 15;
		private static final int field_44521 = 10;
		private static final int field_44522 = 8;
		private static final int field_44523 = 8;
		private static final int field_44524 = 11;

		public PendingInvitesButton() {
			super(
				RealmsMainScreen.this.width / 2 + 64 + 10,
				15,
				18,
				15,
				0,
				0,
				15,
				RealmsMainScreen.INVITE_ICON,
				18,
				30,
				button -> RealmsMainScreen.this.client.setScreen(new RealmsPendingInvitesScreen(RealmsMainScreen.this.parent, INVITES_TITLE)),
				INVITES_TITLE
			);
			this.setTooltip(NO_PENDING_TEXT);
		}

		public void updatePendingText() {
			this.setTooltip(RealmsMainScreen.this.pendingInvitesCount == 0 ? NO_PENDING_TEXT : PENDING_TEXT);
		}

		@Override
		public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
			super.renderButton(context, mouseX, mouseY, delta);
			this.render(context);
		}

		private void render(DrawContext context) {
			boolean bl = this.active && RealmsMainScreen.this.pendingInvitesCount != 0;
			if (bl) {
				int i = (Math.min(RealmsMainScreen.this.pendingInvitesCount, 6) - 1) * 8;
				int j = (int)(
					Math.max(
							0.0F, Math.max(MathHelper.sin((float)(10 + RealmsMainScreen.this.animTick) * 0.57F), MathHelper.cos((float)RealmsMainScreen.this.animTick * 0.35F))
						)
						* -6.0F
				);
				float f = this.isSelected() ? 8.0F : 0.0F;
				context.drawTexture(RealmsMainScreen.INVITATION_ICON, this.getX() + 11, this.getY() + j, (float)i, f, 8, 8, 48, 16);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class RealmSelectionList extends RealmsObjectSelectionList<RealmsMainScreen.Entry> {
		public RealmSelectionList() {
			super(RealmsMainScreen.this.width, RealmsMainScreen.this.height, 44, RealmsMainScreen.this.height - 64, 36);
		}

		public void setSelected(@Nullable RealmsMainScreen.Entry entry) {
			super.setSelected(entry);
			if (entry != null) {
				RealmsMainScreen.this.updateButtonStates(entry.getRealmsServer());
			} else {
				RealmsMainScreen.this.updateButtonStates(null);
			}
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

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (this.server.state == RealmsServer.State.UNINITIALIZED) {
				RealmsMainScreen.this.client.setScreen(new RealmsCreateRealmScreen(this.server, RealmsMainScreen.this));
			} else if (RealmsMainScreen.this.shouldPlayButtonBeActive(this.server)) {
				if (Util.getMeasuringTimeMs() - RealmsMainScreen.this.lastPlayButtonClickTime < 250L && this.isFocused()) {
					RealmsMainScreen.this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
					RealmsMainScreen.this.play(this.server, RealmsMainScreen.this);
				}

				RealmsMainScreen.this.lastPlayButtonClickTime = Util.getMeasuringTimeMs();
			}

			return true;
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (KeyCodes.isToggle(keyCode) && RealmsMainScreen.this.shouldPlayButtonBeActive(this.server)) {
				RealmsMainScreen.this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
				RealmsMainScreen.this.play(this.server, RealmsMainScreen.this);
				return true;
			} else {
				return super.keyPressed(keyCode, scanCode, modifiers);
			}
		}

		private void render(RealmsServer server, DrawContext context, int x, int y, int mouseX, int mouseY) {
			this.renderRealmsServerItem(server, context, x + 36, y, mouseX, mouseY);
		}

		private void renderRealmsServerItem(RealmsServer server, DrawContext context, int x, int y, int mouseX, int mouseY) {
			if (server.state == RealmsServer.State.UNINITIALIZED) {
				context.drawTexture(RealmsMainScreen.WORLD_ICON, x + 10, y + 6, 0.0F, 0.0F, 40, 20, 40, 20);
				float f = 0.5F + (1.0F + MathHelper.sin((float)RealmsMainScreen.this.animTick * 0.25F)) * 0.25F;
				int i = 0xFF000000 | (int)(127.0F * f) << 16 | (int)(255.0F * f) << 8 | (int)(127.0F * f);
				context.drawCenteredTextWithShadow(RealmsMainScreen.this.textRenderer, RealmsMainScreen.UNINITIALIZED_TEXT, x + 10 + 40 + 75, y + 12, i);
			} else {
				int j = 225;
				int i = 2;
				this.drawServerState(server, context, x, y, mouseX, mouseY, 225, 2);
				if (!"0".equals(server.serverPing.nrOfPlayers)) {
					String string = Formatting.GRAY + server.serverPing.nrOfPlayers;
					context.drawText(RealmsMainScreen.this.textRenderer, string, x + 207 - RealmsMainScreen.this.textRenderer.getWidth(string), y + 3, 8421504, false);
					if (mouseX >= x + 207 - RealmsMainScreen.this.textRenderer.getWidth(string)
						&& mouseX <= x + 207
						&& mouseY >= y + 1
						&& mouseY <= y + 10
						&& mouseY < RealmsMainScreen.this.height - 40
						&& mouseY > 32
						&& !RealmsMainScreen.this.shouldShowPopup()) {
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

				context.drawText(RealmsMainScreen.this.textRenderer, server.getName(), x + 2, y + 1, 16777215, false);
				RealmsUtil.drawPlayerHead(context, x - 36, y, 32, server.ownerUUID);
			}
		}

		private void drawServerState(RealmsServer server, DrawContext context, int x, int y, int mouseX, int mouseY, int xOffset, int yOffset) {
			int i = x + xOffset + 22;
			if (server.expired) {
				RealmsMainScreen.this.drawExpired(context, i, y + yOffset, mouseX, mouseY);
			} else if (server.state == RealmsServer.State.CLOSED) {
				RealmsMainScreen.this.drawClose(context, i, y + yOffset, mouseX, mouseY);
			} else if (RealmsMainScreen.this.isSelfOwnedServer(server) && server.daysLeft < 7) {
				RealmsMainScreen.this.drawExpiring(context, i, y + yOffset, mouseX, mouseY, server.daysLeft);
			} else if (server.state == RealmsServer.State.OPEN) {
				RealmsMainScreen.this.drawOpen(context, i, y + yOffset, mouseX, mouseY);
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
	class RealmSelectionListTrialEntry extends RealmsMainScreen.Entry {
		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.renderTrialItem(context, index, x, y, mouseX, mouseY);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			RealmsMainScreen.this.popupOpenedByUser = true;
			return true;
		}

		private void renderTrialItem(DrawContext context, int index, int x, int y, int mouseX, int mouseY) {
			int i = y + 8;
			int j = 0;
			boolean bl = false;
			if (x <= mouseX && mouseX <= (int)RealmsMainScreen.this.realmSelectionList.getScrollAmount() && y <= mouseY && mouseY <= y + 32) {
				bl = true;
			}

			int k = 8388479;
			if (bl && !RealmsMainScreen.this.shouldShowPopup()) {
				k = 6077788;
			}

			for (Text text : RealmsMainScreen.TRIAL_MESSAGE_LINES) {
				context.drawCenteredTextWithShadow(RealmsMainScreen.this.textRenderer, text, RealmsMainScreen.this.width / 2, i + j, k);
				j += 10;
			}
		}

		@Override
		public Text getNarration() {
			return RealmsMainScreen.TRIAL_NARRATION;
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
			this.grid.add(new IconWidget(20, 20, RealmsMainScreen.INFO_ICON), 0, 0, this.grid.copyPositioner().margin(7, 7, 0, 0));
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
