/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.KeyCombo;
import net.minecraft.client.realms.Ping;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.realms.dto.PingResult;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerPlayerList;
import net.minecraft.client.realms.dto.RealmsServerPlayerLists;
import net.minecraft.client.realms.dto.RegionPingResult;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsDataFetcher;
import net.minecraft.client.realms.gui.screen.RealmsClientOutdatedScreen;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsCreateRealmScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongConfirmationScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsParentalConsentScreen;
import net.minecraft.client.realms.gui.screen.RealmsPendingInvitesScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.task.RealmsGetServerDetailsTask;
import net.minecraft.client.realms.util.RealmsPersistence;
import net.minecraft.client.realms.util.RealmsTextureManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsMainScreen
extends RealmsScreen {
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
    private static final Identifier QUESTIONMARK = new Identifier("realms", "textures/gui/realms/questionmark.png");
    private static final Identifier NEWS_ICON = new Identifier("realms", "textures/gui/realms/news_icon.png");
    private static final Identifier POPUP = new Identifier("realms", "textures/gui/realms/popup.png");
    private static final Identifier DARKEN = new Identifier("realms", "textures/gui/realms/darken.png");
    static final Identifier CROSS_ICON = new Identifier("realms", "textures/gui/realms/cross_icon.png");
    private static final Identifier TRIAL_ICON = new Identifier("realms", "textures/gui/realms/trial_icon.png");
    static final Identifier WIDGETS = new Identifier("minecraft", "textures/gui/widgets.png");
    static final Text NO_PENDING_TEXT = new TranslatableText("mco.invites.nopending");
    static final Text PENDING_TEXT = new TranslatableText("mco.invites.pending");
    static final List<Text> TRIAL_MESSAGE_LINES = ImmutableList.of(new TranslatableText("mco.trial.message.line1"), new TranslatableText("mco.trial.message.line2"));
    static final Text UNINITIALIZED_TEXT = new TranslatableText("mco.selectServer.uninitialized");
    static final Text EXPIRED_LIST_TEXT = new TranslatableText("mco.selectServer.expiredList");
    static final Text EXPIRED_RENEW_TEXT = new TranslatableText("mco.selectServer.expiredRenew");
    static final Text EXPIRED_TRIAL_TEXT = new TranslatableText("mco.selectServer.expiredTrial");
    static final Text EXPIRED_SUBSCRIBE_TEXT = new TranslatableText("mco.selectServer.expiredSubscribe");
    static final Text MINIGAME_TEXT = new TranslatableText("mco.selectServer.minigame").append(" ");
    private static final Text POPUP_TEXT = new TranslatableText("mco.selectServer.popup");
    private static final Text EXPIRED_TEXT = new TranslatableText("mco.selectServer.expired");
    private static final Text EXPIRES_SOON_TEXT = new TranslatableText("mco.selectServer.expires.soon");
    private static final Text EXPIRES_IN_A_DAY_TEXT = new TranslatableText("mco.selectServer.expires.day");
    private static final Text OPEN_TEXT = new TranslatableText("mco.selectServer.open");
    private static final Text CLOSED_TEXT = new TranslatableText("mco.selectServer.closed");
    private static final Text LEAVE_TEXT = new TranslatableText("mco.selectServer.leave");
    private static final Text CONFIGURE_TEXT = new TranslatableText("mco.selectServer.configure");
    private static final Text INFO_TEXT = new TranslatableText("mco.selectServer.info");
    private static final Text NEWS_TEXT = new TranslatableText("mco.news");
    static final Text UNINITIALIZED_BUTTON_NARRATION = new TranslatableText("gui.narrate.button", UNINITIALIZED_TEXT);
    static final Text TRIAL_NARRATION = ScreenTexts.joinLines(TRIAL_MESSAGE_LINES);
    private static List<Identifier> IMAGES = ImmutableList.of();
    static final RealmsDataFetcher REALMS_DATA_FETCHER = new RealmsDataFetcher(MinecraftClient.getInstance(), RealmsClient.createRealmsClient());
    static boolean overrideConfigure;
    private static int lastScrollYPosition;
    static volatile boolean hasParentalConsent;
    static volatile boolean checkedParentalConsent;
    static volatile boolean checkedClientCompatibility;
    @Nullable
    static Screen realmsGenericErrorScreen;
    private static boolean regionsPinged;
    private final RateLimiter rateLimiter;
    private boolean dontSetConnectedToRealms;
    final Screen lastScreen;
    RealmSelectionList realmSelectionList;
    private boolean field_33775;
    private ButtonWidget playButton;
    private ButtonWidget backButton;
    private ButtonWidget renewButton;
    private ButtonWidget configureButton;
    private ButtonWidget leaveButton;
    @Nullable
    private List<Text> tooltip;
    private List<RealmsServer> realmsServers = ImmutableList.of();
    volatile int numberOfPendingInvites;
    int animTick;
    private boolean hasFetchedServers;
    boolean popupOpenedByUser;
    private boolean justClosedPopup;
    private volatile boolean trialsAvailable;
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
    HoverState hoverState;
    private ButtonWidget showPopupButton;
    private PendingInvitesButton pendingInvitesButton;
    private ButtonWidget newsButton;
    private ButtonWidget createTrialButton;
    private ButtonWidget buyARealmButton;
    private ButtonWidget closeButton;

    public RealmsMainScreen(Screen lastScreen) {
        super(NarratorManager.EMPTY);
        this.lastScreen = lastScreen;
        this.rateLimiter = RateLimiter.create(0.01666666753590107);
    }

    private boolean shouldShowMessageInList() {
        if (!RealmsMainScreen.hasParentalConsent() || !this.hasFetchedServers) {
            return false;
        }
        if (this.trialsAvailable && !this.createdTrial) {
            return true;
        }
        for (RealmsServer realmsServer : this.realmsServers) {
            if (!realmsServer.ownerUUID.equals(this.client.getSession().getUuid())) continue;
            return false;
        }
        return true;
    }

    public boolean shouldShowPopup() {
        if (!RealmsMainScreen.hasParentalConsent() || !this.hasFetchedServers) {
            return false;
        }
        if (this.popupOpenedByUser) {
            return true;
        }
        return this.realmsServers.isEmpty();
    }

    @Override
    public void init() {
        this.keyCombos = Lists.newArrayList(new KeyCombo(new char[]{'3', '2', '1', '4', '5', '6'}, () -> {
            overrideConfigure = !overrideConfigure;
        }), new KeyCombo(new char[]{'9', '8', '7', '1', '2', '3'}, () -> {
            if (RealmsClient.currentEnvironment == RealmsClient.Environment.STAGE) {
                this.switchToProd();
            } else {
                this.switchToStage();
            }
        }), new KeyCombo(new char[]{'9', '8', '7', '4', '5', '6'}, () -> {
            if (RealmsClient.currentEnvironment == RealmsClient.Environment.LOCAL) {
                this.switchToProd();
            } else {
                this.switchToLocal();
            }
        }));
        if (realmsGenericErrorScreen != null) {
            this.client.setScreen(realmsGenericErrorScreen);
            return;
        }
        this.connectLock = new ReentrantLock();
        if (checkedClientCompatibility && !RealmsMainScreen.hasParentalConsent()) {
            this.checkParentalConsent();
        }
        this.checkClientCompatibility();
        if (!this.dontSetConnectedToRealms) {
            this.client.setConnectedToRealms(false);
        }
        this.client.keyboard.setRepeatEvents(true);
        if (RealmsMainScreen.hasParentalConsent()) {
            REALMS_DATA_FETCHER.forceUpdate();
        }
        this.showingPopup = false;
        this.addButtons();
        this.realmSelectionList = new RealmSelectionList();
        if (lastScrollYPosition != -1) {
            this.realmSelectionList.setScrollAmount(lastScrollYPosition);
        }
        this.addSelectableChild(this.realmSelectionList);
        this.field_33775 = true;
        this.focusOn(this.realmSelectionList);
        this.popupText = MultilineText.create(this.textRenderer, (StringVisitable)POPUP_TEXT, 100);
    }

    private static boolean hasParentalConsent() {
        return checkedParentalConsent && hasParentalConsent;
    }

    public void addButtons() {
        this.leaveButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 202, this.height - 32, 90, 20, new TranslatableText("mco.selectServer.leave"), button -> this.leaveClicked(this.findServer())));
        this.configureButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 190, this.height - 32, 90, 20, new TranslatableText("mco.selectServer.configure"), button -> this.configureClicked(this.findServer())));
        this.playButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 93, this.height - 32, 90, 20, new TranslatableText("mco.selectServer.play"), button -> this.play(this.findServer(), this)));
        this.backButton = this.addDrawableChild(new ButtonWidget(this.width / 2 + 4, this.height - 32, 90, 20, ScreenTexts.BACK, button -> {
            if (!this.justClosedPopup) {
                this.client.setScreen(this.lastScreen);
            }
        }));
        this.renewButton = this.addDrawableChild(new ButtonWidget(this.width / 2 + 100, this.height - 32, 90, 20, new TranslatableText("mco.selectServer.expiredRenew"), button -> this.onRenew(this.findServer())));
        this.pendingInvitesButton = this.addDrawableChild(new PendingInvitesButton());
        this.newsButton = this.addDrawableChild(new NewsButton());
        this.showPopupButton = this.addDrawableChild(new ShowPopupButton());
        this.closeButton = this.addDrawableChild(new CloseButton());
        this.createTrialButton = this.addDrawableChild(new ButtonWidget(this.width / 2 + 52, this.popupY0() + 137 - 20, 98, 20, new TranslatableText("mco.selectServer.trial"), button -> {
            if (!this.trialsAvailable || this.createdTrial) {
                return;
            }
            Util.getOperatingSystem().open("https://aka.ms/startjavarealmstrial");
            this.client.setScreen(this.lastScreen);
        }));
        this.buyARealmButton = this.addDrawableChild(new ButtonWidget(this.width / 2 + 52, this.popupY0() + 160 - 20, 98, 20, new TranslatableText("mco.selectServer.buy"), button -> Util.getOperatingSystem().open("https://aka.ms/BuyJavaRealms")));
        this.updateButtonStates(null);
    }

    void updateButtonStates(@Nullable RealmsServer server) {
        boolean bl;
        this.backButton.active = true;
        if (!RealmsMainScreen.hasParentalConsent() || !this.hasFetchedServers) {
            RealmsMainScreen.hide(this.playButton, this.renewButton, this.configureButton, this.createTrialButton, this.buyARealmButton, this.closeButton, this.newsButton, this.pendingInvitesButton, this.showPopupButton, this.leaveButton);
            return;
        }
        this.playButton.visible = true;
        this.playButton.active = this.shouldPlayButtonBeActive(server) && !this.shouldShowPopup();
        this.renewButton.visible = this.shouldRenewButtonBeActive(server);
        this.configureButton.visible = this.shouldConfigureButtonBeVisible(server);
        this.leaveButton.visible = this.shouldLeaveButtonBeVisible(server);
        this.createTrialButton.visible = bl = this.shouldShowPopup() && this.trialsAvailable && !this.createdTrial;
        this.createTrialButton.active = bl;
        this.buyARealmButton.visible = this.shouldShowPopup();
        this.closeButton.visible = this.shouldShowPopup() && this.popupOpenedByUser;
        this.renewButton.active = !this.shouldShowPopup();
        this.configureButton.active = !this.shouldShowPopup();
        this.leaveButton.active = !this.shouldShowPopup();
        this.newsButton.active = true;
        this.pendingInvitesButton.active = true;
        this.showPopupButton.active = !this.shouldShowPopup();
    }

    private boolean shouldShowPopupButton() {
        return (!this.shouldShowPopup() || this.popupOpenedByUser) && RealmsMainScreen.hasParentalConsent() && this.hasFetchedServers;
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
        ++this.animTick;
        if (!RealmsMainScreen.hasParentalConsent()) {
            return;
        }
        REALMS_DATA_FETCHER.init();
        if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.SERVER_LIST)) {
            boolean bl;
            List<RealmsServer> list = REALMS_DATA_FETCHER.getServers();
            RealmsServer realmsServer = this.findServer();
            RealmSelectionListEntry entry = null;
            this.realmSelectionList.clear();
            boolean bl2 = bl = !this.hasFetchedServers;
            if (bl) {
                this.hasFetchedServers = true;
            }
            if (list != null) {
                boolean bl22 = false;
                for (RealmsServer realmsServer2 : list) {
                    if (!this.isOwnedNotExpired(realmsServer2)) continue;
                    bl22 = true;
                }
                this.realmsServers = list;
                if (this.shouldShowMessageInList()) {
                    this.realmSelectionList.addEntry(new RealmSelectionListTrialEntry());
                }
                for (RealmsServer realmsServer2 : this.realmsServers) {
                    RealmSelectionListEntry realmSelectionListEntry = new RealmSelectionListEntry(realmsServer2);
                    this.realmSelectionList.addEntry(realmSelectionListEntry);
                    if (realmsServer == null || realmsServer.id != realmsServer2.id) continue;
                    entry = realmSelectionListEntry;
                }
                if (!regionsPinged && bl22) {
                    regionsPinged = true;
                    this.pingRegions();
                }
            }
            if (bl) {
                this.updateButtonStates(null);
            } else {
                this.realmSelectionList.setSelected(entry);
            }
        }
        if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.numberOfPendingInvites = REALMS_DATA_FETCHER.getPendingInvitesCount();
            if (this.numberOfPendingInvites > 0 && this.rateLimiter.tryAcquire(1)) {
                NarratorManager.INSTANCE.narrate(new TranslatableText("mco.configure.world.invite.narration", this.numberOfPendingInvites));
            }
        }
        if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE) && !this.createdTrial) {
            boolean bl3 = REALMS_DATA_FETCHER.isTrialAvailable();
            if (bl3 != this.trialsAvailable && this.shouldShowPopup()) {
                this.trialsAvailable = bl3;
                this.showingPopup = false;
            } else {
                this.trialsAvailable = bl3;
            }
        }
        if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.LIVE_STATS)) {
            RealmsServerPlayerLists realmsServerPlayerLists = REALMS_DATA_FETCHER.getLivestats();
            block2: for (RealmsServerPlayerList realmsServerPlayerList : realmsServerPlayerLists.servers) {
                for (RealmsServer realmsServer3 : this.realmsServers) {
                    if (realmsServer3.id != realmsServerPlayerList.serverId) continue;
                    realmsServer3.updateServerPing(realmsServerPlayerList);
                    continue block2;
                }
            }
        }
        if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            this.hasUnreadNews = REALMS_DATA_FETCHER.hasUnreadNews();
            this.newsLink = REALMS_DATA_FETCHER.newsLink();
        }
        REALMS_DATA_FETCHER.markClean();
        if (this.shouldShowPopup()) {
            ++this.carouselTick;
        }
        if (this.showPopupButton != null) {
            this.showPopupButton.active = this.showPopupButton.visible = this.shouldShowPopupButton();
        }
    }

    private void pingRegions() {
        new Thread(() -> {
            List<RegionPingResult> list = Ping.pingAllRegions();
            RealmsClient realmsClient = RealmsClient.createRealmsClient();
            PingResult pingResult = new PingResult();
            pingResult.pingResults = list;
            pingResult.worldIds = this.getOwnedNonExpiredWorldIds();
            try {
                realmsClient.sendPingResults(pingResult);
            } catch (Throwable throwable) {
                LOGGER.warn("Could not send ping result to Realms: ", throwable);
            }
        }).start();
    }

    private List<Long> getOwnedNonExpiredWorldIds() {
        ArrayList<Long> list = Lists.newArrayList();
        for (RealmsServer realmsServer : this.realmsServers) {
            if (!this.isOwnedNotExpired(realmsServer)) continue;
            list.add(realmsServer.id);
        }
        return list;
    }

    @Override
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
        this.stopRealmsFetcher();
    }

    public void setCreatedTrial(boolean createdTrial) {
        this.createdTrial = createdTrial;
    }

    void onRenew(@Nullable RealmsServer realmsServer) {
        if (realmsServer != null) {
            String string = "https://aka.ms/ExtendJavaRealms?subscriptionId=" + realmsServer.remoteSubscriptionId + "&profileId=" + this.client.getSession().getUuid() + "&ref=" + (realmsServer.expiredTrial ? "expiredTrial" : "expiredRealm");
            this.client.keyboard.setClipboard(string);
            Util.getOperatingSystem().open(string);
        }
    }

    private void checkClientCompatibility() {
        if (!checkedClientCompatibility) {
            checkedClientCompatibility = true;
            new Thread("MCO Compatability Checker #1"){

                @Override
                public void run() {
                    RealmsClient realmsClient = RealmsClient.createRealmsClient();
                    try {
                        RealmsClient.CompatibleVersionResponse compatibleVersionResponse = realmsClient.clientCompatible();
                        if (compatibleVersionResponse == RealmsClient.CompatibleVersionResponse.OUTDATED) {
                            realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, true);
                            RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(realmsGenericErrorScreen));
                            return;
                        }
                        if (compatibleVersionResponse == RealmsClient.CompatibleVersionResponse.OTHER) {
                            realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, false);
                            RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(realmsGenericErrorScreen));
                            return;
                        }
                        RealmsMainScreen.this.checkParentalConsent();
                    } catch (RealmsServiceException realmsServiceException) {
                        checkedClientCompatibility = false;
                        LOGGER.error("Couldn't connect to realms", realmsServiceException);
                        if (realmsServiceException.httpResultCode == 401) {
                            realmsGenericErrorScreen = new RealmsGenericErrorScreen(new TranslatableText("mco.error.invalid.session.title"), new TranslatableText("mco.error.invalid.session.message"), RealmsMainScreen.this.lastScreen);
                            RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(realmsGenericErrorScreen));
                        }
                        RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(new RealmsGenericErrorScreen(realmsServiceException, RealmsMainScreen.this.lastScreen)));
                    }
                }
            }.start();
        }
    }

    void checkParentalConsent() {
        new Thread("MCO Compatability Checker #1"){

            @Override
            public void run() {
                RealmsClient realmsClient = RealmsClient.createRealmsClient();
                try {
                    Boolean boolean_ = realmsClient.mcoEnabled();
                    if (boolean_.booleanValue()) {
                        LOGGER.info("Realms is available for this user");
                        hasParentalConsent = true;
                    } else {
                        LOGGER.info("Realms is not available for this user");
                        hasParentalConsent = false;
                        RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(new RealmsParentalConsentScreen(RealmsMainScreen.this.lastScreen)));
                    }
                    checkedParentalConsent = true;
                } catch (RealmsServiceException realmsServiceException) {
                    LOGGER.error("Couldn't connect to realms", realmsServiceException);
                    RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(new RealmsGenericErrorScreen(realmsServiceException, RealmsMainScreen.this.lastScreen)));
                }
            }
        }.start();
    }

    private void switchToStage() {
        if (RealmsClient.currentEnvironment != RealmsClient.Environment.STAGE) {
            new Thread("MCO Stage Availability Checker #1"){

                @Override
                public void run() {
                    RealmsClient realmsClient = RealmsClient.createRealmsClient();
                    try {
                        Boolean boolean_ = realmsClient.stageAvailable();
                        if (boolean_.booleanValue()) {
                            RealmsClient.switchToStage();
                            LOGGER.info("Switched to stage");
                            REALMS_DATA_FETCHER.forceUpdate();
                        }
                    } catch (RealmsServiceException realmsServiceException) {
                        LOGGER.error("Couldn't connect to Realms: {}", (Object)realmsServiceException.toString());
                    }
                }
            }.start();
        }
    }

    private void switchToLocal() {
        if (RealmsClient.currentEnvironment != RealmsClient.Environment.LOCAL) {
            new Thread("MCO Local Availability Checker #1"){

                @Override
                public void run() {
                    RealmsClient realmsClient = RealmsClient.createRealmsClient();
                    try {
                        Boolean boolean_ = realmsClient.stageAvailable();
                        if (boolean_.booleanValue()) {
                            RealmsClient.switchToLocal();
                            LOGGER.info("Switched to local");
                            REALMS_DATA_FETCHER.forceUpdate();
                        }
                    } catch (RealmsServiceException realmsServiceException) {
                        LOGGER.error("Couldn't connect to Realms: {}", (Object)realmsServiceException.toString());
                    }
                }
            }.start();
        }
    }

    private void switchToProd() {
        RealmsClient.switchToProd();
        REALMS_DATA_FETCHER.forceUpdate();
    }

    private void stopRealmsFetcher() {
        REALMS_DATA_FETCHER.stop();
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
            TranslatableText text = new TranslatableText("mco.configure.world.leave.question.line1");
            TranslatableText text2 = new TranslatableText("mco.configure.world.leave.question.line2");
            this.client.setScreen(new RealmsLongConfirmationScreen(confirmed -> this.leaveServer(confirmed, selectedServer), RealmsLongConfirmationScreen.Type.INFO, text, text2, true));
        }
    }

    private void saveListScrollPosition() {
        lastScrollYPosition = (int)this.realmSelectionList.getScrollAmount();
    }

    @Nullable
    private RealmsServer findServer() {
        if (this.realmSelectionList == null) {
            return null;
        }
        Entry entry = (Entry)this.realmSelectionList.getSelectedOrNull();
        return entry != null ? entry.getRealmsServer() : null;
    }

    private void leaveServer(boolean confirmed, final RealmsServer realmsServer) {
        if (confirmed) {
            new Thread("Realms-leave-server"){

                @Override
                public void run() {
                    try {
                        RealmsClient realmsClient = RealmsClient.createRealmsClient();
                        realmsClient.uninviteMyselfFrom(realmsServer.id);
                        RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.removeServer(realmsServer));
                    } catch (RealmsServiceException realmsServiceException) {
                        LOGGER.error("Couldn't configure world");
                        RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(new RealmsGenericErrorScreen(realmsServiceException, (Screen)RealmsMainScreen.this)));
                    }
                }
            }.start();
        }
        this.client.setScreen(this);
    }

    void removeServer(RealmsServer serverData) {
        this.realmsServers = REALMS_DATA_FETCHER.removeItem(serverData);
        this.realmSelectionList.children().removeIf(child -> {
            RealmsServer realmsServer2 = child.getRealmsServer();
            return realmsServer2 != null && realmsServer2.id == realmsServer.id;
        });
        this.realmSelectionList.setSelected((Entry)null);
        this.updateButtonStates(null);
        this.playButton.active = false;
    }

    public void removeSelection() {
        if (this.realmSelectionList != null) {
            this.realmSelectionList.setSelected((Entry)null);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.keyCombos.forEach(KeyCombo::reset);
            this.onClosePopup();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
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
        this.hoverState = HoverState.NONE;
        this.tooltip = null;
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
                if (!this.field_33775) {
                    this.addSelectableChild(this.realmSelectionList);
                    this.field_33775 = true;
                }
                this.playButton.active = this.shouldPlayButtonBeActive(this.findServer());
            }
            this.showingPopup = false;
        }
        super.render(matrices, mouseX, mouseY, delta);
        if (this.tooltip != null) {
            this.renderMousehoverTooltip(matrices, this.tooltip, mouseX, mouseY);
        }
        if (this.trialsAvailable && !this.createdTrial && this.shouldShowPopup()) {
            RenderSystem.setShaderTexture(0, TRIAL_ICON);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            int i = 8;
            int j = 8;
            int k = 0;
            if ((Util.getMeasuringTimeMs() / 800L & 1L) == 1L) {
                k = 8;
            }
            DrawableHelper.drawTexture(matrices, this.createTrialButton.x + this.createTrialButton.getWidth() - 8 - 4, this.createTrialButton.y + this.createTrialButton.getHeight() / 2 - 4, 0.0f, k, 8, 8, 8, 16);
        }
    }

    private void drawRealmsLogo(MatrixStack matrices, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, REALMS);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        matrices.push();
        matrices.scale(0.5f, 0.5f, 0.5f);
        DrawableHelper.drawTexture(matrices, x * 2, y * 2 - 5, 0.0f, 0.0f, 200, 50, 200, 50);
        matrices.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isOutsidePopup(mouseX, mouseY) && this.popupOpenedByUser) {
            this.popupOpenedByUser = false;
            this.justClosedPopup = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
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
            if (this.field_33775) {
                this.remove(this.realmSelectionList);
                this.field_33775 = false;
            }
            NarratorManager.INSTANCE.narrate(POPUP_TEXT);
        }
        if (this.hasFetchedServers) {
            this.showingPopup = true;
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.7f);
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, DARKEN);
        boolean k = false;
        int l = 32;
        DrawableHelper.drawTexture(matrices, 0, 32, 0.0f, 0.0f, this.width, this.height - 40 - 32, 310, 166);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, POPUP);
        DrawableHelper.drawTexture(matrices, i, j, 0.0f, 0.0f, 310, 166, 310, 166);
        if (!IMAGES.isEmpty()) {
            RenderSystem.setShaderTexture(0, IMAGES.get(this.carouselIndex));
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            DrawableHelper.drawTexture(matrices, i + 7, j + 7, 0.0f, 0.0f, 195, 152, 195, 152);
            if (this.carouselTick % 95 < 5) {
                if (!this.hasSwitchedCarouselImage) {
                    this.carouselIndex = (this.carouselIndex + 1) % IMAGES.size();
                    this.hasSwitchedCarouselImage = true;
                }
            } else {
                this.hasSwitchedCarouselImage = false;
            }
        }
        this.popupText.draw(matrices, this.width / 2 + 52, j + 7, 10, 0x4C4C4C);
    }

    int popupX0() {
        return (this.width - 310) / 2;
    }

    int popupY0() {
        return this.height / 2 - 80;
    }

    void drawInvitationPendingIcon(MatrixStack matrices, int mouseX, int mouseY, int x, int y, boolean hovered, boolean active) {
        boolean bl5;
        int l;
        int k;
        boolean bl4;
        boolean bl2;
        int i = this.numberOfPendingInvites;
        boolean bl = this.inPendingInvitationArea(mouseX, mouseY);
        boolean bl3 = bl2 = active && hovered;
        if (bl2) {
            float f = 0.25f + (1.0f + MathHelper.sin((float)this.animTick * 0.5f)) * 0.25f;
            int j = 0xFF000000 | (int)(f * 64.0f) << 16 | (int)(f * 64.0f) << 8 | (int)(f * 64.0f) << 0;
            this.fillGradient(matrices, x - 2, y - 2, x + 18, y + 18, j, j);
            j = 0xFF000000 | (int)(f * 255.0f) << 16 | (int)(f * 255.0f) << 8 | (int)(f * 255.0f) << 0;
            this.fillGradient(matrices, x - 2, y - 2, x + 18, y - 1, j, j);
            this.fillGradient(matrices, x - 2, y - 2, x - 1, y + 18, j, j);
            this.fillGradient(matrices, x + 17, y - 2, x + 18, y + 18, j, j);
            this.fillGradient(matrices, x - 2, y + 17, x + 18, y + 18, j, j);
        }
        RenderSystem.setShaderTexture(0, INVITE_ICON);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        boolean bl32 = active && hovered;
        float g = bl32 ? 16.0f : 0.0f;
        DrawableHelper.drawTexture(matrices, x, y - 6, g, 0.0f, 15, 25, 31, 25);
        boolean bl6 = bl4 = active && i != 0;
        if (bl4) {
            k = (Math.min(i, 6) - 1) * 8;
            l = (int)(Math.max(0.0f, Math.max(MathHelper.sin((float)(10 + this.animTick) * 0.57f), MathHelper.cos((float)this.animTick * 0.35f))) * -6.0f);
            RenderSystem.setShaderTexture(0, INVITATION_ICON);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            float h = bl ? 8.0f : 0.0f;
            DrawableHelper.drawTexture(matrices, x + 4, y + 4 + l, k, h, 8, 8, 48, 16);
        }
        k = mouseX + 12;
        l = mouseY;
        boolean bl7 = bl5 = active && bl;
        if (bl5) {
            Text text = i == 0 ? NO_PENDING_TEXT : PENDING_TEXT;
            int m = this.textRenderer.getWidth(text);
            this.fillGradient(matrices, k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
            this.textRenderer.drawWithShadow(matrices, text, (float)k, (float)l, -1);
        }
    }

    private boolean inPendingInvitationArea(double xm, double ym) {
        int i = this.width / 2 + 50;
        int j = this.width / 2 + 66;
        int k = 11;
        int l = 23;
        if (this.numberOfPendingInvites != 0) {
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
            } catch (InterruptedException interruptedException) {
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
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 10, 28, 10, 28);
        if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27 && mouseY < this.height - 40 && mouseY > 32 && !this.shouldShowPopup()) {
            this.setTooltips(EXPIRED_TEXT);
        }
    }

    void drawExpiring(MatrixStack matrices, int x, int y, int mouseX, int mouseY, int remainingDays) {
        RenderSystem.setShaderTexture(0, EXPIRES_SOON_ICON);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.animTick % 20 < 10) {
            DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 10, 28, 20, 28);
        } else {
            DrawableHelper.drawTexture(matrices, x, y, 10.0f, 0.0f, 10, 28, 20, 28);
        }
        if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27 && mouseY < this.height - 40 && mouseY > 32 && !this.shouldShowPopup()) {
            if (remainingDays <= 0) {
                this.setTooltips(EXPIRES_SOON_TEXT);
            } else if (remainingDays == 1) {
                this.setTooltips(EXPIRES_IN_A_DAY_TEXT);
            } else {
                this.setTooltips(new TranslatableText("mco.selectServer.expires.days", remainingDays));
            }
        }
    }

    void drawOpen(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, ON_ICON);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 10, 28, 10, 28);
        if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27 && mouseY < this.height - 40 && mouseY > 32 && !this.shouldShowPopup()) {
            this.setTooltips(OPEN_TEXT);
        }
    }

    void drawClose(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, OFF_ICON);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 10, 28, 10, 28);
        if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27 && mouseY < this.height - 40 && mouseY > 32 && !this.shouldShowPopup()) {
            this.setTooltips(CLOSED_TEXT);
        }
    }

    void drawLeave(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        boolean bl = false;
        if (mouseX >= x && mouseX <= x + 28 && mouseY >= y && mouseY <= y + 28 && mouseY < this.height - 40 && mouseY > 32 && !this.shouldShowPopup()) {
            bl = true;
        }
        RenderSystem.setShaderTexture(0, LEAVE_ICON);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        float f = bl ? 28.0f : 0.0f;
        DrawableHelper.drawTexture(matrices, x, y, f, 0.0f, 28, 28, 56, 28);
        if (bl) {
            this.setTooltips(LEAVE_TEXT);
            this.hoverState = HoverState.LEAVE;
        }
    }

    void drawConfigure(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        boolean bl = false;
        if (mouseX >= x && mouseX <= x + 28 && mouseY >= y && mouseY <= y + 28 && mouseY < this.height - 40 && mouseY > 32 && !this.shouldShowPopup()) {
            bl = true;
        }
        RenderSystem.setShaderTexture(0, CONFIGURE_ICON);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        float f = bl ? 28.0f : 0.0f;
        DrawableHelper.drawTexture(matrices, x, y, f, 0.0f, 28, 28, 56, 28);
        if (bl) {
            this.setTooltips(CONFIGURE_TEXT);
            this.hoverState = HoverState.CONFIGURE;
        }
    }

    protected void renderMousehoverTooltip(MatrixStack matrices, List<Text> tooltips, int x, int y) {
        if (tooltips.isEmpty()) {
            return;
        }
        int i = 0;
        int j = 0;
        for (Text text : tooltips) {
            int k = this.textRenderer.getWidth(text);
            if (k <= j) continue;
            j = k;
        }
        int l = x - j - 5;
        int m = y;
        if (l < 0) {
            l = x + 12;
        }
        for (Text text2 : tooltips) {
            int n = m - (i == 0 ? 3 : 0) + i;
            this.fillGradient(matrices, l - 3, n, l + j + 3, m + 8 + 3 + i, -1073741824, -1073741824);
            this.textRenderer.drawWithShadow(matrices, text2, (float)l, (float)(m + i), 0xFFFFFF);
            i += 10;
        }
    }

    void renderMoreInfo(MatrixStack matrices, int mouseX, int mouseY, int x, int y, boolean hovered) {
        boolean bl = false;
        if (mouseX >= x && mouseX <= x + 20 && mouseY >= y && mouseY <= y + 20) {
            bl = true;
        }
        RenderSystem.setShaderTexture(0, QUESTIONMARK);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        float f = hovered ? 20.0f : 0.0f;
        DrawableHelper.drawTexture(matrices, x, y, f, 0.0f, 20, 20, 40, 20);
        if (bl) {
            this.setTooltips(INFO_TEXT);
        }
    }

    void renderNews(MatrixStack matrices, int mouseX, int mouseY, boolean hasUnread, int x, int y, boolean hovered, boolean active) {
        boolean bl = false;
        if (mouseX >= x && mouseX <= x + 20 && mouseY >= y && mouseY <= y + 20) {
            bl = true;
        }
        RenderSystem.setShaderTexture(0, NEWS_ICON);
        if (active) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1.0f);
        }
        boolean bl2 = active && hovered;
        float f = bl2 ? 20.0f : 0.0f;
        DrawableHelper.drawTexture(matrices, x, y, f, 0.0f, 20, 20, 40, 20);
        if (bl && active) {
            this.setTooltips(NEWS_TEXT);
        }
        if (hasUnread && active) {
            int i = bl ? 0 : (int)(Math.max(0.0f, Math.max(MathHelper.sin((float)(10 + this.animTick) * 0.57f), MathHelper.cos((float)this.animTick * 0.35f))) * -6.0f);
            RenderSystem.setShaderTexture(0, INVITATION_ICON);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            DrawableHelper.drawTexture(matrices, x + 10, y + 2 + i, 40.0f, 0.0f, 8, 8, 48, 16);
        }
    }

    private void renderLocal(MatrixStack matrices) {
        String string = "LOCAL!";
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        matrices.push();
        matrices.translate(this.width / 2 - 25, 20.0, 0.0);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-20.0f));
        matrices.scale(1.5f, 1.5f, 1.5f);
        this.textRenderer.draw(matrices, "LOCAL!", 0.0f, 0.0f, 0x7FFF7F);
        matrices.pop();
    }

    private void renderStage(MatrixStack matrices) {
        String string = "STAGE!";
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        matrices.push();
        matrices.translate(this.width / 2 - 25, 20.0, 0.0);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-20.0f));
        matrices.scale(1.5f, 1.5f, 1.5f);
        this.textRenderer.draw(matrices, "STAGE!", 0.0f, 0.0f, -256);
        matrices.pop();
    }

    public RealmsMainScreen newScreen() {
        RealmsMainScreen realmsMainScreen = new RealmsMainScreen(this.lastScreen);
        realmsMainScreen.init(this.client, this.width, this.height);
        return realmsMainScreen;
    }

    public static void loadImages(ResourceManager manager) {
        Collection<Identifier> collection = manager.findResources("textures/gui/images", filename -> filename.endsWith(".png"));
        IMAGES = collection.stream().filter(id -> id.getNamespace().equals("realms")).toList();
    }

    void setTooltips(Text ... tooltips) {
        this.tooltip = Arrays.asList(tooltips);
    }

    private void openPendingInvitesScreen(ButtonWidget button) {
        this.client.setScreen(new RealmsPendingInvitesScreen(this.lastScreen));
    }

    static {
        lastScrollYPosition = -1;
    }

    @Environment(value=EnvType.CLIENT)
    class RealmSelectionList
    extends RealmsObjectSelectionList<Entry> {
        public RealmSelectionList() {
            super(RealmsMainScreen.this.width, RealmsMainScreen.this.height, 32, RealmsMainScreen.this.height - 40, 36);
        }

        @Override
        public boolean isFocused() {
            return RealmsMainScreen.this.getFocused() == this;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                Entry entry = (Entry)this.getSelectedOrNull();
                if (entry == null) {
                    return super.keyPressed(keyCode, scanCode, modifiers);
                }
                return entry.mouseClicked(0.0, 0.0, 0);
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
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
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void setSelected(@Nullable Entry entry) {
            super.setSelected(entry);
            if (entry != null) {
                RealmsMainScreen.this.updateButtonStates(entry.getRealmsServer());
            } else {
                RealmsMainScreen.this.updateButtonStates(null);
            }
        }

        @Override
        public void itemClicked(int cursorY, int selectionIndex, double mouseX, double mouseY, int listWidth) {
            Entry entry = (Entry)this.getEntry(selectionIndex);
            if (entry instanceof RealmSelectionListTrialEntry) {
                RealmsMainScreen.this.popupOpenedByUser = true;
                return;
            }
            RealmsServer realmsServer = entry.getRealmsServer();
            if (realmsServer == null) {
                return;
            }
            if (realmsServer.state == RealmsServer.State.UNINITIALIZED) {
                MinecraftClient.getInstance().setScreen(new RealmsCreateRealmScreen(realmsServer, RealmsMainScreen.this));
                return;
            }
            if (RealmsMainScreen.this.hoverState == HoverState.CONFIGURE) {
                RealmsMainScreen.this.configureClicked(realmsServer);
            } else if (RealmsMainScreen.this.hoverState == HoverState.LEAVE) {
                RealmsMainScreen.this.leaveClicked(realmsServer);
            } else if (RealmsMainScreen.this.hoverState == HoverState.EXPIRED) {
                RealmsMainScreen.this.onRenew(realmsServer);
            } else if (RealmsMainScreen.this.shouldPlayButtonBeActive(realmsServer)) {
                if (Util.getMeasuringTimeMs() - RealmsMainScreen.this.lastPlayButtonClickTime < 250L && this.isSelectedEntry(selectionIndex)) {
                    RealmsMainScreen.this.play(realmsServer, RealmsMainScreen.this);
                }
                RealmsMainScreen.this.lastPlayButtonClickTime = Util.getMeasuringTimeMs();
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

    @Environment(value=EnvType.CLIENT)
    class PendingInvitesButton
    extends ButtonWidget {
        public PendingInvitesButton() {
            super(RealmsMainScreen.this.width / 2 + 47, 6, 22, 22, LiteralText.EMPTY, RealmsMainScreen.this::openPendingInvitesScreen);
        }

        public void updatePendingText() {
            this.setMessage(RealmsMainScreen.this.numberOfPendingInvites == 0 ? NO_PENDING_TEXT : PENDING_TEXT);
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RealmsMainScreen.this.drawInvitationPendingIcon(matrices, mouseX, mouseY, this.x, this.y, this.isHovered(), this.active);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class NewsButton
    extends ButtonWidget {
        public NewsButton() {
            super(RealmsMainScreen.this.width - 62, 6, 20, 20, new TranslatableText("mco.news"), button -> {
                if (realmsMainScreen.newsLink == null) {
                    return;
                }
                Util.getOperatingSystem().open(realmsMainScreen.newsLink);
                if (realmsMainScreen.hasUnreadNews) {
                    RealmsPersistence.RealmsPersistenceData realmsPersistenceData = RealmsPersistence.readFile();
                    realmsPersistenceData.hasUnreadNews = false;
                    realmsMainScreen.hasUnreadNews = false;
                    RealmsPersistence.writeFile(realmsPersistenceData);
                }
            });
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RealmsMainScreen.this.renderNews(matrices, mouseX, mouseY, RealmsMainScreen.this.hasUnreadNews, this.x, this.y, this.isHovered(), this.active);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ShowPopupButton
    extends ButtonWidget {
        public ShowPopupButton() {
            super(RealmsMainScreen.this.width - 37, 6, 20, 20, new TranslatableText("mco.selectServer.info"), button -> {
                realmsMainScreen.popupOpenedByUser = !realmsMainScreen.popupOpenedByUser;
            });
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RealmsMainScreen.this.renderMoreInfo(matrices, mouseX, mouseY, this.x, this.y, this.isHovered());
        }
    }

    @Environment(value=EnvType.CLIENT)
    class CloseButton
    extends ButtonWidget {
        public CloseButton() {
            super(RealmsMainScreen.this.popupX0() + 4, RealmsMainScreen.this.popupY0() + 4, 12, 12, new TranslatableText("mco.selectServer.close"), button -> RealmsMainScreen.this.onClosePopup());
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RenderSystem.setShaderTexture(0, CROSS_ICON);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            float f = this.isHovered() ? 12.0f : 0.0f;
            CloseButton.drawTexture(matrices, this.x, this.y, 0.0f, f, 12, 12, 12, 24);
            if (this.isMouseOver(mouseX, mouseY)) {
                RealmsMainScreen.this.setTooltips(this.getMessage());
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class RealmSelectionListTrialEntry
    extends Entry {
        RealmSelectionListTrialEntry() {
        }

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
            int k = 0x7FFF7F;
            if (bl && !RealmsMainScreen.this.shouldShowPopup()) {
                k = 6077788;
            }
            for (Text text : TRIAL_MESSAGE_LINES) {
                DrawableHelper.drawCenteredText(matrices, RealmsMainScreen.this.textRenderer, text, RealmsMainScreen.this.width / 2, i + j, k);
                j += 10;
            }
        }

        @Override
        public Text getNarration() {
            return TRIAL_NARRATION;
        }

        @Override
        @Nullable
        public RealmsServer getRealmsServer() {
            return null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class RealmSelectionListEntry
    extends Entry {
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
                RenderSystem.setShaderTexture(0, WORLD_ICON);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                DrawableHelper.drawTexture(matrices, x + 10, y + 6, 0.0f, 0.0f, 40, 20, 40, 20);
                float f = 0.5f + (1.0f + MathHelper.sin((float)RealmsMainScreen.this.animTick * 0.25f)) * 0.25f;
                int i = 0xFF000000 | (int)(127.0f * f) << 16 | (int)(255.0f * f) << 8 | (int)(127.0f * f);
                DrawableHelper.drawCenteredText(matrices, RealmsMainScreen.this.textRenderer, UNINITIALIZED_TEXT, x + 10 + 40 + 75, y + 12, i);
                return;
            }
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
            if (!RealmsMainScreen.this.isSelfOwnedServer(serverData) && !overrideConfigure) {
                RealmsMainScreen.this.drawLeave(matrices, x + 225, y + 2, mouseX, mouseY);
            } else {
                RealmsMainScreen.this.drawConfigure(matrices, x + 225, y + 2, mouseX, mouseY);
            }
            if (!"0".equals(serverData.serverPing.nrOfPlayers)) {
                String string = Formatting.GRAY + serverData.serverPing.nrOfPlayers;
                RealmsMainScreen.this.textRenderer.draw(matrices, string, (float)(x + 207 - RealmsMainScreen.this.textRenderer.getWidth(string)), (float)(y + 3), 0x808080);
                if (mouseX >= x + 207 - RealmsMainScreen.this.textRenderer.getWidth(string) && mouseX <= x + 207 && mouseY >= y + 1 && mouseY <= y + 10 && mouseY < RealmsMainScreen.this.height - 40 && mouseY > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                    RealmsMainScreen.this.setTooltips(new LiteralText(serverData.serverPing.playerList));
                }
            }
            if (RealmsMainScreen.this.isSelfOwnedServer(serverData) && serverData.expired) {
                Text text2;
                Text text;
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.enableBlend();
                RenderSystem.setShaderTexture(0, WIDGETS);
                RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
                if (serverData.expiredTrial) {
                    text = EXPIRED_TRIAL_TEXT;
                    text2 = EXPIRED_SUBSCRIBE_TEXT;
                } else {
                    text = EXPIRED_LIST_TEXT;
                    text2 = EXPIRED_RENEW_TEXT;
                }
                int k = RealmsMainScreen.this.textRenderer.getWidth(text2) + 17;
                int l = 16;
                int m = x + RealmsMainScreen.this.textRenderer.getWidth(text) + 8;
                int n = y + 13;
                boolean bl = false;
                if (mouseX >= m && mouseX < m + k && mouseY > n && mouseY <= n + 16 && mouseY < RealmsMainScreen.this.height - 40 && mouseY > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                    bl = true;
                    RealmsMainScreen.this.hoverState = HoverState.EXPIRED;
                }
                int o = bl ? 2 : 1;
                DrawableHelper.drawTexture(matrices, m, n, 0.0f, 46 + o * 20, k / 2, 8, 256, 256);
                DrawableHelper.drawTexture(matrices, m + k / 2, n, 200 - k / 2, 46 + o * 20, k / 2, 8, 256, 256);
                DrawableHelper.drawTexture(matrices, m, n + 8, 0.0f, 46 + o * 20 + 12, k / 2, 8, 256, 256);
                DrawableHelper.drawTexture(matrices, m + k / 2, n + 8, 200 - k / 2, 46 + o * 20 + 12, k / 2, 8, 256, 256);
                RenderSystem.disableBlend();
                int p = y + 11 + 5;
                int q = bl ? 0xFFFFA0 : 0xFFFFFF;
                RealmsMainScreen.this.textRenderer.draw(matrices, text, (float)(x + 2), (float)(p + 1), 15553363);
                DrawableHelper.drawCenteredText(matrices, RealmsMainScreen.this.textRenderer, text2, m + k / 2, p + 1, q);
            } else {
                if (serverData.worldType == RealmsServer.WorldType.MINIGAME) {
                    int r = 0xCCAC5C;
                    int s = RealmsMainScreen.this.textRenderer.getWidth(MINIGAME_TEXT);
                    RealmsMainScreen.this.textRenderer.draw(matrices, MINIGAME_TEXT, (float)(x + 2), (float)(y + 12), 0xCCAC5C);
                    RealmsMainScreen.this.textRenderer.draw(matrices, serverData.getMinigameName(), (float)(x + 2 + s), (float)(y + 12), 0x6C6C6C);
                } else {
                    RealmsMainScreen.this.textRenderer.draw(matrices, serverData.getDescription(), (float)(x + 2), (float)(y + 12), 0x6C6C6C);
                }
                if (!RealmsMainScreen.this.isSelfOwnedServer(serverData)) {
                    RealmsMainScreen.this.textRenderer.draw(matrices, serverData.owner, (float)(x + 2), (float)(y + 12 + 11), 0x4C4C4C);
                }
            }
            RealmsMainScreen.this.textRenderer.draw(matrices, serverData.getName(), (float)(x + 2), (float)(y + 1), 0xFFFFFF);
            RealmsTextureManager.withBoundFace(serverData.ownerUUID, () -> {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                DrawableHelper.drawTexture(matrices, x - 36, y, 32, 32, 8.0f, 8.0f, 8, 8, 64, 64);
                DrawableHelper.drawTexture(matrices, x - 36, y, 32, 32, 40.0f, 8.0f, 8, 8, 64, 64);
            });
        }

        @Override
        public Text getNarration() {
            if (this.mServerData.state == RealmsServer.State.UNINITIALIZED) {
                return UNINITIALIZED_BUTTON_NARRATION;
            }
            return new TranslatableText("narrator.select", this.mServerData.name);
        }

        @Override
        @Nullable
        public RealmsServer getRealmsServer() {
            return this.mServerData;
        }
    }

    @Environment(value=EnvType.CLIENT)
    abstract class Entry
    extends AlwaysSelectedEntryListWidget.Entry<Entry> {
        Entry() {
        }

        @Nullable
        public abstract RealmsServer getRealmsServer();
    }

    @Environment(value=EnvType.CLIENT)
    static enum HoverState {
        NONE,
        EXPIRED,
        LEAVE,
        CONFIGURE;

    }
}

