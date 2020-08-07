/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui.screen;

import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.Subscription;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongConfirmationScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsSubscriptionInfoScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Text subscriptionTitle = new TranslatableText("mco.configure.world.subscription.title");
    private static final Text subscriptionStartLabelText = new TranslatableText("mco.configure.world.subscription.start");
    private static final Text timeLeftLabelText = new TranslatableText("mco.configure.world.subscription.timeleft");
    private static final Text daysLeftLabelText = new TranslatableText("mco.configure.world.subscription.recurring.daysleft");
    private static final Text field_26517 = new TranslatableText("mco.configure.world.subscription.expired");
    private static final Text field_26518 = new TranslatableText("mco.configure.world.subscription.less_than_a_day");
    private static final Text field_26519 = new TranslatableText("mco.configure.world.subscription.month");
    private static final Text field_26520 = new TranslatableText("mco.configure.world.subscription.months");
    private static final Text field_26521 = new TranslatableText("mco.configure.world.subscription.day");
    private static final Text field_26522 = new TranslatableText("mco.configure.world.subscription.days");
    private final Screen parent;
    private final RealmsServer serverData;
    private final Screen mainScreen;
    private Text daysLeft;
    private String startDate;
    private Subscription.SubscriptionType type;

    public RealmsSubscriptionInfoScreen(Screen parent, RealmsServer serverData, Screen mainScreen) {
        this.parent = parent;
        this.serverData = serverData;
        this.mainScreen = mainScreen;
    }

    @Override
    public void init() {
        this.getSubscription(this.serverData.id);
        Realms.narrateNow(subscriptionTitle.getString(), subscriptionStartLabelText.getString(), this.startDate, timeLeftLabelText.getString(), this.daysLeft.getString());
        this.client.keyboard.setRepeatEvents(true);
        this.addButton(new ButtonWidget(this.width / 2 - 100, RealmsSubscriptionInfoScreen.row(6), 200, 20, new TranslatableText("mco.configure.world.subscription.extend"), buttonWidget -> {
            String string = "https://aka.ms/ExtendJavaRealms?subscriptionId=" + this.serverData.remoteSubscriptionId + "&profileId=" + this.client.getSession().getUuid();
            this.client.keyboard.setClipboard(string);
            Util.getOperatingSystem().open(string);
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, RealmsSubscriptionInfoScreen.row(12), 200, 20, ScreenTexts.BACK, buttonWidget -> this.client.openScreen(this.parent)));
        if (this.serverData.expired) {
            this.addButton(new ButtonWidget(this.width / 2 - 100, RealmsSubscriptionInfoScreen.row(10), 200, 20, new TranslatableText("mco.configure.world.delete.button"), buttonWidget -> {
                TranslatableText text = new TranslatableText("mco.configure.world.delete.question.line1");
                TranslatableText text2 = new TranslatableText("mco.configure.world.delete.question.line2");
                this.client.openScreen(new RealmsLongConfirmationScreen(this::method_25271, RealmsLongConfirmationScreen.Type.Warning, text, text2, true));
            }));
        }
    }

    private void method_25271(boolean bl) {
        if (bl) {
            new Thread("Realms-delete-realm"){

                @Override
                public void run() {
                    try {
                        RealmsClient realmsClient = RealmsClient.createRealmsClient();
                        realmsClient.deleteWorld(((RealmsSubscriptionInfoScreen)RealmsSubscriptionInfoScreen.this).serverData.id);
                    } catch (RealmsServiceException realmsServiceException) {
                        LOGGER.error("Couldn't delete world");
                        LOGGER.error(realmsServiceException);
                    }
                    RealmsSubscriptionInfoScreen.this.client.execute(() -> RealmsSubscriptionInfoScreen.this.client.openScreen(RealmsSubscriptionInfoScreen.this.mainScreen));
                }
            }.start();
        }
        this.client.openScreen(this);
    }

    private void getSubscription(long worldId) {
        RealmsClient realmsClient = RealmsClient.createRealmsClient();
        try {
            Subscription subscription = realmsClient.subscriptionFor(worldId);
            this.daysLeft = this.daysLeftPresentation(subscription.daysLeft);
            this.startDate = RealmsSubscriptionInfoScreen.localPresentation(subscription.startDate);
            this.type = subscription.type;
        } catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't get subscription");
            this.client.openScreen(new RealmsGenericErrorScreen(realmsServiceException, this.parent));
        }
    }

    private static String localPresentation(long l) {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.setTimeInMillis(l);
        return DateFormat.getDateTimeInstance().format(calendar.getTime());
    }

    @Override
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.openScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        int i = this.width / 2 - 100;
        RealmsSubscriptionInfoScreen.drawCenteredText(matrices, this.textRenderer, subscriptionTitle, this.width / 2, 17, 0xFFFFFF);
        this.textRenderer.draw(matrices, subscriptionStartLabelText, (float)i, (float)RealmsSubscriptionInfoScreen.row(0), 0xA0A0A0);
        this.textRenderer.draw(matrices, this.startDate, (float)i, (float)RealmsSubscriptionInfoScreen.row(1), 0xFFFFFF);
        if (this.type == Subscription.SubscriptionType.NORMAL) {
            this.textRenderer.draw(matrices, timeLeftLabelText, (float)i, (float)RealmsSubscriptionInfoScreen.row(3), 0xA0A0A0);
        } else if (this.type == Subscription.SubscriptionType.RECURRING) {
            this.textRenderer.draw(matrices, daysLeftLabelText, (float)i, (float)RealmsSubscriptionInfoScreen.row(3), 0xA0A0A0);
        }
        this.textRenderer.draw(matrices, this.daysLeft, (float)i, (float)RealmsSubscriptionInfoScreen.row(4), 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private Text daysLeftPresentation(int daysLeft) {
        if (daysLeft == -1 && this.serverData.expired) {
            return field_26517;
        }
        if (daysLeft <= 1) {
            return field_26518;
        }
        int i = daysLeft / 30;
        int j = daysLeft % 30;
        LiteralText mutableText = new LiteralText("");
        if (i > 0) {
            mutableText.append(Integer.toString(i)).append(" ");
            if (i == 1) {
                mutableText.append(field_26519);
            } else {
                mutableText.append(field_26520);
            }
        }
        if (j > 0) {
            if (i > 0) {
                mutableText.append(", ");
            }
            mutableText.append(Integer.toString(j)).append(" ");
            if (j == 1) {
                mutableText.append(field_26521);
            } else {
                mutableText.append(field_26522);
            }
        }
        return mutableText;
    }
}

