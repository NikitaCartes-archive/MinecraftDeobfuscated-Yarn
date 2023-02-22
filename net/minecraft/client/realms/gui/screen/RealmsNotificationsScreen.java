/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.realms.dto.RealmsNews;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsNotificationsScreen
extends RealmsScreen {
    private static final Identifier INVITE_ICON = new Identifier("realms", "textures/gui/realms/invite_icon.png");
    private static final Identifier TRIAL_ICON = new Identifier("realms", "textures/gui/realms/trial_icon.png");
    private static final Identifier NEWS_NOTIFICATION = new Identifier("realms", "textures/gui/realms/news_notification_mainscreen.png");
    private static final Identifier UNSEEN_NOTIFICATION = new Identifier("minecraft", "textures/gui/unseen_notification.png");
    @Nullable
    private PeriodicRunnerFactory.RunnersManager periodicRunnersManager;
    @Nullable
    private NotificationRunnersFactory currentRunnersFactory;
    private volatile int pendingInvitesCount;
    static boolean checkedMcoAvailability;
    private static boolean trialAvailable;
    static boolean validClient;
    private static boolean hasUnreadNews;
    private static boolean hasUnseenNotification;
    private final NotificationRunnersFactory newsAndNotifications = new NotificationRunnersFactory(){

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
    private final NotificationRunnersFactory notificationsOnly = new NotificationRunnersFactory(){

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
    private NotificationRunnersFactory getRunnersFactory() {
        boolean bl;
        boolean bl2 = bl = this.isTitleScreen() && validClient;
        if (!bl) {
            return null;
        }
        return this.shouldShowRealmsNews() ? this.newsAndNotifications : this.notificationsOnly;
    }

    @Override
    public void tick() {
        NotificationRunnersFactory notificationRunnersFactory = this.getRunnersFactory();
        if (!Objects.equals(this.currentRunnersFactory, notificationRunnersFactory)) {
            this.currentRunnersFactory = notificationRunnersFactory;
            this.periodicRunnersManager = this.currentRunnersFactory != null ? this.currentRunnersFactory.createPeriodicRunnersManager(this.client.getRealmsPeriodicCheckers()) : null;
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
            new Thread("Realms Notification Availability checker #1"){

                @Override
                public void run() {
                    RealmsClient realmsClient = RealmsClient.create();
                    try {
                        RealmsClient.CompatibleVersionResponse compatibleVersionResponse = realmsClient.clientCompatible();
                        if (compatibleVersionResponse != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                            return;
                        }
                    } catch (RealmsServiceException realmsServiceException) {
                        if (realmsServiceException.httpResultCode != 401) {
                            checkedMcoAvailability = false;
                        }
                        return;
                    }
                    validClient = true;
                }
            }.start();
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (validClient) {
            this.drawIcons(matrices, mouseX, mouseY);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void drawIcons(MatrixStack matrices, int mouseX, int mouseY) {
        int i = this.pendingInvitesCount;
        int j = 24;
        int k = this.height / 4 + 48;
        int l = this.width / 2 + 80;
        int m = k + 48 + 2;
        int n = 0;
        if (hasUnseenNotification) {
            RenderSystem.setShaderTexture(0, UNSEEN_NOTIFICATION);
            DrawableHelper.drawTexture(matrices, l - n + 5, m + 3, 0.0f, 0.0f, 10, 10, 10, 10);
            n += 14;
        }
        if (this.currentRunnersFactory != null && this.currentRunnersFactory.isNews()) {
            if (hasUnreadNews) {
                RenderSystem.setShaderTexture(0, NEWS_NOTIFICATION);
                matrices.push();
                matrices.scale(0.4f, 0.4f, 0.4f);
                DrawableHelper.drawTexture(matrices, (int)((double)(l + 2 - n) * 2.5), (int)((double)m * 2.5), 0.0f, 0.0f, 40, 40, 40, 40);
                matrices.pop();
                n += 14;
            }
            if (i != 0) {
                RenderSystem.setShaderTexture(0, INVITE_ICON);
                DrawableHelper.drawTexture(matrices, l - n, m - 6, 0.0f, 0.0f, 15, 25, 31, 25);
                n += 16;
            }
            if (trialAvailable) {
                RenderSystem.setShaderTexture(0, TRIAL_ICON);
                int o = 0;
                if ((Util.getMeasuringTimeMs() / 800L & 1L) == 1L) {
                    o = 8;
                }
                DrawableHelper.drawTexture(matrices, l + 4 - n, m + 4, 0.0f, o, 8, 8, 8, 16);
            }
        }
    }

    void addRunners(RealmsPeriodicCheckers checkers, PeriodicRunnerFactory.RunnersManager manager) {
        manager.add(checkers.pendingInvitesCount, pendingInvitesCount -> {
            this.pendingInvitesCount = pendingInvitesCount;
        });
        manager.add(checkers.trialAvailability, trialAvailable -> {
            RealmsNotificationsScreen.trialAvailable = trialAvailable;
        });
        manager.add(checkers.news, news -> {
            realmsPeriodicCheckers.newsUpdater.updateNews((RealmsNews)news);
            hasUnreadNews = realmsPeriodicCheckers.newsUpdater.hasUnreadNews();
        });
    }

    void addNotificationRunner(RealmsPeriodicCheckers checkers, PeriodicRunnerFactory.RunnersManager manager) {
        manager.add(checkers.notifications, notifications -> {
            hasUnseenNotification = false;
            for (RealmsNotification realmsNotification : notifications) {
                if (realmsNotification.isSeen()) continue;
                hasUnseenNotification = true;
                break;
            }
        });
    }

    @Environment(value=EnvType.CLIENT)
    static interface NotificationRunnersFactory {
        public PeriodicRunnerFactory.RunnersManager createPeriodicRunnersManager(RealmsPeriodicCheckers var1);

        public boolean isNews();
    }
}

