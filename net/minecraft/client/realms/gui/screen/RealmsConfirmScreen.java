/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RealmsConfirmScreen
extends RealmsScreen {
    protected BooleanConsumer callback;
    private final Text title1;
    private final Text title2;

    public RealmsConfirmScreen(BooleanConsumer callback, Text title1, Text title2) {
        super(NarratorManager.EMPTY);
        this.callback = callback;
        this.title1 = title1;
        this.title2 = title2;
    }

    @Override
    public void init() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 105, RealmsConfirmScreen.row(9), 100, 20, ScreenTexts.YES, button -> this.callback.accept(true)));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, RealmsConfirmScreen.row(9), 100, 20, ScreenTexts.NO, button -> this.callback.accept(false)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        RealmsConfirmScreen.drawCenteredText(matrices, this.textRenderer, this.title1, this.width / 2, RealmsConfirmScreen.row(3), 0xFFFFFF);
        RealmsConfirmScreen.drawCenteredText(matrices, this.textRenderer, this.title2, this.width / 2, RealmsConfirmScreen.row(5), 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }
}

