/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class RealmsLongConfirmationScreen
extends RealmsScreen {
    private final Type type;
    private final Text line2;
    private final Text line3;
    protected final BooleanConsumer field_22697;
    private final boolean yesNoQuestion;

    public RealmsLongConfirmationScreen(BooleanConsumer booleanConsumer, Type type, Text text, Text text2, boolean yesNoQuestion) {
        this.field_22697 = booleanConsumer;
        this.type = type;
        this.line2 = text;
        this.line3 = text2;
        this.yesNoQuestion = yesNoQuestion;
    }

    @Override
    public void init() {
        Realms.narrateNow(this.type.text, this.line2.getString(), this.line3.getString());
        if (this.yesNoQuestion) {
            this.addButton(new ButtonWidget(this.width / 2 - 105, RealmsLongConfirmationScreen.row(8), 100, 20, ScreenTexts.YES, buttonWidget -> this.field_22697.accept(true)));
            this.addButton(new ButtonWidget(this.width / 2 + 5, RealmsLongConfirmationScreen.row(8), 100, 20, ScreenTexts.NO, buttonWidget -> this.field_22697.accept(false)));
        } else {
            this.addButton(new ButtonWidget(this.width / 2 - 50, RealmsLongConfirmationScreen.row(8), 100, 20, new TranslatableText("mco.gui.ok"), buttonWidget -> this.field_22697.accept(true)));
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
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        RealmsLongConfirmationScreen.drawCenteredString(matrices, this.textRenderer, this.type.text, this.width / 2, RealmsLongConfirmationScreen.row(2), this.type.colorCode);
        RealmsLongConfirmationScreen.drawCenteredText(matrices, this.textRenderer, this.line2, this.width / 2, RealmsLongConfirmationScreen.row(4), 0xFFFFFF);
        RealmsLongConfirmationScreen.drawCenteredText(matrices, this.textRenderer, this.line3, this.width / 2, RealmsLongConfirmationScreen.row(6), 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
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

