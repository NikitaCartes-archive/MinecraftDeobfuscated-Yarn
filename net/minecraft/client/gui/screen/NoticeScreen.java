/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class NoticeScreen
extends Screen {
    private final Runnable actionHandler;
    protected final Text notice;
    private MultilineText noticeLines = MultilineText.EMPTY;
    protected final Text buttonString;

    public NoticeScreen(Runnable actionHandler, Text title, Text notice) {
        this(actionHandler, title, notice, ScreenTexts.BACK);
    }

    public NoticeScreen(Runnable actionHandler, Text title, Text notice, Text text) {
        super(title);
        this.actionHandler = actionHandler;
        this.notice = notice;
        this.buttonString = text;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 168, 200, 20, this.buttonString, buttonWidget -> this.actionHandler.run()));
        this.noticeLines = MultilineText.create(this.textRenderer, (StringVisitable)this.notice, this.width - 50);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        NoticeScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 70, 0xFFFFFF);
        this.noticeLines.drawCenterWithShadow(matrices, this.width / 2, 90);
        super.render(matrices, mouseX, mouseY, delta);
    }
}

