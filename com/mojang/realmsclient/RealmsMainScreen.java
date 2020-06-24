/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.KeyCombo;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.util.RealmsPersistence;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsGetServerDetailsTask;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsMainScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier ON_ICON = new Identifier("realms", "textures/gui/realms/on_icon.png");
    private static final Identifier OFF_ICON = new Identifier("realms", "textures/gui/realms/off_icon.png");
    private static final Identifier EXPIRED_ICON = new Identifier("realms", "textures/gui/realms/expired_icon.png");
    private static final Identifier EXPIRES_SOON_ICON = new Identifier("realms", "textures/gui/realms/expires_soon_icon.png");
    private static final Identifier LEAVE_ICON = new Identifier("realms", "textures/gui/realms/leave_icon.png");
    private static final Identifier INVITATION_ICON = new Identifier("realms", "textures/gui/realms/invitation_icons.png");
    private static final Identifier INVITE_ICON = new Identifier("realms", "textures/gui/realms/invite_icon.png");
    private static final Identifier WORLD_ICON = new Identifier("realms", "textures/gui/realms/world_icon.png");
    private static final Identifier REALMS = new Identifier("realms", "textures/gui/title/realms.png");
    private static final Identifier CONFIGURE_ICON = new Identifier("realms", "textures/gui/realms/configure_icon.png");
    private static final Identifier QUESTIONMARK = new Identifier("realms", "textures/gui/realms/questionmark.png");
    private static final Identifier NEWS_ICON = new Identifier("realms", "textures/gui/realms/news_icon.png");
    private static final Identifier POPUP = new Identifier("realms", "textures/gui/realms/popup.png");
    private static final Identifier DARKEN = new Identifier("realms", "textures/gui/realms/darken.png");
    private static final Identifier CROSS_ICON = new Identifier("realms", "textures/gui/realms/cross_icon.png");
    private static final Identifier TRIAL_ICON = new Identifier("realms", "textures/gui/realms/trial_icon.png");
    private static final Identifier WIDGETS = new Identifier("minecraft", "textures/gui/widgets.png");
    private static List<Identifier> IMAGES = ImmutableList.of();
    private static final RealmsDataFetcher realmsDataFetcher = new RealmsDataFetcher();
    private static boolean overrideConfigure;
    private static int lastScrollYPosition;
    private static volatile boolean hasParentalConsent;
    private static volatile boolean checkedParentalConsent;
    private static volatile boolean checkedClientCompatability;
    private static Screen realmsGenericErrorScreen;
    private static boolean regionsPinged;
    private final RateLimiter rateLimiter;
    private boolean dontSetConnectedToRealms;
    private final Screen lastScreen;
    private volatile RealmSelectionList realmSelectionList;
    private long selectedServerId = -1L;
    private ButtonWidget playButton;
    private ButtonWidget backButton;
    private ButtonWidget renewButton;
    private ButtonWidget configureButton;
    private ButtonWidget leaveButton;
    private List<Text> toolTip;
    private List<RealmsServer> realmsServers = Lists.newArrayList();
    private volatile int numberOfPendingInvites;
    private int animTick;
    private boolean hasFetchedServers;
    private boolean popupOpenedByUser;
    private boolean justClosedPopup;
    private volatile boolean trialsAvailable;
    private volatile boolean createdTrial;
    private volatile boolean showingPopup;
    private volatile boolean hasUnreadNews;
    private volatile String newsLink;
    private int carouselIndex;
    private int carouselTick;
    private boolean hasSwitchedCarouselImage;
    private List<KeyCombo> keyCombos;
    private int clicks;
    private ReentrantLock connectLock = new ReentrantLock();
    private class_5220 field_24198;
    private ButtonWidget showPopupButton;
    private ButtonWidget pendingInvitesButton;
    private ButtonWidget newsButton;
    private ButtonWidget createTrialButton;
    private ButtonWidget buyARealmButton;
    private ButtonWidget closeButton;

    public RealmsMainScreen(Screen screen) {
        this.lastScreen = screen;
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
        if (this.trialsAvailable && !this.createdTrial && this.realmsServers.isEmpty()) {
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
            this.client.openScreen(realmsGenericErrorScreen);
            return;
        }
        this.connectLock = new ReentrantLock();
        if (checkedClientCompatability && !RealmsMainScreen.hasParentalConsent()) {
            this.checkParentalConsent();
        }
        this.checkClientCompatability();
        this.checkUnreadNews();
        if (!this.dontSetConnectedToRealms) {
            this.client.setConnectedToRealms(false);
        }
        this.client.keyboard.enableRepeatEvents(true);
        if (RealmsMainScreen.hasParentalConsent()) {
            realmsDataFetcher.forceUpdate();
        }
        this.showingPopup = false;
        if (RealmsMainScreen.hasParentalConsent() && this.hasFetchedServers) {
            this.addButtons();
        }
        this.realmSelectionList = new RealmSelectionList();
        if (lastScrollYPosition != -1) {
            this.realmSelectionList.setScrollAmount(lastScrollYPosition);
        }
        this.addChild(this.realmSelectionList);
        this.focusOn(this.realmSelectionList);
    }

    private static boolean hasParentalConsent() {
        return checkedParentalConsent && hasParentalConsent;
    }

    public void addButtons() {
        this.configureButton = this.addButton(new ButtonWidget(this.width / 2 - 190, this.height - 32, 90, 20, new TranslatableText("mco.selectServer.configure"), buttonWidget -> this.configureClicked(this.findServer(this.selectedServerId))));
        this.playButton = this.addButton(new ButtonWidget(this.width / 2 - 93, this.height - 32, 90, 20, new TranslatableText("mco.selectServer.play"), buttonWidget -> {
            RealmsServer realmsServer = this.findServer(this.selectedServerId);
            if (realmsServer == null) {
                return;
            }
            this.play(realmsServer, this);
        }));
        this.backButton = this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 32, 90, 20, ScreenTexts.BACK, buttonWidget -> {
            if (!this.justClosedPopup) {
                this.client.openScreen(this.lastScreen);
            }
        }));
        this.renewButton = this.addButton(new ButtonWidget(this.width / 2 + 100, this.height - 32, 90, 20, new TranslatableText("mco.selectServer.expiredRenew"), buttonWidget -> this.onRenew()));
        this.leaveButton = this.addButton(new ButtonWidget(this.width / 2 - 202, this.height - 32, 90, 20, new TranslatableText("mco.selectServer.leave"), buttonWidget -> this.leaveClicked(this.findServer(this.selectedServerId))));
        this.pendingInvitesButton = this.addButton(new PendingInvitesButton());
        this.newsButton = this.addButton(new NewsButton());
        this.showPopupButton = this.addButton(new ShowPopupButton());
        this.closeButton = this.addButton(new CloseButton());
        this.createTrialButton = this.addButton(new ButtonWidget(this.width / 2 + 52, this.popupY0() + 137 - 20, 98, 20, new TranslatableText("mco.selectServer.trial"), buttonWidget -> {
            if (!this.trialsAvailable || this.createdTrial) {
                return;
            }
            Util.getOperatingSystem().open("https://aka.ms/startjavarealmstrial");
            this.client.openScreen(this.lastScreen);
        }));
        this.buyARealmButton = this.addButton(new ButtonWidget(this.width / 2 + 52, this.popupY0() + 160 - 20, 98, 20, new TranslatableText("mco.selectServer.buy"), buttonWidget -> Util.getOperatingSystem().open("https://aka.ms/BuyJavaRealms")));
        RealmsServer realmsServer = this.findServer(this.selectedServerId);
        this.updateButtonStates(realmsServer);
    }

    private void updateButtonStates(@Nullable RealmsServer server) {
        boolean bl;
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
        this.backButton.active = true;
        this.showPopupButton.active = !this.shouldShowPopup();
    }

    private boolean shouldShowPopupButton() {
        return (!this.shouldShowPopup() || this.popupOpenedByUser) && RealmsMainScreen.hasParentalConsent() && this.hasFetchedServers;
    }

    private boolean shouldPlayButtonBeActive(@Nullable RealmsServer server) {
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
        this.justClosedPopup = false;
        ++this.animTick;
        --this.clicks;
        if (this.clicks < 0) {
            this.clicks = 0;
        }
        if (!RealmsMainScreen.hasParentalConsent()) {
            return;
        }
        realmsDataFetcher.init();
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.SERVER_LIST)) {
            boolean bl;
            List<RealmsServer> list = realmsDataFetcher.getServers();
            this.realmSelectionList.clear();
            boolean bl2 = bl = !this.hasFetchedServers;
            if (bl) {
                this.hasFetchedServers = true;
            }
            if (list != null) {
                boolean bl22 = false;
                for (RealmsServer realmsServer : list) {
                    if (!this.method_25001(realmsServer)) continue;
                    bl22 = true;
                }
                this.realmsServers = list;
                if (this.shouldShowMessageInList()) {
                    this.realmSelectionList.method_30161(new RealmSelectionListTrialEntry());
                }
                for (RealmsServer realmsServer : this.realmsServers) {
                    this.realmSelectionList.addEntry(new RealmSelectionListEntry(realmsServer));
                }
                if (!regionsPinged && bl22) {
                    regionsPinged = true;
                    this.pingRegions();
                }
            }
            if (bl) {
                this.addButtons();
            }
        }
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.numberOfPendingInvites = realmsDataFetcher.getPendingInvitesCount();
            if (this.numberOfPendingInvites > 0 && this.rateLimiter.tryAcquire(1)) {
                Realms.narrateNow(I18n.translate("mco.configure.world.invite.narration", this.numberOfPendingInvites));
            }
        }
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE) && !this.createdTrial) {
            boolean bl3 = realmsDataFetcher.isTrialAvailable();
            if (bl3 != this.trialsAvailable && this.shouldShowPopup()) {
                this.trialsAvailable = bl3;
                this.showingPopup = false;
            } else {
                this.trialsAvailable = bl3;
            }
        }
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.LIVE_STATS)) {
            RealmsServerPlayerLists realmsServerPlayerLists = realmsDataFetcher.getLivestats();
            block2: for (RealmsServerPlayerList realmsServerPlayerList : realmsServerPlayerLists.servers) {
                for (RealmsServer realmsServer : this.realmsServers) {
                    if (realmsServer.id != realmsServerPlayerList.serverId) continue;
                    realmsServer.updateServerPing(realmsServerPlayerList);
                    continue block2;
                }
            }
        }
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            this.hasUnreadNews = realmsDataFetcher.hasUnreadNews();
            this.newsLink = realmsDataFetcher.newsLink();
        }
        realmsDataFetcher.markClean();
        if (this.shouldShowPopup()) {
            ++this.carouselTick;
        }
        if (this.showPopupButton != null) {
            this.showPopupButton.visible = this.shouldShowPopupButton();
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
            if (!this.method_25001(realmsServer)) continue;
            list.add(realmsServer.id);
        }
        return list;
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
        this.stopRealmsFetcher();
    }

    private void onRenew() {
        RealmsServer realmsServer = this.findServer(this.selectedServerId);
        if (realmsServer == null) {
            return;
        }
        String string = "https://aka.ms/ExtendJavaRealms?subscriptionId=" + realmsServer.remoteSubscriptionId + "&profileId=" + this.client.getSession().getUuid() + "&ref=" + (realmsServer.expiredTrial ? "expiredTrial" : "expiredRealm");
        this.client.keyboard.setClipboard(string);
        Util.getOperatingSystem().open(string);
    }

    private void checkClientCompatability() {
        if (!checkedClientCompatability) {
            checkedClientCompatability = true;
            new Thread("MCO Compatability Checker #1"){

                @Override
                public void run() {
                    RealmsClient realmsClient = RealmsClient.createRealmsClient();
                    try {
                        RealmsClient.CompatibleVersionResponse compatibleVersionResponse = realmsClient.clientCompatible();
                        if (compatibleVersionResponse == RealmsClient.CompatibleVersionResponse.OUTDATED) {
                            realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, true);
                            RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.openScreen(realmsGenericErrorScreen));
                            return;
                        }
                        if (compatibleVersionResponse == RealmsClient.CompatibleVersionResponse.OTHER) {
                            realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, false);
                            RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.openScreen(realmsGenericErrorScreen));
                            return;
                        }
                        RealmsMainScreen.this.checkParentalConsent();
                    } catch (RealmsServiceException realmsServiceException) {
                        checkedClientCompatability = false;
                        LOGGER.error("Couldn't connect to realms", (Throwable)realmsServiceException);
                        if (realmsServiceException.httpResultCode == 401) {
                            realmsGenericErrorScreen = new RealmsGenericErrorScreen(new TranslatableText("mco.error.invalid.session.title"), new TranslatableText("mco.error.invalid.session.message"), RealmsMainScreen.this.lastScreen);
                            RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.openScreen(realmsGenericErrorScreen));
                        }
                        RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.openScreen(new RealmsGenericErrorScreen(realmsServiceException, RealmsMainScreen.this.lastScreen)));
                    }
                }
            }.start();
        }
    }

    private void checkUnreadNews() {
    }

    private void checkParentalConsent() {
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
                        RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.openScreen(new RealmsParentalConsentScreen(RealmsMainScreen.this.lastScreen)));
                    }
                    checkedParentalConsent = true;
                } catch (RealmsServiceException realmsServiceException) {
                    LOGGER.error("Couldn't connect to realms", (Throwable)realmsServiceException);
                    RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.openScreen(new RealmsGenericErrorScreen(realmsServiceException, RealmsMainScreen.this.lastScreen)));
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
                            realmsDataFetcher.forceUpdate();
                        }
                    } catch (RealmsServiceException realmsServiceException) {
                        LOGGER.error("Couldn't connect to Realms: " + realmsServiceException);
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
                            realmsDataFetcher.forceUpdate();
                        }
                    } catch (RealmsServiceException realmsServiceException) {
                        LOGGER.error("Couldn't connect to Realms: " + realmsServiceException);
                    }
                }
            }.start();
        }
    }

    private void switchToProd() {
        RealmsClient.switchToProd();
        realmsDataFetcher.forceUpdate();
    }

    private void stopRealmsFetcher() {
        realmsDataFetcher.stop();
    }

    private void configureClicked(RealmsServer realmsServer) {
        if (this.client.getSession().getUuid().equals(realmsServer.ownerUUID) || overrideConfigure) {
            this.saveListScrollPosition();
            this.client.openScreen(new RealmsConfigureWorldScreen(this, realmsServer.id));
        }
    }

    private void leaveClicked(@Nullable RealmsServer selectedServer) {
        if (selectedServer != null && !this.client.getSession().getUuid().equals(selectedServer.ownerUUID)) {
            this.saveListScrollPosition();
            TranslatableText text = new TranslatableText("mco.configure.world.leave.question.line1");
            TranslatableText text2 = new TranslatableText("mco.configure.world.leave.question.line2");
            this.client.openScreen(new RealmsLongConfirmationScreen(this::method_24991, RealmsLongConfirmationScreen.Type.Info, text, text2, true));
        }
    }

    private void saveListScrollPosition() {
        lastScrollYPosition = (int)this.realmSelectionList.getScrollAmount();
    }

    @Nullable
    private RealmsServer findServer(long id) {
        for (RealmsServer realmsServer : this.realmsServers) {
            if (realmsServer.id != id) continue;
            return realmsServer;
        }
        return null;
    }

    private void method_24991(boolean bl) {
        if (bl) {
            new Thread("Realms-leave-server"){

                @Override
                public void run() {
                    try {
                        RealmsServer realmsServer = RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId);
                        if (realmsServer != null) {
                            RealmsClient realmsClient = RealmsClient.createRealmsClient();
                            realmsClient.uninviteMyselfFrom(realmsServer.id);
                            realmsDataFetcher.removeItem(realmsServer);
                            RealmsMainScreen.this.realmsServers.remove(realmsServer);
                            RealmsMainScreen.this.realmSelectionList.children().removeIf(entry -> entry instanceof RealmSelectionListEntry && ((RealmSelectionListEntry)((RealmSelectionListEntry)entry)).mServerData.id == RealmsMainScreen.this.selectedServerId);
                            RealmsMainScreen.this.realmSelectionList.setSelected((Entry)null);
                            RealmsMainScreen.this.updateButtonStates(null);
                            RealmsMainScreen.this.selectedServerId = -1L;
                            ((RealmsMainScreen)RealmsMainScreen.this).playButton.active = false;
                        }
                    } catch (RealmsServiceException realmsServiceException) {
                        LOGGER.error("Couldn't configure world");
                        RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.openScreen(new RealmsGenericErrorScreen(realmsServiceException, (Screen)RealmsMainScreen.this)));
                    }
                }
            }.start();
        }
        this.client.openScreen(this);
    }

    public void removeSelection() {
        this.selectedServerId = -1L;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.keyCombos.forEach(KeyCombo::reset);
            this.onClosePopup();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void onClosePopup() {
        if (this.shouldShowPopup() && this.popupOpenedByUser) {
            this.popupOpenedByUser = false;
        } else {
            this.client.openScreen(this.lastScreen);
        }
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        this.keyCombos.forEach(keyCombo -> keyCombo.keyPressed(chr));
        return true;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.field_24198 = class_5220.field_24199;
        this.toolTip = null;
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
            this.drawPopup(matrices, mouseX, mouseY);
        } else {
            if (this.showingPopup) {
                this.updateButtonStates(null);
                if (!this.children.contains(this.realmSelectionList)) {
                    this.children.add(this.realmSelectionList);
                }
                RealmsServer realmsServer = this.findServer(this.selectedServerId);
                this.playButton.active = this.shouldPlayButtonBeActive(realmsServer);
            }
            this.showingPopup = false;
        }
        super.render(matrices, mouseX, mouseY, delta);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(matrices, this.toolTip, mouseX, mouseY);
        }
        if (this.trialsAvailable && !this.createdTrial && this.shouldShowPopup()) {
            this.client.getTextureManager().bindTexture(TRIAL_ICON);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
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
        this.client.getTextureManager().bindTexture(REALMS);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.5f, 0.5f, 0.5f);
        DrawableHelper.drawTexture(matrices, x * 2, y * 2 - 5, 0.0f, 0.0f, 200, 50, 200, 50);
        RenderSystem.popMatrix();
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

    private void drawPopup(MatrixStack matrices, int mouseX, int mouseY) {
        int i = this.popupX0();
        int j = this.popupY0();
        TranslatableText text = new TranslatableText("mco.selectServer.popup");
        List<StringRenderable> list = this.textRenderer.wrapLines(text, 100);
        if (!this.showingPopup) {
            RealmSelectionList element;
            this.carouselIndex = 0;
            this.carouselTick = 0;
            this.hasSwitchedCarouselImage = true;
            this.updateButtonStates(null);
            if (this.children.contains(this.realmSelectionList) && !this.children.remove(element = this.realmSelectionList)) {
                LOGGER.error("Unable to remove widget: " + element);
            }
            Realms.narrateNow(text.getString());
        }
        if (this.hasFetchedServers) {
            this.showingPopup = true;
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 0.7f);
        RenderSystem.enableBlend();
        this.client.getTextureManager().bindTexture(DARKEN);
        boolean k = false;
        int l = 32;
        DrawableHelper.drawTexture(matrices, 0, 32, 0.0f, 0.0f, this.width, this.height - 40 - 32, 310, 166);
        RenderSystem.disableBlend();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(POPUP);
        DrawableHelper.drawTexture(matrices, i, j, 0.0f, 0.0f, 310, 166, 310, 166);
        if (!IMAGES.isEmpty()) {
            this.client.getTextureManager().bindTexture(IMAGES.get(this.carouselIndex));
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
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
        int m = 0;
        for (StringRenderable stringRenderable : list) {
            int n = j + 10 * ++m - 3;
            this.textRenderer.draw(matrices, stringRenderable, (float)(this.width / 2 + 52), (float)n, 0x4C4C4C);
        }
    }

    private int popupX0() {
        return (this.width - 310) / 2;
    }

    private int popupY0() {
        return this.height / 2 - 80;
    }

    private void drawInvitationPendingIcon(MatrixStack matrixStack, int i, int j, int k, int l, boolean bl, boolean bl2) {
        boolean bl7;
        int p;
        int o;
        boolean bl6;
        boolean bl4;
        int m = this.numberOfPendingInvites;
        boolean bl3 = this.inPendingInvitationArea(i, j);
        boolean bl5 = bl4 = bl2 && bl;
        if (bl4) {
            float f = 0.25f + (1.0f + MathHelper.sin((float)this.animTick * 0.5f)) * 0.25f;
            int n = 0xFF000000 | (int)(f * 64.0f) << 16 | (int)(f * 64.0f) << 8 | (int)(f * 64.0f) << 0;
            this.fillGradient(matrixStack, k - 2, l - 2, k + 18, l + 18, n, n);
            n = 0xFF000000 | (int)(f * 255.0f) << 16 | (int)(f * 255.0f) << 8 | (int)(f * 255.0f) << 0;
            this.fillGradient(matrixStack, k - 2, l - 2, k + 18, l - 1, n, n);
            this.fillGradient(matrixStack, k - 2, l - 2, k - 1, l + 18, n, n);
            this.fillGradient(matrixStack, k + 17, l - 2, k + 18, l + 18, n, n);
            this.fillGradient(matrixStack, k - 2, l + 17, k + 18, l + 18, n, n);
        }
        this.client.getTextureManager().bindTexture(INVITE_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        boolean bl52 = bl2 && bl;
        float g = bl52 ? 16.0f : 0.0f;
        DrawableHelper.drawTexture(matrixStack, k, l - 6, g, 0.0f, 15, 25, 31, 25);
        boolean bl8 = bl6 = bl2 && m != 0;
        if (bl6) {
            o = (Math.min(m, 6) - 1) * 8;
            p = (int)(Math.max(0.0f, Math.max(MathHelper.sin((float)(10 + this.animTick) * 0.57f), MathHelper.cos((float)this.animTick * 0.35f))) * -6.0f);
            this.client.getTextureManager().bindTexture(INVITATION_ICON);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            float h = bl3 ? 8.0f : 0.0f;
            DrawableHelper.drawTexture(matrixStack, k + 4, l + 4 + p, o, h, 8, 8, 48, 16);
        }
        o = i + 12;
        p = j;
        boolean bl9 = bl7 = bl2 && bl3;
        if (bl7) {
            String string = m == 0 ? "mco.invites.nopending" : "mco.invites.pending";
            String string2 = I18n.translate(string, new Object[0]);
            int q = this.textRenderer.getWidth(string2);
            this.fillGradient(matrixStack, o - 3, p - 3, o + q + 3, p + 8 + 3, -1073741824, -1073741824);
            this.textRenderer.drawWithShadow(matrixStack, string2, (float)o, (float)p, -1);
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

    public void play(RealmsServer realmsServer, Screen screen) {
        if (realmsServer != null) {
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
            this.client.openScreen(new RealmsLongRunningMcoTaskScreen(screen, new RealmsGetServerDetailsTask(this, screen, realmsServer, this.connectLock)));
        }
    }

    private boolean isSelfOwnedServer(RealmsServer serverData) {
        return serverData.ownerUUID != null && serverData.ownerUUID.equals(this.client.getSession().getUuid());
    }

    private boolean method_25001(RealmsServer realmsServer) {
        return this.isSelfOwnedServer(realmsServer) && !realmsServer.expired;
    }

    private void drawExpired(MatrixStack matrixStack, int i, int j, int k, int l) {
        this.client.getTextureManager().bindTexture(EXPIRED_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        DrawableHelper.drawTexture(matrixStack, i, j, 0.0f, 0.0f, 10, 28, 10, 28);
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27 && l < this.height - 40 && l > 32 && !this.shouldShowPopup()) {
            this.method_27452(new TranslatableText("mco.selectServer.expired"));
        }
    }

    private void method_24987(MatrixStack matrixStack, int i, int j, int k, int l, int m) {
        this.client.getTextureManager().bindTexture(EXPIRES_SOON_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.animTick % 20 < 10) {
            DrawableHelper.drawTexture(matrixStack, i, j, 0.0f, 0.0f, 10, 28, 20, 28);
        } else {
            DrawableHelper.drawTexture(matrixStack, i, j, 10.0f, 0.0f, 10, 28, 20, 28);
        }
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27 && l < this.height - 40 && l > 32 && !this.shouldShowPopup()) {
            if (m <= 0) {
                this.method_27452(new TranslatableText("mco.selectServer.expires.soon"));
            } else if (m == 1) {
                this.method_27452(new TranslatableText("mco.selectServer.expires.day"));
            } else {
                this.method_27452(new TranslatableText("mco.selectServer.expires.days", m));
            }
        }
    }

    private void drawOpen(MatrixStack matrixStack, int i, int j, int k, int l) {
        this.client.getTextureManager().bindTexture(ON_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        DrawableHelper.drawTexture(matrixStack, i, j, 0.0f, 0.0f, 10, 28, 10, 28);
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27 && l < this.height - 40 && l > 32 && !this.shouldShowPopup()) {
            this.method_27452(new TranslatableText("mco.selectServer.open"));
        }
    }

    private void drawClose(MatrixStack matrixStack, int i, int j, int k, int l) {
        this.client.getTextureManager().bindTexture(OFF_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        DrawableHelper.drawTexture(matrixStack, i, j, 0.0f, 0.0f, 10, 28, 10, 28);
        if (k >= i && k <= i + 9 && l >= j && l <= j + 27 && l < this.height - 40 && l > 32 && !this.shouldShowPopup()) {
            this.method_27452(new TranslatableText("mco.selectServer.closed"));
        }
    }

    private void drawLeave(MatrixStack matrixStack, int i, int j, int k, int l) {
        boolean bl = false;
        if (k >= i && k <= i + 28 && l >= j && l <= j + 28 && l < this.height - 40 && l > 32 && !this.shouldShowPopup()) {
            bl = true;
        }
        this.client.getTextureManager().bindTexture(LEAVE_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = bl ? 28.0f : 0.0f;
        DrawableHelper.drawTexture(matrixStack, i, j, f, 0.0f, 28, 28, 56, 28);
        if (bl) {
            this.method_27452(new TranslatableText("mco.selectServer.leave"));
            this.field_24198 = class_5220.field_24201;
        }
    }

    private void drawConfigure(MatrixStack matrixStack, int i, int j, int k, int l) {
        boolean bl = false;
        if (k >= i && k <= i + 28 && l >= j && l <= j + 28 && l < this.height - 40 && l > 32 && !this.shouldShowPopup()) {
            bl = true;
        }
        this.client.getTextureManager().bindTexture(CONFIGURE_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = bl ? 28.0f : 0.0f;
        DrawableHelper.drawTexture(matrixStack, i, j, f, 0.0f, 28, 28, 56, 28);
        if (bl) {
            this.method_27452(new TranslatableText("mco.selectServer.configure"));
            this.field_24198 = class_5220.field_24202;
        }
    }

    protected void renderMousehoverTooltip(MatrixStack matrixStack, List<Text> list, int i, int j) {
        if (list.isEmpty()) {
            return;
        }
        int k = 0;
        int l = 0;
        for (Text text : list) {
            int m = this.textRenderer.getWidth(text);
            if (m <= l) continue;
            l = m;
        }
        int n = i - l - 5;
        int o = j;
        if (n < 0) {
            n = i + 12;
        }
        for (Text text2 : list) {
            int p = o - (k == 0 ? 3 : 0) + k;
            this.fillGradient(matrixStack, n - 3, p, n + l + 3, o + 8 + 3 + k, -1073741824, -1073741824);
            this.textRenderer.drawWithShadow(matrixStack, text2, (float)n, (float)(o + k), 0xFFFFFF);
            k += 10;
        }
    }

    private void renderMoreInfo(MatrixStack matrixStack, int i, int j, int k, int l, boolean bl) {
        boolean bl2 = false;
        if (i >= k && i <= k + 20 && j >= l && j <= l + 20) {
            bl2 = true;
        }
        this.client.getTextureManager().bindTexture(QUESTIONMARK);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = bl ? 20.0f : 0.0f;
        DrawableHelper.drawTexture(matrixStack, k, l, f, 0.0f, 20, 20, 40, 20);
        if (bl2) {
            this.method_27452(new TranslatableText("mco.selectServer.info"));
        }
    }

    private void renderNews(MatrixStack matrixStack, int i, int j, boolean bl, int k, int l, boolean bl2, boolean bl3) {
        boolean bl4 = false;
        if (i >= k && i <= k + 20 && j >= l && j <= l + 20) {
            bl4 = true;
        }
        this.client.getTextureManager().bindTexture(NEWS_ICON);
        if (bl3) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            RenderSystem.color4f(0.5f, 0.5f, 0.5f, 1.0f);
        }
        boolean bl5 = bl3 && bl2;
        float f = bl5 ? 20.0f : 0.0f;
        DrawableHelper.drawTexture(matrixStack, k, l, f, 0.0f, 20, 20, 40, 20);
        if (bl4 && bl3) {
            this.method_27452(new TranslatableText("mco.news"));
        }
        if (bl && bl3) {
            int m = bl4 ? 0 : (int)(Math.max(0.0f, Math.max(MathHelper.sin((float)(10 + this.animTick) * 0.57f), MathHelper.cos((float)this.animTick * 0.35f))) * -6.0f);
            this.client.getTextureManager().bindTexture(INVITATION_ICON);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            DrawableHelper.drawTexture(matrixStack, k + 10, l + 2 + m, 40.0f, 0.0f, 8, 8, 48, 16);
        }
    }

    private void renderLocal(MatrixStack matrixStack) {
        String string = "LOCAL!";
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(this.width / 2 - 25, 20.0f, 0.0f);
        RenderSystem.rotatef(-20.0f, 0.0f, 0.0f, 1.0f);
        RenderSystem.scalef(1.5f, 1.5f, 1.5f);
        this.textRenderer.draw(matrixStack, "LOCAL!", 0.0f, 0.0f, 0x7FFF7F);
        RenderSystem.popMatrix();
    }

    private void renderStage(MatrixStack matrixStack) {
        String string = "STAGE!";
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(this.width / 2 - 25, 20.0f, 0.0f);
        RenderSystem.rotatef(-20.0f, 0.0f, 0.0f, 1.0f);
        RenderSystem.scalef(1.5f, 1.5f, 1.5f);
        this.textRenderer.draw(matrixStack, "STAGE!", 0.0f, 0.0f, -256);
        RenderSystem.popMatrix();
    }

    public RealmsMainScreen newScreen() {
        RealmsMainScreen realmsMainScreen = new RealmsMainScreen(this.lastScreen);
        realmsMainScreen.init(this.client, this.width, this.height);
        return realmsMainScreen;
    }

    public static void method_23765(ResourceManager manager) {
        Collection<Identifier> collection = manager.findResources("textures/gui/images", string -> string.endsWith(".png"));
        IMAGES = collection.stream().filter(identifier -> identifier.getNamespace().equals("realms")).collect(ImmutableList.toImmutableList());
    }

    private void method_27452(Text ... texts) {
        this.toolTip = Arrays.asList(texts);
    }

    private void method_24985(ButtonWidget buttonWidget) {
        this.client.openScreen(new RealmsPendingInvitesScreen(this.lastScreen));
    }

    static {
        lastScrollYPosition = -1;
    }

    @Environment(value=EnvType.CLIENT)
    class CloseButton
    extends ButtonWidget {
        public CloseButton() {
            super(RealmsMainScreen.this.popupX0() + 4, RealmsMainScreen.this.popupY0() + 4, 12, 12, new TranslatableText("mco.selectServer.close"), buttonWidget -> RealmsMainScreen.this.onClosePopup());
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RealmsMainScreen.this.client.getTextureManager().bindTexture(CROSS_ICON);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            float f = this.isHovered() ? 12.0f : 0.0f;
            CloseButton.drawTexture(matrices, this.x, this.y, 0.0f, f, 12, 12, 12, 24);
            if (this.isMouseOver(mouseX, mouseY)) {
                RealmsMainScreen.this.method_27452(new Text[]{this.getMessage()});
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ShowPopupButton
    extends ButtonWidget {
        public ShowPopupButton() {
            super(RealmsMainScreen.this.width - 37, 6, 20, 20, new TranslatableText("mco.selectServer.info"), buttonWidget -> RealmsMainScreen.this.popupOpenedByUser = !RealmsMainScreen.this.popupOpenedByUser);
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RealmsMainScreen.this.renderMoreInfo(matrices, mouseX, mouseY, this.x, this.y, this.isHovered());
        }
    }

    @Environment(value=EnvType.CLIENT)
    class NewsButton
    extends ButtonWidget {
        public NewsButton() {
            super(RealmsMainScreen.this.width - 62, 6, 20, 20, LiteralText.EMPTY, buttonWidget -> {
                if (RealmsMainScreen.this.newsLink == null) {
                    return;
                }
                Util.getOperatingSystem().open(RealmsMainScreen.this.newsLink);
                if (RealmsMainScreen.this.hasUnreadNews) {
                    RealmsPersistence.RealmsPersistenceData realmsPersistenceData = RealmsPersistence.readFile();
                    realmsPersistenceData.hasUnreadNews = false;
                    RealmsMainScreen.this.hasUnreadNews = false;
                    RealmsPersistence.writeFile(realmsPersistenceData);
                }
            });
            this.setMessage(new TranslatableText("mco.news"));
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RealmsMainScreen.this.renderNews(matrices, mouseX, mouseY, RealmsMainScreen.this.hasUnreadNews, this.x, this.y, this.isHovered(), this.active);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class PendingInvitesButton
    extends ButtonWidget
    implements TickableElement {
        public PendingInvitesButton() {
            super(RealmsMainScreen.this.width / 2 + 47, 6, 22, 22, LiteralText.EMPTY, buttonWidget -> RealmsMainScreen.this.method_24985(buttonWidget));
        }

        @Override
        public void tick() {
            this.setMessage(new TranslatableText(RealmsMainScreen.this.numberOfPendingInvites == 0 ? "mco.invites.nopending" : "mco.invites.pending"));
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RealmsMainScreen.this.drawInvitationPendingIcon(matrices, mouseX, mouseY, this.x, this.y, this.isHovered(), this.active);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class RealmSelectionListEntry
    extends Entry {
        private final RealmsServer mServerData;

        public RealmSelectionListEntry(RealmsServer serverData) {
            this.mServerData = serverData;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.method_20945(this.mServerData, matrices, x, y, mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.mServerData.state == RealmsServer.State.UNINITIALIZED) {
                RealmsMainScreen.this.selectedServerId = -1L;
                RealmsMainScreen.this.client.openScreen(new RealmsCreateRealmScreen(this.mServerData, RealmsMainScreen.this));
            } else {
                RealmsMainScreen.this.selectedServerId = this.mServerData.id;
            }
            return true;
        }

        private void method_20945(RealmsServer realmsServer, MatrixStack matrixStack, int i, int j, int k, int l) {
            this.renderMcoServerItem(realmsServer, matrixStack, i + 36, j, k, l);
        }

        private void renderMcoServerItem(RealmsServer serverData, MatrixStack matrixStack, int i, int j, int k, int l) {
            String string;
            if (serverData.state == RealmsServer.State.UNINITIALIZED) {
                RealmsMainScreen.this.client.getTextureManager().bindTexture(WORLD_ICON);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.enableAlphaTest();
                DrawableHelper.drawTexture(matrixStack, i + 10, j + 6, 0.0f, 0.0f, 40, 20, 40, 20);
                float f = 0.5f + (1.0f + MathHelper.sin((float)RealmsMainScreen.this.animTick * 0.25f)) * 0.25f;
                int m = 0xFF000000 | (int)(127.0f * f) << 16 | (int)(255.0f * f) << 8 | (int)(127.0f * f);
                RealmsMainScreen.this.drawCenteredString(matrixStack, RealmsMainScreen.this.textRenderer, I18n.translate("mco.selectServer.uninitialized", new Object[0]), i + 10 + 40 + 75, j + 12, m);
                return;
            }
            int n = 225;
            int m = 2;
            if (serverData.expired) {
                RealmsMainScreen.this.drawExpired(matrixStack, i + 225 - 14, j + 2, k, l);
            } else if (serverData.state == RealmsServer.State.CLOSED) {
                RealmsMainScreen.this.drawClose(matrixStack, i + 225 - 14, j + 2, k, l);
            } else if (RealmsMainScreen.this.isSelfOwnedServer(serverData) && serverData.daysLeft < 7) {
                RealmsMainScreen.this.method_24987(matrixStack, i + 225 - 14, j + 2, k, l, serverData.daysLeft);
            } else if (serverData.state == RealmsServer.State.OPEN) {
                RealmsMainScreen.this.drawOpen(matrixStack, i + 225 - 14, j + 2, k, l);
            }
            if (!RealmsMainScreen.this.isSelfOwnedServer(serverData) && !overrideConfigure) {
                RealmsMainScreen.this.drawLeave(matrixStack, i + 225, j + 2, k, l);
            } else {
                RealmsMainScreen.this.drawConfigure(matrixStack, i + 225, j + 2, k, l);
            }
            if (!"0".equals(serverData.serverPing.nrOfPlayers)) {
                string = (Object)((Object)Formatting.GRAY) + "" + serverData.serverPing.nrOfPlayers;
                RealmsMainScreen.this.textRenderer.draw(matrixStack, string, (float)(i + 207 - RealmsMainScreen.this.textRenderer.getWidth(string)), (float)(j + 3), 0x808080);
                if (k >= i + 207 - RealmsMainScreen.this.textRenderer.getWidth(string) && k <= i + 207 && l >= j + 1 && l <= j + 10 && l < RealmsMainScreen.this.height - 40 && l > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                    RealmsMainScreen.this.method_27452(new Text[]{new LiteralText(serverData.serverPing.playerList)});
                }
            }
            if (RealmsMainScreen.this.isSelfOwnedServer(serverData) && serverData.expired) {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.enableBlend();
                RealmsMainScreen.this.client.getTextureManager().bindTexture(WIDGETS);
                RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
                string = I18n.translate("mco.selectServer.expiredList", new Object[0]);
                String string2 = I18n.translate("mco.selectServer.expiredRenew", new Object[0]);
                if (serverData.expiredTrial) {
                    string = I18n.translate("mco.selectServer.expiredTrial", new Object[0]);
                    string2 = I18n.translate("mco.selectServer.expiredSubscribe", new Object[0]);
                }
                int o = RealmsMainScreen.this.textRenderer.getWidth(string2) + 17;
                int p = 16;
                int q = i + RealmsMainScreen.this.textRenderer.getWidth(string) + 8;
                int r = j + 13;
                boolean bl = false;
                if (k >= q && k < q + o && l > r && l <= r + 16 & l < RealmsMainScreen.this.height - 40 && l > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                    bl = true;
                    RealmsMainScreen.this.field_24198 = class_5220.field_24200;
                }
                int s = bl ? 2 : 1;
                DrawableHelper.drawTexture(matrixStack, q, r, 0.0f, 46 + s * 20, o / 2, 8, 256, 256);
                DrawableHelper.drawTexture(matrixStack, q + o / 2, r, 200 - o / 2, 46 + s * 20, o / 2, 8, 256, 256);
                DrawableHelper.drawTexture(matrixStack, q, r + 8, 0.0f, 46 + s * 20 + 12, o / 2, 8, 256, 256);
                DrawableHelper.drawTexture(matrixStack, q + o / 2, r + 8, 200 - o / 2, 46 + s * 20 + 12, o / 2, 8, 256, 256);
                RenderSystem.disableBlend();
                int t = j + 11 + 5;
                int u = bl ? 0xFFFFA0 : 0xFFFFFF;
                RealmsMainScreen.this.textRenderer.draw(matrixStack, string, (float)(i + 2), (float)(t + 1), 15553363);
                RealmsMainScreen.this.drawCenteredString(matrixStack, RealmsMainScreen.this.textRenderer, string2, q + o / 2, t + 1, u);
            } else {
                if (serverData.worldType == RealmsServer.WorldType.MINIGAME) {
                    int v = 0xCCAC5C;
                    String string2 = I18n.translate("mco.selectServer.minigame", new Object[0]) + " ";
                    int o = RealmsMainScreen.this.textRenderer.getWidth(string2);
                    RealmsMainScreen.this.textRenderer.draw(matrixStack, string2, (float)(i + 2), (float)(j + 12), 0xCCAC5C);
                    RealmsMainScreen.this.textRenderer.draw(matrixStack, serverData.getMinigameName(), (float)(i + 2 + o), (float)(j + 12), 0x6C6C6C);
                } else {
                    RealmsMainScreen.this.textRenderer.draw(matrixStack, serverData.getDescription(), (float)(i + 2), (float)(j + 12), 0x6C6C6C);
                }
                if (!RealmsMainScreen.this.isSelfOwnedServer(serverData)) {
                    RealmsMainScreen.this.textRenderer.draw(matrixStack, serverData.owner, (float)(i + 2), (float)(j + 12 + 11), 0x4C4C4C);
                }
            }
            RealmsMainScreen.this.textRenderer.draw(matrixStack, serverData.getName(), (float)(i + 2), (float)(j + 1), 0xFFFFFF);
            RealmsTextureManager.withBoundFace(serverData.ownerUUID, () -> {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                DrawableHelper.drawTexture(matrixStack, i - 36, j, 32, 32, 8.0f, 8.0f, 8, 8, 64, 64);
                DrawableHelper.drawTexture(matrixStack, i - 36, j, 32, 32, 40.0f, 8.0f, 8, 8, 64, 64);
            });
        }
    }

    @Environment(value=EnvType.CLIENT)
    class RealmSelectionListTrialEntry
    extends Entry {
        private RealmSelectionListTrialEntry() {
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
            String string = I18n.translate("mco.trial.message.line1", new Object[0]) + "\\n" + I18n.translate("mco.trial.message.line2", new Object[0]);
            boolean bl = false;
            if (x <= mouseX && mouseX <= (int)RealmsMainScreen.this.realmSelectionList.getScrollAmount() && y <= mouseY && mouseY <= y + 32) {
                bl = true;
            }
            int k = 0x7FFF7F;
            if (bl && !RealmsMainScreen.this.shouldShowPopup()) {
                k = 6077788;
            }
            for (String string2 : string.split("\\\\n")) {
                RealmsMainScreen.this.drawCenteredString(matrices, RealmsMainScreen.this.textRenderer, string2, RealmsMainScreen.this.width / 2, i + j, k);
                j += 10;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    abstract class Entry
    extends AlwaysSelectedEntryListWidget.Entry<Entry> {
        private Entry() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    class RealmSelectionList
    extends RealmsObjectSelectionList<Entry> {
        private boolean field_25723;

        public RealmSelectionList() {
            super(RealmsMainScreen.this.width, RealmsMainScreen.this.height, 32, RealmsMainScreen.this.height - 40, 36);
        }

        @Override
        public void clear() {
            super.clear();
            this.field_25723 = false;
        }

        public int method_30161(Entry entry) {
            this.field_25723 = true;
            return this.addEntry(entry);
        }

        @Override
        public boolean isFocused() {
            return RealmsMainScreen.this.getFocused() == this;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == 257 || keyCode == 32 || keyCode == 335) {
                AlwaysSelectedEntryListWidget.Entry entry = (AlwaysSelectedEntryListWidget.Entry)this.getSelected();
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
                if (mouseX >= (double)i && mouseX <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
                    this.itemClicked(k, l, mouseX, mouseY, this.width);
                    RealmsMainScreen.this.clicks = RealmsMainScreen.this.clicks + 7;
                    this.setSelected(l);
                }
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void setSelected(int index) {
            RealmsServer realmsServer;
            this.setSelectedItem(index);
            if (index == -1) {
                return;
            }
            if (this.field_25723) {
                if (index == 0) {
                    Realms.narrateNow(I18n.translate("mco.trial.message.line1", new Object[0]), I18n.translate("mco.trial.message.line2", new Object[0]));
                    realmsServer = null;
                } else {
                    if (index - 1 >= RealmsMainScreen.this.realmsServers.size()) {
                        RealmsMainScreen.this.selectedServerId = -1L;
                        return;
                    }
                    realmsServer = (RealmsServer)RealmsMainScreen.this.realmsServers.get(index - 1);
                }
            } else {
                if (index >= RealmsMainScreen.this.realmsServers.size()) {
                    RealmsMainScreen.this.selectedServerId = -1L;
                    return;
                }
                realmsServer = (RealmsServer)RealmsMainScreen.this.realmsServers.get(index);
            }
            RealmsMainScreen.this.updateButtonStates(realmsServer);
            if (realmsServer == null) {
                RealmsMainScreen.this.selectedServerId = -1L;
                return;
            }
            if (realmsServer.state == RealmsServer.State.UNINITIALIZED) {
                Realms.narrateNow(I18n.translate("mco.selectServer.uninitialized", new Object[0]) + I18n.translate("mco.gui.button", new Object[0]));
                RealmsMainScreen.this.selectedServerId = -1L;
                return;
            }
            RealmsMainScreen.this.selectedServerId = realmsServer.id;
            if (RealmsMainScreen.this.clicks >= 10 && ((RealmsMainScreen)RealmsMainScreen.this).playButton.active) {
                RealmsMainScreen.this.play(RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId), RealmsMainScreen.this);
            }
            Realms.narrateNow(I18n.translate("narrator.select", realmsServer.name));
        }

        @Override
        public void setSelected(@Nullable Entry entry) {
            super.setSelected(entry);
            int i = this.children().indexOf(entry);
            if (!this.field_25723 || i > 0) {
                RealmsServer realmsServer = (RealmsServer)RealmsMainScreen.this.realmsServers.get(i - (this.field_25723 ? 1 : 0));
                RealmsMainScreen.this.selectedServerId = realmsServer.id;
                RealmsMainScreen.this.updateButtonStates(realmsServer);
            }
        }

        @Override
        public void itemClicked(int cursorY, int selectionIndex, double mouseX, double mouseY, int listWidth) {
            if (this.field_25723) {
                if (selectionIndex == 0) {
                    RealmsMainScreen.this.popupOpenedByUser = true;
                    return;
                }
                --selectionIndex;
            }
            if (selectionIndex >= RealmsMainScreen.this.realmsServers.size()) {
                return;
            }
            RealmsServer realmsServer = (RealmsServer)RealmsMainScreen.this.realmsServers.get(selectionIndex);
            if (realmsServer == null) {
                return;
            }
            if (realmsServer.state == RealmsServer.State.UNINITIALIZED) {
                RealmsMainScreen.this.selectedServerId = -1L;
                MinecraftClient.getInstance().openScreen(new RealmsCreateRealmScreen(realmsServer, RealmsMainScreen.this));
            } else {
                RealmsMainScreen.this.selectedServerId = realmsServer.id;
            }
            if (RealmsMainScreen.this.field_24198 == class_5220.field_24202) {
                RealmsMainScreen.this.selectedServerId = realmsServer.id;
                RealmsMainScreen.this.configureClicked(realmsServer);
            } else if (RealmsMainScreen.this.field_24198 == class_5220.field_24201) {
                RealmsMainScreen.this.selectedServerId = realmsServer.id;
                RealmsMainScreen.this.leaveClicked(realmsServer);
            } else if (RealmsMainScreen.this.field_24198 == class_5220.field_24200) {
                RealmsMainScreen.this.onRenew();
            }
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }

        @Override
        public int getRowWidth() {
            return 300;
        }

        @Override
        public /* synthetic */ void setSelected(@Nullable EntryListWidget.Entry entry) {
            this.setSelected((Entry)entry);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum class_5220 {
        field_24199,
        field_24200,
        field_24201,
        field_24202;

    }
}

