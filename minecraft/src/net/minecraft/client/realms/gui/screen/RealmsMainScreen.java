package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.realms.KeyCombo;
import net.minecraft.client.realms.Ping;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsNewsUpdater;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.realms.dto.PingResult;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerPlayerList;
import net.minecraft.client.realms.dto.RegionPingResult;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.task.RealmsGetServerDetailsTask;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.realms.util.RealmsPersistence;
import net.minecraft.client.realms.util.RealmsServerFilterer;
import net.minecraft.client.realms.util.RealmsTextureManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
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
	private static final Identifier LEAVE_ICON = new Identifier("realms", "textures/gui/realms/leave_icon.png");
	private static final Identifier INVITATION_ICON = new Identifier("realms", "textures/gui/realms/invitation_icons.png");
	private static final Identifier INVITE_ICON = new Identifier("realms", "textures/gui/realms/invite_icon.png");
	static final Identifier WORLD_ICON = new Identifier("realms", "textures/gui/realms/world_icon.png");
	private static final Identifier REALMS = new Identifier("realms", "textures/gui/title/realms.png");
	private static final Identifier CONFIGURE_ICON = new Identifier("realms", "textures/gui/realms/configure_icon.png");
	private static final Identifier NEWS_ICON = new Identifier("realms", "textures/gui/realms/news_icon.png");
	private static final Identifier POPUP = new Identifier("realms", "textures/gui/realms/popup.png");
	private static final Identifier DARKEN = new Identifier("realms", "textures/gui/realms/darken.png");
	static final Identifier CROSS_ICON = new Identifier("realms", "textures/gui/realms/cross_icon.png");
	private static final Identifier TRIAL_ICON = new Identifier("realms", "textures/gui/realms/trial_icon.png");
	static final Identifier WIDGETS = new Identifier("minecraft", "textures/gui/widgets.png");
	static final Text NO_PENDING_TEXT = Text.translatable("mco.invites.nopending");
	static final Text PENDING_TEXT = Text.translatable("mco.invites.pending");
	static final List<Text> TRIAL_MESSAGE_LINES = ImmutableList.of(Text.translatable("mco.trial.message.line1"), Text.translatable("mco.trial.message.line2"));
	static final Text UNINITIALIZED_TEXT = Text.translatable("mco.selectServer.uninitialized");
	static final Text EXPIRED_LIST_TEXT = Text.translatable("mco.selectServer.expiredList");
	static final Text EXPIRED_RENEW_TEXT = Text.translatable("mco.selectServer.expiredRenew");
	static final Text EXPIRED_TRIAL_TEXT = Text.translatable("mco.selectServer.expiredTrial");
	static final Text EXPIRED_SUBSCRIBE_TEXT = Text.translatable("mco.selectServer.expiredSubscribe");
	static final Text MINIGAME_TEXT = Text.translatable("mco.selectServer.minigame").append(ScreenTexts.SPACE);
	private static final Text POPUP_TEXT = Text.translatable("mco.selectServer.popup");
	private static final Text EXPIRED_TEXT = Text.translatable("mco.selectServer.expired");
	private static final Text EXPIRES_SOON_TEXT = Text.translatable("mco.selectServer.expires.soon");
	private static final Text EXPIRES_IN_A_DAY_TEXT = Text.translatable("mco.selectServer.expires.day");
	private static final Text OPEN_TEXT = Text.translatable("mco.selectServer.open");
	private static final Text CLOSED_TEXT = Text.translatable("mco.selectServer.closed");
	private static final Text LEAVE_TEXT = Text.translatable("mco.selectServer.leave");
	private static final Text CONFIGURE_TEXT = Text.translatable("mco.selectServer.configureRealm");
	private static final Text NEWS_TEXT = Text.translatable("mco.news");
	static final Text UNINITIALIZED_BUTTON_NARRATION = Text.translatable("gui.narrate.button", UNINITIALIZED_TEXT);
	static final Text TRIAL_NARRATION = ScreenTexts.joinLines(TRIAL_MESSAGE_LINES);
	private static List<Identifier> IMAGES = ImmutableList.of();
	@Nullable
	private PeriodicRunnerFactory.RunnersManager periodicRunnersManager;
	private RealmsServerFilterer serverFilterer;
	static boolean overrideConfigure;
	private static int lastScrollYPosition = -1;
	static volatile boolean hasParentalConsent;
	static volatile boolean checkedParentalConsent;
	static volatile boolean checkedClientCompatibility;
	@Nullable
	static Screen realmsGenericErrorScreen;
	private static boolean regionsPinged;
	private final RateLimiter rateLimiter;
	private boolean dontSetConnectedToRealms;
	final Screen lastScreen;
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
	RealmsMainScreen.HoverState hoverState;
	private ButtonWidget showPopupButton;
	private RealmsMainScreen.PendingInvitesButton pendingInvitesButton;
	private ButtonWidget newsButton;
	private ButtonWidget createTrialButton;
	private ButtonWidget buyARealmButton;
	private ButtonWidget closeButton;

	public RealmsMainScreen(Screen lastScreen) {
		super(NarratorManager.EMPTY);
		this.lastScreen = lastScreen;
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
			this.addInvitesAndNewsButtons();
			this.realmSelectionList = new RealmsMainScreen.RealmSelectionList();
			if (lastScrollYPosition != -1) {
				this.realmSelectionList.setScrollAmount((double)lastScrollYPosition);
			}

			this.addSelectableChild(this.realmSelectionList);
			this.hasSelectionList = true;
			this.setInitialFocus(this.realmSelectionList);
			this.addPurchaseButtons();
			this.addPlayButtons();
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
				.dimensions(this.width - 90, 6, 80, 20)
				.build()
		);
	}

	public void addPurchaseButtons() {
		this.createTrialButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.selectServer.trial"), button -> {
			if (this.trialAvailable && !this.createdTrial) {
				Util.getOperatingSystem().open("https://aka.ms/startjavarealmstrial");
				this.client.setScreen(this.lastScreen);
			}
		}).dimensions(this.width / 2 + 52, this.popupY0() + 137 - 20, 98, 20).build());
		this.buyARealmButton = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("mco.selectServer.buy"), button -> Util.getOperatingSystem().open("https://aka.ms/BuyJavaRealms"))
				.dimensions(this.width / 2 + 52, this.popupY0() + 160 - 20, 98, 20)
				.build()
		);
		this.closeButton = this.addDrawableChild(new RealmsMainScreen.CloseButton());
	}

	public void addPlayButtons() {
		this.configureButton = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("mco.selectServer.configure"), button -> this.configureClicked(this.findServer()))
				.dimensions(this.width / 2 - 190, this.height - 32, 90, 20)
				.build()
		);
		this.leaveButton = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("mco.selectServer.leave"), button -> this.leaveClicked(this.findServer()))
				.dimensions(this.width / 2 - 190, this.height - 32, 90, 20)
				.build()
		);
		this.playButton = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("mco.selectServer.play"), button -> this.play(this.findServer(), this))
				.dimensions(this.width / 2 - 93, this.height - 32, 90, 20)
				.build()
		);
		this.backButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> {
			if (!this.justClosedPopup) {
				this.client.setScreen(this.lastScreen);
			}
		}).dimensions(this.width / 2 + 4, this.height - 32, 90, 20).build());
		this.renewButton = this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("mco.selectServer.expiredRenew"), button -> this.onRenew(this.findServer()))
				.dimensions(this.width / 2 + 100, this.height - 32, 90, 20)
				.build()
		);
	}

	void updateButtonStates(@Nullable RealmsServer server) {
		this.backButton.active = true;
		if (hasParentalConsent() && this.hasFetchedServers) {
			this.playButton.visible = true;
			this.playButton.active = this.shouldPlayButtonBeActive(server) && !this.shouldShowPopup();
			this.renewButton.visible = this.shouldRenewButtonBeActive(server);
			this.configureButton.visible = this.shouldConfigureButtonBeVisible(server);
			this.leaveButton.visible = this.shouldLeaveButtonBeVisible(server);
			boolean bl = this.shouldShowPopup() && this.trialAvailable && !this.createdTrial;
			this.createTrialButton.visible = bl;
			this.createTrialButton.active = bl;
			this.buyARealmButton.visible = this.shouldShowPopup();
			this.closeButton.visible = this.shouldShowPopup() && this.popupOpenedByUser;
			this.renewButton.active = !this.shouldShowPopup();
			this.configureButton.active = !this.shouldShowPopup();
			this.leaveButton.active = !this.shouldShowPopup();
			this.newsButton.active = true;
			this.newsButton.visible = this.newsLink != null;
			this.pendingInvitesButton.active = true;
			this.pendingInvitesButton.visible = true;
			this.showPopupButton.active = !this.shouldShowPopup();
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

	private boolean shouldConfigureButtonBeVisible(@Nullable RealmsServer server) {
		return server != null && this.isSelfOwnedServer(server);
	}

	private boolean shouldLeaveButtonBeVisible(@Nullable RealmsServer server) {
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
			RealmsServer realmsServer = this.findServer();
			RealmsMainScreen.Entry entry = null;
			this.realmSelectionList.clear();
			boolean bl = !this.hasFetchedServers;
			if (bl) {
				this.hasFetchedServers = true;
			}

			boolean bl2 = false;

			for (RealmsServer realmsServer2 : list) {
				if (this.isOwnedNotExpired(realmsServer2)) {
					bl2 = true;
				}
			}

			this.realmsServers = list;
			if (this.shouldShowMessageInList()) {
				this.realmSelectionList.addEntry(new RealmsMainScreen.RealmSelectionListTrialEntry());
			}

			for (RealmsServer realmsServer2x : this.realmsServers) {
				RealmsMainScreen.RealmSelectionListEntry realmSelectionListEntry = new RealmsMainScreen.RealmSelectionListEntry(realmsServer2x);
				this.realmSelectionList.addEntry(realmSelectionListEntry);
				if (realmsServer != null && realmsServer.id == realmsServer2x.id) {
					entry = realmSelectionListEntry;
				}
			}

			if (!regionsPinged && bl2) {
				regionsPinged = true;
				this.pingRegions();
			}

			if (bl) {
				this.updateButtonStates(null);
			} else {
				this.realmSelectionList.setSelected(entry);
			}
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

	void onRenew(@Nullable RealmsServer realmsServer) {
		if (realmsServer != null) {
			String string = "https://aka.ms/ExtendJavaRealms?subscriptionId="
				+ realmsServer.remoteSubscriptionId
				+ "&profileId="
				+ this.client.getSession().getUuid()
				+ "&ref="
				+ (realmsServer.expiredTrial ? "expiredTrial" : "expiredRealm");
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
								RealmsMainScreen.realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen);
								RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(RealmsMainScreen.realmsGenericErrorScreen));
								return;
							}

							RealmsMainScreen.this.checkParentalConsent();
						} catch (RealmsServiceException var3) {
							RealmsMainScreen.checkedClientCompatibility = false;
							RealmsMainScreen.LOGGER.error("Couldn't connect to realms", (Throwable)var3);
							if (var3.httpResultCode == 401) {
								RealmsMainScreen.realmsGenericErrorScreen = new RealmsGenericErrorScreen(
									Text.translatable("mco.error.invalid.session.title"), Text.translatable("mco.error.invalid.session.message"), RealmsMainScreen.this.lastScreen
								);
								RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(RealmsMainScreen.realmsGenericErrorScreen));
							} else {
								RealmsMainScreen.this.client
									.execute(() -> RealmsMainScreen.this.client.setScreen(new RealmsGenericErrorScreen(var3, RealmsMainScreen.this.lastScreen)));
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
						RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(new RealmsParentalConsentScreen(RealmsMainScreen.this.lastScreen)));
					}

					RealmsMainScreen.checkedParentalConsent = true;
				} catch (RealmsServiceException var3) {
					RealmsMainScreen.LOGGER.error("Couldn't connect to realms", (Throwable)var3);
					RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(new RealmsGenericErrorScreen(var3, RealmsMainScreen.this.lastScreen)));
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

	void configureClicked(@Nullable RealmsServer serverData) {
		if (serverData != null && (this.client.getSession().getUuid().equals(serverData.ownerUUID) || overrideConfigure)) {
			this.saveListScrollPosition();
			this.client.setScreen(new RealmsConfigureWorldScreen(this, serverData.id));
		}
	}

	void leaveClicked(@Nullable RealmsServer selectedServer) {
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
			this.client.setScreen(this.lastScreen);
		}
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		this.keyCombos.forEach(keyCombo -> keyCombo.keyPressed(chr));
		return true;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.hoverState = RealmsMainScreen.HoverState.NONE;
		this.renderBackground(matrices);
		this.realmSelectionList.render(matrices, mouseX, mouseY, delta);
		this.drawRealmsLogo(matrices, this.width / 2 - 50, 7);
		if (RealmsClient.currentEnvironment == RealmsClient.Environment.STAGE) {
			this.renderStage(matrices);
		}

		if (RealmsClient.currentEnvironment == RealmsClient.Environment.LOCAL) {
			this.renderLocal(matrices);
		}

		if (this.shouldShowPopup()) {
			this.drawPopup(matrices);
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

		super.render(matrices, mouseX, mouseY, delta);
		if (this.trialAvailable && !this.createdTrial && this.shouldShowPopup()) {
			RenderSystem.setShaderTexture(0, TRIAL_ICON);
			int i = 8;
			int j = 8;
			int k = 0;
			if ((Util.getMeasuringTimeMs() / 800L & 1L) == 1L) {
				k = 8;
			}

			DrawableHelper.drawTexture(
				matrices,
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

	private void drawRealmsLogo(MatrixStack matrices, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderTexture(0, REALMS);
		matrices.push();
		matrices.scale(0.5F, 0.5F, 0.5F);
		DrawableHelper.drawTexture(matrices, x * 2, y * 2 - 5, 0.0F, 0.0F, 200, 50, 200, 50);
		matrices.pop();
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

	private void drawPopup(MatrixStack matrices) {
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

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.7F);
		RenderSystem.enableBlend();
		RenderSystem.setShaderTexture(0, DARKEN);
		int k = 0;
		int l = 32;
		DrawableHelper.drawTexture(matrices, 0, 32, 0.0F, 0.0F, this.width, this.height - 40 - 32, 310, 166);
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, POPUP);
		DrawableHelper.drawTexture(matrices, i, j, 0.0F, 0.0F, 310, 166, 310, 166);
		if (!IMAGES.isEmpty()) {
			RenderSystem.setShaderTexture(0, (Identifier)IMAGES.get(this.carouselIndex));
			DrawableHelper.drawTexture(matrices, i + 7, j + 7, 0.0F, 0.0F, 195, 152, 195, 152);
			if (this.carouselTick % 95 < 5) {
				if (!this.hasSwitchedCarouselImage) {
					this.carouselIndex = (this.carouselIndex + 1) % IMAGES.size();
					this.hasSwitchedCarouselImage = true;
				}
			} else {
				this.hasSwitchedCarouselImage = false;
			}
		}

		this.popupText.draw(matrices, this.width / 2 + 52, j + 7, 10, 5000268);
	}

	int popupX0() {
		return (this.width - 310) / 2;
	}

	int popupY0() {
		return this.height / 2 - 80;
	}

	void drawInvitationPendingIcon(MatrixStack matrices, int mouseX, int mouseY, int x, int y, boolean hovered, boolean active) {
		int i = this.pendingInvitesCount;
		boolean bl = this.inPendingInvitationArea((double)mouseX, (double)mouseY);
		boolean bl2 = active && hovered;
		if (bl2) {
			float f = 0.25F + (1.0F + MathHelper.sin((float)this.animTick * 0.5F)) * 0.25F;
			int j = 0xFF000000 | (int)(f * 64.0F) << 16 | (int)(f * 64.0F) << 8 | (int)(f * 64.0F) << 0;
			this.fillGradient(matrices, x - 2, y - 2, x + 18, y + 18, j, j);
			j = 0xFF000000 | (int)(f * 255.0F) << 16 | (int)(f * 255.0F) << 8 | (int)(f * 255.0F) << 0;
			this.fillGradient(matrices, x - 2, y - 2, x + 18, y - 1, j, j);
			this.fillGradient(matrices, x - 2, y - 2, x - 1, y + 18, j, j);
			this.fillGradient(matrices, x + 17, y - 2, x + 18, y + 18, j, j);
			this.fillGradient(matrices, x - 2, y + 17, x + 18, y + 18, j, j);
		}

		RenderSystem.setShaderTexture(0, INVITE_ICON);
		boolean bl3 = active && hovered;
		float g = bl3 ? 16.0F : 0.0F;
		DrawableHelper.drawTexture(matrices, x, y - 6, g, 0.0F, 15, 25, 31, 25);
		boolean bl4 = active && i != 0;
		if (bl4) {
			int k = (Math.min(i, 6) - 1) * 8;
			int l = (int)(Math.max(0.0F, Math.max(MathHelper.sin((float)(10 + this.animTick) * 0.57F), MathHelper.cos((float)this.animTick * 0.35F))) * -6.0F);
			RenderSystem.setShaderTexture(0, INVITATION_ICON);
			float h = bl ? 8.0F : 0.0F;
			DrawableHelper.drawTexture(matrices, x + 4, y + 4 + l, (float)k, h, 8, 8, 48, 16);
		}

		int k = mouseX + 12;
		boolean bl5 = active && bl;
		if (bl5) {
			Text text = i == 0 ? NO_PENDING_TEXT : PENDING_TEXT;
			int m = this.textRenderer.getWidth(text);
			this.fillGradient(matrices, k - 3, mouseY - 3, k + m + 3, mouseY + 8 + 3, -1073741824, -1073741824);
			this.textRenderer.drawWithShadow(matrices, text, (float)k, (float)mouseY, -1);
		}
	}

	private boolean inPendingInvitationArea(double xm, double ym) {
		int i = this.width / 2 + 50;
		int j = this.width / 2 + 66;
		int k = 11;
		int l = 23;
		if (this.pendingInvitesCount != 0) {
			i -= 3;
			j += 3;
			k -= 5;
			l += 5;
		}

		return (double)i <= xm && xm <= (double)j && (double)k <= ym && ym <= (double)l;
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

	void drawExpired(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, EXPIRED_ICON);
		DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, 10, 28, 10, 28);
		if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27 && mouseY < this.height - 40 && mouseY > 32 && !this.shouldShowPopup()) {
			this.setTooltip(EXPIRED_TEXT);
		}
	}

	void drawExpiring(MatrixStack matrices, int x, int y, int mouseX, int mouseY, int remainingDays) {
		RenderSystem.setShaderTexture(0, EXPIRES_SOON_ICON);
		if (this.animTick % 20 < 10) {
			DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, 10, 28, 20, 28);
		} else {
			DrawableHelper.drawTexture(matrices, x, y, 10.0F, 0.0F, 10, 28, 20, 28);
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

	void drawOpen(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, ON_ICON);
		DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, 10, 28, 10, 28);
		if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27 && mouseY < this.height - 40 && mouseY > 32 && !this.shouldShowPopup()) {
			this.setTooltip(OPEN_TEXT);
		}
	}

	void drawClose(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, OFF_ICON);
		DrawableHelper.drawTexture(matrices, x, y, 0.0F, 0.0F, 10, 28, 10, 28);
		if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27 && mouseY < this.height - 40 && mouseY > 32 && !this.shouldShowPopup()) {
			this.setTooltip(CLOSED_TEXT);
		}
	}

	void drawLeave(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		boolean bl = false;
		if (mouseX >= x && mouseX <= x + 28 && mouseY >= y && mouseY <= y + 28 && mouseY < this.height - 40 && mouseY > 32 && !this.shouldShowPopup()) {
			bl = true;
		}

		RenderSystem.setShaderTexture(0, LEAVE_ICON);
		float f = bl ? 28.0F : 0.0F;
		DrawableHelper.drawTexture(matrices, x, y, f, 0.0F, 28, 28, 56, 28);
		if (bl) {
			this.setTooltip(LEAVE_TEXT);
			this.hoverState = RealmsMainScreen.HoverState.LEAVE;
		}
	}

	void drawConfigure(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		boolean bl = false;
		if (mouseX >= x && mouseX <= x + 28 && mouseY >= y && mouseY <= y + 28 && mouseY < this.height - 40 && mouseY > 32 && !this.shouldShowPopup()) {
			bl = true;
		}

		RenderSystem.setShaderTexture(0, CONFIGURE_ICON);
		float f = bl ? 28.0F : 0.0F;
		DrawableHelper.drawTexture(matrices, x, y, f, 0.0F, 28, 28, 56, 28);
		if (bl) {
			this.setTooltip(CONFIGURE_TEXT);
			this.hoverState = RealmsMainScreen.HoverState.CONFIGURE;
		}
	}

	void renderNews(MatrixStack matrices, int mouseX, int mouseY, boolean hasUnread, int x, int y, boolean hovered, boolean active) {
		boolean bl = false;
		if (mouseX >= x && mouseX <= x + 20 && mouseY >= y && mouseY <= y + 20) {
			bl = true;
		}

		RenderSystem.setShaderTexture(0, NEWS_ICON);
		if (!active) {
			RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 1.0F);
		}

		boolean bl2 = active && hovered;
		float f = bl2 ? 20.0F : 0.0F;
		DrawableHelper.drawTexture(matrices, x, y, f, 0.0F, 20, 20, 40, 20);
		if (bl && active) {
			this.setTooltip(NEWS_TEXT);
		}

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		if (hasUnread && active) {
			int i = bl ? 0 : (int)(Math.max(0.0F, Math.max(MathHelper.sin((float)(10 + this.animTick) * 0.57F), MathHelper.cos((float)this.animTick * 0.35F))) * -6.0F);
			RenderSystem.setShaderTexture(0, INVITATION_ICON);
			DrawableHelper.drawTexture(matrices, x + 10, y + 2 + i, 40.0F, 0.0F, 8, 8, 48, 16);
		}
	}

	private void renderLocal(MatrixStack matrices) {
		String string = "LOCAL!";
		matrices.push();
		matrices.translate((float)(this.width / 2 - 25), 20.0F, 0.0F);
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-20.0F));
		matrices.scale(1.5F, 1.5F, 1.5F);
		this.textRenderer.draw(matrices, "LOCAL!", 0.0F, 0.0F, 8388479);
		matrices.pop();
	}

	private void renderStage(MatrixStack matrices) {
		String string = "STAGE!";
		matrices.push();
		matrices.translate((float)(this.width / 2 - 25), 20.0F, 0.0F);
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-20.0F));
		matrices.scale(1.5F, 1.5F, 1.5F);
		this.textRenderer.draw(matrices, "STAGE!", 0.0F, 0.0F, -256);
		matrices.pop();
	}

	public RealmsMainScreen newScreen() {
		RealmsMainScreen realmsMainScreen = new RealmsMainScreen(this.lastScreen);
		realmsMainScreen.init(this.client, this.width, this.height);
		return realmsMainScreen;
	}

	public static void loadImages(ResourceManager manager) {
		Collection<Identifier> collection = manager.findResources("textures/gui/images", filename -> filename.getPath().endsWith(".png")).keySet();
		IMAGES = collection.stream().filter(id -> id.getNamespace().equals("realms")).toList();
	}

	private void openPendingInvitesScreen(ButtonWidget button) {
		this.client.setScreen(new RealmsPendingInvitesScreen(this.lastScreen));
	}

	@Environment(EnvType.CLIENT)
	class CloseButton extends ButtonWidget {
		public CloseButton() {
			super(
				RealmsMainScreen.this.popupX0() + 4,
				RealmsMainScreen.this.popupY0() + 4,
				12,
				12,
				Text.translatable("mco.selectServer.close"),
				button -> RealmsMainScreen.this.onClosePopup(),
				DEFAULT_NARRATION_SUPPLIER
			);
		}

		@Override
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			RenderSystem.setShaderTexture(0, RealmsMainScreen.CROSS_ICON);
			float f = this.isHovered() ? 12.0F : 0.0F;
			drawTexture(matrices, this.getX(), this.getY(), 0.0F, f, 12, 12, 12, 24);
			if (this.isMouseOver((double)mouseX, (double)mouseY)) {
				RealmsMainScreen.this.setTooltip(this.getMessage());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	abstract class Entry extends AlwaysSelectedEntryListWidget.Entry<RealmsMainScreen.Entry> {
		@Nullable
		public abstract RealmsServer getRealmsServer();
	}

	@Environment(EnvType.CLIENT)
	static enum HoverState {
		NONE,
		EXPIRED,
		LEAVE,
		CONFIGURE;
	}

	@Environment(EnvType.CLIENT)
	class NewsButton extends ButtonWidget {
		public NewsButton() {
			super(RealmsMainScreen.this.width - 115, 6, 20, 20, Text.translatable("mco.news"), button -> {
				if (RealmsMainScreen.this.newsLink != null) {
					RealmsMainScreen.this.client.setScreen(new ConfirmLinkScreen(confirmed -> {
						if (confirmed) {
							Util.getOperatingSystem().open(RealmsMainScreen.this.newsLink);
						}

						RealmsMainScreen.this.client.setScreen(RealmsMainScreen.this);
					}, RealmsMainScreen.this.newsLink, true));
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
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			RealmsMainScreen.this.renderNews(matrices, mouseX, mouseY, RealmsMainScreen.this.hasUnreadNews, this.getX(), this.getY(), this.isHovered(), this.active);
		}
	}

	@Environment(EnvType.CLIENT)
	class PendingInvitesButton extends ButtonWidget {
		public PendingInvitesButton() {
			super(RealmsMainScreen.this.width / 2 + 47, 6, 22, 22, ScreenTexts.EMPTY, RealmsMainScreen.this::openPendingInvitesScreen, DEFAULT_NARRATION_SUPPLIER);
		}

		public void updatePendingText() {
			this.setMessage(RealmsMainScreen.this.pendingInvitesCount == 0 ? RealmsMainScreen.NO_PENDING_TEXT : RealmsMainScreen.PENDING_TEXT);
		}

		@Override
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			RealmsMainScreen.this.drawInvitationPendingIcon(matrices, mouseX, mouseY, this.getX(), this.getY(), this.isHovered(), this.active);
		}
	}

	@Environment(EnvType.CLIENT)
	class RealmSelectionList extends RealmsObjectSelectionList<RealmsMainScreen.Entry> {
		public RealmSelectionList() {
			super(RealmsMainScreen.this.width, RealmsMainScreen.this.height, 32, RealmsMainScreen.this.height - 40, 36);
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_SPACE && keyCode != GLFW.GLFW_KEY_KP_ENTER) {
				return super.keyPressed(keyCode, scanCode, modifiers);
			} else {
				RealmsMainScreen.Entry entry = this.getSelectedOrNull();
				return entry == null ? super.keyPressed(keyCode, scanCode, modifiers) : entry.mouseClicked(0.0, 0.0, 0);
			}
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (button == 0 && mouseX < (double)this.getScrollbarPositionX() && mouseY >= (double)this.top && mouseY <= (double)this.bottom) {
				int i = RealmsMainScreen.this.realmSelectionList.getRowLeft();
				int j = this.getScrollbarPositionX();
				int k = (int)Math.floor(mouseY - (double)this.top) - this.headerHeight + (int)this.getScrollAmount() - 4;
				int l = k / this.itemHeight;
				if (mouseX >= (double)i && mouseX <= (double)j && l >= 0 && k >= 0 && l < this.getEntryCount()) {
					this.itemClicked(k, l, mouseX, mouseY, this.width);
					this.setSelected(l);
				}

				return true;
			} else {
				return super.mouseClicked(mouseX, mouseY, button);
			}
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
		public void itemClicked(int cursorY, int selectionIndex, double mouseX, double mouseY, int listWidth) {
			RealmsMainScreen.Entry entry = this.getEntry(selectionIndex);
			if (entry instanceof RealmsMainScreen.RealmSelectionListTrialEntry) {
				RealmsMainScreen.this.popupOpenedByUser = true;
			} else {
				RealmsServer realmsServer = entry.getRealmsServer();
				if (realmsServer != null) {
					if (realmsServer.state == RealmsServer.State.UNINITIALIZED) {
						MinecraftClient.getInstance().setScreen(new RealmsCreateRealmScreen(realmsServer, RealmsMainScreen.this));
					} else {
						if (RealmsMainScreen.this.hoverState == RealmsMainScreen.HoverState.CONFIGURE) {
							RealmsMainScreen.this.configureClicked(realmsServer);
						} else if (RealmsMainScreen.this.hoverState == RealmsMainScreen.HoverState.LEAVE) {
							RealmsMainScreen.this.leaveClicked(realmsServer);
						} else if (RealmsMainScreen.this.hoverState == RealmsMainScreen.HoverState.EXPIRED) {
							RealmsMainScreen.this.onRenew(realmsServer);
						} else if (RealmsMainScreen.this.shouldPlayButtonBeActive(realmsServer)) {
							if (Util.getMeasuringTimeMs() - RealmsMainScreen.this.lastPlayButtonClickTime < 250L && this.isSelectedEntry(selectionIndex)) {
								RealmsMainScreen.this.play(realmsServer, RealmsMainScreen.this);
							}

							RealmsMainScreen.this.lastPlayButtonClickTime = Util.getMeasuringTimeMs();
						}
					}
				}
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
		private final RealmsServer mServerData;

		public RealmSelectionListEntry(RealmsServer serverData) {
			this.mServerData = serverData;
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.render(this.mServerData, matrices, x, y, mouseX, mouseY);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (this.mServerData.state == RealmsServer.State.UNINITIALIZED) {
				RealmsMainScreen.this.client.setScreen(new RealmsCreateRealmScreen(this.mServerData, RealmsMainScreen.this));
			}

			return true;
		}

		private void render(RealmsServer serverData, MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
			this.renderMcoServerItem(serverData, matrices, x + 36, y, mouseX, mouseY);
		}

		private void renderMcoServerItem(RealmsServer serverData, MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
			if (serverData.state == RealmsServer.State.UNINITIALIZED) {
				RenderSystem.setShaderTexture(0, RealmsMainScreen.WORLD_ICON);
				DrawableHelper.drawTexture(matrices, x + 10, y + 6, 0.0F, 0.0F, 40, 20, 40, 20);
				float f = 0.5F + (1.0F + MathHelper.sin((float)RealmsMainScreen.this.animTick * 0.25F)) * 0.25F;
				int i = 0xFF000000 | (int)(127.0F * f) << 16 | (int)(255.0F * f) << 8 | (int)(127.0F * f);
				DrawableHelper.drawCenteredText(matrices, RealmsMainScreen.this.textRenderer, RealmsMainScreen.UNINITIALIZED_TEXT, x + 10 + 40 + 75, y + 12, i);
			} else {
				int j = 225;
				int i = 2;
				if (serverData.expired) {
					RealmsMainScreen.this.drawExpired(matrices, x + 225 - 14, y + 2, mouseX, mouseY);
				} else if (serverData.state == RealmsServer.State.CLOSED) {
					RealmsMainScreen.this.drawClose(matrices, x + 225 - 14, y + 2, mouseX, mouseY);
				} else if (RealmsMainScreen.this.isSelfOwnedServer(serverData) && serverData.daysLeft < 7) {
					RealmsMainScreen.this.drawExpiring(matrices, x + 225 - 14, y + 2, mouseX, mouseY, serverData.daysLeft);
				} else if (serverData.state == RealmsServer.State.OPEN) {
					RealmsMainScreen.this.drawOpen(matrices, x + 225 - 14, y + 2, mouseX, mouseY);
				}

				if (!RealmsMainScreen.this.isSelfOwnedServer(serverData) && !RealmsMainScreen.overrideConfigure) {
					RealmsMainScreen.this.drawLeave(matrices, x + 225, y + 2, mouseX, mouseY);
				} else {
					RealmsMainScreen.this.drawConfigure(matrices, x + 225, y + 2, mouseX, mouseY);
				}

				if (!"0".equals(serverData.serverPing.nrOfPlayers)) {
					String string = Formatting.GRAY + serverData.serverPing.nrOfPlayers;
					RealmsMainScreen.this.textRenderer.draw(matrices, string, (float)(x + 207 - RealmsMainScreen.this.textRenderer.getWidth(string)), (float)(y + 3), 8421504);
					if (mouseX >= x + 207 - RealmsMainScreen.this.textRenderer.getWidth(string)
						&& mouseX <= x + 207
						&& mouseY >= y + 1
						&& mouseY <= y + 10
						&& mouseY < RealmsMainScreen.this.height - 40
						&& mouseY > 32
						&& !RealmsMainScreen.this.shouldShowPopup()) {
						RealmsMainScreen.this.setTooltip(Text.literal(serverData.serverPing.playerList));
					}
				}

				if (RealmsMainScreen.this.isSelfOwnedServer(serverData) && serverData.expired) {
					RenderSystem.enableBlend();
					RenderSystem.setShaderTexture(0, RealmsMainScreen.WIDGETS);
					RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
					Text text;
					Text text2;
					if (serverData.expiredTrial) {
						text = RealmsMainScreen.EXPIRED_TRIAL_TEXT;
						text2 = RealmsMainScreen.EXPIRED_SUBSCRIBE_TEXT;
					} else {
						text = RealmsMainScreen.EXPIRED_LIST_TEXT;
						text2 = RealmsMainScreen.EXPIRED_RENEW_TEXT;
					}

					int k = RealmsMainScreen.this.textRenderer.getWidth(text2) + 17;
					int l = 16;
					int m = x + RealmsMainScreen.this.textRenderer.getWidth(text) + 8;
					int n = y + 13;
					boolean bl = false;
					if (mouseX >= m
						&& mouseX < m + k
						&& mouseY > n
						&& mouseY <= n + 16
						&& mouseY < RealmsMainScreen.this.height - 40
						&& mouseY > 32
						&& !RealmsMainScreen.this.shouldShowPopup()) {
						bl = true;
						RealmsMainScreen.this.hoverState = RealmsMainScreen.HoverState.EXPIRED;
					}

					int o = bl ? 2 : 1;
					DrawableHelper.drawTexture(matrices, m, n, 0.0F, (float)(46 + o * 20), k / 2, 8, 256, 256);
					DrawableHelper.drawTexture(matrices, m + k / 2, n, (float)(200 - k / 2), (float)(46 + o * 20), k / 2, 8, 256, 256);
					DrawableHelper.drawTexture(matrices, m, n + 8, 0.0F, (float)(46 + o * 20 + 12), k / 2, 8, 256, 256);
					DrawableHelper.drawTexture(matrices, m + k / 2, n + 8, (float)(200 - k / 2), (float)(46 + o * 20 + 12), k / 2, 8, 256, 256);
					RenderSystem.disableBlend();
					int p = y + 11 + 5;
					int q = bl ? 16777120 : 16777215;
					RealmsMainScreen.this.textRenderer.draw(matrices, text, (float)(x + 2), (float)(p + 1), 15553363);
					DrawableHelper.drawCenteredText(matrices, RealmsMainScreen.this.textRenderer, text2, m + k / 2, p + 1, q);
				} else {
					if (serverData.worldType == RealmsServer.WorldType.MINIGAME) {
						int r = 13413468;
						int s = RealmsMainScreen.this.textRenderer.getWidth(RealmsMainScreen.MINIGAME_TEXT);
						RealmsMainScreen.this.textRenderer.draw(matrices, RealmsMainScreen.MINIGAME_TEXT, (float)(x + 2), (float)(y + 12), 13413468);
						RealmsMainScreen.this.textRenderer.draw(matrices, serverData.getMinigameName(), (float)(x + 2 + s), (float)(y + 12), 7105644);
					} else {
						RealmsMainScreen.this.textRenderer.draw(matrices, serverData.getDescription(), (float)(x + 2), (float)(y + 12), 7105644);
					}

					if (!RealmsMainScreen.this.isSelfOwnedServer(serverData)) {
						RealmsMainScreen.this.textRenderer.draw(matrices, serverData.owner, (float)(x + 2), (float)(y + 12 + 11), 5000268);
					}
				}

				RealmsMainScreen.this.textRenderer.draw(matrices, serverData.getName(), (float)(x + 2), (float)(y + 1), 16777215);
				RealmsTextureManager.withBoundFace(serverData.ownerUUID, () -> PlayerSkinDrawer.draw(matrices, x - 36, y, 32));
			}
		}

		@Override
		public Text getNarration() {
			return (Text)(this.mServerData.state == RealmsServer.State.UNINITIALIZED
				? RealmsMainScreen.UNINITIALIZED_BUTTON_NARRATION
				: Text.translatable("narrator.select", this.mServerData.name));
		}

		@Nullable
		@Override
		public RealmsServer getRealmsServer() {
			return this.mServerData;
		}
	}

	@Environment(EnvType.CLIENT)
	class RealmSelectionListTrialEntry extends RealmsMainScreen.Entry {
		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.renderTrialItem(matrices, index, x, y, mouseX, mouseY);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			RealmsMainScreen.this.popupOpenedByUser = true;
			return true;
		}

		private void renderTrialItem(MatrixStack matrices, int index, int x, int y, int mouseX, int mouseY) {
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
				DrawableHelper.drawCenteredText(matrices, RealmsMainScreen.this.textRenderer, text, RealmsMainScreen.this.width / 2, i + j, k);
				j += 10;
			}
		}

		@Override
		public Text getNarration() {
			return RealmsMainScreen.TRIAL_NARRATION;
		}

		@Nullable
		@Override
		public RealmsServer getRealmsServer() {
			return null;
		}
	}
}
