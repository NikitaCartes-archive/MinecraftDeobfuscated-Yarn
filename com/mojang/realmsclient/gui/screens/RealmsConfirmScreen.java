/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.RealmsScreen;

@Environment(value=EnvType.CLIENT)
public class RealmsConfirmScreen
extends RealmsScreen {
    protected BooleanConsumer field_22692;
    protected String title1;
    private final String title2;
    protected String yesButton = I18n.translate("gui.yes", new Object[0]);
    protected String noButton = I18n.translate("gui.no", new Object[0]);
    private int delayTicker;

    public RealmsConfirmScreen(BooleanConsumer booleanConsumer, String title1, String title2) {
        this.field_22692 = booleanConsumer;
        this.title1 = title1;
        this.title2 = title2;
    }

    @Override
    public void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 105, RealmsConfirmScreen.row(9), 100, 20, this.yesButton, buttonWidget -> this.field_22692.accept(true)));
        this.addButton(new ButtonWidget(this.width / 2 + 5, RealmsConfirmScreen.row(9), 100, 20, this.noButton, buttonWidget -> this.field_22692.accept(false)));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.textRenderer, this.title1, this.width / 2, RealmsConfirmScreen.row(3), 0xFFFFFF);
        this.drawCenteredString(this.textRenderer, this.title2, this.width / 2, RealmsConfirmScreen.row(5), 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        super.tick();
        if (--this.delayTicker == 0) {
            for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
                abstractButtonWidget.active = true;
            }
        }
    }
}

