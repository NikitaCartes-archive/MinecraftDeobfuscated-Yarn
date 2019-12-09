/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.util.RealmsUtil;
import java.io.IOException;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsSubscriptionInfoScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final RealmsScreen lastScreen;
    private final RealmsServer serverData;
    private final RealmsScreen mainScreen;
    private final int BUTTON_BACK_ID = 0;
    private final int BUTTON_DELETE_ID = 1;
    private final int BUTTON_SUBSCRIPTION_ID = 2;
    private final String subscriptionTitle;
    private final String subscriptionStartLabelText;
    private final String timeLeftLabelText;
    private final String daysLeftLabelText;
    private int daysLeft;
    private String startDate;
    private Subscription.SubscriptionType type;
    private final String PURCHASE_LINK = "https://aka.ms/ExtendJavaRealms";

    public RealmsSubscriptionInfoScreen(RealmsScreen lastScreen, RealmsServer serverData, RealmsScreen mainScreen) {
        this.lastScreen = lastScreen;
        this.serverData = serverData;
        this.mainScreen = mainScreen;
        this.subscriptionTitle = RealmsSubscriptionInfoScreen.getLocalizedString("mco.configure.world.subscription.title");
        this.subscriptionStartLabelText = RealmsSubscriptionInfoScreen.getLocalizedString("mco.configure.world.subscription.start");
        this.timeLeftLabelText = RealmsSubscriptionInfoScreen.getLocalizedString("mco.configure.world.subscription.timeleft");
        this.daysLeftLabelText = RealmsSubscriptionInfoScreen.getLocalizedString("mco.configure.world.subscription.recurring.daysleft");
    }

    @Override
    public void init() {
        this.getSubscription(this.serverData.id);
        Realms.narrateNow(this.subscriptionTitle, this.subscriptionStartLabelText, this.startDate, this.timeLeftLabelText, this.daysLeftPresentation(this.daysLeft));
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.buttonsAdd(new RealmsButton(2, this.width() / 2 - 100, RealmsConstants.row(6), RealmsSubscriptionInfoScreen.getLocalizedString("mco.configure.world.subscription.extend")){

            @Override
            public void onPress() {
                String string = "https://aka.ms/ExtendJavaRealms?subscriptionId=" + ((RealmsSubscriptionInfoScreen)RealmsSubscriptionInfoScreen.this).serverData.remoteSubscriptionId + "&profileId=" + Realms.getUUID();
                Realms.setClipboard(string);
                RealmsUtil.browseTo(string);
            }
        });
        this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, RealmsConstants.row(12), RealmsSubscriptionInfoScreen.getLocalizedString("gui.back")){

            @Override
            public void onPress() {
                Realms.setScreen(RealmsSubscriptionInfoScreen.this.lastScreen);
            }
        });
        if (this.serverData.expired) {
            this.buttonsAdd(new RealmsButton(1, this.width() / 2 - 100, RealmsConstants.row(10), RealmsSubscriptionInfoScreen.getLocalizedString("mco.configure.world.delete.button")){

                @Override
                public void onPress() {
                    String string = RealmsScreen.getLocalizedString("mco.configure.world.delete.question.line1");
                    String string2 = RealmsScreen.getLocalizedString("mco.configure.world.delete.question.line2");
                    Realms.setScreen(new RealmsLongConfirmationScreen(RealmsSubscriptionInfoScreen.this, RealmsLongConfirmationScreen.Type.Warning, string, string2, true, 1));
                }
            });
        }
    }

    private void getSubscription(long worldId) {
        RealmsClient realmsClient = RealmsClient.createRealmsClient();
        try {
            Subscription subscription = realmsClient.subscriptionFor(worldId);
            this.daysLeft = subscription.daysLeft;
            this.startDate = this.localPresentation(subscription.startDate);
            this.type = subscription.type;
        } catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't get subscription");
            Realms.setScreen(new RealmsGenericErrorScreen(realmsServiceException, this.lastScreen));
        } catch (IOException iOException) {
            LOGGER.error("Couldn't parse response subscribing");
        }
    }

    @Override
    public void confirmResult(boolean result, int id) {
        if (id == 1 && result) {
            new Thread("Realms-delete-realm"){

                @Override
                public void run() {
                    try {
                        RealmsClient realmsClient = RealmsClient.createRealmsClient();
                        realmsClient.deleteWorld(((RealmsSubscriptionInfoScreen)RealmsSubscriptionInfoScreen.this).serverData.id);
                    } catch (RealmsServiceException realmsServiceException) {
                        LOGGER.error("Couldn't delete world");
                        LOGGER.error(realmsServiceException);
                    } catch (IOException iOException) {
                        LOGGER.error("Couldn't delete world");
                        iOException.printStackTrace();
                    }
                    Realms.setScreen(RealmsSubscriptionInfoScreen.this.mainScreen);
                }
            }.start();
        }
        Realms.setScreen(this);
    }

    private String localPresentation(long cetTime) {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.setTimeInMillis(cetTime);
        return DateFormat.getDateTimeInstance().format(calendar.getTime());
    }

    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }

    @Override
    public boolean keyPressed(int eventKey, int scancode, int mods) {
        if (eventKey == 256) {
            Realms.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed(eventKey, scancode, mods);
    }

    @Override
    public void render(int xm, int ym, float a) {
        this.renderBackground();
        int i = this.width() / 2 - 100;
        this.drawCenteredString(this.subscriptionTitle, this.width() / 2, 17, 0xFFFFFF);
        this.drawString(this.subscriptionStartLabelText, i, RealmsConstants.row(0), 0xA0A0A0);
        this.drawString(this.startDate, i, RealmsConstants.row(1), 0xFFFFFF);
        if (this.type == Subscription.SubscriptionType.NORMAL) {
            this.drawString(this.timeLeftLabelText, i, RealmsConstants.row(3), 0xA0A0A0);
        } else if (this.type == Subscription.SubscriptionType.RECURRING) {
            this.drawString(this.daysLeftLabelText, i, RealmsConstants.row(3), 0xA0A0A0);
        }
        this.drawString(this.daysLeftPresentation(this.daysLeft), i, RealmsConstants.row(4), 0xFFFFFF);
        super.render(xm, ym, a);
    }

    private String daysLeftPresentation(int daysLeft) {
        if (daysLeft == -1 && this.serverData.expired) {
            return RealmsSubscriptionInfoScreen.getLocalizedString("mco.configure.world.subscription.expired");
        }
        if (daysLeft <= 1) {
            return RealmsSubscriptionInfoScreen.getLocalizedString("mco.configure.world.subscription.less_than_a_day");
        }
        int i = daysLeft / 30;
        int j = daysLeft % 30;
        StringBuilder stringBuilder = new StringBuilder();
        if (i > 0) {
            stringBuilder.append(i).append(" ");
            if (i == 1) {
                stringBuilder.append(RealmsSubscriptionInfoScreen.getLocalizedString("mco.configure.world.subscription.month").toLowerCase(Locale.ROOT));
            } else {
                stringBuilder.append(RealmsSubscriptionInfoScreen.getLocalizedString("mco.configure.world.subscription.months").toLowerCase(Locale.ROOT));
            }
        }
        if (j > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(j).append(" ");
            if (j == 1) {
                stringBuilder.append(RealmsSubscriptionInfoScreen.getLocalizedString("mco.configure.world.subscription.day").toLowerCase(Locale.ROOT));
            } else {
                stringBuilder.append(RealmsSubscriptionInfoScreen.getLocalizedString("mco.configure.world.subscription.days").toLowerCase(Locale.ROOT));
            }
        }
        return stringBuilder.toString();
    }
}

