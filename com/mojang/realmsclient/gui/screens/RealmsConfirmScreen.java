/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RealmsConfirmScreen
extends RealmsScreen {
    protected BooleanConsumer field_22692;
    private final Text title1;
    private final Text title2;
    private int delayTicker;

    public RealmsConfirmScreen(BooleanConsumer booleanConsumer, Text text, Text text2) {
        this.field_22692 = booleanConsumer;
        this.title1 = text;
        this.title2 = text2;
    }

    @Override
    public void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 105, RealmsConfirmScreen.row(9), 100, 20, ScreenTexts.YES, buttonWidget -> this.field_22692.accept(true)));
        this.addButton(new ButtonWidget(this.width / 2 + 5, RealmsConfirmScreen.row(9), 100, 20, ScreenTexts.NO, buttonWidget -> this.field_22692.accept(false)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.method_27534(matrices, this.textRenderer, this.title1, this.width / 2, RealmsConfirmScreen.row(3), 0xFFFFFF);
        this.method_27534(matrices, this.textRenderer, this.title2, this.width / 2, RealmsConfirmScreen.row(5), 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
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

