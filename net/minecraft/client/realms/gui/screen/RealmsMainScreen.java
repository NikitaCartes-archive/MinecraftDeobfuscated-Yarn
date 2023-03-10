/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawableHelper;
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
import net.minecraft.client.realms.KeyCombo;
import net.minecraft.client.realms.Ping;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsNewsUpdater;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.realms.dto.PingResult;
import net.minecraft.client.realms.dto.RealmsNews;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerPlayerList;
import net.minecraft.client.realms.dto.RegionPingResult;
import net.minecraft.client.realms.exception.RealmsServiceException;
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
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.realms.util.RealmsPersistence;
import net.minecraft.client.realms.util.RealmsServerFilterer;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Urls;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
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
    private static final Identifier INVITATION_ICON = new Identifier("realms", "textures/gui/realms/invitation_icons.png");
    private static final Identifier INVITE_ICON = new Identifier("realms", "textures/gui/realms/invite_icon.png");
    static final Identifier WORLD_ICON = new Identifier("realms", "textures/gui/realms/world_icon.png");
    private static final Identifier REALMS = new Identifier("realms", "textures/gui/title/realms.png");
    private static final Identifier NEWS_ICON = new Identifier("realms", "textures/gui/realms/news_icon.png");
    private static final Identifier POPUP = new Identifier("realms", "textures/gui/realms/popup.png");
    private static final Identifier DARKEN = new Identifier("realms", "textures/gui/realms/darken.png");
    static final Identifier CROSS_ICON = new Identifier("realms", "textures/gui/realms/cross_icon.png");
    private static final Identifier TRIAL_ICON = new Identifier("realms", "textures/gui/realms/trial_icon.png");
    static final Identifier INFO_ICON = new Identifier("minecraft", "textures/gui/info_icon.png");
    static final Text NO_PENDING_TEXT = Text.translatable("mco.invites.nopending");
    static final Text PENDING_TEXT = Text.translatable("mco.invites.pending");
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
    private static List<Identifier> IMAGES = ImmutableList.of();
    @Nullable
    private PeriodicRunnerFactory.RunnersManager periodicRunnersManager;
    private RealmsServerFilterer serverFilterer;
    private final Set<UUID> seenNotifications = new HashSet<UUID>();
    private static boolean overrideConfigure;
    private static int lastScrollYPosition;
    static volatile boolean hasParentalConsent;
    static volatile boolean checkedParentalConsent;
    static volatile boolean checkedClientCompatibility;
    @Nullable
    static Screen realmsGenericErrorScreen;
    private static boolean regionsPinged;
    private final RateLimiter rateLimiter;
    private boolean dontSetConnectedToRealms;
    final Screen parent;
    RealmSelectionList realmSelectionList;
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
    private final List<RealmsNotification> notifications = new ArrayList<RealmsNotification>();
    private ButtonWidget showPopupButton;
    private PendingInvitesButton pendingInvitesButton;
    private ButtonWidget newsButton;
    private ButtonWidget createTrialButton;
    private ButtonWidget buyARealmButton;
    private ButtonWidget closeButton;

    public RealmsMainScreen(Screen parent) {
        super(NarratorManager.EMPTY);
        this.parent = parent;
        this.rateLimiter = RateLimiter.create(0.01666666753590107);
    }

    private boolean shouldShowMessageInList() {
        if (!RealmsMainScreen.hasParentalConsent() || !this.hasFetchedServers) {
            return false;
        }
        if (this.trialAvailable && !this.createdTrial) {
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
        this.showingPopup = false;
        this.realmSelectionList = new RealmSelectionList();
        if (lastScrollYPosition != -1) {
            this.realmSelectionList.setScrollAmount(lastScrollYPosition);
        }
        this.addSelectableChild(this.realmSelectionList);
        this.hasSelectionList = true;
        this.setInitialFocus(this.realmSelectionList);
        this.addPurchaseButtons();
        this.addLowerButtons();
        this.addInvitesAndNewsButtons();
        this.updateButtonStates(null);
        this.popupText = MultilineText.create(this.textRenderer, (StringVisitable)POPUP_TEXT, 100);
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

    private static boolean hasParentalConsent() {
        return checkedParentalConsent && hasParentalConsent;
    }

    public void addInvitesAndNewsButtons() {
        this.pendingInvitesButton = this.addDrawableChild(new PendingInvitesButton());
        this.newsButton = this.addDrawableChild(new NewsButton());
        this.showPopupButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.selectServer.purchase"), button -> {
            this.popupOpenedByUser = !this.popupOpenedByUser;
        }).dimensions(this.width - 90, 6, 80, 20).build());
    }

    public void addPurchaseButtons() {
        this.createTrialButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.selectServer.trial"), button -> {
            if (!this.trialAvailable || this.createdTrial) {
                return;
            }
            Util.getOperatingSystem().open("https://aka.ms/startjavarealmstrial");
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 + 52, this.popupY0() + 137 - 20, 98, 20).build());
        this.buyARealmButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.selectServer.buy"), button -> Util.getOperatingSystem().open("https://aka.ms/BuyJavaRealms")).dimensions(this.width / 2 + 52, this.popupY0() + 160 - 20, 98, 20).build());
        this.closeButton = this.addDrawableChild(new CloseButton());
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
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, this.height - 64, this.width, 64);
    }

    void updateButtonStates(@Nullable RealmsServer server) {
        boolean bl;
        this.backButton.active = true;
        if (!RealmsMainScreen.hasParentalConsent() || !this.hasFetchedServers) {
            RealmsMainScreen.hide(this.playButton, this.renewButton, this.configureButton, this.createTrialButton, this.buyARealmButton, this.closeButton, this.newsButton, this.pendingInvitesButton, this.showPopupButton, this.leaveButton);
            return;
        }
        this.createTrialButton.visible = bl = this.shouldShowPopup() && this.trialAvailable && !this.createdTrial;
        this.createTrialButton.active = bl;
        this.buyARealmButton.visible = this.shouldShowPopup();
        this.closeButton.visible = this.shouldShowPopup() && this.popupOpenedByUser;
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
        ++this.animTick;
        boolean bl = RealmsMainScreen.hasParentalConsent();
        if (this.periodicRunnersManager == null && bl) {
            this.periodicRunnersManager = this.createPeriodicRunnersManager(this.client.getRealmsPeriodicCheckers());
        } else if (this.periodicRunnersManager != null && !bl) {
            this.periodicRunnersManager = null;
        }
        if (this.periodicRunnersManager != null) {
            this.periodicRunnersManager.runAll();
        }
        if (this.shouldShowPopup()) {
            ++this.carouselTick;
        }
        if (this.showPopupButton != null) {
            this.showPopupButton.active = this.showPopupButton.visible = this.shouldShowPopupButton();
        }
    }

    private PeriodicRunnerFactory.RunnersManager createPeriodicRunnersManager(RealmsPeriodicCheckers periodicCheckers) {
        PeriodicRunnerFactory.RunnersManager runnersManager = periodicCheckers.runnerFactory.create();
        runnersManager.add(periodicCheckers.serverList, servers -> {
            List<RealmsServer> list = this.serverFilterer.filterAndSort((List<RealmsServer>)servers);
            boolean bl = false;
            for (RealmsServer realmsServer : list) {
                if (!this.isOwnedNotExpired(realmsServer)) continue;
                bl = true;
            }
            this.realmsServers = list;
            this.hasFetchedServers = true;
            this.refresh();
            if (!regionsPinged && bl) {
                regionsPinged = true;
                this.pingRegions();
            }
        });
        RealmsMainScreen.request(RealmsClient::listNotifications, notifications -> {
            this.notifications.clear();
            this.notifications.addAll((Collection<RealmsNotification>)notifications);
            this.refresh();
        });
        runnersManager.add(periodicCheckers.pendingInvitesCount, pendingInvitesCount -> {
            this.pendingInvitesCount = pendingInvitesCount;
            if (this.pendingInvitesCount > 0 && this.rateLimiter.tryAcquire(1)) {
                this.client.getNarratorManager().narrate(Text.translatable("mco.configure.world.invite.narration", this.pendingInvitesCount));
            }
        });
        runnersManager.add(periodicCheckers.trialAvailability, trialAvailable -> {
            if (this.createdTrial) {
                return;
            }
            if (trialAvailable != this.trialAvailable && this.shouldShowPopup()) {
                this.trialAvailable = trialAvailable;
                this.showingPopup = false;
            } else {
                this.trialAvailable = trialAvailable;
            }
        });
        runnersManager.add(periodicCheckers.liveStats, liveStats -> {
            block0: for (RealmsServerPlayerList realmsServerPlayerList : liveStats.servers) {
                for (RealmsServer realmsServer : this.realmsServers) {
                    if (realmsServer.id != realmsServerPlayerList.serverId) continue;
                    realmsServer.updateServerPing(realmsServerPlayerList);
                    continue block0;
                }
            }
        });
        runnersManager.add(periodicCheckers.news, news -> {
            realmsPeriodicCheckers.newsUpdater.updateNews((RealmsNews)news);
            this.hasUnreadNews = realmsPeriodicCheckers.newsUpdater.hasUnreadNews();
            this.newsLink = realmsPeriodicCheckers.newsUpdater.getNewsLink();
            this.updateButtonStates(null);
        });
        return runnersManager;
    }

    private static <T> void request(Request<T> request, Consumer<T> resultConsumer) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ((CompletableFuture)CompletableFuture.supplyAsync(() -> {
            try {
                return request.request(RealmsClient.createRealmsClient(minecraftClient));
            } catch (RealmsServiceException realmsServiceException) {
                throw new RuntimeException(realmsServiceException);
            }
        }).thenAcceptAsync(resultConsumer, (Executor)minecraftClient)).exceptionally(throwable -> {
            LOGGER.error("Failed to execute call to Realms Service", (Throwable)throwable);
            return null;
        });
    }

    private void refresh() {
        boolean bl = !this.hasFetchedServers;
        this.realmSelectionList.clear();
        ArrayList<UUID> list = new ArrayList<UUID>();
        for (RealmsNotification realmsNotification : this.notifications) {
            this.addNotificationEntry(this.realmSelectionList, realmsNotification);
            if (realmsNotification.isSeen() || this.seenNotifications.contains(realmsNotification.getUuid())) continue;
            list.add(realmsNotification.getUuid());
        }
        if (!list.isEmpty()) {
            RealmsMainScreen.request(client -> {
                client.markNotificationsAsSeen(list);
                return null;
            }, void_ -> this.seenNotifications.addAll(list));
        }
        if (this.shouldShowMessageInList()) {
            this.realmSelectionList.addEntry(new RealmSelectionListTrialEntry());
        }
        RealmSelectionListEntry entry = null;
        RealmsServer realmsServer = this.findServer();
        for (RealmsServer realmsServer2 : this.realmsServers) {
            RealmSelectionListEntry realmSelectionListEntry = new RealmSelectionListEntry(realmsServer2);
            this.realmSelectionList.addEntry(realmSelectionListEntry);
            if (realmsServer == null || realmsServer.id != realmsServer2.id) continue;
            entry = realmSelectionListEntry;
        }
        if (bl) {
            this.updateButtonStates(null);
        } else {
            this.realmSelectionList.setSelected(entry);
        }
    }

    private void addNotificationEntry(RealmSelectionList selectionList, RealmsNotification notification) {
        if (notification instanceof RealmsNotification.VisitUrl) {
            RealmsNotification.VisitUrl visitUrl = (RealmsNotification.VisitUrl)notification;
            selectionList.addEntry(new VisitUrlNotification(visitUrl.getDefaultMessage(), visitUrl));
            selectionList.addEntry(new VisitButtonEntry(visitUrl.createButton(this)));
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
            new Thread("MCO Compatability Checker #1"){

                @Override
                public void run() {
                    RealmsClient realmsClient = RealmsClient.create();
                    try {
                        RealmsClient.CompatibleVersionResponse compatibleVersionResponse = realmsClient.clientCompatible();
                        if (compatibleVersionResponse != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                            realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.parent);
                            RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(realmsGenericErrorScreen));
                            return;
                        }
                        RealmsMainScreen.this.checkParentalConsent();
                    } catch (RealmsServiceException realmsServiceException) {
                        checkedClientCompatibility = false;
                        LOGGER.error("Couldn't connect to realms", realmsServiceException);
                        if (realmsServiceException.httpResultCode == 401) {
                            realmsGenericErrorScreen = new RealmsGenericErrorScreen(Text.translatable("mco.error.invalid.session.title"), Text.translatable("mco.error.invalid.session.message"), RealmsMainScreen.this.parent);
                            RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(realmsGenericErrorScreen));
                        }
                        RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(new RealmsGenericErrorScreen(realmsServiceException, RealmsMainScreen.this.parent)));
                    }
                }
            }.start();
        }
    }

    void checkParentalConsent() {
        new Thread("MCO Compatability Checker #1"){

            @Override
            public void run() {
                RealmsClient realmsClient = RealmsClient.create();
                try {
                    Boolean boolean_ = realmsClient.mcoEnabled();
                    if (boolean_.booleanValue()) {
                        LOGGER.info("Realms is available for this user");
                        hasParentalConsent = true;
                    } else {
                        LOGGER.info("Realms is not available for this user");
                        hasParentalConsent = false;
                        RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(new RealmsParentalConsentScreen(RealmsMainScreen.this.parent)));
                    }
                    checkedParentalConsent = true;
                } catch (RealmsServiceException realmsServiceException) {
                    LOGGER.error("Couldn't connect to realms", realmsServiceException);
                    RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(new RealmsGenericErrorScreen(realmsServiceException, RealmsMainScreen.this.parent)));
                }
            }
        }.start();
    }

    private void switchToStage() {
        if (RealmsClient.currentEnvironment != RealmsClient.Environment.STAGE) {
            new Thread("MCO Stage Availability Checker #1"){

                @Override
                public void run() {
                    RealmsClient realmsClient = RealmsClient.create();
                    try {
                        Boolean boolean_ = realmsClient.stageAvailable();
                        if (boolean_.booleanValue()) {
                            RealmsClient.switchToStage();
                            LOGGER.info("Switched to stage");
                            RealmsMainScreen.this.resetPeriodicRunnersManager();
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
                    RealmsClient realmsClient = RealmsClient.create();
                    try {
                        Boolean boolean_ = realmsClient.stageAvailable();
                        if (boolean_.booleanValue()) {
                            RealmsClient.switchToLocal();
                            LOGGER.info("Switched to local");
                            RealmsMainScreen.this.resetPeriodicRunnersManager();
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
            MutableText text = Text.translatable("mco.configure.world.leave.question.line1");
            MutableText text2 = Text.translatable("mco.configure.world.leave.question.line2");
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
                        RealmsClient realmsClient = RealmsClient.create();
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
        this.realmsServers = this.serverFilterer.remove(serverData);
        this.realmSelectionList.children().removeIf(child -> {
            RealmsServer realmsServer2 = child.getRealmsServer();
            return realmsServer2 != null && realmsServer2.id == realmsServer.id;
        });
        this.realmSelectionList.setSelected((Entry)null);
        this.updateButtonStates(null);
        this.playButton.active = false;
    }

    void dismissNotification(UUID notification) {
        RealmsMainScreen.request(client -> {
            client.dismissNotifications(List.of(notification));
            return null;
        }, void_ -> {
            this.notifications.removeIf(notificationId -> notificationId.isDismissable() && notification.equals(notificationId.getUuid()));
            this.refresh();
        });
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
            this.client.setScreen(this.parent);
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        this.keyCombos.forEach(keyCombo -> keyCombo.keyPressed(chr));
        return true;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
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
            matrices.push();
            matrices.translate(0.0f, 0.0f, 100.0f);
            this.drawPopup(matrices);
            matrices.pop();
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
            DrawableHelper.drawTexture(matrices, this.createTrialButton.getX() + this.createTrialButton.getWidth() - 8 - 4, this.createTrialButton.getY() + this.createTrialButton.getHeight() / 2 - 4, 0.0f, k, 8, 8, 8, 16);
        }
    }

    private void drawRealmsLogo(MatrixStack matrices, int x, int y) {
        RenderSystem.setShaderTexture(0, REALMS);
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
            if (this.hasSelectionList) {
                this.remove(this.realmSelectionList);
                this.hasSelectionList = false;
            }
            this.client.getNarratorManager().narrate(POPUP_TEXT);
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
        this.popupText.draw(matrices, this.width / 2 + 52, j + 7, 10, 0xFFFFFF);
    }

    int popupX0() {
        return (this.width - 310) / 2;
    }

    int popupY0() {
        return this.height / 2 - 80;
    }

    void drawInvitationPendingIcon(MatrixStack matrices, int mouseX, int mouseY, int x, int y, boolean hovered, boolean active) {
        boolean bl5;
        boolean bl4;
        int m;
        int l;
        boolean bl2;
        int i = this.pendingInvitesCount;
        boolean bl = this.inPendingInvitationArea(mouseX, mouseY);
        boolean bl3 = bl2 = active && hovered;
        if (bl2) {
            float f = 0.25f + (1.0f + MathHelper.sin((float)this.animTick * 0.5f)) * 0.25f;
            int j = 0xFF000000 | (int)(f * 64.0f) << 16 | (int)(f * 64.0f) << 8 | (int)(f * 64.0f) << 0;
            int k = x - 2;
            l = x + 16;
            m = y + 1;
            int n = y + 16;
            RealmsMainScreen.fillGradient(matrices, k, m, l, n, j, j);
            j = 0xFF000000 | (int)(f * 255.0f) << 16 | (int)(f * 255.0f) << 8 | (int)(f * 255.0f) << 0;
            RealmsMainScreen.fillGradient(matrices, k, y, l, y + 1, j, j);
            RealmsMainScreen.fillGradient(matrices, k - 1, y, k, n + 1, j, j);
            RealmsMainScreen.fillGradient(matrices, l, y, l + 1, n, j, j);
            RealmsMainScreen.fillGradient(matrices, k, n, l + 1, n + 1, j, j);
        }
        RenderSystem.setShaderTexture(0, INVITE_ICON);
        boolean bl32 = active && hovered;
        float g = bl32 ? 16.0f : 0.0f;
        DrawableHelper.drawTexture(matrices, x, y - 6, g, 0.0f, 15, 25, 31, 25);
        boolean bl6 = bl4 = active && i != 0;
        if (bl4) {
            l = (Math.min(i, 6) - 1) * 8;
            m = (int)(Math.max(0.0f, Math.max(MathHelper.sin((float)(10 + this.animTick) * 0.57f), MathHelper.cos((float)this.animTick * 0.35f))) * -6.0f);
            RenderSystem.setShaderTexture(0, INVITATION_ICON);
            float h = bl ? 8.0f : 0.0f;
            DrawableHelper.drawTexture(matrices, x + 4, y + 4 + m, l, h, 8, 8, 48, 16);
        }
        l = mouseX + 12;
        m = mouseY;
        boolean bl7 = bl5 = active && bl;
        if (bl5) {
            Text text = i == 0 ? NO_PENDING_TEXT : PENDING_TEXT;
            int o = this.textRenderer.getWidth(text);
            RealmsMainScreen.fillGradient(matrices, l - 3, m - 3, l + o + 3, m + 8 + 3, -1073741824, -1073741824);
            this.textRenderer.drawWithShadow(matrices, text, (float)l, (float)m, -1);
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
        DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 10, 28, 10, 28);
        if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27 && mouseY < this.height - 40 && mouseY > 32 && !this.shouldShowPopup()) {
            this.setTooltip(EXPIRED_TEXT);
        }
    }

    void drawExpiring(MatrixStack matrices, int x, int y, int mouseX, int mouseY, int remainingDays) {
        RenderSystem.setShaderTexture(0, EXPIRES_SOON_ICON);
        if (this.animTick % 20 < 10) {
            DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 10, 28, 20, 28);
        } else {
            DrawableHelper.drawTexture(matrices, x, y, 10.0f, 0.0f, 10, 28, 20, 28);
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
        DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 10, 28, 10, 28);
        if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27 && mouseY < this.height - 40 && mouseY > 32 && !this.shouldShowPopup()) {
            this.setTooltip(OPEN_TEXT);
        }
    }

    void drawClose(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, OFF_ICON);
        DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 10, 28, 10, 28);
        if (mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 27 && mouseY < this.height - 40 && mouseY > 32 && !this.shouldShowPopup()) {
            this.setTooltip(CLOSED_TEXT);
        }
    }

    void renderNews(MatrixStack matrices, int mouseX, int mouseY, boolean hasUnread, int x, int y, boolean hovered, boolean active) {
        boolean bl = false;
        if (mouseX >= x && mouseX <= x + 20 && mouseY >= y && mouseY <= y + 20) {
            bl = true;
        }
        RenderSystem.setShaderTexture(0, NEWS_ICON);
        if (!active) {
            RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1.0f);
        }
        boolean bl2 = active && hovered;
        float f = bl2 ? 20.0f : 0.0f;
        DrawableHelper.drawTexture(matrices, x, y, f, 0.0f, 20, 20, 40, 20);
        if (bl && active) {
            this.setTooltip(NEWS_TEXT);
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (hasUnread && active) {
            int i = bl ? 0 : (int)(Math.max(0.0f, Math.max(MathHelper.sin((float)(10 + this.animTick) * 0.57f), MathHelper.cos((float)this.animTick * 0.35f))) * -6.0f);
            RenderSystem.setShaderTexture(0, INVITATION_ICON);
            DrawableHelper.drawTexture(matrices, x + 10, y + 2 + i, 40.0f, 0.0f, 8, 8, 48, 16);
        }
    }

    private void renderLocal(MatrixStack matrices) {
        String string = "LOCAL!";
        matrices.push();
        matrices.translate(this.width / 2 - 25, 20.0f, 0.0f);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-20.0f));
        matrices.scale(1.5f, 1.5f, 1.5f);
        this.textRenderer.draw(matrices, "LOCAL!", 0.0f, 0.0f, 0x7FFF7F);
        matrices.pop();
    }

    private void renderStage(MatrixStack matrices) {
        String string = "STAGE!";
        matrices.push();
        matrices.translate(this.width / 2 - 25, 20.0f, 0.0f);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-20.0f));
        matrices.scale(1.5f, 1.5f, 1.5f);
        this.textRenderer.draw(matrices, "STAGE!", 0.0f, 0.0f, -256);
        matrices.pop();
    }

    public RealmsMainScreen newScreen() {
        RealmsMainScreen realmsMainScreen = new RealmsMainScreen(this.parent);
        realmsMainScreen.init(this.client, this.width, this.height);
        return realmsMainScreen;
    }

    public static void loadImages(ResourceManager manager) {
        Set<Identifier> collection = manager.findResources("textures/gui/images", filename -> filename.getPath().endsWith(".png")).keySet();
        IMAGES = collection.stream().filter(id -> id.getNamespace().equals("realms")).toList();
    }

    private void openPendingInvitesScreen(ButtonWidget button) {
        this.client.setScreen(new RealmsPendingInvitesScreen(this.parent));
    }

    static {
        lastScrollYPosition = -1;
    }

    @Environment(value=EnvType.CLIENT)
    class RealmSelectionList
    extends RealmsObjectSelectionList<Entry> {
        public RealmSelectionList() {
            super(RealmsMainScreen.this.width, RealmsMainScreen.this.height, 32, RealmsMainScreen.this.height - 64, 36);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                Entry entry = (Entry)this.getSelectedOrNull();
                if (entry == null) {
                    return super.keyPressed(keyCode, scanCode, modifiers);
                }
                entry.keyPressed(keyCode, scanCode, modifiers);
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
                    this.itemClicked(k, l, mouseX, mouseY, this.width, button);
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
        public void itemClicked(int cursorY, int selectionIndex, double mouseX, double mouseY, int listWidth, int i) {
            Entry entry = (Entry)this.getEntry(selectionIndex);
            if (entry.mouseClicked(mouseX, mouseY, i)) {
                return;
            }
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
            if (RealmsMainScreen.this.shouldPlayButtonBeActive(realmsServer)) {
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
            super(RealmsMainScreen.this.width / 2 + 50, 6, 22, 22, ScreenTexts.EMPTY, RealmsMainScreen.this::openPendingInvitesScreen, DEFAULT_NARRATION_SUPPLIER);
        }

        public void updatePendingText() {
            this.setMessage(RealmsMainScreen.this.pendingInvitesCount == 0 ? NO_PENDING_TEXT : PENDING_TEXT);
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RealmsMainScreen.this.drawInvitationPendingIcon(matrices, mouseX, mouseY, this.getX(), this.getY(), this.isSelected(), this.active);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class NewsButton
    extends ButtonWidget {
        public NewsButton() {
            super(RealmsMainScreen.this.width - 115, 6, 20, 20, Text.translatable("mco.news"), button -> {
                if (realmsMainScreen.newsLink == null) {
                    return;
                }
                ConfirmLinkScreen.open(realmsMainScreen.newsLink, RealmsMainScreen.this, true);
                if (realmsMainScreen.hasUnreadNews) {
                    RealmsPersistence.RealmsPersistenceData realmsPersistenceData = RealmsPersistence.readFile();
                    realmsPersistenceData.hasUnreadNews = false;
                    realmsMainScreen.hasUnreadNews = false;
                    RealmsPersistence.writeFile(realmsPersistenceData);
                }
            }, DEFAULT_NARRATION_SUPPLIER);
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RealmsMainScreen.this.renderNews(matrices, mouseX, mouseY, RealmsMainScreen.this.hasUnreadNews, this.getX(), this.getY(), this.isSelected(), this.active);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class CloseButton
    extends CrossButton {
        public CloseButton() {
            super(RealmsMainScreen.this.popupX0() + 4, RealmsMainScreen.this.popupY0() + 4, button -> RealmsMainScreen.this.onClosePopup(), Text.translatable("mco.selectServer.close"));
        }
    }

    @Environment(value=EnvType.CLIENT)
    static interface Request<T> {
        public T request(RealmsClient var1) throws RealmsServiceException;
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
                DrawableHelper.drawCenteredTextWithShadow(matrices, RealmsMainScreen.this.textRenderer, text, RealmsMainScreen.this.width / 2, i + j, k);
                j += 10;
            }
        }

        @Override
        public Text getNarration() {
            return TRIAL_NARRATION;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class RealmSelectionListEntry
    extends Entry {
        private static final int field_32054 = 36;
        private final RealmsServer server;

        public RealmSelectionListEntry(RealmsServer server) {
            this.server = server;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.render(this.server, matrices, x, y, mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.server.state == RealmsServer.State.UNINITIALIZED) {
                RealmsMainScreen.this.client.setScreen(new RealmsCreateRealmScreen(this.server, RealmsMainScreen.this));
            }
            return true;
        }

        private void render(RealmsServer server, MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            this.renderRealmsServerItem(server, matrices, x + 36, y, mouseX, mouseY);
        }

        private void renderRealmsServerItem(RealmsServer server, MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            if (server.state == RealmsServer.State.UNINITIALIZED) {
                RenderSystem.setShaderTexture(0, WORLD_ICON);
                DrawableHelper.drawTexture(matrices, x + 10, y + 6, 0.0f, 0.0f, 40, 20, 40, 20);
                float f = 0.5f + (1.0f + MathHelper.sin((float)RealmsMainScreen.this.animTick * 0.25f)) * 0.25f;
                int i = 0xFF000000 | (int)(127.0f * f) << 16 | (int)(255.0f * f) << 8 | (int)(127.0f * f);
                DrawableHelper.drawCenteredTextWithShadow(matrices, RealmsMainScreen.this.textRenderer, UNINITIALIZED_TEXT, x + 10 + 40 + 75, y + 12, i);
                return;
            }
            int j = 225;
            int i = 2;
            this.drawServerState(server, matrices, x, y, mouseX, mouseY, 225, 2);
            if (!"0".equals(server.serverPing.nrOfPlayers)) {
                String string = Formatting.GRAY + server.serverPing.nrOfPlayers;
                RealmsMainScreen.this.textRenderer.draw(matrices, string, (float)(x + 207 - RealmsMainScreen.this.textRenderer.getWidth(string)), (float)(y + 3), 0x808080);
                if (mouseX >= x + 207 - RealmsMainScreen.this.textRenderer.getWidth(string) && mouseX <= x + 207 && mouseY >= y + 1 && mouseY <= y + 10 && mouseY < RealmsMainScreen.this.height - 40 && mouseY > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                    RealmsMainScreen.this.setTooltip(Text.literal(server.serverPing.playerList));
                }
            }
            if (RealmsMainScreen.this.isSelfOwnedServer(server) && server.expired) {
                Text text = server.expiredTrial ? EXPIRED_TRIAL_TEXT : EXPIRED_LIST_TEXT;
                int k = y + 11 + 5;
                RealmsMainScreen.this.textRenderer.draw(matrices, text, (float)(x + 2), (float)(k + 1), 15553363);
            } else {
                if (server.worldType == RealmsServer.WorldType.MINIGAME) {
                    int l = 0xCCAC5C;
                    int k = RealmsMainScreen.this.textRenderer.getWidth(MINIGAME_TEXT);
                    RealmsMainScreen.this.textRenderer.draw(matrices, MINIGAME_TEXT, (float)(x + 2), (float)(y + 12), 0xCCAC5C);
                    RealmsMainScreen.this.textRenderer.draw(matrices, server.getMinigameName(), (float)(x + 2 + k), (float)(y + 12), 0x6C6C6C);
                } else {
                    RealmsMainScreen.this.textRenderer.draw(matrices, server.getDescription(), (float)(x + 2), (float)(y + 12), 0x6C6C6C);
                }
                if (!RealmsMainScreen.this.isSelfOwnedServer(server)) {
                    RealmsMainScreen.this.textRenderer.draw(matrices, server.owner, (float)(x + 2), (float)(y + 12 + 11), 0x4C4C4C);
                }
            }
            RealmsMainScreen.this.textRenderer.draw(matrices, server.getName(), (float)(x + 2), (float)(y + 1), 0xFFFFFF);
            RealmsUtil.drawPlayerHead(matrices, x - 36, y, 32, server.ownerUUID);
        }

        private void drawServerState(RealmsServer server, MatrixStack matrices, int x, int y, int mouseX, int mouseY, int xOffset, int yOffset) {
            int i = x + xOffset + 22;
            if (server.expired) {
                RealmsMainScreen.this.drawExpired(matrices, i, y + yOffset, mouseX, mouseY);
            } else if (server.state == RealmsServer.State.CLOSED) {
                RealmsMainScreen.this.drawClose(matrices, i, y + yOffset, mouseX, mouseY);
            } else if (RealmsMainScreen.this.isSelfOwnedServer(server) && server.daysLeft < 7) {
                RealmsMainScreen.this.drawExpiring(matrices, i, y + yOffset, mouseX, mouseY, server.daysLeft);
            } else if (server.state == RealmsServer.State.OPEN) {
                RealmsMainScreen.this.drawOpen(matrices, i, y + yOffset, mouseX, mouseY);
            }
        }

        @Override
        public Text getNarration() {
            if (this.server.state == RealmsServer.State.UNINITIALIZED) {
                return UNINITIALIZED_BUTTON_NARRATION;
            }
            return Text.translatable("narrator.select", this.server.name);
        }

        @Override
        @Nullable
        public RealmsServer getRealmsServer() {
            return this.server;
        }
    }

    @Environment(value=EnvType.CLIENT)
    abstract class Entry
    extends AlwaysSelectedEntryListWidget.Entry<Entry> {
        Entry() {
        }

        @Nullable
        public RealmsServer getRealmsServer() {
            return null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class VisitUrlNotification
    extends Entry {
        private static final int field_43002 = 40;
        private static final int field_43003 = 36;
        private static final int field_43004 = -12303292;
        private final Text message;
        private final List<ClickableWidget> gridChildren = new ArrayList<ClickableWidget>();
        @Nullable
        private final CrossButton dismissButton;
        private final MultilineTextWidget textWidget;
        private final GridWidget grid;
        private final SimplePositioningWidget textGrid;
        private int width = -1;

        public VisitUrlNotification(Text message, RealmsNotification notification) {
            this.message = message;
            this.grid = new GridWidget();
            int i = 7;
            this.grid.add(new IconWidget(20, 20, INFO_ICON), 0, 0, this.grid.copyPositioner().margin(7, 7, 0, 0));
            this.grid.add(EmptyWidget.ofWidth(40), 0, 0);
            this.textGrid = this.grid.add(new SimplePositioningWidget(0, ((RealmsMainScreen)RealmsMainScreen.this).textRenderer.fontHeight * 3), 0, 1, this.grid.copyPositioner().marginTop(7));
            this.textWidget = this.textGrid.add(new MultilineTextWidget(message, RealmsMainScreen.this.textRenderer).setCentered(true).setMaxRows(3), this.textGrid.copyPositioner().alignHorizontalCenter().alignTop());
            this.grid.add(EmptyWidget.ofWidth(40), 0, 2);
            this.dismissButton = notification.isDismissable() ? this.grid.add(new CrossButton(button -> RealmsMainScreen.this.dismissNotification(notification.getUuid()), Text.translatable("mco.notification.dismiss")), 0, 2, this.grid.copyPositioner().alignRight().margin(0, 7, 7, 0)) : null;
            this.grid.forEachChild(this.gridChildren::add);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (this.dismissButton != null && this.dismissButton.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
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
        public void drawBorder(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            super.drawBorder(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
            DrawableHelper.drawBorder(matrices, x - 2, y - 2, entryWidth, 70, -12303292);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.grid.setPosition(x, y);
            this.setWidth(entryWidth - 4);
            this.gridChildren.forEach(child -> child.render(matrices, mouseX, mouseY, tickDelta));
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.dismissButton != null && this.dismissButton.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public Text getNarration() {
            return this.message;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class VisitButtonEntry
    extends Entry {
        private final ButtonWidget button;
        private final int x;

        public VisitButtonEntry(ButtonWidget button) {
            this.x = RealmsMainScreen.this.width / 2 - 75;
            this.button = button;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.button.isMouseOver(mouseX, mouseY)) {
                return this.button.mouseClicked(mouseX, mouseY, button);
            }
            return false;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (this.button.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.button.setPosition(this.x, y + 4);
            this.button.render(matrices, mouseX, mouseY, tickDelta);
        }

        @Override
        public Text getNarration() {
            return this.button.getMessage();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CrossButton
    extends ButtonWidget {
        protected CrossButton(ButtonWidget.PressAction onPress, Text message) {
            this(0, 0, onPress, message);
        }

        protected CrossButton(int x, int y, ButtonWidget.PressAction onPress, Text message) {
            super(x, y, 14, 14, message, onPress, DEFAULT_NARRATION_SUPPLIER);
            this.setTooltip(Tooltip.of(message));
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RenderSystem.setShaderTexture(0, CROSS_ICON);
            float f = this.isSelected() ? 14.0f : 0.0f;
            CrossButton.drawTexture(matrices, this.getX(), this.getY(), 0.0f, f, 14, 14, 14, 28);
        }
    }
}

