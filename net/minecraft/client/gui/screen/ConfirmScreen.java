/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.chat.Component;

@Environment(value=EnvType.CLIENT)
public class ConfirmScreen
extends Screen {
    private final Component message;
    private final List<String> messageSplit = Lists.newArrayList();
    protected String yesTranslated;
    protected String noTranslated;
    private int buttonEnableTimer;
    protected final BooleanConsumer callback;

    public ConfirmScreen(BooleanConsumer booleanConsumer, Component component, Component component2) {
        this(booleanConsumer, component, component2, I18n.translate("gui.yes", new Object[0]), I18n.translate("gui.no", new Object[0]));
    }

    public ConfirmScreen(BooleanConsumer booleanConsumer, Component component, Component component2, String string, String string2) {
        super(component);
        this.callback = booleanConsumer;
        this.message = component2;
        this.yesTranslated = string;
        this.noTranslated = string2;
    }

    @Override
    public String getNarrationMessage() {
        return super.getNarrationMessage() + ". " + this.message.getString();
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height / 6 + 96, 150, 20, this.yesTranslated, buttonWidget -> this.callback.accept(true)));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, this.noTranslated, buttonWidget -> this.callback.accept(false)));
        this.messageSplit.clear();
        this.messageSplit.addAll(this.font.wrapStringToWidthAsList(this.message.getFormattedText(), this.width - 50));
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 70, 0xFFFFFF);
        int k = 90;
        for (String string : this.messageSplit) {
            this.drawCenteredString(this.font, string, this.width / 2, k, 0xFFFFFF);
            k += this.font.fontHeight;
        }
        super.render(i, j, f);
    }

    public void disableButtons(int i) {
        this.buttonEnableTimer = i;
        for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
            abstractButtonWidget.active = false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (--this.buttonEnableTimer == 0) {
            for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
                abstractButtonWidget.active = true;
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.callback.accept(false);
            return true;
        }
        return super.keyPressed(i, j, k);
    }
}

