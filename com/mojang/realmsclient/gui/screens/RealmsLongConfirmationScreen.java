/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;

@Environment(value=EnvType.CLIENT)
public class RealmsLongConfirmationScreen
extends RealmsScreen {
    private final Type type;
    private final String line2;
    private final String line3;
    protected final BooleanConsumer field_22697;
    protected final String yesButton;
    protected final String noButton;
    private final String okButton;
    private final boolean yesNoQuestion;

    public RealmsLongConfirmationScreen(BooleanConsumer booleanConsumer, Type type, String line2, String line3, boolean yesNoQuestion) {
        this.field_22697 = booleanConsumer;
        this.type = type;
        this.line2 = line2;
        this.line3 = line3;
        this.yesNoQuestion = yesNoQuestion;
        this.yesButton = I18n.translate("gui.yes", new Object[0]);
        this.noButton = I18n.translate("gui.no", new Object[0]);
        this.okButton = I18n.translate("mco.gui.ok", new Object[0]);
    }

    @Override
    public void init() {
        Realms.narrateNow(this.type.text, this.line2, this.line3);
        if (this.yesNoQuestion) {
            this.addButton(new ButtonWidget(this.width / 2 - 105, RealmsLongConfirmationScreen.row(8), 100, 20, this.yesButton, buttonWidget -> this.field_22697.accept(true)));
            this.addButton(new ButtonWidget(this.width / 2 + 5, RealmsLongConfirmationScreen.row(8), 100, 20, this.noButton, buttonWidget -> this.field_22697.accept(false)));
        } else {
            this.addButton(new ButtonWidget(this.width / 2 - 50, RealmsLongConfirmationScreen.row(8), 100, 20, this.okButton, buttonWidget -> this.field_22697.accept(true)));
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.field_22697.accept(false);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.textRenderer, this.type.text, this.width / 2, RealmsLongConfirmationScreen.row(2), this.type.colorCode);
        this.drawCenteredString(this.textRenderer, this.line2, this.width / 2, RealmsLongConfirmationScreen.row(4), 0xFFFFFF);
        this.drawCenteredString(this.textRenderer, this.line3, this.width / 2, RealmsLongConfirmationScreen.row(6), 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Type {
        Warning("Warning!", 0xFF0000),
        Info("Info!", 8226750);

        public final int colorCode;
        public final String text;

        private Type(String text, int colorCode) {
            this.text = text;
            this.colorCode = colorCode;
        }
    }
}

